import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Sanjay on 1/4/2022.
 */
public class PayoutTransactionDetails extends HttpServlet
{   private static Logger log = new Logger(TransactionDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {   log.debug("Entering in TransactionDetails ");
        String errormsg="";
        HttpSession session = request.getSession();

        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ActionEntry entry = new ActionEntry();
        Functions functions =new Functions();
        String iciciTransId=null;
        String merchantId = (String) session.getAttribute("merchantid");
        String buttonValue = request.getParameter("buttonValue");
        PrintWriter out = response.getWriter();
        boolean archive=false;
        String accountid =null;
        String status = "";
        try
        {
            iciciTransId = ESAPI.validator().getValidInput("trackingid",request.getParameter("trackingid"),"Numbers",10,false);
        }
        catch(ValidationException e)
        {
            log.error("TrackingID or Archive or Accountid is Wrong",e);
            archive=false;
        }

        log.debug("Successful");
        Hashtable hash = null;
//        Hashtable childhash=null;
        Hashtable actionhash=null;
        Hashtable hash_payout= null;
        List<RuleMasterVO> masterVOList=null;
        String gateway =null;
        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }
        try
        {

                hash = getTransactionDetails(merchantId, iciciTransId, archive,gateway,status);
//                childhash=getChildDetails(iciciTransId,status);
                actionhash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId,gateway);
                hash_payout = getCustomerPayoutDetails(iciciTransId);

            request.setAttribute("transactionsDetails", hash);

//            request.setAttribute("childhash",childhash);

            entry.closeConnection();
            request.setAttribute("actionHistory", actionhash);
            request.setAttribute("buttonValue", buttonValue);
            request.setAttribute("hashpayout",hash_payout);


            hash=null;
            RequestDispatcher view = request.getRequestDispatcher("/payoutTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            view.forward(request, response);
        }
        catch (SystemError se)
        {
            log.error("SystemError In TransactionDetails---", se);
            errormsg= errormsg+"No Record Found";
            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
        }
        catch (Exception e)
        {
            log.error("Exception In TransactionDetails ---", e);
            errormsg= errormsg+"No Record Found";
            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
        }
    }

    private Hashtable getTransactionDetails(String memberId, String iciciTransId, boolean archive,String gatewayType,String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        Hashtable actionhash=null;
        String tableName ="transaction_common";

        PreparedStatement pstmt = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query =null;

                query = new StringBuffer("select td.status,td.remark as authqsiresponsecode,td.trackingid as icicitransid,refundamount,captureamount,chargebackamount," +
                        "payoutamount,td.timestamp,td.name ,td.paymentid,td.amount,td.currency ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') " +
                        "as \"date\",td.emailaddr as email,td.city as city,td.templateamount as templateAmount,td.templatecurrency as templatecurrency,td.cardtype as cardtype," +
                        "td.paymodeid as paymodeid,td.description as description,td.orderdescription as orderdescription,td.terminalid as terminalid,street,td.state as state,td.country " +
                        "as country,td.customerId as customerId,td.eci as ECI,td.emiCount as Emi,td.notificationUrl as NotificationUrl,bd.isSuccessful,bd.isSettled,bd.isRefund,bd.isChargeback,bd.isFraud,bd.isRollingReserveKept,bd.isRollingReserveReleased," +
                        "tcd.ipaddress,tcd.transactionReceiptImg,td.fromtype from transaction_common td,members,bin_details as bd," +
                        "transaction_common_details AS tcd where td.toid=members.memberid and td.trackingid= ? and toid = ?  and td.trackingid=tcd.trackingid");

            log.error("Query for merchant interface::::: "+query.toString());
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

        }
        catch (SQLException e)
        {
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }


        ctx.log("Leaving getTransactionDetails");
        return hash;
    }



    public List<RuleMasterVO> getFraudDetails(String memberId, String iciciTransId)throws SystemError
    {
        List<RuleMasterVO> ruleMasterVOList=new ArrayList<>();
        RuleMasterVO ruleMasterVO=null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer stringBuffer=new StringBuffer();
        try
        {
            conn=Database.getRDBConnection();
            stringBuffer.append("SELECT rt.rulename,rt.rulescore,rt.status FROM fraudtransaction_rules_triggered AS rt JOIN fraud_transaction AS ft ON rt.fraud_transid=ft.fraud_transaction_id WHERE ft.trackingid=? AND ft.memberid=?");
            pstmt=conn.prepareStatement(stringBuffer.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            rs=pstmt.executeQuery();
            while (rs.next()){
                ruleMasterVO=new RuleMasterVO();
                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("rulescore"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));
                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return ruleMasterVOList;
    }

    private Hashtable getChildDetailscardPresent(String iciciTransId,String status) throws SystemError
    {
        log.debug("Entering getChildDetailscardPresent:::");
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        Hashtable actionhash=null;
        //String tableName = "transaction_icicicredit";
        PreparedStatement pstmt = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_card_present AS td WHERE td.parentTrackingid= ?");
            }
            else
            {
                query = new StringBuffer("SELECT td.status,td.remark AS authqsiresponsecode,td.trackingid AS icicitransid,refundamount,captureamount,chargebackamount,payoutamount,td.timestamp,td.name ,td.paymentid,td.amount,td.currency ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,td.templateamount AS templateAmount,td.templatecurrency AS templatecurrency,td.cardtype AS cardtype,td.paymodeid AS paymodeid,td.description AS description,td.orderdescription AS orderdescription,td.terminalid AS terminalid,street,td.state AS state,td.country AS country,td.eci AS ECI,td.emiCount AS Emi,td.notificationUrl AS NotificationUrl FROM transaction_card_present td WHERE td.parentTrackingid= ?");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            log.error("pstmt----------->"+pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

        }
        catch (SQLException e)
        {
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        ctx.log("Leaving getTransactionDetails");
        return hash;
    }

    private Hashtable getChildDetails(String iciciTransId,String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        Hashtable actionhash=null;
        //String tableName = "transaction_icicicredit";
        PreparedStatement pstmt = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_common AS td WHERE td.parentTrackingid= ?");
            }
            else
            {
                query = new StringBuffer("SELECT td.status,td.remark AS authqsiresponsecode,td.trackingid AS icicitransid,refundamount,captureamount,chargebackamount,payoutamount,td.timestamp,td.name ,td.paymentid,td.amount,td.currency ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,td.templateamount AS templateAmount,td.templatecurrency AS templatecurrency,td.cardtype AS cardtype,td.paymodeid AS paymodeid,td.description AS description,td.orderdescription AS orderdescription,td.terminalid AS terminalid,street,td.state AS state,td.country AS country,td.eci AS ECI,td.emiCount AS Emi,td.notificationUrl AS NotificationUrl FROM transaction_common td WHERE td.parentTrackingid= ?");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            log.error("pstmt----------->"+pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());

        }
        catch (SQLException e)
        {
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }

        ctx.log("Leaving getTransactionDetails");
        return hash;
    }

    private Hashtable getCustomerPayoutDetails(String iciciTransId)
    {
        log.debug("fetch record 1");
        StringBuffer query  = null;
        Hashtable hash      = null;
        Connection conn     = null;

        try
        {
            conn = Database.getRDBConnection();

            query = new StringBuffer("select distinct TS.trackingid, TS.fullname,TS.bankaccount,TS.ifsc from transaction_safexpay_details as TS  where TS.trackingid=?  ");

            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1, iciciTransId);
            System.out.println("QUERY:::::: "+pstmt);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            log.debug("query for customerpayout transaction list-----" + pstmt);
        }
        catch (SQLException se)
        {
            log.error("SQLException----",se);
        }
        catch (SystemError se)
        {
            log.error("SystemError----",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        log.debug("Leaving getPayoutSuccessTransactionDetails");
        return hash;
    }
}
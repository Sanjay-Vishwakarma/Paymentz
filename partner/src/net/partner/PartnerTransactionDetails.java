package net.partner;

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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 10/15/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class PartnerTransactionDetails extends HttpServlet
{
    static Logger log = new Logger(PartnerTransactionDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Entering in PartnerTransactionDetails ");
        String errormsg="";
        HttpSession session = request.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions =new Functions();
        String iciciTransId=null;
        String merchantId = null;
        PrintWriter out = response.getWriter();
        boolean archive=false;
        String accountid =null;
        String status = "";
        String cardtypeId="";
        String cardpresent ="";

        try
        {
            merchantId= ESAPI.validator().getValidInput("memberid",request.getParameter("memberid"),"Numbers",10,false);
            iciciTransId = ESAPI.validator().getValidInput("trackingid",request.getParameter("trackingid"),"Numbers",10,false);
            archive = Boolean.parseBoolean(ESAPI.validator().getValidInput("archive",request.getParameter("archive"),"Archive",7,false));
            status = request.getParameter("status");
            cardpresent=request.getParameter("cardpresent");
             cardtypeId=request.getParameter("cardtypeid");
        }
        catch(ValidationException e)
        {
            log.error("ValidationException----",e);
            log.error("TrackingID or Archive or Accountid is Wrong",e);
            archive=false;
        }
        HashMap hash = null;
        Hashtable actionhash=null;
        List<RuleMasterVO> ruleMasterVOList=null;
        String gateway ="";
        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }
        try
        {
            ActionEntry entry = new ActionEntry();
            if(cardpresent.equals("Y")){
                hash = getCardPresentTransactionDetails(merchantId, iciciTransId, archive, gateway, status);
                actionhash = entry.getActionHistoryByTrackingIdAndGatewayCP(iciciTransId,gateway);
            }else{
                hash = getTransactionDetails(merchantId, iciciTransId, archive,gateway,status);
                actionhash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId,gateway);
            }
            String message= (String) request.getAttribute("message");
            ruleMasterVOList = getFraudDetails(merchantId, iciciTransId);
            request.setAttribute("transactionDetailsFraud",ruleMasterVOList);
            request.setAttribute("transactionsDetails", hash);
            // Start : Added for Action and Status Entry in Action History table

            entry.closeConnection();
            request.setAttribute("cardtypeid", cardtypeId);
            request.setAttribute("actionHistory", actionhash);
            request.setAttribute("memberid",merchantId);
            request.setAttribute("message",message);
            // End : Added for Action and Status Entry in Action History table

            hash=null;
            RequestDispatcher view = request.getRequestDispatcher("/partnerTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            view.forward(request, response);
        }
        catch (SystemError se)
        {

            log.error("SystemError----",se);
            errormsg.concat("No Record Found");
            out.println(Functions.ShowConfirmation("Sorry", "No records found. Invalid TrackingID"));
        }
        catch (Exception e)
        {
            log.error("Exception----",e);
            errormsg.concat("No Record Found");
            /*out.println(Functions.ShowConfirmation("Sorry", "No records found.Invalid TrackingID"));*/
        }
    }

    private HashMap getTransactionDetails(String memberId, String iciciTransId, boolean archive,String gatewayType, String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        HashMap hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String tableName = Database.getTableName(gatewayType);
        String tableName2= "bin_details";

        if (archive)
        {
            tableName = "transaction_common_archive";
            tableName2 ="bin_details_archive";
        }

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status,ccnum, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM "+tableName+" AS td, members WHERE td.toid=members.memberid AND trackingid= ? AND toid = ?");
            }
            else if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select status,remark as authqsiresponsecode ,icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and icicitransid= ? and toid = ? ");
            }
            else if(tableName.equals("transaction_qwipi"))
            {
                query = new StringBuffer("SELECT STATUS, ccnum, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country,bd.bin_brand, bd.bin_trans_type, bd.bin_card_type, bd.bin_card_category, bd.bin_usage_type,bd.first_six,bd.last_four FROM " + tableName + " td,members, bin_details AS bd WHERE td.toid=members.memberid AND trackingid= ? AND toid = ? AND bd.icicitransid=td.trackingid");
            }
            else
            {
                query = new StringBuffer("SELECT td.status,ccnum, td.remark AS authqsiresponsecode,td.trackingid AS icicitransid,refundamount,captureamount,chargebackamount,payoutamount,td.timestamp,td.name,td.amount,td.paymentid,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country,td.eci AS ECI,td.emiCount as Emi,td.currency,td.templatecurrency,td.templateamount,td.notificationUrl,td.redirecturl,td.cardtype as cardtype,td.paymodeid as paymodeid,td.description as description,td.orderdescription as orderdescription,td.terminalid as terminalid,bd.bin_brand,bd.country_name,bd.issuing_bank,bd.bin_trans_type,bd.bin_card_type,bd.bin_card_category,bd.bin_usage_type, bd.isSuccessful,bd.isSettled,bd.isRefund,bd.isChargeback,bd.isFraud,bd.isRollingReserveKept,bd.isRollingReserveReleased,bd.first_six,bd.last_four,tcd.ipaddress,members.memberid,members.contact_persons,members.contact_emails,td.customerId,td.ipAddress as 'merchantIp',tcd.transactionReceiptImg FROM " + tableName + "  AS td, "+tableName2+" AS bd," +tableName+"_details AS tcd, members WHERE td.toid=members.memberid AND td.trackingid= ? AND toid = ? AND td.trackingid=bd.icicitransid and td.trackingid=tcd.trackingid");
            }
            log.error("QUERY IN PARTNER INTERFACE++++++++ "+query.toString());

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            log.debug("query----"+pstmt);
            log.error("query for partner interface for +++++ "+pstmt);
            hash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            String fraudscore="N/A";
            String fraudremark="N/A";
            String query1="select score, fs_responsedesc from at_fraud_trans_details where trackingid=?";
            pstmt=conn.prepareStatement(query1);
            pstmt.setString(1,iciciTransId);
            log.debug("query pstmt:::::"+pstmt);

            rs=pstmt.executeQuery();
            if(rs.next())
            {
                fraudscore=rs.getString("score");
                fraudremark=rs.getString("fs_responsedesc");
            }
            hash.put("fraudscore",fraudscore);
            hash.put("fraudremark", fraudremark);
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

        ctx.log("Leaving getTransactionDetails");
        return hash;
    }

    // To retrieve card present details.
    private HashMap getCardPresentTransactionDetails(String memberId, String iciciTransId, boolean archive,String gatewayType, String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        HashMap hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String tableName = Database.getTableName(gatewayType);
        if (archive)
        {
            tableName = "transaction_icicicredit_archive";
        }
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status,ccnum, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_card_present AS td, members WHERE td.toid=members.memberid AND trackingid= ? AND toid = ?");
            }
            else if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select status,remark as authqsiresponsecode ,icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and icicitransid= ? and toid = ? ");

            }
            else if(tableName.equals("transaction_qwipi"))
            {
                query = new StringBuffer("SELECT STATUS, ccnum, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country,bd.bin_brand, bd.bin_trans_type, bd.bin_card_type, bd.bin_card_category, bd.bin_usage_type FROM " + tableName + " td,members, bin_details AS bd WHERE td.toid=members.memberid AND trackingid= ? AND toid = ? AND bd.icicitransid=td.trackingid");
            }
            else
            {
                query = new StringBuffer("SELECT td.status,ccnum, td.remark AS authqsiresponsecode,td.trackingid AS icicitransid,refundamount,captureamount,chargebackamount,payoutamount,td.timestamp,td.name,td.amount,td.paymentid,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country,td.eci AS ECI,td.emiCount as Emi,td.currency,td.templatecurrency,td.templateamount,td.notificationUrl,td.redirecturl,td.cardtype as cardtype,td.paymodeid as paymodeid,td.description as description,td.orderdescription as orderdescription,td.terminalid as terminalid,tcd.ipaddress,members.memberid,members.contact_persons,members.contact_emails,td.customerId,td.ipAddress as 'merchantIp' FROM transaction_card_present  AS td,transaction_common_details_card_present AS tcd, members WHERE td.toid=members.memberid AND td.trackingid= ? AND toid = ? and td.trackingid=tcd.trackingid");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2, memberId);
            log.debug("query----"+pstmt);
            hash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            String fraudscore="N/A";
            String fraudremark="N/A";
            String query1="select score, fs_responsedesc from at_fraud_trans_details where trackingid=?";
            pstmt=conn.prepareStatement(query1);
            pstmt.setString(1,iciciTransId);
            log.debug("query pstmt:::::"+pstmt);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                fraudscore=rs.getString("score");
                fraudremark=rs.getString("fs_responsedesc");
            }
            hash.put("fraudscore",fraudscore);
            hash.put("fraudremark", fraudremark);
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
}
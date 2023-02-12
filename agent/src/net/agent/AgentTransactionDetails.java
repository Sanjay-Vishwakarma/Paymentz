package net.agent;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
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
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Saurabh
 * Date: 2/21/14
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentTransactionDetails extends HttpServlet
{
    private static Logger log = new Logger(AgentTransactionDetails.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Entering in AgentTransactionDetails ");
        StringBuffer errormsg= new StringBuffer();
        HttpSession session = request.getSession();
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/agent/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions = new Functions();
        String iciciTransId=null;
        String merchantId = null;
        PrintWriter out = response.getWriter();
        boolean archive=false;
        String accountid =null;
        String status = "";
        String cardtype=request.getParameter("cardtype");
        RequestDispatcher view = request.getRequestDispatcher("/agentTransactionDetails.jsp?ctoken="+user.getCSRFToken());

        try
        {
            merchantId= ESAPI.validator().getValidInput("memberid",request.getParameter("memberid"),"Numbers",10,false);
            iciciTransId = ESAPI.validator().getValidInput("trackingid",request.getParameter("trackingid"),"Numbers",10,false);
            archive = Boolean.parseBoolean(ESAPI.validator().getValidInput("archive",request.getParameter("archive"),"Archive",7,false));
            status = request.getParameter("status");
            //accountid = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",11,false);
        }
        catch(ValidationException e)
        {
            log.error("TrackingID or Archive or Accountid is Wrong",e);
            archive=false;
        }
        Hashtable hash = null;
        Hashtable actionhash=null;
        String gateway =null;
        if(functions.isValueNull(accountid))
        {
            gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        }
        try
        {

            ActionEntry entry = new ActionEntry();
            if(cardtype.equals("CP")){
                hash = getCPTransactionDetails(merchantId, iciciTransId, archive, gateway, status);
                actionhash = entry.getActionHistoryByTrackingIdAndGatewayCP(iciciTransId, gateway);
            }else
            {
                hash = getTransactionDetails(merchantId, iciciTransId, archive, gateway, status);
                actionhash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId, gateway);
            }



            request.setAttribute("transactionsDetails", hash);
            // Start : Added for Action and Status Entry in Action History table
            entry.closeConnection();
            request.setAttribute("actionHistory", actionhash);
            // End : Added for Action and Status Entry in Action History table

            hash=null;
            request.setAttribute("memberid",merchantId);
            request.setAttribute("trackingid",iciciTransId);
            view.forward(request, response);
        }
        catch (SystemError se)
        {
            log.error("SystemError::",se);
            errormsg.append("No Record Found");
            request.setAttribute("errormsg",errormsg.toString());
            view.forward(request, response);
        }
        catch (Exception e)
        {
            log.error("Exception::",e);
            errormsg.append("No Record Found");
            request.setAttribute("errormsg",errormsg.toString());
            view.forward(request, response);
        }
    }

    private Hashtable getTransactionDetails(String memberId, String iciciTransId, boolean archive,String gatewayType,String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        Hashtable actionhash=null;
        String tableName = Database.getTableName(gatewayType);

        if (archive)
        {
            tableName = "transaction_icicicredit_archive";
        }
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') as \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_common AS td, members WHERE td.toid=members.memberid AND trackingid= ? AND toid = ?");
            }
            else if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select status,authqsiresponsecode ,icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y %T') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and icicitransid= ? and toid = ? ");
            }
            else if(tableName.equals("transaction_qwipi"))
            {
                query = new StringBuffer("select status,remark as authqsiresponsecode,trackingid as icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y %T') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and trackingid= ? and toid = ? ");
            }
            else
            {
                query = new StringBuffer("SELECT status,trackingid AS icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y %T') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country, bd.bin_brand,bd.bin_sub_brand,bd.bin_card_type,bd.bin_card_category,bd.bin_usage_type FROM " + tableName + " td,members, bin_details AS bd WHERE td.toid=members.memberid AND trackingid= ? AND toid = ? AND td.trackingid=bd.icicitransid");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            //log.debug(query);
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
    private Hashtable getCPTransactionDetails(String memberId, String iciciTransId, boolean archive,String gatewayType,String status) throws SystemError
    {
        ServletContext ctx = getServletContext();
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        Hashtable actionhash=null;
        String tableName = Database.getTableName(gatewayType);

        if (archive)
        {
            tableName = "transaction_icicicredit_archive";
        }
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query =null;
            if (status.equalsIgnoreCase("Begun Processing"))
            {
                query = new StringBuffer("SELECT status, remark AS authqsiresponsecode,trackingid AS icicitransid ,NAME , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y') as \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_common AS td, members WHERE td.toid=members.memberid AND trackingid= ? AND toid = ?");
            }
            else if(tableName.equals("transaction_icicicredit")||tableName.equals("transaction_icicicredit_archive"))
            {
                query = new StringBuffer("select status,authqsiresponsecode ,icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y %T') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and icicitransid= ? and toid = ? ");
            }
            else if(tableName.equals("transaction_qwipi"))
            {
                query = new StringBuffer("select status,remark as authqsiresponsecode,trackingid as icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,date_format(from_unixtime(td.dtstamp),'%d-%m-%Y %T') as \"date\",emailaddr as email,td.city as city,street,td.state as state,td.country as country from "+tableName+" td,members where td.toid=members.memberid and trackingid= ? and toid = ? ");
            }
            else
            {
                query = new StringBuffer("SELECT status,trackingid AS icicitransid ,name , amount ,td.zip,td.telnocc,td.telno,DATE_FORMAT(FROM_UNIXTIME(td.dtstamp),'%d-%m-%Y %T') AS \"date\",td.emailaddr AS email,td.city AS city,street,td.state AS state,td.country AS country FROM transaction_card_present td,members WHERE td.toid=members.memberid AND trackingid= ? AND toid = ? ");
            }
            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            pstmt.setString(2,memberId);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            //log.debug(query);
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

}

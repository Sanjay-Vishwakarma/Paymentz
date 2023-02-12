package net.partner;

import com.directi.pg.*;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;

public class RejectedTransactionDetails extends HttpServlet
{
    private static Logger log = new Logger(RejectedTransactionDetails.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RejectedTransactionDetails.class.getName());
    private Functions functions = new Functions();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //boolean flag=false;
        String errorMsg="";
        String EOL = "<BR>";

        String partnerId = (String) session.getAttribute("merchantid");

        if(functions.isValueNull(req.getParameter("action")) && "RejectedTransactionsList".equals(req.getParameter("action")) && functions.isValueNull(req.getParameter("STrackingid")) )
        {
            String icicitransid = req.getParameter("STrackingid");
            try
            {
                Hashtable hash = getTransactionDetails(icicitransid);
                req.setAttribute("transactionsdetails", hash);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (SystemError se)
            {
                log.error("SystemError:::::",se);
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errorMsg + se.getMessage() + EOL + "</b></font></center>";
                req.setAttribute("errormessage",errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (Exception e)
            {
                errorMsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errorMsg + e.getMessage() + EOL + "</b></font></center>";
                log.error("Exception:::::", e);
                req.setAttribute("errormessage",errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            return;
        }

        String toId = req.getParameter("toid");
        String trackingid = req.getParameter("STrackingid");
        transactionLogger.debug("trackingid----"+trackingid);

        try
        {
            if(!functions.isValueNull(toId))
            {
                toId=partner.getPartnerMemberRS(partnerId);
            }

            Hashtable hash = listTransactions(trackingid);
            req.setAttribute("transactionsdetails", hash);
            RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            log.error("SystemError::::::",se);
            req.setAttribute("errormessage",se.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("Exception::::",e);
            req.setAttribute("errormessage",e.getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/rejectedTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }
    public Hashtable listTransactions(String trackingid) throws SystemError
    {
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            StringBuilder query = new StringBuilder("SELECT tfl.*,from_unixtime(tfl.dtstamp) as transactiontime,m.* from transaction_fail_log as tfl left join members as m on tfl.toid=m.memberid where id=?");

            pstmt = conn.prepareStatement(query.toString());
            pstmt.setString(1,trackingid);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
            log.debug("query in getTransactionDetails()---" + pstmt);

        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }
    public Hashtable getTransactionDetails(String iciciTransId) throws SystemError
    {
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT tfl.*,from_unixtime(tfl.dtstamp) as transactiontime,m.* from transaction_fail_log as tfl left join members as m on tfl.toid=m.memberid where id=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,iciciTransId);
            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return hash;
    }
}

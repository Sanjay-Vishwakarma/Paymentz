package net.partner;

import com.directi.pg.*;
import com.manager.dao.AgentDAO;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Kanchan on 03-02-2021.
 * Time: 5.20 PM
 */
public class AgentMerchantTransactionSummary extends HttpServlet
{
    private static Logger log = new Logger(AgentMerchantTransactionSummary.class.getName());
    PartnerFunctions partner= new PartnerFunctions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("Entering in merchant transaction summary list....");
        HttpSession session= request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken:: "+request.getParameter("ctoken"));
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("partner is logout");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        String error="";

        String agentid= request.getParameter("agentid");
        Hashtable statushash= null;
        request.setAttribute("agentid",agentid);

        try
        {
            statushash= getReport(agentid);
        }
        catch (SystemError s)
        {
            log.error("error while collection status report",s);
        }

        request.setAttribute("status_report",statushash);
        request.setAttribute("error",error);
        RequestDispatcher rd= request.getRequestDispatcher("/agentMerchantTransactionSummary.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request,response);
    }

    public Hashtable getReport(String agentid) throws SystemError
    {
        Connection con= null;
        PreparedStatement ps= null;
        ResultSet rs= null;

        con= Database.getConnection();

        AgentDAO agentDAO= new AgentDAO();
        String allmemberid= agentDAO.getAgentMemberList(agentid);

        StringBuffer reportquery = new StringBuffer("SELECT status,COUNT(*) AS count,SUM(amount) AS amount FROM transaction_common WHERE toid IN (" + allmemberid + ") GROUP BY status");

      //  StringBuffer reportquery = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN (" + allmemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN (" + allmemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN (" + allmemberid + ") group by status) as temp group by status");

        StringBuffer countquery = new StringBuffer("SELECT COUNT(*) AS count,SUM(amount) AS grandtotal FROM `transaction_common` WHERE toid IN (" + allmemberid + ")");

       // StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN(" + allmemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN(" + allmemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN(" + allmemberid + ") group by status) as temp");

        log.error("=========i======="+ reportquery);
        log.error("=========j======="+ countquery);

        Hashtable statusreport = new Hashtable();

        try
        {
            ps= con.prepareStatement(reportquery.toString());
            rs= ps.executeQuery();
            statusreport= Database.getHashFromResultSet(rs);
            ps=con.prepareStatement(countquery.toString());
            rs=ps.executeQuery();
            int total=0;
            String totalamount= "";
            if (rs.next())
            {
                total= rs.getInt("count");
                totalamount= rs.getString("grandtotal");
            }
            if (totalamount!= null)
            {
                statusreport.put("grandtotal",totalamount);
                statusreport.put("totalrecords", "" + total);
            }
            else
            {
                statusreport.put("grandtotal", "00");
                statusreport.put("totalrecords", "00");
            }
            statusreport.put("records", "0");
            if (total > 0)
                statusreport.put("records", "" + (statusreport.size() - 2));
        }
        catch (SQLException e)
        {
           log.error("sql error while executing status report",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(con);
        }
        log.debug(statusreport);
        return statusreport;
    }
}

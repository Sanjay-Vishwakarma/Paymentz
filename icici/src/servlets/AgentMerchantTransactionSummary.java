import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
 * Created with IntelliJ IDEA.
 * User: WAHEED
 * Date: 2/11/14
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentMerchantTransactionSummary extends HttpServlet
{
    private static Logger log = new Logger(AgentMerchantTransactionSummary.class.getName());

    /**
     * doGet Method
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * doPost Method
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in MerchantTransSummaryList");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken===" + req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String error = "";

        String agentid = req.getParameter("agentid");
        Hashtable statushash = null;
        req.setAttribute("agentid", agentid);
        try
        {

            statushash = getReport(agentid);
        }
        catch (SystemError systemError)
        {
            log.error("Error while collection status Report", systemError);
        }

        req.setAttribute("status_report", statushash);
        req.setAttribute("error", error);
        RequestDispatcher rd = req.getRequestDispatcher("/agentMerchantTransactionSummary.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    /**
     * Report Details
     *
     * @param agentid
     * @return
     * @throws SystemError
     */
    public Hashtable getReport(String agentid) throws SystemError
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = Database.getConnection();
        AgentDAO agentDAO = new AgentDAO();
        String allMemberid = agentDAO.getAgentMemberList(agentid);
      //  StringBuffer reportquery = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN (" + allMemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN (" + allMemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN (" + allMemberid + ") group by status) as temp group by status");
      //  StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN(" + allMemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN(" + allMemberid + ") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN(" + allMemberid + ") group by status) as temp");

        StringBuffer reportquery = new StringBuffer("SELECT status, SUM(count) AS count,SUM(amount) AS amount FROM (SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_common WHERE toid IN ("+ allMemberid + ") GROUP BY status)AS temp GROUP BY status");
        StringBuffer countquery = new StringBuffer("SELECT SUM(count) AS count,SUM(amount) AS grandtotal FROM (SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_common WHERE toid IN("+ allMemberid +") GROUP BY status) AS temp");
        log.debug("========i==" + reportquery);
        log.debug("========j==" + countquery);

        Hashtable statusreport = new Hashtable();
        try
        {
            ps = conn.prepareStatement(reportquery.toString());
            log.error("REPORT QUERY+++ "+reportquery);
            rs = ps.executeQuery();
            statusreport = Database.getHashFromResultSet(rs);
            ps = conn.prepareStatement(countquery.toString());
            log.error("COUNT QUERY+++ "+countquery);
            rs = ps.executeQuery();
            int total = 0;
            String totalamount = "";
            if (rs.next())
            {
                total = rs.getInt("count");
                totalamount = rs.getString("grandtotal");
            }
            if (totalamount != null)
            {
                statusreport.put("grandtotal", totalamount);
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
            log.error("sql error while executing status report", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        log.debug(statusreport);
        return statusreport;
    }
}
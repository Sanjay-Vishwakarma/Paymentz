import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
 * Date: 6/2/14
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerMerchantTransSummary extends HttpServlet
{
    private static Logger log = new Logger(PartnerMerchantTransSummary.class.getName());

    /**
     * doGet Method
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
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in MerchantTransSummaryList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String error= "";

        String partnerid=req.getParameter("partnerid");
        Hashtable statushash=null;
        req.setAttribute("partnerid", partnerid);
        try
        {
            statushash=getReport(partnerid);
        }
        catch (SystemError systemError)
        {
            log.error("Error while collection status Report",systemError);
        }

        req.setAttribute("status_report",statushash);
        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantTransSummary.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    /**
     * Get report details
     * @param partnerid
     * @return
     * @throws SystemError
     */
    public Hashtable getReport(String partnerid) throws SystemError
    {
        Connection conn = null;
        conn = Database.getRDBConnection();
        String allMemberid=getPartnerMemberRS(partnerid);
      //  StringBuffer reportquery = new StringBuffer("select status, SUM(count) as count,SUM(amount) as amount from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN ("+allMemberid+") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN ("+allMemberid+") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN ("+allMemberid+") group by status) as temp group by status");
     //   StringBuffer countquery = new StringBuffer("select SUM(count) as count,SUM(amount) as grandtotal from (select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_qwipi where toid IN("+allMemberid+") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_ecore where toid IN("+allMemberid+") group by status UNION select STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount from transaction_common where toid IN("+allMemberid+") group by status) as temp");

        StringBuffer reportquery= new StringBuffer("SELECT status, SUM(count) AS count,SUM(amount) AS amount FROM (SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_common WHERE toid IN ("+allMemberid+") GROUP BY status) AS temp GROUP BY status");
        StringBuffer countquery = new StringBuffer("SELECT SUM(count) AS count,SUM(amount) AS grandtotal FROM (SELECT STATUS,COUNT(*) AS COUNT,SUM(amount) AS amount FROM transaction_common WHERE toid IN("+allMemberid+") GROUP BY status) AS temp");
        log.debug("========i=="+reportquery);
        log.debug("========j=="+countquery);
        Hashtable statusreport= new Hashtable();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            log.error("reportquery for PartnerMerchantTransSummary++ "+reportquery);
            ps =  conn.prepareStatement(reportquery.toString());
            rs = ps.executeQuery();
            statusreport=Database.getHashFromResultSet(rs);
            log.error("countquery for PartnerMerchantTransSummary++ "+countquery);
            ps = conn.prepareStatement(countquery.toString());
            rs = ps.executeQuery();
            int total = 0;
            String totalamount="";
            if (rs.next())
            {
                total = rs.getInt("count");
                totalamount=rs.getString("grandtotal");
            }
            if(totalamount!=null)
            {
                statusreport.put("grandtotal",totalamount);
                statusreport.put("totalrecords", "" + total);
            }
            else
            {
                statusreport.put("grandtotal","00");
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
            Database.closeConnection(conn);
        }
        log.debug(statusreport);
        return statusreport;
    }

    /**
     * Getting all member id
     * @param partnerId
     * @return
     */
    public String getPartnerMemberRS(String partnerId)
    {
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        Connection con=null;
        String sMemberList="";
        try
        {
            con=Database.getRDBConnection();
            String qry="select memberid from members where partnerId=?";
            pstmt= con.prepareStatement(qry);
            pstmt.setString(1,partnerId);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                sMemberList = sMemberList + rs.getString("memberid") + ",";
            }
            if (!sMemberList.equalsIgnoreCase(""))
            {
                sMemberList = sMemberList.substring(0,sMemberList.length()-1);
            }
            else
            {
                sMemberList = "0";
            }
        }
        catch(Exception e)
        {
            log.error("error",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return sMemberList;
    }
}

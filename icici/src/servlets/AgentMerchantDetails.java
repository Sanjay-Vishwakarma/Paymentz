import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
public class AgentMerchantDetails extends HttpServlet
{
    private static Logger log = new Logger(AgentMerchantDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in ListAgentDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        ResultSet rs=null;
        PreparedStatement ps = null;

        int records=15;
        int pageno=1;
        Functions functions = new Functions();
        Hashtable hash = null;

        String agentId = req.getParameter("agentid");
        log.debug("agentID======="+agentId);
        String agentName = req.getParameter("agentName");

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        log.debug("Page No ======================="+pageno);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {

            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT mam.memberid,login,company_name, contact_emails FROM members AS m INNER JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE");

            StringBuffer countquery = new StringBuffer("SELECT COUNT(*) FROM members AS m INNER JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE");

            if (functions.isValueNull(agentId))
            {
                query.append(" mam.agentId=" + ESAPI.encoder().encodeForSQL(me,agentId));
                countquery.append(" mam.agentId=" + ESAPI.encoder().encodeForSQL(me,agentId));
            }

            query.append(" order by memberid LIMIT " + start + "," + end);

            ps = conn.prepareStatement(query.toString());
            //ps.setString(1,agentId);
            log.debug("QRY--->"+ps);
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSetForTransactionEntry(rs);

            ps = conn.prepareStatement(countquery.toString());
            // ps.setString(1,agentId);
            rs = ps.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);
            req.setAttribute("agentid",agentId);
            RequestDispatcher rd = req.getRequestDispatcher("/agentMerchantInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError s)
        {
            log.error("System error while performing select query",s);
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
}

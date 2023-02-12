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
public class PartnerMerchantDetails extends HttpServlet
{
    private static Logger log = new Logger(PartnerMerchantDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in partnerMerchantsDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        int records=15;
        int pageno=1;
        Functions functions = new Functions();
        Hashtable hash = null;
        String partnerid = req.getParameter("partnerid");
        log.debug("partnerid======="+partnerid);
        int start = 0; // start index
        int end = 0; // end index
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select memberid,login,company_name, contact_emails from members where partnerId>0");
            StringBuffer countquery = new StringBuffer("select count(*) from members where partnerId>0");

            if (functions.isValueNull(partnerid))
            {
                query.append(" and partnerId=" + ESAPI.encoder().encodeForSQL(me,partnerid));
                countquery.append(" and partnerId=" + ESAPI.encoder().encodeForSQL(me,partnerid));
            }
            query.append(" order by memberid desc LIMIT " + start + "," + end);

            log.info("query-----------"+query);
            log.info("countquery-----------"+countquery);

            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSetForTransactionEntry(rs);

            ps=conn.prepareStatement(countquery.toString());
            rs = ps.executeQuery();
            rs.next();

            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);
            req.setAttribute("partnerid",partnerid);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantInterface.jsp?ctoken="+user.getCSRFToken());
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

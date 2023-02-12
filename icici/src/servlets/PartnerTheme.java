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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/2/16
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerTheme extends HttpServlet
{
    static Logger logger = new Logger(PartnerTheme.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/partnerTheme.jsp?ctoken="+user.getCSRFToken());

        int records=15;
        int pageno=1;

        String themeType = req.getParameter("theme_type");

        if (!ESAPI.validator().isValidInput("theme_type", themeType, "SafeString", 100, true))
        {
            req.setAttribute("errormessage","Invalid theme_type Theme");
            rd.forward(req,res);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection conn = null;
        Hashtable hash = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();

            StringBuffer query = new StringBuffer("select theme_id,current_theme,theme_type from theme where theme_id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from theme where theme_id>0 ");


            if (functions.isValueNull(themeType))
            {
                query.append(" and theme_type='" + ESAPI.encoder().encodeForSQL(me,themeType) + "'");
                countquery.append(" and theme_type='" + ESAPI.encoder().encodeForSQL(me,themeType) + "'");
            }


            query.append(" order by theme_id desc LIMIT " + start + "," + end);

            //System.out.println("query---"+query);


            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
        }
        catch (SystemError s)
        {
            logger.error("SystemError:::::::", s);
            //req.setAttribute("errormessage","Internal error while processing your request");
            Functions.ShowMessage("Error", "Internal error while processing your request");
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
            //req.setAttribute("errormessage","Internal error while processing your request");
            Functions.ShowMessage("Error", "Internal error while processing your request");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        rd.forward(req,res);
        return;
    }

}

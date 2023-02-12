import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

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
/**
 * Created with IntelliJ IDEA.
 * User: Naushad
 * Date: 9/2/16
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModuleMappingList extends HttpServlet
{
    static Logger logger = new Logger(AdminModuleMappingList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String adminId = req.getParameter("adminid");

        RequestDispatcher rd = req.getRequestDispatcher("/adminModuleMappingList.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("adminid", adminId, "SafeString", 100, false))
        {
            req.setAttribute("errormessage", "Please select admin user");
            rd.forward(req, res);
        }

        Connection conn = null;
        ResultSet rs = null;
        Hashtable hash = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select amm.mappingid,amm.moduleid,ams.modulename FROM admin_modules_mapping as amm join admin_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and adminid="+adminId);
            StringBuffer countquery = new StringBuffer("select count(*)FROM admin_modules_mapping as amm join admin_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and adminid="+adminId);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            rd.forward(req, res);
        }
        catch (SystemError s)
        {
            logger.error("SystemError:::::::", s);
            req.setAttribute("errormessage","Internal error while processing your request");
            rd.forward(req,res);
            return;
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
            req.setAttribute("errormessage","Internal error while processing your request");
            rd.forward(req,res);
            return;
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }
}

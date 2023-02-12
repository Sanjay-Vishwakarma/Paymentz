package net.partner;

import com.directi.pg.*;
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
import java.util.*;


/**
 * Created by admin on 5/12/2016.
 */
public class PartnerModuleMappingList extends HttpServlet
{
    static Logger logger = new Logger(PartnerModuleMappingList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        Merchants merchants = new Merchants();
        HttpSession session = req.getSession();

        if (!merchants.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String moduleId=req.getParameter("moduleid");
        String partnerid= (String) session.getAttribute("merchantid");
        String userId = req.getParameter("userid");

        String login = req.getParameter("login");

        req.setAttribute("login",login);
        req.setAttribute("userid",userId);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerModuleMappingList.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("userid", userId, "SafeString", 100, false))
        {
            req.setAttribute("errormessage", "Please select Partner user");
            rd.forward(req, res);
        }

        Connection conn = null;
        Hashtable hash = null;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select amm.mappingid,amm.moduleid,ams.modulename FROM partner_users_modules_mapping as amm join partner_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);
            StringBuffer countquery = new StringBuffer("select count(*)FROM partner_users_modules_mapping as amm join partner_modules_master as ams on amm.moduleid=ams.moduleid and amm.moduleid>0 and userid="+userId);

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

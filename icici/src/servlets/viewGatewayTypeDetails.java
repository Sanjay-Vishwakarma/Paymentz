import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;


/**
 * Created with IntelliJ IDEA.
 * User: mukesh.a
 * Date: 2/03/14
 * Time: 19:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class viewGatewayTypeDetails extends HttpServlet
{
    private static Logger log = new Logger(viewGatewayTypeDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in viewGatewayTypeDetails");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String pgtypeid="";
        String action = "";
        String isReadOnly = "view";

        try
        {
            pgtypeid = ESAPI.validator().getValidInput("pgtypeid",req.getParameter("pgtypeid"),"Numbers",10,true);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,true);
        }
        catch (ValidationException e)
        {
            log.error("ValidationException while GatewayTypeDetails ",e);
        }
        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try
        {
            Hashtable hash = null;
            conn = Database.getRDBConnection();
            String query = "SELECT * FROM gateway_type WHERE pgtypeid=?";
            ps = conn.prepareStatement(query);
            ps.setString(1,pgtypeid);

            rs = ps.executeQuery();
            hash = Database.getHashFromResultSet(rs);
            if(action.equalsIgnoreCase("modify"))
            {
                isReadOnly="modify";
            }

            req.setAttribute("isreadonly",isReadOnly);
            req.setAttribute("gatewaytypedetails",hash);
            req.setAttribute("pgtypeid",pgtypeid);
            req.setAttribute("action",action);

            RequestDispatcher rd = req.getRequestDispatcher("/viewGatewayTypeDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {

            log.error("Sql Exception :::::",systemError);
            log.error("Exception while updating template fees : " + systemError.getMessage());
        }
        catch (SQLException e)
        {
            log.error("Sql Exception :::::",e);
            log.error("Exception while updating template fees : " + e.getMessage());

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
}

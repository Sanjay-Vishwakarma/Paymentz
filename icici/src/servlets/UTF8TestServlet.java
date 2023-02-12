import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/30/14
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class UTF8TestServlet extends HttpServlet
{
    static Logger logger = new Logger(UTF8TestServlet.class.getName());

    public static List<String> getData()
    {
        int k=0;
        Connection con = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        List<String> list=null;
        try
        {
            con = Database.getConnection();
            preparedStatement=null;
            String qry="select * from test";
            preparedStatement=con.prepareStatement(qry);
            rs=preparedStatement.executeQuery();
            list=new ArrayList<String>();
            while(rs.next())
            {
                list.add(rs.getString("name"));
            }
        }
        catch (Exception systemError)
        {
            logger.error("Catch Exception..",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return list;
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering in UTF8TestServlet");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        try
        {
            String enc = request.getCharacterEncoding();
            logger.debug("Request Is Come======" + request.getParameter("name"));
            insert(request.getParameter("name"));
            request.setAttribute("list",getData());
            RequestDispatcher rd = request.getRequestDispatcher("/utf8test.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.debug("Error"+e.getStackTrace());
        }
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        String enc = request.getCharacterEncoding();
        logger.debug("Entering in UTF8TestServlet");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        try
        {
            logger.debug("Request Is Come======" + request.getParameter("name"));
            String name=new String(request.getParameter("name").getBytes("8859_1"),enc);
            insert(name);
            request.setAttribute("list",getData());
            RequestDispatcher rd = request.getRequestDispatcher("/utf8test.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.debug("Error"+e.getStackTrace());
        }
    }

    public int insert(String name)
    {
        Connection con = null;
        PreparedStatement preparedStatement=null;
        int k=0;
        try
        {
            con= Database.getConnection();
            preparedStatement=null;
            String qry="insert into test values(null,?)";
            preparedStatement=con.prepareStatement(qry);
            preparedStatement.setString(1,name);
            k=preparedStatement.executeUpdate();

        }
        catch (Exception systemError)
        {
            logger.error("Catch Exception..",systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return k;
    }
}

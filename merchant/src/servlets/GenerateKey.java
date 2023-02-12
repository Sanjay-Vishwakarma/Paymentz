import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
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
import java.util.Date;
import java.util.Random;

//import java.io.PrintWriter;


public class GenerateKey extends HttpServlet
{
    private static Logger Log = new Logger(GenerateKey.class.getName());
   // private static SystemAccessLogger accessLogger = new SystemAccessLogger(GenerateKey.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");
        HttpSession session = request.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   Log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Log.debug("CSRF check successful ");

        //PrintWriter out = response.getWriter();

        //String data = request.getParameter("data");




        //Generates a unique 32 byte key for the merchant
        Log.debug("Entering generateKey");
        String pwdData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = pwdData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuilder key = new StringBuilder();
        int index = -1;
        for (int i = 0; i < 32; i++)
        {
            index = rand.nextInt(len);
            key.append(pwdData.substring(index, index + 1));
        }
        request.setAttribute("key", key.toString());
        String merchantid = (String) session.getAttribute("merchantid");

        StringBuilder updQuery = new StringBuilder("update members");
        updQuery.append(" set clkey = ?  where memberid= ? " );
        Connection conn = null;
        PreparedStatement pstmt= null;

        try
        {
            conn = Database.getConnection();
            pstmt= conn.prepareStatement(updQuery.toString());
            pstmt.setString(1,key.toString());
            pstmt.setString(2,merchantid);
            pstmt.executeUpdate();
        }
        catch (SystemError se)
        {
            Log.error("System  Error:::::", se);
            //System.out.println(se.toString());
        }
        catch (Exception e)
        {
            Log.error("Error!", e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = request.getRequestDispatcher("/generatedkey.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(request, response);
        Log.debug("Leaving generateKey");
    }
}

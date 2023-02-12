package practice;

import com.directi.pg.Logger;
import org.owasp.esapi.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Admin on 7/30/2020.
 */
public class practice_whitelabelinvoice extends HttpServlet
{
    private static Logger log = new Logger(practice_whitelabelinvoice.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException,IOException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req,HttpServletResponse res)throws  ServletException,IOException
    {
        HttpSession httpsession =req.getSession();
        User user =(User)httpsession.getAttribute("ESAPIUserSessionKey");

    }
}

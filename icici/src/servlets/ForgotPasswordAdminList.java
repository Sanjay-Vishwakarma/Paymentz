package servlets;

import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.manager.AdminManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
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
import java.util.HashMap;

/**
 * Created by Balaji on 27-Mar-20.
 */
public class ForgotPasswordAdminList extends HttpServlet
{

    private static Logger log = new Logger(ForgotPasswordAdminList.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        System.out.println("forgot");
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        String errormsg="";
        String mailresult="fail";
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        AdminManager adminManager = new AdminManager();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String actionExecuterId=(String) session.getAttribute("merchantid");
        Functions functions = new Functions();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
       // String partnerId=null;
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/adminlist.jsp?ctoken=" + user.getCSRFToken());

        log.error("inside VerifyMerchantRegistration  servlet--------");
        String signuptime = req.getParameter("signuptime")!=null?req.getParameter("signuptime"):"";
        String adminid = req.getParameter("adminid")!=null?req.getParameter("adminid"):"";
        String login = req.getParameter("login")!=null?req.getParameter("login"):"";
        String contact_emails = req.getParameter("contact_emails")!=null?req.getParameter("contact_emails"):"";


        log.error("signuptime--------"+signuptime);
        log.error("adminid--------"+adminid);
        log.error("login--------"+login);
        log.error("contact_emails--------"+contact_emails);
        //log.error("partnerid--------"+partnerId);

        String redirectpage = null;
        boolean flag = false;

        try
        {
            flag = adminManager.adminForgotPassword(login, user, remoteAddr, header, actionExecuterId,adminid);
        }

        catch (SystemError systemError)
        {
           log.error("Catch SystemError...",systemError);
        }
        log.debug("flag for forget password partner----"+flag);
        if (flag == true)
        {
            req.setAttribute("fmsg","Password changed successfully");
            log.info("send mail on merchant id on this page"+redirectpage);
            rd.forward(req, res);
        }
        else
        {
            req.setAttribute("fmsg","Please try after 1 Hour");
            log.debug("Invalid emailID ");
            //response.sendRedirect("/partner/partnerfpassword.jsp?action=F");
            rd.forward(req, res);
        }
                /*rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);*/

        log.debug("Process Successful for Forgotpassword");
            /*}
            else
            {
                log.debug(" username not present in the system");
                response.sendRedirect("/partner/partnerfpassword.jsp?action=E");
            }*/
    }
}




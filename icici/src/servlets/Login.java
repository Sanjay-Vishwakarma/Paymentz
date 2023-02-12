import com.directi.pg.Admin;
import com.directi.pg.*;

import com.manager.AdminManager;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationLoginException;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import java.util.*;

public class Login extends HttpServlet
{


    private static Logger logger = new Logger(Login.class.getName());
    //private static SystemAccessLogger accessLogger = new SystemAccessLogger(Login.class.getName());

/*    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doPost(request, response);
    }*/

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

        HttpSession session = Functions.getNewSession(request);
        AdminManager adminManager= new AdminManager();

        User user =  (User)session.getAttribute("Anonymous");
        //User user2 =  (User)session.getAttribute("Anonymous");

        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {
            logger.debug("successful    "+user.getCSRFToken());
            session.setAttribute("Anonymous",user);

        }
       /* else if (Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user2) )
        {
            logger.debug("successful    "+user.getCSRFToken());
            session.setAttribute("Anonymous",user2);
        }*/
        else
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");

            return;
        }

        logger.debug("successful");

        String username=null;
        String password=null;


        if(request.getParameter("password")!=null )
        {

            password = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);
            request.setAttribute("password",password);


        }
        String redirectpage = null;
        try
        {
            username = ESAPI.validator().getValidInput("username", request.getParameter("username"), "Login", 100, false);

        }
        catch(ValidationException e)
        {
            logger.error("Given Value is not valid ",e);

            redirectpage = "/admin/login.jsp?action=E&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            return;
        }
        try
        {
            password =ESAPI.validator().getValidInput("password",password,"Password",25,false);
        }
        catch(ValidationException e)
        {
            logger.error("Given Value is not valid ",e);

            RequestDispatcher rd = request.getRequestDispatcher("/admin/login.jsp?action=E&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;

        }

        response.setContentType("text/html");
        PrintWriter printwriter = response.getWriter();

        String temp = (String) session.getAttribute("valid");
        //System.out.println("temp in login---"+session.getAttribute("valid"));

        if (temp == null)
        {
            logger.debug("not a valid user...");
            //response.sendRedirect( "/icici/admin/login.jsp?action=F&ctoken="+user.getCSRFToken());

            redirectpage = "/admin/login.jsp?action=F&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            rd.forward(request, response);

        }
        else
            logger.debug("valid user."+username);

        String adminid= adminManager.getAdminFromLogin(username);
        String status= adminManager.getLoginAndisActive( adminid);
        String login[]= status.split(" ");
        if ( "N".equalsIgnoreCase(login[1]))
        {
            logger.error("INSIDE DEACTIVATE MESSAGE...");
            redirectpage="/admin/login.jsp?action=N&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request,response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }

        try
        {

            /*logger.debug(member);*/

            /* if (member.authenticate.equals("true") || member.authenticate.equals("forgot"))
     {
         int merchantid = member.memberid;

         logger.debug("Memeber activation detail :"+member.activation);
         logger.debug("Memeber authenticate detail :"+member.authenticate);*/

            ESAPI.httpUtilities().setCurrentHTTP(request,response);

            try{
                //User user2 = ESAPI.authenticator().getCurrentUser();

                session.invalidate();
                //Added for PCI Requirment SessionCuncurency
                if(ESAPI.authenticator().getCurrentUser() != null && !ESAPI.authenticator().getCurrentUser().isAnonymous())
                {
                    ESAPI.authenticator().getCurrentUser().logout();
                }
                /*if(ESAPI.authenticator().getCurrentUser() !=null)
                {
                    ESAPI.authenticator().getCurrentUser().logout();
                }*/
/*
                if(user !=null && user2 !=null && user2.isAnonymous() && user.getAccountId()==user2.getAccountId())
                {
                    user2.logout();
                }*/

                user = ESAPI.authenticator().login(request,response);
                //System.out.println("user in login.java----"+user);
                redirectpage = "/welcome.jsp?ctoken="+ESAPI.httpUtilities().getCSRFToken();
            }catch(Exception e)
            {
                session = request.getSession(true);

                session.setAttribute("valid", "true");

                session.setAttribute("Anonymous",user);

                if(e.getMessage().toLowerCase().contains("disabled"))
                {
                    redirectpage="/admin/login.jsp?action=D&ctoken="+user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request,response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else{
                    if(e.getMessage().toLowerCase().contains("locked"))
                    {
                        redirectpage="/admin/login.jsp?action=L&ctoken="+user.getCSRFToken();
                        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                        rd.forward(request,response);
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                    else if(e.getMessage().toLowerCase().contains("accessdenied"))
                    {
                        redirectpage="/admin/login.jsp?action=AD&ctoken="+user.getCSRFToken();
                        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                        rd.forward(request,response);
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                    else
                    {
                        logger.error("error::",e);
                        throw new AuthenticationLoginException("LOGIN FAILED","DUE TO INTERNAL ERROR");
                    }
                }
            }
            Member member = Admin.Admin(username,user);
            session = request.getSession();


            session.setAttribute("username", username);
            session.setAttribute("password", password);

            session.setAttribute("merchantid", "" + member.memberid);

            session.setAttribute("memberObj", member);

            logger.info("Admin user is Authenticate successful ");

            /*if (member.authenticate.equals("true"))
            {
                logger.debug("redirecting to index page");
                redirectpage = "/index.jsp?ctoken="+ESAPI.httpUtilities().getCSRFToken();

            }*/

            if (member.authenticate.equals("forgot"))
            {   logger.debug("redirecting to change password page");
                redirectpage = "/chngpassword.jsp?ctoken="+ESAPI.httpUtilities().getCSRFToken();//"/icici/chngpassword.jsp?MES=FP";
            }



            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -90);

            if(user.getLastPasswordChangeTime().compareTo(cal.getTime())==-1)
            {
                logger.debug("redirecting to change password page");
                redirectpage = "/chngpassword.jsp?MES=UP";
            }


            /* }
            else
            {   logger.info(" User not authenticated redirect to login page");
                redirectpage = "/admin/login.jsp?action=F&ctoken="+user.getCSRFToken();
            }*/

            logger.debug("redirecting to the page "+redirectpage);
            //response.sendRedirect(redirectpage);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            rd.forward(request, response);


        }
        catch (SystemError systemerror)
        {   logger.error("System Error :::::",systemerror);
            /* printwriter.println(Functions.ShowMessage("Error", systemerror.toString()));*/
        }
        catch (Exception exception)
        {   logger.error("Exception:::::",exception);
            /*printwriter.println(Functions.ShowMessage("Error!", exception.toString()));*/
            response.sendRedirect("/icici/admin/login.jsp?action=F");
        }
    }

}

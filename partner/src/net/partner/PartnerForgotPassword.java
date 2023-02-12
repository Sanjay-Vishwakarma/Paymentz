package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Partner;
import com.directi.pg.SystemError;
import com.manager.vo.ActivityTrackerVOs;
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

public class PartnerForgotPassword extends HttpServlet
{
    static Logger log = new Logger(PartnerForgotPassword.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("Anonymous");
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String actionExecuterId=(String) session.getAttribute("merchantid");
        //System.out.println("Anonymous______"+user);
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {

            session.setAttribute("Anonymous",user);

        }
        else
        {
            log.debug("session out");
            response.sendRedirect("/partner/sessionout.jsp");

            return;
        }

        log.debug("successful");

        String redirectpage = null;
        String username=null;

        RequestDispatcher rd = null;
        try
        {
            username = ESAPI.validator().getValidInput("Username",request.getParameter("username"),"SafeString",100,false);
        }

        catch (ValidationException e)
        {

            log.error("Invalid Username" ,e);
            response.sendRedirect( "/partner/partnerfpassword.jsp?action=F");

        }

        String activation = null;
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        boolean flag = false;

        try
        {
            PartnerFunctions partner=new PartnerFunctions();


            //System.out.println("user 1-----"+user);
            //String parentLogin = username;
            String userLogin = partner.getPartnerLoginfromUser(username);
            //System.out.println("user 2-----"+userLogin);

            PartnerFunctions partnerFunctions = new PartnerFunctions();

            if(userLogin!=null)
            {
                //parentLogin = userLogin;
                //System.out.println("user 222-----"+userLogin);
                request.setAttribute("role", "subpartner");
            }
            String role = partnerFunctions.getRole(username);
            if (partnerFunctions.isValueNull(role))
            {
                request.setAttribute("role", role);
            }

            user =  ESAPI.authenticator().getUser(username);
            flag = partner.partnerForgotPassword(username, user,remoteAddr,header,actionExecuterId);
            log.debug("flag for forget password partner----"+flag);
                if (flag == true)
                {
                    log.info("send mail on merchant id on this page"+redirectpage);
                    response.sendRedirect( "/partner/partnerFpasswordSentMail.jsp");
                }
                else
                {
                    log.debug("Invalid emailID ");
                    //response.sendRedirect("/partner/partnerfpassword.jsp?action=F");
                    response.sendRedirect( "/partner/partnerFpasswordSentMail.jsp");
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
        catch (SystemError se)
        {   log.error("System Error:::::::",se);
            out.println(Functions.ShowMessage("Error", se.toString()));
            //System.out.println(se.toString());

        }
        catch (Exception e)
        {   log.error("Exception:::::::",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }

    }
}

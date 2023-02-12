package net.agent;
import com.directi.pg.Functions;
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

public class AgentForgotPassword extends HttpServlet
{
    private static Logger log = new Logger(AgentForgotPassword.class.getName());

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

        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {
            session.setAttribute("Anonymous",user);
        }
        else
        {     log.debug("session out");
            response.sendRedirect("/agent/sessionout.jsp");

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
            String message="Username Field should not be empty OR Invalid Username,kindly enter valid Username.";
            request.setAttribute("error",message);
            rd = request.getRequestDispatcher("/agentfpassword.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
            /*log.error("Invalid Username" ,e);
            response.sendRedirect( "/agent/loginerror.jsp?action=F");*/

        }

        String activation = null;
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        boolean flag = false;

        try
        {
            AgentFunctions agent=new AgentFunctions();
            user =  ESAPI.authenticator().getUser(username);
            if(user!=null)
            {
                flag = agent.agentForgotPassword(username, user.getAccountId(), user,remoteAddr,header,actionExecuterId);
            }
            if (flag == true)
            {
                log.info("send mail on merchant id on this page"+redirectpage);
                response.sendRedirect( "/agent/agentFpasswordSentMail.jsp");
            }
            else
            {
                log.debug("Invalid emailID ");
                /*response.sendRedirect("/agent/agentfpassword.jsp?action=F");*/
                response.sendRedirect( "/agent/agentFpasswordSentMail.jsp");

            }
            /*rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);*/

            log.debug("Process Successful for Forgotpassword");

            /*else
            {
                response.sendRedirect("/agent/agentfpassword.jsp?action=E");
            }*/

        }
        catch (SystemError se)
        {   log.error("System Error:::::::",se);
            out.println(Functions.NewShowConfirmation1("Error", se.toString()));


        }
        catch (Exception e)
        {   log.error("Exception:::::::",e);
            out.println(Functions.NewShowConfirmation1("Error!", e.toString()));
        }

    }
}

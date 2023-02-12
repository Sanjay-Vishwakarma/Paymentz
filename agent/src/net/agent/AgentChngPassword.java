package net.agent;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import javacryption.aes.AesCtr;
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
import java.util.HashMap;

import java.util.*;

public class AgentChngPassword extends HttpServlet
{
    private static Logger Log = new Logger(AgentChngPassword.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Log.debug("success");
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {   Log.debug("Agent is logout ");
            response.sendRedirect("/Agent/logout.jsp");
            return;
        }

        String oldpwd=null;
        String newpwd=null;
        String confirmpwd=null;

        //Generates a unique 32 byte key for the merchant
        Log.info("Entering Change Password");

        String EOL = "<BR>";
        String errormsg="";

        if(request.getParameter("oldpwd")!=null )
        {
            oldpwd = AesCtr.decrypt(request.getParameter("oldpwd"), request.getParameter("ctoken"), 256);
            request.setAttribute("oldpwd",oldpwd);
        }

        if(request.getParameter("newpwd")!=null )
        {
            newpwd = AesCtr.decrypt(request.getParameter("newpwd"), request.getParameter("ctoken"), 256);
            request.setAttribute("newpwd",newpwd);
        }

        if(request.getParameter("confirmpwd")!=null )
        {
            confirmpwd = AesCtr.decrypt(request.getParameter("confirmpwd"), request.getParameter("ctoken"), 256);
            request.setAttribute("confirmpwd",confirmpwd);
        }
        RequestDispatcher rdError = request.getRequestDispatcher("/agentChngpassword.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/agentChngpassword.jsp?action=Y&ctoken="+user.getCSRFToken());

        boolean  flag = true;
        try
        {
            oldpwd = ESAPI.validator().getValidInput("oldpwd",oldpwd,"Password",20,false);
        }
        catch(ValidationException e)
        {
            flag = false;
            Log.error("OldPassword is Invalid ",e);
            errormsg = "Please enter valid format of Old Password."+EOL;
        }

        try
        {
            newpwd = ESAPI.validator().getValidInput("newpwd",newpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            flag = false;
            Log.error("New Password is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of New Password."+EOL;
        }
        try
        {
            confirmpwd = ESAPI.validator().getValidInput("confirmpwd",confirmpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            flag = false;
            Log.error("Confirm Password is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of Confirm Password."+EOL;
        }

        if(!flag)
        {
            request.setAttribute("error",errormsg);
            session.setAttribute("changepwderror",errormsg);
            rdError.forward(request, response);
            return;
        }

        session.setAttribute("changepwderror","");

        if (confirmpwd.equals(newpwd))
        {
            try
            {
                if (agent.agentchangePassword(oldpwd, newpwd,user.getAccountId(),user))
                {
                    Log.info("Password changed successfully");
                    //MailService mailService=new MailService();
                    AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                    HashMap mailValue=new HashMap();
                    mailValue.put(MailPlaceHolder.TOID,session.getAttribute("merchantid"));
                    asynchronousMailService.sendMerchantSignup(MailEventEnum.AGENT_CHANGE_PASSWORD,mailValue);
                    //mailService.sendMail(MailEventEnum.AGENT_CHANGE_PASSWORD,mailValue);
                    errormsg = errormsg + "Password Changed Successfully."+EOL;
                    request.setAttribute("error", errormsg);
                    rdError.forward(request, response);
                }
                else
                {
                    Log.info("Password could not be successfully changed ");
                    errormsg = errormsg+"Password should not be last 5 password."+EOL;
                    request.setAttribute("error",errormsg);
                    session.setAttribute("changepwderror",errormsg);
                    rdError.forward(request, response);
                }
            }
            catch (Exception e)
            {
                Log.error("Exception while forwarding to change password page ",e);
                if(e.getMessage().toLowerCase().contains("mismatch"))
                {
                    errormsg=errormsg+"Please enter valid Old Password"+EOL;
                    request.setAttribute("error",errormsg);
                    rdError.forward(request,response);
                }
                out.println(Functions.NewShowConfirmation1("Error!", "Internal System Error "));
                rdError.forward(request, response);
            }
        }
        else
        {
            errormsg =  "Newpassword and Confirm Password should be same"+EOL;
            request.setAttribute("error",errormsg);
            session.setAttribute("changepwderror",errormsg);
            rdError.forward(request, response);
        }
    }
}

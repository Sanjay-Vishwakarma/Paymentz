package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
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




public class PartnerChngPassword extends HttpServlet
{
    private static Logger Log = new Logger(PartnerChngPassword.class.getName());
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
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {   Log.debug("Partner is logout ");
            response.sendRedirect("/Partner/Logout.jsp");
            return;
        }

        String oldpwd=null;
        String newpwd=null;
        String confirmpwd=null;
        String login = (String)session.getAttribute("username");

        //Generates a unique 32 byte key for the merchant
        Log.info("ENTER Changing Password");

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

        RequestDispatcher rdError = request.getRequestDispatcher("/partnerChngpassword.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        //RequestDispatcher rdSuccess = request.getRequestDispatcher("/partnerChngedpwd.jsp?ctoken="+user.getCSRFToken());
        try
        {
            oldpwd = ESAPI.validator().getValidInput("oldpwd",oldpwd,"Password",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("OldPassword is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of Old Password."+EOL;
            request.setAttribute("error",errormsg);
            session.setAttribute("changepwderror",errormsg);
            rdError.forward(request, response);
            /*
            response.sendRedirect("/partnerChngpassword.jsp?MES=ERR");*/
            return;
        }
        try
        {
            newpwd = ESAPI.validator().getValidInput("newpwd",newpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("New Password is Invalid ",e);
            errormsg = errormsg + "invalid new password format."+EOL;    // invalid new password format    old message Please enter valid format of Confirm Password
            request.setAttribute("error",errormsg);
            session.setAttribute("changepwderror",errormsg);
            rdError.forward(request, response);
            /*
            response.sendRedirect("/partnerChngpassword.jsp?MES=ERR");*/
            return;
        }
        try
        {
            confirmpwd = ESAPI.validator().getValidInput("confirmpwd",confirmpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("Confirm Password is Invalid ",e);
            errormsg = errormsg + "invalid new password format ."+EOL;    // invalid new password format     // old message Please enter valid format of Confirm Password
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
            session.setAttribute("changepwderror",errormsg);
            response.sendRedirect("/partnerChngpassword.jsp?MES=ERR");
            return;
        }

        if (confirmpwd.equals(newpwd))
        {
            try
            {
                if (partner.partnerchangePassword(oldpwd, newpwd,login,user))
                {
                    Log.info("Password changed successfully");
                    /*MailService mailService=new MailService();
                    HashMap mailValue=new HashMap();
                    mailValue.put(MailPlaceHolder.TOID,session.getAttribute("merchantid"));
                    mailService.sendMail(MailEventEnum.PARTNER_CHANGE_PASSWORD,mailValue);*/
                    errormsg = errormsg + "Password Changed Successfully."+EOL;
                    request.setAttribute("error",errormsg);
                    rdError.forward(request, response);

                }
                else
                {
                    Log.info("Change password is not successful ");
                    errormsg = errormsg + "Password should not be last 5 password."+EOL;
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
                out.println(Functions.ShowMessage("Error!", "Intrnal System Error "));
            }

        }
        else
        {   errormsg = errormsg + "New password and Confirm Password Should be Same"+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
        }

    }
}

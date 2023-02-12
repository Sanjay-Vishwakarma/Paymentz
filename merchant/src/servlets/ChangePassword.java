import com.directi.pg.*;
import javacryption.aes.AesCtr;
import org.apache.commons.lang.StringUtils;
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
import java.util.ResourceBundle;

//import com.payment.Mail.MailEventEnum;
//import com.payment.Mail.MailPlaceHolder;
//import com.payment.Mail.MailService;

public class ChangePassword extends HttpServlet
{
    private static Logger Log = new Logger(ChangePassword.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

       // String data = request.getParameter("data");
        Merchants merchants = new Merchants();


        HttpSession session = request.getSession();
        if (!Admin.isLoggedIn(session))
        {   Log.debug("member is logout ");
            response.sendRedirect("/icici/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        //Generates a unique 32 byte key for the merchant
        Log.info("ENTER Changing Password");


        String oldpwd=null;
        String newpwd=null;
        String confirmpwd=null;
        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String ChangePassword_please_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_please_errormsg"))?rb1.getString("ChangePassword_please_errormsg"): "Please enter valid format of Old Password.";
        String ChangePassword_newpassword_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_newpassword_errormsg"))?rb1.getString("ChangePassword_newpassword_errormsg"): "Please enter valid format of New Password.";
        String ChangePassword_confirm_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_confirm_errormsg"))?rb1.getString("ChangePassword_confirm_errormsg"): "Please enter valid format of Confirm Password.";
        String ChangePassword_successfully_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_successfully_errormsg"))?rb1.getString("ChangePassword_successfully_errormsg"): "Password Changed Successfully.";
        String ChangePassword_password_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_password_errormsg"))?rb1.getString("ChangePassword_password_errormsg"): "Password should not be last 5 password.";
        String ChangePassword_oldpassword_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_oldpassword_errormsg"))?rb1.getString("ChangePassword_oldpassword_errormsg"): "Please enter valid Old Password";
        String ChangePassword_confirm1_errormsg = StringUtils.isNotEmpty(rb1.getString("ChangePassword_confirm1_errormsg"))?rb1.getString("ChangePassword_confirm1_errormsg"): "New Password and Confirm Password should be same";


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


        String EOL = "<BR>";
        String errormsg="";
       // RequestDispatcher rdSuccess = request.getRequestDispatcher("/chngedpwd.jsp?ctoken="+user.getCSRFToken());
        RequestDispatcher rdError = request.getRequestDispatcher("/chngpwd.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            oldpwd = ESAPI.validator().getValidInput("oldpwd",oldpwd,"Password",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("OldPassword is Invalid ",e);
            errormsg = errormsg + ChangePassword_please_errormsg+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
            return;
        }
        try
        {
            newpwd = ESAPI.validator().getValidInput("newpwd",newpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("New Password is Invalid ",e);
            errormsg = errormsg + ChangePassword_newpassword_errormsg+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
            return;
        }
        try
        {
            confirmpwd = ESAPI.validator().getValidInput("confirmpwd",confirmpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("Confirm Password is Invalid ",e);
            errormsg = errormsg + ChangePassword_confirm_errormsg+EOL;
            rdError.forward(request, response);
            return;
        }

        if (confirmpwd.equals(newpwd))
        {

            try
            {
                HashMap passMap = new HashMap();
                passMap.put("merchantid",(String) session.getAttribute("merchantid"));
                passMap.put("userid",(String) session.getAttribute("userid"));
                //Log.debug("pass map---"+passMap);
                if (merchants.changePassword(oldpwd, newpwd,passMap ,user))
                {
                    Log.info("Password changed successfully");

                    errormsg = errormsg + ChangePassword_successfully_errormsg+EOL;
                    request.setAttribute("error",errormsg);
                    rdError.forward(request, response);
                    /*MailService mailService=new MailService();
                    HashMap mailValue=new HashMap();
                    mailValue.put(MailPlaceHolder.TOID,session.getAttribute("merchantid"));
                    mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_CHANGE_PASSWORD,mailValue);*/

                }
                else
                {
                    Log.info("Change password is not successful ");
                    errormsg = errormsg + ChangePassword_password_errormsg+EOL;
                    request.setAttribute("error",errormsg);
                    rdError.forward(request, response);

                }
            }

            catch (Exception e)
            {
                Log.error("Exception while forwarding to change password page ",e);
                if(e.getMessage().toLowerCase().contains("mismatch"))
                {
                    errormsg=errormsg+ChangePassword_oldpassword_errormsg+EOL;
                    request.setAttribute("error",errormsg);
                    rdError.forward(request,response);
                }
                out.println(Functions.ShowMessage("Error!", "Intrnal System Error "));
            }

        }
        else
        {
            errormsg = errormsg + ChangePassword_confirm1_errormsg+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
        }


        Log.debug("Leaving Change password");
    }
}
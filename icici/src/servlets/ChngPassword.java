import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.*;



public class ChngPassword extends HttpServlet
{
    private static Logger Log = new Logger(ChngPassword.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");

        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Log.debug("user name---"+user.getAccountName());
        Log.debug("user name---"+user);

        /*if(Functions.validateCSRF(request.getParameter("ctoken"),user))
        {
            session.setAttribute("ESAPIUserSessionKey",user);
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }*/
        Log.debug("success");

        if (!Admin.isLoggedIn(session))
        {   Log.debug("Admin is logout ");
            response.sendRedirect("/icici/Logout.jsp");
            return;
        }

        String oldpwd=null;
        String newpwd=null;
        String confirmpwd=null;

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


        RequestDispatcher rdError = request.getRequestDispatcher("/chngpassword.jsp?MES=ERR");

        try
        {
            oldpwd = ESAPI.validator().getValidInput("oldpwd",oldpwd,"Password",20,false);
            //validateMandatoryParameter(request);
        }
        catch(ValidationException e)
        {
            Log.error("OldPassword is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of Old Password."+EOL;
            request.setAttribute("error",errormsg);
             rdError.forward(request, response);
            /*session.setAttribute("changepwderror",errormsg);*/
            /*response.sendRedirect("/chngpassword.jsp?MES=ERR");;*/

            return;
        }
        try
        {
            newpwd = ESAPI.validator().getValidInput("newpwd",newpwd,"NewPassword",20,false);
            //validateMandatoryParameter(request);
        }
        catch(ValidationException e)
        {
            Log.error("New Password is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of new Password."+EOL;
            request.setAttribute("error",errormsg);
            rdError.forward(request, response);
           /* session.setAttribute("changepwderror",errormsg);*/
          /*  response.sendRedirect("/chngpassword.jsp?MES=ERR");;*/
            return;
        }
        try
        {
            confirmpwd = ESAPI.validator().getValidInput("confirmpwd",confirmpwd,"NewPassword",20,false);
            //validateMandatoryParameter(request);
        }
        catch(ValidationException e)
        {
            Log.error("Confirm Password is Invalid ",e);
            errormsg = errormsg + "Please enter valid format of Confirm Password."+EOL;
            request.setAttribute("error",errormsg);
           /* session.setAttribute("changepwderror",errormsg);*/
           rdError.forward(request,response);



            return;
        }

        if (confirmpwd.equals(newpwd))
        {



            try
            {
                Log.debug("old password in ChngPassword.java---"+oldpwd);
                Log.debug("new password in ChngPassword.java---"+newpwd);
                Log.debug("Merchant in ChngPassword.java---"+(String) session.getAttribute("merchantid"));
                Log.debug("new password in ChngPassword.java---"+user);
                if (Admin.AdminchangePassword(oldpwd, newpwd, (String) session.getAttribute("merchantid"),user))
                {
                    Log.info("Password changed successfully");
                    errormsg = errormsg + "Password Changed Successfully."+EOL;
                    request.setAttribute("error",errormsg);
                   /* session.setAttribute("changepwderror",errormsg);*/
                    rdError.forward(request,response);

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
        {    Log.info("Newpassword and Confirm Password should be same");
            errormsg = errormsg + "Newpassword and Confirm Password should be same"+EOL;
            request.setAttribute("error",errormsg);


            rdError.forward(request, response);
        }
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.OLD_PASSWORD);
        inputFieldsListMandatory.add(InputFields.NEW_PASSWORD);
        inputFieldsListMandatory.add(InputFields.CONFIRM_PASSWORD);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}

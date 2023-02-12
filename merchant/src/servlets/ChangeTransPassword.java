import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
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

public class ChangeTransPassword extends HttpServlet
{
    private static Logger Log = new Logger(ChangeTransPassword.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String data = request.getParameter("data");
        Merchants merchants = new Merchants();

        HttpSession session = request.getSession();
        if (!merchants.isLoggedIn(session))
        {   Log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        Log.debug("CSRF check successful ");

        //Generates a unique 32 byte key for the merchant
        Log.debug("Changing Withdrawal Password ");
        String oldpwd=null;
        String newpwd=null;
        String confirmpwd=null;

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



        String errormsg = "";
        String msg = "F";
        String EOL = "<BR>";
        request.setAttribute("error",errormsg);
        RequestDispatcher rd1 = request.getRequestDispatcher("/chngwtdlpwd.jsp?ctoken="+user.getCSRFToken());

        try
        {
            oldpwd = ESAPI.validator().getValidInput("oldpwd",oldpwd,"Password",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("Invalid format of Old Password : ",e);
            errormsg = errormsg + "Please enter valid format of Old Password."+EOL;
        }
        try
        {
            newpwd = ESAPI.validator().getValidInput("newpwd",newpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("Invalid format of New Password : ",e);
            errormsg = errormsg + "Please enter valid format of New Password."+EOL;
        }
        try
        {
            confirmpwd = ESAPI.validator().getValidInput("confirmpwd",confirmpwd,"NewPassword",20,false);
        }
        catch(ValidationException e)
        {
            Log.error("Invalid format of Confirm Password : ",e);
            errormsg = errormsg + "Please enter valid format of Confirm Password."+EOL;
        }
        if(!errormsg.equals(""))
        {
            request.setAttribute("error",errormsg);
            rd1.forward(request,response);
            return;
        }

        boolean result = false;
        if (confirmpwd.equals(newpwd))
        {
            try
            {   Log.debug("check old password and set new password");
                result = merchants.changeTransPassword(oldpwd, newpwd, (String) session.getAttribute("merchantid"));
                Log.debug("check old password and set new password successfully");
                if (result)
                    request.setAttribute("MESSAGE", "Withdrawal Password Changed");
                else
                    request.setAttribute("MESSAGE", "Withdrawal Password is not Changed. Please verify your old withdrawal password.Note: Login Password is different from the Withdrawal Password.");

            }
            catch (Exception e)
            {
                Log.error("ERROR in change password",e);
                out.println(Functions.NewShowConfirmation1("Error!", "Internal Error "));
            }
            Log.info("PASSWORD change successfully");
            RequestDispatcher rd = request.getRequestDispatcher("/chngedtranspwd.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);

        }
        else
        {
            Log.debug("change password error: confirm password is different from new password");
            errormsg = errormsg + "Confirm password should be same as new password";
            request.setAttribute("error",errormsg);
            rd1.forward(request, response);

        }


        Log.debug("Leaving ChangePassword");
    }
}

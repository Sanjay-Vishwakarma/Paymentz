import com.directi.pg.*;

import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;

import java.util.*;

public class CustSuppSignup extends HttpServlet
{
    static Logger log=new Logger(CustSuppSignup.class.getName());
    static String classname= CustSuppSignup.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);

        if (!com.directi.pg.Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user = (User)session.getAttribute("ESAPIUserSessionKey");

        String errormsg="";
        String EOL = "<BR>";
        String msg = "F";
        String redirectpage="";
        String password = "";
        String confpassword = "";
        Hashtable detailhash = new Hashtable();

        try
        {
            Functions functions=new Functions();
            if(functions.isValueNull(request.getParameter("password")))
            {
                password = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);
            }
            if(functions.isValueNull(request.getParameter("confirmpassword")))
            {

                confpassword = AesCtr.decrypt(request.getParameter("confirmpassword"), request.getParameter("ctoken"), 256);
            }

            boolean flag = true;
            if (!ESAPI.validator().isValidInput("name",request.getParameter("name"),"Description",100,false))
            {
                log.debug(classname+":: USERname not valid....");
                msg="X";
                errormsg = errormsg + "Please Enter Valid Customer Support Executive Name."+EOL;
                flag = false;
            }
            else
                detailhash.put("csName",request.getParameter("name"));

            if (!ESAPI.validator().isValidInput("username",request.getParameter("username"),"alphanum",100,false))
            {
                log.debug(classname+":: username not valid....");
                msg="X";
                errormsg = errormsg + "Please Enter Valid Username."+EOL;
                flag = false;
            }
            else
                detailhash.put("csLogin",request.getParameter("username"));

            if ((!ESAPI.validator().isValidInput("password",password,"NewPassword",20,false)) || (!(password).equals(confpassword)))
            {
                flag = false;
                log.debug("wrong password");
                msg="X";
                errormsg = errormsg + "Please enter valid Password/Confirm Password."+EOL;
            }
            else
                detailhash.put("csPassword", password);


            if (!ESAPI.validator().isValidInput("contact_emails",request.getParameter("emailid"),"Email",100,false))
            {

                flag = false;
                msg = "X";
                log.debug("Enter valid EmailID"+request.getParameter("emailid"));
                errormsg = errormsg +"Please enter valid Contact emailaddress."+EOL;
            }
            else
            {
                detailhash.put("csEmail",request.getParameter("emailid"));
            }
            if (!ESAPI.validator().isValidInput("telno",(String) request.getParameter("phoneno"),"SignupPhone",20,false))
            {   log.debug(classname+"::PLS enter valid phone number ");
                errormsg = errormsg + "Please enter valid phone number."+EOL;
                flag = false;
            }
            else
                detailhash.put("csContactNumber", (String) request.getParameter("phoneno"));
            if (flag == true)
            {

                log.debug(classname+"Valid user data");
                if (CustomerSupport.isCustomerSuppEx((String) detailhash.get("csLogin")))
                {   log.debug("redirect to signup for new username");
                    redirectpage = "/cseSignup.jsp?MES=username&ctoken="+user.getCSRFToken();
                    request.setAttribute("details", detailhash);
                    request.setAttribute("username", null);
                }
                else
                {
                    request.setAttribute("role","support");
                    addCustSuppEx(detailhash);
                    request.setAttribute("username", (String) detailhash.get("csLogin"));
                    redirectpage = "/cseSignup.jsp?MES=success&ctoken="+user.getCSRFToken();
                    log.info(classname+"::THANK YOU for signup  "+redirectpage);
                }
            }
            else
            {
                log.debug(classname+"::ENTER VALID DATA");
                redirectpage = "/cseSignup.jsp?MES="+msg+"&ctoken="+user.getCSRFToken();
                request.setAttribute("details", detailhash);
                request.setAttribute("error",errormsg);
            }

        }
        catch (Exception e)
        {
            if(e.getMessage().contains("Duplicate"))
            {
                log.debug("redirect to NEWLOGIN");
                redirectpage = "/cseSignup.jsp?MES=username&ctoken="+user.getCSRFToken();
                request.setAttribute("details", detailhash);
                request.setAttribute("username", null);
            }
            else
            {
                log.error(classname + "::Leaving CUSTOMERSUPPORT  throwing Exception::", e);
                redirectpage = "/cseSignup.jsp?MES=I&ctoken=\"+user.getCSRFToken()";
            }
        }

        RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
        rd.forward(request, response);
        log.debug(classname+"redirectpage::"+redirectpage);

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request,response);
    }

    public void addCustSuppEx(Hashtable details) throws SystemError
    {
        try
        {
            User user= ESAPI.authenticator().createUser((String)details.get("csLogin"),(String)details.get("csPassword"),"support");
            customerSupportExecutive cse = CustomerSupport.addnew_customerSupport(user.getAccountId(), details);
        }
        catch(Exception e)
        {
            log.error("Add user throwing Authentication Exception ",e);

            if(e instanceof AuthenticationAccountsException)
            {
                String message=((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    throw new SystemError("Error: " + message);
                }
            }

            try
            {
                CustomerSupport.DeleteBoth((String) details.get("csLogin"));
            }catch(Exception e1)
            {
               log.error(" Deleting user throwing Exception::",e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }


    }
}

import com.directi.pg.CustomerSupport;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.customerSupportExecutive;
import com.payment.validators.InputFields;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationLoginException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class Login extends HttpServlet
{
    static Logger log = new Logger("logger1");
    static final String classname= Login.class.getName();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        PrintWriter out =response.getWriter();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("Anonymous");
        log.debug(classname+" user Top::"+user);
        Hashtable error=null;
        String Ename = null;
        String Epass=null;
        RequestDispatcher rd;
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"), user))
        {

            session.setAttribute("Anonymous",user);

        }
        else
        {     log.debug(classname+" session out::");
            response.sendRedirect("/support/sessionout.jsp");

            return;
        }
        /*request.setAttribute("role","support");*/
        String redirectPage = "";
        try
        {
            if(request.getParameter("password")!=null )
            {

                Epass = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);
                request.setAttribute("password",Epass);
            }
            List<InputFields> inputFieldsListMandatory= new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.USERNAME);
            inputFieldsListMandatory.add(InputFields.PASSWORD2);
            error= CustomerSupport.validateMandatoryParameters(request, inputFieldsListMandatory);
            if(error.get(InputFields.USERNAME.toString())==null)
            {
                String hashpassword = null;
                response.setContentType("text/html");
                String temp = (String) session.getAttribute("valid");

                if (temp == null)
                {
                    log.debug("not a valid user...");
                    redirectPage = "/login.jsp?isValid=false&ctoken="+user.getCSRFToken();
                    rd = request.getRequestDispatcher(redirectPage);
                    rd.forward(request, response);
                    return;
                }
                else
                {


                    try{

                        long logintime= CustomerSupport.getDate();
                        String Ip= Functions.getIpAddress(request);


                        log.info("Executive Entered Successfully: "+Ename+("csName")+"::IPaddress: "+Ip);

                        ESAPI.httpUtilities().setCurrentHTTP(request,response);

                        try
                        {
                            session.invalidate();

                            user = ESAPI.authenticator().login(request,response);
                            redirectPage ="/welcome.jsp";
                            /*if(!user.isInRole("support"))
                            {
                                user.incrementFailedLoginCount();
                                ESAPI.authenticator().logout();
                                redirectPage="/login.jsp?isValid=I&ctoken="+user.getCSRFToken();
                                rd = request.getRequestDispatcher(redirectPage);
                                rd.forward(request,response);
                                return;
                            }
                            log.debug(classname+" Last login Time::"+new Timestamp(user.getLastLoginTime().getTime())+" user name::"+user.getAccountName());*/
                        }
                        catch(Exception e)
                        {
                            log.error(classname+" esapi authenticator exception",e);

                            session = request.getSession(true);

                            session.setAttribute("valid", "true");

                            session.setAttribute("Anonymous",user);

                            if(e.getMessage().toLowerCase().contains("disabled"))
                            {
                                redirectPage="/login.jsp?isValid=D&ctoken="+user.getCSRFToken();
                                rd = request.getRequestDispatcher(redirectPage);
                                rd.forward(request,response);
                                return;
                            }
                            else{
                                if(e.getMessage().toLowerCase().contains("locked"))
                                {
                                    redirectPage="/login.jsp?isValid=L&ctoken="+user.getCSRFToken();
                                    rd = request.getRequestDispatcher(redirectPage);
                                    rd.forward(request,response);
                                    return;
                                }else
                                {  if(e.getMessage().toLowerCase().contains("accessdenied"))
                                {
                                    redirectPage="/login.jsp?isValid=AD&ctoken="+user.getCSRFToken();
                                    rd = request.getRequestDispatcher(redirectPage);
                                    rd.forward(request,response);
                                    return;
                                }
                                else
                                {

                                    throw new AuthenticationLoginException("LOGIN FAILED","DUE TO INTERNAL ERROR");
                                }
                                }
                            }
                        }
                        customerSupportExecutive cse= CustomerSupport.authenticate(request.getParameter("username"), user);
                        int csId = CustomerSupport.customerSupportId(user.getAccountId());
                        CustomerSupport.updateCSE(csId, logintime, Ip);
                        CustomerSupport.updateLastLogin(logintime, csId);
                        session= ESAPI.httpUtilities().getCurrentRequest().getSession();

                        request.setAttribute("logintime",logintime);
                        session.setAttribute("LoginDate",logintime);
                        session.setAttribute("username",request.getParameter("username"));
                        session.setAttribute("password",Epass);

                        session.setAttribute("csId",csId);

                        log.debug(classname+" forwarding to page welcome");
                        /*if(error.get(InputFields.PASSWORD2.toString())!=null)
                        {
                            rd=request.getRequestDispatcher("/changePassword.jsp");
                            rd.forward(request,response);
                            return;
                        }*/
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, -91);
                        if (user.getLastPasswordChangeTime().compareTo(cal.getTime()) == -1)
                        {
                            log.debug("redirecting to change password page");
                            redirectPage = "/changePassword.jsp";
                        }

                        if (cse.csauthenticate.equals("forgot"))
                        {
                            log.debug("redirecting to change password page");
                            redirectPage = "/changePassword.jsp";
                        }
                        rd = request.getRequestDispatcher(redirectPage);
                        rd.forward(request,response);


                    }
                    catch (Exception e)
                    {

                        log.error(classname+" main class exception::",e);
                        log.debug(classname + " forwarding to page custSuppLogin");

                        response.sendRedirect("/support/login.jsp?isValid=false");


                    }

                }
            }else{
                log.debug(classname+" error found in validation Name::"+Ename+" password::"+Epass);
                request.setAttribute("error",error);
                rd = request.getRequestDispatcher("/login.jsp?MES=X&ctoken="+user.getCSRFToken());
                rd.forward(request,response);

            }

        }
        catch (Exception e)
        {

            log.error(classname+" Error ::",e);


        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }

}

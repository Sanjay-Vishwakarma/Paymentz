package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PartnerAuthenticate;
import com.directi.pg.TransactionEntry;
import com.manager.PartnerModuleManager;
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
import java.util.Set;

public class Login extends HttpServlet
{


    static Logger logger = new Logger(Login.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("Anonymous");
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {
            logger.debug("set user");
            logger.debug("successful    "+user.getCSRFToken());
            session.setAttribute("Anonymous",user);

        }
        else
        {     logger.debug("session out");
            response.sendRedirect("/partner/sessionout.jsp");

            return;
        }

        logger.debug("successful");

        String username=null;
        String password=null;


        if(request.getParameter("password")!=null )
        {
            password=request.getParameter("password");
            /*password = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);*/
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

            redirectpage = "/login.jsp?action=F&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }
        try
        {
            password =ESAPI.validator().getValidInput("password",password,"Password",25,false);
        }
        catch(ValidationException e)
        {
            logger.error("Given Value is not valid ",e);

            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp?action=F&ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;

        }

        response.setContentType("text/html");
        PrintWriter printwriter = response.getWriter();

        String temp = (String) session.getAttribute("valid");

        if (temp == null)
        {
            logger.debug("not a valid user...");
            //response.sendRedirect( "/icici/admin/login.jsp?action=F&ctoken="+user.getCSRFToken());

            redirectpage = "/login.jsp?action=F&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );

        }
        else
            logger.debug("valid user."+username);

        try
        {
            PartnerFunctions partner = new PartnerFunctions();

            String role = partner.getRole(username);
            request.setAttribute("role", role);
            ESAPI.httpUtilities().setCurrentHTTP(request, response);

//            logger.debug("role in login----------------------"+role);


            PartnerAuthenticate member;
            int userId = 0;

            try
            {
                session.invalidate();

                member = partner.partnerLoginAuthentication(username,request);

                if(member.getPartnerUser()!=null)
                {
                    role = partner.getRole(username);
                    request.setAttribute("role", role);
                    userId = member.getPartnerUser().getPartnerUserId();
                }

                user = ESAPI.authenticator().login(request, response);
                redirectpage = "/net/PartnerDashboard?ctoken=" + ESAPI.httpUtilities().getCSRFToken();
               // redirectpage = "/partnerChngpassword.jsp?ctoken=" + ESAPI.httpUtilities().getCSRFToken();       //"/icici/chngpassword.jsp?MES=FP";

            }
            catch (Exception e)
            {

                session = request.getSession(true);

                session.setAttribute("valid", "true");

                session.setAttribute("Anonymous",user);

                logger.error(" esapi authenticator exception", e);
                logger.error(request.getParameter("logo_reset") + "logo");
                logger.error(request.getParameter("icon_reset") + "logo");
                session.setAttribute("logo", request.getParameter("logo_reset"));
                session.setAttribute("icon", request.getParameter("icon_reset"));

                if (e.getMessage().toLowerCase().contains("disabled"))
                {
                    redirectpage = "/login.jsp?action=D&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (e.getMessage().toLowerCase().contains("locked"))
                {
                    redirectpage = "/login.jsp?action=L&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (e.getMessage().toLowerCase().contains("accessdenied"))
                {
                    redirectpage = "/login.jsp?action=AD&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("whitelisted"))
                {

                    redirectpage = "/login.jsp?action=IP&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("unauthorized"))
                {

                    redirectpage = "/login.jsp?action=A&ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else
                {

                    throw new AuthenticationLoginException("LOGIN FAILED", "DUE TO INTERNAL ERROR");
                }
            }

            if(!request.getAttribute("role").equals("subpartner") && !request.getAttribute("role").equals("childsuperpartner"))
            {
                userId = member.partnerid;
            }

            session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("address", "" + member.address);
            session.setAttribute("contactemails", "" + member.contactemails);
            session.setAttribute("merchantid", "" + member.partnerid);
            session.setAttribute("userid", "" + userId);
            session.setAttribute("memberObj", member);
            session.setAttribute("partnername", "" + member.partnername);
            session.setAttribute("template", "" + member.template);
            session.setAttribute("authenticate", "" + member.authenticate);
            session.setAttribute("telno", "" + member.telno);
            session.setAttribute("isFlightPartner", member.isFlightPartner);
            session.setAttribute("transactionentry", new TransactionEntry(member.partnerid));
            session.setAttribute("logo", member.logoname);
            session.setAttribute("hostURL", member.hostURL);
            session.setAttribute("isRefund", member.isRefund);
            session.setAttribute("partnerId", member.partnerid);
            session.setAttribute("partnerid", member.partnerid);
            session.setAttribute("emiConfiguration", member.emiConfiguration);
            session.setAttribute("role", role);

            //Getting module allocation configuration after successful login
            PartnerModuleManager partnerModuleManager=new PartnerModuleManager();
            PartnerFunctions functions = new PartnerFunctions();

            Set<String> moduleSet = null;
            if(request.getAttribute("role").equals("subpartner") || request.getAttribute("role").equals("childsuperpartner"))
            {
                moduleSet = partnerModuleManager.getPartnerAccessModuleSet(String.valueOf(userId));
            }else{
                if(functions.isPartneridExistforModules(String.valueOf(userId))){
                    moduleSet = partnerModuleManager.getPartnerAccessModuleList(String.valueOf(userId));
                }
                else{
                    moduleSet = partnerModuleManager.getPartnerModuleList();
                }
            }
            session.setAttribute("moduleset",moduleSet);
            logger.info("Partner user is Authenticate successful");

            if (member.authenticate.equals("forgot"))
            {
                logger.debug("redirecting to change password page");
                redirectpage = "/partnerChngpassword.jsp?ctoken=" + ESAPI.httpUtilities().getCSRFToken();//"/icici/chngpassword.jsp?MES=FP";
            }
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -90);

            if (user.getLastPasswordChangeTime().compareTo(cal.getTime()) == -1)
            {
                logger.debug("redirecting to change password page");
                redirectpage = "/partnerChngpassword.jsp?MES=UP";
            }

            // Start : Added for IP Check
            /* PaymentChecker paymentChecker = new PaymentChecker();
            String ipAddress = Functions.getIpAddress(request);
            String partnerId = String.valueOf(merchantid);

            if (!paymentChecker.isIpWhitelistedForPartner(partnerId, ipAddress))
            {
                session = request.getSession();
                session.setAttribute("Anonymous", user);

                RequestDispatcher rd = request.getRequestDispatcher("/login.jsp?action=IP&ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }*/

            logger.debug("redirecting to the page " + redirectpage);
            //response.sendRedirect(redirectpage);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );


        }
        catch (Exception exception)
        {   /*logger.error("Exception:::::",exception);
            *//* printwriter.println(Functions.ShowMessage("Error!", exception.toString()));*//*
            try
            {
                ESAPI.httpUtilities().setCurrentHTTP(request,response);
                user = ESAPI.authenticator().login(request,response);

            }
            catch(Exception e1)
            {
                logger.error(" ESAPI authentication failed ",e1);
            }
            logger.info(" User not authenticated redirect to login page");
            session = request.getSession();
            session.setAttribute("Anonymous",user);
            redirectpage = "/index.jsp?action=F&ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);*/
            response.sendRedirect("/partner/login.jsp?action=F");
        }

    }

}

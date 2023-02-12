package net.agent;

import com.directi.pg.*;
import com.manager.dao.AgentDAO;
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

import java.util.*;

public class Login extends HttpServlet
{

    /*private static Logger logger = new Logger("logger1");*/

    private static Logger logger = new Logger(Login.class.getName());

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
     //   logger.debug(request.getParameter("ctoken")+"         "+user);
        if(Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
        {
            logger.debug("set user");
            logger.debug("successful    "+user.getCSRFToken());
            session.setAttribute("Anonymous",user);

        }
        else
        {     logger.debug("session out");
            response.sendRedirect("/agent/sessionout.jsp");

            return;
        }

        logger.debug("successful");

        String username=null;
        String password=null;
        String logo=(String)session.getAttribute("logo");
        String icon=(String)session.getAttribute("icon");
        String company=(String)session.getAttribute("company");
        String partnerid=(String)session.getAttribute("partnerid");
        AgentDAO agentDAO= new AgentDAO();
        String agentpartner= null;
        String hostname=(String)session.getAttribute("hostname");
        AgentFunctions agent = new AgentFunctions();
        String role="";

        if(request.getParameter("password")!=null )
        {

            password = AesCtr.decrypt(request.getParameter("password"), request.getParameter("ctoken"), 256);
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

            redirectpage = "/login.jsp?ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            request.setAttribute("action","E");
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

            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp?ctoken="+user.getCSRFToken());
            request.setAttribute("action","E");
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

            redirectpage = "/login.jsp?ctoken="+user.getCSRFToken();
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            request.setAttribute("action","E");
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );

        }
        else
            logger.debug("valid user."+username);

        try
        {
            agentpartner= String.valueOf(agentDAO.getAgentPartner(username));
            logger.error("agentpartner:: " + agentpartner);
            role=agent.getRoleofPartner(agentpartner);
            logger.error("role:: "+role);
        }
        catch (Exception e)
        {
            logger.error("Exception while fetching partner::: ",e);
        }

        try
        {
            ESAPI.httpUtilities().setCurrentHTTP(request, response);

            AgentAuthenticate member=null;
            try
            {

                session.invalidate();

                if ( role.contains("superpartner")|| role.contains("partner"))
                {
                    member = agent.agentLoginAuthentication(username,request,agentpartner);
                }
                else
                {
                    member = agent.agentLoginAuthentication(username, request, partnerid);
                }
                user = ESAPI.authenticator().login(request, response);


                redirectpage = "/net/AgentDashboard?ctoken=" + ESAPI.httpUtilities().getCSRFToken();
            }
            catch (Exception e)
            {
                logger.error(" esapi authenticator exception", e);

                session=request.getSession(true);

                session.setAttribute("valid", "true");
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company",company);
                session.setAttribute("hostname",hostname);
                session.setAttribute("partnerid",partnerid);

                session.setAttribute("Anonymous",user);

                if (e.getMessage().toLowerCase().contains("disabled"))
                {
                    redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    request.setAttribute("action","D");
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (e.getMessage().toLowerCase().contains("locked"))
                {
                    redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    request.setAttribute("action","L");
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if (e.getMessage().toLowerCase().contains("accessdenied"))
                {
                    redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    request.setAttribute("action","AD");
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("whitelisted"))
                {

                    redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    request.setAttribute("action","IP");
                    rd.forward(request, response);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else if(e.getMessage().toLowerCase().contains("unauthorized"))//UNAUTHORIZED
                {

                    redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                    request.setAttribute("action","A");
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

/*            if (member.partnerid != null && !member.partnerid.equalsIgnoreCase(partnerid))
            {
                redirectpage = "/login.jsp?ctoken=" + user.getCSRFToken();
                RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                request.setAttribute("action","F");
                rd.forward(request, response);
                return;
            }*/


            session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("password", password);
            session.setAttribute("address", "" + member.address);
            session.setAttribute("contactemails", "" + member.contactemails);
            session.setAttribute("merchantid", "" + member.agentid);
            session.setAttribute("memberObj", member);
            session.setAttribute("agentname",member.agentname);
            session.setAttribute("template", "" + member.template);
            session.setAttribute("authenticate", "" + member.authenticate);
            session.setAttribute("telno", "" + member.telno);
            session.setAttribute("transactionentry", new TransactionEntry(member.agentid));
            session.setAttribute("partnerid",partnerid);
            session.setAttribute("logo",logo);
            session.setAttribute("icon",icon);
            session.setAttribute("company",company);


            logger.info("Admin user is Authenticate successful ");

            if (member.authenticate.equals("forgot"))
            {
                logger.debug("redirecting to change password page");
                redirectpage = "/agentChngpassword.jsp?ctoken=" + ESAPI.httpUtilities().getCSRFToken();//"/icici/chngpassword.jsp?MES=FP";
            }
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -90);

            if (user.getLastPasswordChangeTime().compareTo(cal.getTime()) == -1)
            {
                logger.debug("redirecting to change password page");
                redirectpage = "/agentChngpassword.jsp?MES=UP&ctoken=" + user.getCSRFToken();
            }



            logger.debug("redirecting to the page " + redirectpage);
            RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
            rd.forward(request, response);
            ESAPI.httpUtilities().setNoCacheHeaders( response );


        }
        catch (Exception exception)
        {  /* logger.error("Exception:::::",exception);
            response.sendRedirect("/agent/login.jsp?action=F&ctoken=" + user.getCSRFToken());*/
            /*try
            {
                ESAPI.httpUtilities().setCurrentHTTP(request,response);
                user = ESAPI.authenticator().login(request,response);

            }
            catch(Exception e1)
            {
                logger.info(" ESAPI authentication failed ");
            }*/


            logger.info(" User not authenticated redirect to login page");

            try
            {
                session.invalidate();

                session = request.getSession(true);

                session.setAttribute("Anonymous",user);
                session.setAttribute("logo",logo);
                session.setAttribute("icon",icon);
                session.setAttribute("company",company);
                session.setAttribute("partnerid",partnerid);
                redirectpage = "/login.jsp?ctoken="+user.getCSRFToken();
                request.setAttribute("action","F");
                RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                rd.forward(request, response);
            }catch(Exception ee)
            {
                logger.error(" exception redirecting ::",ee);
            }
        }
    }

}

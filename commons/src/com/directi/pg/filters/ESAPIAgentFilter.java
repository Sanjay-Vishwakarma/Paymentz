package com.directi.pg.filters;

import com.directi.pg.*;
import com.payment.checkers.PaymentChecker;
import org.apache.axis.utils.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import org.apache.log4j.MDC;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 2/24/14
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESAPIAgentFilter implements Filter
{
    private final Logger logger = ESAPI.getLogger("ESAPIAgentFilter");
    private static final Set<String> excludedFiles = new HashSet<String>();
   // private final SystemAccessLogger accessLogger = new SystemAccessLogger("ESAPIAdminFilter");

    static
    {
        excludedFiles.add("logout.jsp");
        excludedFiles.add("login.jsp");
        excludedFiles.add("Login");
        excludedFiles.add("loginerror.jsp");
        excludedFiles.add("sessionout.jsp");
        excludedFiles.add("agentfpassword.jsp");
        excludedFiles.add("agentFpasswordSentMail.jsp");
        excludedFiles.add("AgentForgotPassword");
        excludedFiles.add("getURLtest.jsp");
        excludedFiles.add("SendFile");
        excludedFiles.add("ActionMerchantWireReports");
        excludedFiles.add("ExportTransactionByAgent");
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
        String path = filterConfig.getInitParameter("resourceDirectory");
        if ( path != null )
        {
            ESAPI.securityConfiguration().setResourceDirectory( path );
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setAttribute("role","agent");
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        String partner=null;
        String companyname=null;
        PaymentChecker paymentChecker = new PaymentChecker();

        try
        {
            MDC.put("ip", Functions.getIpAddress(request));

            HashMap<String,String> map = new HashMap<String, String>();

            for(Object key : request.getParameterMap().keySet())
            {
                map.put((String) key,request.getParameter((String) key));
                if(request.getParameterMap().keySet().contains("password"))
                    map.put("password","");

                if(request.getParameterMap().keySet().contains("cardnumber"))
                    map.put("cardnumber","");

                if(request.getParameterMap().keySet().contains("expiry_month"))
                    map.put("expiry_month","");

                if(request.getParameterMap().keySet().contains("expiry_year"))
                    map.put("expiry_year","");

                if(request.getParameterMap().keySet().contains("cvv"))
                    map.put("cvv","");

                if(request.getParameterMap().keySet().contains("passwd"))
                    map.put("passwd","");

                if(request.getParameterMap().keySet().contains("conpasswd"))
                    map.put("conpasswd","");

                if(request.getParameterMap().keySet().contains("ccnum"))
                    map.put("ccnum","");

                if(request.getParameterMap().keySet().contains("lastfour"))
                    map.put("lastfour","");

                if(request.getParameterMap().keySet().contains("firstsix"))
                    map.put("firstsix","");

                if(request.getParameterMap().keySet().contains("wspassword"))
                    map.put("wspassword","");
            }
            MDC.put("request",map);
            MDC.put("filepath",ESAPI.httpUtilities().getCurrentRequest().getRequestURI()+" Module Name - AGENT");

            /*if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                Functions.setCookie(request);
            }*/

            if("login.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                HttpSession session = request.getSession(true);
                session.setAttribute("valid", "true");
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                logger.error(Logger.SECURITY_FAILURE, "insude index.jsp of ESAPIAGENTFilter...");
                if (!ESAPI.validator().isValidInput("partnerid ",request.getParameter("partnerid"),"Numbers",3,true))
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside if---index.jsp of ESAPIFilter...");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    response.sendRedirect("/agent/sessionout.jsp");
                    return;
                }
                else
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---else index.jsp of ESAPIFilter...");
                    partner= request.getParameter("partnerid");
                }
                if (!ESAPI.validator().isValidInput("fromtype ",request.getParameter("fromtype"),"SafeString",20,true))
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---if(from type) index.jsp of ESAPIFilter...");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    response.sendRedirect("/agent/sessionout.jsp");
                    return;
                }
                else
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---else(from type) index.jsp of ESAPIFilter...");
                    companyname= request.getParameter("fromtype");
                }

                String hostname=request.getRemoteHost();
                String ipaddress=Functions.getIpAddress(request);
                String forwardedhostname =Functions.getForwardedHost(request);
                if(partner!=null && companyname!=null)
                {
                    if(paymentChecker.isPartnerexistindb(partner, ipaddress, companyname))
                    {


                        logger.error(Logger.SECURITY_FAILURE,"insude partner block of ESAPIFilter...");
                        session.setAttribute("logo",Merchants.logoHash.get(partner));
                        session.setAttribute("icon",Merchants.iconHash.get(partner));
                        session.setAttribute("company",companyname);
                        session.setAttribute("hostname",hostname);
                        session.setAttribute("partnerid",partner);
                    }
                    else
                    {
                        response.sendRedirect("/agent/sessionout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                }
                else if (!StringUtils.isEmpty(forwardedhostname))
                {
                    Partner partnerClass = new Partner();
                    Hashtable partnerDetails = partnerClass.getPartnerDetailsByHost(forwardedhostname);
                    session.setAttribute("logo", partnerDetails.get("LOGO"));
                    session.setAttribute("icon", partnerDetails.get("ICON"));
                    session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                    session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));

                    logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID"));
                }
                else
                {
                    Enumeration enum1 = request.getParameterNames();
                    if(enum1.hasMoreElements())
                    {
                    if(!enum1.equals("ctoken"))
                    {
                        logger.debug(Logger.SECURITY_FAILURE,"inside if of partner block of ESAPIFILTER other parameter values if present");
                        response.sendRedirect("/agent/sessionout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                    }
                    else
                    {
                        logger.error(Logger.SECURITY_FAILURE,"inside else of partner block of ESAPIFilter...");
                        session.setAttribute("logo", Merchants.adminPartnerHash.get("logo"));
                        session.setAttribute("icon", Merchants.adminPartnerHash.get("icon"));
                        session.setAttribute("company",Merchants.adminPartnerHash.get("fromtype"));
                        session.setAttribute("partnerid",Merchants.adminPartnerHash.get("partnerid"));
                    }
                }

            }
            if(!excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.authenticator().login(request, response);
                Functions.getNewSession(request);
                User user =  (User)ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                ESAPI.httpUtilities().setNoCacheHeaders( response );

                if(!Functions.validateCSRF(request.getParameter("ctoken"),user))
                {
                    response.sendRedirect("/agent/logout.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
            }

            if("logout.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                User user =  (User)ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                if(request.getParameter("ctoken")==null || request.getParameter("ctoken").equals("")||!Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
                {
                    request.getSession().invalidate();
                    ESAPI.authenticator().logout();
                    response.sendRedirect("/agent/login.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }

            }
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }
        catch( AuthenticationException e )
        {
            ESAPI.authenticator().logout();
            request.setAttribute("message", "Authentication failed");
            response.sendRedirect("/agent/logout.jsp");
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }
        catch (SystemError systemError)
        {
            ESAPI.authenticator().logout();
            request.setAttribute("message", "Authentication failed");
            response.sendRedirect("/agent/logout.jsp");
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }

        // forward this request on to the web application
        filterChain.doFilter(request, response);

        // set up response with content type
        ESAPI.httpUtilities().setContentType( response );

        // set no-cache headers on every response
        // only do this if the entire site should not be cached
        // otherwise you should do this strategically in your controller or actions
        ESAPI.httpUtilities().setNoCacheHeaders( response );
    }

    public void destroy()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getFileName(String requestURI)
    {
        String filename = null;

        filename = requestURI.substring(requestURI.lastIndexOf("/")+1);

        return filename;  //To change body of created methods use File | Settings | File Templates.
    }
}

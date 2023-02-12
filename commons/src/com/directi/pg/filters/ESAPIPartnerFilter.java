package com.directi.pg.filters;

import com.directi.pg.Functions;
import com.directi.pg.Merchants;
import com.directi.pg.Partner;
import com.directi.pg.SystemError;
import com.payment.checkers.PaymentChecker;
import org.apache.axis.utils.StringUtils;
import org.apache.log4j.MDC;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/14/14
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESAPIPartnerFilter implements Filter
{
    private static final Set<String> excludedFiles  = new HashSet<String>();
    private static final Set<String> apiFiles       = new HashSet<String>();
    static
    {
        excludedFiles.add("logout.jsp");
        excludedFiles.add("login.jsp");
        excludedFiles.add("Login");
        excludedFiles.add("loginerror.jsp");
        excludedFiles.add("sessionout.jsp");
        excludedFiles.add("partnerfpassword.jsp");
        excludedFiles.add("partnerFpasswordSentMail.jsp");
        excludedFiles.add("PartnerForgotPassword");
        excludedFiles.add("getURLtest.jsp");
        excludedFiles.add("SendFile");
        excludedFiles.add("SendPartnerFile");
        excludedFiles.add("GenerateRiskProfileXML");
        excludedFiles.add("GenerateBusinessProfileXML");
        excludedFiles.add("GenerateUserProfileXML");
        excludedFiles.add("GetGatewayAccount");
        excludedFiles.add("ExportTransactionByPartner");
        excludedFiles.add("PartnerRiskRuleDetails");
        excludedFiles.add("ViewKycDocument");
        excludedFiles.add("ExportMemberDetails");
        excludedFiles.add("PartnerMonitoringRuleLog");
        excludedFiles.add("PartnerMerchantResendLog");
        excludedFiles.add("RejectedTransactionsList");
        excludedFiles.add("RejectedTransactionDetails");
        excludedFiles.add("ExportTransactions");
        excludedFiles.add("EmiConfiguration");
        excludedFiles.add("ExportActionHistoryByPartner");
        excludedFiles.add("ExportBankTransactions");
        excludedFiles.add("PayoutTransactionList");
        excludedFiles.add("MerchantPayoutTransactionDetails");
        excludedFiles.add("ExportPayoutTransactionByPartner");
        excludedFiles.add("CheckoutConfiguration");
        excludedFiles.add("TransactionsLogs");
        excludedFiles.add("ActivityTracker");

        excludedFiles.add("CreatePartnerBankApplication");
        excludedFiles.add("GeneratedPartnerConsolidatedApplication");
        excludedFiles.add("thankYou.jsp");
        excludedFiles.add("ExportFraudAlertTransactionList");
        excludedFiles.add("ExportTerminalDetails");
        apiFiles.add("GetDetails");

    }

    private final Logger logger = ESAPI.getLogger("ESAPIPartnerFilter");
    // private final SystemAccessLogger accessLogger = new SystemAccessLogger("ESAPIPartnerFilter");

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
        HttpServletRequest request      = (HttpServletRequest) servletRequest;
        HttpServletResponse response    = (HttpServletResponse) servletResponse;
        request.setAttribute("role","partner");
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        PaymentChecker paymentChecker   = new PaymentChecker();
        String partner                  = null;
        String companyname              = null;

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

                if(request.getParameterMap().keySet().contains("conpasswd"))
                    map.put("conpasswd","");

                if(request.getParameterMap().keySet().contains("passwd"))
                    map.put("passwd","");

                if(request.getParameterMap().keySet().contains("newpwd"))
                    map.put("newpwd","");

                if(request.getParameterMap().keySet().contains("oldpwd"))
                    map.put("oldpwd","");

                if(request.getParameterMap().keySet().contains("confirmpwd"))
                    map.put("confirmpwd","");
            }
            MDC.put("request",map);
            MDC.put("filepath", ESAPI.httpUtilities().getCurrentRequest().getRequestURI() +" Module name - PARTNER");


            /*if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                Functions.setCookie(request);
            }*/

            if("Login".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.httpUtilities().setNoCacheHeaders( response );
            }

            if("login.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                HttpSession session = request.getSession(true);
                session.setAttribute("valid", "true");
                logger.error(Logger.SECURITY_FAILURE, "insude login.jsp of ESAPIPartnerFilter...");
                ESAPI.httpUtilities().setNoCacheHeaders( response );

                if (!ESAPI.validator().isValidInput("partnerid ",request.getParameter("partnerid"),"Numbers",3,true))
                {
                    //response.sendRedirect(req.getScheme()+"://"+Functions.getIpAddress(request));
                    logger.error(Logger.SECURITY_FAILURE,"inside if---login.jsp of ESAPIPartnerFilter...");
                    response.sendRedirect("/partner/logout.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---else login.jsp of ESAPIPartnerFilter...");
                    partner= servletRequest.getParameter("partnerid");
                }
                if (!ESAPI.validator().isValidInput("fromtype ",request.getParameter("fromtype"),"SafeString",20,true))
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---if(from type) login.jsp of ESAPIPartnerFilter...");
                    response.sendRedirect("/partner/logout.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                else
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---else(from type) login.jsp of ESAPIPartnerFilter...");
                    companyname= servletRequest.getParameter("fromtype");
                }

                String hostname             = servletRequest.getRemoteHost();
                String ipaddress            = Functions.getIpAddress(request);
                String forwardedhostname    = Functions.getForwardedHost(request);
                Functions functions         = new Functions();
                logger.error(Logger.SECURITY_FAILURE, "forwardedhostname" + forwardedhostname + "hostname" + hostname);
                logger.error(Logger.SECURITY_FAILURE, "partnerid" + partner + "comapany name" + companyname);
                if(functions.isValueNull(partner) && functions.isValueNull(companyname))
                {
                    logger.error(Logger.SECURITY_FAILURE,"inside---if(partner!=null && companyname!=null) login.jsp of ESAPIPartnerFilter..."+partner +"==="+companyname);
                    if(paymentChecker.isPartnerexistindb(partner, ipaddress, companyname))
                    {
                        logger.error(Logger.SECURITY_FAILURE,"inside partner block of ESAPIPartnerFilter...");
                        session.setAttribute("logo", Merchants.logoHash.get(partner));
                        session.setAttribute("icon", Merchants.iconHash.get(partner));
                        session.setAttribute("defaulttheme",Merchants.defaultTheme.get(partner));
                        session.setAttribute("currenttheme",Merchants.currentTheme.get(partner));
                        session.setAttribute("company",companyname);
                        session.setAttribute("hostname",hostname);
                        session.setAttribute("partnerid",partner);
                        session.setAttribute("ispcilogo",Merchants.pciLogoHash.get(partner));
                    }
                    else
                    {
                        response.sendRedirect("/partner/logout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                }
                else if (!StringUtils.isEmpty(forwardedhostname))
                {
                    Partner partnerClass        = new Partner();
                    Hashtable partnerDetails    = partnerClass.getPartnerDetailsByHost(forwardedhostname);
                    session.setAttribute("logo", partnerDetails.get("LOGO"));
                    session.setAttribute("icon", partnerDetails.get("ICON"));
                    session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                    session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));
                    session.setAttribute("defaulttheme",partnerDetails.get("DEFAULTTHEME"));
                    session.setAttribute("currenttheme",partnerDetails.get("CURRENTTHEME"));
                    session.setAttribute("ispcilogo",partnerDetails.get("ispcilogo"));
                    session.setAttribute("hostname",hostname);
                    logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID")+"------"+partnerDetails.get("ICON")+"-----"+partnerDetails.get("DEFAULTTHEME")+"------"+partnerDetails.get("CURRENTTHEME"));
                }
                else if (!StringUtils.isEmpty(hostname))
                {
                    Partner partnerClass        = new Partner();
                    Hashtable partnerDetails    = partnerClass.getPartnerDetailsByHost(hostname);
                    session.setAttribute("logo", partnerDetails.get("LOGO"));
                    session.setAttribute("icon", partnerDetails.get("ICON"));
                    session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                    session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));
                    session.setAttribute("defaulttheme",partnerDetails.get("DEFAULTTHEME"));
                    session.setAttribute("currenttheme",partnerDetails.get("CURRENTTHEME"));
                    session.setAttribute("ispcilogo",partnerDetails.get("ispcilogo"));
                    session.setAttribute("hostname",hostname);
                    logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID")+"------"+partnerDetails.get("ICON")+"-----"+partnerDetails.get("DEFAULTTHEME")+"------"+partnerDetails.get("CURRENTTHEME"));
                }
                else
                {
                    Enumeration enum1 = request.getParameterNames();
                    if(enum1.hasMoreElements())
                    {
                        response.sendRedirect("/partner/logout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                    else
                    {
                        logger.error(Logger.SECURITY_FAILURE,"inside else of partner block of ESAPIPartnerFilter...");
                        //session.setAttribute("logo", Merchants.adminPartnerHash.get("logo"));
                        //session.setAttribute("icon", Merchants.adminPartnerHash.get("icon"));
                        //session.setAttribute("company",Merchants.adminPartnerHash.get("fromtype"));
                        session.setAttribute("partnerid",Merchants.adminPartnerHash.get("partnerid"));
                        session.setAttribute("ispcilogo",Merchants.adminPartnerHash.get("ispcilogo"));
                    }
                }

            }

            if("partnerfpassword.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                HttpSession session     = request.getSession(true);
                session.setAttribute("valid", "true");
                logger.error(Logger.SECURITY_FAILURE, "insude partnerfpassword.jsp of ESAPIPartnerFilter...");
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                String hostname             = servletRequest.getRemoteHost();
                String ipaddress            = Functions.getIpAddress(request);
                String forwardedhostname    = Functions.getForwardedHost(request);
                Functions functions         = new Functions();
                logger.error(Logger.SECURITY_FAILURE, "forwardedhostname_forgotpass" + forwardedhostname + "hostname_forgotpassword" + hostname);
                logger.error(Logger.SECURITY_FAILURE, "partnerid_forgotpass" + partner + "comapany name_forgotpass" + companyname);
                 if (!StringUtils.isEmpty(forwardedhostname))
                {
                    Partner partnerClass        = new Partner();
                    Hashtable partnerDetails    = partnerClass.getPartnerDetailsByHost(forwardedhostname);
                    session.setAttribute("logo", partnerDetails.get("LOGO"));
                    session.setAttribute("icon", partnerDetails.get("ICON"));
                    session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                    session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));
                    session.setAttribute("defaulttheme",partnerDetails.get("DEFAULTTHEME"));
                    session.setAttribute("currenttheme",partnerDetails.get("CURRENTTHEME"));
                    session.setAttribute("ispcilogo",partnerDetails.get("ispcilogo"));
                    session.setAttribute("hostname",hostname);
                    logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID")+"------"+partnerDetails.get("ICON")+"-----"+partnerDetails.get("DEFAULTTHEME")+"------"+partnerDetails.get("CURRENTTHEME"));
                }
                else if (!StringUtils.isEmpty(hostname))
                {
                    Partner partnerClass        = new Partner();
                    Hashtable partnerDetails    = partnerClass.getPartnerDetailsByHost(hostname);
                    session.setAttribute("logo", partnerDetails.get("LOGO"));
                    session.setAttribute("icon", partnerDetails.get("ICON"));
                    session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                    session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));
                    session.setAttribute("defaulttheme",partnerDetails.get("DEFAULTTHEME"));
                    session.setAttribute("currenttheme",partnerDetails.get("CURRENTTHEME"));
                    session.setAttribute("ispcilogo",partnerDetails.get("ispcilogo"));
                    session.setAttribute("hostname",hostname);
                    logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID")+"------"+partnerDetails.get("ICON")+"-----"+partnerDetails.get("DEFAULTTHEME")+"------"+partnerDetails.get("CURRENTTHEME"));
                }

            }

            if("logout.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                User user   =  (User)ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                //System.out.println("inside Logout---");

                if(request.getParameter("ctoken")==null || request.getParameter("ctoken").equals("")||!Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
                {
                    request.getSession().invalidate();
                    ESAPI.authenticator().logout();
                    response.sendRedirect("/partner/login.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }

            }

            if(apiFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                ESAPI.authenticator().login(request, response);
                //Functions.getNewSession(request);

                User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                if(!Functions.validateAPIToken(request.getParameter("ctoken"),user))
                {
                    response.sendRedirect("/partner/logout.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                logger.debug(Logger.SECURITY_SUCCESS,"API file check done");
            }
            else if(!excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                ESAPI.authenticator().login(request, response);
                Functions.getNewSession(request);

                User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                ESAPI.httpUtilities().setNoCacheHeaders( response );
                if(!Functions.validateCSRF(request.getParameter("ctoken"),user))
                {
                    response.sendRedirect("/partner/logout.jsp");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );
                    return;
                }
                logger.debug(Logger.SECURITY_SUCCESS,"API file check done");
            }

            response.setHeader("X-Frame-Options", "SAMEORIGIN");
        }
        catch( AuthenticationException e )
        {
            ESAPI.authenticator().logout();
            request.setAttribute("message", "Authentication failed");
            response.sendRedirect("/partner/logout.jsp");
            ESAPI.httpUtilities().setNoCacheHeaders( response );
            return;
        }
        catch (SystemError systemError)
        {
            logger.error(Logger.SECURITY_FAILURE, "SystemError--->", systemError);
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
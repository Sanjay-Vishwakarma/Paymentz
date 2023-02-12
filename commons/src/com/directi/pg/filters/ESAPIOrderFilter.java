package com.directi.pg.filters;

import com.directi.pg.DefaultUser;
import com.directi.pg.Functions;
import com.directi.pg.Merchants;
import com.directi.pg.Partner;
import com.payment.checkers.PaymentChecker;
import org.apache.axis.utils.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 4/7/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESAPIOrderFilter implements Filter
{
    private final Logger logger = ESAPI.getLogger("ESAPIOrderFilter");
    private static final Set<String> excludedFiles = new HashSet<String>();

  static
    {
        excludedFiles.add("index.jsp");
        excludedFiles.add("testPayforasia.jsp");
        excludedFiles.add("systemstatuscheck.jsp");
        excludedFiles.add("systemhealthcheck_DR.jsp");
        excludedFiles.add("systemhealthcheck_PR.jsp");
        excludedFiles.add("BankConnHealthCheck");
        excludedFiles.add("redirecturl.jsp");
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
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
        String partner=null;
        String companyname=null;
        PaymentChecker paymentChecker = new PaymentChecker();

        try {
            // figure out who the current user is

            try {



                if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    Functions.setCookie(request);
                }

                if("index.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    HttpSession session = request.getSession(true);
                    String ctoken= request.getParameter("ctoken");
                    User user =  (User)session.getAttribute("Anonymous");
                    session.setAttribute("valid", "true");
                    //System.out.println("1");
                    if(ctoken !=null)
                    {   //System.out.println("2");
                        if(!Functions.validateCSRFAnonymos(ctoken,user))
                        {   //System.out.println("3");
                            response.sendRedirect("/order/sessionout.jsp");
                            return;
                        }
                    }
                    else
                    {   //System.out.println("5");
                        user =   new DefaultUser(User.ANONYMOUS.getName());
                        ctoken = (user).resetCSRFToken();
                    }
                    session.setAttribute("Anonymous", user);
                    request.setAttribute("ctoken",ctoken);

                    logger.error(Logger.SECURITY_FAILURE, "insude index.jsp of ESAPIFilter...");
                    if (!ESAPI.validator().isValidInput("partnerid ",request.getParameter("partnerid"),"Numbers",3,true))
                    {
                        //response.sendRedirect(req.getScheme()+"://"+Functions.getIpAddress(request));
                        logger.error(Logger.SECURITY_FAILURE,"inside if---index.jsp of ESAPIFilter...");
                        response.sendRedirect("/order/sessionout.jsp");
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
                        response.sendRedirect("/order/sessionout.jsp");
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
                            logger.error(Logger.SECURITY_FAILURE,"insideeee partner block of ESAPIFilter...");
                            session.setAttribute("logo", Merchants.logoHash.get(partner));
                            session.setAttribute("company",companyname);
                            session.setAttribute("hostname",hostname);
                            session.setAttribute("partnerid",partner);
                        }
                        else
                        {
                            response.sendRedirect("/order/sessionout.jsp");
                            return;
                        }
                    }
                    else if (!StringUtils.isEmpty(forwardedhostname))
                    {
                        Partner partnerClass = new Partner();
                        Hashtable partnerDetails = partnerClass.getPartnerDetailsByHost(forwardedhostname);
                        session.setAttribute("logo", partnerDetails.get("LOGO"));
                        session.setAttribute("company",partnerDetails.get("PARTNERNAME"));
                        session.setAttribute("partnerid",partnerDetails.get("PARTNERID"));

                        logger.debug(Logger.EVENT_SUCCESS,"----"+partnerDetails.get("LOGO")+"----"+partnerDetails.get("PARTNERNAME")+"----"+partnerDetails.get("PARTNERID"));
                    }
                    else
                    {

                            logger.error(Logger.SECURITY_FAILURE,"inside else of partner block of ESAPIFilter...");
                            session.setAttribute("logo", Merchants.adminPartnerHash.get("logo"));
                            session.setAttribute("company",Merchants.adminPartnerHash.get("fromtype"));
                            session.setAttribute("partnerid",Merchants.adminPartnerHash.get("partnerid"));

                    }

                }
                if(!excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    //System.out.println("-1");
                    String ctoken = request.getParameter("ctoken");
                    HttpSession newsession = Functions.getNewSession(request);
                    User user =  (User)ESAPI.httpUtilities().getSessionAttribute("Anonymous");
                    //System.out.println("-2");

                    //System.out.println(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())+"req "+request.getParameter("ctoken")+"  session "+user.getCSRFToken());
                    if(!Functions.validateCSRFAnonymos(ctoken, user))
                    {                       //System.out.println("-3");

                        response.sendRedirect("/order/sessionout.jsp");
                        return;
                    }

                    (user).resetCSRFToken();
                    newsession.setAttribute("Anonymous", user);
                }



            } catch( Exception e ) {
                logger.error(Logger.SECURITY_FAILURE,"error",e);
                request.setAttribute("message", "Authentication failed");
                //response.sendRedirect("/transaction/sessionout.jsp");


                return;
            }

            // log this request, obfuscating any parameter named password
            //ESAPI.httpUtilities().logHTTPRequest(request, logger, Arrays.asList(obfuscate));


            // check access to this URL
            /*if ( !ESAPI.accessController().isAuthorizedForURL(request.getRequestURI()) ) {
                   request.setAttribute("message", "Unauthorized" );
                   RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/index.jsp");
                   dispatcher.forward(request, response);
                   return;
               }*/

            // check for CSRF attacks
            //ESAPI.httpUtilities().verifyCSRFToken();

            // forward this request on to the web application

            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            filterChain.doFilter(request, response);

            // set up response with content type
            ESAPI.httpUtilities().setContentType( response );

            // set no-cache headers on every response
            // only do this if the entire site should not be cached
            // otherwise you should do this strategically in your controller or actions
            ESAPI.httpUtilities().setNoCacheHeaders( response );

        } catch (Exception e) {
            logger.error( Logger.SECURITY_FAILURE, "Error in ESAPI security filter: " + e.getMessage(), e );
            request.setAttribute("message", e.getMessage() );

        } finally {
            // VERY IMPORTANT
            // clear out the ThreadLocal variables in the authenticator
            // some containers could possibly reuse this thread without clearing the User
            //ESAPI.clearCurrent();
        }
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

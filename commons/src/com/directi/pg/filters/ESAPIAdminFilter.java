package com.directi.pg.filters;

import com.directi.pg.Functions;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/19/13
 * Time: 8:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESAPIAdminFilter implements Filter
{
//    private final Logger logger = ESAPI.getLogger("ESAPIAdminFilter");

    private static final String[] obfuscate = { "password" };

    private static final Set<String> includedFiles = new HashSet<String>();
    private static final Set<String> virtualFiles = new HashSet<String>();
    private static final Set<String> excludedFiles = new HashSet<String>();
    private static final Set<String> apiFiles = new HashSet<String>();
    static{


        //Virtual Files
        virtualFiles.add("ExportTransactions");
        virtualFiles.add("GetGatewayAccount");
        virtualFiles.add("MonitoringRiskRuleDetails");
        virtualFiles.add("GetRuleChangeHistoryDetails");


        //Excluded Files
        excludedFiles.add("logout.jsp");
        excludedFiles.add("login.jsp");
        excludedFiles.add("Login");
        excludedFiles.add("CupBackendServlet");
        excludedFiles.add("CupFrontendServlet");
        excludedFiles.add("CupProcess");
        excludedFiles.add("CupServLet");
        excludedFiles.add("DdpFrontendServlet");
        excludedFiles.add("DdpServlet");
        excludedFiles.add("PaySBMPostVBVServlet");
        excludedFiles.add("PaySBMPostWait");
        excludedFiles.add("PaySBMProcess");
        excludedFiles.add("PaySBMServlet");
        excludedFiles.add("PaySBMVBVServlet");
        excludedFiles.add("PaySBMWait");
        excludedFiles.add("PayVTPostWait");
        excludedFiles.add("PayVTProcess");
        excludedFiles.add("PayVTServlet");
        excludedFiles.add("PayVTWaitServlet");
        excludedFiles.add("ExportMemberDetails");
        excludedFiles.add("ActionWireManager");
        excludedFiles.add("ActionAgentWireManager");
        excludedFiles.add("ActionPartnerWireManager");
        excludedFiles.add("ViewKycDocument");
        excludedFiles.add("GeneratedConsolidatedApplication");
        excludedFiles.add("CreateBankApplication");
        excludedFiles.add("UploadBankTemplate");
        excludedFiles.add("ViewBankTemplate");
        excludedFiles.add("ActionISOCommissionWireManager");
        excludedFiles.add("ActionWLInvoiceManager");
        excludedFiles.add("ExportExcelForMerchant");
        excludedFiles.add("ExportExcelForPartnerTransactions");
        excludedFiles.add("ExportRejectedTransactionsList");
        excludedFiles.add("ExportExcelForBankTransactions");
        excludedFiles.add("ExportFraudAlertTransactions");
        excludedFiles.add("ExportChargeDetails");
        excludedFiles.add("ExportActionHistory");
        excludedFiles.add("ExportTerminalDetails");
        excludedFiles.add("PayoutAmountLimit");
        excludedFiles.add("BlacklistBankAccountNo");
        excludedFiles.add("BdPayoutBatch");
        excludedFiles.add("TransactionsLogs");



        excludedFiles.add("logout.jsp");
        excludedFiles.add("login.jsp");
        excludedFiles.add("Login");
        excludedFiles.add("CupBackendServlet");
        excludedFiles.add("CupFrontendServlet");
        excludedFiles.add("CupProcess");
        excludedFiles.add("CupServLet");
        excludedFiles.add("DdpFrontendServlet");
        excludedFiles.add("DdpServlet");
        excludedFiles.add("PaySBMPostVBVServlet");
        excludedFiles.add("PaySBMPostWait");
        excludedFiles.add("PaySBMProcess");
        excludedFiles.add("PaySBMServlet");
        excludedFiles.add("PaySBMVBVServlet");
        excludedFiles.add("PaySBMWait");
        excludedFiles.add("PayVTPostWait");
        excludedFiles.add("PayVTProcess");
        excludedFiles.add("PayVTServlet");
        excludedFiles.add("PayVTWaitServlet");
        excludedFiles.add("ExportMemberDetails");
        excludedFiles.add("ActionWireManager");
        excludedFiles.add("ActionAgentWireManager");
        excludedFiles.add("ActionPartnerWireManager");
        excludedFiles.add("ViewKycDocument");
        excludedFiles.add("GeneratedConsolidatedApplication");
        excludedFiles.add("CreateBankApplication");
        excludedFiles.add("UploadBankTemplate");
        excludedFiles.add("ViewBankTemplate");
        excludedFiles.add("ActionISOCommissionWireManager");
        excludedFiles.add("ActionWLInvoiceManager");
        excludedFiles.add("ExportExcelForPartnerTransactions");
        excludedFiles.add("ExportExcelForMerchant");
        excludedFiles.add("UploadNewTemplate");
        excludedFiles.add("SendMailToBank");
        excludedFiles.add("ConsolidatedHistoryAction");
        excludedFiles.add("ListMerchantRegisterCard");
        excludedFiles.add("ExportWhitelistDetails");
        excludedFiles.add("ExportPayoutTransactions");
        excludedFiles.add("SetMerchantReservesTemplate");
        excludedFiles.add("AddMerchantTemplateColors");
        apiFiles.add("GetDetailsAPI");

    }




    /**
     * Called by the web container to indicate to a filter that it is being
     * placed into service. The servlet container calls the init method exactly
     * once after instantiating the filter. The init method must complete
     * successfully before the filter is asked to do any filtering work.
     *
     * @param filterConfig
     *            configuration object
     */
    public void init(FilterConfig filterConfig) {
        String path = filterConfig.getInitParameter("resourceDirectory");
        if ( path != null ) {
            ESAPI.securityConfiguration().setResourceDirectory( path );
        }
    }

    /**
     * The doFilter method of the Filter is called by the container each time a
     * request/response pair is passed through the chain due to a client request
     * for a resource at the end of the chain. The FilterChain passed in to this
     * method allows the Filter to pass on the request and response to the next
     * entity in the chain.
     *
     * @param req
     *            Request object to be processed
     * @param resp
     *            Response object
     * @param chain
     *            current FilterChain
     * @exception java.io.IOException
     *                if any occurs
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setAttribute("role","admin");
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        try {
            // figure out who the current user is

            try {

                /*if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    ESAPI.httpUtilities().setNoCacheHeaders( response );

                    Functions.setCookie(request);
                }*/

                if(apiFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {

                    ESAPI.authenticator().login(request, response);
                    //Functions.getNewSession(request);
                    User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );

//                    logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                    if(!Functions.validateAPIToken(request.getParameter("ctoken"), user))
                    {
                        response.sendRedirect("/icici/logout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }

                }

                else if(virtualFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {

                    ESAPI.authenticator().login(request, response);
                    Functions.getNewSession(request);
                    User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    ESAPI.httpUtilities().setNoCacheHeaders( response );

//                    logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                    if(!Functions.validateVirtualCSRF(request.getParameter("ctoken"), user))
                    {
                        response.sendRedirect("/icici/logout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }

                }
                else if(!excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    ESAPI.authenticator().login(request, response);
                    Functions.getNewSession(request);
                    ESAPI.httpUtilities().setNoCacheHeaders( response );

                    User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    //logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                    if(!Functions.validateAdminCSRF(request.getParameter("ctoken"),user))
                    {
                        request.getSession().invalidate();
                        ESAPI.authenticator().logout();
                        response.sendRedirect("/icici/logout.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }
                }

                if("logout.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    User user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    //logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
                    if(request.getParameter("ctoken")==null || request.getParameter("ctoken").equals("")||!Functions.validateCSRFAnonymos(request.getParameter("ctoken"),user))
                    {
                        request.getSession().invalidate();
                        ESAPI.authenticator().logout();
                        response.sendRedirect("/icici/admin/login.jsp");
                        ESAPI.httpUtilities().setNoCacheHeaders( response );
                        return;
                    }

                }
                response.setHeader("X-Frame-Options", "DENY");
            } catch( AuthenticationException e ) {
                ESAPI.authenticator().logout();

                request.setAttribute("message", "Authentication failed");
                response.sendRedirect("/icici/logout.jsp");
                /*RequestDispatcher dispatcher = request.getRequestDispatcher("/merchant/index.jsp");
                    dispatcher.forward(request, response);*/
                ESAPI.httpUtilities().setNoCacheHeaders( response );
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

            //Added for Setting secure cookies
            /*HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // if errors exist then create a sanitized cookie header and continue
            SecurityWrapperResponse securityWrapperResponse = new SecurityWrapperResponse(httpServletResponse, "sanitize");
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookie != null) {
                        // ESAPI.securityConfiguration().getHttpSessionIdName() returns JSESSIONID by default configuration
                        if (ESAPI.securityConfiguration().getHttpSessionIdName().equals(cookie.getName())) {
                            securityWrapperResponse.addCookie(cookie);
                        }
                    }
                }
            }*/


            // forward this request on to the web application
            chain.doFilter(request, response);

            // set up response with content type
            ESAPI.httpUtilities().setContentType( response );

            // set no-cache headers on every response
            // only do this if the entire site should not be cached
            // otherwise you should do this strategically in your controller or actions
            ESAPI.httpUtilities().setNoCacheHeaders( response );


        } catch (Exception e) {
//            logger.error( Logger.SECURITY_FAILURE, "Error in ESAPI security filter: " + e.getMessage(), e );
            request.setAttribute("message", e.getMessage() );

        } finally {
            // VERY IMPORTANT
            // clear out the ThreadLocal variables in the authenticator
            // some containers could possibly reuse this thread without clearing the User
            //ESAPI.clearCurrent();
        }
    }

    private String getFileName(String requestURI)
    {
        String filename = null;

        filename = requestURI.substring(requestURI.lastIndexOf("/")+1);

        return filename;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service. This method is only called once all threads within
     * the filter's doFilter method have exited or after a timeout period has
     * passed. After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter.
     */
    public void destroy() {
        // finalize
    }
}
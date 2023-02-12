package com.directi.pg.filters;

import com.directi.pg.CustomerSupport;
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


public class ESAPIcustomersupport implements Filter
{
    static org.owasp.esapi.Logger log = ESAPI.getLogger("logger1");
    private static final Set<String> excludedFiles = new HashSet<String>();
    static
    {
        excludedFiles.add("signup.jsp");
        excludedFiles.add("sessionout.jsp");
        excludedFiles.add("CustSuppSignup");
        excludedFiles.add("Login");
        excludedFiles.add("login.jsp");
        excludedFiles.add("logout.jsp");
        excludedFiles.add("AGGRequest.jsp");
        excludedFiles.add("checkEntries.jsp");
        excludedFiles.add("customerRegister.jsp");
        excludedFiles.add("customerResponse.jsp");
        excludedFiles.add("merchantDetails.jsp");
        excludedFiles.add("merchants.jsp");
        excludedFiles.add("page.jsp");
        excludedFiles.add("paymentRequest.jsp");
        excludedFiles.add("paymentResponse.jsp");
        excludedFiles.add("transactionDetails.jsp");
        excludedFiles.add("transactions.jsp");
        excludedFiles.add("AGG");
        excludedFiles.add("MerchantDetails");
        excludedFiles.add("TransactionDetails");
        excludedFiles.add("suppUtil");
        excludedFiles.add("forgotPassword.jsp");
        excludedFiles.add("ForgotPassword");
        excludedFiles.add("loginerror.jsp");
        excludedFiles.add("successForwarded.jsp");
    }
    public void init(FilterConfig config) throws ServletException
    {
        String path = config.getInitParameter("resourceDirectory");
        if ( path != null ) {
            ESAPI.securityConfiguration().setResourceDirectory( path );
        }
    }


    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setAttribute("role","support");
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        log.debug(org.owasp.esapi.Logger.SECURITY_SUCCESS, ESAPI.httpUtilities().getCurrentRequest().getRequestURI());
        try {
            log.debug(org.owasp.esapi.Logger.SECURITY_SUCCESS,getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI()));
            try {


                if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    Functions.setCookie(request);
                }

                if(!excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {

                    ESAPI.authenticator().login (request, response);
                    Functions.getNewSession(request);
                    User user =  (User) ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    if(!Functions.validateCSECSRF(request.getParameter("ctoken"), user))
                    {
                        response.sendRedirect("/support/logout.jsp");
                        return;
                    }



                }
                if("logout.jsp".equals(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    User user =  (User) ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");

                    if(request.getParameter("ctoken")==null || request.getParameter("ctoken").equals("")||!Functions.validateCSRFAnonymos(request.getParameter("ctoken"), user))
                    {
                        request.getSession().invalidate();
                        ESAPI.authenticator().logout();
                        CustomerSupport.sessionout_updateLogout();
                        response.sendRedirect("/support/login.jsp");
                        return;
                    }

                }
            } catch( AuthenticationException e ) {
                log.error(org.owasp.esapi.Logger.SECURITY_FAILURE, " AUTHENTICATION EXCEPTION", e);
                ESAPI.authenticator().logout();
                CustomerSupport.sessionout_updateLogout();
                request.setAttribute("isValid", "Authentication failed");
                response.sendRedirect("/support/logout.jsp");

            }


            chain.doFilter(request, response);

            ESAPI.httpUtilities().setContentType( response );

            ESAPI.httpUtilities().setNoCacheHeaders( response );

            response.setHeader("X-Frame-Options", "SAMEORIGIN");

        }catch(Exception e)
        {

            request.setAttribute("message", e.getMessage());
        }
    }

    public void destroy()
    {
    }
    private String getFileName(String requestURI)
    {
        String filename = null;

        filename = requestURI.substring(requestURI.lastIndexOf("/")+1);

        return filename;
    }

}

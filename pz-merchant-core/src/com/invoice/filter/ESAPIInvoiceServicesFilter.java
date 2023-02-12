package com.invoice.filter;

import com.auth.AuthFunctions;
import com.directi.pg.Functions;
import com.directi.pg.Merchants;
import com.merchant.manager.MerchantAuthManager;
import com.merchant.utils.WriteMerchantServiceResponse;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Admin on 6/6/2017.
 */
public class ESAPIInvoiceServicesFilter implements Filter
{

    private final Logger logger = ESAPI.getLogger("ESAPIInvoiceServicesFilter");

    private static final Set<String> excludedFiles = new HashSet<String>();
    private static final Set<String> postCall = new HashSet<String>();
    static{

        excludedFiles.add("authToken");
        excludedFiles.add("demo");
        excludedFiles.add("regenerateToken");

        postCall.add("generate");
        postCall.add("cancel");
        postCall.add("regenerate");
        postCall.add("remind");
        postCall.add("inquiry");
        postCall.add("invoiceTransInquiry");
        postCall.add("getInvoice");
        postCall.add("getInvoiceDetails");
        postCall.add("updateInvoiceConfig");
        postCall.add("getOrderID");
        postCall.add("getInvoiceConfig");

    }

    public void init(FilterConfig filterConfig) {
        String path = filterConfig.getInitParameter("resourceDirectory");
        if ( path != null ) {
            ESAPI.securityConfiguration().setResourceDirectory( path );
        }
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setAttribute("role", "merchant");
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        logger.debug(Logger.SECURITY_FAILURE, "URI---: " + ESAPI.httpUtilities().getCurrentRequest().getRequestURI());

        try
        {
            // figure out who the current user is


            if (excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.httpUtilities().setNoCacheHeaders(response);
                //Functions.setCookie(request);
            }



            if (postCall.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                AuthFunctions authFunctions = new AuthFunctions();
                MerchantAuthManager merchantAuthManager = new MerchantAuthManager();
                Merchants merchants =new Merchants();
                Functions functions=new Functions();
                WriteMerchantServiceResponse writeMerchantServiceResponse = new WriteMerchantServiceResponse();

                String token = request.getHeader("AuthToken");//from header
                String userName = authFunctions.getUserName(token);
                String role = authFunctions.getUserRole(token);

                HttpSession session = request.getSession(true);

                if(userName!=null)
                {

                    request.setAttribute("role",role);
                    User user = (User) ESAPI.authenticator().getUser(userName);



                    boolean isTokenValid = authFunctions.verifyToken(token, userName, role);

                    logger.debug(Logger.SECURITY_FAILURE, "In Included Files List Token---: " + isTokenValid);
                    logger.debug(Logger.SECURITY_FAILURE, "In Included Details---: " + userName + "-token-" + token + "-role-"+role);
                    if(user != null)
                    logger.debug(Logger.SECURITY_FAILURE, "user----" + user.getAccountName());

                    if(isTokenValid)
                    {
                        String loginAuthToken = merchants.getAuthTokenFromMember(userName,role);
                        if (functions.isValueNull(loginAuthToken))
                        {
                            session.setAttribute("ESAPIUserSessionKey", user);
                            ESAPI.authenticator().setCurrentUser(user);
                            request.setAttribute("authfail", true);
                            request.setAttribute("username", userName);
                        }
                        else
                        {
                            session.setAttribute("ESAPIUserSessionKey", null);
                            //ESAPI.authenticator().setCurrentUser(null);
                            request.setAttribute("authfail",false);
                        }
                    }
                    else
                    {
                        session.setAttribute("ESAPIUserSessionKey", null);
                        //ESAPI.authenticator().setCurrentUser(null);
                        request.setAttribute("authfail",false);
                    }

                }
                else
                {
                    session.setAttribute("ESAPIUserSessionKey", null);
                    //ESAPI.authenticator().setCurrentUser(null);
                    request.setAttribute("authfail",false);
                }

            }

            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
            response.setHeader("Pragma","no-cache"); //HTTP 1.0
            response.setDateHeader ("Expires", 0); //prevents caching at the proxy server


            chain.doFilter(request, response);

            // set up response with content type
            //ESAPI.httpUtilities().setContentType( response );

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

    private String getFileName(String requestURI)
    {
        String filename = null;

        filename = requestURI.substring(requestURI.lastIndexOf("/")+1);

        return filename;  //To change body of created methods use File | Settings | File Templates.
    }

    public void destroy() {
        // finalize
    }
}

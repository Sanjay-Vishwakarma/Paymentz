package com.transaction.filters;

import com.auth.AuthFunctions;
import com.directi.pg.Functions;
import com.directi.pg.Merchants;
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
 * Created by Trupti on 6/23/2017.
 */
public class ESAPITransactionServicesFilter implements Filter
{
    private final Logger logger = ESAPI.getLogger("ESAPITransactionServicesFilter");

    private static final Set<String> excludedFiles = new HashSet<String>();
    private static final Set<String> postCall = new HashSet<String>();
    static{

        excludedFiles.add("authToken");
        excludedFiles.add("partnerAuthToken");
        excludedFiles.add("regenerateToken");
        excludedFiles.add("exchangerDeposit");


        postCall.add("payments");
        postCall.add("payments/{id}");
        postCall.add("paywithtoken/{id}");
        postCall.add("getInstallmentWithToken/{id}");
        postCall.add("registrations");
        postCall.add("getCardsAndAccounts");
        postCall.add("checkout");
        postCall.add("paywithtoken");
        postCall.add("getTransactionDetails");
         postCall.add("payout");
        postCall.add("validateWalletDetails");
        postCall.add("checkConfirmation");
        postCall.add("QRPayments");
        postCall.add("QRCheckout");
        postCall.add("QRPayments/{id}");
        postCall.add("inquiryStatus/{id}");
        postCall.add("initiateAuthentication");
        postCall.add("authenticate");
        postCall.add("sendSMSCode");
        postCall.add("getPaymentAndCardType");
        postCall.add("saveTransactionReceipt");
        postCall.add("getTransactionList");
        postCall.add("customerCardWhitelisting");
        postCall.add("query");
        postCall.add("refund");
        postCall.add("getSalesReport");
        postCall.add("generateTransactionOTP");
        postCall.add("verifyTransactionOTP");
        postCall.add("updateUpiTransactionDetails");

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

        //logger.error(Logger.SECURITY_FAILURE, "URI-123 TransactionServices--: " + ESAPI.httpUtilities().getCurrentRequest().getRequestURI());

        try
        {
            // figure out who the current user is
            if (excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
            {
                ESAPI.httpUtilities().setNoCacheHeaders(response);
                //Functions.setCookie(request);
            }

            if (postCall.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())) ||
                    ESAPI.httpUtilities().getCurrentRequest().getRequestURI().contains("paywithtoken") ||
                    ESAPI.httpUtilities().getCurrentRequest().getRequestURI().contains("QRPayments")  ||
                    ESAPI.httpUtilities().getCurrentRequest().getRequestURI().contains("inquiryStatus") ||
                    ESAPI.httpUtilities().getCurrentRequest().getRequestURI().contains("getInstallmentWithToken"))
            {
                Functions functions = new Functions();
                AuthFunctions authFunctions = new AuthFunctions();
                String token = "";

                HttpSession session = request.getSession(true);

                if (functions.isValueNull(request.getHeader("AuthToken")))
                {
                    token = request.getHeader("AuthToken");
                    //logger.error(Logger.SECURITY_FAILURE, "token TransactionServices from Header----" + token);
                }
                else if (functions.isValueNull(request.getParameter("AuthToken")))
                {
                    token = request.getParameter("AuthToken");
                    //logger.error(Logger.SECURITY_FAILURE, "token TransactionServices from Request----" + token);
                }

                if (functions.isValueNull(token))
                {

                    //logger.error(Logger.SECURITY_FAILURE, "token-TransactionServices---" + token);

                    String userName = authFunctions.getUserName(token);
                    String role = authFunctions.getUserRole(token);
                    logger.error(Logger.SECURITY_FAILURE, "In Included Details-TransactionServices--: " + userName + "-token-" + token + "-role-" + role);

                    /*if (userName != null && "merchant".equalsIgnoreCase(role))
                    {*/
                        request.setAttribute("role", role);
                        User user = (User) ESAPI.authenticator().getUser(userName);
                        if(user!=null)
                        {
                            boolean isTokenValid = authFunctions.verifyToken(token, userName, role);
                            logger.error(Logger.SECURITY_FAILURE, "token, userName, role In Included Files List Token-TransactionServices --: " + isTokenValid);
                            logger.error(Logger.SECURITY_FAILURE, "In Included Details-TransactionServices--: " + userName + "-token-" + token + "-role-" + role);
                            logger.error(Logger.SECURITY_FAILURE, "user-TransactionServices---" + user.getAccountName());

                            if (isTokenValid)
                            {
                                logger.error(Logger.SECURITY_FAILURE, "In Token Valid---");
                                session.setAttribute("ESAPIUserSessionKey", user);
                                ESAPI.authenticator().setCurrentUser(user);
                                request.setAttribute("authfail", true);
                                /*if("getTransactionList".equalsIgnoreCase(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))//session deletion
                                {
                                    Merchants merchants=new Merchants();
                                    String loginAuthToken = merchants.getAuthTokenFromMember(userName,role);
                                    if (!functions.isValueNull(loginAuthToken))
                                    {
                                        logger.error(Logger.SECURITY_FAILURE, "User logout---");
                                        session.setAttribute("ESAPIUserSessionKey", user);
                                        ESAPI.authenticator().setCurrentUser(user);
                                        request.setAttribute("authfail", false);
                                    }
                                }*/
                            }
                            else
                            {
                                logger.error(Logger.SECURITY_FAILURE, "In Token In Valid---");
                                session.setAttribute("ESAPIUserSessionKey", null);
                                //ESAPI.authenticator().setCurrentUser(null);
                                request.setAttribute("authfail", false);
                            }
                        }
                        else
                        {
                            logger.error(Logger.SECURITY_FAILURE, "In Token In Valid---");
                            session.setAttribute("ESAPIUserSessionKey", null);
                            //ESAPI.authenticator().setCurrentUser(null);
                            request.setAttribute("authfail", false);
                        }

                    }
                    else
                    {
                        session.setAttribute("ESAPIUserSessionKey", null);
                        //ESAPI.authenticator().setCurrentUser(null);
                        request.setAttribute("authfail", false);
                    }

                /*}
                else
                {
                    session.setAttribute("ESAPIUserSessionKey", null);
                    //ESAPI.authenticator().setCurrentUser(null);
                    request.setAttribute("authfail", false);
                }*/
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

            logger.error( Logger.SECURITY_FAILURE, "Error in ESAPI security filter TransactionServices:::", e );
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

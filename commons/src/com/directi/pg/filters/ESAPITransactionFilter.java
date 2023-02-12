package com.directi.pg.filters;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 1/15/14
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESAPITransactionFilter implements Filter
{
    private static  final TransactionLogger transactionLogger   = new TransactionLogger(ESAPITransactionFilter.class.getName());
    private static final String[] obfuscate                     = { "password" };
    private static final Set<String> newSkitFiles               = new HashSet<String>();
    private static final Set<String> excludedFiles              = new HashSet<String>();
    private static final Set<String> new3DFiles                 = new HashSet<String>();
    static{

        excludedFiles.add("PayProcessController");
        excludedFiles.add("Checkout");
        excludedFiles.add("VirtualCheckout");
        excludedFiles.add("CommProcess");
        excludedFiles.add("EcoreProcess");
        excludedFiles.add("QProcess");
        excludedFiles.add("PayDRProcess");
        excludedFiles.add("PayVoucherProcess");
        excludedFiles.add("PayUGProcess");
        excludedFiles.add("MyMonederoProcess");
        excludedFiles.add("InPayProcess");
        excludedFiles.add("SingleCallGenericCaptureServlet");
        excludedFiles.add("SingleCallGenericReverse");
        excludedFiles.add("SingleCallGenericServlet");
        excludedFiles.add("SingleCallGenericStatus");
        excludedFiles.add("PayInvoice");
        excludedFiles.add("InvoiceAPIController");
        excludedFiles.add("CupBackendServlet");
        excludedFiles.add("CupFrontendServlet");
        excludedFiles.add("InPayFrontEndServlet");
        excludedFiles.add("InPayBackEndServlet");
        excludedFiles.add("InPayBackEndServlet");
        excludedFiles.add("SingleCallGenericVoid");
        excludedFiles.add("PfsBackendServlet");
        excludedFiles.add("PaySafeCardBackEndServlet");
        excludedFiles.add("okPSCconfirmationpage.jsp");
        excludedFiles.add("nokPSCconfirmationpage.jsp");
        excludedFiles.add("SofortBackEndServlet");
        excludedFiles.add("SofortFrontEndServlet");
        excludedFiles.add("P4FrontEndServlet");
        excludedFiles.add("P4BackEndServlet");
        excludedFiles.add("PerfectMoneyBackEndServlet");
        excludedFiles.add("IdealBackEndServlet");
        excludedFiles.add("IdealFrontEndServlet");
        excludedFiles.add("RBFrontendNotification");
        excludedFiles.add("RBBackendNotification");
        excludedFiles.add("EmaxCBNotification");
        excludedFiles.add("QwipiCBNotification");
        excludedFiles.add("PfsRecurringNotification");
        excludedFiles.add("PfsCaptureBackendNotification");
        excludedFiles.add("QwipiBackEndNotification");
        excludedFiles.add("SingleCallTokenTransaction");
        excludedFiles.add("PayProcess3DController");
        excludedFiles.add("SkrillBackendServlet");
        excludedFiles.add("AuxpayBackendNotification");
        excludedFiles.add("AuxpayFrontendNotification");
        excludedFiles.add("PaysecFrontendNotification");
        excludedFiles.add("PaySecBackendNotification");
        excludedFiles.add("ApcoPayFrontEndServlet");
        excludedFiles.add("ApcoPayBackEndServlet");
        excludedFiles.add("ApcoPayFrontEndServlet3D");

        excludedFiles.add("KotakFrontEndNotification");
        excludedFiles.add("VoucherMoneyFrontEndServlet");
        excludedFiles.add("VoucherMoneyBackEndServlet");
        excludedFiles.add("confirm-payment");
        excludedFiles.add("withdrawal-confirm");
        excludedFiles.add("SingleCallManualRebill");
        excludedFiles.add("SingleCallTokenServlet");
        excludedFiles.add("SingleCallCustomerSignup");
        excludedFiles.add("SingleCallMerchantSignup");
        excludedFiles.add("SingleCallFetchCard");
        excludedFiles.add("SingleCallInvalidateToken");
        excludedFiles.add("PBSFrontendServlet");
        excludedFiles.add("StandardProcessController");
        excludedFiles.add("NetellerBackEndServlet");
        excludedFiles.add("NetellerFrontEndServlet");
        excludedFiles.add("ClearSettleFrontEndServlet");
        excludedFiles.add("SkrillFrontEndServlet");
        excludedFiles.add("PerfectMoneyFrontEndServlet");
        excludedFiles.add("DCFrontendNotification");
        excludedFiles.add("DCBackendNotification");
        excludedFiles.add("PaySafeCardFrontEndServlet");
        excludedFiles.add("PaySpaceFrontEndServlet");
        excludedFiles.add("CommINFrontEndServlet");
        excludedFiles.add("BDBackendServlet");
        excludedFiles.add("JetonFrontEndServlet");
        excludedFiles.add("BOFrontEndServlet");
        excludedFiles.add("ClearSettleVoucherFrontEndServlet");
        excludedFiles.add("SCFrontEndServlet");
        excludedFiles.add("EpayFrontEndServlet");
        excludedFiles.add("EpayBackEndServlet");
        excludedFiles.add("RVFrontEndServlet");
        excludedFiles.add("WCFrontEndServlet");
        excludedFiles.add("TrustlyFrontEndServlet");
        excludedFiles.add("TrustlyBackEndServlet");
        excludedFiles.add("TestBENotification");
        excludedFiles.add("PyncsFrontEndServlet");
        excludedFiles.add("PVFrontEndServlet");
        excludedFiles.add("EMXPFrontEndServlet");
        excludedFiles.add("EMSFrontEndServlet");
        excludedFiles.add("ProcessingFrontEndServlet");
        excludedFiles.add("UnicreditFrontEndServlet");
        excludedFiles.add("FourStopBackEndNotification");
        excludedFiles.add("TRANSCTMFrontEndServlet");
        excludedFiles.add("CDNITYFrontEndServlet");
        excludedFiles.add("AWFrontEndServlet");
        excludedFiles.add("LpbFrontEndServlet");
        excludedFiles.add("RSPFrontEndServlet");
        excludedFiles.add("VMWithdrawalBackEndServlet");
        excludedFiles.add("SafexPayFrontEndServlet");
        excludedFiles.add("NPOFrontEndServlet");
        excludedFiles.add("NestPayFrontEndServlet");
        excludedFiles.add("ZotapayFrontEndServlet");
        excludedFiles.add("ZotaPayBackEndServlet");
        excludedFiles.add("SabadellFrontEndServlet");
        excludedFiles.add("SabadellBackEndServlet");
        excludedFiles.add("ClearSettleHPPFrontEndServlet");
        excludedFiles.add("PaySecFrontEndServlet");
        excludedFiles.add("CPFrontEndServlet");
        excludedFiles.add("sessionout.jsp");
        excludedFiles.add("Common3DFrontEndServlet");
        excludedFiles.add("JetonBackEndServlet");
        excludedFiles.add("BitcoinPaygateFrontEndServlet");
        excludedFiles.add("CommonBackEndServlet");
        excludedFiles.add("CardRegistration");
        excludedFiles.add("SingleCallCardRegistration");
        excludedFiles.add("RomCardFrontEndServlet");
        excludedFiles.add("ElegroFrontEndServlet");
        excludedFiles.add("ElegroBackEndServlet");
        excludedFiles.add("TojikaFrontEndServlet");
        excludedFiles.add("FlutterWaveFrontEndServlet");
        excludedFiles.add("OneRoadFrontEndServlet");
        excludedFiles.add("VoguePayFrontEndServlet");
        excludedFiles.add("ICardFrontEndServlet");
        excludedFiles.add("checkout.min.js");
        excludedFiles.add("checkout.js");
        excludedFiles.add("SingleCallCheckout");
        excludedFiles.add("SingleCallVirtualCheckout");
        excludedFiles.add("SingleCallCheckoutSplit");
        excludedFiles.add("checkConfirmation");
        excludedFiles.add("CupUpiSMS");
        excludedFiles.add("UnionPayInternationalFrontEndServlet");
        excludedFiles.add("UnionPayInternationalBackEndServlet");
        excludedFiles.add("ECPFrontEndServlet");
        excludedFiles.add("ECPBackEndServlet");
        excludedFiles.add("DectaNewFrontEndServlet");
        excludedFiles.add("DectaNewBackEndServlet");
        excludedFiles.add("NexiFrontEndServlet");
        excludedFiles.add("FlutterWaveBackEndServlet");
        excludedFiles.add("XPateFrontEndServlet");
        excludedFiles.add("OnlinePayFrontEndServlet");
        excludedFiles.add("OnlinePayBackEndServlet");
        excludedFiles.add("JPBankTransferBackEndServlet");
        excludedFiles.add("PaySendFrontEndServletSuccess");
        excludedFiles.add("PaySendFrontEndServletFail");
        excludedFiles.add("PaySendBackEndServlet");
        excludedFiles.add("NinjaWalletFrontEndServlet");
        excludedFiles.add("PayBoutiqueFrontEndServlet");
        excludedFiles.add("PayBoutiqueBackendServlet");
        excludedFiles.add("SBMFrontEndServlet");
        excludedFiles.add("PhoneixFrontEndServlet");
        excludedFiles.add("CuroFrontEndServlet");
        excludedFiles.add("InvoiceAppFrontEndServlet");
        excludedFiles.add("BhartiPayFrontEndServlet");
        excludedFiles.add("ZotaFrontEndServlet");
        excludedFiles.add("ZotaBackEndServlet");
        excludedFiles.add("EMerchantPayFrontEndServlet");
        excludedFiles.add("FlutterWaveBarterFrontEndServlet");
        excludedFiles.add("FlutterWaveBarterInquiryServlet");
        excludedFiles.add("JetonCardBackEndServlet");
        excludedFiles.add("JetonCardFrontEndServlet");
        excludedFiles.add("rubixpayfrontendservlet");
        excludedFiles.add("TapMioFrontEndServlet");
        excludedFiles.add("XpateBackEndServlet");
        excludedFiles.add("JPBankTransferBackEndServlet");
        excludedFiles.add("rubixpayfrontendservlet");
        excludedFiles.add("RubixPayBackEndServlet");
        excludedFiles.add("EcommpayBackEndServlet");
        excludedFiles.add("SafexPayBackEndServlet");
        excludedFiles.add("CBCGBackEndServlet");
        excludedFiles.add("SafeChargeV2FrontEndServlet");
        excludedFiles.add("SafeChargeV2BackEndServlet");
        excludedFiles.add("vervepayfrontendservlet");
        excludedFiles.add("JPBankTransferBackEndServlet");
        excludedFiles.add("IppoPayBackEndServlet");
        excludedFiles.add("IppoPayFrontEndServlet");
        excludedFiles.add("AlweaveFrontEndServlet");
        excludedFiles.add("AlweaveBackEndServlet");
        excludedFiles.add("ThreedacsRedirect");
        excludedFiles.add("threedacsRedirect.jsp");
        excludedFiles.add("ZhixinfuFrontEndServlet");
        excludedFiles.add("ZhixinfuBackEndServlet");
        excludedFiles.add("OctapayFrontEndServlet");
        excludedFiles.add("OctapayBackEndServlet");
        excludedFiles.add("PayFluidFrontEndServlet");
        excludedFiles.add("PayClubFrontEndServlet");
        excludedFiles.add("PayClubBackEndServlet");
        excludedFiles.add("TWDTaiwanFrontEndServlet");
        excludedFiles.add("TWDTaiwanBackEndServlet");
        excludedFiles.add("GlobalPayFrontEndServlet");
        excludedFiles.add("vervepaybackendservlet");
        excludedFiles.add("virtualTerminalRedirect.jsp");
        excludedFiles.add("QikPayFrontEndServlet");
        excludedFiles.add("LetzPayFrontEndServlet");
        excludedFiles.add("GiftPayFrontEndServlet");
       // excludedFiles.add("BamboraAcquiringFrontEndServlet");
        excludedFiles.add("SecureTradingBackEndServlet");
        excludedFiles.add("QuickPaymentsBackEndServlet");
        excludedFiles.add("PayneteasyBackEndServlet");
        excludedFiles.add("PayEasyWorldFrontEndServlet");
        excludedFiles.add("PayEasyWorldBackEndServlet");
        excludedFiles.add("BennupayBackEndServlet");
        excludedFiles.add("DokuFrontEndServlet");
        excludedFiles.add("DokuBackEndServlet");
        excludedFiles.add("QikPayBackEndServlet");
        excludedFiles.add("EPaymentzFrontEndServlet");
        excludedFiles.add("AsianCheckoutFrontEndServlet");
        excludedFiles.add("OculusBackEndServlet");
        excludedFiles.add("IMoneyPayFrontEndServlet");
        excludedFiles.add("WealthPayFrontEndServlet");
        excludedFiles.add("AppletreeBackentServlet");
        excludedFiles.add("GlobalGateFrontEndServlet");
        excludedFiles.add("GlobalGateBackEndServlet");
        excludedFiles.add("PayUPaymentFrontEndServlet");
        excludedFiles.add("LZPBackEndServlet");
        excludedFiles.add("CashFreeFrontEndServlet");
        excludedFiles.add("OculusFrontEndServlet");
        excludedFiles.add("Fastpayv6FrontendServlet");
        excludedFiles.add("Fastpayv6BackendServlet");
        excludedFiles.add("EcospendBackentServlet");
        excludedFiles.add("GPayFrontEndServlet");
        excludedFiles.add("GPayBackEndServlet");
        excludedFiles.add("PayGPaymentFrontEndServlet");
        excludedFiles.add("PayGPaymentBackEndServlet");
        excludedFiles.add("AXPayFrontEndServlet");
        excludedFiles.add("AirPayBackEndServlet");
        excludedFiles.add("ClevelandFrontEndServlet");
        excludedFiles.add("QPFrontEndServlet");
        excludedFiles.add("CFCFrontEndServlet");
        excludedFiles.add("EPFrontEndServlet");
        excludedFiles.add("EPBackendServlet");
        excludedFiles.add("BoltPayBackendServlet");
        excludedFiles.add("TPayFrontEndServlet");
        excludedFiles.add("TPayBackEndServlet");
        excludedFiles.add("ARFrontEndServlet");
        excludedFiles.add("CRBackendServlet");
        excludedFiles.add("CRFrontendServlet");
        excludedFiles.add("PAFrontendServlet");
        excludedFiles.add("EEBFrontEndServlet");
        excludedFiles.add("OPFrontEndServlet");
        excludedFiles.add("AMPayFrontEndServlet");
        excludedFiles.add("ESPFrontEndServlet");
        excludedFiles.add("TGPayBackEndServlet");
        excludedFiles.add("OPBackEndServlet");
        excludedFiles.add("OMFrontendServlet");
        excludedFiles.add("OMBackendServlet");
        excludedFiles.add("PC21FrontendServlet");
        excludedFiles.add("PC21BackendServlet");
        excludedFiles.add("ESBackendServlet");
        excludedFiles.add("PTMFrontEndServlet");
        excludedFiles.add("LYFrontEndServlet");
        excludedFiles.add("AppleTreePaymentTypeServlet");
        excludedFiles.add("AMBackEndServlet");
        excludedFiles.add("MTBackEndServlet");
        excludedFiles.add("HDFrontEndServlet");
        excludedFiles.add("LYPaymentBackEndServlet");
        excludedFiles.add("UBAMCBackEndServlet");
        excludedFiles.add("EBFrontEndServlet");
        excludedFiles.add("EBBackEndServlet");
        excludedFiles.add("CTFrontendServlet");
        excludedFiles.add("CTBackendServlet");
        excludedFiles.add("BBFrontendServlet");
        excludedFiles.add("TrFrontEndServlet");
        excludedFiles.add("BitCFrontEndServlet");
        excludedFiles.add("XFrontendServlet");
        excludedFiles.add("XBackendServlet");
        excludedFiles.add("BNMQBackEndServlet");
        excludedFiles.add("FPBackendServlet");
        excludedFiles.add("SCBackEndServlet");
        excludedFiles.add("CPayBackEndServlet");
        excludedFiles.add("SFPPaymentBackEndServlet");
        excludedFiles.add("ARBackEndServlet");
        excludedFiles.add("TXFrontEndServlet");
        excludedFiles.add("TXBackEndServlet");
        excludedFiles.add("IPayBackEndServlet");
        excludedFiles.add("PGSFrontEndServlet");
        excludedFiles.add("PGSBackEndServlet");
        excludedFiles.add("SPFrontEndServlet");
        excludedFiles.add("SPBackEndServlet");
        excludedFiles.add("APFrontEndServlet");
        excludedFiles.add("APBackEndServlet");


        //excludedFiles.add("EmiServlet");
    }

    static
    {
        newSkitFiles.add("error.jsp");
        newSkitFiles.add("confirmationpage.jsp");
        newSkitFiles.add("confirmationCheckout.jsp");
        newSkitFiles.add("SingleCallPayment");
        //newSkitFiles.add("SingleCallCheckout");
        newSkitFiles.add("commonPayment.jsp");
        newSkitFiles.add("checkoutPayment.jsp");
        newSkitFiles.add("cardRegistration.jsp");
        /*newSkitFiles.add("cardRegistrationRequest.jsp");*/

    }
    static {

        new3DFiles.add("Process3DConfirmation");

    }

    private final Logger logger = ESAPI.getLogger("ESAPITransactionFilter");

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
        Calendar calendar               = Calendar.getInstance();
        HttpServletRequest request      = (HttpServletRequest) req;
        HttpServletResponse response    = (HttpServletResponse) resp;
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        logger.debug(Logger.SECURITY_SUCCESS,ESAPI.httpUtilities().getCurrentRequest().getRequestURI());

        logger.error(Logger.EVENT_SUCCESS,req.getRemoteAddr());
        logger.error(Logger.EVENT_SUCCESS, req.getRemoteHost());

        try {
            // figure out who the current user is

            try {
                Enumeration<String> parameterNames  = request.getParameterNames();
                transactionLogger.debug("Incoming parameters for transaction from IpAddress::"+request.getRemoteAddr()+" are as follows");
                while(parameterNames.hasMoreElements())
                {
                    String parameterName    = parameterNames.nextElement();
                    //transactionLogger.debug(parameterName+"="+request.getParameter(parameterName));
                }
                HttpSession session = Functions.getNewSession(request);
                User user           = null;
                transactionLogger.debug("Incoming parameters for transaction from IpAddress::"+getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())+" are as follows");
                if(excludedFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    //this condition is using for Session timeout ticker
                    if("PayProcessController".contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())) || "Checkout".contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                    {

                        calendar.setTimeInMillis(session.getLastAccessedTime());
                        session.setAttribute("startTime",calendar);
                        session.setAttribute("timeDiff",0);
                    }

                    if("StandardProcessController".contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                    {
                        calendar.setTimeInMillis(session.getLastAccessedTime());
                        session.setAttribute("startTime",calendar);
                        session.setAttribute("timeDiff",0);
                    }

                    if("PayProcess3DController".contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                    {
                        transactionLogger.debug("inside 3d process");
                        calendar.setTimeInMillis(session.getLastAccessedTime());
                        session.setAttribute("startTime",calendar);
                        session.setAttribute("timeDiff",0);
                    }

                    //user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");
                    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
                    session.setAttribute("ctoken", ctoken);

                }
                else if(newSkitFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    //user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");

                    //this condition is using for Session timeout ticker
                    if("SingleCallPayment".contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                    {
                        long finalCal=Functions.getDiffBetweenTwoCalander((Calendar)session.getAttribute("startTime"),calendar);
                        session.setAttribute("timeDiff",finalCal);

                    }
                    if(!Functions.validateSKitTransactionCSRF(request.getParameter("ctoken"), session))
                    {
                        transactionLogger.debug("validation failed for validateSKitTransactionCSRF");
                        logger.error(Logger.SECURITY_FAILURE,"good one...");
                        response.sendRedirect("/transaction/sessionout.jsp");
                        return;
                    }
                    else
                    {
                        transactionLogger.debug("validation passed for validateSKitTransactionCSRF");
                        String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
                        session.setAttribute("ctoken", ctoken);
                    }
                }
                else if(new3DFiles.contains(getFileName(ESAPI.httpUtilities().getCurrentRequest().getRequestURI())))
                {
                    //user = ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");

                    //this condition is using for Session timeout ticker

                    if(!Functions.validate3DTransactionCSRF(request.getParameter("ctoken"), session))
                    {
                        transactionLogger.debug("validation failed for validate3DTransactionCSRF");
                        logger.error(Logger.SECURITY_FAILURE,"good one...");
                        response.sendRedirect("/transaction/sessionout.jsp");
                        return;
                    }
                    else
                    {
                        transactionLogger.debug("validation passed for validate3DTransactionCSRF");
                        String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
                        session.setAttribute("ctoken", ctoken);
                    }
                }
                else
                {
                    //User user =  (User)ESAPI.httpUtilities().getSessionAttribute("ESAPIUserSessionKey");

                    if(!Functions.validateTransactionCSRF(request.getParameter("ctoken"), session))
                    {
                        transactionLogger.debug("validation failed for validateTransactionCSRF");
                        //response.sendRedirect("/transaction/sessionout.jsp");
                        return;
                    }


                }





            } catch( Exception e ) {

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
            chain.doFilter(request, response);

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

    private String getFileName(String requestURI)
    {
        String filename = null;

        filename        = requestURI.substring(requestURI.lastIndexOf("/")+1);

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
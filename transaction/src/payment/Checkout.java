package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.JPBankTransferVO;
import com.invoice.dao.InvoiceEntry;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.infipay.InfiPayPaymentGateway;
import com.payment.jpbanktransfer.JPBankTransferUtils;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;
import payment.util.ReadRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by Admin on 6/2/2018.
 */
public class    Checkout extends HttpServlet
{

    private static Logger log                           = new Logger(Checkout.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(Checkout.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("entering in Checkout----");
        String error        = "";
        String errorName    = "";
        CommonValidatorVO standardKitValidatorVO    = new CommonValidatorVO();
        Functions functions                         = new Functions();
        CommonInputValidator commonInputValidator   = new CommonInputValidator();
        TransactionHelper transactionHelper         = new TransactionHelper();
        PartnerManager partnerManager               = new PartnerManager();
        PaymentManager paymentManager               = new PaymentManager();
        ErrorCodeUtils errorCodeUtils               = new ErrorCodeUtils();
        MarketPlaceVO marketPlaceVO                 = new MarketPlaceVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        String remoteAddr   = Functions.getIpAddress(req);
        int serverPort      = req.getServerPort();
        String servletPath  = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent    = req.getHeader("User-Agent");
        String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName     = httpProtocol + "://" + remoteAddr;
        String memberId     = null;
        int paymentType     = 0;
        int cardType        = 0;
        String totype       = "";
        int trackingId      = 0;
        int child_trackingid = 0;

        String mailtransactionStatus    = "Failed";
        String billingDiscriptor        = "";

        PrintWriter pWriter = res.getWriter();
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");


        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        try
        {
            standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForSale(req);
            standardKitValidatorVO.setVersion("2");
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

            if (standardKitValidatorVO == null)
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMENT_DETAILS, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            //Member id validation for partner
            memberId    = standardKitValidatorVO.getMerchantDetailsVO().getMemberId();
            totype      = standardKitValidatorVO.getTransDetailsVO().getTotype();
            if (!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length() > 10 || !ESAPI.validator().isValidInput("memberId", memberId, "Numbers", 10, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error                   = errorCodeVO.getErrorDescription();

                transactionLogger.debug("error----" + error+"---"+errorCodeVO.getErrorCode());
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }
            if (!functions.isValueNull(totype) || totype.length() > 30 || !ESAPI.validator().isValidInput("totype", totype, "organizationName", 30, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                error                   = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                log.debug("totype error----" + errorCodeListVO.getListOfError().get(0).getErrorReason());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOTYPE.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("Checkout.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            User user               = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session     = req.getSession();
            session.setAttribute("ctoken", ctoken);
            session.setAttribute("version","2");
            req.setAttribute("ctoken", ctoken);

            //validation
            standardKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
            standardKitValidatorVO = commonInputValidator.performStandardProcessStep1Validations(standardKitValidatorVO,session);
            transactionLogger.debug("merchant logo from Checkout-----" + standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

            if (functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName", standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            String headerVal        = "";
            String merchantSiteName = standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain();
            /*String partrnerTemplate=standardKitValidatorVO.getPartnerDetailsVO().getPartnertemplate();*/
            //String partnerSiteUrl = standardKitValidatorVO.getMerchantDetailsVO().getPartnerDomain();

            transactionLogger.debug("merchantSiteName PayProcessControl---" + merchantSiteName);
            //transactionLogger.error("partnerSiteUrl PayProcessControl---"+partnerSiteUrl);
            transactionLogger.debug("X-Frame-Options from session---" + session.getAttribute("X-Frame-Options"));
            transactionLogger.error("REFERER from header---" + req.getHeader("referer")+"--memberId--"+standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
            if("Y".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getIsDomainWhitelisted()))
            {
                String responseOrigin = req.getHeader("referer");
                standardKitValidatorVO.getAddressDetailsVO().setRequestedReferer(responseOrigin);
                if (functions.isValueNull(responseOrigin))
                {
                    transactionLogger.debug("from merchantSiteName---" + merchantSiteName);
                    boolean isWhitelistedDomin = functions.isDomainWhitelisted(responseOrigin, merchantSiteName);
                    if(!isWhitelistedDomin){
                        error   = "Unauthorized member";
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER));
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.SYS_DOMAIN_WHITELIST_CHECK.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                    }
                }
            }
            if (functions.isValueNull((String) session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String) session.getAttribute("X-Frame-Options")))
            {
                transactionLogger.debug("X-Frame-Options from IF---" + session.getAttribute("X-Frame-Options"));
                res.setHeader("X-Frame-Options", (String) session.getAttribute("X-Frame-Options"));
            }
            else
            {
                String responseOrigin = req.getHeader("referer");
                if (functions.isValueNull(responseOrigin) && functions.isValueNull(merchantSiteName) /*&& responseOrigin.contains(merchantSiteName)*/)
                {
                    transactionLogger.debug("from merchantSiteName---" + merchantSiteName);
                    headerVal = functions.getIFrameHeaderValue(responseOrigin, merchantSiteName);
                    if ("SAMEORIGIN".equals(headerVal))
                    {
                        res.setHeader("X-Frame-Options", headerVal);
                        session.setAttribute("X-Frame-Options", headerVal);
                    }

                    transactionLogger.debug("X-Frame-Options merchantSiteName---" + headerVal);
                }

                else
                {
                    headerVal   = "SAMEORIGIN";
                    res.setHeader("X-Frame-Options", headerVal);
                    session.setAttribute("X-Frame-Options", headerVal);
                    transactionLogger.debug("X-Frame-Options from else---" + headerVal);
                }
                transactionLogger.debug("origin PayProcessControl---" + responseOrigin);

            }

            res.setHeader("Cache-control", "no-store"); //HTTP 1.1
            res.setHeader("Pragma","no-cache"); //HTTP1.0
            res.setDateHeader("Expire", 0); //prevents caching at the proxy server
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            transactionLogger.debug("final header value from session---" + session.getAttribute("X-Frame-Options"));

            if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                //ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                //errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                //errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, standardKitValidatorVO.getErrorCodeListVO(), null, null);
            }

            if (standardKitValidatorVO != null)
            {
                transactionLogger.debug("Amount-----" + standardKitValidatorVO.getTransDetailsVO().getAmount());
                transactionLogger.debug("Tmpl_Amount-----" + standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount());
                if ((functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getAmount())))
                {
                    standardKitValidatorVO.getTransDetailsVO().setAmount(Functions.roundOff(standardKitValidatorVO.getTransDetailsVO().getAmount())); //Amount * 100 according to the docs
                }
                if ((functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount())))
                {
                    standardKitValidatorVO.getAddressDetailsVO().setTmpl_amount(Functions.roundOff(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount()));
                }
            }

            transactionHelper.checksumValidationforSK(standardKitValidatorVO);

            //setting white lable template page
            //getPaymentPageTemplateDetails(standardKitValidatorVO, session);

            transactionLogger.debug("is processed from pay process---" + req.getParameter("isProcessed"));
            if ((!functions.isValueNull(req.getParameter("isProcessed")) || req.getParameter("isProcessed").equalsIgnoreCase("f")))
            {
                transactionLogger.debug("inside is processed if trackingid got---");
                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep0(standardKitValidatorVO);
                if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                }

                partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO);
                standardKitValidatorVO.getTransDetailsVO().setTotype(standardKitValidatorVO.getPartnerName());

                //Begun Transaction Entry
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberId);
                auditTrailVO.setActionExecutorName("CustomerCheckout");
                //if request comes from invoice, remark add to DB
                if (functions.isValueNull(standardKitValidatorVO.getInvoiceId()))
                {
                    standardKitValidatorVO.setReason("Payment is initiated with InvoiceNO:" + standardKitValidatorVO.getInvoiceId());
                }
                if(!"split".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                {
                    trackingId = paymentManager.insertBegunTransactionEntry(standardKitValidatorVO, auditTrailVO, header);

                    if (trackingId != 0)
                    {
                        standardKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                    else
                    {
                        PZExceptionHandler.raiseGenericViolationException("Checkout.class", "doPost()", null, "Transaction", ErrorMessages.DB_COMMUNICATION, null, null, null);
                    }
                }
                if(!"N".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                {
                    String totalamount  = standardKitValidatorVO.getTransDetailsVO().getAmount();
                    if(standardKitValidatorVO.getMarketPlaceVOList()!= null)
                    {
                        List<MarketPlaceVO> mpDetailsList1  = standardKitValidatorVO.getMarketPlaceVOList();
                        List<MarketPlaceVO> mpDetailsList   = new ArrayList<>();
                        String orderid                      = standardKitValidatorVO.getTransDetailsVO().getOrderId();
                        for (int i = 0 ; i < mpDetailsList1.size() ; i++)
                        {
                            marketPlaceVO   = mpDetailsList1.get(i);
                            standardKitValidatorVO.getMerchantDetailsVO().setMemberId(marketPlaceVO.getMemberid());
                            standardKitValidatorVO.getTransDetailsVO().setAmount(marketPlaceVO.getAmount());
                            standardKitValidatorVO.getTransDetailsVO().setOrderId(marketPlaceVO.getOrderid());
                            standardKitValidatorVO.getTransDetailsVO().setOrderDesc(marketPlaceVO.getOrderDesc());
                            child_trackingid    = paymentManager.insertBegunTransactionEntryForMarketPlace(standardKitValidatorVO, auditTrailVO, header,marketPlaceVO.getMerchantDetailsVO().getPartnerName());
                            marketPlaceVO.setTrackingid(String.valueOf(child_trackingid));
                            mpDetailsList.add(i,marketPlaceVO);
                        }
                        standardKitValidatorVO.getMerchantDetailsVO().setMemberId(memberId);
                        standardKitValidatorVO.getTransDetailsVO().setAmount(totalamount);
                        standardKitValidatorVO.getTransDetailsVO().setOrderId(orderid);
                        standardKitValidatorVO.setMarketPlaceVOList(mpDetailsList);
                    }
                }
            }

            if ((functions.isValueNull(standardKitValidatorVO.getPaymentType()) && functions.isValueNull(standardKitValidatorVO.getCardType())) || functions.isValueNull(standardKitValidatorVO.getTerminalId()))
            {
                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep1(standardKitValidatorVO);

                standardKitValidatorVO = commonInputValidator.validateResfield1(standardKitValidatorVO, "STDKIT");

                if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                }

                String fromtype = standardKitValidatorVO.getTransDetailsVO().getFromtype();

                if (fromtype == null)
                {
                    PZExceptionHandler.raiseGenericViolationException("Checkout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_FROMTYPE, null, "", null);
                }
                // autoredirect
                String autoRedirectRequest = standardKitValidatorVO.getTerminalVO().getAutoRedirectRequest();
                if (autoRedirectRequest.equals("Y") && functions.isValueNull(standardKitValidatorVO.getTerminalId()) && standardKitValidatorVO.getTerminalVO().getAddressValidation().equals("N"))
                {
                    AuditTrailVO auditTrailVO = new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    auditTrailVO.setActionExecutorName("CustomerCheckout");

                    MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
                    MerchantDAO merchantDAO             = new MerchantDAO();
                    merchantDetailsVO                   = merchantDAO.getMemberDetails(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                    standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    AbstractPaymentGateway pg   = null;
                    pg                          = AbstractPaymentGateway.getGateway(merchantDetailsVO.getAccountId());

                    //Gateway Specific Validation
                    AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation                = GatewayAccountService.getGatewayAccount(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error                                   = paymentProcess.validateIntegrationSpecificParameters(standardKitValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        errorName   = errorCodeUtils.getErrorNames(standardKitValidatorVO.getErrorCodeListVO().getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, memberId, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        //HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        //standardKitValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        //session.setAttribute("paymentMap", paymentTypeMap);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    else if (standardKitValidatorVO.getMerchantDetailsVO().getIsService().equalsIgnoreCase("N"))
                    {
                        if(fromtype.equalsIgnoreCase("omnipay") ||fromtype.equalsIgnoreCase("Flutter") ||fromtype.equalsIgnoreCase("safexpay") ||fromtype.equalsIgnoreCase("payg") ||fromtype.equalsIgnoreCase("globalpay") ||fromtype.equalsIgnoreCase("payfluid") ||fromtype.equalsIgnoreCase("ippopay") ||
                                fromtype.equalsIgnoreCase("vervepay") ||fromtype.equalsIgnoreCase("bitcoinpg") || fromtype.equalsIgnoreCase("OneRoad") ||
                                fromtype.equalsIgnoreCase("voguepay") || fromtype.equalsIgnoreCase("boutique") || fromtype.equalsIgnoreCase("duspaydd") ||
                                fromtype.equalsIgnoreCase("curo") || fromtype.equalsIgnoreCase("ZOTA") || fromtype.equalsIgnoreCase("doku") ||
                                fromtype.equalsIgnoreCase("wealthpay") || fromtype.equalsIgnoreCase("transfr") || fromtype.equalsIgnoreCase("ubamc") || fromtype.equalsIgnoreCase("paygsmile") || fromtype.equalsIgnoreCase("bnmquick"))
                        {
                            transactionLogger.debug("inside autoRediect n ---");
                            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                            PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                        }
                    }
                    PaymentManager.insertAuthStartedTransactionEntryForCommon(standardKitValidatorVO, standardKitValidatorVO.getTrackingid(), auditTrailVO);
                    String html = "";
                    if(fromtype.equalsIgnoreCase("jpbt"))
                    {
                        InvoiceEntry invoiceEntry   = new InvoiceEntry();
                        if (functions.isValueNull(standardKitValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(standardKitValidatorVO.getInvoiceId(), Integer.parseInt(standardKitValidatorVO.getTrackingid()), standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                        }
                        CommRequestVO commRequestVO     = new CommRequestVO();
                        CommResponseVO transRespDetails = null;
                        commRequestVO                   = JPBankTransferUtils.getCommRequestFromUtils(standardKitValidatorVO);
                        transRespDetails                = (JPBankTransferVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                        if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                        {
                            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                            req.setAttribute("responseStatus", "pending");
                            paymentManager.updatePaymentIdForCommon(transRespDetails, standardKitValidatorVO.getTrackingid());
                        }
                        else
                        {
                            req.setAttribute("responseStatus", "fail");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingId), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = transRespDetails.getStatus().trim();
                        }
                        req.setAttribute("transDetail", standardKitValidatorVO);
                        req.setAttribute("transRespDetails", transRespDetails);
                        req.setAttribute("displayName", billingDiscriptor);
                        req.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_JPBank.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    else
                    {
                        html = pg.processAutoRedirect(standardKitValidatorVO);
                    }


                    if(functions.isValueNull(html) && (html.equalsIgnoreCase("success") || html.equalsIgnoreCase("failed")|| html.equalsIgnoreCase("fail")))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();
                        if (functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getNotificationUrl()))
                        {
                            transactionLogger.error("inside sending notification from Checkout---" + standardKitValidatorVO.getTransDetailsVO().getNotificationUrl());
                            standardKitValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                            TransactionDetailsVO transactionDetailsVO1          = transactionUtility.getTransactionDetails(standardKitValidatorVO);
                            AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, standardKitValidatorVO.getTrackingid(), html, html,"");
                        }
                        if (standardKitValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                        {
                            req.setAttribute("transDetail", standardKitValidatorVO);
                            req.setAttribute("errorName", errorName);
                            req.setAttribute("responceStatus", html);
                            req.setAttribute("displayName", billingDiscriptor);
                            req.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        else
                        {
                            try
                            {

                                transactionUtility.doAutoRedirect(standardKitValidatorVO, res, html, billingDiscriptor);
                            }
                            catch (SystemError systemError)
                            {
                                log.error("System Error while redirecting to redirect url", systemError);
                            }
                        }
                    }
                    else if(!functions.isValueNull(html))
                    {
                        transactionLogger.debug("inside autoRediect n ---");
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                        PZExceptionHandler.raiseConstraintViolationException("Checkout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    else
                    {
                        pWriter.println(html);
                        return;
                    }
                }
                else
                {

                    if(standardKitValidatorVO.getTerminalMap() != null ){
                        String gateway  = "";
                        Iterator keys   = standardKitValidatorVO.getTerminalMap().keySet().iterator();
                        String key      = "";
                        while (keys.hasNext())
                        {
                            key                     = (String)keys.next();
                            TerminalVO terminalVO   = (TerminalVO) standardKitValidatorVO.getTerminalMap().get(key);

                            if(terminalVO.getGateway().equalsIgnoreCase(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE)){
                                req.setAttribute("transDetails", standardKitValidatorVO);
                                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                                req.setAttribute("accountId", terminalVO.getAccountId());
                                RequestDispatcher rd = req.getRequestDispatcher("/checkoutCountryList.jsp?ctoken=" + ctoken);
                                rd.forward(req, res);
                                return;
                            }
                            else if(terminalVO.getGateway().equalsIgnoreCase(InfiPayPaymentGateway.GATEWAY_TYPE)) {
                                String trackingID = standardKitValidatorVO.getTrackingid();
                                String currency   = terminalVO.getCurrency();
                                transactionLogger.error("InfiPayPaymentGateway >>>>>>>>>>>>>>>>>>>>>>> "+terminalVO.getCardType());
                                if("Internet Banking".equalsIgnoreCase(terminalVO.getCardType()) || "Bank Transfer".equalsIgnoreCase(terminalVO.getCardType()))
                                {
                                    List<CommTransactionDetailsVO> list = InfiPayPaymentGateway.processInitialBankCode(trackingID, currency);

                                    req.setAttribute("bankcodeList", list);
                                    break;
                                }

                            }
                        }
                    }

                    transactionLogger.error("test ------------1 ");
                    req.setAttribute("transDetails", standardKitValidatorVO);
                    req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                    req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                    RequestDispatcher rd = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                    rd.forward(req, res);
                    return;
                }

            }/*ne flow appletree else if (functions.isValueNull(standardKitValidatorVO.getCardType()) && standardKitValidatorVO.getCardType().equals("123"))
            {
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                auditTrailVO.setActionExecutorName("CustomerCheckout");

                MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
                MerchantDAO merchantDAO = new MerchantDAO();
                System.out.println("mEMBER iD" + standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO = merchantDAO.getMemberDetails(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                System.out.println("FIRST" + standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                System.out.println("SECOND" + merchantDetailsVO.getAccountId());

                merchantDetailsVO.setAccountId(standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractPaymentGateway pg = null;
                pg = AbstractPaymentGateway.getGateway(merchantDetailsVO.getAccountId());
                CommRequestVO commRequestVO = new CommRequestVO();
                Comm3DResponseVO transRespDetails = null;
                commRequestVO = AppleTreeCellulantUtils.getCommRequestFromUtils(standardKitValidatorVO);
                transRespDetails = (Comm3DResponseVO) pg.getOptionalCodeList(String.valueOf(trackingId), commRequestVO);
                String optionalcode= transRespDetails.getOptionalCode();
                System.out.println(optionalcode);
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    req.setAttribute("responseStatus", "pending");
                    paymentManager.updatePaymentIdForCommon(transRespDetails, standardKitValidatorVO.getTrackingid());
                }
                else
                {
                    req.setAttribute("responseStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingId), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", standardKitValidatorVO);
                req.setAttribute("transRespDetails", transRespDetails);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }*/
            else
            {
                LimitChecker limitChecker           = new LimitChecker();
                MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
                if (merchantDetailsVO.getCheckLimit().equals("1"))
                {
                    //TODO : Merchant amount limit check
                    Date date3 = new Date();
                    transactionLogger.debug("TransactionHelper checkAmountLimit start #########" + date3.getTime());
                    limitChecker.checkAmountLimit(standardKitValidatorVO);
                    transactionLogger.debug("TransactionHelper checkAmountLimit end #########" + new Date().getTime());
                    transactionLogger.debug("TransactionHelper checkAmountLimit diff #########" + (new Date().getTime() - date3.getTime()));
                }
                transactionLogger.debug("standardKitValidatorVO.getTerminalMap() ----> " + standardKitValidatorVO.getTerminalMap());
                if(standardKitValidatorVO.getTerminalMap() != null ){
                    String gateway  = "";
                    Iterator keys   = standardKitValidatorVO.getTerminalMap().keySet().iterator();
                    String key      = "";
                    while (keys.hasNext())
                    {
                        key                     = (String)keys.next();
                        TerminalVO terminalVO   = (TerminalVO) standardKitValidatorVO.getTerminalMap().get(key);
                        transactionLogger.debug("terminalVO.getGateway() ----> " + terminalVO.getGateway());
                        if(terminalVO.getGateway().equalsIgnoreCase(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE)){
                            req.setAttribute("transDetails", standardKitValidatorVO);
                            req.setAttribute("ctoken", session.getAttribute("ctoken"));
                            req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                            req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                            req.setAttribute("accountId", terminalVO.getAccountId());

                            RequestDispatcher rd = req.getRequestDispatcher("/checkoutCountryList.jsp?ctoken=" + ctoken);
                            rd.forward(req, res);
                            return;
                        }else if(terminalVO.getGateway().equalsIgnoreCase(InfiPayPaymentGateway.GATEWAY_TYPE)) {
                            String trackingID = standardKitValidatorVO.getTrackingid();
                            String currency   = terminalVO.getCurrency();
                            if("Internet Banking".equalsIgnoreCase(terminalVO.getCardType()) || "Bank Transfer".equalsIgnoreCase(terminalVO.getCardType()) )
                            {
                                List<CommTransactionDetailsVO> list = InfiPayPaymentGateway.processInitialBankCode(trackingID, currency);

                                req.setAttribute("bankcodeList", list);
                                break;
                            }

                        }
                    }
                }


                req.setAttribute("transDetails", standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                RequestDispatcher rd = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("-------PZConstraintViolationException in Checkout------", cve);
            transactionLogger.error("PZConstraintViolationException occured ", cve);
            error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
            if (standardKitValidatorVO != null && functions.isValueNull(standardKitValidatorVO.getTrackingid()))
            {
                errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                try
                {
                    paymentManager.updateDetailsTablewithErrorName(errorName, standardKitValidatorVO.getTrackingid());
                }
                catch (PZDBViolationException d)
                {
                    log.error("----PZDBViolationException in update with error name-----", d);
                }

            }
            PZExceptionHandler.handleCVEException(cve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetail", standardKitValidatorVO);
            //RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dve)
        {
            log.error("----PZDBViolationException in Checkout-----", dve);
            error = "Internal Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleDBCVEException(dve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + dve.getPzdbConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + dve.getPzdbConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            //RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZTechnicalViolationException tve)
        {
            log.error("----PZTechnicalViolationException in Checkout-----", tve);
            error = "Technical Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleTechicalCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + tve.getPzGenericConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            //RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in Checkout-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleGenericCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + tve.getPzGenericConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            //RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (SystemError tve)
        {
            log.error("----PZGenericConstraintViolationException in Checkout-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            req.setAttribute("transDetail", standardKitValidatorVO);
            //RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
    }
}


package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.unicredit.UnicreditPaymentGateway;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 8/21/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayProcessController extends HttpServlet
{
    private static Logger log = new Logger(PayProcessController.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayProcessController.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("entering in PayProcessController----");
        String error="";
        String errorName ="";
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        Functions functions=new Functions();
        CommonInputValidator commonInputValidator=new CommonInputValidator();
        TransactionHelper transactionHelper=new TransactionHelper();
        PartnerManager partnerManager=new PartnerManager();
        PaymentManager paymentManager=new PaymentManager();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol=req.getScheme();
        String userAgent = req.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent=" + userAgent;
        transactionLogger.debug("Header::::-->"+header);
        String hostName=httpProtocol+"://"+remoteAddr;
        String memberId = null;
        int paymentType=0;
        int cardType=0;
        String totype="";
        int trackingId = 0;

        String mailtransactionStatus = "Failed";
        String billingDiscriptor = "";

        PrintWriter pWriter = res.getWriter();
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");


        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        try
        {
            standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForSale(req);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            standardKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

            if(standardKitValidatorVO==null)
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                error=errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class","doPost()",null,"Transaction",ErrorMessages.INVALID_PAYMENT_DETAILS,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            //Member id validation for partner
            memberId=standardKitValidatorVO.getMerchantDetailsVO().getMemberId();
            totype=standardKitValidatorVO.getTransDetailsVO().getTotype();
            if(!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length()>10 || !ESAPI.validator().isValidInput("memberId",memberId,"Numbers",10,false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error=errorCodeVO.getErrorDescription();

                transactionLogger.debug("error----"+error);
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class","doPost()",null,"Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }
            if(!functions.isValueNull(totype) || totype.length()>30 || !ESAPI.validator().isValidInput("totype",totype,"organizationName",30,false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                error=errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                log.debug("totype error----"+errorCodeListVO.getListOfError().get(0).getErrorReason());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOTYPE.toString(),ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("PayProcessController.java","doPost()",null,"Transaction",ErrorMessages.INVALID_TOTYPE,PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }

            User user = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken",ctoken);
            req.setAttribute("ctoken",ctoken);


            //setting white lable template page
            //getPaymentPageTemplateDetails(standardKitValidatorVO, session);

            //validation
            standardKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
            standardKitValidatorVO =  commonInputValidator.performStandardProcessStep1Validations(standardKitValidatorVO, session);
            transactionLogger.debug("merchant logo from PayProcessController-----"+standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

            String headerVal = "";
            String merchantSiteName = standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain();
            /*String partrnerTemplate=standardKitValidatorVO.getPartnerDetailsVO().getPartnertemplate();*/
            //System.out.println("partrnerTemplate------"+partrnerTemplate);
            //String partnerSiteUrl = standardKitValidatorVO.getMerchantDetailsVO().getPartnerDomain();

            transactionLogger.error("merchantSiteName PayProcessControl---"+merchantSiteName);
            //transactionLogger.error("partnerSiteUrl PayProcessControl---"+partnerSiteUrl);
            transactionLogger.error("X-Frame-Options from session---"+session.getAttribute("X-Frame-Options"));
            transactionLogger.error("REFERER from header---"+req.getHeader("referer"));

            if(functions.isValueNull((String)session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String)session.getAttribute("X-Frame-Options")))
            {
                transactionLogger.error("X-Frame-Options from IF---"+session.getAttribute("X-Frame-Options"));
                res.setHeader("X-Frame-Options", (String) session.getAttribute("X-Frame-Options"));
            }
            else
            {
                String responseOrigin = req.getHeader("referer");
                if(functions.isValueNull(responseOrigin) && functions.isValueNull(merchantSiteName) /*&& responseOrigin.contains(merchantSiteName)*/)
                {
                    transactionLogger.error("from merchantSiteName---"+merchantSiteName);
                    headerVal = functions.getIFrameHeaderValue(responseOrigin,merchantSiteName);
                    if ("SAMEORIGIN".equals(headerVal))
                    {
                        res.setHeader("X-Frame-Options", headerVal);
                        session.setAttribute("X-Frame-Options", headerVal);
                    }

                    transactionLogger.error("X-Frame-Options merchantSiteName---"+headerVal);
                }

                else
                {
                    headerVal = "SAMEORIGIN";
                    res.setHeader("X-Frame-Options",headerVal);
                    session.setAttribute("X-Frame-Options", headerVal);
                    transactionLogger.error("X-Frame-Options from else---"+headerVal);
                }
                transactionLogger.error("origin PayProcessControl---"+responseOrigin);

            }
            transactionLogger.error("final header value from session---"+session.getAttribute("X-Frame-Options"));

            if(!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                //ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                //errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                //errorCodeListVO.addListOfError(errorCodeVO);
                //System.out.println("msggg---"+standardKitValidatorVO.getErrorMsg());
                PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class","doPost()",null,"Transaction",standardKitValidatorVO.getErrorMsg(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,standardKitValidatorVO.getErrorCodeListVO(),null,null);
            }

            if(standardKitValidatorVO!=null)
            {
                transactionLogger.debug("Amount-----"+standardKitValidatorVO.getTransDetailsVO().getAmount());
                transactionLogger.debug("Tmpl_Amount-----"+standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount());
                if((functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getAmount()))){
                    standardKitValidatorVO.getTransDetailsVO().setAmount(Functions.roundOff(standardKitValidatorVO.getTransDetailsVO().getAmount())); //Amount * 100 according to the docs
                }
                if((functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount()))){
                    standardKitValidatorVO.getAddressDetailsVO().setTmpl_amount(Functions.roundOff(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount()));
                }
            }

            transactionHelper.checksumValidationforSK(standardKitValidatorVO);

            if (functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName", standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }

            transactionLogger.debug("is processed from pay process---"+req.getParameter("isProcessed"));
            if((!functions.isValueNull(req.getParameter("isProcessed")) || req.getParameter("isProcessed").equalsIgnoreCase("f")))
            {
                transactionLogger.debug("inside is processed if trackingid got---");
                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep0(standardKitValidatorVO);
                if(!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class","doPost()",null,"Transaction",standardKitValidatorVO.getErrorMsg(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
                }

                partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO);
                standardKitValidatorVO.getTransDetailsVO().setTotype(standardKitValidatorVO.getPartnerName());

                //Begun Transaction Entry
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberId);
                auditTrailVO.setActionExecutorName("Customer");
                //if request comes from invoice, remark add to DB
                if(functions.isValueNull(standardKitValidatorVO.getInvoiceId()))
                {
                    standardKitValidatorVO.setReason("Payment is initiated with InvoiceNO:"+standardKitValidatorVO.getInvoiceId());
                }
                trackingId = paymentManager.insertBegunTransactionEntry(standardKitValidatorVO,auditTrailVO, header);

                if (trackingId != 0)
                {
                    standardKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                }
                else
                {
                    PZExceptionHandler.raiseGenericViolationException("PayProcessController.class", "doPost()", null, "Transaction", ErrorMessages.DB_COMMUNICATION, null, null, null);
                }
            }

            if("t".equalsIgnoreCase(req.getParameter("isProcessed")))
            {
                transactionHelper.checksumValidationforSK(standardKitValidatorVO);
                HashMap paymentMap = (HashMap) session.getAttribute("paymentMap");
                standardKitValidatorVO.setMapOfPaymentCardType(paymentMap);
            }

            //First card type selected for all payment type
            if(functions.isValueNull(standardKitValidatorVO.getPaymentType()) && functions.isEmptyOrNull(standardKitValidatorVO.getCardType()) && functions.isEmptyOrNull(standardKitValidatorVO.getTerminalId()))
            {
                HashMap pMap = standardKitValidatorVO.getMapOfPaymentCardType();
                List<String> sCard = (List<String>) pMap.get(standardKitValidatorVO.getPaymentType());

                standardKitValidatorVO.setPaymentType(standardKitValidatorVO.getPaymentType());
                if(sCard.size()==1)
                    standardKitValidatorVO.setCardType(sCard.get(0));
            }
            //For first payment type and first cardtype selected
            if(functions.isEmptyOrNull(standardKitValidatorVO.getPaymentType()) && functions.isEmptyOrNull(standardKitValidatorVO.getTerminalId()))
            {
                HashMap pMap = standardKitValidatorVO.getMapOfPaymentCardType();
                String firstPayMode = (String)pMap.keySet().toArray()[0];
                List<String>  firstCardType = (List<String>) pMap.get(firstPayMode);
                standardKitValidatorVO.setPaymentType(firstPayMode);
                if(firstCardType.size()==1)
                    standardKitValidatorVO.setCardType(firstCardType.get(0));
            }
            if((functions.isValueNull(standardKitValidatorVO.getPaymentType()) && functions.isValueNull(standardKitValidatorVO.getCardType())) || functions.isValueNull(standardKitValidatorVO.getTerminalId()) )
            {
                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep1(standardKitValidatorVO);

                standardKitValidatorVO = commonInputValidator.validateResfield1(standardKitValidatorVO, "STDKIT");

                if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                }

                String fromtype = standardKitValidatorVO.getTransDetailsVO().getFromtype();

                if (fromtype == null)
                {
                    PZExceptionHandler.raiseGenericViolationException("PayProcessController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_FROMTYPE, null, "", null);
                }

                paymentType = Integer.valueOf(standardKitValidatorVO.getPaymentType());
                cardType = Integer.valueOf(standardKitValidatorVO.getCardType());

                transactionLogger.debug("paymentType------"+paymentType);
                transactionLogger.debug("cardType------"+cardType);

                String autoRedirectRequest = standardKitValidatorVO.getTerminalVO().getAutoRedirectRequest();
                if (autoRedirectRequest.equals("Y") && functions.isValueNull(standardKitValidatorVO.getTerminalId()) && standardKitValidatorVO.getTerminalVO().getAddressValidation().equals("N"))
                {
                    AuditTrailVO auditTrailVO = new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    auditTrailVO.setActionExecutorName("Customer");

                    MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
                    MerchantDAO merchantDAO = new MerchantDAO();
                    merchantDetailsVO = merchantDAO.getMemberDetails(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                    standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    AbstractPaymentGateway pg = null;
                    pg = AbstractPaymentGateway.getGateway(merchantDetailsVO.getAccountId());

                    //Gateway Specific Validation
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(standardKitValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, memberId, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        //HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        //standardKitValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        //session.setAttribute("paymentMap", paymentTypeMap);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    PaymentManager.insertAuthStartedTransactionEntryForCommon(standardKitValidatorVO, standardKitValidatorVO.getTrackingid(), auditTrailVO);

                    String html = pg.processAutoRedirect(standardKitValidatorVO);

                    if(functions.isValueNull(html) && (html.equalsIgnoreCase("success") || html.equalsIgnoreCase("failed")))
                    {
                        //For Perfect Money Server to Server e-voucher
                        TransactionUtility transactionUtility = new TransactionUtility();
                        if (functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getNotificationUrl()))
                        {
                            transactionLogger.error("inside sending notification---" + standardKitValidatorVO.getTransDetailsVO().getNotificationUrl());
                            standardKitValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(standardKitValidatorVO);

                            if(functions.isValueNull(standardKitValidatorVO.getCardDetailsVO().getVoucherNumber()))
                                transactionDetailsVO1.setTrackingid(standardKitValidatorVO.getTrackingid()+"-"+standardKitValidatorVO.getCardDetailsVO().getVoucherNumber());
                            else
                                transactionDetailsVO1.setTrackingid(standardKitValidatorVO.getTrackingid());

                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, transactionDetailsVO1.getTrackingid(), html, html,"");
                        }
                        if (standardKitValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                        {
                            req.setAttribute("transDetail", standardKitValidatorVO);
                            req.setAttribute("errorName", errorName);
                            req.setAttribute("responceStatus", html);
                            req.setAttribute("displayName", billingDiscriptor);
                            req.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
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
                    else
                    {
                        pWriter.println(html);
                        return;
                    }
                }
                else
                {
                    if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType) && CardTypeEnum.CUP_CARDTYPE.ordinal() == cardType)
                    {
                        //redirect to cup process
                        session.setAttribute("childfile", "cupPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.INPAY_CARDTYPE.ordinal() == cardType)
                    {
                        //NET_BANKING
                        session.setAttribute("childfile", "inpayPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.PAYSEC_CARDTYPE.ordinal() == cardType)
                    {
                        //NET_BANKING
                        session.setAttribute("childfile", "inpayPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.BILLDESK.ordinal() == cardType)
                    {
                        //NET_BANKING
                        session.setAttribute("childfile", "inpayPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.TRUSTLY.ordinal() == cardType)
                    {
                        //NET_BANKING
                        //session.setAttribute("childfile", "inpayPayment.jsp");
                        session.setAttribute("childfile", "trustlyspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    } else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.SAFEXPAY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "safexpay.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "paysafecardPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.SOFORT_CARDTYPE.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "sofortPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));

                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.IDEAL_CARDTYPE.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "idealPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));

                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "p4Payment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.NEOSURF.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.PURPLEPAY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "purplepaypayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.AVISA.ordinal() == cardType || CardTypeEnum.AMC.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "aldrapayspcificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.FLUTTER.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.GIROPAY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.MULTIBANCO.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.QIWI.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.YANDEX.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.AllPay88.ordinal() == cardType)
                    {
                    session.setAttribute("childfile", "allPay88.jsp");
                    req.setAttribute("transDetails", standardKitValidatorVO);
                    req.setAttribute("ctoken", session.getAttribute("ctoken"));
                   }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.NETELLER.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "netellerPayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.SKRILL.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "skrillpayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.EPAY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "epaypayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.JETON.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "jetonpayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.JETON_VOUCHER.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "jetonvoucher.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if ((PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType || PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType) && CardTypeEnum.PERFECTMONEY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.ASTROPAY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "apcopayspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.SEPA.ordinal() == paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal() == cardType)
                    {
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                        session.setAttribute("childfile", "p4SepaPayment.jsp");
                    }
                    else if (PaymentModeEnum.ACH.ordinal() == paymentType && CardTypeEnum.ACH.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "paymitcopayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.CHK.ordinal() == paymentType && CardTypeEnum.CHK.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "paymitcopayment.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.SEPA.ordinal() == paymentType && CardTypeEnum.SEPA_EXPRESS.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "b4SepaExpress.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }

                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.VOUCHERMONEY.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "vouchermoneyspecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType)
                    {
                        session.setAttribute("childfile", "clearsettlevoucherpayment.jsp");
                        session.setAttribute("filename", "clearsettlespecificfields.jsp");
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    }
                    else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType)
                    {
                        transactionLogger.debug("fromtype------"+fromtype);
                        if (Functions.checkProcessGateways(fromtype))
                        {
                            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(standardKitValidatorVO.getMerchantDetailsVO().getAccountId() + ""));
                            String filename = paymentProcess.getSpecificVirtualTerminalJSP();
                            req.setAttribute("filename", filename);
                            session.setAttribute("filename", filename);
                            if (ReitumuBankSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || DectaSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype))
                            {
                                session.setAttribute("childfile", "reitumuCreditPage.jsp");//credit page without card details
                            }
                            else if(UnicreditPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype))
                            {
                                session.setAttribute("childfile", "paynow.jsp");//credit page without card details
                            }
                            else if(ClearSettleHPPGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype)){
                                session.setAttribute("childfile", "inpayPayment.jsp");
                                session.setAttribute("filename", "clearsettlespecificfields.jsp");
                            }
                            else
                            {
                                session.setAttribute("childfile", "creditcardpayment.jsp");
                            }
                            req.setAttribute("transDetails", standardKitValidatorVO);
                            req.setAttribute("ctoken", session.getAttribute("ctoken"));
                        }
                        else
                        {
                            PZExceptionHandler.raiseGenericViolationException("PayProcessController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_GATEWYAY, null, null, null);
                        }
                    }
                    else
                    {
                        PZExceptionHandler.raiseGenericViolationException("PayProcessController.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMODE_CARDTYPE, null, null, null);
                    }
                    req.setAttribute("transDetails", standardKitValidatorVO);
                    req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    req.setAttribute("call", "2");
                    req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                    req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                    RequestDispatcher rd = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    rd.forward(req, res);
                    return;
                }

            }
            else
            {
                LimitChecker limitChecker=new LimitChecker();
                MerchantDetailsVO merchantDetailsVO=standardKitValidatorVO.getMerchantDetailsVO();
                if (merchantDetailsVO.getCheckLimit().equals("1"))
                {
                    //TODO : Merchant amount limit check
                    Date date3 = new Date();
                    transactionLogger.debug("TransactionHelper checkAmountLimit start #########" + date3.getTime());
                    limitChecker.checkAmountLimit(standardKitValidatorVO);
                    transactionLogger.debug("TransactionHelper checkAmountLimit end #########" + new Date().getTime());
                    transactionLogger.debug("TransactionHelper checkAmountLimit diff #########" + (new Date().getTime() - date3.getTime()));
                }
                req.setAttribute("transDetails", standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                req.setAttribute("call", "2");
                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                RequestDispatcher rd = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("-------PZConstraintViolationException in PayProcessController------",cve);
            transactionLogger.error("PZConstraintViolationException occured ", cve);
            error = errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());
            if(standardKitValidatorVO!=null && functions.isValueNull(standardKitValidatorVO.getTrackingid()))
            {
                errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                try
                {
                    paymentManager.updateDetailsTablewithErrorName(errorName,standardKitValidatorVO.getTrackingid());
                }
                catch (PZDBViolationException d)
                {
                    log.error("----PZDBViolationException in update with error name-----", d);
                }

            }
            PZExceptionHandler.handleCVEException(cve,standardKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error);
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZDBViolationException dve)
        {
            log.error("----PZDBViolationException in PayProcessController-----",dve);
            error = "Internal Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleDBCVEException(dve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + dve.getPzdbConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + dve.getPzdbConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZTechnicalViolationException tve)
        {
            log.error("----PZTechnicalViolationException in PayProcessController-----",tve);
            error = "Technical Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleTechicalCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error +" : "+tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error+" : "+tve.getPzGenericConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
        catch(PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in PayProcessController-----",tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleGenericCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error",error+" : "+tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error+" : "+tve.getPzGenericConstraint().getMessage());
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            rd.forward(req,res);
            return;
        }
        catch (SystemError tve)
        {
            log.error("----PZGenericConstraintViolationException in PayProcessController-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            req.setAttribute("standardvo",standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            rd.forward(req,res);
            return;
        }
    }
}
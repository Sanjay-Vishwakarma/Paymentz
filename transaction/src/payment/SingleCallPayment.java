package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.fraud.FraudChecker;
import com.invoice.dao.InvoiceEntry;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.dao.FraudServiceDAO;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.*;
import com.payment.PZTransactionStatus;
import com.payment.PayMitco.core.PayMitcoUtility;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.allPay88.AllPay88Utils;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.b4payment.B4Utils;
import com.payment.b4payment.vos.TransactionResponse;
import com.payment.billdesk.BillDeskUtils;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.clearsettle.ClearSettleUtills;
import com.payment.common.core.*;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.epay.EpayUtils;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.inpay.InPayUtil;
import com.payment.jeton.JetonResponseVO;
import com.payment.jeton.JetonUtils;
import com.payment.neteller.NetellerUtils;
import com.payment.neteller.response.Links;
import com.payment.neteller.response.NetellerResponse;
import com.payment.p4.gateway.P4ResponseVO;
import com.payment.p4.gateway.P4Utils;
import com.payment.paySafeCard.PaySafeCardUtils;
import com.payment.paysec.PaySecUtils;
import com.payment.perfectmoney.PerfectMoneyUtils;
import com.payment.safexpay.SafexPayUtils;
import com.payment.skrill.SkrillUtills;
import com.payment.sms.AsynchronousSmsService;
import com.payment.sofort.SofortUtility;
import com.payment.sofort.VO.SofortResponseVO;
import com.payment.trustly.TrustlyUtils;
import com.payment.unicredit.UnicreditUtils;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.vouchermoney.VoucherMoneyResponse;
import com.payment.vouchermoney.VoucherMoneyUtils;
import com.payment.whitelabel.WLUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import payment.util.ReadRequest;
import payment.util.SingleCallPaymentDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.payment.paysec.PaySecUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 8/26/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallPayment extends HttpServlet
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CupServlet");
    final static ResourceBundle paysafecard = LoadProperties.getProperty("com.directi.pg.paysafecard");
    final static ResourceBundle apcopay = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
    private static Logger log = new Logger(SingleCallPayment.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallPayment.class.getName());
    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
    TransactionUtility transactionUtility = new TransactionUtility();

    public static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet())
        {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Inside single call::");
        //check Authenticate request and session
        PrintWriter pWriter = res.getWriter();
        String error = "";
        HttpSession session = req.getSession();
        String ctoken = (String) session.getAttribute("ctoken");
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        CommonValidatorVO commonValidatorVO = null;
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        commonValidatorVO = ReadRequest.getSpecificRequestParametersForSale(req);
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        AbstractPaymentGateway pg = null;
        SingleCallPaymentDAO singleCallPaymentDAO = new SingleCallPaymentDAO();
        ActionEntry entry = new ActionEntry();
        Functions functions = new Functions();
        PaymentManager paymentManager = new PaymentManager();
        TransactionHelper transactionHelper = new TransactionHelper();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        RecurringManager recurringManager = new RecurringManager();
        RecurringBillingVO recurringBillingVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        int paymentType = 0;
        int cardType = 0;
        String respStatus = null;
        String respPaymentId = null;
        String mailtransactionStatus = "Failed";
        String errorName = "";
        String eci="";
        String billingDiscriptor = "";
        String respRemark = "";
        String machineid = "";
        String respDateTime = null;
        String trackingid = null;
        String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String fromType = "";
        int detailId = 0;
        String isSuccessful = "";
        String token = "";
        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent = req.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName = httpProtocol + "://" + remoteAddr;
        String customerIpAddress = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        commonValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
        String tableName = Database.getTableName(gatewayType.getGateway());

        try
        {
            if (commonValidatorVO == null)
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", ErrorMessages.MANDATORY_INPUTDATA, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                req.setAttribute("error", error);
                HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                req.setAttribute("transDetails", commonValidatorVO);
                session.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            User user = new DefaultUser(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            ESAPI.authenticator().setCurrentUser(user);
            commonValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            commonValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);
            fromType = commonValidatorVO.getTransDetailsVO().getFromtype();
            trackingid = commonValidatorVO.getTrackingid();
            paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
            cardType = Integer.parseInt(commonValidatorVO.getCardType());

            if (commonValidatorVO.getTerminalVO() == null)
            {
                // terminal not valid
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setTerminalVO(commonValidatorVO.getRequestedTerminalVO());
                error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                errorCodeListVO.addListOfError(errorCodeVO);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_CONFIGURATION*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),"0", error, TransReqRejectCheck.MERCHANT_TERMINAL_CONFIGURATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            error = singleCallPaymentDAO.transactionExistCheck(tableName, trackingid, commonValidatorVO);
            if (!functions.isEmptyOrNull(error))
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(error);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseDBViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZDBExceptionEnum.SQL_EXCEPTION, errorCodeListVO, null, null);
            }

            GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if(commonValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport().equalsIgnoreCase("Y"))
                genericTransDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            else
                genericTransDetailsVO.setCurrency(pg.getCurrency());
            commonValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            auditTrailVO.setActionExecutorName("Customer");


            if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType) && CardTypeEnum.CUP_CARDTYPE.ordinal() == cardType)
            {
                detailId = paymentManager.insertAuthStartedTransactionEntryForCup(commonValidatorVO, trackingid, auditTrailVO);
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                {
                    commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                    FraudChecker fraudChecker = new FraudChecker();
                    fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                    if (commonValidatorVO.isFraud())
                    {
                        //Action To Be Taken
                    }
                }
                CupUtils cupUtils=new CupUtils();
                String html = cupUtils.getCupRequest(commonValidatorVO, detailId);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal() == cardType)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                paymentManager.insertAuthStartedTransactionEntryForPaySafeCard(commonValidatorVO, trackingid, auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                PaySafeCardUtils paySafeCardUtils=new PaySafeCardUtils();
                commRequestVO = paySafeCardUtils.getPaySafeRequestVO(commonValidatorVO);
                try
                {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                catch (PZGenericConstraintViolationException gce)
                {
                    mailtransactionStatus = gce.getMessage();
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                if (transRespDetails != null)
                {
                    log.error("paysafecard res-----> " + transRespDetails.getStatus() + "---" + transRespDetails.getDescription() + "---" + transRespDetails.getErrorCode());
                    transactionLogger.error("paysafecard res-----> " + transRespDetails.getStatus() + "---" + transRespDetails.getDescription() + "---" + transRespDetails.getErrorCode());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        String redirectUrl = paysafecard.getString("CUSTOMER_CPANEL") + "mid=" + commRequestVO.getCommMerchantVO().getMerchantId() + "&mtid=" + trackingid + "&amount=" + commonValidatorVO.getTransDetailsVO().getAmount() + "&currency=" + commonValidatorVO.getTransDetailsVO().getCurrency();

                        /*String html=generateAutoSubmitFormForPaySafeCard(redirectUrl);
                        pWriter.println(html);
                        return;*/
                        res.sendRedirect(redirectUrl);
                        return;
                    }
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && (CardTypeEnum.NEOSURF.ordinal() == cardType))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills=new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && (CardTypeEnum.PURPLEPAY.ordinal() == cardType))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType && (CardTypeEnum.AVISA.ordinal() == cardType) || (CardTypeEnum.AMC.ordinal() == cardType))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && (CardTypeEnum.FLUTTER.ordinal() == cardType))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.GIROPAY.ordinal() == cardType)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills=new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && (CardTypeEnum.QIWI.ordinal() == cardType || CardTypeEnum.YANDEX.ordinal() == cardType))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType)
            {
                CommonPaymentProcess paymentProcess=new CommonPaymentProcess();
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                CommRequestVO commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);
                CommResponseVO commResponseVO =(CommResponseVO)pg.processSale(trackingid, commRequestVO);
                if(commResponseVO!=null){
                    if("pending".equalsIgnoreCase(commResponseVO.getStatus()) && functions.isValueNull(commResponseVO.getRedirectUrl()) ){
                        ClearSettleUtills clearSettleUtills=new ClearSettleUtills();
                        String html=clearSettleUtills.getVoucherPurchaseForm(commResponseVO);
                        pWriter.println(html);
                        return;
                    }else{
                        respStatus = "authfailed";
                        paymentProcess.actionEntry(trackingid, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, commRequestVO, auditTrailVO);
                        respDateTime = commResponseVO.getResponseTime();
                        respRemark = commResponseVO.getDescription();
                        respPaymentId = commResponseVO.getTransactionId();
                        singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(),commRequestVO.getAddressDetailsVO().getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,commResponseVO.getRrn(),commResponseVO.getArn(),commResponseVO.getAuthCode(),null);
                    }
                }else{
                    respStatus = "authfailed";
                    paymentProcess.actionEntry(trackingid, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, commRequestVO, auditTrailVO);
                    respDateTime = commResponseVO.getResponseTime();
                    respRemark = commResponseVO.getDescription();
                    respPaymentId = commResponseVO.getTransactionId();
                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), commRequestVO.getAddressDetailsVO().getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,commResponseVO.getRrn(),commResponseVO.getArn(),commResponseVO.getAuthCode(),null);
                }
                mailtransactionStatus = singleCallPaymentDAO.sendTransactionMail(respStatus, trackingid, respRemark, merchantDetailsVO.getEmailSent(), billingDiscriptor);
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.SKRILL.ordinal() == cardType)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                paymentManager.insertSkrillNetellerDetailEntry(commonValidatorVO,"transaction_skrill_details");
                SkrillUtills skrillUtills=new SkrillUtills();
                CommRequestVO commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);
                GenericResponseVO genericResponseVO = pg.processSale(trackingid, commRequestVO);
                CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                String html = SkrillUtills.generateAutoSubmitForm(commResponseVO.getResponseHashInfo(), commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.JETON.ordinal() == cardType)

            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    //ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    //errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(commonValidatorVO.getErrorCodeListVO());
                    errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                SkrillUtills skrillUtills=new SkrillUtills();
                CommRequestVO commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);
                GenericResponseVO genericResponseVO = pg.processSale(trackingid, commRequestVO);
                CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                String html = JetonUtils.generateAutoSubmitForm(commResponseVO.getRedirectUrl(), commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymentType && CardTypeEnum.AllPay88.ordinal()==cardType)
            {
                transactionLogger.debug("----inside--AllPay88----");
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"STDKIT",addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                AllPay88Utils allPay88Utils = new AllPay88Utils();
                commRequestVO = allPay88Utils.getAllPay88RequestVO(commonValidatorVO);


                transRespDetails =(CommResponseVO) pg.processSale(trackingid, commRequestVO);
                String rpStatus="";
                if (transRespDetails != null)
                {
                    log.debug("transRespDetails inside -----------"+transRespDetails);
                    String responseStatus="";
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        rpStatus= "success"+"("+transRespDetails.getResponseHashInfo()+")";
                        responseStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        if (functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDiscriptor = transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDiscriptor = pg.getDisplayName();
                        }
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        rpStatus="Failed";
                        mailtransactionStatus = "Failed";
                        billingDiscriptor = "";
                        responseStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    transactionLogger.debug("---line 636---");
                    paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    transactionLogger.debug("-----line 638----");
                }

                Date date72 = new Date();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                transactionLogger.debug("bilingDescripto in Singlecallpayment after asynchronousMailService initialisation-----"+billingDiscriptor);
                transactionLogger.debug("mailtransactionStatus in Singlecallpayment after asynchronousMailService initialisation-----" + mailtransactionStatus);
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                transactionLogger.debug("SingleCallPayment sendTransactionEventMail end time 72########" + new Date().getTime());
                transactionLogger.debug("SingleCallPayment sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);


                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", rpStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if(PaymentModeEnum.WALLET_PAYMODE.ordinal()==paymentType && CardTypeEnum.NETELLER.ordinal()==cardType)
            {
                InvoiceEntry invoiceEntry = new InvoiceEntry();
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO,"STDKIT",addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                CommRequestVO commRequestVO = null;
                NetellerResponse transRespDetails = null;
                NetellerUtils netellerUtils = new NetellerUtils();
                commRequestVO = netellerUtils.getNetellerRequestVO(commonValidatorVO);

                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }

                if (merchantDetailsVO.getIsService().equalsIgnoreCase("N"))
                {
                    transRespDetails = (NetellerResponse) pg.processAuthentication(trackingid, commRequestVO);
                }
                else
                {
                    transRespDetails = (NetellerResponse) pg.processSale(trackingid, commRequestVO);
                }

                if (transRespDetails != null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        for(Links links : transRespDetails.getLinks())
                        {
                            if(links.getRel().equals("hosted_payment"))
                            {
                                res.sendRedirect(links.getUrl());
                                return;
                            }
                        }
                        mailtransactionStatus = "Pending";
                        if (functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDiscriptor = transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDiscriptor = pg.getDisplayName();
                        }
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        mailtransactionStatus = "Failed";
                        billingDiscriptor = "";
                    }
                }

                Date date72 = new Date();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                transactionLogger.debug("bilingDescripto in Singlecallpayment after asynchronousMailService initialisation-----"+billingDiscriptor);
                transactionLogger.debug("mailtransactionStatus in Singlecallpayment after asynchronousMailService initialisation-----" + mailtransactionStatus);
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                transactionLogger.debug("SingleCallPayment sendTransactionEventMail end time 72########" + new Date().getTime());
                transactionLogger.debug("SingleCallPayment sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);


                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if ((PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType || PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType) && (CardTypeEnum.PERFECTMONEY.ordinal() == cardType ))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                PerfectMoneyUtils perfectMoneyUtils = new PerfectMoneyUtils();
                String html = perfectMoneyUtils.generateAutoSubmitForm(commonValidatorVO);
                transactionLogger.debug("PerfectMoney request form :::::"+html);
                transactionLogger.error("PerfectMoney request form :::::");
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.VOUCHERMONEY.ordinal() == cardType)
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                //String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in SinglecallPayment---"+addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                PaymentManager.insertAuthStartedTransactionEntryForVoucherMoney(commonValidatorVO, trackingid, auditTrailVO);
                CommRequestVO commRequestVO = null;
                VoucherMoneyResponse transRespDetails = null;
                VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
                commRequestVO = voucherMoneyUtils.getVoucherMoneyRequestVO(commonValidatorVO);

                if (merchantDetailsVO.getIsService().equalsIgnoreCase("N"))
                {
                    transRespDetails = (VoucherMoneyResponse) pg.processAuthentication(trackingid, commRequestVO);
                }
                else
                {
                    transRespDetails = (VoucherMoneyResponse) pg.processSale(trackingid, commRequestVO);
                }


                if(transRespDetails != null)
                {
                    transactionLogger.debug("status----"+transRespDetails.getStatus());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        String html=voucherMoneyUtils.generateAutoSubmitForm(transRespDetails.getPaymentFormUrl());
                        pWriter.println(html);
                        return;
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    }
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.TRUSTLY.ordinal() == cardType)
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                //String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in SinglecallPayment---"+addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", commonValidatorVO.getErrorMsg());
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                TrustlyUtils trustlyUtils = new TrustlyUtils();
                commRequestVO = trustlyUtils.getTrustlyRequestVO(commonValidatorVO);

                if (merchantDetailsVO.getIsService().equalsIgnoreCase("N"))
                {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else
                {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }



                if(transRespDetails != null)
                {
                    transactionLogger.debug("status----"+transRespDetails.getStatus());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        String html=trustlyUtils.generateAutoSubmitForm(transRespDetails.getRedirectUrl());
                        pWriter.println(html);
                        return;
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    }
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            else
            {
                if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymentType && CardTypeEnum.JETON_VOUCHER.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    JetonResponseVO jetonResponseVO = null;
                    JetonUtils jetonUtils = new JetonUtils();
                    CommRequestVO commRequestVO = jetonUtils.getJetonRequestVO(commonValidatorVO);
                    pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    jetonResponseVO = (JetonResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (jetonResponseVO != null)
                    {
                        transactionLogger.debug("Status::::::::"+jetonResponseVO.getStatus().trim());
                        if ((jetonResponseVO.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updatePaymentIdForCommon(jetonResponseVO, trackingid);
                            paymentManager.updateTransactionForCommon(jetonResponseVO, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", jetonResponseVO.getTransactionId(), jetonResponseVO.getResponseTime(), jetonResponseVO.getRemark());
                            mailtransactionStatus = jetonResponseVO.getStatus().trim();
                            billingDiscriptor =jetonResponseVO.getDescriptor();
                            transactionLogger.debug("billingDiscriptor:::::"+billingDiscriptor);
                            //res.sendRedirect(transRespDetails.getRedirectUrl());
                            // return;
                        }
                        else
                        {

                            paymentManager.updateTransactionForCommon(jetonResponseVO, "authfailed", trackingid, auditTrailVO, "transaction_common", "", jetonResponseVO.getTransactionId(), jetonResponseVO.getResponseTime(), jetonResponseVO.getRemark());
                            mailtransactionStatus = jetonResponseVO.getStatus().trim();

                            //PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java","doPost()",null,"Transaction",transRespDetails.getRemark(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,null,null);
                        }
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.INPAY_CARDTYPE.ordinal() == cardType)
                {
                    InvoiceEntry invoiceEntry = new InvoiceEntry();
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                    FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                        if (commonValidatorVO.isFraud())
                        {
                            //Action To Be Taken
                        }
                    }
                    InPayUtil inPayUtil=new InPayUtil();
                    String html = inPayUtil.getInPayRequest(commonValidatorVO);
                    pWriter.println(html);

                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    return;
                }
                else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymentType && CardTypeEnum.EPAY.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForEpay(commonValidatorVO, trackingid, auditTrailVO);
                    String html = EpayUtils.getEpayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.BILLDESK.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

                    detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                    BillDeskUtils billDeskUtils = new BillDeskUtils();
                    FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                        if (commonValidatorVO.isFraud())
                        {
                            //Action To Be Taken
                        }
                    }

                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    CommRequestVO commRequestVO = null;
                    Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

                    commRequestVO = billDeskUtils.getBilldeskRequestVO(commonValidatorVO);


                    if (merchantDetailsVO.getIsService().equals("N"))
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    }



                    String form = billDeskUtils.generateAutoSubmitForm(commResponseVO.getUrlFor3DRedirect(), commResponseVO.getPaReq());

                    log.debug("form in singlecall---" + form);
                    pWriter.println(form);
                    return;

                    //transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    //log.debug("response---"+transRespDetails.getStatus());
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.PAYSEC_CARDTYPE.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);

                    PaySecUtils paySecUtils = new PaySecUtils();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = paySecUtils.getCommRequestVO(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    if (transRespDetails != null)
                    {
                        Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                        String html = paySecUtils.generateAutoSubmitForm(response3D);
                        pWriter.println(html);
                        return;
                    }
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.SOFORT_CARDTYPE.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    CommRequestVO commRequestVO = null;
                    SofortResponseVO transRespDetails = null;
                    SofortUtility sofortUtility = new SofortUtility();
                    commRequestVO = sofortUtility.getSofortRequestVO(commonValidatorVO);
                    transRespDetails = (SofortResponseVO) pg.processAuthentication(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            res.sendRedirect(transRespDetails.getPaymentURL());
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {

                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                            //PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java","doPost()",null,"Transaction",transRespDetails.getRemark(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,null,null);
                        }
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if ((PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType) && (CardTypeEnum.SAFEXPAY.ordinal() == cardType ))
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    SafexPayUtils safexPayUtils = new SafexPayUtils();
                    String html = safexPayUtils.generateAutoSubmitForm(commonValidatorVO);
                    transactionLogger.debug("SafexPay request form :::::" + html);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForP4(commonValidatorVO, trackingid, auditTrailVO, false);
                    CommRequestVO commRequestVO = null;
                    P4ResponseVO transRespDetails = null;
                    P4Utils p4Utils = new P4Utils();
                    commRequestVO = p4Utils.getRequestForOnlineBankTransfer(commonValidatorVO);
                    transRespDetails = (P4ResponseVO) pg.processAuthentication(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            res.sendRedirect(transRespDetails.getFormularURL());
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {

                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());//TODO Update query for the P4 as of sofort

                            //PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java","doPost()",null,"Transaction",transRespDetails.getRemark(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,null,null);
                        }

                        Date date72 = new Date();
                        transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.SEPA.ordinal() == paymentType && CardTypeEnum.DIRECT_DEBIT.ordinal() == cardType)
                {
                    TokenManager tokenManager = new TokenManager();

                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (functions.isValueNull(error))
                    {
                        //ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        //errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(commonValidatorVO.getErrorCodeListVO());
                        errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForP4(commonValidatorVO, trackingid, auditTrailVO, true);
                    CommRequestVO commRequestVO = null;
                    P4ResponseVO transRespDetails = null;

                    String mandateId = commonValidatorVO.getCardDetailsVO().getMandateId();

                    P4Utils p4Utils = new P4Utils();
                    commRequestVO = p4Utils.getRequestForSEPATransfer(commonValidatorVO);
                    transRespDetails = (P4ResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, trackingid, auditTrailVO, "transaction_common", machineid, transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                            String mandatePk = "";

                            if (!functions.isValueNull(mandateId))
                            {
                                mandatePk = tokenManager.insertMandateForSEPA(commRequestVO.getCardDetailsVO().getMandateId(), toid, trackingid, transRespDetails.getMandateURL(), transRespDetails.getRevokeMandateURL(), transRespDetails.isRecurring());
                            }
                            else
                            {
                                mandatePk = tokenManager.gatewayMandateIdFromMandateToken(mandateId);
                            }

                            if (functions.isValueNull(mandatePk))
                                tokenManager.insertSEPATransactionHistory(mandatePk, trackingid);

                            mailtransactionStatus = "Pending";
                            commonValidatorVO.getCardDetailsVO().setMandateId(commRequestVO.getCardDetailsVO().getMandateId());

                        }
                        else
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, PZTransactionStatus.AUTH_FAILED.toString(), trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            log.debug("SEPA Billing Descripor In SingleCallPayment----" + transRespDetails.getDescriptor());
                            transactionLogger.debug("SEPA Billing Descripor In SingleCallPayment----" + transRespDetails.getDescriptor());
                        }

                        Date date72 = new Date();
                        transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);

                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymentType && CardTypeEnum.IDEAL_CARDTYPE.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    CommRequestVO commRequestVO = null;
                    SofortResponseVO transRespDetails = null;
                    SofortUtility sofortUtility = new SofortUtility();
                    commRequestVO = sofortUtility.getIdealRequestVO(commonValidatorVO);
                    transRespDetails = (SofortResponseVO) pg.processAuthentication(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            //paymentManager.updatePaymentIdForSofort(transRespDetails.getTransactionId(), trackingid);
                            res.sendRedirect(transRespDetails.getPaymentURL());
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {

                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                            //PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java","doPost()",null,"Transaction",transRespDetails.getRemark(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,null,null);
                        }
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.ACH.ordinal() == paymentType && CardTypeEnum.ACH.ordinal() == cardType)
                {
                    AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    //commonValidatorVO = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                    if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", commonValidatorVO.getErrorMsg());
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }


                    if (!functions.isEmptyOrNull(error))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;
                    PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                    commonValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.ACH.ordinal()));
                    commRequestVO = payMitcoUtility.getPayMitcoRequestVO(commonValidatorVO);

                    if ("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                            if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                            {
                                mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                            }
                            else
                            {
                                mailtransactionStatus = "Successful";
                            }
                            if (functions.isValueNull(transRespDetails.getDescriptor()))
                            {
                                billingDiscriptor = transRespDetails.getDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = pg.getDisplayName();
                            }
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            paymentManager.updateTransactionForPayMitco(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                            log.debug("ACH BillingDescriptor in SingleCallPayment-----" + transRespDetails.getDescriptor());
                            transactionLogger.debug("ACH BillingDescriptor in SingleCallPayment-----" + transRespDetails.getDescriptor());
                            if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                            {
                                mailtransactionStatus = "Failed (" + transRespDetails.getDescription() + ")";
                            }
                            else
                            {
                                mailtransactionStatus = "Failed";
                            }
                            billingDiscriptor = "";
                        }
                    }

                    Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.CHK.ordinal() == paymentType && CardTypeEnum.CHK.ordinal() == cardType)
                {
                    AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    if (!functions.isEmptyOrNull(error))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;
                    PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                    commonValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.CHK.ordinal()));
                    commRequestVO = payMitcoUtility.getPayMitcoRequestVO(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                            if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                            {
                                mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                            }
                            else
                            {
                                mailtransactionStatus = "Successful";
                            }
                            if (functions.isValueNull(transRespDetails.getDescriptor()))
                            {
                                billingDiscriptor = transRespDetails.getDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = pg.getDisplayName();
                            }
                            billingDiscriptor = "";
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            paymentManager.updateTransactionForPayMitco(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                            log.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                            transactionLogger.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                            if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                            {
                                mailtransactionStatus = "Failed (" + transRespDetails.getDescription() + ")";
                            }
                            else
                            {
                                mailtransactionStatus = "Failed";
                            }
                        }
                    }
                    Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                }
                else if (PaymentModeEnum.SEPA.ordinal() == paymentType && CardTypeEnum.SEPA_EXPRESS.ordinal() == cardType)
                {
                    String mailtransactionStatusB4 = "";

                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                    if (functions.isValueNull(error))
                    {
                        //ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        //errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(commonValidatorVO.getErrorCodeListVO());
                        errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        errorCodeListVO.addListOfError(commonValidatorVO.getErrorCodeListVO());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForB4(commonValidatorVO, trackingid, auditTrailVO);
                    CommRequestVO commRequestVO = null;
                    TransactionResponse transRespDetails = null;

                    B4Utils b4Utils = new B4Utils();
                    commRequestVO = b4Utils.getRequestForB4SEPATransfer(commonValidatorVO);
                    pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    transRespDetails = (TransactionResponse) pg.processSale(trackingid, commRequestVO);
                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            billingDiscriptor = transRespDetails.getDescriptor();
                            paymentManager.updateTransactionForCommon(transRespDetails, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = "Success";
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("authfailed"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, PZTransactionStatus.AUTH_FAILED.toString(), trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = "Failed";
                        }
                    }

                    if (commonValidatorVO.getMerchantDetailsVO().getEmailSent().equalsIgnoreCase("Y"))
                    {
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatusB4, null, billingDiscriptor);
                    }
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatusB4, null, billingDiscriptor);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.MULTIBANCO.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                    String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.ASTROPAY.ordinal() == cardType)
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                    String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.UNICREDIT.ordinal() == cardType)
                {
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    UnicreditUtils unicreditUtils = new UnicreditUtils();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = unicreditUtils.getUnicreditRequestVO(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    Comm3DResponseVO response3D = null;

                    if (transRespDetails != null)
                    {
                        response3D = (Comm3DResponseVO) transRespDetails;
                        transactionLogger.error("URL Unicredit---"+response3D.getUrlFor3DRedirect());
                        transactionLogger.error("Messgae Unicredit---"+response3D.getPaReq());
                        String html = unicreditUtils.generateAutoSubmitForm(response3D);
                        transactionLogger.error("HTML Unicredit---"+html);
                        pWriter.println(html);
                    }
                    return;
                }
                else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType && CardTypeEnum.CLEARSETTLE.ordinal() == cardType)
                {
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    ClearSettleUtills clearSettleUtills = new ClearSettleUtills();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = clearSettleUtills.getClearSettleHPPRequest(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    Comm3DResponseVO response3D = null;

                    if (transRespDetails != null)
                    {
                        response3D = (Comm3DResponseVO) transRespDetails;
                        String html = clearSettleUtills.generateAutoSubmitFormNew(response3D);
                        pWriter.println(html);
                    }
                    return;
                }
                else if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardType || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardType || CardTypeEnum.JCB.ordinal() == cardType || CardTypeEnum.RUPAY.ordinal() == cardType || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardType || CardTypeEnum.MAESTRO.ordinal() == cardType || CardTypeEnum.INSTAPAYMENT.ordinal() == cardType))
                {
                    //validation Check
                    if (ReitumuBankSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || DectaSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || ClearSettleHPPGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                    {
                        AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                        error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                        if (!functions.isEmptyOrNull(error))
                        {
                            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                            errorCodeVO.setErrorReason(error);
                            errorCodeListVO.addListOfError(errorCodeVO);

                            PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            req.setAttribute("transDetails", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        //paymentManager.insertAuthStartedTransactionEntryForReitumu3D(commonValidatorVO, trackingid, auditTrailVO, tableName);
                    }
                    else
                    {
                        log.debug("key map 123 Singlecall----" + session.getAttribute("paymentMap"));
                        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        commonValidatorVO = commonInputValidator.performStandardKitStep2Validations(commonValidatorVO);

                        if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
                        {
                            /*ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                            errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                            errorCodeListVO.addListOfError(errorCodeVO);*/
                            try
                            {
                                //errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                                paymentManager.updateDetailsTablewithErrorName(ErrorName.VALIDATION_INVALID_INPUT.toString(),commonValidatorVO.getTrackingid());
                            }
                            catch (PZDBViolationException d)
                            {
                                log.error("----PZDBViolationException in update with error name-----", d);
                            }
                            PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, commonValidatorVO.getErrorCodeListVO(), null, null, toid, PZOperations.STANDARDKIT_SALE);
                            req.setAttribute("error", commonValidatorVO.getErrorMsg());
                            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            req.setAttribute("transDetails", commonValidatorVO);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        try
                        {
                            log.debug("card type in singlecallpayment----" + cardType);
                            // checksum verification
                            //transactionHelper.checksumVerificationForSTDKit(commonValidatorVO);
                            transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);
                            if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
                            {

                            }
                        }
                        catch (PZConstraintViolationException cve)
                        {
                            PZExceptionHandler.handleCVEException(cve, toid, PZOperations.STANDARDKIT_SALE);
                            error = errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());

                            errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                            try
                            {
                                paymentManager.updateDetailsTablewithErrorName(errorName,commonValidatorVO.getTrackingid() );
                            }
                            catch (PZDBViolationException d)
                            {
                                log.error("----PZDBViolationException in update with error name-----", d);
                            }

                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            req.setAttribute("transDetails", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        catch (PZDBViolationException dbe)
                        {
                            PZExceptionHandler.handleDBCVEException(dbe, toid, PZOperations.STANDARDKIT_SALE);
                            error = "Internal Error Occurred : Contact Customer Support for more detail's";
                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            req.setAttribute("transDetails", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }

                    /*paymentManager.insertAuthStartedTransactionEntryForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName);*/
                        if ("Y".equalsIgnoreCase(account.getIsRecurring()))
                        {
                            recurringBillingVO = new RecurringBillingVO();
                            if (commonValidatorVO.getRecurringBillingVO() != null)
                            {


                                recurringBillingVO.setInterval(commonValidatorVO.getRecurringBillingVO().getInterval());
                                recurringBillingVO.setFrequency(commonValidatorVO.getRecurringBillingVO().getFrequency());
                                recurringBillingVO.setRunDate(commonValidatorVO.getRecurringBillingVO().getRunDate());
                            }
                            recurringBillingVO.setOriginTrackingId(trackingid);
                            recurringBillingVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                            recurringBillingVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                            recurringBillingVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                            recurringBillingVO.setTerminalid(commonValidatorVO.getTerminalId());
                            if ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()))
                            {
                                recurringBillingVO.setRecurringType("Manual");
                                recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                            }
                            else if ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()))
                            {
                                recurringBillingVO.setRecurringType("Automatic");
                                recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                            }
                            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
                        }
                    }

                    merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
                    GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                    genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

                    if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromType))
                    {

                        //create VO
                        QwipiResponseVO transRespDetails = null;
                        QwipiRequestVO RequestDetail = getQRequestVO(commonValidatorVO);

                        /*if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
                        {
                            String firstSix = "";
                            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                            {
                                firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
                            }

                            BinResponseVO binResponseVO = new BinResponseVO();
                            binResponseVO = functions.getBinDetails(firstSix, commonValidatorVO.getMerchantDetailsVO().getCountry());
                            commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                            commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                            commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                            commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                            commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                            commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
                        }*/

                        paymentManager.insertAuthStartedTransactionEntryForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName);

                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                        {
                            FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                            FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(merchantDetailsVO.getMemberId());
                            if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                            {
                                commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                                FraudChecker fraudChecker = new FraudChecker();
                                fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                                if (commonValidatorVO.isFraud())
                                {
                                    respStatus = "failed";
                                    mailtransactionStatus = "failed";
                                    String remark = "High Risk Transaction Detected";
                                    CommResponseVO commResponseVO = new CommResponseVO();
                                    commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,commResponseVO.getRrn(),commResponseVO.getArn(),commResponseVO.getAuthCode(),null);
                                    if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                                    {
                                        req.setAttribute("transDetail", commonValidatorVO);
                                        req.setAttribute("responceStatus", respStatus + "(" + ActionEntry.ACTION_FRAUD_VALIDATION_FAILED + ")");
                                        req.setAttribute("displayName", billingDiscriptor);
                                        req.setAttribute("ctoken", ctoken);
                                        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                                        //TODO:Add sms service code
                                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                                        requestDispatcher.forward(req, res);
                                        return;
                                    }
                                    else
                                    {
                                        transactionUtility.newDoAutoRedirect(commonValidatorVO.getTransDetailsVO().getRedirectUrl(), ctoken, res, mailtransactionStatus, billingDiscriptor);
                                    }
                                }
                            }
                        }

                        transRespDetails = (QwipiResponseVO) pg.processSale(trackingid, RequestDetail);

                        if (transRespDetails != null)
                        {
                            respDateTime = transRespDetails.getDateTime();
                            respRemark = transRespDetails.getRemark();
                            respPaymentId = transRespDetails.getPaymentOrderNo();
                            if ((transRespDetails.getResultCode().trim()).equals("0"))
                            {
                                isSuccessful = "Y";
                                respStatus = "capturesuccess";
                                paymentManager.updateCaptureSuccessTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                                if (respRemark != null && !respRemark.equals(""))
                                {
                                    mailtransactionStatus = "Successful (" + respRemark + ")";
                                }
                                else
                                {
                                    mailtransactionStatus = "Successful";
                                }
                            }
                            else
                            {
                                respStatus = "authfailed";
                                paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                                if (respRemark != null && !respRemark.equals(""))
                                {
                                    mailtransactionStatus = "Failed (" + respRemark + ")";
                                }
                                else
                                {
                                    mailtransactionStatus = "Failed";
                                }
                            }

                            if (!(transRespDetails.getBillingDescriptor().equals("")))
                            {
                                billingDiscriptor = transRespDetails.getBillingDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = account.getDisplayName();
                            }
                            String md5check = null;
                            log.error("isKSN---> " + RequestDetail.getKsnUrlFlag());
                            transactionLogger.error("isKSN---> " + RequestDetail.getKsnUrlFlag());

                            if ("Y".equals(merchantDetailsVO.getIsTokenizationAllowed()) && "Y".equals(isSuccessful))
                            {
                                TerminalManager terminalManager = new TerminalManager();
                                if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                                {
                                    TokenManager tokenManager = new TokenManager();
                                    String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                                    if (functions.isValueNull(strToken))
                                    {
                                        token = strToken;
                                        commonValidatorVO.setToken(token);
                                    }
                                    else
                                    {
                                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                        TokenResponseVO tokenResponseVO = null;
                                        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                        commCardDetailsVO.setCardType(String.valueOf(cardType));
                                        commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                        commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                        commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());

                                        tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                        tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                        tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());

                                        tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                        tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                        tokenRequestVO.setMerchantDetailsVO(merchantDetailsVO);
                                        tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                                        tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                        if ("success".equals(tokenResponseVO.getStatus()))
                                        {
                                            token = tokenResponseVO.getToken();
                                            commonValidatorVO.setToken(token);
                                        }
                                    }
                                }
                            }

                            md5check = Functions.convertmd5(transRespDetails.getMerNo() + transRespDetails.getBillNo() + transRespDetails.getCurrency() + transRespDetails.getAmount() + transRespDetails.getResultCode() + transRespDetails.getDateTime() + RequestDetail.getMd5Key());
                            log.error(transRespDetails.getMd5Info() + "  <---->  " + md5check);
                            transactionLogger.error(transRespDetails.getMd5Info() + "  <---->  " + md5check);
                            if (!transRespDetails.getMd5Info().equals(md5check.toUpperCase()))
                            {
                                String message = "ERROR!!! We have encountered an internal error while processing your request. Your MD5Info is invalid<BR><BR>";
                                //sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_FAILED_TRANSACTION_NOTIFICATION,trackingid,respStatus,message);
                            /*if("Y".equalsIgnoreCase(merchantDetailsVO.getEmailSent()))
                            {*/
                                Date date72 = new Date();
                                transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                                //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                                transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                                transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                            /*}*/
                            }
                            //singleCallPaymentDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,trackingid);
                        }
                        String statusMsg = getTransactionstatus(respStatus, respRemark);
                        String display = "";
                        transactionLogger.debug("statusMsg:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + statusMsg);

                        if(statusMsg.contains("Declined")|| statusMsg.contains("Failed")|| statusMsg.contains("fail")){
                               display ="style=\"display:none;\"";
                        }
                        transactionLogger.debug("display:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + display);

                        HashMap mailData = new HashMap();
                        mailData.put(MailPlaceHolder.TOID, commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        mailData.put(MailPlaceHolder.CustomerEmail, commonValidatorVO.getAddressDetailsVO().getEmail());
                        mailData.put(MailPlaceHolder.TRACKINGID, trackingid);
                        mailData.put(MailPlaceHolder.IPADDRESS, commonValidatorVO.getAddressDetailsVO().getIp());
                        mailData.put(MailPlaceHolder.ORDERDESCRIPTION, commonValidatorVO.getTransDetailsVO().getOrderDesc());
                        mailData.put(MailPlaceHolder.DESC, commonValidatorVO.getTransDetailsVO().getOrderId());
                        mailData.put(MailPlaceHolder.AMOUNT, commonValidatorVO.getTransDetailsVO().getAmount());
                        mailData.put(MailPlaceHolder.CURRENCY, commonValidatorVO.getTransDetailsVO().getCurrency());
                        mailData.put(MailPlaceHolder.DISPLAYNAME, transRespDetails.getBillingDescriptor());
                        mailData.put(MailPlaceHolder.STATUS, statusMsg);
                        mailData.put(MailPlaceHolder.PARTNERID, commonValidatorVO.getTransDetailsVO().getTotype());
                        mailData.put(MailPlaceHolder.NAME, commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                        mailData.put(MailPlaceHolder.DISPLAYTR, display);
                        MailService mailService = new MailService();
                        mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, mailData);
                        //TODO:Add sms service code
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                        //mailtransactionStatus = singleCallPaymentDAO.sendTransactionMail(respStatus,trackingid,respRemark);
                    }
                    else if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromType))
                    {
                        EcoreRequestVO RequestDetail = null;
                        RequestDetail = getEcrRequestVO(commonValidatorVO);

                        /*if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
                        {
                            String firstSix = "";
                            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                            {
                                firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
                            }

                            BinResponseVO binResponseVO = new BinResponseVO();
                            binResponseVO = functions.getBinDetails(firstSix, commonValidatorVO.getMerchantDetailsVO().getCountry());
                            commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                            commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                            commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                            commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                            commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                            commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
                        }*/

                        paymentManager.insertAuthStartedTransactionEntryForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName);
                        EcoreResponseVO transRespDetails = new EcoreResponseVO();

                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                        {
                            FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                            FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(merchantDetailsVO.getMemberId());
                            if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                            {
                                commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                                FraudChecker fraudChecker = new FraudChecker();
                                fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                                if (commonValidatorVO.isFraud())
                                {
                                    respStatus = "failed";
                                    mailtransactionStatus = "failed";
                                    String remark = "High Risk Transaction Detected";
                                    CommResponseVO commResponseVO = new CommResponseVO();
                                    commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,"","","",null);
                                    if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                                    {
                                        req.setAttribute("transDetail", commonValidatorVO);
                                        req.setAttribute("responceStatus", respStatus + "(" + ActionEntry.ACTION_FRAUD_VALIDATION_FAILED + ")");
                                        req.setAttribute("displayName", billingDiscriptor);
                                        req.setAttribute("ctoken", ctoken);
                                        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                                        //TODO:Add sms service code
                                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);

                                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                                        requestDispatcher.forward(req, res);
                                        return;
                                    }
                                    else
                                    {
                                        transactionUtility.newDoAutoRedirect(commonValidatorVO.getTransDetailsVO().getRedirectUrl(), ctoken, res, mailtransactionStatus, billingDiscriptor);
                                    }
                                }
                            }
                        }


                        String status = "";
                        pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                        if (merchantDetailsVO.getService().equalsIgnoreCase("N"))
                        {
                            transRespDetails = (EcoreResponseVO) pg.processAuthentication(trackingid, RequestDetail);
                            status = "authsuccessful";
                        }
                        else
                        {
                            transRespDetails = (EcoreResponseVO) pg.processSale(trackingid, RequestDetail);
                            status = "capturesuccess";
                        }

                        if (transRespDetails != null)
                        {
                            respDateTime = transRespDetails.getProcessingTime();
                            respRemark = transRespDetails.getDescription();
                            respPaymentId = transRespDetails.getTransactionID();
                            if ((transRespDetails.getResponseCode().trim()).equals("100"))
                            {
                                isSuccessful = "Y";
                                if (status.equals("capturesuccess"))
                                {
                                    respStatus = "capturesuccess";
                                    paymentManager.updateCaptureSuccessTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                                    mailtransactionStatus = "successful";
                                }
                                else
                                {
                                    respStatus = "authsuccessful";
                                    log.debug("7    " + commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    transactionLogger.debug("7    " + commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    paymentManager.updateAuthSuccessTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                                    mailtransactionStatus = "successful";
                                }
                                billingDiscriptor = pg.getDisplayName();
                            }
                            else
                            {

                                respStatus = "authfailed";
                                paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                            }
                            //singleCallPaymentDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,trackingid);
                        }
                        if ("Y".equals(isSuccessful) && "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                        {
                            TerminalManager terminalManager = new TerminalManager();
                            if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                            {
                                TokenManager tokenManager = new TokenManager();
                                String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                                if (functions.isValueNull(strToken))
                                {
                                    token = strToken;
                                    commonValidatorVO.setToken(token);
                                }
                                else
                                {
                                    TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                    TokenResponseVO tokenResponseVO = null;
                                    CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                    commCardDetailsVO.setCardType(String.valueOf(cardType));
                                    commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                    commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                    commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                    commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());

                                    tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                    tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                    tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                    tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                    tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                    tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                    if ("success".equals(tokenResponseVO.getStatus()))
                                    {
                                        token = tokenResponseVO.getToken();
                                        log.debug("Token::" + token);
                                    }
                                }
                            }
                        }

                        //mailtransactionStatus = singleCallPaymentDAO.sendTransactionMail(respStatus,trackingid,respRemark);
                    /*if("Y".equalsIgnoreCase(merchantDetailsVO.getEmailSent()))
                    {*/
                        Date date72 = new Date();
                        transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                        //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    /*}*/
                    }
                    else if (Functions.checkCommonProcessGateways(fromType))
                    {
                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                        InvoiceEntry invoiceEntry = new InvoiceEntry();
                        CommRequestVO commRequestVO = null;
                        CommResponseVO transRespDetails = null;

                        log.debug("is recurring from gateway account----" + account.getIsRecurring());

/*
                        if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
                        {
                            String firstSix = "";
                            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                            {
                                firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
                            }

                            BinResponseVO binResponseVO = new BinResponseVO();
                            binResponseVO = functions.getBinDetails(firstSix,commonValidatorVO.getMerchantDetailsVO().getCountry());
                            commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                            commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                            commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                            commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                            commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                            commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                            commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
                            commonValidatorVO.getCardDetailsVO().setCountryName(binResponseVO.getCountryname());
                            commonValidatorVO.getCardDetailsVO().setIssuingBank(binResponseVO.getBank());

                        }
*/

                        commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);

                        paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);

                        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        }

                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                        {
                            FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                            FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(merchantDetailsVO.getMemberId());
                            if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                            {
                                commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                                FraudChecker fraudChecker = new FraudChecker();
                                fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                                if (commonValidatorVO.isFraud())
                                {
                                    respStatus = "failed";
                                    mailtransactionStatus = "failed";
                                    String remark = "High Risk Transaction Detected";
                                    CommResponseVO commResponseVO = new CommResponseVO();
                                    commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,"","","",null);
                                    if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                                    {
                                        req.setAttribute("transDetail", commonValidatorVO);
                                        req.setAttribute("responceStatus", respStatus + "(" + ActionEntry.ACTION_FRAUD_VALIDATION_FAILED + ")");
                                        req.setAttribute("displayName", billingDiscriptor);
                                        req.setAttribute("ctoken", ctoken);
                                        //SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                        AsynchronousMailService mailService = new AsynchronousMailService();
                                        mailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                                        //TODO:Add sms service code
                                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
                                        requestDispatcher.forward(req, res);
                                        return;
                                    }
                                    else
                                    {
                                        transactionUtility.newDoAutoRedirect(commonValidatorVO.getTransDetailsVO().getRedirectUrl(), ctoken, res, mailtransactionStatus, billingDiscriptor);
                                    }
                                }
                            }
                        }


                        String status = "";
                        if (merchantDetailsVO.getService().equalsIgnoreCase("N"))
                        {
                            Date date3 = new Date();
                            transactionLogger.debug("SingCallPayment processAuthentication start #########" + date3.getTime());
                            transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                            transactionLogger.debug("SingCallPayment processAuthentication end #########" + new Date().getTime());
                            transactionLogger.debug("SingCallPayment processAuthentication diff #########" + (new Date().getTime() - date3.getTime()));

                            status = "authsuccessful";
                        }
                        else
                        {
                            Date date3 = new Date();
                            transactionLogger.debug("SingCallPayment processSale start #########" + date3.getTime());
                            transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                            transactionLogger.debug("SingCallPayment processSale end #########" + new Date().getTime());
                            transactionLogger.debug("SingCallPayment processSale diff #########" + (new Date().getTime() - date3.getTime()));
                            status = "capturesuccess";

                        }

                        if (transRespDetails != null)
                        {


                            if (functions.isValueNull(transRespDetails.getStatus()) && (transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                            {
                                isSuccessful = "Y";
                                if (status.equals("capturesuccess"))
                                {
                                    respStatus = "capturesuccess";
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO);
                                }
                                else
                                {
                                    respStatus = "authsuccessful";
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO);
                                }
                                if (functions.isValueNull(transRespDetails.getDescriptor()))
                                {
                                    billingDiscriptor = transRespDetails.getDescriptor();
                                }
                                else
                                {
                                    billingDiscriptor = pg.getDisplayName();
                                }
                            }
                            else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                            {

                                Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                                if(!functions.isValueNull(transRespDetails.getThreeDVersion()))
                                    transRespDetails.setThreeDVersion("3Dv1");
                                paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                                String form = "";
                                if (fromType.equalsIgnoreCase("pbs") || fromType.equalsIgnoreCase("billdesk") || fromType.equalsIgnoreCase("safexpay"))
                                {
                                    form = paymentProcess.get3DConfirmationForm(commonValidatorVO, response3D);
                                }
                                else
                                {
                                    transactionLogger.debug("inside else line 2337:::::");
                                    form = paymentProcess.get3DConfirmationForm(trackingid, ctoken, response3D);
                                    transactionLogger.debug("inside else line 2339:::::");
                                }
                                //log.debug("form in singlecall---" + form);
                                transactionLogger.error("form in singlecall---" + form);
                                if ("Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                                {
                                    TerminalManager terminalManager = new TerminalManager();
                                    if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                                    {
                                        TokenManager tokenManager = new TokenManager();
                                        String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                                        if (functions.isValueNull(strToken))
                                        {
                                            token = strToken;
                                            commonValidatorVO.setToken(token);
                                        }
                                        else
                                        {
                                            TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                            TokenResponseVO tokenResponseVO = null;
                                            CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                            commCardDetailsVO.setCardType(String.valueOf(cardType));
                                            commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                            commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                            commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                            commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
                                            tokenRequestVO.setIsActive("N");
                                            tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                            tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());

                                            tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                            tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());

                                            tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());

                                            tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                            tokenRequestVO.setTokenId(tokenResponseVO.getTokenId());
                                            tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                            if ("success".equals(tokenResponseVO.getStatus()))
                                            {
                                                token = tokenResponseVO.getToken();
                                                commonValidatorVO.setToken(token);
                                            }
                                        }
                                    }
                                }
                                pWriter.println(form);
                                return;
                            }
                            else if (functions.isValueNull(transRespDetails.getStatus()) && (transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                            {
                                if (fromType.equalsIgnoreCase("aldrapay") || fromType.equalsIgnoreCase("purplepay") || fromType.equalsIgnoreCase("ravedirect") || fromType.equalsIgnoreCase("fastpay") )
                                {
                                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                                    String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO, transRespDetails.getResponseHashInfo(), fromType);
                                    pWriter.println(html);
                                    return;
                                }
                                else
                                {
                                    respStatus = "authstarted";
                                    if (functions.isValueNull(transRespDetails.getDescriptor()))
                                    {
                                        billingDiscriptor = transRespDetails.getDescriptor();
                                    }
                                    else
                                    {
                                        billingDiscriptor = pg.getDisplayName();
                                    }
                                }

                            }
                            else
                            {
                                respStatus = "authfailed";
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO, auditTrailVO);
                            }
                            respDateTime = transRespDetails.getResponseTime();
                            respRemark = transRespDetails.getDescription();
                            respPaymentId = transRespDetails.getTransactionId();
                            if (functions.isValueNull(transRespDetails.getErrorName()))
                                errorName = transRespDetails.getErrorName();
                            if(functions.isValueNull(transRespDetails.getEci())){
                                eci=transRespDetails.getEci();
                                commonValidatorVO.setEci(eci);
                            }
                            singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                        }
                        if ("Y".equals(isSuccessful) && "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                        {
                            TerminalManager terminalManager = new TerminalManager();
                            if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                            {
                                TokenManager tokenManager = new TokenManager();
                                String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                                if (functions.isValueNull(strToken))
                                {
                                    token = strToken;
                                    commonValidatorVO.setToken(token);
                                }
                                else
                                {
                                    TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                    TokenResponseVO tokenResponseVO = null;
                                    CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                    commCardDetailsVO.setCardType(String.valueOf(cardType));
                                    commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                    commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                    commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                    commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
                                    tokenRequestVO.setIsActive("Y");
                                    tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                    tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                    tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                    tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                    tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());

                                    tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                    tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                    tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                    tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());

                                    tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                    tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                    tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                    tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());

                                    tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                    tokenRequestVO.setTokenId(tokenResponseVO.getTokenId());
                                    tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                    if ("success".equals(tokenResponseVO.getStatus()))
                                    {
                                        token = tokenResponseVO.getToken();
                                        commonValidatorVO.setToken(token);
                                    }
                                }
                            }
                        }
                        log.debug("status---" + respStatus);
                        mailtransactionStatus = singleCallPaymentDAO.sendTransactionMail(respStatus, trackingid, respRemark, merchantDetailsVO.getEmailSent(), billingDiscriptor);
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                    }

                    else
                    {
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMODE_CARDTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, null, null);
                    }


                }
                else
                {
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMODE_CARDTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, null, null);
                }
            }
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolation Exception in SingleCallPayment---", e);
            transactionLogger.error("PZTechnicalViolation Exception in SingleCallPayment---", e);
            String remarkEnum = e.getPzTechnicalConstraint().getPzTechEnum().toString();
            if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SERVICE_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.AXISFAULT.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.HTTP_EXCEPTION.toString()))
            {

                try
                {

                    paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                    paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                    error = "Internal Errror Occured : Please contact Customer support for further help";
                    PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                mailtransactionStatus = "Bank Connectivity Issue while processing your request.";
            }
            else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
            {
                //no status has to be chaged for this since the transaction has not been requested to bank
                mailtransactionStatus = "Your transaction is Timed out.Please check with support team.";
                try
                {

                    paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                    error = "Internal Errror Occured : Please contact Customer support for further help";
                    PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
            }
            else
            {
                /*try
                {
                    paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("----PZDBViolationException in SingleCallPayment----",dbe);
                    transactionLogger.error("----PZDBViolationException in SingleCallPayment----",dbe);
                    error = "Internal Errror Occured : Please contact Customer support for further help";
                    PZExceptionHandler.handleDBCVEException(dbe,commonValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }*/
                mailtransactionStatus = "We have encountered an internal error while processing your request.";
                try
                {

                    paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                }
                catch (PZDBViolationException dbe)
                {
                    log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                    error = "Internal Errror Occured : Please contact Customer support for further help";
                    PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

            }
            PZExceptionHandler.handleTechicalCVEException(e, toid, PZOperations.STANDARDKIT_SALE);


            req.setAttribute("error", mailtransactionStatus);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("----PZConstraintViolationException in SingleCallPayment----", cve);
            if (cve.getPzConstraint().getErrorCodeListVO() != null)
            {
                error = errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());
            }
            else
            {
                error = cve.getPzConstraint().getMessage();
            }
            req.setAttribute("error", error);
            HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
            req.setAttribute("errorName", errorName);
            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
            req.setAttribute("transDetails", commonValidatorVO);
            session.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/commonPayment.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;

            /*PZExceptionHandler.handleCVEException(cve,commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetails",commonValidatorVO);
            session.setAttribute("ctoken",ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken="+ctoken);
            requestDispatcher.forward(req, res);
            return;*/
        }
        catch (PZDBViolationException dbe)
        {
            log.error("----PZDBViolationException in SingleCallPayment----", dbe);
            error = "Internal Errror Occured : Please contact Customer support for further help";
            try
            {

                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
            }
            catch (PZDBViolationException dbee)
            {
                log.error("----PZDBViolationException in SingleCallPayment----", dbee);
                error = "Internal Errror Occured : Please contact Customer support for further help";
                PZExceptionHandler.handleDBCVEException(dbee, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                req.setAttribute("error", error);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;

        }
        catch (PZGenericConstraintViolationException gce)
        {
            log.error("PZGenericConstraintViolationException in SingleCallPayment---", gce);
            mailtransactionStatus = gce.getMessage();

            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
            }
            catch (PZDBViolationException dbe)
            {
                log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                error = "Internal Errror Occured : Please contact Customer support for further help";
                PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                req.setAttribute("error", error);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            req.setAttribute("transDetail", commonValidatorVO);
            req.setAttribute("errorName", errorName);
            req.setAttribute("responceStatus", mailtransactionStatus);
            req.setAttribute("displayName", billingDiscriptor);
            session.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;

        }
        catch (SystemError se)
        {
            log.error("System Error while processing transaction", se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", "System Error Exception:::", null, se.getMessage(), se.getCause(), toid, PZOperations.STANDARDKIT_SALE);

            mailtransactionStatus = "We have encountered an internal error while processing your request";
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                paymentManager.updateFailedTransactionEntryForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, "", "", "Bank Connection issue");
            }
            catch (PZDBViolationException e)
            {
                log.error("error while enter fail transaction", e);
                PZExceptionHandler.handleDBCVEException(e, toid, PZOperations.STANDARDKIT_SALE);
            }
            //send Admin Mail
            sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_FAILED_TRANSACTION_NOTIFICATION, trackingid, respStatus, se.getStackTrace().toString(), billingDiscriptor);

            req.setAttribute("transDetail", commonValidatorVO);
            req.setAttribute("errorName", errorName);
            req.setAttribute("responceStatus", mailtransactionStatus);
            req.setAttribute("displayName", "");
            req.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationpage.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Exception ", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", "No Such Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), toid, PZOperations.STANDARDKIT_SALE);
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid() );
                paymentManager.updateFailedTransactionEntryForQwipiEcoreCommon(commonValidatorVO, trackingid, auditTrailVO, tableName, machineid, "", "", "Bank Connection issue");
            }
            catch (PZDBViolationException i)
            {
                log.error("NosuchAlgorithm", i);
                PZExceptionHandler.handleDBCVEException(i, toid, PZOperations.STANDARDKIT_SALE);
            }
        }

        //auto redirect check
        if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
        {
            req.setAttribute("transDetail", commonValidatorVO);
            req.setAttribute("errorName", errorName);
            req.setAttribute("responceStatus", mailtransactionStatus);
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
                transactionUtility.doAutoRedirect(commonValidatorVO, res, mailtransactionStatus, billingDiscriptor);
            }
            catch (SystemError systemError)
            {
                log.error("System Error while redirecting to redirect url", systemError);
            }
        }
    }

    private QwipiRequestVO getQRequestVO(CommonValidatorVO commonValidatorVO)
    {
        QwipiRequestVO RequestDetail = null;
        GenericCardDetailsVO cardDetail = commonValidatorVO.getCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail = new QwipiAddressDetailsVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        Transaction transaction = new Transaction();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String tredtime = dateFormat.format(calendar.getTime());
        String MD5key = null;
        String isKsnUrlFlag = null;
        Hashtable midHash = null;

        String MD5INFO = null;
        try
        {
            midHash = transaction.getMidKeyForQwipi(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            if (!midHash.isEmpty())
            {
                MD5key = (String) midHash.get("midkey");
                isKsnUrlFlag = (String) midHash.get("isksnurlflag");
            }
            log.error("isKSN---> " + isKsnUrlFlag);
            log.error(commonValidatorVO.getTransDetailsVO().getFromid() + "-" + commonValidatorVO.getTransDetailsVO().getOrderId() + "-" + commonValidatorVO.getTransDetailsVO().getCurrency() + "-" + commonValidatorVO.getTransDetailsVO().getAmount() + "-" + tredtime + "-" + MD5key);

            transactionLogger.error("isKSN---> " + isKsnUrlFlag);
            transactionLogger.error(commonValidatorVO.getTransDetailsVO().getFromid() + "-" + commonValidatorVO.getTransDetailsVO().getOrderId() + "-" + commonValidatorVO.getTransDetailsVO().getCurrency() + "-" + commonValidatorVO.getTransDetailsVO().getAmount() + "-" + tredtime + "-" + MD5key);
            MD5INFO = Functions.convertmd5(commonValidatorVO.getTransDetailsVO().getFromid().trim() + commonValidatorVO.getTransDetailsVO().getOrderId().trim() + commonValidatorVO.getTransDetailsVO().getCurrency().trim() + commonValidatorVO.getTransDetailsVO().getAmount().trim() + tredtime.trim() + MD5key.trim());

            cardDetail.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
            cardDetail.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
            cardDetail.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
            cardDetail.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear().substring(2));

            AddressDetail.setMd5key(MD5key);
            AddressDetail.setTime(tredtime);
            AddressDetail.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
            AddressDetail.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
            AddressDetail.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());
            AddressDetail.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
            AddressDetail.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
            AddressDetail.setProducts(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            AddressDetail.setState(commonValidatorVO.getAddressDetailsVO().getState());
            AddressDetail.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
            AddressDetail.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
            AddressDetail.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
            AddressDetail.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
            AddressDetail.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
            AddressDetail.setMd5info(MD5INFO);
            AddressDetail.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
            AddressDetail.setSsn(commonValidatorVO.getAddressDetailsVO().getSsn());

            TransDetail.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            TransDetail.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            TransDetail.setOrderId(commonValidatorVO.getTrackingid());
            TransDetail.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
            TransDetail.setMerNo(commonValidatorVO.getTransDetailsVO().getFromid());

            RequestDetail = new QwipiRequestVO(cardDetail, AddressDetail, TransDetail);
            RequestDetail.setKsnUrlFlag(isKsnUrlFlag);
            RequestDetail.setMd5Key(MD5key);
            RequestDetail.setMiddleName("");
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("ERROR", e);
            transactionLogger.error("ERROR", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallPayment", "getQRequestVO()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, PZOperations.STANDARDKIT_SALE);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("UnsupportedEncodingException", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallPayment", "getQRequestVO()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause(), null, PZOperations.STANDARDKIT_SALE);
            transactionLogger.error("UnsupportedEncodingException", e);
        }
        return RequestDetail;
    }

    private EcoreRequestVO getEcrRequestVO(CommonValidatorVO commonValidatorVO)
    {
        EcoreRequestVO RequestDetail = null;
        GenericCardDetailsVO cardDetail = commonValidatorVO.getCardDetailsVO();
        EcoreAddressDetailsVO AddressDetail = new EcoreAddressDetailsVO();
        EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String tredtime = dateFormat.format(calendar.getTime());

        String MD5INFO = null;
        Transaction transaction = new Transaction();
        Hashtable mdHash = transaction.getMidKeyForEcore(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String MD5key = "";
        if (!mdHash.isEmpty())
        {
            MD5key = (String) mdHash.get("midkey");
        }
        try
        {
            MD5INFO = Functions.convertmd5(commonValidatorVO.getTransDetailsVO().getFromid() + commonValidatorVO.getTransDetailsVO().getOrderId() + commonValidatorVO.getTransDetailsVO().getCurrency() + commonValidatorVO.getTransDetailsVO().getAmount() + tredtime + MD5key);

            AddressDetail.setMd5key(MD5key);
            AddressDetail.setTime(tredtime);
            AddressDetail.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
            AddressDetail.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
            AddressDetail.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());
            AddressDetail.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
            AddressDetail.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
            AddressDetail.setProducts(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            AddressDetail.setState(commonValidatorVO.getAddressDetailsVO().getState());
            AddressDetail.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
            AddressDetail.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
            AddressDetail.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
            AddressDetail.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
            AddressDetail.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
            AddressDetail.setMd5info(MD5INFO);
            AddressDetail.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
            AddressDetail.setSsn(commonValidatorVO.getAddressDetailsVO().getSsn());

            TransDetail.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            TransDetail.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            TransDetail.setOrderId(commonValidatorVO.getTrackingid());
            TransDetail.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
            TransDetail.setMerNo(commonValidatorVO.getTransDetailsVO().getFromid());

            RequestDetail = new EcoreRequestVO(cardDetail, AddressDetail, TransDetail);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("ERROR", e);
            transactionLogger.error("ERROR", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallPayment", "getEcrRequestVO()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, PZOperations.STANDARDKIT_SALE);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("UnsupportedEncodingException", e);
            transactionLogger.error("UnsupportedEncodingException", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallPayment", "getEcrRequestVO()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause(), null, PZOperations.STANDARDKIT_SALE);
        }
        return RequestDetail;
    }

    private CommRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO,String fromType) throws PZDBViolationException
    {
        Functions functions = new Functions();
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        RecurringBillingVO recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        CommDeviceDetailsVO commDeviceDetailsVO=new CommDeviceDetailsVO();

        if (null==recurringBillingVO)
        {
            recurringBillingVO = new RecurringBillingVO();
            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        }

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String alias = account.getAliasName();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        String displayname = account.getDisplayName();
        /*String isAutomaticRecurring = commonValidatorVO.getTerminalVO().getIsRecurring();*/
        //String isManualRecurring = commonValidatorVO.getTerminalVO().getIsManualRecurring();
        String recurringType = "";
        String consentStmnt="";



        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCountry_code_A2())){
            cardDetailsVO.setCountry_code_A2((commonValidatorVO.getCardDetailsVO().getCountry_code_A2()));
        }

        if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
        {
            cardDetailsVO.setCardType(Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
        }
        else
        {
            cardDetailsVO.setCardType("");
        }
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTelnocc()))
            addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount()))
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
            addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setMerchantOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        merchantAccountVO.setZipCode(commonValidatorVO.getMerchantDetailsVO().getZip());
        merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
        merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());


        String contactPerson = commonValidatorVO.getMerchantDetailsVO().getContact_persons().trim();
        String firstName = "";
        String lastName = "";

        if (functions.isValueNull(contactPerson) && contactPerson.contains(" "))
        {
            String[] person = contactPerson.split(" ");
            firstName = person[0];
            lastName = person[1];
        }
        else
        {
            firstName = contactPerson;
            lastName = contactPerson;
        }
        transactionLogger.error("First Name in request---"+firstName);
        transactionLogger.error("Last Name in request---"+lastName);
        transactionLogger.error("Zip in request---"+commonValidatorVO.getMerchantDetailsVO().getZip());
        merchantAccountVO.setFirstName(firstName);
        merchantAccountVO.setLastName(lastName);
        merchantAccountVO.setIsService(commonValidatorVO.getMerchantDetailsVO().getService());
        String merchantTelNo = commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber();
        String mPhone = "";

        if (functions.isValueNull(merchantTelNo) && merchantTelNo.contains("-"))
        {
            String[] phone = merchantTelNo.split("-");
            mPhone = phone[1];
        }
        else
        {
            mPhone = merchantTelNo;
        }

        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if (functions.isValueNull(mPhone))
            merchantAccountVO.setPartnerSupportContactNumber(mPhone);

        ReserveField2VO reserveField2VO = null;

        /*if(commonValidatorVO.getReserveField2VO()!=null)
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
        }*/

        reserveField2VO = new ReserveField2VO();
        reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
        reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
        reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());

        recurringBillingVO.setIsAutomaticRecurring(commonValidatorVO.getTerminalVO().getIsRecurring());
        recurringBillingVO.setIsManualRecurring(commonValidatorVO.getTerminalVO().getIsManualRecurring());

        if ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()))
        {
            recurringType = "Manual";
        }
        else if ("N".equalsIgnoreCase(commonValidatorVO.getRecurringBillingVO().getIsAutomaticRecurring()))
        {
            recurringType = "Automatic";
        }
        recurringBillingVO.setRecurringType(recurringType);
        if(commonValidatorVO.getDeviceDetailsVO() != null)
        {
            commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
            commDeviceDetailsVO.setAcceptHeader(commonValidatorVO.getDeviceDetailsVO().getAcceptHeader());
            commDeviceDetailsVO.setFingerprints(commonValidatorVO.getDeviceDetailsVO().getFingerprints());
        }
        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        if("PFS".equalsIgnoreCase(fromType))
        {
            transDetailsVO.setResponseHashInfo(UUID.randomUUID().toString());
        }
        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            commRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);
        commRequestVO.setReserveField2VO(reserveField2VO);
        commRequestVO.setIsPSTProcessingRequest(commonValidatorVO.getIsPSTProcessingRequest());
        commRequestVO.setReject3DCard(commonValidatorVO.getReject3DCard());
        commRequestVO.setCurrencyConversion(commonValidatorVO.getCurrencyConversion());
        commRequestVO.setConversionCurrency(commonValidatorVO.getConversionCurrency());
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);
        if (functions.isValueNull(commonValidatorVO.getAttemptThreeD()))
            commRequestVO.setAttemptThreeD(commonValidatorVO.getAttemptThreeD());

        if (functions.isValueNull(commonValidatorVO.getConsentStmnt())){
            //System.out.println("consent3----"+commonValidatorVO.getConsentStmnt());

            consentStmnt=commonValidatorVO.getConsentStmnt();
            //System.out.println("consent3----"+consentStmnt);
            commRequestVO.setConsentStmnt(consentStmnt);
        }


        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()),Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        paymentProcess.setTransactionVOExtension(commRequestVO,commonValidatorVO);

        return commRequestVO;
    }


    public String getTransactionstatus(String status,String remark)
    {
        String mailtransactionStatus="Failed";
        if(status.equalsIgnoreCase("capturesuccess") || status.equalsIgnoreCase("authsuccessful"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus="Successful ("+remark+")";
            }
            else
            {
                mailtransactionStatus="Successful";
            }
        }
        else if(status.equalsIgnoreCase("authstarted"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = remark;
            }
        }
        else if(status.equalsIgnoreCase("authfailed"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = mailtransactionStatus +" ("+remark+")";
            }
        }
        return mailtransactionStatus;
    }


}
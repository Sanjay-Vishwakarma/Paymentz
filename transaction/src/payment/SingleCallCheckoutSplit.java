package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
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
import com.payment.PayMitco.core.PayMitcoPaymentProcess;
import com.payment.PayMitco.core.PayMitcoResponseVO;
import com.payment.PayMitco.core.PayMitcoUtility;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.allPay88.AllPay88ResponseVO;
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
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.skrill.SkrillUtills;
import com.payment.sms.AsynchronousSmsService;
import com.payment.sofort.SofortUtility;
import com.payment.sofort.VO.SofortResponseVO;
import com.payment.tojika.TojikaUtils;
import com.payment.trustly.TrustlyUtils;
import com.payment.unicredit.UnicreditUtils;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.vouchermoney.VoucherMoneyResponse;
import com.payment.vouchermoney.VoucherMoneyUtils;
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
import java.security.NoSuchAlgorithmException;
import java.util.*;

//import com.payment.paysec.PaySecUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 8/26/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallCheckoutSplit extends HttpServlet
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CupServlet");
    final static ResourceBundle paysafecard = LoadProperties.getProperty("com.directi.pg.paysafecard");
    final static ResourceBundle apcopay = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
    private static Logger log = new Logger(SingleCallCheckout.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallCheckoutSplit.class.getName());
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
        log.debug("Inside single call split::");
        //check Authenticate request and session
        PrintWriter pWriter = res.getWriter();
        String error = "";
        HttpSession session = req.getSession();
        String ctoken = (String) session.getAttribute("ctoken");
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        //CommonValidatorVO commonValidatorVO = (CommonValidatorVO) session.getAttribute("commonValidator");
        CommonValidatorVO commonValidatorVO = null;

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        commonValidatorVO = ReadRequest.getSpecificRequestParametersForSale1(req);
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();



        String paymentBrand = "";
        String paymentMode = "";

        SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        AbstractPaymentGateway pg = null;
        SingleCallPaymentDAO singleCallPaymentDAO = new SingleCallPaymentDAO();
        ActionEntry entry = new ActionEntry();
        Functions functions = new Functions();
        PaymentManager paymentManager = new PaymentManager();
        TransactionHelper transactionHelper = new TransactionHelper();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDetailsVO parentMerchantDetailsVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        RecurringManager recurringManager = new RecurringManager();
        TerminalManager terminalManager=new TerminalManager();
        RecurringBillingVO recurringBillingVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        int paymentType = 0;
        int cardType = 0;
        String respStatus = null;
        String respPaymentId = null;
        String mailtransactionStatus = "Failed";
        String transactionStatus="";
        String errorName = "";
        String eci="";
        String billingDiscriptor = "";
        String respRemark = "";
        String machineid = "";
        String respDateTime = null;
        String trackingid = null;
        String trackingids="";
        String splitTrans="";
        String currency = "";
        String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String fromType = "";
        String fromID = "";
        int detailId = 0;
        String isSuccessful = "";
        String token = "";
        String remoteAddr = Functions.getIpAddress(req);
        String orderid="";
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent = req.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName = httpProtocol + "://" + remoteAddr;
        String customerIpAddress = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
        String tableName = "";
        HashMap terminalMap = (HashMap)session.getAttribute("terminalmap");
        LinkedHashMap<String,TerminalVO> terminalMapLimitRouting=(LinkedHashMap<String,TerminalVO>)session.getAttribute("terminalMapLimitRouting");
        List<MarketPlaceVO> mpDetailsList=(List<MarketPlaceVO>)session.getAttribute("mpDetailsList");
        HashMap terminalHash = new HashMap();
        paymentBrand = commonValidatorVO.getPaymentBrand();
        paymentMode = commonValidatorVO.getPaymentMode();
        int j=0;

        try
        {
            parentMerchantDetailsVO=merchantDAO.getMemberDetails(toid);
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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            User user = new DefaultUser(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            ESAPI.authenticator().setCurrentUser(user);
            commonValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            commonValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);
            if(mpDetailsList == null)
            {
                String[] childTrackingid=req.getParameterValues("marketPlaceTrackingid[]");
                if(childTrackingid != null)
                {
                    mpDetailsList=new ArrayList<>();
                    for (int i = 0; i < childTrackingid.length; i++)
                    {
                        transactionLogger.error("Child Trackingid------>"+childTrackingid[i]);
                        marketPlaceVO=paymentManager.getMarketPlaceDetailsByTrackingid(childTrackingid[i]);
                        mpDetailsList.add(marketPlaceVO);
                    }
                }
            }
            if(mpDetailsList != null)
            {
                for (int i = 0; i < mpDetailsList.size(); i++)
                {
                    marketPlaceVO=(MarketPlaceVO)mpDetailsList.get(i);
                    commonValidatorVO.getMerchantDetailsVO().setMemberId(marketPlaceVO.getMemberid());
                    String memberid=marketPlaceVO.getMemberid();
                    commonValidatorVO.getTransDetailsVO().setAmount(marketPlaceVO.getAmount());
                    commonValidatorVO.getTransDetailsVO().setOrderId(marketPlaceVO.getOrderid());
                    commonValidatorVO.getTransDetailsVO().setOrderDesc(marketPlaceVO.getOrderDesc());
                    commonValidatorVO.setTrackingid(marketPlaceVO.getTrackingid());
                    paymentManager.getMerchantandTransactionDetails(commonValidatorVO);
                    currency = commonValidatorVO.getTransDetailsVO().getCurrency();
                    trackingid = commonValidatorVO.getTrackingid();
                    if(i==0)
                    {
                        trackingids += trackingid;
                        orderid+=marketPlaceVO.getOrderid();
                    }
                    else
                    {
                        trackingids += "," + trackingid;
                        orderid+=","+marketPlaceVO.getOrderid();
                    }
                    transactionLogger.error("Action called---" + commonValidatorVO.getActionType());
                    transactionLogger.error("Action called MID---" + commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    transactionLogger.error("Action called Redirect URL---" + commonValidatorVO.getTransDetailsVO().getRedirectUrl());
                    if (functions.isValueNull(commonValidatorVO.getActionType()) && commonValidatorVO.getActionType().equalsIgnoreCase("cancel"))
                    {
                        String notificationUrl = commonValidatorVO.getTransDetailsVO().getNotificationUrl();
                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification for cancel transaction---" + notificationUrl + "--- for trackingid---" + trackingid);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, "cancelled", "Cancel by Customer", "");
                        }
                        paymentManager.updateCancelTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                        mailtransactionStatus="Cancel";
                        billingDiscriptor="";
                        if(j==0)
                            splitTrans += trackingid + "-" + memberid+"-"+"C";
                        else
                            splitTrans+=","+ trackingid + "-" + memberid+"-"+"C";
                        j++;
                        continue;
                    }
                    TerminalVO terminalVO = null;
                    if("Y".equalsIgnoreCase(parentMerchantDetailsVO.getMultiCurrencySupport()))
                        terminalHash=terminalManager.getMultipleCurrencyPaymdeCardTerminalVO(memberid, currency,"ALL",commonValidatorVO.getAccountId());
                    else
                        terminalHash=terminalManager.getPaymdeCardTerminalVO(memberid, currency,commonValidatorVO.getAccountId());
                    if(terminalHash != null && terminalHash.size()>0)
                    {
                        transactionLogger.error("terminalHash----->" + terminalHash);
                        terminalVO=(TerminalVO)terminalHash.get(paymentMode + "-" + paymentBrand + "-" + currency);
                    }
                    if(terminalVO==null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error = "Terminal ID provided by you is not valid.";
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    else
                    {
                        paymentType = Integer.parseInt(terminalVO.getPaymodeId());
                        cardType = Integer.parseInt(terminalVO.getCardTypeId());
                        //transactionLogger.debug("paymode enum------------------ line 366-------" + String.valueOf(PaymentModeEnum.OneRoad.getValue()));
                        transactionLogger.error("Terminal id---" + terminalVO.getTerminalId());
                        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                        commonValidatorVO.setCardType(String.valueOf(cardType));
                        commonValidatorVO.setPaymentType(String.valueOf(paymentType));
                        commonValidatorVO.getMerchantDetailsVO().setAccountId(terminalVO.getAccountId());
                        commonValidatorVO.setTerminalVO(terminalVO);
                    }
                    commonValidatorVO.getMerchantDetailsVO().setAccountId(terminalVO.getAccountId());
                    commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());

                    fromType = terminalVO.getGateway();
                    fromID = terminalVO.getMemberId();
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
                    tableName = Database.getTableName(gatewayType.getGateway());

                    commonValidatorVO.getTransDetailsVO().setFromtype(fromType);
                    commonValidatorVO.getTransDetailsVO().setFromid(fromID);

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

                    if (commonValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport().equalsIgnoreCase("Y"))
                        genericTransDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                    else
                        genericTransDetailsVO.setCurrency(pg.getCurrency());
                    commonValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
                    auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    auditTrailVO.setActionExecutorName("Customer");

                    if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType || String.valueOf(PaymentModeEnum.CREDIT_CARD_INDIA.getValue()).equals(terminalVO.getPaymodeId()) || String.valueOf(PaymentModeEnum.DEBIT_CARD_INDIA.getValue()).equals(terminalVO.getPaymodeId())) &&
                                (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardType || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardType || CardTypeEnum.JCB.ordinal() == cardType || CardTypeEnum.RUPAY.ordinal() == cardType || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardType || CardTypeEnum.MAESTRO.ordinal() == cardType || CardTypeEnum.INSTAPAYMENT.ordinal() == cardType))
                    {
                        //validation Check
                        log.debug("key map 123 Singlecall----" + session.getAttribute("paymentMap"));
                        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        commonValidatorVO = commonInputValidator.performStandardKitStep2Validations(commonValidatorVO);
                        if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
                        {
                            PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, commonValidatorVO.getErrorCodeListVO(), null, null);

                        }
                        try
                        {
                            log.debug("card type in singlecallpayment----" + cardType);
                            transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);
                        }
                        catch (PZConstraintViolationException cve)
                        {
                            PZExceptionHandler.handleCVEException(cve, toid, PZOperations.STANDARDKIT_SALE);
                            error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
                            log.error("----PZConstraintViolationException for SystemChecksStep2-----", cve);

                            errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                            try
                            {
                                paymentManager.updateDetailsTablewithErrorName(errorName, commonValidatorVO.getTrackingid());
                            }
                            catch (PZDBViolationException d)
                            {
                                log.error("----PZDBViolationException in update with error name-----", d);
                            }

                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = new HashMap();

                            if (session.getAttribute("paymentMap") != null)
                                paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                            if (paymentTypeMap == null || paymentTypeMap.isEmpty())
                            {
                                transactionLogger.error("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                                List<String> cardList = new ArrayList<String>();
                                cardList.add(commonValidatorVO.getCardType());
                                paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                                PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

                                transactionLogger.error("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                                partnerDetailsVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                partnerDetailsVO.setPartnertemplate(commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                                commonValidatorVO.setVersion("2");
                                commonInputValidator.setAllTemplateInformationRelatedToMerchant(commonValidatorVO.getMerchantDetailsVO(), commonValidatorVO.getVersion());
                                commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
                                commonInputValidator.getPaymentPageTemplateDetails(commonValidatorVO, session);

                                session.setAttribute("merchantLogoName", commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
                            }
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            commonValidatorVO.setTerminalMap(terminalMap);
                            commonValidatorVO.setTerminalMapLimitRouting(terminalMapLimitRouting);
                            req.setAttribute("transDetails", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        catch (PZDBViolationException dbe)
                        {
                            PZExceptionHandler.handleDBCVEException(dbe, toid, PZOperations.STANDARDKIT_SALE);
                            error = "Internal Error Occurred : Contact Customer Support for more detail's";
                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = new HashMap();
                            if (session.getAttribute("paymentMap") != null)
                                paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                            if (paymentTypeMap == null || paymentTypeMap.isEmpty())
                            {
                                transactionLogger.error("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                                List<String> cardList = new ArrayList<String>();
                                cardList.add(commonValidatorVO.getCardType());
                                paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                                PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

                                transactionLogger.error("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                                partnerDetailsVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                partnerDetailsVO.setPartnertemplate(commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                                commonValidatorVO.setVersion("2");
                                commonInputValidator.setAllTemplateInformationRelatedToMerchant(commonValidatorVO.getMerchantDetailsVO(), commonValidatorVO.getVersion());
                                commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
                                commonInputValidator.getPaymentPageTemplateDetails(commonValidatorVO, session);

                                session.setAttribute("merchantLogoName", commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
                            }
                            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                            req.setAttribute("transDetails", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        if (RomCardPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                        {
                            String html = pg.processAutoRedirect(commonValidatorVO);
                            pWriter.println(html);
                            return;
                        }
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
                            String cardNumber = commonValidatorVO.getCardDetailsVO().getCardNum();
                            recurringBillingVO.setFirstSix(cardNumber.substring(0, 6));
                            recurringBillingVO.setLastFour(cardNumber.substring((cardNumber.length() - 4), cardNumber.length()));
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

                        merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
                        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                        genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

                        if (Functions.checkCommonProcessGateways(fromType))
                        {
                            transactionLogger.debug("fromtype------" + fromType);
                            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                            InvoiceEntry invoiceEntry = new InvoiceEntry();
                            CommRequestVO commRequestVO = null;
                            CommResponseVO transRespDetails = null;

                            log.debug("is recurring from gateway account----" + account.getIsRecurring());

                            commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                            paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);

                            if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                            {
                                invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                            }

                            transactionLogger.error("Internal Fraud Check---" + merchantDetailsVO.getInternalFraudCheck() + "-MemberID---" + merchantDetailsVO.getMemberId() + "---VipCard---" + commonValidatorVO.isVIPCard() + "-VIPEmail---" + commonValidatorVO.isVIPEmail());
                            if ("Y".equalsIgnoreCase(parentMerchantDetailsVO.getInternalFraudCheck()) && !(commonValidatorVO.isVIPCard() || commonValidatorVO.isVIPEmail()))
                            {
                                FraudChecker fraudChecker = new FraudChecker();
                                fraudChecker.checkInternalFraudRules(commonValidatorVO);

                                transactionLogger.error("Is Fraud Transaction---" + commonValidatorVO.isFraud());
                                if (commonValidatorVO.isFraud())
                                {
                                    respStatus = "failed";
                                    mailtransactionStatus = "failed";
                                    String remark = "High Risk Transaction Detected";
                                    CommResponseVO commResponseVO = new CommResponseVO();
                                    commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci,"","","",null);
                                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                    {
                                        transactionLogger.error("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                        commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                        asyncNotificationService.sendNotification(transactionDetailsVO1, commonValidatorVO.getTrackingid(), respStatus, respRemark, "");
                                    }
                                    transactionLogger.error("Fraud Error---" + commonValidatorVO.getErrorCodeListVO().getListOfError().get(0).getErrorCode());
                                    req.setAttribute("error", commonValidatorVO.getErrorCodeListVO().getListOfError().get(0).getApiCode());
                                    req.setAttribute("transDetails", commonValidatorVO);
                                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                                    requestDispatcher.forward(req, res);
                                    return;
                            /*if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                            {
                                req.setAttribute("transDetail", commonValidatorVO);
                                req.setAttribute("responceStatus", respStatus + "(" + ActionEntry.ACTION_FRAUD_VALIDATION_FAILED + ")");
                                req.setAttribute("displayName", billingDiscriptor);
                                req.setAttribute("ctoken", ctoken);
                                //SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                AsynchronousMailService mailService = new AsynchronousMailService();
                                mailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                                //TODO:Add sms service code
                                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                                requestDispatcher.forward(req, res);
                                return;
                            }
                            else
                            {
                                //Sending Merchant Notification
                                transactionUtility.doAutoRedirect(commonValidatorVO, res, mailtransactionStatus, billingDiscriptor);
                                return;
                            }*/
                                }
                            }

                            if ("Y".equalsIgnoreCase(parentMerchantDetailsVO.getOnlineFraudCheck()))
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
                                        singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci,"","","",null);
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
                                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                                            requestDispatcher.forward(req, res);
                                            return;
                                        }
                                        else
                                        {
                                            transactionUtility.doAutoRedirect(commonValidatorVO, res, mailtransactionStatus, billingDiscriptor);
                                            return;
                                        }
                                    }
                                }
                            }


                            String status = "";
                            if (parentMerchantDetailsVO.getService().equalsIgnoreCase("N"))
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

                            transactionLogger.debug("transRespDetails------" + transRespDetails);
                            if (transRespDetails != null)
                            {
                                transactionLogger.debug("status------" + transRespDetails.getStatus());
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
                                    if(j==0)
                                        splitTrans += trackingid + "-" + memberid+"-"+"Y";
                                    else
                                        splitTrans+=","+ trackingid + "-" + memberid+"-"+"Y";
                                    j++;
                                }
                                else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                                {
                                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                                    error = "Transaction not permitted.";
                                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                                    errorCodeListVO.addListOfError(errorCodeVO);
                                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TRANSACTION_NOT_PERMITTED.toString(), ErrorType.SYSCHECK.toString());
                                    PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);

                                    Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                                    if(!functions.isValueNull(transRespDetails.getThreeDVersion()))
                                        transRespDetails.setThreeDVersion("3Dv1");
                                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                                    String form = "";
                                    if (fromType.equalsIgnoreCase("pbs") || fromType.equalsIgnoreCase("billdesk"))
                                    {
                                        form = paymentProcess.get3DConfirmationForm(commonValidatorVO, response3D);
                                    }
                                    else
                                    {
                                        form = paymentProcess.get3DConfirmationForm(trackingid, ctoken, response3D);
                                    }
                                    log.debug("form in singlecall---" + form);
                                    pWriter.println(form);
                                    if(j==0)
                                        splitTrans += trackingid + "-" + memberid+"-"+"N";
                                    else
                                        splitTrans+=","+ trackingid + "-" + memberid+"-"+"N";
                                    j++;
                                    continue;
                                }
                                else if (functions.isValueNull(transRespDetails.getStatus()) && (transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                                {
                                    if (fromType.equalsIgnoreCase("aldrapay") || fromType.equalsIgnoreCase("purplepay"))
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
                                    if(j==0)
                                        splitTrans += trackingid + "-" + memberid+"-"+"P";
                                    else
                                        splitTrans+=","+ trackingid + "-" + memberid+"-"+"P";
                                    j++;

                                }
                                else
                                {
                                    respStatus = "authfailed";
                                    if(j==0)
                                        splitTrans += trackingid + "-" + memberid+"-"+"N";
                                    else
                                        splitTrans+=","+ trackingid + "-" + memberid+"-"+"N";
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO, auditTrailVO);
                                    j++;
                                }
                                respDateTime = transRespDetails.getResponseTime();
                                respRemark = transRespDetails.getDescription();
                                respPaymentId = transRespDetails.getTransactionId();
                                if (functions.isValueNull(transRespDetails.getErrorName()))
                                    errorName = transRespDetails.getErrorName();
                                if (functions.isValueNull(transRespDetails.getEci()))
                                {
                                    eci = transRespDetails.getEci();
                                    commonValidatorVO.setEci(eci);
                                }
                                singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci,transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                            }
                            if ("Y".equals(isSuccessful) && "Y".equals(parentMerchantDetailsVO.getIsTokenizationAllowed()))
                            {
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
                                        tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());

                                        tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                        tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                        tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                        tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                        tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

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
                            if(i==0)
                            transactionStatus+=mailtransactionStatus;
                            else
                                transactionStatus+=","+mailtransactionStatus;

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
                commonValidatorVO.setTrackingid(trackingids);
                commonValidatorVO.setFailedSplitTransactions(splitTrans);
                commonValidatorVO.getTransDetailsVO().setOrderId(orderid);
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
                    //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
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
                    //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
            }
            else
            {
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
                    //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

            }
            PZExceptionHandler.handleTechicalCVEException(e, toid, PZOperations.STANDARDKIT_SALE);


            req.setAttribute("error", mailtransactionStatus);
            //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("----PZConstraintViolationException in SingleCallPayment----", cve);
            if (cve.getPzConstraint().getErrorCodeListVO() != null)
            {
                error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
            }
            else
            {
                error = cve.getPzConstraint().getMessage();
            }
            req.setAttribute("error", error);
            HashMap paymentTypeMap = new HashMap();
            if(session.getAttribute("paymentMap")!=null)
                paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

            if(paymentTypeMap==null || paymentTypeMap.isEmpty())
            {
                transactionLogger.error("ConsException PT CT---"+commonValidatorVO.getPaymentType()+"--"+commonValidatorVO.getCardType());
                List<String> cardList = new ArrayList<String>();
                cardList.add(commonValidatorVO.getCardType());
                paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

                transactionLogger.error("template details---"+commonValidatorVO.getMerchantDetailsVO().getPartnerId()+"---"+commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                partnerDetailsVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                partnerDetailsVO.setPartnertemplate(commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                commonValidatorVO.setVersion("2");
                try
                {
                    commonInputValidator.setAllTemplateInformationRelatedToMerchant(commonValidatorVO.getMerchantDetailsVO(), commonValidatorVO.getVersion());
                    commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
                    commonInputValidator.getPaymentPageTemplateDetails(commonValidatorVO, session);

                }
                catch (PZDBViolationException dbe)
                {
                    log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                    error = "Internal Errror Occured : Please contact Customer support for further help";
                    PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                session.setAttribute("merchantLogoName", commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            req.setAttribute("errorName", errorName);
            commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
            commonValidatorVO.setTerminalMap(terminalMap);
            commonValidatorVO.setTerminalMapLimitRouting(terminalMapLimitRouting);
            req.setAttribute("transDetails", commonValidatorVO);
            session.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
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
                //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
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
                //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            req.setAttribute("transDetail", commonValidatorVO);
            req.setAttribute("errorName", errorName);
            req.setAttribute("responceStatus", mailtransactionStatus);
            req.setAttribute("displayName", billingDiscriptor);
            session.setAttribute("ctoken", ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
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
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        try
        {
            if(merchantDetailsVO==null)
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            transactionLogger.error("commonValidatorVO.getTransDetailsVO().getNotificationUrl()-->" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            transactionLogger.error("TransactionNotification flag for ---" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + merchantDetailsVO.getTransactionNotification());
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()) && ("Non-3D".equals(merchantDetailsVO.getTransactionNotification()) || "Both".equals(merchantDetailsVO.getTransactionNotification())))
            {
                transactionLogger.error("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, commonValidatorVO.getTrackingid(), respStatus, respRemark, "");
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException--"+commonValidatorVO.getTrackingid()+"-->",e);
        }
        //auto redirect check
        if (parentMerchantDetailsVO.getAutoRedirect().equals("N"))
        {
            req.setAttribute("transDetail", commonValidatorVO);
            req.setAttribute("errorName", errorName);
            req.setAttribute("responceStatus", mailtransactionStatus);
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
                transactionUtility.doAutoRedirect(commonValidatorVO, res, mailtransactionStatus, billingDiscriptor);
            }
            catch (SystemError systemError)
            {
                log.error("System Error while redirecting to redirect url", systemError);
            }
        }
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


        cardDetailsVO.setName_on_card(commonValidatorVO.getCardDetailsVO().getCardHolderName());
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCountry_code_A2())){
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
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount()))
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
            addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());


        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setMerchantOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setEmiCount(commonValidatorVO.getTransDetailsVO().getEmiCount());

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

        if ("PFS".equalsIgnoreCase(fromType))
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

        if (functions.isValueNull(commonValidatorVO.getConsentStmnt()))
        {
            consentStmnt=commonValidatorVO.getConsentStmnt();
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
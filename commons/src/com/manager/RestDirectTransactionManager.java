package com.manager;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.SwiffpayPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.fraud.FraudChecker;
import com.google.gson.Gson;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PaymentDAO;
import com.manager.helper.TransactionHelper;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.payment.AbstractPaymentProcess;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.LetzPay.LetzPayUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.TWDTaiwan.TWDTaiwanUtils;
import com.payment.alphapay.AlphapayPaymentGateway;
import com.payment.alphapay.AlphapayPaymentProcess;
import com.payment.alphapay.AlphapayUtils;
import com.payment.bitclear.BitClearPaymentProcess;
import com.payment.bitclear.BitClearUtils;
import com.payment.bnmquick.BnmQuickUtils;
import com.payment.checkers.PaymentChecker;
import com.payment.clearsettle.ClearSettleUtills;
import com.payment.common.core.*;
import com.payment.cupUPI.*;
import com.payment.doku.DokuPaymentProcess;
import com.payment.doku.DokuUtils;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.giftpay.GiftPayPaymentProcess;
import com.payment.giftpay.GiftPayUtils;
import com.payment.jpbanktransfer.JPBankPaymentProcess;
import com.payment.jpbanktransfer.JPBankTransferUtils;
import com.payment.neteller.NetellerUtils;
import com.payment.neteller.response.Links;
import com.payment.neteller.response.NetellerResponse;
import com.payment.p4.gateway.P4ResponseVO;
import com.payment.p4.gateway.P4Utils;
import com.payment.payg.PayGUtils;
import com.payment.paysend.PaySendUtils;
import com.payment.paytm.PayTMPaymentProcess;
import com.payment.paytm.PayTMUtils;
import com.payment.plmp.PLMPUtils;
import com.payment.quickpayments.QuickPaymentsPaymentProcess;
import com.payment.quickpayments.QuickPaymentsUtils;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.*;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.sofort.IdealPaymentGateway;
import com.payment.sofort.SofortUtility;
import com.payment.statussync.StatusSyncDAO;
import com.payment.swiffy.SwiffyPaymentProcess;
import com.payment.swiffy.SwiffyUtils;
import com.payment.transfr.TransfrPaymentProcess;
import com.payment.transfr.TransfrUtils;
import com.payment.trustly.TrustlyUtils;
import com.payment.unicredit.UnicreditUtils;
import com.payment.utils.TransactionUtilsDAO;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.payment.vouchermoney.VoucherMoneyResponse;
import com.payment.vouchermoney.VoucherMoneyUtils;
import com.sofort.lib.products.response.IDealBanksResponse;
import com.sofort.lib.products.response.parts.IDealBank;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationAccountsException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sneha on 3/30/2016.
 */
public class RestDirectTransactionManager
{
    final static ResourceBundle rb          = LoadProperties.getProperty("com.directi.pg.PaysecServlet");
    final static ResourceBundle RB1         = LoadProperties.getProperty("com.directi.pg.InPayServlet");
    final static ResourceBundle RB2         = LoadProperties.getProperty("com.directi.pg.AsyncRedirectionUrl");
    final static ResourceBundle RB3         = LoadProperties.getProperty("com.directi.pg.PfsServlet");
    final static ResourceBundle paysafecard = LoadProperties.getProperty("com.directi.pg.paysafecard");
    final static ResourceBundle SEPBANKS    = LoadProperties.getProperty("com.directi.pg.SEPBANKS");
    final static ResourceBundle SEPWALLETS  = LoadProperties.getProperty("com.directi.pg.SEPWALLETS");
    final static ResourceBundle LZPBANKS    = LoadProperties.getProperty("com.directi.pg.LZPBANKS");
    final static ResourceBundle LZPWALLETS  = LoadProperties.getProperty("com.directi.pg.LZPWALLETS");
    final static ResourceBundle BDBANKS     = LoadProperties.getProperty("com.directi.pg.BDBANKS");
    final static ResourceBundle BDWALLETS   = LoadProperties.getProperty("com.directi.pg.BDWALLETS");
    final static ResourceBundle BPBANKS     = LoadProperties.getProperty("com.directi.pg.BPBANKS");
    final static ResourceBundle BPWALLETS   = LoadProperties.getProperty("com.directi.pg.BPWALLETS");
    private static Logger logger                        = new Logger(RestDirectTransactionManager.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(RestDirectTransactionManager.class.getName());
    private static String defaultchargepercent  = "500";
    private static String INR_defaulttaxpercent = "1224";
    private static String USD_defaulttaxpercent = "1224";
    @Context
    HttpServletRequest request1;
    private Functions functions             = new Functions();
    private TransactionUtil transactionUtil = new TransactionUtil();

    public static String generateRandom(int size)
    {
        String tokenData    = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len             = tokenData.length();
        Date date           = new Date();
        Random rand         = new Random(date.getTime());
        StringBuffer token  = new StringBuffer();
        int index           = -1;

        for (int i = 0; i < size; i++)
        {
            index = rand.nextInt(len);
            token.append(tokenData.substring(index, index + 1));
        }
        return token.toString();
    }

    public String processExchangerCapture(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        AsyncAPICall asyncAPICall = AsyncAPICall.getInstance();
        String isUpdated = "";
        asyncAPICall.customerValidation(commonValidatorVO);

        isUpdated = getPaymentUpdate(commonValidatorVO);

        return isUpdated;
    }

    public DirectKitResponseVO processValidateandUpdateExchanger(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        ActionEntry entry                       = new ActionEntry();
        AuditTrailVO auditTrailVO               = new AuditTrailVO();
        CommResponseVO transRespDetails         = new CommResponseVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        PaymentManager paymentManager           = new PaymentManager();

        DateFormat dateFormat                   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date                               = new Date();
        String respDateTime                     = String.valueOf(dateFormat.format(date));

        paymentManager.updateCommonForExchanger(commonValidatorVO, "capturesuccess");
        entry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getIp());

        return null;
    }

    public DirectKitResponseVO processExchanger(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZDBViolationException
    {
        ActionEntry entry                           = new ActionEntry();
        PaymentManager paymentManager               = new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO     = new TransactionUtilsDAO();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        CommResponseVO transRespDetails             = new CommResponseVO();
        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();

        DateFormat dateFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date               = new Date();
        String respDateTime     = String.valueOf(dateFormat.format(date));

        GenericTransDetailsVO genericTransDetailsVO         = commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO     = commonValidatorVO.getAddressDetailsVO();
        int trackingId  = 0;
        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("Exchanger");
        trackingId      = paymentManager.insertAuthStartedTransactionEntryForAsyncFlow(commonValidatorVO, auditTrailVO);

        transRespDetails.setAmount(genericTransDetailsVO.getAmount());
        transRespDetails.setTransactionStatus("success");
        transRespDetails.setDescription("Exchanger Transaction");
        transRespDetails.setStatus("success");
        transRespDetails.setTransactionType("sale");

        entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
        transactionUtilsDAO.updateTransactionAfterResponse("transaction_common", "authsuccessful", genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), "", "", "Exchanger Transaction", respDateTime, String.valueOf(trackingId),transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),null);

        directKitResponseVO.setTrackingId(String.valueOf(trackingId));
        return directKitResponseVO;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8081/transactionServices").build();
    }

    public String getCustomerValidation(CommonValidatorVO commonValidatorVO)
    {
        ClientConfig config     = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client           = Client.create(config);
        WebResource service     = client.resource(getBaseURI());
        service.setProperty("Content-type", "application/json");
        service.setProperty("Content-Length", "1000");

        //TODO to set customerAccount
        return service.path("REST").path("v1").path("customerValidation").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, commonValidatorVO);
    }

    public String getPaymentUpdate(CommonValidatorVO commonValidatorVO)throws  PZDBViolationException
    {
        ActionEntry entry                   = new ActionEntry();
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        CommResponseVO transRespDetails         = new CommResponseVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        PaymentManager paymentManager           = new PaymentManager();

        DateFormat dateFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date               = new Date();
        String respDateTime     = String.valueOf(dateFormat.format(date));

        ClientConfig config     = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client       = Client.create(config);
        WebResource service = client.resource(getBaseURI());
        service.setProperty("Content-type", "application/json");
        service.setProperty("Content-Length", "1000");

        //Called BetConstruct's UpdatePayment

        service.path("REST").path("v1").path("paymentUpdate").type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, commonValidatorVO);

        paymentManager.updateCommonForExchanger(commonValidatorVO, "capturesuccess");
        entry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getIp());

        return "";
    }

    public DirectKitResponseVO processDirectTransactionUPI(CommonValidatorVO commonValidatorVO) throws  PZGenericConstraintViolationException, PZDBViolationException, PZConstraintViolationException
    {
        ActionEntry entry               = new ActionEntry();
        Functions functions             = new Functions();
        PaymentManager paymentManager   = new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO     = new TransactionUtilsDAO();
        AbstractPaymentGateway pg                   = null;
        Transaction transaction                     = new Transaction();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        ErrorCodeListVO errorCodeListVO             = new ErrorCodeListVO();

        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO commCardDetailsVO          = commonValidatorVO.getCardDetailsVO();
        DirectKitResponseVO directKitResponseVO         = new DirectKitResponseVO();
        CommRequestVO commRequestVO1                    = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO         = new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = new CommAddressDetailsVO();
        RecurringBillingVO recurringBillingVO           = null;
        TokenManager tokenManager                       = new TokenManager();
        RecurringManager recurringManager               = new RecurringManager();
        RestDirectTransactionManager restDirectTransactionManager   = new RestDirectTransactionManager();
        UnionPayInternationalUtils unionPayInternationalUtils       = new UnionPayInternationalUtils();

        if(commonValidatorVO.getRecurringBillingVO() != null)
        {
            recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        }
        if(!functions.isValueNull(genericTransDetailsVO.getRedirectUrl()))
        {
            genericTransDetailsVO.setRedirectUrl(" ");
        }

        String toid             = merchantDetailsVO.getMemberId();
        String respStatus       = null;
        String respPaymentId    = null;
        String errorName                = "";
        String mailtransactionStatus    = "Failed";
        String billingDiscriptor        = "";
        String respRemark       = "";
        String machineid        = "";
        String respDateTime     = null;
        String fStatus          = "";
        String accountId        = merchantDetailsVO.getAccountId();
        int trackingId          = 0;
        String paymodeid        = commonValidatorVO.getPaymentType();
        String cardtypeid       = commonValidatorVO.getCardType();

        //account details
        String fromtype         = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName        = Database.getTableName(gatewayType.getGateway());

        transDetailsVO.setResponseHashInfo(commonValidatorVO.getTransDetailsVO().getResponseHashInfo());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectMethod(commonValidatorVO.getTransDetailsVO().getRedirectMethod());
        //commAddressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        String phone=commonValidatorVO.getAddressDetailsVO().getPhone();
        String phoneCC=commonValidatorVO.getAddressDetailsVO().getTelnocc();
        transactionLogger.debug("phone --------- "+phone);
        transactionLogger.debug("phoneCC --------- "+phoneCC);
        //  commAddressDetailsVO.setPhone();

        commRequestVO1.setTransDetailsVO(transDetailsVO);
        commRequestVO1.setAddressDetailsVO(commAddressDetailsVO);
        auditTrailVO.setActionExecutorId(toid);
        auditTrailVO.setActionExecutorName("REST API");
        if(genericAddressDetailsVO.getEmail() == null)
            genericAddressDetailsVO.setEmail(" ");

        if (functions.isValueNull(commonValidatorVO.getTerminalVO().getTerminalId()))
            commonValidatorVO.setTerminalId(commonValidatorVO.getTerminalVO().getTerminalId());

        // Step 1 ::::::  Bin Check For China Card Or Other than china Card :::::
        // transactionLogger.debug("Card Number ---"+commCardDetailsVO.getCardNum());
        //  transactionLogger.debug("Inside China Card Condition ---");
        String cardNumber=commCardDetailsVO.getCardNum();
        // transactionLogger.debug("cardNumber ----"+cardNumber);
        String phoneNumber=commAddressDetailsVO.getPhone();
        transactionLogger.debug("phoneNumber --"+phoneNumber);
        String firstSix = functions.getFirstSix(cardNumber);
        transactionLogger.error("First Six ---"+firstSix);
        boolean checkBin_Result=unionPayInternationalUtils.checkBinFromFile(firstSix);
        transactionLogger.error("checkBin_Result ---------"+checkBin_Result);

        if (checkBin_Result)  // If China Card
        {
            transactionLogger.debug("Inside China Condition");
            //Step 2 ::: Enrollment Check From Our DB :::
            boolean isEnrolled_Result=unionPayInternationalUtils.isEnrolledCard(cardNumber,phoneNumber);
            transactionLogger.error("isEnrolled_Result ----------"+isEnrolled_Result);
            if (isEnrolled_Result) // already enrolled card to do direct purchase
            {
                transactionLogger.debug("isEnrolled_Result ----"+isEnrolled_Result);
                Date date3 = new Date();
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date3.getTime());
                directKitResponseVO = restDirectTransactionManager.processDirectTransaction(commonValidatorVO);
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));

                return directKitResponseVO;

            }
            else    // not  enrolled card do sms+enrollment
            {
                transactionLogger.error("Inside else  isEnrolled_Result ----"+isEnrolled_Result);
                //Step 2 ::: sms stared entry
                transactionLogger.debug("trackingId 1111111-------------"+trackingId);
                transactionLogger.debug("trackingId 222222222-------------"+commonValidatorVO.getTrackingid());
                paymentManager.insertSMSStartedTransactionEntryForCupUPIRest(commonValidatorVO,auditTrailVO,commRequestVO1,"transaction_common");


                // paymentManager.insertSMSStartedTransactionEntryForCupUPI(commonValidatorVO, String.valueOf(trackingId), auditTrailVO); //smsstared entry
                UnionPayInternationalPaymentGateway unionPayInternationalPaymentGateway=new UnionPayInternationalPaymentGateway(accountId);
                UnionPayInternationalUtility unionPayInternationalUtility=new UnionPayInternationalUtility();
                CommRequestVO commRequestVO = null;
                commRequestVO= unionPayInternationalUtility.getUnionPayRequestVO(commonValidatorVO);
                UnionPayInternationalResponseVO transRespDetails=null;
                transRespDetails = (UnionPayInternationalResponseVO) unionPayInternationalPaymentGateway.processSendSMS(commonValidatorVO.getTrackingid(),commRequestVO);
                transactionLogger.error("sms sending status ---------"+transRespDetails.getStatus());

                if (transRespDetails.getStatus().equalsIgnoreCase("success"))
                {
                    directKitResponseVO.setStatus("success");
                    fStatus = "Y";
                    respStatus="smsstarted";
                    transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,commonValidatorVO.getTransDetailsVO().getAmount(),genericAddressDetailsVO.getIp(),machineid,transRespDetails.getTransactionId(),transRespDetails.getRemark(),respDateTime,commonValidatorVO.getTrackingid(),transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),null);
                }
                else
                {
                    transactionLogger.debug("Insdei else fail of sms");
                    directKitResponseVO.setStatus("fail");
                    fStatus = "N";
                    respStatus="authfailed";
                    transactionLogger.debug("Trackingid --------------------------hfdvdvd-ddd------------- "+commonValidatorVO.getTrackingid());
                    CommonPaymentProcess paymentProcess=new CommonPaymentProcess();
                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                    transactionLogger.debug("After action entry ----------------");
                    transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,commonValidatorVO.getTransDetailsVO().getAmount(),genericAddressDetailsVO.getIp(),machineid,transRespDetails.getTransactionId(),transRespDetails.getRemark(),respDateTime,commonValidatorVO.getTrackingid(),transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),null);
                    //  transactionLogger.debug("After update ------");
                    // int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), transRespDetails.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                }
                directKitResponseVO.setTrackingId(String.valueOf(trackingId));
                transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,"Y".equalsIgnoreCase(fStatus)?PZResponseStatus.SUCCESS:PZResponseStatus.FAILED,transRespDetails.getRemark(),billingDiscriptor);
            }
        }
        else // else other than china then direct purchase
        {
            transactionLogger.debug("Inside other than China Condition");
            Date date3 = new Date();
            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date3.getTime());
            directKitResponseVO = restDirectTransactionManager.processDirectTransaction(commonValidatorVO);
            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));

            return directKitResponseVO;
        }
        return directKitResponseVO;
    }

    public DirectKitResponseVO processDirectTransaction(CommonValidatorVO commonValidatorVO) throws  PZGenericConstraintViolationException, PZDBViolationException, PZConstraintViolationException
    {
        ActionEntry entry               = new ActionEntry();
        Functions functions             = new Functions();
        PaymentManager paymentManager   = new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO = new TransactionUtilsDAO();
        AbstractPaymentGateway pg       = null;
        Transaction transaction         = new Transaction();
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        AccountInfoVO accountInfoVO                     = commonValidatorVO.getAccountInfoVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        DirectKitResponseVO directKitResponseVO         = new DirectKitResponseVO();
        CommRequestVO commRequestVO1                    = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO         = new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = new CommAddressDetailsVO();
        GenericCardDetailsVO commCardDetailsVO          = commonValidatorVO.getCardDetailsVO();
        RecurringBillingVO recurringBillingVO           = null;
        TokenManager tokenManager                       = new TokenManager();
        RecurringManager recurringManager               = new RecurringManager();
        CommAccountInfoVO commAccountInfoVO             = new CommAccountInfoVO();
        PZResponseStatus pzResponseStatus               = null;

        if(commonValidatorVO.getRecurringBillingVO()!=null)
        {
            recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        }
        if(!functions.isValueNull(genericTransDetailsVO.getRedirectUrl()))
        {
            genericTransDetailsVO.setRedirectUrl(" ");
        }

        String toid=merchantDetailsVO.getMemberId();
        String respStatus=null;
        String respPaymentId=null;
        String errorName = "";
        String mailtransactionStatus="Failed";
        String billingDiscriptor="";
        String respRemark="";
        String machineid="";
        String respDateTime=null;
        String fStatus = "";
        String accountId = merchantDetailsVO.getAccountId();
        int trackingId = 0;
        String paymodeid = commonValidatorVO.getPaymentType();
        String cardtypeid = commonValidatorVO.getCardType();

        //account details
        String fromtype         = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName        = Database.getTableName(gatewayType.getGateway());

        transDetailsVO.setResponseHashInfo(commonValidatorVO.getTransDetailsVO().getResponseHashInfo());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectMethod(commonValidatorVO.getTransDetailsVO().getRedirectMethod());
        transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
        //commAddressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
        commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());

        commRequestVO1.setCommAccountInfoVO(commAccountInfoVO);
        commRequestVO1.setTransDetailsVO(transDetailsVO);
        commRequestVO1.setAddressDetailsVO(commAddressDetailsVO);
        auditTrailVO.setActionExecutorId(toid);
        auditTrailVO.setActionExecutorName("REST API");
        if(genericAddressDetailsVO.getEmail() == null)
            genericAddressDetailsVO.setEmail(" ");

        if (functions.isValueNull(commonValidatorVO.getTerminalVO().getTerminalId()))
            commonValidatorVO.setTerminalId(commonValidatorVO.getTerminalVO().getTerminalId());

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
            commonValidatorVO.getCardDetailsVO().setIssuingBank(binResponseVO.getBank());
            commonValidatorVO.getCardDetailsVO().setCountryName(binResponseVO.getCountryname());
        }
*/

        if(functions.isValueNull(commCardDetailsVO.getCardNum()))
        {
            trackingId = paymentManager.insertAuthStartedTransactionEntry(commonValidatorVO, auditTrailVO, commRequestVO1, tableName);
        }
        else
        {
            trackingId = paymentManager.insertAuthStartedEntryForAccount(commonValidatorVO, auditTrailVO, commRequestVO1, tableName);
        }
        commonValidatorVO.setTrackingid(String.valueOf(trackingId));

        if (commonValidatorVO.getMerchantDetailsVO().getInternalFraudCheck().equalsIgnoreCase("Y")  && !(commonValidatorVO.isVIPCard() || commonValidatorVO.isVIPEmail()))
        {
            FraudChecker fraudChecker = new FraudChecker();
            fraudChecker.checkInternalFraudRules(commonValidatorVO);

            transactionLogger.error("Is Fraud Transaction in REST API---"+commonValidatorVO.isFraud());

            if(commonValidatorVO.isFraud())
            {
                String remark = "High Risk Transaction Detected";
                directKitResponseVO.setFraud(commonValidatorVO.isFraud());
                directKitResponseVO.setTrackingId(String.valueOf(trackingId));
                CommResponseVO commResponseVO   =new CommResponseVO();
                commResponseVO.setRemark(remark);
                paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, remark, null, String.valueOf(trackingId));
                int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commResponseVO, auditTrailVO, genericAddressDetailsVO.getIp());
                AsynchronousMailService mailService = new AsynchronousMailService();
                mailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark, null);
                return directKitResponseVO;
            }
        }

        try
        {
            Date date2 = new Date();
            pg =  AbstractPaymentGateway.getGateway(accountId);

            transactionLogger.debug("trackingid-----"+commonValidatorVO.getTrackingid());
            transactionLogger.debug("accountId-----"+accountId);
            if (Functions.checkAPIGateways(fromtype))
            {
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(accountId));
                CommRequestVO commRequestVO         = null;
                CommResponseVO transRespDetails     = null;
                commRequestVO = getCommonRequestVO(commonValidatorVO,transDetailsVO);
                String status="";
                transactionLogger.error("recurringBillingVO.getRecurringType()--------------->"+recurringBillingVO.getRecurringType());
                if ((recurringBillingVO.getRecurringType()!=null) && ((recurringBillingVO.getRecurringType().equals("INITIAL")) || (recurringBillingVO.getRecurringType().equals("REPEATED"))))
                {
                    recurringBillingVO.setIsManualRecurring(commonValidatorVO.getTerminalVO().getIsManualRecurring());
                    recurringBillingVO.setIsAutomaticRecurring(commonValidatorVO.getTerminalVO().getIsRecurring());
                    if((recurringBillingVO.getRecurringType().equals("INITIAL")))
                    {
                        if ("Y".equalsIgnoreCase(account.getIsRecurring()))
                        {
                            recurringBillingVO  = new RecurringBillingVO();
                            String cardNum      = commonValidatorVO.getCardDetailsVO().getCardNum();
                            MerchantDetailsVO merchantDetailsVO2 = null;

                            if(commonValidatorVO.getMerchantDetailsVO() != null){
                                merchantDetailsVO2 = commonValidatorVO.getMerchantDetailsVO();
                            }
                            recurringBillingVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                            recurringBillingVO.setOriginTrackingId(String.valueOf(trackingId));

                            recurringBillingVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                            if (functions.isValueNull(cardNum))
                            {
                                if(merchantDetailsVO2 != null  && merchantDetailsVO2.getIsCardStorageRequired().equalsIgnoreCase("N")){
                                    recurringBillingVO.setFirstSix("");
                                    recurringBillingVO.setLastFour("");
                                }else{
                                    recurringBillingVO.setFirstSix(cardNum.substring(0, 6));
                                    recurringBillingVO.setLastFour(cardNum.substring((cardNum.length() - 4), cardNum.length()));
                                }

                            }
                            else if (functions.isValueNull(commCardDetailsVO.getBIC()))
                            {
                                recurringBillingVO.setBIC(commCardDetailsVO.getBIC());
                                recurringBillingVO.setIBAN(commCardDetailsVO.getIBAN());
                            }
                            else if (functions.isValueNull(commRequestVO.getReserveField2VO().getAccountNumber()))
                            {
                                recurringBillingVO.setAccountNumber(commRequestVO.getReserveField2VO().getAccountNumber());
                                recurringBillingVO.setAccountType(commRequestVO.getReserveField2VO().getAccountType());
                                recurringBillingVO.setRoutingNumber(commRequestVO.getReserveField2VO().getRoutingNumber());
                            }
                            if ((paymodeid != null) && (cardtypeid != null))
                            {
                                recurringBillingVO.setPaymentType(paymodeid);
                                recurringBillingVO.setCardType(cardtypeid);
                            }
                            recurringBillingVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                            recurringBillingVO.setTerminalid(commonValidatorVO.getTerminalId());
                            recurringBillingVO.setRecurringType("INITIAL");
                            recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());
                            Gson gson=new Gson();
                            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
                        }
                        else
                        {
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_RECURRINGALLOW, ErrorMessages.INVALID_IS_RECURRING));
                            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", ErrorMessages.INVALID_IS_RECURRING, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO,ErrorMessages.INVALID_IS_RECURRING ,new Throwable(ErrorMessages.INVALID_IS_RECURRING));
                        }
                    }
                }

                try
                {
                    if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype)){
                        transactionLogger.error("inside ---->  CashFlowsCaiboPaymentGateway isService : N AND type PA");
                        transRespDetails = (CommResponseVO) pg.processInitialSale(String.valueOf(trackingId), commRequestVO);
                        status = "authsuccessful";
                    }
                    else if(merchantDetailsVO.getService().equalsIgnoreCase("N") || "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingId), commRequestVO);
                        status = "authsuccessful";
                    }
                    else
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                        status = "capturesuccess";
                    }
                }
                catch (PZTechnicalViolationException tve)
                {
                    logger.error("PZTechnicalViolationException in processTransaction---", tve);
                    String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
                    if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()))
                    {
                        try
                        {
                            paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid());
                        }
                        catch (PZDBViolationException d)
                        {
                            logger.error("----PZDBViolationException in update with error name-----", d);
                        }
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "Bank Connectivity Issue while processing your request";
                    }
                    else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
                    {
                        try
                        {
                            paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid());
                        }
                        catch (PZDBViolationException d)
                        {
                            logger.error("----PZDBViolationException in update with error name-----", d);
                        }
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "Your transaction is Timed out.Please check at bank side.";
                    }
                    else
                    {
                        try
                        {
                            paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(),commonValidatorVO.getTrackingid());
                        }
                        catch (PZDBViolationException d)
                        {
                            logger.error("----PZDBViolationException in update with error name-----", d);
                        }
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "We have encountered an internal error while processing your request.";
                    }
                    transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), null, commonValidatorVO, PZResponseStatus.ERROR, respRemark, null);
                    PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.DIRECT_KIT_WEBSERVICE);
                    return directKitResponseVO;
                }

                if(transRespDetails!=null && functions.isValueNull(transRespDetails.getStatus()))
                {
                    transactionLogger.error("transRespDetails---"+transRespDetails.getStatus());
                    transactionLogger.error("-----------status from gateway:::"+transRespDetails.getStatus());
                    //For Phoneix Integration
                    if (functions.isValueNull(transRespDetails.getBankCode()) && functions.isValueNull(transRespDetails.getBankDescription()))
                    {
                        commonValidatorVO.setBankCode(transRespDetails.getBankCode());
                        commonValidatorVO.setBankDescription(transRespDetails.getBankDescription());
                    }
                    if(functions.isValueNull(transRespDetails.getTransactionId())){
                        directKitResponseVO.setBankReferenceId(transRespDetails.getTransactionId());
                    }

                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        if(status.equals("capturesuccess"))
                        {
                            respStatus              = "capturesuccess";
                            mailtransactionStatus   = "capturesuccess";
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                        }
                        else
                        {
                            respStatus              = "authsuccessful";
                            mailtransactionStatus   = "authsuccessful";
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                        }
                        if(functions.isValueNull(commonValidatorVO.getToken()))
                        {
                            if((functions.isValueNull(recurringBillingVO.getRecurringType()) && "INITIAL".equals(recurringBillingVO.getRecurringType())))
                            {
                                //recurring's originatingTrackingId != token's trackingId then update the token's trackingId
                                if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                                {
                                    TokenDetailsVO tokenDetailsVO   = new TokenDetailsVO();
                                    tokenDetailsVO                  = tokenManager.getRegisteredTokenDetailsByMerchant(toid, commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO);
                                    if (recurringManager.isRecurringTrackingIdMatchesWithMerchantToken(commonValidatorVO.getToken(), recurringBillingVO.getOriginTrackingId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                    {
                                        tokenManager.updateTrackingIdForRegistrationTokenByMerchant(commonValidatorVO.getToken(), String.valueOf(trackingId), toid);
                                    }
                                    else
                                    {
                                        if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                            tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(),commonValidatorVO.getMerchantDetailsVO().getMemberId(),recurringBillingVO.getOriginTrackingId());
                                    }
                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, recurringBillingVO.getOriginTrackingId());
                                }
                                else
                                {
                                    TokenDetailsVO tokenDetailsVO   = new TokenDetailsVO();
                                    tokenDetailsVO                  = tokenManager.getRegisteredTokenDetailsByPartner(commonValidatorVO.getPartnerDetailsVO().getPartnerId(),commonValidatorVO.getToken(),commonValidatorVO);

                                    if(recurringManager.isRecurringTrackingIdMatchesPartnerWithToken(commonValidatorVO.getToken(), recurringBillingVO.getOriginTrackingId(), commonValidatorVO.getMerchantDetailsVO().getPartnerId()))
                                        tokenManager.updateTrackingIdForRegistrationTokenByPartner(commonValidatorVO.getToken(), String.valueOf(trackingId), merchantDetailsVO.getPartnerId());
                                    else
                                    {
                                        if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                            tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), recurringBillingVO.getOriginTrackingId());
                                    }
                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, recurringBillingVO.getOriginTrackingId());
                                }
                            }
                            else
                            {
                                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                                {
                                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(toid, commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO);
                                    if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), toid))
                                    {
                                        tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), String.valueOf(trackingId));
                                    }

                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, String.valueOf(trackingId));
                                }
                                else
                                {
                                    tokenDetailsVO = tokenManager.getInitialTokenDetailsWithPartnerId(commonValidatorVO.getToken(), merchantDetailsVO.getPartnerId()); //with PartnerId
                                    if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                        tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), String.valueOf(trackingId));

                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, String.valueOf(trackingId));
                                }
                            }
                        }

                        if(commonValidatorVO.getCreateRegistration()!=null || recurringBillingVO.getRecurringType() != null)
                        {
                            if(commonValidatorVO.getCreateRegistration() != null && (commonValidatorVO.getCreateRegistration().equalsIgnoreCase("true")))
                            {
                                if ("Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                                {
                                    TerminalManager terminalManager = new TerminalManager();
                                    if (terminalManager.isTokenizationActiveOnTerminal(merchantDetailsVO.getMemberId(),commonValidatorVO.getTerminalId()))
                                    {
                                        transactionLogger.error("-----Inside Token Creation-----");
                                        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                                        {
                                            if (!tokenManager.isCardholderRegistered(merchantDetailsVO.getMemberId(), commonValidatorVO.getCustomerId()))
                                            {
                                                commonValidatorVO.setCustomerId(null);
                                            }
                                        }

                                        String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();
                                        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                        TokenResponseVO tokenResponseVO = new TokenResponseVO();
                                        genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                                        genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

                                        String existingTokenId = null;
                                        String registrationStatus = null;

                                        if(commonValidatorVO.getCardDetailsVO().getCardNum() != null)
                                        {
                                            tokenRequestVO.setMemberId(toid);
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setPaymentType(commonValidatorVO.getTerminalVO().getPaymodeId());
                                            tokenRequestVO.setCardType(commonValidatorVO.getTerminalVO().getCardTypeId());
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setGeneratedBy(generatedBy);
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            genericTransDetailsVO.setCurrency(transDetailsVO.getCurrency());
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                                            tokenRequestVO.setIsActive("Y");
                                            //tokenRequestVO.setCvv(commonValidatorVO.getCardDetailsVO().getcVV());
                                            String expDate="";
                                            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
                                                expDate=commonValidatorVO.getCardDetailsVO().getExpMonth()+"/"+commonValidatorVO.getCardDetailsVO().getExpYear();

                                            existingTokenId = tokenManager.isTokenAvailable(tokenRequestVO.getMemberId(), tokenRequestVO.getCardDetailsVO().getCardNum(),expDate);
                                            if(functions.isValueNull(existingTokenId))
                                            {
                                                tokenRequestVO.setTokenId(existingTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                            else
                                            {
                                                String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO);
                                                tokenRequestVO.setTokenId(newTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                        }

                                        else if(commonValidatorVO.getCardDetailsVO().getBIC() != null || commRequestVO.getReserveField2VO().getAccountNumber() != null)
                                        {
                                            tokenRequestVO.setMemberId(toid);
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setGeneratedBy(generatedBy);
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                            tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                            tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            tokenRequestVO.setReserveField2VO(commonValidatorVO.getReserveField2VO());
                                            genericTransDetailsVO.setCurrency(transDetailsVO.getCurrency());
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                                            tokenRequestVO.setIsActive("Y");
                                            existingTokenId = tokenManager.isNewAccount(tokenRequestVO.getMemberId(), tokenRequestVO);
                                            if(functions.isValueNull(existingTokenId))
                                            {
                                                tokenRequestVO.setTokenId(existingTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                            else
                                            {
                                                String bankAccountId = tokenManager.insertBankAccountDetails(tokenRequestVO); //inserting bank account details
                                                tokenRequestVO.setBankAccountId(bankAccountId);
                                                String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO); //new token creation in token_master
                                                tokenRequestVO.setTokenId(newTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO); //new registration in registration_master
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId()); //inserting membersId and trackingId in mapping table
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                        }

                                        if ("success".equals(tokenResponseVO.getStatus()))
                                        {
                                            directKitResponseVO.setToken(tokenResponseVO.getRegistrationToken());
                                            directKitResponseVO.setStatus(tokenResponseVO.getStatus());

                                            //registration transactions entry
                                            TokenTransactionDetailsVO tokenTransactionDetailsVO = new TokenTransactionDetailsVO();
                                            tokenTransactionDetailsVO.setToid(tokenRequestVO.getMemberId());
                                            tokenTransactionDetailsVO.setTrackingid(String.valueOf(trackingId));
                                            tokenTransactionDetailsVO.setAmount(transDetailsVO.getAmount());
                                            tokenTransactionDetailsVO.setRegistrationId(tokenDetailsVO.getRegistrationId());

                                            tokenManager.manageRegistrationTransactionDetails(tokenTransactionDetailsVO);
                                        }
                                    }
                                }
                            }
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
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation") )
                    {
                        transactionLogger.debug("inside 3D------"+transRespDetails.getStatus());
                        if(!functions.isValueNull(transRespDetails.getThreeDVersion()))
                            transRespDetails.setThreeDVersion("3Dv1");
                        paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                        paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                        Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                        directKitResponseVO.setTrackingId(String.valueOf(trackingId));
                        directKitResponseVO.setStatus("pending3DConfirmation");

                        if (fromtype.equalsIgnoreCase("qikpayv2") || fromtype.equalsIgnoreCase("cleveland") || fromtype.equalsIgnoreCase("apexpay") || fromtype.equalsIgnoreCase("fastpayv6") || fromtype.equalsIgnoreCase("payu") || fromtype.equalsIgnoreCase("wealthpay") || fromtype.equalsIgnoreCase("imoneypay") || fromtype.equalsIgnoreCase("epaymentz") ||fromtype.equalsIgnoreCase("qikpay") ||fromtype.equalsIgnoreCase("cashfree") ||fromtype.equalsIgnoreCase("vervepay") ||fromtype.equalsIgnoreCase("billdesk") || fromtype.equalsIgnoreCase("pbs") || fromtype.equalsIgnoreCase("safexpay") || fromtype.equalsIgnoreCase("bhartipay"))
                        {
                            transactionLogger.debug("inside 3D------pbs");
                            paymentProcess.set3DResponseVO(directKitResponseVO, response3D, commonValidatorVO);
                        }
                        else
                        {
                            paymentProcess.set3DResponseVO(directKitResponseVO, response3D);
                        }
                        if(functions.isValueNull(commonValidatorVO.getToken()))
                        {
                            if((functions.isValueNull(recurringBillingVO.getRecurringType()) && "INITIAL".equals(recurringBillingVO.getRecurringType())))
                            {
                                //recurring's originatingTrackingId != token's trackingId then update the token's trackingId
                                if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                                {
                                    TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(toid, commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO);
                                    if (recurringManager.isRecurringTrackingIdMatchesWithMerchantToken(commonValidatorVO.getToken(), recurringBillingVO.getOriginTrackingId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                    {
                                        tokenManager.updateTrackingIdForRegistrationTokenByMerchant(commonValidatorVO.getToken(), String.valueOf(trackingId), toid);
                                    }
                                    else
                                    {
                                        if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                            tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(),commonValidatorVO.getMerchantDetailsVO().getMemberId(),recurringBillingVO.getOriginTrackingId());
                                    }
                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, recurringBillingVO.getOriginTrackingId());
                                }
                                else
                                {
                                    TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(commonValidatorVO.getPartnerDetailsVO().getPartnerId(),commonValidatorVO.getToken(),commonValidatorVO);

                                    if(recurringManager.isRecurringTrackingIdMatchesPartnerWithToken(commonValidatorVO.getToken(), recurringBillingVO.getOriginTrackingId(), commonValidatorVO.getMerchantDetailsVO().getPartnerId()))
                                        tokenManager.updateTrackingIdForRegistrationTokenByPartner(commonValidatorVO.getToken(), String.valueOf(trackingId), merchantDetailsVO.getPartnerId());
                                    else
                                    {
                                        if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                            tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), recurringBillingVO.getOriginTrackingId());
                                    }
                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, recurringBillingVO.getOriginTrackingId());
                                }
                            }
                            else
                            {
                                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                                {
                                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(toid, commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO);
                                    if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), toid))
                                    {
                                        tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), String.valueOf(trackingId));
                                    }

                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, String.valueOf(trackingId));
                                }
                                else
                                {
                                    tokenDetailsVO = tokenManager.getInitialTokenDetailsWithPartnerId(commonValidatorVO.getToken(), merchantDetailsVO.getPartnerId()); //with PartnerId
                                    if(!tokenManager.isTokenMappedWithTrackingId(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                                        tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getTokenId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), String.valueOf(trackingId));

                                    tokenManager.insertTrackingIdForRegistrationTokenByMerchant(commonValidatorVO, tokenDetailsVO, String.valueOf(trackingId));
                                }
                            }
                        }

                        if(commonValidatorVO.getCreateRegistration()!=null || recurringBillingVO.getRecurringType() != null)
                        {
                            if(commonValidatorVO.getCreateRegistration() != null && (commonValidatorVO.getCreateRegistration().equalsIgnoreCase("true")))
                            {
                                if ("Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                                {
                                    TerminalManager terminalManager = new TerminalManager();
                                    if (terminalManager.isTokenizationActiveOnTerminal(merchantDetailsVO.getMemberId(),commonValidatorVO.getTerminalId()))
                                    {
                                        transactionLogger.error("-----Inside Token Creation-----");
                                        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                                        {
                                            if (!tokenManager.isCardholderRegistered(merchantDetailsVO.getMemberId(), commonValidatorVO.getCustomerId()))
                                            {
                                                commonValidatorVO.setCustomerId(null);
                                            }
                                        }

                                        String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();
                                        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                        TokenResponseVO tokenResponseVO = new TokenResponseVO();
                                        genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                                        genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

                                        String existingTokenId = null;
                                        String registrationStatus = null;

                                        if(commonValidatorVO.getCardDetailsVO().getCardNum() != null)
                                        {
                                            tokenRequestVO.setMemberId(toid);
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setPaymentType(commonValidatorVO.getTerminalVO().getPaymodeId());
                                            tokenRequestVO.setCardType(commonValidatorVO.getTerminalVO().getCardTypeId());
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setGeneratedBy(generatedBy);
                                            tokenRequestVO.setRegistrationGeneratedBy(generatedBy);
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            genericTransDetailsVO.setCurrency(transDetailsVO.getCurrency());
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                                            tokenRequestVO.setIsActive("N");
                                            //tokenRequestVO.setCvv(commonValidatorVO.getCardDetailsVO().getcVV());
                                            String expDate="";
                                            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
                                                expDate=commonValidatorVO.getCardDetailsVO().getExpMonth()+"/"+commonValidatorVO.getCardDetailsVO().getExpYear();

                                            existingTokenId = tokenManager.isTokenAvailable(tokenRequestVO.getMemberId(), tokenRequestVO.getCardDetailsVO().getCardNum(),expDate);
                                            if(functions.isValueNull(existingTokenId))
                                            {
                                                tokenRequestVO.setTokenId(existingTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                            else
                                            {
                                                String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO);
                                                tokenRequestVO.setTokenId(newTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                        }

                                        else if(commonValidatorVO.getCardDetailsVO().getBIC() != null || commRequestVO.getReserveField2VO().getAccountNumber() != null)
                                        {
                                            tokenRequestVO.setMemberId(toid);
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setGeneratedBy(generatedBy);
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                            tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                            tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            tokenRequestVO.setReserveField2VO(commonValidatorVO.getReserveField2VO());
                                            genericTransDetailsVO.setCurrency(transDetailsVO.getCurrency());
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                                            tokenRequestVO.setIsActive("N");
                                            existingTokenId = tokenManager.isNewAccount(tokenRequestVO.getMemberId(), tokenRequestVO);
                                            if(functions.isValueNull(existingTokenId))
                                            {
                                                tokenRequestVO.setTokenId(existingTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                            else
                                            {
                                                String bankAccountId = tokenManager.insertBankAccountDetails(tokenRequestVO); //inserting bank account details
                                                tokenRequestVO.setBankAccountId(bankAccountId);
                                                String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO); //new token creation in token_master
                                                tokenRequestVO.setTokenId(newTokenId);
                                                tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO); //new registration in registration_master
                                                registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId()); //inserting membersId and trackingId in mapping table
                                                tokenResponseVO.setStatus(registrationStatus);
                                                tokenResponseVO.setTokenId(existingTokenId);
                                                tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            }
                                        }

                                        /*if ("success".equals(tokenResponseVO.getStatus()))
                                        {
                                            directKitResponseVO.setToken(tokenResponseVO.getRegistrationToken());
                                            directKitResponseVO.setStatus(tokenResponseVO.getStatus());

                                            //registration transactions entry
                                            TokenTransactionDetailsVO tokenTransactionDetailsVO = new TokenTransactionDetailsVO();
                                            tokenTransactionDetailsVO.setToid(tokenRequestVO.getMemberId());
                                            tokenTransactionDetailsVO.setTrackingid(String.valueOf(trackingId));
                                            tokenTransactionDetailsVO.setAmount(transDetailsVO.getAmount());
                                            tokenTransactionDetailsVO.setRegistrationId(tokenDetailsVO.getRegistrationId());

                                            tokenManager.manageRegistrationTransactionDetails(tokenTransactionDetailsVO);
                                        }*/
                                    }
                                }
                            }
                        }
                        return directKitResponseVO;
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                    {
                        Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                        directKitResponseVO.setTrackingId(String.valueOf(trackingId));
                        if(fromtype.equalsIgnoreCase("aldrapay") || fromtype.equalsIgnoreCase("purplepay") || fromtype.equalsIgnoreCase("ravedirect"))
                        {
                            paymentProcess.set3DResponseVO(directKitResponseVO, response3D,commonValidatorVO);
                            directKitResponseVO.setStatus("pending");
                            return directKitResponseVO;

                        }
                        else if(fromtype.equalsIgnoreCase("agnipay") || fromtype.equalsIgnoreCase("trnsactWLD") || fromtype.equalsIgnoreCase("shimotomo"))
                        {
                            paymentProcess.set3DResponseVO(directKitResponseVO, response3D);
                            directKitResponseVO.setStatus("pending");
                            return directKitResponseVO;
                        }
                        else if(fromtype.equalsIgnoreCase("decta"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, directKitResponseVO.getTrackingId());
                            paymentProcess.set3DResponseVO(directKitResponseVO, response3D,commonValidatorVO);
                            directKitResponseVO.setStatus("pending");
                            return directKitResponseVO;
                        }
                        else if (fromtype.equalsIgnoreCase("Oculus"))
                        {
                            transactionLogger.error("Oculus Frictionless3DS 3Dv2");
                            transRespDetails.setThreeDVersion("3Dv2");
                            paymentManager.updatePaymentIdForCommon(transRespDetails, directKitResponseVO.getTrackingId());
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO);
                            respStatus = "capturesuccess";

                        }
                        else
                        {
                            respStatus = "authstarted";
                            mailtransactionStatus = "authstarted";
                            billingDiscriptor = pg.getDisplayName();
                        }
                    }
                    else
                    {
                        respStatus="authfailed";
                        mailtransactionStatus  = "authfailed";
                        paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                    }
                    respDateTime = transRespDetails.getResponseTime();
                    respRemark = transRespDetails.getDescription();
                    respPaymentId =  transRespDetails.getTransactionId();
                    if (functions.isValueNull(transRespDetails.getErrorName()))
                    {
                        errorName = transRespDetails.getErrorName();
                        directKitResponseVO.setErrorName(transRespDetails.getErrorName());
                    }

                    if(functions.isValueNull(transRespDetails.getTerminalId()))
                        transactionUtilsDAO.updateTransactionAfterResponseFor3DRouting(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, String.valueOf(trackingId), transRespDetails.getRrn(), transRespDetails.getArn(), transRespDetails.getAuthCode(), transRespDetails.getTerminalId(), transRespDetails.getFromAccountId(),transRespDetails.getFromMid(),"Non-3D");
                    else
                        transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,String.valueOf(trackingId),transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                }
/* if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getSuccessReconMail()))
 {
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDiscriptor);

                AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null, billingDiscriptor);
 }*/
            }
            if("capturesuccess".equalsIgnoreCase(respStatus) || "authsuccessful".equalsIgnoreCase(respStatus))
            {
                pzResponseStatus = PZResponseStatus.SUCCESS;
            }else if("authstarted".equalsIgnoreCase(respStatus))
            {
                pzResponseStatus=PZResponseStatus.PENDING;
            }
            else
            {
                pzResponseStatus = PZResponseStatus.FAILED;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in RestDirectTransactionManager---", se);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(RestDirectTransactionManager.class.getName(),"processDirectTransaction()",null,"common","Technical Exception",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,se.getMessage(),se.getCause(),toid,"Transaction Web Service");
            transactionUtilsDAO.insertFailedTransaction(String.valueOf(trackingId), se.getMessage(), tableName, commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,PZResponseStatus.FAILED,respRemark,billingDiscriptor);
        }
        transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,pzResponseStatus,respRemark,billingDiscriptor);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processHostedPaymentTransaction(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException,PZGenericConstraintViolationException{
        DirectKitResponseVO directKitResponseVO=null;
        if(commonValidatorVO!=null)
        {
            directKitResponseVO = new DirectKitResponseVO();
            AbstractPaymentProcess paymentProcess = new CommonPaymentProcess();
            directKitResponseVO = paymentProcess.setHostedPaymentResponseVO(directKitResponseVO, commonValidatorVO);
        }
        return directKitResponseVO;
    }
    public DirectKitResponseVO processAsyncDirectTransaction(CommonValidatorVO commonValidatorVO)throws PZConstraintViolationException,PZGenericConstraintViolationException
    {
        PaymentManager paymentManager           = new PaymentManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        int trackingid                  = 0;
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        UnicreditUtils unicreditUtils   = new UnicreditUtils();

        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("REST API");
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg   = null;
        if("NBI".equalsIgnoreCase(commonValidatorVO.getPaymentMode()) ||"EWI".equalsIgnoreCase(commonValidatorVO.getPaymentMode()) || "NBB".equalsIgnoreCase(commonValidatorVO.getPaymentMode()) || "EWB".equalsIgnoreCase(commonValidatorVO.getPaymentMode()))
        {
           if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider())){
            commonValidatorVO.setCustomerId(commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_"));
           }
            else{
                commonValidatorVO.setCustomerId(commonValidatorVO.getTransDetailsVO().getPaymentProvider());
            }
        }
        else if("UPI".equalsIgnoreCase(commonValidatorVO.getPaymentMode()))
            commonValidatorVO.setCustomerId(commonValidatorVO.getVpa_address());

        trackingid = paymentManager.insertAuthStartedTransactionEntryForAsyncFlow(commonValidatorVO,auditTrailVO);
        commonValidatorVO.setTrackingid(String.valueOf(trackingid));
        directKitResponseVO.setTrackingId(String.valueOf(trackingid));

        try
        {
            pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if(commonValidatorVO.getPaymentMode().equalsIgnoreCase("NB"))
            {
                if ((commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SOFORT")) || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("IDEAL"))
                {
                    SofortUtility sofortUtility = new SofortUtility();
                    CommResponseVO transRespDetails = null;

                    if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SOFORT"))
                    {
                        commRequestVO = sofortUtility.getSofortRequestVO(commonValidatorVO);
                    }
                    else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("IDEAL"))
                    {
                        IdealPaymentGateway idealPaymentGateway = new IdealPaymentGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        IDealBanksResponse iDealBanksResponse = idealPaymentGateway.getIDealBankDetails();

                        String bankCode = "";
                        String bankName = "";
                        Map<String, String> idealBankMap = new HashMap<String, String>();
                        for (IDealBank bank : iDealBanksResponse.getBanks())
                        {
                            // build a bank selection

                            bankCode = bank.getCode();
                            bankName = bank.getName();
                            idealBankMap.put(bankName, bankCode);

                        }
                        if (idealBankMap.containsKey(commonValidatorVO.getCardDetailsVO().getBankName()))
                        {
                            commonValidatorVO.setSenderBankCode(idealBankMap.get(commonValidatorVO.getCardDetailsVO().getBankName()));
                        }
                        else
                        {
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALID_BANKNAME, ErrorMessages.INVALID_BANKNAME));
                            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_BANKNAME, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_BANKNAME, new Throwable(ErrorMessages.INVALID_BANKNAME));
                        }
                        commRequestVO = sofortUtility.getIdealRequestVO(commonValidatorVO);
                    }

                    if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsService()) || "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
                        transRespDetails = (CommResponseVO) pg.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
                    else
                        transRespDetails = (CommResponseVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                            directKitResponseVO.setBankRedirectionUrl(transRespDetails.getRedirectUrl());
                            directKitResponseVO.setStatus("pending");

                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");

                        }
                    }

                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("P4"))
                {
                    P4ResponseVO transRespDetails = null;
                    P4Utils p4Utils = new P4Utils();

                    commRequestVO = p4Utils.getRequestForOnlineBankTransfer(commonValidatorVO);
                    transRespDetails = (P4ResponseVO) pg.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                            directKitResponseVO.setBankRedirectionUrl(transRespDetails.getFormularURL());
                            directKitResponseVO.setStatus("success");
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());//TODO Update query for the P4 as of sofort
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("TRUSTLY"))
                {
                    CommResponseVO transRespDetails = null;
                    TrustlyUtils trustlyUtils = new TrustlyUtils();
                    commonValidatorVO.setCustomerId("12585");
                    commRequestVO = trustlyUtils.getTrustlyRequestVO(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);

                    if(transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            directKitResponseVO.setBankRedirectionUrl(transRespDetails.getRedirectUrl());
                            paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingid));
                            directKitResponseVO.setStatus("success");
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }

                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("PV"))
            {
                commonValidatorVO.getAddressDetailsVO().setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commonValidatorVO.setCustomerId("24532");
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("VOUCHERMONEY"))
                {
                    VoucherMoneyResponse transRespDetails = null;
                    VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
                    commRequestVO = voucherMoneyUtils.getVoucherMoneyRequestVO(commonValidatorVO);

                    if (commonValidatorVO.getMerchantDetailsVO().getIsService().equalsIgnoreCase("N") || "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
                    {
                        transRespDetails = (VoucherMoneyResponse) pg.processAuthentication(String.valueOf(trackingid), commRequestVO);
                    }
                    else
                    {
                        transRespDetails = (VoucherMoneyResponse) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingid));
                            //String html=voucherMoneyUtils.generateAutoSubmitForm(transRespDetails.getPaymentFormUrl());
                            directKitResponseVO.setBankRedirectionUrl(transRespDetails.getPaymentFormUrl());
                            directKitResponseVO.setStatus("success");
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("PAYSAFECARD"))
                {
                    commRequestVO = getPaySafeRequestVO(commonValidatorVO);
                    CommResponseVO transRespDetails = null;
                    if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsService()) || "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
                        transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingid), commRequestVO);
                    else
                        transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    if ("pending".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                    {
                        directKitResponseVO.setBankRedirectionUrl(transRespDetails.getRedirectUrl());
                        directKitResponseVO.setStatus("pending");
                    }
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("PURPLEPAY"))
                {

                }
                else if ( commonValidatorVO.getPaymentBrand().equalsIgnoreCase("FLEXEPIN VOUCHER"))
                {
                    String voucherPin                           = commonValidatorVO.getCardDetailsVO().getVoucherNumber();
                    String customerIpAddress                    = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                    Comm3DResponseVO transRespDetails           = null;
                    Comm3DRequestVO comm3DRequestVO             = new Comm3DRequestVO();
                    CommCardDetailsVO commCardDetailsVO         = new CommCardDetailsVO();
                    CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();

                    commCardDetailsVO.setVoucherNumber(voucherPin);
                    commAddressDetailsVO.setCardHolderIpAddress(customerIpAddress);
                    comm3DRequestVO.setCardDetailsVO(commCardDetailsVO);
                    comm3DRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                    commonValidatorVO.setAddressDetailsVO(commAddressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(commCardDetailsVO);

                    transactionLogger.error(trackingid + " --> voucherPin: "+ functions.maskingPan(voucherPin));
                    transactionLogger.error(trackingid + " --> voucherPin from VO: "+ functions.maskingPan(commCardDetailsVO.getVoucherNumber()));

                    if (commonValidatorVO.getMerchantDetailsVO().getIsService().equalsIgnoreCase("N") || "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
                        transRespDetails = (Comm3DResponseVO) pg.processAuthentication(String.valueOf(trackingid), comm3DRequestVO);
                    else
                        transRespDetails = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), comm3DRequestVO);

                    if (transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            transactionLogger.error("inside success for flexepin from rest");
                            paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingid));
                            paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("success");
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            transactionLogger.error("inside failed for flexepin from rest");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("NBI"))
            {
                transactionLogger.error("Inside   RestDirectTransactionManager NBI PayMode--->>>"+commonValidatorVO.getPaymentMode());
                MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
                String accountId = merchantDetailsVO.getAccountId();
                String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("ICICI") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("AXIS") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("HDFC") ||
                        commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SBI") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("KOTAK") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("YES BANK")||commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SEPBANKS")
                        || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BDBANKS") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BPBANKS") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("LZPBANKS")
                        ||commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QPBANKS") ||commonValidatorVO.getPaymentBrand().equalsIgnoreCase("VPBANKS"))
                {
                    String html="";
                    transactionLogger.error("fromtype is for india processing --- >"+fromtype);
                    if (fromtype.equalsIgnoreCase("safexpay"))
                    {   if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SEPBANKS"))
                    {
                        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                        {
                            String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                            commonValidatorVO.setProcessorName(SEPBANKS.getString(key));
                        }
                        else
                        {
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                        }
                    }else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("KOTAK"))
                        commonValidatorVO.setProcessorName(SEPBANKS.getString("KOTAK_BANK"));
                    else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("AXIS"))
                        commonValidatorVO.setProcessorName(SEPBANKS.getString("AXIS"));
                    else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("YES BANK"))
                        commonValidatorVO.setProcessorName(SEPBANKS.getString("YES_BANK"));
                    else
                        commonValidatorVO.setProcessorName(SEPBANKS.getString(commonValidatorVO.getPaymentBrand()));
                        html = pg.processAutoRedirect(commonValidatorVO);
                        transactionLogger.error("Html In netbanking  safexpay RestDirectManager ------" + html);
                    }
                    else if ( fromtype.equalsIgnoreCase("billdesk"))
                    {
                        transactionLogger.error("inside billdesk NB >> ");
                        if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BDBANKS"))
                        {
                            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                            {
                                String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                                commonValidatorVO.setProcessorName(BDBANKS.getString(key));
                            }
                            else
                            {
                                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                            }
                        }
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("YES BANK"))
                            commonValidatorVO.setProcessorName(BDBANKS.getString("Yes_Bank_Ltd"));
                        else
                            commonValidatorVO.setProcessorName(BDBANKS.getString(commonValidatorVO.getPaymentBrand()));
                        html = pg.processAutoRedirect(commonValidatorVO);
                        transactionLogger.error("Html In netbanking  Billdesk RestDirectManager ------"+html);
                    }
                    else if (fromtype.equalsIgnoreCase("letzpay"))
                    {
                        LetzPayUtils letzPayUtils=new LetzPayUtils();
                        AbstractPaymentProcess abstractPaymentProcess=PaymentProcessFactory.getPaymentProcessInstance(fromtype);
                        if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("LZPBANKS"))
                        {
                            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                            {
                                String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                                commonValidatorVO.setProcessorName(LZPBANKS.getString(key));
                            }
                            else
                            {
                                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                            }
                        }else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("KOTAK"))
                            commonValidatorVO.setProcessorName(LZPBANKS.getString("KOTAK_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("AXIS"))
                            commonValidatorVO.setProcessorName(LZPBANKS.getString("AXIS"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("YES BANK"))
                            commonValidatorVO.setProcessorName(LZPBANKS.getString("YES_BANK"));
                        else
                            commonValidatorVO.setProcessorName(LZPBANKS.getString(commonValidatorVO.getPaymentBrand()));

                        commRequestVO=letzPayUtils.getCommRequestVO(commonValidatorVO);
                        Comm3DResponseVO comm3DResponseVO= (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                        transactionLogger.error("comm3DResponseVO.getStatus()---"+trackingid+"-->"+comm3DResponseVO.getStatus());
                        if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                        {
                            abstractPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                        }
                        directKitResponseVO.setStatus("success");
                    }
                    else if ( fromtype.equalsIgnoreCase("bhartipay"))
                    {
                        transactionLogger.error("inside bhartipay NB >> ");
                        if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BPBANKS"))
                        {
                           /* if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                            {
                                String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                                commonValidatorVO.setProcessorName(BPBANKS.getString(key));
                            }
                            else
                            {
                                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                            }*/

                        /*else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("AXIS"))
                            commonValidatorVO.setProcessorName(BPBANKS.getString("AXIS_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("ICICI"))
                            commonValidatorVO.setProcessorName(BPBANKS.getString("ICICI_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("KOTAK"))
                            commonValidatorVO.setProcessorName(BPBANKS.getString("KOTAK_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("YES BANK"))
                            commonValidatorVO.setProcessorName(BPBANKS.getString("YES_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SBI"))
                            commonValidatorVO.setProcessorName(BPBANKS.getString("SBI_BANK"));
                        else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("HDFC"))
                           commonValidatorVO.setProcessorName(BPBANKS.getString("HDFC_BANK"));*/

                            // commonValidatorVO.setProcessorName(BPBANKS.getString(commonValidatorVO.getPaymentBrand()));
                            html = pg.processAutoRedirect(commonValidatorVO);
                            transactionLogger.error("Html In netbanking  BhartiPay RestDirectManager ------" + html);
                        }
                    }
                            directKitResponseVO.setBankRedirectionUrl(html);
                            directKitResponseVO.setStatus("success");
                        }
                    }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("EWI"))
            {
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SEPWALLETS"))
                {
                    transactionLogger.error("inside sepwallets condition >>>");
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                    {
                        String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                        commonValidatorVO.setProcessorName(SEPWALLETS.getString(key));
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                    }
                    transactionLogger.error("Inside walletIndia RestDirectTransactionManager Safexpay CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In safexpay RestDirectManager ------"+html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BDWALLETS"))
                {
                    transactionLogger.error("inside BDWALLETS condition >>>>>");
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                    {
                        String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                        commonValidatorVO.setProcessorName(BDWALLETS.getString(key));
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                    }

                    transactionLogger.error("Inside walletIndia RestDirectTransactionManager billdesk CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In billdesk RestDirectManager ------"+html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("LZPWALLETS"))
                {
                    String fromtype                                 = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getGateway();
                    LetzPayUtils letzPayUtils                       = new LetzPayUtils();
                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromtype);
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentProvider()))
                    {
                        String key = commonValidatorVO.getTransDetailsVO().getPaymentProvider().replaceAll(" ", "_");
                        commonValidatorVO.setProcessorName(LZPWALLETS.getString(key));
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processAsyncDirectTransaction()", null, "Common", ErrorMessages.INVALID_PAYMENT_PROVIDER, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_PAYMENT_PROVIDER, new Throwable(ErrorMessages.INVALID_PAYMENT_PROVIDER));
                    }

                    commRequestVO                       = letzPayUtils.getCommRequestVO(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO   = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    transactionLogger.error("comm3DResponseVO.getStatus()---" + trackingid + "-->" + comm3DResponseVO.getStatus());
                    if ("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                    {
                        abstractPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                    }
                    directKitResponseVO.setStatus("success");
                }
            }
            else if(commonValidatorVO.getPaymentMode().equalsIgnoreCase("UPI"))
            {
                MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
                String accountId                    = merchantDetailsVO.getAccountId();
                String fromtype                     = GatewayAccountService.getGatewayAccount(accountId).getGateway();
                String html                         ="";
                String error                        = "";
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                FailedTransactionLogEntry failedTransactionLogEntry     = new FailedTransactionLogEntry();
                ErrorCodeUtils errorCodeUtils                           = new ErrorCodeUtils();
                TransactionHelper transactionHelper1                    = new TransactionHelper();
                AbstractPaymentProcess abstractPaymentProcess           = PaymentProcessFactory.getPaymentProcessInstance(fromtype);
                transactionLogger.error("inside Rest UPI blacklist check  ----");
                if (commonValidatorVO.getMerchantDetailsVO().getIsBlacklistTransaction().equalsIgnoreCase("Y"))
                {
                    if (functions.isValueNull(commonValidatorVO.getVpa_address())&&!transactionHelper1.isVPAaddressBlocked(commonValidatorVO.getVpa_address()))
                    {     transactionLogger.error("BlacklistTransaction vpa address-----" + commonValidatorVO.getVpa_address());
                        error = "Your VPA Address is Blocked:::Please contact support for further assistance";
                        errorCodeListVO.addListOfError(errorCodeUtils.getSystemErrorCode(ErrorName.SYS_BLOCKEDVPA));
                        //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_EMAIL
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDVPA.toString(), ErrorType.SYSCHECK.toString());
                        //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(),error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
                if (fromtype.equalsIgnoreCase("safexpay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager safexpay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In safexpay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (fromtype.equalsIgnoreCase("billdesk"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager billdesk -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In safexpay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }

                else if (fromtype.equalsIgnoreCase("bhartipay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager bhartipay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In bhartipay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (fromtype.equalsIgnoreCase("qikpay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager qikpay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In qikpay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (fromtype.equalsIgnoreCase("vervepay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager qikpay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In vervepay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (fromtype.equalsIgnoreCase("letzpay"))
                {
                   /* LetzPayUtils letzPayUtils=new LetzPayUtils();
                    commRequestVO=letzPayUtils.getCommRequestVO(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO= (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid),commRequestVO);
                    transactionLogger.error("comm3DResponseVO.getStatus()---" + trackingid + "-->" + comm3DResponseVO.getStatus());
                    if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                    {
                        abstractPaymentProcess.set3DResponseVO(directKitResponseVO,comm3DResponseVO);
                    }
                    directKitResponseVO.setStatus("success");*/
                    transactionLogger.error("Inside UPI RestDirectManager epaymentz -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In LetzPay RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else if (fromtype.equalsIgnoreCase("epaymentz"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager epaymentz -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In epaymentz RestDirectManager ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }else if (fromtype.equalsIgnoreCase("imoneypay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager imoneypay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In imoneypay RestDirectManager imoneypay ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }else if (fromtype.equalsIgnoreCase("payu"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager PayU -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In Pay RestDirectManager PayU ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }else if (fromtype.equalsIgnoreCase("payg"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager PayG -->> "+trackingid);
                    PayGUtils payGUtils = new PayGUtils();
                    commRequestVO       = payGUtils.getPayGRequestVO(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO= (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid),commRequestVO);
                    if("pending".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                    {
                        directKitResponseVO.setStatus("success");
                        directKitResponseVO.setDescription("UPI payment request is sent to Customer Wallet.");
                    }
                    transactionLogger.error("Inside UPI RestDirectManager PayU -->>");

                }else if (fromtype.equalsIgnoreCase("apexpay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager apexpay -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In apexpay RestDirectManager apexpay ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");

                }else if (fromtype.equalsIgnoreCase("qikpayv2"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager qikpayv2 -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In imoneypay RestDirectManager qikpayv2 ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");

                }
                else if (fromtype.equalsIgnoreCase("onepay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager qikpayv2 -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In imoneypay RestDirectManager qikpayv2 ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");

                }
                else if (fromtype.equalsIgnoreCase("PayaidPay"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager qikpayv2 -->>");
                    html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In imoneypay RestDirectManager qikpayv2 ------" + html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");

                }
                else if (fromtype.equalsIgnoreCase("bnmquick"))
                {
                    transactionLogger.error("Inside UPI RestDirectManager PayG -->> "+trackingid);
                   BnmQuickUtils bnmQuickUtils=new BnmQuickUtils();
                    commRequestVO       = bnmQuickUtils.getBnmQuickPaymentRequestVO(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO= (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid),commRequestVO);
                    if("pending".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                    {
                        directKitResponseVO.setStatus("success");
                        directKitResponseVO.setDescription("UPI payment request is sent to Customer Wallet.");
                    }
                    transactionLogger.error("Inside UPI RestDirectManager PayU -->>");

                }
                else if (fromtype.equalsIgnoreCase("paytm"))
                {
                    Comm3DResponseVO comm3DResponseVO       = null;
                    PayTMPaymentProcess payTMPaymentProcess = new PayTMPaymentProcess();
                    PayTMUtils payTMUtils                   = new PayTMUtils();

                    if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("UPIQR")){

                        transactionLogger.error("Inside PayTM QR >>>>> "+trackingid);

                        commRequestVO       = payTMUtils.getPayTMRequestVO(commonValidatorVO);
                        comm3DResponseVO    = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);

                        if (comm3DResponseVO != null)
                        {
                            if ("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                            {
                                payTMPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                            }
                        }
                        directKitResponseVO.setStatus("success");
                    }else{
                        transactionLogger.error("Inside UPI RestDirectManager paytm -->>");
                        html = pg.processAutoRedirect(commonValidatorVO);
                        transactionLogger.error("Html In PayTM RestDirectManager paytm ------" + html);
                        directKitResponseVO.setBankRedirectionUrl(html);
                        directKitResponseVO.setStatus("success");
                    }
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("CU"))
            {
                transactionLogger.error("Inside RestDirectTransactionManager CU PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SecurePay"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager SecurePay CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In RestDirectManager ------"+html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }


            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("FASTPAY"))
            {
                transactionLogger.error("Inside RestDirectTransactionManager FP PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("FASTPAY"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager FASTPAY CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    transactionLogger.error("Html In RestDirectManager ------"+html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("BC"))
            {

                transactionLogger.error("Inside RestDirectTransactionManager BC PayMode "+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BCPAYGATE"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager BC CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
                else  if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QKBANK"))
                {
                    Comm3DResponseVO comm3DResponseVO = null;
                    QuickPaymentsPaymentProcess quickPaymentsPaymentProcess = new QuickPaymentsPaymentProcess();
                    transactionLogger.error("Inside QKBANK QuickPayments RestDirectTransactionManager PayMode " + commonValidatorVO.getPaymentMode());
                    {
                        if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QKBANK"))
                            transactionLogger.error("Inside RestDirectTransactionManager CardType");
                        commRequestVO = QuickPaymentsUtils.getQuickPaymentsRequestVO(commonValidatorVO);
                        comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                        directKitResponseVO.setStatus("success");
                        if (comm3DResponseVO != null)
                        {
                            if ("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                            {
                                quickPaymentsPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                            }
                            directKitResponseVO.setStatus("success");
                        }
                    }
                }
                else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("BITCLEAR"))
                {
                    String html ="";
                    TransactionHelper transactionHelper     = new TransactionHelper();
                    BitClearUtils bitClearUtils             = new BitClearUtils();
                    commonValidatorVO                       = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                    GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    boolean isTest                          = gatewayAccount.isTest();
                    commRequestVO                           = bitClearUtils.getBitClearGRequestVO(commonValidatorVO);
                    BitClearPaymentProcess bitClearPaymentProcess = new BitClearPaymentProcess();
                    Comm3DResponseVO commResponseVO           = (Comm3DResponseVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

                    transactionLogger.debug("process Sale finished");
                    transactionLogger.error("isTest in BITCOIN autoredirect ---"+isTest);
                    if (commResponseVO != null && commResponseVO.getStatus().equalsIgnoreCase("pending"))
                    {
                        directKitResponseVO.setStatus("success");
                        html = bitClearPaymentProcess.get3DConfirmationForm(commonValidatorVO,commResponseVO);
                        paymentManager.updatePaymentIdForCommon(commResponseVO, commonValidatorVO.getTrackingid());
                        transactionLogger.debug("html---------------"+html);
                        directKitResponseVO.setBankRedirectionUrl(html);
                    }else if ((commResponseVO.getStatus().trim()).equalsIgnoreCase("failed") || (commResponseVO.getStatus().trim()).equalsIgnoreCase("fail"))
                    {
                        directKitResponseVO.setStatus("failed");
                        paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), commResponseVO.getRemark());
                    }else{
                        directKitResponseVO.setStatus("success");
                    }
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("TM"))
            {
                transactionLogger.error("Inside RestDirectTransactionManager TM PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("TAPMIO"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager TM CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }

            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("ZOTA"))//added by Balaji for ZotaPay
            {
                transactionLogger.error("Inside RestDirectTransactionManager ZOTA PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("ZOTA"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager ZOTA CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("BT"))//added for JP BankTransfer
            {
                if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QKBANK"))
                {
                    Comm3DResponseVO comm3DResponseVO = null;
                    QuickPaymentsPaymentProcess quickPaymentsPaymentProcess = new QuickPaymentsPaymentProcess();
                    transactionLogger.error("Inside QKBANK QuickPayments RestDirectTransactionManager PayMode " + commonValidatorVO.getPaymentMode());
                    {
                        if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QKBANK"))
                            transactionLogger.error("Inside RestDirectTransactionManager CardType");
                        commRequestVO = QuickPaymentsUtils.getQuickPaymentsRequestVO(commonValidatorVO);
                        comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                        directKitResponseVO.setStatus("success");
                        if (comm3DResponseVO != null)
                        {
                            if ("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                            {
                                quickPaymentsPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                            }
                            directKitResponseVO.setStatus("success");
                        }

                    }
                }
                else if(commonValidatorVO.getPaymentBrand().equalsIgnoreCase("Transfr"))
                {
                    Comm3DResponseVO comm3DResponseVO = null;
                    String html = "";
                    TransfrPaymentProcess transfrPaymentProcess = new TransfrPaymentProcess();
                    TransfrUtils transfrUtils = new TransfrUtils();
                    transactionLogger.error("Inside Transfr RestDirectTransactionManager PayMode " + commonValidatorVO.getPaymentMode());

                    if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("Transfr"))
                        transactionLogger.error("Inside RestDirectTransactionManager CardType");
                    commRequestVO = transfrUtils.getTransfrRequestVO(commonValidatorVO);
                    comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
//                    directKitResponseVO.setStatus("success");
                    if (comm3DResponseVO != null)
                    {
                        if ("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                        {
                            html = transfrPaymentProcess.get3DConfirmationForm(commonValidatorVO, comm3DResponseVO);
                            directKitResponseVO.setStatus("success");
                            directKitResponseVO.setBankRedirectionUrl(html);
                        }
                        else if ((comm3DResponseVO.getStatus().trim()).equalsIgnoreCase("failed") || (comm3DResponseVO.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            directKitResponseVO.setStatus("failed");
                            paymentManager.updateTransactionForCommon(comm3DResponseVO, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", comm3DResponseVO.getTransactionId(), comm3DResponseVO.getResponseTime(), comm3DResponseVO.getRemark());
                        }
                        else{
                            directKitResponseVO.setStatus("pending");
                        }
                    }
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("EFT") || commonValidatorVO.getPaymentBrand().equalsIgnoreCase("VFD"))
                {
                    transactionLogger.error("Inside RestDirectTranactionManager condition ------------------------->");
                    String html ="";
                    TransactionHelper transactionHelper     = new TransactionHelper();
                    SwiffyUtils swiffyUtils                 = new SwiffyUtils();
                    AlphapayUtils alphapayUtils             = new AlphapayUtils();
                    commonValidatorVO                       = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                    GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    Comm3DResponseVO commResponseVO         = null;
                    transactionLogger.error("Commonvalidator name >>>>>>>>>>>>>>>>>>>>>>>>>>>."+commonValidatorVO.getAddressDetailsVO().getFirstname());
                    if(gatewayAccount.getGateway().equals(AlphapayPaymentGateway.GATEWAY_TYPE))
                    {
                        commRequestVO = alphapayUtils.getAlphapayRequestVo(commonValidatorVO);
                        AlphapayPaymentProcess alphapayPaymentProcess = new AlphapayPaymentProcess();
                        String isService= commonValidatorVO.getMerchantDetailsVO().getIsService();
                        if ("N".equalsIgnoreCase(isService))
                        {
                            commResponseVO  = (Comm3DResponseVO) pg.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
                        }
                        else
                        {
                            commResponseVO = (Comm3DResponseVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
                        }
                        if (commResponseVO != null && commResponseVO.getStatus().equalsIgnoreCase("pending3DConfirmation"))
                        {
                            transactionLogger.error("Inside pending condition ------------------>");
                            alphapayPaymentProcess.set3DResponseVO(directKitResponseVO, commResponseVO);
                            paymentManager.updatePaymentIdForCommon(commResponseVO, commonValidatorVO.getTrackingid());

                            directKitResponseVO.setStatus("success");
                        }
                        else if ((commResponseVO.getStatus().trim()).equalsIgnoreCase("failed") || (commResponseVO.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            directKitResponseVO.setStatus("failed");
                            paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), commResponseVO.getRemark());
                        }
                        else
                        {
                            directKitResponseVO.setStatus("pending");
                        }
                    }
                    else if(gatewayAccount.getGateway().equals(SwiffpayPaymentGateway.GATEWAY_TYPE))
                    {
                        commRequestVO = swiffyUtils.getCommRequestFromUtils(commonValidatorVO);
                        SwiffyPaymentProcess swiffyPaymentProcess = new SwiffyPaymentProcess();
                        String isService= commonValidatorVO.getMerchantDetailsVO().getIsService();
                        if ("N".equalsIgnoreCase(isService))
                        {
                            commResponseVO  = (Comm3DResponseVO) pg.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
                        }
                        else
                        {
                            commResponseVO = (Comm3DResponseVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
                        }
                        if (commResponseVO != null && commResponseVO.getStatus().equalsIgnoreCase("pending3DConfirmation"))
                        {
                            transactionLogger.error("Inside pending condition ------------------>");
                            swiffyPaymentProcess.set3DResponseVO(directKitResponseVO, commResponseVO);
                            paymentManager.updatePaymentIdForCommon(commResponseVO, commonValidatorVO.getTrackingid());
                            directKitResponseVO.setStatus("success");
                        }
                        else if ((commResponseVO.getStatus().trim()).equalsIgnoreCase("failed") || (commResponseVO.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            directKitResponseVO.setStatus("failed");
                            paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), commResponseVO.getRemark());
                        }
                        else
                        {
                            directKitResponseVO.setStatus("pending");
                        }
                    }
                }
                else
                {

                    JPBankPaymentProcess paymentProcess = new JPBankPaymentProcess();
                    transactionLogger.error("Inside RestDirectTransactionManager BankTransfer PayMode" + commonValidatorVO.getPaymentMode());
                    JPBankTransferVO transRespDetails = null;
                    commRequestVO = JPBankTransferUtils.getCommRequestFromUtils(commonValidatorVO);

                    transRespDetails = (JPBankTransferVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
                    transactionLogger.debug("process Sale finished");

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                            directKitResponseVO.setBankRedirectionUrl(transRespDetails.getRedirectUrl());
                            directKitResponseVO.setStatus("success");
                            paymentProcess.set3DResponseVO(directKitResponseVO, transRespDetails);

                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("KCP"))
            {
                transactionLogger.error("Inside RestDirectTransactionManager KCP PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("KCP3D"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager KCP3D CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("TWD"))
            {
                Comm3DResponseVO comm3DResponseVO=null;
                transactionLogger.error("Inside RestDirectTransactionManager PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("TWD"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager CardType");
                    commRequestVO= TWDTaiwanUtils.getTaiwanRequestVO(commonValidatorVO);
                    comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    directKitResponseVO.setStatus("success");
                    if(comm3DResponseVO!=null)
                    {
                        directKitResponseVO.setBankRedirectionUrl(comm3DResponseVO.getUrlFor3DRedirect());
                        directKitResponseVO.setPaReq(comm3DResponseVO.getPaReq());
                    }
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("Gift"))
            {
                Comm3DResponseVO comm3DResponseVO=null;
                GiftPayPaymentProcess giftPayPaymentProcess=new GiftPayPaymentProcess();
                transactionLogger.error("Inside Giftpay RestDirectTransactionManager PayMode "+commonValidatorVO.getPaymentMode());
                {
                    if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("Giftpay"))
                        transactionLogger.error("Inside RestDirectTransactionManager CardType");
                    commRequestVO= GiftPayUtils.getGiftpayRequestVO(commonValidatorVO);
                    comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    directKitResponseVO.setStatus("success");
                    if(comm3DResponseVO!=null)
                    {
                        if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                        {
                            giftPayPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                        }
                        directKitResponseVO.setStatus("success");
                    }

                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("QKC"))
            {
                Comm3DResponseVO comm3DResponseVO=null;
                QuickPaymentsPaymentProcess quickPaymentsPaymentProcess=new QuickPaymentsPaymentProcess();
                transactionLogger.error("Inside QKBANK QuickPayments RestDirectTransactionManager PayMode "+commonValidatorVO.getPaymentMode());
                {
                    if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QKBANK"))
                        transactionLogger.error("Inside RestDirectTransactionManager CardType");
                    commRequestVO= QuickPaymentsUtils.getQuickPaymentsRequestVO(commonValidatorVO);
                    comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    directKitResponseVO.setStatus("success");
                    if(comm3DResponseVO!=null)
                    {
                        if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
                        {
                            quickPaymentsPaymentProcess.set3DResponseVO(directKitResponseVO, comm3DResponseVO);
                        }
                        directKitResponseVO.setStatus("success");
                    }

                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("PB"))
            {
                transactionLogger.error("Inside RestDirectTransactionManager PB PayMode"+commonValidatorVO.getPaymentMode());
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("PayBoutique"))
                {
                    transactionLogger.error("Inside RestDirectTransactionManager PayBoutique CardType");
                    String html = pg.processAutoRedirect(commonValidatorVO);
                    directKitResponseVO.setBankRedirectionUrl(html);
                    directKitResponseVO.setStatus("success");
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("EW"))
            {
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("SKRILL"))
                {
                    commRequestVO = getSkrillRequestVO(commonValidatorVO);
                    GenericResponseVO genericResponseVO = pg.processSale(String.valueOf(trackingid), commRequestVO);
                    CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                    commonValidatorVO.setCustomerId(commResponseVO.getResponseHashInfo());
                    directKitResponseVO.setStatus("success");
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("PERFECTMONEY"))
                {

                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("NETELLER"))
                {
                    NetellerUtils netellerUtils = new NetellerUtils();
                    NetellerResponse transRespDetails = null;
                    commRequestVO = netellerUtils.getNetellerRequestVO(commonValidatorVO);
                    transRespDetails = (NetellerResponse) pg.processSale(String.valueOf(trackingid), commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ("pending".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingid));
                            for(Links links : transRespDetails.getLinks())
                            {
                                if(links.getRel().equals("hosted_payment"))
                                {
                                    directKitResponseVO.setBankRedirectionUrl(links.getUrl());
                                    directKitResponseVO.setStatus("success");
                                }
                            }

                        }
                        else if ("failed".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }

                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("JETON"))
                {
                    commRequestVO = getSkrillRequestVO(commonValidatorVO);
                    GenericResponseVO genericResponseVO = pg.processSale(String.valueOf(trackingid), commRequestVO);
                    CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                    directKitResponseVO.setBankRedirectionUrl(commResponseVO.getRedirectUrl());
                    directKitResponseVO.setStatus("success");
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("EPAY"))
                {

                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("PaySend"))
                {
                    PaySendUtils paySendUtils = new PaySendUtils();
                    commRequestVO = paySendUtils.getPaySendRequestVO(commonValidatorVO);
                    GenericResponseVO genericResponseVO = pg.processSale(String.valueOf(trackingid), commRequestVO);
                    CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                    directKitResponseVO.setBankRedirectionUrl(commResponseVO.getRedirectUrl());
                    directKitResponseVO.setStatus("success");
                }
            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("CC"))
            {
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("UNICREDIT"))
                {
                    commRequestVO = unicreditUtils.getUnicreditRequestVO(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    directKitResponseVO.setBankRedirectionUrl(comm3DResponseVO.getUrlFor3DRedirect());
                    directKitResponseVO.setStatus("success");
                }
                else if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("CLEARSETTLE"))
                {
                    ClearSettleUtills clearSettleUtills=new ClearSettleUtills();
                    commRequestVO=clearSettleUtills.getClearSettleHPPRequest(commonValidatorVO);
                    Comm3DResponseVO comm3DResponseVO = (Comm3DResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    directKitResponseVO.setBankRedirectionUrl(comm3DResponseVO.getRedirectUrl());
                    directKitResponseVO.setStatus("success");
                }

            }
            else if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("DOKU"))
            {
                String html ="";
                TransactionHelper transactionHelper     = new TransactionHelper();
                DokuUtils dokuUtils                     = new DokuUtils();
                commonValidatorVO                       = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                boolean isTest                          = gatewayAccount.isTest();
                commRequestVO                           = dokuUtils.getCommRequestFromUtils(commonValidatorVO);
                CommResponseVO commResponseVO           = (CommResponseVO) pg.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
                transactionLogger.debug("process Sale finished");
                transactionLogger.error("isTest in DOKU autoredirect ---"+isTest);
                if (commResponseVO != null && commResponseVO.getStatus().equalsIgnoreCase("pending"))
                {
                    directKitResponseVO.setStatus("success");
                    html = DokuPaymentProcess.getPaymentForm(commResponseVO.getRedirectUrl(), isTest);
                    paymentManager.updatePaymentIdForCommon(commResponseVO, commonValidatorVO.getTrackingid());
                    transactionLogger.debug("html---------------"+html);
                    directKitResponseVO.setBankRedirectionUrl(html);
                }else if ((commResponseVO.getStatus().trim()).equalsIgnoreCase("failed") || (commResponseVO.getStatus().trim()).equalsIgnoreCase("fail"))
                {
                    directKitResponseVO.setStatus("failed");
                    paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), commResponseVO.getRemark());
                }else{
                    directKitResponseVO.setStatus("success");
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in RestDirectTransactionManager---", se);
        }
        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(trackingid, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        directKitResponseVO = paymentProcess.setNBResponseVO(directKitResponseVO, commonValidatorVO);

        return directKitResponseVO;
    }

    public DirectKitResponseVO processRefund(DirectRefundValidatorVO directRefundValidatorVO) throws PZDBViolationException, PZConstraintViolationException, PZTechnicalViolationException
    {
        Transaction transaction         = new Transaction();
        PaymentChecker paymentChecker   = new PaymentChecker();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        TransactionEntry transactionEntry   = new TransactionEntry();
        ActionEntry entry                           = new ActionEntry();
        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();
        MarketPlaceVO marketPlaceVO         = null;
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        RestCommonInputValidator commonInputValidator=new RestCommonInputValidator();

        String stat = "";
        String statMessage = "";
        String trackingId   = directRefundValidatorVO.getTrackingid();
        String refundAmt    = directRefundValidatorVO.getTransDetailsVO().getAmount();
        String refReason    = directRefundValidatorVO.getRefundReason();
        String marketPlaceFlag="";
        String accountId = "";
        String currency="";
        String captureAmount = "";
        String refundAmount = "";
        String transactionStatus = "";
        String refundStatus = "";
        String notificationUrl = "";

        Connection connection=null;
        try
        {
            connection          = Database.getConnection();
            Hashtable commHash  = transaction.getCaptureTransactionCommon(trackingId,directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
            if (!commHash.isEmpty())
            {
                accountId = (String) commHash.get("accountid");

                if(functions.isValueNull((String)commHash.get("notificationUrl"))){
                    notificationUrl = (String) commHash.get("notificationUrl");
                }

                if(functions.isValueNull((String)commHash.get("currency"))){
                    currency=(String) commHash.get("currency");
                }else{
                    currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                }
                if (currency.equals("JPY"))
                {
                    if (!paymentChecker.isAmountValidForJPY(currency, refundAmt))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_JPY_CURRENCY_CHECK, ErrorMessages.JPY_CURRENCY));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.JPY_CURRENCY, new Throwable(ErrorMessages.JPY_CURRENCY));
                    }
                }
                else
                {
                    if (!Functions.checkAccuracy(refundAmt, 2))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                        PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));
                    }
                }
                RefundChecker refundChecker=new RefundChecker();
                captureAmount       = (String) commHash.get("captureamount");
                refundAmount        = (String) commHash.get("refundamount");
                transactionStatus   = (String) commHash.get("status");
                if(!refundChecker.isRefundAllowed(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()))
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_REFUND_ALLOWED, ErrorMessages.REFUND_ALLOWED));
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.REFUND_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }
                int refundAllowedDays           = Integer.parseInt((String) directRefundValidatorVO.getMerchantDetailsVO().getRefundAllowedDays());
                String transactionDate          = (String) commHash.get("transactiondate");
                SimpleDateFormat targetFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long d = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                if (d > refundAllowedDays)
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REFUND_ALLOWEDDAYS_VALIDATION, ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION));
                    PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, errorCodeListVO, null, null);
                }else if (d == refundAllowedDays)
                {
                    long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
                    Date dateIni = null;
                    Date dateFin = null;
                    dateIni = format.parse(transactionDate);
                    dateFin = format.parse(targetFormat.format(new Date()));
                    long days = (dateFin.getTime() - dateIni.getTime());
                    if (days > MILLISECS_PER_DAY)
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REFUND_ALLOWEDDAYS_VALIDATION, ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION));
                        PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, errorCodeListVO, null, null);
                    }
                }
                MerchantDetailsVO merchantDetailsVO1=directRefundValidatorVO.getMerchantDetailsVO();
                if(directRefundValidatorVO.getMarketPlaceVO() != null)
                {
                    merchantDetailsVO=new MerchantDetailsVO();
                    String memberid = directRefundValidatorVO.getMarketPlaceVO().getMemberid();
                    merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(memberid);
                    directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    if(!refundChecker.isRefundAllowed(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_REFUND_ALLOWED, ErrorMessages.REFUND_ALLOWED));
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.PARENT_REFUND_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                    }
                    if("N".equalsIgnoreCase(merchantDetailsVO.getPartialRefund())&& Double.parseDouble(directRefundValidatorVO.getMarketPlaceVO().getCapturedAmount())!=Double.parseDouble(directRefundValidatorVO.getMarketPlaceVO().getRefundAmount())){
                        statMessage= "Partial Refund is not allowed for Vendor Merchant.";
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_PARTIAL_REFUND_NOT_ALLOWED, ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED));
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                    }
                    String parentStatus=directRefundValidatorVO.getMarketPlaceVO().getStatus();
                    if("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && parentStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                        statMessage= "Multiple Refund is not allowed.";
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MULTIPLE_REFUND_NOT_ALLOWED, ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED));
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                    }
                    refundAllowedDays=Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());
                    if (d > refundAllowedDays)
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REFUND_ALLOWEDDAYS_VALIDATION, ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION));
                        PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, errorCodeListVO, null, null);
                    }
                }
                marketPlaceFlag=directRefundValidatorVO.getMerchantDetailsVO().getMarketPlace();
                if(directRefundValidatorVO.getMarketPlaceVOList() != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                {
                    List <MarketPlaceVO> marketPlaceVOList=new ArrayList<>();
                    for(MarketPlaceVO marketPlaceVO1:directRefundValidatorVO.getMarketPlaceVOList())
                    {
                        merchantDetailsVO = new MerchantDetailsVO();
                        String memberid = marketPlaceVO1.getMemberid();
                        merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(memberid);
                        marketPlaceVO1.setMerchantDetailsVO(merchantDetailsVO);
                        if (!refundChecker.isRefundAllowed(merchantDetailsVO.getMemberId()))
                        {
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_REFUND_ALLOWED, ErrorMessages.REFUND_ALLOWED));
                            PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.VENDOR_REFUND_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                        }
                        if("N".equalsIgnoreCase(merchantDetailsVO.getPartialRefund())&& Double.parseDouble(marketPlaceVO1.getCapturedAmount())!=Double.parseDouble(marketPlaceVO1.getRefundAmount())){
                            statMessage= "Partial Refund is not allowed for Vendor Merchant.";
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_PARTIAL_REFUND_NOT_ALLOWED, ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED));
                            PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                        }
                        String childstatus = marketPlaceVO1.getStatus();
                        if("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && childstatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                            statMessage= "Multiple Refund is not allowed for Vendor Merchant.";
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MULTIPLE_REFUND_NOT_ALLOWED, ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED));
                            PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                        }
                        refundAllowedDays=Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());
                        transactionLogger.error("refundAllowedDays---------3-------------->"+refundAllowedDays);
                        if (d > refundAllowedDays)
                        {
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REFUND_ALLOWEDDAYS_VALIDATION, ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION));
                            PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, errorCodeListVO, null, null);
                        }
                        marketPlaceVOList.add(marketPlaceVO1);
                    }

                }

                AbstractPaymentProcess paymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));
                PZRefundRequest refundRequest           = new PZRefundRequest();
                transactionLogger.error("marketPlaceFlag------------->"+marketPlaceFlag);
                if(directRefundValidatorVO.getMarketPlaceVO() != null && !"N".equalsIgnoreCase(marketPlaceFlag))//when child trackingid pass in request
                {
                    marketPlaceVO=directRefundValidatorVO.getMarketPlaceVO();
                    refundRequest.setTrackingId(Integer.valueOf(marketPlaceVO.getTrackingid()));
                    if(functions.isValueNull(marketPlaceVO.getReversedAmount()))
                        refundRequest.setReversedAmount(marketPlaceVO.getReversedAmount());
                    if(functions.isValueNull(marketPlaceVO.getCapturedAmount()))
                        refundRequest.setCaptureAmount(marketPlaceVO.getCapturedAmount());

                    refundRequest.setRequestedTrackingid(Integer.parseInt(directRefundValidatorVO.getTrackingid()));
                    refundRequest.setRequestedReversedAmount(refundAmount);
                    refundRequest.setRequestedCaptureAmount(captureAmount);
                }
                else
                {
                    refundRequest.setTrackingId(Integer.valueOf(trackingId));
                    refundRequest.setReversedAmount(refundAmount);
                    refundRequest.setCaptureAmount(captureAmount);
                    if(directRefundValidatorVO.getMarketPlaceVOList() != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        refundRequest.setChildDetailsList(directRefundValidatorVO.getMarketPlaceVOList());
                    }
                }
                refundRequest.setMarketPlaceFlag(marketPlaceFlag);
                refundRequest.setAccountId(Integer.valueOf(accountId));
                refundRequest.setMemberId(Integer.valueOf(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()));
                refundRequest.setRefundAmount(refundAmt);
                refundRequest.setCurrency(currency);
                refundRequest.setNotificationURL(notificationUrl);
                refundRequest.setRefundReason("-");
                refundRequest.setTransactionStatus(transactionStatus);
                refundRequest.setIpAddress(directRefundValidatorVO.getMerchantIpAddress());
                auditTrailVO.setActionExecutorId(directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
                auditTrailVO.setActionExecutorName("REST Reverse");
                refundRequest.setAuditTrailVO(auditTrailVO);
                transactionLogger.error("merchantDetailsVO1.getPartialRefund()--->" + merchantDetailsVO1.getPartialRefund());
                transactionLogger.error("refundRequest.getCaptureAmount()--->"+refundRequest.getCaptureAmount());
                transactionLogger.error("refundRequest.getRefundAmount()--->"+refundRequest.getRefundAmount());
                if("N".equalsIgnoreCase(merchantDetailsVO1.getPartialRefund()) && Double.parseDouble(refundRequest.getCaptureAmount())!=Double.parseDouble(refundRequest.getRefundAmount())){
                    statMessage= "Partial Refund is not allowed.";
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_PARTIAL_REFUND_NOT_ALLOWED, ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED));
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.PARTIAL_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                }
                else if("N".equalsIgnoreCase(merchantDetailsVO1.getMultipleRefund()) && transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                    statMessage= "Multiple Refund is not allowed.";
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MULTIPLE_REFUND_NOT_ALLOWED, ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED));
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null,"Transaction", ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);

                }else{
                    if(directRefundValidatorVO.getMarketPlaceVO() != null && !"N".equalsIgnoreCase(marketPlaceFlag))//when child trackingid pass in request
                    {
                        String parentStatus=marketPlaceVO.getStatus();
                        if ("N".equalsIgnoreCase(directRefundValidatorVO.getMerchantDetailsVO().getMultipleRefund()) && parentStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
                        {
                            statMessage = "Multiple Refund is not allowed for Parent Merchant.";
                            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MULTIPLE_REFUND_NOT_ALLOWED, ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED));
                            PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.MULTPLE_REFUND_NOT_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);

                        }
                    }
                    PZRefundResponse refundResponse = paymentProcess.refund(refundRequest);
                    PZResponseStatus responseStatus = refundResponse.getStatus();
                    String refundDescription = refundResponse.getResponseDesceiption();

                    if (PZResponseStatus.SUCCESS.equals(responseStatus))
                    {
                        stat = "Y";
                        statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed");
                        refundStatus = PZResponseStatus.SUCCESS.toString();
                        if (Double.parseDouble(refundAmt) < Double.parseDouble(captureAmount))
                        {
                            refundStatus = PZResponseStatus.PARTIALREFUND.toString();
                            refundResponse.setStatus(PZResponseStatus.PARTIALREFUND);
                        }

                    }
                    else
                    {
                        stat = "N";
                    }
                    String reason="";
                    statMessage = refundDescription;
                    if ("Y".equalsIgnoreCase(merchantDetailsVO1.getIsRefundEmailSent()))
                    {
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, trackingId, responseStatus.toString(), reason, null);
                    }
                    if (directRefundValidatorVO.getMarketPlaceVO() != null && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        marketPlaceVO=directRefundValidatorVO.getMarketPlaceVO();
                        merchantDetailsVO=directRefundValidatorVO.getMerchantDetailsVO();
                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getIsRefundEmailSent()))
                        {
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, marketPlaceVO.getTrackingid(), responseStatus.toString(), reason, null);
                        }
                    }
                    if(!"N".equalsIgnoreCase(marketPlaceFlag) && (directRefundValidatorVO.getMarketPlaceVOList() != null && directRefundValidatorVO.getMarketPlaceVOList().size()>0))
                    {
                        for(MarketPlaceVO marketPlaceVO1:directRefundValidatorVO.getMarketPlaceVOList())
                        {
                            merchantDetailsVO1 = marketPlaceVO1.getMerchantDetailsVO();
                            logger.error("marketPlaceVO.getTrackingid()---------->"+marketPlaceVO1.getTrackingid());
                            if ("Y".equalsIgnoreCase(merchantDetailsVO1.getIsRefundEmailSent()))
                            {
                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, marketPlaceVO1.getTrackingid(), responseStatus.toString(), reason, null);
                            }
                        }
                    }
                }
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", "System error while refunding transaction", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", "Parsing error while refunding transaction", PZTechnicalExceptionEnum.DATE_PARSING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        transactionUtil.setRefundSystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directRefundValidatorVO, "Y".equals(stat) ? PZResponseStatus.SUCCESS : PZResponseStatus.FAILED, statMessage);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processCapture(DirectCaptureValidatorVO directCaptureValidatorVO) throws PZGenericConstraintViolationException
    {

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        Transaction transaction                 = new Transaction();
        PaymentChecker paymentChecker           = new PaymentChecker();
        PZCaptureResponse captureResponse       = null;
        TransactionHelper transactionHelper     = new TransactionHelper();

        String memberid         = directCaptureValidatorVO.getMerchantDetailsVO().getMemberId();
        String captureAmt       = directCaptureValidatorVO.getTransDetailsVO().getAmount();
        String trackingId       = directCaptureValidatorVO.getTrackingid();
        String stat             = "";
        String statMessage      = "";
        String mailtransactionStatus = "";
        String currency="";
        String notificationUrl="";

        Hashtable authTransactionHash = transaction.getAuthTransactionCommon(memberid,trackingId);
        if(authTransactionHash==null || authTransactionHash.size()==0)
        {
            String error1="Transaction not found for tracking ID-"+trackingId;
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDTRANSACTION_DATA, error1));
            PZExceptionHandler.raiseGenericViolationException(RestDirectTransactionManager.class.getName(), "processCancel()", null, "Common", error1, errorCodeListVO, null, null);
        }
        String amount       = (String) authTransactionHash.get("amount");
        String accountId    = (String) authTransactionHash.get("accountid");
        notificationUrl    = (String) authTransactionHash.get("notificationUrl");

        if(functions.isValueNull((String)authTransactionHash.get("currency"))){
            currency    = (String)authTransactionHash.get("currency");
        }else{
            currency    = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
        }


        if(currency.equals("JPY"))
        {
            if(!paymentChecker.isAmountValidForJPY(currency,captureAmt))
            {
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_JPY_CURRENCY_CHECK, ErrorMessages.JPY_CURRENCY));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.JPY_CURRENCY, new Throwable(ErrorMessages.JPY_CURRENCY));
            }
        }
        else
        {
            if (!Functions.checkAccuracy(captureAmt, 2))
            {
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));
            }
        }

        String isExcessCaptureAllowed = "";
        isExcessCaptureAllowed          = (String) authTransactionHash.get("isExcessCaptureAllowed");
        boolean isOverCaptureRequest    = isOverCaptureRequest(captureAmt,amount);
        boolean isValidCaptureAmount    = true;
        boolean isOverCaptureAllowed    = false;
        boolean isValidCaptureRequest   = true;
        AuditTrailVO auditTrailVO       = new AuditTrailVO();

        if("Y".equals(isExcessCaptureAllowed))
        {
            isOverCaptureAllowed=true;
        }
        else
        {
            errorCodeListVO = transactionHelper.getReferencedCaptureCancelRefundTransDetails(directCaptureValidatorVO);
            if (errorCodeListVO != null)
            {
                directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(isOverCaptureRequest)
        {
            if(isOverCaptureAllowed)
            {
                GatewayType gatewayType         = GatewayTypeService.getGatewayType(GatewayAccountService.getGatewayAccount(accountId).getPgTypeId());
                String overCapturePercentage    = gatewayType.getExcessCapturePercentage();
                double validOverCaptureAmount   = (Double.valueOf(amount)*Double.valueOf(overCapturePercentage))/100;
                if(Double.valueOf(captureAmt) > validOverCaptureAmount)
                {
                    isValidCaptureAmount    = false;
                }
            }
            else
            {
                isValidCaptureRequest   = false;
            }
        }

        if (isValidCaptureRequest && isValidCaptureAmount)
        {
            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));

            PZCaptureRequest captureRequest = new PZCaptureRequest();
            captureRequest.setAccountId(Integer.valueOf(accountId));
            captureRequest.setMemberId(Integer.valueOf(memberid));
            captureRequest.setTrackingId(Integer.valueOf(trackingId));
            captureRequest.setAmount(Double.valueOf(captureAmt));
            captureRequest.setCurrency(currency);
            captureRequest.setNotificationUrl(notificationUrl);
            captureRequest.setIpAddress(directCaptureValidatorVO.getAddressDetailsVO().getIp());
            captureRequest.setPod("Capture Transaction");
            auditTrailVO.setActionExecutorId(memberid);
            auditTrailVO.setActionExecutorName("REST API");
            captureRequest.setAuditTrailVO(auditTrailVO);

            captureResponse                     = paymentProcess.capture(captureRequest);
            PZResponseStatus responseStatus     = captureResponse.getStatus();
            String captureDescription           = captureResponse.getResponseDesceiption();

            if (PZResponseStatus.SUCCESS.equals(responseStatus))
            {
                stat                    = "Y";
                mailtransactionStatus   = "successful";
            }
            else
            {
                stat                    = "N";
                mailtransactionStatus   = "failed";
            }
            statMessage = captureDescription;
        }
        else
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_EXCESS_CAPTURE_AMOUNT, ErrorMessages.INVALID_EXCESS_CAPTURE_AMOUNT));
            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_EXCESS_CAPTURE_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));

        }
        transactionUtil.setCaptureSystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directCaptureValidatorVO, stat, statMessage);
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,null);

        return directKitResponseVO;
    }

    public DirectKitResponseVO processCancel(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException,PZGenericConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Merchants merchants         = new Merchants();
        Transaction transaction     = new Transaction();

        String description = "";
        String trackingid = "";
        String memberid = "";
        String key = "";
        String checksumAlgorithm = "";
        String activation = "";
        String partnerId = "";
        String accountId = "";
        String terminalid = "";
        String transactionStatus = "";
        String fromtype = "";
        String status = "";
        String statusMsg = "";
        String mailtransactionStatus = "";
        String amount = "";
        String notificationUrl = "";
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        Hashtable data=new Hashtable();

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        PZCancelRequest cancelRequest   = new PZCancelRequest();
        PZCancelResponse cancelResponse = new PZCancelResponse();

        trackingid  = commonValidatorVO.getTrackingid();
        memberid    = commonValidatorVO.getMerchantDetailsVO().getMemberId();

        Hashtable merchantHash = merchants.getMemberDetailsForTransaction(memberid);
        if (!merchantHash.isEmpty())
        {
            key                 = (String) merchantHash.get("clkey");
            checksumAlgorithm   = (String) merchantHash.get("checksumalgo");
            activation          = (String) merchantHash.get("activation");
            partnerId           = (String)merchantHash.get("partnerId");
        }
        else
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_TOID, ErrorMessages.INVALID_TOID));
            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processCancel()", null, "Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_TOID, new Throwable(ErrorMessages.INVALID_TOID));
        }

        data = transaction.getTransactionDetailsFormCommonForCancel(trackingid, memberid);
        if(data==null || data.size()==0)
        {
            String error1="Transaction not found for tracking ID-"+trackingid+" and description-"+description;
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDTRANSACTION_DATA, error1));
            PZExceptionHandler.raiseGenericViolationException(RestDirectTransactionManager.class.getName(), "processCancel()", null, "Common", error1, errorCodeListVO, null, null);
        }
        accountId   = (String)data.get("accountid");
        terminalid  = (String)data.get("terminalid");
        amount      = (String)data.get("amount");

        if(data.get("notificationUrl") != null){
            notificationUrl      = (String)data.get("notificationUrl");
        }

        transactionStatus= (String)data.get("status");
        if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
        {
            String error1 = "Captured transaction can not be cancel for trackingid "+trackingid;
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_VOID_TRANSACTION_NOTALLOWED, error1));
            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processCancel()", null, "Common", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error1, new Throwable(error1));
        }
        fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        if (Functions.checkAPIGateways(fromtype))
        {
            cancelRequest = new PZCancelRequest();
            cancelRequest.setMemberId(Integer.parseInt(memberid));
            cancelRequest.setAccountId(Integer.parseInt(accountId));
            cancelRequest.setTrackingId(Integer.parseInt(trackingid));
            cancelRequest.setTerminalId(terminalid);
            cancelRequest.setAmount(amount);
            cancelRequest.setNotificationURL(notificationUrl);
            cancelRequest.setCancelReason("Cancel Transaction " + trackingid);
            cancelRequest.setIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
            auditTrailVO.setActionExecutorId(memberid);
            auditTrailVO.setActionExecutorName("REST API");
            cancelRequest.setAuditTrailVO(auditTrailVO);
            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));

            if (fromtype != null && com.payment.sbm.core.SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype) && PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
            {
                cancelResponse = paymentProcess.cancelCapture(cancelRequest);
            }
            else
            {
                cancelResponse = paymentProcess.cancel(cancelRequest);
            }

            PZResponseStatus status1 = cancelResponse.getStatus();
            if (PZResponseStatus.ERROR.equals(status1))
            {
                statusMsg = "Error while Cancel Transaction";
                status = "N";
                mailtransactionStatus = "Error while Cancel Transaction";
            }
            else if (PZResponseStatus.FAILED.equals(status1))
            {
                statusMsg = "Transaction failed while cancel";
                status = "N";
                mailtransactionStatus = "Transaction failed while cancel";
            }
            else if (PZResponseStatus.SUCCESS.equals(status1))
            {
                statusMsg = "Cancel Transaction Process is successful";
                status = "Y";
                mailtransactionStatus = "Cancel Transaction Process is successful";
            }
            else if (PZResponseStatus.PENDING.equals(status1))
            {
                statusMsg = cancelResponse.getResponseDesceiption();
                status = "N";
                mailtransactionStatus = "pending";
            }
        }
        transactionUtil.setCancelSystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, commonValidatorVO, status, statusMsg);
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null,null);

        return directKitResponseVO;
    }

    public DirectKitResponseVO processInquiry(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        GenericCardDetailsVO cardDetailsVO      = commonValidatorVO.getCardDetailsVO();

        Hashtable data                          = new Hashtable();
        Transaction transaction                 = new Transaction();
        TransactionManager transactionManager   = new TransactionManager();
        String accountId            = null;
        AbstractPaymentGateway pg   = null;
        String fromtype             = null;

        /*data = transaction.getQwipiTransDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderDesc());
        accountId = (String) data.get("accountid");

        if (accountId == null)
        {
            data = transaction.getTransactionDetailsForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderId());
            accountId = (String) data.get("accountid");
        }

        if (accountId == null)
        {
            directKitResponseVO = transactionUtil.setInquirySystemResponseAndErrorCodeListVOForRest(errorCodeListVO, null, commonValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found");
            return directKitResponseVO;
        }
        fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();*/

        //select payment gateway for payment start
        /*if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
        {
            Hashtable dataHash = new Hashtable();

            dataHash = transaction.getTransactionDetails(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), "transaction_qwipi");
            if (!dataHash.equals("") && dataHash != null)
            {
                directKitResponseVO = transactionUtil.setInquirySystemResponseAndErrorCodeListVO(errorCodeListVO,null,commonValidatorVO,(String)dataHash.get("status"),"Your record is found");
            }
            else
            {
                directKitResponseVO = transactionUtil.setInquirySystemResponseAndErrorCodeListVO(errorCodeListVO, null, commonValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found");
            }
        }*/

        /*else if (Functions.checkInquiryAPIGateways(fromtype))
        {*/
        Hashtable dataHash = null;

        Date date1 = new Date();
        transactionLogger.error("getTransactionDetails DataHash Start time 1#######" + date1.getTime());

        dataHash = transaction.getTransactionDetails(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderId(), "transaction_common");

        transactionLogger.error("getTransactionDetails DataHash end time 1 ######" + new Date().getTime());
        transactionLogger.error("getTransactionDetails DataHash diff time 1 ######" + (new Date().getTime() - date1.getTime()));
        if (null == dataHash || dataHash.size() == 0)
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REJECTED_RECORD_NOT_FOUND, ErrorMessages.INVALID_TRANSACTION_DATA));
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processInquiry()", null, "Common", ErrorMessages.INVALID_TRACKINGID_MEMBERID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_TRACKINGID_MEMBERID, new Throwable(ErrorMessages.INVALID_TRACKINGID_MEMBERID));
        }
        if(functions.isValueNull(dataHash.get("ccnum").toString()))
        {
            cardDetailsVO.setCardNum(PzEncryptor.decryptPAN(dataHash.get("ccnum").toString()));
            String expiryDate[] = PzEncryptor.decryptExpiryDate(dataHash.get("expdate").toString()).split("/");
            cardDetailsVO.setExpMonth(expiryDate[0]);
            cardDetailsVO.setExpYear(expiryDate[1]);
            cardDetailsVO.setCardHolderName(dataHash.get("name").toString());
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
        }

        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()) && !dataHash.get("toid").equals(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDTRANSACTION_DATA, ErrorMessages.INVALID_TRANSACTION_DATA));
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processInquiry()", null, "Common", ErrorMessages.INVALID_TRACKINGID_MEMBERID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_TRACKINGID_MEMBERID, new Throwable(ErrorMessages.INVALID_TRACKINGID_MEMBERID));
        }
        String updateStatus     = (String) dataHash.get("status") ;
        String remark           = (String) dataHash.get("remark");
        String displayname      = "";
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        accountId                           = (String) dataHash.get("accountid");
        String trackingid                   = (String) dataHash.get("trackingid");
        String toid             = (String) dataHash.get("toid");
        String notificationUrl  = (String) dataHash.get("notificationUrl");
        merchantDetailsVO       = merchantDAO.getMemberDetails(toid);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String fromType         = String.valueOf(dataHash.get("fromtype"));
        transactionLogger.error("fromtype----->"+dataHash.get("fromtype"));
        transactionLogger.error("status----->"+dataHash.get("status"));

      /*  if(dataHash.get("fromtype")!=null&&SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase((String) dataHash.get("fromtype"))&&("a".equalsIgnoreCase((String) dataHash.get("status"))))
        {
            CommResponseVO commResponseVO                       = null;
            CommRequestVO commRequestVO                         = new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
            CommMerchantVO commMerchantVO                       = new CommMerchantVO();
            CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();
            commTransactionDetailsVO.setOrderId(trackingid);
            commMerchantVO.setAccountId(accountId);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
            commRequestVO.setCommMerchantVO(commMerchantVO);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
            PaymentDAO paymentDAO           = new PaymentDAO();


            try
            {
                pg              = AbstractPaymentGateway.getGateway(accountId);
                commResponseVO  = (CommResponseVO) pg.processInquiry(commRequestVO);
                if (commResponseVO == null)
                {
                    commResponseVO = (CommResponseVO) pg.processQuery(trackingid, commRequestVO);
                }
                ActionEntry entry           = new ActionEntry();
                AuditTrailVO auditTrailVO   = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("RestInquiry");
                auditTrailVO.setActionExecutorId((String) dataHash.get("toid"));

                //update new status
                String transactionid        = commResponseVO.getTransactionId();
                boolean isStatusChnged      = false;
                String transactionStatus    = commResponseVO.getTransactionStatus();
                String captureamount        = commResponseVO.getAmount();
                remark                      = commResponseVO.getRemark();
                if ("success".equalsIgnoreCase(transactionStatus)&&("authstarted".equalsIgnoreCase((String) dataHash.get("status"))||"authfailed".equalsIgnoreCase((String) dataHash.get("status"))))
                {
                    displayname=gatewayAccount.getDisplayName();
                    commResponseVO.setDescriptor(displayname);
                    updateStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    transactionLogger.error("inside success transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingid);
                    transactionLogger.error("captureamount--------------->" + captureamount);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, captureamount, "", transactionid, remark, "", trackingid);
                    entry.actionEntryForCommon(trackingid, captureamount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, updateStatus);
                    isStatusChnged = true;
                }
                else if (("fail".equalsIgnoreCase(transactionStatus) || "failed".equalsIgnoreCase(transactionStatus))&&"authstarted".equalsIgnoreCase((String) dataHash.get("status")))
                {
                    updateStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    transactionLogger.error("inside fail transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingid);
                    transactionLogger.error("captureamount--------------->" + captureamount);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, captureamount, "", transactionid, remark, "", trackingid);
                    entry.actionEntryForCommon(trackingid, captureamount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, updateStatus);
                    isStatusChnged = true;
                }
                transactionLogger.error("isStatusChnged-->"+isStatusChnged);
                transactionLogger.error("isStatusChnged-->"+isStatusChnged);

            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--->",e);
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--->", e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("systemError--->", systemError);
            }

        }*/
        /*if (SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase((String) dataHash.get("fromtype"))&&!"authstarted".equalsIgnoreCase(updateStatus)&&"Y".equalsIgnoreCase(merchantDetailsVO.getReconciliationNotification()) && functions.isValueNull(notificationUrl))
        {
            if("capturesuccess".equalsIgnoreCase(updateStatus)){
                displayname = gatewayAccount.getDisplayName();
            }
            transactionLogger.error("---In side sending notification--");
            TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(trackingid);
            AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
            transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
            transactionDetailsVO.setBillingDesc(displayname);
            if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            if(functions.isValueNull(transactionDetailsVO.getExpdate()))
                transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
            asyncNotificationService.sendNotification(transactionDetailsVO, trackingid, updateStatus, remark);
        }*/

        if(functions.isValueNull((String)dataHash.get("orderdescription")))
            commonValidatorVO.getTransDetailsVO().setOrderDesc(dataHash.get("orderdescription").toString());
        commonValidatorVO.getTransDetailsVO().setAmount(dataHash.get("amount").toString());

        if (!dataHash.equals("") && dataHash != null)
        {
            if (dataHash.get("cardtypeid") != null)
                commonValidatorVO.setPaymentBrand(transaction.getPaymentBrandForRest(dataHash.get("cardtypeid").toString()));
            if (dataHash.get("paymodeid") != null)
                commonValidatorVO.setPaymentMode(transaction.getPaymentModeForRest(dataHash.get("paymodeid").toString()));

            if ((functions.isValueNull(fromType) && ("Flexepin".equalsIgnoreCase(fromType) || "infipay".equalsIgnoreCase(fromType))) && Double.parseDouble((String) dataHash.get("captureamount")) > 0)
            {
                commonValidatorVO.getTransDetailsVO().setAmount(String.valueOf(dataHash.get("captureamount")));
            }
            else
            {
                commonValidatorVO.getTransDetailsVO().setAmount(dataHash.get("amount").toString());
            }

            if(functions.isValueNull((String) dataHash.get("refundamount")))
            {
                commonValidatorVO.getTransDetailsVO().setRefundAmount((String) dataHash.get("refundamount"));
            }
            if (functions.isValueNull((String) dataHash.get("payoutamount")))
            {
                commonValidatorVO.getTransDetailsVO().setPayoutAmount((String)dataHash.get("payoutamount"));
            }
            commonValidatorVO.getTransDetailsVO().setCurrency(dataHash.get("currency").toString());
            commonValidatorVO.getTransDetailsVO().setOrderId(dataHash.get("description").toString());
            commonValidatorVO.getAddressDetailsVO().setTmpl_amount(dataHash.get("templateamount").toString());
            commonValidatorVO.getAddressDetailsVO().setTmpl_currency(dataHash.get("templatecurrency").toString());
            commonValidatorVO.getAddressDetailsVO().setEmail("");//setting blank email as mercahnt email set in the VO
            if(functions.isValueNull((String) dataHash.get("emailaddr")))
                commonValidatorVO.getAddressDetailsVO().setEmail((String) dataHash.get("emailaddr"));
            if(functions.isValueNull((String) dataHash.get("firstname")))
                commonValidatorVO.getAddressDetailsVO().setFirstname((String) dataHash.get("firstname"));
            if(functions.isValueNull((String) dataHash.get("lastname")))
                commonValidatorVO.getAddressDetailsVO().setLastname((String) dataHash.get("lastname"));
            if(functions.isValueNull((String) dataHash.get("responsedescriptor")))
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor((String) dataHash.get("responsedescriptor"));
            if(functions.isValueNull((String) dataHash.get("eci")))
                commonValidatorVO.setEci((String) dataHash.get("eci"));
            if(functions.isValueNull((String) dataHash.get("errorName")))
                commonValidatorVO.setErrorName((String) dataHash.get("errorName"));

            if(functions.isValueNull((String) dataHash.get("toid")))
                commonValidatorVO.getMerchantDetailsVO().setMemberId((String) dataHash.get("toid"));
            if(functions.isValueNull((String) dataHash.get("customerId")))
                commonValidatorVO.setCustomerId((String) dataHash.get("customerId"));
                directKitResponseVO.setCustId((String) dataHash.get("customerId"));

            commonValidatorVO.setTrackingid(dataHash.get("trackingid").toString());
           /* List<MarketPlaceVO> marketPlaceVOList=transactionManager.getChildDetailsByParentTrackingid(commonValidatorVO.getTrackingid());
            commonValidatorVO.setMarketPlaceVOList(marketPlaceVOList);*/

            //CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
            Date date2 = new Date();
            transactionLogger.error("getExtentionDetails Start time 2#######" + date2.getTime());

            if(functions.isValueNull((String) dataHash.get("accountid")))
            {
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(dataHash.get("accountid").toString()));
                commonValidatorVO = paymentProcess.getExtentionDetails(commonValidatorVO);
            }
            transactionLogger.error("getExtentionDetails end time 2 ######"+new Date().getTime());
            transactionLogger.error("getExtentionDetails diff time 2 ######"+(new Date().getTime()-date2.getTime()));

            Date date3 = new Date();
            transactionLogger.error("setInquirySystemResponseAndErrorCodeListVOForRest Start time 3#######" + date3.getTime());

            directKitResponseVO = transactionUtil.setInquirySystemResponseAndErrorCodeListVOForRest(errorCodeListVO, null, commonValidatorVO, updateStatus, remark, "Your record is found");


            transactionLogger.error("setInquirySystemResponseAndErrorCodeListVOForRest end time 3 ######"+new Date().getTime());
            transactionLogger.error("setInquirySystemResponseAndErrorCodeListVOForRest diff time 3 ######"+(new Date().getTime()-date3.getTime()));

        }
        else
        {
            directKitResponseVO = transactionUtil.setInquirySystemResponseAndErrorCodeListVOForRest(errorCodeListVO, null, commonValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found");
        }

        return directKitResponseVO;
    }

    public DirectKitResponseVO processDeleteRegisteredToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Inside processDeleteToken :::");

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        TokenManager tokenManager=new TokenManager();
        String status = "";
        Date date3 = new Date();


        if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
            status = tokenManager.doMerchantRegisteredTokenInactive(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getToken());
        else if("N".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
            status = tokenManager.doPartnerRegisteredTokenInactive(commonValidatorVO.getPartnerDetailsVO().getPartnerId(), commonValidatorVO.getToken());

        directKitResponseVO.setStatus(status);

        return directKitResponseVO;
    }

    private QwipiRequestVO getQRequestVO(CommonValidatorVO commonValidatorVO)throws PZTechnicalViolationException
    {
        QwipiRequestVO RequestDetail=null;
        GenericCardDetailsVO cardDetail= commonValidatorVO.getCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        Transaction transaction=new Transaction();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String tredtime=dateFormat.format(calendar.getTime()) ;
        String MD5key=null;
        String isKsnUrlFlag=null;
        Hashtable midHash = null;

        String MD5INFO= null;
        try
        {
            midHash = transaction.getMidKeyForQwipi(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            if(!midHash.isEmpty())
            {
                MD5key = (String) midHash.get("midkey");
                isKsnUrlFlag= (String) midHash.get("isksnurlflag");
            }
            MD5INFO = Functions.convertmd5(commonValidatorVO.getTransDetailsVO().getFromid().trim()+commonValidatorVO.getTransDetailsVO().getOrderId().trim()+commonValidatorVO.getTransDetailsVO().getCurrency().trim()+commonValidatorVO.getTransDetailsVO().getAmount().trim()+tredtime.trim()+MD5key.trim());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RestDirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", "Internal error while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(RestDirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", "Internal error while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

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

        RequestDetail = new QwipiRequestVO(cardDetail,AddressDetail, TransDetail);
        RequestDetail.setKsnUrlFlag(isKsnUrlFlag);
        RequestDetail.setMd5Key(MD5key);
        RequestDetail.setMiddleName("");
        return RequestDetail;
    }

    private CommRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO,CommTransactionDetailsVO transDetailsVO) throws PZDBViolationException
    {
        CommRequestVO commRequestVO             = null;
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        RecurringBillingVO recurringBillingVO   = commonValidatorVO.getRecurringBillingVO();
        GatewayAccount account                  = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId          = account.getMerchantId();
        String alias            = account.getAliasName();
        String username         = account.getFRAUD_FTP_USERNAME();
        String password         = account.getFRAUD_FTP_PASSWORD();
        String displayname      = account.getDisplayName();
        ReserveField2VO reserveField2VO             = new ReserveField2VO();
        CommAccountInfoVO commAccountInfoVO         = new CommAccountInfoVO();
        CommDeviceDetailsVO commDeviceDetailsVO     = new CommDeviceDetailsVO();

        if(functions.isValueNull(commonValidatorVO.getEci()) && functions.isValueNull(commonValidatorVO.getXid()) && functions.isValueNull(commonValidatorVO.getVerificationId()))
        {
            transDetailsVO.setEci(commonValidatorVO.getEci());
            transDetailsVO.setXid(commonValidatorVO.getXid());
            transDetailsVO.setVerificationId(commonValidatorVO.getVerificationId());
        }

        if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
        {
            cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
            cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
            cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
            cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
            cardDetailsVO.setCardType(Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
            transactionLogger.error("A2 country---"+commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            cardDetailsVO.setCountry_code_A2(commonValidatorVO.getCardDetailsVO().getCountry_code_A2());

        }
        else if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
        {
            cardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
            cardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());
            addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
            cardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());
            cardDetailsVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname()+ " " +commonValidatorVO.getAddressDetailsVO().getLastname());
        }
        else if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getAccountNumber()))
        {
            reserveField2VO.setAccountType(commonValidatorVO.getCardDetailsVO().getAccountType());
            reserveField2VO.setAccountNumber(commonValidatorVO.getCardDetailsVO().getAccountNumber());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getCardDetailsVO().getRoutingNumber());
        }
        else if(commonValidatorVO.getReserveField2VO()!=null)
        {
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setCheckNumber(commonValidatorVO.getReserveField2VO().getCheckNumber());
        }

        logger.error("firsname:::::::::::"+ commonValidatorVO.getAddressDetailsVO().getFirstname());
        System.out.println("firsname:::::::::::"+ commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        logger.error("firsname:::::::::::"+ addressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setToId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        transDetailsVO.setTotype(commonValidatorVO.getTransDetailsVO().getTotype());

        transactionLogger.debug("paymentType------"+transDetailsVO.getPaymentType());
        transactionLogger.debug("cardType------"+transDetailsVO.getCardType());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
        merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        merchantAccountVO.setCountry(commonValidatorVO.getMerchantDetailsVO().getCountry());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        merchantAccountVO.setZipCode(commonValidatorVO.getMerchantDetailsVO().getZip());
        //  merchantAccountVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantTelNo = commonValidatorVO.getMerchantDetailsVO().getTelNo();
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
        if (functions.isValueNull(mPhone))
            merchantAccountVO.setMerchantSupportNumber(mPhone);
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMerchantLogo()) && commonValidatorVO.getMerchantDetailsVO().getMerchantLogo().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag()) && commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        String partnerTelNo = commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber();
        String pPhone = "";
        if (functions.isValueNull(partnerTelNo) && partnerTelNo.contains("-"))
        {
            String[] phone = partnerTelNo.split("-");
            pPhone = phone[1];
        }
        else
        {
            pPhone = partnerTelNo;
        }
        if (functions.isValueNull(pPhone))
            merchantAccountVO.setPartnerSupportContactNumber(pPhone);
        if (commonValidatorVO.getAccountInfoVO() !=null)
        {
            if (functions.isValueNull(commonValidatorVO.getAccountInfoVO().getAccChangeDate()))
                commAccountInfoVO.setAccChangeDate(commonValidatorVO.getAccountInfoVO().getAccChangeDate());
            if (functions.isValueNull(commonValidatorVO.getAccountInfoVO().getAccActivationDate()))
                commAccountInfoVO.setAccActivationDate(commonValidatorVO.getAccountInfoVO().getAccActivationDate());
            if (functions.isValueNull(commonValidatorVO.getAccountInfoVO().getAccPwChangeDate()))
                commAccountInfoVO.setAccPwChangeDate(commonValidatorVO.getAccountInfoVO().getAccPwChangeDate());
            if (functions.isValueNull(commonValidatorVO.getAccountInfoVO().getAddressUseDate()))
                commAccountInfoVO.setAddressUseDate(commonValidatorVO.getAccountInfoVO().getAddressUseDate());
            if (functions.isValueNull(commonValidatorVO.getAccountInfoVO().getPaymentAccActivationDate()))
                commAccountInfoVO.setPaymentAccActivationDate(commonValidatorVO.getAccountInfoVO().getPaymentAccActivationDate());
        }
        if(commonValidatorVO.getDeviceDetailsVO()!=null)
        {
            commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
            commDeviceDetailsVO.setAcceptHeader(commonValidatorVO.getDeviceDetailsVO().getAcceptHeader());
            commDeviceDetailsVO.setBrowserColorDepth(commonValidatorVO.getDeviceDetailsVO().getBrowserColorDepth());
            commDeviceDetailsVO.setBrowserLanguage(commonValidatorVO.getDeviceDetailsVO().getBrowserLanguage());
            commDeviceDetailsVO.setBrowserTimezoneOffset(commonValidatorVO.getDeviceDetailsVO().getBrowserTimezoneOffset());
            commDeviceDetailsVO.setBrowserScreenHeight(commonValidatorVO.getDeviceDetailsVO().getBrowserScreenHeight());
            commDeviceDetailsVO.setBrowserScreenWidth(commonValidatorVO.getDeviceDetailsVO().getBrowserScreenWidth());
            commDeviceDetailsVO.setBrowserJavaEnabled(commonValidatorVO.getDeviceDetailsVO().getBrowserJavaEnabled());
        }



        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);
        commRequestVO.setReserveField2VO(reserveField2VO);
        commRequestVO.setCommAccountInfoVO(commAccountInfoVO);
        commRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);

        if (functions.isValueNull(commonValidatorVO.getAttemptThreeD()))
            commRequestVO.setAttemptThreeD(commonValidatorVO.getAttemptThreeD());

        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()),Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        paymentProcess.setTransactionVOExtension(commRequestVO, commonValidatorVO);

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

    public TokenResponseVO processTokenGeneration(TokenRequestVO tokenRequestVO) throws PZGenericConstraintViolationException
    {
        PartnerDetailsVO partnerDetailsVO = tokenRequestVO.getPartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO=tokenRequestVO.getMerchantDetailsVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        TokenManager tokenManager = new TokenManager();
        TokenResponseVO tokenResponseVO = new TokenResponseVO();
        BinResponseVO binResponseVO = new BinResponseVO();

        String cardNumber = tokenRequestVO.getCardDetailsVO().getCardNum();
        String status = "N";
        String remark = "";

        String firstSix = "";
        String paymentType = "1";//By default Payment Type will be Credit Card
        String cardType="";
        if (!tokenRequestVO.getCardDetailsVO().getCardNum().equals(""))
        {
            firstSix = functions.getFirstSix(tokenRequestVO.getCardDetailsVO().getCardNum());
        }
        binResponseVO = functions.getBinDetails(firstSix, tokenRequestVO.getMerchantDetailsVO().getCountry());
        cardType=Functions.getCardTypeFromCardNumber(tokenRequestVO.getCardDetailsVO().getCardNum());

        if(binResponseVO!=null && functions.isValueNull(binResponseVO.getCardcategory()) && functions.isValueNull(binResponseVO.getBrand()))
        {
            if("CREDIT".equalsIgnoreCase(binResponseVO.getCardcategory()))
                paymentType = "1";
            else if("DEBIT".equalsIgnoreCase(binResponseVO.getCardcategory()))
                paymentType = "4";
        }

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()) && "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
        {
            tokenRequestVO.setMemberId(tokenRequestVO.getMerchantDetailsVO().getMemberId());
            tokenRequestVO.setIsActive("Y");
            Date date3 = new Date();
            transactionLogger.error("DirectTransaction.common.processTokenRegistration start #########" + date3.getTime());

            if (functions.isValueNull(cardNumber))
            {
                String existingTokenId = "";
                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                String expDate=functions.isValueNull(tokenRequestVO.getCardDetailsVO().getExpMonth())+"/"+functions.isValueNull(tokenRequestVO.getCardDetailsVO().getExpYear());
                existingTokenId = tokenManager.isTokenAvailable(tokenRequestVO.getMemberId(), tokenRequestVO.getCardDetailsVO().getCardNum(),expDate);
                if (functions.isValueNull(existingTokenId))
                {
                    tokenRequestVO.setTokenId(existingTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
                else
                {
                    String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO);
                    tokenRequestVO.setTokenId(newTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
                CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setCardNum(tokenRequestVO.getCardDetailsVO().getCardNum());
                commCardDetailsVO.setFirstSix(tokenRequestVO.getCardDetailsVO().getFirstSix());
                commCardDetailsVO.setLastFour(tokenRequestVO.getCardDetailsVO().getLastFour());
                commCardDetailsVO.setExpMonth(tokenRequestVO.getCardDetailsVO().getExpMonth());
                commCardDetailsVO.setExpYear(tokenRequestVO.getCardDetailsVO().getExpYear());
                commCardDetailsVO.setCardHolderFirstName(tokenRequestVO.getAddressDetailsVO().getFirstname());
                commCardDetailsVO.setCardHolderSurname(tokenRequestVO.getAddressDetailsVO().getLastname());
                tokenDetailsVO.setCommCardDetailsVO(commCardDetailsVO);
                tokenResponseVO.setTokenDetailsVO(tokenDetailsVO);
            }
            else if((tokenRequestVO.getCardDetailsVO().getBIC() != null && tokenRequestVO.getCardDetailsVO().getIBAN() != null) || tokenRequestVO.getReserveField2VO().getAccountNumber() != null)
            {
                String existingTokenId = "";
                existingTokenId = tokenManager.isNewAccount(tokenRequestVO.getMemberId(), tokenRequestVO);
                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();

                if(functions.isValueNull(existingTokenId))
                {
                    tokenRequestVO.setTokenId(existingTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
                else
                {
                    String bankAccountId = tokenManager.insertBankAccountDetails(tokenRequestVO); //inserting bank account details
                    tokenRequestVO.setBankAccountId(bankAccountId);
                    String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO); //new token creation in token_master
                    tokenRequestVO.setTokenId(newTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO); //new registration in registration_master
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
            }

            transactionLogger.error("DirectTransaction.common.processTokenRegistration end #########" + new Date().getTime());
            transactionLogger.error("DirectTransaction.common.processTokenRegistration diff #########"+(new Date().getTime()-date3.getTime()));
            if(functions.isValueNull(tokenRequestVO.getCustomerId()))
                tokenResponseVO.setCustomerId(tokenRequestVO.getCustomerId());
            tokenResponseVO.setMemberId(tokenRequestVO.getMerchantDetailsVO().getMemberId());


            if (functions.isValueNull(cardNumber))
            {
                MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                HashMap hashMap = new HashMap();
                hashMap.put(MailPlaceHolder.NAME,tokenRequestVO.getAddressDetailsVO().getFirstname()+" "+tokenRequestVO.getAddressDetailsVO().getLastname());
                hashMap.put(MailPlaceHolder.CARDHOLDERNAME,tokenRequestVO.getAddressDetailsVO().getFirstname()+" "+tokenRequestVO.getAddressDetailsVO().getLastname());
                hashMap.put(MailPlaceHolder.CustomerEmail, tokenRequestVO.getAddressDetailsVO().getEmail());
                hashMap.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTableForCardDetails(tokenResponseVO.getTokenDetailsVO()));
                hashMap.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
                asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANTS_LEVEL_CARD_REGISTRATION, hashMap);
            }

        }
        else
        {
            tokenRequestVO.setIsActive("Y");
            if (functions.isValueNull(cardNumber))
            {
                String existingTokenId = "";
                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                existingTokenId = tokenManager.getExistingTokenByPartner(tokenRequestVO.getPartnerId(), tokenRequestVO.getCardDetailsVO().getCardNum());

                if (functions.isValueNull(existingTokenId))
                {
                    tokenRequestVO.setTokenId(existingTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByPartner(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
                else
                {
                    String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO);
                    tokenRequestVO.setTokenId(newTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByPartner(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
            }
            else if (tokenRequestVO.getCardDetailsVO().getBIC() != null || tokenRequestVO.getReserveField2VO().getAccountNumber() != null)
            {
                String existingTokenId = "";
                existingTokenId = tokenManager.isNewAccount(tokenRequestVO.getMemberId(), tokenRequestVO);
                TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();

                if (functions.isValueNull(existingTokenId))
                {
                    tokenRequestVO.setTokenId(existingTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByPartner(tokenRequestVO);
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
                else
                {
                    String bankAccountId = tokenManager.insertBankAccountDetails(tokenRequestVO); //inserting bank account details
                    tokenRequestVO.setBankAccountId(bankAccountId);
                    String newTokenId = tokenManager.createTokenWithAccount(bankAccountId,tokenRequestVO); //new token creation in token_master
                    tokenRequestVO.setTokenId(newTokenId);
                    tokenRequestVO.setPaymentType(paymentType);
                    tokenRequestVO.setCardType(cardType);
                    tokenRequestVO.setCvv(tokenRequestVO.getCardDetailsVO().getcVV());
                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByPartner(tokenRequestVO); //new registration in registration_master
                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                    tokenResponseVO.setTokenId(existingTokenId);
                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                }
            }

            if(functions.isValueNull(tokenRequestVO.getCustomerId()))
                tokenResponseVO.setCustomerId(tokenRequestVO.getCustomerId());
            tokenResponseVO.setPartnerId(tokenRequestVO.getPartnerDetailsVO().getPartnerId()); //partnerId set in response parameter
        }
        if ("success".equals(tokenResponseVO.getStatus()))
        {
            remark = "Token Created Successfully";
            status = "Y";
        }
        else
        {
            remark = "Token can not created";
        }
        transactionUtil.setTokenSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,tokenResponseVO,status,remark);
        return tokenResponseVO;
    }

    public Member addMerchant(Hashtable details) throws PZConstraintViolationException
    {
        Merchants merchants=new Merchants();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Member mem = null;
        try
        {
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "merchant");
            mem = merchants.addMerchant_new(user.getAccountId(), details);

        }
        catch (Exception e)
        {
            transactionLogger.error("Add user throwing Authentication Exception ", e);
            logger.error("Add user throwing Authentication Exception ", e);

            if(e instanceof AuthenticationAccountsException)
            {
                String message=((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    String error = "You cannot register token";
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_ALLOWED, error));
                    PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processTokenGeneration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
                    //throw new SystemError("Error: " + message);
                }
            }

            try
            {
                merchants.DeleteBoth((String) details.get("login"));
            }
            catch(Exception e1)
            {
                transactionLogger.error("Exception while deletion of Details::",e1);
                logger.error("Exception while deletion of Details::",e1);
            }
        }

        //client mail
       /* MailService mailService=new MailService();
        HashMap merchantSignupMail=new HashMap();
        merchantSignupMail.put(MailPlaceHolder.USERNAME,details.get("login"));
        merchantSignupMail.put(MailPlaceHolder.NAME,details.get("contact_persons"));
        merchantSignupMail.put(MailPlaceHolder.TOID,String.valueOf(mem.memberid));
        mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION,merchantSignupMail);*/
        return mem;
    }

    private boolean isOverCaptureRequest(String captureAmount,String authAmount)
    {
        boolean isOverCaptureRequest=false;
        if (new BigDecimal(captureAmount).compareTo(new BigDecimal(authAmount)) > 0)
        {
            isOverCaptureRequest=true;
        }
        return isOverCaptureRequest;
    }

    public List<TransactionVO> processGetTransactionList (TransactionVO transactionVO,CommonValidatorVO commonValidatorVO,List<TransactionVO> transDetailVOList) throws PZDBViolationException, PZConstraintViolationException
    {
        //InvoiceEntry invoiceEntry = new InvoiceEntry();
        List<TransactionVO> list = new ArrayList<TransactionVO>();
        Hashtable hash = new Hashtable();
        TransactionManager transactionManager = new TransactionManager();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String role=commonValidatorVO.getMerchantDetailsVO().getRole();
        String fdate = "";
        String fmonth = "";
        String fyear = "";
        String tdate = "";
        String tmonth = "";
        String tyear = "";

        if (functions.isValueNull(commonValidatorVO.getPaginationVO().getStartdate()))
        {
            String[] startDate = commonValidatorVO.getPaginationVO().getStartdate().split("/");
            fdate = startDate[0];
            fmonth = startDate[1];
            fyear = startDate[2];
        }

        if (functions.isValueNull(commonValidatorVO.getPaginationVO().getEnddate()))
        {
            String[] endDate = commonValidatorVO.getPaginationVO().getEnddate().split("/");
            tdate = endDate[0];
            tmonth = endDate[1];
            tyear = endDate[2];
        }

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        if("merchant".equalsIgnoreCase(role)){
            list = transactionManager.getTransactionDetail(transactionVO,commonValidatorVO,transDetailVOList,fdtstamp,tdtstamp);
        }
        else if ("submerchant".equalsIgnoreCase(role)){
            list=transactionManager.gettransactionDetailsMemberUser(transactionVO,commonValidatorVO,transDetailVOList,fdtstamp,tdtstamp);
        }


        if (list.isEmpty())
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, ErrorMessages.INVALID_RECORDS));
            PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", ErrorMessages.INVALID_RECORDS, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }


        return list;

    }

    public DirectKitResponseVO processPayout(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        PZPayoutRequest pzPayoutRequest         = new PZPayoutRequest();
        PZPayoutResponse pzPayoutResponse       = new PZPayoutResponse();
        AbstractPaymentGateway pg               = null;
        //CommonPaymentProcess commonPaymentProcess = new CommonPaymentProcess();
        GenericAddressDetailsVO addressDetailsVO    = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO transDetailsVO        = commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        CommCardDetailsVO commCardDetailsVO         = new CommCardDetailsVO();
        PaymentManager paymentManager               = new PaymentManager();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        String status                   = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String custAccount              = "";
        custAccount                     = commonValidatorVO.getCustAccount();
        //System.out.println("custAccount-1--"+custAccount);

        AbstractPaymentProcess paymentProcess = null;

        if (functions.isValueNull(commonValidatorVO.getTrackingid()))
        {
            status = paymentManager.getStatusOfTransaction(commonValidatorVO.getTrackingid());

            transactionLogger.debug("status-----"+status+"------payoutType-----"+commonValidatorVO.getPayoutType());

            if(!"cancel".equalsIgnoreCase(commonValidatorVO.getPayoutType()))
            {
                if(!status.equals("authsuccessful") && !status.equals("capturesuccess") && !status.equals("setteled") && !status.equals("settled"))
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, ErrorMessages.INVALID_TRANSACTION));//SYS_INVALID_TRANSACTION
                    PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", ErrorMessages.INVALID_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
            commonValidatorVO = paymentProcess.getExtentionDetails(commonValidatorVO);
            pzPayoutRequest.setTrackingId(Integer.parseInt(commonValidatorVO.getTrackingid()));
        }
        else
        {
            paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(0, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        }

        //System.out.println("commvalidator----"+commonValidatorVO.getCustAccount());
        auditTrailVO.setActionExecutorId(merchantDetailsVO.getMemberId());
        auditTrailVO.setActionExecutorName("REST API");
        pzPayoutRequest.setOrderId(transDetailsVO.getOrderId());
        pzPayoutRequest.setPayoutAmount(transDetailsVO.getAmount());
        pzPayoutRequest.setOrderDescription(transDetailsVO.getOrderDesc());
        pzPayoutRequest.setNotificationUrl(transDetailsVO.getNotificationUrl());
        pzPayoutRequest.setCustomerAccount(commonValidatorVO.getCustAccount());
        pzPayoutRequest.setAccountId(Integer.parseInt(merchantDetailsVO.getAccountId()));
        pzPayoutRequest.setMemberId(Integer.parseInt(merchantDetailsVO.getMemberId()));
        pzPayoutRequest.setTerminalId(commonValidatorVO.getTerminalId());
        pzPayoutRequest.setAuditTrailVO(auditTrailVO);

        if(functions.isValueNull(commonValidatorVO.getCustEmail()))
            pzPayoutRequest.setCustomerEmail(commonValidatorVO.getCustEmail());
        else
            pzPayoutRequest.setCustomerEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()))
            pzPayoutRequest.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhoneCountryCode()))
            pzPayoutRequest.setPhoneCountryCode(commonValidatorVO.getAddressDetailsVO().getPhoneCountryCode());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTelnocc())){
            pzPayoutRequest.setPhoneCountryCode(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        }

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCountry()))
            pzPayoutRequest.setCustomerCountry(commonValidatorVO.getAddressDetailsVO().getCountry());

        pzPayoutRequest.setExpDateOffset(merchantDetailsVO.getExpDateOffset());
        pzPayoutRequest.setPayoutCurrency(transDetailsVO.getCurrency());
        pzPayoutRequest.setTmpl_currency(addressDetailsVO.getTmpl_currency());
        pzPayoutRequest.setTmpl_amount(addressDetailsVO.getTmpl_amount());
        pzPayoutRequest.setCustomerId(commonValidatorVO.getCustomerId());
        pzPayoutRequest.setCustomerBankId(commonValidatorVO.getCustomerBankId());
        pzPayoutRequest.setWalletId(transDetailsVO.getWalletId());
        pzPayoutRequest.setWalletAmount(transDetailsVO.getWalletAmount());
        pzPayoutRequest.setWalletCurrency(transDetailsVO.getWalletCurrency());
        pzPayoutRequest.setIpAddress(commonValidatorVO.getRequestedIP());
       /* //System.out.println("commonValidatorVO.getReserveField2VO().getBankAccountNumber() is --" + commonValidatorVO.getReserveField2VO().getBankAccountNumber());
        System.out.println("commonValidatorVO.getReserveField2VO().getBankAccountName() --"+commonValidatorVO.getReserveField2VO().getBankAccountName());
        System.out.println("commonValidatorVO.getReserveField2VO().getBankIfsc() --"+commonValidatorVO.getReserveField2VO().getBankIfsc());
        System.out.println("commonValidatorVO.getReserveField2VO().getTransferType() --"+commonValidatorVO.getReserveField2VO().getTransferType());*/
        pzPayoutRequest.setBankAccountNo(commonValidatorVO.getReserveField2VO().getBankAccountNumber());
        pzPayoutRequest.setCustomerBankAccountName(commonValidatorVO.getReserveField2VO().getBankAccountName());
        pzPayoutRequest.setBankIfsc(commonValidatorVO.getReserveField2VO().getBankIfsc());
        pzPayoutRequest.setBankTransferType(commonValidatorVO.getReserveField2VO().getTransferType());

        if(commonValidatorVO.getReserveField2VO().getAccountNumber() != null){
            pzPayoutRequest.setCustomerBankAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
        }
        if(commonValidatorVO.getReserveField2VO().getBankName() != null){
            pzPayoutRequest.setBankName(commonValidatorVO.getReserveField2VO().getBankName());
        }
        if(commonValidatorVO.getReserveField2VO().getBranchName() != null){
            pzPayoutRequest.setBranchName(commonValidatorVO.getReserveField2VO().getBranchName());
        }
        if(commonValidatorVO.getReserveField2VO().getBankCode() != null){
            pzPayoutRequest.setBankCode(commonValidatorVO.getReserveField2VO().getBankCode());
        }
        if(commonValidatorVO.getReserveField2VO().getAccountType() != null){
            pzPayoutRequest.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
        }
        if(commonValidatorVO.getReserveField2VO().getBranchCode() != null){
            pzPayoutRequest.setBranchCode(commonValidatorVO.getReserveField2VO().getBranchCode());
        }

        if(functions.isValueNull(commonValidatorVO.getPayoutType())){
            pzPayoutRequest.setPayoutType(commonValidatorVO.getPayoutType());
        }

        //System.out.println("custAccount  ()----"+pzPayoutRequest.getCustomerAccount());

        transactionLogger.error("from pz email---"+pzPayoutRequest.getCustomerEmail());
        transactionLogger.error("from pz id---"+pzPayoutRequest.getCustomerId());
        transactionLogger.error("from pz bankid---"+pzPayoutRequest.getCustomerBankId());
        transactionLogger.error("from pz bankid---"+pzPayoutRequest.getCustomerBankId());
        transactionLogger.error("from payout Type---"+pzPayoutRequest.getPayoutType());
        transactionLogger.error("from walletId---"+pzPayoutRequest.getWalletId());
        transactionLogger.error("from walletAmount ---"+pzPayoutRequest.getWalletAmount());
        transactionLogger.error("from walletCurrency ---"+pzPayoutRequest.getWalletCurrency());
        String mailTransactionStatus = "";

        pzPayoutResponse = paymentProcess.payout(pzPayoutRequest);

        logger.debug("status----" + pzPayoutResponse.getStatus());
        logger.error("newVoucherSerialNumber----" + pzPayoutResponse.getVoucherNumber());
        logger.error("Remark RestDireectManager----" + pzPayoutResponse.getRemark());
        logger.error("Commission RestDireectManager----" + pzPayoutResponse.getCommissionToPay());
        logger.error("Currency RestDireectManager----" + pzPayoutResponse.getCommissionCurrency());

        if (pzPayoutResponse.getStatus() != null)
            directKitResponseVO.setStatus(String.valueOf(pzPayoutResponse.getStatus()));
        if (pzPayoutResponse.getTrackingId() != null)
            directKitResponseVO.setTrackingId(pzPayoutResponse.getTrackingId());
        if (pzPayoutResponse.getVoucherNumber() != null)
            directKitResponseVO.setVoucherNumber(pzPayoutResponse.getVoucherNumber());
        if (pzPayoutResponse.getRemark() != null)
            directKitResponseVO.setRemark(pzPayoutResponse.getRemark());
        if (pzPayoutResponse.getTmpl_amount() != null)
            directKitResponseVO.setTmpl_amount(pzPayoutResponse.getTmpl_amount());
        if (pzPayoutResponse.getTmpl_currency() != null)
            directKitResponseVO.setTmpl_currency(pzPayoutResponse.getTmpl_currency());
        if (pzPayoutResponse.getPayoutAmount() != null)
            directKitResponseVO.setAmount(pzPayoutResponse.getPayoutAmount());
        if(functions.isValueNull(commonValidatorVO.getCustEmail()))
            directKitResponseVO.setEmail(commonValidatorVO.getCustEmail());
        else
            directKitResponseVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        directKitResponseVO.setCustId(commonValidatorVO.getCustomerId());
        directKitResponseVO.setCustBankId(commonValidatorVO.getCustomerBankId());
        directKitResponseVO.setCustAccount(commonValidatorVO.getCustAccount());

        //For VM response
        if(functions.isValueNull(pzPayoutResponse.getMerchantUsersCommission()))
            directKitResponseVO.setMerchantUsersCommission(pzPayoutResponse.getMerchantUsersCommission());
        if(functions.isValueNull(pzPayoutResponse.getCommissionCurrency()))
            directKitResponseVO.setCommissionCurrency(pzPayoutResponse.getCommissionCurrency());

        transactionLogger.error("from res email---"+directKitResponseVO.getEmail());
        transactionLogger.error("from res id---"+directKitResponseVO.getCustId());
        transactionLogger.error("from res bankid---"+directKitResponseVO.getCustBankId());

        if (pzPayoutResponse.getStatus().equals(PZResponseStatus.SUCCESS) || pzPayoutResponse.getStatus().equals(PZResponseStatus.PAYOUTSUCCESSFUL))
        {
            mailTransactionStatus = "success";
        }
        else if(pzPayoutResponse.getStatus().equals(PZResponseStatus.PENDING))
        {
            mailTransactionStatus = "pending";
        }
        else
        {
            mailTransactionStatus = "failed";
        }

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.MERCHANT_PAYOUT_ALERT_MAIL, String.valueOf(directKitResponseVO.getTrackingId()), mailTransactionStatus, null,null);


        return directKitResponseVO;
    }

    private CommRequestVO getSkrillRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());

        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    private CommRequestVO getPaySafeRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setSex(commonValidatorVO.getAddressDetailsVO().getSex());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCardType(commonValidatorVO.getPaymentBrand());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }


    public DirectKitResponseVO processVerifyWalletDetials(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("INSIDE processVerifyWalletDetials");
        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO             = new ErrorCodeListVO();
        TransactionManager transactionManager       = new TransactionManager();
        TerminalManager terminalManager             = new TerminalManager();
        PaymentManager paymentManager               = new PaymentManager();

        String trackingid   = commonValidatorVO.getTrackingid();
        String currency     = commonValidatorVO.getTransDetailsVO().getCurrency();

        transactionLogger.error("in processVerifyWalletDetials ------- trackingid -----"+trackingid);
        transactionLogger.error("in processVerifyWalletDetials ------- currency ------"+currency);
        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommonForAuthStarted(trackingid);
            String memberid                 = transactionDetailsVO.getToid();
            List<TerminalVO> accountList    = new ArrayList<TerminalVO>();
            accountList                     = terminalManager.getMemberandTerminalList(memberid,currency);

            transactionLogger.error("IN processVerifyWalletDetials ------ memberid ------ "+memberid);

            String terminalId   = "";
            String paymodeid    = "";
            String cardtypeid   = "";
            String accountId    = "";

            for(TerminalVO terminalVO : accountList)
            {
                terminalId  = terminalVO.getTerminalId();
                paymodeid   = terminalVO.getPaymodeId();
                cardtypeid  = terminalVO.getCardTypeId();
                accountId   = terminalVO.getAccountId();
            }

            if(functions.isValueNull(terminalId))
                commonValidatorVO.setTerminalId(terminalId);
            if(functions.isValueNull(paymodeid))
                commonValidatorVO.setPayoutType(paymodeid);
            if(functions.isValueNull(cardtypeid))
                commonValidatorVO.setCardType(cardtypeid);

            paymentManager.updateAuthSuccessfulTransactionForWallet(commonValidatorVO, trackingid);

            String walletAddress = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            transactionLogger.error(" IN processVerifyWalletDetials ------ Wallet addresss ------ "+walletAddress);

            directKitResponseVO.setWalletAddress(walletAddress);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in processVerifyWalletDetials---", e);
        }
        return directKitResponseVO;
    }


    public DirectKitResponseVO processQRTransaction(CommonValidatorVO commonValidatorVO)throws PZConstraintViolationException,PZGenericConstraintViolationException
    {
        transactionLogger.error("------ in RestDirectTransactionManager processQRTransaction ---------");
        PaymentManager paymentManager           = new PaymentManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        int trackingid              = 0;
        AuditTrailVO auditTrailVO   = new AuditTrailVO();

        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg   = null;

        if ("PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
        {
            auditTrailVO.setActionExecutorName("Wallet Owner DynamicVerify");
            trackingid = Integer.parseInt(commonValidatorVO.getTrackingid());

            //commonValidatorVO.getAddressDetailsVO().setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
            //commonValidatorVO.getAddressDetailsVO().setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
//            commonValidatorVO.getTransDetailsVO().setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
//            commonValidatorVO.getTransDetailsVO().setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
//            commonValidatorVO.getTransDetailsVO().setWalletAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
//            commonValidatorVO.getTransDetailsVO().setWalletCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
            transactionLogger.error("before uppdate wallet amount = "+commonValidatorVO.getTransDetailsVO().getWalletAmount());
            transactionLogger.error("before uppdate wallet currency = "+commonValidatorVO.getTransDetailsVO().getWalletCurrency());

            paymentManager.updateAuthStartedTransactionEntryForDynamicQR(commonValidatorVO, commonValidatorVO.getTrackingid(), auditTrailVO);
        }
        else if ("DB".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
        {
            auditTrailVO.setActionExecutorName("Wallet Owner StaticVerify");

            commonValidatorVO.getAddressDetailsVO().setTmpl_amount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
            commonValidatorVO.getAddressDetailsVO().setTmpl_currency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
            commonValidatorVO.getTransDetailsVO().setAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
            commonValidatorVO.getTransDetailsVO().setCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
            commonValidatorVO.getTransDetailsVO().setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            commonValidatorVO.getTransDetailsVO().setRedirectUrl("https://ife.pz.com/TestApp/redirecturl.jsp");

            trackingid = paymentManager.insertCaptureStartedTransactionEntryForStaticQR(commonValidatorVO,auditTrailVO);

        }
        commonValidatorVO.setTrackingid(String.valueOf(trackingid));
        directKitResponseVO.setTrackingId(String.valueOf(trackingid));


        transactionLogger.error(" ---- in processQRTransaction Manager tracking id -----"+commonValidatorVO.getTrackingid());


        try
        {
            pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("EW"))
            {
                commonValidatorVO.getAddressDetailsVO().setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
                //commonValidatorVO.setCustomerId("24532");
                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QR"))
                {
                    CommResponseVO transRespDetails = null;
                    PLMPUtils plmpUtils = new PLMPUtils();
                    commRequestVO       = plmpUtils.getPLMPRequestVO(commonValidatorVO);

                    if ("PA".equalsIgnoreCase(commonValidatorVO.getTransactionType())) //Dynamic
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingid), commRequestVO);
                    }
                    else//DB = Static
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            //paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("success");
                            directKitResponseVO.setWalletAddress(transRespDetails.getWalletId());
                        }
                        else
                        {
                            //paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", String.valueOf(trackingid), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in RestDirectTransactionManager---", se);
        }
        //AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(trackingid, Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        //directKitResponseVO = paymentProcess.setNBResponseVO(directKitResponseVO, commonValidatorVO);

        return directKitResponseVO;
    }


    public DirectKitResponseVO processQRCheckout(CommonValidatorVO commonValidatorVO)throws PZConstraintViolationException,PZGenericConstraintViolationException
    {
        transactionLogger.error("------ in RestDirectTransactionManager processQRCheckout ---------");
        PaymentManager paymentManager = new PaymentManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        int trackingid = 0;
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("Customer QR Checkout");

        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg = null;

        transactionLogger.error("tptype -----------"+commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.getTransDetailsVO().setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.getTransDetailsVO().setRedirectUrl("https://ife.pz.com/TestApp/redirecturl.jsp");

        trackingid = paymentManager.insertBegunTransactionEntryForQRCheckout(commonValidatorVO,auditTrailVO);

        transactionLogger.error("tracking id --------- "+trackingid);
        commonValidatorVO.setTrackingid(String.valueOf(trackingid));
        directKitResponseVO.setTrackingId(String.valueOf(trackingid));

        transactionLogger.error(" ---- in processQRTransaction Manager tracking id -----"+commonValidatorVO.getTrackingid());


        if(functions.isValueNull(String.valueOf(trackingid))){
            directKitResponseVO.setStatus("success");
        }
        else{
            directKitResponseVO.setStatus("failed");
        }

/*        try
        {
            pg = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if (commonValidatorVO.getPaymentMode().equalsIgnoreCase("EW"))
            {
                commonValidatorVO.getAddressDetailsVO().setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());

                if (commonValidatorVO.getPaymentBrand().equalsIgnoreCase("QR"))
                {
                    CommResponseVO transRespDetails = null;
                    VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
                    commRequestVO = voucherMoneyUtils.getVoucherMoneyRequestVO(commonValidatorVO);

                    if (commonValidatorVO.getMerchantDetailsVO().getIsService().equalsIgnoreCase("N"))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingid), commRequestVO);
                    }
                    else
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingid), commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus().trim()))
                        {
                            directKitResponseVO.setStatus("success");
                        }
                        else
                        {
                            directKitResponseVO.setStatus("failed");
                        }
                    }
                }
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError in RestDirectTransactionManager---", se);
        }*/

        return directKitResponseVO;
    }


    public DirectKitResponseVO processQRConfirmation (CommonValidatorVO commonValidatorVO,String status,String captureAmount)throws PZConstraintViolationException,PZGenericConstraintViolationException
    {
        transactionLogger.error("------ in RestDirectTransactionManager processQRConfirmation ---------");
        PaymentManager paymentManager = new PaymentManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        int trackingid = 0;
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("Wallet Owner Confirm");
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg = null;

        trackingid = Integer.parseInt(commonValidatorVO.getTrackingid());

        commonValidatorVO.setTrackingid(String.valueOf(trackingid));
        directKitResponseVO.setTrackingId(String.valueOf(trackingid));


        transactionLogger.error(" ---- in processQRConfirmation Manager tracking id -----" + commonValidatorVO.getTrackingid());
        transactionLogger.error(" ---- in processQRConfirmation Manager status -----" + status);


        try
        {
            paymentManager.updateConfirmationStatusQR(commonValidatorVO, status, commonValidatorVO.getTrackingid(), auditTrailVO,captureAmount);
        }
        catch (Exception se)
        {
            logger.error("SystemError in RestDirectTransactionManager---", se);
        }

        return directKitResponseVO;

    }
    public DirectKitResponseVO processGetEmiCount(CommonValidatorVO commonValidatorVO) throws  PZGenericConstraintViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = null;
        TokenManager tokenManager =new TokenManager();
        EmiVO emiVO=new EmiVO();

        emiVO=tokenManager.getEmiCountWithTerminalId(commonValidatorVO);
        if(emiVO!= null)
        {
            directKitResponseVO=new DirectKitResponseVO();
            directKitResponseVO.setStartDate(emiVO.getStartDate());
            directKitResponseVO.setEndDate(emiVO.getEndDate());
            directKitResponseVO.setEmiPeriod(emiVO.getEmiPeriod());
        }

        return directKitResponseVO;
    }

    public DirectKitResponseVO processInitiateAuthentication(CommonValidatorVO commonValidatorVO)throws PZDBViolationException
    {
        AbstractPaymentGateway pg=null;
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        PaymentManager paymentManager=new PaymentManager();
        DirectKitResponseVO directKitResponseVO=null;

        String toid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();
        int trackingId= 0 ;

        transactionLogger.error("Toid---"+toid+"---accountid----"+accountId);

        String fromtype=GatewayAccountService.getGatewayAccount(accountId).getGateway();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());

        transactionLogger.error("Inside fromtype----"+fromtype);

        CommRequestVO commRequestVO=new CommRequestVO();
        CommTransactionDetailsVO transactionDetailsVO=new CommTransactionDetailsVO();

        transactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transactionDetailsVO.setCardType(commonValidatorVO.getTransDetailsVO().getCardType());
        transactionDetailsVO.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());
        transactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        commRequestVO.setTransDetailsVO(transactionDetailsVO);

        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            trackingId = paymentManager.insertInitAuthTransactionEntry(commonValidatorVO, auditTrailVO, commRequestVO, tableName);
        }

        commonValidatorVO.setTrackingid(String.valueOf(trackingId));

        transactionLogger.error("Tracking id------"+trackingId);

        try
        {
            pg = AbstractPaymentGateway.getGateway(accountId);

            if (Functions.checkAPIGateways(fromtype))
            {
                CommResponseVO commResponseVO = null;

                commResponseVO = (CommResponseVO) pg.processInitiateAuthentication(String.valueOf(trackingId), commRequestVO);

                Gson gson = new Gson();
                String comResponse1 = gson.toJson(commResponseVO);
                transactionLogger.error("commresponse-----" + comResponse1);

                directKitResponseVO = new DirectKitResponseVO();

                directKitResponseVO.setInitToken(commResponseVO.getResponseHashInfo());
                directKitResponseVO.setTrackingId(String.valueOf(trackingId));
                directKitResponseVO.setReferenceId(commResponseVO.getTransactionId());
                directKitResponseVO.setRemark(commResponseVO.getDescription());

            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in InitiateAuth----",e);
        }
        return directKitResponseVO;
    }

    public DirectKitResponseVO processAuthenticate(CommonValidatorVO commonValidatorVO)
    {
        AbstractPaymentGateway pg=null;
        DirectKitResponseVO directKitResponseVO=null;
        String memberid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();
        String trackingId="";

        transactionLogger.error("memberid-----"+memberid);

        transactionLogger.error("Accountid----"+accountId);

        String fromtype=GatewayAccountService.getGatewayAccount(accountId).getGateway();
        transactionLogger.error("Fromtype-----"+fromtype);

        CommRequestVO commRequestVO=new CommRequestVO();

        CommTransactionDetailsVO transactionDetailsVO=new CommTransactionDetailsVO();
        transactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transactionDetailsVO.setCardType(commonValidatorVO.getTransDetailsVO().getCardType());
        transactionDetailsVO.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());
        transactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transactionDetailsVO.setResponseOrderNumber(commonValidatorVO.getTransDetailsVO().getResponseOrderNumber());
        commRequestVO.setTransDetailsVO(transactionDetailsVO);

        CommCardDetailsVO cardDetailsVO=new CommCardDetailsVO();
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        commRequestVO.setCardDetailsVO(cardDetailsVO);

        CommAddressDetailsVO addressDetailsVO=new CommAddressDetailsVO();
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commRequestVO.setAddressDetailsVO(addressDetailsVO);

        trackingId=commonValidatorVO.getTrackingid();


        try
        {
            pg = AbstractPaymentGateway.getGateway(accountId);

            if (Functions.checkAPIGateways(fromtype))
            {
                CommResponseVO commResponseVO = null;
                commResponseVO = (CommResponseVO) pg.processAuthenticate(trackingId, commRequestVO);

                Gson gson = new Gson();
                String comResponse1 = gson.toJson(commResponseVO);
                transactionLogger.error("commresponse-----" + comResponse1);

                directKitResponseVO = new DirectKitResponseVO();
                directKitResponseVO.setStatus(commResponseVO.getStatus());
                directKitResponseVO.setRemark(commResponseVO.getDescription());
                transactionLogger.error("REMARK-----"+directKitResponseVO.getRemark());
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in Authenticate-----",e);
        }

        return directKitResponseVO;
    }
    public DirectKitResponseVO processSendSmsCode(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside processSendSmsCode -------------");
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        TransactionDetailsVO transactionDetailsVO=commonValidatorVO.getTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();

        commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());

        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        commAddressDetailsVO.setTmpl_amount(commonValidatorVO.getTransactionDetailsVO().getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(commonValidatorVO.getTransactionDetailsVO().getTemplatecurrency());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commonValidatorVO.setAddressDetailsVO(commAddressDetailsVO);

        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        if (functions.isValueNull(commonValidatorVO.getTransactionDetailsVO().getCurrency()))
            transDetailsVO.setCurrency(commonValidatorVO.getTransactionDetailsVO().getCurrency());
        if (functions.isValueNull(commonValidatorVO.getTransactionDetailsVO().getAmount()))
            transDetailsVO.setAmount(commonValidatorVO.getTransactionDetailsVO().getAmount());
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        UnionPayInternationalResponseVO UnionPayInternationalResponseVO1=new UnionPayInternationalResponseVO();
        UnionPayInternationalRequestVO unionPayInternationalRequestVO=new UnionPayInternationalRequestVO();
        UnionPayInternationalPaymentGateway unionPayInternationalPaymentGateway=new UnionPayInternationalPaymentGateway(transactionDetailsVO.getAccountId());
        PaymentManager paymentManager=new PaymentManager();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        CommResponseVO transRespDetails=new CommResponseVO();
        String trackingid=commonValidatorVO.getTrackingid();
        transactionLogger.debug("trackingid --------------"+trackingid);
        String customerIp="";
        String phoneno=commonValidatorVO.getTransactionDetailsVO().getTelno();
        String phoneCC=commonValidatorVO.getTransactionDetailsVO().getTelcc();
        String phoneNoNew=phoneCC+"-"+phoneno;
        transactionLogger.debug("tel cc ----"+commonValidatorVO.getTransactionDetailsVO().getTelcc());
        transactionLogger.debug("tel no ----"+commonValidatorVO.getTransactionDetailsVO().getTelno());
        transactionLogger.debug("phoneno in processSendSmsCode------------"+phoneno);
        transactionLogger.debug("phoneNoNew in processSendSmsCode------------"+phoneNoNew);
        String status="";
        String remark="";
        AbstractPaymentGateway pg = null;
        int detailId= 0;
        transactionLogger.error("db status -----------------"+transactionDetailsVO.getStatus());

        try
        {
            transactionLogger.debug("Inside this before smsstarted--");
            if("smsstarted".equalsIgnoreCase(transactionDetailsVO.getStatus()))
            {
                transactionLogger.debug("Inside SMS Started status ------------");
                UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
                UnionPayInternationalUtility unionPayInternationalUtility=new UnionPayInternationalUtility();
                unionPayInternationalRequestVO=unionPayInternationalUtility.getUnionPayRequestVO(transactionDetailsVO,commCardDetailsVO,commAddressDetailsVO,phoneNoNew);
                transactionLogger.debug("phone before enrollment call -----------------"+unionPayInternationalRequestVO.getPhone());

                //smsstared entry
                transactionLogger.debug("trackingid --------------"+trackingid);
                paymentManager.insertEnrollmentStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO, "enrollmentstarted");
                transactionLogger.debug("after enrollmentstarted   entry ----------------------- ");
                UnionPayInternationalResponseVO1 = (UnionPayInternationalResponseVO) unionPayInternationalPaymentGateway.processEasyEnrollment(trackingid, unionPayInternationalRequestVO, commonValidatorVO.getSmsCode());
                transactionLogger.debug("after processEasyEnrollment--------------- ");
                transactionLogger.error("after UnionPayInternationalResponseVO1 --- " + UnionPayInternationalResponseVO1.getStatus());
                pg = AbstractPaymentGateway.getGateway(transactionDetailsVO.getAccountId());
                String responseStatus="";
                if (UnionPayInternationalResponseVO1.getStatus().equalsIgnoreCase("success"))
                {
                    // Insert this card into upi_bin_card for next time use.
                    String queryResult = UnionPayInternationalUtils.insertCardForEnrollment(unionPayInternationalRequestVO.getCardDetailsVO().getCardNum(), unionPayInternationalRequestVO.getAddressDetailsVO().getPhone());
                    transactionLogger.error("queryResult ----------" + queryResult);
                    // System.out.println("queryResult in CupUpi SMS ------------" + queryResult);
                    // procee auth started
                    paymentManager.insertEnrollmentStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO, "authstarted");
                    //  detailId=paymentManager.insertAuthStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO);

                    if ("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsService())) {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, unionPayInternationalRequestVO);
                        responseStatus="authsuccessful";
                    }
                    else {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, unionPayInternationalRequestVO);
                        responseStatus="capturesuccess";
                        directKitResponseVO.setBillingDescriptor(transRespDetails.getDescriptor());
                    }
                    // String responseStatus="";
                    if (transRespDetails.getStatus().equalsIgnoreCase("success")) {
                        transactionLogger.debug("process sale success -------------");
                        //  System.out.println("--inside success if--");
                        status = "success";
                        //  paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    }
                    else {
                        responseStatus="authfailed";
                        //  System.out.println("--inside failed else--");
                        transactionLogger.debug("process sale fail -------------");
                        status = "failed";
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIp); // have to set value for audit trail and ip
                    }
                    // paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                }
                else
                {
                    responseStatus="authfailed";
                    transactionLogger.error("--inside SMS Started else--");
                    status = "failed";
                    transRespDetails.setStatus("fail");
                    transactionLogger.debug("Inside This enroolllme fail");
                    //  paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIp); // have to set value for audit trail and ip
                }
                paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
            }
            else
            {
                status="failed";
            }
            if (functions.isValueNull(transRespDetails.getRemark())){
                remark=transRespDetails.getRemark();
            }
            directKitResponseVO.setStatusMsg(remark);
            directKitResponseVO.setStatus(status);
            directKitResponseVO.setTrackingId(trackingid);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException----->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException----->",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError----->", systemError);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException----->", e);
        }

        return directKitResponseVO;
    }
    public DirectKitResponseVO processGetPaymentAndCardTypePerCurrency(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        TerminalManager terminalManager=new TerminalManager();
        Map<String,Map<String,Set<String>>> hashMap=null;

        hashMap=terminalManager.getPaymentAndCardTypePerCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        transactionLogger.error("Map--->"+hashMap);
        directKitResponseVO.setPaymentCardTypeMap(hashMap);
        return directKitResponseVO;
    }
    public DirectKitResponseVO processSaveTransactionReceipt(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        TransactionManager transactionManager=new TransactionManager();

        boolean isUpdate=transactionManager.updateTransactionReceiptByDetailId(commonValidatorVO);
        if(isUpdate)
            directKitResponseVO.setStatus("success");
        else
            directKitResponseVO.setStatus("failed");

        return directKitResponseVO;
    }

    public List<TransactionVO> processGetTransactionListVirtualCheckout(CommonValidatorVO commonValidatorVO)throws PZDBViolationException, PZConstraintViolationException
    {
        List<TransactionVO> list                = new ArrayList<TransactionVO>();
        TransactionManager transactionManager   = new TransactionManager();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        SimpleDateFormat sdf                    = new SimpleDateFormat("dd/MM/yyyy");

        String fdate    = "";
        String fmonth   = "";
        String fyear    = "";
        String tdate    = "";
        String tmonth   = "";
        String tyear    = "";
        try
        {
            String fdtstamp = "";
            String tdtStamp = "";
            if("chargeback".equalsIgnoreCase(commonValidatorVO.getStatus()) || ((commonValidatorVO.getTimeZone()!=null) || ((commonValidatorVO.getPaginationVO().getStartdate()!=null) && (commonValidatorVO.getPaginationVO().getEnddate()!=null))))
            {
                SimpleDateFormat sdf2   = new SimpleDateFormat("yyyy-MM-dd");
                fdtstamp                = sdf2.format(sdf.parse(commonValidatorVO.getPaginationVO().getStartdate()))+" 00:00:00";
                tdtStamp                = sdf2.format(sdf.parse(commonValidatorVO.getPaginationVO().getEnddate()))+" 23:59:59";
            }else
            {
                Date date           = null;
                Calendar rightNow   = Calendar.getInstance();
                if (functions.isValueNull(commonValidatorVO.getPaginationVO().getStartdate()))
                {
                    date        = sdf.parse(commonValidatorVO.getPaginationVO().getStartdate());
                    rightNow.setTime(date);
                    fdate       = String.valueOf(rightNow.get(Calendar.DATE));
                    fmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                    fyear       = String.valueOf(rightNow.get(Calendar.YEAR));
                }
                if (functions.isValueNull(commonValidatorVO.getPaginationVO().getEnddate()))
                {
                    date        = sdf.parse(commonValidatorVO.getPaginationVO().getEnddate());
                    rightNow.setTime(date);
                    tdate       = String.valueOf(rightNow.get(Calendar.DATE));
                    tmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                    tyear       = String.valueOf(rightNow.get(Calendar.YEAR));
                }
                fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
                tdtStamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            }

            list = transactionManager.getTransactionListForVirtualCheckout(commonValidatorVO, fdtstamp, tdtStamp);
            if (list.isEmpty())
            {
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, "NO Record Found."));
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", "NO Record Found.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (ParseException e)
        {
            transactionLogger.error("ParseException e-->");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INTERNAL_ERROR, "Internal Error"));
            PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", "Internal Error", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }


        return list;

    }


    public CommonValidatorVO processCardWhitelisting(CommonValidatorVO commonValidatorVO)throws PZDBViolationException, PZConstraintViolationException
    {
        String memberId=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String accountId=commonValidatorVO.getAccountId();
        String customerId=commonValidatorVO.getAddressDetailsVO().getCustomerid();
        String emailAddress=commonValidatorVO.getAddressDetailsVO().getEmail();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        HashMap<String, com.transaction.vo.restVO.RequestVO.CardVO> getCardDetailsVo=commonValidatorVO.getCardVOHashMap();
        transactionLogger.error("AccountId:::::"+accountId+"--------"+commonValidatorVO.getAccountId());
        HashMap<String, com.transaction.vo.restVO.RequestVO.CardVO> existCardVo=new HashMap<>();
        try
        {
            WhiteListManager whiteListManager=new WhiteListManager();
            Iterator<Map.Entry<String, com.transaction.vo.restVO.RequestVO.CardVO>> map = getCardDetailsVo.entrySet().iterator();
            transactionLogger.error("Map:::::"+map);
            while (map.hasNext()) {
                Map.Entry<String, com.transaction.vo.restVO.RequestVO.CardVO> entry = map.next();
                transactionLogger.error("Key:::" + entry.getKey()+"------------"+getCardDetailsVo.get(entry.getKey()));
                transactionLogger.error("--email---"+emailAddress+"---customerId---"+customerId+"---memberId---"+memberId+"---AccountId--"+accountId);
                com.transaction.vo.restVO.RequestVO.CardVO cardVO=getCardDetailsVo.get(entry.getKey());
                transactionLogger.error("FirstSix:::"+cardVO.getFirstsix()+"--Last--"+cardVO.getLastfour());
                boolean isChecked=whiteListManager.isRecordAvailableInSystemCustomerLevel(cardVO.getFirstsix(),cardVO.getLastfour(),emailAddress,customerId,memberId,accountId);
                transactionLogger.error("isChecked:::"+isChecked);
                if(!isChecked){
                    transactionLogger.error("inside ischecked:::"+isChecked);
                    existCardVo.put(customerId,cardVO);
                    boolean result=whiteListManager.addCardForCustomer(cardVO.getFirstsix(),cardVO.getLastfour(),emailAddress,accountId,memberId,customerId);
                    transactionLogger.error("result:::"+result);
                    if(result)
                    {
                        commonValidatorVO.setExistCardVOHash(existCardVo);
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception e-->",e);
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INTERNAL_ERROR, "Internal Error"));
            PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", "Internal Error", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        return commonValidatorVO;
    }
    public DirectKitResponseVO processGetDailySalesReport(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        List<Date> datesRange = getRangeOfDates(commonValidatorVO.getPaginationVO().getStartdate(),commonValidatorVO.getPaginationVO().getEnddate());
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        TransactionManager transactionManager=new TransactionManager();
        Map<String,Map<String,Map<String,Object>>> hashMap=null;
        hashMap=transactionManager.getDailySalesReport(commonValidatorVO,datesRange);
        transactionLogger.error("Map--->"+hashMap.size());
        directKitResponseVO.setDailySalesReport(hashMap);
        return directKitResponseVO;
    }
    public List<Date> getRangeOfDates(String dateString1, String dateString2) {
        ArrayList<Date> dates = new ArrayList<Date>();
        DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

}
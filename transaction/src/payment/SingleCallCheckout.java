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
import com.manager.*;
import com.manager.dao.FraudServiceDAO;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.manager.vo.ReserveField2VO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.Easebuzz.EasebuzzUtils;
import com.payment.Ecospend.EcospendRequestVo;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.LetzPay.LetzPayUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.PayMitco.core.PayMitcoPaymentProcess;
import com.payment.PayMitco.core.PayMitcoResponseVO;
import com.payment.PayMitco.core.PayMitcoUtility;
import com.payment.PaymentProcessFactory;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.aamarpay.AamarPayResponseVO;
import com.payment.aamarpay.AamarPayUtils;
import com.payment.airpay.AirpayUtils;
import com.payment.airtel.AirtelUgandaUtils;
import com.payment.allPay88.AllPay88ResponseVO;
import com.payment.allPay88.AllPay88Utils;
import com.payment.alphapay.AlphapayPaymentGateway;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.apcofastpayv6.ApcoFastPayV6Utils;
import com.payment.apexpay.ApexPayUtils;
import com.payment.appletree.AppleTreeCellulantPaymentGateway;
import com.payment.appletree.AppleTreeCellulantUtils;
import com.payment.asiancheckout.AsianCheckoutUtils;
import com.payment.b4payment.B4Utils;
import com.payment.b4payment.vos.TransactionResponse;
import com.payment.bhartiPay.BhartiPayUtils;
import com.payment.billdesk.BillDeskUtils;
import com.payment.bitclear.BitClearUtils;
import com.payment.bitcoinpayget.BitcoinPaygateUtils;
import com.payment.bnmquick.BnmQuickUtils;
import com.payment.cashfree.CashFreeUtils;
import com.payment.cellulant.AtCellulantUtils;
import com.payment.clearsettle.ClearSettleHPPGateway;
import com.payment.clearsettle.ClearSettleUtills;
import com.payment.common.core.*;
import com.payment.cupUPI.UnionPayInternationalPaymentGateway;
import com.payment.cupUPI.UnionPayInternationalResponseVO;
import com.payment.cupUPI.UnionPayInternationalUtility;
import com.payment.cupUPI.UnionPayInternationalUtils;
import com.payment.decta.core.DectaSMSPaymentGateway;
import com.payment.dectaNew.DectaNewUtils;
import com.payment.doku.DokuPaymentProcess;
import com.payment.doku.DokuUtils;
import com.payment.duspaydirectdebit.DusPayDDUtils;
import com.payment.easypaymentz.EasyPaymentzUtils;
import com.payment.epay.EpayUtils;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.flexepin.FlexepinPaymentGateway;
import com.payment.flexepin.FlexepinUtils;
import com.payment.imoneypay.IMoneypPayUtils;
import com.payment.infipay.InfiPayPaymentGateway;
import com.payment.infipay.InfiPayUtils;
import com.payment.inpay.InPayUtil;
import com.payment.jeton.JetonResponseVO;
import com.payment.jeton.JetonUtils;
import com.payment.jpbanktransfer.JPBTPaymentGateway;
import com.payment.jpbanktransfer.JPBankTransferUtils;
import com.payment.lyra.LyraPaymentUtils;
import com.payment.mtn.MtnUgandaUtils;
import com.payment.neteller.NetellerUtils;
import com.payment.neteller.response.Links;
import com.payment.neteller.response.NetellerResponse;
import com.payment.onepay.OnepayUtils;
import com.payment.p4.gateway.P4ResponseVO;
import com.payment.p4.gateway.P4Utils;
import com.payment.paySafeCard.PaySafeCardUtils;
import com.payment.payaidpayments.PayaidPaymentUtils;
import com.payment.payboutique.PayBoutiqueUtils;
import com.payment.payg.PayGUtils;
import com.payment.paygsmile.PayGSmilePaymentGateway;
import com.payment.paysec.PaySecUtils;
import com.payment.paytm.PayTMUtils;
import com.payment.payu.PayUUtils;
import com.payment.perfectmoney.PerfectMoneyUtils;
import com.payment.qikpay.QikPayUtils;
import com.payment.qikpayv2.QikPayV2Utils;
import com.payment.cajarural.CajaRuralPaymentProcess;
import com.payment.cajarural.CajaRuralUtils;
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.safexpay.SafexPayUtils;
import com.payment.skrill.SkrillUtills;
//import com.payment.smartcode.SmartCodePayUtils;
import com.payment.smartfastpay.SmartFastPayPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.sofort.SofortUtility;
import com.payment.sofort.VO.SofortResponseVO;
import com.payment.swiffy.SwiffyPaymentGateway;
import com.payment.tigergateway.TigerGateWayUtils;
import com.payment.tigerpay.TigerPayUtils;
import com.payment.transfr.TransfrPaymentProcess;
import com.payment.transfr.TransfrUtils;
import com.payment.trustly.TrustlyUtils;
import com.payment.unicredit.UnicreditUtils;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.*;
import com.payment.lyra.LyraPaymentUtils;
import com.payment.verve.VerveUtils;
import com.payment.voguePay.VoguePayUtils;
import com.payment.vouchermoney.VoucherMoneyResponse;
import com.payment.vouchermoney.VoucherMoneyUtils;
import com.payment.wealthpay.WealthPayPaymentProcess;
import com.payment.wealthpay.WealthPayUtils;
import com.payment.zotapaygateway.ZotapayUtils;
import com.sun.jersey.api.client.UniformInterfaceException;
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
public class SingleCallCheckout extends HttpServlet
{
    final static ResourceBundle RB          = LoadProperties.getProperty("com.directi.pg.CupServlet");
    final static ResourceBundle paysafecard = LoadProperties.getProperty("com.directi.pg.paysafecard");
    final static ResourceBundle apcopay     = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
    private static Logger log               = new Logger(SingleCallCheckout.class.getName());
    private static TransactionLogger transactionLogger      = new TransactionLogger(SingleCallCheckout.class.getName());
    SendTransactionEventMailUtil sendTransactionEventMail   = new SendTransactionEventMailUtil();
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
        String error        = "";
        HttpSession session = req.getSession();
        String ctoken       = (String) session.getAttribute("ctoken");
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        //CommonValidatorVO commonValidatorVO = (CommonValidatorVO) session.getAttribute("commonValidator");
        CommonValidatorVO commonValidatorVO = null;
        InvoiceEntry invoiceEntry           = new InvoiceEntry();
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        commonValidatorVO                   = ReadRequest.getSpecificRequestParametersForSale1(req);
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        LimitChecker limitChecker       = new LimitChecker();
        /*transactionLogger.error("req.getParameter(\"fingerprints\")---->"+req.getParameter("fingerprints"));
        session.setAttribute("fingerprints",req.getParameter("fingerprints"));*/
        //session.setAttribute("customerIpAddress",commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());



        String paymentBrand = commonValidatorVO.getPaymentBrand();
        String paymentMode  = commonValidatorVO.getPaymentMode();

        SendTransactionEventMailUtil sendTransactionEventMailUtil   = new SendTransactionEventMailUtil();
        CommonInputValidator commonInputValidator                   = new CommonInputValidator();
        AbstractPaymentGateway pg                   = null;
        SingleCallPaymentDAO singleCallPaymentDAO   = new SingleCallPaymentDAO();
        ActionEntry entry   = new ActionEntry();
        Functions functions = new Functions();
        PaymentManager paymentManager       = new PaymentManager();
        TransactionHelper transactionHelper = new TransactionHelper();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO             = new MerchantDAO();
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        RecurringManager recurringManager   = new RecurringManager();
        TerminalManager terminalManager     = new TerminalManager();
        RecurringBillingVO recurringBillingVO   = null;
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        LimitRouting limitRouting               = new LimitRouting();
        MarketPlaceVO marketPlaceVO             = new MarketPlaceVO();
        int paymentType         = 0;
        int cardType            = 0;
        String respStatus       = null;
        String respPaymentId    = null;
        String mailtransactionStatus    = "Failed";
        String errorName                = "";
        String eci                      ="";
        String billingDiscriptor        = "";
        String respRemark   = "";
        String machineid    = "";
        String respDateTime = null;
        String trackingid   = null;
        String currency     = "";
        String toid         = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String fromType     = "";
        String fromID       = "";
        int detailId        = 0;
        String isSuccessful = "";
        String token        = "";
        String remoteAddr   = Functions.getIpAddress(req);
        int serverPort      = req.getServerPort();
        String servletPath  = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent    = req.getHeader("User-Agent");
        String header       = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName     = httpProtocol + "://" + remoteAddr;
        String customerIpAddress = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
        String tableName    = "";
        HashMap terminalMap = (HashMap)session.getAttribute("terminalmap");
        LinkedHashMap<String,TerminalVO> terminalMapLimitRouting    = (LinkedHashMap<String,TerminalVO>)session.getAttribute("terminalMapLimitRouting");
        List<MarketPlaceVO> mpDetailsList                           = (List<MarketPlaceVO>)session.getAttribute("mpDetailsList");

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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            User user = new DefaultUser(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            ESAPI.authenticator().setCurrentUser(user);
            commonValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
            commonValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

            paymentManager.getMerchantandTransactionDetails(commonValidatorVO);

            trackingid  = commonValidatorVO.getTrackingid();
            currency    = commonValidatorVO.getTransDetailsVO().getCurrency();

            if((terminalMap == null || terminalMap.size() == 0 || terminalMap.isEmpty()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
            {
                String pMode    = GatewayAccountService.getPaymentId(commonValidatorVO.getPaymentMode());
                String pBrand   = GatewayAccountService.getCardId(commonValidatorVO.getPaymentBrand());

                terminalMap     = terminalManager.getPaymdeCardTerminalVOfromPaymodeCardType(commonValidatorVO.getMerchantDetailsVO().getMemberId(), currency, pMode, pBrand,commonValidatorVO.getAccountId());

                transactionLogger.debug("Terminal Map from Query---" + terminalMap);
            }
            if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()) && mpDetailsList == null)
            {
                String[] childTrackingid    = req.getParameterValues("marketPlaceTrackingid[]");
                if(childTrackingid != null)
                {
                    mpDetailsList=new ArrayList<>();
                    for (int i = 0; i < childTrackingid.length; i++)
                    {
                        transactionLogger.debug("Child Trackingid------>" + childTrackingid[i]);
                        marketPlaceVO   = paymentManager.getMarketPlaceDetailsByTrackingid(childTrackingid[i]);
                        mpDetailsList.add(marketPlaceVO);
                    }
                }
            }
            commonValidatorVO.setMarketPlaceVOList(mpDetailsList);
            transactionLogger.debug("Action called---" + commonValidatorVO.getActionType());
            transactionLogger.debug("Action called MID---" + commonValidatorVO.getMerchantDetailsVO().getMemberId());
            transactionLogger.debug("Action called Redirect URL---" + commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            if(functions.isValueNull(commonValidatorVO.getActionType()) && commonValidatorVO.getActionType().equalsIgnoreCase("cancel"))
            {
                String notificationUrl  = commonValidatorVO.getTransDetailsVO().getNotificationUrl();
                merchantDetailsVO       = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                transactionLogger.debug("TransactionNotification flag for ---" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + merchantDetailsVO.getTransactionNotification());
                if(functions.isValueNull(notificationUrl))//&& ("Non-3D".equals(merchantDetailsVO.getTransactionNotification())||"Both".equals(merchantDetailsVO.getTransactionNotification()))
                {
                    transactionLogger.error("inside sending notification for cancel transaction---" + notificationUrl + "--- for trackingid---" + trackingid);
                    TransactionDetailsVO transactionDetailsVO1          = transactionUtility.getTransactionDetails(commonValidatorVO);
                    if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                    }else{
                        transactionDetailsVO1.setMerchantNotificationUrl("");
                    }
                    AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, "cancelled", "Cancel by Customer", "");
                }
                paymentManager.updateCancelTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()) && mpDetailsList != null && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                {
                    for (int i = 0; i < mpDetailsList.size(); i++)
                    {
                        marketPlaceVO   = mpDetailsList.get(i);
                        commonValidatorVO.setTrackingid(marketPlaceVO.getTrackingid());
                        paymentManager.updateCancelTransactionEntryForCommon(commonValidatorVO, marketPlaceVO.getTrackingid(), auditTrailVO);
                    }
                    commonValidatorVO.setTrackingid(trackingid);
                }
                transactionUtility.doAutoRedirect(commonValidatorVO, res, "Cancel", "");
                return;
            }
            TerminalVO terminalVO = null;
            if(!commonValidatorVO.getMerchantDetailsVO().getBinRouting().equalsIgnoreCase("N") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()) && !functions.isValueNull(commonValidatorVO.getAccountId()))
            {
                commonValidatorVO.setCardType(GatewayAccountService.getCardId(commonValidatorVO.getPaymentBrand()));
                commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(commonValidatorVO.getPaymentMode()));

                if("Card".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
                {
                    String cNum = commonValidatorVO.getCardDetailsVO().getCardNum();
                    terminalVO = terminalManager.getBinRoutingTerminalDetailsByCardNumber(cNum, toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                    //If terminalvo is null, find BinRouting terminal = N
                    if (terminalVO == null)
                        terminalVO = terminalManager.getBinRoutingTerminalDetails(toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                    paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
                    cardType    = Integer.parseInt(commonValidatorVO.getCardType());
                    commonValidatorVO.setTerminalVO(terminalVO);
                }
                else if("Bin_Country".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
                {
                    BinVerificationManager binVerificationManager=new BinVerificationManager();
                    BinResponseVO binResponseVO=binVerificationManager.getBinDetailsFromFirstSix(functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum()));

                    if(functions.isValueNull(binResponseVO.getCountrycodeA3()))
                        terminalVO = terminalManager.getBinCountryRoutingTerminalDetails(binResponseVO.getCountrycodeA3(), toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());


                    //If terminalvo is null, find BinRouting terminal = N
                    if (terminalVO == null)
                        terminalVO = terminalManager.getBinRoutingTerminalDetails(toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                    paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
                    cardType = Integer.parseInt(commonValidatorVO.getCardType());
                    commonValidatorVO.setTerminalVO(terminalVO);
}
                else
                {
                    String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                    terminalVO = terminalManager.getBinRoutingTerminalDetails(firstSix, toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                    //If terminalvo is null, find BinRouting terminal = N
                    if (terminalVO == null)
                        terminalVO = terminalManager.getBinRoutingTerminalDetails(toid, commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                    paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
                    cardType = Integer.parseInt(commonValidatorVO.getCardType());
                    commonValidatorVO.setTerminalVO(terminalVO);
                }

                //If terminalvo is still null, display error
                if(terminalVO == null)
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    error           = "Terminal ID provided by you is not valid.";
                    errorCodeVO     = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                //PZExceptionHandler.raiseConstraintViolationException("SinglecallCheckout.class", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, commonValidatorVO.getErrorCodeListVO(), null, null);

            }
            else
            {
                transactionLogger.debug("commonValidatorVO.getMerchantDetailsVO().getLimitRouting()--->" + commonValidatorVO.getMerchantDetailsVO().getLimitRouting());
                transactionLogger.debug("commonValidatorVO.getAccountId()--->" + commonValidatorVO.getAccountId());
                if(commonValidatorVO.getMerchantDetailsVO().getLimitRouting().equalsIgnoreCase("Y") && !functions.isValueNull(commonValidatorVO.getAccountId()))
                {
                    commonValidatorVO.setCardType(GatewayAccountService.getCardId(commonValidatorVO.getPaymentBrand()));
                    commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(commonValidatorVO.getPaymentMode()));
                    terminalVO = (TerminalVO) terminalMap.get(paymentMode + "-" + paymentBrand + "-" + currency);
                    /*if(terminalVO!=null)
                        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                transactionLogger.error("terminalMap--->"+terminalMapLimitRouting);*/
                    if(terminalMapLimitRouting == null)
                    {
                        terminalMapLimitRouting = terminalManager.getTerminalFromCurrency(commonValidatorVO);
                        for (Map.Entry entry1 : terminalMapLimitRouting.entrySet())
                        {
                            String key  = (String)entry1.getKey();
                            transactionLogger.debug("Terminal Id::::" + key);
                        }
                        Date date3 = new Date();
                        transactionLogger.debug("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                        terminalMapLimitRouting = limitRouting.getTerminalVOBasedOnAmountLimitRouting(terminalMapLimitRouting, commonValidatorVO);
                        transactionLogger.debug("TransactionHelper getTerminalVOBasedOnAmountLimitRouting end #########" + new Date().getTime());
                        transactionLogger.debug("TransactionHelper getTerminalVOBasedOnAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                        /*if(terminalMapLimitRouting!=null && terminalMapLimitRouting.size()>0)
                            terminalVO = terminalMapLimitRouting.get(0);*/
                        commonValidatorVO.setTerminalMapLimitRouting(terminalMapLimitRouting);
                    }
                    if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                    {
                        Date date3 = new Date();
                        transactionLogger.debug("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting start #########" + date3.getTime());
                        terminalVO = limitRouting.getTerminalVOBasedOnCardAndCardAmountLimitRouting(terminalMapLimitRouting, commonValidatorVO);
                        transactionLogger.debug("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting end #########" + new Date().getTime());
                        transactionLogger.debug("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                    }

                    else{

                        for (Map.Entry entry2 : terminalMapLimitRouting.entrySet())
                        {
                            String terminalId   = (String) entry2.getKey();
                            terminalVO          = terminalMapLimitRouting.get(terminalId);
                            transactionLogger.error("Accountid:::" + terminalVO.getAccountId() + ",TerminalId:::::" + terminalVO.getTerminalId());
                            break;
                        }

                    }
                    if(terminalVO == null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error                   = "Terminal ID provided by you is not valid.";
                        errorCodeVO             = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AMOUNT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }

                    paymentType = Integer.parseInt(terminalVO.getPaymodeId());
                    cardType    = Integer.parseInt(terminalVO.getCardTypeId());

                    commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
                    commonValidatorVO.setCardType(terminalVO.getCardTypeId());
                    transactionLogger.debug("terminalVO.getTerminalId()-->" + terminalVO.getTerminalId());
                    commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                    commonValidatorVO.getMerchantDetailsVO().setAccountId(terminalVO.getAccountId());
                    commonValidatorVO.setTerminalVO(terminalVO);
                }
                else
                {
                    if (commonValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        terminalVO = (TerminalVO) terminalMap.get(paymentMode + "-" + paymentBrand + "-" + currency);
                        if (terminalVO == null)
                            terminalVO = (TerminalVO) terminalMap.get(paymentMode + "-" + paymentBrand + "-ALL");
                        commonValidatorVO.setTerminalVO(terminalVO);
                    }
                    else
                    {
                        transactionLogger.error(" single call inside else getMultiCurrencySupport ::::::"+terminalMap);
                        transactionLogger.error(" single call inside else mapkey ::::::"+paymentMode + "-" + paymentBrand + "-" + currency) ;

                        terminalVO = (TerminalVO) terminalMap.get(paymentMode + "-" + paymentBrand + "-" + currency);
                        commonValidatorVO.setTerminalVO(terminalVO);

                    }
                    transactionLogger.debug("single call terminalmap paymentMode  paymentMode currency-->" + paymentMode + "-" + paymentBrand + "-" + currency);
                    transactionLogger.debug("single call terminalmap -->" + terminalMap);
                  //  transactionLogger.error("single call terminalVO.getPaymodeId()) -->" + terminalVO.getPaymodeId());
                    transactionLogger.error("single call commonValidatorVO.getCardType -->" + commonValidatorVO.getCardType());
                    transactionLogger.error("single call terminal-->" + commonValidatorVO.getTerminalId() +"  accountid-->"+commonValidatorVO.getAccountId());

                    if(functions.isValueNull(commonValidatorVO.getTerminalId())&&functions.isValueNull(commonValidatorVO.getAccountId())){

                        transactionLogger.error(" single call inside if multibank list ::::::");

                        terminalVO= terminalManager.getMemberTerminalfromTerminal(commonValidatorVO.getTerminalId());
                        commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
                        terminalVO.setGateway(GatewayAccountService.getGatewayAccount(commonValidatorVO.getAccountId()).getGateway());
                        commonValidatorVO.setCardType(GatewayAccountService.getCardId(commonValidatorVO.getPaymentBrand()));
                    }
                    else{
                        transactionLogger.error(" single call inside else multibank list ::::::");
                        paymentType = Integer.parseInt(terminalVO.getPaymodeId());
                        cardType    = Integer.parseInt(terminalVO.getCardTypeId());
                        commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
                        commonValidatorVO.setCardType(terminalVO.getCardTypeId());
                    }

                }

            }
            if(terminalVO!=null)
            {

                transactionLogger.debug("paymode enum------------------ line 366-------"+String.valueOf(PaymentModeEnum.OneRoad.getValue()));
                transactionLogger.debug("Terminal id---" + terminalVO.getTerminalId());
                commonValidatorVO.setTerminalId(terminalVO.getTerminalId());

            }
            limitChecker.checkTransactionAmountNew(commonValidatorVO.getTransDetailsVO().getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

            commonValidatorVO.getMerchantDetailsVO().setAccountId(terminalVO.getAccountId());
            commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());

            fromType 				= terminalVO.getGateway();
            fromID 					= terminalVO.getMemberId();
            GatewayAccount account 	= GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
            GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
            tableName 				= Database.getTableName(gatewayType.getGateway());
            commonValidatorVO.getTransDetailsVO().setFromtype(fromType);
            commonValidatorVO.getTransDetailsVO().setFromid(fromID);

            error = singleCallPaymentDAO.transactionExistCheck(tableName, trackingid, commonValidatorVO);
            if (!functions.isEmptyOrNull(error))
            {
                if(JPBTPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(account.getGateway()))
                    commonValidatorVO.getTransDetailsVO().setNotificationUrl("");
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(error);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseDBViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZDBExceptionEnum.SQL_EXCEPTION, errorCodeListVO, null, null);
            }

            GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            pg 											= AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if(commonValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport().equalsIgnoreCase("Y"))
                genericTransDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            else
                genericTransDetailsVO.setCurrency(pg.getCurrency());
            commonValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            auditTrailVO.setActionExecutorName("Customer");
            if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.getValue() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType) && CardTypeEnum.CUP_CARDTYPE.getValue() == cardType)
            {
                detailId = paymentManager.insertAuthStartedTransactionEntryForCup(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
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
                CupUtils cupUtils   = new CupUtils();
                String html         = cupUtils.getCupRequest(commonValidatorVO, detailId);
                pWriter.println(html);
                return;
            }
            else if (String.valueOf(PaymentModeEnum.CupUpi.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.CupUPI.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside CupUpi ---");
                AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error               = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = commonInputValidator.performStandardKitStep2Validations(commonValidatorVO);
                transactionLogger.debug("Inside CupUpi ---");
                if (!functions.isEmptyOrNull(error))
                {
                    transactionLogger.debug("Inside error ---");
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                try
                {
                    transactionHelper.performCommonSystemChecksStep2(commonValidatorVO);
                }
                catch (PZConstraintViolationException cve)
                {
                    PZExceptionHandler.handleCVEException(cve, toid, PZOperations.STANDARDKIT_SALE);
                    error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
                    log.error("----PZConstraintViolationException for SystemChecksStep2-----", cve);
                    transactionLogger.debug("error in singkecall ----------------"+error);

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
                    HashMap paymentTypeMap = new HashMap();

                    if(session.getAttribute("paymentMap")!=null)
                        paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                    if(paymentTypeMap==null || paymentTypeMap.isEmpty())
                    {
                        transactionLogger.debug("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                        List<String> cardList = new ArrayList<String>();
                        cardList.add(commonValidatorVO.getCardType());
                        paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);
                        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

                        transactionLogger.debug("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

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
                    if(session.getAttribute("paymentMap")!=null)
                        paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                    if(paymentTypeMap==null || paymentTypeMap.isEmpty())
                    {
                        transactionLogger.debug("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                        List<String> cardList = new ArrayList<String>();
                        cardList.add(commonValidatorVO.getCardType());
                        paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);
                        PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();
                        transactionLogger.debug("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

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
                CommRequestVO commRequestVO = null;
                UnionPayInternationalResponseVO transRespDetails=null;
                UnionPayInternationalUtility unionPayInternationalUtility=new UnionPayInternationalUtility();
                commRequestVO= unionPayInternationalUtility.getUnionPayRequestVO(commonValidatorVO);
                UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
                UnionPayInternationalPaymentGateway unionPayInternationalPaymentGateway=new UnionPayInternationalPaymentGateway("");
                String cardNumber=commRequestVO.getCardDetailsVO().getCardNum();
                String phoneNumber=commRequestVO.getAddressDetailsVO().getPhone(); // Here phoneNumber=telcc + telNo
                transactionLogger.debug("cardnumber ---" + cardNumber);
                transactionLogger.debug("phoneNumber ---" + phoneNumber);

                //Step 1 ::: Bin Check from UPI BIN file:::
                String firstSix = functions.getFirstSix(cardNumber);
                transactionLogger.debug("First Six ---" + firstSix);
                boolean checkBin_Result=unionPayInternationalUtils.checkBinFromFile(firstSix);
                transactionLogger.debug("checkBin_Result ---------" + checkBin_Result);
                String responseStatus="";
                if (checkBin_Result)  // If China Card
                {
                    // check in bin_upi_details if card is present or not, if present call purchase if not call SMS+Enrollment
                    //Step 1 ::: Enrollment Check From Our DB :::
                    boolean isEnrolled_Result=unionPayInternationalUtils.isEnrolledCard(cardNumber,phoneNumber);
                    transactionLogger.debug("isEnrolled_Result ----------" + isEnrolled_Result);

                    if (isEnrolled_Result) // already enrolled card direct purchase
                    {
                        transactionLogger.debug("Inside if isEnrolled_Result ----" + isEnrolled_Result);
                        paymentManager.insertAuthStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO);
                        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        }
                        //unionPayInternationalPaymentProcess.actionEntryExtension(detailId, trackingid, commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,transRespDetails,commRequestVO );
                        if (merchantDetailsVO.getIsService().equalsIgnoreCase("N")) {
                            transRespDetails = (UnionPayInternationalResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                            responseStatus = "authsuccessful";
                        }
                        else {
                            transRespDetails = (UnionPayInternationalResponseVO) pg.processSale(trackingid, commRequestVO);
                            responseStatus = "capturesuccess";
                        }

                        transactionLogger.debug("purchase responce after card enrollment check ----" + transRespDetails.getStatus());
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                    }
                    else // not enrolled card call sms+enrollment
                    {
                        transactionLogger.debug("Inside else  isEnrolled_Result ----" + isEnrolled_Result);
                        //  added  sms started entry
                        paymentManager.insertSMSStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO); //smsstared entry
                        //Step 3 ::: Process SMS :::
                        transRespDetails = (UnionPayInternationalResponseVO) unionPayInternationalPaymentGateway.processSendSMS(trackingid,commRequestVO);
                        transactionLogger.debug("sms sending status ---------" + transRespDetails.getStatus());

                        if (functions.isValueNull(transRespDetails.getStatus()) && transRespDetails.getStatus().equalsIgnoreCase("success")) {
                            //added -  SMS screen display to customer
                            req.setAttribute("transDetails", commonValidatorVO);
                            req.setAttribute("trackingid", trackingid);
                            req.setAttribute("unionPayInternationalRequestVO", commRequestVO);
                            String CupUpiPage="/CupUpiSMS.jsp?";
                            RequestDispatcher rd = req.getRequestDispatcher(CupUpiPage);
                            rd.forward(req, res);
                            return;
                        }
                        else {
                            transRespDetails.setStatus("fail");
                        }
                    }
                }
                else  // Other Than China Card direct purchase
                {
                    paymentManager.insertAuthStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    // unionPayInternationalPaymentProcess.actionEntryExtension(detailId, trackingid, commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,transRespDetails,commRequestVO );
                    if (merchantDetailsVO.getIsService().equalsIgnoreCase("N")) {
                        transRespDetails = (UnionPayInternationalResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                        responseStatus = "authsuccessful";
                    }
                    else {
                        transRespDetails = (UnionPayInternationalResponseVO) pg.processSale(trackingid, commRequestVO);
                        responseStatus="capturesuccess";
                    }
                    //paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                }

                if (transRespDetails != null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                        if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                            mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                        }
                        else {
                            mailtransactionStatus = "Successful";
                        }
                        if (functions.isValueNull(transRespDetails.getDescriptor())) {
                            billingDiscriptor = transRespDetails.getDescriptor();
                        }
                        else {
                            billingDiscriptor = pg.getDisplayName();
                        }
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                    {
                        responseStatus="authfailed";
                        paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                        log.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                        transactionLogger.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                        if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                            mailtransactionStatus = "Failed (" + transRespDetails.getDescription() + ")";
                        }
                        else {
                            mailtransactionStatus = "Failed";
                        }
                    }
                }
            }
            else if ((String.valueOf(PaymentModeEnum.ES.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.INSTANTPAYMENT.getValue()).equals(terminalVO.getCardTypeId())) || (String.valueOf(PaymentModeEnum.ES.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.STANDINGORDERS.getValue()).equals(terminalVO.getCardTypeId())) || (String.valueOf(PaymentModeEnum.ES.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.SCHEDULEDPAYMENT.getValue()).equals(terminalVO.getCardTypeId())) || (String.valueOf(PaymentModeEnum.ES.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.PAYBYLINK.getValue()).equals(terminalVO.getCardTypeId())))
            {
                transactionLogger.debug("Inside ECOSPEND ---");
                AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
                commonValidatorVO.setCardType(terminalVO.getCardTypeId());
                commonValidatorVO.getTransDetailsVO().setFromtype(fromType);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
                //  commonValidatorVO = commonInputValidator.performStandardKitStep2Validations(commonValidatorVO);
                transactionLogger.debug("Inside ECOSPEND ---"+error);
                if (!functions.isEmptyOrNull(error))
                {
                    transactionLogger.debug("Inside error ---");
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                EcospendResponseVO transRespDetails = null;
                EcospendRequestVo commRequestVO     = null;
                commRequestVO       = (EcospendRequestVo)getCommonRequestVO(commonValidatorVO, fromType);
                transRespDetails    = (EcospendResponseVO) pg.processInitialSale(trackingid, commRequestVO);
                TreeMap<String ,String> BankidList = transRespDetails.getBankid();

                commRequestVO.setAccessTken(transRespDetails.getAccessTken());
                commRequestVO.setCreditorID(transRespDetails.getCreditorID());
                commRequestVO.setCreditorName(transRespDetails.getCreditorName());
                commRequestVO.setCreditorBic(transRespDetails.getCreditorBic());
                transactionLogger.debug("Inside transRespDetails.getAccessTken() ---"+ transRespDetails.getAccessTken());

                String responseStatus="";
                if(functions.isValueNull(transRespDetails.getAccessTken())){

                    req.setAttribute("transDetails", commonValidatorVO);
                    req.setAttribute("trackingid", trackingid);
                    req.setAttribute("EcospendRequestVo", commRequestVO);
                    req.setAttribute("BankidList", BankidList);
                    String CupUpiPage="/EcospendBankList.jsp?";
                    RequestDispatcher rd = req.getRequestDispatcher(CupUpiPage);
                    rd.forward(req, res);
                    return;
                }
                else  // Other Than China Card direct purchase
                {
                    paymentManager.insertAuthStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    // unionPayInternationalPaymentProcess.actionEntryExtension(detailId, trackingid, commonValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,transRespDetails,commRequestVO );
                    if (merchantDetailsVO.getIsService().equalsIgnoreCase("N")) {
                        transRespDetails = (EcospendResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                        responseStatus = "authsuccessful";
                    }
                    else {
                        transRespDetails = (EcospendResponseVO) pg.processSale(trackingid, commRequestVO);
                        responseStatus="capturesuccess";
                    }
                    //paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                }

                if (transRespDetails != null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                        if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                            mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                        }
                        else {
                            mailtransactionStatus = "Successful";
                        }
                        if (functions.isValueNull(transRespDetails.getDescriptor())) {
                            billingDiscriptor = transRespDetails.getDescriptor();
                        }
                        else {
                            billingDiscriptor = pg.getDisplayName();
                        }
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                    {
                        responseStatus="authfailed";
                        paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        // paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                        log.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                        transactionLogger.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                        if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                            mailtransactionStatus = "Failed (" + transRespDetails.getDescription() + ")";
                        }
                        else {
                            mailtransactionStatus = "Failed";
                        }
                    }
                }
            }
            else if (String.valueOf(PaymentModeEnum.CupUpi.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.SecurePay.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside SecurePay ---");
                AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                transactionLogger.debug("Inside CupUpi 1");
                if (!functions.isEmptyOrNull(error))
                {
                    transactionLogger.debug("Here inside error null");
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                log.debug("----- inside SecurePay ------- ");

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                String html = pg.processAutoRedirect(commonValidatorVO);
                log.debug("html------------" + html);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && CardTypeEnum.PAYSAFECARD_CARDTYPE.getValue() == cardType)
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                paymentManager.insertAuthStartedTransactionEntryForPaySafeCard(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                PaySafeCardUtils paySafeCardUtils=new PaySafeCardUtils();
                commRequestVO = paySafeCardUtils.getPaySafeRequestVO(commonValidatorVO);
                try
                {
                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    else
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                catch (PZGenericConstraintViolationException gce)
                {
                    mailtransactionStatus = gce.getMessage();
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                if (transRespDetails != null)
                {
                    log.debug("paysafecard res-----> " + transRespDetails.getStatus() + "---" + transRespDetails.getDescription() + "---" + transRespDetails.getErrorCode());
                    transactionLogger.debug("paysafecard res-----> " + transRespDetails.getStatus() + "---" + transRespDetails.getDescription() + "---" + transRespDetails.getErrorCode());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                    {

                        /*String html=generateAutoSubmitFormForPaySafeCard(redirectUrl);
                        pWriter.println(html);
                        return;*/
                        res.sendRedirect(transRespDetails.getRedirectUrl());
                        return;
                    }else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        mailtransactionStatus=transRespDetails.getDescription();
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    }
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && (CardTypeEnum.NEOSURF.getValue() == cardType))
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills=new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && (CardTypeEnum.PURPLEPAY.getValue() == cardType))
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue() == paymentType && (CardTypeEnum.AVISA.getValue() == cardType) || (CardTypeEnum.AMC.getValue() == cardType))
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && (CardTypeEnum.FLUTTER.getValue() == cardType))
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.GIROPAY.getValue() == cardType)
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills=new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.getValue()==paymentType && CardTypeEnum.AllPay88.getValue()==cardType)
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
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                CommRequestVO commRequestVO = null;
                AllPay88ResponseVO transRespDetails = null;
                AllPay88Utils allPay88Utils = new AllPay88Utils();
                commRequestVO = allPay88Utils.getAllPay88RequestVO(commonValidatorVO);


                transRespDetails =(AllPay88ResponseVO) pg.processSale(trackingid, commRequestVO);
                String rpStatus="";
                if (transRespDetails != null)
                {
                    String responseStatus="";
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        rpStatus= "success";
                        responseStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        if (functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDiscriptor = transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDiscriptor = pg.getDisplayName();
                        }
                        commonValidatorVO.getReserveField2VO().setBankName(transRespDetails.getBankflag());
                        commonValidatorVO.getReserveField2VO().setCardnumber(transRespDetails.getCardnumber());
                        commonValidatorVO.getReserveField2VO().setLocation(transRespDetails.getLocation());
                        commonValidatorVO.getReserveField2VO().setCardname(transRespDetails.getCardname());

                        transactionLogger.debug("bankName-----"+commonValidatorVO.getReserveField2VO().getBankName());
                        transactionLogger.debug("CardNumber-----"+commonValidatorVO.getReserveField2VO().getCardnumber());
                        transactionLogger.debug("Location-----"+commonValidatorVO.getReserveField2VO().getLocation());
                        transactionLogger.debug("Cardname-----"+commonValidatorVO.getReserveField2VO().getCardname());
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        if(functions.isValueNull(transRespDetails.getRemark())){
                            rpStatus=transRespDetails.getRemark();
                        }else {
                            rpStatus="Failed";
                        }
                        mailtransactionStatus = "Failed";
                        billingDiscriptor = "";
                        responseStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    }

                    paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_AllPay88.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType && (CardTypeEnum.QIWI.getValue() == cardType || CardTypeEnum.YANDEX.getValue() == cardType))
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if(String.valueOf(PaymentModeEnum.WALLET_PAYMODE.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.PaySend.getValue()).equals(terminalVO.getCardTypeId())))
            {
                log.debug("----- inside PaySend ------- ");

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                String html= pg.processAutoRedirect(commonValidatorVO);
                log.debug("html------------"+html);
                pWriter.println(html);
                return;

            }

            else if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && CardTypeEnum.VISA_CARDTYPE.getValue() == cardType)
            {
                CommonPaymentProcess paymentProcess=new CommonPaymentProcess();
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                CommRequestVO commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
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
                }
                else
                {
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
            else if (PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType && CardTypeEnum.SKRILL.getValue() == cardType)
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                paymentManager.insertSkrillNetellerDetailEntry(commonValidatorVO,"transaction_skrill_details");
                SkrillUtills skrillUtills=new SkrillUtills();
                CommRequestVO commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);
                GenericResponseVO genericResponseVO = pg.processSale(trackingid, commRequestVO);
                CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                String html = SkrillUtills.generateAutoSubmitForm(commResponseVO.getResponseHashInfo(), commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType && CardTypeEnum.JETON.getValue() == cardType)

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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                SkrillUtills skrillUtills=new SkrillUtills();
                CommRequestVO commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);
                GenericResponseVO genericResponseVO = pg.processSale(trackingid, commRequestVO);
                CommResponseVO commResponseVO = (CommResponseVO) genericResponseVO;
                String html = JetonUtils.generateAutoSubmitForm(commResponseVO.getRedirectUrl(), commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if(PaymentModeEnum.WALLET_PAYMODE.getValue()==paymentType && CardTypeEnum.NETELLER.getValue()==cardType)
            {
                //InvoiceEntry invoiceEntry = new InvoiceEntry();
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
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if ((PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType || PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType || PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType) && (CardTypeEnum.PERFECTMONEY.getValue() == cardType ))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                PerfectMoneyUtils perfectMoneyUtils = new PerfectMoneyUtils();
                String html = perfectMoneyUtils.generateAutoSubmitForm(commonValidatorVO);
                transactionLogger.debug("PerfectMoney request form :::::"+html);
                transactionLogger.debug("PerfectMoney request form :::::");
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && CardTypeEnum.VOUCHERMONEY.getValue() == cardType)
            {
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                //String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in SinglecallPayment---" + addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    errorCodeListVO=commonValidatorVO.getErrorCodeListVO();
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                PaymentManager.insertAuthStartedTransactionEntryForVoucherMoney(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if (FlexepinPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) && PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && CardTypeEnum.FLEXEPIN_VOUCHER.getValue() == cardType)
            {
                String voucherPin                           = req.getParameter("voucherPin");
                Comm3DResponseVO transRespDetails           = null;
                Comm3DRequestVO comm3DRequestVO             = new Comm3DRequestVO();
                CommCardDetailsVO commCardDetailsVO         = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                HashMap<String,String> hashMap              = new HashMap<>();
                pg 											= AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commCardDetailsVO.setVoucherNumber(voucherPin);
                commAddressDetailsVO.setCardHolderIpAddress(customerIpAddress);
                commTransactionDetailsVO.setTotype(commonValidatorVO.getTransDetailsVO().getTotype());
                commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());

                comm3DRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                comm3DRequestVO.setCardDetailsVO(commCardDetailsVO);
                comm3DRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                commonValidatorVO.getAddressDetailsVO().setCardHolderIpAddress(customerIpAddress);
                commonValidatorVO.getCardDetailsVO().setVoucherNumber(voucherPin);

                transactionLogger.error(trackingid + " --> voucherPin: "+ functions.maskingPan(voucherPin));
                transactionLogger.error(trackingid + " --> voucherPin from VO: "+ functions.maskingPan(commCardDetailsVO.getVoucherNumber()));

                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
//                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.error("address validatin in SinglecallPayment099---" + addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    errorCodeListVO=commonValidatorVO.getErrorCodeListVO();
                    errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                PaymentManager.insertAuthStartedTransactionEntryForFlexepinVoucher(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());

                if (merchantDetailsVO.getIsService().equalsIgnoreCase("N"))
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, comm3DRequestVO);
                else
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, comm3DRequestVO);

                if(transRespDetails != null)
                {
                    transactionLogger.error("status form response----> " + transRespDetails.getStatus());
                    if (functions.isValueNull(transRespDetails.getDescriptor()))
                        billingDiscriptor = transRespDetails.getDescriptor();
                    else
                        billingDiscriptor = pg.getDisplayName();

                    if (functions.isValueNull(billingDiscriptor))
                        transRespDetails.setDescriptor(billingDiscriptor);

                    transactionLogger.error("transRespDetails.getDescriptor(): "+ transRespDetails.getDescriptor());
                    transactionLogger.error("transRespDetails.getAmount(): "+ transRespDetails.getAmount());

                    respPaymentId   = transRespDetails.getTransactionId();
                    hashMap = (HashMap<String, String>) transRespDetails.getRequestMap();

                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        respStatus              = "capturesuccess";
                        paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        paymentManager.updateCapturesuccessForFlexepinVoucher(hashMap,transRespDetails,transRespDetails.getTransactionId(), trackingid,"capturesuccess", transRespDetails.getAmount());
                        mailtransactionStatus   = "Successful";
                        FlexepinUtils.updateBillingDescriptor(billingDiscriptor,trackingid);
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        respStatus = "authfailed";
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        paymentManager.updateCapturesuccessForFlexepinVoucher(hashMap,transRespDetails,transRespDetails.getTransactionId(), trackingid,"authfailed", transRespDetails.getAmount());
                        mailtransactionStatus = "Failed";
                    }
                    else
                    {
                        mailtransactionStatus = "Pending";
                        billingDiscriptor = "";
                        respStatus = "authstarted";
                    }
                }

                if(functions.isValueNull(transRespDetails.getAmount()))
                    commonValidatorVO.getTransDetailsVO().setAmount(transRespDetails.getAmount());

                transactionLogger.error("commonValidatorVO.getTransDetailsVO().getNotificationUrl()-->" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                transactionLogger.error("TransactionNotification flag for ---" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + merchantDetailsVO.getTransactionNotification());
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())&& ("Non-3D".equals(merchantDetailsVO.getTransactionNotification()) || "Both".equals(merchantDetailsVO.getTransactionNotification())) && !"authstarted".equalsIgnoreCase(respStatus))
                {
                    transactionLogger.debug("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                    commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                    TransactionDetailsVO transactionDetailsVO1 	= transactionUtility.getTransactionDetails(commonValidatorVO);
                    if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                    }else{
                        transactionDetailsVO1.setMerchantNotificationUrl("");
                    }
                    transactionDetailsVO1.setBankReferenceId(respPaymentId);
                    AsyncNotificationService asyncNotificationService 	= AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, commonValidatorVO.getTrackingid(), respStatus, respRemark, "");
                }

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("transRespDetails", transRespDetails);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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
            else if (SmartFastPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) && PaymentModeEnum.SMARTFASTPAY.getValue() == paymentType
                    && (CardTypeEnum.TED.getValue() == cardType || CardTypeEnum.PICPAY.getValue() == cardType
                    || CardTypeEnum.BOLETO.getValue() == cardType || CardTypeEnum.PIX.getValue() == cardType))
            {
                String document                           = req.getParameter("document");
                transactionLogger.debug("********** Inside SmartFastPay *********** ");

                CommRequestVO commRequestVO             = null;
                Comm3DResponseVO transRespDetails       = null;
                boolean isTest                          = account.isTest();
                CommCardDetailsVO commCardDetailsVO     = null;
                HashMap<String,String> requestParamMap  = null;

                AbstractPaymentProcess commonPaymentProcess  = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                String responseStatus       = "";
                String responseRemark       = "";

                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess       = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                    = commonValidatorVO.getTerminalVO().getAddressValidation();
                commonValidatorVO.setCardType(cardType+"");
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                if(commRequestVO.getCardDetailsVO() != null){
                    commCardDetailsVO = commRequestVO.getCardDetailsVO();
                    commCardDetailsVO.setAccountNumber(document);
                }else{
                    commCardDetailsVO = new CommCardDetailsVO();
                    commCardDetailsVO.setAccountNumber(document);
                }
                commRequestVO.setCardDetailsVO(commCardDetailsVO);

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }

                transactionLogger.error("fromType ----> "+trackingid+" "+fromType+" cardType >>> "+cardType);

                if (transRespDetails != null)
                {
                    responseStatus = transRespDetails.getStatus();
                    responseRemark = transRespDetails.getRemark();
                    transactionLogger.error("responseStatus --> "+trackingid+" "+responseStatus+" responseRemark "+responseRemark);

                    if(transRespDetails.getRequestMap() != null){
                        requestParamMap = (HashMap<String, String>) transRespDetails.getRequestMap();
                    }
                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);

                    if (responseStatus.equalsIgnoreCase("success"))
                    {
                        //paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                        commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());

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

                    }else if (responseStatus.equalsIgnoreCase("failed"))
                    {
                        transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                        req.setAttribute("responceStatus", "fail");
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        mailtransactionStatus = transRespDetails.getStatus().trim();
                    }else if(responseStatus.equalsIgnoreCase("pending3DConfirmation")){
                        String html = commonPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails);
                        transactionLogger.debug("Html in processAutoRedirect -------" + html);
                        pWriter.println(html);
                        return;
                    }else if(responseStatus.equalsIgnoreCase("pending") && requestParamMap != null && !requestParamMap.isEmpty() ){

                        transRespDetails.getRequestMap();
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("transRespDetails", transRespDetails);
                        req.setAttribute("displayName", billingDiscriptor);
                        req.setAttribute("requestParamMap", requestParamMap);
                        req.setAttribute("ctoken", ctoken);
                        req.setAttribute("cardType", cardType);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_SmartFastPay.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    else
                    {
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }
                }
                else
                {
                    billingDiscriptor       = pg.getDisplayName();
                    mailtransactionStatus   = "pending";
                }

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    req.setAttribute("responceStatus", mailtransactionStatus);
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
            else if (String.valueOf(PaymentModeEnum.BITCOIN.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.BCPAYGATE.getValue()).equals(terminalVO.getCardTypeId()) || String.valueOf(CardTypeEnum.BITCLEAR.getValue()).equals(terminalVO.getCardTypeId())))
            {
                transactionLogger.debug("Inside BITCOIN ----------------------");
                CommRequestVO commRequestVO         = null;
                Comm3DResponseVO transRespDetails   = null;

                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                AbstractPaymentProcess commonPaymentProcess  = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);

                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                BitcoinPaygateUtils bitcoinPaygateUtils = new BitcoinPaygateUtils();
                BitClearUtils bitClearUtils             = new BitClearUtils();

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                if("bitcoinpg".equalsIgnoreCase(fromType)){
                    commRequestVO = bitcoinPaygateUtils.getCommRequestFromUtils(commonValidatorVO);
                }else if("bitclear".equalsIgnoreCase(fromType)){
                    commRequestVO = bitClearUtils.getBitClearGRequestVO(commonValidatorVO);
                }

                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }else{
                    transRespDetails    = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }



                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    String html = "";
                    transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                    paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());

                    if("bitcoinpg".equalsIgnoreCase(fromType)){
                        BitcoinPaygateUtils.updateResponseHashInfo(transRespDetails, commonValidatorVO.getTrackingid());
                        html = bitcoinPaygateUtils.getRedirectForm(trackingid,transRespDetails);
                    }else if("bitclear".equalsIgnoreCase(fromType)){
                        BitClearUtils.updateResponseHashInfo(transRespDetails, commonValidatorVO.getTrackingid());
                        html = commonPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                    }

                   /* BitcoinPaygateUtils.updateResponseHashInfo(transRespDetails, commonValidatorVO.getTrackingid());
                    String html = bitcoinPaygateUtils.getRedirectForm(trackingid,transRespDetails);*/
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                    return;
                }else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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
            else if (String.valueOf(PaymentModeEnum.DOKU.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.DOKU.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside DOKU ----------------------");

                DokuUtils dokuUtils                 = new DokuUtils();
                CommRequestVO commRequestVO         = null;
                CommResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in Doku SinglecallPayment---" + addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = dokuUtils.getCommRequestFromUtils(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                    String html = DokuPaymentProcess.getPaymentForm(transRespDetails.getRedirectUrl(), isTest);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                    return;
                }
                else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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

            else if ("airteluganda".equalsIgnoreCase(fromType) && String.valueOf(CardTypeEnum.Airtel_Uganda.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside Airtel Uganda ----------- ");

                AirtelUgandaUtils airtelUgandaUtils = new AirtelUgandaUtils();
                CommRequestVO commRequestVO         = null;
                CommResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in Airtel Uganda SinglecallPayment---" + addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = airtelUgandaUtils.getCommRequestFromUtils(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                {
                    paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                    if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                        mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                    }
                    else {
                        mailtransactionStatus = "Successful";
                    }
                    if (functions.isValueNull(transRespDetails.getDescriptor())) {
                        billingDiscriptor = transRespDetails.getDescriptor();
                    }
                    else {
                        billingDiscriptor = pg.getDisplayName();
                    }
                }
                else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                else
                {
                    billingDiscriptor       = pg.getDisplayName();
                    mailtransactionStatus   = "pending";
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            }

            else if ("mtnuganda".equalsIgnoreCase(fromType) && String.valueOf(CardTypeEnum.MTN_Uganda.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside MTN Uganda ----------- ");

                MtnUgandaUtils mtnUgandaUtils = new MtnUgandaUtils();
                CommRequestVO commRequestVO         = null;
                CommResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in MTN Uganda SinglecallPayment---" + addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = mtnUgandaUtils.getCommRequestFromUtils(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                {
                    paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                    if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals("")) {
                        mailtransactionStatus = "Successful (" + transRespDetails.getDescription() + ")";
                    }
                    else {
                        mailtransactionStatus = "Successful";
                    }
                    if (functions.isValueNull(transRespDetails.getDescriptor())) {
                        billingDiscriptor = transRespDetails.getDescriptor();
                    }
                    else {
                        billingDiscriptor = pg.getDisplayName();
                    }
                }
                else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                else
                {
                    billingDiscriptor       = pg.getDisplayName();
                    mailtransactionStatus   = "pending";
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            }

            else if ("transfr".equalsIgnoreCase(fromType) && String.valueOf(CardTypeEnum.Transfr.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside Transfr ----------- ");

                TransfrUtils transfrUtils = new TransfrUtils();
                CommRequestVO commRequestVO         = null;
                Comm3DResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("inside transfr SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in Transfr SinglecallPayment---"+addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = transfrUtils.getTransfrRequestVO(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);

                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }

                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    TransfrPaymentProcess transfrPaymentProcess  = new TransfrPaymentProcess();
                    String html = transfrPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                    return;
                }
                else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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

            else if((fromType.equalsIgnoreCase("tigerpay") && String.valueOf(PaymentModeEnum.TIGERPAY.getValue()).equalsIgnoreCase(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.TigerPayWallet.getValue()).equalsIgnoreCase(terminalVO.getCardTypeId()))
                    || (fromType.equalsIgnoreCase("tigergate") && String.valueOf(PaymentModeEnum.TIGERPAY.getValue()).equalsIgnoreCase(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.JPBANK.getValue()).equalsIgnoreCase(terminalVO.getCardTypeId()))
                    )
            {
                transactionLogger.debug("Inside Tiger pay And Tiger gateway ----------------------");

                TigerPayUtils       tigerPayUtils             = new TigerPayUtils();
                TigerGateWayUtils   tigerGateWayUtils         = new TigerGateWayUtils();
                CommRequestVO commRequestVO                   = null;
                Comm3DResponseVO transRespDetails             = null;
                boolean isTest                                = account.isTest();
                AbstractPaymentProcess commonPaymentProcess  = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                String responseStatus       = "";
                String responseRemark       = "";

                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess       = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                    = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in TIGERPAY SinglecallPayment---"+addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                if(fromType.equalsIgnoreCase("tigerpay")){
                    commRequestVO = tigerPayUtils.getTigerPayRequestVO(commonValidatorVO);
                }else{
                    commRequestVO        = tigerGateWayUtils.getTigerPayRequestVO(commonValidatorVO);
                }

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (TigerPayResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (TigerPayResponseVO) pg.processSale(trackingid, commRequestVO);
                }

                transactionLogger.error("fromType ----> "+trackingid+" "+fromType);

                if (transRespDetails != null)
                {
                    responseStatus = transRespDetails.getStatus();
                    responseRemark = transRespDetails.getRemark();
                    transactionLogger.error("responseStatus --> "+trackingid+" "+responseStatus+" responseRemark "+responseRemark);

                    if (responseStatus.equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                        commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());

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

                    }else if (responseStatus.equalsIgnoreCase("failed"))
                    {
                        transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                        req.setAttribute("responceStatus", "fail");
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        mailtransactionStatus = transRespDetails.getStatus().trim();
                    }else if(responseStatus.equalsIgnoreCase("pending3DConfirmation")){
                        String html = commonPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails);
                        transactionLogger.debug("Html in processAutoRedirect -------" + html);
                        pWriter.println(html);
                        return;
                    }else if(responseStatus.equalsIgnoreCase("pending") && responseRemark.equalsIgnoreCase("applying")){
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("transRespDetails", transRespDetails);
                        req.setAttribute("displayName", billingDiscriptor);
                        req.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_TigerPay.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    else
                    {
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }
                }
                else
                {
                    billingDiscriptor       = pg.getDisplayName();
                    mailtransactionStatus   = "pending";
                }

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    req.setAttribute("responceStatus", mailtransactionStatus);
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
           /* else if ((String.valueOf(PaymentModeEnum.CARD.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.CARDS.getValue()).equals(terminalVO.getCardTypeId()))
                    || (String.valueOf(PaymentModeEnum.BDMobileBanking.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.MOBILEBANKING.getValue()).equals(terminalVO.getCardTypeId()))
                    )*/
            else if((fromType.equalsIgnoreCase("aamarpay") && (String.valueOf(PaymentModeEnum.CARD.getValue()).equals(terminalVO.getPaymodeId()) || String.valueOf(PaymentModeEnum.BDMobileBanking.getValue()).equals(terminalVO.getPaymodeId()))))
            {
                transactionLogger.debug("Inside BDPAYMENT ---");
              //  AamarPayResponseVO transRespDetails = null;
                Comm3DResponseVO transRespDetails     = null;

                CommRequestVO commRequestVO = null;
                AamarPayUtils aamarPayUtils = new AamarPayUtils();

                //merchantDetailsVO                               = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator abstractInputValidator   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error                                           = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");


                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                commonValidatorVO = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                commRequestVO       = (CommRequestVO)aamarPayUtils.getAamarPayRequestVO(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                AbstractPaymentProcess commonPaymentProcess = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                if (merchantDetailsVO.getIsService().equalsIgnoreCase("N")) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }

                String form = "";
                if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                {

                    form = commonPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);


                    transactionLogger.debug("form in BD mobilebanking call-----------" + form);
                    pWriter.println(form);
                    return;
                }

                else if ("fail".equalsIgnoreCase(transRespDetails.getStatus()))
                {
                    String responseMessgage = transRespDetails.getDescription();

                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), responseMessgage);
                }

                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            else if (String.valueOf(PaymentModeEnum.CajaRural.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.CajaRural.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside CajaRural ----------------------");
                CajaRuralPaymentProcess cajaRuralPaymentProcess=new CajaRuralPaymentProcess();
                CajaRuralUtils cajaRuralUtils                 = new CajaRuralUtils();
                CommRequestVO commRequestVO         = null;
                Comm3DResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in CajaRural SinglecallPayment---"+addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = cajaRuralUtils.getCommRequestFromUtils(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    transactionLogger.debug("status  in if -------------------" + transRespDetails);

                }
                else {

                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    transactionLogger.debug("status in else -------------------" + transRespDetails);

                }
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails);

                    String html = cajaRuralPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    return;
                }
                else
                {
                    transactionLogger.debug("Inside Else===================>");
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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
            else if (String.valueOf(PaymentModeEnum.Wealthpay.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.eBanking.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside Wealthpay ----------------------");

                WealthPayUtils wealthPayUtils                 = new WealthPayUtils();
                CommRequestVO commRequestVO         = null;
                Comm3DResponseVO transRespDetails     = null;
                boolean isTest                      = account.isTest();
                transactionLogger.debug("is test in SingleCallCheckout = " + isTest);

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation                = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in Wealthpay SinglecallPayment---" + addressValidation);

                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

                if (error != null && !error.equals(""))
                {
                    errorCodeListVO     = commonValidatorVO.getErrorCodeListVO();
                    errorName           = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());

                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);

                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);

                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                commRequestVO        = wealthPayUtils.getWealthRequestVO(commonValidatorVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else {
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    WealthPayPaymentProcess wealthPayPaymentProcess  = new WealthPayPaymentProcess();
                    String html = wealthPayPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                    return;
                }
                else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                if("N".equalsIgnoreCase(merchantDetailsVO.getAutoRedirect()))
                {
                    req.setAttribute("transDetail", commonValidatorVO);

                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
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
            else if (String.valueOf(PaymentModeEnum.PAYG.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.DUSPAYDD.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside PAYG And DUSPAYDD----------------------");
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                DusPayDDUtils dusPayDDUtils=new DusPayDDUtils();
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = dusPayDDUtils.getCommRequestFromUtils(commonValidatorVO);
                transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------" + transRespDetails.getStatus());
                    String html = dusPayDDUtils.getRedirectForm(trackingid,transRespDetails);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    pWriter.println(html);
                }
                return;
            }
            else if (String.valueOf(PaymentModeEnum.ZOTA.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.ZOTA.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside ZOTA ---------------------");
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", "");
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }


                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                ZotapayUtils zotapayUtils = new ZotapayUtils();
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = zotapayUtils.getCommRequestFromUtils(commonValidatorVO);
                transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                    String html = zotapayUtils.getRedirectForm(transRespDetails);
                    transactionLogger.debug("Html in processAutoRedirect -------" + html);
                    req.setAttribute("responceStatus", "pending");
                    pWriter.println(html);
                    return;
                }else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", commonValidatorVO);

                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
// Condition for JP BankTransfer
            else if (String.valueOf(PaymentModeEnum.BankTransfer.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.JPBANK.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside JP BankTransfer ---------------------");

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                // PayBoutiqueUtils payBoutiqueUtils=new PayBoutiqueUtils();
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO transRespDetails = null;
                commRequestVO = JPBankTransferUtils.getCommRequestFromUtils(commonValidatorVO);
                transRespDetails = (JPBankTransferVO) pg.processSale(trackingid, commRequestVO);
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                    //  String html = payBoutiqueUtils.getRedirectForm(trackingid,transRespDetails);
                    req.setAttribute("responseStatus", "pending");
                    paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                }
                else
                {
                    req.setAttribute("responseStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("transRespDetails", transRespDetails);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout_JPBank.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else if (fromType.equalsIgnoreCase("Appletree") && String.valueOf(PaymentModeEnum.BankTransfer.getValue()).equals(terminalVO.getPaymodeId())
                    && (String.valueOf(CardTypeEnum.EcocashUSD.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.EcocashRTGS.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.Ecobank.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.NMB.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.OneMoneyUSD.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.OneMoneyRTGS.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.MTNMomo.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.PayDunya.getValue()).equals(terminalVO.getCardTypeId())
                    || String.valueOf(CardTypeEnum.CBZBank.getValue()).equals(terminalVO.getCardTypeId())))
            {
                transactionLogger.debug("Inside Appletree BankTransfer ---------------------");

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                // PayBoutiqueUtils payBoutiqueUtils=new PayBoutiqueUtils();
                CommRequestVO commRequestVO         = new CommRequestVO();
                CommResponseVO transRespDetails     = null;
                commonValidatorVO.getTransDetailsVO().setCardType(terminalVO.getCardTypeId());
                commRequestVO   = AppleTreeCellulantUtils.getCommRequestFromUtils(commonValidatorVO);
                if(merchantDetailsVO.getService().equalsIgnoreCase("N"))
                {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else
                {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    if(functions.isValueNull(transRespDetails.getRedirectUrl()))
                    {
                        transactionLogger.debug("In single call Appletree ");
                        transRespDetails.setThreeDVersion("Non-3D");
                        String Remark="";
                        if(functions.isValueNull(transRespDetails.getRemark())){
                            Remark=transRespDetails.getRemark();
                        }
                        paymentManager.updatePaymentRemarkforCommon(Remark, trackingid);
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        pWriter.println(transRespDetails.getRedirectUrl());
                        return;
                    }else{
                        transactionLogger.debug("In single call Appletree Query for paymet id " + transRespDetails.getTransactionId());
                        commRequestVO.getTransDetailsVO().setPreviousTransactionId(transRespDetails.getTransactionId());
                        String Remark=transRespDetails.getRemark();
                        transRespDetails = (CommResponseVO) pg.processQuery(trackingid, commRequestVO);
                        transRespDetails.setThreeDVersion("Non-3D");
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        if(functions.isValueNull(transRespDetails.getRemark())){
                            Remark=transRespDetails.getRemark();
                        }
                        paymentManager.updatePaymentRemarkforCommon(Remark, trackingid);
                        commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());
                        req.setAttribute("responceStatus", "pending");
                    }
                }
                else
                {
                    transactionLogger.debug("In single call Appletree failed::::::::::");
                    req.setAttribute("responceStatus", "fail");

                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("transRespDetails", transRespDetails);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            //appletree
            else if((fromType.equalsIgnoreCase("Appletree")) && (String.valueOf(PaymentModeEnum.BankTransferAfrica.getValue()).equals(terminalVO.getPaymodeId())
            || String.valueOf(PaymentModeEnum.MobileMoneyAfrica.getValue()).equals(terminalVO.getPaymodeId())
            || String.valueOf(PaymentModeEnum.GiftCardAfrica.getValue()).equals(terminalVO.getPaymodeId())
            || String.valueOf(PaymentModeEnum.WalletAfrica.getValue()).equals(terminalVO.getPaymodeId())
            ))
            {

                transactionLogger.debug("Inside Appletree --------"+paymentMode + " "+cardType);
                String financialServiceName = "";
                String paymentOtionCode     = "";
                String paymentRequestId     = "";
                //String ThreeDSVersion     = "";
                if(req.getParameter("financialServiceName") != null){
                    financialServiceName = req.getParameter("financialServiceName");
                    commonValidatorVO.setCustomerId(financialServiceName);
                }
                if(req.getParameter("paymentOtionCode") != null){
                    paymentOtionCode =  req.getParameter("paymentOtionCode");
                    commonValidatorVO.setProcessorName(paymentOtionCode);
                }
                /*if(req.getParameter("ThreeDSVersion") != null){
                    ThreeDSVersion = req.getParameter("ThreeDSVersion");
                    commonValidatorVO.setAttemptThreeD(ThreeDSVersion);
                }*/

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                // PayBoutiqueUtils payBoutiqueUtils=new PayBoutiqueUtils();
                CommRequestVO commRequestVO         = new CommRequestVO();
                Comm3DResponseVO transRespDetails     = null;
                commonValidatorVO.getTransDetailsVO().setCardType(terminalVO.getCardTypeId());
                commonValidatorVO.getTransDetailsVO().setPaymentType(commonValidatorVO.getPaymentType());

                commRequestVO       = AppleTreeCellulantUtils.getCommRequestFromUtils(commonValidatorVO);
                if(merchantDetailsVO.getService().equalsIgnoreCase("N"))
                {
                    transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                }
                else
                {
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                }
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    if(functions.isValueNull(transRespDetails.getRedirectUrl()))
                    {
                        transactionLogger.debug("In single call Appletree ");
                        transRespDetails.setThreeDVersion("Non-3D");
                        String Remark="";
                        if(functions.isValueNull(transRespDetails.getRemark())){
                            Remark=transRespDetails.getRemark();
                        }
                        paymentManager.updatePaymentRemarkforCommon(Remark, trackingid);
                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        pWriter.println(transRespDetails.getRedirectUrl());
                        return;
                    }else{

                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        if((String.valueOf(PaymentModeEnum.BankTransferAfrica.getValue()).equals(terminalVO.getPaymodeId())
                        || String.valueOf(PaymentModeEnum.MobileMoneyAfrica.getValue()).equals(terminalVO.getPaymodeId())
                        || String.valueOf(PaymentModeEnum.WalletAfrica.getValue()).equals(terminalVO.getPaymodeId()))
                                && transRespDetails.getUrlFor3DRedirect() != null ){
                            req.setAttribute("transDetail", commonValidatorVO);
                            transactionLogger.error(" Inside Payment Completion Instructions BankTransferAfrica " + trackingid + " " + transRespDetails.getUrlFor3DRedirect());
                            req.setAttribute("transRespDetails", transRespDetails);
                            req.setAttribute("displayName", billingDiscriptor);
                            req.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckoutBankTransfer.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }

                        transactionLogger.debug("In single call Appletree Query for paymet id " + transRespDetails.getTransactionId());
                        commRequestVO.getTransDetailsVO().setPreviousTransactionId(transRespDetails.getTransactionId());
                        String Remark       = transRespDetails.getRemark();
                        transRespDetails    = (Comm3DResponseVO) pg.processQuery(trackingid, commRequestVO);
                        transRespDetails.setThreeDVersion("Non-3D");


                        if(functions.isValueNull(transRespDetails.getRemark())){
                            Remark=transRespDetails.getRemark();
                        }
                        paymentManager.updatePaymentRemarkforCommon(Remark, trackingid);
                        commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());

                        if (transRespDetails.getRemark() != null && !transRespDetails.getRemark().equals(""))
                        {
                            mailtransactionStatus = "pending (" + transRespDetails.getRemark() + ")";
                        }
                        else
                        {
                            mailtransactionStatus = "pending";
                        }


                        req.setAttribute("responceStatus",mailtransactionStatus);
                    }
                }
                else if(transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("success")){
                    transactionLogger.debug("In single call Appletree success::::::::::");
                    req.setAttribute("responceStatus", "success");

                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                    paymentManager.updateTransactionForCommon(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                    commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());

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
                else
                {
                    transactionLogger.debug("In single call Appletree failed::::::::::");

                    if (transRespDetails.getRemark() != null && !transRespDetails.getRemark().equals(""))
                    {
                        mailtransactionStatus = "failed (" + transRespDetails.getRemark() + ")";
                    }
                    else
                    {
                        mailtransactionStatus = "failed";
                    }

                    req.setAttribute("responceStatus", mailtransactionStatus);
                    commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    commonValidatorVO.setDisplayCurrency(transRespDetails.getTmpl_Currency());
                    //mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("transRespDetails", transRespDetails);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
//            condition for PayBoutique
            else if (String.valueOf(PaymentModeEnum.PayBoutique.getValue()).equals(terminalVO.getPaymodeId()) && String.valueOf(CardTypeEnum.PayBoutique.getValue()).equals(terminalVO.getCardTypeId()))
            {
                transactionLogger.debug("Inside PayBoutique ---------------------");

                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                PayBoutiqueUtils payBoutiqueUtils=new PayBoutiqueUtils();
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = payBoutiqueUtils.getCommRequestFromUtils(commonValidatorVO);
                transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                {
                    transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                    String html = payBoutiqueUtils.getRedirectForm(trackingid,transRespDetails);

                    req.setAttribute("responceStatus", "pending");
                    pWriter.println(html);
                    return;
                }
                else
                {
                    req.setAttribute("responceStatus", "fail");
                    paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                    mailtransactionStatus = transRespDetails.getStatus().trim();
                }
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }


            else if (String.valueOf(PaymentModeEnum.CRYPTO_PAYMODE.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.ELEGRO.getValue()).equals(terminalVO.getCardTypeId())))
            {
                transactionLogger.debug("-----inside elegro----- ");
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validation in SingleCallCheckout---"+addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                String html= pg.processAutoRedirect(commonValidatorVO);
                pWriter.println(html);
                return;
            }

            else if (String.valueOf(PaymentModeEnum.ROMCARD.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.ROMCARD.getValue()).equals(terminalVO.getCardTypeId())))
            {
                transactionLogger.debug("-----inside Romcard EMIs----- ");
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validation in SingleCallCheckout---" + addressValidation);
                error = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);
                if (error != null && !error.equals(""))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    req.setAttribute("error", error);
                    HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                    commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                    req.setAttribute("transDetails", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
                String html= pg.processAutoRedirect(commonValidatorVO);
                pWriter.println(html);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.TRUSTLY.getValue() == cardType)
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                String addressValidation = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                //String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                log.debug("address validatin in SinglecallPayment---" + addressValidation);
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                {
                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                }
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
                    transactionLogger.debug("status----" + transRespDetails.getStatus());
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
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            else
            {
                if (PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType && CardTypeEnum.JETON_VOUCHER.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.INPAY_CARDTYPE.getValue() == cardType)
                {
                    //InvoiceEntry invoiceEntry = new InvoiceEntry();
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                else if (PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType && CardTypeEnum.EPAY.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForEpay(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    String html = EpayUtils.getEpayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }

                else if (fromType.equalsIgnoreCase(PayGSmilePaymentGateway.GATEWAY_TYPE) &&(PaymentModeEnum.BankTransfer.getValue() == paymentType || PaymentModeEnum.CASH.getValue() == paymentType || PaymentModeEnum.VOUCHERS_PAYMODE.getValue() == paymentType))
                {
                    String bank                                 = req.getParameter("bankName");
                    System.out.println("Bank in singlecheckout is >>>>>>>>>"+bank);
                    String identify_number                      = req.getParameter("number");
                    CommRequestVO commRequestVO                 = new CommRequestVO();
                    Comm3DResponseVO transRespDetails           = null;
                    HashMap<String,String> hashMap              = new HashMap<>();
                    pg                                                                                         = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    transactionLogger.error("********** Inside Paygsmile BankTransfer condition *********** ");

                    String addressValidation    = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error                       = paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", addressValidation);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                    transactionLogger.error("*** After Auth entry ***");
                    commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);

                    commRequestVO.getTransDetailsVO().setBankAccountNo(identify_number);
                    commRequestVO.getTransDetailsVO().setCustomerBankAccountName(bank);

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);

                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }


                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService())){
                        transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else{
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    }
                    if (transRespDetails != null)
                    {
                        if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                        {
                            String form ="";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            form = abstractPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                            transactionLogger.debug("form in PaygSmile BankTransfer  call---" + form);
                            pWriter.println(form);
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                            req.setAttribute("responceStatus", "fail");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = transRespDetails.getStatus().trim();
                        }
                        else
                        {
                            billingDiscriptor       = pg.getDisplayName();
                            mailtransactionStatus   = "pending";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        }
                    }else{
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if(PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType && (CardTypeEnum.PICPAY.getValue() == cardType || CardTypeEnum.AME.getValue()==cardType || CardTypeEnum.Paypal.getValue()==cardType
                        || CardTypeEnum.Todito.getValue()==cardType || CardTypeEnum.TPaga.getValue()==cardType || CardTypeEnum.Mach.getValue()==cardType || CardTypeEnum.Vita.getValue()==cardType))
                {
                    String identify_number                      = req.getParameter("number");
                    String verify_code                          = req.getParameter("verifycode");
                    String accountnumber                        = req.getParameter("accountnumber");
                    System.out.println("identify_number-verify_code-accountnumber>>>>>>>"+identify_number+"-"+verify_code+"-"+accountnumber);
                    Comm3DResponseVO transRespDetails           = null;
                    CommRequestVO commRequestVO                 = new CommRequestVO();
//                    CommCardDetailsVO commCardDetailsVO         = new CommCardDetailsVO();
//                    CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
//                    CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                    HashMap<String,String> hashMap              = new HashMap<>();
                    pg                                                                                         = AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    transactionLogger.error("********** Inside Paygsmile wallet condition *********** ");

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    commonValidatorVO    = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                    commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);

                    commRequestVO.getTransDetailsVO().setBankAccountNo(identify_number);
                    commRequestVO.getTransDetailsVO().setCustomerBankCode(verify_code);
                    commRequestVO.getCardDetailsVO().setVoucherNumber(accountnumber);

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);

                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    transactionLogger.error("*** After Auth entry ***");

                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                    {
                        transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }else{
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    }

                    if (transRespDetails != null)
                    {
                        if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                        {
                            String form ="";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            form = abstractPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                            transactionLogger.debug("form in PaygSmile wallet  call---" + form);
                            pWriter.println(form);
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                            req.setAttribute("responceStatus", "fail");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = transRespDetails.getStatus().trim();
                        }
                        else
                        {
                            billingDiscriptor       = pg.getDisplayName();
                            mailtransactionStatus   = "pending";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                        }
                    }else{
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                else if (String.valueOf(PaymentModeEnum.NetBankingIndia_PAYMODE.getValue()).equals(terminalVO.getPaymodeId()) || String.valueOf(PaymentModeEnum.NetBankingBangla.getValue()).equals(terminalVO.getPaymodeId()))
                {
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    if(functions.isValueNull(commonValidatorVO.getProcessorBankName()))
                        commonValidatorVO.setCustomerId(commonValidatorVO.getProcessorBankName());
                    CommRequestVO commRequestVO         = null;
                    Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();

                    BillDeskUtils billDeskUtils         = new BillDeskUtils();
                    SafexPayUtils safexPayUtils         = new SafexPayUtils();
                    BhartiPayUtils bhartiPayUtils       = new BhartiPayUtils();
                    QikPayUtils qikPayUtils             = new QikPayUtils();
                    LetzPayUtils letzPayUtils           = new LetzPayUtils();
                    VerveUtils verveUtils               = new VerveUtils();
                    AsianCheckoutUtils asianCheckoutUtils   = new AsianCheckoutUtils();
                    CashFreeUtils cashFreeUtils             = new CashFreeUtils();
                    EasyPaymentzUtils easyPaymentzUtils     = new EasyPaymentzUtils();
                    IMoneypPayUtils iMoneypPayUtils         = new IMoneypPayUtils();
                    WealthPayUtils wealthPayUtils           = new WealthPayUtils();
                    PayUUtils payUUtils                     = new PayUUtils();
                    PayGUtils payGUtils                     = new PayGUtils();
                    ApexPayUtils apexPayUtils               = new ApexPayUtils();
                    AirpayUtils airpayUtils                 = new AirpayUtils();
                    QikPayV2Utils qikPayV2Utils             = new QikPayV2Utils();
                    PayaidPaymentUtils payaidPaymentUtils   = new PayaidPaymentUtils();
                    EasebuzzUtils easebuzzUtils             = new EasebuzzUtils();
                    OnepayUtils onepayUtils                 = new OnepayUtils();
                    LyraPaymentUtils lyraPaymentUtils       = new LyraPaymentUtils();
                    commonValidatorVO                       = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                    TerminalVO terminalVO1                  = new TerminalVO();
                    commonValidatorVO = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);
                    terminalVO1.setAutoRedirectRequest("N");
                    commonValidatorVO.setTerminalVO(terminalVO1);
                    if (fromType.equalsIgnoreCase("billdesk"))
                        commRequestVO = billDeskUtils.getBilldeskRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("safexpay"))
                        commRequestVO = safexPayUtils.getSafexPayRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("bhartipay"))
                        commRequestVO = bhartiPayUtils.getBhartiPayRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("qikpay"))
                        commRequestVO = qikPayUtils.getQikPayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("letzpay"))
                        commRequestVO = letzPayUtils.getCommRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("vervepay"))
                        commRequestVO = verveUtils.getVervePayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("asianchck"))
                        commRequestVO = asianCheckoutUtils.getAsianCheckoutRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("cashfree"))
                        commRequestVO = cashFreeUtils.getCashFreeRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("epaymentz"))
                        commRequestVO = easyPaymentzUtils.getEasyPayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("imoneypay"))
                        commRequestVO = iMoneypPayUtils.getIMoneyPayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("wealthpay"))
                        commRequestVO = wealthPayUtils.getWealthRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("payu"))
                        commRequestVO = payUUtils.getPayURequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("payg"))
                        commRequestVO = payGUtils.getPayGRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("apexpay"))
                        commRequestVO = apexPayUtils.getApexPayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("airpay"))
                        commRequestVO = airpayUtils.getAirPayRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("qikpayv2"))
                        commRequestVO = qikPayV2Utils.getQPayNRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("PayaidPay"))
                        commRequestVO = payaidPaymentUtils.getPayaidPaymentRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("onepay"))
                        commRequestVO = onepayUtils.getOnepayPaymentRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("lyra"))
                        commRequestVO = lyraPaymentUtils.getLyraPaymentRequestVO(commonValidatorVO);
                    else if (fromType.equalsIgnoreCase("easebuzz"))
                        commRequestVO = easebuzzUtils.getEasebuzzPaymentRequestVO(commonValidatorVO);

                    detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    //  commonValidatorVO.getCardDetailsVO().get
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
                    AbstractPaymentProcess commonPaymentProcess = PaymentProcessFactory.getPaymentProcessInstance(fromType);
                    if (merchantDetailsVO.getIsService().equals("N"))
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    }
                    String form = "";
                    if ("pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus()))
                    {

                        form = commonPaymentProcess.get3DConfirmationForm(commonValidatorVO, commResponseVO);


                        transactionLogger.debug("form in Netbanking call-----------" + form);
                        pWriter.println(form);
                        return;
                    }

                    else if ("fail".equalsIgnoreCase(commResponseVO.getStatus()))
                    {
                        String responseMessgage = commResponseVO.getDescription();

                        paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", trackingid, auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), responseMessgage);
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                    //transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    //log.debug("response---"+transRespDetails.getStatus());
                }
                else if (String.valueOf(PaymentModeEnum.WalletIndia_PAYMODE.getValue()).equals(terminalVO.getPaymodeId()) || String.valueOf(PaymentModeEnum.WalletBangla.getValue()).equals(terminalVO.getPaymodeId()) || String.valueOf(PaymentModeEnum.UPI).equals(paymentMode))
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    if (functions.isValueNull(commonValidatorVO.getProcessorBankName()))
                        commonValidatorVO.setCustomerId(commonValidatorVO.getProcessorBankName());
                    if (functions.isValueNull(commonValidatorVO.getVpa_address()))
                        commonValidatorVO.setCustomerId(commonValidatorVO.getVpa_address());
                    transactionLogger.debug("inside WalletIndia ----");

                    commonValidatorVO = transactionHelper.performSystemCheckForNetBanking(commonValidatorVO);

                    AbstractInputValidator inputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = inputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", account.getAddressValidation());
                    if (error != null && !error.equals(""))
                    {


                        if (functions.isValueNull(commonValidatorVO.getVpa_address()))
                            commonValidatorVO.setCustomerId(commonValidatorVO.getVpa_address());
                        errorCodeListVO.addListOfError(commonValidatorVO.getErrorCodeListVO());
                        errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, commonValidatorVO.getErrorCodeListVO(), null, null);
                        // PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallCheckout.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    transactionLogger.debug("merchnt order display---" + commonValidatorVO.getMerchantDetailsVO().getMerchantOrderDetailsDisplay());

                    CommRequestVO commRequestVO         = null;
                    Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
                    BillDeskUtils billDeskUtils         = new BillDeskUtils();
                    SafexPayUtils safexPayUtils         = new SafexPayUtils();
                    BhartiPayUtils bhartiPayUtils       = new BhartiPayUtils();
                    QikPayUtils qikPayUtils             = new QikPayUtils();
                    LetzPayUtils letzPayUtils           = new LetzPayUtils();
                    VerveUtils verveUtils               = new VerveUtils();
                    AsianCheckoutUtils asianCheckoutUtils   = new AsianCheckoutUtils();
                    CashFreeUtils cashFreeUtils             = new CashFreeUtils();
                    EasyPaymentzUtils easyPaymentzUtils     = new EasyPaymentzUtils();
                    IMoneypPayUtils iMoneypPayUtils         = new IMoneypPayUtils();
                    PayUUtils payUUtils                     = new PayUUtils();
                    PayGUtils payGUtils                     = new PayGUtils();
                    ApexPayUtils apexPayUtils               = new ApexPayUtils();
                    AirpayUtils airpayUtils                 = new AirpayUtils();
                    QikPayV2Utils qikPayV2Utils             = new QikPayV2Utils();
                    PayaidPaymentUtils payaidPaymentUtils   = new PayaidPaymentUtils();
                    OnepayUtils onepayUtils                 = new OnepayUtils();
                    PayTMUtils payTMUtils                   = new PayTMUtils();
                    LyraPaymentUtils lyraPaymentUtils       = new LyraPaymentUtils();
                    EasebuzzUtils easebuzzUtils             = new EasebuzzUtils();
                    BnmQuickUtils bnmQuickUtils             = new BnmQuickUtils();
//                    SmartCodePayUtils smartCodePayUtils     = new SmartCodePayUtils();
                    TerminalVO terminalVO1                  = new TerminalVO();
                    terminalVO1.setAutoRedirectRequest("N");
                    commonValidatorVO.setTerminalVO(terminalVO1);
                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                    if (fromType.equalsIgnoreCase("billdesk"))
                    {
                        commRequestVO = billDeskUtils.getBilldeskRequestVO(commonValidatorVO);
                    }else if (fromType.equalsIgnoreCase("safexpay"))
                    {
                        commRequestVO = safexPayUtils.getSafexPayRequestVO(commonValidatorVO);
                    }else if (fromType.equalsIgnoreCase("bhartipay"))
                    {
                        commRequestVO = bhartiPayUtils.getBhartiPayRequestVO(commonValidatorVO);
                    }else if (fromType.equalsIgnoreCase("qikpay"))
                    {
                        commRequestVO = qikPayUtils.getQikPayRequestVO(commonValidatorVO);
                    }else if (fromType.equalsIgnoreCase("letzpay"))
                    {
                        commRequestVO = letzPayUtils.getCommRequestVO(commonValidatorVO);
                    }
                    else if(fromType.equalsIgnoreCase("vervepay"))
                        commRequestVO = verveUtils.getVervePayRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("asianchck"))
                        commRequestVO = asianCheckoutUtils.getAsianCheckoutRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("cashfree"))
                        commRequestVO = cashFreeUtils.getCashFreeRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("epaymentz"))
                        commRequestVO = easyPaymentzUtils.getEasyPayRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("imoneypay"))
                        commRequestVO = iMoneypPayUtils.getIMoneyPayRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("payu"))
                        commRequestVO = payUUtils.getPayURequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("payg"))
                        commRequestVO = payGUtils.getPayGRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("apexpay"))
                        commRequestVO = apexPayUtils.getApexPayRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("airpay"))
                        commRequestVO = airpayUtils.getAirPayRequestVO(commonValidatorVO);

                    else if(fromType.equalsIgnoreCase("qikpayv2"))
                        commRequestVO = qikPayV2Utils.getQPayNRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("PayaidPay"))
                        commRequestVO = payaidPaymentUtils.getPayaidPaymentRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("onepay"))
                        commRequestVO = onepayUtils.getOnepayPaymentRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("paytm"))
                        commRequestVO = payTMUtils.getPayTMRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("lyra"))
                        commRequestVO = lyraPaymentUtils.getLyraPaymentRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("easebuzz"))
                        commRequestVO = easebuzzUtils.getEasebuzzPaymentRequestVO(commonValidatorVO);

                    else if (fromType.equalsIgnoreCase("bnmquick"))
                        commRequestVO = bnmQuickUtils.getBnmQuickPaymentRequestVO(commonValidatorVO);
//                    else if (fromType.equalsIgnoreCase("smartcode"))
//                    {
//                        commRequestVO = smartCodePayUtils.getSmartCodeRequestVO(commonValidatorVO);
//                    }

                    detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();

                   /*FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                        if (commonValidatorVO.isFraud())
                        {
                            //Action To Be Taken
                        }
                    }*/

                    if (merchantDetailsVO.getIsService().equals("N"))
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else
                    {
                        commResponseVO = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    }
                    String form = "";

                if ("pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus()))
                {

                    form = abstractPaymentProcess.get3DConfirmationForm(commonValidatorVO, commResponseVO);

                    transactionLogger.debug("form in WalletIndia  call---" + form);
                    pWriter.println(form);
                    return;
                }

                else if ("fail".equalsIgnoreCase(commResponseVO.getStatus()))
                {
                    String responseMessgage = commResponseVO.getDescription();

                    paymentManager.updateTransactionForCommon(commResponseVO, "authfailed", trackingid, auditTrailVO, "transaction_common", "", commResponseVO.getTransactionId(), commResponseVO.getResponseTime(), responseMessgage);
                }else if("pending".equalsIgnoreCase(commResponseVO.getStatus()) && (fromType.equalsIgnoreCase("payg") ||  fromType.equalsIgnoreCase("lyra") || fromType.equalsIgnoreCase("bnmquick") ))
                {

                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";

                }

                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;

                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.PAYSEC_CARDTYPE.getValue() == cardType)
                {
                    detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                    PaySecUtils paySecUtils = new PaySecUtils();
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
                    String html = paySecUtils.getPaySecRequest(commonValidatorVO);
                    //transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    //log.debug("response---"+transRespDetails.getStatus());

                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.SOFORT_CARDTYPE.getValue() == cardType)
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;
                    SofortUtility sofortUtility = new SofortUtility();
                    commRequestVO = sofortUtility.getSofortRequestVO(commonValidatorVO);
                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    else
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                        {
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            res.sendRedirect(transRespDetails.getRedirectUrl());
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                else if ((SwiffyPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType)|| AlphapayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType)) && PaymentModeEnum.BankTransfer.getValue() == paymentType)
                {
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    CommRequestVO commRequestVO         = null;
                    Comm3DResponseVO transRespDetails     = null;
                    commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                        transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    else
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                        {

                            String form ="";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            form = abstractPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                            transactionLogger.debug("form in Bank Transfer  call---" + form);
                            pWriter.println(form);
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {

                            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                            req.setAttribute("responceStatus", "fail");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = transRespDetails.getStatus().trim();
                        }
                        else
                        {
                            billingDiscriptor       = pg.getDisplayName();
                            mailtransactionStatus   = "pending";
                        }

                    }else{
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                //else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && (CardTypeEnum.Internet_Banking.getValue() == cardType|| CardTypeEnum.Bank_Transfer.getValue() == cardType))
                else if (InfiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) &&(PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType || PaymentModeEnum.WALLET_PAYMODE.getValue() == paymentType))
                {
                    transactionLogger.error("Inside singlecall InternetBanking method =========>");

                    String bankCode = "";

                    if(req.getAttribute("bankCode") != null){
                        bankCode = (String) req.getAttribute("bankCode");
                    }
                    if(req.getParameter("bankCode") != null){
                        bankCode = (String) req.getParameter("bankCode");
                    }

                    transactionLogger.error("bankCode >>>>>>>>>> "+bankCode);


                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    CommRequestVO commRequestVO         = null;
                    Comm3DResponseVO transRespDetails     = null;
                    InfiPayUtils infiPayUtils = new InfiPayUtils();
                    commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                    AbstractPaymentProcess abstractPaymentProcess   = PaymentProcessFactory.getPaymentProcessInstance(fromType);

                    commRequestVO.getTransDetailsVO().setCustomerBankAccountName(bankCode);

                    if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
                        transRespDetails = (Comm3DResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    else
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);

                    if (transRespDetails != null)
                    {
                        if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                        {

                            String form ="";
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            form = abstractPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                            transactionLogger.debug("form in Internet Banking  call---" + form);
                            pWriter.println(form);
                            return;
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {

                            transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                            req.setAttribute("responceStatus", "fail");
                            paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                            mailtransactionStatus = transRespDetails.getStatus().trim();
                        }
                        else
                        {
                            billingDiscriptor       = pg.getDisplayName();
                            mailtransactionStatus   = "pending";
                        }

                    }else{
                        billingDiscriptor       = pg.getDisplayName();
                        mailtransactionStatus   = "pending";
                    }

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", mailtransactionStatus);
                    req.setAttribute("displayName", billingDiscriptor);
                    req.setAttribute("ctoken", ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.DIRECT_DEBIT.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForP4(commonValidatorVO, trackingid, auditTrailVO, false);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.SEPA.getValue() == paymentType && CardTypeEnum.DIRECT_DEBIT.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForP4(commonValidatorVO, trackingid, auditTrailVO, true);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.NETBANKING_PAYMODE.getValue() == paymentType && CardTypeEnum.IDEAL_CARDTYPE.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.ACH.getValue() == paymentType && CardTypeEnum.ACH.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    CommRequestVO commRequestVO     = null;
                    CommResponseVO transRespDetails = null;
                    PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                    commonValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.ACH.getValue()));
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (String.valueOf(PaymentModeEnum.CHK.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.CHK.getValue()).equals(terminalVO.getCardTypeId()) || String.valueOf(CardTypeEnum.eCheck.getValue()).equals(terminalVO.getCardTypeId()) || String.valueOf(CardTypeEnum.EZPAY.getValue()).equals(terminalVO.getCardTypeId())) )
                {
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    AbstractInputValidator abstractInputValidator = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", gatewayAccount.getAddressValidation());
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    PayMitcoPaymentProcess payMitcoPaymentProcess = new PayMitcoPaymentProcess();

                    if (!functions.isEmptyOrNull(error))
                    {
                        transactionLogger.debug("inside CHK--");
                        errorCodeListVO=commonValidatorVO.getErrorCodeListVO();
                        errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    CommRequestVO commRequestVO = null;
                    PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                    commonValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.CHK.getValue()));

                    commRequestVO = payMitcoUtility.getPayMitcoRequestVO(commonValidatorVO);
                    detailId = paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    if("paymitco".equalsIgnoreCase(fromType))
                    {
                        PayMitcoResponseVO transRespDetails = null;
                        //All Request value Entry
                        payMitcoPaymentProcess.actionEntryExtension(detailId, trackingid, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, transRespDetails, commRequestVO);

                        transRespDetails = (PayMitcoResponseVO) pg.processSale(trackingid, commRequestVO);

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
                    }else
                    {
                        Comm3DResponseVO transRespDetails=null;
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                        if (transRespDetails != null)
                        {
                            if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
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
                            }else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "authstarted", trackingid, auditTrailVO, "transaction_common", customerIpAddress);

                                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                                {
                                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
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
                            else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
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
                else if (String.valueOf(PaymentModeEnum.eCheck.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.CHK.getValue()).equals(terminalVO.getCardTypeId()) || String.valueOf(CardTypeEnum.eCheck.getValue()).equals(terminalVO.getCardTypeId()) || String.valueOf(CardTypeEnum.EZPAY.getValue()).equals(terminalVO.getCardTypeId())) )
                {
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    GatewayAccount gatewayAccount 					= GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    AbstractInputValidator abstractInputValidator 	= InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error 											= abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", gatewayAccount.getAddressValidation());
                    merchantDetailsVO 								= merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    PayMitcoPaymentProcess payMitcoPaymentProcess = new PayMitcoPaymentProcess();

                    if (!functions.isEmptyOrNull(error))
                    {
                        transactionLogger.debug("inside eCheck--");
                        errorCodeListVO=commonValidatorVO.getErrorCodeListVO();
                        errorName = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    CommRequestVO commRequestVO = null;
                    PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                    commonValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.eCheck.getValue()));

                    commRequestVO 	= payMitcoUtility.getPayMitcoRequestVO(commonValidatorVO);
                    detailId 		= paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    if("paymitco".equalsIgnoreCase(fromType))
                    {
                        PayMitcoResponseVO transRespDetails = null;
                        //All Request value Entry
                        payMitcoPaymentProcess.actionEntryExtension(detailId, trackingid, commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, transRespDetails, commRequestVO);

                        transRespDetails = (PayMitcoResponseVO) pg.processSale(trackingid, commRequestVO);

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
                                log.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                                transactionLogger.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
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
                    }else
                    {
                        Comm3DResponseVO transRespDetails=null;
                        transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                        if (transRespDetails != null)
                        {
                            if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
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
                            }else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "authstarted", trackingid, auditTrailVO, "transaction_common", customerIpAddress);

                                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                                {
                                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
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
                            else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                            {
                                paymentManager.updateTransactionForEZPayNow(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                                log.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                                transactionLogger.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
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
                /*else if ((PaymentModeEnum.MobileMoneyAfrica.getValue() == paymentType || PaymentModeEnum.BankTransfer.getValue() == paymentType)
                        && (CardTypeEnum.ECOCASH.getValue() == cardType || CardTypeEnum.TELECASH.getValue() == cardType || CardTypeEnum.EcocashUSD.getValue() == cardType
                        || CardTypeEnum.EcocashRTGS.getValue() == cardType || CardTypeEnum.OneMoneyUSD.getValue() == cardType || CardTypeEnum.OneMoneyRTGS.getValue() == cardType || CardTypeEnum.MTNMomo.getValue() == cardType))
                {
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    AbstractInputValidator abstractInputValidator   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    error               = abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT", gatewayAccount.getAddressValidation());
                    merchantDetailsVO   = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    if (!functions.isEmptyOrNull(error))
                    {
                        transactionLogger.debug("inside eCheck--");
                        errorCodeListVO = commonValidatorVO.getErrorCodeListVO();
                        errorName       = errorCodeUtils.getErrorNames(errorCodeListVO.getListOfError());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        HashMap paymentTypeMap = (HashMap) session.getAttribute("paymentMap");
                        commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                        req.setAttribute("transDetails", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    Comm3DResponseVO transRespDetails   = null;
                    CommRequestVO commRequestVO         = null;
                    AtCellulantUtils atCellulantUtils   = new AtCellulantUtils();
                    commRequestVO                       = atCellulantUtils.getRequestVO(commonValidatorVO);
                    detailId                            = paymentManager.insertAuthStartedTransactionEntryForPayMitco(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    transRespDetails = (Comm3DResponseVO) pg.processSale(trackingid, commRequestVO);
                    if (transRespDetails != null)
                    {
                        transactionLogger.debug("transRespDetails.getStatus().trim()---->" + transRespDetails.getStatus().trim());
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            respStatus  = transRespDetails.getStatus().trim();
                            paymentManager.updateTransactionForEZPayNow(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
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
                        }else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                        {
                            mailtransactionStatus   = "pending";
                            respStatus              = transRespDetails.getStatus().trim();
                            paymentManager.updateTransactionForEZPayNow(transRespDetails, "authstarted", trackingid, auditTrailVO, "transaction_common", customerIpAddress);

                            if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                            {
                                invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                            }
                            *//*if (functions.isValueNull(transRespDetails.getDescriptor()))
                            {
                                billingDiscriptor = transRespDetails.getDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = pg.getDisplayName();
                            }*//*
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                        {
                            respStatus=transRespDetails.getStatus().trim();
                            paymentManager.updateTransactionForEZPayNow(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIpAddress);
                            log.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                            transactionLogger.debug("eCheck BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
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
                }*/
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    paymentManager.updateAuthStartedTransactionEntryForB4(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }
                else if (PaymentModeEnum.CLEARSETTLE_PAYMODE.getValue() == paymentType && CardTypeEnum.CLEARSETTLE.getValue() == cardType)
                {
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    ClearSettleUtills clearSettleUtills = new ClearSettleUtills();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = clearSettleUtills.getClearSettleHPPRequest(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    if(transRespDetails != null)
                    {
                        Comm3DResponseVO response3D = null;
                        transactionLogger.debug("status----"+transRespDetails.getStatus());
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                        {
                            response3D = (Comm3DResponseVO) transRespDetails;
                            String html = clearSettleUtills.generateAutoSubmitFormNew(response3D);
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
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

                else if (String.valueOf(PaymentModeEnum.TOJIKA.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.TOJIKA.getValue()).equals(terminalVO.getCardTypeId())))
                {
                    transactionLogger.debug("inside tojika condition---------" + PaymentModeEnum.TOJIKA);
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                    log.debug("address validation in SingleCallCheckout---" + addressValidation);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    String html= pg.processAutoRedirect(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (String.valueOf(PaymentModeEnum.OneRoad.getValue()).equals(terminalVO.getPaymodeId()) && ((String.valueOf(CardTypeEnum.UnionPay.getValue()).equals(terminalVO.getCardTypeId()))||(String.valueOf(CardTypeEnum.WechatPay.getValue()).equals(terminalVO.getCardTypeId()))))
                {
                    transactionLogger.debug("inside OneRoadPayment condition---------" + CardTypeEnum.WechatPay);
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    String html= pg.processAutoRedirect(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
           /*     else if (String.valueOf(PaymentModeEnum.MULA.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.MULA.getValue()).equals(terminalVO.getCardTypeId())))
                {
                    transactionLogger.debug("inside APSPayment condition---------"+PaymentModeEnum.MULA);
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = commonValidatorVO.getTerminalVO().getAddressValidation();
                    log.debug("address validation in SingleCallCheckout---"+addressValidation);

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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    PaymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    String html= pg.processAutoRedirect(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }*/
                else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.getValue() == paymentType && CardTypeEnum.MULTIBANCO.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                    String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.getValue() == paymentType && CardTypeEnum.ASTROPAY.getValue() == cardType)
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
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                    String html = apcoPayUtills.getApcoPayRequest(commonValidatorVO);
                    pWriter.println(html);
                    return;
                }
                else if (PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue() == paymentType && CardTypeEnum.UNICREDIT.getValue() == cardType)
                {
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    UnicreditUtils unicreditUtils = new UnicreditUtils();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = unicreditUtils.getUnicreditRequestVO(commonValidatorVO);
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    Comm3DResponseVO response3D = null;

                    if (transRespDetails != null)
                    {
                        response3D = (Comm3DResponseVO) transRespDetails;
                        transactionLogger.debug("URL Unicredit---" + response3D.getUrlFor3DRedirect());
                        transactionLogger.debug("Messgae Unicredit---" + response3D.getPaReq());
                        String html = unicreditUtils.generateAutoSubmitForm(response3D);
                        transactionLogger.debug("HTML Unicredit---" + html);
                        pWriter.println(html);
                    }
                    return;
                }

               else if(PaymentModeEnum.FASTPAY.getValue() == paymentType && CardTypeEnum.FASTPAY.getValue() == cardType)
                {
                    transactionLogger.error("Inside FASTPAY SingleCall Checkout =============>");

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    ApcoFastPayV6Utils apcoFastPayV6Utils = new ApcoFastPayV6Utils();
                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;
                    commRequestVO        = apcoFastPayV6Utils.getCommRequestFromUtils(commonValidatorVO);

                   // transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);

                    Comm3DResponseVO response3D = null;
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    String isService =merchantDetailsVO.getIsService();
                    transactionLogger.error("isService is ---- SingleCallCheckout-->" + isService);
                    response3D = (Comm3DResponseVO) transRespDetails;
                    if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                    }
                    else {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    }
                    if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
                    {

                        response3D = (Comm3DResponseVO) transRespDetails;
                        transactionLogger.debug("URL Fastpay---" + response3D.getUrlFor3DRedirect());
                        transactionLogger.debug("Messgae Fastpay---" + response3D.getPaReq());
                        String html = apcoFastPayV6Utils.generateAutoSubmitForm(response3D);
                        transactionLogger.debug("HTML Fastpay---" + html);
                        pWriter.println(html);

                    }
                    else
                    {
                        req.setAttribute("responceStatus", "fail");
                        paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        mailtransactionStatus = transRespDetails.getStatus().trim();
                    }

                }
                else if (String.valueOf(PaymentModeEnum.PAYG.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.PAYG.getValue()).equals(terminalVO.getCardTypeId())))
                {
                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
                    VoguePayUtils voguePayUtils = new VoguePayUtils();
                    CommRequestVO commRequestVO =null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = voguePayUtils.getVoguePayUtils(commonValidatorVO);
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    String isService =merchantDetailsVO.getIsService();
                    transactionLogger.debug("isService is ---- SingleCallCheckout-->" + isService);
                    if (isService.equalsIgnoreCase("Y"))
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                        PZGenericConstraint genConstraint = new PZGenericConstraint("SingleCallCheckout.java","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
                        throw new PZGenericConstraintViolationException("This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::");
                    }
                    // Comm3DResponseVO comm3DResponseVO = null;
                    if (transRespDetails !=null)
                    {
                       /* comm3DResponseVO = (Comm3DResponseVO) transRespDetails;
                        transactionLogger.error("Url VoguePay---"+comm3DResponseVO.getRedirectUrl());*/
                        transactionLogger.debug("redirect url -------------" + transRespDetails.getRedirectUrl());
                        String html = VoguePayUtils.getRedirectForm(trackingid, transRespDetails);
                        transactionLogger.debug("html form is -----"+html.toString());
                        pWriter.println(html);
                    }
                    return;
                }
                else if (String.valueOf(PaymentModeEnum.ZOTA.getValue()).equals(terminalVO.getPaymodeId()) && (String.valueOf(CardTypeEnum.ZOTA.getValue()).equals(terminalVO.getCardTypeId())))
                {
                    //BALAJI - ADDED CONDITION

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                    if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                    {
                        invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                    }
//                    VoguePayUtils voguePayUtils = new VoguePayUtils();
                    CommRequestVO commRequestVO =null;
                    CommResponseVO transRespDetails = null;

//                    commRequestVO = voguePayUtils.getVoguePayUtils(commonValidatorVO);
                    merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                    String isService =merchantDetailsVO.getIsService();
                    transactionLogger.debug("isService is ---- SingleCallCheckout-->" + isService);
                    if (isService.equalsIgnoreCase("Y"))
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                        transactionLogger.debug("inside singlecallcheckout status==" + transRespDetails.getStatus());
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                        PZGenericConstraint genConstraint = new PZGenericConstraint("SingleCallCheckout.java","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
                        throw new PZGenericConstraintViolationException("This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::");
                    }
                    // Comm3DResponseVO comm3DResponseVO = null;
                    if (transRespDetails !=null)
                    {
                       /* comm3DResponseVO = (Comm3DResponseVO) transRespDetails;
                        transactionLogger.error("Url VoguePay---"+comm3DResponseVO.getRedirectUrl());*/
                        transactionLogger.debug("redirect url -------------" + transRespDetails.getRedirectUrl());
                        String html = new ZotapayUtils().getRedirectForm(transRespDetails);
                        transactionLogger.debug("html form is -----" + html.toString());
                        pWriter.println(html);
                    }
                    return;

                }
                else if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.getValue() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.getValue() == paymentType || String.valueOf(PaymentModeEnum.CREDIT_CARD_INDIA.getValue()).equals(terminalVO.getPaymodeId())   || String.valueOf(PaymentModeEnum.DEBIT_CARD_INDIA.getValue()).equals(terminalVO.getPaymodeId()) ) &&
                        (CardTypeEnum.VISA_CARDTYPE.getValue() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.getValue() == cardType || CardTypeEnum.DINER_CARDTYPE.getValue() == cardType || CardTypeEnum.AMEX_CARDTYPE.getValue() == cardType || CardTypeEnum.JCB.getValue() == cardType || CardTypeEnum.RUPAY.getValue() == cardType || CardTypeEnum.DISC_CARDTYPE.getValue() == cardType || CardTypeEnum.MAESTRO.getValue() == cardType || CardTypeEnum.INSTAPAYMENT.getValue() == cardType))
                {
                    //validation Check
                    if (ReitumuBankSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || DectaSMSPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || ClearSettleHPPGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                    {
                        AbstractInputValidator abstractInputValidator 	= InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                        error 											= abstractInputValidator.validateIntegrationSpecificParameters(commonValidatorVO, "STDKIT");
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
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        merchantDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        paymentManager.insertAuthStartedTransactionEntryForReitumu3D(commonValidatorVO, trackingid, auditTrailVO, tableName);
                        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        }
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
                            PZExceptionHandler.raiseConstraintViolationException("PayProcessController.class", "doPost()", null, "Transaction", commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, commonValidatorVO.getErrorCodeListVO(), null, null);

                            /*try
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
                            commonValidatorVO.setTerminalMap(terminalMap);
                            commonValidatorVO.setTerminalList(terminalList);
                            req.setAttribute("transDetails", commonValidatorVO);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;*/
                        }
                        try
                        {
                            log.debug("card type in singlecallpayment----" + cardType);

                            // checksum verification
                            //transactionHelper.checksumVerificationForSTDKit(commonValidatorVO);
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
                                paymentManager.updateDetailsTablewithErrorName(errorName,commonValidatorVO.getTrackingid() );
                            }
                            catch (PZDBViolationException d)
                            {
                                log.error("----PZDBViolationException in update with error name-----", d);
                            }

                            req.setAttribute("error", error);
                            HashMap paymentTypeMap = new HashMap();

                            if(session.getAttribute("paymentMap")!=null)
                                paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                            if(paymentTypeMap==null || paymentTypeMap.isEmpty())
                            {
                                transactionLogger.debug("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                                List<String> cardList = new ArrayList<String>();
                                cardList.add(commonValidatorVO.getCardType());
                                paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                                PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

                                transactionLogger.debug("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

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
                            if(session.getAttribute("paymentMap")!=null)
                                paymentTypeMap = (HashMap) session.getAttribute("paymentMap");

                            if(paymentTypeMap==null || paymentTypeMap.isEmpty())
                            {
                                transactionLogger.debug("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                                List<String> cardList = new ArrayList<String>();
                                cardList.add(commonValidatorVO.getCardType());
                                paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                                PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

                                transactionLogger.debug("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

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
                            String html= pg.processAutoRedirect(commonValidatorVO);
                            pWriter.println(html);
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

                            if(commonValidatorVO.getMerchantDetailsVO() != null){
                                merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
                            }

                            recurringBillingVO.setOriginTrackingId(trackingid);
                            recurringBillingVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                            recurringBillingVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                            recurringBillingVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                            recurringBillingVO.setTerminalid(commonValidatorVO.getTerminalId());
                            String cardNumber = commonValidatorVO.getCardDetailsVO().getCardNum();
                            if(merchantDetailsVO != null && merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N")){
                                recurringBillingVO.setFirstSix("");
                                recurringBillingVO.setLastFour("");
                            }else{
                                recurringBillingVO.setFirstSix(cardNumber.substring(0, 6));
                                recurringBillingVO.setLastFour(cardNumber.substring((cardNumber.length() - 4), cardNumber.length()));
                            }

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

                    merchantDetailsVO 								= commonValidatorVO.getMerchantDetailsVO();
                    GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                    genericTransDetailsVO 							= commonValidatorVO.getTransDetailsVO();

                    if (Functions.checkCommonProcessGateways(fromType))
                    {
                        transactionLogger.debug("fromtype------"+fromType);
                        AbstractPaymentProcess paymentProcess 	= PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
                        //InvoiceEntry invoiceEntry 			= new InvoiceEntry();
                        CommRequestVO commRequestVO 			= null;
                        CommResponseVO transRespDetails 		= null;

                        String paymentOtionCode = "";
                        String ThreeDSVersion   = "";
                        log.debug("is recurring from gateway account----" + account.getIsRecurring());
                        if(AppleTreeCellulantPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType)){

                            if(req.getParameter("paymentOtionCode") != null){
                                paymentOtionCode = req.getParameter("paymentOtionCode");
                                commonValidatorVO.setProcessorName(paymentOtionCode);
                            }
                            /*if(req.getParameter("ThreeDSVersion") != null){
                                ThreeDSVersion = req.getParameter("ThreeDSVersion");
                                commonValidatorVO.setAttemptThreeD(ThreeDSVersion);
                            }*/
                        }
                        commRequestVO = getCommonRequestVO(commonValidatorVO, fromType);
                        paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);
                        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        }
                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                        {
                            String child_trackingid="";
                            String terminalid   = commonValidatorVO.getTerminalId();
                            for (int i = 0; i < mpDetailsList.size(); i++)
                            {
                                marketPlaceVO = mpDetailsList.get(i);
                                child_trackingid=marketPlaceVO.getTrackingid();
                                commonValidatorVO.setMarketPlaceVO(marketPlaceVO);
                                if("submerchant_level_charges".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()))
                                {
                                    commonValidatorVO=paymentManager.getTerminalBasedOnMarketPlaceSubmerchant(commonValidatorVO,terminalid);
                                }
                                paymentManager.insertAuthStartedTransactionEntryForMarketPlaceCommon(commonValidatorVO, child_trackingid,trackingid, commRequestVO, auditTrailVO, tableName);
                                if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                                {
                                    invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                                }
                            }
                            commonValidatorVO.setTrackingid(trackingid);
                        }

                        if (functions.isValueNull(commonValidatorVO.getInvoiceId()))
                        {
                            invoiceEntry.processInvoice(commonValidatorVO.getInvoiceId(), Integer.parseInt(commonValidatorVO.getTrackingid()), commonValidatorVO.getMerchantDetailsVO().getAccountId());
                        }

                        transactionLogger.debug("Internal Fraud Check---" + merchantDetailsVO.getInternalFraudCheck() + "-MemberID---" + merchantDetailsVO.getMemberId() + "---VipCard---" + commonValidatorVO.isVIPCard() + "-VIPEmail---" + commonValidatorVO.isVIPEmail());
                        if("Y".equalsIgnoreCase(merchantDetailsVO.getInternalFraudCheck()) && !(commonValidatorVO.isVIPCard() || commonValidatorVO.isVIPEmail()))
                        {
                            FraudChecker fraudChecker = new FraudChecker();
                            fraudChecker.checkInternalFraudRules(commonValidatorVO);

                            transactionLogger.debug("Is Fraud Transaction---" + commonValidatorVO.isFraud());
                            if (commonValidatorVO.isFraud())
                            {
                                respStatus 						= "failed";
                                mailtransactionStatus 			= "failed";
                                String remark 					= "High Risk Transaction Detected";
                                respRemark 						= "High Risk Transaction Detected";
                                CommResponseVO commResponseVO 	= new CommResponseVO();
                                commResponseVO.setRemark(remark);
                                commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,"","","",null);
                                if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                                {
                                    for (int i = 0; i < mpDetailsList.size(); i++)
                                    {
                                        marketPlaceVO=mpDetailsList.get(i);
                                        singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, marketPlaceVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, marketPlaceVO.getTrackingid(), eci,"","","",null);
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    }
                                }
                                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                transactionLogger.debug("TransactionNotification flag for ---" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + merchantDetailsVO.getTransactionNotification());
                                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))//&& ("Non-3D".equals(merchantDetailsVO.getTransactionNotification())||"Both".equals(merchantDetailsVO.getTransactionNotification()))
                                {
                                    transactionLogger.debug("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                                    commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                                    if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                                        transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                                    }else{
                                        transactionDetailsVO1.setMerchantNotificationUrl("");
                                    }
                                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                    asyncNotificationService.sendNotification(transactionDetailsVO1, commonValidatorVO.getTrackingid(), respStatus, respRemark,"");
                                }
                                transactionLogger.debug("Fraud Error---" + commonValidatorVO.getErrorCodeListVO().getListOfError().get(0).getErrorCode());
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

                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                        {
                            FraudServiceDAO fraudServiceDAO 				= new FraudServiceDAO();
                            FraudAccountDetailsVO merchantFraudAccountVO 	= fraudServiceDAO.getMerchantFraudConfigurationDetails(merchantDetailsVO.getMemberId());
                            if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                            {
                                commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                                FraudChecker fraudChecker = new FraudChecker();
                                fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                                if (commonValidatorVO.isFraud())
                                {
                                    respStatus 						= "failed";
                                    mailtransactionStatus 			= "failed";
                                    String remark 					= "High Risk Transaction Detected";
                                    respRemark 						= "High Risk Transaction Detected";
                                    CommResponseVO commResponseVO 	= new CommResponseVO();
                                    commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,eci,"","","",null);
                                    if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                                    {
                                        for (int i = 0; i < mpDetailsList.size(); i++)
                                        {
                                            marketPlaceVO=mpDetailsList.get(i);
                                            singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, marketPlaceVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, marketPlaceVO.getTrackingid(), eci,"","","",null);
                                            entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                        }
                                    }
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
                        if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                        {
                            transactionLogger.debug("inside SingCallPayment processInitialSale --");
                            transRespDetails = (CommResponseVO) pg.processInitialSale(trackingid, commRequestVO);

                            status = "authsuccessful";
                        }
                        else if (merchantDetailsVO.getService().equalsIgnoreCase("N"))
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

                        transactionLogger.debug("transRespDetails------"+transRespDetails);
                        if (transRespDetails != null)
                        {
                            transactionLogger.debug("status------" + transRespDetails.getStatus());
                            if (functions.isValueNull(transRespDetails.getStatus()) && (transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                            {
                                isSuccessful = "Y";
                                if (!functions.isValueNull(transRespDetails.getDescriptor()))
                                {
                                    transRespDetails.setDescriptor(pg.getDisplayName());
                                }

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
                                transactionLogger.debug("response3D.getThreeDVersion()--->" + response3D.getThreeDVersion());
                                transactionLogger.debug("INSIDE pending3DConfirmation---==============>");

                                if (!functions.isValueNull(transRespDetails.getThreeDVersion()))
                                    transRespDetails.setThreeDVersion("3Dv1");
                                paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                                if (!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                                {
                                    for (int i = 0; i < mpDetailsList.size(); i++)
                                    {
                                        marketPlaceVO = mpDetailsList.get(i);
                                        paymentManager.updatePaymentIdForCommon(transRespDetails, marketPlaceVO.getTrackingid());
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    }
                                }
                                String form = "";
                                if (fromType.equalsIgnoreCase("lyra") || fromType.equalsIgnoreCase("PayaidPay") ||fromType.equalsIgnoreCase("qikpayv2") || fromType.equalsIgnoreCase("cleveland") || fromType.equalsIgnoreCase("airpay") || fromType.equalsIgnoreCase("apexpay") ||  fromType.equalsIgnoreCase("payg") || fromType.equalsIgnoreCase("payu") || fromType.equalsIgnoreCase("imoneypay") || fromType.equalsIgnoreCase("epaymentz") || fromType.equalsIgnoreCase("asianchck") || fromType.equalsIgnoreCase("cashfree") || fromType.equalsIgnoreCase("vervepay") || fromType.equalsIgnoreCase("qikpay") || fromType.equalsIgnoreCase("pbs") || fromType.equalsIgnoreCase("billdesk") || fromType.equalsIgnoreCase("safexpay") || fromType.equalsIgnoreCase("bhartipay") || fromType.equalsIgnoreCase("letzpay")|| fromType.equalsIgnoreCase("easebuzz"))
                                {

                                    form = paymentProcess.get3DConfirmationForm(commonValidatorVO, response3D);
                                }
                                else
                                {
                                    form = paymentProcess.get3DConfirmationForm(trackingid, ctoken, response3D);
                                }
                                if ("Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                                {
                                    if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                                    {
                                        TokenManager tokenManager = new TokenManager();
                                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                        TokenResponseVO tokenResponseVO = new TokenResponseVO();
                                        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                                        String expDate = "";
                                        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
                                            expDate = commonValidatorVO.getCardDetailsVO().getExpMonth() + "/" + commonValidatorVO.getCardDetailsVO().getExpYear();
                                        String strToken = tokenManager.isTokenAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum(), expDate);
                                        transactionLogger.debug("strToken--->" + strToken);
                                        tokenRequestVO.setIsActive("N");
                                        if (functions.isValueNull(strToken))
                                        {
                                            tokenRequestVO.setTokenId(strToken);
                                            tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

                                            tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                            tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                            tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());

                                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                            String registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                            tokenResponseVO.setStatus(registrationStatus);
                                            tokenResponseVO.setTokenId(strToken);
                                            tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                            token = tokenDetailsVO.getRegistrationToken();
                                            commonValidatorVO.setToken(token);
                                        }
                                        else
                                        {
                                            CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                            commCardDetailsVO.setCardType(String.valueOf(cardType));
                                            commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                            commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                            commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                            commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());

                                            tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                            tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                            tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                            tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                            tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                                tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

                                            tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                            tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                            tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                            tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                            tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                                            tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                            tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                            tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                            tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                            tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());

                                            tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                            tokenRequestVO.setTokenId(tokenResponseVO.getTokenId());
                                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                            String registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                                            tokenResponseVO.setStatus(registrationStatus);
                                            if ("success".equals(registrationStatus))
                                            {
                                                token = tokenDetailsVO.getRegistrationToken();
                                                transactionLogger.debug("token--->" + token);
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
                                if (fromType.equalsIgnoreCase("aldrapay") || fromType.equalsIgnoreCase("purplepay"))
                                {
                                    ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                                    String html                 = apcoPayUtills.getApcoPayRequest(commonValidatorVO, transRespDetails.getResponseHashInfo(), fromType);
                                    pWriter.println(html);
                                    return;
                                }
                                else if (fromType.equalsIgnoreCase("decta"))
                                {
                                    transactionLogger.debug("In single call DECTA ");
                                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                    String html = DectaNewUtils.getCardPaymentForm(commonValidatorVO, transRespDetails.getRedirectUrl());
                                    //transactionLogger.error("Decta form = "+html);
                                    pWriter.println(html);
                                    return;
                                }
                                else if (fromType.equalsIgnoreCase("Appletree"))
                                {
                                    transactionLogger.debug("In single call Appletree ");
                                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                    //String html = AppleTreeCellulantUtils.getCardPaymentForm(transRespDetails.getRedirectUrl());
                                    pWriter.println(transRespDetails.getRedirectUrl());
                                    return;
                                }
                                else if (fromType.equalsIgnoreCase("Oculus"))
                                {
                                    transactionLogger.debug("Oculus Frictionless3DS 3Dv2");
                                    transRespDetails.setThreeDVersion("3Dv2");
                                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                                    paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO);
                                    respStatus = "Frictionless3DS";

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
                            respRemark      = transRespDetails.getDescription();
                            respPaymentId   = transRespDetails.getTransactionId();
                            if (functions.isValueNull(transRespDetails.getErrorName()))
                                errorName = transRespDetails.getErrorName();
                            if (functions.isValueNull(transRespDetails.getEci()))
                            {
                                eci = transRespDetails.getEci();
                                commonValidatorVO.setEci(eci);
                            }
                            if (functions.isValueNull(transRespDetails.getBankCode()) && functions.isValueNull(transRespDetails.getBankDescription()))
                            {
                                commonValidatorVO.setBankCode(transRespDetails.getBankCode());
                                commonValidatorVO.setBankDescription(transRespDetails.getBankDescription());
                            }
                            if (fromType.equalsIgnoreCase("Oculus") && respStatus.equals("Frictionless3DS"))
                            {
                                respStatus = "capturesuccess";
                                singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci, transRespDetails.getRrn(), transRespDetails.getArn(), transRespDetails.getAuthCode(), "3Dv2");
                            }
                            else if (functions.isValueNull(transRespDetails.getTerminalId()))
                            {
                                singleCallPaymentDAO.updateTransactionAfterResponseFor3DRouting(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci, transRespDetails.getRrn(), transRespDetails.getArn(), transRespDetails.getAuthCode(), transRespDetails.getTerminalId(), transRespDetails.getFromAccountId(), transRespDetails.getFromMid(), "Non-3D");
                            }
                            else{
                                singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid, eci, transRespDetails.getRrn(), transRespDetails.getArn(), transRespDetails.getAuthCode(), "Non-3D");
                            }
                            if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && !"split".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                            {
                                for (int i = 0; i < mpDetailsList.size(); i++)
                                {
                                    marketPlaceVO=mpDetailsList.get(i);
                                    singleCallPaymentDAO.updateTransactionAfterResponse(tableName, respStatus, marketPlaceVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, marketPlaceVO.getTrackingid(), eci,transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                                    if(respStatus.equalsIgnoreCase("capturesuccess"))
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    else if(respStatus.equalsIgnoreCase("authsuccessful"))
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                    else if (respStatus.equalsIgnoreCase("authfailed"))
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), commonValidatorVO.getMarketPlaceVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO, auditTrailVO, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                }
                            }
                        }
                        transactionLogger.debug("isSuccessful----------->" + isSuccessful);
                        transactionLogger.debug("merchantDetailsVO.getIsTokenizationAllowed()-------------->" + merchantDetailsVO.getIsTokenizationAllowed());
                        if ("Y".equals(isSuccessful) && "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                        {
                            if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                            {
                                TokenManager tokenManager = new TokenManager();
                                TokenRequestVO tokenRequestVO = new TokenRequestVO();
                                TokenResponseVO tokenResponseVO = new TokenResponseVO();
                                TokenDetailsVO tokenDetailsVO=new TokenDetailsVO();
                                String expDate="";
                                if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
                                    expDate=commonValidatorVO.getCardDetailsVO().getExpMonth()+"/"+commonValidatorVO.getCardDetailsVO().getExpYear();
                                String strToken = tokenManager.isTokenAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum(),expDate);
                                transactionLogger.debug("strToken--->" + strToken);
                                tokenRequestVO.setIsActive("Y");
                                if (functions.isValueNull(strToken))
                                {
                                    tokenRequestVO.setTokenId(strToken);
                                    tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                    tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                    tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                    tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                        tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

                                    tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                    tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                    tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                    tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                    tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                                    tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                    tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                    tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                    tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());

                                    tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                    tokenResponseVO.setStatus(tokenDetailsVO.getStatus());
                                    tokenResponseVO.setTokenId(strToken);
                                    tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                                    token = tokenDetailsVO.getRegistrationToken();
                                    commonValidatorVO.setToken(token);
                                }
                                else
                                {
                                    CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                                    commCardDetailsVO.setCardType(String.valueOf(cardType));
                                    commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                                    commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                                    commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                                    commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());

                                    tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                    tokenRequestVO.setTrackingId(String.valueOf(trackingid));
                                    tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                                    tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                                    tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                                        tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

                                    tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                                    tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                                    tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                                    tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                                    tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                                    tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                                    tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                                    tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                                    tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                                    tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());

                                    tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                                    tokenRequestVO.setTokenId(tokenResponseVO.getTokenId());
                                    tokenDetailsVO=tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                                    if ("success".equals(tokenResponseVO.getStatus()))
                                    {
                                        token = tokenDetailsVO.getRegistrationToken();
                                        transactionLogger.debug("token--->" + token);
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
                    req.setAttribute("transDetails", commonValidatorVO);
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
                    req.setAttribute("transDetails", commonValidatorVO);
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
                    req.setAttribute("transDetails", commonValidatorVO);
                    //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }

            }
            PZExceptionHandler.handleTechicalCVEException(e, toid, PZOperations.STANDARDKIT_SALE);


            req.setAttribute("error", mailtransactionStatus);
            req.setAttribute("transDetails", commonValidatorVO);
            //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            try
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
                    transactionLogger.debug("ConsException PT CT---" + commonValidatorVO.getPaymentType() + "--" + commonValidatorVO.getCardType());
                    List<String> cardList = new ArrayList<String>();
                    cardList.add(commonValidatorVO.getCardType());
                    paymentTypeMap.put(commonValidatorVO.getPaymentType(), cardList);


                    PartnerDetailsVO partnerDetailsVO=new PartnerDetailsVO();

                    transactionLogger.debug("template details---" + commonValidatorVO.getMerchantDetailsVO().getPartnerId() + "---" + commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                    partnerDetailsVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                    partnerDetailsVO.setPartnertemplate(commonValidatorVO.getMerchantDetailsVO().getPartnertemplate());

                    commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                    commonValidatorVO.setVersion("2");

                    commonInputValidator.setAllTemplateInformationRelatedToMerchant(commonValidatorVO.getMerchantDetailsVO(), commonValidatorVO.getVersion());
                    commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
                    commonInputValidator.getPaymentPageTemplateDetails(commonValidatorVO, session);

                    session.setAttribute("merchantLogoName", commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
                }
                merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

                req.setAttribute("errorName", errorName);
                commonValidatorVO.setMapOfPaymentCardType(paymentTypeMap);
                commonValidatorVO.setTerminalMap(terminalMap);
                commonValidatorVO.setTerminalMapLimitRouting(terminalMapLimitRouting);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                req.setAttribute("transDetails", commonValidatorVO);
                session.setAttribute("ctoken", ctoken);

                if(paymentTypeMap != null && (paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.CARD.getValue())) ||
                        paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.BDMobileBanking.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.TIGERPAY.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.SMARTFASTPAY.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.BITCOIN.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.CASH.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.BankTransfer.getValue()))
                        || paymentTypeMap.containsKey(String.valueOf(PaymentModeEnum.WALLET_PAYMODE.getValue()))
                        )){
                    transactionLogger.error(" inside checkoutError.jsp ");
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }else{
                    transactionLogger.error(" inside checkoutPayment.jsp ");
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutPayment.jsp?ctoken=" + ctoken);
                    requestDispatcher.forward(req, res);
                    return;
                }


            }
            catch (PZDBViolationException dbe)
            {
                log.error("----PZDBViolationException in SingleCallPayment----", dbe);
                error = "Internal Errror Occured : Please contact Customer support for further help";
                PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
                req.setAttribute("error", error);
                req.setAttribute("transDetails", commonValidatorVO);
                //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
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
                req.setAttribute("transDetails", commonValidatorVO);
                //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetails", commonValidatorVO);
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
                req.setAttribute("transDetails", commonValidatorVO);
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
        catch (UniformInterfaceException e)
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
                req.setAttribute("transDetails", commonValidatorVO);
                //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }

            mailtransactionStatus = "Bank Connectivity Issue while processing your request.";
            req.setAttribute("error", mailtransactionStatus);
            req.setAttribute("transDetails", commonValidatorVO);
            //RequestDispatcher requestDispatcher = req.getRequestDispatcher("/error.jsp?ctoken=" + ctoken);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            requestDispatcher.forward(req, res);
            return;
        }
        try
        {
            merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            transactionLogger.debug("TransactionNotification flag for ---" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + merchantDetailsVO.getTransactionNotification());

            String notificationUrl = "";

            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())){
                notificationUrl = commonValidatorVO.getTransDetailsVO().getNotificationUrl();
            }
            if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                notificationUrl = merchantDetailsVO.getNotificationUrl();
            }

            transactionLogger.error(trackingid + " NotificationUrl() --> " + notificationUrl);

            if (functions.isValueNull(notificationUrl) && ("Non-3D".equals(merchantDetailsVO.getTransactionNotification()) || "Both".equals(merchantDetailsVO.getTransactionNotification())) && !"pending".equalsIgnoreCase(respStatus))
            {
                transactionLogger.debug("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                TransactionDetailsVO transactionDetailsVO1 			= transactionUtility.getTransactionDetails(commonValidatorVO);
                if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(transactionDetailsVO1.getCcnum())){
                    transactionDetailsVO1.setCcnum("");
                }
                if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    transactionDetailsVO1.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                }else{
                    transactionDetailsVO1.setMerchantNotificationUrl("");
                }
                transactionDetailsVO1.setBankReferenceId(respPaymentId);
                AsyncNotificationService asyncNotificationService 	= AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, commonValidatorVO.getTrackingid(), respStatus, respRemark, "");
            }
        }catch(PZDBViolationException e){
            transactionLogger.error("PZDBViolationException-----------", e);
        }

        //auto redirect check
        if (commonValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
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
        Functions functions                     = new Functions();
        CommRequestVO commRequestVO             = null;
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO = new CommDeviceDetailsVO();
        RecurringBillingVO recurringBillingVO   = commonValidatorVO.getRecurringBillingVO();

        if (null == recurringBillingVO)
        {
            recurringBillingVO = new RecurringBillingVO();
            commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        }

        GatewayAccount account  = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId          = account.getMerchantId();
        String alias            = account.getAliasName();
        String username         = account.getFRAUD_FTP_USERNAME();
        String password         = account.getFRAUD_FTP_PASSWORD();
        String displayname      = account.getDisplayName();
        /*String isAutomaticRecurring = commonValidatorVO.getTerminalVO().getIsRecurring();*/
        //String isManualRecurring = commonValidatorVO.getTerminalVO().getIsManualRecurring();
        String recurringType    = "";
        String consentStmnt     = "";


        cardDetailsVO.setName_on_card(commonValidatorVO.getCardDetailsVO().getCardHolderName());
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
        transDetailsVO.setToId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        transDetailsVO.setTotype(commonValidatorVO.getTransDetailsVO().getTotype());

        transDetailsVO.setTotype(commonValidatorVO.getTransDetailsVO().getTotype());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        merchantAccountVO.setZipCode(commonValidatorVO.getMerchantDetailsVO().getZip());
        merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
        merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        String merchantTelNo = commonValidatorVO.getMerchantDetailsVO().getTelNo();
        String mPhone = "";

        if (functions.isValueNull(merchantTelNo) && merchantTelNo.contains("-"))
        {
            String[] phone  = merchantTelNo.split("-");
            mPhone          = phone[1];
        }
        else
        {
            mPhone = merchantTelNo;
        }
        if (functions.isValueNull(mPhone))
            merchantAccountVO.setMerchantSupportNumber(mPhone);
        merchantAccountVO.setCountry(commonValidatorVO.getMerchantDetailsVO().getCountry());

        String contactPerson = commonValidatorVO.getMerchantDetailsVO().getContact_persons().trim();
        String firstName    = "";
        String lastName     = "";

        if (functions.isValueNull(contactPerson) && contactPerson.contains(" "))
        {
            String[] person = contactPerson.split(" ");
            firstName   = person[0];
            lastName    = person[1];
        }
        else
        {
            firstName   = contactPerson;
            lastName    = contactPerson;
        }

        merchantAccountVO.setFirstName(firstName);
        merchantAccountVO.setLastName(lastName);
        merchantAccountVO.setIsService(commonValidatorVO.getMerchantDetailsVO().getService());
        String partnerTelNo = commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber();
        String pPhone = "";
        if (functions.isValueNull(partnerTelNo) && partnerTelNo.contains("-"))
        {
            String[] phone  = partnerTelNo.split("-");
            pPhone          = phone[1];
        }
        else
        {
            pPhone = partnerTelNo;
        }
        if (functions.isValueNull(pPhone))
            merchantAccountVO.setPartnerSupportContactNumber(pPhone);
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMerchantLogo()) && commonValidatorVO.getMerchantDetailsVO().getMerchantLogo().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag()) && commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        if(commonValidatorVO.getDeviceDetailsVO() != null)
        {
            commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
            commDeviceDetailsVO.setAcceptHeader(commonValidatorVO.getDeviceDetailsVO().getAcceptHeader());
            commDeviceDetailsVO.setFingerprints(commonValidatorVO.getDeviceDetailsVO().getFingerprints());
        }

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

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        if("PFS".equalsIgnoreCase(fromType))
        {
            transDetailsVO.setResponseHashInfo(UUID.randomUUID().toString());
        }
        commRequestVO.setDeviceId(commonValidatorVO.getDeviceId());
        commRequestVO.setUniqueId(commonValidatorVO.getUniqueId());
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
            consentStmnt    = commonValidatorVO.getConsentStmnt();
            commRequestVO.setConsentStmnt(consentStmnt);
        }
        Hashtable additioanlParams = new Hashtable();
        if(commonValidatorVO.getProcessorName() != null && AppleTreeCellulantPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType)){
            additioanlParams.put("paymentOtionCode",commonValidatorVO.getProcessorName());
            commRequestVO.setAdditioanlParams(additioanlParams);
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
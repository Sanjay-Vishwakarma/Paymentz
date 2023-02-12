package com.payment.paytm;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class PayTMPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(PayTMPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "paytm";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.paytm");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PayTMPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of PayTMPaymentGateway......");

        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions             = new Functions();

        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest          = gatewayAccount.isTest();
        String Merchant_ID      = gatewayAccount.getMerchantId();
        String Merchant_Key     = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String requestType      = "Payment";
        String websiteName      = "WEBSTAGING";
        String cardTypeId       = transactionDetailsVO.getCardType();
        String channelCode      = transactionDetailsVO.getCustomerBankId();
        String paymentMode      = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        paymentMode             = PayTMUtils.getPaymentType(paymentMode);
        String cardType         = GatewayAccountService.getCardType(cardTypeId);
        transactionlogger.error("Sale cardType ---> "+trackingID+" "+cardType);

        String REQUEST_URL  = "";
        String TOKEN_URL    = "";

        //Card Details
        /*String ccnum        = commCardDetailsVO.getCardNum();
        String ccname       = "";
        String ccvv         = commCardDetailsVO.getcVV();
        String ccexpmon     = commCardDetailsVO.getExpMonth();
        String ccexpyr      = commCardDetailsVO.getExpYear();*/

        //txnAmount
        String value     = transactionDetailsVO.getAmount();
        String currency  = transactionDetailsVO.getCurrency();

        //userInfo
        String custId       = trackingID;
        String orderId      = trackingID;
        String callbackUrl  =  RB.getString("RETURN_URL")+trackingID;

        String txnToken         = "";
        String payerAccount     = commRequestVO.getCustomerId();

        JSONObject paytmParams      = new JSONObject();
        JSONObject body             = new JSONObject();
        JSONObject txnAmount        = new JSONObject();
        JSONObject head             = new JSONObject();
        JSONObject userInfo         = new JSONObject();
        JSONObject paytmFinalParams = new JSONObject();

        try
        {
            if("UPIQR".equalsIgnoreCase(cardType)){

                if(isTest){
                    REQUEST_URL     = RB.getString("TEST_QR_SALE_URL");
                }else{
                    REQUEST_URL     = RB.getString("LIVE_QR_SALE_URL");
                }

                body.put("mid", Merchant_ID);
                body.put("orderId", orderId);
                body.put("amount", value);
                body.put("businessType", "UPI_QR_CODE");
                body.put("posId", orderId);

                String checksum = PayTMUtils.generateSignature(body.toString(), Merchant_Key);

                head.put("clientId", "C11");
                head.put("version", "v1");
                head.put("signature", checksum);

                paytmParams.put("body", body);
                paytmParams.put("head", head);

                transactionlogger.error("Sale REQUEST_URL ---> "+trackingID+" "+REQUEST_URL);
                transactionlogger.error("Sale REQUEST ---> "+trackingID+" "+paytmParams.toString());

                String responseString  =   PayTMUtils.doPostHttpUrlConnection(paytmParams.toString(), REQUEST_URL);

                transactionlogger.error("Sale response ---> "+trackingID+" "+responseString.toString());

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){

                    JSONObject responseJSON     =  new JSONObject(responseString);
                    JSONObject resBodyJSON      =  null;
                    JSONObject resultInfoJSON   =  null;
                    String resultStatus = "";
                    String resultCode   = "";
                    String resultMsg    = "";
                    String qrCodeId     = "";
                    String qrData       = "";
                    String image        = "";

                    if(responseJSON.has("body")){
                        resBodyJSON = responseJSON.getJSONObject("body");

                        if(resBodyJSON != null){

                            if(resBodyJSON.has("qrCodeId")){
                                qrCodeId = resBodyJSON.getString("qrCodeId");
                            }
                            if(resBodyJSON.has("qrData")){
                                qrData = resBodyJSON.getString("qrData");
                            }
                            if(resBodyJSON.has("image")){
                                image = resBodyJSON.getString("image");
                            }

                            if(resBodyJSON.has("resultInfo")){
                                resultInfoJSON = resBodyJSON.getJSONObject("resultInfo");
                            }

                            if(resultInfoJSON != null){

                                if(resultInfoJSON.has("resultStatus")){
                                    resultStatus = resultInfoJSON.getString("resultStatus");
                                }

                                if(resultInfoJSON.has("resultCode")){
                                    resultCode = resultInfoJSON.getString("resultCode");
                                }

                                if(resultInfoJSON.has("resultMsg")){
                                    resultMsg = resultInfoJSON.getString("resultMsg");
                                }
                            }
                        }

                        if("SUCCESS".equalsIgnoreCase(resultStatus) && "QR_0001".equalsIgnoreCase(resultCode)){
                            Map<String, String> requestMap = new HashMap<String, String>();
                            requestMap.put("image",image);

                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setRequestMap(requestMap);

                        }else if("FAILURE".equalsIgnoreCase(resultStatus)){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setRemark(resultStatus);
                            commResponseVO.setDescription(resultMsg);
                        }else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }
                    }

                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else{

                if(isTest){
                    REQUEST_URL     = RB.getString("TEST_SALE_URL");
                    TOKEN_URL       = RB.getString("TEST_TOKEN_URL");
                }else{
                    REQUEST_URL     = RB.getString("LIVE_SALE_URL");
                    TOKEN_URL       = RB.getString("LIVE_TOKEN_URL");
                }

                body.put("requestType", requestType);
                body.put("mid", Merchant_ID);
                body.put("websiteName", websiteName);
                body.put("orderId", orderId);
                body.put("callbackUrl", callbackUrl);

                txnAmount.put("value", value);
                txnAmount.put("currency", currency);

                userInfo.put("custId", custId);

                body.put("txnAmount", txnAmount);
                body.put("userInfo", userInfo);

                REQUEST_URL = REQUEST_URL+"mid="+Merchant_ID+"&orderId="+orderId;
                TOKEN_URL   = TOKEN_URL+"mid="+Merchant_ID+"&orderId="+orderId;

                String checksum = PayTMUtils.generateSignature(body.toString(), Merchant_Key);
                head.put("signature", checksum);

                paytmParams.put("body", body);
                paytmParams.put("head", head);

                transactionlogger.error("Sale paytm initate Token REQUEST_URL ---> "+trackingID+" "+TOKEN_URL);
                transactionlogger.error("Sale paytm initate  Token Request ---> "+trackingID+" "+paytmParams.toString());

                String tokenResponse  =   PayTMUtils.doPostHttpUrlConnection(paytmParams.toString(), TOKEN_URL);

                transactionlogger.error("Sale paytm initate  Token Response ---> "+trackingID+" "+tokenResponse);

                if(functions.isValueNull(tokenResponse) ){
                    JSONObject tokenJSON        = new JSONObject(tokenResponse);
                    JSONObject tokenbodyJSON    = null;
                    if(tokenJSON.has("body")){
                        tokenbodyJSON = tokenJSON.getJSONObject("body");
                    }
                    if(tokenbodyJSON != null && tokenJSON.has("body")){
                        txnToken = tokenbodyJSON.getString("txnToken");
                    }
                }else{
                    transactionlogger.error("Paytmgateway Tokengeneration failed");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                    return commResponseVO;
                }

                if(functions.isValueNull(txnToken)){
                    body                = new JSONObject();
                    requestType         = "NATIVE";

                    //Final Request
                    body.put("requestType", requestType);
                    body.put("mid", Merchant_ID);
                    body.put("orderId", orderId);
                    body.put("paymentMode",paymentMode);
                    if("UPI".equalsIgnoreCase(paymentMode)){
                        body.put("payerAccount", payerAccount);
                    }
                    else if ("NET_BANKING".equalsIgnoreCase(paymentMode)){
                        body.put("channelCode",channelCode);
                    }
                     else if ("WALLET".equalsIgnoreCase(paymentMode)){
                        body.put("WALLET",channelCode);
                    }
                    else{
                        //todo need to failed
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription("Incorrect Request");
                        commResponseVO.setRemark("Incorrect Request");
                        return commResponseVO;
                    }

                    head = new JSONObject();
                    head.put("txnToken", txnToken);

                    paytmFinalParams.put("body", body);
                    paytmFinalParams.put("head", head);

                    transactionlogger.error("Sale process transaction paytm REQUEST_URL ---> "+trackingID+" "+REQUEST_URL);
                    transactionlogger.error("Sale process transaction paytm Request ---> "+trackingID+" "+paytmFinalParams.toString());

                    String responseString  =   PayTMUtils.doPostHttpUrlConnection(paytmFinalParams.toString(), REQUEST_URL);

                    transactionlogger.error("Sale process transaction paytm Response ---> "+trackingID+" "+responseString);

                    String actionUrl = "";
                    String mbid	     = "";
                    String orderid	 = "";
                    String txnamount = "";
                    String bankAbbr	 = "";
                    String channelid = "";
                    String txnid	 = "";
                    String isResend  = "";
                    String resultStatus = "";
                    String resultCode   = "";
                    String resultMsg    = "";

                    String MERCHANT_VPA ="";
                    String resptxnToken ="";
                    String CHANNEL      ="";
                    String externalSrNo ="";
                    String MCC          ="";
                    String payerVpa     ="";
                    String restxnAmount ="";

                    if(functions.isValueNull(responseString) ){

                        JSONObject responseJSON     = new JSONObject(responseString);
                        JSONObject bodyJSON         = null;
                        JSONObject bankFormJSON     = null;
                        JSONObject redirectFormJSON = null;
                        JSONObject contentJSON      = null;
                        JSONObject resultInfoJSON   = null;


                        if(responseJSON.has("body")){
                            bodyJSON = responseJSON.getJSONObject("body");
                        }
                        transactionlogger.error("Sale Response bodyJSON---> "+trackingID+" "+bodyJSON);
                        if(bodyJSON  != null){
                            if(bodyJSON.has("bankForm")){
                                bankFormJSON = bodyJSON.getJSONObject("bankForm");
                            }
                            transactionlogger.error("Sale Response bankFormJSON---> "+trackingID+" "+bankFormJSON);
                            if(bodyJSON.has("resultInfo")){
                                resultInfoJSON = bodyJSON.getJSONObject("resultInfo");
                            }
                            transactionlogger.error("Sale Response resultInfoJSON---> "+trackingID+" "+resultInfoJSON);
                            if(resultInfoJSON != null){
                                transactionlogger.error("Inside resultInfoJSON---> ");
                                if(resultInfoJSON.has("resultStatus")){
                                    resultStatus = resultInfoJSON.getString("resultStatus");
                                }
                                if(resultInfoJSON.has("resultCode")){
                                    resultCode = resultInfoJSON.getString("resultCode");
                                }
                                if(resultInfoJSON.has("resultMsg")){
                                    resultMsg = resultInfoJSON.getString("resultMsg");
                                }
                            }

                            if(bankFormJSON != null){
                                if(bankFormJSON.has("redirectForm")){
                                    redirectFormJSON = bankFormJSON.getJSONObject("redirectForm");
                                }

                                if( redirectFormJSON != null && redirectFormJSON.has("actionUrl")){
                                    if(redirectFormJSON.has("actionUrl")){
                                        actionUrl = redirectFormJSON.getString("actionUrl");
                                    }
                                    if(redirectFormJSON.has("content")){
                                        contentJSON = redirectFormJSON.getJSONObject("content");
                                    }
                                    if(contentJSON != null){
                                        transactionlogger.error("Inside contentJSON actionUrl---> "+contentJSON);
                                        if(contentJSON.has("MERCHANT_VPA")){
                                            MERCHANT_VPA = contentJSON.getString("MERCHANT_VPA");
                                        }
                                        if(contentJSON.has("txnToken")){
                                            resptxnToken = contentJSON.getString("txnToken");
                                        }
                                        if(contentJSON.has("CHANNEL")){
                                            CHANNEL = contentJSON.getString("CHANNEL");
                                        }
                                        if(contentJSON.has("externalSrNo")){
                                            externalSrNo = contentJSON.getString("externalSrNo");
                                        }
                                        if(contentJSON.has("MCC")){
                                            MCC = contentJSON.getString("MCC");
                                        }
                                        if(contentJSON.has("payerVpa")){
                                            payerVpa = contentJSON.getString("payerVpa");
                                        }
                                        if(contentJSON.has("txnAmount")){
                                            restxnAmount = contentJSON.getString("txnAmount");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    transactionlogger.error("actionUrl ---> "+actionUrl);
                    transactionlogger.error("resultStatus "+resultStatus);

                    if(functions.isValueNull(actionUrl) && resultStatus.equalsIgnoreCase("S")
                            && resultCode.equalsIgnoreCase("0000")){
                        Map<String, String> requestMap = new HashMap<String, String>();
                        transactionlogger.error("Inside requestMap---> "+requestMap);
                        requestMap.put("MERCHANT_VPA",MERCHANT_VPA);
                        requestMap.put("txnToken",resptxnToken);
                        requestMap.put("CHANNEL",CHANNEL);
                        requestMap.put("externalSrNo",externalSrNo);
                        requestMap.put("MCC",MCC);
                        requestMap.put("payerVpa",payerVpa);
                        requestMap.put("txnAmount",restxnAmount);

                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(actionUrl);
                        commResponseVO.setRequestMap(requestMap);

                    }else if( resultStatus.equalsIgnoreCase("F") && resultCode.equalsIgnoreCase("0001")){
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark(resultMsg);
                        commResponseVO.setDescription(resultMsg);
                    }else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    transactionlogger.error("Transaction Failed due to null json");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
        }catch (Exception e){
            transactionlogger.error("PayTMPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in PayU ---- ");
        String html                             = "";
        Comm3DResponseVO transRespDetails       = null;
        PayTMUtils payTMUtils                     = new PayTMUtils();
        PayTMPaymentProcess payTMPaymentProcess   = new PayTMPaymentProcess();
        CommRequestVO commRequestVO              = payTMUtils.getPayTMRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount            = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = payTMPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect PayU form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in PayTMPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of paytm---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                = gatewayAccount.isTest();
        Functions functions           = new Functions();
        PayTMUtils payTMUtils         = new PayTMUtils();


        JSONObject paytmParams  = new JSONObject();
        JSONObject body         = new JSONObject();
        JSONObject head         = new JSONObject();

        String Merchant_ID     = gatewayAccount.getMerchantId();
        String Merchant_Key    = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String orderId         = trackingId;

        String REQUEST_URL     = "";

        String inquiry_res = "";

        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            body.put("mid", Merchant_ID);
            body.put("orderId", orderId);

            String checksum = payTMUtils.generateSignature(body.toString(), Merchant_Key);
            head.put("signature", checksum);

            paytmParams.put("body", body);
            paytmParams.put("head", head);

            transactionlogger.error("inquiry res is -- > "+trackingId+" "+paytmParams.toString());
            inquiry_res = payTMUtils.doPostHttpUrlConnection(paytmParams.toString(), REQUEST_URL);
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);

            if (functions.isValueNull(inquiry_res) )
            {
                String status           = "";
                String transactionId    = "";
                String approvedAmount   = "";
                String dateTime         = "";
                String bank_ref_num     = "";
                String unmappedstatus   = "";
                String mode             = "";
                String bankcode         = "";
                String field7           = "";

                String resultStatus = "";
                String resultCode   = "";
                String resultMsg    = "";

                String txnId        = "";
                String bankTxnId    = "";
                String txnAmount    = "";
                String authRefId    = "";
                String txnDate      = "";


                JSONObject inquiryJSON  = new JSONObject(inquiry_res);
                JSONObject bodyJSON             = null;
                JSONObject resultInfoJSON       = null;
                if(inquiryJSON.has("body"))
                {
                    bodyJSON = inquiryJSON.getJSONObject("body");
                    if(bodyJSON != null){
                        resultInfoJSON = bodyJSON.getJSONObject("resultInfo");

                        if(resultInfoJSON != null){
                            if(resultInfoJSON.has("resultStatus")){
                                resultStatus = resultInfoJSON.getString("resultStatus");
                            }
                            if(resultInfoJSON.has("resultCode")){
                                resultCode = resultInfoJSON.getString("resultCode");
                            }
                            if(resultInfoJSON.has("resultMsg")){
                                resultMsg = resultInfoJSON.getString("resultMsg");
                            }
                            if(resultInfoJSON.has("authRefId")){
                                authRefId = resultInfoJSON.getString("authRefId");
                            }
                            if(resultInfoJSON.has("txnDate")){
                                authRefId = resultInfoJSON.getString("txnDate");
                            }
                        }

                        if(bodyJSON.has("txnId")){
                            txnId = bodyJSON.getString("txnId");
                        }
                        if(bodyJSON.has("bankTxnId")){
                            bankTxnId = bodyJSON.getString("bankTxnId");
                        }
                        if(bodyJSON.has("txnAmount")){
                            txnAmount = bodyJSON.getString("txnAmount");
                        }
                        if(bodyJSON.has("txnDate")){
                            txnDate = bodyJSON.getString("txnDate");
                        }
                    }
                }


                    if (resultStatus.equalsIgnoreCase("TXN_SUCCESS") && resultCode.equalsIgnoreCase("01") )
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(resultMsg);
                        commResponseVO.setAmount(txnAmount);
                        commResponseVO.setTransactionId(txnId);
                        commResponseVO.setBankTransactionDate(txnDate);
                        commResponseVO.setResponseHashInfo(txnId);
                        commResponseVO.setRrn(bankTxnId);
                        commResponseVO.setMerchantId(Merchant_ID);

                        payTMUtils.updateRRNMainTableEntry(bank_ref_num,trackingId);
                    }
                    else if(resultStatus.equalsIgnoreCase("TXN_FAILURE"))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(resultMsg);
                        commResponseVO.setAmount(txnAmount);
                        commResponseVO.setTransactionId(txnId);
                        commResponseVO.setBankTransactionDate(txnDate);
                        commResponseVO.setResponseHashInfo(txnId);
                        commResponseVO.setRrn(bankTxnId);
                        commResponseVO.setMerchantId(Merchant_ID);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
            }
            // no response set pending
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

            commResponseVO.setTransactionType(PZProcessType.SALE.toString());

        }catch (Exception e){
            transactionlogger.error("PayTMPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside PayTM process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        PayTMUtils payTMUtils                               = new PayTMUtils();


        String Merchant_ID    = gatewayAccount.getMerchantId();
        String Merchant_Key   = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String subwalletGuid  = gatewayAccount.getFRAUD_FTP_USERNAME();
        String hostURL        = "";

        JSONObject paytmParams  = new JSONObject();
        Date date                   = new Date();
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd");

        String orderId              = trackingId;
        String beneficiaryAccount   = "";
        String beneficiaryIFSC      = "";
        String amount               = "";
        String purpose              = "OTHERS";
        String strDate              = formatter.format(date);
        String transferMode         = "";
        String beneficiaryPhoneNo   = "";
        String beneficiaryVPA        = ""; //need if transferMode is UPI
        String beneficiaryName      = "";

        try
        {
            if (isTest)
            {
                hostURL  = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                hostURL = RB.getString("LIVE_PAYOUT_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                String tempbeneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();

                if(tempbeneficiaryName.contains("-")||tempbeneficiaryName.contains("_")||tempbeneficiaryName.contains(",")){
                    beneficiaryName = tempbeneficiaryName.replaceAll("-","").replaceAll("_","").replaceAll(",","");
                }
                else {
                    beneficiaryName = tempbeneficiaryName;
                }

            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                beneficiaryAccount = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                beneficiaryIFSC = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount = commTransactionDetailsVO.getAmount();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                transferMode = commTransactionDetailsVO.getBankTransferType();
            }

            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-")){
                    beneficiaryPhoneNo  = commAddressDetailsVO.getPhone().split("-")[1];
                }
                else{
                    beneficiaryPhoneNo  = commAddressDetailsVO.getPhone();
                }
            }

            paytmParams.put("subwalletGuid", subwalletGuid);
            paytmParams.put("orderId", orderId);
            paytmParams.put("beneficiaryAccount", beneficiaryAccount);
            paytmParams.put("beneficiaryIFSC", beneficiaryIFSC);
            paytmParams.put("beneficiaryName", beneficiaryName);
            paytmParams.put("beneficiaryPhoneNo", beneficiaryPhoneNo);
            paytmParams.put("amount", amount);
            paytmParams.put("purpose", purpose);
            paytmParams.put("date", strDate);
            paytmParams.put("transferMode", transferMode);
            //paytmParams.put("beneficiaryVPA", beneficiaryVPA);

            String checksum = payTMUtils.generateSignature(paytmParams.toString(), Merchant_Key);

            String responeString = payTMUtils.doPostURLConnectionClient(paytmParams.toString(),hostURL, Merchant_ID,checksum);

            transactionlogger.error("Payout Requesr ------> "+trackingId+ responeString);


            String statusCode       = "";
            String statusMessage    = "";
            String status           = "";

            if(functions.isValueNull(responeString) && responeString.contains("{"))
            {
                JSONObject payoutResponse = new JSONObject(responeString);


                if (payoutResponse.has("statusCode") && functions.isValueNull(payoutResponse.getString("statusCode")))
                {
                    statusCode = payoutResponse.getString("statusCode");
                }
                if (payoutResponse.has("status") && functions.isValueNull(payoutResponse.getString("status")))
                {
                    status = payoutResponse.getString("status");
                }

                if (payoutResponse.has("statusMessage") && functions.isValueNull(payoutResponse.getString("statusMessage")))
                {
                    statusMessage = payoutResponse.getString("statusMessage");
                }


                if (status.equalsIgnoreCase("SUCCESS") && statusCode.equalsIgnoreCase("DE_001"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);

                }if (status.equalsIgnoreCase("ACCEPTED") && statusCode.equalsIgnoreCase("DE_002"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);

                }
                else if ((status.equalsIgnoreCase("FAILURE") && PayTMUtils.isErrorCodeExists(statusCode))
                    ||(status.equalsIgnoreCase("FAILED") && PayTMUtils.isErrorCodeExists(statusCode)))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

            }


            /*String CALL_EXECUTE_AFTER       = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL    = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC          = RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed          = RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncPayTMPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }*/

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayout ----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside IMoneyPay processPayout inquiry------->");

        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        PayTMUtils payTMUtils           = new PayTMUtils();
        Functions functions             = new Functions();
        String Merchant_ID    = gatewayAccount.getMerchantId();
        String Merchant_Key   = gatewayAccount.getFRAUD_FTP_PASSWORD();

        boolean isTest          = false;
        String hostURL          = "";
        String orderId          = trackingId;
        JSONObject paytmParams  = new JSONObject();
        try
        {
            isTest    = gatewayAccount.isTest();
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            paytmParams.put("orderId", orderId);

            String checksum     = payTMUtils.generateSignature(paytmParams.toString(), Merchant_Key);
            transactionlogger.error("payoutInquiry Request ------> "+ trackingId +" "+ paytmParams.toString());
            String responeString = payTMUtils.doPostURLConnectionClient(paytmParams.toString(), hostURL, Merchant_ID, checksum);

            transactionlogger.error("payoutInquiry Response ------> "+ trackingId +" "+ responeString);

            String statusCode    = "";
            String statusMessage = "";
            String status        = "";
            String paytmOrderId  = "";
            String amount        = "";
            String rrn           = "";

            if(functions.isValueNull(responeString) )
            {
                JSONObject payoutResponse = new JSONObject(responeString);
                JSONObject resultJSON = null;

                if (payoutResponse.has("statusCode") && functions.isValueNull(payoutResponse.getString("statusCode")))
                {
                    statusCode = payoutResponse.getString("statusCode");
                }
                if (payoutResponse.has("status") && functions.isValueNull(payoutResponse.getString("status")))
                {
                    status = payoutResponse.getString("status");
                }

                if (payoutResponse.has("statusMessage") && functions.isValueNull(payoutResponse.getString("statusMessage")))
                {
                    statusMessage = payoutResponse.getString("statusMessage");
                }

                if (payoutResponse.has("result") && functions.isValueNull(payoutResponse.getString("result")))
                {
                    resultJSON = payoutResponse.getJSONObject("result");

                    if(resultJSON != null){
                        if (payoutResponse.has("paytmOrderId") && functions.isValueNull(payoutResponse.getString("paytmOrderId")))
                        {
                            paytmOrderId = payoutResponse.getString("paytmOrderId");
                        }
                        if (payoutResponse.has("amount") && functions.isValueNull(payoutResponse.getString("amount")))
                        {
                            paytmOrderId = payoutResponse.getString("amount");
                        }
                        if (payoutResponse.has("rrn") && functions.isValueNull(payoutResponse.getString("rrn")))
                        {
                            rrn = payoutResponse.getString("rrn");
                        }
                    }
                }


                if (status.equalsIgnoreCase("SUCCESS") && statusCode.equalsIgnoreCase("DE_001"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);
                    commResponseVO.setTransactionId(paytmOrderId);
                    commResponseVO.setResponseHashInfo(paytmOrderId);
                    commResponseVO.setRrn(rrn);
                    commResponseVO.setAmount(amount);

                }if (status.equalsIgnoreCase("ACCEPTED") && statusCode.equalsIgnoreCase("DE_002"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);

                }
                else if ((status.equalsIgnoreCase("FAILURE") && PayTMUtils.isErrorCodeExists(statusCode))
                    ||(status.equalsIgnoreCase("FAILED") && PayTMUtils.isErrorCodeExists(statusCode)))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(statusMessage);
                    commResponseVO.setDescription(statusMessage);
                    commResponseVO.setTransactionId(paytmOrderId);
                    commResponseVO.setResponseHashInfo(paytmOrderId);
                    commResponseVO.setRrn(rrn);
                    commResponseVO.setAmount(amount);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

            }
        }
        catch (Exception e)
        {
            transactionlogger.error("processPayoutInquiry JSONException-->" ,e );
        }

        return commResponseVO;

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("PayTMPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
}
package com.payment.lyra;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Rubixpay.RubixpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.verve.VerveUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 11/02/2022.
 */
public class LyraPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(LyraPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "lyra";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.lyra");
   // private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.LYBANKS");
    String redirecturl= "";
    public LyraPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("Lyra Gateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of LyraPaymentGateway......");
        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String bill_address ="";
        String bill_city    ="";
        String bill_state   ="";
        String bill_country ="";
        String bill_postalcode="";
        String payment_url="";
        String phone="";
        String request="";
        String nbcode="";
        String cardNumber   = commCardDetailsVO.getCardNum();
        String expMonth     = commCardDetailsVO.getExpMonth();
        String expYear      = commCardDetailsVO.getExpYear();
        String CVV          = commCardDetailsVO.getcVV();
        String shopid       = gatewayAccount.getMerchantId();
        String api_Key      = gatewayAccount.getFRAUD_FTP_PASSWORD();

        nbcode              = transactionDetailsVO.getCustomerBankId();
        String vpa          = commRequestVO.getCustomerId();
        String paymode      = transactionDetailsVO.getPaymentType();
        String paymentmode  = LyraPaymentUtils.getPaymentMethod(payment_Card);
        boolean isTest      = gatewayAccount.isTest();
        String client_ip    = "";

        String domainname = gatewayAccount.getFRAUD_FTP_PATH();
        String fullname ="";
        String email = "";
        String orderid      = trackingID;
        String countrycode  = "IND";
        bill_country        = "IND";
        String currencycode = transactionDetailsVO.getCurrency();
        String amount       = LyraPaymentUtils.getAmount(transactionDetailsVO.getAmount());
        String paymenttype  = "sale";


        try{

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
            {
                fullname = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
            }
            else{
                fullname = "Customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                email   = commAddressDetailsVO.getEmail();
            }
            else{
                email   = "transaction@support.com";
            }


            if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
            {
                client_ip =commAddressDetailsVO.getCardHolderIpAddress();

            }
            else{
                client_ip="193.168.1.1";
            }

            if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            {
                bill_address=commAddressDetailsVO.getStreet();
            }
            else{
                bill_address="Rajiv Chowk";
            }
            transactionlogger.error("bill_address is-------------->"+bill_address);
            if(functions.isValueNull(commAddressDetailsVO.getCity()))
            {
                bill_city=commAddressDetailsVO.getCity();
            }
            else{
                bill_city="Delhi";
            }

            if(functions.isValueNull(commAddressDetailsVO.getState()))
            {
                bill_state=commAddressDetailsVO.getState();
            }
            else{
                bill_state="New Delhi";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    phone = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(phone.length()>10){

                    phone = phone.substring(phone.length() - 10);
                }
                else{
                    phone = commAddressDetailsVO.getPhone();
                }
            }
            else{
                phone   = "9999999999";
            }

            if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            {
                bill_postalcode = commAddressDetailsVO.getZipCode();
            }
            else{
                bill_postalcode = "110017";
            }

            String response_url =RB.getString("PAYTM_RU")+trackingID;
            transactionlogger.error("response_url is-------------->"+response_url);
            String cancel_url =RB.getString("PAYTM_RU")+trackingID+"&status=cancel";;
            transactionlogger.error("cancel_url is-------------->"+cancel_url);

            String securePaymentGateway="";

            if (isTest){
                payment_url = RB.getString("TEST_SALE_URL");
            }
            else {
                payment_url = RB.getString("LIVE_SALE_URL");
            }
            transactionlogger.error("payment_url is-------------->"+payment_url);

            String createChargeRequest="{\n" +
                    "\"orderId\": \""+orderid+"\",\n" +
                    "\"orderInfo\": \""+orderid+"\",\n" +
                    "\"currency\": \"INR\",\n" +
                    "\"amount\": "+amount+",\n" +
                    "\"customer\": {\n" +
                    "  \"name\": \""+fullname+"\",\n" +
                    "  \"emailId\": \""+email+"\",\n" +
                    "  \"phone\": \""+phone+"\"\n" +
                    "},\n" +
                    "\"webhook\": {\n" +
                    "  \"url\": \""+response_url+"\"\n" +
                    "},\n" +
                    "\"maxAttempts\":1,\n" +
                    "\"url\" : \""+response_url+"\"\n" +
                    "}";

            transactionlogger.error("CreateCharge REQUEST URL >>>>>>> "+trackingID +" "+payment_url);
            transactionlogger.error("CreateCharge REQUEST >>>>>>>> "+trackingID +" "+createChargeRequest);

            String response= LyraPaymentUtils.doPostHttpUrlConnection(payment_url,createChargeRequest,shopid,api_Key);

            transactionlogger.error("CreateCharge Response >>>>>>> "+trackingID+" "+response);

            JSONObject jsonobj = new JSONObject(response);

            if(jsonobj != null)
            {
                if (jsonobj.has("uuid") && jsonobj.has("status"))
                {
                    if ("DUE".equalsIgnoreCase(jsonobj.getString("status")))
                    {
                        String uuid= jsonobj.getString("uuid");
                                     jsonobj.getString("status");
                        LyraPaymentUtils.updateTransaction(orderid, uuid);
                        String initiatePaymentRequest   = "";
                        if ("CARD".equalsIgnoreCase(paymentmode))
                        {
                            //todo https://api.in.lyra.com/public/v1/charge/{uuid}/submit/card
                            payment_url = RB.getString("INIT_LIVE_SALE_URL")+uuid+"/submit/card";
                            initiatePaymentRequest= "{\n" +
                                    "  \"cardNumber\": \""+cardNumber+"\",\n" +
                                    "  \"expMonth\": \""+expMonth+"\",\n" +
                                    "  \"expYear\": \""+expYear+"\",\n" +
                                    "  \"cvv\": \""+CVV+"\",\n" +
                                    "  \"cardHolderName\": \""+fullname+"\"\n" +
                                    "}";
                        }else if ("NET_BANKING".equalsIgnoreCase(paymentmode))
                        {
                            //todo https://api.in.lyra.com/public/v1/charge/{uuid}/submit/nb
                            payment_url = RB.getString("INIT_LIVE_SALE_URL")+uuid+"/submit/nb";
                            initiatePaymentRequest = "{\n" +
                                    "  \"bankCode\": \""+nbcode+"\"\n" +
                                    "}";

                        }else if ("WALLET".equalsIgnoreCase(paymentmode))
                        {
                            //todo https://api.in.lyra.com/public/v1/charge/{uuid}/submit/wallet
                            payment_url = RB.getString("INIT_LIVE_SALE_URL")+uuid+"/submit/wallet";
                            initiatePaymentRequest = "{\n" +
                                    "  \"walletName\": \""+nbcode+"\"\n" +
                                    "}";

                        }

                        else if ("UPI".equalsIgnoreCase(paymentmode))

                           {
                            payment_url = RB.getString("INIT_LIVE_SALE_URL")+uuid+"/submit/upi";
                               transactionlogger.error("inside upi Request URL "+trackingID +"--"+paymentmode);

                            response_url =  RB.getString("NOTIFY_RU")+trackingID;
                            initiatePaymentRequest = "{\n" +
                                    "\"vpa\": \""+vpa+"\"\n," +
                                    "\"webhook\": \""+response_url+"\"\n" +
                                    "}";

                        }

                        transactionlogger.error("InitiatePayment Request URL "+trackingID +"--"+payment_url);
                        transactionlogger.error("InitiatePayment Request URL "+trackingID +"--"+payment_url);
                        transactionlogger.error("InitiatePayment Request >>>>>>>>> "+ trackingID +""+initiatePaymentRequest);

                        String paymentResponse  = LyraPaymentUtils.doPostInitHttpUrlConnection(payment_url, initiatePaymentRequest, "","");
                        transactionlogger.error("InitiatePayment response--------------for--trackingid::::"+trackingID+"--"+paymentResponse);

                        JSONObject redirectResponseJSON = new JSONObject(paymentResponse);
                        JSONObject paramsJSON           = null;
                        if(redirectResponseJSON.has("params")){
                            paramsJSON           = redirectResponseJSON.getJSONObject("params");
                        }

                        String url="";
                        String MerchantId="";
                        String EncData="";
                        String mid1="";
                        String bankId="";
                        String orderId="";
                        String amount1="";
                        String returnUrl="";
                        String signature="";
                        String ORDER_ID="";
                        String CUST_ID="";
                        String TXN_AMOUNT="";
                        String CALLBACK_URL="";
                        String CHECKSUMHASH="";
                        String PaReq="";
                        String TermUrl="";
                        String pass="";
                        String amt="";
                        String txncurr="";
                        String prodid="";
                        String login="";
                        String bankid="";
                        String ttype="";
                        String custacc="";
                        String udf9="";
                        String ru="";
                        String txnid="";
                        String txnscamt="";
                        String clientcode="";
                        String date="";
                        String code="";
                        String message="";
                        String mid="";
                        Map<String,String> requestMap = new HashMap<>();

                        if(redirectResponseJSON != null)
                        {
                            if (redirectResponseJSON.has("code")){
                                code = redirectResponseJSON.getString("code");
                            }
                            if (redirectResponseJSON.has("message")){
                                message = redirectResponseJSON.getString("message");
                            }

                            if (code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500")){
                                commResponseVO.setStatus("failed");
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                                return  commResponseVO;
                            }


                            if (redirectResponseJSON.has("customerName") && redirectResponseJSON.has("transactionUuid")){
                                transactionlogger.error("Inside UPI condition ");
                                commResponseVO.setStatus("pending");
                                commResponseVO.setDescription("pending");
                                commResponseVO.setRemark("pending");
                                return  commResponseVO;
                            }
                            transactionlogger.error("Inside out side UPI condition ");


                            if (redirectResponseJSON.has("url")){
                                payment_url = redirectResponseJSON.getString("url");
                            }

                            if(paramsJSON != null){
                                if (paramsJSON.has("MerchantId")){
                                    MerchantId  = paramsJSON.getString("MerchantId");
                                    requestMap.put("MerchantId",MerchantId);
                                }

                                if (paramsJSON.has("EncData")){
                                    EncData = paramsJSON.getString("EncData");
                                    requestMap.put("EncData",EncData);
                                }

                                if (paramsJSON.has("PaReq")){
                                    PaReq=paramsJSON.getString("PaReq");
                                    requestMap.put("PaReq",PaReq);
                                }

                                if (paramsJSON.has("mid")){
                                    mid =   paramsJSON.getString("mid");
                                    requestMap.put("mid",mid);
                                }
                                if (paramsJSON.has("bankId")){
                                    bankId  = paramsJSON.getString("bankId");
                                    requestMap.put("bankId",bankId);
                                }
                                if (paramsJSON.has("orderId")){
                                    orderId = paramsJSON.getString("orderId");
                                    requestMap.put("orderId",orderId);
                                }
                                if (paramsJSON.has("amount")){
                                    amount  = paramsJSON.getString("amount");
                                    requestMap.put("amount",amount);
                                }
                                if (paramsJSON.has("returnUrl")){
                                    returnUrl   = paramsJSON.getString("returnUrl");
                                    requestMap.put("returnUrl",returnUrl);
                                }
                                if (paramsJSON.has("signature")){
                                    signature   = paramsJSON.getString("signature");
                                    requestMap.put("signature",signature);
                                }
                                if (paramsJSON.has("ORDER_ID")){
                                    ORDER_ID    = paramsJSON.getString("ORDER_ID");
                                    requestMap.put("ORDER_ID",ORDER_ID);
                                }
                                if (paramsJSON.has("CUST_ID")){
                                    CUST_ID = paramsJSON.getString("CUST_ID");
                                    requestMap.put("CUST_ID",CUST_ID);
                                }
                                if (paramsJSON.has("TXN_AMOUNT")){
                                    TXN_AMOUNT  = paramsJSON.getString("TXN_AMOUNT");
                                    requestMap.put("TXN_AMOUNT",TXN_AMOUNT);
                                }
                                if (paramsJSON.has("CALLBACK_URL")){
                                    CALLBACK_URL =paramsJSON.getString("CALLBACK_URL");
                                }

                                requestMap.put("CALLBACK_URL",response_url);

                                if (paramsJSON.has("CHECKSUMHASH")){
                                    CHECKSUMHASH    = paramsJSON.getString("CHECKSUMHASH");
                                    requestMap.put("CHECKSUMHASH",CHECKSUMHASH);
                                }
                                requestMap.put("TermUrl",response_url);
                                if (paramsJSON.has("pass")){
                                    pass    = paramsJSON.getString("pass");
                                    requestMap.put("pass",pass);
                                }
                                if (paramsJSON.has("amt")){
                                    amt = paramsJSON.getString("amt");
                                    requestMap.put("amt",amt);
                                }
                                if (paramsJSON.has("txncurr")){
                                    txncurr = paramsJSON.getString("txncurr");
                                    requestMap.put("txncurr",txncurr);
                                }
                                if (paramsJSON.has("prodid")){
                                    prodid  = paramsJSON.getString("prodid");
                                    requestMap.put("prodid",prodid);
                                }
                                if (paramsJSON.has("login")){
                                    login = paramsJSON.getString("login");
                                    requestMap.put("login",login);
                                }
                                if (paramsJSON.has("bankid")){
                                    bankid  = paramsJSON.getString("bankid");
                                    requestMap.put("bankCode",bankid);
                                }
                                if (paramsJSON.has("mid")){
                                    mid = paramsJSON.getString("mid");
                                    requestMap.put("mid",mid);
                                }
                                if (paramsJSON.has("custacc")){
                                    custacc = paramsJSON.getString("custacc");
                                    requestMap.put("custacc",custacc);
                                }
                                if (paramsJSON.has("udf9")){
                                    udf9    = paramsJSON.getString("udf9");
                                    requestMap.put("udf9",udf9);
                                }
                                if (paramsJSON.has("ttype")){
                                    ttype   = paramsJSON.getString("ttype");
                                    requestMap.put("ttype",ttype);
                                }
                                if (paramsJSON.has("txnid")){
                                    txnid   = paramsJSON.getString("txnid");
                                    requestMap.put("txnid",txnid);
                                }
                                if (paramsJSON.has("clientcode")){
                                    clientcode  = paramsJSON.getString("clientcode");
                                    requestMap.put("clientcode",clientcode);
                                }
                                if (paramsJSON.has("ru")){
                                    ru  = paramsJSON.getString("ru");
                                    requestMap.put("ru",ru);
                                }
                                if (paramsJSON.has("txnscamt")){
                                    txnscamt    = paramsJSON.getString("txnscamt");
                                    requestMap.put("txnscamt",txnscamt);
                                }
                                if (paramsJSON.has("date")){
                                    date    = paramsJSON.getString("date");
                                    requestMap.put("date",date);
                                }
                            }

                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(payment_url);
                            commResponseVO.setRequestMap(requestMap);
                        }


                    }
                }else{
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setRemark("Charge Request Failed");
                    return commResponseVO ;
                }

            }

        }catch(Exception e){
            transactionlogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredirect in Lyra ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails       = null;
        LyraPaymentUtils lyraUtils              = new LyraPaymentUtils();
        CommRequestVO commRequestVO             = lyraUtils.getLyraPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount           = GatewayAccountService.getGatewayAccount(accountId);
        LyraPaymentProcess lyraPaymentProcess   = new LyraPaymentProcess();
        boolean isTest = gatewayAccount.isTest();
        try
        {


            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = lyraPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect Lyra form -- >>"+commonValidatorVO.getTrackingid()+" "+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());


        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in LyraPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        CommResponseVO commResponseVO   = new CommResponseVO();
        Functions functions             = new Functions();
        String status           = "";
        String inquiry_url      = "";
        String request          = "";
        boolean isTest          = gatewayAccount.isTest();
        String transactionid    = "";

        String shopid   = gatewayAccount.getMerchantId();
        String api_Key  = gatewayAccount.getFRAUD_FTP_PASSWORD();

        try
        {
            if (isTest){
                inquiry_url = RB.getString("GET_TRANSACTION_TEST");
            }
            else {
                inquiry_url = RB.getString("GET_TRANSACTION_LIVE");
            }

            transactionid    = transactionDetailsVO.getPreviousTransactionId();
            transactionlogger.error("processQuery Request ---------> " + trackingId +" "+ inquiry_url+transactionid);

            String inquiryResponse  = LyraPaymentUtils.doGetHttpUrlConnection(inquiry_url+transactionid,shopid,api_Key);
            transactionlogger.error("processQuery response ---------> " + trackingId +" "+ inquiryResponse);

            String resRemark        = "";
            String resNotFoundRemark= "";
            String currency         = "";
            String captureamount    = "";
            String cardtype         = "";
            String responseCode     = "";
            String txstatus         = "";
            String txresRemark      = "";
            if (inquiryResponse != null)
            {

                JSONObject jsonObject = new JSONObject(inquiryResponse);
                JSONArray txJSONArray = new JSONArray();
                JSONObject txJSONObj;
                if (jsonObject.has("status"))
                {
                    status  = jsonObject.getString("status");
                }
                if (jsonObject.has("currency"))
                {
                    currency    = jsonObject.getString("currency");
                }
                if (jsonObject.has("paid"))
                    captureamount   = jsonObject.getString("paid");


                if (jsonObject.has("dropReason"))
                    resRemark   = jsonObject.getString("dropReason");


                if (jsonObject.has("tx"))
                {
                    txJSONArray = jsonObject.getJSONArray("tx");
                    txJSONObj   = txJSONArray.getJSONObject(0);
                    if (txJSONObj.has("status"))
                        txstatus    = jsonObject.getString("status");

                    if (txJSONObj.has("error"))
                        txresRemark = jsonObject.getString("error");

                }

                if ("PAID".equalsIgnoreCase(status)&& "ACCEPTED".equalsIgnoreCase(txstatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    if (functions.isValueNull(txresRemark)) resRemark=txresRemark;
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setAmount(captureamount);
                    commResponseVO.setTransactionId(transactionid);

                }
                else if("DECLINED".equalsIgnoreCase(txstatus) || "DROPPED".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if (functions.isValueNull(txresRemark)) resRemark=txresRemark;
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setResponseHashInfo(transactionid);
                    commResponseVO.setTransactionId(transactionid);

                }
                else if(status.equalsIgnoreCase("DUE")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
                    commResponseVO.setTransactionId(transactionid);
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(resRemark);
                    commResponseVO.setDescription(resRemark);
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
            transactionlogger.error("Exception-----", e);
        }

        return commResponseVO;
    }


}

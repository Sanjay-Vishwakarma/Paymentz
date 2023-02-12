package com.payment.Easebuzz;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONArray;
import org.json.JSONObject;


import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 18-Feb-22.
 */
public class EaseBuzzPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(EaseBuzzPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "easebuzz";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.easebuzz");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public EaseBuzzPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("******* Inside EaseBuzzPaymentGateway processSale() **********");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        boolean isTest      = gatewayAccount.isTest();
        String INIT_URL     ="";
        String REQUEST_URL  = "";
        EasebuzzUtils easebuzzUtils = new EasebuzzUtils();
        String salt             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String key              = gatewayAccount.getMerchantId();
        String RETURN_URL       = "";
        String txnid            = trackingID;
        String amount           = transactionDetailsVO.getAmount();
        CommMerchantVO commMerchantVO  = commRequestVO.getCommMerchantVO();

        String productinfo  = "";
        String firstname    = "";
        String phone        = "";
        String email        = "";
        String surl         = "";
        String furl         = "";
        String address1     = "";
        String city         = "";
        String state        = "";
        String country      = "";
        String zipcode      = "";
        String payment_mode =  easebuzzUtils.getPaymentType(payment_Card);
        String bank_code    = "";
        String card_number  = "";
        String card_holder_name ="";
        String card_cvv         ="";
        String card_expiry_date ="";
        String upi_va           = commRequestVO.getCustomerId();
        String request_flow     ="SEAMLESS";
        /*String upi_qr="";
        String pay_later_app="Simpl";
        String request_mode="SUVA";*/

        Map<String, String> requestMap      = new HashMap<String, String>();
        Map<String, String> saleRequestMap  = new HashMap<String, String>();

        try
        {
            if(isTest){
                INIT_URL = RB.getString("TEST_INIT_URL");
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                INIT_URL = RB.getString("LIVE_INIT_URL");
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                RETURN_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            }else{
                RETURN_URL = RB.getString("RETURN_URL") + trackingID;
            }

            surl            = RETURN_URL+"&success=success";
            furl            = RETURN_URL+"&failed=failed";

            if (functions.isValueNull(transactionDetailsVO.getCustomerBankId())){
                bank_code   = transactionDetailsVO.getCustomerBankId();
            }
            if (functions.isValueNull(commCardDetailsVO.getCardNum())){
                card_number = commCardDetailsVO.getCardNum();
            }
            if (functions.isValueNull(commCardDetailsVO.getcVV())){
                card_cvv    = commCardDetailsVO.getcVV();
            }

            if (functions.isValueNull(commCardDetailsVO.getExpMonth()) && functions.isValueNull(commCardDetailsVO.getExpYear())){
                card_expiry_date = commCardDetailsVO.getExpMonth() +""+ commCardDetailsVO.getExpYear();
            }

            productinfo     = trackingID;
            if(functions.isValueNull(addressDetailsVO.getFirstname())){
                firstname       = addressDetailsVO.getFirstname();
            }

            if(functions.isValueNull(addressDetailsVO.getStreet())){
                address1        = addressDetailsVO.getStreet();
            }
            if(functions.isValueNull(addressDetailsVO.getCity())){
                city            = addressDetailsVO.getCity();
            }
            if(functions.isValueNull(addressDetailsVO.getState())){
                state            = addressDetailsVO.getState();
            }
            if(functions.isValueNull(addressDetailsVO.getCountry())){
                country         = addressDetailsVO.getCountry();
            }
            if(functions.isValueNull(addressDetailsVO.getZipCode())){
                zipcode         = addressDetailsVO.getZipCode();
            }

            String hash="";

            if(functions.isValueNull(addressDetailsVO.getFirstname()) && functions.isValueNull(addressDetailsVO.getLastname()))
                card_holder_name   = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
            else {
                card_holder_name  = "customer";
            }
            if(functions.isValueNull(addressDetailsVO.getEmail()))
                email   = addressDetailsVO.getEmail().trim();
            else {
                email  = "customer@gmail.com";
            }

            if(functions.isValueNull(addressDetailsVO.getPhone())) {

                if(addressDetailsVO.getPhone().contains("-"))
                {
                    phone = addressDetailsVO.getPhone().split("\\-")[1];
                }
                if(phone.length()>10)
                {

                    phone   = phone.substring(phone.length() - 10);
                }
                else
                {
                    phone = addressDetailsVO.getPhone();
                }

            }
            else
            {
                phone  = "9999999999";
            }

            String strforHash   = key+"|"+txnid+"|"+amount+"|"+productinfo+"|"+firstname+"|"+email+"|||||||||||"+salt;

            hash    = easebuzzUtils.GenerateSHA512(strforHash);

            requestMap.put("key",key);
            requestMap.put("txnid" ,txnid);
            requestMap.put("amount", amount);
            requestMap.put("productinfo",productinfo);
            requestMap.put("firstname",firstname);
            requestMap.put("phone",phone);
            requestMap.put("email",email);
            requestMap.put("surl",surl);
            requestMap.put("furl",furl);
            requestMap.put("hash",hash);
            requestMap.put("address1",address1);
            requestMap.put("city",city);
            requestMap.put("state",state);
            requestMap.put("country",country);
            requestMap.put("zipcode",zipcode);
            requestMap.put("request_flow",request_flow);

            transactionlogger.error("Initiate Payment request===>"+requestMap.toString());
            String result       = "";
            StringBuilder sb    = new StringBuilder();
            for (Map.Entry<String, String> e : requestMap.entrySet()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(e.getKey().trim(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue().trim(), "UTF-8"));
            }

            transactionlogger.error("Initiate Payment request===>"+sb.toString());
            result = easebuzzUtils.doHttpPostConnection(INIT_URL,sb.toString());

            transactionlogger.error("Initiate Payment response===>"+result.toString());

            String status       = "";
            String access_key   = "";
            if (functions.isValueNull(result) && functions.isJSONValid(result))
            {
                JSONObject resultReader = new JSONObject(result);
                if (resultReader != null)
                {
                    if (resultReader.has("status"))
                    {
                        status = resultReader.getString("status");
                    }
                    if (resultReader.has("data"))
                    {
                        access_key = resultReader.getString("data");
                    }

                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Failed");
                commResponseVO.setRemark("Initiated Payment Request failed");
                return commResponseVO;
            }
            if(functions.isValueNull(access_key)){

                saleRequestMap.put("access_key", access_key);
                saleRequestMap.put("payment_mode", payment_mode);

                if ("NB".equalsIgnoreCase(payment_mode))
                {
                    saleRequestMap.put("bank_code", bank_code);
                }
                else if ("CC".equalsIgnoreCase(payment_mode) || "DC".equalsIgnoreCase(payment_mode))
                {
                    saleRequestMap.put("card_number", card_number);
                    saleRequestMap.put("card_holder_name", card_holder_name);
                    saleRequestMap.put("card_cvv", card_cvv);
                    saleRequestMap.put("card_expiry_date", card_expiry_date);
                }
                else if ("UPI".equalsIgnoreCase(payment_mode))
                {
                    saleRequestMap.put("upi_va", upi_va);
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Incorrect Request");
                    commResponseVO.setRemark("Incorrect Request");
                    return commResponseVO;
                }

                transactionlogger.error("resquestString "+trackingID+" "+saleRequestMap.toString());
                commResponseVO.setStatus("pending3DConfirmation");
                //commResponseVO.setUrlFor3DRedirect(REQUEST_URL+access_key);
                commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
                commResponseVO.setRequestMap(saleRequestMap);
            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e){
            transactionlogger.error("EaseBuzzPaymentGateway processSale exception ---"+e);
        }
        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" **** processAutoRedirect in EaseBuzzPaymentGateway **** ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        EasebuzzUtils easebuzzUtils                         = new EasebuzzUtils();
        EasebuzzPaymentProcess easebuzzPaymentProcess=new EasebuzzPaymentProcess();
        CommRequestVO commRequestVO     = easebuzzUtils.getEasebuzzPaymentRequestVO(commonValidatorVO);
        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            transactionlogger.error("transRespDetails.getStatus()==>"+transRespDetails.getStatus());
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = easebuzzPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("redirect EaseBuzzPaymentGateway form ==>"+html);
            }

        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in EaseBuzzPaymentGateway---", e);
        }
        return html;
    }

    @Override
    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {

        transactionlogger.error(" Inside EaseBuzzPaymentGateway processQuery ");
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO         = commRequestVO.getAddressDetailsVO();
        Functions functions         = new Functions();
        EasebuzzUtils easebuzzUtils = new EasebuzzUtils();
        boolean isTest              = gatewayAccount.isTest();

        String REQUEST_URL  = "";
        Map<String, String> requestMap = new HashMap<String, String>();
        String salt     = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String key      = gatewayAccount.getMerchantId();
        String txnid    = trackingId;
        String amount   = transactionDetailsVO.getAmount();
        String phone    = addressDetailsVO.getPhone();
        String email    = addressDetailsVO.getEmail();
        String hash     = "";

        try
        {
            if(isTest){
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_INQUIRY_URL");
            }

            String strforHash = key+"|"+txnid+"|"+amount+"|"+email+"|"+phone+"|"+salt;

            hash = easebuzzUtils.GenerateSHA512(strforHash);

            requestMap.put("key", key);
            requestMap.put("txnid", txnid);
            requestMap.put("amount", amount);
            requestMap.put("phone", phone);
            requestMap.put("email", email);
            requestMap.put("hash", hash);

            StringBuilder request = new StringBuilder();
            for (Map.Entry<String, String> e : requestMap.entrySet()) {
                if (request.length() > 0) {
                    request.append('&');
                }
                request.append(URLEncoder.encode(e.getKey().trim(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue().trim(), "UTF-8"));
            }

            transactionlogger.error("Inquiry Request --> "+trackingId+" "+ request.toString());
            String response     = easebuzzUtils.doHttpPostConnection(REQUEST_URL, request.toString());
            transactionlogger.error("Inquiry Response --> "+trackingId+" "+ response.toString());

            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {

                String status       = "";
                String easepayid    = "";
                String error_Message= "";
                String txn_amount   = "";
                String bankcode     = "";
                String bank_ref_num = "";

                JSONObject responseJOSON  = new JSONObject(response);
                JSONObject msgJSON        = null;

                if(responseJOSON.has("msg")){
                    msgJSON = responseJOSON.getJSONObject("msg");
                }

                if (msgJSON.has("status"))
                {
                    status = msgJSON.getString("status");
                }
                if (msgJSON.has("easepayid"))
                {
                    easepayid = msgJSON.getString("easepayid");
                }
                if (msgJSON.has("error_Message"))
                {
                    error_Message = msgJSON.getString("error_Message");
                }
                if (msgJSON.has("amount"))
                {
                    txn_amount = msgJSON.getString("amount");
                }
                if (msgJSON.has("bankcode"))
                {
                    bankcode = msgJSON.getString("bankcode");
                }
                if (msgJSON.has("bank_ref_num"))
                {
                    bank_ref_num = msgJSON.getString("bank_ref_num");
                }


                if ("success".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescription(error_Message);
                    commResponseVO.setRemark(error_Message);
                    commResponseVO.setAmount(txn_amount);
                    commResponseVO.setTransactionId(easepayid);
                    commResponseVO.setAuthCode(bankcode);
                    commResponseVO.setErrorCode(bankcode);
                }
                else if("failure".equalsIgnoreCase(status) || "userCancelled".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setDescription(error_Message);
                    commResponseVO.setRemark(error_Message);
                    commResponseVO.setAmount(txn_amount);
                    commResponseVO.setTransactionId(easepayid);
                    commResponseVO.setAuthCode(bankcode);
                    commResponseVO.setErrorCode(bankcode);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setTransactionStatus("pending");
                }
            }else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("EaseBuzzPaymentGateway processQuery exception ---"+e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionlogger.error("*******Inside EaseBuzzPaymentGateway processRefund()**********");

        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO   = new CommResponseVO();
        GatewayAccount gatewayAccount   = new GatewayAccount();
        Functions functions             = new Functions();

        EasebuzzUtils easebuzzUtils                     = new EasebuzzUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO           = commRequestVO.getAddressDetailsVO();
        boolean isTest                                  = gatewayAccount.isTest();

        String REQUEST_URL="";
        Map<String, String> requestMap  = new HashMap<String, String>();
        StringBuffer request            = new StringBuffer();
        String hash = "";

        String salt         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String key          = gatewayAccount.getFRAUD_FTP_USERNAME();
        String txnid        = transactionDetailsVO.getPreviousTransactionId();
        String amount       = transactionDetailsVO.getPreviousTransactionAmount();
        String refund_amount    = transactionDetailsVO.getAmount();
        String phone            = addressDetailsVO.getPhone();
        String email            = addressDetailsVO.getEmail();


        transactionlogger.error("phone==="+addressDetailsVO.getPhone());
        transactionlogger.error("email==="+addressDetailsVO.getEmail());

        try
        {

            if(isTest){
                REQUEST_URL = RB.getString("TEST_REFUND_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_REFUND_URL");
            }

            String strforHash   = key+"|"+txnid+"|"+amount+"|"+email+"|"+phone+"|"+salt;

            hash = easebuzzUtils.GenerateSHA512(strforHash);

            requestMap.put("key", key);
            requestMap.put("txnid", txnid);
            requestMap.put("refund_amount", refund_amount);
            requestMap.put("amount", amount);
            requestMap.put("phone", phone);
            requestMap.put("email", email);
            requestMap.put("hash", hash);

            for (Map.Entry<String, String> e : requestMap.entrySet())
            {
                if (request.length() > 0)
                {
                    request.append('&');
                }
                request.append(URLEncoder.encode(e.getKey().trim(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue().trim(), "UTF-8"));
            }
            transactionlogger.error("Refund Request===" + trackingID + "==>" + request);

            String response = easebuzzUtils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionlogger.error("Refund Response===" + trackingID + "==>" + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {
                JSONObject jsonObject       = new JSONObject(response);
                String status         = "";
                String refund_amount1 = "";
                String easepayid      = "";
                String amount1        = "";
                String reason         = "";
                String refund_id      = "";

                if (jsonObject.has("status") && functions.isValueNull(jsonObject.getString("status")))
                {
                    status = jsonObject.getString("status");
                }
                if (jsonObject.has("refund_amount") && functions.isValueNull(jsonObject.getString("refund_amount")))
                {
                    refund_amount1 = jsonObject.getString("refund_amount");
                }
                if (jsonObject.has("reason") && functions.isValueNull(jsonObject.getString("reason")))
                {
                    reason = jsonObject.getString("reason");
                }
                if (jsonObject.has("easepayid") && functions.isValueNull(jsonObject.getString("easepayid")))
                {
                    easepayid = jsonObject.getString("easepayid");
                }
                if (jsonObject.has("refund_id") && functions.isValueNull(jsonObject.getString("refund_id")))
                {
                    refund_id = jsonObject.getString("refund_id");
                }

                if (status.equalsIgnoreCase("success"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setDescription(reason);
                    commResponseVO.setTransactionStatus("Successful");
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("Failed");
                }
                commResponseVO.setRemark(reason);
                commResponseVO.setTransactionId(easepayid);
                commResponseVO.setAmount(refund_amount1);
                commResponseVO.setTransactionType(status);
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("EaseBuzzPaymentGateway processRefund exception ---"+e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error(" Inside EaseBuzzPaymentGateway processPayout ");
        CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO   = new CommResponseVO();
        GatewayAccount gatewayAccount   = new GatewayAccount();
        EasebuzzUtils easebuzzUtils     = new EasebuzzUtils();

        Map<String, String> requestMap  = new HashMap<String, String>();
        StringBuffer request            = new StringBuffer();

        boolean isTest      = gatewayAccount.isTest();
        String REQUEST_URL  ="";
        String response     = "";
        String hash         = "";


        String merchant_email="";
        String merchant_key="";
        String payout_date="";
        String salt="";

        try
        {

            if(isTest){
                REQUEST_URL = RB.getString("TEST_PAYOUT_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_PAYOUT_URL");
            }

            String strforHash = merchant_key + "|" + merchant_email + "|" + payout_date + "|"+"|"+salt;

            hash = easebuzzUtils.GenerateSHA512(strforHash);

            requestMap.put("merchant_key", merchant_key);
            requestMap.put("merchant_email", merchant_email);
            requestMap.put("payout_date", payout_date);
            requestMap.put("hash", hash);

            for (Map.Entry<String, String> e : requestMap.entrySet())
            {
                if (request.length() > 0)
                {
                    request.append('&');
                }
                request.append(URLEncoder.encode(e.getKey().trim(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue().trim(), "UTF-8"));
            }

            transactionlogger.error("Payout Request===" + trackingID + "=>" + request);

            response = easebuzzUtils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionlogger.error("Payout Response===" + trackingID + "=>" + response);

        }
        catch (Exception e)
        {
            transactionlogger.error("EaseBuzzPaymentGateway processPayout exception ---"+e);
        }


        return commResponseVO;
    }


    }
package com.payment.payeezy;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by Admin on 8/23/2019.
 */
public class PayeezyPaymentGateway extends AbstractPaymentGateway
{

    private static Logger logger=new Logger(PayeezyPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(PayeezyPaymentGateway.class.getName());
    private static ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.payeezy");
    public static final String GATEWAY_TYPE="payeezy";
    Functions functions=new Functions();

    private static final String NONCE = "nonce";
    public static final String APIKEY = "apikey";
    public static final String APISECRET = "pzsecret";
    public static final String TOKEN = "token";
    public static final String TIMESTAMP = "timestamp";
    public static final String AUTHORIZE = "Authorization";
    public static final String PAYLOAD = "payload";
    public static final String MERCHANTID = "merchantid";
    String url1="https://api-cert.payeezy.com/v1/transactions";
    private final static String PURCHASE="purchase";
    private final static String AUTH="authorize";
    private final static String CAPTURE="capture";
    private final static String REFUND="refund";
    private final static String VOID="void";
    private final static String RECURRING="recurring";
    private final static String METHOD="credit_card";


    public PayeezyPaymentGateway(String accountId)
    {
        this.accountId = accountId;

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (isTest)
        {
            url1 = rb.getString("TEST_URL");
        }
        else
        {
            url1 = rb.getString("LIVE_URL");
        }
    }

    private Map<String, String> getSecurityKeys(String trackingID,String apiKey, String token, String apiSecretKey, String payLoad) throws Exception {

        Map<String, String> returnMap = new HashMap<String, String>();
        long nonce;
        try {

            returnMap.put(NONCE, trackingID);
            returnMap.put(APIKEY, apiKey);
            returnMap.put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
            returnMap.put(TOKEN, token);
            returnMap.put(APISECRET, apiSecretKey);

            returnMap.put(PAYLOAD, payLoad);
            returnMap.put(AUTHORIZE, getMacValue(returnMap));
            return returnMap;

        } catch (NoSuchAlgorithmException e) {
            transactionLogger.error(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getMacValue(Map<String, String> data) throws Exception
    {

        Mac mac = Mac.getInstance("HmacSHA256");
      // transactionLogger.error("Data Map---"+data);
        String apiSecret = data.get(APISECRET);
        transactionLogger.error("API_SECRET:{}"+ apiSecret);
        SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
        mac.init(secret_key);
        StringBuilder buff = new StringBuilder();
        buff.append(data.get(APIKEY)).append(data.get(NONCE)).append(data.get(TIMESTAMP));
        if (data.get(TOKEN) != null)
            buff.append(data.get(TOKEN));
        if (data.get(PAYLOAD) != null)
            buff.append(data.get(PAYLOAD));

        byte[] macHash = mac.doFinal(buff.toString().getBytes("UTF-8"));
        String authorizeString = Base64.encodeBase64String(toHex(macHash));
        return authorizeString;

    }

    public byte[] toHex(byte[] arr) {
        String hex = Hex.encodeHexString(arr);
        return hex.getBytes();
    }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Sale...");
        transactionLogger.error("Inside PayeezyPaymentGateway Sale...");

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommAddressDetailsVO addressdetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactiondetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        CommCardDetailsVO carddetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        GatewayAccount gateway= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();
        String is3DSupported=gateway.get_3DSupportAccount();
        String apikey=gateway.getFRAUD_FTP_USERNAME(); //APIKey
        String token=gateway.getFRAUD_FTP_PASSWORD(); //token
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();
        String url="";

        String paymentBrand=GatewayAccountService.getPaymentBrand(transactiondetailsVO.getCardType());
        String cardtype="";
        if("VISA".equals(paymentBrand))
        {
            cardtype="Visa";
        }
        else if ("MC".equals(paymentBrand)){
            cardtype="Mastercard";
        }
        else if ("DINER".equals(paymentBrand)){
            cardtype="Diners Club";
        }
        else if ("AMEX".equals(paymentBrand)){
            cardtype="American Express";
        }
        else if ("DISC".equals(paymentBrand)){
            cardtype="Discover";
        }
        else if ("JCB".equals(paymentBrand)){
            cardtype="JCB";
        }

        String year=carddetailsVO.getExpYear();
        String expyear= year.substring(2,4);
        //transactionLogger.error("expyear-----"+expyear);

        String amt = transactiondetailsVO.getAmount();

        String terminalID=transactiondetailsVO.getTerminalId();
        transactionLogger.error("TerminalID----"+terminalID);

        String conversion_currency=transactiondetailsVO.getCurrency();

        if (functions.isValueNull(terminalID))
        {
            HashMap<String, String> hashMap = null;
            hashMap = PayeezyUtils.getTerminalConversionDetails(terminalID);
            String currency_conversion = hashMap.get("currency_conversion");

            transactionLogger.error("Currency Conversion Flag---"+currency_conversion);
            transactionLogger.error("conversion_currency---"+conversion_currency);

            if ("Y".equalsIgnoreCase(currency_conversion))
            {
                conversion_currency = hashMap.get("conversion_currency");
                transactionLogger.error("Amount----"+amt);
                amt = PayeezyUtils.getConversionAmountFromShare(transactiondetailsVO.getCurrency(),conversion_currency,amt);
                transactionLogger.error("Conversion Amount----"+amt);
            }
        }

        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Final Amount----"+amt);

        transactionLogger.error("Conversion currency---"+conversion_currency);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL"));
            url=rb.getString("TEST_URL");
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL"));
            url=rb.getString("LIVE_URL");
        }

        try
        {
            String request="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_type\": \""+PURCHASE+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"partial_redemption\": \"false\",\n" +
                    "  \"currency_code\": \""+conversion_currency+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \""+cardtype+"\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressdetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressdetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+carddetailsVO.getCardNum()+"\",\n" +
                    "    \"exp_date\": \""+carddetailsVO.getExpMonth()+expyear+"\",\n" +
                    "    \"cvv\": \""+carddetailsVO.getcVV()+"\"\n" +
                    "  }\n" +
                    "}";
 String requestlog="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_type\": \""+PURCHASE+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"partial_redemption\": \"false\",\n" +
                    "  \"currency_code\": \""+conversion_currency+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \""+cardtype+"\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressdetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressdetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+functions.maskingPan(carddetailsVO.getCardNum())+"\",\n" +
                    "    \"exp_date\": \""+functions.maskingNumber(carddetailsVO.getExpMonth())+functions.maskingNumber(expyear)+"\",\n" +
                    "    \"cvv\": \""+functions.maskingNumber(carddetailsVO.getcVV())+"\"\n" +
                    "  }\n" +
                    "}";

            transactionLogger.error("apikey-----"+apikey);
            transactionLogger.error("token-----"+token);
            transactionLogger.error("apiSecretKey----"+apiSecretKey);
            transactionLogger.error("Request of payeezy sale----"+requestlog);

            Map headerMap = getSecurityKeys(trackingID,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response of payeezy sale----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id = jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("token"))
                    {
                        String token_type="";
                        String token_data="";
                        JSONObject jsonObject=jsonobject.getJSONObject("token");
                        if (jsonObject.has("token_type"))
                        {
                            token_type=jsonObject.getString("token_type");
                        }
                        if (jsonObject.has("token_data"))
                        {
                            token_data=jsonObject.getString("token_data");
                            if (functions.isValueNull(token_data) && token_data.contains("{"))
                            {
                                JSONObject jsonObject1=new JSONObject(token_data);
                                String value="";
                                if (jsonObject1.has("value"))
                                {
                                    value=jsonObject1.getString("value");
                                }
                            }
                        }
                    }
                    if (jsonobject.has("card"))
                    {
                        String type="";
                        String cardholder_name="";
                        String card_number="";
                        String exp_date="";
                        JSONObject jsonObject=jsonobject.getJSONObject("card");
                        if (jsonObject.has("type"))
                        {
                            type=jsonObject.getString("type");
                        }
                        if (jsonObject.has("cardholder_name"));
                        {
                            cardholder_name=jsonObject.getString("cardholder_name");
                        }
                        if (jsonObject.has("card_number"))
                        {
                            card_number=jsonObject.getString("card_number");
                        }
                        if (jsonObject.has("exp_date"))
                        {
                            exp_date=jsonObject.getString("exp_date");
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in payeezy sale-----",e);
        }
        return comresponse;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Auth...");
        transactionLogger.error("Inside PayeezyPaymentGateway Auth...");

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommAddressDetailsVO addressdetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactiondetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        CommCardDetailsVO carddetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        GatewayAccount gateway= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();
        String apikey=gateway.getFRAUD_FTP_USERNAME();
        String token=gateway.getFRAUD_FTP_PASSWORD();
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();
        String url="";

        String paymentBrand=GatewayAccountService.getPaymentBrand(transactiondetailsVO.getCardType());
        String cardtype="";
        if("VISA".equals(paymentBrand))
        {
            cardtype="Visa";
        }
        else if ("MC".equals(paymentBrand)){
            cardtype="Mastercard";
        }
        else if ("DINER".equals(paymentBrand)){
            cardtype="Diners Club";
        }
        else if ("AMEX".equals(paymentBrand)){
            cardtype="American Express";
        }
        else if ("DISC".equals(paymentBrand)){
            cardtype="Discover";
        }
        else if ("JCB".equals(paymentBrand)){
            cardtype="JCB";
        }

        String year=carddetailsVO.getExpYear();
        String expyear= year.substring(2,4);
        transactionLogger.error("expyear-----"+expyear);

        String amt = transactiondetailsVO.getAmount();

        String terminalID=transactiondetailsVO.getTerminalId();
        transactionLogger.error("TerminalID----"+terminalID);

        String conversion_currency=transactiondetailsVO.getCurrency();

        if (functions.isValueNull(terminalID))
        {
            HashMap<String, String> hashMap = null;
            hashMap = PayeezyUtils.getTerminalConversionDetails(terminalID);
            String currency_conversion = hashMap.get("currency_conversion");


            transactionLogger.error("Currency Conversion Flag---"+currency_conversion);
            transactionLogger.error("conversion_currency---"+conversion_currency);

            if ("Y".equalsIgnoreCase(currency_conversion))
            {
                conversion_currency = hashMap.get("conversion_currency");
                transactionLogger.error("Amount----"+amt);
                amt = PayeezyUtils.getConversionAmountFromShare(transactiondetailsVO.getCurrency(),conversion_currency,amt);
                transactionLogger.error("Conversion Amount----"+amt);
            }
        }

        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Final Amount----"+amt);

        transactionLogger.error("Conversion currency---"+conversion_currency);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL"));
            url=rb.getString("TEST_URL");
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL"));
            url=rb.getString("LIVE_URL");
        }

        try{

            String request="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_type\": \""+AUTH+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+conversion_currency+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \""+cardtype+"\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressdetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressdetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+carddetailsVO.getCardNum()+"\",\n" +
                    "    \"exp_date\": \""+carddetailsVO.getExpMonth()+expyear+"\",\n" +
                    "    \"cvv\": \""+carddetailsVO.getcVV()+"\"\n" +
                    "  }\n" +
                    "}";
  String requestlog="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_type\": \""+AUTH+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+conversion_currency+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \""+cardtype+"\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressdetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressdetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+functions.maskingPan(carddetailsVO.getCardNum())+"\",\n" +
                    "    \"exp_date\": \""+functions.maskingNumber(carddetailsVO.getExpMonth())+functions.maskingNumber(expyear)+"\",\n" +
                    "    \"cvv\": \""+functions.maskingNumber(carddetailsVO.getcVV())+"\"\n" +
                    "  }\n" +
                    "}";

            transactionLogger.error("Request of payeezy auth----"+requestlog);

            Map headerMap = getSecurityKeys(trackingID,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response of payeezy auth-----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id=jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("token"))
                    {
                        String token_type="";
                        String token_data="";
                        JSONObject jsonObject=jsonobject.getJSONObject("token");
                        if (jsonObject.has("token_type"))
                        {
                            token_type=jsonObject.getString("token_type");
                        }
                        if (jsonObject.has("token_data"))
                        {
                            token_data=jsonObject.getString("token_data");
                            if (functions.isValueNull(token_data) && token_data.contains("{"))
                            {
                                JSONObject jsonObject1=new JSONObject(token_data);
                                String value="";
                                if (jsonObject1.has("value"))
                                {
                                    value=jsonObject1.getString("value");
                                }
                            }
                        }
                    }
                    if (jsonobject.has("card"))
                    {
                        String type="";
                        String cardholder_name="";
                        String card_number="";
                        String exp_date="";
                        JSONObject jsonObject=jsonobject.getJSONObject("card");
                        if (jsonObject.has("type"))
                        {
                            type=jsonObject.getString("type");
                        }
                        if (jsonObject.has("cardholder_name"));
                        {
                            cardholder_name=jsonObject.getString("cardholder_name");
                        }
                        if (jsonObject.has("card_number"))
                        {
                            card_number=jsonObject.getString("card_number");
                        }
                        if (jsonObject.has("exp_date"))
                        {
                            exp_date=jsonObject.getString("exp_date");
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in payeezy auth----",e);
        }
        return comresponse;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Capture...");
        transactionLogger.error("Inside PayeezyPaymentGateway Capture...");

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommTransactionDetailsVO transactiondetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gateway= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();
        String apikey=gateway.getFRAUD_FTP_USERNAME();
        String token=gateway.getFRAUD_FTP_PASSWORD();
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();
        String url="";

        String amt = transactiondetailsVO.getAmount();
        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Amount----"+amt);

        String previousTransactionid=transactiondetailsVO.getPreviousTransactionId();
        transactionLogger.error("previousTransactionid---"+previousTransactionid);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL")+"/"+previousTransactionid);
            url=rb.getString("TEST_URL")+"/"+previousTransactionid;
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL")+"/"+previousTransactionid);
            url=rb.getString("LIVE_URL")+"/"+previousTransactionid;
        }

        String previous_trans_tag1=transactiondetailsVO.getResponseHashInfo();
        transactionLogger.error("previous_trans_tag1----"+previous_trans_tag1);

        try
        {
            String request="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_tag\": \""+previous_trans_tag1+"\",\n" +
                    "  \"transaction_type\": \""+CAPTURE+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+transactiondetailsVO.getCurrency()+"\"\n" +
                    "}";

            transactionLogger.error("Request of payeezy capture----"+request);

            Map headerMap = getSecurityKeys(trackingID,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response of payeezy capture----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id=jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("token"))
                    {
                        String token_type="";
                        String token_data="";
                        JSONObject jsonObject=jsonobject.getJSONObject("token");
                        if (jsonObject.has("token_type"))
                        {
                            token_type=jsonObject.getString("token_type");
                        }
                        if (jsonObject.has("token_data"))
                        {
                            token_data=jsonObject.getString("token_data");
                            if (functions.isValueNull(token_data) && token_data.contains("{"))
                            {
                                JSONObject jsonObject1=new JSONObject(token_data);
                                String value="";
                                if (jsonObject1.has("value"))
                                {
                                    value=jsonObject1.getString("value");
                                }
                            }
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_tag);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in payeezy capture----",e);
        }
        return comresponse;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Refund...");
        transactionLogger.error("Inside PayeezyPaymentGateway Refund...");

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommTransactionDetailsVO transactiondetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gateway= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();
        String apikey=gateway.getFRAUD_FTP_USERNAME();
        String token=gateway.getFRAUD_FTP_PASSWORD();
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();
        String url="";

        String amt = transactiondetailsVO.getAmount();
        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Amount----"+amt);

        String previousTransactionid=transactiondetailsVO.getPreviousTransactionId();
        transactionLogger.error("previousTransactionid---"+previousTransactionid);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL")+"/"+previousTransactionid);
            url=rb.getString("TEST_URL")+"/"+previousTransactionid;
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL")+"/"+previousTransactionid);
            url=rb.getString("LIVE_URL")+"/"+previousTransactionid;
        }

        String previous_trans_tag1=transactiondetailsVO.getResponseHashInfo();
        transactionLogger.error("previous_trans_tag1----"+previous_trans_tag1);

        try{

            String request="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_tag\": \""+previous_trans_tag1+"\",\n" +
                    "  \"transaction_type\": \""+REFUND+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+transactiondetailsVO.getCurrency()+"\"\n" +
                    "}";

            transactionLogger.error("Request of payeezy refund---"+request);

            Map headerMap = getSecurityKeys(trackingID,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response of payeezy refund----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id=jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("card"))
                    {
                        String type="";
                        String cardholder_name="";
                        String card_number="";
                        String exp_date="";
                        JSONObject jsonObject=jsonobject.getJSONObject("card");
                        if (jsonObject.has("type"))
                        {
                            type=jsonObject.getString("type");
                        }
                        if (jsonObject.has("cardholder_name"));
                        {
                            cardholder_name=jsonObject.getString("cardholder_name");
                        }
                        if (jsonObject.has("card_number"))
                        {
                            card_number=jsonObject.getString("card_number");
                        }
                        if (jsonObject.has("exp_date"))
                        {
                            exp_date=jsonObject.getString("exp_date");
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception in payeezy refund----",e);
        }
        return comresponse;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Void...");
        transactionLogger.error("Inside PayeezyPaymentGateway Void...");

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommTransactionDetailsVO transactiondetailsVO = ((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gateway= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();
        String apikey=gateway.getFRAUD_FTP_USERNAME();
        String token=gateway.getFRAUD_FTP_PASSWORD();
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();
        String url="";

        String amt = transactiondetailsVO.getAmount();
        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Amount----"+amt);

        String previousTransactionid=transactiondetailsVO.getPreviousTransactionId();
        transactionLogger.error("previousTransactionid---"+previousTransactionid);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL")+"/"+previousTransactionid);
            url=rb.getString("TEST_URL")+"/"+previousTransactionid;
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL")+"/"+previousTransactionid);
            url=rb.getString("LIVE_URL")+"/"+previousTransactionid;
        }

        String previous_trans_tag2=transactiondetailsVO.getResponseHashInfo();
        transactionLogger.error("previous_trans_tag2----"+previous_trans_tag2);

        try
        {
            String request="{\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"transaction_tag\": \""+previous_trans_tag2+"\",\n" +
                    "  \"transaction_type\": \""+VOID+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+transactiondetailsVO.getCurrency()+"\"\n" +
                    "}";

            transactionLogger.error("Request of payeezy void----"+request);

            Map headerMap = getSecurityKeys(trackingID,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response of payeezy void----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id=jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("token"))
                    {
                        String token_type="";
                        String token_data="";
                        JSONObject jsonObject=jsonobject.getJSONObject("token");
                        if (jsonObject.has("token_type"))
                        {
                            token_type=jsonObject.getString("token_type");
                        }
                        if (jsonObject.has("token_data"))
                        {
                            token_data=jsonObject.getString("token_data");
                            if (functions.isValueNull(token_data) && token_data.contains("{"))
                            {
                                JSONObject jsonObject1=new JSONObject(token_data);
                                String value="";
                                if (jsonObject1.has("value"))
                                {
                                    value=jsonObject1.getString("value");
                                }
                            }
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setResponseHashInfo(transaction_tag);
                        comresponse.setTransactionType(transaction_type);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setResponseHashInfo(transaction_tag);
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setDescriptor(gateway.getDisplayName());
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in payeezy void----",e);
        }
        return comresponse;
    }

    @Override
    public GenericResponseVO processRebilling(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        logger.error("Inside PayeezyPaymentGateway Recurring..."+GatewayAccountService.getGatewayAccount(getMerchantId()));
        transactionLogger.error("Inside PayeezyPaymentGateway Recurring..."+GatewayAccountService.getGatewayAccount(accountId));

        Comm3DResponseVO comresponse=new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        GatewayAccount gateway=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gateway.isTest();

        String url="";
        String apikey=gateway.getFRAUD_FTP_USERNAME();
        String token=gateway.getFRAUD_FTP_PASSWORD();
        String apiSecretKey=gateway.getFRAUD_FTP_PATH();
        String merchantName=gateway.getFRAUD_FILE_SHORT_NAME();

        String amt = transactionDetailsVO.getAmount();
        Double cents=new Double(Double.valueOf(amt)*100);
        int amount1=cents.intValue();
        amt=String.valueOf(amount1);
        transactionLogger.error("Amount----"+amt);

        String year=cardDetailsVO.getExpYear();
        String expyear=year.substring(2,4);
        transactionLogger.error("Expyear----"+expyear);

        if (isTest)
        {
            transactionLogger.error("TEST_URL----"+rb.getString("TEST_URL"));
            url=rb.getString("TEST_URL");
        }
        else
        {
            transactionLogger.error("LIVE_URL----"+rb.getString("LIVE_URL"));
            url=rb.getString("LIVE_URL");
        }

        try
        {
            String request="{\n" +
                    "  \"transaction_type\": \""+RECURRING+"\",\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+transactionDetailsVO.getCurrency()+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \"visa\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+cardDetailsVO.getCardNum()+"\",\n" +
                    "    \"exp_date\": \""+cardDetailsVO.getExpMonth()+expyear+"\",\n" +
                    "    \"cvv\": \""+cardDetailsVO.getcVV()+"\"\n" +
                    "  }\n" +
                    "}";

            String requestlog="{\n" +
                    "  \"transaction_type\": \""+RECURRING+"\",\n" +
                    "  \"merchant_ref\": \""+merchantName+"\",\n" +
                    "  \"method\": \""+METHOD+"\",\n" +
                    "  \"amount\": \""+amt+"\",\n" +
                    "  \"currency_code\": \""+transactionDetailsVO.getCurrency()+"\",\n" +
                    "  \"credit_card\": {\n" +
                    "    \"type\": \"visa\",\n" +
                    "    \"cardholder_name\": \""+ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname())+" "+ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname())+"\",\n" +
                    "    \"card_number\": \""+functions.maskingPan(cardDetailsVO.getCardNum())+"\",\n" +
                    "    \"exp_date\": \""+functions.maskingNumber(cardDetailsVO.getExpMonth())+functions.maskingNumber(expyear)+"\",\n" +
                    "    \"cvv\": \""+functions.maskingNumber(cardDetailsVO.getcVV())+"\"\n" +
                    "  }\n" +
                    "}";

            transactionLogger.error("Request for payeezy recurring----"+requestlog);

            Map headerMap = getSecurityKeys(trackingId,apikey,token,apiSecretKey,request);
            String response=PayeezyUtils.doPostHTTPSURLConnectionClient(url, request,headerMap);

            transactionLogger.error("Response for payeezy recurring---"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String correlation_id="";
                String transaction_status="";
                String validation_status="";
                String transaction_type="";
                String transaction_id="";
                String transaction_tag="";
                String method="";
                String amount="";
                String currency="";
                String cvv2="";
                String bank_resp_code="";
                String bank_message="";
                String gateway_resp_code="";
                String gateway_message="";
                String eCommerce_flag="";
                String retrieval_ref_no="";
                String code="";
                String description="";
                JSONObject jsonobject=new JSONObject(response);
                if (jsonobject!=null)
                {
                    if (jsonobject.has("correlation_id"))
                    {
                        correlation_id=jsonobject.getString("correlation_id");
                    }
                    if (jsonobject.has("transaction_status"))
                    {
                        transaction_status=jsonobject.getString("transaction_status");
                    }
                    if (jsonobject.has("validation_status"))
                    {
                        validation_status=jsonobject.getString("validation_status");
                    }
                    if (jsonobject.has("transaction_type"))
                    {
                        transaction_type=jsonobject.getString("transaction_type");
                    }
                    if (jsonobject.has("transaction_id"))
                    {
                        transaction_id=jsonobject.getString("transaction_id");
                    }
                    if (jsonobject.has("transaction_tag"))
                    {
                        transaction_tag=jsonobject.getString("transaction_tag");
                    }
                    if (jsonobject.has("method"))
                    {
                        method=jsonobject.getString("method");
                    }
                    if (jsonobject.has("amount"))
                    {
                        amount=jsonobject.getString("amount");
                    }
                    if (jsonobject.has("currency"))
                    {
                        currency=jsonobject.getString("currency");
                    }
                    if (jsonobject.has("cvv2"))
                    {
                        cvv2=jsonobject.getString("cvv2");
                    }
                    if (jsonobject.has("bank_resp_code"))
                    {
                        bank_resp_code=jsonobject.getString("bank_resp_code");
                    }
                    if (jsonobject.has("bank_message"))
                    {
                        bank_message=jsonobject.getString("bank_message");
                    }
                    if (jsonobject.has("gateway_resp_code"))
                    {
                        gateway_resp_code=jsonobject.getString("gateway_resp_code");
                    }
                    if (jsonobject.has("gateway_message"))
                    {
                        gateway_message=jsonobject.getString("gateway_message");
                    }
                    if (jsonobject.has("eCommerce_flag"))
                    {
                        eCommerce_flag=jsonobject.getString("eCommerce_flag");
                    }
                    if (jsonobject.has("retrieval_ref_no"))
                    {
                        retrieval_ref_no=jsonobject.getString("retrieval_ref_no");
                    }
                    if (jsonobject.has("token"))
                    {
                        String token_type="";
                        String token_data="";
                        JSONObject jsonObject=jsonobject.getJSONObject("token");
                        if (jsonObject.has("token_type"))
                        {
                            token_type=jsonObject.getString("token_type");
                        }
                        if (jsonObject.has("token_data"))
                        {
                            token_data=jsonObject.getString("token_data");
                            if (functions.isValueNull(token_data) && token_data.contains("{"))
                            {
                                JSONObject jsonObject1=new JSONObject(token_data);
                                String value="";
                                if (jsonObject1.has("value"))
                                {
                                    value=jsonObject1.getString("value");
                                }
                            }
                        }
                    }
                    if (jsonobject.has("card"))
                    {
                        String type="";
                        String cardholder_name="";
                        String card_number="";
                        String exp_date="";
                        JSONObject jsonObject=jsonobject.getJSONObject("card");
                        if (jsonObject.has("type"))
                        {
                            type=jsonObject.getString("type");
                        }
                        if (jsonObject.has("cardholder_name"));
                        {
                            cardholder_name=jsonObject.getString("cardholder_name");
                        }
                        if (jsonObject.has("card_number"))
                        {
                            card_number=jsonObject.getString("card_number");
                        }
                        if (jsonObject.has("exp_date"))
                        {
                            exp_date=jsonObject.getString("exp_date");
                        }
                    }
                    if (jsonobject.has("Error"))
                    {
                        JSONObject jsonObject1=jsonobject.getJSONObject("Error");
                        if (jsonObject1.has("messages"))
                        {
                            JSONArray jsonArray = jsonObject1.getJSONArray("messages");
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                                code = jsonObject3.getString("code");
                                description = jsonObject3.getString("description");
                            }
                        }
                    }
                    if (transaction_status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.error("Inside Approved case----"+transaction_status);
                        comresponse.setStatus("success");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(bank_message+"-"+gateway_message);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                    else
                    {
                        transactionLogger.error("Inside fail case----"+transaction_status);
                        comresponse.setStatus("fail");
                        comresponse.setRemark(transaction_status);
                        comresponse.setDescription(code+"-"+description);
                        comresponse.setTransactionId(transaction_id);
                        comresponse.setTransactionStatus(transaction_status);
                        comresponse.setCurrency(currency);
                        comresponse.setAmount(amount);
                        comresponse.setDescriptor(gateway.getDisplayName());
                        comresponse.setTransactionType(transaction_type);
                        comresponse.setResponseHashInfo(transaction_tag);
                    }
                }
                else
                {
                    comresponse.setStatus("fail");
                    comresponse.setRemark("Fail");
                    comresponse.setDescription("Fail");
                }
            }
            else
            {
                comresponse.setStatus("fail");
                comresponse.setRemark("Fail");
                comresponse.setDescription("Fail");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception in payeezy recurring---",e);
        }
        return comresponse;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}

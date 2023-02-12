package com.payment.infipay;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import okhttp3.*;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;


/**
 * Created by Admin on 2022-01-22.
 */
public class InfiPayUtils
{

    private static TransactionLogger transactionLogger = new TransactionLogger(InfiPayUtils.class.getName());
    static Functions functions = new Functions();

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("170".equalsIgnoreCase(paymentMode))
            payBrand = "IB";
        if("171".equalsIgnoreCase(paymentMode))
            payBrand = "BT";
        if("172".equalsIgnoreCase(paymentMode))
            payBrand = "MOMO";
        if("173".equalsIgnoreCase(paymentMode))
            payBrand = "ZALO";
        if("174".equalsIgnoreCase(paymentMode))
            payBrand = "VIETTEL";
        if("175".equalsIgnoreCase(paymentMode))
            payBrand = "TRUEWALLET";
        if ("191".equalsIgnoreCase(paymentMode))
            payBrand = "VA";
        if ("192".equalsIgnoreCase(paymentMode))
            payBrand = "OVOWALLET";
        if ("193".equalsIgnoreCase(paymentMode))
            payBrand = "GOPAY";
        return payBrand;
    }

    public static String getPaymentBrandPayout(String paymentMode)
    {
        String payBrand = "";
        if("171".equalsIgnoreCase(paymentMode))
            payBrand = "OUTBT";
        if("172".equalsIgnoreCase(paymentMode))
            payBrand = "OUTMOMO";

        return payBrand;
    }

    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }


    public static boolean isJSONARRAYValid(String test)
    {
        try
        {
            new JSONArray(test);
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    public  static  byte[] generateHmacSHA256(String algorithm, byte[] key, byte[] message)
    {
        Mac mac = null;
        try
        {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key,algorithm));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while cretaing hmac --> " + e);
        }
        return mac.doFinal(message);
    }
    public static String bytesToHex(byte[] hashInBytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHmac256Signature(String message, byte[] key)
    {
        byte[] bytes = generateHmacSHA256("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes).toUpperCase();
    }

    public CommRequestVO getInfiPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        //String merctId = account.getMerchantId();
        //String username = account.getFRAUD_FTP_USERNAME();
        //String password = account.getFRAUD_FTP_PASSWORD();
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

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setCardType(commonValidatorVO.getPaymentBrand());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        // merchantAccountVO.setMerchantId(merctId);
        // merchantAccountVO.setPassword(password);
        //merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

    public static String  doGetHttpUrlConnectio3n(String REQUEST_URL) throws Exception {
        HttpClient httpClient   = new HttpClient();
        GetMethod getMethod     = new GetMethod(REQUEST_URL);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        String body             = null;
        try {
            httpClient.executeMethod(getMethod);
            body        = getMethod.getResponseBodyAsString();
        } catch (HttpException he)
        {
            //transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("InfiPayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            //transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("InfiPayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }finally {
            getMethod.releaseConnection();
        }
        return body;
    }

    static String doPostHttpUrlConnection(String REQUEST_URL,String requestBody) throws Exception
    {
        Response response = null;

        try
        {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestBody);
            Request request = new Request.Builder()
                    .url(REQUEST_URL)
                    .method("POST", body)
                    .build();
            response = client.newCall(request).execute();





        }
        catch (HttpException he){
            transactionLogger.error("doPostTokenHTTPSURLConnection IOException ---->"+he);
            PZExceptionHandler.raiseTechnicalViolationException("InfiPayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("InfiPayUtils.java", "doGetPostHTTPUrlConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }

        return response.body().string();
    }

    public static String  doGetHttpUrlConnection(String REQUEST_URL) throws Exception {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(REQUEST_URL)
              //  .method("GET", body)
                .build();
        Response response = client.newCall(request).execute();
     return    response.body().string();
    }

    public static String getAmount(String amount) throws Exception {

       if(amount.contains(".") && amount.split("\\.").length>0)
       {
           amount = amount.split("\\.")[0];
       }
        return amount;
    }

}
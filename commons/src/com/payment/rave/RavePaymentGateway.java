package com.payment.rave;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ResourceBundle;

/**
 * Created by Roshan on 10/26/2017.
 */
public class RavePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "rave";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.rave");
    private static TransactionLogger transactionLogger = new TransactionLogger(RavePaymentGateway.class.getName());

    public RavePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        Functions functions1=new Functions();
        RaveUtils raveUtils= new RaveUtils();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String secKey =gatewayAccount.getMerchantId();
        String pubKey =gatewayAccount.getPassword();
        boolean isTest = gatewayAccount.isTest();

        String status = "";
        String remark = "";
        String descriptor = "";

        String responseData = "";
        try
        {
            String requestData =
                    "{"+
                            "\"PBFPubKey\":\"" + pubKey + "\"," +
                            "\"cardno\":\"" +cardDetailsVO.getCardNum()  + "\"," +
                            "\"currency\":\"" +transDetailsVO.getCurrency() + "\"," +
                            "\"country\":\"" +addressDetailsVO.getCountry() + "\"," +
                            "\"cvv\":\"" + cardDetailsVO.getcVV() + "\"," +
                            "\"amount\":\"" +transDetailsVO.getAmount() + "\"," +
                            "\"expiryyear\":\"" +cardDetailsVO.getExpYear() + "\"," +
                            "\"expirymonth\":\"" +cardDetailsVO.getExpMonth() + "\"," +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstname\":\"" + addressDetailsVO.getFirstname() + "\"," +
                            "\"lastname\":\"" + addressDetailsVO.getLastname() + "\"," +
                            "\"IP\":\"" + addressDetailsVO.getIp() + "\"," +
                            "\"narration\": \""+transDetailsVO.getOrderId()+"\"," +
                            "\"txRef\":\"" + trackingID + "\"," +
                            "\"phonenumber\":\""+addressDetailsVO.getTelnocc()+"-"+addressDetailsVO.getPhone() + "\"," +
                            "\"redirect_url\": \""+RB.getString("TERM_URL_3DS")+"\"" +
                            "}";

            String requestDataLog =
                    "{"+
                            "\"PBFPubKey\":\"" + pubKey + "\"," +
                            "\"cardno\":\"" +functions1.maskingPan(cardDetailsVO.getCardNum())  + "\"," +
                            "\"currency\":\"" +transDetailsVO.getCurrency() + "\"," +
                            "\"country\":\"" +addressDetailsVO.getCountry() + "\"," +
                            "\"cvv\":\"" + functions1.maskingNumber(cardDetailsVO.getcVV()) + "\"," +
                            "\"amount\":\"" +transDetailsVO.getAmount() + "\"," +
                            "\"expiryyear\":\"" +functions1.maskingNumber(cardDetailsVO.getExpYear()) + "\"," +
                            "\"expirymonth\":\"" +functions1.maskingNumber(cardDetailsVO.getExpMonth()) + "\"," +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstname\":\"" + addressDetailsVO.getFirstname() + "\"," +
                            "\"lastname\":\"" + addressDetailsVO.getLastname() + "\"," +
                            "\"IP\":\"" + addressDetailsVO.getIp() + "\"," +
                            "\"narration\": \""+transDetailsVO.getOrderId()+"\"," +
                            "\"txRef\":\"" + trackingID + "\"," +
                            "\"phonenumber\":\""+addressDetailsVO.getTelnocc()+"-"+addressDetailsVO.getPhone() + "\"," +
                            "\"redirect_url\": \""+RB.getString("TERM_URL_3DS")+"\"" +
                            "}";

            String key=raveUtils.getKey(secKey);
            String encData=raveUtils.encryptData(requestData, key);

            transactionLogger.error("-----sale request---"+trackingID+"--:" + requestDataLog);

            String request =
                    "{\"PBFPubKey\":\""+pubKey+"\"," +
                            "\"client\":\"" + encData + "\"," +
                            "\"alg\": \"3DES-24\" " +
                            "}";

            transactionLogger.error("-----sale encrypted request---"+trackingID+"--:" + request);

            if (isTest){
                transactionLogger.error("-----sale test url-----:"+RB.getString("DIRECT_CHARGE_TEST_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionFromData(RB.getString("DIRECT_CHARGE_TEST_URL"), request);
            }
            else{
                transactionLogger.error("-----sale live url-----:"+RB.getString("DIRECT_CHARGE_LIVE_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionFromData(RB.getString("DIRECT_CHARGE_LIVE_URL"), request);
            }

            transactionLogger.error("-----sale response---"+trackingID+"--:" + responseData);
            if(functions1.isValueNull(responseData)){
                JSONObject jsonObject = new JSONObject(responseData);
                String opStatus=jsonObject.getString("status");
                if(functions1.isValueNull(jsonObject.getString("data"))){
                    JSONObject jsonObjData1=jsonObject.getJSONObject("data");
                    if("success".equals(opStatus) && jsonObjData1.has("chargeResponseCode") &&  "02".equals(jsonObjData1.getString("chargeResponseCode"))){
                        if ("success-pending-validation".equals(jsonObjData1.getString("status"))){
                            status = "pending3DConfirmation";
                            commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(jsonObjData1.getString("authurl")));
                        }else  if("successful".equals(jsonObjData1.getString("status"))){
                            status = "success";
                            status =jsonObjData1.getString("chargeResponseMessage");
                        }
                        else{
                            status = "pending";
                            remark=jsonObjData1.getString("chargeResponseMessage");
                        }
                    }
                    else if("error".equals(opStatus) && jsonObjData1.has("chargeResponseCode") && "RR".equals(jsonObjData1.getString("chargeResponseCode"))){
                        status = "failed";
                        remark=jsonObjData1.getString("message");
                    }else if("error".equals(opStatus) && jsonObjData1.has("code") && "FLW_ERR".equals(jsonObjData1.getString("code"))){
                        status = "failed";
                        remark=jsonObjData1.getString("message");
                    }
                    else{
                        status = "pending";
                        remark=jsonObjData1.getString("message");
                    }
                }else{
                    status = "failed";
                    remark=jsonObject.getString("message");
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }
            transactionLogger.error("status::"+status);
            transactionLogger.error("remark::"+remark);
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
        }
        catch(JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        RaveUtils raveUtils= new RaveUtils();
        Functions functions=new Functions();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String secKey =gatewayAccount.getMerchantId();
        String pubKey =gatewayAccount.getPassword();
        boolean isTest = gatewayAccount.isTest();

        String status = "";
        String remark = "";
        String descriptor = "";
        String responseData = "";
        String transactionId="";

        String chargeType ="preauth";

        try
        {
            String requestData =
                    "{"+
                            "\"PBFPubKey\":\"" + pubKey + "\"," +
                            "\"charge_type\":\"" + chargeType + "\"," +
                            "\"cardno\":\"" +cardDetailsVO.getCardNum()  + "\"," +
                            "\"currency\":\"" + transDetailsVO.getCurrency() + "\"," +
                            "\"country\":\"" + addressDetailsVO.getCountry() + "\"," +
                            "\"cvv\":\"" + cardDetailsVO.getcVV() + "\"," +
                            "\"amount\":\"" + transDetailsVO.getAmount() + "\"," +
                            "\"expiryyear\":\"" + cardDetailsVO.getExpYear() + "\"," +
                            "\"expirymonth\":\"" + cardDetailsVO.getExpMonth() + "\"," +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstname\":\"" + addressDetailsVO.getFirstname() + "\"," +
                            "\"lastname\":\"" + addressDetailsVO.getLastname() + "\"," +
                            "\"IP\":\"" + addressDetailsVO.getIp() + "\"," +
                            "\"narration\": \""+transDetailsVO.getOrderId()+"\"," +
                            "\"txRef\":\"" + trackingID + "\"," +
                            "\"phonenumber\":\""+addressDetailsVO.getTelnocc()+"-"+addressDetailsVO.getPhone() + "\"," +
                            "\"redirect_url\": \""+RB.getString("TERM_URL_3DS")+"\"" +
                            "}";
                        String requestDataLog = "{"+
                            "\"PBFPubKey\":\"" + pubKey + "\"," +
                            "\"charge_type\":\"" + chargeType + "\"," +
                            "\"cardno\":\"" +functions.maskingPan(cardDetailsVO.getCardNum())  + "\"," +
                            "\"currency\":\"" + transDetailsVO.getCurrency() + "\"," +
                            "\"country\":\"" + addressDetailsVO.getCountry() + "\"," +
                            "\"cvv\":\"" + functions.maskingNumber(cardDetailsVO.getcVV()) + "\"," +
                            "\"amount\":\"" + transDetailsVO.getAmount() + "\"," +
                            "\"expiryyear\":\"" + functions.maskingNumber(cardDetailsVO.getExpYear()) + "\"," +
                            "\"expirymonth\":\"" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "\"," +
                            "\"email\":\"" + addressDetailsVO.getEmail() + "\"," +
                            "\"firstname\":\"" + addressDetailsVO.getFirstname() + "\"," +
                            "\"lastname\":\"" + addressDetailsVO.getLastname() + "\"," +
                            "\"IP\":\"" + addressDetailsVO.getIp() + "\"," +
                            "\"narration\": \""+transDetailsVO.getOrderId()+"\"," +
                            "\"txRef\":\"" + trackingID + "\"," +
                            "\"phonenumber\":\""+addressDetailsVO.getTelnocc()+"-"+addressDetailsVO.getPhone() + "\"," +
                            "\"redirect_url\": \""+RB.getString("TERM_URL_3DS")+"\"" +
                            "}";

            String key=raveUtils.getKey(secKey);
            String encData=raveUtils.encryptData(requestData, key);

            transactionLogger.error("-----auth request--"+trackingID+"---:"+requestDataLog);

            String request =
                    "{\"PBFPubKey\":\""+pubKey+"\"," +
                            "\"client\":\"" + encData + "\"," +
                            "\"alg\": \"3DES-24\" " +
                            "}";
            transactionLogger.error("-----auth encrypted request---"+trackingID+"--:" + request);

            if (isTest){
                transactionLogger.error("-----auth test url-----:"+RB.getString("PRE_AUTH_CHARGE_TEST_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionFromData(RB.getString("PRE_AUTH_CHARGE_TEST_URL"), request);
            }
            else{
                transactionLogger.error("-----auth live url-----:"+RB.getString("PRE_AUTH_CHARGE_LIVE_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionFromData(RB.getString("PRE_AUTH_CHARGE_LIVE_URL"), request);
            }

            transactionLogger.error("-----auth response--"+trackingID+"---:" + responseData);

            if(functions.isValueNull(responseData)){
                JSONObject jsonObject = new JSONObject(responseData);
                String opStatus=jsonObject.getString("status");
                JSONObject jsonObjData1=jsonObject.getJSONObject("data");
                transactionId=jsonObjData1.getString("flwRef");
                remark=jsonObjData1.getString("chargeResponseMessage");
                if("success".equals(opStatus)){
                    status="success";
                    descriptor=gatewayAccount.getDisplayName();
                }
                else{
                    status = "failed";
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setResponseHashInfo(transactionId);
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions=new Functions();
        RaveUtils raveUtils= new RaveUtils();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String status = "";
        String responseData = "";
        String remark="";
        String responseCode="";
        String transactionId="";
        try
        {
            String requestData =
                    "{"+
                            "\"flwRef\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\"," +
                            "\"SECKEY\":\"" + gatewayAccount.getMerchantId() + "\" " +
                            "}";

            transactionLogger.error("-----capture request-----:" + requestData);

            if (isTest){
                transactionLogger.error("-----capture test url-----:"+RB.getString("PRE_AUTH_CAPTURE_TEST_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionClient(RB.getString("PRE_AUTH_CAPTURE_TEST_URL"), requestData);
            }
            else{
                transactionLogger.error("-----capture live url-----:"+RB.getString("PRE_AUTH_CAPTURE_LIVE_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionClient(RB.getString("PRE_AUTH_CAPTURE_LIVE_URL"), requestData);
            }
            transactionLogger.error("-----capture response-----:"+responseData);

            if(functions.isValueNull(responseData)){
                JSONObject jsonObject = new JSONObject(responseData);
                String opStatus=jsonObject.getString("status");
                remark=jsonObject.getString("message");

                JSONObject jsonObjData1=jsonObject.getJSONObject("data");
                responseCode=jsonObjData1.getString("chargeResponseCode");
                transactionId=jsonObjData1.getString("flwRef");

                if("success".equals(opStatus)){
                    status="success";
                }
                else{
                    status = "failed";
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }

            commResponseVO.setStatus(status);
            commResponseVO.setErrorCode(responseCode);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setResponseHashInfo(transactionId);
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws  PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO responseVO = new CommResponseVO();
        Functions functions=new Functions();
        RaveUtils raveUtils= new RaveUtils();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String status = "";
        String descriptor = "";
        String responseData = "";
        String remark = "";
        String responseCode = "";
        String transactionId="";

        try
        {
            String requestData =
                    "{"+
                            "\"ref\":\"" + transactionDetailsVO.getPreviousTransactionId() + "\" ," +
                            "\"action\":\"void\" ," +
                            "\"SECKEY\" : \"" + gatewayAccount.getMerchantId()+ "\" " +
                            "}";

            transactionLogger.error("-----void request-----:" + requestData);

            if (isTest){
                transactionLogger.error("-----void test url-----:"+RB.getString("PRE_AUTH_VOID_TEST_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionClient(RB.getString("PRE_AUTH_VOID_TEST_URL"), requestData);
            }
            else{
                transactionLogger.error("-----void live url-----:"+RB.getString("PRE_AUTH_VOID_LIVE_URL"));
                responseData = raveUtils.doPostHTTPSURLConnectionClient(RB.getString("PRE_AUTH_VOID_LIVE_URL"), requestData);
            }

            transactionLogger.error("-----void response-----:" + responseData);
            if(functions.isValueNull(responseData)){
                JSONObject jsonObject = new JSONObject(responseData);
                String opStatus=jsonObject.getString("status");
                remark=jsonObject.getString("message");
                //JSONObject jsonObjData1=jsonObject.getJSONObject("data");
                 /*responseCode=jsonObjData1.getString("chargeResponseCode");
                transactionId=jsonObjData1.getString("flwRef");*/
                if("success".equals(opStatus)){
                    status="success";
                }
                else{
                    status = "failed";
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }

            responseVO.setRemark(remark);
            responseVO.setErrorCode(responseCode);
            responseVO.setStatus(status);
            responseVO.setDescriptor(descriptor);
            responseVO.setDescription(remark);
            responseVO.setTransactionId(transactionId);
            responseVO.setResponseHashInfo(transactionId);
        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while void transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO responseVO = new CommResponseVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions=new Functions();
        RaveUtils raveUtils= new RaveUtils();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String isService=commMerchantVO.getIsService();

        String status = "";
        String response = "";
        String remark = "";
        String responseCode = "";
        String transactionId="";
        try
        {
            String request="";
            String testUrl="";
            String liveUrl="";
            if("N".equals(isService)){
                request =
                        "{"+
                                "\"ref\":\"" + transactionDetailsVO.getPreviousTransactionId()+ "\" ," +
                                "\"action\":\"refund\" ," +
                                "\"SECKEY\" : \"" + gatewayAccount.getMerchantId()+ "\" " +
                                "}";

                testUrl=RB.getString("PRE_AUTH_REFUND_TEST_URL");
                liveUrl=RB.getString("PRE_AUTH_REFUND_LIVE_URL");
            }
            else{
                request =
                        "{"+
                                "\"ref\":\"" +transactionDetailsVO.getPreviousTransactionId()+ "\" ," +
                                "\"amount\":\"" + transactionDetailsVO.getAmount() + "\" ," +
                                "\"seckey\" : \"" + gatewayAccount.getFRAUD_FTP_USERNAME()+ "\" " +
                                "}";

                testUrl=RB.getString("DIRECT_CHARGE_REFUND_TEST_URL");
                liveUrl=RB.getString("DIRECT_CHARGE_REFUND_LIVE_URL");
            }

            transactionLogger.error("-----refund request-----:" + request);
            if(isTest){
                transactionLogger.error("-----refund test URL-----:" +testUrl);
                response = raveUtils.doPostHTTPSURLConnectionClient(testUrl, request);
            }
            else{
                transactionLogger.error("-----refund live URL-----:" + liveUrl);
                response = raveUtils.doPostHTTPSURLConnectionClient(liveUrl, request);
            }

            transactionLogger.error("-----refund response-----:" + response);

            if(functions.isValueNull(response)){
                JSONObject jsonObject = new JSONObject(response);
                String opStatus=jsonObject.getString("status");
                remark=jsonObject.getString("message");
                if("success".equals(opStatus)){
                    status="success";
                }
                else{
                    status = "failed";
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }
            responseVO.setStatus(status);
            responseVO.setErrorCode(responseCode);
            responseVO.setDescription(remark);
            responseVO.setRemark(remark);

        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refund transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return responseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}


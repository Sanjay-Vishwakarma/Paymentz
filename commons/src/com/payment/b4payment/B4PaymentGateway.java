package com.payment.b4payment;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.b4payment.vos.*;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Swamy on 2/1/2017.
 */
public class B4PaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "b4payment";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.b4payment");
    private static final Functions functions = new Functions();
    private static TransactionLogger transactionLogger = new TransactionLogger(B4PaymentGateway.class.getName());
    public B4PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args) throws PZTechnicalViolationException, IOException
    {
        String b4pid = "Z1000012";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        String token = "";
        token = "grant_type=password&client_id=default&client_secret=f5e31d92&scope=v1-sandbox&username=PSP6&password=a5e2db2e65c34873a7b3582b8095080c";

        String response = B4Utils.doHttpConnection(RB.getString("TOKEN_GENERATION"), token);
        //System.out.println("Response---------->" + response);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        TokenResponse responseVO = new TokenResponse();
        responseVO = objectMapper.readValue(response, TokenResponse.class);
        //System.out.println("ResponseVo-------->" + responseVO.toString());

        String one = responseVO.getAccess_token();
        String one1 = responseVO.getExpires_in();
        String one2 = responseVO.getToken_type();

        //System.out.println("onetwothreee----------->" + one + "--------ojb----" + one1 + "-----jftyui----" + one2);

        /*String saleRequest = "{\n" +
                "  \"transactionId\": \"2ghk889gu08jh784\",\n" +
                "  \"iban\": \"DE09100100101234567893\",\n" +
                "  \"bic\": \"PBNKDEFFXXX\",\n" +
                "  \"givenName\": \"Pranav\",\n" +
                "  \"familyName\": \"Damani\",\n" +
                "  \"currencyCode\": \"EUR\",\n" +
                "  \"amount\": 100.00,\n" +
                "  \"mandateId\": \"213456708\",\n" +
                "  \"mandateDate\": \"2017-01-17T10:47:40.431Z\",\n" +
                "  \"softDescriptor\": \"Welcome To India\",\n" +
                "  \"orderId\": \"1010553\",\n" +
                "  \"b4pId\": \"Z1000012\"\n" +
                "}";*/

       /* String saleRequest = "{\n" +
                "  \"transactionId\": \"6ois8765\",\n" +
                "  \"currencyCode\": \"EUR\",\n" +
                "  \"amount\": \"100.00\",\n" +
                "  \"softDescriptor\": \"Welcome To India\",\n" +
                "  \"orderId\": \"33445\",\n" +
                "  \"b4pId\": \"Z1000012\"\n" +
                "}";*/

        String saleRequest = "{\n" +
                "  \"transactionId\": \"ompp8\",\n" +
                "  \"orderId\": \"40048\",\n" +
                "  \"amount\": 100.00,\n" +
                "  \"softDescriptor\": \"Noreason\",\n" +
                "  \"currencyCode\": \"EUR\",\n" +
                "  \"b4pId\": \"Z1000012\"\n" +
                "}";

        //System.out.println("SAles REquest----" + saleRequest);

        String response1 = B4Utils.doHttpConnectionHeader(RB.getString("REFUND_URL_TEST"), saleRequest, one, "v1-sandbox");

        //System.out.println("SAles Response----" + response1);

        //ObjectMapper objectMapper1 = new ObjectMapper();
        //objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Response response2 = new Response();

        response2 = objectMapper.readValue(response1, Response.class);

        String end2endId = response2.getEnd2EndId();

        //System.out.println("end2EndId---------->"+end2endId);

        String responseUrl = "https://api.sepa.express/transaction/refund/"+b4pid+"/"+end2endId;
        //System.out.println("ofdfghjkjhgfdfghj--------->" + responseUrl);

        String completeResponse = B4Utils.doGetHTTPSURLConnectionClient(responseUrl,one, "v1-sandbox");
        //System.out.println("completeResponse--------->" + completeResponse);

        RebillResponse rebillResponse = new RebillResponse();
        Result result = new Result();

        ObjectMapper objectMapper1 = new ObjectMapper();
        ObjectMapper objectMapper2 = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        rebillResponse = objectMapper.readValue(completeResponse, RebillResponse.class);
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("B4PaymentGateway","processAuthentication",null,"common","This Functionality is not supported by gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        TransactionResponse transactionResponse = null;
        //AuthHeader Parameter
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = gatewayAccount.getMerchantId();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getPassword();
        String descriptor = gatewayAccount.getDisplayName();
        boolean isTest = gatewayAccount.isTest();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mandateDate = dateFormat.format(date);

        String scope = "v1";
        if(isTest){
            scope = "v1-sandbox";
        }

        try{
            //STEP 1
            String tokenGenerationRequest = null;
            transactionResponse = new TransactionResponse();
            tokenGenerationRequest = "grant_type=password&client_id=default&client_secret=f5e31d92&scope="+scope+"&username="+username+"&password="+password;
            transactionLogger.error("tokenGenerationRequest:::::"+tokenGenerationRequest);
            String tokenGenerationResponse = B4Utils.doHttpConnection(RB.getString("TOKEN_GENERATION"), tokenGenerationRequest);
            transactionLogger.error("tokenGenerationResponse:::::"+tokenGenerationResponse);

            if(!functions.isValueNull(tokenGenerationResponse)){
                transactionResponse.setStatus("authfailed");
                transactionResponse.setDescription("Bank Connectivity Issue");
                transactionResponse.setRemark("Bank Connectivity Issue");
                transactionResponse.setDescriptor("");
                return transactionResponse;
            }

            TokenResponse responseVO =null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(tokenGenerationResponse, TokenResponse.class);
            String token = responseVO.getAccess_token();

            //STEP 2
            String saleStep2Request = "{\n" +
                    "  \"transactionId\": \""+trackingId+"_0\",\n" +
                    "  \"iban\": \""+commRequestVO.getCardDetailsVO().getIBAN()+"\",\n" +
                    "  \"bic\": \""+commRequestVO.getCardDetailsVO().getBIC()+"\",\n" +
                    "  \"givenName\": \""+commRequestVO.getAddressDetailsVO().getFirstname()+"\",\n" +
                    "  \"familyName\": \""+commRequestVO.getAddressDetailsVO().getLastname()+"\",\n" +
                    "  \"currencyCode\": \""+commRequestVO.getTransDetailsVO().getCurrency()+"\",\n" +
                    "  \"amount\": "+commRequestVO.getTransDetailsVO().getAmount()+",\n" +
                    "  \"mandateId\": \""+commRequestVO.getCardDetailsVO().getMandateId()+"\",\n" +
                    "  \"mandateDate\": \""+mandateDate+"\",\n" +
                    "  \"softDescriptor\": \""+commRequestVO.getTransDetailsVO().getOrderDesc()+"\",\n" +
                    "  \"orderId\": \""+trackingId+"\",\n" +
                    "  \"b4pId\": \""+merchantId+"\"\n" +
                    "}";

            transactionLogger.error("saleStep2Request:::::" + saleStep2Request);
            String saleStep2Response="";
            //validation
            if(isTest){
                saleStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("STEP2_TRANSACTION_URL_TEST"), saleStep2Request, token, scope);
            }
            else{
                saleStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("STEP2_TRANSACTION_URL_LIVE"), saleStep2Request, token, scope);
            }

            transactionLogger.error("saleStep2Response:::::" + saleStep2Response);
            if(!functions.isValueNull(saleStep2Response)){
                transactionResponse.setStatus("authfailed");
                transactionResponse.setDescription("Billing Address Details Incorrect");
                transactionResponse.setRemark("Billing Address Details Incorrect");
                transactionResponse.setDescriptor("");
                return transactionResponse;
            }

            Response responseEnd2EndId =null;
            responseEnd2EndId = objectMapper.readValue(saleStep2Response, Response.class);
            String end2endId = responseEnd2EndId.getEnd2EndId();

            //STEP 3
            String saleStep3Request="";
            if(isTest){
                saleStep3Request=RB.getString("STEP3_TRANSACTION_URL_TEST")+merchantId+"/"+end2endId;
            }else{
                saleStep3Request=RB.getString("STEP3_TRANSACTION_URL_LIVE")+merchantId+"/"+end2endId;
            }
            transactionLogger.error("saleStep3Request:::::" + saleStep3Request);
            String salesStep3Response = B4Utils.doGetHTTPSURLConnectionClient(saleStep3Request, token, scope);
            transactionLogger.error("salesStep3Response:::::" + salesStep3Response);
            transactionResponse = objectMapper.readValue(salesStep3Response, TransactionResponse.class);

            Result result = null;
            if(transactionResponse!=null){
                result = transactionResponse.getResult().get(0);
                if(transactionResponse.getResult()!=null && transactionResponse.getResult().size()>0){
                    transactionResponse.setStatus("1".equalsIgnoreCase(result.getState()) ? "Success" : "Failed");
                    transactionResponse.setDescriptor(descriptor);
                    transactionResponse.setMerchantId(result.getB4pId());
                    transactionResponse.setAmount(result.getAmount());
                    transactionResponse.setErrorCode(commRequestVO.getTransDetailsVO().getOrderId());
                    transactionResponse.setTransactionId(result.getEnd2endId());
                    transactionResponse.setRemark("Transaction Approved");
                }
                if(transactionResponse.getMessage()!=null){
                    transactionLogger.error("transactionResponse.getMessage:::::" + transactionResponse.getMessage());
                    transactionResponse.setDescription("".equalsIgnoreCase(transactionResponse.getMessage()) ? "success" : "fail");
                }
                transactionResponse.setDescriptor(descriptor);
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(B4PaymentGateway.class.getName(), "processSale()", null, "common", "IOException while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return transactionResponse;
    }

    public GenericResponseVO processRefund(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        RefundResponse refundResponse = new RefundResponse();
        TokenResponse responseVO = null;
        Response responseEnd2EndId = null;

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId(); //!uZQpQTnE$Q8Rr&T
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getPassword();
        String scope = "v1";
        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        boolean isTest = gatewayAccount.isTest();
        if(isTest){
            scope = "v1-sandbox";
        }
        try{

            //AuthHeader Parameter STEP 1
            String tokenGenerationRequest = "grant_type=password&client_id=default&client_secret=f5e31d92&scope="+scope+"&username="+username+"&password="+password;
            transactionLogger.error("tokenGenerationRequest:::::"+tokenGenerationRequest);
            String tokenGenerationResponse = B4Utils.doHttpConnection(RB.getString("TOKEN_GENERATION"),tokenGenerationRequest);
            transactionLogger.error("tokenGenerationResponse"+tokenGenerationResponse);

            String uniqueTrackingId = B4Utils.generateToken(5);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(tokenGenerationResponse, TokenResponse.class);
            String token = responseVO.getAccess_token();

            //STEP 2
            String refundStep2Request = "{\n" +
                    "  \"transactionId\": \""+uniqueTrackingId+"\",\n" +
                    "  \"orderId\": \""+trackingId+"\",\n" +
                    "  \"amount\": "+commRequestVO.getTransDetailsVO().getAmount()+",\n" +
                    "  \"softDescriptor\": \""+commRequestVO.getTransDetailsVO().getOrderDesc()+"\",\n" +
                    "  \"currencyCode\": \""+commRequestVO.getTransDetailsVO().getCurrency()+"\",\n" +
                    "  \"b4pId\": \""+merchantId+"\"\n" +
                    "}";

            transactionLogger.error("refundStep2Request:::::" +refundStep2Request);
            String refundStep2Response="";
            if(isTest){
                refundStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("REFUND_URL_TEST"), refundStep2Request, token, scope);
            }else{
                refundStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("REFUND_URL_LIVE"), refundStep2Request, token, scope);
            }

            transactionLogger.error("refundStep2Response:::::" + refundStep2Response);
            responseEnd2EndId = objectMapper.readValue(refundStep2Response, Response.class);
            transactionLogger.error("responseEnd2EndId::::" + responseEnd2EndId);
            String end2endId = responseEnd2EndId.getEnd2EndId();

            //STEP 3
            String refundStep3Request="";
            if(isTest){
                refundStep3Request = RB.getString("REFUND_URL_TEST")+"/"+merchantId+"/"+end2endId;
            }else{
                refundStep3Request = RB.getString("REFUND_URL_LIVE")+"/"+merchantId+"/"+end2endId;
            }
            transactionLogger.error("refundStep3Request:::::" + refundStep3Request);
            String refundStep3Response = B4Utils.doGetHTTPSURLConnectionClient(refundStep3Request, token, scope);
            transactionLogger.error("refundStep3Response:::::" + refundStep3Response);

            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            refundResponse = objectMapper.readValue(refundStep3Response, RefundResponse.class);
            if(refundResponse!=null){
                if(refundResponse.getRefundResult()!=null && refundResponse.getRefundResult().size()>0){
                    RefundResult refundResult = refundResponse.getRefundResult().get(0);
                    refundResponse.setTransactionId(refundResult.getTransactionId());
                    refundResponse.setMerchantOrderId(refundResult.getOrderId());
                    refundResponse.setAmount(refundResult.getAmount());
                }
                if(refundResponse.getMessage()!=null){
                    refundResponse.setStatus("".equalsIgnoreCase(refundResponse.getMessage()) ? "success" : "fail");
                }
                refundResponse.setDescriptor(descriptor);
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(B4PaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return refundResponse;
    }

    public GenericResponseVO processRebilling(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        RebillResponse rebillResponse = new RebillResponse();

        //AuthHeader Parameter
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId(); //!uZQpQTnE$Q8Rr&T
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getPassword();
        String scope = "v1";
        boolean isTest = gatewayAccount.isTest();
        if(isTest)
        {
            scope = "v1-sandbox";
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String mandateDate = dateFormat.format(date);

        try
        {
            //STEP 1
            String tokenGenerationRequest = "grant_type=password&client_id=default&client_secret=f5e31d92&scope="+scope+"&username="+username+"&password="+password;
            transactionLogger.error("tokenGenerationRequest:::::"+tokenGenerationRequest);
            String tokenGenerationResponse = B4Utils.doHttpConnection(RB.getString("TOKEN_GENERATION"), tokenGenerationRequest);
            TokenResponse responseVO = null;

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(tokenGenerationResponse, TokenResponse.class);
            String token = responseVO.getAccess_token();

            //STEP 2
            String rebillStep2Request = "{\n" +
                    "  \"userContractId\": \""+commRequestVO.getTransDetailsVO().getPreviousTransactionId()+"\",\n" +
                    "  \"transactionId\": \""+trackingId+"\",\n" +
                    "  \"iban\": \""+genericCardDetailsVO.getIBAN()+"\",\n" +
                    "  \"bic\": \""+genericCardDetailsVO.getBIC()+"\",\n" +
                    "  \"givenName\": \""+genericAddressDetailsVO.getFirstname()+"\",\n" +
                    "  \"familyName\": \""+genericAddressDetailsVO.getLastname()+"\",\n" +
                    "  \"currencyCode\": \""+genericTransDetailsVO.getCurrency()+"\",\n" +
                    "  \"amount\": "+genericTransDetailsVO.getAmount()+",\n" +
                    "  \"mandateId\": \""+genericCardDetailsVO.getMandateId()+"\",\n" +
                    "  \"mandateDate\": \""+mandateDate+"\",\n" +
                    "  \"softDescriptor\": \""+genericTransDetailsVO.getOrderDesc()+"\",\n" +
                    "  \"orderId\": \""+genericTransDetailsVO.getOrderId()+"\",\n" +
                    "  \"b4pId\": \""+merchantId+"\"\n" +
                    "}";

            transactionLogger.error("rebillStep2Request:::::"+rebillStep2Request);
            String rebillStep2Response="";
            if(isTest){
                rebillStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("RECURRING_URL_TEST"), rebillStep2Request, token, scope);
            }else{
                rebillStep2Response = B4Utils.doHttpConnectionHeader(RB.getString("RECURRING_URL_LIVE"), rebillStep2Request, token, scope);
            }
            transactionLogger.error("rebillStep2Response:::::"+rebillStep2Response);
            Response responseEnd2EndId = null;
            responseEnd2EndId = objectMapper.readValue(rebillStep2Response, Response.class);
            transactionLogger.error("responseEnd2EndId::::"+responseEnd2EndId);
            String end2endId = responseEnd2EndId.getEnd2EndId();

            //STEP 3
            String rebillStep3Request="";
            if(isTest){
                rebillStep3Request = RB.getString("RECURRING_URL_TEST")+"/"+merchantId+"/"+end2endId;
            }
            else{
                rebillStep3Request = RB.getString("RECURRING_URL_LIVE")+"/"+merchantId+"/"+end2endId;
            }
            transactionLogger.error("rebillStep3Request:::::"+rebillStep3Request);

            String rebillStep3Response = B4Utils.doGetHTTPSURLConnectionClient(rebillStep3Request, token, scope);
            transactionLogger.error("rebillStep3Response:::::"+rebillStep3Response);
            rebillResponse = objectMapper.readValue(rebillStep3Response, RebillResponse.class);
            if(rebillResponse!=null){
                if(rebillResponse.getResult()!=null && rebillResponse.getResult().size()>0){
                    Result result = rebillResponse.getResult().get(0);
                    rebillResponse.setMerchantId(result.getB4pId());
                    rebillResponse.setAmount(result.getAmount());
                    rebillResponse.setTransactionId(result.getTransactionId());
                    rebillResponse.setResponseHashInfo(result.getEnd2endId());
                }
                if(rebillResponse.getMessage()!=null){
                    transactionLogger.error("transactionResponse.getMessage:::::" + rebillResponse.getMessage());
                    rebillResponse.setDescription(rebillResponse.getMessage());
                    rebillResponse.setRemark(rebillResponse.getMessage());
                }
            }
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(B4PaymentGateway.class.getName(), "processRebilling()", null, "common", "IOException while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (PZTechnicalViolationException e){
            PZExceptionHandler.raiseTechnicalViolationException(B4PaymentGateway.class.getName(), "processRebilling()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return rebillResponse;
    }

    public String getMaxWaitDays()
    {
        return null;
    }
}

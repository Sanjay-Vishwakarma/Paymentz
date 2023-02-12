package com.payment.neteller;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.neteller.response.Links;
import com.payment.neteller.response.NetellerResponse;
import com.payment.neteller.response.TokenResponse;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Sneha on 2/8/2017.
 */
public class NetellerPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "neteller";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.NetellerServlet");
    private static TransactionLogger transactionLogger = new TransactionLogger(NetellerPaymentGateway.class.getName());
    private static Functions functions = new Functions();

    public NetellerPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main1(String[] args)
    {
        String clientId = "AAABWhzY-VVks52j";
        String clientSecret="0.eZ2LPMoSVxD-vKhmT8mO2phW5zyXmgWta2DnKS9M3sM.IBnfGnRSF3I-ENnZgsyb9PmJvf8";
        String clientPass = clientId+ ":" +clientSecret;

        String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));
        String tokenCreationPath = "https://test.api.neteller.com/v1/oauth2/token?grant_type=client_credentials";
        String saleURL = "https://test.api.neteller.com/v1/orders";
        String inquiryURL = "https://test.api.neteller.com/v1/orders/";
        String payoutURL="https://test.api.neteller.com/v1/transferOut";

        try
        {
            NetellerUtils netellerUtils = new NetellerUtils();
            TokenResponse tokenResponse= new TokenResponse();
            // NetellerResponse netellerResponse = null;
            //   TokenResponse tokenResponse = new TokenResponse();
            //token generation process
            String response= NetellerUtils.doPostHTTPSURLConnectionClient(tokenCreationPath, "", "Basic", clientCredentials);
            //  String response= TestManager.dopost(tokenCreationPath, "", "Basic", clientCredentials);
            System.out.println("responseToken:::::"+response);

            ObjectMapper objectMapper= new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);



            String payoutRequest="{\n" +
                    "\"payeeProfile\": {\n" +
                    "\"email\": \"netellertest_BGN@neteller.com\"\n" +
                    "},\n" +
                    "\"transaction\": {\n" +
                    "\"amount\": 10,\n" +
                    "\"currency\": \"EUR\",\n" +
                    "\"merchantRefId\": \"1632851755\"\n" +
                    "},\n" +
                    "\"message\": \"sample message\"\n" +
                    "}'";

//            System.out.println("payoutRequest:::::"+payoutRequest);


            String payoutResponse = netellerUtils.doPostHTTPSURLConnectionClient(payoutURL, payoutRequest, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            // String saleRes = netellerUtils.doPostHTTPSURLConnectionClient(payoutURL, payoutRequest, "Bearer", "0.AQAAAV5-8zzKAAAAAAAEk-CjkUHH-rMVFLKA_FVc-EfR.NChgHAdtm0Kj1fjUVX2rKr1Zlf4");
  //          System.out.println("payoutResponse:::::"+payoutResponse);

            JSONObject jsonObject = new JSONObject(payoutResponse);
            JSONObject jsonObject1 = jsonObject.getJSONObject("transaction");
            String status = (String) jsonObject1.get("status");

       //     System.out.println("status:::::"+status);

            /*JSONObject jsonObject= new JSONObject(payoutResponse);
            JSONObject jsonObject1=jsonObject.getJSONObject("customer");
            JSONObject jsonObject2=jsonObject1.getJSONObject("link");
            String url=(String)jsonObject2.get("url");
            System.out.println("URL::::::"+url);


            String responseToken2= NetellerUtils.doPostHTTPSURLConnectionClient(tokenCreationPath, "", "Basic", clientCredentials);
            JSONObject jsonObject3=new JSONObject(responseToken2);
            String accessToken=(String)jsonObject3.get("accessToken");
            System.out.println("accessToken:::::"+accessToken);

            System.out.println("responseToken2:::::"+responseToken2);
            //   String payoutResponse2= TestManager.dopost(url,accessToken);
            String payoutResponse2= TestManager1.doPostHTTPSURLConnectionClient(url,accessToken);
            System.out.println("payoutResponse2:::::"+payoutResponse2);*/



            // String payoutResponse2=





            /*ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            netellerResponse = new NetellerResponse();
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            if(functions.isValueNull(tokenResponse.getError()))
            {
                return;
            }*/

/*            //sending generated token for transaction process
            String req = "{  \n" +
                    "   \"order\":{  \n" +
                    "      \"merchantRefId\":\"2702201702\",\n" +
                    "      \"totalAmount\":200,\n" +
                    "      \"currency\":\"USD\",\n" +
                    "      \"lang\":\"en_US\",\n" +
                    "\t  \"paymentMethods\": [{\n" +
                    "\t\t   \"type\": \"neteller\",\n" +
                    "\t\t   \"value\": \"neteller\"\n" +
                    "\t\t   }],\t  \n" +
                    "      \"redirects\":[{  \n" +
                    "            \"rel\":\"on_success\",\n" +
                    "            \"uri\":\"https://google.com\"\n" +
                    "         },\n" +
                    "         {  \n" +
                    "            \"rel\":\"on_cancel\",\n" +
                    "            \"uri\":\"http://requestb.in/16bf0vr1?result=cancel\"\n" +
                    "}]}}";

            System.out.println(req);
            String saleRes = netellerUtils.doPostHTTPSURLConnectionClient(saleURL, req, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            System.out.println("SALERES--->"+saleRes);

            ObjectMapper objectMapper1 = new ObjectMapper();
            objectMapper1.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            netellerResponse = new NetellerResponse();
            netellerResponse = objectMapper1.readValue(saleRes, NetellerResponse.class);*/

            //Inquiry functionality
           /* String inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(inquiryURL+"ORD_f514ba67-03bd-4a1f-a48c-d17221e1ddf5", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            System.out.println("inquiryRes--->"+inquiryRes);

            ObjectMapper objectMapper1 = new ObjectMapper();
            objectMapper1.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            netellerResponse = new NetellerResponse();
            netellerResponse = objectMapper1.readValue(inquiryRes, NetellerResponse.class);

            System.out.println("status-->"+netellerResponse.getStatus());
            System.out.println("error--->"+netellerResponse.getError().getCode());
            System.out.println("error--->"+netellerResponse.getErrorVO().getError().getCode());*/
        }
        catch (Exception e)
        {
           // e.printStackTrace();
        }
        /*catch (JsonMappingException e)
        {
            e.printStackTrace();
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }*/
    }

    public static void main(String[] args)
    {
        try
        {
            NetellerUtils netellerUtils = new NetellerUtils();
            String clientId = "AAABWhzY-VVks52j";
            String clientSecret="0.eZ2LPMoSVxD-vKhmT8mO2phW5zyXmgWta2DnKS9M3sM.IBnfGnRSF3I-ENnZgsyb9PmJvf8";
            String clientPass = clientId+ ":" +clientSecret;
            TokenResponse tokenResponse= new TokenResponse();

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));
            String tokenCreationPath = "https://test.api.neteller.com/v1/oauth2/token?grant_type=client_credentials";

            String response= NetellerUtils.doPostHTTPSURLConnectionClient(tokenCreationPath, "", "Basic", clientCredentials);
            //  String response= TestManager.dopost(tokenCreationPath, "", "Basic", clientCredentials);
           // System.out.println("responseToken:::::"+response);

            ObjectMapper objectMapper= new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);

            //String url = "https://test.api.neteller.com/v1/customers/CUS_64DE83ED-CA21-4672-B636-E85854A41185";
            String url = "https://test.api.neteller.com/v1/customers/CUS_18C9DE8B-F1AC-4BCE-96E2-9C760B62C18A?expand=customer";

            String customerResponse = netellerUtils.doGetHTTPSURLConnectionClient(url+"?expand=customer", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
         //   System.out.println("c response---"+customerResponse);
        }
        catch (Exception e)
        {

        }

    }

    public static void main2(String[] args)
    {
        String clientId = "AAABWhzY-VVks52j";
        String clientSecret="0.eZ2LPMoSVxD-vKhmT8mO2phW5zyXmgWta2DnKS9M3sM.IBnfGnRSF3I-ENnZgsyb9PmJvf8";
        String clientPass = clientId+ ":" +clientSecret;

        String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));
        String tokenCreationPath = "https://test.api.neteller.com/v1/oauth2/token?grant_type=client_credentials";
        String saleURL = "https://test.api.neteller.com/v1/orders";
        String inquiryURL = "https://test.api.neteller.com/v1/orders/";
        String payoutURL="https://test.api.neteller.com/v1/transferOut";

        try
        {
            NetellerUtils netellerUtils = new NetellerUtils();
            TokenResponse tokenResponse= new TokenResponse();
            // NetellerResponse netellerResponse = null;
            //   TokenResponse tokenResponse = new TokenResponse();
            //token generation process
            String response= NetellerUtils.doPostHTTPSURLConnectionClient(tokenCreationPath, "", "Basic", clientCredentials);
            //  String response= TestManager.dopost(tokenCreationPath, "", "Basic", clientCredentials);
            //System.out.println("responseToken:::::"+response);

            ObjectMapper objectMapper= new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);


            //String qResponse = netellerUtils.doGetHTTPSURLConnectionClient(inquiryURL+"ORD_9f729a30-c7eb-4252-9760-0ff5a075cb00", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            //String qResponse = netellerUtils.doGetHTTPSURLConnectionClient("https://test.api.neteller.com/v1/payments/199510154563024?expand=customer", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            // String qResponse = netellerUtils.doGetHTTPSURLConnectionClient("https://test.api.neteller.com/v1/orders/ORD_399ff744-6922-46bc-8a51-526ae907637d/invoice?expand=customer", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            String qResponse = netellerUtils.doGetHTTPSURLConnectionClient("https://test.api.neteller.com/v1/payments/52000?refType=merchantRefId", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            // String saleRes = netellerUtils.doPostHTTPSURLConnectionClient(payoutURL, payoutRequest, "Bearer", "0.AQAAAV5-8zzKAAAAAAAEk-CjkUHH-rMVFLKA_FVc-EfR.NChgHAdtm0Kj1fjUVX2rKr1Zlf4");
            //System.out.println("Inquiry Response:::::"+qResponse);

            JSONObject jsonObject = new JSONObject(qResponse);

            if(jsonObject.has("transaction")){

                String custUrl = jsonObject.getJSONObject("customer").getJSONObject("link").getString("url");

                JSONObject jsonObject1 = jsonObject.getJSONObject("transaction");
                String status = (String) jsonObject1.get("status");

              //  System.out.println("status:::::"+custUrl);
            }else
            {
                JSONObject jsonObject2 = jsonObject.getJSONObject("error");
                String code = (String) jsonObject2.get("code");

               // System.out.println("code:::::"+code);
            }



            // JSONObject jsonObject = new JSONObject(qResponse);


        }
        catch (Exception e)
        {
        }
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingid, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("NetellerPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::enter into processSale:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        NetellerResponse netellerResponse = new NetellerResponse();
        TokenResponse tokenResponse = new TokenResponse();
        NetellerUtils netellerUtils = new NetellerUtils();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency=transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }


        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String clientId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String clientSecret = GatewayAccountService.getGatewayAccount(accountId).getPassword();
            String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));
            transactionLogger.error("clientId:::::" + clientId);
            transactionLogger.error("clientSecret:::::" + clientSecret);

            //token generation process
            String response = "";
            if(isTest)
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            else
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }

            transactionLogger.error("token_response:::::" +trackingID + "--" +  response);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            if(functions.isValueNull(tokenResponse.getError()))
            {
                netellerResponse.setRemark(tokenResponse.getError());
                netellerResponse.setStatus("failed");
                return netellerResponse;
            }

            //sending generated token for transaction process
            String saleRes = "";
            if(isTest)
            {
                String saleRequest = "{  \n" +
                        "\"order\":{  \n" +
                        "\"merchantRefId\":\"" + trackingID + "\",\n" +
                        "\"totalAmount\":\"" + getCentAmount(transDetailsVO.getAmount()) + "\",\n" +
                        "\"currency\":\"" + transDetailsVO.getCurrency() + "\",\n" +
                        "\"lang\":\"" + addressDetailsVO.getLanguage() + "\",\n" +
                        "\"paymentMethods\": [{\n" +
                        "\"type\": \"" + commRequestVO.getTransDetailsVO().getCardType() + "\",\n" +
                        "\"value\": \"" + commRequestVO.getTransDetailsVO().getPaymentType() + "\"\n" +
                        "}],\n" +
                        "\"redirects\":[{  \n" +
                        "   \"rel\":\"on_success\",\n" +
                        "   \"uri\":\"" +RB.getString("SuccessURL")+trackingID+ "\"\n" +
                        "},\n" +
                        "{  \n" +
                        "   \"rel\":\"on_cancel\",\n" +
                        "   \"uri\":\"" +RB.getString("AuthFailedURL")+trackingID+ "\"\n" +
                        "},\n" +
                        "{  \n" +
                        "   \"rel\":\"on_timeout\",\n" +
                        "   \"uri\":\"" +RB.getString("TimeoutURL")+trackingID+ "\"\n" +
                        "}]}}";

                transactionLogger.error("-----sale request-----" +trackingID + "--" +  saleRequest);
                saleRes = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_AUTH_URL"), saleRequest, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            }
            else
            {
                String saleRequest = "{  \n" +
                        "\"order\":{  \n" +
                        "\"merchantRefId\":\"" + trackingID + "\",\n" +
                        "\"totalAmount\":\"" + getCentAmount(transDetailsVO.getAmount()) + "\",\n" +
                        "\"currency\":\"" + transDetailsVO.getCurrency() + "\",\n" +
                        "\"lang\":\"" + addressDetailsVO.getLanguage() + "\",\n" +
                        "\"paymentMethods\": [{\n" +
                        "\"type\": \"" + commRequestVO.getTransDetailsVO().getCardType() + "\",\n" +
                        "\"value\": \"" + commRequestVO.getTransDetailsVO().getPaymentType() + "\"\n" +
                        "}],\n" +
                        "\"redirects\":[{  \n" +
                        "   \"rel\":\"on_success\",\n" +
                        "   \"uri\":\"" +RB.getString("SuccessURL")+trackingID+ "\"\n" +
                        "},\n" +
                        "{  \n" +
                        "   \"rel\":\"on_cancel\",\n" +
                        "   \"uri\":\"" +RB.getString("AuthFailedURL")+trackingID+ "\"\n" +
                        "},\n" +
                        "{  \n" +
                        "   \"rel\":\"on_timeout\",\n" +
                        "   \"uri\":\"" +RB.getString("TimeoutURL")+trackingID+ "\"\n" +
                        "}]}}";

                transactionLogger.error("-----sale request-----" +trackingID + "--" +  saleRequest);
                saleRes = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_AUTH_URL"), saleRequest, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            }

            transactionLogger.error("-----sale response-----" + trackingID + "--" + saleRes);
            ObjectMapper objectMapper1 = new ObjectMapper();
            objectMapper1.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            netellerResponse = objectMapper1.readValue(saleRes, NetellerResponse.class);

            String status = "";
            if("pending".equalsIgnoreCase(netellerResponse.getStatus()))
            {
                status = "pending";
                netellerResponse.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                netellerResponse.setTransactionId(netellerResponse.getOrderId());
            }
            else if("failed".equalsIgnoreCase(netellerResponse.getStatus()))
            {
                status = "failed";
                netellerResponse.setRemark(netellerResponse.getError().getMessage());
                transactionLogger.error("failed-----" + netellerResponse.getRemark());
                netellerResponse.setErrorCode(netellerResponse.getError().getCode());
            }
            else if("expired".equalsIgnoreCase(netellerResponse.getStatus()))
            {
                status = "expired";
                netellerResponse.setRemark(netellerResponse.getError().getMessage());
                netellerResponse.setErrorCode(netellerResponse.getError().getCode());
            }

            netellerResponse.setStatus(status);
            netellerResponse.setTransactionType("auth");
            netellerResponse.setRemark(status);
            netellerResponse.setCurrency(currency);
            netellerResponse.setTmpl_Amount(tmpl_amount);
            netellerResponse.setTmpl_Currency(tmpl_currency);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            netellerResponse.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return netellerResponse;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        NetellerResponse netellerResponse = null;
        TokenResponse tokenResponse = new TokenResponse();
        NetellerUtils netellerUtils = new NetellerUtils();

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String clientId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String clientSecret = GatewayAccountService.getGatewayAccount(accountId).getPassword();
            String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));

            //token generation process
            String response;
            if(isTest){
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            else {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            transactionLogger.error("token_response:::::" + trackingID + "--" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            transactionLogger.error("neteller tokenResponse:::::" + tokenResponse);

            //Inquiry functionality
            String inquiryRes = "";
            if(isTest)
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_URL")+commRequestVO.getTransDetailsVO().getPreviousTransactionId(), tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller if inquiryRes----" +trackingID + "--" +  inquiryRes);
            }
            else
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL")+commRequestVO.getTransDetailsVO().getPreviousTransactionId(), tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller else inquiryRes----" + trackingID + "--" + inquiryRes);
            }
            ObjectMapper objectMapper1 = new ObjectMapper();
            objectMapper1.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            netellerResponse = new NetellerResponse();
            netellerResponse = objectMapper1.readValue(inquiryRes, NetellerResponse.class);

            netellerResponse = netellerUtils.readInquiryJson(inquiryRes);
            transactionLogger.error("neteller netellerResponse----" + netellerResponse.getRedirectUrl());

        }
        catch (PZTechnicalViolationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return netellerResponse;
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.error("-----inside neteller processInquiry");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        NetellerResponse commResponseVO= new NetellerResponse();

        TokenResponse tokenResponse = new TokenResponse();
        NetellerUtils netellerUtils = new NetellerUtils();

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String clientId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String clientSecret = GatewayAccountService.getGatewayAccount(accountId).getPassword();
            String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));

            //token generation process
            String response;
            if (isTest)
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            else
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            transactionLogger.error("token_response:::::" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            transactionLogger.error("neteller tokenResponse:::::" +tokenResponse);

            //Inquiry functionality
            String inquiryRes = "";
            if (isTest)
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_URL") +trackingID+ "?refType=merchantRefId", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller if inquiryRes----" +trackingID + "--" +  inquiryRes);
            }
            else
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL") +trackingID+ "?refType=merchantRefId", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller else inquiryRes----" +trackingID + "--" +  inquiryRes);
            }

            if (inquiryRes != null)
            {
                JSONObject jsonObject = new JSONObject(inquiryRes);
                if(jsonObject.has("transaction"))
                {
                    String status = jsonObject.getJSONObject("transaction").getString("status");
                    String id = jsonObject.getJSONObject("transaction").getString("id");

                    String custUrl = "";

                    if(jsonObject.has("customer"))
                    {
                        custUrl = jsonObject.getJSONObject("customer").getJSONObject("link").getString("url");
                    }

                    if ("accepted".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setTransactionType("inquiry");
                        commResponseVO.setRedirectUrl(custUrl);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                    else
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Declined");
                        commResponseVO.setTransactionType("inquiry");
                        commResponseVO.setRedirectUrl(custUrl);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                }
                else //error in transaction/not found
                {
                    String code = jsonObject.getJSONObject("error").getString("code");
                    String errorMsg = jsonObject.getJSONObject("error").getString("message");
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errorMsg);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionType("inquiry");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionType("inquiry");
                commResponseVO.setRemark("Transaction fail");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            }

        }
        catch (PZTechnicalViolationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.error("-----inside neteller processInquiry");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        String trackingID=commTransactionDetailsVO.getOrderId();
        transactionLogger.debug("trackingId-----"+trackingID);

        NetellerResponse commResponseVO= new NetellerResponse();

        TokenResponse tokenResponse = new TokenResponse();
        NetellerUtils netellerUtils = new NetellerUtils();

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String clientId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String clientSecret = GatewayAccountService.getGatewayAccount(accountId).getPassword();
            String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));

            //token generation process
            String response;
            if (isTest)
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            else
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            transactionLogger.error("token_response:::::" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            transactionLogger.error("neteller tokenResponse:::::" + tokenResponse);

            //Inquiry functionality
            String inquiryRes = "";
            if (isTest)
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_INQUIRY_URL") +trackingID+ "?refType=merchantRefId", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller if inquiryRes----" + trackingID + "--" + inquiryRes);
            }
            else
            {
                inquiryRes = netellerUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL") +trackingID+ "?refType=merchantRefId", tokenResponse.getTokenType(), tokenResponse.getAccessToken());
                transactionLogger.error("neteller else inquiryRes----" + trackingID + "--" + inquiryRes);
            }

            if (inquiryRes != null)
            {
                String transactionType="";
                String bankDate="";
                String amount="";
                String currency="";
                String description="";

                JSONObject jsonObject = new JSONObject(inquiryRes);
                if(jsonObject.has("transaction"))
                {
                    String status = jsonObject.getJSONObject("transaction").getString("status");
                    String id = jsonObject.getJSONObject("transaction").getString("id");
                    if(jsonObject.getJSONObject("transaction").has("transactionType")){
                        transactionType=jsonObject.getJSONObject("transaction").getString("transactionType");
                    }
                    if(jsonObject.getJSONObject("transaction").has("description")){
                        description=jsonObject.getJSONObject("transaction").getString("description");
                    }
                    if(jsonObject.getJSONObject("transaction").has("createDate")){
                        bankDate=jsonObject.getJSONObject("transaction").getString("createDate");
                    }
                    if(jsonObject.getJSONObject("transaction").has("amount")){
                        amount=jsonObject.getJSONObject("transaction").getString("amount");
                        amount= String.valueOf(Double.parseDouble(amount)/100);
                        transactionLogger.debug("amount----"+amount);
                    }
                    if(jsonObject.getJSONObject("transaction").has("currency")){
                        currency=jsonObject.getJSONObject("transaction").getString("currency");
                    }

                    String custUrl = "";

                    if(jsonObject.has("customer"))
                    {
                        custUrl = jsonObject.getJSONObject("customer").getJSONObject("link").getString("url");
                    }

                    if ("accepted".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setAuthCode("-");
                        commResponseVO.setDescription(description);
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setTransactionStatus("Transaction Successful");
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setTransactionType(transactionType);
                        commResponseVO.setRedirectUrl(custUrl);
                        commResponseVO.setBankTransactionDate(bankDate);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setCurrency(currency);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                    else
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Declined");
                        commResponseVO.setTransactionStatus("Transaction Declined");
                        commResponseVO.setTransactionType("inquiry");
                        commResponseVO.setRedirectUrl(custUrl);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                }
                else //error in transaction/not found
                {
                    String code = jsonObject.getJSONObject("error").getString("code");
                    String errorMsg = jsonObject.getJSONObject("error").getString("message");
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errorMsg);
                    commResponseVO.setErrorCode(code);
                    commResponseVO.setTransactionType("inquiry");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionType("inquiry"); commResponseVO.setRemark("Transaction fail");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            }

        }
        catch (PZTechnicalViolationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public String getNetellerCustomerDetails(String url,String accoountID)throws PZTechnicalViolationException
    {
        NetellerUtils netellerUtils = new NetellerUtils();
        TokenResponse tokenResponse= new TokenResponse();
        String customerEmail = "";
        String customerResponse = "";
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accoountID);
            boolean isTest = gatewayAccount.isTest();

            String clientId = GatewayAccountService.getGatewayAccount(accoountID).getMerchantId();
            String clientSecret = GatewayAccountService.getGatewayAccount(accoountID).getPassword();
            String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));

            //token generation process
            String response;
            if (isTest)
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            else
            {
                response = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            transactionLogger.error("token_response getNetellerCustomerDetails:::::" + response);

            ObjectMapper objectMapper= new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            tokenResponse = objectMapper.readValue(response, TokenResponse.class);

            customerResponse = netellerUtils.doGetHTTPSURLConnectionClient(url+"?expand=customer", tokenResponse.getTokenType(), tokenResponse.getAccessToken());


        }

        catch (JsonMappingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "getNetellerCustomerDetails()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "getNetellerCustomerDetails()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "getNetellerCustomerDetails()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return customerResponse;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.error(":::::enter into processPayout:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO =commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();

        NetellerUtils netellerUtils = new NetellerUtils();
        TokenResponse tokenResponse= null;

        GatewayAccount  gatewayAccount=GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        String clientId =gatewayAccount.getMerchantId();
        String clientSecret =gatewayAccount.getPassword();
        boolean isTest= gatewayAccount.isTest();
        String clientCredentials = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));
        try{
            String token="";
            if(isTest){
                token = NetellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }else{
                token = NetellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_CREATION_URL"), "", "Basic", clientCredentials);
            }
            transactionLogger.error(":::::token:::::"+token);

            ObjectMapper objectMapper= new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            tokenResponse = objectMapper.readValue(token, TokenResponse.class);

            String payoutRequest="{\n" +
                    "\"payeeProfile\": {\n" +
                    "\"email\": \""+commAddressDetailsVO.getEmail()+"\"\n" +
                    "},\n" +
                    "\"transaction\": {\n" +
                    "\"amount\": "+getCentAmount(commTransactionDetailsVO.getAmount())+",\n" +
                    "\"currency\": \""+commTransactionDetailsVO.getCurrency()+"\",\n" +
                    "\"merchantRefId\": \""+commTransactionDetailsVO.getOrderId()+"\"\n" +
                    "},\n" +
                    "\"message\": \"your order is ready\"\n" +
                    "}'";

            transactionLogger.error("-----payout request-----"+trackingID + "--" + payoutRequest);
            String payoutResponse="";
            if(isTest){
                payoutResponse = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL"), payoutRequest, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            }else{
                payoutResponse = netellerUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYOUT_URL"), payoutRequest, tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            }
            transactionLogger.error("-----payout response-----"+trackingID + "--" + payoutResponse);

            String staus1="";
            String remark="";
            String descriptor="";
            if(payoutResponse!=null){
                JSONObject jsonObject = new JSONObject(payoutResponse);
                JSONObject jsonObject1 = jsonObject.getJSONObject("transaction");
                String status = (String) jsonObject1.get("status");
                if("accepted".equalsIgnoreCase(status)){
                    staus1="success";
                    remark="Payout Successful";
                    descriptor=gatewayAccount.getDisplayName();
                }else{
                    staus1="fail";
                    remark="Payout Fail";
                }
            }else{
                staus1="fail";
                remark="Transaction Fail";
            }
            commResponseVO.setStatus(staus1);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(remark);
            commResponseVO.setDescriptor(descriptor);

        }catch(JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(NetellerPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO ;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        String html = "";

        CommRequestVO commRequestVO = null;
        NetellerResponse transRespDetails = null;
        NetellerUtils netellerUtils = new NetellerUtils();commonValidatorVO.getAddressDetailsVO().setLanguage("en_US");
        commRequestVO = netellerUtils.getNetellerRequestVO(commonValidatorVO);
        PaymentManager paymentManager=new PaymentManager();
        transRespDetails = (NetellerResponse) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);


        if (transRespDetails != null)
        {
            if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
            {
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                for(Links links : transRespDetails.getLinks())
                {
                    if(links.getRel().equals("hosted_payment"))
                    {
                        html = netellerUtils.generateAutoSubmitForm(links.getUrl());
                        /*res.sendRedirect(links.getUrl());
                        return;*/
                    }
                }
            }
            /*else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
            {
                paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                mailtransactionStatus = "Failed";
                billingDiscriptor = "";
            }*/
        }
        return html;
    }

    public String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
}

package com.payment.payvisionV2;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;

/**
 * Created by Admin on 9/6/2019.
 */
public class PayvisionV2PaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger=new Logger(PayvisionV2PaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(PayvisionV2PaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="pavisionV2";
    String url1="";

    public PayvisionV2PaymentGateway(String accountId)
    {
        this.accountId = accountId;

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (isTest)
        {
            url1 = "https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Payment";
        }
        else
        {
            url1 ="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Payment";
        }
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Payment";

        if (isTest)
        {
            transactionLogger.error("Test_URL----"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Payment";
        }
        else
        {
            transactionLogger.error("Live_URL----"+url);
            url="https://payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Payment";
        }

        try
        {
            transactionLogger.error("Inside PayvisionV2 Sale-----"+isTest+"   "+is3dSupported);

            String request="{\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"countryId\": \"528\",\n" +
                    "  \"amount\": \"1.00\",\n" +
                    "  \"currencyId\": \"840\",\n" +
                    "  \"trackingMemberCode\": \"order# 3\",\n" +
                    "  \"cardNumber\": \"4111111111111111\",\n" +
                    "  \"cardHolder\": \"test\",\n" +
                    "  \"cardExpiryMonth\": \"05\",\n" +
                    "  \"cardExpiryYear\": \"2022\",\n" +
                    "  \"cardCvv\": \"123\",\n" +
                    "  \"merchantAccountType\": \"1\"\n" +
                    "}";

            transactionLogger.error("PayvisionV2 request of sale---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisionV2 response of sale---"+response);

            if (response!=null && response.startsWith("{"))
            {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.has("PaymentResult"))
                {
                    String message = "";
                    String result = "";
                    String trackingMemberCode = "";
                    String transactionDateTime = "";
                    String transactionGuid = "";
                    String transactionId = "";
                    String bankCode = "";
                    String bankMsg = "";

                    JSONObject jsonObject1 = jsonObject.getJSONObject("PaymentResult");
                    if (jsonObject1.has("Message"))
                    {
                        message = jsonObject1.getString("Message");
                        transactionLogger.error("Message---" + message);
                    }
                    if (jsonObject1.has("Result"))
                    {
                        result = jsonObject1.getString("Result");
                        transactionLogger.error("Result---" + result);
                    }
                    if (jsonObject1.has("TrackingMemberCode"))
                    {
                        trackingMemberCode = jsonObject1.getString("TrackingMemberCode");
                        transactionLogger.error("TrackingMemberCode---" + trackingMemberCode);
                    }
                    if (jsonObject1.has("TransactionDateTime"))
                    {
                        transactionDateTime = jsonObject1.getString("TransactionDateTime");
                        transactionLogger.error("transactionDateTime---" + transactionDateTime);
                    }
                    if (jsonObject1.has("TransactionGuid"))
                    {
                        transactionGuid = jsonObject1.getString("TransactionGuid");
                        transactionLogger.error("TransactionGuid---" + transactionGuid);
                    }
                    if (jsonObject1.has("TransactionId"))
                    {
                        transactionId = jsonObject1.getString("TransactionId");
                        transactionLogger.error("transactionId---" + transactionId);
                    }

                    if (jsonObject1.has("Cdc"))
                    {
                        JSONObject object = null;
                        JSONArray jsonArray = jsonObject1.getJSONArray("Cdc");
                        for (int i = 0; i <= jsonArray.length(); i++)
                        {
                            object = jsonArray.getJSONObject(0);
                        }

                        JSONObject object1 = null;
                        HashMap hashMap = new HashMap<>();
                        JSONArray jsonArray1 = new JSONArray(object.getString("Items"));
                        for (int j = 0; j < jsonArray1.length(); j++)
                        {
                            object1 = jsonArray1.getJSONObject(j);
                            hashMap.put(object1.getString("Key"), object1.getString("Value"));
                        }
                        if (hashMap != null)
                        {
                            if (hashMap.get("BankCode")!= null && hashMap.get("BankMessage")!=null)
                            {
                                bankCode = (String) hashMap.get("BankCode");
                                bankMsg=(String)hashMap.get("BankMessage");
                                transactionLogger.error("bankcode---" + bankCode + "------bankmsg---" + bankMsg);
                            }else if (hashMap.get("ErrorCode")!=null && hashMap.get("ErrorMessage")!=null)
                            {
                                bankCode=(String)hashMap.get("ErrorCode");
                                bankMsg=(String)hashMap.get("ErrorMessage");
                                transactionLogger.error("errorcode---" + bankCode+"------errormsg---"+bankMsg);
                            }
                        }
                    }

                    if (result.equals("0"))
                    {
                        comm3DResponseVO1.setStatus("success");
                        comm3DResponseVO1.setRemark(message);
                        comm3DResponseVO1.setDescription(bankMsg);
                        comm3DResponseVO1.setTransactionId(transactionId);
                        comm3DResponseVO1.setResponseTime(transactionDateTime);
                    }

                }
                else
                {
                    comm3DResponseVO1.setStatus("fail");
                    comm3DResponseVO1.setRemark("Failed");
                    comm3DResponseVO1.setDescription("Failed");
                }
            }
            else
            {
                comm3DResponseVO1.setStatus("fail");
                comm3DResponseVO1.setRemark("Failed");
                comm3DResponseVO1.setDescription("Failed");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 sale-----",e);
        }
        return comm3DResponseVO1;
    }


    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String url="";

        if (isTest)
        {
            transactionLogger.error("Test_URL---"+url);
            url="";
        }
        else
        {
            transactionLogger.error("Live_URL---"+url);
            url="";
        }

        try
        {
            String request="{\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"countryId\": \"528\",\n" +
                    "  \"amount\": \"1.00\",\n" +
                    "  \"currencyId\": \"840\",\n" +
                    "  \"trackingMemberCode\": \"order# 10\",\n" +
                    "  \"cardNumber\": \"4111111111111111\",\n" +
                    "  \"cardHolder\": \"test\",\n" +
                    "  \"cardExpiryMonth\": \"05\",\n" +
                    "  \"cardExpiryYear\": \"2022\",\n" +
                    "  \"cardCvv\": \"123\",\n" +
                    "  \"merchantAccountType\": \"1\"\n" +
                    "}";

            transactionLogger.error("PayvisionV2 request of auth---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisonV2 response of auth---"+response);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 auth----",e);
        }
        return comm3DResponseVO1;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Capture";

        if (isTest)
        {
            transactionLogger.error("Inside Test_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Capture";
        }
        else
        {
            transactionLogger.error("nside Live_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Capture";
        }

        try
        {
            String request=" {\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"transactionId\":\"2148408777\",\n" +
                    "  \"transactionGuid\":\"46e36a9e-68c8-4130-9a6d-ef39c44715cd\",\n" +
                    "  \"amount\": \"1.00\",\n" +
                    "  \"currencyId\": \"840\",\n" +
                    "  \"trackingMemberCode\": \"capture order# 1\"\n" +
                    "}";

            transactionLogger.error("PayvisionV2 request of capture---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisionV2 response of capture----"+response);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 Capture---",e);
        }
        return comm3DResponseVO1;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String url="";

        if (isTest)
        {
            transactionLogger.error("Inside Test_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Refund";
        }
        else
        {
            transactionLogger.error("nside Live_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Refund";
        }

        try
        {
            String request=" {\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"transactionId\":\"2148409546\",\n" +
                    "  \"transactionGuid\":\"c38f1e13-6d7c-4476-abe3-4b187ceecd0d\",\n" +
                    "  \"amount\": \"1.00\",\n" +
                    "  \"currencyId\": \"840\",\n" +
                    "  \"trackingMemberCode\": \"refund# 1\",\n" +
                    "}";

            transactionLogger.error("PayvisonV2 request of refund---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisionV2 response of refund---"+response);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 refund----",e);
        }
        return comm3DResponseVO1;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String url="";

        if (isTest)
        {
            transactionLogger.error("Inside Test_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Void";
        }
        else
        {
            transactionLogger.error("Inside Live_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/Void";
        }

        try
        {
            String request="  {\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"transactionId\":\"2148409639\",\n" +
                    "  \"transactionGuid\":\"f3b8b31c-8c72-4248-b68e-2d4c08e19ab2\",\n" +
                    "  \"trackingMemberCode\": \"void# 1\",\n" +
                    "}";

            transactionLogger.error("PayvisonV2 request of void---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisionV2 response of void---"+response);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 void---",e);
        }
        return comm3DResponseVO1;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO1=new Comm3DResponseVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String url="";

        if (isTest)
        {
            transactionLogger.error("Inside Test_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/RetrieveTransactionResult";
        }
        else
        {
            transactionLogger.error("Inside Live_URL---"+url);
            url="https://testprocessor.payvisionservices.com/GatewayV2/BasicOperationsService.svc/json/RetrieveTransactionResult";
        }

        try
        {
            String request="{\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"transactionDate\":\"/Date(1567767117130)/\",\n" +
                    "  \"trackingMemberCode\": \"void# 1\",\n" +
                    "}";

            transactionLogger.error("PayvisonV2 request of inquiry---"+request);

            String response=PayvisionV2Utils.doPostHttpConnection(url,request);

            transactionLogger.error("PayvisionV2 response of inquiry---"+response);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in PayvisionV2 inquiry---",e);
        }
        return comm3DResponseVO1;
    }


    public static void main(String[] args)
    {
        //intiateAuth();
        authenticate();
    }

    @Override
    public GenericResponseVO processInitiateAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        CommResponseVO commResponseVO=new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String memberGuid=gatewayAccount.getFRAUD_FTP_USERNAME();

        transactionLogger.error("MID----"+mid+"----mGuid----"+memberGuid);

        try
        {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(180000);
            client.setReadTimeout(180000);

            WebResource service = null;
            service = client.resource("https://testprocessor.payvisionservices.com/");

            String request="{\n" +
                    "  \"memberId\": \""+mid+"\",\n" +
                    "  \"memberGuid\": \""+memberGuid+"\",\n" +
                    "  \"trackingMemberCode\": \""+transactionDetailsVO.getOrderId()+"\"\n" +
                    "}";

            transactionLogger.error("InitiateAuth Request----"+request);
            transactionLogger.error("InitiateAuth Request----" + request);

            String response= service.path("GatewayV2").path("ThreeDSecure2Service.svc").path("json").path("InitiateAuthentication").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, request);

            transactionLogger.error("InitiateAuth Response----"+response);
            transactionLogger.error("InitiateAuth Response----" + response);

            if (functions.isValueNull(response) && response.startsWith("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null)
                {
                    if (jsonObject.has("InitiateAuthenticationResult"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("InitiateAuthenticationResult");
                        if (jsonObject1 != null)
                        {
                            String collectPayload = "";
                            String referenceId = "";
                            String message="";
                            String result="";
                            String trackingMemberCode="";

                            if (jsonObject1.has("CollectPayload"))
                            {
                                collectPayload = jsonObject1.getString("CollectPayload");
                                transactionLogger.error("CollectPayload----" + collectPayload);
                            }
                            if (jsonObject1.has("ReferenceId"))
                            {
                                referenceId = jsonObject1.getString("ReferenceId");
                                transactionLogger.error("ReferenceId----" + referenceId);
                            }
                            if (jsonObject1.has("Message"))
                            {
                                message=jsonObject1.getString("Message");
                                transactionLogger.error("message----" + message);
                            }
                            if (jsonObject1.has("Result"))
                            {
                                result=jsonObject1.getString("Result");
                                transactionLogger.error("result----" + result);
                            }
                            if (jsonObject1.has("TrackingMemberCode"))
                            {
                                trackingMemberCode=jsonObject1.getString("TrackingMemberCode");
                                transactionLogger.error("trackingMemberCode----" + trackingMemberCode);
                            }

                            if (result.equals("0"))
                            {
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark(result);
                                commResponseVO.setDescription(message);
                                commResponseVO.setTransactionId(referenceId);
                                commResponseVO.setResponseHashInfo(collectPayload);
                                commResponseVO.setMerchantOrderId(trackingMemberCode);
                            }
                            else
                            {
                                commResponseVO.setStatus("fail");
                                commResponseVO.setRemark("fail");
                                commResponseVO.setDescription("fail");
                            }
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark("fail");
                            commResponseVO.setDescription("failed");
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("fail");
                        commResponseVO.setDescription("failed");
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("fail");
                    commResponseVO.setDescription("failed");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("fail");
                commResponseVO.setDescription("failed");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Inside exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthenticate(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions=new Functions();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String memberGuid=gatewayAccount.getFRAUD_FTP_USERNAME();

        transactionLogger.error("MID----"+mid+"----mGuid----"+memberGuid);

        try
        {
            ClientConfig config=new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(180000);
            client.setReadTimeout(180000);

            WebResource service=null;
            service= client.resource("https://testprocessor.payvisionservices.com/");

            String request="{\n" +
                    "\"memberId\": \""+mid+"\",\n" +
                    "\"memberGuid\": \""+memberGuid+"\",\n" +
                    "\"referenceId\": \""+transactionDetailsVO.getResponseOrderNumber()+"\",\n" +
                    "\"trackingMemberCode\": \""+transactionDetailsVO.getOrderId()+"\",\n" +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\",\n" +
                    "\"countryId\": \"528\",\n" +
                    "\"currencyId\": \"840\",\n" +
                    "\"cardNumber\": \""+cardDetailsVO.getCardNum()+"\",\n" +
                    "\"cardholder\": \""+addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname()+"\",\n" +
                    "\"cardExpiryMonth\": \""+cardDetailsVO.getExpMonth()+"\",\n" +
                    "\"cardExpiryYear\": \""+cardDetailsVO.getExpYear()+"\",\n" +
                    "\"merchantAccountType\": \"1\",\n" +
                    "\"orderNumber\": \""+trackingID+"\",\n" +
                    "\"billingInfo\": {\n" +
                    "\"email\": \""+addressDetailsVO.getEmail()+"\",\n" +
                    "\"mobilePhone\": \""+addressDetailsVO.getPhone()+"\",\n" +
                    "\"address1\": \""+addressDetailsVO.getStreet()+"\",\n" +
                    "\"country\": \""+addressDetailsVO.getCountry()+"\",\n" +
                    "\"city\": \""+addressDetailsVO.getCity()+"\",\n" +
                    "\"state\": \""+addressDetailsVO.getState()+"\",\n" +
                    "\"postalCode\": \""+addressDetailsVO.getZipCode()+"\"\n" +
                    "},\n" +
                    "\"deviceInfo\": {\n" +
                    "\"deviceType\": \"S\"\n" +
                    "}\n" +
                    "}";

            transactionLogger.error("Authenticate request----"+request);

            String response=service.path("GatewayV2").path("ThreeDSecure2Service.svc").path("json").path("Authenticate").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,request);

            transactionLogger.error("Authenticate response----"+response);

            if (functions.isValueNull(response) && response.startsWith("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject!=null)
                {
                    JSONObject jsonObject1=jsonObject.getJSONObject("AuthenticateResult");
                    if (jsonObject1!=null)
                    {
                        String message = "";
                        String result = "";
                        String authenticationIndicator = "";
                        String dateTime = "";
                        String enrolled = "";
                        String issuerUrl = "";
                        String threeDSFlow = "";
                        String threeDSVersion = "";
                        String trackingMemberCode = "";
                        String challengePayload = "";
                        String paymentPayload = "";
                        String transactionId = "";
                        String token = "";
                        String eci = "";

                        if (jsonObject1.has("Message"))
                        {
                            message = jsonObject1.getString("Message");
                            transactionLogger.error("message----" + message);
                        }
                        if (jsonObject1.has("Result"))
                        {
                            result = jsonObject1.getString("Result");
                            transactionLogger.error("result----" + result);
                        }
                        if (jsonObject1.has("AuthenticationIndicator"))
                        {
                            authenticationIndicator = jsonObject1.getString("AuthenticationIndicator");
                            transactionLogger.error("authenticationIndicator----" + authenticationIndicator);
                        }
                        if (jsonObject1.has("DateTime"))
                        {
                            dateTime = jsonObject1.getString("DateTime");
                            transactionLogger.error("dateTime----" + dateTime);
                        }
                        if (jsonObject1.has("Enrolled"))
                        {
                            enrolled = jsonObject1.getString("Enrolled");
                            transactionLogger.error("enrolled----" + enrolled);
                        }
                        if (jsonObject1.has("IssuerUrl"))
                        {
                            issuerUrl = jsonObject1.getString("IssuerUrl");
                            transactionLogger.error("issuerUrl----" + issuerUrl);
                        }
                        if (jsonObject1.has("ThreeDSFlow"))
                        {
                            threeDSFlow = jsonObject1.getString("ThreeDSFlow");
                            transactionLogger.error("threeDSFlow----" + threeDSFlow);
                        }
                        if (jsonObject1.has("ThreeDSVersion"))
                        {
                            threeDSVersion = jsonObject1.getString("ThreeDSVersion");
                            transactionLogger.error("threeDSVersion----" + threeDSVersion);
                        }
                        if (jsonObject1.has("TrackingMemberCode"))
                        {
                            trackingMemberCode = jsonObject1.getString("TrackingMemberCode");
                            transactionLogger.error("trackingMemberCode----" + trackingMemberCode);
                        }
                        if (jsonObject1.has("ChallengePayload"))
                        {
                            challengePayload = jsonObject1.getString("ChallengePayload");
                            transactionLogger.error("ChallengePayload----" + challengePayload);

                            if (challengePayload != null && challengePayload.startsWith("{"))
                            {
                                JSONObject jsonObject2 = new JSONObject(challengePayload);
                                if (jsonObject2 != null)
                                {
                                    if (jsonObject2.has("TransactionId"))
                                    {
                                        transactionId = jsonObject2.getString("TransactionId");
                                        transactionLogger.error("transid-----" + transactionId);
                                    }
                                    if (jsonObject2.has("Token"))
                                    {
                                        token = jsonObject2.getString("Token");
                                        System.out.println("token----" + token);
                                    }
                                }
                            }
                        }

                            if (jsonObject1.has("PaymentPayload"))
                        {
                            paymentPayload=jsonObject1.getString("PaymentPayload");
                            transactionLogger.error("PaymentPayload----" + paymentPayload);

                            if (paymentPayload!=null && paymentPayload.startsWith("{"))
                            {
                                JSONObject jsonObject2=new JSONObject(paymentPayload);
                                if (jsonObject2!=null)
                                {
                                    if (jsonObject2.has("Eci"))
                                    {
                                        eci=jsonObject2.getString("Eci");
                                        transactionLogger.error("Eci-----"+eci);
                                    }
                                    if (jsonObject2.has("Token"))
                                    {
                                        token=jsonObject2.getString("Token");
                                        transactionLogger.error("token----"+token);
                                    }
                                }
                            }
                        }
                        if (result.equals("0"))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescription(message);
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark("fail");
                            commResponseVO.setDescription(message);
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("fail");
                        commResponseVO.setDescription("failed");
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("fail");
                    commResponseVO.setDescription("failed");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("fail");
                commResponseVO.setDescription("failed");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in Authenticate----",e);
        }
        return commResponseVO;
    }

    public static void intiateAuth()
    {
        Functions functions=new Functions();

        try
        {
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(180000);
            client.setReadTimeout(180000);

            WebResource service = null;
            service = client.resource("https://testprocessor.payvisionservices.com/");

            String request="{\n" +
                    "  \"memberId\": \"142967\",\n" +
                    "  \"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "  \"trackingMemberCode\": \"order# 305\"\n" +
                    "}";

            transactionLogger.error("InitiateAuth Request----"+request);
            System.out.println("InitiateAuth Request----"+request);

            String response= service.path("GatewayV2").path("ThreeDSecure2Service.svc").path("json").path("InitiateAuthentication").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, request);

            transactionLogger.error("IntiateAuth Response----"+response);
            System.out.println("IntiateAuth Response----"+response);

            if (functions.isValueNull(response) && response.startsWith("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null)
                {
                    if (jsonObject.has("InitiateAuthenticationResult"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("InitiateAuthenticationResult");
                        if (jsonObject1 != null)
                        {
                            String collectPayload = "";
                            String referenceId = "";

                            if (jsonObject1.has("CollectPayload"))
                            {
                                collectPayload = jsonObject1.getString("CollectPayload");
                                System.out.println("CollectPayload----" + collectPayload);
                            }
                            if (jsonObject1.has("ReferenceId"))
                            {
                                referenceId = jsonObject1.getString("ReferenceId");
                                System.out.println("ReferenceId----" + referenceId);
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Inside exception-----",e);
        }
    }

    public static void authenticate()
    {
        Functions functions=new Functions();

        try
        {
            ClientConfig config=new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(180000);
            client.setReadTimeout(180000);

            WebResource service=null;
            service= client.resource("https://testprocessor.payvisionservices.com/");

            String request="{\n" +
                    "\"memberId\": \"142967\",\n" +
                    "\"memberGuid\": \"D2949DD1-C8D2-4C2F-9985-5F3DDDE2362D\",\n" +
                    "\"referenceId\": \"12886\",\n" +
                    "\"trackingMemberCode\": \"order# 305\",\n" +
                    "\"amount\": \"1.00\",\n" +
                    "\"countryId\": \"528\",\n" +
                    "\"currencyId\": \"840\",\n" +
                    "\"cardNumber\": \"4111111111111111\",\n" +
                    "\"cardholder\": \"LUCIE\",\n" +
                    "\"cardExpiryMonth\": \"01\",\n" +
                    "\"cardExpiryYear\": \"2020\",\n" +
                    "\"merchantAccountType\": \"1\",\n" +
                    "\"orderNumber\": \"123456\",\n" +
                    "\"billingInfo\": {\n" +
                    "\"email\": \"example@domain.com\",\n" +
                    "\"mobilePhone\": \"123445567\",\n" +
                    "\"address1\": \"Elm street\",\n" +
                    "\"country\": \"US\",\n" +
                    "\"city\": \"Miami\",\n" +
                    "\"state\": \"FL\",\n" +
                    "\"postalCode\": \"10101\"\n" +
                    "},\n" +
                    "\"deviceInfo\": {\n" +
                    "\"deviceType\": \"S\"\n" +
                    "}\n" +
                    "}";

            transactionLogger.error("Authenticate request----"+request);

            String response=service.path("GatewayV2").path("ThreeDSecure2Service.svc").path("json").path("Authenticate").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,request);

            transactionLogger.error("Authenticate response----"+response);

            if (functions.isValueNull(response) && response.startsWith("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject!=null)
                {
                    JSONObject jsonObject1=jsonObject.getJSONObject("AuthenticateResult");
                    if (jsonObject1!=null)
                    {
                        String message="";
                        String result="";
                        String authenticationIndicator="";
                        String challengePayload="";
                        String dateTime="";
                        String enrolled="";
                        String issuerUrl="";
                        String threeDSFlow="";
                        String threeDSVersion="";
                        String trackingMemberCode="";

                        if (jsonObject1.has("Message"))
                        {
                            message=jsonObject1.getString("Message");
                            System.out.println("message----"+message);
                        }
                        if (jsonObject1.has("Result"))
                        {
                            result=jsonObject1.getString("Result");
                            System.out.println("result----"+result);
                        }
                        if (jsonObject1.has("AuthenticationIndicator"))
                        {
                            authenticationIndicator=jsonObject1.getString("AuthenticationIndicator");
                            System.out.println("authenticationIndicator----"+authenticationIndicator);
                        }
                        if (jsonObject1.has("ChallengePayload"))
                        {
                            challengePayload=jsonObject1.getString("ChallengePayload");
                            System.out.println("ChallengePayload----"+challengePayload);

                            if (challengePayload!=null && challengePayload.startsWith("{"))
                            {
                                JSONObject jsonObject2=new JSONObject(challengePayload);
                                if (jsonObject2!=null)
                                {
                                    if (jsonObject2.has("TransactionId"))
                                    {
                                        String transid=jsonObject2.getString("TransactionId");
                                        System.out.println("transid-----"+transid);
                                    }
                                    if (jsonObject2.has("Token"))
                                    {
                                        String token=jsonObject2.getString("Token");
                                        System.out.println("token----"+token);
                                    }
                                }
                            }
                        }
                        if (jsonObject1.has("DateTime"))
                        {
                            dateTime=jsonObject1.getString("DateTime");
                            System.out.println("dateTime----"+dateTime);
                        }
                        if (jsonObject1.has("Enrolled"))
                        {
                            enrolled=jsonObject1.getString("Enrolled");
                            System.out.println("enrolled----"+enrolled);
                        }
                        if (jsonObject1.has("IssuerUrl"))
                        {
                            issuerUrl=jsonObject1.getString("IssuerUrl");
                            System.out.println("issuerUrl----"+issuerUrl);
                        }
                        if (jsonObject1.has("ThreeDSFlow"))
                        {
                            threeDSFlow=jsonObject1.getString("ThreeDSFlow");
                            System.out.println("threeDSFlow----"+threeDSFlow);
                        }
                        if (jsonObject1.has("ThreeDSVersion"))
                        {
                            threeDSVersion=jsonObject1.getString("ThreeDSVersion");
                            System.out.println("threeDSVersion----"+threeDSVersion);
                        }
                        if (jsonObject1.has("TrackingMemberCode"))
                        {
                            trackingMemberCode=jsonObject1.getString("TrackingMemberCode");
                            System.out.println("trackingMemberCode----"+trackingMemberCode);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception in Authenticate----",e);
        }
    }

}

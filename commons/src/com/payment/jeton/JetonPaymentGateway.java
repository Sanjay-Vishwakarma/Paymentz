package com.payment.jeton;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.skrill.SkrillUtills;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Uday on 8/1/17.
 */
public class JetonPaymentGateway extends AbstractPaymentGateway
{
    public static final  String GATEWAY_TYPE="jeton";
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.jeton");
    TransactionLogger transactionLogger=new TransactionLogger(JetonPaymentGateway.class.getName());
    Functions functions= new Functions();

    public JetonPaymentGateway (String accountId){this.accountId=accountId;}

    public static void main(String[] args)
    {
        TransactionLogger transactionLogger=new TransactionLogger(JetonPaymentGateway.class.getName());
        // String apiKey="6ec38e74a58a4bb883f300f4cd683531";
        //    String apiKey="6b8688adfcfa455a9675bc0694f2910b";
        try
        {

         /*   String authorizeRequest="{\n" +
                    "  \"apiKey\": \"35c537399dcf459bb811b9459cb5919e\"\n" +
                    "}";*/

            //System.out.println("authorizeRequest:::::"+authorizeRequest);
            // String authorizationToken= JetonUtils.doPostHTTPSURLConnectionClient2("https://sandbox-walletapi.jeton.com/api/v2/integration/merchant/authorize",authorizeRequest);
            //System.out.println("authorizationToken:::::"+authorizationToken);



           /* String payoutRequest ="{\n" +
                    "   \"merchantOrderId\":\"22222\"\n" +//53917
                    "}";*/


            //System.out.println("payoutRequest::::::::::"+payoutRequest);

            //    String payoutResponse = JetonUtils.doPostHTTPSURLConnectionClient3("https://sandbox-walletapi.jeton.com/api/v2/integration/merchant/payment-status", authorizationToken, payoutRequest);
            //System.out.println("payoutResponse::::::::::"+payoutResponse);

         /*   JSONObject jsonObject= new JSONObject(payoutResponse);

            if(!jsonObject.has("header"))
            {
                transactionLogger.debug("status------" + jsonObject.getString("status"));
            }else {
                transactionLogger.debug(jsonObject.getJSONObject("header").getJSONArray("errors").getJSONObject(0).getString("errorCode"));
            }*/

           /* String  orderId="45639";
            String  orderDescription="uday";
            int  accountId=2721;
            String  payoutAmount="10";
            int   toId=11091;
            String  terminalId="662";
            String  ipAddress="192.168.0.10";
            String  header="hariya";
            String  customerEmail="<emailaddress>";
            String  customerAccount="97677959";

            CommonPaymentProcess commonPaymentProcess=new CommonPaymentProcess();
            PZPayoutRequest payoutRequest=new PZPayoutRequest();
            payoutRequest.setOrderId(orderId);
            payoutRequest.setOrderDescription(orderDescription);
            payoutRequest.setAccountId(accountId);
            payoutRequest.setPayoutAmount(payoutAmount);
            payoutRequest.setMemberId(toId);
            payoutRequest.setTerminalId(terminalId);
            payoutRequest.setIpAddress(ipAddress);
            payoutRequest.setHeader(header);
            payoutRequest.setCustomerEmail(customerEmail);
            payoutRequest.setCustomerAccount(customerAccount);

            PZPayoutResponse payoutResponse=commonPaymentProcess.payout( payoutRequest);
            String dbStatus=payoutResponse.getDbStatus();
            String responseDescription=payoutResponse.getResponseDesceiption();
            String status= String.valueOf(payoutResponse.getStatus());
            String trackingid= payoutResponse.getTrackingId();

            System.out.println("dbStatus:::::"+dbStatus);
            System.out.println("responseDescription:::::"+responseDescription);
            System.out.println("status:::::"+status);
            System.out.println("trackingid:::::"+trackingid);*/


           /* String request="{\n" +
                    "\"orderId\": \"21514\",\n" +
                    "\"currency\": \"EUR\",\n" +
                    "\"amount\": 1,\n" +
                    "\"returnUrl\": \"http://localhost:8081/transaction/JetonFrontEndServlet\",\n" +
                    "\"method\": \"QR\"\n" +
                  *//*  "\"customer\": 76007956\n" +*//*
                    "}";*/

           /* String req="{\n" +
                    "\t\"orderId\": \"63214\",\n" +
                    "\t\"currency\": \"EUR\",\n" +
                    "\t\"amount\": 10,\n" +
                    "\t\"returnUrl\": \"http://localhost:8081/transaction/JetonFrontEndServlet\",\n" +
                    "\t\"method\": \"DIRECT\",\n" +
                    "\t\"customer\": \"76007956\",\n" +
                    "\t\"customerReferenceNo\": \"563255\"\n" +
                    "}";*/


           /* String ipn_url="/api/v3/integration/merchants/payments/ipn/histories";
            String sale_url="/api/v3/integration/merchants/payments/pay";
            String staus_ipn_url="/api/v3/integration/merchants/payments/ipn/status";

            String ipnRequest="{\n" +
                    "\"orderId\": \"82412\",\n" +
                    "\"paymentId\": \"73090\",\n" +
                    "\"type\": \"PAY\"\n" +
                    "}";

            String inquiryRequest="{\n" +
                    "\"orderId\": \"83405\",\n" +   //83142
                 //   "\"paymentId\": \"73115\",\n" +
                    "\"type\": \"PAY\"\n" +
                    "}";
            System.out.println("request-----"+inquiryRequest);*/

            // String respone = JetonUtils.doPostHTTPSURLConnectionClient("https://sandbox-walletapi.jeton.com/api/v3/integration/merchants/payments/ipn/status", "6b8688adfcfa455a9675bc0694f2910b", inquiryRequest);
            // String respone = JetonUtils.doPostHTTPSURLConnectionClient("https://staging.<hostname>.com/transaction/JetonBackEndServlet?trackingid=12121&name=uday&surname=raj", inquiryRequest);

            //System.out.println("response-----"+respone);




        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::::",e );
        }



    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException{
        transactionLogger.error(":::::enter into processSale:::::");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        String apiKey="";
        String saleResponse="";

        try{
            GenericTransDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();
            apiKey=gatewayAccount.getMerchantId();

            String saleRequest="{\n" +
                    "\"orderId\": \""+trackingId+"\",\n" +
                    "\"currency\": \""+transDetailsVO.getCurrency()+"\",\n" +
                    "\"amount\": \""+transDetailsVO.getAmount()+"\",\n" +
                    "\"returnUrl\": \""+RB.getString("REDIRECT_URL")+trackingId+"\",\n" +
                    "\"method\": \"CHECKOUT\"\n" +
                    "}";

            transactionLogger.error("-----sale request-----"+trackingId + "--" + saleRequest);

            if(isTest){
                saleResponse=JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_URL_TEST"),apiKey,saleRequest);
            }else{
                saleResponse=JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_URL_LIVE"),apiKey,saleRequest);
            }
            transactionLogger.error("-----sale response-----"+trackingId + "--" + saleResponse);

            ObjectMapper objectMapper=new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            JetonResponseVO jetonResponseVO=objectMapper.readValue(saleResponse,JetonResponseVO.class);
            commResponseVO.setRedirectUrl(jetonResponseVO.getCheckout());
            commResponseVO.setTransactionId(jetonResponseVO.getPaymentId());

        }catch(JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(),e.getCause());
        }catch(IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(),e.getCause());
        }
        return commResponseVO;

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("JetonPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by PerfectMoney gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException ,PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommResponseVO commResponseVO=new CommResponseVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        try{
            boolean isTest=gatewayAccount.isTest();
            String apiKey=gatewayAccount.getMerchantId();
            CommRequestVO commRequestVO=(CommRequestVO)requestVO;
            CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
            String dbStatus=commTransactionDetailsVO.getPrevTransactionStatus();

            String transType="";
            if(dbStatus.contains("auth") || dbStatus.contains("capture")){
                transType="PAY";
            }else if(dbStatus.contains("payout")){
                transType="PAYOUT";
            }

            String inquiryRequest="{\n" +
                    "\"orderId\": \""+trackingID+"\",\n" +
                    "\"type\": \""+transType+"\"\n" +
                    "}";

            transactionLogger.error("inquiryRequest::::::::::" +trackingID + "--" +  inquiryRequest);

            String inquiryResponse="";
            if(isTest)
            {
                inquiryResponse = JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("INQUIRY_URL_TEST"), apiKey, inquiryRequest);
            }else {
                inquiryResponse = JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("INQUIRY_URL_LIVE"), apiKey, inquiryRequest);
            }
            transactionLogger.error("inquiryResponse::::::::::" + trackingID + "--" + inquiryResponse);


            if(inquiryResponse!=null)
            {
                JSONObject jsonObject = new JSONObject(inquiryResponse);

                String paymentId="";
                String type="";
                String customer="";
                String amount="";
                String currecny="";
                String status="";
                String message="";

                if(jsonObject.has("paymentId"))
                    paymentId=jsonObject.getString("paymentId");
                if(jsonObject.has("type"))
                    type=jsonObject.getString("type");
                if(jsonObject.has("customer"))
                    customer=jsonObject.getString("customer");
                if(jsonObject.has("amount"))
                    amount=jsonObject.getString("amount");
                if(jsonObject.has("currency"))
                    currecny=jsonObject.getString("currency");
                if(jsonObject.has("status"))
                    status=jsonObject.getString("status");
                if(jsonObject.has("message"))
                    message=jsonObject.getString("message");

                if(functions.isValueNull(type)){
                    if(type.equalsIgnoreCase("PAY")){
                        type= PZProcessType.SALE.toString();
                    }else {
                        type=PZProcessType.PAYOUT.toString();
                    }
                }

                if ("SUCCESS".equalsIgnoreCase(status))
                {
                    commResponseVO.setTransactionStatus("Transaction Successful");
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(type);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currecny);

                }
                else if("INITIALIZED".equalsIgnoreCase(status)){
                    commResponseVO.setTransactionStatus("Transaction Initialized");
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setTransactionType(type);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currecny);
                }
                else if(status.contains("fail"))
                {
                    commResponseVO.setTransactionStatus("Transaction Fail");
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setTransactionType(type);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currecny);

                }
                else {
                    commResponseVO.setStatus("Not Found");
                    commResponseVO.setRemark("Transaction not found");
                    commResponseVO.setDescription("Not Found");
                    commResponseVO.setTransactionType("inquiry");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Error");
                commResponseVO.setTransactionType("inquiry");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch(PZTechnicalViolationException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processQuery (String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException ,PZTechnicalViolationException
    {
        transactionLogger.error("-----inside processQuery-----");
        CommResponseVO commResponseVO=new CommResponseVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        try{
            boolean isTest=gatewayAccount.isTest();
            String apiKey=gatewayAccount.getMerchantId();
            CommRequestVO commRequestVO=(CommRequestVO)requestVO;
            CommTransactionDetailsVO commTransactionDetailsVO= commRequestVO.getTransDetailsVO();
            String dbStatus=commTransactionDetailsVO.getPrevTransactionStatus();
            transactionLogger.debug("dbStatus-----"+dbStatus);

            String transType="";
            if(dbStatus.contains("auth") || dbStatus.contains("capture")){
                transType="PAY";
            }else if(dbStatus.contains("payout")){
                transType="PAYOUT";
            }

            transactionLogger.debug("apiKey:::::"+apiKey);

            String inquiryRequest="{\n" +
                    "\"orderId\": \""+trackingID+"\",\n" +
                    "\"type\": \""+transType+"\"\n" +
                    "}";


            transactionLogger.error("inquiryRequest::::::::::" + trackingID + "--" + inquiryRequest);

            String inquiryResponse="";
            if(isTest)
            {
                inquiryResponse = JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("INQUIRY_URL_TEST"), apiKey, inquiryRequest);
            }else {
                inquiryResponse = JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("INQUIRY_URL_LIVE"), apiKey, inquiryRequest);
            }
            transactionLogger.error("inquiryResponse::::::::::" + trackingID + "--" + inquiryResponse);


            if(inquiryResponse!=null)
            {
                JSONObject jsonObject = new JSONObject(inquiryResponse);

                String paymentId="";
                String type="";
                String customer="";
                String amount="";
                String currecny="";
                String status="";
                String message="";

                if(jsonObject.has("paymentId"))
                    paymentId=jsonObject.getString("paymentId");
                if(jsonObject.has("type"))
                    type=jsonObject.getString("type");
                if(jsonObject.has("customer"))
                    customer=jsonObject.getString("customer");
                if(jsonObject.has("amount"))
                    amount=jsonObject.getString("amount");
                if(jsonObject.has("currency"))
                    currecny=jsonObject.getString("currency");
                if(jsonObject.has("status"))
                    status=jsonObject.getString("status");
                if(jsonObject.has("message"))
                    message=jsonObject.getString("message");

                if(functions.isValueNull(type)){
                    if(type.equalsIgnoreCase("PAY")){
                        type= PZProcessType.SALE.toString();
                    }else {
                        type=PZProcessType.PAYOUT.toString();
                    }
                }

                if ("SUCCESS".equalsIgnoreCase(status))
                {
                    commResponseVO.setTransactionStatus("Transaction Successful");
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(type);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currecny);

                }
                else if(status.contains("fail"))
                {
                    commResponseVO.setTransactionStatus("Transaction Fail");
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setTransactionType(type);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currecny);

                }else {
                    commResponseVO.setStatus("Not Found");
                    commResponseVO.setRemark("Transaction not found");
                    commResponseVO.setDescription("Not Found");
                    commResponseVO.setTransactionType("inquiry");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }
            else
            {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Error");
                commResponseVO.setTransactionType("inquiry");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch(PZTechnicalViolationException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonPaymentGateway.class.getName(),"processRefund()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonPaymentGateway.class.getName(),"processCapture()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException(JetonPaymentGateway.class.getName(), "processVoid()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws  PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::enter into processPayout:::::");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        String apiKey="";
        String payoutResponse="";
        String dbStatus="payout";

        try{
            CommTransactionDetailsVO transactionDetailsVO =commRequestVO.getTransDetailsVO();
            CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            apiKey=gatewayAccount.getMerchantId();

            String payoutRequest="{\n" +
                    "\"orderId\": \""+trackingID+"\",\n" +
                    "\"amount\": \""+transactionDetailsVO.getAmount()+"\",\n" +
                    "\"currency\": \""+transactionDetailsVO.getCurrency()+"\",\n" +
                    "\"customer\": \""+commCardDetailsVO.getAccountNumber()+"\",\n" +
                    "\"note\": \""+transactionDetailsVO.getOrderDesc()+"\"\n" +
                    "}";

            transactionLogger.error("-----payout request-----"+trackingID + "--" + payoutRequest);
            if(isTest){
                payoutResponse= JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_URL_TEST"), apiKey, payoutRequest);
            }else{
                payoutResponse= JetonUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_URL_LIVE"), apiKey, payoutRequest);
            }
            transactionLogger.error("-----payout response-----"+trackingID + "--" + payoutResponse);

            transactionLogger.debug("dbStatus-------" + dbStatus);
            ((CommRequestVO) requestVO).getTransDetailsVO().setPrevTransactionStatus(dbStatus);
            commResponseVO= (CommResponseVO) this.processQuery(trackingID,requestVO);

        }catch(PZTechnicalViolationException e){
            PZExceptionHandler.raiseTechnicalViolationException(JetonPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for Jeton:::::");
        CommRequestVO commRequestVO = null;
        String html = "";
        JetonUtils jetonUtils = new JetonUtils();
        PaymentManager paymentManager=new PaymentManager();
        GenericResponseVO transRespDetails = null;

        SkrillUtills skrillUtills=new SkrillUtills();
        commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);

        transRespDetails = this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

        CommResponseVO commResponseVO = (CommResponseVO) transRespDetails;

        html = jetonUtils.generateAutoSubmitForm(commResponseVO.getRedirectUrl(), commonValidatorVO);
        return html;
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}



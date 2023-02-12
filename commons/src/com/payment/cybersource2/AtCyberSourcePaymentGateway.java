package com.payment.cybersource2;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
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
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by Vivek on 11/19/2020.
 */
public class AtCyberSourcePaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AtCyberSourcePaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="atcybersource";
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.atcybersource");
    public AtCyberSourcePaymentGateway(String accountId){this.accountId=accountId;}
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String encodedPassword="";
        if(functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        String termUrl="";
        String phone="";
        String email="";
        String street="";
        String zip="";
        String countryCode="";
        String city="";
        String state="";
        String transactionStatus="";
        String message="";
        String referenceNumber="";
        String transactionDateTime="";
        String paymentRequestId="";
        String responseAmount="";
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            phone=commAddressDetailsVO.getPhone();
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            email=commAddressDetailsVO.getEmail();
        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            street=commAddressDetailsVO.getStreet();
        if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            zip=commAddressDetailsVO.getZipCode();
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode=commAddressDetailsVO.getCountry();
        if(countryCode.length()==3)
            countryCode=AtCyberSourceUtils.getCountryTwoDigit(countryCode);
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
            city=commAddressDetailsVO.getCity();
        if(functions.isValueNull(commAddressDetailsVO.getState()))
            state=commAddressDetailsVO.getState();
        String cardtype=GatewayAccountService.getCardType(commTransactionDetailsVO.getCardType());
        String paymentMethod=AtCyberSourceUtils.getPaymentMethod(cardtype);
       if(functions.isValueNull(commMerchantVO.getHostUrl()))
            termUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
       else
            termUrl=RB.getString("TERM_URL")+trackingID;

        String enData= PzEncryptor.encryptCVV(commCardDetailsVO.getcVV());

        String initUrl="";
        String privatePath="";
        if(isTest)
        {
            initUrl=RB.getString("INIT_TEST_URL");
            privatePath=RB.getString("TEST_PRIVATE_KEY");
        }else{
            initUrl=RB.getString("INIT_LIVE_URL");
            privatePath=RB.getString("LIVE_PRIVATE_KEY");
        }
        String firstname= new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        String lastname= new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        String name="";
        if(functions.isValueNull(firstname) && functions.isValueNull(lastname))
           name= firstname + "~" + lastname;
        else if(functions.isValueNull(firstname))
            name=firstname;
        else
            name=lastname;
        try
        {
            termUrl+="&data="+ ESAPI.encoder().encodeForURL(enData);
            UUID uuid1 = UUID.randomUUID();
            String requestId=uuid1.toString();
            transactionLogger.error("requestId--->"+requestId);
            String authenticationHash = AtCyberSourceUtils.getSignature(mid + requestId + commTransactionDetailsVO.getCurrency() + commTransactionDetailsVO.getAmount(),privatePath);
            transactionLogger.error("authenticationHash--->"+authenticationHash);
            StringBuffer request = new StringBuffer();
            request.append("{" +
                    " \"merchantdetails\": {" +
                    " \"id\": \"" + mid + "\"," +
                    " \"password\": \"" + encodedPassword + "\"" +
                    " }," +
                    " \"paymentrequest\": {" +
                    " \"requestid\": \"" + requestId + "\"," +
                    " \"paymentordernumber\": \"" + trackingID + "\"," +
                    " \"paymentswitch\": \"01\"," +
                    " \"paymentmethod\": \"" + paymentMethod + "\"," +
                    " \"command\": \"Debit\"," +
                    " \"currency\": \"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    " \"amount\": " + commTransactionDetailsVO.getAmount() + "," +
                    " \"mobilenumber\": \"" + phone + "\"," +
                    " \"otp\": null," +
                    " \"accountnumber\": \"" + commCardDetailsVO.getCardNum() + "\"," +
                    " \"cardexpirydatemonth\": \"" + commCardDetailsVO.getExpMonth() + "\"," +
                    " \"cardexpirydateyear\": \"" + commCardDetailsVO.getExpYear() + "\"," +
                    " \"cvv\": \"" + commCardDetailsVO.getcVV() + "\"," +
                    " \"customername\": \"" + name + "\"," +
                    " \"customeraddress\": \"" + street + "\"," +
                    " \"reasonforpayment\": \"00\"," +
                    " \"paymentdetails\": \"" + commTransactionDetailsVO.getOrderDesc() + "\"," +
                    " \"additionaltext1\": \"" + name + "\"," +
                    " \"additionaltext2\": \"" + street + "~"+city+"~"+state+"~" + zip + "~" + countryCode + "\"," +//address1~locality~administrativeArea~postalCode~country
                    " \"additionaltext3\": \"" + phone + "~" + email + "\"," +
                    " \"additionalamount1\": null," +
                    " \"additionalamount2\": null" +
                    " }}");
            StringBuffer requestLog = new StringBuffer();
            requestLog.append("{" +
                    " \"merchantdetails\": {" +
                    " \"id\": \"" + mid + "\"," +
                    " \"password\": \"" + encodedPassword + "\"" +
                    " }," +
                    " \"paymentrequest\": {" +
                    " \"requestid\": \"" + requestId + "\"," +
                    " \"paymentordernumber\": \"" + trackingID + "\"," +
                    " \"paymentswitch\": \"01\"," +
                    " \"paymentmethod\": \"" + paymentMethod + "\"," +
                    " \"command\": \"Debit\"," +
                    " \"currency\": \"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    " \"amount\": " + commTransactionDetailsVO.getAmount() + "," +
                    " \"mobilenumber\": \"" + phone + "\"," +
                    " \"otp\": null," +
                    " \"accountnumber\": \"" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "\"," +
                    " \"cardexpirydatemonth\": \"" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "\"," +
                    " \"cardexpirydateyear\": \"" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "\"," +
                    " \"cvv\": \"" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "\"," +
                    " \"customername\": \"" + name+ "\"," +
                    " \"customeraddress\": \"" + street + "\"," +
                    " \"reasonforpayment\": \"00\"," +
                    " \"paymentdetails\": \"" + commTransactionDetailsVO.getOrderDesc() + "\"," +
                    " \"additionaltext1\": \"" + name + "\"," +
                    " \"additionaltext2\": \"" + street + "~"+city+"~"+state+"~" + zip + "~" + countryCode + "\"," +//address1~locality~administrativeArea~postalCode~country
                    " \"additionaltext3\": \"" + phone + "~" + email + "\"," +
                    " \"additionalamount1\": null," +
                    " \"additionalamount2\": null" +
                    " }}");
            transactionLogger.error("Init Sale Request---" + trackingID + "--->" + requestLog.toString());
            String initResponse = AtCyberSourceUtils.doPostHTTPSURLConnectionClient(initUrl, request.toString(), authenticationHash);
            transactionLogger.error("Init Sale Response---" + trackingID + "--->" + initResponse);
            if(functions.isValueNull(initResponse)){
                JSONObject initResponseJSON=new JSONObject(initResponse);
                if(initResponseJSON.has("TransactionStatus"))
                    transactionStatus=initResponseJSON.getString("TransactionStatus");
                if(initResponseJSON.has("PaymentRequestId"))
                    paymentRequestId=initResponseJSON.getString("PaymentRequestId");
                if(initResponseJSON.has("Message"))
                    message=initResponseJSON.getString("Message");
                if(initResponseJSON.has("TransactionDateTime"))
                    transactionDateTime=initResponseJSON.getString("TransactionDateTime");
                if(initResponseJSON.has("ReferenceNumber"))
                    referenceNumber=initResponseJSON.getString("ReferenceNumber");
                if(initResponseJSON.has("Amount"))
                    responseAmount=initResponseJSON.getString("Amount");
                if("6".equalsIgnoreCase(transactionStatus) || "5".equalsIgnoreCase(transactionStatus))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setAmount(String.format("%.2f",Double.parseDouble(responseAmount)));
                    if(functions.isValueNull(message))
                    {
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction successfully completed");
                        comm3DResponseVO.setDescription("Transaction successfully completed");
                    }

                }else if("0".equalsIgnoreCase(transactionStatus) || "1".equalsIgnoreCase(transactionStatus)){
                    comm3DResponseVO.setStatus("pending");
                    if(functions.isValueNull(message))
                    {
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction is pending");
                        comm3DResponseVO.setDescription("Transaction is pending");
                    }
                }else if("15".equalsIgnoreCase(transactionStatus)){
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setRemark("Transaction is pending");
                    comm3DResponseVO.setDescription("Transaction is pending");
                    if(functions.isValueNull(message) && message.contains("~"))
                    {
                        String[] threeDdate=message.split("~");
                        if(threeDdate.length==3)
                        {
                            comm3DResponseVO.setUrlFor3DRedirect(threeDdate[1]);
                            comm3DResponseVO.setMd(threeDdate[0]);
                            comm3DResponseVO.setPaReq(threeDdate[2]);
                        }else if(threeDdate.length==1)
                        {
                            comm3DResponseVO.setUrlFor3DRedirect(threeDdate[1]);
                        }
                    }
                    comm3DResponseVO.setTerURL(termUrl);

                }
                else{
                    comm3DResponseVO.setStatus("failed");
                    if(functions.isValueNull(message))
                    {
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setDescription(message);
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction Failed");
                        comm3DResponseVO.setDescription("Transaction Failed");
                    }

                }
                comm3DResponseVO.setTransactionId(requestId);
                comm3DResponseVO.setRrn(referenceNumber);
                comm3DResponseVO.setBankTransactionDate(transactionDateTime);
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"-->",e);
        }
        catch (EncodingException e)
        {
            transactionLogger.error("EncodingException---" + trackingID + "-->", e);
        }


        return comm3DResponseVO;
    }
    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("IlixiumPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String encodedPassword="";
        String transactionStatus="";
        String paymentRequestId="";
        String transactionDateTime="";
        String amount="";
        String code="";
        String message="";
        String statusCode="";
        String statusDescription="";
        if(functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        String privatePath="";
        String inquiryUrl="";
        if(isTest)
        {
            inquiryUrl = RB.getString("INQUIRY_TEST_URL");
            privatePath=RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            inquiryUrl = RB.getString("INQUIRY_LIVE_URL");
            privatePath=RB.getString("LIVE_PRIVATE_KEY");
        }
        try
        {
            String authenticationHash = AtCyberSourceUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(),privatePath);
            String request = "{" +
                    " \"merchantdetails\": {" +
                    " \"id\": \"" + mid + "\"," +
                    " \"password\": \"" + encodedPassword + "\"" +
                    " }," +
                    " \"paymentrequest\": {" +
                    " \"requestid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    " \"paymentswitch\": \"01\"" +
                    " }" +
                    "}";

            transactionLogger.error("processQuery Request ----" + trackingID + "-->" + request);
            String response = AtCyberSourceUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request,authenticationHash);
            transactionLogger.error("processQuery Response ----" + trackingID + "-->" + response);
            if (functions.isValueNull(response))
            {
                JSONObject inqueryResJSON = new JSONObject(response);
                if (inqueryResJSON.has("TransactionStatus"))
                    transactionStatus=inqueryResJSON.getString("TransactionStatus");
                if (inqueryResJSON.has("PaymentRequestId"))
                    paymentRequestId=inqueryResJSON.getString("PaymentRequestId");
                if (inqueryResJSON.has("TransactionDateTime"))
                    transactionDateTime=inqueryResJSON.getString("TransactionDateTime");
                if (inqueryResJSON.has("Amount"))
                    amount=inqueryResJSON.getString("Amount");
                if (inqueryResJSON.has("Message"))
                    message=inqueryResJSON.getString("Message");
                if (inqueryResJSON.has("Code"))
                    code=inqueryResJSON.getString("Code");
                if (inqueryResJSON.has("status"))
                {
                    if(inqueryResJSON.getJSONObject("status").has("statusCode"))
                        statusCode = inqueryResJSON.getJSONObject("status").getString("statusCode");
                    if(inqueryResJSON.getJSONObject("status").has("statusDescription"))
                        statusDescription = inqueryResJSON.getJSONObject("status").getString("statusDescription");
                }

                if("6".equalsIgnoreCase(transactionStatus) || "5".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("0".equalsIgnoreCase(transactionStatus) || "1".equalsIgnoreCase(transactionStatus) || "15".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("success");
                }else if("9".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("reversed");
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }
                if(functions.isValueNull(paymentRequestId))
                    commResponseVO.setTransactionId(paymentRequestId);
                if(functions.isValueNull(transactionDateTime))
                    commResponseVO.setBankTransactionDate(transactionDateTime);
                if(functions.isValueNull(message))
                {
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }else if(functions.isValueNull(statusDescription))
                {
                    commResponseVO.setRemark(statusDescription);
                    commResponseVO.setDescription(statusDescription);
                }
                commResponseVO.setAuthCode(code);
                commResponseVO.setAmount(amount);
                commResponseVO.setTransactionType("Sale");

            }
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"-->",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();
        String refundUrl="";
        String transactionStatus="";
        String paymentRequestId="";
        String message="";
        String referenceNumber="";
        String transactionDateTime="";
        String amount="";
        String privatePath="";
        if(isTest)
        {
            refundUrl = RB.getString("REFUND_TEST_URL");
            privatePath=RB.getString("TEST_PRIVATE_KEY");
        }
        else
        {
            refundUrl = RB.getString("REFUND_LIVE_URL");
            privatePath=RB.getString("LIVE_PRIVATE_KEY");
        }
        String encodedPassword="";
        if(functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        try
        {
            String authenticationHash = AtCyberSourceUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(),privatePath);
            String refundRequest = "{" +
                    " \"merchantdetails\": {" +
                    " \"id\": \"" + mid + "\"," +
                    " \"password\": \"" + encodedPassword + "\"" +
                    " }," +
                    " \"paymentrequest\": {" +
                    " \"requestid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    " \"paymentswitch\": \"01\"" +
                    " }" +
                    "}";
            transactionLogger.error("Refund request---"+trackingID+"--->"+refundRequest);
            String response = AtCyberSourceUtils.doPostHTTPSURLConnectionClient(refundUrl, refundRequest,authenticationHash);
            transactionLogger.error("Refund response--"+trackingID+"--->"+response);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("TransactionStatus"))
                    transactionStatus=responseJSON.getString("TransactionStatus");
                if(responseJSON.has("PaymentRequestId"))
                    paymentRequestId=responseJSON.getString("PaymentRequestId");
                if(responseJSON.has("Message"))
                    message=responseJSON.getString("Message");
                if(responseJSON.has("ReferenceNumber"))
                    referenceNumber=responseJSON.getString("ReferenceNumber");
                if(responseJSON.has("TransactionDateTime"))
                    transactionDateTime=responseJSON.getString("TransactionDateTime");
                if(responseJSON.has("Amount"))
                    amount=responseJSON.getString("Amount");
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);
                if("9".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    if(!functions.isValueNull(message))
                    {
                        commResponseVO.setRemark("Refund successfully completed");
                        commResponseVO.setDescription("Refund successful completed");
                    }
                }else
                {
                    commResponseVO.setStatus("failed");
                    if(!functions.isValueNull(message))
                    {
                        commResponseVO.setRemark("Refund failed");
                        commResponseVO.setDescription("Refund failed");
                    }
                }
                if(functions.isValueNull(amount))
                    commResponseVO.setAmount(amount);
                commResponseVO.setTransactionId(paymentRequestId);
                commResponseVO.setRrn(referenceNumber);

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"-->",e);
        }


        return commResponseVO;
    }
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        Comm3DRequestVO comm3DRequestVO = (Comm3DRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=comm3DRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=comm3DRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=comm3DRequestVO.getCardDetailsVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String PaRes = comm3DRequestVO.getPaRes();
        String MD = comm3DRequestVO.getMd();
        String rrn="";
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();

        String initUrl="";
        String privatePath="";
        String phone="";
        String email="";
        String street="";
        String zip="";
        String countryCode="";
        String city="";
        String state="";
        String transactionStatus="";
        String message="";
        String referenceNumber="";
        String transactionDateTime="";
        String responseAmount="";
        String paymentRequestId="";
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            phone=commAddressDetailsVO.getPhone();
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            email=commAddressDetailsVO.getEmail();
        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            street=commAddressDetailsVO.getStreet();
        if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            zip=commAddressDetailsVO.getZipCode();
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode=commAddressDetailsVO.getCountry();
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
            city=commAddressDetailsVO.getCity();
        if(functions.isValueNull(commAddressDetailsVO.getState()))
            state=commAddressDetailsVO.getState();
        String cardtype=commTransactionDetailsVO.getCardType();
        String paymentMethod=AtCyberSourceUtils.getPaymentMethod(cardtype);
        String firstname= new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        String lastname= new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        String name="";
        if(functions.isValueNull(firstname) && functions.isValueNull(lastname))
            name= firstname + "~" + lastname;
        else if(functions.isValueNull(firstname))
            name=firstname;
        else
            name=lastname;
        if(isTest)
        {
            initUrl=RB.getString("INIT_TEST_URL");
            privatePath=RB.getString("TEST_PRIVATE_KEY");
        }else{
            initUrl=RB.getString("INIT_LIVE_URL");
            privatePath=RB.getString("LIVE_PRIVATE_KEY");
        }
        try
        {
            rrn=AtCyberSourceUtils.getRrn(trackingID);
            String encodedPassword = "";
            if (functions.isValueNull(password))
                encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
            String authenticationHash = AtCyberSourceUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId() + commTransactionDetailsVO.getCurrency() + commTransactionDetailsVO.getAmount(), privatePath);
            transactionLogger.error("authenticationHash--->" + authenticationHash);
            StringBuffer request = new StringBuffer("");
            StringBuffer requestLog = new StringBuffer("");
            request.append("{" +
                    "\"merchantdetails\": {" +
                    "\"id\": \"" + mid + "\"," +
                    "\"password\": \"" + encodedPassword + "\"" +
                    "}," +
                    "\"paymentrequest\": {" +
                    "\"requestid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    "\"paymentordernumber\": \"" + trackingID + "\"," +
                    "\"paymentswitch\": \"01\"," +
                    "\"paymentmethod\": \"01\"," +
                    "\"command\": \"Debit\"," +
                    "\"currency\": \"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    "\"amount\": " + commTransactionDetailsVO.getAmount() + "," +
                    "\"mobilenumber\": \"" + phone + "\"," +
                    "\"otp\": \"" + rrn + "\"," +
                    "\"accountnumber\": \"" + commCardDetailsVO.getCardNum() + "\"," +
                    "\"cardexpirydatemonth\": \"" + commCardDetailsVO.getExpMonth() + "\"," +
                    "\"cardexpirydateyear\": \"" + commCardDetailsVO.getExpYear() + "\"," +
                    "\"cvv\": \"" + commCardDetailsVO.getcVV() + "\"," +
                    "\"customername\": null," +
                    "\"customeraddress\": null," +
                    "\"reasonforpayment\": \"00\"," +
                    "\"paymentdetails\": \"" + PaRes + "\"," +
                    "\"additionaltext1\": \"" + name + "\"," +
                    "\"additionaltext2\": \"" + street + "~" + city + "~" + state + "~" + zip + "~" + countryCode + "\"," +//address1~locality~administrativeArea~postalCode~country
                    "\"additionaltext3\": \"" + phone + "~" + email + "\"," +
                    "\"additionalamount1\": null," +
                    "\"additionalamount2\": null" +
                    "}" +
                    "}");
            requestLog.append("{" +
                    "\"merchantdetails\": {" +
                    "\"id\": \"" + mid + "\"," +
                    "\"password\": \"" + encodedPassword + "\"" +
                    "}," +
                    "\"paymentrequest\": {" +
                    "\"requestid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    "\"paymentordernumber\": \"" + trackingID + "\"," +
                    "\"paymentswitch\": \"01\"," +
                    "\"paymentmethod\": \"01\"," +
                    "\"command\": \"Debit\"," +
                    "\"currency\": \"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    "\"amount\": " + commTransactionDetailsVO.getAmount() + "," +
                    "\"mobilenumber\": \"" + phone + "\"," +
                    "\"otp\": \"" + rrn + "\"," +
                    "\"accountnumber\": \"" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "\"," +
                    "\"cardexpirydatemonth\": \"" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "\"," +
                    "\"cardexpirydateyear\": \"" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "\"," +
                    "\"cvv\": \"" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "\"," +
                    "\"customername\": null," +
                    "\"customeraddress\": null," +
                    "\"reasonforpayment\": \"00\"," +
                    "\"paymentdetails\": \"" + PaRes + "\"," +
                    "\"additionaltext1\": \"" + name + "\"," +
                    "\"additionaltext2\": \"" + street + "~" + city + "~" + state + "~" + zip + "~" + countryCode + "\"," +//address1~locality~administrativeArea~postalCode~country
                    "\"additionaltext3\": \"" + phone + "~" + email + "\"," +
                    "\"additionalamount1\": null," +
                    "\"additionalamount2\": null" +
                    "}" +
                    "}");
            transactionLogger.error("Init Sale Request---" + trackingID + "--->" + requestLog.toString());
            String process3DResponse = AtCyberSourceUtils.doPostHTTPSURLConnectionClient(initUrl, request.toString(), authenticationHash);
            transactionLogger.error("Init Sale Response---" + trackingID + "--->" + process3DResponse);

            if (functions.isValueNull(process3DResponse))
            {
                JSONObject responseJSON=new JSONObject(process3DResponse);
                if(responseJSON.has("TransactionStatus"))
                    transactionStatus=responseJSON.getString("TransactionStatus");
                if(responseJSON.has("Message"))
                    message=responseJSON.getString("Message");
                if(responseJSON.has("ReferenceNumber") && !responseJSON.isNull("ReferenceNumber"))
                    referenceNumber=responseJSON.getString("ReferenceNumber");
                if(responseJSON.has("TransactionDateTime"))
                    transactionDateTime=responseJSON.getString("TransactionDateTime");
                if(responseJSON.has("Amount"))
                    responseAmount=responseJSON.getString("Amount");


                if("6".equalsIgnoreCase(transactionStatus) || "5".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setAmount(String.format("%.2f",Double.parseDouble(responseAmount)));
                }else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }
                commResponseVO.setRrn(referenceNumber);
                commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---" + trackingID + "-->", e);
        }
        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

package com.payment.cellulant;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 11/27/2020.
 */
public class AtCellulantPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger=new TransactionLogger(AtCellulantPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="atcellulant";
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.atcellulant");
    public AtCellulantPaymentGateway(String accountId){this.accountId=accountId;}

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        ReserveField2VO reserveField2VO=commRequestVO.getReserveField2VO();
        Functions functions=new Functions();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String shortName=gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String encodedPassword="";
        if(functions.isValueNull(password))
            encodedPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(password.getBytes());
        long expirationMin=5;
        try{
            if(functions.isValueNull(shortName))
                expirationMin= (long) Double.parseDouble(shortName.trim());
        }catch (NumberFormatException e)
        {
            transactionLogger.error("NumberFormatException--"+trackingID+"-->",e);
        }
        Date date=new Date();
        long threadSleepTime=expirationMin*60*1000;
        long time=date.getTime()+(expirationMin*60*1000);
        date=new Date(time);
        String termUrl="";
        String phone="";
        String email="";
        String countryCode="";
        String transactionStatus="";
        String message="";
        String referenceNumber="";
        String checkoutRequestID="";
        String transactionDateTime="";
        String chargeTransactionStatus="";
        String chargeRequestID="";
        String chargeCode="";
        String chargePayments="";
        String chargeAmountPaid=null;
        String accountNumber=reserveField2VO.getAccountNumber();
        transactionLogger.error("accountNumber--->"+accountNumber);
        String cardType=GatewayAccountService.getCardType(commTransactionDetailsVO.getCardType());
        String paymentMode=GatewayAccountService.getPaymentMode(commTransactionDetailsVO.getPaymentType());

        String paymentmethod=AtCellulantUtils.getPaymentBrand(cardType);
        String paymentOption="";
        if("03".equalsIgnoreCase(paymentmethod))
            paymentOption="85";
        else if("04".equalsIgnoreCase(paymentmethod))
            paymentOption="119";
        transactionLogger.error("date--->"+date);
        String transactionExpiredDate=simpleDateFormat.format(date);
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            phone=commAddressDetailsVO.getPhone();
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
            countryCode=commAddressDetailsVO.getCountry();
        if(countryCode.length()==3)
            countryCode=AtCellulantUtils.getCountryTwoDigit(countryCode);
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            email=commAddressDetailsVO.getEmail();
        String firstname= new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);
        String lastname= new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);
        String name="";
        if(functions.isValueNull(firstname) && functions.isValueNull(lastname))
            name= firstname + "~" + lastname;
        else if(functions.isValueNull(firstname))
            name=firstname;
        else
            name=lastname;
        if("MB".equalsIgnoreCase(paymentMode))
            accountNumber=phone;
        if(functions.isValueNull(commMerchantVO.getHostUrl()))
            termUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
        else
            termUrl=RB.getString("TERM_URL");

        String initUrl="";
        String chargeUrl="";
        String privatePath="";
        if(isTest)
        {
            initUrl=RB.getString("INIT_TEST_URL");
            chargeUrl=RB.getString("CHARGE_TEST_URL");
            privatePath=RB.getString("TEST_PRIVATE_KEY");
        }else{
            initUrl=RB.getString("INIT_LIVE_URL");
            chargeUrl=RB.getString("CHARGE_LIVE_URL");
            privatePath=RB.getString("LIVE_PRIVATE_KEY");
        }

        try
        {
            String detailId=AtCellulantUtils.insertTransactionDetails(trackingID,accountNumber,cardType);
            UUID uuid1 = UUID.randomUUID();
            String requestId=uuid1.toString();
            transactionLogger.error("requestId--->"+requestId);
            String authenticationHash = AtCellulantUtils.getSignature(mid + requestId + commTransactionDetailsVO.getCurrency() + commTransactionDetailsVO.getAmount(),privatePath);
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
                    " \"paymentswitch\": \"10\"," +
                    " \"paymentmethod\": \""+paymentmethod+"\"," +
                    " \"command\": \"Debit\"," +
                    " \"currency\": \"" + commTransactionDetailsVO.getCurrency() + "\"," +
                    " \"amount\": " + commTransactionDetailsVO.getAmount() + "," +
                    " \"mobilenumber\": \"" + phone + "\"," +
                    " \"otp\": null," +
                    " \"accountnumber\": \"null\"," +
                    " \"cardexpirydatemonth\": \"null\"," +
                    " \"cardexpirydateyear\": \"null\"," +
                    " \"cvv\": \"null\"," +
                    " \"customername\": \"" + name + "\"," +
                    " \"customeraddress\": \"" + email + "\"," +
                    " \"reasonforpayment\": \"00\"," +
                    " \"paymentdetails\": \"" + commTransactionDetailsVO.getOrderDesc() + "\"," +
                    " \"additionaltext1\": \"" + countryCode + "\"," +
                    " \"additionaltext2\": \"" + transactionExpiredDate + "\"," +
                    " \"additionaltext3\": null," +
                    " \"additionalamount1\": null," +
                    " \"additionalamount2\": null" +
                    " }}");

            transactionLogger.error("Init Sale Request---" + trackingID + "--->" + request.toString());
            String initResponse = AtCellulantUtils.doPostHTTPSURLConnectionClient(initUrl, request.toString(), authenticationHash);
            transactionLogger.error("Init Sale Response---" + trackingID + "--->" + initResponse);
            if(functions.isValueNull(initResponse)){
                JSONObject initResponseJSON=new JSONObject(initResponse);
                if(initResponseJSON.has("TransactionStatus"))
                    transactionStatus=initResponseJSON.getString("TransactionStatus");
                if(initResponseJSON.has("Message"))
                    message=initResponseJSON.getString("Message");
                if(initResponseJSON.has("TransactionDateTime"))
                    transactionDateTime=initResponseJSON.getString("TransactionDateTime");
                if(initResponseJSON.has("ReferenceNumber"))
                    referenceNumber=initResponseJSON.getString("ReferenceNumber");

                if(initResponseJSON.has("results") && !initResponseJSON.isNull("results"))
                {
                    JSONObject results=initResponseJSON.getJSONObject("results");
                    if(results.has("checkoutRequestID"))
                        checkoutRequestID = results.getString("checkoutRequestID");
                }
                if("1".equalsIgnoreCase(transactionStatus))
                {
                    StringBuffer chargeRequest=new StringBuffer();
                    chargeRequest.append("{" +
                            " \"merchantdetails\": {" +
                            " \"id\": \""+mid+"\"," +
                            " \"password\": \""+encodedPassword+"\"" +
                            " }," +
                            " \"paymentrequest\": {" +
                            " \"requestid\": \""+requestId+"\"," +
                            " \"paymentordernumber\": \""+trackingID+"\"," +
                            " \"paymentswitch\": \"10\"," +
                            " \"paymentmethod\": \""+paymentmethod+"\"," +//03 EcoCash 04 TeleCash
                            " \"command\": \"Debit\"," +
                            " \"currency\": \""+commTransactionDetailsVO.getCurrency()+"\"," +
                            " \"amount\": "+commTransactionDetailsVO.getAmount()+"," +
                            " \"mobilenumber\": \""+phone+"\"," +
                            " \"otp\": null," +
                            " \"accountnumber\": \""+accountNumber+"\"," +//0785655096
                            " \"cardexpirydatemonth\": null," +
                            " \"cardexpirydateyear\": null," +
                            " \"cvv\": null," +
                            " \"customername\": null," +
                            " \"customeraddress\": null," +
                            " \"reasonforpayment\": \"00\"," +
                            " \"paymentdetails\": \""+commTransactionDetailsVO.getOrderDesc()+"\"," +
                            " \"additionaltext1\": \""+paymentOption+"\"," + //85 EcoCash 119 TeleCash
                            " \"additionaltext2\": \"en\"," +
                            " \"additionaltext3\": null," +
                            " \"additionalamount1\": null," +
                            " \"additionalamount2\": null" +
                            " }" +
                            "}");
                    StringBuffer chargeRequestLog=new StringBuffer();
                    chargeRequestLog.append("{" +
                            " \"merchantdetails\": {" +
                            " \"id\": \""+mid+"\"," +
                            " \"password\": \""+encodedPassword+"\"" +
                            " }," +
                            " \"paymentrequest\": {" +
                            " \"requestid\": \""+requestId+"\"," +
                            " \"paymentordernumber\": \""+trackingID+"\"," +
                            " \"paymentswitch\": \"10\"," +
                            " \"paymentmethod\": \""+paymentmethod+"\"," +//03 EcoCash 04 TeleCash
                            " \"command\": \"Debit\"," +
                            " \"currency\": \""+commTransactionDetailsVO.getCurrency()+"\"," +
                            " \"amount\": "+commTransactionDetailsVO.getAmount()+"," +
                            " \"mobilenumber\": \""+phone+"\"," +
                            " \"otp\": null," +
                            " \"accountnumber\": \""+functions.maskingNumber(accountNumber)+"\"," +//0785655096
                            " \"cardexpirydatemonth\": null," +
                            " \"cardexpirydateyear\": null," +
                            " \"cvv\": null," +
                            " \"customername\": null," +
                            " \"customeraddress\": null," +
                            " \"reasonforpayment\": \"00\"," +
                            " \"paymentdetails\": \""+commTransactionDetailsVO.getOrderDesc()+"\"," +
                            " \"additionaltext1\": \""+paymentOption+"\"," + //85 EcoCash 119 TeleCash
                            " \"additionaltext2\": \"en\"," +
                            " \"additionaltext3\": null," +
                            " \"additionalamount1\": null," +
                            " \"additionalamount2\": null" +
                            " }" +
                            "}");
                    transactionLogger.error("Charge Sale Request---" + trackingID + "--->" + chargeRequest.toString());
                    String chargeResponse = AtCellulantUtils.doPostHTTPSURLConnectionClient(chargeUrl, chargeRequest.toString(), authenticationHash);
                    transactionLogger.error("Charge Sale Response---" + trackingID + "--->" + chargeResponse);
                    if(functions.isValueNull(chargeResponse))
                    {
                        JSONObject chargeResponseJSON=new JSONObject(chargeResponse);
                        if(chargeResponseJSON.has("TransactionStatus"))
                            chargeTransactionStatus=chargeResponseJSON.getString("TransactionStatus");
                        if(chargeResponseJSON.has("Code"))
                            chargeCode=chargeResponseJSON.getString("Code");
                        if(chargeResponseJSON.has("Message"))
                            message=chargeResponseJSON.getString("Message");
                        if(chargeResponseJSON.has("TransactionDateTime"))
                            transactionDateTime=chargeResponseJSON.getString("TransactionDateTime");
                        if(chargeResponseJSON.has("results") && !chargeResponseJSON.isNull("results"))
                        {
                            JSONObject results=chargeResponseJSON.getJSONObject("results");
                            if(results.has("payments"))
                                chargePayments = results.getString("payments");
                            if(results.has("chargeRequestID"))
                                chargeRequestID = results.getString("chargeRequestID");
                            if(results.has("amountPaid"))
                                chargeAmountPaid = results.getString("amountPaid");
                        }
                        boolean isUpdate=AtCellulantUtils.updateTransactionDetails(detailId,checkoutRequestID,chargeRequestID,chargeAmountPaid);
                        transactionLogger.error("chargeTransactionStatus--->"+chargeTransactionStatus);
                        if("6".equalsIgnoreCase(chargeTransactionStatus) || "5".equalsIgnoreCase(chargeTransactionStatus))
                        {
                            comm3DResponseVO.setStatus("success");
                            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            comm3DResponseVO.setRemark(message);
                            comm3DResponseVO.setDescription(message);
                            comm3DResponseVO.setAmount(String.format("%.2f",Double.parseDouble(chargeAmountPaid)));
                        }else if("0".equalsIgnoreCase(chargeTransactionStatus) || "1".equalsIgnoreCase(chargeTransactionStatus)){
                            comm3DResponseVO.setStatus("pending");
                            comm3DResponseVO.setRemark("Transaction is pending");
                            comm3DResponseVO.setDescription("Transaction is pending");
                            Thread.sleep(threadSleepTime);
                            commTransactionDetailsVO.setPreviousTransactionId(requestId);
                            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            for (int i=0;i<5;i++)
                            {
                                comm3DResponseVO = (Comm3DResponseVO) processQuery(trackingID, commRequestVO);
                                if(comm3DResponseVO!=null && !"pending".equalsIgnoreCase(comm3DResponseVO.getTransactionStatus()))
                                    break;
                            }
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("failed");
                            if(functions.isValueNull(message))
                            {
                                comm3DResponseVO.setRemark(message);
                                comm3DResponseVO.setDescription(message);
                            }else{
                                comm3DResponseVO.setRemark("Transaction failed");
                                comm3DResponseVO.setDescription("Transaction failed");
                            }
                        }
                        comm3DResponseVO.setRrn(chargeRequestID);
                    }

                }/*else if("0".equalsIgnoreCase(transactionStatus)){
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
                }*/
                else{
                    boolean isUpdate=AtCellulantUtils.updateTransactionDetails(detailId,checkoutRequestID,"",null);
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
        catch (InterruptedException e)
        {
            transactionLogger.error("InterruptedException---" + trackingID + "-->", e);
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
            String authenticationHash = AtCellulantUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(), privatePath);
            String request = "{" +
                    " \"merchantdetails\": {" +
                    " \"id\": \"" + mid + "\"," +
                    " \"password\": \"" + encodedPassword + "\"" +
                    " }," +
                    " \"paymentrequest\": {" +
                    " \"requestid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    " \"paymentswitch\": \"10\"" +
                    " }" +
                    "}";

            transactionLogger.error("processQuery Request ----" + trackingID + "-->" + request);
            String response = AtCellulantUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request,authenticationHash);
            transactionLogger.error("processQuery Response ----" + trackingID + "-->" + response);
            if (functions.isValueNull(response))
            {
                JSONObject inquiryResJSON = new JSONObject(response);
                if (inquiryResJSON.has("TransactionStatus"))
                    transactionStatus=inquiryResJSON.getString("TransactionStatus");
                if (inquiryResJSON.has("PaymentRequestId"))
                    paymentRequestId=inquiryResJSON.getString("PaymentRequestId");
                if (inquiryResJSON.has("TransactionDateTime"))
                    transactionDateTime=inquiryResJSON.getString("TransactionDateTime");
                if (inquiryResJSON.has("Amount"))
                    amount=inquiryResJSON.getString("Amount");
                if (inquiryResJSON.has("Message"))
                    message=inquiryResJSON.getString("Message");
                if (inquiryResJSON.has("Code"))
                    code=inquiryResJSON.getString("Code");
                if (inquiryResJSON.has("status") && !inquiryResJSON.isNull("status"))
                {
                    if(inquiryResJSON.getJSONObject("status").has("statusCode"))
                        statusCode = inquiryResJSON.getJSONObject("status").getString("statusCode");
                    if(inquiryResJSON.getJSONObject("status").has("statusDescription"))
                        statusDescription = inquiryResJSON.getJSONObject("status").getString("statusDescription");
                }

                if("6".equalsIgnoreCase(transactionStatus) || "5".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    if(!functions.isValueNull(message))
                        message="Transaction Successful";
                }else if("0".equalsIgnoreCase(transactionStatus) || "1".equalsIgnoreCase(transactionStatus)|| "15".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }else if("9".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("reversed");
                }
                else if( "2".equalsIgnoreCase(transactionStatus) || "3".equalsIgnoreCase(transactionStatus) ||"4".equalsIgnoreCase(transactionStatus))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if(!functions.isValueNull(message))
                        message="Transaction Failed";
                }
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
            commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
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
            String authenticationHash = AtCellulantUtils.getSignature(mid + commTransactionDetailsVO.getPreviousTransactionId(),privatePath);
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
            String response = AtCellulantUtils.doPostHTTPSURLConnectionClient(refundUrl, refundRequest,authenticationHash);
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
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

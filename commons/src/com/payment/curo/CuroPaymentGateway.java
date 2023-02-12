package com.payment.curo;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;

import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by Admin on 1/6/2020.
 */
public class CuroPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "curo";
    private static TransactionLogger transactionLogger=new TransactionLogger(CuroPaymentGateway.class.getName());
    private static final ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.curo");
    Functions functions=new Functions();
    public CuroPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("--- Inside Curo process Sale ---");
        CommResponseVO commResponseVO=new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        String mid=gatewayAccount.getMerchantId();
        String saleUrl="";
        String termUrl="";
        String notifyUrl="";
        String gender="";
        String status="";
        String birthDate="";
        String amount="";
        String firstName="";
        String lastName="";
        String state="";

        try
        {
            if(functions.isValueNull(addressDetailsVO.getFirstname()) && functions.isValueNull(addressDetailsVO.getLastname()))
            {
                firstName = ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
                lastName = ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
                state = ESAPI.encoder().encodeForURL(addressDetailsVO.getState());
            }
        }
        catch (EncodingException e)
        {
            transactionLogger.error("Curo processSale --->",e);
        }
        int amt=Integer.parseInt(transDetailsVO.getAmount().replace(".", ""));
        amount=String.valueOf(amt);
        String paymentMethodEndPoint=CuroUtils.getPaymentMethodEndPoint(transDetailsVO.getCardType());

        if("male".equalsIgnoreCase(addressDetailsVO.getSex()))
            gender="M";
        else if("female".equalsIgnoreCase(addressDetailsVO.getSex()))
            gender="F";
        boolean isTest=gatewayAccount.isTest();
        if(isTest)
            saleUrl=RB.getString("TEST_SALE_URL")+paymentMethodEndPoint;
        else
            saleUrl=RB.getString("LIVE_SALE_URL")+paymentMethodEndPoint;

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            notifyUrl = RB.getString("NOTIFY_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            notifyUrl = RB.getString("NOTIFY_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }
        transactionLogger.error("Birth date---->"+addressDetailsVO.getBirthdate());
        if(functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                birthDate=dateFormat.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));
            }
            catch (ParseException e)
            {
                try
                {
                    birthDate=dateFormat.format(dateFormat2.parse(addressDetailsVO.getBirthdate()));
                }
                catch (ParseException e1)
                {
                   transactionLogger.error("Curo Gateway ParseException--->",e1);
                }
            }
        }
        String request="{" +
                "\"site_id\":\""+mid+"\"," +
                "\"url_success\":\""+termUrl+"&txnstatus=success\"," +
                "\"url_pending\":\""+termUrl+"&txnstatus=pending\"," +
                "\"url_failure\":\""+termUrl+"&txnstatus=failed\"," +
                "\"url_cancel\":\""+termUrl+"&txnstatus=cancel\"," +
                "\"url_callback\":\""+notifyUrl+"\"," +
                "\"reference\":\""+trackingID+"\"," +
                "\"amount\":\""+amount+"\"," +
                "\"currency_id\":\""+transDetailsVO.getCurrency()+"\"," +
                "\"description\":\""+transDetailsVO.getOrderDesc()+"\"," +
                "\"ip\":\""+addressDetailsVO.getCardHolderIpAddress()+"\"," +
                "\"country_id\":\""+addressDetailsVO.getCountry()+"\"," +
                "\"language_id\":\""+addressDetailsVO.getLanguage()+"\"," +
                "\"customer\":{" +
                "\"firstname\":\""+firstName+"\"," +
                "\"lastname\":\""+lastName+"\"," +
                "\"gender\":\""+gender+"\"," +
                "\"dob\":\""+birthDate+"\"," +
                "\"address\":\""+addressDetailsVO.getStreet()+"\"," +
                "\"city\":\""+addressDetailsVO.getCity()+"\"," +
                "\"state\":\""+state+"\"," +
                "\"zipcode\":\""+addressDetailsVO.getZipCode()+"\"," +
                "\"country_id\":\""+addressDetailsVO.getCountry()+"\"," +
                "\"phone\":\""+addressDetailsVO.getPhone()+"\"," +
                "\"email\":\""+addressDetailsVO.getEmail()+"\"}" +
                /*"\"ssn\":\"41563\"," +
                "\"cartitem\":{" +
                "\"sku\":\"415\"," +
                "\"name\":\"Test\"," +
                "\"price\":\"1.00\"," +
                "\"vat\":\"10\"}" +*/
                "}";
        transactionLogger.error("Curo Sale request---->"+request);
        String hash=CuroUtils.encodeBase64(gatewayAccount.getFRAUD_FTP_USERNAME()+":"+gatewayAccount.getFRAUD_FTP_PASSWORD());//AccountName + : + account key
        String response=CuroUtils.doHttpPostConnection(saleUrl,request,hash);
        transactionLogger.error("Curo sale response---->"+response);
        try
        {
            if (functions.isValueNull(response) && response.contains("{"))
            {

                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("success") && "true".equalsIgnoreCase(responseJSON.getString("success")))
                {
                    if(responseJSON.has("payment") && responseJSON.getJSONObject("payment").has("url"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setRedirectUrl(responseJSON.getJSONObject("payment").getString("url"));
                        commResponseVO.setTransactionId(responseJSON.getJSONObject("payment").getString("transaction"));
                    }
                }else if(responseJSON.has("success") && responseJSON.has("error") && "false".equalsIgnoreCase(responseJSON.getString("success")))
                {
                    System.out.println("Inside Failed");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setErrorCode(responseJSON.getJSONObject("error").getString("code"));
                    commResponseVO.setRemark(responseJSON.getJSONObject("error").getString("message"));
                    commResponseVO.setDescription(responseJSON.getJSONObject("error").getString("message"));
                    if(!functions.isValueNull(commResponseVO.getRemark()))
                        commResponseVO.setRemark("Transaction Declined");
                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException e--->",e);
        }

        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in CuroPaymentGateway ----");
        String html="";
        PaymentManager paymentManager = new PaymentManager();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = CuroUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if ("pending".equalsIgnoreCase(transRespDetails.getStatus()))
            {
                transactionLogger.debug("status -------------------"+transRespDetails.getStatus());
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                html = CuroUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                transactionLogger.error("Html in processAutoRedirect -------" + html);
            }
            else if ("failed".equalsIgnoreCase(transRespDetails.getStatus()))
            {
                html="failed";
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in CuroPaymentGateway ---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("error", e);
        }
        return html;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        String refundUrl = "";

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        if (isTest)
            refundUrl = RB.getString("TEST_REFUND_URL");
        else
            refundUrl = RB.getString("LIVE_REFUND_URL");
        transactionLogger.error("refundUrl---->" + refundUrl);
        String mid = gatewayAccount.getMerchantId();
        String amount = "";
        int amt=Integer.parseInt(transDetailsVO.getAmount().replace(".", ""));
        amount=String.valueOf(amt);
        String transaction_id = transDetailsVO.getPreviousTransactionId();
        String refund_description = transDetailsVO.getOrderDesc();
        try
        {
            String request = "{\"site_id\":\""+mid+"\"\"transaction_id\":\"" + transaction_id + "\",\"amount\":\"" + amount + "\",\"description\":\"" + refund_description + "\"}";
            transactionLogger.error("Curo refund request---->" + request);
            String hash = CuroUtils.encodeBase64(gatewayAccount.getFRAUD_FTP_USERNAME() + ":" + gatewayAccount.getFRAUD_FTP_PASSWORD());//AccountName + : + account key
            String response = CuroUtils.doHttpPostConnection(refundUrl, request, hash);
            transactionLogger.error("Curo refund response---->" + response);
            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject refundResponse = new JSONObject(response);
                if(refundResponse.has("success") && "failed".equalsIgnoreCase(refundResponse.getString("success")))
                {
                    commResponseVO.setStatus("failed");
                    if(refundResponse.has("refund"))
                    {
                        commResponseVO.setErrorCode(refundResponse.getJSONObject("refund").getString("code"));
                        commResponseVO.setRemark(refundResponse.getJSONObject("refund").getString("message"));
                        commResponseVO.setDescription(refundResponse.getJSONObject("refund").getString("message"));
                    }else
                    {
                        commResponseVO.setErrorCode(refundResponse.getString("code"));
                        commResponseVO.setRemark(refundResponse.getString("message"));
                        commResponseVO.setDescription(refundResponse.getString("message"));
                    }
                }else if(refundResponse.getJSONObject("refund").has("transaction_id"))
                {
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(refundResponse.getJSONObject("refund").getString("transaction_id"));
                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("Curo Refund JSONException------>",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processQuery:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();
        String enquiryUrl = "";
        if (isTest)
            enquiryUrl = RB.getString("TEST_ENQUIRY_URL")+commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        else
            enquiryUrl = RB.getString("LIVE_ENQUIRY_URL")+commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        try
        {
            String mid = gatewayAccount.getMerchantId();
            String inquiryCurrency ="";
            String inquiryAmount ="";
            String inquiryPaymentId ="";
            String inquiryStatus ="";
            String paymentTime ="";
            String inquiryMessage ="";
            String authCode ="";
            String request="" ;
            String hash = CuroUtils.encodeBase64(gatewayAccount.getFRAUD_FTP_USERNAME() + ":" + gatewayAccount.getFRAUD_FTP_PASSWORD());//AccountName + : + account key
            String response=CuroUtils.doHttpPostConnection(enquiryUrl, request,hash);
            transactionLogger.error("response---->"+response);
            if(functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject inquiryResponse=new JSONObject(response);
                if(inquiryResponse.has("success") && "true".equalsIgnoreCase(inquiryResponse.getString("success")) && inquiryResponse.has("transaction"))
                {
                    JSONObject transaction=inquiryResponse.getJSONObject("transaction");
                    if(transaction.has("merchant_id"))
                        mid=transaction.getString("merchant_id");
                    if(transaction.has("code"))
                        authCode=transaction.getString("code");
                    if(transaction.has("currency_id"))
                        inquiryCurrency=transaction.getString("currency_id");
                    if(transaction.has("amount"))
                    {
                        double amount= Double.parseDouble(transaction.getString("amount"));
                        inquiryAmount=String.format("%.2f",amount/100);
                    }
                    if(transaction.has("finished"))
                    {
                        paymentTime=transaction.getString("finished");
                    }
                    if(transaction.has("id"))
                    {
                        inquiryPaymentId=transaction.getString("id");
                    }
                    if(transaction.has("description"))
                    {
                        inquiryMessage=transaction.getString("description");
                    }
                    if("200".equalsIgnoreCase(authCode))
                    {
                        inquiryStatus="success";
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                    else if("308".equalsIgnoreCase(authCode) || "301".equalsIgnoreCase(authCode) || "309".equalsIgnoreCase(authCode) || "300".equalsIgnoreCase(authCode))
                    {
                        inquiryStatus="failed";
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }
            }
            commResponseVO.setAmount(inquiryAmount);
            commResponseVO.setBankTransactionDate(paymentTime);
            commResponseVO.setCurrency(inquiryCurrency);
            commResponseVO.setDescription(inquiryMessage);
            commResponseVO.setResponseTime(paymentTime);
            commResponseVO.setTransactionId(inquiryPaymentId);
            commResponseVO.setTransactionStatus(inquiryStatus);
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setAuthCode(authCode);
        }
        catch (JSONException e)
        {
            transactionLogger.error("Curo inquiry JSONException--->",e);
        }
        return commResponseVO;
    }
    public String getMaxWaitDays()
    {
        return null;
    }
}

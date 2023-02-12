package com.payment.ilixium;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
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
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Vivek on 3/30/2020.
 */
public class IlixiumPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "ilixium";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ilixium");
    private static TransactionLogger transactionLogger = new TransactionLogger(IlixiumPaymentGateway.class.getName());
    private static Logger log = new Logger(IlixiumPaymentGateway.class.getName());
    private static IlixiumUtils ilixiumUtils=new IlixiumUtils();

    public IlixiumPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String username = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String termUrl = "";
        String notifyUrl = "";
        String saleUrl = "";
        String amount="";
        String dateOfBirth="";
        String firstName=addressDetailsVO.getFirstname();
        String lastName=addressDetailsVO.getLastname();
        String street=addressDetailsVO.getStreet();
        boolean isTest = gatewayAccount.isTest();

        if(functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2=new SimpleDateFormat("ddMMyyyy");
            try
            {
                if (!addressDetailsVO.getBirthdate().contains("-"))
                {
                    dateOfBirth = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));
                }
                else
                {
                    dateOfBirth = dateFormat2.format(dateFormat.parse(addressDetailsVO.getBirthdate()));
                }
            }catch (ParseException e)
            {
                transactionLogger.error("Parse Execption-->",e);
            }
        }
        if (isTest)
        {
            saleUrl = RB.getString("TEST_AUTH_URL");
        }
        else
        {
            saleUrl = RB.getString("LIVE_AUTH_URL");
        }
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }


        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount=IlixiumUtils.getJPYAmount(transDetailsVO.getAmount());
        else
            amount=IlixiumUtils.getCentAmount(transDetailsVO.getAmount());

        StringBuffer requestLog = new StringBuffer("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
        requestLog.append("<authRequest>" +
                /*" <deferredCapture>false</deferredCapture>" +*/
                " <version>2</version>" +
                " <transaction>" +
                " <transactionType>ECOMMERCE</transactionType>" +
                " <merchantRef>"+trackingID+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <paymentMethodType>CARD</paymentMethodType>" +
                " <merchant>" +
                " <merchantId>"+mId+"</merchantId>" +
                " <accountId>"+username+"</accountId>" +
                " </merchant>" +
                "<card>" +
                "<cardNumber>"+functions.maskingPan(cardDetailsVO.getCardNum())+"</cardNumber>" +
                "<securityCode>"+functions.maskingNumber(cardDetailsVO.getcVV())+"</securityCode>" +
                "<expiryDate>"+functions.maskingNumber(cardDetailsVO.getExpMonth())+functions.maskingNumber(cardDetailsVO.getExpYear())+"</expiryDate>" +
                "</card>");
        requestLog.append("<customer>");
        if(functions.isValueNull(addressDetailsVO.getCustomerid()))
            requestLog.append("<customerId>"+addressDetailsVO.getCustomerid()+"</customerId>");
        else
            requestLog.append("<customerId>"+addressDetailsVO.getEmail()+"</customerId>");
        if(functions.isValueNull(addressDetailsVO.getEmail()))
            requestLog.append("<email>"+addressDetailsVO.getEmail()+"</email>");
        if (functions.isValueNull(firstName) && functions.isValueNull(lastName))
        {
            requestLog.append(" <firstName>" + StringEscapeUtils.escapeXml(firstName) + "</firstName>" +
                    " <surname>" + StringEscapeUtils.escapeXml(lastName) + "</surname>");
        }
        requestLog.append(" <dateOfBirth>"+dateOfBirth+"</dateOfBirth>");
        if(functions.isValueNull(addressDetailsVO.getPhone()))
            requestLog.append(" <mobileNumber>"+addressDetailsVO.getPhone()+"</mobileNumber>");
        requestLog.append(" <address>");
        if(functions.isValueNull(street))
            requestLog.append(" <addressLine1>"+StringEscapeUtils.escapeXml(street)+"</addressLine1>");
        if(functions.isValueNull(addressDetailsVO.getCity()))
            requestLog.append(" <city>"+addressDetailsVO.getCity()+"</city>");
        if(functions.isValueNull(addressDetailsVO.getState()))
            requestLog.append(" <province>"+addressDetailsVO.getState()+"</province>");
        if(functions.isValueNull(addressDetailsVO.getZipCode()))
            requestLog.append(" <postcode>"+addressDetailsVO.getZipCode()+"</postcode>");
        if(functions.isValueNull(addressDetailsVO.getCountry()))
            requestLog.append(" <country>"+addressDetailsVO.getCountry()+"</country>");
        requestLog.append(" </address></customer>");
        //request.append(" <paymentInfo> <country>IN</country> </paymentInfo>");
                /*request.append(" <url>" +
                " <notificationUrl>"+notifyUrl+"</notificationUrl>" +
                " </url>");*/
        requestLog.append("</authRequest>");

        StringBuffer request = new StringBuffer("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
                request.append("<authRequest>" +
                /*" <deferredCapture>false</deferredCapture>" +*/
                " <version>2</version>" +
                " <transaction>" +
                " <transactionType>ECOMMERCE</transactionType>" +
                " <merchantRef>"+trackingID+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <paymentMethodType>CARD</paymentMethodType>" +
                " <merchant>" +
                " <merchantId>"+mId+"</merchantId>" +
                " <accountId>"+username+"</accountId>" +
                " </merchant>" +
                "<card>" +
                "<cardNumber>"+cardDetailsVO.getCardNum()+"</cardNumber>" +
                "<securityCode>"+cardDetailsVO.getcVV()+"</securityCode>" +
                "<expiryDate>"+cardDetailsVO.getExpMonth()+cardDetailsVO.getExpYear()+"</expiryDate>" +
                "</card>");
                request.append("<customer>");
                if(functions.isValueNull(addressDetailsVO.getCustomerid()))
                    request.append("<customerId>"+addressDetailsVO.getCustomerid()+"</customerId>");
                else
                    request.append("<customerId>"+addressDetailsVO.getEmail()+"</customerId>");
                if(functions.isValueNull(addressDetailsVO.getEmail()))
                    request.append("<email>"+addressDetailsVO.getEmail()+"</email>");
                if (functions.isValueNull(firstName) && functions.isValueNull(lastName))
                {
                    request.append(" <firstName>" + StringEscapeUtils.escapeXml(firstName) + "</firstName>" +
                            " <surname>" + StringEscapeUtils.escapeXml(lastName) + "</surname>");
                }
                request.append(" <dateOfBirth>"+dateOfBirth+"</dateOfBirth>");
                if(functions.isValueNull(addressDetailsVO.getPhone()))
                    request.append(" <mobileNumber>"+addressDetailsVO.getPhone()+"</mobileNumber>");
                request.append(" <address>");
                if(functions.isValueNull(street))
                    request.append(" <addressLine1>"+StringEscapeUtils.escapeXml(street)+"</addressLine1>");
                if(functions.isValueNull(addressDetailsVO.getCity()))
                    request.append(" <city>"+addressDetailsVO.getCity()+"</city>");
                if(functions.isValueNull(addressDetailsVO.getState()))
                    request.append(" <province>"+addressDetailsVO.getState()+"</province>");
                if(functions.isValueNull(addressDetailsVO.getZipCode()))
                    request.append(" <postcode>"+addressDetailsVO.getZipCode()+"</postcode>");
                if(functions.isValueNull(addressDetailsVO.getCountry()))
                    request.append(" <country>"+addressDetailsVO.getCountry()+"</country>");
                request.append(" </address></customer>");
                //request.append(" <paymentInfo> <country>IN</country> </paymentInfo>");
                /*request.append(" <url>" +
                " <notificationUrl>"+notifyUrl+"</notificationUrl>" +
                " </url>");*/
            request.append("</authRequest>");


            String encode = IlixiumUtils.sha512(request.toString());
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            transactionLogger.error("Sale request--for--" + trackingID + "--" + requestLog);
            String response = IlixiumUtils.doPostHTTPSURLConnectionClient(saleUrl, request.toString(), encode2);
            transactionLogger.error("Sale response--for--" + trackingID + "--" + response);
        if(functions.isValueNull(response))
        {
            Map<String,String> responseMap=ilixiumUtils.readSoapResponse(response);
                if ("SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("reason"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setResponseTime(responseMap.get("timestamp"));

                }else if("PENDING".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                    commResponseVO.setUrlFor3DRedirect(responseMap.get("threeDSecureAcsUrl"));
                    commResponseVO.setMd(responseMap.get("threeDSecureMd"));
                    commResponseVO.setPaReq(responseMap.get("threeDSecurePaReq"));
                    commResponseVO.setTerURL(termUrl);
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("reason"));
                    commResponseVO.setTransactionType(responseMap.get("type"));
                    commResponseVO.setResponseTime(responseMap.get("timestamp"));
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setErrorCode(responseMap.get("reason"));
                    commResponseVO.setResponseTime(responseMap.get("timestamp"));
                }

            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
            return commResponseVO;

    }
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        Comm3DRequestVO comm3DRequestVO=(Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String PaRes=comm3DRequestVO.getPaRes();
        String MD=comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String username=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String authConfirmationResponse="";


        try{

            String authConfirmation="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                    "<threeDSecureCompleteRequest>" +
                    " <version>2</version>" +
                    " <merchant>" +
                    " <merchantId>"+mid+"</merchantId>" +
                    " <accountId>"+username+"</accountId>" +
                    " </merchant>" +
                    " <transaction>" +
                    " <merchantRef>"+trackingID+"</merchantRef>" +
                    " </transaction>" +
                    " <threeDSecure>" +
                    "<md>"+MD+"</md>" +
                    "<paRes>"+PaRes+"</paRes>" +
                    " </threeDSecure>" +
                    "</threeDSecureCompleteRequest>";

            transactionLogger.error("-----3D Sale Request--for--" + trackingID + "--"+authConfirmation);
            String encode = IlixiumUtils.sha512(authConfirmation);
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            if (isTest)
            {
                authConfirmationResponse= IlixiumUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_3DS_URL"), authConfirmation,encode2);
            }
            else
            {
                authConfirmationResponse= IlixiumUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_3DS_URL"), authConfirmation,encode2);
            }

            transactionLogger.error("-----3D Sale Response--for--" + trackingID + "--" + authConfirmationResponse);

            if(functions.isValueNull(authConfirmationResponse)){
                Map<String,String> responseMap=ilixiumUtils.readSoapResponse(authConfirmationResponse);
                if("SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setBankTransactionDate(responseMap.get("timestamp"));
                    commResponseVO.setErrorCode(responseMap.get("reason"));
                    commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                    commResponseVO.setCurrency(responseMap.get("currency"));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setResponseTime(responseMap.get("timestamp"));
                    commResponseVO.setResponseHashInfo(responseMap.get("authCode"));
                }else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(responseMap.get("message"));
                    commResponseVO.setDescription(responseMap.get("message"));
                    commResponseVO.setBankTransactionDate(responseMap.get("timestamp"));
                    commResponseVO.setErrorCode(responseMap.get("reason"));
                    commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                    commResponseVO.setResponseTime(responseMap.get("timestamp"));
                }
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
            commResponseVO.setTmpl_Amount(comm3DRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(comm3DRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("processCommon3DSaleConfirmation Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processCapture ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount="";
        String captureUrl="";

        if(isTest)
        {
            captureUrl=RB.getString("TEST_CAPTURE_URL");
        }else
        {
            captureUrl=RB.getString("LIVE_CAPTURE_URL");
        }
        if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount=IlixiumUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
        else
            amount=IlixiumUtils.getCentAmount(commTransactionDetailsVO.getAmount());
        String request="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<captureRequest>" +
                " <version>2</version>" +
                " <transaction>" +
                " <merchantRef>"+trackingID+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <merchant>" +
                " <merchantId>"+mid+"</merchantId>" +
                " <accountId>"+userName+"</accountId>" +
                " </merchant>" +
                "</captureRequest>";
        String encode = IlixiumUtils.sha512(request);
        String concatEncode = encode + password;
        String encode2 = IlixiumUtils.sha512(concatEncode);

        try
        {
            transactionLogger.error("Capture request--for--" + trackingID + "--"+request);
            String response=IlixiumUtils.doPostHTTPSURLConnectionClient(captureUrl,request,encode2);
            transactionLogger.error("Capture response--for--" + trackingID + "--"+response);
            if(functions.isValueNull(response))
            {
                Map<String,String> responseMap=ilixiumUtils.readSoapResponse(response);
                if ( "SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                commResponseVO.setRemark(responseMap.get("message"));
                commResponseVO.setDescription(responseMap.get("message"));
                commResponseVO.setTransactionType(responseMap.get("type"));
                commResponseVO.setErrorCode(responseMap.get("reason"));
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription("Transaction Declined");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("Capture PZTechnicalViolationException--for--" + trackingID + "--", e);
        }


        return commResponseVO;
    }
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processVoid ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount="";
        String cancelUrl="";

        if(isTest)
        {
            cancelUrl=RB.getString("TEST_CANCEL_URL");
        }else
        {
            cancelUrl=RB.getString("LIVE_CANCEL_URL");
        }
        if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount=IlixiumUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
        else
            amount=IlixiumUtils.getCentAmount(commTransactionDetailsVO.getAmount());

        String request="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<reversalRequest>" +
                " <version>2</version>" +
                " <transaction>" +
                " <merchantRef>"+trackingID+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <merchant>" +
                " <merchantId>"+mid+"</merchantId>" +
                " <accountId>"+userName+"</accountId>" +
                " </merchant>" +
                "</reversalRequest>";
        try
        {
            String encode = IlixiumUtils.sha512(request);
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            transactionLogger.error("Cancel request--for--" + trackingID + "--" + request);
            String response = IlixiumUtils.doPostHTTPSURLConnectionClient(cancelUrl, request, encode2);
            transactionLogger.error("Cancel response--for--" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                Map<String,String> responseMap=ilixiumUtils.readSoapResponse(response);
                if ("SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                commResponseVO.setRemark(responseMap.get("message"));
                commResponseVO.setDescription(responseMap.get("message"));
                commResponseVO.setTransactionType(responseMap.get("type"));
                commResponseVO.setErrorCode(responseMap.get("reason"));
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processVoid PZTechnicalViolationException e--for--" + trackingID + "--",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount="";
        String refundUrl="";

        if(isTest)
        {
            refundUrl=RB.getString("TEST_REFUND_URL");
        }else
        {
            refundUrl=RB.getString("LIVE_REFUND_URL");
        }
        if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount=IlixiumUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
        else
            amount=IlixiumUtils.getCentAmount(commTransactionDetailsVO.getAmount());

        String request="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<refundRequest>" +
                " <version>2</version>" +
                " <transaction>" +
                " <merchantRef>"+trackingID+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <merchant>" +
                " <merchantId>"+mid+"</merchantId>" +
                " <accountId>"+userName+"</accountId>" +
                " </merchant>" +
                "</refundRequest>";
        try
        {
            String encode = IlixiumUtils.sha512(request);
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            transactionLogger.error("refund request--for--" + trackingID + "--" + request);
            String response = IlixiumUtils.doPostHTTPSURLConnectionClient(refundUrl, request, encode2);
            transactionLogger.error("refund response--for--" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                Map<String,String> responseMap=ilixiumUtils.readSoapResponse(response);
                if ("SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                commResponseVO.setRemark(responseMap.get("message"));
                commResponseVO.setDescription(responseMap.get("message"));
                commResponseVO.setTransactionType(responseMap.get("type"));
                commResponseVO.setErrorCode(responseMap.get("reason"));
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processRefund PZTechnicalViolationException e--for--" + trackingID + "--",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String inquiryUrl="";
        String startDate="";
        String endDate="";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try{
            startDate=simpleDateFormat.format(simpleDateFormat1.parse(commTransactionDetailsVO.getResponsetime()).getTime());
            endDate=simpleDateFormat.format(simpleDateFormat1.parse(commTransactionDetailsVO.getResponsetime()).getTime() + 1000);
        }
        catch (ParseException e)
        {
            transactionLogger.error("ParseException e--->",e);
        }

        if(isTest)
        {
            inquiryUrl=RB.getString("TEST_INQUIRY_URL");
        }else
        {
            inquiryUrl=RB.getString("LIVE_INQUIRY_URL");
        }

        String request="<?xml version='1.0' encoding='UTF-8' ?>" +
                "<historyRequest>" +
                " <merchant>" +
                " <merchantId>"+mid+"</merchantId>" +
                " <accountId>"+userName+"</accountId>" +
                "</merchant>" +
                " <periodStartDate>"+startDate+"</periodStartDate>" +
                " <periodEndDate>"+endDate+"</periodEndDate>" +
                " <reportFormat>XML</reportFormat>" +
                "</historyRequest>";
        try
        {
            String encode = IlixiumUtils.sha512(request);
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            transactionLogger.error("inquiry request--for--" + trackingID + "--" + request);
            String response = IlixiumUtils.doPostHTTPSURLConnectionClient(inquiryUrl, request, encode2);
            transactionLogger.error("inquiry response--for--" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                Map<String,Map<String,String>> responseMap=ilixiumUtils.readSoapResponseGorInquiry(response);
                if(responseMap!=null)
                {
                    commResponseVO.setStatus("success");
                    Map<String,String> innerResponseMap=responseMap.get(trackingID);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                    commResponseVO.setRemark(innerResponseMap.get("message"));
                    commResponseVO.setDescription(innerResponseMap.get("message"));
                    commResponseVO.setTransactionStatus(innerResponseMap.get("code"));
                    commResponseVO.setTransactionId(innerResponseMap.get("gatewayRef"));
                    commResponseVO.setBankTransactionDate(innerResponseMap.get("timestamp"));
                    commResponseVO.setTransactionType(innerResponseMap.get("type"));
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Declined");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processQuery PZTechnicalViolationException e--for--" + trackingID + "--",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("--- Inside IlixiumPaymentGateway Payout ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String amount="";
        String payoutUrl="";
        String paymentid =commTransactionDetailsVO.getPaymentId();
        //String transactionTrackingId=ilixiumUtils.getTransactionid(paymentid);
        String transactionTrackingId=commTransactionDetailsVO.getPreviousTransactionId();

        transactionLogger.error("transactionTrackingId-------------------------------------->"+transactionTrackingId);
        transactionLogger.error("paymentid-------------------------------------->"+paymentid);
        if(isTest)
        {
            payoutUrl=RB.getString("TEST_CREDIT_URL");
        }else
        {
            payoutUrl=RB.getString("LIVE_CREDIT_URL");
        }

        transactionLogger.error(" url-------------------------------------->"+payoutUrl);
        transactionLogger.error("istest-------------------------------------->"+isTest);
        if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
            amount=IlixiumUtils.getJPYAmount(commTransactionDetailsVO.getAmount());
        else
            amount=IlixiumUtils.getCentAmount(commTransactionDetailsVO.getAmount());

        StringBuffer request=new StringBuffer("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" +
                "<creditRequest>" +
                " <version>2</version>" +
                " <transaction>" +
                " <merchantRef>"+transactionTrackingId+"</merchantRef>" +
                " <amount>"+amount+"</amount>" +
                " <currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</currency>" +
                " </transaction>" +
                " <merchant>" +
                " <merchantId>"+mid+"</merchantId>" +
                " <accountId>"+userName+"</accountId>" +
                " </merchant>");

        request.append("<customer>");
        if(functions.isValueNull(commAddressDetailsVO.getCustomerid()))
            request.append("<customerId>"+commAddressDetailsVO.getCustomerid()+"</customerId>");
        else
        {
            request.append("<customerId>" + commAddressDetailsVO.getEmail() + "</customerId>");
        }

        request.append( " </customer>");
        request.append("</creditRequest>");
        try
        {
            String encode = IlixiumUtils.sha512(request.toString());
            String concatEncode = encode + password;
            String encode2 = IlixiumUtils.sha512(concatEncode);
            transactionLogger.error("payout request--for--" + trackingID + "--" + request);
            String response = IlixiumUtils.doPostHTTPSURLConnectionClient(payoutUrl, request.toString(), encode2);
            transactionLogger.error("payout response--for--" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                Map<String,String> responseMap=ilixiumUtils.readSoapResponse(response);
                if ("SUCCESS".equalsIgnoreCase(responseMap.get("status")))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(responseMap.get("gatewayRef"));
                commResponseVO.setRemark(responseMap.get("message"));
                commResponseVO.setDescription(responseMap.get("message"));
                commResponseVO.setTransactionType(responseMap.get("type"));
                commResponseVO.setErrorCode(responseMap.get("reason"));
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("processPayout PZTechnicalViolationException e--for--" + trackingID + "--",e);
        }
        return  commResponseVO;
    }
        @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

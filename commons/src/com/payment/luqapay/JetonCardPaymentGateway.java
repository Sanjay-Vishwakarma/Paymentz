package com.payment.luqapay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.ecomprocessing.ECPUtils;
import com.payment.emerchantpay.EMerchantPayUtils;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZConstraint;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Vivek on 2/20/2020.
 */
public class JetonCardPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE                 = "jetoncard";
    private static TransactionLogger transactionLogger      = new TransactionLogger(JetonCardPaymentGateway.class.getName());
    private static final ResourceBundle RB                  = LoadProperties.getProperty("com.directi.pg.jetoncard");

    public JetonCardPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO=commRequestVO.getCommDeviceDetailsVO();
        Functions functions=new Functions();
        SimpleDateFormat format=new SimpleDateFormat("yyyy");
        SimpleDateFormat format2=new SimpleDateFormat("yy");

        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        String is3dSupported= gatewayAccount.get_3DSupportAccount();

        String initUrl="";
        String saleUrl="";

        if(isTest)
        {
            initUrl = RB.getString("INIT_TEST_URL");
            saleUrl= RB.getString("SALE_TEST_URL");
        }
        else
        {
            initUrl = RB.getString("INIT_LIVE_URL");
            saleUrl= RB.getString("SALE_LIVE_URL");
        }

        String amount=commTransactionDetailsVO.getAmount();
        String country="";
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
        {
            country = commAddressDetailsVO.getCountry();
            transactionLogger.error("country.length()--->"+country.length());
            if(country.length()==3)
                country= JetonCardUtils.getCountryTwoDigit(country);
        }else
            country="TR";
        String currency=commTransactionDetailsVO.getCurrency();
        String dateOfBirth="";
        String defaultPaymentMethod="CREDIT_CARD";
        String email=commAddressDetailsVO.getEmail();
        String firstName=commAddressDetailsVO.getFirstname();
        String lastName=commAddressDetailsVO.getLastname();
        String address=commAddressDetailsVO.getStreet();
        String city=commAddressDetailsVO.getCity();
        if(!functions.isValueNull(city))
            city="ANKARA";
        String postCode=commAddressDetailsVO.getZipCode();
        if(!functions.isValueNull(postCode))
            postCode="06210";
        String language=commAddressDetailsVO.getLanguage();
        String cardNumber=commCardDetailsVO.getCardNum();
        String expDate="";
        if(!functions.isValueNull(address))
            address="25 MART MAH. DEMETEVLER";

        if(functions.isValueNull(commAddressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                if (!commAddressDetailsVO.getBirthdate().contains("-"))
                {
                    dateOfBirth = dateFormat2.format(dateFormat1.parse(commAddressDetailsVO.getBirthdate()));
                }
                else
                {
                    dateOfBirth = commAddressDetailsVO.getBirthdate();
                }
            }catch (ParseException e)
            {
                dateOfBirth = commAddressDetailsVO.getBirthdate();
                transactionLogger.error("Parse Execption-->for "+trackingID+ "---" ,e);
            }
        }else
        {
            dateOfBirth="1986-05-22";
        }
        try
        {
            expDate=commCardDetailsVO.getExpMonth()+"-"+format2.format(format.parse(commCardDetailsVO.getExpYear()));
        }
        catch (ParseException e)
        {
            transactionLogger.error("ParseException --->for "+trackingID+ "---",e);
            expDate=commCardDetailsVO.getExpMonth()+"-"+commCardDetailsVO.getExpYear().substring(commCardDetailsVO.getExpYear().length()-2,commCardDetailsVO.getExpYear().length());
        }
        if(functions.isValueNull(language))
        {
            if ("ENG".equalsIgnoreCase(language.toUpperCase()) || "EN".equalsIgnoreCase(language.toUpperCase()))
            {
                language = "EN";
            }
            else if("TUR".equalsIgnoreCase(language.toUpperCase()) || "TR".equalsIgnoreCase(language.toUpperCase()))
            {
                language="TR";
            }else {
                language="";
            }
        }
        String pin=commCardDetailsVO.getcVV();
        String customerIp=commAddressDetailsVO.getCardHolderIpAddress();
        String userAgent=commDeviceDetailsVO.getUser_Agent();
        String is3d="";
        String only3d="";
        String failRedirectUrl="";
        String successRedirectUrl="";
        String responseCode="";
        String responseMessage="";
        String responseStatus="";
        String reference="";
        String type="";
        String redirectUrl="";
        String token="";
        String transactionId="";
        String saleTransactionId="";

        try
        {
            address= URLEncoder.encode(address,"UTF-8");
            firstName= URLEncoder.encode(firstName,"UTF-8");
            lastName= URLEncoder.encode(lastName,"UTF-8");
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                failRedirectUrl ="https://"+commMerchantVO.getHostUrl()+ RB.getString("HOST_URL") + trackingID + "&status=failed";
                successRedirectUrl ="https://"+commMerchantVO.getHostUrl()+ RB.getString("HOST_URL") + trackingID + "&status=success";
            }
            else
            {
                failRedirectUrl = RB.getString("TERM_URL") + trackingID + "&status=failed";
                successRedirectUrl = RB.getString("TERM_URL") + trackingID + "&status=success";
            }

            if ("O".equalsIgnoreCase(is3dSupported))
            {
                is3d = "false";
                only3d = "true";
            }
            else if ("Y".equalsIgnoreCase(is3dSupported))
            {
                is3d = "true";
                only3d = "false";
            }
            else
            {
                is3d = "false";
                only3d = "false";
            }

            String initRequestLog = "{" +
                    "\"apiKey\": \"" + mid + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    "\"country\": \"" + country + "\"," +
                    "\"defaultPaymentMethod\": \"" + defaultPaymentMethod + "\"," +
                    "\"failRedirectUrl\": \"" + failRedirectUrl + "\"," +
                    "\"successRedirectUrl\": \"" + successRedirectUrl + "\"," +
                    "\"email\": \"" + email + "\"," +
                    "\"firstName\": \"" + firstName + "\"," +
                    "\"lastName\": \"" + lastName + "\"," +
                    "\"referenceNo\": \"" + trackingID + "\"," +
                    "\"cardNumber\": \"" + functions.maskingPan(cardNumber) + "\"," +
                    "\"expDate\": \"" + functions.maskingExpiry(expDate) + "\"," +
                    "\"pin\": \"" + functions.maskingNumber(pin) + "\"," +
                    "\"is3d\": \"" + is3d + "\"," +
                    "\"only3d\": \"" + only3d + "\"";
            if(functions.isValueNull(address))
                initRequestLog+=",\"address\": \"" + address + "\"";
            if(functions.isValueNull(city))
                initRequestLog+=",\"city\": \"" + city + "\"" ;
            if(functions.isValueNull(postCode))
                initRequestLog+=",\"postCode\": \"" + postCode + "\"" ;
            if(functions.isValueNull(dateOfBirth))
                initRequestLog+=",\"dateOfBirth\": \"" + dateOfBirth + "\"";
            if(functions.isValueNull(language))
                initRequestLog+=",\"language\": \"" + language + "\"" ;

            initRequestLog+="}";



            String initRequest = "{" +
                    "\"apiKey\": \"" + mid + "\"," +
                    "\"amount\": \"" + amount + "\"," +
                    "\"currency\": \"" + currency + "\"," +
                    "\"country\": \"" + country + "\"," +
                    "\"defaultPaymentMethod\": \"" + defaultPaymentMethod + "\"," +
                    "\"failRedirectUrl\": \"" + failRedirectUrl + "\"," +
                    "\"successRedirectUrl\": \"" + successRedirectUrl + "\"," +
                    "\"email\": \"" + email + "\"," +
                    "\"firstName\": \"" + firstName + "\"," +
                    "\"lastName\": \"" + lastName + "\"," +
                    "\"referenceNo\": \"" + trackingID + "\"," +
                    "\"cardNumber\": \"" + cardNumber + "\"," +
                    "\"expDate\": \"" + expDate + "\"," +
                    "\"pin\": \"" + pin + "\"," +
                    "\"is3d\": \"" + is3d + "\"," +
                    "\"only3d\": \"" + only3d + "\"";
                if(functions.isValueNull(address))
                    initRequest+=",\"address\": \"" + address + "\"";
                if(functions.isValueNull(city))
                    initRequest+=",\"city\": \"" + city + "\"" ;
                if(functions.isValueNull(postCode))
                    initRequest+=",\"postCode\": \"" + postCode + "\"" ;
                if(functions.isValueNull(dateOfBirth))
                    initRequest+=",\"dateOfBirth\": \"" + dateOfBirth + "\"";
                if(functions.isValueNull(language))
                    initRequest+=",\"language\": \"" + language + "\"" ;

            initRequest+="}";
            transactionLogger.error("initial request ---> for "+trackingID+ "---" + initRequestLog.toString());

            String response = JetonCardUtils.doPostHTTPSURLConnectionClient(initUrl, initRequest.toString(),token);
            transactionLogger.error("initial response ----> for "+trackingID+ "---" + response);
            if(functions.isValueNull(response))
            {
                JSONObject initResponse = new JSONObject(response);
                if (initResponse.has("code"))
                    responseCode = initResponse.getString("code");
                if (initResponse.has("status"))
                    responseStatus = initResponse.getString("status");
                if (initResponse.has("message"))
                    responseMessage = initResponse.getString("message");
                if (initResponse.has("token"))
                    token = initResponse.getString("token");
                if (initResponse.has("transactionId"))
                    transactionId = initResponse.getString("transactionId");
                if ("00000".equalsIgnoreCase(responseCode))
                {

                    String saleRequest = "{" +
                            "\"account\": {" +
                            "\"cardNumber\": \"" + cardNumber + "\"," +
                            "  \"expDate\": \"" + expDate + "\"," +
                            "  \"pin\": \"" + pin + "\"" +
                            "},\n" +
                            "\"customerInfo\": {\n" +
                            "  \"ip\": \"" + customerIp + "\"," +
                            "  \"agent\": \"" + userAgent + "\"" +
                            "},\n" +
                            "\"paymentMethod\": \"" + defaultPaymentMethod + "\"" +
                            "}";

                    String saleRequestLog = "{" +
                            "\"account\": {" +
                            "\"cardNumber\": \"" + functions.maskingPan(cardNumber) + "\"," +
                            "  \"expDate\": \"" + functions.maskingExpiry(expDate) + "\"," +
                            "  \"pin\": \"" + functions.maskingNumber(pin) + "\"" +
                            "},\n" +
                            "\"customerInfo\": {\n" +
                            "  \"ip\": \"" + customerIp + "\"," +
                            "  \"agent\": \"" + userAgent + "\"" +
                            "},\n" +
                            "\"paymentMethod\": \"" + defaultPaymentMethod + "\"" +
                            "}";
                    transactionLogger.error("sale request--->for "+trackingID+ "--" + saleRequestLog);
                    response = JetonCardUtils.doPostHTTPSURLConnectionClient(saleUrl, saleRequest, token);
                    transactionLogger.error("sale response--->for "+trackingID+ "--" + response);
                    if (functions.isValueNull(response))
                    {
                        responseCode = "";
                        responseStatus = "";
                        responseMessage = "";
                        JSONObject saleResponse = new JSONObject(response);
                        if (saleResponse.has("code"))
                            responseCode = saleResponse.getString("code");
                        if (saleResponse.has("status"))
                            responseStatus = saleResponse.getString("status");
                        if (saleResponse.has("message"))
                            responseMessage = saleResponse.getString("message");
                        if (saleResponse.has("transactionId"))
                            saleTransactionId = saleResponse.getString("transactionId");
                        if (saleResponse.has("reference"))
                            reference = saleResponse.getString("reference");
                        if (saleResponse.has("type"))
                            type = saleResponse.getString("type");
                        if (saleResponse.has("redirectUrl"))
                            redirectUrl = saleResponse.getString("redirectUrl");

                        comm3DResponseVO.setRemark(responseMessage);
                        comm3DResponseVO.setDescription(responseMessage);
                        comm3DResponseVO.setTransactionType(type);
                        comm3DResponseVO.setErrorCode(responseCode);
                        comm3DResponseVO.setTransactionId(saleTransactionId);
                        comm3DResponseVO.setResponseHashInfo(reference);
                        if ("00000".equalsIgnoreCase(responseCode) || "40009".equalsIgnoreCase(responseCode))
                        {
                            comm3DResponseVO.setStatus("success");
                        }
                        else if ("40106".equalsIgnoreCase(responseCode))
                        {
                            comm3DResponseVO.setStatus("pending3DConfirmation");
                            comm3DResponseVO.setUrlFor3DRedirect(redirectUrl);
                            comm3DResponseVO.setMethod("GET");
                        }
                        else
                        {
                            comm3DResponseVO.setStatus("failed");
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setRemark("Transaction Declined(Empty Response)");
                        comm3DResponseVO.setDescription("Transaction Declined(Empty Response)");
                    }

                }
                else
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionId(transactionId);
                    comm3DResponseVO.setRemark(responseMessage);
                    comm3DResponseVO.setDescription(responseMessage);
                    comm3DResponseVO.setErrorCode(responseCode);
                }
            }else
            {
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setRemark("Transaction Declined(Empty Response)");
                comm3DResponseVO.setDescription("Transaction Declined(Empty Response)");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException --->for "+trackingID+ "---",e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException --->for "+trackingID+ "---",e);
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("JetonCardPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processInquiry ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = gatewayAccount.getMerchantId();
        boolean isTest=gatewayAccount.isTest();

        String tokenUrl="";
        String inquiryUrl="";
        String responseCode="";
        String currency="";
        String amount="";
        String status="";
        String transactionId="";
        String transactionDate="";
        String bankCode="";
        String ipnMessages="";
        try
        {
            if(isTest)
            {
                tokenUrl=RB.getString("TOKEN_TEST_URL");
                inquiryUrl=RB.getString("INQUIRY_TEST_URL");
            }else
            {
                tokenUrl=RB.getString("TOKEN_LIVE_URL");
                inquiryUrl=RB.getString("INQUIRY_LIVE_URL");
            }
            String tokenRequest="{\n" +
                    "\"apiKey\": \""+mid+"\"\n" +
                    "}";
            transactionLogger.error("token request ----> for "+trackingID+ "----" +tokenRequest);
            String response=JetonCardUtils.doPostHTTPSURLConnectionClient(tokenUrl, tokenRequest.toString(), "");
            transactionLogger.error("token response ---->for "+trackingID+ "----" +response);
            if(functions.isValueNull(response))
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("accessToken"))
                {
                    String token = jsonObject.getString("accessToken");
                    String inquiryRequest = "{\n" +
                            "  \"transactionId\": \""+commTransactionDetailsVO.getPreviousTransactionId()+"\"\n" +
                            "}";
                    transactionLogger.error("inquiryRequest--->for "+trackingID+ "----" + inquiryRequest);
                    response = JetonCardUtils.doPostHTTPSURLConnectionClient(inquiryUrl, inquiryRequest.toString(), token);
                    transactionLogger.error("inquiryResponse--->for "+trackingID+ "----" + response);
                    if (functions.isValueNull(response) && response.contains("["))
                    {
                        JSONArray inquiryResponseArray = new JSONArray(response);

                        if(inquiryResponseArray.length()>0)
                        {
                            JSONObject inquiryResponseJSON=inquiryResponseArray.getJSONObject(0);
                            if(inquiryResponseJSON.has("transactionAmount"))
                            {
                                currency=inquiryResponseJSON.getJSONObject("transactionAmount").getString("currencyCode");
                                amount=inquiryResponseJSON.getJSONObject("transactionAmount").getString("amount");
                            }
                            if(inquiryResponseJSON.has("status"))
                                status=inquiryResponseJSON.getString("status");
                            if(inquiryResponseJSON.has("transactionId"))
                                transactionId=inquiryResponseJSON.getString("transactionId");
                            if(inquiryResponseJSON.has("updateDate"))
                                transactionDate=inquiryResponseJSON.getString("updateDate");
                            if(inquiryResponseJSON.has("bankCode"))
                                bankCode=inquiryResponseJSON.getString("bankCode");
                            if(inquiryResponseJSON.has("ipnMessages") && inquiryResponseJSON.getJSONArray("ipnMessages") != null && inquiryResponseJSON.getJSONArray("ipnMessages").length()>0)
                                ipnMessages= (String) inquiryResponseJSON.getJSONArray("ipnMessages").get(0);

                            if("APPROVED".equalsIgnoreCase(status))
                                commResponseVO.setStatus("success");
                            else if("WAITING".equalsIgnoreCase(status))
                                commResponseVO.setStatus("pending");
                            else
                                commResponseVO.setStatus("fail");

                            commResponseVO.setAmount(amount);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                            commResponseVO.setRemark(ipnMessages);
                            commResponseVO.setDescription(ipnMessages);
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setBankTransactionDate(transactionDate);
                            commResponseVO.setTransactionType("SALE");
                            if(functions.isValueNull(bankCode))
                                commResponseVO.setAuthCode(bankCode);
                            else
                                commResponseVO.setAuthCode("-");
                        }
                        else
                        {
                            status = "pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark("Transaction Pending");
                            commResponseVO.setDescription("Transaction Pending");
                            commResponseVO.setTransactionId(transactionId);
                        }
                    }
                    else
                    {
                        status = "pending";
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark("Transaction Pending");
                        commResponseVO.setDescription("Transaction Pending");
                        commResponseVO.setTransactionId(transactionId);
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----for "+trackingID+ "----",e);
        }
        return commResponseVO;
    }
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO= ((CommRequestVO)requestVO).getCardDetailsVO();
        Functions functions=new Functions();
        SimpleDateFormat format=new SimpleDateFormat("yyyy");
        SimpleDateFormat format2=new SimpleDateFormat("yy");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        String tokenUrl="";
        String payoutUrl="";
        String responseCode="";
        String currency="";
        String amount="";
        String message_fromResponse="";
        String transactionId="";
        String transactionDate="";
        String bankCode="";
        String dateOfBirth="";
        String expDate="";
        String country="";
        if(functions.isValueNull(addressDetailsVO.getCountry()))
        {
            country = addressDetailsVO.getCountry();
            if(country.length()==3)
                country= EMerchantPayUtils.getCountryTwoDigit(country);
        }else
            country="TR";
        try
        {

            dateOfBirth="1986-05-22";
            try
            {
                expDate=cardDetailsVO.getExpMonth()+"-"+format2.format(format.parse(cardDetailsVO.getExpYear()));
                transactionLogger.error("exp month " + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ functions.maskingNumber(format2.format(format.parse(cardDetailsVO.getExpYear()))));

            }
            catch (ParseException e)
            {
                transactionLogger.error("ParseException --->for "+trackingID+ "----",e);
                expDate=cardDetailsVO.getExpMonth()+"-"+cardDetailsVO.getExpYear().substring(cardDetailsVO.getExpYear().length()-2,cardDetailsVO.getExpYear().length());
                transactionLogger.error("exp month " + functions.maskingNumber(cardDetailsVO.getExpMonth()) + "exp year "+ cardDetailsVO.getExpYear().substring(cardDetailsVO.getExpYear().length()-2,cardDetailsVO.getExpYear().length()));
            }
            if (isTest)
            {
                tokenUrl = RB.getString("TOKEN_TEST_URL");
                payoutUrl = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                tokenUrl = RB.getString("TOKEN_LIVE_URL");
                payoutUrl = RB.getString("PAYOUT_LIVE_URL");
            }
            String tokenRequest = "{\n" +
                    "\"apiKey\": \"" + mid + "\"\n" +
                    "}";
            transactionLogger.error("token request ---->for "+trackingID+ "----" + tokenRequest);
            String response = JetonCardUtils.doPostHTTPSURLConnectionClient(tokenUrl, tokenRequest.toString(), "");
            transactionLogger.error("token response ---->for "+trackingID+ "----" + response);
            if (functions.isValueNull(response))
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("accessToken"))
                {
                    String token = jsonObject.getString("accessToken");

                    String payoutRequest="{" +
                            "  \"amount\": "+transactionDetailsVO.getAmount()+"," +
                            "  \"cardNumber\": \""+cardDetailsVO.getCardNum()+"\"," +
                            "  \"country\": \""+country+"\"," +
                            "  \"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                            "  \"dateOfBirth\": \""+dateOfBirth+"\"," +
                            "  \"email\": \""+addressDetailsVO.getEmail()+"\"," +
                            "  \"expDate\": \""+expDate+"\"," +
                            "  \"firstName\": \""+addressDetailsVO.getFirstname()+"\"," +
                            "  \"lastName\": \""+addressDetailsVO.getLastname()+"\"," +
                            "  \"referenceNo\": \""+trackingID+"\"" +
                            "}";

                    String payoutRequestLog="{" +
                            "  \"amount\": "+transactionDetailsVO.getAmount()+"," +
                            "  \"cardNumber\": \""+functions.maskingPan(cardDetailsVO.getCardNum())+"\"," +
                            "  \"country\": \""+country+"\"," +
                            "  \"currency\": \""+transactionDetailsVO.getCurrency()+"\"," +
                            "  \"dateOfBirth\": \""+dateOfBirth+"\"," +
                            "  \"email\": \""+addressDetailsVO.getEmail()+"\"," +
                            //"  \"expDate\": \""+functions.maskingExpiryNew(expDate)+"\"," +
                            "  \"firstName\": \""+addressDetailsVO.getFirstname()+"\"," +
                            "  \"lastName\": \""+addressDetailsVO.getLastname()+"\"," +
                            "  \"referenceNo\": \""+trackingID+"\"" +
                            "}";


                    transactionLogger.error("payout request---->for "+trackingID+ "----" +payoutRequestLog);
                    response=JetonCardUtils.doPostHTTPSURLConnectionClient(payoutUrl,payoutRequest,token);
                    transactionLogger.error("payout response--->"+response);
                    if(functions.isValueNull(response))
                    {
                        JSONObject payoutResponse=new JSONObject(response);
                        if(payoutResponse.has("status") && "APPROVED".equalsIgnoreCase(payoutResponse.getString("status")))
                        {
                            commResponseVO.setStatus("success");
                            if(payoutResponse.has("transactionId"))
                                commResponseVO.setTransactionId(payoutResponse.getString("transactionId"));
                            if(payoutResponse.has("code"))
                                commResponseVO.setErrorCode(payoutResponse.getString("code"));
                            if(payoutResponse.has("timestamp"))
                                commResponseVO.setResponseTime(payoutResponse.getString("timestamp"));
                            if(payoutResponse.has("message")){
                                commResponseVO.setRemark(payoutResponse.getString("message"));
                                commResponseVO.setDescription(payoutResponse.getString("message"));
                            }
                        }
                        else if(payoutResponse.has("status") && "WAITING".equalsIgnoreCase(payoutResponse.getString("status")))
                        {
                            commResponseVO.setStatus("pending");
                            if(payoutResponse.has("transactionId"))
                                commResponseVO.setTransactionId(payoutResponse.getString("transactionId"));
                            if(payoutResponse.has("code"))
                                commResponseVO.setErrorCode(payoutResponse.getString("code"));
                            if(payoutResponse.has("message")){
                                commResponseVO.setRemark(payoutResponse.getString("message"));
                                commResponseVO.setDescription(payoutResponse.getString("message"));
                            }

                        }else
                        {
                            commResponseVO.setStatus("failed");
                            if(payoutResponse.has("message"))
                            {
                                commResponseVO.setRemark(payoutResponse.getString("message"));
                                commResponseVO.setDescription(payoutResponse.getString("message"));
                            }
                            if(payoutResponse.has("code"))
                            {
                                commResponseVO.setErrorCode(payoutResponse.getString("code"));
                            }
                        }

                    }


                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ::::::::: ",e);
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

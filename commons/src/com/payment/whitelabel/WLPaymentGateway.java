package com.payment.whitelabel;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
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
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

/**
 * Created by Uday on 8/22/18.
 */
public class WLPaymentGateway extends AbstractPaymentGateway
{
    public static final TransactionLogger transactionLogger= new TransactionLogger(WLPaymentGateway.class.getName());
    public static  final String GATEWAY_TYPE_TRANSACTWORLD="trnsactWLD";
    public static final String GATEWAY_TYPE_AGNIPAY="agnipay";
    public static final String GATEWAY_TYPE_SHIMOTOMO="shimotomo";
    public static final String GATEWAY_TYPE_SAMPLEPSP="samplepsp";
    public static final String GATEWAY_TYPE_FIDOMS="FidoMS";

    private final ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.whitelabel");

    public WLPaymentGateway(String accountId){
        this.accountId=accountId;
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {

        transactionLogger.error("-----inside processSale-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String ccnum="";
        String expMonth="";
        String expYear="";
        String cvv="";
        if(commCardDetailsVO!=null){
            ccnum=commCardDetailsVO.getCardNum();
            expMonth=commCardDetailsVO.getExpMonth();
            expYear=commCardDetailsVO.getExpYear();
            cvv=commCardDetailsVO.getcVV();
        }

        transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
        String token_url="";
        String paymentUrl="";
        String Url_3D="";
        String Non_3D="";
        String backEnd_Url="";
        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
            paymentUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYMENTS_HOST_URL");
            Url_3D="https://"+commMerchantVO.getHostUrl()+RB.getString("3D_HOST_URL");
            Non_3D= "https://"+commMerchantVO.getHostUrl()+RB.getString("NON_3D_HOST_URL");
            backEnd_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("BACKEND_HOST_URL");

        }else {
            Url_3D=RB.getString("merchantRedirectUrl_3D");
            Non_3D=RB.getString("merchantRedirectUrl");
            backEnd_Url=RB.getString("merchantNotificationUrl");

            if(isTest){
                token_url=RB.getString("TEST_TOKEN_URL");
                paymentUrl=RB.getString("TEST_URL");

            }else {
                token_url=RB.getString("LIVE_TOKEN_URL");
                paymentUrl=RB.getString("LIVE_URL");
            }
        }


        try
        {

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse="";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken="";
            if(functions.isValueNull(tokenResponse) && tokenResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(tokenResponse);
                if(jsonObject.has("AuthToken")){
                    authToken=jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----"+authToken);
            }

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String orderId = trackingID;
            String amount = commTransactionDetailsVO.getAmount();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("orderId=" + orderId);
            transactionLogger.debug("amount=" + amount);
            String checksum = WLUtils.getCheckSum(merchantId, key, orderId, amount);


            String paymentBrand="";
            String paymentMode="";
            String redirectionUrl="";

            transactionLogger.debug("PaymentType-----"+commTransactionDetailsVO.getPaymentType());
            transactionLogger.debug("CardType-----"+commTransactionDetailsVO.getCardType());

            String paymentType="";
            if(functions.isValueNull(commTransactionDetailsVO.getPaymentType()))
            {
                paymentType = commTransactionDetailsVO.getPaymentType();

                if (paymentType.equalsIgnoreCase("1"))
                {
                    paymentMode = "CC";
                    redirectionUrl = Url_3D;
                }
                else if (paymentType.equalsIgnoreCase("3"))
                {
                    paymentMode = "EW";
                    redirectionUrl = Non_3D;
                }
            }

            if(functions.isValueNull(commTransactionDetailsVO.getCardType()))
            {
                paymentBrand=commTransactionDetailsVO.getCardType();
                if(paymentBrand.equalsIgnoreCase("1"))
                {
                    paymentBrand="VISA";
                }
                else if(paymentBrand.equalsIgnoreCase("24"))
                {
                    paymentBrand="SKRILL";
                }
            }

            String saleRequest = ""
                    + "authentication.memberId=" + merchantId + ""
                    + "&authentication.checksum=" + checksum + ""
                    + "&authentication.terminalId=" + gatewayAccount.getFRAUD_FILE_SHORT_NAME() + ""
                    + "&merchantTransactionId=" + orderId + ""
                    + "&amount=" + amount + ""
                    + "&currency=" + commTransactionDetailsVO.getCurrency() + ""
                    + "&orderDescriptor=" + commTransactionDetailsVO.getOrderDesc() + ""
                    + "&shipping.country=" + commAddressDetailsVO.getCountry() + ""
                    + "&shipping.city=" + commAddressDetailsVO.getCity() + ""
                    + "&shipping.state=" + commAddressDetailsVO.getState() + ""
                    + "&shipping.postcode=" + commAddressDetailsVO.getZipCode() + ""
                    + "&shipping.street1=" + commAddressDetailsVO.getStreet() + ""
                    + "&customer.telnocc=" + commAddressDetailsVO.getTelnocc() + ""
                    + "&customer.phone=" + commAddressDetailsVO.getPhone() + ""
                    + "&customer.email=" + commAddressDetailsVO.getEmail() + ""
                    + "&customer.givenName=" + commAddressDetailsVO.getFirstname() + ""
                    + "&customer.surname=" + commAddressDetailsVO.getLastname() + ""
                    + "&customer.ip=" + commAddressDetailsVO.getCardHolderIpAddress() + ""
                    + "&customer.birthDate=" + commAddressDetailsVO.getBirthdate() + ""
                    + "&card.number=" + ccnum + ""
                    + "&card.expiryMonth=" + expMonth + ""
                    + "&card.expiryYear=" + expYear + ""
                    + "&card.cvv=" + cvv + ""
                    + "&paymentBrand="+paymentBrand+""  //commTransactionDetailsVO.getCardType()
                    + "&paymentMode="+paymentMode+"" // commTransactionDetailsVO.getPaymentType()
                    + "&paymentType=DB"
                    + "&merchantRedirectUrl="+redirectionUrl+trackingID+""
                    + "&notificationUrl="+backEnd_Url+trackingID+""
                    + "&tmpl_amount=" + commAddressDetailsVO.getTmpl_amount() + ""
                    + "&tmpl_currency=" + commAddressDetailsVO.getTmpl_currency() + ""
                    + "&customer.customerId=" + commAddressDetailsVO.getCustomerid() + "";

            String saleRequestLog = ""
                    + "authentication.memberId=" + merchantId + ""
                    + "&authentication.checksum=" + checksum + ""
                    + "&authentication.terminalId=" + gatewayAccount.getFRAUD_FILE_SHORT_NAME() + ""
                    + "&merchantTransactionId=" + orderId + ""
                    + "&amount=" + amount + ""
                    + "&currency=" + commTransactionDetailsVO.getCurrency() + ""
                    + "&orderDescriptor=" + commTransactionDetailsVO.getOrderDesc() + ""
                    + "&shipping.country=" + commAddressDetailsVO.getCountry() + ""
                    + "&shipping.city=" + commAddressDetailsVO.getCity() + ""
                    + "&shipping.state=" + commAddressDetailsVO.getState() + ""
                    + "&shipping.postcode=" + commAddressDetailsVO.getZipCode() + ""
                    + "&shipping.street1=" + commAddressDetailsVO.getStreet() + ""
                    + "&customer.telnocc=" + commAddressDetailsVO.getTelnocc() + ""
                    + "&customer.phone=" + commAddressDetailsVO.getPhone() + ""
                    + "&customer.email=" + commAddressDetailsVO.getEmail() + ""
                    + "&customer.givenName=" + commAddressDetailsVO.getFirstname() + ""
                    + "&customer.surname=" + commAddressDetailsVO.getLastname() + ""
                    + "&customer.ip=" + commAddressDetailsVO.getCardHolderIpAddress() + ""
                    + "&customer.birthDate=" + commAddressDetailsVO.getBirthdate() + ""
                    + "&card.number=" + functions.maskingPan(ccnum) + ""
                    + "&card.expiryMonth=" + functions.maskingNumber(expMonth) + ""
                    + "&card.expiryYear=" + functions.maskingNumber(expYear) + ""
                    + "&card.cvv=" + functions.maskingNumber(cvv) + ""
                    + "&paymentBrand="+paymentBrand+""  //commTransactionDetailsVO.getCardType()
                    + "&paymentMode="+paymentMode+"" // commTransactionDetailsVO.getPaymentType()
                    + "&paymentType=DB"
                    + "&merchantRedirectUrl="+redirectionUrl+trackingID+""
                    + "&notificationUrl="+backEnd_Url+trackingID+""
                    + "&tmpl_amount=" + commAddressDetailsVO.getTmpl_amount() + ""
                    + "&tmpl_currency=" + commAddressDetailsVO.getTmpl_currency() + ""
                    + "&customer.customerId=" + commAddressDetailsVO.getCustomerid() + "";

            transactionLogger.error("saleRequest---"+trackingID+"--" + saleRequestLog);

            String saleResponse="";

            saleResponse = WLUtils.doPostHTTPSURLConnectionClient(paymentUrl, authToken, saleRequest);

            transactionLogger.error("saleResponse---"+trackingID+"--" + saleResponse);

            if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
            {
                String paymentId = "";
                String respAmount = "";
                String respCurrency = "";
                String errorCode = "";
                String description = "";
                String descriptor="";
                String transactionStatus = "";
                String tmpl_currency = "";
                String tmpl_amount = "";
                String timestamp="";
                String url = "";
                String paReq = "";

                JSONObject jsonObject = new JSONObject(saleResponse);
                if(jsonObject.has("paymentId")){
                    paymentId=jsonObject.getString("paymentId");
                }
                if(jsonObject.has("amount")){
                    respAmount=jsonObject.getString("amount");
                }
                if(jsonObject.has("currency")){
                    respCurrency=jsonObject.getString("currency");
                }
                if(jsonObject.has("descriptor")){
                    descriptor=jsonObject.getString("descriptor");
                }
                if(jsonObject.has("result")){
                    if(jsonObject.getJSONObject("result").has("code")){
                        errorCode=jsonObject.getJSONObject("result").getString("code");
                    }
                    if(jsonObject.getJSONObject("result").has("description")){
                        description=jsonObject.getJSONObject("result").getString("description");
                    }
                }
                if(jsonObject.has("transactionStatus")){
                    transactionStatus=jsonObject.getString("transactionStatus");
                }
                if(jsonObject.has("tmpl_currency")){
                    tmpl_currency=jsonObject.getString("tmpl_currency");
                }
                if(jsonObject.has("tmpl_amount")){
                    tmpl_amount=jsonObject.getString("tmpl_amount");
                }
                if(jsonObject.has("timestamp")){
                    timestamp=jsonObject.getString("timestamp");
                }
                if(jsonObject.has("url")){
                    url=jsonObject.getString("url");
                }
                if(jsonObject.has("redirect")){
                    JSONObject jsonObject1=jsonObject.getJSONObject("redirect");
                    if(jsonObject1.has("url")){
                        url=jsonObject1.getString("url");
                    }
                    JSONArray jsonArray=jsonObject1.getJSONArray("parameters");
                    transactionLogger.debug("jsonArray-----size-----"+jsonArray.length());
                    HashMap hashMap = new HashMap();
                    for(int i=0 ;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject2=jsonArray.getJSONObject(i);
                        String name=jsonObject2.getString("name");
                        String value=jsonObject2.getString("value");
                        hashMap.put(name,value);
                    }
                    commResponseVO.setRequestMap(hashMap);
                    if(!hashMap.containsKey("launch3D"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setUrlFor3DRedirect(url);
                        return commResponseVO;
                    }
                }
                if(transactionStatus.equalsIgnoreCase("3D")){
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setRemark("pending 3D Confirmation");
                    commResponseVO.setPaReq(paReq);
                    commResponseVO.setUrlFor3DRedirect(url);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timestamp);
                    commResponseVO.setAmount(respAmount);
                    commResponseVO.setCurrency(respCurrency);
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                    commResponseVO.setErrorCode(errorCode);
                    return commResponseVO;
                }else if(transactionStatus.equalsIgnoreCase("Y")){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescriptor(descriptor);
                }else if(transactionStatus.equalsIgnoreCase("P")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                }
                else if(transactionStatus.equalsIgnoreCase("N")){
                    commResponseVO.setStatus("Transaction Failed");
                }else {
                    commResponseVO.setStatus("error");
                    commResponseVO.setRemark("Error");
                }
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setDescription(description);
                commResponseVO.setBankTransactionDate(timestamp);
                commResponseVO.setAmount(respAmount);
                commResponseVO.setCurrency(respCurrency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setErrorCode(errorCode);
            }else {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Response not received");
            }
        }catch (JSONException e){
            transactionLogger.error("JsonException-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String ccnum="";
        String expMonth="";
        String expYear="";
        String cvv="";
        if(commCardDetailsVO!=null){
            ccnum=commCardDetailsVO.getCardNum();
            expMonth=commCardDetailsVO.getExpMonth();
            expYear=commCardDetailsVO.getExpYear();
            cvv=commCardDetailsVO.getcVV();
        }

        transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
        String token_url="";
        String paymentUrl="";
        String Url_3D="";
        String Non_3D="";
        String backEnd_Url="";
        /*if(functions.isValueNull(commMerchantVO.getHostUrl())){
            token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
            paymentUrl="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYMENTS_HOST_URL");
            Url_3D="https://"+commMerchantVO.getHostUrl()+RB.getString("3D_HOST_URL");
            Non_3D= "https://"+commMerchantVO.getHostUrl()+RB.getString("NON_3D_HOST_URL");
            backEnd_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("BACKEND_HOST_URL");

        }else {*/
            Url_3D=RB.getString("merchantRedirectUrl_3D");
            Non_3D=RB.getString("merchantRedirectUrl");
            backEnd_Url=RB.getString("merchantNotificationUrl");

            if(isTest){
                token_url=RB.getString("TEST_TOKEN_URL");
                paymentUrl=RB.getString("TEST_URL");

            }else {
                token_url=RB.getString("LIVE_TOKEN_URL");
                paymentUrl=RB.getString("LIVE_URL");
            }
        /*}*/

        try
        {

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse="";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken="";
            if(functions.isValueNull(tokenResponse) && tokenResponse.contains("{")){
                JSONObject jsonObject= new JSONObject(tokenResponse);
                if(jsonObject.has("AuthToken")){
                    authToken=jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----"+authToken);
            }

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String orderId = trackingID;
            String amount = commTransactionDetailsVO.getAmount();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("orderId=" + orderId);
            transactionLogger.debug("amount=" + amount);
            String checksum = WLUtils.getCheckSum(merchantId, key, orderId, amount);


            String paymentBrand="";
            String paymentMode="";
            String redirectionUrl="";

            transactionLogger.debug("PaymentType-----"+commTransactionDetailsVO.getPaymentType());
            transactionLogger.debug("CardType-----"+commTransactionDetailsVO.getCardType());

            String paymentType="";
            if(functions.isValueNull(commTransactionDetailsVO.getPaymentType()))
            {
                paymentType = commTransactionDetailsVO.getPaymentType();

                if (paymentType.equalsIgnoreCase("1"))
                {
                    paymentMode = "CC";
                    redirectionUrl = Url_3D;
                }
                else if (paymentType.equalsIgnoreCase("3"))
                {
                    paymentMode = "EW";
                    redirectionUrl = Non_3D;
                }
            }

            if(functions.isValueNull(commTransactionDetailsVO.getCardType()))
            {
                paymentBrand=commTransactionDetailsVO.getCardType();
                if(paymentBrand.equalsIgnoreCase("1"))
                {
                    paymentBrand="VISA";
                }
                else if(paymentBrand.equalsIgnoreCase("24"))
                {
                    paymentBrand="SKRILL";
                }
            }


            String authRequest = ""
                    + "authentication.memberId=" + merchantId + ""
                    + "&authentication.checksum=" + checksum + ""
                    + "&authentication.terminalId=" + gatewayAccount.getFRAUD_FILE_SHORT_NAME() + ""
                    + "&merchantTransactionId=" + orderId + ""
                    + "&amount=" + amount + ""
                    + "&currency=" + commTransactionDetailsVO.getCurrency() + ""
                    + "&orderDescriptor=" + commTransactionDetailsVO.getOrderDesc() + ""
                    + "&shipping.country=" + commAddressDetailsVO.getCountry() + ""
                    + "&shipping.city=" + commAddressDetailsVO.getCity() + ""
                    + "&shipping.state=" + commAddressDetailsVO.getState() + ""
                    + "&shipping.postcode=" + commAddressDetailsVO.getZipCode() + ""
                    + "&shipping.street1=" + commAddressDetailsVO.getStreet() + ""
                    + "&customer.telnocc=" + commAddressDetailsVO.getTelnocc() + ""
                    + "&customer.phone=" + commAddressDetailsVO.getPhone() + ""
                    + "&customer.email=" + commAddressDetailsVO.getEmail() + ""
                    + "&customer.givenName=" + commAddressDetailsVO.getFirstname() + ""
                    + "&customer.surname=" + commAddressDetailsVO.getLastname() + ""
                    + "&customer.ip=" + commAddressDetailsVO.getCardHolderIpAddress() + ""
                    + "&customer.birthDate=" + commAddressDetailsVO.getBirthdate() + ""
                    + "&card.number=" + ccnum + ""
                    + "&card.expiryMonth=" + expMonth + ""
                    + "&card.expiryYear=" + expYear + ""
                    + "&card.cvv=" + cvv + ""
                    + "&paymentBrand="+paymentBrand+""  //commTransactionDetailsVO.getCardType()
                    + "&paymentMode="+paymentMode+"" // commTransactionDetailsVO.getPaymentType()
                    + "&paymentType=PA"
                    + "&merchantRedirectUrl="+redirectionUrl+trackingID+""
                    + "&notificationUrl="+backEnd_Url+trackingID+""
                    + "&tmpl_amount=" + commAddressDetailsVO.getTmpl_amount() + ""
                    + "&tmpl_currency=" + commAddressDetailsVO.getTmpl_currency() + ""
                    + "&customer.customerId=" + commAddressDetailsVO.getCustomerid() + "";

            String authRequestLog = ""
                    + "authentication.memberId=" + merchantId + ""
                    + "&authentication.checksum=" + checksum + ""
                    + "&authentication.terminalId=" + gatewayAccount.getFRAUD_FILE_SHORT_NAME() + ""
                    + "&merchantTransactionId=" + orderId + ""
                    + "&amount=" + amount + ""
                    + "&currency=" + commTransactionDetailsVO.getCurrency() + ""
                    + "&orderDescriptor=" + commTransactionDetailsVO.getOrderDesc() + ""
                    + "&shipping.country=" + commAddressDetailsVO.getCountry() + ""
                    + "&shipping.city=" + commAddressDetailsVO.getCity() + ""
                    + "&shipping.state=" + commAddressDetailsVO.getState() + ""
                    + "&shipping.postcode=" + commAddressDetailsVO.getZipCode() + ""
                    + "&shipping.street1=" + commAddressDetailsVO.getStreet() + ""
                    + "&customer.telnocc=" + commAddressDetailsVO.getTelnocc() + ""
                    + "&customer.phone=" + commAddressDetailsVO.getPhone() + ""
                    + "&customer.email=" + commAddressDetailsVO.getEmail() + ""
                    + "&customer.givenName=" + commAddressDetailsVO.getFirstname() + ""
                    + "&customer.surname=" + commAddressDetailsVO.getLastname() + ""
                    + "&customer.ip=" + commAddressDetailsVO.getCardHolderIpAddress() + ""
                    + "&customer.birthDate=" + commAddressDetailsVO.getBirthdate() + ""
                    + "&card.number=" + functions.maskingPan(ccnum) + ""
                    + "&card.expiryMonth=" + functions.maskingNumber(expMonth) + ""
                    + "&card.expiryYear=" + functions.maskingNumber(expYear) + ""
                    + "&card.cvv=" + functions.maskingNumber(cvv) + ""
                    + "&paymentBrand="+paymentBrand+""  //commTransactionDetailsVO.getCardType()
                    + "&paymentMode="+paymentMode+"" // commTransactionDetailsVO.getPaymentType()
                    + "&paymentType=PA"
                    + "&merchantRedirectUrl="+redirectionUrl+trackingID+""
                    + "&notificationUrl="+backEnd_Url+trackingID+""
                    + "&tmpl_amount=" + commAddressDetailsVO.getTmpl_amount() + ""
                    + "&tmpl_currency=" + commAddressDetailsVO.getTmpl_currency() + ""
                    + "&customer.customerId=" + commAddressDetailsVO.getCustomerid() + "";

            transactionLogger.error("authRequest---"+trackingID+"--" + authRequestLog);

            String authResponse="";

            authResponse = WLUtils.doPostHTTPSURLConnectionClient(paymentUrl, authToken, authRequest);

            transactionLogger.error("authResponse---"+trackingID+"--" + authResponse);

            if (functions.isValueNull(authResponse) && authResponse.contains("{"))
            {
                String paymentId = "";
                String respAmount = "";
                String respCurrency = "";
                String errorCode = "";
                String description = "";
                String descriptor="";
                String transactionStatus = "";
                String tmpl_currency = "";
                String tmpl_amount = "";
                String timestamp="";
                String url = "";
                String paReq = "";
                String termUrl = "";
                String md = "";
                String launch3D="";

                JSONObject jsonObject = new JSONObject(authResponse);
                if(jsonObject.has("paymentId")){
                    paymentId=jsonObject.getString("paymentId");
                }
                if(jsonObject.has("amount")){
                    respAmount=jsonObject.getString("amount");
                }
                if(jsonObject.has("currency")){
                    respCurrency=jsonObject.getString("currency");
                }
                if(jsonObject.has("descriptor")){
                    descriptor=jsonObject.getString("descriptor");
                }
                if(jsonObject.has("result")){
                    if(jsonObject.getJSONObject("result").has("code")){
                        errorCode=jsonObject.getJSONObject("result").getString("code");
                    }
                    if(jsonObject.getJSONObject("result").has("description")){
                        description=jsonObject.getJSONObject("result").getString("description");
                    }
                }
                if(jsonObject.has("transactionStatus")){
                    transactionStatus=jsonObject.getString("transactionStatus");
                }
                if(jsonObject.has("tmpl_currency")){
                    tmpl_currency=jsonObject.getString("tmpl_currency");
                }
                if(jsonObject.has("tmpl_amount")){
                    tmpl_amount=jsonObject.getString("tmpl_amount");
                }
                if(jsonObject.has("timestamp")){
                    timestamp=jsonObject.getString("timestamp");
                }
                if(jsonObject.has("url")){
                    url=jsonObject.getString("url");
                }
                if(jsonObject.has("redirect")){
                    JSONObject jsonObject1=jsonObject.getJSONObject("redirect");
                    if(jsonObject1.has("url")){
                        url=jsonObject1.getString("url");
                    }
                    JSONArray jsonArray=jsonObject1.getJSONArray("parameters");
                    transactionLogger.debug("jsonArray-----size-----"+jsonArray.length());
                    HashMap hashMap = new HashMap();
                    for(int i=0 ;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject2=jsonArray.getJSONObject(i);
                        String name=jsonObject2.getString("name");
                        String value=jsonObject2.getString("value");
                        hashMap.put(name,value);
                    }
                    commResponseVO.setRequestMap(hashMap);

                    if(!hashMap.containsKey("launch3D"))
                    {
                        transactionLogger.debug("inside----pending");
                        commResponseVO.setStatus("pending");
                        commResponseVO.setUrlFor3DRedirect(url);
                        return commResponseVO;
                    }
                }
                if(transactionStatus.equalsIgnoreCase("3D")){
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setRemark("pending 3D Confirmation");
                    commResponseVO.setPaReq(paReq);
                    commResponseVO.setUrlFor3DRedirect(url);
                    commResponseVO.setTerURL(termUrl);
                    commResponseVO.setMd(md);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timestamp);
                    commResponseVO.setAmount(respAmount);
                    commResponseVO.setCurrency(respCurrency);
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                    commResponseVO.setErrorCode(errorCode);
                    return commResponseVO;
                }else if(transactionStatus.equalsIgnoreCase("Y")){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescriptor(descriptor);
                }else if(transactionStatus.equalsIgnoreCase("P")){
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                }
                else if(transactionStatus.equalsIgnoreCase("N")){
                    commResponseVO.setStatus("Transaction Failed");
                }else {
                    commResponseVO.setStatus("error");
                    commResponseVO.setRemark("Error");
                }
                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setDescription(description);
                commResponseVO.setBankTransactionDate(timestamp);
                commResponseVO.setAmount(respAmount);
                commResponseVO.setCurrency(respCurrency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setErrorCode(errorCode);
            }else {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Response not received");
            }
        }catch (JSONException e){
            transactionLogger.error("JsonException-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        try
        {
            transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
            String token_url="";
            String endpoint_Url="";
            if(functions.isValueNull(commMerchantVO.getHostUrl())){
                token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
                endpoint_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYMENT_ENDPOINT_HOST_URL");

            }else {
                if(isTest){
                    token_url=RB.getString("TEST_TOKEN_URL");
                    endpoint_Url=RB.getString("TEST_ENDPOINT_PAYMENTID");

                }else {
                    token_url=RB.getString("LIVE_TOKEN_URL");
                    endpoint_Url=RB.getString("LIVE_ENDPOINT_PAYMENTID");
                }
            }

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse = "";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken = "";
            if (functions.isValueNull(tokenResponse) && tokenResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(tokenResponse);
                if (jsonObject.has("AuthToken"))
                {
                    authToken = jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----" + authToken);
            }

            String transactionId="";
            if(functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId())){
                transactionId=commTransactionDetailsVO.getPreviousTransactionId();
            }
            transactionLogger.debug("transactionId------"+transactionId);

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String amount = commTransactionDetailsVO.getAmount();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("orderId=" + transactionId);
            transactionLogger.debug("amount=" + amount);

            String checksum = WLUtils.getCheckSum(merchantId, key, transactionId, amount);


            String captureRequest = ""
                    + "authentication.memberId="+gatewayAccount.getMerchantId()+""
                    + "&authentication.checksum="+checksum+""
                    + "&amount="+amount+""
                    + "&paymentType=CP";

            transactionLogger.error("captureRequest-----"+captureRequest);

            String captureResponse="";

            captureResponse = WLUtils.doPostHTTPSURLConnectionClient(endpoint_Url+transactionId, authToken, captureRequest);

            transactionLogger.error("captureResponse-----" + captureResponse);

            if(functions.isValueNull(captureResponse) && captureResponse.contains("{")){
                JSONObject jsonObject = new JSONObject(captureResponse);
                if(jsonObject!=null){
                    String paymentId="";
                    String code="";
                    String description="";
                    String timseStamp="";
                    if(jsonObject.has("paymentId")){
                        paymentId=jsonObject.getString("paymentId");
                    }
                    if(jsonObject.has("result")){
                        code=jsonObject.getJSONObject("result").getString("code");
                        description=jsonObject.getJSONObject("result").getString("description");
                    }
                    if(jsonObject.has("timestamp")){
                        timseStamp=jsonObject.getString("timestamp");
                    }

                    if("00004".equalsIgnoreCase(code)){
                        commResponseVO.setStatus("success");
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setRemark(description);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timseStamp);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                }
            }

        }catch (JSONException e){
            transactionLogger.error("JSONException-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        boolean isTest = gatewayAccount.isTest();

        try
        {
            transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
            String token_url="";
            String endpoint_Url="";
            if(functions.isValueNull(commMerchantVO.getHostUrl())){
                token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
                endpoint_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYMENT_ENDPOINT_HOST_URL");

            }else {
                if(isTest){
                    token_url=RB.getString("TEST_TOKEN_URL");
                    endpoint_Url=RB.getString("TEST_ENDPOINT_PAYMENTID");

                }else {
                    token_url=RB.getString("LIVE_TOKEN_URL");
                    endpoint_Url=RB.getString("LIVE_ENDPOINT_PAYMENTID");
                }
            }

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse = "";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken = "";
            if (functions.isValueNull(tokenResponse) && tokenResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(tokenResponse);
                if (jsonObject.has("AuthToken"))
                {
                    authToken = jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----" + authToken);
            }

            String transactionId="";
            if(functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId())){
                transactionId=commTransactionDetailsVO.getPreviousTransactionId();
            }
            transactionLogger.debug("transactionId------"+transactionId);

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();

            String amount = commTransactionDetailsVO.getAmount();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("amount=" + amount);
            String checksum = WLUtils.getCheckSum(merchantId, key, transactionId, amount);

            String refundRequest = ""
                    + "authentication.memberId="+gatewayAccount.getMerchantId()+""
                    + "&authentication.checksum="+checksum+""
                    + "&amount="+amount+""
                    + "&paymentType=RF";

            transactionLogger.error("refundRequest-----"+refundRequest);

            String refundResponse="";

            refundResponse = WLUtils.doPostHTTPSURLConnectionClient(endpoint_Url+transactionId, authToken, refundRequest);

            transactionLogger.error("refundResponse-----" + refundResponse);

            if(functions.isValueNull(refundResponse) && refundResponse.contains("{")){
                JSONObject jsonObject = new JSONObject(refundResponse);
                if(jsonObject!=null){
                    String paymentId="";
                    String code="";
                    String description="";
                    String timseStamp="";

                    if (jsonObject.has("paymentId"))
                    {
                        paymentId = jsonObject.getString("paymentId");
                    }
                    if (jsonObject.has("result"))
                    {
                        code = jsonObject.getJSONObject("result").getString("code");
                        description = jsonObject.getJSONObject("result").getString("description");
                    }
                    if (jsonObject.has("timestamp"))
                    {
                        timseStamp = jsonObject.getString("timestamp");
                    }

                    if ("00005".equalsIgnoreCase(code))
                    {
                        commResponseVO.setStatus("success");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setRemark(description);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timseStamp);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                }
            }

        }catch (JSONException e){
            transactionLogger.error("JSONException-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        boolean isTest = gatewayAccount.isTest();

        try
        {

            transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
            String token_url="";
            String endpoint_Url="";
            if(functions.isValueNull(commMerchantVO.getHostUrl())){
                token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
                endpoint_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYMENT_ENDPOINT_HOST_URL");

            }else {
                if(isTest){
                    token_url=RB.getString("TEST_TOKEN_URL");
                    endpoint_Url=RB.getString("TEST_ENDPOINT_PAYMENTID");

                }else {
                    token_url=RB.getString("LIVE_TOKEN_URL");
                    endpoint_Url=RB.getString("LIVE_ENDPOINT_PAYMENTID");
                }
            }

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse="";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken = "";
            if (functions.isValueNull(tokenResponse) && tokenResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(tokenResponse);
                if (jsonObject.has("AuthToken"))
                {
                    authToken = jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----" + authToken);
            }

            String transactionId="";
            if(functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId())){
                transactionId=commTransactionDetailsVO.getPreviousTransactionId();
            }
            transactionLogger.debug("transactionId------"+transactionId);

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String amount = commTransactionDetailsVO.getAmount();


            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("amount=" + amount);
            String checksum = WLUtils.getCheckSum(merchantId, key, transactionId);

            String voidRequest = ""
                    + "authentication.memberId="+gatewayAccount.getMerchantId()+""
                    + "&authentication.checksum="+checksum+""
                    + "&paymentType=RV";

            transactionLogger.error("voidRequest-----"+voidRequest);

            String voidResponse="";

            voidResponse = WLUtils.doPostHTTPSURLConnectionClient(endpoint_Url+transactionId, authToken, voidRequest);

            transactionLogger.error("voidResponse-----" + voidResponse);

            if(functions.isValueNull(voidResponse) && voidResponse.contains("{")){
                JSONObject jsonObject = new JSONObject(voidResponse);
                if(jsonObject!=null){
                    String paymentId="";
                    String code="";
                    String description="";
                    String timseStamp="";

                    if (jsonObject.has("paymentId"))
                    {
                        paymentId = jsonObject.getString("paymentId");
                    }
                    if (jsonObject.has("result"))
                    {
                        code = jsonObject.getJSONObject("result").getString("code");
                        description = jsonObject.getJSONObject("result").getString("description");
                    }
                    if (jsonObject.has("timestamp"))
                    {
                        timseStamp = jsonObject.getString("timestamp");
                    }

                    if ("00006".equalsIgnoreCase(code))
                    {
                        commResponseVO.setStatus("success");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setRemark(description);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timseStamp);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                }
            }

        }catch (JSONException e){
            transactionLogger.error("JSONException-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        boolean isTest = gatewayAccount.isTest();

        try
        {
            transactionLogger.debug("Host Url-----"+commMerchantVO.getHostUrl());
            String token_url="";
            String payout_Url="";
            if(functions.isValueNull(commMerchantVO.getHostUrl())){
                token_url="https://"+commMerchantVO.getHostUrl()+RB.getString("TOKEN_HOST_URL");
                payout_Url="https://"+commMerchantVO.getHostUrl()+RB.getString("PAYOUT_HOST_URL");

            }else {
                if(isTest){
                    token_url=RB.getString("TEST_TOKEN_URL");
                    payout_Url=RB.getString("TEST_PAYOUT_URL");

                }else {
                    token_url=RB.getString("LIVE_TOKEN_URL");
                    payout_Url=RB.getString("LIVE_PAYOUT_URL");
                }
            }

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse = "";

            tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(token_url, tokenRequest);

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken = "";
            if (functions.isValueNull(tokenResponse) && tokenResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(tokenResponse);
                if (jsonObject.has("AuthToken"))
                {
                    authToken = jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----" + authToken);
            }

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String orderId = commTransactionDetailsVO.getOrderDesc();
            String amount = commTransactionDetailsVO.getAmount();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            transactionLogger.debug("orderId=" + orderId);
            transactionLogger.debug("amount=" + amount);
            String checksum = WLUtils.getCheckSumPayout(merchantId, orderId, amount, key);

            String payoutRequest = ""
                    + "authentication.memberId="+gatewayAccount.getMerchantId()+""
                    + "&authentication.checksum="+checksum+""
                    + "&merchantTransactionId="+commTransactionDetailsVO.getOrderDesc()+""
                    + "&amount="+amount+""
                    + "&currency="+commTransactionDetailsVO.getCurrency()+""
                    + "&paymentId="+commTransactionDetailsVO.getPreviousTransactionId()+"";
            transactionLogger.error("payoutRequest-----"+payoutRequest);

            String payoutResponse="";

            payoutResponse = WLUtils.doPostHTTPSURLConnectionClient(payout_Url, authToken, payoutRequest);

            transactionLogger.error("payoutResponse-----" + payoutResponse);

            if(functions.isValueNull(payoutResponse) && payoutResponse.contains("{")){

                JSONObject jsonObject = new JSONObject(payoutResponse);
                if(jsonObject!=null){

                    String paymentId = "";
                    String respAmount = "";
                    String respCurrency = "";
                    String errorCode = "";
                    String description = "";
                    String descriptor="";
                    String transactionStatus = "";
                    String tmpl_currency = "";
                    String tmpl_amount = "";
                    String timestamp="";

                    if(jsonObject.has("paymentId")){
                        paymentId=jsonObject.getString("paymentId");
                    }
                    if(jsonObject.has("amount")){
                        respAmount=jsonObject.getString("amount");
                    }
                    if(jsonObject.has("currency")){
                        respCurrency=jsonObject.getString("currency");
                    }
                    if(jsonObject.has("descriptor")){
                        descriptor=jsonObject.getString("descriptor");
                    }
                    if(jsonObject.has("result")){
                        if(jsonObject.getJSONObject("result").has("code")){
                            errorCode=jsonObject.getJSONObject("result").getString("code");
                        }
                        if(jsonObject.getJSONObject("result").has("description")){
                            description=jsonObject.getJSONObject("result").getString("description");
                        }
                    }
                    if(jsonObject.has("transactionStatus")){
                        transactionStatus=jsonObject.getString("transactionStatus");
                        if(transactionStatus.equalsIgnoreCase("Y")){
                            transactionStatus="Transaction Successful";
                        }else if(transactionStatus.equalsIgnoreCase("N")){
                            transactionStatus="Transaction Failed";
                        }
                    }
                    if(jsonObject.has("tmpl_currency")){
                        tmpl_currency=jsonObject.getString("tmpl_currency");
                    }
                    if(jsonObject.has("tmpl_amount")){
                        tmpl_amount=jsonObject.getString("tmpl_amount");
                    }
                    if(jsonObject.has("timestamp")){
                        timestamp=jsonObject.getString("timestamp");
                    }
                    if(transactionStatus.equalsIgnoreCase("Y")){
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescriptor(descriptor);
                    }else if(transactionStatus.equalsIgnoreCase("P")){
                        commResponseVO.setStatus("pending");
                        commResponseVO.setRemark("Transaction Pending");
                    }else if(transactionStatus.equalsIgnoreCase("N")){
                        commResponseVO.setStatus("Transaction Failed");
                    }else {
                        commResponseVO.setStatus("error");
                        commResponseVO.setRemark("Error");
                    }
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setTransactionStatus(transactionStatus);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timestamp);
                    commResponseVO.setAmount(respAmount);
                    commResponseVO.setCurrency(respCurrency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setErrorCode(errorCode);
                }else {
                    commResponseVO.setStatus("Error");
                    commResponseVO.setRemark("Error");
                    commResponseVO.setDescription("Error");
                }
            }else {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Error");
            }

        }catch (JSONException e){
            transactionLogger.error("JSONException-----",e);
        }
        return commResponseVO;
    }

    // @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        try
        {

            String tokenRequest = ""
                    + "authentication.partnerId=" + gatewayAccount.getFRAUD_FTP_PATH() + ""
                    + "&merchant.username=" + gatewayAccount.getFRAUD_FTP_USERNAME() + ""
                    + "&authentication.sKey=" + gatewayAccount.getFRAUD_FTP_PASSWORD() + "";

            transactionLogger.error("tokenRequest-----" + tokenRequest);

            String tokenResponse = "";
            if (isTest)
            {
                tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_TOKEN_URL"), tokenRequest);
            }
            else
            {
                tokenResponse = WLUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_TOKEN_URL"), tokenRequest);
            }

            transactionLogger.error("tokenResponse-----" + tokenResponse);

            String authToken = "";
            if (functions.isValueNull(tokenResponse) && tokenResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(tokenResponse);
                if (jsonObject.has("AuthToken"))
                {
                    authToken = jsonObject.getString("AuthToken");
                }
                transactionLogger.error("authToken-----" + authToken);
            }

            String merchantId = gatewayAccount.getMerchantId();
            String key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String trackingId = commTransactionDetailsVO.getOrderId();

            transactionLogger.debug("merchantId=" + merchantId);
            transactionLogger.debug("key=" + key);
            String checksum = WLUtils.getCheckSum(merchantId, key, trackingId);

            String inquiryRequest = ""
                    + "authentication.memberId="+gatewayAccount.getMerchantId()+""
                    + "&authentication.checksum="+checksum+""
                    + "&paymentType=IN"
                    + "&idType=MID";

            transactionLogger.error("inquiryRequest-----"+inquiryRequest);

            String inquiryResponse="";
            if(isTest){
                inquiryResponse = WLUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ENDPOINT_PAYMENTID")+trackingId, authToken, inquiryRequest);
            }else{
                inquiryResponse = WLUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ENDPOINT_PAYMENTID")+trackingId, authToken, inquiryRequest);
            }
            transactionLogger.error("inquiryResponse-----" + inquiryResponse);

            if(functions.isValueNull(inquiryResponse) && inquiryResponse.contains("{")){
                JSONObject jsonObject = new JSONObject(inquiryResponse);
                if(jsonObject!=null){

                    String paymentId = "";
                    String respAmount = "";
                    String respCurrency = "";
                    String errorCode = "";
                    String description = "";
                    String descriptor="";
                    String transactionStatus = "";
                    String tmpl_currency = "";
                    String tmpl_amount = "";
                    String timestamp="";
                    String status="";
                    String transType="";

                    if(jsonObject.has("paymentId")){
                        paymentId=jsonObject.getString("paymentId");
                    }
                    if(jsonObject.has("status")){
                        status=jsonObject.getString("status");
                    }
                    if(jsonObject.has("amount")){
                        respAmount=jsonObject.getString("amount");
                    }
                    if(jsonObject.has("currency")){
                        respCurrency=jsonObject.getString("currency");
                    }
                    if(jsonObject.has("descriptor")){
                        descriptor=jsonObject.getString("descriptor");
                    }
                    if(jsonObject.has("result")){
                        if(jsonObject.getJSONObject("result").has("code")){
                            errorCode=jsonObject.getJSONObject("result").getString("code");
                        }
                        if(jsonObject.getJSONObject("result").has("description")){
                            description=jsonObject.getJSONObject("result").getString("description");
                        }
                    }
                    if(jsonObject.has("transactionStatus")){
                        transactionStatus=jsonObject.getString("transactionStatus");
                        if(transactionStatus.equalsIgnoreCase("Y")){
                            transactionStatus="Transaction Successful";
                        }else if(transactionStatus.equalsIgnoreCase("N")){
                            transactionStatus="Transaction Failed";
                        }
                    }
                    if(jsonObject.has("tmpl_currency")){
                        tmpl_currency=jsonObject.getString("tmpl_currency");
                    }
                    if(jsonObject.has("tmpl_amount")){
                        tmpl_amount=jsonObject.getString("tmpl_amount");
                    }
                    if(jsonObject.has("timestamp")){
                        timestamp=jsonObject.getString("timestamp");
                    }

                    commResponseVO.setTransactionType(WLUtils.getTranstype(status));
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setTransactionStatus(transactionStatus);
                    commResponseVO.setRemark(description);
                    commResponseVO.setDescription(description);
                    commResponseVO.setBankTransactionDate(timestamp);
                    commResponseVO.setAmount(respAmount);
                    commResponseVO.setCurrency(respCurrency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setErrorCode(errorCode);

                    }else {
                    commResponseVO.setStatus("Error");
                    commResponseVO.setRemark("Error");
                    commResponseVO.setDescription("Error");
                }
            }else {
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Error");
            }


        }catch (JSONException e){
            transactionLogger.error("JSONException-----",e);
        }
        return commResponseVO;
    }
}
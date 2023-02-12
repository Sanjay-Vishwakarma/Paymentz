package com.payment.cardpay;


import com.directi.pg.*;
import com.directi.pg.Base64;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.request.PZInquiryRequest;
import com.payment.response.PZInquiryResponse;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 7/24/2018.
 */
public class CardPayPaymentGateway extends AbstractPaymentGateway
{
    private static CardPayLogger transactionLogger = new CardPayLogger(CardPayPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.cardpay");

    public static final String GATEWAY_TYPE = "cardpay";

    public CardPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public static void main(String[] args)
    {
        try
        {

           String login="aashay.gandhi@transactworld.com2";
            String password="Paymentz@789";

            String userPassword = login + ":" + password;
            String encodedCredentials = Base64.encode(userPassword.getBytes());

                String InquiryResponse = "";

            String clientPass = login+":"+password;

            System.out.println(RB.getString("INQUIRY_LIVE_URL") + "number=6563492");
            InquiryResponse = CardPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE_URL") + "number=6563492", "Basic", encodedCredentials);
            //}
            System.out.println("InquiryResponse------" + InquiryResponse);

        }
        catch (Exception e){
            System.out.println("error----------------" + e);
        }
    }


    @Override
    public String getMaxWaitDays() {   return null;   }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO             = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO         = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO     = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO           = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String addressRequired          = gatewayAccount.getAddressValidation();

        transactionLogger.debug("addressRequired-----"+addressRequired);

        boolean isTest          = gatewayAccount.isTest();
        String orderDesc        = commTransactionDetailsVO.getOrderId();
        String threeDsVersion   = gatewayAccount.getThreeDsVersion();

        String ip="";
        String state="NA";
        String street="NA";
        String city="NA";
        String country      = "NA";
        String zip="";

        String TOKEN_REQUEST_URL    ="";
        String SALE_REQUEST_URL     ="";

        String email    = "customer@gmail.com";
        String phone    = "1111111111";
        String ccPhone  = "+11";
        String addr_line_2  = "NA";

        String currency         = "";
        String pan          = "";
        String holder       = "";
        String security_code= "";
        String expiration   = "";

        String payment_method   = "BANKCARD";
        String grant_type       = "password";
        String terminal_code                = gatewayAccount.getMerchantId();
        String terminal_password            = gatewayAccount.getFRAUD_FTP_PATH();
        StringBuffer tokenRequestParameter  = new StringBuffer();

        String access_token ="";
        String message ="";
        String termUrl = "";


        try
        {

            transactionLogger.debug("redirect url------------> "+commTransactionDetailsVO.getRedirectUrl());
            transactionLogger.debug("threeDsVersion ------------> "+threeDsVersion);
            transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL")+trackingID;
                transactionLogger.error("from RB----"+termUrl);
            }

           if(functions.isValueNull(commAddressDetailsVO.getState())){
               state = commAddressDetailsVO.getState();
           }
           if(functions.isValueNull(commAddressDetailsVO.getStreet())){
               street = commAddressDetailsVO.getStreet();
           }
           if(functions.isValueNull(commAddressDetailsVO.getCity())){
               city = commAddressDetailsVO.getCity();
           }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
               zip = commAddressDetailsVO.getZipCode();
           }

           if (orderDesc.equals(trackingID))
           {
               orderDesc = commTransactionDetailsVO.getMerchantOrderId();
           }
           if (!functions.isValueNull(orderDesc))
           {
               orderDesc = commTransactionDetailsVO.getOrderDesc();
           }

           if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
               ip  = commAddressDetailsVO.getCardHolderIpAddress();
           }else {
               ip  = commAddressDetailsVO.getIp();
           }


            String amount=commTransactionDetailsVO.getAmount();
            if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                double amt      = Double.parseDouble(commTransactionDetailsVO.getAmount());
                double roundOff = Math.round(amt);
                int value       = (int)roundOff;
                amount=String.valueOf(value);
            }

            if("3Dsv2".equalsIgnoreCase(threeDsVersion)){

                JSONObject jsonRequestObject        = new JSONObject();
                JSONObject requestJSON              = new JSONObject();
                JSONObject customerJSON             = new JSONObject();
                JSONObject merchant_orderJSON       = new JSONObject();
                JSONObject payment_dataJSON         = new JSONObject();
                JSONObject return_urlsJSON          = new JSONObject();
                JSONObject card_accountJSON         = new JSONObject();
                JSONObject cardJSON                 = new JSONObject();
                JSONObject billing_addressJSON      = new JSONObject();

                SimpleDateFormat simpleDateFormat       = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date       = new Date();
                String time     = simpleDateFormat.format(date);

                if (isTest)
                {
                    TOKEN_REQUEST_URL   = RB.getString("TEST_TOKEN_URL");
                    SALE_REQUEST_URL    = RB.getString("TEST_SALE_URL");
                }
                else
                {
                    TOKEN_REQUEST_URL   = RB.getString("LIVE_TOKEN_URL");
                    SALE_REQUEST_URL    = RB.getString("LIVE_SALE_URL");
                }

                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    country = commAddressDetailsVO.getCountry();
                }

                if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname())){
                    holder =  commAddressDetailsVO.getFirstname() +" "+commAddressDetailsVO.getLastname();
                }


                if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                    email = commAddressDetailsVO.getEmail();
                }
                if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                    phone = commAddressDetailsVO.getPhone();
                }
                if(functions.isValueNull(commAddressDetailsVO.getTelnocc())){
                    ccPhone = commAddressDetailsVO.getTelnocc();
                }

                pan             = commCardDetailsVO.getCardNum();
                security_code   = commCardDetailsVO.getcVV();
                expiration      = commCardDetailsVO.getExpMonth() +"/"+ commCardDetailsVO.getExpYear();
                phone           = ccPhone+" "+phone;
                currency        = commTransactionDetailsVO.getCurrency();

                tokenRequestParameter.append("grant_type="+grant_type);
                tokenRequestParameter.append("&password="+terminal_password);
                tokenRequestParameter.append("&terminal_code="+terminal_code);

                transactionLogger.error("Token Request Parameter >>> " +trackingID+" " + tokenRequestParameter.toString());

                String tokenResponse = CardPayUtils.doPostFormHTTPSURLConnectionClient(TOKEN_REQUEST_URL, tokenRequestParameter.toString());

                transactionLogger.error("Token Response Parameter >>> " +trackingID+" "+ tokenResponse.toString());
                if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                    JSONObject tokenResponsJSON =  new JSONObject(tokenResponse);

                    if(tokenResponsJSON != null){
                        if(tokenResponsJSON.has("access_token")){
                            access_token = tokenResponsJSON.getString("access_token");
                        }

                        if(tokenResponsJSON.has("message")){
                            message = tokenResponsJSON.getString("message");
                        }
                    }
                }

                if(functions.isValueNull(access_token)){

                    requestJSON.put("id",trackingID);
                    requestJSON.put("time",time);

                    jsonRequestObject.put("request",requestJSON);

                    cardJSON.put("pan",pan);
                    cardJSON.put("holder",holder);
                    cardJSON.put("security_code",security_code);
                    cardJSON.put("expiration",expiration);

                    if("Y".equalsIgnoreCase(addressRequired)){

                        billing_addressJSON.put("addr_line_1",street);
                        billing_addressJSON.put("addr_line_2",street +" "+city);
                        billing_addressJSON.put("city",city);
                        billing_addressJSON.put("country",country);
                        billing_addressJSON.put("state",state);
                        billing_addressJSON.put("zip",zip);

                        card_accountJSON.put("billing_address",billing_addressJSON);
                    }
                    card_accountJSON.put("card",cardJSON);

                    jsonRequestObject.put("card_account",card_accountJSON);

                    customerJSON.put("email",email);
                    customerJSON.put("phone",phone);
                    customerJSON.put("ip",ip);

                    jsonRequestObject.put("customer",customerJSON);

                    merchant_orderJSON.put("description",orderDesc);
                    merchant_orderJSON.put("id",trackingID);

                    jsonRequestObject.put("merchant_order",merchant_orderJSON);

                    payment_dataJSON.put("amount",amount);
                    payment_dataJSON.put("currency",currency);
                    jsonRequestObject.put("payment_data",payment_dataJSON);

                    jsonRequestObject.put("payment_method",payment_method);

                    return_urlsJSON.put("cancel_url",termUrl+"&status=cancel");
                    return_urlsJSON.put("decline_url",termUrl+"&status=decline");
                    return_urlsJSON.put("inprocess_url",termUrl+"&status=inprocess");
                    return_urlsJSON.put("success_url",termUrl+"&status=success");

                    jsonRequestObject.put("return_urls",return_urlsJSON);

                    String requestLogs                  = jsonRequestObject.toString();
                    JSONObject requestLogsJOSN          =  new JSONObject(requestLogs);
                    JSONObject cardLogsJOSN             =  new JSONObject();
                    JSONObject card_accountLogJSON      =  new JSONObject();

                    cardLogsJOSN.put("pan",functions.maskingPan(pan));
                    cardLogsJOSN.put("holder",functions.getNameMasking(holder));
                    cardLogsJOSN.put("security_code",functions.maskingNumber(security_code));
                    cardLogsJOSN.put("expiration",functions.maskingNumber(expiration));

                    if("Y".equalsIgnoreCase(addressRequired)){
                        card_accountLogJSON.put("billing_address",billing_addressJSON);
                    }

                    card_accountLogJSON.put("card",cardLogsJOSN);

                    requestLogsJOSN.put("card_account",card_accountLogJSON);

                    //transactionLogger.error("sale Request ---> "+trackingID+" >>>> " + jsonRequestObject.toString());
                    transactionLogger.error("sale Request Logs ---> "+trackingID+" >>>> " + requestLogsJOSN.toString());

                   String saleResponse = CardPayUtils.doPostHTTPSURLConnection(SALE_REQUEST_URL, jsonRequestObject.toString(),access_token);
                    transactionLogger.error("sale Response---"+trackingID+" >>>> " + saleResponse);

                    if(functions.isValueNull(saleResponse) && functions.isJSONValid(saleResponse)){
                        JSONObject responseJSON     =  new JSONObject(saleResponse);
                        JSONObject paymentdataJSON  =  null;
                        String redirect_url         = "";
                        String id                   = "";
                        String errorName            = "";
                        String errorMessage         = "";

                        if(responseJSON != null){

                            if(responseJSON.has("redirect_url")){
                                redirect_url = responseJSON.getString("redirect_url");
                            }
                            if(responseJSON.has("name")){
                                errorName = responseJSON.getString("name");
                            }
                            if(responseJSON.has("name")){
                                errorMessage = responseJSON.getString("name");
                            }

                            if(responseJSON.has("payment_data")){
                                paymentdataJSON = responseJSON.getJSONObject("payment_data");
                            }

                            if(paymentdataJSON !=null && paymentdataJSON.has("id")){
                                id = paymentdataJSON.getString("id");
                            }

                        }


                        if(functions.isValueNull(redirect_url))
                        {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            commResponseVO.setUrlFor3DRedirect(redirect_url);
                            commResponseVO.setThreeDSMethodData("3Dsv2");
                            commResponseVO.setThreeDVersion("3Dv2");
                            commResponseVO.setTransactionId(id);


                        }else if("INVALID_API_REQUEST".equalsIgnoreCase(errorName))
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setTransactionStatus("fail");
                            commResponseVO.setRemark(errorName);
                            commResponseVO.setDescription(errorMessage);
                            commResponseVO.setThreeDSMethodData("3Dsv2");
                            commResponseVO.setThreeDVersion("3Dv2");
                            commResponseVO.setTransactionId(id);
                        }else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                            commResponseVO.setThreeDSMethodData("3Dsv2");
                            commResponseVO.setThreeDVersion("3Dv2");
                            commResponseVO.setTransactionId(id);
                        }
                    }else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    }
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                }else{
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setThreeDVersion("3Dv2");
                }


            }else{

                String address="";
                if("Y".equalsIgnoreCase(addressRequired)){
                    address= "<billing country=\""+ commAddressDetailsVO.getCountry() +"\"" +
                            " state=\""+ESAPI.encoder().encodeForXML(state)+ "\" " +
                            " zip=\"" + commAddressDetailsVO.getZipCode() +"\" " +
                            " city=\""+ESAPI.encoder().encodeForXML(city)+"\" " +
                            " street=\""+ESAPI.encoder().encodeForXML(street)+"\"" +
                            " phone=\""+ commAddressDetailsVO.getPhone() +"\" />";
                }
                String saleParams =
                            "<order wallet_id=\""+gatewayAccount.getMerchantId()+"\"" +
                                " number=\""+ trackingID +"\" " +
                                " description=\""+ orderDesc + "\"" +
                                " currency=\""+commTransactionDetailsVO.getCurrency()+"\"" +
                                " amount=\""+ amount +"\" " +
                                " email=\"" + commAddressDetailsVO.getEmail() +"\"" +
                                " ip=\"" + ip +"\" " +
                                " is_two_phase=\"false\"" +    //false means direct sale
                                " return_url=\""+commTransactionDetailsVO.getRedirectUrl()+"\" >\n"  +
                            "<card num=\"" + commCardDetailsVO.getCardNum() +"\"" +
                                " holder=\""+ ESAPI.encoder().encodeForXML(commAddressDetailsVO.getFirstname()) +" " +ESAPI.encoder().encodeForXML(commAddressDetailsVO.getLastname())+"\"\n"+
                                " cvv=\""+ commCardDetailsVO.getcVV() +"\"" +
                                " expires=\""+ commCardDetailsVO.getExpMonth() +"/"+ commCardDetailsVO.getExpYear() +"\" />\n" +
                                    address+
                            "</order>";
                String saleParamsLogs="<order wallet_id=\""+gatewayAccount.getMerchantId()+"\"" +
                        " number=\""+ trackingID +"\" " +
                        " description=\""+ orderDesc + "\"" +
                        " currency=\""+commTransactionDetailsVO.getCurrency()+"\"" +
                        " amount=\""+ amount +"\" " +
                        " email=\"" + commAddressDetailsVO.getEmail() +"\"" +
                        " ip=\"" + ip +"\" " +
                        " is_two_phase=\"false\"" +    //false means direct sale
                        " return_url=\""+commTransactionDetailsVO.getRedirectUrl()+"\" >"  +
                        "<card num=\"" + functions.maskingPan(commCardDetailsVO.getCardNum()) +"\"" +
                        " holder=\""+ ESAPI.encoder().encodeForXML(commAddressDetailsVO.getFirstname()) +" " +ESAPI.encoder().encodeForXML(commAddressDetailsVO.getLastname())+"\""+
                        " cvv=\""+ functions.maskingNumber(commCardDetailsVO.getcVV()) +"\"" +
                        " expires=\""+ functions.maskingNumber(commCardDetailsVO.getExpMonth()) +"/"+ functions.maskingNumber(commCardDetailsVO.getExpYear()) +"\" />" +
                        address+
                        "</order>";

                transactionLogger.error("saleParams ---"+trackingID+"-- " + saleParamsLogs);

                String orderXML     = Base64.encode(saleParams.getBytes());
                String secret_word  = gatewayAccount.getFRAUD_FTP_PATH();
                String sha512       = CardPayUtils.sha512(saleParams.trim() + secret_word.trim());
                transactionLogger.error("secret_word------------" + secret_word);
                transactionLogger.error("orderXML------------" + orderXML);
                transactionLogger.error("sha512------------" + sha512);

                String saleRequest="orderXML="+URLEncoder.encode(orderXML,"UTF-8")+"&sha512="+URLEncoder.encode(sha512,"UTF-8")+"";
                transactionLogger.error("saleRequest--------"+trackingID+"-------" +saleRequest);

                String saleResponse = "";

                if (isTest)
                {
                    saleResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , saleRequest);
                }
                else
                {
                    saleResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , saleRequest);
                }
                transactionLogger.error("saleResponse---"+trackingID+"---" + saleResponse);

                if(functions.isValueNull(saleResponse)){
                    commResponseVO=CardPayUtils.readXmlResponse(saleResponse,gatewayAccount.getDisplayName());
                    if("pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus())){
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                        commResponseVO.setTerURL(termUrl);

                    }else {
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(" Error");
                    commResponseVO.setDescription(" Error");
                }
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
            }
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String addressRequired=gatewayAccount.getAddressValidation();

        transactionLogger.debug("addressRequired-----"+addressRequired);
        boolean isTest = gatewayAccount.isTest();
        String orderDesc = commTransactionDetailsVO.getOrderId();
        if (orderDesc.equals(trackingID))
        {
            orderDesc = commTransactionDetailsVO.getMerchantOrderId();
        }
        if (!functions.isValueNull(orderDesc))
        {
            orderDesc = commTransactionDetailsVO.getOrderDesc();
        }

        String ip="";
        if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress())){
            ip=commAddressDetailsVO.getCardHolderIpAddress();
        }else {
            ip=commAddressDetailsVO.getIp();
        }
        String state="NA";
        String street="NA";
        String city="NA";
        if(functions.isValueNull(commAddressDetailsVO.getState()))
            state=commAddressDetailsVO.getState();
        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            street=commAddressDetailsVO.getStreet();
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
            city=commAddressDetailsVO.getCity();
        try{
            String amount=commTransactionDetailsVO.getAmount();
            if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                double amt= Double.parseDouble(commTransactionDetailsVO.getAmount());
                double roundOff=Math.round(amt);
                int value=(int)roundOff;
                amount=String.valueOf(value);
            }

            String address="";
            if("Y".equalsIgnoreCase(addressRequired)){
                address= "<billing country=\""+ commAddressDetailsVO.getCountry() +"\"" +
                        " state=\""+ESAPI.encoder().encodeForXML(state)+ "\" " +
                        " zip=\"" + commAddressDetailsVO.getZipCode() +"\" " +
                        " city=\""+ESAPI.encoder().encodeForXML(city)+"\" " +
                        " street=\""+ESAPI.encoder().encodeForXML(street)+"\"" +
                        " phone=\""+ commAddressDetailsVO.getPhone() +"\" />";
            }

            String authParams =
                    "<order wallet_id=\""+gatewayAccount.getMerchantId()+"\"" +
                        " number=\""+ trackingID +"\" " +
                        " description=\""+ orderDesc + "\"" +
                        " currency=\""+commTransactionDetailsVO.getCurrency()+"\"" +
                        " amount=\""+ amount +"\" " +
                        " email=\"" + commAddressDetailsVO.getEmail() +"\"" +
                        " ip=\"" +ip+"\"" +
                        " is_two_phase=\"true\" " +    //false means direct sale
                        " return_url=\""+commTransactionDetailsVO.getRedirectUrl()+"\" >\n"  +

                    "<card num=\"" + commCardDetailsVO.getCardNum() +"\"" +
                        " holder=\""+ ESAPI.encoder().encodeForXML(commAddressDetailsVO.getFirstname()) +" " +ESAPI.encoder().encodeForXML(commAddressDetailsVO.getLastname())+"\"\n"+
                        " cvv=\""+ commCardDetailsVO.getcVV() +"\"" +
                        " expires=\""+ commCardDetailsVO.getExpMonth() +"/"+ commCardDetailsVO.getExpYear() +"\" />\n" +
                        address+
                            "</order>";

            String authParamsLog=
                    "<order wallet_id=\""+gatewayAccount.getMerchantId()+"\"" +
                            " number=\""+ trackingID +"\" " +
                            " description=\""+ orderDesc + "\"" +
                            " currency=\""+commTransactionDetailsVO.getCurrency()+"\"" +
                            " amount=\""+ amount +"\" " +
                            " email=\"" + commAddressDetailsVO.getEmail() +"\"" +
                            " ip=\"" +ip+"\"" +
                            " is_two_phase=\"true\" " +    //false means direct sale
                            " return_url=\""+commTransactionDetailsVO.getRedirectUrl()+"\" >"  +

                            "<card num=\"" + functions.maskingPan(commCardDetailsVO.getCardNum()) +"\"" +
                            " holder=\""+ ESAPI.encoder().encodeForXML(commAddressDetailsVO.getFirstname()) +" " +ESAPI.encoder().encodeForXML(commAddressDetailsVO.getLastname())+"\""+
                            " cvv=\""+ functions.maskingNumber(commCardDetailsVO.getcVV()) +"\"" +
                            " expires=\""+ functions.maskingNumber(commCardDetailsVO.getExpMonth()) +"/"+ functions.maskingNumber(commCardDetailsVO.getExpYear()) +"\" />" +
                            address+
                            "</order>";

            transactionLogger.error("authParams ---"+trackingID+"-- " + authParamsLog);

            String orderXML = Base64.encode(authParams.getBytes());
            String secret_word = gatewayAccount.getFRAUD_FTP_PATH();
            String sha512 = CardPayUtils.sha512(authParams + secret_word);
            transactionLogger.error("orderXML------------" + orderXML);
            transactionLogger.error("sha512------------" + sha512);

            String authRequest="orderXML="+URLEncoder.encode(orderXML,"UTF-8")+"&sha512="+URLEncoder.encode(sha512,"UTF-8")+"";
            transactionLogger.error("authRequest-------"+trackingID+"--------" +authRequest);

            String termUrl = "";
            transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL");
                transactionLogger.error("from RB----"+termUrl);
            }

            String authResponse = "";

            if (isTest)
            {
                authResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") , authRequest);
            }
            else
            {
                authResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") , authRequest);
            }

            transactionLogger.error("authResponse----"+trackingID+"--" + authResponse);

            if(functions.isValueNull(authResponse)){
                commResponseVO=CardPayUtils.readXmlResponse(authResponse,gatewayAccount.getDisplayName());
                if("pending3DConfirmation".equalsIgnoreCase(commResponseVO.getStatus())){
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                    commResponseVO.setTerURL(termUrl+trackingID);
                }else {
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String shaPassword=CardPayUtils.sha256(gatewayAccount.getFRAUD_FTP_PASSWORD().trim());

        boolean isTest = gatewayAccount.isTest();


        try{

            String captureRequest=
                    "client_login="+URLEncoder.encode(gatewayAccount.getFRAUD_FTP_USERNAME(), "UTF-8")+
                    "&client_password="+URLEncoder.encode(shaPassword, "UTF-8")+
                    "&id="+URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8")+
                    "&status_to="+URLEncoder.encode("capture","UTF-8");

            transactionLogger.error("captureRequest--"+trackingID+"--" + captureRequest);


            String captureResponse = "";

            if (isTest)
            {
                captureResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_TEST_URL") , captureRequest);
            }
            else
            {
                captureResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_LIVE_URL") , captureRequest);
            }


            transactionLogger.error("captureResponse---"+trackingID+"---" + captureResponse);
            if(functions.isValueNull(captureResponse)){
                commResponseVO=CardPayUtils.readXmlStatusResponse(captureResponse,gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }else {
                 commResponseVO.setStatus("fail");
                 commResponseVO.setRemark(" Error");
                 commResponseVO.setDescription(" Error");
            }
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String shaPassword=CardPayUtils.sha256(gatewayAccount.getFRAUD_FTP_PASSWORD().trim());
        boolean isTest = gatewayAccount.isTest();

        try{

            String voidRequest=
                    "client_login="+URLEncoder.encode(gatewayAccount.getFRAUD_FTP_USERNAME(), "UTF-8")+
                    "&client_password="+URLEncoder.encode(shaPassword, "UTF-8") +
                    "&id="+URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8")+
                    "&status_to="+URLEncoder.encode("void","UTF-8");

            transactionLogger.error("voidRequest--"+trackingID+"--" + voidRequest);


            String voidResponse = "";

            if (isTest)
            {
                voidResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_TEST_URL") , voidRequest);
            }
            else
            {
                voidResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_LIVE_URL") , voidRequest);
            }


            transactionLogger.error("voidResponse---"+trackingID+"---" + voidResponse);
            if(functions.isValueNull(voidResponse)){
                commResponseVO=CardPayUtils.readXmlStatusResponse(voidResponse,gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }


        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        String shaPassword                  = CardPayUtils.sha256(gatewayAccount.getFRAUD_FTP_PASSWORD().trim());
        boolean isTest                      = gatewayAccount.isTest();

        String TOKEN_REQUEST_URL   ="";
        String REFUND_REQUEST_URL  ="";
        String description         ="Return order";
        String currency            ="";
        String access_token            ="";
        String message            ="";

        String threeDsVersion   = gatewayAccount.getThreeDsVersion();
        String paymentId        = commTransactionDetailsVO.getPreviousTransactionId();
        StringBuffer tokenRequestParameter  = new StringBuffer();

        String payment_method   = "BANKCARD";
        String grant_type       = "password";
        String terminal_code                = gatewayAccount.getMerchantId();
        String terminal_password            = gatewayAccount.getFRAUD_FTP_PATH();
        UUID uuid1              = UUID.randomUUID();
        String RequestId        = uuid1.toString();
        try
        {
            String amount   = commTransactionDetailsVO.getAmount();

            if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                double amt      = Double.parseDouble(commTransactionDetailsVO.getAmount());
                double roundOff = Math.round(amt);
                int value       = (int)roundOff;
                amount          = String.valueOf(value);
            }

            if("3Dsv2".equalsIgnoreCase(threeDsVersion))
            {
                JSONObject requestParamJSON    = new JSONObject();
                JSONObject requestJSON         = new JSONObject();
                JSONObject merchant_orderJSON  = new JSONObject();
                JSONObject payment_dataJSON    = new JSONObject();
                JSONObject refund_dataJSON     = new JSONObject();

                SimpleDateFormat simpleDateFormat       = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date       = new Date();
                String time     = simpleDateFormat.format(date);

                currency =  commTransactionDetailsVO.getCurrency();

                if(functions.isValueNull(commTransactionDetailsVO.getOrderDesc())){
                    description = commTransactionDetailsVO.getOrderDesc();
                }

                if (isTest)
                {
                    TOKEN_REQUEST_URL   = RB.getString("TEST_TOKEN_URL");
                    REFUND_REQUEST_URL  = RB.getString("TEST_REFUND_URL");
                }
                else
                {
                    TOKEN_REQUEST_URL       = RB.getString("LIVE_TOKEN_URL");
                    REFUND_REQUEST_URL      = RB.getString("LIVE_REFUND_URL");
                }

                requestJSON.put("id",RequestId);
                requestJSON.put("time",time);
                requestParamJSON.put("request",requestJSON);

                merchant_orderJSON.put("description",description);
                merchant_orderJSON.put("id",trackingID);
                requestParamJSON.put("merchant_order",merchant_orderJSON);

                payment_dataJSON.put("id",paymentId);
                requestParamJSON.put("payment_data",payment_dataJSON);

                refund_dataJSON.put("amount",amount);
                refund_dataJSON.put("currency",currency);
                requestParamJSON.put("refund_data",refund_dataJSON);

                tokenRequestParameter.append("grant_type="+grant_type);
                tokenRequestParameter.append("&password="+terminal_password);
                tokenRequestParameter.append("&terminal_code="+terminal_code);

                transactionLogger.error("Token Request Parameter >>> " +trackingID+" " + tokenRequestParameter.toString());

                String tokenResponse = CardPayUtils.doPostFormHTTPSURLConnectionClient(TOKEN_REQUEST_URL, tokenRequestParameter.toString());

                transactionLogger.error("Token Response Parameter >>> " +trackingID+" "+ tokenResponse.toString());
                if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                    JSONObject tokenResponsJSON =  new JSONObject(tokenResponse);

                    if(tokenResponsJSON != null){
                        if(tokenResponsJSON.has("access_token")){
                            access_token = tokenResponsJSON.getString("access_token");
                        }

                        if(tokenResponsJSON.has("message")){
                            message = tokenResponsJSON.getString("message");
                        }
                    }
                }

                if(functions.isValueNull(access_token))
                {
                    transactionLogger.error("Refund Request Parameter >>> " +trackingID+" " + requestParamJSON.toString());

                    String refundResponse = CardPayUtils.doPostHTTPSURLConnection(REFUND_REQUEST_URL, requestParamJSON.toString(),access_token);

                    transactionLogger.error("Refund Response Parameter >>> " +trackingID+" " + refundResponse.toString());

                    if(functions.isValueNull(refundResponse) && functions.isJSONValid(refundResponse) ){

                        JSONObject responseJSON     =  new JSONObject(refundResponse);
                        JSONObject refund_dataResJSON  =  null;

                        String id           = "";
                        String status       = "";
                        String amountRes    = "";
                        String currencyRes  = "";
                        String created      = "";
                        String auth_code    = "";
                        String rrn          = "";

                        if(responseJSON.has("refund_data")){
                            refund_dataResJSON =  responseJSON.getJSONObject("refund_data");
                        }

                        if(refund_dataResJSON != null  ){

                            if(refund_dataResJSON.has("id")){
                                id = refund_dataResJSON.getString("id");
                            }

                            if(refund_dataResJSON.has("status")){
                                status = refund_dataResJSON.getString("status");
                            }
                            if(refund_dataResJSON.has("amount")){
                                amount = refund_dataResJSON.getString("amount");
                            }
                            if(refund_dataResJSON.has("currency")){
                                currency = refund_dataResJSON.getString("currency");
                            }
                            if(refund_dataResJSON.has("created")){
                                created = refund_dataResJSON.getString("created");
                            }
                            if(refund_dataResJSON.has("auth_code")){
                                auth_code = refund_dataResJSON.getString("auth_code");
                            }
                            if(refund_dataResJSON.has("rrn")){
                                rrn = refund_dataResJSON.getString("rrn");
                            }

                        }


                        if(status.equalsIgnoreCase("COMPLETED"))
                        {
                            commResponseVO.setStatus("Success");
                            commResponseVO.setTransactionStatus("Success");
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAmount(amountRes);
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currencyRes);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setResponseTime(created);

                        }else if(status.equalsIgnoreCase("DECLINED"))
                        {
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionStatus("failed");
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAmount(amountRes);
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currencyRes);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setResponseTime(created);
                        }else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }

                        commResponseVO.setMerchantId(terminal_code);
                    }

                }else{
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }


            }else
            {
                String refundRequest=
                        "client_login="+URLEncoder.encode(gatewayAccount.getFRAUD_FTP_USERNAME(), "UTF-8")+
                        "&client_password="+URLEncoder.encode(shaPassword, "UTF-8")+
                        "&id="+ URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8")+
                        "&status_to="+URLEncoder.encode("refund", "UTF-8")+
                        "&amount="+amount+
                        "&reason="+commTransactionDetailsVO.getOrderDesc();

                transactionLogger.error("refundRequest--"+trackingID+"--" + refundRequest);
                String refundResponse = "";

                if (isTest)
                {
                    refundResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_TEST_URL") , refundRequest);
                }
                else
                {
                    refundResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_LIVE_URL") , refundRequest);
                }


                transactionLogger.error("refundResponse---"+trackingID+"---" + refundResponse);
                if(functions.isValueNull(refundResponse)){
                    commResponseVO=CardPayUtils.readXmlStatusResponse(refundResponse,gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(" Error");
                    commResponseVO.setDescription(" Error");
                }
            }



        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String shaPassword              = CardPayUtils.sha256(gatewayAccount.getFRAUD_FTP_PASSWORD().trim());
        boolean isTest                  = gatewayAccount.isTest();
        String threeDsVersion           = gatewayAccount.getThreeDsVersion();

        String city         = "NA";
        String country      = "NA";
        String state        = "NA";
        String street       = "NA";
        String zip          = "";
        String currency     = "";
        String email        = "customer@gmail.com";
        String phone        = "1111111111";
        String ccPhone      = "+11";
        String ip           = "";

        String pan          = "";
        String holder       = "";
        String expiration   = "";

        String TOKEN_REQUEST_URL    = "";
        String PAYOUT_REQUEST_URL   = "";

        String access_token     = "";
        String message          = "";
        String payment_method   = "BANKCARD";
        String grant_type       = "password";
        String terminal_code                = gatewayAccount.getMerchantId();
        String terminal_password            = gatewayAccount.getFRAUD_FTP_PATH();
        StringBuffer tokenRequestParameter  = new StringBuffer();


        transactionLogger.debug("transaction id --------------- "+commTransactionDetailsVO.getPaymentId()+"---"+commTransactionDetailsVO.getOrderId());
        try{
            String amount       = commTransactionDetailsVO.getAmount();

            if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency())){
                double amt          = Double.parseDouble(commTransactionDetailsVO.getAmount());
                double roundOff     = Math.round(amt);
                int value           = (int)roundOff;
                amount              = String.valueOf(value);
            }

            transactionLogger.error("threeDsVersion ----> "+trackingId+" " + threeDsVersion);

            if("3Dsv2".equalsIgnoreCase(threeDsVersion)){
                JSONObject jsonRequestObject        =  new JSONObject();
                JSONObject requestJSON              = new JSONObject();
                JSONObject customerJSON             = new JSONObject();
                JSONObject merchant_orderJSON       = new JSONObject();
                JSONObject payout_dataJSON         = new JSONObject();
                JSONObject card_accountJSON         = new JSONObject();
                JSONObject cardJSON                 = new JSONObject();
                JSONObject billing_addressJSON      = new JSONObject();

                UUID uuid1              = UUID.randomUUID();
                String RequestId        = uuid1.toString();

                SimpleDateFormat simpleDateFormat       = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date                   = new Date();
                String time   = simpleDateFormat.format(date);

                if (isTest)
                {
                    TOKEN_REQUEST_URL  = RB.getString("TEST_TOKEN_URL");
                    PAYOUT_REQUEST_URL = RB.getString("TEST_PAYOUT_URL");
                }
                else
                {
                    TOKEN_REQUEST_URL   = RB.getString("LIVE_TOKEN_URL");
                    PAYOUT_REQUEST_URL  = RB.getString("LIVE_PAYOUT_URL");
                }

                if(functions.isValueNull(commAddressDetailsVO.getState())){
                    state = commAddressDetailsVO.getState();
                }
                if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                    street = commAddressDetailsVO.getStreet();
                }
                if(functions.isValueNull(commAddressDetailsVO.getCity())){
                    city = commAddressDetailsVO.getCity();
                }
                if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                    zip = commAddressDetailsVO.getZipCode();
                }

                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    country = commAddressDetailsVO.getCountry();
                }

                if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname())){
                    holder =  commAddressDetailsVO.getFirstname() +" "+commAddressDetailsVO.getLastname();
                }


                if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                    email = commAddressDetailsVO.getEmail();
                }
                if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                    phone = commAddressDetailsVO.getPhone();
                }
                if(functions.isValueNull(commAddressDetailsVO.getTelnocc())){
                    ccPhone = commAddressDetailsVO.getTelnocc();
                }

                pan             = commCardDetailsVO.getCardNum();
                expiration      = commCardDetailsVO.getExpMonth() +"/"+ commCardDetailsVO.getExpYear();
                phone           = ccPhone+" "+phone;
                currency        = commTransactionDetailsVO.getCurrency();

                tokenRequestParameter.append("grant_type="+grant_type);
                tokenRequestParameter.append("&password="+terminal_password);
                tokenRequestParameter.append("&terminal_code="+terminal_code);

                transactionLogger.error("Token Request Parameter >>> " +trackingId+" " + tokenRequestParameter.toString());

                String tokenResponse = CardPayUtils.doPostFormHTTPSURLConnectionClient(TOKEN_REQUEST_URL, tokenRequestParameter.toString());

                transactionLogger.error("Token Response Parameter >>> " +trackingId+" "+ tokenResponse.toString());

                if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                    JSONObject tokenResponsJSON =  new JSONObject(tokenResponse);

                    if(tokenResponsJSON != null){
                        if(tokenResponsJSON.has("access_token")){
                            access_token = tokenResponsJSON.getString("access_token");
                        }

                        if(tokenResponsJSON.has("message")){
                            message = tokenResponsJSON.getString("message");
                        }
                    }
                }

                if(functions.isValueNull(access_token)){
                    requestJSON.put("id",RequestId);
                    requestJSON.put("time",time);

                    jsonRequestObject.put("request",requestJSON);

                    cardJSON.put("pan",pan);
                    cardJSON.put("expiration",expiration);

                    billing_addressJSON.put("addr_line_1",street);
                    billing_addressJSON.put("addr_line_2",street +" "+city );
                    billing_addressJSON.put("city",city);
                    billing_addressJSON.put("country",country);
                    billing_addressJSON.put("state",state);
                    billing_addressJSON.put("zip",zip);

                    card_accountJSON.put("billing_address",billing_addressJSON);
                    card_accountJSON.put("card",cardJSON);
                    card_accountJSON.put("recipient_info",holder);

                    jsonRequestObject.put("card_account",card_accountJSON);

                    customerJSON.put("email",email);
                    customerJSON.put("phone",phone);
                    customerJSON.put("ip",ip);

                    jsonRequestObject.put("customer",customerJSON);

                    merchant_orderJSON.put("id",trackingId);

                    jsonRequestObject.put("merchant_order",merchant_orderJSON);

                    payout_dataJSON.put("amount",amount);
                    payout_dataJSON.put("currency",currency);
                    jsonRequestObject.put("payout_data",payout_dataJSON);

                    jsonRequestObject.put("payment_method",payment_method);


                    String requestLogs                  = jsonRequestObject.toString();
                    JSONObject requestLogsJOSN          =  new JSONObject(requestLogs);
                    JSONObject cardLogsJOSN             =  new JSONObject();
                    JSONObject card_accountLogJSON      =  new JSONObject();

                    cardLogsJOSN.put("pan",functions.maskingPan(pan));
                    cardLogsJOSN.put("expiration",functions.maskingNumber(expiration));

                    card_accountLogJSON.put("billing_address",billing_addressJSON);
                    card_accountLogJSON.put("card",cardLogsJOSN);
                    card_accountLogJSON.put("recipient_info",functions.getNameMasking(holder));

                    requestLogsJOSN.put("card_account",card_accountLogJSON);

                    transactionLogger.error("Payout Request Logs ---> "+trackingId+" >>>> " + requestLogsJOSN.toString());
                    //transactionLogger.error("Payout Request  ---> "+trackingId+" >>>> " + jsonRequestObject.toString());

                    String payOutResponse = CardPayUtils.doPostHTTPSURLConnection(PAYOUT_REQUEST_URL, jsonRequestObject.toString(),access_token);
                    transactionLogger.error("Payout Response---"+trackingId+" >>>> " + payOutResponse);

                    if(functions.isValueNull(payOutResponse) && functions.isJSONValid(payOutResponse)){
                        JSONObject responseJOSN     = new JSONObject(payOutResponse);
                        JSONObject payoutDataJOSN   = null;

                        String id               = "";
                        String created          = "";
                        String status           = "";
                        String amountResp       = "";
                        String currencyResp     = "";
                        String name     = "";

                        if(responseJOSN.has("payout_data")){
                            payoutDataJOSN = responseJOSN.getJSONObject("payout_data");
                        }
                        if(responseJOSN.has("name")){
                            name = responseJOSN.getString("name");
                        }
                        if(responseJOSN.has("message")){
                            message = responseJOSN.getString("message");
                        }

                        if(payoutDataJOSN != null){

                            if(payoutDataJOSN.has("id")){
                                id = payoutDataJOSN.getString("id");
                            }
                            if(payoutDataJOSN.has("created")){
                                created = payoutDataJOSN.getString("created");
                            }
                            if(payoutDataJOSN.has("status")){
                                status = payoutDataJOSN.getString("status");
                            }
                            if(payoutDataJOSN.has("amountResp")){
                                amountResp = payoutDataJOSN.getString("amountResp");
                                double amt  = Double.parseDouble(amountResp);
                                amountResp      = String.format("%.2f", amt);
                            }
                            if(payoutDataJOSN.has("currencyResp")){
                                currencyResp = payoutDataJOSN.getString("currencyResp");
                            }
                        }

                        if("COMPLETED".equalsIgnoreCase(status)){
                            commResponseVO.setStatus("Success");
                            commResponseVO.setTransactionStatus("Success");
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAmount(amountResp);
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currencyResp);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setMerchantId(terminal_code);
                        }else if("DECLINED".equalsIgnoreCase(status) ){
                            commResponseVO.setStatus("Failed");
                            commResponseVO.setTransactionStatus("Failed");
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);
                            commResponseVO.setAmount(amountResp);
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currencyResp);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setMerchantId(terminal_code);
                        }else if("INVALID_API_REQUEST".equalsIgnoreCase(name) ){
                            commResponseVO.setStatus("Failed");
                            commResponseVO.setTransactionStatus("Failed");
                            commResponseVO.setRemark(name);
                            commResponseVO.setDescription(message);
                            commResponseVO.setMerchantId(terminal_code);
                        }else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }


                    }else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }



                }else{
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                }


            }else{

                String payoutRequest=
                        "client_login="+URLEncoder.encode(gatewayAccount.getFRAUD_FTP_USERNAME(), "UTF-8")+
                                "&client_password="+URLEncoder.encode(shaPassword, "UTF-8")+
                                "&id="+URLEncoder.encode(commTransactionDetailsVO.getPaymentId(), "UTF-8")+
                                "&status_to="+URLEncoder.encode("payout", "UTF-8")+
                                "&amount="+amount+
                                "&reason="+commTransactionDetailsVO.getOrderDesc();

                transactionLogger.error("payoutRequest--"+trackingId+"--" + payoutRequest);


                String payoutResponse = "";

                if (isTest)
                {
                    payoutResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_TEST_URL") , payoutRequest);
                }
                else
                {
                    payoutResponse = CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("ORDER_STATUS_LIVE_URL") , payoutRequest);
                }


                transactionLogger.error("payoutResponse---"+trackingId+"---" + payoutResponse);
                if(functions.isValueNull(payoutResponse)){
                    commResponseVO=CardPayUtils.readXmlStatusResponse(payoutResponse,gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(" Error");
                    commResponseVO.setDescription(" Error");
                }
            }



        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        Functions functions                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String orderid                                      = commTransactionDetailsVO.getOrderId();
        String merchantid                                   = gatewayAccount.getMerchantId();


        PZInquiryRequest pzInquiryRequest = new PZInquiryRequest();
        pzInquiryRequest.setTrackingId(Integer.parseInt(orderid));
        setInquiryVOParamsExtension(commRequestVO, pzInquiryRequest);

        String userName         = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD().trim();
        String userPassword     = userName + ":" + password;

        //3DSV2
        String threeDsVersion   = gatewayAccount.getThreeDsVersion();
        String terminal_code                = gatewayAccount.getMerchantId();
        String terminal_password            = gatewayAccount.getFRAUD_FTP_PATH();
        transactionLogger.error("Middetails------for"  + orderid+ ":::::::" +userPassword);

        String encodedCredentials = Base64.encode(userPassword.getBytes());

        boolean isTest = gatewayAccount.isTest();
        //"startMillis="+commTransactionDetailsVO.getResponsetime()+
        String payment_id = commTransactionDetailsVO.getPreviousTransactionId();

        String INQUIRY_REQUEST_URL  = "";
        String TOKEN_REQUEST_URL    = "";
        String statusPrevious       = "";

        String payment_method               = "BANKCARD";
        String grant_type                   = "password";
        String access_token                 = "";
        String message                      = "";
        StringBuffer tokenRequestParameter  = new StringBuffer();

        try{
               String inquiryRequest=
                     "number="+orderid+
                    "&walletId="+merchantid;
            transactionLogger.error("processInquiry threeDsVersion >>> " +orderid+" "+threeDsVersion);


            if("3Dsv2".equalsIgnoreCase(threeDsVersion))
            {
                if(functions.isValueNull(commTransactionDetailsVO.getPrevTransactionStatus())){
                    statusPrevious = commTransactionDetailsVO.getPrevTransactionStatus();
                }
                transactionLogger.error("processInquiry statusPrevious >>> " +orderid+" "+statusPrevious);
                if (isTest)
                {
                    TOKEN_REQUEST_URL       = RB.getString("TEST_TOKEN_URL");
                    INQUIRY_REQUEST_URL     = RB.getString("TEST_INQUIRY_URL")+payment_id;
                }
                else
                {
                    TOKEN_REQUEST_URL   = RB.getString("LIVE_TOKEN_URL");
                    INQUIRY_REQUEST_URL    = RB.getString("LIVE_INQUIRY_URL")+payment_id;
                }

                tokenRequestParameter.append("grant_type="+grant_type);
                tokenRequestParameter.append("&password="+terminal_password);
                tokenRequestParameter.append("&terminal_code="+terminal_code);

                transactionLogger.error("Token Request Parameter >>> " +orderid+" " + tokenRequestParameter.toString());

                String tokenResponse = CardPayUtils.doPostFormHTTPSURLConnectionClient(TOKEN_REQUEST_URL, tokenRequestParameter.toString());

                transactionLogger.error("Token Response Parameter >>> " +orderid+" "+ tokenResponse.toString());
                if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                    JSONObject tokenResponsJSON =  new JSONObject(tokenResponse);

                    if(tokenResponsJSON != null){
                        if(tokenResponsJSON.has("access_token")){
                            access_token = tokenResponsJSON.getString("access_token");
                        }

                    }
                }

                if(functions.isValueNull(access_token)){

                    transactionLogger.error("Inquiry Request >>> " +orderid+" "+ INQUIRY_REQUEST_URL);

                    String responseString = CardPayUtils.doGetHTTPSURLConnectionClient(INQUIRY_REQUEST_URL,access_token);

                    transactionLogger.error("Inquiry Response >>> " +orderid+" "+ responseString);

                    if(functions.isValueNull(responseString) && functions.isJSONValid(responseString)){

                        JSONObject responseJSON     =  new JSONObject(responseString);
                        JSONObject payment_dataJSON  =  null;

                        String id       ="";
                        String status   ="";
                        String amount   ="";
                        String currency ="";
                        String created  ="";
                        String auth_code  ="";

                        if(responseJSON.has("payment_data")){
                            payment_dataJSON =  responseJSON.getJSONObject("payment_data");
                        }

                        if(payment_dataJSON != null  ){

                            if(payment_dataJSON.has("id")){
                                id = payment_dataJSON.getString("id");
                            }

                            if(payment_dataJSON.has("status")){
                                status = payment_dataJSON.getString("status");
                            }
                            if(payment_dataJSON.has("amount")){
                                amount      = payment_dataJSON.getString("amount");
                                double amt  = Double.parseDouble(amount);
                                amount      = String.format("%.2f", amt);
                            }
                            if(payment_dataJSON.has("currency")){
                                currency = payment_dataJSON.getString("currency");
                            }
                            if(payment_dataJSON.has("created")){
                                created = payment_dataJSON.getString("created");
                            }
                            if(payment_dataJSON.has("auth_code")){
                                auth_code = payment_dataJSON.getString("auth_code");
                            }

                        }


                        if(status.equalsIgnoreCase("COMPLETED")){
                            commResponseVO.setStatus("success");
                            commResponseVO.setTransactionStatus("success");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRemark("Transaction completed successfully");
                            commResponseVO.setDescription("Transaction completed successfully");

                            commResponseVO.setTransactionId(id);
                            commResponseVO.setBankTransactionDate(created);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setErrorCode(auth_code);

                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());


                        }
                        else if(status.equalsIgnoreCase("CANCELLED")){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionStatus("failed");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRemark("Transaction was cancelled by customer");
                            commResponseVO.setDescription("Transaction was cancelled by customer");
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setBankTransactionDate(created);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                        }
                        else if(status.equalsIgnoreCase("DECLINED")){
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionStatus("failed");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRemark("Transaction was rejected");
                            commResponseVO.setDescription("Transaction was rejected");
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setBankTransactionDate(created);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setMerchantId(terminal_code);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                        }
                        else if(status.equalsIgnoreCase("IN_PROGRESS") || status.equalsIgnoreCase("NEW")){
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);

                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                        }else if((status.equalsIgnoreCase("REFUNDED"))){
                            commResponseVO.setStatus("reversed");
                            commResponseVO.setTransactionStatus("reversed");
                            commResponseVO.setAmount(amount);
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescription(status);
                            commResponseVO.setTransactionId(id);
                            commResponseVO.setBankTransactionDate(created);
                            commResponseVO.setResponseHashInfo(id);
                            commResponseVO.setMerchantId(terminal_code);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setResponseTime(created);
                            commResponseVO.setAuthCode(auth_code);
                            commResponseVO.setErrorCode(auth_code);
                            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setTransactionStatus("pending");
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                            commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                        }


                    }else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    }
                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }

                commResponseVO.setMerchantId(terminal_code);


            }else
            {
                String InquiryResponse = "";


                if (isTest)
                {
                    transactionLogger.error("InquiryRequest------for"  + orderid+ ":::::::" + RB.getString("INQUIRY_TEST_URL")+inquiryRequest);
                    InquiryResponse = CardPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_TEST_URL")+inquiryRequest, "Basic" , encodedCredentials);
                }
                else
                {
                    transactionLogger.error("InquiryRequest------for"  + orderid+ ":::::::" + RB.getString("INQUIRY_LIVE_URL")+inquiryRequest);
                    InquiryResponse = CardPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE_URL") + inquiryRequest, "Basic", encodedCredentials);
                }

                transactionLogger.error("InquiryResponse------"  + orderid+ ":::::::" +  InquiryResponse);


                String State="";
                String paymentid="";
                String Authcode="";
                String amount="";
                String refundamount="";
                String currency ="";
                String createddate="";
                String rrn="";

                JSONObject jsonObject = new JSONObject(InquiryResponse);

                if (jsonObject != null)
                {
                    if (jsonObject.has("data"))
                    {

                        Object o = jsonObject.get("data");
                        if (o instanceof JSONArray)
                        {
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0)
                            {
                                JSONObject json = (JSONObject) data.get(0);
                                if (json.has("state"))
                                {
                                    State=json.getString("state");
                                }

                                if (json.has("id"))
                                {
                                    paymentid=json.getString("id");
                                }

                                if (json.has("authCode"))
                                {
                                    Authcode=json.getString("authCode");
                                }

                                if (json.has("rrn"))
                                {
                                    rrn=json.getString("rrn");
                                }

                                if (json.has("amount"))
                                {
                                    amount=json.getString("amount");
                                }

                                if (json.has("refundedAmount"))
                                {
                                    refundamount=json.getString("refundedAmount");
                                }

                                if (json.has("currency"))
                                {
                                    if(functions.isValueNull(json.getString("currency")))
                                    {
                                        currency = json.getString("currency");
                                    }
                                    else{
                                        currency=commTransactionDetailsVO.getCurrency();
                                    }
                                }
                                if (json.has("date"))
                                {
                                    String date = json.getString("date");
                                    createddate = functions.convertDtstampToDateTime(date);
                                }
                            }

                        }

                    }

                }
                transactionLogger.error("Status------for" + orderid+ "::::::::" + State);

                if(functions.isValueNull(State) && State.equals("COMPLETED")){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRemark("Transaction completed successfully");
                    commResponseVO.setDescription("Transaction completed successfully");
                }
                else if(functions.isValueNull(State) && State.equals("CANCELLED")){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRemark("Transaction was cancelled by customer");
                    commResponseVO.setDescription("Transaction was cancelled by customer");
                }
                else if(functions.isValueNull(State) && State.equals("DECLINED")){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRemark("Transaction was rejected");
                    commResponseVO.setDescription("Transaction was rejected");
                }
                else if(functions.isValueNull(State) && State.equals("IN_PROGRESS")){
                    commResponseVO.setStatus("authstarted");
                    commResponseVO.setTransactionStatus("authstarted");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRemark("Transaction is being processed");
                    commResponseVO.setDescription("Transaction is being processed");
                }
                else if(functions.isValueNull(State) &&(State.equals("AUTHORIZED"))){
                    commResponseVO.setStatus("authstarted");
                    commResponseVO.setTransactionStatus("authstarted");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setRemark("Transaction authorized successfully");
                    commResponseVO.setDescription("Transaction authorized successfully");
                }
                else if(functions.isValueNull(State) &&(State.equals("REFUNDED"))){
                    commResponseVO.setStatus("reversed");
                    commResponseVO.setTransactionStatus("reversed");
                    commResponseVO.setAmount(refundamount);
                    commResponseVO.setRemark("Transaction refunded successfully");
                    commResponseVO.setDescription("Transaction refunded successfully");
                }
                else{
                    commResponseVO.setStatus(State);
                    commResponseVO.setTransactionStatus(State);
                    commResponseVO.setRemark(State);
                    commResponseVO.setDescription(State);
                    commResponseVO.setAmount(amount);

                }
                PZInquiryResponse response =null;
                commResponseVO.setBankTransactionDate(createddate);
                commResponseVO.setTransactionId(paymentid);
                commResponseVO.setMerchantId(merchantid);
                commResponseVO.setAuthCode(Authcode);
                commResponseVO.setRrn(rrn);
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setCurrency(currency);
            }


           }
           catch (PZTechnicalViolationException e)
           {
               transactionLogger.error("Inquiry PZTechnicalViolationException--for--"+orderid+"--",e);
           }
           catch (JSONException e)
           {
               transactionLogger.error("Inquiry JSONException--for--" + orderid + "--",e);
           }



        return commResponseVO;
    }


    public GenericResponseVO process3DSaleConfirmation(String PaRes, String MD,CommRequestVO commRequestVO)
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String saleConfirmationResponse="";

        try{
            String saleConfirmation="PaRes="+URLEncoder.encode(PaRes,"UTF-8")+"&MD="+URLEncoder.encode(MD, "UTF-8")+"";

            transactionLogger.error("saleConfirmation---Request--"+commTransactionDetailsVO.getOrderId()+"--"+saleConfirmation);
            if (isTest)
            {
                saleConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleConfirmation);
            }
            else
            {
                saleConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleConfirmation);
            }

            transactionLogger.error("saleConfirmation----Response----"+commTransactionDetailsVO.getOrderId()+"--------" + saleConfirmationResponse);

            if(functions.isValueNull(saleConfirmationResponse)){
                commResponseVO=CardPayUtils.readXmlResponse(saleConfirmationResponse,gatewayAccount.getDisplayName());
               commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }
            commResponseVO.setTmpl_Amount(commRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(commRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;


    }

    public GenericResponseVO process3DAuthConfirmation(String PaRes, String MD,CommRequestVO commRequestVO)
    {
        transactionLogger.error("-----inside process3DAuthConfirmation-----");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String authConfirmationResponse="";


        try{

            String authConfirmation="PaRes="+URLEncoder.encode(PaRes,"UTF-8")+"&MD="+URLEncoder.encode(MD,"UTF-8")+"";

            transactionLogger.error("authConfirmation----Request--"+commTransactionDetailsVO.getOrderId()+"--"+authConfirmation);
            if (isTest)
            {
                authConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), authConfirmation);
            }
            else
            {
                authConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), authConfirmation);
            }

            transactionLogger.error("authConfirmation----Response---"+commTransactionDetailsVO.getOrderId()+"--" + authConfirmationResponse);

            if(functions.isValueNull(authConfirmationResponse)){
                commResponseVO=CardPayUtils.readXmlResponse(authConfirmationResponse,gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }
            commResponseVO.setTmpl_Amount(commRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(commRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }


        return commResponseVO;
    }


    //Common3dFrontEndServlet
    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DSaleConfirmation-----");
        Comm3DRequestVO comm3DRequestVO=(Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String PaRes=comm3DRequestVO.getPaRes();
        String MD=comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String saleConfirmationResponse="";

        try{
            String saleConfirmation="PaRes="+URLEncoder.encode(PaRes,"UTF-8")+"&MD="+URLEncoder.encode(MD, "UTF-8")+"";

            transactionLogger.error("-----3D Sale Request---"+trackingID+"--"+saleConfirmation);
            if (isTest)
            {
                saleConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), saleConfirmation);
            }
            else
            {
                saleConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), saleConfirmation);
            }

            transactionLogger.error("-----3D Sale Response--"+trackingID+"---" + saleConfirmationResponse);

            if(functions.isValueNull(saleConfirmationResponse)){
                commResponseVO=CardPayUtils.readXmlResponse(saleConfirmationResponse,gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }
            commResponseVO.setTmpl_Amount(comm3DRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(comm3DRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DAuthConfirmation-----");
        Comm3DRequestVO comm3DRequestVO=(Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String PaRes=comm3DRequestVO.getPaRes();
        String MD=comm3DRequestVO.getMd();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String authConfirmationResponse="";


        try{

            String authConfirmation="PaRes="+URLEncoder.encode(PaRes,"UTF-8")+"&MD="+URLEncoder.encode(MD,"UTF-8")+"";

            transactionLogger.error("-----3D Auth Request--"+trackingID+"---"+authConfirmation);
            if (isTest)
            {
                authConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), authConfirmation);
            }
            else
            {
                authConfirmationResponse= CardPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), authConfirmation);
            }

            transactionLogger.error("-----3D Auth Response---"+trackingID+"---" + authConfirmationResponse);

            if(functions.isValueNull(authConfirmationResponse)){
                commResponseVO=CardPayUtils.readXmlResponse(authConfirmationResponse,gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark(" Error");
                commResponseVO.setDescription(" Error");
            }
            commResponseVO.setTmpl_Amount(comm3DRequestVO.getAddressDetailsVO().getTmpl_amount());
            commResponseVO.setTmpl_Currency(comm3DRequestVO.getAddressDetailsVO().getTmpl_currency());
        }
        catch (Exception e){
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        Functions functions= new Functions();
        transactionLogger.debug("Inside CardpayPaymentProcess------");
        int trackingid                                  = pzInquiryRequest.getTrackingId();
        String time                                     = getPreviousTransactionDetails(String.valueOf(trackingid));
        CommTransactionDetailsVO transactionDetailsVO   = requestVO.getTransDetailsVO();
        transactionDetailsVO.setResponsetime(time);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }
    public static String getPreviousTransactionDetails(String trackingid)
    {
        String time = "";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String query = "select dtstamp from transaction_common where trackingid='" + trackingid + "' ";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                time = rs.getString("dtstamp");
            }
            transactionLogger.error("Sql Query-----" + ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----" , e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException-----" , se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return time;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutInquiry() of UBAMCPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO commResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String terminal_code                = gatewayAccount.getMerchantId();
        String terminal_password            = gatewayAccount.getFRAUD_FTP_PATH();

        String INQUIRY_REQUEST_URL  = "";
        String TOKEN_REQUEST_URL    = "";

        String grant_type                   = "password";
        String access_token                 = "";
        String message                      = "";
        StringBuffer tokenRequestParameter  = new StringBuffer();

        boolean isTest = gatewayAccount.isTest();
        String payment_id      =  transactionDetailsVO.getPreviousTransactionId();
        try
        {

            if (isTest)
            {
                TOKEN_REQUEST_URL       = RB.getString("TEST_TOKEN_URL");
                INQUIRY_REQUEST_URL     = RB.getString("TEST_PAYOUT_INQUIRY_URL")+payment_id;
            }
            else
            {
                TOKEN_REQUEST_URL   = RB.getString("LIVE_TOKEN_URL");
                INQUIRY_REQUEST_URL    = RB.getString("LIVE_PAYOUT_INQUIRY_URL")+payment_id;
            }


            tokenRequestParameter.append("grant_type="+grant_type);
            tokenRequestParameter.append("&password="+terminal_password);
            tokenRequestParameter.append("&terminal_code="+terminal_code);

            transactionLogger.error("Token Request Parameter >>> " +trackingId+" " + tokenRequestParameter.toString());

            String tokenResponse = CardPayUtils.doPostFormHTTPSURLConnectionClient(TOKEN_REQUEST_URL, tokenRequestParameter.toString());

            transactionLogger.error("Token Response Parameter >>> " +trackingId+" "+ tokenResponse.toString());
            if(functions.isValueNull(tokenResponse) && functions.isJSONValid(tokenResponse)){
                JSONObject tokenResponsJSON =  new JSONObject(tokenResponse);

                if(tokenResponsJSON != null){
                    if(tokenResponsJSON.has("access_token")){
                        access_token = tokenResponsJSON.getString("access_token");
                    }

                }
            }

            if(functions.isValueNull(access_token))
            {
                transactionLogger.error("processPayoutInquiry() Request ---> " + trackingId + "--->" + INQUIRY_REQUEST_URL);

                String responseString = CardPayUtils.doGetHTTPSURLConnectionClient(INQUIRY_REQUEST_URL, access_token);

                transactionLogger.error("processPayoutInquiry() Response ---> " + trackingId + "--->" + responseString);

                if(functions.isValueNull(responseString) && functions.isJSONValid(responseString))
                {
                    JSONObject responseJOSN     = new JSONObject(responseString);
                    JSONObject payoutDataJOSN   = null;

                    String id               = "";
                    String created          = "";
                    String status           = "";
                    String amountResp       = "";
                    String currencyResp     = "";
                    String name     = "";

                    if(responseJOSN.has("payout_data")){
                        payoutDataJOSN = responseJOSN.getJSONObject("payout_data");
                    }
                    if(responseJOSN.has("name")){
                        name = responseJOSN.getString("name");
                    }
                    if(responseJOSN.has("message")){
                        message = responseJOSN.getString("message");
                    }

                    if(payoutDataJOSN != null){

                        if(payoutDataJOSN.has("id")){
                            id = payoutDataJOSN.getString("id");
                        }
                        if(payoutDataJOSN.has("created")){
                            created = payoutDataJOSN.getString("created");
                        }
                        if(payoutDataJOSN.has("status")){
                            status = payoutDataJOSN.getString("status");
                        }
                        if(payoutDataJOSN.has("amount")){
                            amountResp = payoutDataJOSN.getString("amount");
                            double amt  = Double.parseDouble(amountResp);
                            amountResp      = String.format("%.2f", amt);
                        }
                        if(payoutDataJOSN.has("currency")){
                            currencyResp = payoutDataJOSN.getString("currency");
                        }
                    }

                    if("COMPLETED".equalsIgnoreCase(status)){
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setDescription(status);
                        commResponseVO.setAmount(amountResp);
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setResponseHashInfo(id);
                        commResponseVO.setCurrency(currencyResp);
                        commResponseVO.setResponseTime(created);
                        commResponseVO.setMerchantId(terminal_code);
                    }else if("DECLINED".equalsIgnoreCase(status) ){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setDescription(status);
                        commResponseVO.setAmount(amountResp);
                        commResponseVO.setTransactionId(id);
                        commResponseVO.setResponseHashInfo(id);
                        commResponseVO.setCurrency(currencyResp);
                        commResponseVO.setResponseTime(created);
                        commResponseVO.setMerchantId(terminal_code);
                    }else if("INVALID_API_REQUEST".equalsIgnoreCase(name) ){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(name);
                        commResponseVO.setDescription(message);
                        commResponseVO.setMerchantId(terminal_code);
                    }else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

                commResponseVO.setMerchantId(terminal_code);
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
            }else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }


        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CardPayPaymentGateway.class.getName(), "processPayoutInquiry()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
}

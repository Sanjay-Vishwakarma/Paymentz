package com.payment.Wirecardnew;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by admin on 31-01-2017.
 */
public class WireCardNPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "Wirecard1";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.WireCard");
    private final static String SALEURL = "http://api-test.wirecard.com/engine/rest/payments";
    private final static int TIMEOUT = 30000;
    private static TransactionLogger transactionLogger = new TransactionLogger(WireCardNPaymentGateway.class.getName());
    private static Logger log = new Logger(WireCardNPaymentGateway.class.getName());
    //String webPage = "https://c3-test.wirecard.com/secure/ssl-gateway";
    //String webPage = RB.getString("wirecardurl");
    //String mode = RB.getString("mode");
    //private String mode = "demo";
    private String xmlSchema = "http://www.w3.org/1999/XMLSchema-instance";
    private String xsdValue = "wirecard.xsd";
    public WireCardNPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    private static URI getTestBaseURI(){
        return UriBuilder.fromUri(RB.getString("TEST_URL")).build();
    }

    private static URI getTestBaseInquiryURI(String memberid,String trackingid){
        return UriBuilder.fromUri(RB.getString("TEST_URL")+"/engine/rest/merchants/"+memberid+"/payments/?request_id="+trackingid+"").build();
    }

    private static URI getLiveBaseInquiryURI(String memberid,String trackingid){
        return UriBuilder.fromUri(RB.getString("LIVE_URL")+"/engine/rest/merchants/"+memberid+"/payments/?request_id="+trackingid+"").build();
    }

    private static URI getLiveBaseURI(){
        return UriBuilder.fromUri(RB.getString("LIVE_URL")).build();
    }

    /*public static void main(String[] args)
    {

        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        try{
            String username="101660-IMPERIAL";
            String password="1811ntI-5JYo6J";

            String userPassword=username+":"+password;

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            System.out.println("system property---" + System.getProperty("https.protocols"));
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service =null;
            service=client.resource(getLiveBaseURI());

            String saleRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<payment xmlns=\"http://www.elastic-payments.com/schema/payment\">\n" +
                    "       <merchant-account-id>77024dc9-5e90-4300-a996-6d68e9fbaa77</merchant-account-id>\n" +
                    "       <request-id>1492509_2</request-id>\n" +
                    "       <transaction-type>check-payer-response</transaction-type>\n" +
                    "       <parent-transaction-id>f57fc9b1-97c9-4016-9a17-fb1fa958cc73</parent-transaction-id>\n" +
                    "       <three-d>\n" +
                    "        <pares></pares>\n" +
                    "       </three-d>\n" +
                    "</payment>";


            String inquiryRequest= "merchant_account_id=" + URLEncoder.encode("0c9558df-a2e8-4fe9-ab10-f650deaeeb94", "UTF-8")+

                    "&request_id=" + URLEncoder.encode("53821", "UTF-8") ;

            System.out.println("saleRequest:::::" + saleRequest);

            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            String saleResponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/xml").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, saleRequest);


            //String saleResponse= WirecardUtills.doPostHTTPSURLConnectionClient("https://api-test.wirecard.com/engine/rest/payments", saleRequest, "Basic", encodedCredentials);

            System.out.println("saleResponse:::::"+saleResponse);


            Map readResponse = WirecardUtills.ReadSalesResponse(StringUtils.trim(saleResponse));
            commResponseVO.setPaReq((String) readResponse.get("pareq"));
            commResponseVO.setUrlFor3DRedirect((String) readResponse.get("acs-url"));
            commResponseVO.setTerURL(RB.getString("Term_url"));

            String paReq=commResponseVO.getPaReq();
            String url=commResponseVO.getUrlFor3DRedirect();

            System.out.println("paReq:::::"+paReq);
            System.out.println("url:::::"+url);

            System.out.println("Form submitting::::"+WireCardNPaymentGateway.get3DConfirmationForm(commResponseVO));

            String afterResponse="";
                   String finalResponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, afterResponse);

            System.out.println("finalResponse::::::"+finalResponse);




        }catch (Exception e){
            e.printStackTrace();
        }

    }*/


    public static String get3DConfirmationForm(Comm3DResponseVO response3D)
    {
        //System.out.println("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\"merchant_account_id=0c9558df-a2e8-4fe9-ab10-f650deaeeb94&transaction_type=purchase&nonce3d=3d\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    private static String   getCardType(String  cardType){
        String wireCardType = "";
        Functions functions = new Functions();
        if(functions.isValueNull(cardType))
        {

            if(cardType.equals("VISA"))
            {
                wireCardType="visa" ;
            }
            else if(cardType.equals("MC"))
            {
                wireCardType="mastercard" ;
            }
            else if(cardType.equals("AMEX"))
            {
                wireCardType="amex";
            }
            else if(cardType.equals("DINER"))
            {
                wireCardType="diners";
            }
            else if(cardType.equals("DISC"))
            {
                wireCardType="discover";
            }
            else if(cardType.equals("JCB")){

                wireCardType="jcb";
            }
        }
        return wireCardType;
    }

    /*public static void main(String[] args)
    {
        try
        {
            String _3DsaleRequest =
                    "first_name=" + URLEncoder.encode("test", "UTF-8") +
                            "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType("VISA"), "UTF-8") +
                            "&transaction_type=check-enrollment" +
                            "&expiration_month=" + URLEncoder.encode("12", "UTF-8") +
                            "&account_number=" + URLEncoder.encode("4000000000000002", "UTF-8") +
                            "&expiration_year=" + URLEncoder.encode("2034", "UTF-8") +
                            "&card_security_code=" + URLEncoder.encode("123", "UTF-8") +
                            "&ip_address=" + URLEncoder.encode("192.168.1.5", "UTF-8") +
                            "&last_name=" + URLEncoder.encode("test", "UTF-8") +
                            "&email=" + URLEncoder.encode("test@gamil.com", "UTF-8") +
                            "&phone=" + URLEncoder.encode("9857845612", "UTF-8") +
                            "&address_street1=" + URLEncoder.encode("malad", "UTF-8") +
                            "&address_street2=" + URLEncoder.encode("", "UTF-8") +
                            "&address_city=" + URLEncoder.encode("Mumbai", "UTF-8") +
                            "&address_state=" + URLEncoder.encode("MH", "UTF-8") +
                            "&address_country=" + URLEncoder.encode("IN", "UTF-8") +
                            "&requested_amount_currency=" + URLEncoder.encode("EUR", "UTF-8") +
                            "&request_id=" + URLEncoder.encode("89456", "UTF-8") +
                            "&merchant_account_id=" + URLEncoder.encode("4c4503b8-b120-4008-b833-5e74f2d669cd", "UTF-8") +
                            "&address_postal_code=" + URLEncoder.encode("154870", "UTF-8") +
                            "&requested_amount=" + URLEncoder.encode("1.00", "UTF-8");

            WebResource service = null;
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            service = client.resource(getLiveBaseURI());
            String userPassword = "112189-Blackrock" + ":" + "U8KcBJQEfvZl";
            System.out.println("userPassword--->"+userPassword);
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            System.out.println("encodeBase64--->"+encodedCredentials);
            System.out.println("-----3D enrollment request-----" + _3DsaleRequest);

            String _3Dresponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials)
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DsaleRequest);
            System.out.println("_3Dresponse---->" + _3Dresponse);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

    }*/

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into  processSale:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String attemptThreeD="";
        try
        {
            GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
            GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

            if(functions.isValueNull(commRequestVO.getAttemptThreeD()))
                attemptThreeD=commRequestVO.getAttemptThreeD();

            transactionLogger.error("attemptThreeD-----"+attemptThreeD);

            String termUrl = "";
            transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("Term_url");
                transactionLogger.error("from RB----"+termUrl);
            }

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            String member_id = gatewayAccount.getMerchantId();
            boolean isTest = gatewayAccount.isTest();
            String is3dSupported = gatewayAccount.get_3DSupportAccount();

            transactionLogger.error("");

            WebResource service = null;
            if (isTest)
            {
                transactionLogger.error(":::::inside isTest:::::");
                service = client.resource(getTestBaseURI());
            }
            else
            {
                transactionLogger.error(":::::inside Live:::::");
                service = client.resource(getLiveBaseURI());
            }
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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

            String _3Dresponse = "";
            if ("Y".equals(is3dSupported))
            {
                transactionLogger.debug("Inside is3dSupported-----");

                if(!attemptThreeD.equalsIgnoreCase("Direct"))
                {
                    transactionLogger.debug("attemptThreeD-----"+attemptThreeD);

                    String _3DsaleRequest =
                            "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=check-enrollment" +
                                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");

                    String _3DsaleRequestLog =
                            "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=check-enrollment" +
                                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");
                    transactionLogger.error("-----3D enrollment request---"+trackingID+"--" + _3DsaleRequestLog);

                    _3Dresponse = service.path("engine").path("rest").path("payments")
                            .header("Content-Type", "application/x-www-form-urlencoded;").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials)
                            .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DsaleRequest);

                    //_3Dresponse= WirecardUtills.doPostHTTPSURLConnectionClient("https://api-test.wirecard.com/engine/rest/payments", _3DsaleRequest, "Basic", encodedCredentials);

                    transactionLogger.error("-----3D enrollment response--"+trackingID+"---" + _3Dresponse);
                    if (functions.isValueNull(_3Dresponse))
                    {
                        Map readResponse = WirecardUtills.ReadSalesResponse(_3Dresponse);
                        if (readResponse != null && "success".equals(readResponse.get("transaction-state")))
                        {
                            commResponseVO.setPaReq((String) readResponse.get("pareq"));
                            commResponseVO.setUrlFor3DRedirect((String) readResponse.get("acs-url"));
                            commResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                            commResponseVO.setTerURL(termUrl + trackingID);
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setRemark("");
                            commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                            commResponseVO.setTransactionType("sale");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                        else
                        {
                            if ("Only3D".equalsIgnoreCase(attemptThreeD))
                            {
                                transactionLogger.error("Only 3D Card Required");
                                commResponseVO.setStatus("failed");
                                commResponseVO.setDescription("Only 3D Card Required");
                                commResponseVO.setRemark("Only 3D Card Required");
                                commResponseVO.setCurrency(currency);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                return commResponseVO;
                            }
                            else
                            {
                                String saleRequest =
                                        "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                                "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                                "&transaction_type=purchase" +
                                                "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                                                "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                                                "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                                                "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                                                "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                                "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                                "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                                "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                                "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                                "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                                "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                                "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                                "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                                "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                                "&request_id=" + URLEncoder.encode(trackingID + "_1", "UTF-8") +
                                                "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                                "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                                "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") +
                                                "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");
                                String saleRequestLog =
                                        "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                                "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                                "&transaction_type=purchase" +
                                                "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                                                "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                                                "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                                                "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                                                "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                                "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                                "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                                "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                                "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                                "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                                "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                                "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                                "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                                "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                                "&request_id=" + URLEncoder.encode(trackingID + "_1", "UTF-8") +
                                                "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                                "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                                "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") +
                                                "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");

                                transactionLogger.error("-----sale request---"+trackingID+"--" + saleRequestLog);
                                transactionLogger.error("Transaction_id::::::::::" + (String) readResponse.get("transaction-id"));

                                String response = service.path("engine").path("rest").path("payments")
                                        .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                                        .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, saleRequest);
                                transactionLogger.error("-----sale response---"+trackingID+"--" + response);

                                readResponse = WirecardUtills.ReadSalesResponse(response);
                                Map readStatus = WirecardUtills.ReadStatusResponse(response);

                                String status = "fail";
                                if (!readStatus.equals("") && readStatus != null)
                                {
                                    if (readStatus.get("code").equals("201.0000"))
                                    {
                                        status = "success";
                                        commResponseVO.setDescription((String) readStatus.get("description"));
                                    }
                                    else
                                    {
                                        commResponseVO.setDescription((String) readStatus.get("description"));
                                    }
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                                    commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                                    commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                                    commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                                    commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                                    commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                                    commResponseVO.setErrorCode((String) readResponse.get("status code"));
                                    commResponseVO.setRemark((String) readResponse.get("description"));
                                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                    commResponseVO.setTransactionType("purchase");
                                }
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("3D Transaction failed");
                        commResponseVO.setTransactionType("sale");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {
                        transactionLogger.error("Non 3D Transaction Not Allowed");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Non 3D Transaction Not Allowed");
                        commResponseVO.setRemark("Non 3D Transaction Not Allowed");
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        return commResponseVO;
                }
            }
            else
            {
                String saleRequest =
                        "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                "&transaction_type=purchase" +
                                "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                                "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                                "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                                "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                                "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") ;
                //   "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");
                String saleRequestLog =
                        "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                "&transaction_type=purchase" +
                                "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                                "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                                "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                                "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                                "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") ;

                transactionLogger.error("-----sale request---"+trackingID+"--" + saleRequestLog);

                String response = service.path("engine").path("rest").path("payments")
                        .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                        .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, saleRequest);
                transactionLogger.error("-----sale response---"+trackingID+"--" + response);

                Map   readResponse = WirecardUtills.ReadSalesResponse(response);
                Map readStatus = WirecardUtills.ReadStatusResponse(response);

                String status = "fail";
                if (!readStatus.equals("") && readStatus != null)
                {
                    if (readStatus.get("code").equals("201.0000"))
                    {
                        status = "success";
                        commResponseVO.setDescription((String) readStatus.get("description"));
                    }
                    else
                    {
                        commResponseVO.setDescription((String) readStatus.get("description"));
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                    commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                    commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                    commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                    commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                    commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                    commResponseVO.setErrorCode((String) readResponse.get("status code"));
                    commResponseVO.setRemark((String) readResponse.get("description"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType("purchase");
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);

        }
        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        transactionLogger.error(":::::Entering into processAuthentication::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String attemptThreeD="";
        try {
            GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
            GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

            if(functions.isValueNull(commRequestVO.getAttemptThreeD()))
                attemptThreeD=commRequestVO.getAttemptThreeD();

            String termUrl = "";
            transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("Term_url");
                transactionLogger.error("from RB----"+termUrl);
            }

            boolean isTest=gatewayAccount.isTest();
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String is3dSupported=gatewayAccount.get_3DSupportAccount();
            WebResource service=null;
            String _3Dresponse = "";

            if(isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                service = client.resource(getTestBaseURI());
            }else
            {
                transactionLogger.debug(":::::inside Live:::::");
                service = client.resource(getLiveBaseURI());
            }
            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//
            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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
            if("Y".equals(is3dSupported))
            {
                transactionLogger.debug("Inside is3dSupported-----");

                if (!attemptThreeD.equalsIgnoreCase("Direct"))
                {
                    transactionLogger.debug("attemptThreeD-----" + attemptThreeD);


                    String _3DauthRequest =
                            "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=check-enrollment" +
                                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");
                    String _3DauthRequestLog =
                            "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=check-enrollment" +
                                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");

                    transactionLogger.error("-----3D enrollment request---"+trackingID+"--" + _3DauthRequestLog);

                    String _3Dauthresponse = service.path("engine").path("rest").path("payments")
                            .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                            .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DauthRequest);

                    transactionLogger.error("-----3D enrollment response---"+trackingID+"--" + _3Dauthresponse);
                    if (functions.isValueNull(_3Dauthresponse))
                    {
                        Map readResponse = WirecardUtills.ReadSalesResponse(_3Dauthresponse);
                        if (readResponse != null && "success".equals(readResponse.get("transaction-state")))
                        {
                            commResponseVO.setPaReq((String) readResponse.get("pareq"));
                            commResponseVO.setUrlFor3DRedirect((String) readResponse.get("acs-url"));
                            commResponseVO.setMd(PzEncryptor.encryptCVV(cardDetailsVO.getcVV()));
                            commResponseVO.setTerURL(termUrl + trackingID);
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setRemark("");
                            commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                            commResponseVO.setTransactionType("auth");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                        else if ("Only3D".equalsIgnoreCase(attemptThreeD))
                        {
                            transactionLogger.error("Only 3D Card Required");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("Only 3D Card Required");
                            commResponseVO.setRemark("Only 3D Card Required");
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            return commResponseVO;
                        }
                        else
                        {

                            String authRequest = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=authorization" +
                                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID + "_1", "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") +
                                    "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");

                            String authRequestLog = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                                    "&transaction_type=authorization" +
                                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                                    "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                                    "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                                    "&request_id=" + URLEncoder.encode(trackingID + "_1", "UTF-8") +
                                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") +
                                    "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");

                            transactionLogger.error("-----auth request---" + trackingID + "--" + authRequestLog);

                            //Base64encoded
                            String response = service.path("engine").path("rest").path("payments")
                                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, authRequest);

                            transactionLogger.error("-----auth response---"+trackingID+"--" + response);

                            readResponse = WirecardUtills.ReadSalesResponse(response);
                            Map readStatus = WirecardUtills.ReadStatusResponse(response);
                            String status = "fail";
                            if (!readStatus.equals("") && readStatus != null)
                            {
                                if (readStatus.get("code").equals("201.0000"))
                                {
                                    status = "success";
                                    commResponseVO.setDescription((String) readStatus.get("description"));
                                }
                                else
                                {
                                    commResponseVO.setDescription((String) readStatus.get("description"));
                                }
                                commResponseVO.setStatus(status);
                                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                                commResponseVO.setRemark((String) readStatus.get("description"));
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                commResponseVO.setTransactionType("authorization");
                            }
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("3D Transaction failed");
                        commResponseVO.setTransactionType("sale");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {
                    transactionLogger.error("Non 3D Transaction Not Allowed");
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescription("Non 3D Transaction Not Allowed");
                    commResponseVO.setRemark("Non 3D Transaction Not Allowed");
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    return commResponseVO;
                }
            }
            else
            {
                String authRequest = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                        "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                        "&transaction_type=authorization" +
                        "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                        "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                        "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                        "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                        "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                        "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                        "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                        "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                        "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                        "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                        "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                        "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                        "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                        "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                        "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                        "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                        "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                        "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");

                String authRequestLog = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                        "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                        "&transaction_type=authorization" +
                        "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                        "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                        "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                        "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                        "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                        "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                        "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                        "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                        "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                        "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                        "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                        "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                        "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                        "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                        "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                        "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                        "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                        "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");
                // "&parent_transaction_id=" + URLEncoder.encode((String) readResponse.get("transaction-id"), "UTF-8");

                transactionLogger.error("-----auth request---" + trackingID + "--" + authRequestLog);

                //Base64encoded
                String response = service.path("engine").path("rest").path("payments")
                        .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                        .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, authRequest);

                transactionLogger.error("-----auth response---"+trackingID+"--" + response);

                Map readResponse = WirecardUtills.ReadSalesResponse(response);
                Map readStatus = WirecardUtills.ReadStatusResponse(response);
                String status = "fail";
                if (!readStatus.equals("") && readStatus != null)
                {
                    if (readStatus.get("code").equals("201.0000"))
                    {
                        status = "success";
                        commResponseVO.setDescription((String) readStatus.get("description"));
                    }
                    else
                    {
                        commResponseVO.setDescription((String) readStatus.get("description"));
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                    commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                    commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                    commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                    commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                    commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                    commResponseVO.setErrorCode((String) readResponse.get("status code"));
                    commResponseVO.setRemark((String) readStatus.get("description"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType("authorization");
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into processCapture:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        try {
            GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            WebResource service=null;
            if(isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                service = client.resource(getTestBaseURI());
            }else
            {
                transactionLogger.debug(":::::inside Live:::::");
                service = client.resource(getLiveBaseURI());
            }
            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

            String captureRequest = "merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID + "_capture", "UTF-8") +
                    "&transaction_type=capture-authorization" +
                    "&parent_transaction_id=" + URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8");

            transactionLogger.error("-----capture request-----" + captureRequest);

            //Base64encoded
            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, captureRequest);

            transactionLogger.error("-----capture response-----" + response);

            Map readResponse = WirecardUtills.ReadSalesResponse(response);
            Map readStatus = WirecardUtills.ReadStatusResponse(response);

            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{

                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setDescription((String) readResponse.get("description"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("capture-authorization");
            }
        }

        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    /*public static void main(String[] args)
    {
        WireCardNPaymentGateway s = new WireCardNPaymentGateway("2681");
        CommRequestVO commRequestVO = new CommRequestVO();


        //   CommTransactionDetailsVO genericTransactionDetailsVO =  new CommTransactionDetailsVO();

        //   genericTransactionDetailsVO.setResponsetime("2017-01-28 14:12:52");

        //   commRequestVO.setTransDetailsVO(genericTransactionDetailsVO);
        try
        {
            String refund = "merchant_account_id=0c9558df-a2e8-4fe9-ab10-f6\n" +
                    "50deaeeb94&request_id=55327_Refund&transaction_type=refund-purchase&parent_transaction_id=e208a7e9-1318-469b-a2cd-ae1490751edb&payment_ip_address=54.229.9.44";

            String username="70000-APILUHN-CARD";
            String password="8mhwavKVb91T";
            String userPassword = username + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            //System.out.println("system property---" + System.getProperty("https.protocols"));
            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            WebResource service =null;
            service=client.resource(getTestBaseURI());
            String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, refund);

            //System.out.println(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/


   /* public static void main(String[] args)
    {
        WebResource service=null;

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.setConnectTimeout(180000);
        client.setReadTimeout(180000);

        transactionLogger.debug(":::::inside isTest:::::");
            service = client.resource(getTestBaseURI());
            transactionLogger.debug("service-----"+service);
            transactionLogger.debug("service in string Format-----"+service.toString());

        String req="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<payment xmlns=\"http://www.elastic-payments.com/schema/payment\">\n" +
                "    <merchant-account-id>0c9558df-a2e8-4fe9-ab10-f650deaeeb94</merchant-account-id>\n" +
                "    <request-id>83710_Refund6</request-id>\n" +
                "    <transaction-type>refund-purchase</transaction-type>\n" +
                "    <requested-amount currency=\"USD\">2.00</requested-amount>\n" +
                "    <parent-transaction-id>2f757b58-3340-4877-9c6f-bdb6653460a3</parent-transaction-id>\n" +
                "    <ip-address>127.0.0.1</ip-address>\n" +
                "</payment>";

        System.out.println("req------"+req);

        String userPassword ="70000-APILUHN-CARD:8mhwavKVb91T";
        transactionLogger.debug("userpassword-----"+userPassword);
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        System.out.println(("encodedCredentials-----"+encodedCredentials));
            *//*String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, refundRequest);
*//*
        String response = service.path("engine").path("rest").path("payments")
                .header("Content-Type", "application/xml;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                .type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(String.class, req);

        System.out.println("-----refund response-----" + response);
    }*/

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Enter into processRefund:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        try {
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);
            client.setConnectTimeout(180000);
            client.setReadTimeout(180000);

            WebResource service=null;
            if(isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                service = client.resource(getTestBaseURI());
            }else
            {
                transactionLogger.debug(":::::inside Live:::::");
                service = client.resource(getLiveBaseURI());
            }

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

            String currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }

            String rCount = "";
            if (functions.isValueNull(commRequestVO.getCount()))
                rCount = commRequestVO.getCount();



            /*String refundRequest = "merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID + "_Refund"+rCount, "UTF-8") +
                    "&transaction_type=refund-purchase" +
                    "&requested-amount=" +URLEncoder.encode(commTransactionDetailsVO.getAmount(), "UTF-8") +
                    "&parent_transaction_id=" + URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&payment_ip_address=" + URLEncoder.encode(ipAddress, "UTF-8");*/

            String refundRequest="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<payment xmlns=\"http://www.elastic-payments.com/schema/payment\">\n" +
                    "    <merchant-account-id>"+URLEncoder.encode(member_id, "UTF-8")+"</merchant-account-id>\n" +
                    "    <request-id>"+URLEncoder.encode(trackingID + "_Refund"+rCount, "UTF-8")+"</request-id>\n" +
                    "    <transaction-type>refund-purchase</transaction-type>\n" +
                    "    <requested-amount currency=\""+commTransactionDetailsVO.getCurrency()+"\">"+URLEncoder.encode(commTransactionDetailsVO.getAmount(), "UTF-8")+"</requested-amount>\n" +
                    "    <parent-transaction-id>"+URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8")+"</parent-transaction-id>\n" +
                    "    <ip-address>"+URLEncoder.encode(ipAddress, "UTF-8")+"</ip-address>\n" +
                    "</payment>";

            transactionLogger.error("-----refund request-----" + refundRequest);

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            /*String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, refundRequest);
*/
            String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/xml;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(String.class, refundRequest);

            transactionLogger.error("-----refund response-----" + response);

            Map readResponse = WirecardUtills.ReadSalesResponse(response);
            Map readStatus = WirecardUtills.ReadStatusResponse(response);
            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000"))
                {
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                    commResponseVO.setTransactionType("refund-purchase");
                }
                else if(readStatus.get("code").equals("400.1023"))
                { // second call if refund purchase is fail

                    String refundRequest2 = "merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                            "&request_id=" + URLEncoder.encode(trackingID + "_Capture_Refund", "UTF-8") +
                            "&transaction_type=refund-capture" +
                            "&parent_transaction_id=" + URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8") +
                            "&payment_ip_address=" + URLEncoder.encode(ipAddress, "UTF-8");

                    transactionLogger.error("-----refund_capture request-----" + refundRequest2);

                    userPassword = userName + ":" + password;
                    encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
                    String response2 = service.path("engine").path("rest").path("payments")
                            .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                            .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, refundRequest2);

                    transactionLogger.error("-----refund_capture response-----" + response2);

                    readResponse = WirecardUtills.ReadSalesResponse(response2);
                    readStatus = WirecardUtills.ReadStatusResponse(response2);
                    status = "fail";
                    if (!readStatus.equals("") && readStatus != null)
                    {
                        if (readStatus.get("code").equals("201.0000"))
                        {
                            status = "success";
                            commResponseVO.setDescription((String) readStatus.get("description"));
                            commResponseVO.setTransactionType("refund-capture");
                        }
                        else
                        {
                            commResponseVO.setDescription((String) readStatus.get("description"));
                        }
                    }
                }
                else{
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setDescription((String) readResponse.get("description"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setCurrency(currency);
            }
        }

        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error(":::::Entering into processVoid:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        try {
            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            WebResource service=null;
            if(isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                service = client.resource(getTestBaseURI());
            }else
            {
                transactionLogger.debug(":::::inside Live:::::");
                service = client.resource(getLiveBaseURI());
            }
            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

            String refundRequest = "merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID + "cancel", "UTF-8") +
                    "&transaction_type=void-authorization" +
                    "&parent_transaction_id=" + URLEncoder.encode(commTransactionDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8");

            transactionLogger.error("-----void request-----" + refundRequest);
            //Base64encoded
            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, refundRequest);
            transactionLogger.error("-----void response------" + response);

            Map readResponse = WirecardUtills.ReadSalesResponse(response);
            Map readStatus = WirecardUtills.ReadStatusResponse(response);

            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("void-authorization");
            }
        }

        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processQuery:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            WebResource service = null;
            if (isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                //System.out.println(":::::inside isTest:::::");
                service = client.resource(getTestBaseInquiryURI(member_id,trackingID+"_1"));
                //System.out.println(":::::service:::::"+service);
            }
            else
            {
                transactionLogger.debug(":::::inside Live:::::");
                //System.out.println(":::::inside Live:::::");
                service = client.resource(getLiveBaseInquiryURI(member_id,trackingID+"_1"));
            }

            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            String response = service.header("Authorization", "Basic " + encodedCredentials).accept(MediaType.APPLICATION_XML).get(String.class);

            transactionLogger.error("-----Query response-----" + response);
            //System.out.println("-----Query response-----"+response);
            Map readResponse = WirecardUtills.ReadSalesResponse(response);
            Map readStatus = WirecardUtills.ReadStatusResponse(response);

            String status = "";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000") || readStatus.get("code").equals("200.1083")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    status = "fail";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setBankTransactionDate((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setAuthCode((String) readResponse.get("authorization-code"));
                commResponseVO.setAmount((String) readResponse.get("requested-amount"));
                commResponseVO.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch(Exception e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processPayout:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        try {
            CommTransactionDetailsVO  transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();
            WebResource service =null;
            if(isTest){
                transactionLogger.debug(":::::inside isTest:::::");
                service=client.resource(getTestBaseURI());
            }
            else{
                transactionLogger.debug(":::::inside Live:::::");
                service=client.resource(getLiveBaseURI());
            }

            //Auth header
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();//
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();//

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

            String payoutRequest = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=original-credit" +
                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                    "&account_number="+ URLEncoder.encode(cardDetailsVO.getCardNum(),"UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");

            String payoutRequestLog = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=original-credit" +
                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                    "&account_number="+ URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()),"UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID, "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8");

            transactionLogger.error("-----payout request---"+trackingID+"--" + payoutRequestLog);

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
            String response = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, payoutRequest);

            transactionLogger.error("-----payout response---"+trackingID+"--" + response);

            Map readResponse = WirecardUtills.ReadSalesResponse(response);
            Map readStatus = WirecardUtills.ReadStatusResponse(response);

            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("payout");
            }
        }

        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error(":::::Entering into  process3DSaleConfirmation:::::");
        Functions functions= new Functions();
        WireCardRequestVO commRequestVO = (WireCardRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        try {
            CommTransactionDetailsVO  transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            WebResource service =null;
            if(isTest){
                transactionLogger.error(":::::inside isTest:::::");
                service=client.resource(getTestBaseURI());
            }
            else{
                transactionLogger.error(":::::inside Live:::::");
                service=client.resource(getLiveBaseURI());
            }
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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


            String _3DsaleRequest =
                    "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                            "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                            "&transaction_type=purchase" +
                            "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                            "&account_number="+ URLEncoder.encode(cardDetailsVO.getCardNum(),"UTF-8") +
                            "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                            "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                            "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                            "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                            "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                            "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                            "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                            "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                            "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                            "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                            "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                            "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                            "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                            "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                            "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                            "&pares=" + URLEncoder.encode(commRequestVO.getPARes(), "UTF-8");

                        String _3DsaleRequestLog =
                                "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                            "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                            "&transaction_type=purchase" +
                            "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                            "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                            "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                            "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                            "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                            "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                            "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                            "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                            "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                            "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                            "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                            "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                            "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                            "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                            "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                            "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                            "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                            "&pares=" + URLEncoder.encode(commRequestVO.getPARes(), "UTF-8");

            transactionLogger.error("-----3D sale confirmation request---"+trackingID+"--" + _3DsaleRequestLog);

            String _3DSaleresponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DsaleRequest);
            transactionLogger.error("-----3D sale confirmation response---"+trackingID+"--" + _3DSaleresponse);

            Map readResponse = WirecardUtills.ReadSalesResponse(_3DSaleresponse);
            Map readStatus = WirecardUtills.ReadStatusResponse(_3DSaleresponse);

            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000")|| readStatus.get("code").equals("200.1083")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("purchase");
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);

        }
        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO process3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error(":::::Entering into  process3DAuthConfirmation:::::");
        Functions functions= new Functions();
        WireCardRequestVO commRequestVO = (WireCardRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        try {
            CommTransactionDetailsVO  transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            WebResource service =null;
            if(isTest){
                transactionLogger.error(":::::inside isTest:::::");
                service=client.resource(getTestBaseURI());
            }
            else{
                transactionLogger.error(":::::inside Live:::::");
                service=client.resource(getLiveBaseURI());
            }
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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

            String _3DauthRequest = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=authorization" +
                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                    "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                    "&card_security_code=" + URLEncoder.encode(cardDetailsVO.getcVV(), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                    "&pares=" + URLEncoder.encode(commRequestVO.getPARes(), "UTF-8");

            String _3DauthRequestLog = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=authorization" +
                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                    "&account_number=" + URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()), "UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                    "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getcVV()), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                    "&pares=" + URLEncoder.encode(commRequestVO.getPARes(), "UTF-8");

            transactionLogger.error("-----3D auth confirmation request---"+trackingID+"--" + _3DauthRequestLog);

            //Base64encoded
            String _3DAuthresponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DauthRequest);

            transactionLogger.error("-----3D auth confirmation response---"+trackingID+"--" + _3DAuthresponse);

            Map readResponse = WirecardUtills.ReadSalesResponse(_3DAuthresponse);
            Map readStatus = WirecardUtills.ReadStatusResponse(_3DAuthresponse);
            String status = "fail";
            if (!readStatus.equals("") && readStatus != null)
            {
                if (readStatus.get("code").equals("201.0000")|| readStatus.get("code").equals("200.1083"))
                {
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else
                {
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("authorization");
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error(":::::Entering into  process3DSaleConfirmation:::::");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        try {
            CommTransactionDetailsVO  transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            String cvv="";
            if(functions.isValueNull(commRequestVO.getMd())){
                cvv= PzEncryptor.decryptCVV(commRequestVO.getMd());
            }
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            WebResource service =null;
            if(isTest){
                transactionLogger.error(":::::inside isTest:::::");
                service=client.resource(getTestBaseURI());
            }
            else{
                transactionLogger.error(":::::inside Live:::::");
                service=client.resource(getLiveBaseURI());
            }
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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


            String _3DsaleRequest =
                    "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                            "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                            "&transaction_type=purchase" +
                            "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                            "&account_number="+ URLEncoder.encode(cardDetailsVO.getCardNum(),"UTF-8") +
                            "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                            "&card_security_code=" + URLEncoder.encode(cvv, "UTF-8") +
                            "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                            "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                            "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                            "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                            "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                            "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                            "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                            "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                            "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                            "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                            "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                            "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                            "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                            "&pares=" + URLEncoder.encode(commRequestVO.getPaRes(), "UTF-8");

            String _3DsaleRequestLog =
                    "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                            "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                            "&transaction_type=purchase" +
                            "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                            "&account_number="+ URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()),"UTF-8") +
                            "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                            "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cvv), "UTF-8") +
                            "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                            "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                            "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                            "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                            "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                            "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                            "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                            "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                            "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                            "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                            "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                            "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                            "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                            "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                            "&pares=" + URLEncoder.encode(commRequestVO.getPaRes(), "UTF-8");

            transactionLogger.error("-----3D sale confirmation request---"+trackingID+"--" + _3DsaleRequestLog);

            String _3DSaleresponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DsaleRequest);
            transactionLogger.error("-----3D sale confirmation response---"+trackingID+"--" + _3DSaleresponse);

            Map readResponse = WirecardUtills.ReadSalesResponse(_3DSaleresponse);
            Map readStatus = WirecardUtills.ReadStatusResponse(_3DSaleresponse);

            String status = "fail";
            if (!readStatus.equals("") && readStatus != null){
                if (readStatus.get("code").equals("201.0000")|| readStatus.get("code").equals("200.1083")){
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("purchase");
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);

        }
        catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error(":::::Entering into  process3DAuthConfirmation:::::");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        String cvv="";
        if(functions.isValueNull(commRequestVO.getMd())){
            cvv= PzEncryptor.decryptCVV(commRequestVO.getMd());
        }
        try {
            CommTransactionDetailsVO  transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");

            ClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config);

            String member_id = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest=gatewayAccount.isTest();

            WebResource service =null;
            if(isTest){
                transactionLogger.error(":::::inside isTest:::::");
                service=client.resource(getTestBaseURI());
            }
            else{
                transactionLogger.error(":::::inside Live:::::");
                service=client.resource(getLiveBaseURI());
            }
            String userName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
            String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

            String userPassword = userName + ":" + password;
            String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

            String ipAddress="";

            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                ipAddress=addressDetailsVO.getCardHolderIpAddress();
            }else {
                ipAddress=addressDetailsVO.getIp();
            }

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

            String _3DauthRequest = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=authorization" +
                    "&expiration_month=" + URLEncoder.encode(cardDetailsVO.getExpMonth(), "UTF-8") +
                    "&account_number=" + URLEncoder.encode(cardDetailsVO.getCardNum(), "UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(cardDetailsVO.getExpYear(), "UTF-8") +
                    "&card_security_code=" + URLEncoder.encode(cvv, "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                    "&pares=" + URLEncoder.encode(commRequestVO.getPaRes(), "UTF-8");

            String _3DauthRequestLog = "first_name=" + URLEncoder.encode(addressDetailsVO.getFirstname(), "UTF-8") +
                    "&card_type=" + URLEncoder.encode(WireCardNPaymentGateway.getCardType(cardDetailsVO.getCardType()), "UTF-8") +
                    "&transaction_type=authorization" +
                    "&expiration_month=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpMonth()), "UTF-8") +
                    "&account_number="+ URLEncoder.encode(functions.maskingPan(cardDetailsVO.getCardNum()),"UTF-8") +
                    "&expiration_year=" + URLEncoder.encode(functions.maskingNumber(cardDetailsVO.getExpYear()), "UTF-8") +
                    "&card_security_code=" + URLEncoder.encode(functions.maskingNumber(cvv), "UTF-8") +
                    "&ip_address=" + URLEncoder.encode(ipAddress, "UTF-8") +
                    "&last_name=" + URLEncoder.encode(addressDetailsVO.getLastname(), "UTF-8") +
                    "&email=" + URLEncoder.encode(addressDetailsVO.getEmail(), "UTF-8") +
                    "&phone=" + URLEncoder.encode(addressDetailsVO.getPhone(), "UTF-8") +
                    "&address_street1=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_street2=" + URLEncoder.encode(addressDetailsVO.getStreet(), "UTF-8") +
                    "&address_city=" + URLEncoder.encode(addressDetailsVO.getCity(), "UTF-8") +
                    "&address_state=" + URLEncoder.encode(addressDetailsVO.getState(), "UTF-8") +
                    "&address_country=" + URLEncoder.encode(addressDetailsVO.getCountry(), "UTF-8") +
                    "&requested_amount_currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") +
                    "&parent_transaction_id=" + URLEncoder.encode(transDetailsVO.getPreviousTransactionId(), "UTF-8") +
                    "&request_id=" + URLEncoder.encode(trackingID+"_1", "UTF-8") +
                    "&merchant_account_id=" + URLEncoder.encode(member_id, "UTF-8") +
                    "&address_postal_code=" + URLEncoder.encode(addressDetailsVO.getZipCode(), "UTF-8") +
                    "&requested_amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8")+
                    "&pares=" + URLEncoder.encode(commRequestVO.getPaRes(), "UTF-8");

            transactionLogger.error("-----3D auth confirmation request--"+trackingID+"---" + _3DauthRequestLog);

            //Base64encoded
            String _3DAuthresponse = service.path("engine").path("rest").path("payments")
                    .header("Content-Type", "application/x-www-form-urlencoded;").header("Content-Length", "554").header("Connection", "Keep-Alive").header("Authorization", "Basic " + encodedCredentials).header("User-Agent", "Apache-HttpClient/4.3.6 (java 1.5)")
                    .type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, _3DauthRequest);

            transactionLogger.error("-----3D auth confirmation response--"+trackingID+"---" + _3DAuthresponse);

            Map readResponse = WirecardUtills.ReadSalesResponse(_3DAuthresponse);
            Map readStatus = WirecardUtills.ReadStatusResponse(_3DAuthresponse);
            String status = "fail";
            if (!readStatus.equals("") && readStatus != null)
            {
                if (readStatus.get("code").equals("201.0000")|| readStatus.get("code").equals("200.1083"))
                {
                    status = "success";
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else
                {
                    commResponseVO.setDescription((String) readStatus.get("description"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId((String) readResponse.get("merchant-account-id"));
                commResponseVO.setMerchantOrderId((String) readResponse.get("request-id"));
                commResponseVO.setTransactionId((String) readResponse.get("transaction-id"));
                commResponseVO.setTransactionType((String) readResponse.get("transaction-type"));
                commResponseVO.setTransactionStatus((String) readResponse.get("transaction-state"));
                commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setErrorCode((String) readResponse.get("status code"));
                commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("authorization");
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }catch(UnsupportedEncodingException e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public String getMaxWaitDays()
    {
        return "5";
    }
}


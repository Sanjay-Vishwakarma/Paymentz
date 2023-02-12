package com.payment.ecomprocessing;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Balaji Sawant on 26-Sep-19.
 */
public class ECPPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "ecp";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ecp");
    private static TransactionLogger transactionLogger = new TransactionLogger(ECPPaymentGateway.class.getName());
    private static Logger log = new Logger(ECPPaymentGateway.class.getName());

    public ECPPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale ---");

        Functions functions = new Functions();
        ECPUtils ecpUtils=new ECPUtils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        String card_number=cardDetailsVO.getCardNum();
        String cvv=cardDetailsVO.getcVV();
        String expiration_month=cardDetailsVO.getExpMonth();
        String expiration_year=cardDetailsVO.getExpYear();

        String firstName="";
        String lastName="";
        if (functions.isValueNull(addressDetailsVO.getFirstname()))
            firstName=addressDetailsVO.getFirstname();
        if (functions.isValueNull(addressDetailsVO.getLastname()))
            lastName=addressDetailsVO.getLastname();

        String cardHolderName="";
        cardHolderName=firstName+" "+lastName;
        String customer_email=addressDetailsVO.getEmail();
        String remote_ip="";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())) {
            remote_ip=addressDetailsVO.getCardHolderIpAddress();
        }
        else {
            remote_ip=addressDetailsVO.getIp();
        }
        String address1=addressDetailsVO.getStreet();
        String address2=addressDetailsVO.getStreet();
        String city=addressDetailsVO.getCity();
        String zip_code=addressDetailsVO.getZipCode();
        String state=addressDetailsVO.getState();
        String country=addressDetailsVO.getCountry();

        // gatewayAccount
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName.trim() + ":" + password.trim();
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        String member_id = gatewayAccount.getMerchantId();
        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        transactionLogger.debug("is3dSupported --------"+is3dSupported);
        String transaction_type="";

        // commMerchantVO
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }

        String notificationUrlNew=notificationUrl+trackingID;
        String return_success_url=termUrl+trackingID+"&status=success";
        String return_failure_url=termUrl+trackingID+"&status=fail";

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String amount=transDetailsVO.getAmount();
        transactionLogger.error("amount ---"+amount);

        if(currency.equals("JPY") ){
            amount=ecpUtils.getJPYSupportedAmount(amount);
        }
        else if(currency.equals("KWD")){
            amount=ecpUtils.getKWDSupportedAmount(amount);
        }else{
            amount=ecpUtils.getAmount(amount);
        }
        transactionLogger.error("amount after formatting  ---"+amount);

        String token=ecpUtils.getToken(currency,is3dSupported,isTest,accountId);
        transactionLogger.error("token ---"+token);
        String testUrl=RB.getString("TEST_URL")+token;
        transactionLogger.debug("testUrl ---"+testUrl);
        String liveUrl=RB.getString("LIVE_URL")+token;
        transactionLogger.debug("liveUrl ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String  unique_id = "";
        String responseCode = "";
        String technical_message = "";
        String message = "";
        String acs_url = "";
        String authorization_code = "";
        String transactionid_fromResponse = "";
        String mode = "";
        String timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sent_to_acquirer = "";
        String descriptor_fromResponse = "";
        String moto = "";

        try
        {
            if ("Y".equalsIgnoreCase(is3dSupported) || "O".equalsIgnoreCase(is3dSupported))
            {

                if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                transactionLogger.debug("Inside is3dSupported ---");
                transaction_type="sale3d";

                StringBuffer sale3DRequest=new StringBuffer();
                       sale3DRequest.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>" +
                                    "<transaction_type>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type)) + "</transaction_type>" +
                                    "<transaction_id>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID)) + "</transaction_id>" +
                                    "<notification_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(notificationUrlNew)) + "</notification_url>" +
                                    "<return_success_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_success_url)) + "</return_success_url>" +
                                    "<return_failure_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_failure_url)) + "</return_failure_url>" +
                                    "<amount>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount)) + "</amount>" +
                                    "<currency>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency)) + "</currency>" +
                                    "<card_holder>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName)) + "</card_holder>" +
                                    "<card_number>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(card_number)) + "</card_number>" +
                                    "<cvv>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cvv)) + "</cvv>" +
                                    "<expiration_month>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_month)) + "</expiration_month>" +
                                    "<expiration_year>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_year)) + "</expiration_year>" +
                                    "<customer_email>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email)) + "</customer_email>" +
                                    "<remote_ip>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip)) + "</remote_ip>" );
                                        if (functions.isValueNull(moto)){
                                            sale3DRequest.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                                        }

                                        sale3DRequest.append(  "<billing_address>" +
                                        "<first_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName)) + "</first_name>" +
                                        "<last_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName)) + "</last_name>" +
                                        "<address1>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1)) + "</address1>" +
                                        "<address2>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2)) + "</address2>" +
                                        "<zip_code>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code)) + "</zip_code>" +
                                        "<city>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(city)) + "</city>" +
                                        "<state>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(state)) + "</state>" +
                                        "<country>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(country)) + "</country>" +
                                    "</billing_address>" +
                                "</payment_transaction>");
                StringBuffer sale3DRequestlog=new StringBuffer();
                sale3DRequestlog.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<payment_transaction>" +
                        "<transaction_type>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type)) + "</transaction_type>" +
                        "<transaction_id>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID)) + "</transaction_id>" +
                        "<notification_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(notificationUrlNew)) + "</notification_url>" +
                        "<return_success_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_success_url)) + "</return_success_url>" +
                        "<return_failure_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_failure_url)) + "</return_failure_url>" +
                        "<amount>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount)) + "</amount>" +
                        "<currency>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency)) + "</currency>" +
                        "<card_holder>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName)) + "</card_holder>" +
                        "<card_number>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingPan(card_number))) + "</card_number>" +
                        "<cvv>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(cvv))) + "</cvv>" +
                        "<expiration_month>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_month))) + "</expiration_month>" +
                        "<expiration_year>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_year))) + "</expiration_year>" +
                        "<customer_email>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email)) + "</customer_email>" +
                        "<remote_ip>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip)) + "</remote_ip>" );
                if (functions.isValueNull(moto)){
                    sale3DRequestlog.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                }

                sale3DRequestlog.append(  "<billing_address>" +
                        "<first_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName)) + "</first_name>" +
                        "<last_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName)) + "</last_name>" +
                        "<address1>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1)) + "</address1>" +
                        "<address2>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2)) + "</address2>" +
                        "<zip_code>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code)) + "</zip_code>" +
                        "<city>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(city)) + "</city>" +
                        "<state>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(state)) + "</state>" +
                        "<country>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(country)) + "</country>" +
                        "</billing_address>" +
                        "</payment_transaction>");

                transactionLogger.error("sale3DRequest ----" +trackingID + "--" + sale3DRequestlog);

                String sale3DResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest saleRequest3D-----" + testUrl);
                    sale3DResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, sale3DRequest.toString(), "Basic", encodedCredentials);
                }
                else
                {
                    transactionLogger.error("inside isLive saleRequest3D-----" + liveUrl);
                    sale3DResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, sale3DRequest.toString(), "Basic", encodedCredentials);
                }
                transactionLogger.error("sale3DResponse ---" + trackingID+"----"+sale3DResponse);
                responseMap = ecpUtils.readSoapResponse(sale3DResponse);
                if (responseMap != null)
                {
                    transactionType_fromResponse = responseMap.get("transaction_type");
                    status_fromResponse = responseMap.get("status");
                    authorization_code = responseMap.get("authorization_code");
                    unique_id = responseMap.get("unique_id");
                    transactionid_fromResponse = responseMap.get("transaction_id");
                    responseCode = responseMap.get("response_code");
                    technical_message = responseMap.get("technical_message");
                    message = responseMap.get("message");
                    mode = responseMap.get("mode");
                    timestamp_fromResponse = responseMap.get("timestamp");
                    amount_fromResponse = responseMap.get("amount");
                    currency_fromResponse = responseMap.get("currency");
                    sent_to_acquirer = responseMap.get("sent_to_acquirer");
                    descriptor_fromResponse = responseMap.get("descriptor");
                    acs_url = responseMap.get("redirect_url");
                    if("approved".equalsIgnoreCase(status_fromResponse)|| responseCode.equalsIgnoreCase("00"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(technical_message);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionType(transactionType_fromResponse);
                        commResponseVO.setErrorCode(responseCode);
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setAuthCode(authorization_code);
                    }
                    else if (status_fromResponse.equalsIgnoreCase("pending_async"))
                    {
                        transactionLogger.error("inside--------- status = pending_async");
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(acs_url);
                        commResponseVO.setTerURL(notificationUrlNew);
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription("SYS:3D Authentication Pending / " + technical_message);
                        commResponseVO.setTransactionType(transactionType_fromResponse);
                        commResponseVO.setErrorCode(responseCode);
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription("SYS:3D Authentication Pending " + technical_message);
                        commResponseVO.setErrorCode(responseCode);
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }
            else
            {
                transactionLogger.error("Inside Non 3D ---");
                transaction_type="sale";

                 if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                StringBuffer saleNon3dRequest =new StringBuffer();
                saleNon3dRequest.append(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>"+
                                "<transaction_type>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type))+"</transaction_type>"+
                                "<transaction_id>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID))+"</transaction_id>"+
                                "<amount>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount))+"</amount>"+
                                "<currency>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency))+"</currency>"+
                                "<card_holder>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName))+"</card_holder>"+
                                "<card_number>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(card_number))+"</card_number>"+
                                "<cvv>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cvv))+"</cvv>"+
                                "<expiration_month>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_month))+"</expiration_month>"+
                                "<expiration_year>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_year))+"</expiration_year>"+
                                "<customer_email>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email))+"</customer_email>"+
                                "<remote_ip>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip))+"</remote_ip>");
                                    if (functions.isValueNull(moto)){
                                        saleNon3dRequest.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                                    }

                                    saleNon3dRequest.append( "<billing_address>"+
                                "<first_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName))+"</first_name>"+
                                "<last_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName))+"</last_name>"+
                                "<address1>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1))+"</address1>"+
                                "<address2>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2))+"</address2>"+
                                "<zip_code>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code))+"</zip_code>"+
                                "<city>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(city))+"</city>"+
                                "<state>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(state))+"</state>"+
                                "<country>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(country))+"</country>"+
                                "</billing_address>"+
                                "</payment_transaction>");

                StringBuffer saleNon3dRequestlog =new StringBuffer();
                saleNon3dRequestlog.append(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>"+
                                "<transaction_type>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type))+"</transaction_type>"+
                                "<transaction_id>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID))+"</transaction_id>"+
                                "<amount>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount))+"</amount>"+
                                "<currency>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency))+"</currency>"+
                                "<card_holder>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName))+"</card_holder>"+
                                "<card_number>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingPan(card_number)))+"</card_number>"+
                                "<cvv>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(cvv)))+"</cvv>"+
                                "<expiration_month>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_month)))+"</expiration_month>"+
                                "<expiration_year>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_year)))+"</expiration_year>"+
                                "<customer_email>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email))+"</customer_email>"+
                                "<remote_ip>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip))+"</remote_ip>");
                if (functions.isValueNull(moto)){
                    saleNon3dRequestlog.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                }

                saleNon3dRequestlog.append( "<billing_address>"+
                        "<first_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName))+"</first_name>"+
                        "<last_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName))+"</last_name>"+
                        "<address1>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1))+"</address1>"+
                        "<address2>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2))+"</address2>"+
                        "<zip_code>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code))+"</zip_code>"+
                        "<city>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(city))+"</city>"+
                        "<state>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(state))+"</state>"+
                        "<country>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(country))+"</country>"+
                        "</billing_address>"+
                        "</payment_transaction>");

                transactionLogger.error("saleNon3dRequest ---"+trackingID + "--" +saleNon3dRequestlog);

                String saleNon3dResponse="";
                if(isTest){
                    transactionLogger.error("inside saleNon3dRequest isTest-----" +testUrl);
                    saleNon3dResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, saleNon3dRequest.toString(), "Basic", encodedCredentials);
                }else {
                    transactionLogger.error("inside saleNon3dRequest isLive-----" + liveUrl);
                    saleNon3dResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, saleNon3dRequest.toString(), "Basic", encodedCredentials);
                }
                transactionLogger.error("saleNon3dResponse ---" + trackingID + "--" +saleNon3dResponse);

                responseMap=ecpUtils.readSoapResponse(saleNon3dResponse);
                if (responseMap!=null)
                {
                    transactionType_fromResponse = responseMap.get("transaction_type");
                    status_fromResponse = responseMap.get("status");
                    authorization_code = responseMap.get("authorization_code");
                    unique_id = responseMap.get("unique_id");
                    transactionid_fromResponse = responseMap.get("transaction_id");
                    responseCode = responseMap.get("response_code");
                    technical_message = responseMap.get("technical_message");
                    message = responseMap.get("message");
                    mode = responseMap.get("mode");
                    timestamp_fromResponse = responseMap.get("timestamp");
                    amount_fromResponse = responseMap.get("amount");
                    currency_fromResponse = responseMap.get("currency");
                    sent_to_acquirer= responseMap.get("sent_to_acquirer");
                    descriptor_fromResponse = responseMap.get("descriptor");

                    if ( status_fromResponse.equalsIgnoreCase("approved") || responseCode.equalsIgnoreCase("00") )
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionId(unique_id);
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(technical_message);
                    commResponseVO.setTransactionType(transactionType_fromResponse);
                    commResponseVO.setErrorCode(responseCode);
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setCurrency(currency_fromResponse);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }

        return  commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processAuthentication ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils=new ECPUtils();

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";

        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String amount=transDetailsVO.getAmount();
        transactionLogger.error("amount ---"+amount);

        if(currency.equals("JPY") ){
            amount=ecpUtils.getJPYSupportedAmount(amount);
        }
        else if(currency.equals("KWD")){
            amount=ecpUtils.getKWDSupportedAmount(amount);
        }else{
            amount=ecpUtils.getAmount(amount);
        }
        transactionLogger.error("amount after formatting  ---"+amount);

        String cardHolderName="";
        String firstName="";
        String lastName="";
        if (functions.isValueNull(addressDetailsVO.getFirstname()))
            firstName=addressDetailsVO.getFirstname();
        if (functions.isValueNull(addressDetailsVO.getLastname()))
            lastName=addressDetailsVO.getLastname();

        cardHolderName=firstName+" "+lastName;
        String card_number=cardDetailsVO.getCardNum();
        String cvv=cardDetailsVO.getcVV();
        String expiration_month=cardDetailsVO.getExpMonth();
        String expiration_year=cardDetailsVO.getExpYear();
        String customer_email=addressDetailsVO.getEmail();
        String remote_ip="";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())) {
            remote_ip=addressDetailsVO.getCardHolderIpAddress();
        }
        else {
            remote_ip=addressDetailsVO.getIp();
        }

        String address1=addressDetailsVO.getStreet();
        String address2=addressDetailsVO.getStreet();
        String city=addressDetailsVO.getCity();
        String zip_code=addressDetailsVO.getZipCode();
        String state=addressDetailsVO.getState();
        String country=addressDetailsVO.getCountry();

        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String transaction_type="";
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }
        String notificationUrlNew=notificationUrl+trackingID;
        transactionLogger.error("notificationUrlNew ---"+notificationUrlNew);
        String return_success_url=termUrl+trackingID+"&status=success";
        transactionLogger.error("return_success_url ---"+return_success_url);
        String return_failure_url=termUrl+trackingID+"&status=fail";
        transactionLogger.error("return_failure_url ---"+return_failure_url);


        String token=ecpUtils.getToken(currency,is3dSupported,isTest,accountId);
        transactionLogger.error("token ---"+token);
        String testUrl=RB.getString("TEST_URL")+token;
        transactionLogger.debug("testUrl ---"+testUrl);
        String liveUrl=RB.getString("LIVE_URL")+token;
        transactionLogger.debug("liveUrl ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String  unique_id = "";
        String responseCode = "";
        String technical_message = "";
        String message = "";
        String acs_url = "";
        String authorization_code = "";
        String transactionid_fromResponse = "";
        String mode = "";
        String timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sent_to_acquirer = "";
        String descriptor_fromResponse = "";
        String moto= "";

        try
        {
            if ("Y".equals(is3dSupported) || "O".equals(is3dSupported))
            {
                transaction_type = "authorize3d";
                StringBuffer authorize3DRequest=new StringBuffer();
                if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                 authorize3DRequest.append(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>" +
                                "<transaction_type>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type)) + "</transaction_type>" +
                                "<transaction_id>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID)) + "</transaction_id>" +
                                "<notification_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(notificationUrlNew)) + "</notification_url>" +
                                "<return_success_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_success_url)) + "</return_success_url>" +
                                "<return_failure_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_failure_url)) + "</return_failure_url>" +
                                "<amount>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount)) + "</amount>" +
                                "<currency>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency)) + "</currency>" +
                                "<card_holder>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName)) + "</card_holder>" +
                                "<card_number>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(card_number)) + "</card_number>" +
                                "<cvv>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cvv)) + "</cvv>" +
                                "<expiration_month>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_month)) + "</expiration_month>" +
                                "<expiration_year>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_year)) + "</expiration_year>" +
                                "<customer_email>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email)) + "</customer_email>" +
                                "<remote_ip>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip)) + "</remote_ip>" );
                                if(functions.isValueNull(moto)){
                                authorize3DRequest.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                                 }
                                authorize3DRequest.append( "<billing_address>" +
                                "<first_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName)) + "</first_name>" +
                                "<last_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName)) + "</last_name>" +
                                "<address1>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1)) + "</address1>" +
                                "<address2>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2)) + "</address2>" +
                                "<zip_code>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code)) + "</zip_code>" +
                                "<city>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(city)) + "</city>" +
                                "<state>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(state)) + "</state>" +
                                "<country>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(country)) + "</country>" +
                                "</billing_address>" +
                                "</payment_transaction>");
                StringBuffer authorize3DRequestlog=new StringBuffer();
                if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                authorize3DRequestlog.append(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>" +
                                "<transaction_type>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type)) + "</transaction_type>" +
                                "<transaction_id>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID)) + "</transaction_id>" +
                                "<notification_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(notificationUrlNew)) + "</notification_url>" +
                                "<return_success_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_success_url)) + "</return_success_url>" +
                                "<return_failure_url>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(return_failure_url)) + "</return_failure_url>" +
                                "<amount>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount)) + "</amount>" +
                                "<currency>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency)) + "</currency>" +
                                "<card_holder>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName)) + "</card_holder>" +
                                "<card_number>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingPan(card_number))) + "</card_number>" +
                                "<cvv>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(cvv))) + "</cvv>" +
                                "<expiration_month>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_month))) + "</expiration_month>" +
                                "<expiration_year>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_year))) + "</expiration_year>" +
                                "<customer_email>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email)) + "</customer_email>" +
                                "<remote_ip>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip)) + "</remote_ip>" );
                if(functions.isValueNull(moto)){
                    authorize3DRequestlog.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                }
                authorize3DRequestlog.append( "<billing_address>" +
                        "<first_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName)) + "</first_name>" +
                        "<last_name>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName)) + "</last_name>" +
                        "<address1>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1)) + "</address1>" +
                        "<address2>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2)) + "</address2>" +
                        "<zip_code>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code)) + "</zip_code>" +
                        "<city>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(city)) + "</city>" +
                        "<state>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(state)) + "</state>" +
                        "<country>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(country)) + "</country>" +
                        "</billing_address>" +
                        "</payment_transaction>");

                transactionLogger.error("authorize3DRequest ---" +trackingID+"-----" +authorize3DRequestlog);
                String authorize3DResponse = "";
                if (isTest)
                {
                    transactionLogger.error("inside isTest-----" + testUrl);
                    authorize3DResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, authorize3DRequest.toString(), "Basic", encodedCredentials);
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + liveUrl);
                    authorize3DResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, authorize3DRequest.toString(), "Basic", encodedCredentials);
                }
                transactionLogger.error("authorize3DResponse ---" + "-----" +authorize3DResponse);

                responseMap = ecpUtils.readSoapResponse(authorize3DResponse);
                if (responseMap != null)
                {
                    transactionType_fromResponse = responseMap.get("transaction_type");
                    status_fromResponse = responseMap.get("status");
                    authorization_code = responseMap.get("authorization_code");
                    unique_id = responseMap.get("unique_id");
                    transactionid_fromResponse = responseMap.get("transaction_id");
                    responseCode = responseMap.get("response_code");
                    technical_message = responseMap.get("technical_message");
                    message = responseMap.get("message");
                    mode = responseMap.get("mode");
                    timestamp_fromResponse = responseMap.get("timestamp");
                    amount_fromResponse = responseMap.get("amount");
                    currency_fromResponse = responseMap.get("currency");
                    sent_to_acquirer = responseMap.get("sent_to_acquirer");
                    descriptor_fromResponse = responseMap.get("descriptor");
                    acs_url = responseMap.get("redirect_url");
                    if("approved".equalsIgnoreCase(status_fromResponse)|| responseCode.equalsIgnoreCase("00"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(technical_message);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setTransactionType(transactionType_fromResponse);
                        commResponseVO.setErrorCode(responseCode);
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setAuthCode(authorization_code);
                    }
                    else if (status_fromResponse.equalsIgnoreCase("pending_async"))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(acs_url);
                        commResponseVO.setTerURL(notificationUrlNew);
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setRemark(message );
                        commResponseVO.setDescription("SYS:3D Authentication Pending / "+technical_message);
                        commResponseVO.setTransactionType(transactionType_fromResponse);
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionId(unique_id);
                        commResponseVO.setRemark(message + "-" + technical_message);
                        commResponseVO.setDescription(technical_message);
                        commResponseVO.setErrorCode(responseCode);
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }
            else
            {
                transaction_type="authorize";
                StringBuffer authorizeRequest = new StringBuffer();
                if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                authorizeRequest.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<payment_transaction>"+
                                "<transaction_type>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type))+"</transaction_type>"+
                                "<transaction_id>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID))+"</transaction_id>"+
                                "<amount>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount))+"</amount>"+
                                "<currency>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency))+"</currency>"+
                                "<card_holder>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName))+"</card_holder>"+
                                "<card_number>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(card_number))+"</card_number>"+
                                "<cvv>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cvv))+"</cvv>"+
                                "<expiration_month>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_month))+"</expiration_month>"+
                                "<expiration_year>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(expiration_year))+"</expiration_year>"+
                                "<customer_email>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email))+"</customer_email>"+
                                "<remote_ip>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip))+"</remote_ip>");
                                if(functions.isValueNull(moto)){
                                    authorizeRequest.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                                }

                                   authorizeRequest.append("<billing_address>"+
                                "<first_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName))+"</first_name>"+
                                "<last_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName))+"</last_name>"+
                                "<address1>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1))+"</address1>"+
                                "<address2>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2))+"</address2>"+
                                "<zip_code>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code))+"</zip_code>"+
                                "<city>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(city))+"</city>"+
                                "<state>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(state))+"</state>"+
                                "<country>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(country))+"</country>"+
                                "</billing_address>"+
                                "</payment_transaction>");
                StringBuffer authorizeRequestlog = new StringBuffer();
                if("Y".equalsIgnoreCase(gatewayAccount.getForexMid())){
                    moto="true";
                }
                authorizeRequestlog.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<payment_transaction>"+
                        "<transaction_type>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(transaction_type))+"</transaction_type>"+
                        "<transaction_id>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(trackingID))+"</transaction_id>"+
                        "<amount>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(amount))+"</amount>"+
                        "<currency>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(currency))+"</currency>"+
                        "<card_holder>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(cardHolderName))+"</card_holder>"+
                        "<card_number>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingPan(card_number))) + "</card_number>" +
                        "<cvv>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(cvv))) + "</cvv>" +
                        "<expiration_month>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_month))) + "</expiration_month>" +
                        "<expiration_year>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(functions.maskingNumber(expiration_year))) + "</expiration_year>" +
                        "<customer_email>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(customer_email))+"</customer_email>"+
                        "<remote_ip>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(remote_ip))+"</remote_ip>");
                if(functions.isValueNull(moto)){
                    authorizeRequestlog.append( "<moto>" + StringEscapeUtils.escapeXml(ecpUtils.checkNull(moto)) + "</moto>" );
                }

                authorizeRequestlog.append("<billing_address>"+
                        "<first_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(firstName))+"</first_name>"+
                        "<last_name>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(lastName))+"</last_name>"+
                        "<address1>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address1))+"</address1>"+
                        "<address2>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(address2))+"</address2>"+
                        "<zip_code>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(zip_code))+"</zip_code>"+
                        "<city>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(city))+"</city>"+
                        "<state>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(state))+"</state>"+
                        "<country>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(country))+"</country>"+
                        "</billing_address>"+
                        "</payment_transaction>");

                transactionLogger.error("authorizeRequest ---"+trackingID + "--" + authorizeRequestlog);
                 String authorizeResponse="";
                if(isTest){
                    transactionLogger.error("inside isTest-----" +testUrl);
                    authorizeResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, authorizeRequest.toString(), "Basic", encodedCredentials);
                }else {
                    transactionLogger.error("inside isLive-----" + liveUrl);
                    authorizeResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, authorizeRequest.toString(), "Basic", encodedCredentials);
                }
                transactionLogger.error("authorizeResponse ---"+trackingID + "--" + authorizeResponse);

                responseMap=ecpUtils.readSoapResponse(authorizeResponse);
                if (responseMap!=null)
                {

                    transactionType_fromResponse = responseMap.get("transaction_type");
                    status_fromResponse = responseMap.get("status");
                    authorization_code = responseMap.get("authorization_code");
                    unique_id = responseMap.get("unique_id");
                    transactionid_fromResponse = responseMap.get("transaction_id");
                    responseCode = responseMap.get("response_code");
                    technical_message = responseMap.get("technical_message");
                    message = responseMap.get("message");
                    mode = responseMap.get("mode");
                    timestamp_fromResponse = responseMap.get("timestamp");
                    amount_fromResponse = responseMap.get("amount");
                    currency_fromResponse = responseMap.get("currency");
                    sent_to_acquirer= responseMap.get("sent_to_acquirer");
                    descriptor_fromResponse = responseMap.get("descriptor");


                    if ( status_fromResponse.equalsIgnoreCase("approved") || responseCode.equalsIgnoreCase("00") )
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setTransactionId(unique_id);
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(technical_message);
                    commResponseVO.setTransactionType(transactionType_fromResponse);
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS:Transaction Declined");
                }
            }
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setCurrency(currency_fromResponse);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processCapture ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils=new ECPUtils();
        String currency = "";
        String tmpl_amount = "";
       String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String amount=transDetailsVO.getAmount();
        transactionLogger.error("amount ---"+amount);

        if(currency.equals("JPY") ){
            amount=ecpUtils.getJPYSupportedAmount(amount);
        }
        else if(currency.equals("KWD")){
            amount=ecpUtils.getKWDSupportedAmount(amount);
        }else{
            amount=ecpUtils.getAmount(amount);
        }
        transactionLogger.error("amount after formatting  ---"+amount);

        boolean isTest = gatewayAccount.isTest();
//        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String is3dSupported =ecpUtils.checkPreviousTransaction3D_Non3D(trackingID);
        String transaction_type="capture";
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }
        String notificationUrlNew=notificationUrl+trackingID;
        transactionLogger.error("notificationUrlNew ---"+notificationUrlNew);
        String return_success_url=termUrl+trackingID+"&status=success";
        transactionLogger.error("return_success_url ---"+return_success_url);
        String return_failure_url=termUrl+trackingID+"&status=fail";
        transactionLogger.error("return_failure_url ---"+return_failure_url);

        String token=ecpUtils.getToken(currency,is3dSupported,isTest,accountId);
        transactionLogger.error("token ---"+token);
        String testUrl=RB.getString("TEST_URL")+token;
        transactionLogger.debug("testUrl ---"+testUrl);
        String liveUrl=RB.getString("LIVE_URL")+token;
        transactionLogger.debug("liveUrl ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String authorizationCode_fromResponse = "";
        String
                unique_id = "";
        if (functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId()))
        {
            unique_id=commTransactionDetailsVO.getPreviousTransactionId();
        }
        transactionLogger.debug("unique_id for capture ---"+unique_id);
        String transactionId_fromResponse = "";
        String responseCode_fromResponse = "";
        String technical_message = "";
        String message_fromResponse = "";
        String  timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sentToAcquirer_fromResponse = "";
        String descriptor_fromResponse = "";
        String redirect_url = "";
        String acs_url = "";
        String mode_fromResponse = "";

        try
        {
            String captureRequest =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<payment_transaction>" +
                            "<transaction_type>" + ecpUtils.checkNull(transaction_type) + "</transaction_type>" +
                            "<transaction_id>" + ecpUtils.checkNull(trackingID) + "_C</transaction_id>" +
                            "<reference_id>" + ecpUtils.checkNull(unique_id) + "</reference_id>" +
                            "<amount>" + ecpUtils.checkNull(amount) + "</amount>" +
                            "<currency>" + ecpUtils.checkNull(currency) + "</currency>" +
                            "</payment_transaction>";

            transactionLogger.error("captureRequest ---" +trackingID + "--" + captureRequest);
            String captureResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + testUrl);
                captureResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, captureRequest, "Basic", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + liveUrl);
                captureResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, captureRequest, "Basic", encodedCredentials);
            }
            transactionLogger.error("captureResponse ---" + trackingID + "--" +captureResponse);

            responseMap=ecpUtils.readSoapResponse(captureResponse);
            if (responseMap!=null)
            {

                transactionType_fromResponse = responseMap.get("transaction_type");
                status_fromResponse = responseMap.get("status");
                authorizationCode_fromResponse = responseMap.get("authorization_code");
                unique_id = responseMap.get("unique_id");
                transactionId_fromResponse = responseMap.get("transaction_id");
                responseCode_fromResponse = responseMap.get("response_code");
                technical_message = responseMap.get("technical_message");
                message_fromResponse = responseMap.get("message");
                mode_fromResponse = responseMap.get("mode");
                timestamp_fromResponse= responseMap.get("timestamp");
                amount_fromResponse = responseMap.get("amount");
                currency_fromResponse = responseMap.get("currency");
                sentToAcquirer_fromResponse = responseMap.get("sent_to_acquirer");
                descriptor_fromResponse = responseMap.get("descriptor");


                if ( status_fromResponse.equalsIgnoreCase("approved") || responseCode_fromResponse.equalsIgnoreCase("00") )
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(unique_id);
                commResponseVO.setRemark(message_fromResponse);
                commResponseVO.setDescription(technical_message+"-"+message_fromResponse);
                commResponseVO.setTransactionType(transactionType_fromResponse);
                commResponseVO.setErrorCode(responseCode_fromResponse);
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils=new ECPUtils();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency())) {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount())) {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency())) {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String amount=transDetailsVO.getAmount();
        transactionLogger.error("amount ---"+amount);

        if(currency.equals("JPY") ){
            amount=ecpUtils.getJPYSupportedAmount(amount);
        }
        else if(currency.equals("KWD")){
            amount=ecpUtils.getKWDSupportedAmount(amount);
        }else{
            amount=ecpUtils.getAmount(amount);
        }
        transactionLogger.error("amount after formatting  ---"+amount);

        String remote_ip="";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())) {
            remote_ip=addressDetailsVO.getCardHolderIpAddress();
        }
        else {
            remote_ip=addressDetailsVO.getIp();
        }

        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = ecpUtils.checkPreviousTransaction3D_Non3D(trackingID);
        String  transaction_type ="refund";
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }
        transactionLogger.debug("after if else for host url");
        String notificationUrlNew=notificationUrl+trackingID;
        transactionLogger.error("notificationUrlNew ---"+notificationUrlNew);
        String return_success_url=termUrl+trackingID+"&status=success";
        transactionLogger.error("return_success_url ---"+return_success_url);
        String return_failure_url=termUrl+trackingID+"&status=fail";
        transactionLogger.error("return_failure_url ---"+return_failure_url);

        String token=ecpUtils.getToken(currency,is3dSupported,isTest,accountId);
        transactionLogger.error("token ---"+token);
        String testUrl=RB.getString("TEST_URL")+token;
        transactionLogger.debug("testUrl ---"+testUrl);
        String liveUrl=RB.getString("LIVE_URL")+token;
        transactionLogger.debug("liveUrl ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String authorizationCode_fromResponse = "";
        String  unique_id = "";
        String transactionId_fromResponse = "";
        String responseCode_fromResponse = "";
        String technical_message = "";
        String message_fromResponse = "";
        String  timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sentToAcquirer_fromResponse = "";
        String descriptor_fromResponse = "";
        String redirect_url = "";
        String acs_url = "";
        String mode_fromResponse = "";
        String refundCount = commRequestVO.getCount();

        unique_id = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            String refundRequest =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<payment_transaction>"+
                            "<transaction_type>"+ecpUtils.checkNull(transaction_type)+"</transaction_type>"+
                            "<transaction_id>"+ecpUtils.checkNull(trackingID)+"_R"+ecpUtils.checkNull(refundCount)+"</transaction_id>"+
                            "<remote_ip>"+ecpUtils.checkNull(remote_ip)+"</remote_ip>"+
                            "<reference_id>"+StringEscapeUtils.escapeXml(ecpUtils.checkNull(unique_id))+"</reference_id>"+
                            "<amount>"+ecpUtils.checkNull(amount)+"</amount>"+
                            "<currency>"+ecpUtils.checkNull(currency)+"</currency>"+
                            "</payment_transaction>";

            transactionLogger.error("refundRequest ---" +trackingID + "--" + refundRequest);
            String refundResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + testUrl);
                refundResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, refundRequest, "Basic", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + liveUrl);
                refundResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, refundRequest, "Basic", encodedCredentials);
            }
            transactionLogger.error("refundResponse ---" + trackingID + "--" +refundResponse);

            responseMap=ecpUtils.readSoapResponse(refundResponse);
            if (responseMap!=null)
            {

                transactionType_fromResponse = responseMap.get("transaction_type");
                status_fromResponse = responseMap.get("status");
                authorizationCode_fromResponse = responseMap.get("authorization_code");
                unique_id = responseMap.get("unique_id");
                transactionId_fromResponse = responseMap.get("transaction_id");
                responseCode_fromResponse = responseMap.get("response_code");
                technical_message = responseMap.get("technical_message");
                message_fromResponse = responseMap.get("message");
                mode_fromResponse = responseMap.get("mode");
                timestamp_fromResponse= responseMap.get("timestamp");
                amount_fromResponse = responseMap.get("amount");
                currency_fromResponse = responseMap.get("currency");
                sentToAcquirer_fromResponse = responseMap.get("sent_to_acquirer");
                descriptor_fromResponse = responseMap.get("descriptor");


                if ( status_fromResponse.equalsIgnoreCase("approved") || responseCode_fromResponse.equalsIgnoreCase("00") )
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(unique_id);
                commResponseVO.setRemark(message_fromResponse);
                commResponseVO.setDescription(technical_message);
                commResponseVO.setTransactionType(transactionType_fromResponse);
                commResponseVO.setErrorCode(responseCode_fromResponse);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
        }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;

    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processVoid ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils=new ECPUtils();
        String currency = commRequestVO.getTransDetailsVO().getCurrency();


        String amount=transDetailsVO.getAmount();
        transactionLogger.error("amount ---"+amount);
        if(currency.equals("JPY") ){
            amount=ecpUtils.getJPYSupportedAmount(amount);
        }
        else if(currency.equals("KWD")){
            amount=ecpUtils.getKWDSupportedAmount(amount);
        }else{
            amount=ecpUtils.getAmount(amount);
        }
        transactionLogger.error("amount after formatting  ---"+amount);

        String remote_ip="";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())) {
            remote_ip=addressDetailsVO.getCardHolderIpAddress();
        }
        else {
            remote_ip=addressDetailsVO.getIp();
        }

        boolean isTest = gatewayAccount.isTest();
//        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String is3dSupported = ecpUtils.checkPreviousTransaction3D_Non3D(trackingID);
        String transaction_type="";
        String termUrl = "";
        String notificationUrl="";
        transactionLogger.error("HOST_URL ---" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notificationUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("NOTIFICATION_HOST_URL");
            transactionLogger.error("From HOST_URL----" + termUrl);
            transactionLogger.error("From HOST_URL notificationUrl ----" + notificationUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notificationUrl = RB.getString("NOTIFICATION_TERM_URL");
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
            transactionLogger.error("From RB notificationUrl ----" + notificationUrl);
        }
        String notificationUrlNew=notificationUrl+trackingID;
        transactionLogger.error("notificationUrlNew ---"+notificationUrlNew);
        String return_success_url=termUrl+trackingID+"&status=success";
        transactionLogger.error("return_success_url ---"+return_success_url);
        String return_failure_url=termUrl+trackingID+"&status=fail";
        transactionLogger.error("return_failure_url ---"+return_failure_url);

        String token=ecpUtils.getToken(currency,is3dSupported,isTest,accountId);
        transactionLogger.error("token ---"+token);
        String testUrl=RB.getString("TEST_URL")+token;
        transactionLogger.debug("testUrl ---"+testUrl);
        String liveUrl=RB.getString("LIVE_URL")+token;
        transactionLogger.debug("liveUrl ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String authorizationCode_fromResponse = "";
        String  unique_id = "";
        String transactionId_fromResponse = "";
        String responseCode_fromResponse = "";
        String technical_message = "";
        String message_fromResponse = "";
        String  timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sentToAcquirer_fromResponse = "";
        String descriptor_fromResponse = "";
        String redirect_url = "";
        String acs_url = "";
        String mode_fromResponse = "";

        try
        {
            transaction_type = "void";
            unique_id = commTransactionDetailsVO.getPreviousTransactionId();
            String voidRequest =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<payment_transaction>"+
                        "<transaction_type>"+ecpUtils.checkNull(transaction_type)+"</transaction_type>"+
                        "<transaction_id>"+ecpUtils.checkNull(trackingID)+"_RV</transaction_id>"+
                        "<remote_ip>"+ecpUtils.checkNull(remote_ip)+"</remote_ip>"+
                        "<reference_id>"+ecpUtils.checkNull(unique_id)+"</reference_id>"+
                    "</payment_transaction>";

            transactionLogger.error("voidRequest ---" +trackingID + "--" + voidRequest);
            String voidResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + testUrl);
                voidResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, voidRequest, "Basic", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + liveUrl);
                voidResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, voidRequest, "Basic", encodedCredentials);
            }
            transactionLogger.error("voidResponse ---" + trackingID + "--" +voidResponse);

            responseMap=ecpUtils.readSoapResponse(voidResponse);
            if (responseMap!=null)
            {
                transactionType_fromResponse = responseMap.get("transaction_type");
                status_fromResponse = responseMap.get("status");
                authorizationCode_fromResponse = responseMap.get("authorization_code");
                unique_id = responseMap.get("unique_id");
                transactionId_fromResponse = responseMap.get("transaction_id");
                responseCode_fromResponse = responseMap.get("response_code");
                technical_message = responseMap.get("technical_message");
                message_fromResponse = responseMap.get("message");
                mode_fromResponse = responseMap.get("mode");
                timestamp_fromResponse= responseMap.get("timestamp");
                amount_fromResponse = responseMap.get("amount");
                currency_fromResponse = responseMap.get("currency");
                sentToAcquirer_fromResponse = responseMap.get("sent_to_acquirer");
                descriptor_fromResponse = responseMap.get("descriptor");


                if ( status_fromResponse.equalsIgnoreCase("approved") || responseCode_fromResponse.equalsIgnoreCase("00") )
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
                commResponseVO.setTransactionId(unique_id);
                commResponseVO.setRemark(message_fromResponse);
                commResponseVO.setDescription(technical_message);
                commResponseVO.setTransactionType(transactionType_fromResponse);
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }

        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;

    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processInquiry ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils=new ECPUtils();
//        String reject3DCard=commRequestVO.getReject3DCard();
        String currency = "";

        boolean isTest = gatewayAccount.isTest();
        transactionLogger.error("token ---" );
        String testUrl=RB.getString("INQUIRY_TEST_URL");
        transactionLogger.debug("INQUIRY_TEST_URL ---"+testUrl);
        String liveUrl=RB.getString("INQUIRY_LIVE_URL");
        transactionLogger.debug("INQUIRY_LIVE_URL ---"+liveUrl);
        Map<String,String> responseMap;
        String transactionType_fromResponse = "";
        String status_fromResponse = "";
        String authorizationCode_fromResponse = "";
        String  unique_id = "";
        String transactionId_fromResponse = "";
        String responseCode_fromResponse = "";
        String technical_message = "";
        String message_fromResponse = "";
        String  timestamp_fromResponse = "";
        String amount_fromResponse = "";
        String currency_fromResponse = "";
        String sentToAcquirer_fromResponse = "";
        String descriptor_fromResponse = "";
        String redirect_url = "";
        String acs_url = "";
        String mode_fromResponse = "";
        String transaction_date="";
        String unique_idFromResponse="";
        String auth_code_FromResponse="";
        unique_id = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("unique_id --------------"+unique_id);
        try
        {
            String inquiryRequest =
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<processed_transaction_request>" +
                            "<unique_id>" + ecpUtils.checkNull(unique_id) + "</unique_id>" +
                            "</processed_transaction_request>";

            transactionLogger.error("inquiryRequest ---" + inquiryRequest);
            String inquiryResponse = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + testUrl);
                inquiryResponse = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, inquiryRequest, "Basic", encodedCredentials);
            }
            else
            {
                transactionLogger.error("inside isLive-----" + liveUrl);
                inquiryResponse = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, inquiryRequest, "Basic", encodedCredentials);
            }
            transactionLogger.error("inquiryResponse ---" + inquiryResponse);

            responseMap=ecpUtils.readSoapResponse(inquiryResponse);
            if (responseMap!=null)
            {
//                transactionType_fromResponse = responseMap.get("transaction_type");
                  status_fromResponse = responseMap.get("status");
//                authorizationCode_fromResponse = responseMap.get("authorization_code");
//                unique_id = responseMap.get("unique_id");
//                transactionId_fromResponse = responseMap.get("transaction_id");
//                responseCode_fromResponse = responseMap.get("response_code");
//                technical_message = responseMap.get("technical_message");
                message_fromResponse = responseMap.get("message");
//                mode_fromResponse = responseMap.get("mode");
//                timestamp_fromResponse= responseMap.get("timestamp");
//                amount_fromResponse = responseMap.get("amount");
//                currency_fromResponse = responseMap.get("currency");
//                sentToAcquirer_fromResponse = responseMap.get("sent_to_acquirer");
//                descriptor_fromResponse = responseMap.get("descriptor");

                transaction_date = responseMap.get("transaction_date");
                amount_fromResponse = responseMap.get("amount");
                currency_fromResponse = responseMap.get("currency");
                unique_idFromResponse = responseMap.get("unique_id");
                auth_code_FromResponse = responseMap.get("auth_code");


                if (functions.isValueNull(status_fromResponse) && status_fromResponse.equalsIgnoreCase("error"))
                {
                    transactionLogger.debug("inside if for fail");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus("fail");
                }
                else
                {
                    transactionLogger.debug("inside else for success");
                    commResponseVO.setStatus("success");
//                   commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                    commResponseVO.setBankTransactionDate(transaction_date);
                    commResponseVO.setTransactionStatus("success");
//                    commResponseVO.setTransactionType("Inquiry");
//                    commResponseVO.setTransactionStatus("success");
                }
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setCurrency(currency);
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                commResponseVO.setRemark(message_fromResponse);
                commResponseVO.setDescription(message_fromResponse);
                commResponseVO.setTransactionType("Inquiry");
                commResponseVO.setTransactionId(unique_id);

                if( functions.isValueNull(auth_code_FromResponse)){
                    commResponseVO.setAuthCode(auth_code_FromResponse);
                }
                else{
                    commResponseVO.setAuthCode("-");
                }
                if( functions.isValueNull(message_fromResponse)){
                    commResponseVO.setDescription(message_fromResponse);
                }
                else{
                    commResponseVO.setDescription("-");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }
    public void processChargebackByUID(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processChargebackByUID() ---");

        GenericResponseVO genericResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils = new ECPUtils();

        boolean isTest = gatewayAccount.isTest();

        String unique_id  = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("unique_id --------------" + unique_id);

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<chargeback_request>" +
                        "<original_transaction_unique_id>"+ecpUtils.checkNull(unique_id)+"</original_transaction_unique_id>" +
                        "</chargeback_request>";

        transactionLogger.error("processChargebackByUID request --------------" + request);
        try
        {
            String response="";
            if(isTest)
            {
                transactionLogger.error("CHARGEBACK_TEST_URL_BY_UID --------------" + RB.getString("CHARGEBACK_TEST_URL_BY_UID"));
                response = ecpUtils.doPostHTTPSURLConnectionClient(RB.getString("CHARGEBACK_TEST_URL_BY_UID"), request, "Basic", encodedCredentials);
            }
            else{
                transactionLogger.error("CHARGEBACK_LIVE_URL_BY_UID --------------" + RB.getString("CHARGEBACK_LIVE_URL_BY_UID"));
                response = ecpUtils.doPostHTTPSURLConnectionClient(RB.getString("CHARGEBACK_LIVE_URL_BY_UID"), request, "Basic", encodedCredentials);
            }
            transactionLogger.error("processChargebackByUID response --------------" + response);

            EcpResponseVo ecpResponseVo = ecpUtils.readChargebackResponse(response);
            if(ecpResponseVo !=null){
//                String trackingId = ecpUtils.getTrackingIdFromPaymetId(ecpResponseVo.getOriginalTransactionUniqueId());
//                ecpUtils.updateChargebackEntry(ecpResponseVo,trackingId);
            }
        }catch(Exception e){
            transactionLogger.error("Exception In ECPPaymentGateway ::::",e);
        }
    }
    public List<EcpResponseVo> processChargebackByDate(String fromDate,String toDate) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processChargebackByDate() ---");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        List<EcpResponseVo> responseList=new ArrayList<>();

        ECPUtils ecpUtils = new ECPUtils();
        String testUrl = RB.getString("CHARGEBACK_TEST_URL_BY_DATE");
        String liveUrl = RB.getString("CHARGEBACK_LIVE_URL_BY_DATE");

        boolean isTest = gatewayAccount.isTest();
        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<chargeback_request>"+
                        "<start_date>"+ecpUtils.checkNull(fromDate)+"</start_date>"+
                        "<end_date>"+ecpUtils.checkNull(toDate)+"</end_date>" +
                        "</chargeback_request>";

        transactionLogger.error("processChargebackByDate request --------------" + request);
        try
        {
            String response="";
            if(isTest)
            {
                transactionLogger.error("CHARGEBACK_TEST_URL--------------" + testUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, request, "Basic", encodedCredentials);
            }
            else{
                transactionLogger.error("CHARGEBACK_LIVE_URL--------------" + liveUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, request, "Basic", encodedCredentials);
            }
            transactionLogger.error("processChargebackByUID response --------------" + response);
           responseList = ecpUtils.readMultipleChargebackResponse(response);
        }catch(Exception e){
            transactionLogger.error("Exception -----------" + e);
        }
        return responseList;
    }
    public void processRetrievalRequestByUID(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        GenericResponseVO genericResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));

        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        ECPUtils ecpUtils = new ECPUtils();

        boolean isTest = gatewayAccount.isTest();

        String unique_id = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("unique_id --------------" + unique_id);

        String testUrl = RB.getString("RETRIEVAL_TEST_URL_BY_UID");
        String liveUrl = RB.getString("RETRIEVAL_LIVE_URL_BY_UID");

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<chargeback_request>" +
                        "<original_transaction_unique_id>" + ecpUtils.checkNull(unique_id) + "</original_transaction_unique_id>" +
                        "</chargeback_request>";

        transactionLogger.error("processRetrievalRequestByUID request -----" + request);
        try
        {
            String response = "";
            if (isTest)
            {
                transactionLogger.error("RETRIEVAL_TEST_URL_BY_UID --------" + testUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, request, "Basic", encodedCredentials);
            }
            else
            {
                transactionLogger.error("RETRIEVAL_LIVE_URL_BY_UID -------" + liveUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, request, "Basic", encodedCredentials);
            }
            transactionLogger.error("processRetrievalRequestByUID response ------" + response);
            EcpResponseVo ecpResponseVo = ecpUtils.readChargebackResponse(response);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception --------------" + e);
        }
    }

    public List<EcpResponseVo> processRetrievalByDate(String fromDate,String toDate) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processRetrievalByDate() ---");
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String userPassword = userName + ":" + password;
        String encodedCredentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        ECPUtils ecpUtils = new ECPUtils();
        List<EcpResponseVo> responseList =new  ArrayList();

        boolean isTest = gatewayAccount.isTest();
        String testUrl = RB.getString("RETRIEVAL_TEST_URL_BY_DATE");
        String liveUrl = RB.getString("RETRIEVAL_LIVE_URL_BY_DATE");

        String request =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<retrieval_request_request>" +
                        "<start_date>"+ecpUtils.checkNull(fromDate)+"</start_date>" +
                        "<end_date>"+ecpUtils.checkNull(toDate)+"</end_date>" +
                        "</retrieval_request_request>";

        transactionLogger.error("processChargebackByDate request -------------" + request);
        try
        {
            String response="";
            if(isTest)
            {
                transactionLogger.error("Test url  --------------" + testUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, request, "Basic", encodedCredentials);
            }
            else{
                transactionLogger.error("Live url  --------------" + liveUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, request, "Basic", encodedCredentials);
            }
            transactionLogger.error("processRetrievalByDate response --------------" + response);

             responseList = ecpUtils.readMultipleRetrievalResponse(response);
        }catch(Exception e){
            transactionLogger.error("Exception -----------" + e);
        }
        return responseList;
    }

    public List<EcpResponseVo> processCardPresentTransactionByDate(String fromDate,String toDate,String processingType)
    {
        transactionLogger.error("Inside processRetrievalByDate() ---");
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String userName                 = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password                 = gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionLogger.error("user name -----------: "+userName);
        String userPassword         = userName + ":" + password;
        String encodedCredentials   = new String(org.apache.commons.codec.binary.Base64.encodeBase64(userPassword.getBytes()));
        transactionLogger.error("encodedCredentials ------------: "+encodedCredentials);
        ECPUtils ecpUtils           = new ECPUtils();
        List<EcpResponseVo> list    = new  ArrayList();

        boolean isTest = gatewayAccount.isTest();
        String testUrl = RB.getString("CARD_PRESENT_TEST_URL");
        String liveUrl = RB.getString("CARD_PRESENT_LIVE_URL");

        String request = ""
                + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<processed_transaction_request>"
                + "<start_date>"+ecpUtils.checkNull(fromDate)+"</start_date>"
                + "<end_date>"+ecpUtils.checkNull(toDate)+"</end_date>"
                + "<processing_type>"+ecpUtils.checkNull(processingType)+"</processing_type>"
                + "</processed_transaction_request>";

        transactionLogger.error("request---------"+request);
        try
        {
            String response="";
            if(isTest)
            {
                transactionLogger.error("test url ------ "+testUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(testUrl, request, "Basic", encodedCredentials);
            }else{
                transactionLogger.error("test url ------ "+liveUrl);
                response = ecpUtils.doPostHTTPSURLConnectionClient(liveUrl, request, "Basic", encodedCredentials);
            }
            transactionLogger.error("card present response ------"+response);
            list = ecpUtils.readCardPresentResponse(response);
        }catch (Exception e){
            transactionLogger.error(e);
        }
        return list;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

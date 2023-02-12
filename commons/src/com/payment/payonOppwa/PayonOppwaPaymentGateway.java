package com.payment.payonOppwa;

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
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import com.payment.npayon.NPayOnResponseVO;
import com.payment.npayon.Parameter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Admin on 08-Jun-20.
 */
public class PayonOppwaPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "payonOppwa";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payon-oppwa");
    private static TransactionLogger transactionLogger = new TransactionLogger(PayonOppwaPaymentGateway.class.getName());

    public PayonOppwaPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

        @Override
        public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
        {
            transactionLogger.error("PayonOppwaPaymentGateway:: inside processSale()");
            String reqType = "DB";
            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
            PayonOppwaUtils payonOppwaUtils= new PayonOppwaUtils();
            Functions functions = new Functions();
            String recurringType = commRequestVO.getRecurringBillingVO().getRecurringType();
            CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            CommDeviceDetailsVO deviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();
            Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            NPayOnResponseVO responseVO = null;

          String termUrl = "";
            transactionLogger.error("host url----for "+trackingID+ "----" +commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from RB HOST_URL----for "+trackingID+ "----" +termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL");
                transactionLogger.error("from RB TERM_URL----for "+trackingID+ "----" +termUrl);
       }

            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();

            String merchantId = gatewayAccount.getMerchantId();
            transactionLogger.error("PayonOppwaPaymentGateway:: merchantId(entity id) ---"+merchantId);
            String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
            transactionLogger.debug("PayonOppwaPaymentGateway:: authorizationToken ---" + authorizationToken);

            String status = "";
            String remark = "";
            String descriptor = "";
            String response = "";
            String bankTransactionId = "";
            String time = "";
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

            String transactionCurrency = transDetailsVO.getCurrency();
            String transactionAmount = transDetailsVO.getAmount();
            String cardType =payonOppwaUtils.getPayonOppwaCardTypeName(cardDetailsVO.getCardType());

            String country="";
            String customerCity="";
            String customerZipCode="";
            String customerStreet="";
            String hostIPAddress="";
            String customerEmail="";
            String lastName="";
            String firstName="";
            String customerCvv="";
            String customerCardExpiryYear="";
            String customerCardExpiryMonth="";
            String customerCardNumber="";
            String customerState ="";
            String WalletId ="";
            String customer_birthDate ="";
            String is3dSupported= gatewayAccount.get_3DSupportAccount();
            String transactionCategory="EC";
            String customerbrowseracceptHeader="";
            String customerbrowserscreenColorDepth="";
            String customerbrowserjavaEnabled="";
            String customerbrowserlanguage="";
            String customerbrowserscreenHeight="";
            String customerbrowserscreenWidth="";
            String customerbrowsertimezone="";
            String customerbrowserchallengeWindow="";
            String customerbrowseruserAgent="";
            String testMode="EXTERNAL";

            if(functions.isValueNull(addressDetailsVO.getCountry())){
                country =addressDetailsVO.getCountry().substring(0,2).toUpperCase();
            }
            if(functions.isValueNull(addressDetailsVO.getCity())){
                customerCity =addressDetailsVO.getCity();
            }
            if(functions.isValueNull(addressDetailsVO.getZipCode())){
                customerZipCode =addressDetailsVO.getZipCode();
            }
            if(functions.isValueNull(addressDetailsVO.getState())){
                customerState =addressDetailsVO.getState();
            }
            if(functions.isValueNull(addressDetailsVO.getStreet())){
                customerStreet =addressDetailsVO.getStreet();
            }
            if(functions.isValueNull(addressDetailsVO.getCardHolderIpAddress())){
                hostIPAddress =addressDetailsVO.getCardHolderIpAddress();
            }
            if(functions.isValueNull(addressDetailsVO.getEmail())){
                customerEmail =addressDetailsVO.getEmail();
            }
            if(functions.isValueNull(addressDetailsVO.getLastname())){
                lastName =addressDetailsVO.getLastname();
            }
            if(functions.isValueNull(addressDetailsVO.getFirstname())){
                firstName =addressDetailsVO.getFirstname();
            }
            if(functions.isValueNull(cardDetailsVO.getcVV())){
                customerCvv =cardDetailsVO.getcVV();
            }
            if(functions.isValueNull(cardDetailsVO.getExpYear())){
                customerCardExpiryYear =cardDetailsVO.getExpYear();
            }
            if(functions.isValueNull(cardDetailsVO.getExpMonth())){
                customerCardExpiryMonth =cardDetailsVO.getExpMonth();
            }
            if(functions.isValueNull(cardDetailsVO.getCardNum())){
                customerCardNumber =cardDetailsVO.getCardNum();
            }
            transactionLogger.error("addressDetailsVO.getBirthdate--------------------------"+addressDetailsVO.getBirthdate());
            if(functions.isValueNull(addressDetailsVO.getBirthdate()))
            {

                SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy-MM-dd");
                try
                {
                    if (!addressDetailsVO.getBirthdate().contains("-"))
                    {
                        customer_birthDate = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));

                    }
                    else
                    {
                        customer_birthDate=addressDetailsVO.getBirthdate();
                    }
                }catch (ParseException e)
                {
                    customer_birthDate=addressDetailsVO.getBirthdate();

                    transactionLogger.error("Parse Exception-->",e);
                }
            }

            if(functions.isValueNull(addressDetailsVO.getCustomerid()))
            {
                WalletId =addressDetailsVO.getCustomerid();
            }
            transactionLogger.error("customer_birthDate---------------------"+customer_birthDate);
            transactionLogger.error("WalletId---------------------------"+WalletId);
          if(functions.isValueNull(deviceDetailsVO.getFingerprints()))
            {
                transactionLogger.error("Inside Fingerprints if condition---------------------------");
                HashMap fingerPrintMap = functions.getFingerPrintMap(deviceDetailsVO.getFingerprints());
                org.codehaus.jettison.json.JSONArray screenResolution = new org.codehaus.jettison.json.JSONArray();
                try
                {
                    if (fingerPrintMap.containsKey("userAgent"))
                        customerbrowseruserAgent = (String) fingerPrintMap.get("userAgent");
                    if (fingerPrintMap.containsKey("language"))
                        customerbrowserlanguage = (String) fingerPrintMap.get("language");
                    if (fingerPrintMap.containsKey("colorDepth"))
                        customerbrowserscreenColorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                    if (fingerPrintMap.containsKey("timezoneOffset"))
                        customerbrowsertimezone = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                    if (fingerPrintMap.containsKey("screenResolution"))
                        screenResolution = (org.codehaus.jettison.json.JSONArray) fingerPrintMap.get("screenResolution");
                    if (screenResolution.length() > 0)
                        customerbrowserscreenHeight= String.valueOf(screenResolution.getString(0));
                    if (screenResolution.length() > 1)
                        customerbrowserscreenWidth = String.valueOf(screenResolution.getString(1));
                          customerbrowserjavaEnabled="TRUE";
                    transactionLogger.error("screenResolution --------"+ screenResolution);

                }
                catch (org.codehaus.jettison.json.JSONException e)
                {
                    transactionLogger.error("JSONException---trackingId--" + trackingID + "--", e);
                }
            }else
            {
                transactionLogger.error("Inside Fingerprints else condition---------------------------");
                if(functions.isValueNull(deviceDetailsVO.getUser_Agent()))
                    customerbrowseruserAgent=deviceDetailsVO.getUser_Agent();
                else
                    customerbrowseruserAgent="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
                if(functions.isValueNull(deviceDetailsVO.getBrowserLanguage()))
                    customerbrowserlanguage=deviceDetailsVO.getBrowserLanguage();
                else
                    customerbrowserlanguage="en-US";
                if(functions.isValueNull(deviceDetailsVO.getBrowserColorDepth()))
                    customerbrowserscreenColorDepth=deviceDetailsVO.getBrowserColorDepth();
                else
                    customerbrowserscreenColorDepth="24";
                if(functions.isValueNull(deviceDetailsVO.getBrowserTimezoneOffset()))
                    customerbrowsertimezone=deviceDetailsVO.getBrowserTimezoneOffset();
                else
                    customerbrowsertimezone="5";
                if(functions.isValueNull(deviceDetailsVO.getBrowserScreenHeight()))
                    customerbrowserscreenHeight=deviceDetailsVO.getBrowserScreenHeight();
                else
                    customerbrowserscreenHeight="939";
                if(functions.isValueNull(deviceDetailsVO.getBrowserScreenWidth()))
                    customerbrowserscreenWidth=deviceDetailsVO.getBrowserScreenWidth();
                else
                    customerbrowserscreenWidth="1255";
                if(functions.isValueNull(deviceDetailsVO.getBrowserJavaEnabled()))
                    customerbrowserjavaEnabled=deviceDetailsVO.getBrowserJavaEnabled();
                else
                    customerbrowserjavaEnabled="TRUE";
           }
            try
            {
                if(is3dSupported.equalsIgnoreCase("Y")||is3dSupported.equalsIgnoreCase("O"))
                {
                    transactionLogger.error("Inside 3D condition");
                    StringBuffer sale3DRequest = new StringBuffer();
                    sale3DRequest.append( "entityId=" + merchantId
                            + "&amount=" + transactionAmount
                            + "&currency=" + transactionCurrency
                            + "&paymentBrand=" + cardType
                            + "&paymentType=" + reqType
                            + "&merchantTransactionId=" + trackingID
                            + "&transactionCategory=" + transactionCategory
                            + "&card.number=" + customerCardNumber
                            + "&card.holder=" + URLEncoder.encode(firstName,"UTF-8")+" "+URLEncoder.encode(lastName,"UTF-8")
                            + "&card.expiryMonth=" + customerCardExpiryMonth
                            + "&card.expiryYear=" + customerCardExpiryYear
                            + "&card.cvv=" + customerCvv
                            + "&shopperResultUrl="+ URLEncoder.encode(termUrl + trackingID, "UTF-8")
                            + "&customer.ip="+hostIPAddress
                            + "&customer.browser.acceptHeader="+customerbrowseracceptHeader
                            + "&customer.browser.screenColorDepth="+customerbrowserscreenColorDepth
                            + "&customer.browser.javaEnabled="+customerbrowserjavaEnabled
                            + "&customer.browser.language="+customerbrowserlanguage
                            +"&customer.browser.screenHeight="+customerbrowserscreenHeight
                            +"&customer.browser.screenWidth="+customerbrowserscreenWidth
                            +"&customer.browser.timezone="+customerbrowsertimezone
                            + "&customer.browser.challengeWindow="+customerbrowserchallengeWindow
                            +"&customer.browser.userAgent="+customerbrowseruserAgent
                           /* +"&testMode="+testMode*/);

                    StringBuffer sale3DRequestlog = new StringBuffer();
                    sale3DRequestlog.append( "entityId=" + merchantId
                            + "&amount=" + transactionAmount
                            + "&currency=" + transactionCurrency
                            + "&paymentBrand=" + cardType
                            + "&paymentType=" + reqType
                            + "&merchantTransactionId=" + trackingID
                            + "&transactionCategory=" + transactionCategory
                            + "&card.number=" + functions.maskingPan(customerCardNumber)
                            + "&card.holder=" + URLEncoder.encode(firstName,"UTF-8")+" "+URLEncoder.encode(lastName,"UTF-8")
                            + "&card.expiryMonth=" + functions.maskingNumber(customerCardExpiryMonth)
                            + "&card.expiryYear=" + functions.maskingNumber(customerCardExpiryYear)
                            + "&card.cvv=" + functions.maskingNumber(customerCvv)
                            + "&shopperResultUrl="+ URLEncoder.encode(termUrl + trackingID, "UTF-8")
                            + "&customer.ip="+hostIPAddress
                            + "&customer.browser.acceptHeader="+customerbrowseracceptHeader
                            + "&customer.browser.screenColorDepth="+customerbrowserscreenColorDepth
                            + "&customer.browser.javaEnabled="+customerbrowserjavaEnabled
                            + "&customer.browser.language="+customerbrowserlanguage
                            +"&customer.browser.screenHeight="+customerbrowserscreenHeight
                            +"&customer.browser.screenWidth="+customerbrowserscreenWidth
                            +"&customer.browser.timezone="+customerbrowsertimezone
                            + "&customer.browser.challengeWindow="+customerbrowserchallengeWindow
                            +"&customer.browser.userAgent="+customerbrowseruserAgent
                          /*  +"&testMode="+testMode*/);
                    if ("INITIAL".equals(recurringType) || "Manual".equalsIgnoreCase(recurringType))
                    {
                        sale3DRequest.append("&recurringType=INITIAL&createRegistration=true");
                    }


                    transactionLogger.error("recurringType----->" + recurringType);
                    transactionLogger.error("PayonOppwaPaymentGateway:: SaleRequest --- for " + trackingID + "----" + sale3DRequestlog);

                    if (isTest)
                    {
                        transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                        response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), authorizationToken, sale3DRequest.toString());
                    }
                    else
                    {
                        transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                        response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), authorizationToken, sale3DRequest.toString());
                    }

                    transactionLogger.error("PayonOppwaPaymentGateway:: sale response ---for " + trackingID + "----" + response);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);

                }else
                {
                    transactionLogger.error("Inside non-3D condition");
                    StringBuffer saleRequest = new StringBuffer();
                    saleRequest.append("authentication.entityId=" + merchantId
                            + "&amount=" + transactionAmount
                            + "&currency=" + transactionCurrency
                            + "&paymentBrand=" + cardType
                            + "&paymentType=" + reqType
                            + "&merchantTransactionId=" + trackingID
                            + "&card.number=" + customerCardNumber
                            + "&card.holder=" + URLEncoder.encode(firstName, "UTF-8") + " " + URLEncoder.encode(lastName, "UTF-8")
                            + "&card.expiryMonth=" + customerCardExpiryMonth
                            + "&card.expiryYear=" + customerCardExpiryYear
                            + "&card.cvv=" + customerCvv
                            + "&customer.givenName=" + URLEncoder.encode(firstName, "UTF-8")
                            + "&customer.surname=" + URLEncoder.encode(lastName, "UTF-8")
                            + "&customer.email=" + customerEmail
                            + "&customer.ip=" + hostIPAddress
                            + "&billing.city=" + URLEncoder.encode(customerCity, "UTF-8")
                            + "&billing.street1=" + URLEncoder.encode(customerStreet, "UTF-8")
                            + "&billing.state=" + URLEncoder.encode(customerState, "UTF-8")
                            + "&billing.postcode=" + customerZipCode
                            + "&billing.country=" + country
                            + "&customer.birthDate=" + customer_birthDate
                            + "&customParameters[WalletId]=" + WalletId
                            + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8"));

                    StringBuffer saleRequestLog = new StringBuffer();
                    saleRequestLog.append("authentication.entityId=" + merchantId
                            + "&amount=" + transactionAmount
                            + "&currency=" + transactionCurrency
                            + "&paymentBrand=" + cardType
                            + "&paymentType=" + reqType
                            + "&merchantTransactionId=" + trackingID
                            + "&card.number=" + functions.maskingPan(customerCardNumber)
                            + "&card.holder=" + URLEncoder.encode(firstName, "UTF-8") + " " + URLEncoder.encode(lastName, "UTF-8")
                            + "&card.expiryMonth=" + functions.maskingNumber(customerCardExpiryMonth)
                            + "&card.expiryYear=" + functions.maskingNumber(customerCardExpiryYear)
                            + "&card.cvv=" + functions.maskingNumber(customerCvv)
                            + "&customer.givenName=" + URLEncoder.encode(firstName, "UTF-8")
                            + "&customer.surname=" + URLEncoder.encode(lastName, "UTF-8")
                            + "&customer.email=" + customerEmail
                            + "&customer.ip=" + hostIPAddress
                            + "&billing.city=" + URLEncoder.encode(customerCity, "UTF-8")
                            + "&billing.street1=" + URLEncoder.encode(customerStreet, "UTF-8")
                            + "&billing.state=" + URLEncoder.encode(customerState, "UTF-8")
                            + "&billing.postcode=" + customerZipCode
                            + "&billing.country=" + country
                            + "&customer.birthDate=" + customer_birthDate
                            + "&customParameters[WalletId]=" + WalletId
                            + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8"));

                    if ("INITIAL".equals(recurringType) || "Manual".equalsIgnoreCase(recurringType))
                    {
                        saleRequest.append("&recurringType=INITIAL&createRegistration=true");
                    }


                    transactionLogger.error("recurringType----->" + recurringType);
                    transactionLogger.error("PayonOppwaPaymentGateway:: SaleRequest --- for " + trackingID + "----" + saleRequestLog);

                    if (isTest)
                    {
                        transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                        response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), authorizationToken, saleRequest.toString());
                    }
                    else
                    {
                        transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                        response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), authorizationToken, saleRequest.toString());
                    }

                    transactionLogger.error("PayonOppwaPaymentGateway:: sale response ---for " + trackingID + "----" + response);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);

                }

                transactionLogger.error("TransactionType ->"+responseVO.getTransactionType());
                if (responseVO != null)
                {
                    transactionLogger.error("code:" + responseVO.getResult().getCode());
                    if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                    {
                        status = "success";
                        remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                        descriptor = gatewayAccount.getDisplayName();
                        if(gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                        {
                            descriptor = responseVO.getDescriptor();
                        }else
                        {
                            descriptor = gatewayAccount.getDisplayName();
                        }
                    }
                    else if ("000.200.000".equalsIgnoreCase(responseVO.getResult().getCode()))
                    {
                        transactionLogger.error("inside 3d");
                        transactionLogger.error("Code----->"+responseVO.getResult().getCode());

                        String MD = "";
                        String URL = "";
                        String PaReq = "";
                        String TermUrl = "";
                        String connector = "";
                        String Creq = "";
                        String names = "";
                        String ThreeDSSessionData = "";
                        String threeDSMethodData="";





                        status = "pending3DConfirmation";

                        URL = responseVO.getRedirect().getUrl();

                        List<Parameter> parameters = responseVO.getRedirect().getParameters();
                        for (Parameter parameter : parameters)
                        {
                            String name = parameter.getName();
                            String value = parameter.getValue();

                            if ("PaReq".equals(name))
                            {
                                PaReq = value;//name & value made equal
                            }
                            else if ("MD".equals(name))
                            {
                                MD = value;
                            }
                            else if ("TermUrl".equals(name))
                            {
                                TermUrl = value;
                            }
                            else if ("connector".equals(name))
                            {
                                connector = value;
                            }
                            else if("creq".equals(name))
                            {
                                Creq=value;
                            }
                            else if("threeDSSessionData".equals(name))
                            {
                                ThreeDSSessionData=value;
                            }
                            else if("threeDSMethodData".equals(name))
                            {
                                threeDSMethodData=value;
                            }

                        }
                        transactionLogger.error("URL -------->"+URL);
                        transactionLogger.error("Creq value-------->"+Creq);
                        transactionLogger.debug("TermUrl------"+TermUrl);


                        comm3DResponseVO.setPaReq(PaReq);
                        comm3DResponseVO.setUrlFor3DRedirect(URL);
                        comm3DResponseVO.setMd(MD);
                        comm3DResponseVO.setConnector(connector);
                        comm3DResponseVO.setRedirectMethod("POST");
                        comm3DResponseVO.setTerURL(TermUrl);
                        comm3DResponseVO.setStatus(status);
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setTransactionType("Sale");
                        comm3DResponseVO.setTransactionId(responseVO.getId());
                        comm3DResponseVO.setDescription(responseVO.getResult().getDescription().replaceAll("'", " "));//from contains of rsult this are replaced ("'", " ")
                        comm3DResponseVO.setRemark(responseVO.getResult().getDescription().replaceAll("'", " "));
                        comm3DResponseVO.setCreq(Creq);
                        comm3DResponseVO.setThreeDSSessionData(ThreeDSSessionData);
                        comm3DResponseVO.setThreeDSMethodData(threeDSMethodData);
                        if(response.contains("3D Secure 2.0")||response.contains("creq"))
                        {
                            comm3DResponseVO.setThreeDVersion("3Dv2");
                        }


                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);

                        transactionLogger.error("redirecting for 3d");
                        return comm3DResponseVO;
                    }
                    else
                    {
                        status = "failed";
                        remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    }
                    bankTransactionId = responseVO.getId();
                    time = responseVO.getTimestamp();
                }
                commResponseVO.setMerchantOrderId(trackingID);
                commResponseVO.setTransactionId(bankTransactionId);
                commResponseVO.setStatus(status);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setDescription(remark);
                commResponseVO.setRemark(remark);
                commResponseVO.setResponseTime(time);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                //recurringBillingVO.setRecurringType(recurringType);
                commResponseVO.setResponseHashInfo(responseVO.getRegistrationId());


                transactionLogger.error("TransactionId ->"+responseVO.getId());            //paymentid
                transactionLogger.error("TransactionType ->"+responseVO.getTransactionType());
                transactionLogger.error("ResponseHashInfo registrationId->"+responseVO.getRegistrationId()); //Registrationid from response
            }
            catch (IOException e1)
            {
               transactionLogger.error("IOException---"+trackingID+"----------",e1);
            }
                return commResponseVO;
            }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("PayonOppwaPaymentGateway:: inside processAuthentication()");
        String reqType = "PA";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();
        Functions functions = new Functions();
        String recurringType = commRequestVO.getRecurringBillingVO().getRecurringType();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommDeviceDetailsVO deviceDetailsVO = commRequestVO.getCommDeviceDetailsVO();
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        NPayOnResponseVO responseVO=null;

        String termUrl = "";
        transactionLogger.error("host url----for " + trackingID + "----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from RB HOST_URL----for " + trackingID + "----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB TERM_URL----for " + trackingID + "----" + termUrl);
        }

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        transactionLogger.error("PayonOppwaPaymentGateway:: merchantId(entity id) ---" + merchantId);
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionLogger.debug("PayonOppwaPaymentGateway:: authorizationToken ---" + authorizationToken);

        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String bankTransactionId = "";
        String time = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        String cardType = payonOppwaUtils.getPayonOppwaCardTypeName(cardDetailsVO.getCardType());

        String country = "";
        String customerCity = "";
        String customerZipCode = "";
        String customerStreet = "";
        String hostIPAddress = "";
        String customerEmail = "";
        String lastName = "";
        String firstName = "";
        String customerCvv = "";
        String customerCardExpiryYear = "";
        String customerCardExpiryMonth = "";
        String customerCardNumber = "";
        String customerState = "";
        String WalletId = "";
        String customer_birthDate = "";

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String transactionCategory = "EC";
        String customerbrowseracceptHeader = "";
        String customerbrowserscreenColorDepth = "";
        String customerbrowserjavaEnabled = "";
        String customerbrowserlanguage = "";
        String customerbrowserscreenHeight = "";
        String customerbrowserscreenWidth = "";
        String customerbrowsertimezone = "";
        String customerbrowserchallengeWindow = "";
        String customerbrowseruserAgent = "";
        String testMode = "EXTERNAL";

        if (functions.isValueNull(addressDetailsVO.getCountry()))
        {
            country = addressDetailsVO.getCountry().substring(0, 2).toUpperCase();
        }
        if (functions.isValueNull(addressDetailsVO.getCity()))
        {
            customerCity = addressDetailsVO.getCity();
        }
        if (functions.isValueNull(addressDetailsVO.getZipCode()))
        {
            customerZipCode = addressDetailsVO.getZipCode();
        }
        if (functions.isValueNull(addressDetailsVO.getState()))
        {
            customerState = addressDetailsVO.getState();
        }
        if (functions.isValueNull(addressDetailsVO.getStreet()))
        {
            customerStreet = addressDetailsVO.getStreet();
        }
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
        {
            hostIPAddress = addressDetailsVO.getCardHolderIpAddress();
        }
        if (functions.isValueNull(addressDetailsVO.getEmail()))
        {
            customerEmail = addressDetailsVO.getEmail();
        }
        if (functions.isValueNull(addressDetailsVO.getLastname()))
        {
            lastName = addressDetailsVO.getLastname();
        }
        if (functions.isValueNull(addressDetailsVO.getFirstname()))
        {
            firstName = addressDetailsVO.getFirstname();
        }
        if (functions.isValueNull(cardDetailsVO.getcVV()))
        {
            customerCvv = cardDetailsVO.getcVV();
        }
        if (functions.isValueNull(cardDetailsVO.getExpYear()))
        {
            customerCardExpiryYear = cardDetailsVO.getExpYear();
        }
        if (functions.isValueNull(cardDetailsVO.getExpMonth()))
        {
            customerCardExpiryMonth = cardDetailsVO.getExpMonth();
        }
        if (functions.isValueNull(cardDetailsVO.getCardNum()))
        {
            customerCardNumber = cardDetailsVO.getCardNum();
        }
        transactionLogger.error("addressDetailsVO.getBirthdate--------------------------" + addressDetailsVO.getBirthdate());
        if (functions.isValueNull(addressDetailsVO.getBirthdate()))
        {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                if (!addressDetailsVO.getBirthdate().contains("-"))
                {
                    customer_birthDate = dateFormat2.format(dateFormat1.parse(addressDetailsVO.getBirthdate()));

                }
                else
                {
                    customer_birthDate = addressDetailsVO.getBirthdate();
                }
            }
            catch (ParseException e)
            {
                customer_birthDate = addressDetailsVO.getBirthdate();

                transactionLogger.error("Parse Exception-->"+ trackingID + "----" ,  e);
            }
        }

        if (functions.isValueNull(addressDetailsVO.getCustomerid()))
        {
            WalletId = addressDetailsVO.getCustomerid();
        }
        transactionLogger.error("customer_birthDate---------------------" + customer_birthDate);
        transactionLogger.error("WalletId---------------------------" + WalletId);
        if (functions.isValueNull(deviceDetailsVO.getFingerprints()))
        {
            transactionLogger.error("Inside Fingerprints if condition---------------------------");
            HashMap fingerPrintMap = functions.getFingerPrintMap(deviceDetailsVO.getFingerprints());
            org.codehaus.jettison.json.JSONArray screenResolution = new org.codehaus.jettison.json.JSONArray();
            try
            {
                if (fingerPrintMap.containsKey("userAgent"))
                    customerbrowseruserAgent = (String) fingerPrintMap.get("userAgent");
                if (fingerPrintMap.containsKey("language"))
                    customerbrowserlanguage = (String) fingerPrintMap.get("language");
                if (fingerPrintMap.containsKey("colorDepth"))
                    customerbrowserscreenColorDepth = String.valueOf(fingerPrintMap.get("colorDepth"));
                if (fingerPrintMap.containsKey("timezoneOffset"))
                    customerbrowsertimezone = String.valueOf(fingerPrintMap.get("timezoneOffset"));
                if (fingerPrintMap.containsKey("screenResolution"))
                    screenResolution = (org.codehaus.jettison.json.JSONArray) fingerPrintMap.get("screenResolution");
                if (screenResolution.length() > 0)
                    customerbrowserscreenHeight = String.valueOf(screenResolution.getString(0));
                if (screenResolution.length() > 1)
                    customerbrowserscreenWidth = String.valueOf(screenResolution.getString(1));
                customerbrowserjavaEnabled = "TRUE";
                transactionLogger.error("screenResolution --------" + screenResolution);

            }
            catch (org.codehaus.jettison.json.JSONException e)
            {
                transactionLogger.error("JSONException---trackingId--" + trackingID + "--", e);
            }
        }
        else
        {
            transactionLogger.error("Inside Fingerprints else condition---------------------------");
            if (functions.isValueNull(deviceDetailsVO.getUser_Agent()))
                customerbrowseruserAgent = deviceDetailsVO.getUser_Agent();
            else
                customerbrowseruserAgent = "Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0";
            if (functions.isValueNull(deviceDetailsVO.getBrowserLanguage()))
                customerbrowserlanguage = deviceDetailsVO.getBrowserLanguage();
            else
                customerbrowserlanguage = "en-US";
            if (functions.isValueNull(deviceDetailsVO.getBrowserColorDepth()))
                customerbrowserscreenColorDepth = deviceDetailsVO.getBrowserColorDepth();
            else
                customerbrowserscreenColorDepth = "24";
            if (functions.isValueNull(deviceDetailsVO.getBrowserTimezoneOffset()))
                customerbrowsertimezone = deviceDetailsVO.getBrowserTimezoneOffset();
            else
                customerbrowsertimezone = "5";
            if (functions.isValueNull(deviceDetailsVO.getBrowserScreenHeight()))
                customerbrowserscreenHeight = deviceDetailsVO.getBrowserScreenHeight();
            else
                customerbrowserscreenHeight = "939";
            if (functions.isValueNull(deviceDetailsVO.getBrowserScreenWidth()))
                customerbrowserscreenWidth = deviceDetailsVO.getBrowserScreenWidth();
            else
                customerbrowserscreenWidth = "1255";
            if (functions.isValueNull(deviceDetailsVO.getBrowserJavaEnabled()))
                customerbrowserjavaEnabled = deviceDetailsVO.getBrowserJavaEnabled();
            else
                customerbrowserjavaEnabled = "TRUE";
        }
        try
        {
            if (is3dSupported.equalsIgnoreCase("Y") || is3dSupported.equalsIgnoreCase("O"))
            {
                transactionLogger.error("Inside 3D condition");
                StringBuffer auth3DRequest = new StringBuffer();
                auth3DRequest.append("entityId=" + merchantId
                                + "&amount=" + transactionAmount
                                + "&currency=" + transactionCurrency
                                + "&paymentBrand=" + cardType
                                + "&paymentType=" + reqType
                                + "&merchantTransactionId=" + trackingID
                                + "&transactionCategory=" + transactionCategory
                                + "&card.number=" + customerCardNumber
                                + "&card.holder=" + URLEncoder.encode(firstName, "UTF-8") + " " + URLEncoder.encode(lastName, "UTF-8")
                                + "&card.expiryMonth=" + customerCardExpiryMonth
                                + "&card.expiryYear=" + customerCardExpiryYear
                                + "&card.cvv=" + customerCvv
                                + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8")
                                + "&customer.ip=" + hostIPAddress
                                + "&customer.browser.acceptHeader=" + customerbrowseracceptHeader
                                + "&customer.browser.screenColorDepth=" + customerbrowserscreenColorDepth
                                + "&customer.browser.javaEnabled=" + customerbrowserjavaEnabled
                                + "&customer.browser.language=" + customerbrowserlanguage
                                + "&customer.browser.screenHeight=" + customerbrowserscreenHeight
                                + "&customer.browser.screenWidth=" + customerbrowserscreenWidth
                                + "&customer.browser.timezone=" + customerbrowsertimezone
                                + "&customer.browser.challengeWindow=" + customerbrowserchallengeWindow
                                + "&customer.browser.userAgent=" + customerbrowseruserAgent
                                + "&customer.givenName=" + URLEncoder.encode(firstName, "UTF-8")
                                + "&customer.surname=" + URLEncoder.encode(lastName, "UTF-8")
                                + "&customer.email=" + URLEncoder.encode(customerEmail, "UTF-8")
                                + "&billing.city=" + URLEncoder.encode(customerCity, "UTF-8")
                                + "&billing.street1=" + URLEncoder.encode(customerStreet, "UTF-8")
                                + "&billing.state=" + URLEncoder.encode(customerState, "UTF-8")
                                + "&billing.postcode=" + customerZipCode
                                + "&billing.country=" + country
                                + "&customer.birthDate=" + customer_birthDate
                );

                StringBuffer auth3DRequestlog = new StringBuffer();
                auth3DRequestlog.append("entityId=" + merchantId
                        + "&amount=" + transactionAmount
                        + "&currency=" + transactionCurrency
                        + "&paymentBrand=" + cardType
                        + "&paymentType=" + reqType
                        + "&merchantTransactionId=" + trackingID
                        + "&transactionCategory=" + transactionCategory
                        + "&card.number=" + functions.maskingPan(customerCardNumber)
                        + "&card.holder=" + URLEncoder.encode(firstName, "UTF-8") + " " + URLEncoder.encode(lastName, "UTF-8")
                        + "&card.expiryMonth=" + functions.maskingNumber(customerCardExpiryMonth)
                        + "&card.expiryYear=" + functions.maskingNumber(customerCardExpiryYear)
                        + "&card.cvv=" + functions.maskingNumber(customerCvv)
                        + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8")
                        + "&customer.ip=" + hostIPAddress
                        + "&customer.browser.acceptHeader=" + customerbrowseracceptHeader
                        + "&customer.browser.screenColorDepth=" + customerbrowserscreenColorDepth
                        + "&customer.browser.javaEnabled=" + customerbrowserjavaEnabled
                        + "&customer.browser.language=" + customerbrowserlanguage
                        + "&customer.browser.screenHeight=" + customerbrowserscreenHeight
                        + "&customer.browser.screenWidth=" + customerbrowserscreenWidth
                        + "&customer.browser.timezone=" + customerbrowsertimezone
                        + "&customer.browser.challengeWindow=" + customerbrowserchallengeWindow
                        + "&customer.browser.userAgent=" + customerbrowseruserAgent
                        + "&customer.givenName=" + URLEncoder.encode(firstName, "UTF-8")
                        + "&customer.surname=" + URLEncoder.encode(lastName, "UTF-8")
                        + "&customer.email=" + URLEncoder.encode(customerEmail, "UTF-8")
                        + "&billing.city=" + URLEncoder.encode(customerCity, "UTF-8")
                        + "&billing.street1=" + URLEncoder.encode(customerStreet, "UTF-8")
                        + "&billing.state=" + URLEncoder.encode(customerState, "UTF-8")
                        + "&billing.postcode=" + customerZipCode
                        + "&billing.country=" + country
                        + "&customer.birthDate=" + customer_birthDate

                        /*+ "&testMode=" + testMode*/);
                if ("INITIAL".equals(recurringType) || "Manual".equalsIgnoreCase(recurringType))
                {
                    auth3DRequest.append("&recurringType=INITIAL&createRegistration=true");
                }
                transactionLogger.error("recurringType----->" + recurringType);

                if (isTest)
                {
                  /*  auth3DRequest.append("&testMode=" + testMode*/
                    transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), authorizationToken, auth3DRequest.toString());
                }
                else
                {
                    transactionLogger.error("inside isLive-----" + RB.getString("LIVE_URL"));
                    response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), authorizationToken, auth3DRequest.toString());
                }
                transactionLogger.error("PayonOppwaPaymentGateway:: Auth request--- processAuthentication --- for " + trackingID + "----" + auth3DRequestlog);

                transactionLogger.error("PayonOppwaPaymentGateway:: Auth response--- processAuthentication---for " + trackingID + "----" + response);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            }
            if (responseVO != null)
            {
                transactionLogger.error("code:" + responseVO.getResult().getCode());
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    if(gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                    {
                        descriptor = responseVO.getDescriptor();
                    }else
                    {
                        descriptor = gatewayAccount.getDisplayName();
                    }

                }
                else if ("000.200.000".equalsIgnoreCase(responseVO.getResult().getCode()))
                {
                    transactionLogger.error("inside 3d");
                    transactionLogger.error("Code----->" + responseVO.getResult().getCode());

                    String MD = "";
                    String URL = "";
                    String PaReq = "";
                    String TermUrl = "";
                    String connector = "";
                    String Creq = "";
                    String names = "";
                    String ThreeDSSessionData = "";
                    String threeDSMethodData = "";


                    status = "pending3DConfirmation";

                    URL = responseVO.getRedirect().getUrl();

                    List<Parameter> parameters = responseVO.getRedirect().getParameters();
                    for (Parameter parameter : parameters)
                    {
                        String name = parameter.getName();
                        String value = parameter.getValue();

                        if ("PaReq".equals(name))
                        {
                            PaReq = value;//name & value made equal
                        }
                        else if ("MD".equals(name))
                        {
                            MD = value;
                        }
                        else if ("TermUrl".equals(name))
                        {
                            TermUrl = value;
                        }
                        else if ("connector".equals(name))
                        {
                            connector = value;
                        }
                        else if ("creq".equals(name))
                        {
                            Creq = value;
                        }
                        else if ("threeDSSessionData".equals(name))
                        {
                            ThreeDSSessionData = value;
                        }
                        else if ("threeDSMethodData".equals(name))
                        {
                            threeDSMethodData = value;
                        }

                    }
                    transactionLogger.error("URL -------->" + URL);
                    transactionLogger.error("Creq value-------->" + Creq);
                    transactionLogger.debug("TermUrl------" + TermUrl);


                    comm3DResponseVO.setPaReq(PaReq);
                    comm3DResponseVO.setUrlFor3DRedirect(URL);
                    comm3DResponseVO.setMd(MD);
                    comm3DResponseVO.setConnector(connector);
                    comm3DResponseVO.setRedirectMethod("POST");
                    comm3DResponseVO.setTerURL(TermUrl);
                    comm3DResponseVO.setStatus(status);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setTransactionType("Auth");
                    comm3DResponseVO.setTransactionId(responseVO.getId());
                    comm3DResponseVO.setDescription(responseVO.getResult().getDescription().replaceAll("'", " "));//from contains of rsult this are replaced ("'", " ")
                    comm3DResponseVO.setRemark(responseVO.getResult().getDescription().replaceAll("'", " "));
                    comm3DResponseVO.setCreq(Creq);
                    comm3DResponseVO.setThreeDSSessionData(ThreeDSSessionData);
                    comm3DResponseVO.setThreeDSMethodData(threeDSMethodData);
                    if (response.contains("3D Secure 2.0")||response.contains("creq"))
                    {
                        comm3DResponseVO.setThreeDVersion("3Dv2");
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);

                    transactionLogger.error("redirecting for 3d");
                    return comm3DResponseVO;
                }
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
                bankTransactionId = responseVO.getId();
                time = responseVO.getTimestamp();
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setTransactionId(bankTransactionId);
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(time);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setResponseHashInfo(responseVO.getRegistrationId());
            transactionLogger.error("TransactionId ->" + responseVO.getId());            //paymentid
            transactionLogger.error("TransactionType ->" + responseVO.getTransactionType());
            transactionLogger.error("ResponseHashInfo registrationId->" + responseVO.getRegistrationId()); //Registrationid from response
        }

        catch (IOException e1)
        {
            transactionLogger.error("IOException---" +trackingID + "----" ,  e1);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("PayonOppwaPaymentGateway:: inside processCapture()");
        String reqType = "CP";
        String time="";
        NPayOnResponseVO responseVO = null;
        PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String testMode = "EXTERNAL";
        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }
        try
        {
            StringBuffer request =new StringBuffer(
                    "entityId=" + merchantId +
                            "&amount=" + commTransactionDetailsVO.getAmount() +
                            "&currency=" + commTransactionDetailsVO.getCurrency() +
                            "&paymentType=" + reqType
            );


            if (isTest)
            {
                //request.append("&testMode="+testMode);
                transactionLogger.error("PayonOppwaPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("TEST_URL")+"/"+  previousTransactionId);
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+"/"+  previousTransactionId, authorizationToken, request.toString());
            }
            else
            {
                transactionLogger.error("PayonOppwaPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("LIVE_URL")+"/"+  previousTransactionId);
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+"/"+ previousTransactionId, authorizationToken, request.toString());
            }
            transactionLogger.error("PayonOppwaPaymentGateway :: Capture request-----for "+trackingID+ "----" + request);

            transactionLogger.error("PayonOppwaPaymentGateway :: Capture response-----for "+trackingID+ "----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            if (responseVO != null)
            {
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    if(gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                    {
                        descriptor = responseVO.getDescriptor();
                    }else
                    {
                        descriptor = gatewayAccount.getDisplayName();
                    }

                }
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setStatus(status);
            time = responseVO.getTimestamp();
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setTransactionId(previousTransactionId);
            commResponseVO.setResponseTime(time);

        }
        catch (JsonMappingException e)
        {
            transactionLogger.error("JsonMappingException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capture transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            transactionLogger.error("JsonParseException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capture transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capture transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("PayonOppwaPaymentGateway:: inside processVoid()");
        String reqType = "RV";
        String time="";
        NPayOnResponseVO responseVO = null;
        PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions=new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String merchantId = gatewayAccount.getMerchantId();
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String testMode = "EXTERNAL";
        String status = "";
        String remark = "";
        String descriptor = "";
        String response = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }
        try
        {
            StringBuffer request =new StringBuffer(
                    "entityId=" + merchantId +
                            "&paymentType=" + reqType
            );


            if (isTest)
            {
               // request.append("&testMode="+testMode);
                transactionLogger.error("PayonOppwaPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("TEST_URL")+"/"+  previousTransactionId);
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+"/"+  previousTransactionId, authorizationToken, request.toString());

            }
            else
            {
                transactionLogger.error("PayonOppwaPaymentGateway :: inside isLive -----for "+trackingID+ "----" + RB.getString("LIVE_URL")+"/"+  previousTransactionId);
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+"/"+ previousTransactionId, authorizationToken, request.toString());
            }
            transactionLogger.error("PayonOppwaPaymentGateway :: cancel request-----for "+trackingID+ "----" + request);
            transactionLogger.error("PayonOppwaPaymentGateway :: cancel response-----for "+trackingID+ "----" + response);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
            if (responseVO != null)
            {
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    descriptor = gatewayAccount.getDisplayName();
                }
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setStatus(status);
            time = responseVO.getTimestamp();
            commResponseVO.setResponseTime(time);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JsonMappingException e)
        {
            transactionLogger.error("JsonMappingException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while void transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            transactionLogger.error("JsonParseException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while void transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while void transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

        public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
        {
            transactionLogger.error("---::: Inside processRefund :::---" );
            String reqType                  = "RF";
            String testMode                 = "EXTERNAL";
            NPayOnResponseVO responseVO     = null;
            PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();

            CommResponseVO commResponseVO   = new CommResponseVO();
            CommRequestVO commRequestVO     = (CommRequestVO) requestVO;
            Functions functions             = new Functions();

            CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO            = commRequestVO.getAddressDetailsVO();
            GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

            boolean isTest              = gatewayAccount.isTest();
            String merchantId           = gatewayAccount.getMerchantId();
            String authorizationToken   = gatewayAccount.getFRAUD_FTP_PASSWORD();

            String status = "";
            String remark = "";
            String descriptor = "";
            String response = "";
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            String previousTransactionId    =commTransactionDetailsVO.getPreviousTransactionId();
            try
            {
                if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
                {
                    currency=commTransactionDetailsVO.getCurrency();
                }
                if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
                {
                    tmpl_amount=addressDetailsVO.getTmpl_amount();
                }
                if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
                {
                    tmpl_currency=addressDetailsVO.getTmpl_currency();
                }

                StringBuffer request =new StringBuffer(
                        "entityId=" + merchantId +
                                "&amount=" + commTransactionDetailsVO.getAmount() +
                                "&currency=" + commTransactionDetailsVO.getCurrency() +
                                "&paymentType=" + reqType);

                if (isTest)
                {
                   request.append("&testMode="+testMode);
                    transactionLogger.error("PayonOppwaPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("TEST_URL")+"/"+  previousTransactionId);
                    response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+"/"+  previousTransactionId, authorizationToken, request.toString());
                }
                else
                {
                    transactionLogger.error("PayonOppwaPaymentGateway :: inside isTest -----for "+trackingID+ "----" + RB.getString("LIVE_URL")+"/"+  previousTransactionId);
                    response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+"/"+ previousTransactionId, authorizationToken, request.toString());
                }
                transactionLogger.error("PayonOppwaPaymentGateway :: refund request-----for "+trackingID+ "----" + request);
                transactionLogger.error("PayonOppwaPaymentGateway :: refund response-----for "+trackingID+ "----" + response);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
                if (responseVO != null)
                {
                    if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                    {
                        status = "success";
                        remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                        descriptor = gatewayAccount.getDisplayName();
                    }
                    else
                    {
                        status = "failed";
                        remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    }
                }
                commResponseVO.setMerchantOrderId(trackingID);
                commResponseVO.setStatus(status);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setDescription(remark);
                commResponseVO.setRemark(remark);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }
            catch (JsonMappingException e)
            {
                transactionLogger.error("JsonMappingException---" +trackingID + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (JsonParseException e)
            {            transactionLogger.error("JsonParseException---" +trackingID + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (IOException e)
            {            transactionLogger.error("IOException---" +trackingID + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            return commResponseVO;
        }

        @Override
        public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
        {
            transactionLogger.error("PayonOppwaPaymentGateway :: inside processInquiry()");
            Functions functions=new Functions();
            PayonOppwaUtils payonOppwaUtils=new PayonOppwaUtils();
            Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            String merchantId = gatewayAccount.getMerchantId();
            String authenticationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            String status = "";
            String remark = "";
            String descriptor = "";
            String response = "";
            String transactionStatus = "";

            String previousTransactionId=commTransactionDetailsVO.getPreviousTransactionId();

            if (functions.isValueNull(commTransactionDetailsVO.getOrderId()))
            {
                HashMap<String,String> hashMapDetails=payonOppwaUtils.getTransactionStatus(commTransactionDetailsVO.getOrderId());
                transactionStatus= hashMapDetails.get("dbStatus");
            }
            try
            {

                if (isTest)
                {
                    String testUrl=RB.getString("TEST_URL") + "/" + previousTransactionId+ "?entityId=" + merchantId;
                    transactionLogger.error("Test url----- for "+previousTransactionId+ "----"+testUrl);
                    response = payonOppwaUtils.doHttpPostConnection(testUrl, authenticationToken);
                }
                else
                {
                    String liveUrl=RB.getString("LIVE_URL") + "/" + previousTransactionId+ "?entityId=" + merchantId;
                    transactionLogger.error("liveUrl----- for "+previousTransactionId+ "----"+liveUrl);
                    response = payonOppwaUtils.doHttpPostConnection(liveUrl, authenticationToken);
                }
                transactionLogger.error("PayonOppwaPaymentGateway :: inquiry response-----for "+previousTransactionId+ "----" + response);
                NPayOnResponseVO nPayOnResponseVO = null;
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                nPayOnResponseVO = objectMapper.readValue(response, NPayOnResponseVO.class);
                String eci="";
                if (nPayOnResponseVO != null)
                {
                    if ("000.000.000".equals(nPayOnResponseVO.getResult().getCode()) || "000.000.100".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.110".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.111".equals(nPayOnResponseVO.getResult().getCode()) || "000.100.112".equals(nPayOnResponseVO.getResult().getCode()))
                    {
                        status = "success";
                        remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                        if(gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
                        {
                            descriptor = nPayOnResponseVO.getDescriptor();
                        }else
                        {
                            descriptor = gatewayAccount.getDisplayName();
                        }
                    }
                    else
                    {
                        status = "failed";
                        remark = nPayOnResponseVO.getResult().getDescription().replaceAll("'", " ");
                    }
                    if(nPayOnResponseVO.getThreeDSecure()!=null){
                        eci=nPayOnResponseVO.getThreeDSecure().getEci();
                    }
                }
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setMerchantId(merchantId);
                commResponseVO.setMerchantOrderId(commTransactionDetailsVO.getOrderId());
                commResponseVO.setTransactionId(nPayOnResponseVO.getId());
                commResponseVO.setAuthCode("-");
                commResponseVO.setTransactionType(transactionStatus);
                commResponseVO.setTransactionStatus(status);
                commResponseVO.setStatus(status);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                commResponseVO.setEci(eci);
                commResponseVO.setDescription(remark);
                commResponseVO.setRemark(remark);
                commResponseVO.setDescriptor(descriptor);
                commResponseVO.setResponseHashInfo(nPayOnResponseVO.getRegistrationId());

            }
            catch (JsonMappingException e)
            {
                transactionLogger.error("JsonMappingException---" + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (JsonParseException e)
            {
                transactionLogger.error("JsonParseException---"  + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (IOException e)
            {
                transactionLogger.error("IOException---"  + "----" ,  e);
                PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while canceling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
            }
            return commResponseVO;
        }
    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("PayonOppwaPaymentGateway:: inside processRecurring()");
        String reqType = "DB";
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        PayonOppwaUtils payonOppwaUtils= new PayonOppwaUtils();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String recurringType = commRequestVO.getRecurringBillingVO().getRecurringType();
        String termUrl = "";
                termUrl = RB.getString("TERM_URL");
                transactionLogger.error("from RB TERM_URL----for "+trackingID+ "----" +termUrl);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String merchantId = gatewayAccount.getMerchantId();
        transactionLogger.error("PayonOppwaPaymentGateway:: merchantId(entity id) ---"+merchantId);
        String authorizationToken = gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionLogger.debug("PayonOppwaPaymentGateway:: authorizationToken ---" + authorizationToken);

        String status = "";
        String remark = "";
        String billidesc = "";
        String response = "";
        String bankTransactionId = "";
        String time = "";
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

        String transactionCurrency = transDetailsVO.getCurrency();
        String transactionAmount = transDetailsVO.getAmount();
        String WalletId ="";
        String previousTrackingId= commTransactionDetailsVO.getPreviousTransactionId();
        String registrationId=PayonOppwaUtils.getRegistrationId(previousTrackingId);
        transactionLogger.error("RecurringId--> "+registrationId);
        if(functions.isValueNull(addressDetailsVO.getCustomerid()))
        {
            WalletId =addressDetailsVO.getCustomerid();
        }
        transactionLogger.error("WalletId---------------------------"+WalletId);

        try
        {
            StringBuffer RecurringRequest = new StringBuffer();
            RecurringRequest.append("entityId=" + merchantId
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentType=" + reqType
                    + "&recurringType=REPEATED");

            transactionLogger.error("recurringType----->" + recurringType);
            transactionLogger.error("PayonOppwaPaymentGateway:: RecurringRequest --- for "+trackingID+ "----"+RecurringRequest);

            if (isTest)
            {
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_Recurring_URL")+"/"+registrationId+"/payments" ,authorizationToken,RecurringRequest.toString());
                transactionLogger.error("inside Recurring isTest-----for "+trackingID+ "----" + RB.getString("TEST_Recurring_URL")+"/"+registrationId+"/payments" );

            }
            else
            {
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_Recurring_URL")+"/"+registrationId+"/payments",authorizationToken,RecurringRequest.toString());
                transactionLogger.error("inside Recurring isLive-----for "+trackingID+ "----" + RB.getString("LIVE_Recurring_URL")+"/"+registrationId+"/payments");

            }
            transactionLogger.error("PayonOppwaPaymentGateway:: Recurring response ---"+response);
            NPayOnResponseVO responseVO = null;
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            responseVO = objectMapper.readValue(response, NPayOnResponseVO.class);

            if (responseVO != null)
            {
                transactionLogger.error("code:" + responseVO.getResult().getCode());
                if ("000.000.000".equals(responseVO.getResult().getCode()) || "000.000.100".equals(responseVO.getResult().getCode()) || "000.100.110".equals(responseVO.getResult().getCode()) || "000.100.111".equals(responseVO.getResult().getCode()) || "000.100.112".equals(responseVO.getResult().getCode()))
                {
                    status = "success";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                    billidesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                }
/*
                else if ("000.200.000".equalsIgnoreCase(responseVO.getResult().getCode()))
                {
                    transactionLogger.error("inside 3d");
                    String MD = "";
                    String URL = "";
                    String PaReq = "";
                    String TermUrl = "";
                    String connector = "";

                    status = "pending3DConfirmation";

                    URL = responseVO.getRedirect().getUrl();
                    List<Parameter> parameters = responseVO.getRedirect().getParameters();
                    for (Parameter parameter : parameters)
                    {
                        String name = parameter.getName();
                        String value = parameter.getValue();

                        if ("PaReq".equals(name))
                        {
                            PaReq = value;
                        }
                        else if ("MD".equals(name))
                        {
                            MD = value;
                        }
                        else if ("TermUrl".equals(name))
                        {
                            TermUrl = value;
                        }
                        else if ("connector".equals(name))
                        {
                            connector = value;
                        }
                    }


                    //transactionLogger.debug("TermUrl------"+TermUrl);
                    Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
                    //comm3DResponseVO.setPaReq(PaReq);
                    //comm3DResponseVO.setUrlFor3DRedirect(URL);
                    //comm3DResponseVO.setMd(MD);
                    //comm3DResponseVO.setConnector(connector);
                    comm3DResponseVO.setRedirectMethod("POST");
                    //comm3DResponseVO.setTerURL(TermUrl);
                    comm3DResponseVO.setStatus(status);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setTransactionType("Sale");
                    comm3DResponseVO.setTransactionId(responseVO.getId());
                    comm3DResponseVO.setDescription(responseVO.getResult().getDescription().replaceAll("'", " "));
                    comm3DResponseVO.setRemark(responseVO.getResult().getDescription().replaceAll("'", " "));
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    transactionLogger.error("redirecting for 3d");
                    transactionLogger.error("TransactionId->"+responseVO.getId());
                    return comm3DResponseVO;
                }*/
                else
                {
                    status = "failed";
                    remark = responseVO.getResult().getDescription().replaceAll("'", " ");
                }
                bankTransactionId = responseVO.getId();
                time = responseVO.getTimestamp();
            }
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setTransactionId(bankTransactionId);
            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(billidesc);
            commResponseVO.setDescription(remark);
            commResponseVO.setRemark(remark);
            commResponseVO.setResponseTime(time);
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);

        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processRecurring()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonMappingException e)
        {
            transactionLogger.error("JsonMappingException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRecurring()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_MAPPING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e)
        {
            transactionLogger.error("JsonParseException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRecurring()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---" +trackingID + "----" ,  e);
            PZExceptionHandler.raiseTechnicalViolationException(PayonOppwaPaymentGateway.class.getName(), "processRecurring()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
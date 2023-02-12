package com.payment.Gpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.globalgate.GlobalGateUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GpayPaymentzGateway extends AbstractPaymentGateway
{
    @Override
    public String getMaxWaitDays() { return null;}
    public static TransactionLogger transactionLogger = new TransactionLogger(GpayPaymentzGateway.class.getName());
    private static Logger logger = new Logger(GpayPaymentzGateway.class.getName());
    public static final String GATEWAY_TYPE ="gpay";
    ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.GpayServlet");
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    public GpayPaymentzGateway(String accountId)
    {
        this.accountId =accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.error("Entering into processSale of GpayPaymentzGateway :::::");
        transactionLogger.error("Entering into processSale of GpayPaymentzGateway:::::");
        CommRequestVO commRequestVO                 = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO             = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO     = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO   = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO         = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO               = commRequestVO.getCommMerchantVO();
        Functions functions             = new Functions();
        String currency                 = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode            = Integer.parseInt(currency);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String MerchID                  = gatewayAccount.getMerchantId();
        String Pass                     = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String profileId                = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord               = gatewayAccount.getFRAUD_FTP_PATH();
        String forcePaymentTagValue     = "";
        String Amount    = transDetailsVO.getAmount();
        String Addr      = "";
        String Email     = "";
        String mobile    = "";
        String state     = "";
        String street    = "";
        String zip       = "";
        String city      = "";
        String firstname = "";
        String lastname  = "";
        String Address   = "";//Addr+", "+street+", "+city+", "+zip+", "+state;
        String CardHName = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        String userAgent ="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0"; //deviceDetailsVO.getUser_Agent();

        Boolean isTest=gatewayAccount.isTest();
        String RegCountry = "";
        String country="";
        String testString="";


        try
        {

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                mobile    = commAddressDetailsVO.getPhone();
            }
            else{
                mobile="9999999999";
            }
            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                firstname       = commAddressDetailsVO.getFirstname();
            }
            else{
                firstname="customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                lastname       = commAddressDetailsVO.getLastname();
            }
            else{
                firstname="customer";
            }

          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }
            if (functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length() != 3)
            {
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>" + country);
            }
            else
            {
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>" + country);
            }

            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>" + RegCountry);
            }



            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue    = gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue    = "FRP";
            }

            String Language = commAddressDetailsVO.getLanguage();
            String UDF1 = transDetailsVO.getOrderId();
            if (UDF1.equals(trackingID))
            {
                UDF1 = transDetailsVO.getMerchantOrderId();

            }
            transactionLogger.error("Addr------>" + Addr);

            transactionLogger.error("city------>" + city);
            transactionLogger.error("zip------>" + zip);
            transactionLogger.error("state------>" + state);
            transactionLogger.debug("UDF1------" + UDF1);

            transactionLogger.error("forcePaymentTagValue=========>"+forcePaymentTagValue);
            String UDF2 = "";
            String UDF3 = "";
            String redirectionUrl = rb.getString("GPAY_FRONTEND") + trackingID + "_sale";
            String statusUrl = rb.getString("GPAY_BACKEND") + trackingID + "_sale";

            Address = Addr + ", " + Addr + ", " + city + ", " + zip + ", " + state;

            //Request for Generating Token
            String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>" + MerchID + "</MerchID>" +
                    "      <Password>" + Pass + "</Password>" +
                    "      <CardType>" + GpayUtils.getCardType(commCardDetailsVO.getCardType()) + "</CardType>" +
                    "      <CardNo>" + commCardDetailsVO.getCardNum() + "</CardNo>" +
                    "      <ExpMonth>" + commCardDetailsVO.getExpMonth() + "</ExpMonth>" +
                    "      <ExpYear>" + commCardDetailsVO.getExpYear() + "</ExpYear>" +
                    "      <Ext>" + commCardDetailsVO.getcVV() + "</Ext>" +
                    "      <CardHolderName>" + commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname() + "</CardHolderName>" +
                    "      <CardHolderAddress>" + commAddressDetailsVO.getStreet() + "</CardHolderAddress>" +
                    "      <CardIssueNum></CardIssueNum>" +
                    "      <CardStartMonth></CardStartMonth>" +
                    "      <CardStartYear></CardStartYear>" +
                    "      <PspID></PspID>" +
                    "    </PopulateTransactionData2>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";

            String requestLog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>" + MerchID + "</MerchID>" +
                    "      <Password>" + Pass + "</Password>" +
                    "      <CardType>" + GpayUtils.getCardType(commCardDetailsVO.getCardType()) + "</CardType>" +
                    "      <CardNo>" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "</CardNo>" +
                    "      <ExpMonth>" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "</ExpMonth>" +
                    "      <ExpYear>" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "</ExpYear>" +
                    "      <Ext>" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "</Ext>" +
                    "      <CardHolderName>" + functions.maskingFirstName(commAddressDetailsVO.getFirstname()) + " " + functions.maskingLastName(commAddressDetailsVO.getLastname()) + "</CardHolderName>" +
                    "      <CardHolderAddress>" + commAddressDetailsVO.getStreet() + "</CardHolderAddress>" +
                    "      <CardIssueNum></CardIssueNum>" +
                    "      <CardStartMonth></CardStartMonth>" +
                    "      <CardStartYear></CardStartYear>" +
                    "      <PspID></PspID>" +
                    "    </PopulateTransactionData2>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";

            transactionLogger.error("request---" + trackingID + "--" + requestLog);
            transactionLogger.error("Address---" + trackingID + "--" + Address);

            String response = "";

            //Token Response
            if (isTest)
            {


                testString = "<TEST />";

                transactionLogger.error("Test_token_url================> "+rb.getString("Test_token_url"));
                response = GpayUtils.doPostHTTPSURLConnection(rb.getString("Test_token_url"), request);
            }
            else
            {
                transactionLogger.error("Live_token_url================> "+rb.getString("Live_token_url"));
                response = GpayUtils.doPostHTTPSURLConnection(rb.getString("Live_token_url"), request);
            }

            transactionLogger.error("response---" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                String ticketToken = "";
                HashMap map = (HashMap) GpayUtils.readGpayRedirectionXMLReponse(response);
                transactionLogger.error("Response-----" + trackingID + "--" + response);
                if (map != null || map.size() > 0)
                {
                    ticketToken = (String) map.get("PopulateTransactionData2Result");
                    transactionLogger.debug("ticketToken-----" + ticketToken);


                    //Request for Server to Server Flow
                    String xmlToPost = "<Transaction hash='" + secretWord.trim() + "'>" +
                            "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                            "<status_url urlEncode='true'>" + statusUrl + "</status_url>" +
                            "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                            "<ProfileID>" + profileId + "</ProfileID>" +
                            "<TicketNo>" + ticketToken + "</TicketNo>" +
                            "<ActionType>1</ActionType>" +
                            "<Value>" + Amount + "</Value>" +
                            "<Curr>" + currencyCode + "</Curr>" +
                            "<Lang>en</Lang>" +
                            "<ORef>" + trackingID + "</ORef>" +
                            "<Email>" + Email + "</Email>" +
                            "<MobileNo>" + mobile + "</MobileNo>" +
                            "<Address>" + Address + "</Address>" +
                            "<RegCountry>" + RegCountry + "</RegCountry>" +
                            "<Country>" + country + "</Country>" +
                            "<UDF1></UDF1>" +
                            "<UDF2></UDF2>" +
                            "<UDF3 />" +
                            "" + testString + "" +
                           /* "<TEST />" +*/
                            /*"<ForceBank>FRP</ForceBank>" +
                            "<ForcePayment>FRP</ForcePayment>" +*/
                            "<ForceBank>"+forcePaymentTagValue+"</ForceBank>" +
                            "<ForcePayment>"+forcePaymentTagValue+"</ForcePayment>" +
                            "<CIP>" + addressDetailsVO.getCardHolderIpAddress() + "</CIP>" +
                            "<UserAgent>" + userAgent + "</UserAgent>" +
                            "<NoCardList />" +
                            "<ClientAcc>" + firstname + " " + lastname + "</ClientAcc>" +
                            "<RegName>" + firstname + " " + lastname + "</RegName>" +
                            "<CardInput>New</CardInput>" +
                            "</Transaction>";
                   // transactionLogger.error("xmlToPost:::" + xmlToPost);

                    String xmlToPostLog = "<Transaction hash='" + secretWord.trim() + "'>" +
                            "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                            "<status_url urlEncode='true'>" + statusUrl + "</status_url>" +
                            "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                            "<ProfileID>" + profileId + "</ProfileID>" +
                            "<TicketNo>" + ticketToken + "</TicketNo>" +
                            "<ActionType>1</ActionType>" +
                            "<Value>" + Amount + "</Value>" +
                            "<Curr>" + currencyCode + "</Curr>" +
                            "<Lang>en</Lang>" +
                            "<ORef>" + trackingID + "</ORef>" +
                            "<Email>" + Email + "</Email>" +
                            "<MobileNo>" + mobile + "</MobileNo>" +
                            "<Address>" + Address + "</Address>" +
                            "<RegCountry>" + RegCountry + "</RegCountry>" +
                            "<Country>" + country + "</Country>" +
                            "<UDF1></UDF1>" +
                            "<UDF2></UDF2>" +
                            "<UDF3 />" +
                            "" + testString + "" +
                           /* "<TEST />" +*/
                           /* "<ForceBank>FRP</ForceBank>" +
                            "<ForcePayment>FRP</ForcePayment>" +*/
                            "<ForceBank>"+forcePaymentTagValue+"</ForceBank>" +
                            "<ForcePayment>"+forcePaymentTagValue+"</ForcePayment>" +
                            "<CIP>" + addressDetailsVO.getCardHolderIpAddress() + "</CIP>" +
                            "<UserAgent>" + userAgent + "</UserAgent>" +
                            "<NoCardList />" +
                            "<ClientAcc>" + functions.maskingFirstName(firstname) + " " + functions.maskingLastName(lastname) + "</ClientAcc>" +
                            "<RegName>" + functions.maskingFirstName(firstname) + " " +functions.maskingLastName(lastname) + "</RegName>" +
                            "<CardInput>New</CardInput>" +
                            "</Transaction>";
                    transactionLogger.error("xmlToPost:::" + xmlToPostLog);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("MerchID", MerchID);
                    jsonObject.put("MerchPass", Pass);
                    jsonObject.put("XMLParam", URLEncoder.encode(xmlToPost, "UTF-8"));

                    transactionLogger.error("Sale XML REQUEST====================>" + jsonObject);
                    String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN_URL"), jsonObject.toString());

                    transactionLogger.error("SALE RESPONSE===========>" + result);


                    if (functions.isValueNull(result) && functions.isJSONValid(result))
                    {
                        String tokenResult = "";
                        String errorMsg = "";
                        String token = "";
                        String transactionURL = "";
                        JSONObject resultReader = new JSONObject(result);
                        if (resultReader != null)
                        {
                            if (resultReader.has("Result") && functions.isValueNull(resultReader.getString("Result")))
                            {
                                tokenResult = resultReader.getString("Result");
                            }
                            if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg")))
                            {
                                errorMsg = resultReader.getString("ErrorMsg");
                            }
                            if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL")))
                            {
                                transactionURL = resultReader.getString("BaseURL");
                            }
                            if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token")))
                            {
                                token = resultReader.getString("Token");
                            }
                            if (tokenResult.equalsIgnoreCase("OK") && functions.isValueNull(token))
                            {
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setDescription(errorMsg);
                                commResponseVO.setUrlFor3DRedirect(transactionURL + token);
                                transactionLogger.error("UrlFor3DRedirect GPay===" + commResponseVO.getUrlFor3DRedirect());
                                return commResponseVO;

                            }
                            else if (tokenResult.equalsIgnoreCase("NOTOK"))
                            {
                                commResponseVO.setStatus("Failed");
                                commResponseVO.setTransactionStatus("Failed");
                                commResponseVO.setDescription(errorMsg);
                                commResponseVO.setRemark(errorMsg);

                            }
                            else
                            {
                                commResponseVO.setStatus("pending");
                                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                commResponseVO.setTransactionStatus("pending");
                            }
                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }

                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

/*

           /* String xml ="<Transaction hash=\""+secretWord+"\">"+
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>"+
                    "<ActionType>1</ActionType>"+
                    "<Value>"+Amount+"</Value>"+
                    "<Curr>"+currencyCode+"</Curr>"+
                    "<Lang>en</Lang>"+
                    "<ORef>"+trackingID+"</ORef>"+
                    "<Email>"+Email+"</Email>"+
                    "<MobileNo>"+mobile+"</MobileNo>"+
                    "<Address>"+Addr+", "+street+", "+city+", "+zip+", "+state+"</Address>"+
                    "<RegCountry>"+RegCountry+"</RegCountry>"+
                    "<Country>"+country+"</Country>"+
                    "<UDF1>"+UDF1+"</UDF1>"+
                    "<UDF2>"+UDF2+"</UDF2>"+
                    "<UDF3 />"+
                    "<TEST />"+
                    "<ForceBank>FRP</ForceBank>"+
                    "<NoCardList />"+
                    "<ClientAcc>"+firstname+" "+lastname+"</ClientAcc>"+
                    "<RegName>"+firstname+" "+lastname+"</RegName>"+
                    "</Transaction>";*/

            /*transactionLogger.error(" XML SALE Request  ====>"+trackingID+"  is=="+xml);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass",Pass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));

            transactionLogger.error(" json token request for Gpay processSale ====>"+trackingID+" is===>"+jsonObject);

            String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN"),jsonObject.toString());

            transactionLogger.error("Result of GpayPaymentzGateway Process sell ===>"+trackingID+" is===>"+result);

            if (functions.isValueNull(result) && functions.isJSONValid(result)){
                String tokenResult = "";
                String errorMsg = "";
                String token = "";
                String transactionURL = "";
                JSONObject resultReader = new JSONObject(result);
                if (resultReader!= null){
                    if (resultReader.has("Result")  && functions.isValueNull(resultReader.getString("Result"))){
                        tokenResult = resultReader.getString("Result");
                    }
                    if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg"))){
                        errorMsg = resultReader.getString("ErrorMsg");
                    }
                    if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL"))){
                        transactionURL = resultReader.getString("BaseURL");
                    }
                    if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token"))){
                        token = resultReader.getString("Token");
                    }
                    if (tokenResult.equalsIgnoreCase("OK") && functions.isValueNull(token)){
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setUrlFor3DRedirect(transactionURL+token);
                        transactionLogger.error("UrlFor3DRedirect GPay===" + commResponseVO.getUrlFor3DRedirect());
                        return commResponseVO;

                    }
                    else  if (tokenResult.equalsIgnoreCase("NOTOK")){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setRemark(errorMsg);

                    }
                    else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }*/

            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("GpayPaymentzGateway JSONException ---" + e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("GpayPaymentzGateway UnsupportedEncodingException ---"+e);
        }
       catch (Exception e){
            transactionLogger.error("GpayPaymentzGateway processSale exception ---"+e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.error("Entering into processAuthentication of GpayPaymentzGateway :::::");
        transactionLogger.error("Entering into processAuthentication of GpayPaymentzGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID              = gatewayAccount.getMerchantId();
        String Pass                 = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String profileId            = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord           = gatewayAccount.getFRAUD_FTP_PATH();
        String forcePaymentTagValue = "";

        String Amount = transDetailsVO.getAmount();
        String Addr     = "";
        String Email    = "";
        String mobile   = "";
        String state    = "";
        String street   = "";
        String zip      = "";
        String city     = "";
        String Address  = "";//Addr+", "+street+", "+city+", "+zip+", "+state;
        String firstname ="";
        String lastname  ="";
        String country  ="";
        String RegCountry ="";
        String userAgent ="Mozilla/5.0 (Android; Mobile; rv:13.0) Gecko/13.0 Firefox/13.0"; //deviceDetailsVO.getUser_Agent();

        Boolean isTest=gatewayAccount.isTest();


        try{

            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                mobile    = commAddressDetailsVO.getPhone();
            }
            else{
                mobile="9999999999";
            }
            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                firstname       = commAddressDetailsVO.getFirstname();
            }
            else{
                firstname="customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                lastname       = commAddressDetailsVO.getLastname();
            }
            else{
                firstname="customer";
            }

          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length()!=3){
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>"+country);
            }
            else{
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>"+country);
            }
            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>"+RegCountry);
            }

            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue=gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue="FRP";
            }

            String Language = commAddressDetailsVO.getLanguage();
            String UDF1=transDetailsVO.getOrderId();
            if(UDF1.equals(trackingID)){
                UDF1=transDetailsVO.getMerchantOrderId();
            }
            transactionLogger.debug("UDF1------"+UDF1);
            String UDF2 = "";
            String UDF3 = "";
            String testString="";


            String redirectionUrl =rb.getString("GPAY_FRONTEND")+trackingID+"_auth";
            String statusUrl =rb.getString("GPAY_BACKEND")+trackingID+"_auth";


            Address = Addr + ", " + Addr + ", " + city + ", " + zip + ", " + state;


            String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>" + MerchID + "</MerchID>" +
                    "      <Password>" + Pass + "</Password>" +
                    "      <CardType>" + GpayUtils.getCardType(commCardDetailsVO.getCardType()) + "</CardType>" +
                    "      <CardNo>" + commCardDetailsVO.getCardNum() + "</CardNo>" +
                    "      <ExpMonth>" + commCardDetailsVO.getExpMonth() + "</ExpMonth>" +
                    "      <ExpYear>" + commCardDetailsVO.getExpYear() + "</ExpYear>" +
                    "      <Ext>" + commCardDetailsVO.getcVV() + "</Ext>" +
                    "      <CardHolderName>" + commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname() + "</CardHolderName>" +
                    "      <CardHolderAddress>" + commAddressDetailsVO.getStreet() + "</CardHolderAddress>" +
                    "      <CardIssueNum></CardIssueNum>" +
                    "      <CardStartMonth></CardStartMonth>" +
                    "      <CardStartYear></CardStartYear>" +
                    "      <PspID></PspID>" +
                    "    </PopulateTransactionData2>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";

            String requestLog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>" + MerchID + "</MerchID>" +
                    "      <Password>" + Pass + "</Password>" +
                    "      <CardType>" + GpayUtils.getCardType(commCardDetailsVO.getCardType()) + "</CardType>" +
                    "      <CardNo>" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "</CardNo>" +
                    "      <ExpMonth>" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "</ExpMonth>" +
                    "      <ExpYear>" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "</ExpYear>" +
                    "      <Ext>" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "</Ext>" +
                    "      <CardHolderName>" + functions.maskingFirstName(commAddressDetailsVO.getFirstname()) + " " + functions.maskingLastName(commAddressDetailsVO.getLastname()) + "</CardHolderName>" +
                    "      <CardHolderAddress>" + commAddressDetailsVO.getStreet() + "</CardHolderAddress>" +
                    "      <CardIssueNum></CardIssueNum>" +
                    "      <CardStartMonth></CardStartMonth>" +
                    "      <CardStartYear></CardStartYear>" +
                    "      <PspID></PspID>" +
                    "    </PopulateTransactionData2>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";

            transactionLogger.error("request---" + trackingID + "--" + requestLog);
            transactionLogger.error("Address---" + trackingID + "--" + Address);

            String response = "";
            if (isTest)
            {
                testString = "<TEST />";
                transactionLogger.error("Test_token_url================> "+rb.getString("Test_token_url"));
                response = GpayUtils.doPostHTTPSURLConnection(rb.getString("Test_token_url"), request);
            }
            else
            {
                transactionLogger.error("Live_token_url================> "+rb.getString("Live_token_url"));
                response = GpayUtils.doPostHTTPSURLConnection(rb.getString("Live_token_url"), request);
            }

            transactionLogger.error("response---" + trackingID + "--" + response);
            if (functions.isValueNull(response))
            {
                String ticketToken = "";
                HashMap map = (HashMap) GpayUtils.readGpayRedirectionXMLReponse(response);
                transactionLogger.error("Response-----" + trackingID + "--" + response);
                if (map != null || map.size() > 0)
                {
                    ticketToken = (String) map.get("PopulateTransactionData2Result");
                    transactionLogger.debug("ticketToken-----" + ticketToken);

                    String xmlToPost = "<Transaction hash='" + secretWord.trim() + "'>" +
                            "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                            "<status_url urlEncode='true'>" + statusUrl + "</status_url>" +
                            "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                            "<ProfileID>" + profileId + "</ProfileID>" +
                            "<TicketNo>" + ticketToken + "</TicketNo>" +
                            "<ActionType>4</ActionType>" +
                            "<Value>" + Amount + "</Value>" +
                            "<Curr>" + currencyCode + "</Curr>" +
                            "<Lang>en</Lang>" +
                            "<ORef>" + trackingID + "</ORef>" +
                            "<Email>" + Email + "</Email>" +
                            "<MobileNo>" + mobile + "</MobileNo>" +
                            "<Address>" + Address + "</Address>" +
                            "<RegCountry>" + RegCountry + "</RegCountry>" +
                            "<Country>" + country + "</Country>" +
                            "<UDF1></UDF1>" +
                            "<UDF2></UDF2>" +
                            "<UDF3 />" +
                            "" + testString + "" +
                           /* "<TEST />" +*/
                            /*"<ForceBank>FRP</ForceBank>" +
                            "<ForcePayment>FRP</ForcePayment>" +*/
                            "<ForceBank>"+forcePaymentTagValue+"</ForceBank>" +
                            "<ForcePayment>"+forcePaymentTagValue+"</ForcePayment>" +
                            "<CIP>" + addressDetailsVO.getCardHolderIpAddress() + "</CIP>" +
                            "<UserAgent>" + userAgent + "</UserAgent>" +
                            "<NoCardList />" +
                            "<ClientAcc>" + firstname + " " + lastname + "</ClientAcc>" +
                            "<RegName>" + firstname + " " + lastname + "</RegName>" +
                            "<CardInput>New</CardInput>" +
                            "</Transaction>";
                  //  transactionLogger.error("xmlToPost:::" + xmlToPost);

                    String xmlToPostlog = "<Transaction hash='" + secretWord.trim() + "'>" +
                            "<RedirectionURL>" + redirectionUrl + "</RedirectionURL>" +
                            "<status_url urlEncode='true'>" + statusUrl + "</status_url>" +
                            "<FailedRedirectionURL>" + redirectionUrl + "</FailedRedirectionURL>" +
                            "<ProfileID>" + profileId + "</ProfileID>" +
                            "<TicketNo>" + ticketToken + "</TicketNo>" +
                            "<ActionType>4</ActionType>" +
                            "<Value>" + Amount + "</Value>" +
                            "<Curr>" + currencyCode + "</Curr>" +
                            "<Lang>en</Lang>" +
                            "<ORef>" + trackingID + "</ORef>" +
                            "<Email>" + Email + "</Email>" +
                            "<MobileNo>" + mobile + "</MobileNo>" +
                            "<Address>" + Address + "</Address>" +
                            "<RegCountry>" + RegCountry + "</RegCountry>" +
                            "<Country>" + country + "</Country>" +
                            "<UDF1></UDF1>" +
                            "<UDF2></UDF2>" +
                            "<UDF3 />" +
                            "" + testString + "" +
                           /* "<TEST />" +*/
                            /*"<ForceBank>FRP</ForceBank>" +
                            "<ForcePayment>FRP</ForcePayment>" +*/
                            "<ForceBank>"+forcePaymentTagValue+"</ForceBank>" +
                            "<ForcePayment>"+forcePaymentTagValue+"</ForcePayment>" +
                            "<CIP>" + addressDetailsVO.getCardHolderIpAddress() + "</CIP>" +
                            "<UserAgent>" + userAgent + "</UserAgent>" +
                            "<NoCardList />" +
                            "<ClientAcc>" + functions.maskingFirstName(firstname) + " " + functions.maskingLastName(lastname) + "</ClientAcc>" +
                            "<RegName>" + functions.maskingFirstName(firstname) + " " + functions.maskingLastName(lastname) + "</RegName>" +
                            "<CardInput>New</CardInput>" +
                            "</Transaction>";
                    transactionLogger.error("xmlToPost:::" + xmlToPostlog);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("MerchID", MerchID);
                    jsonObject.put("MerchPass", Pass);
                    jsonObject.put("XMLParam", URLEncoder.encode(xmlToPost, "UTF-8"));

                    transactionLogger.error("Auth XML REQUEST====================>" + jsonObject);
                    String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN_URL"), jsonObject.toString());

                    transactionLogger.error("Auth  RESPONSE===========>" + result);


                    if (functions.isValueNull(result) && functions.isJSONValid(result))
                    {
                        String tokenResult = "";
                        String errorMsg = "";
                        String token = "";
                        String transactionURL = "";
                        JSONObject resultReader = new JSONObject(result);
                        if (resultReader != null)
                        {
                            if (resultReader.has("Result") && functions.isValueNull(resultReader.getString("Result")))
                            {
                                tokenResult = resultReader.getString("Result");
                            }
                            if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg")))
                            {
                                errorMsg = resultReader.getString("ErrorMsg");
                            }
                            if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL")))
                            {
                                transactionURL = resultReader.getString("BaseURL");
                            }
                            if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token")))
                            {
                                token = resultReader.getString("Token");
                            }
                            if (tokenResult.equalsIgnoreCase("OK") && functions.isValueNull(token))
                            {
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setDescription(errorMsg);
                                commResponseVO.setUrlFor3DRedirect(transactionURL + token);
                                transactionLogger.error("UrlFor3DRedirect GPay===" + commResponseVO.getUrlFor3DRedirect());
                                return commResponseVO;

                            }
                            else if (tokenResult.equalsIgnoreCase("NOTOK"))
                            {
                                commResponseVO.setStatus("Failed");
                                commResponseVO.setTransactionStatus("Failed");
                                commResponseVO.setDescription(errorMsg);
                                commResponseVO.setRemark(errorMsg);

                            }
                            else
                            {
                                commResponseVO.setStatus("pending");
                                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                                commResponseVO.setTransactionStatus("pending");
                            }
                        }
                        else
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                            commResponseVO.setTransactionStatus("pending");
                        }

                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }



           /* String xml ="<Transaction hash=\""+secretWord+"\">"+
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>"+
                    "<ActionType>4</ActionType>"+
                    "<Value>"+Amount+"</Value>"+
                    "<Curr>"+currencyCode+"</Curr>"+
                    "<Lang>en</Lang>"+
                    "<ORef>"+trackingID+"</ORef>"+
                    "<Email>"+Email+"</Email>"+
                    "<MobileNo>"+mobile+"</MobileNo>"+
                    "<Address>"+Addr+", "+street+", "+city+", "+zip+", "+state+"</Address>"+
                    "<RegCountry>"+RegCountry+"</RegCountry>"+
                    "<Country>"+country+"</Country>"+
                    "<UDF1></UDF1>"+
                    "<UDF2></UDF2>"+
                    "<UDF3 />"+
                    "<TEST />"+
                    "<ForceBank>FRP</ForceBank>"+
                    "<NoCardList />"+
                    "<ClientAcc>"+firstName+" "+lastName+"</ClientAcc>"+
                    "<RegName>"+firstName+" "+lastName+"</RegName>"+
                    "</Transaction>";

            transactionLogger.error(" XML Request of Gpay processAuthentication ====>"+trackingID+" is====>"+xml);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass",Pass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));
            transactionLogger.error(" json request for Gpay processAuthentication ====>"+jsonObject);
            String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN"),jsonObject.toString());
            transactionLogger.error("Result of GpayPaymentzGateway processAuthentication ===>"+trackingID+" is===>"+result);
            if (functions.isValueNull(result) && functions.isJSONValid(result)){
                String tokenResult = "";
                String errorMsg = "";
                String token = "";
                String transactionURL = "";
                JSONObject resultReader = new JSONObject(result);
                if (resultReader!= null){
                    if (resultReader.has("Result")  && functions.isValueNull(resultReader.getString("Result"))){
                        tokenResult = resultReader.getString("Result");
                    }
                    if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg"))){
                        errorMsg = resultReader.getString("ErrorMsg");
                    }
                    if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL"))){
                        transactionURL = resultReader.getString("BaseURL");
                    }
                    if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token"))){
                        token = resultReader.getString("Token");
                    }
                    if (tokenResult.equalsIgnoreCase("OK")){
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setUrlFor3DRedirect(transactionURL+token);
                        System.out.println("UrlFor3DRedirect GPay processAuthentication ==="+commResponseVO.getUrlFor3DRedirect());
                    }
                    else if (tokenResult.equalsIgnoreCase("NOTOK")){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setRemark(errorMsg);

                    }
                    else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
*/  }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("GpayPaymentzGateway JSONException ---" + e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("GpayPaymentzGateway UnsupportedEncodingException ---"+e);
        }
        catch (Exception e){
            transactionLogger.error("GpayPaymentzGateway processAuth exception ---"+e);
        }
        return commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in GpayPaymentzGateway ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        GpayUtils gpayUtils                         = new GpayUtils();
        CommRequestVO commRequestVO     = gpayUtils.getGPayRequestVO(commonValidatorVO);

        try
        {
            String IsServiceFlag = commonValidatorVO.getMerchantDetailsVO().getIsService();
            transactionLogger.error("isService Flag -----" + IsServiceFlag);
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            if (IsServiceFlag.equalsIgnoreCase("Y")){
                transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            }else{
                transRespDetails = (Comm3DResponseVO) this.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
            }
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = GpayUtils.generateAutoSubmitForm(transRespDetails) ;
                transactionLogger.error("automatic redirect GpayPaymentzGateway form -- >>"+html);
            }
        } catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in GpayPaymentzGateway---", e);
        }
        return html;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        logger.error("Entering into processCapture of GpayPaymentzGateway :::::");
        transactionLogger.error("Entering into processCapture of GpayPaymentzGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID       = gatewayAccount.getMerchantId();
        String Pass          = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String profileId     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord    = gatewayAccount.getFRAUD_FTP_PATH();

        String forcePaymentTagValue="";

        String Amount    = transDetailsVO.getAmount();
        Boolean isTest=gatewayAccount.isTest();
        String Addr      ="";
        String Email     ="";
        String mobile    ="";
        String state     ="";
        String street    ="";
        String zip       ="";
        String city      ="";
        String firstname ="";
        String lastname  ="";

        String CardHName = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        String country="";
        String RegCountry = "";

        try{
            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                mobile    = commAddressDetailsVO.getPhone();
            }
            else{
                mobile="9999999999";
            }
            if(functions.isValueNull(commAddressDetailsVO.getFirstname())){
                firstname       = commAddressDetailsVO.getFirstname();
            }
            else{
                firstname="customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getLastname())){
                lastname       = commAddressDetailsVO.getLastname();
            }
            else{
                firstname="customer";
            }

          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length()!=3){
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>"+country);
            }
            else if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>"+country);
            }

            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>"+RegCountry);
            }

            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue=gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue="FRP";
            }

            String TransID = transDetailsVO.getPreviousTransactionId();
            String UDF1=transDetailsVO.getOrderId();
            if(UDF1.equals(trackingID)){
                UDF1=transDetailsVO.getMerchantOrderId();
            }
            transactionLogger.debug("UDF1------"+UDF1);
            String UDF2 = "";
            String UDF3 = "";
            String redirectionUrl =rb.getString("GPAY_FRONTEND")+trackingID+"_capture";
            String statusUrl =rb.getString("GPAY_BACKEND")+trackingID+"_capture";
            String Address   =Addr+", "+Addr+", "+city+", "+zip+", "+state;
            String testString = "";
            if (isTest){
                testString = "<TEST />";
            }
           /* String xml ="<Transaction hash="+secretWord+">"+
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>"+
                    "<ActionType>5</ActionType>"+
                    "<Value>"+Amount+"</Value>"+
                    "<Curr>"+currencyCode+"</Curr>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<Lang>en</Lang>"+
                    "<ORef>"+trackingID+"</ORef>"+
                    "<Email>"+Email+"</Email>"+
                    "<MobileNo>"+mobile+"</MobileNo>"+
                    "<Address>"+Addr+", "+street+", "+city+", "+zip+", "+state+"</Address>"+
                    "<RegCountry>"+RegCountry+"</RegCountry>"+
                    "<Country>"+country+"</Country>"+
                    "<UDF1></UDF1>"+
                    "<UDF2></UDF2>"+
                    "<UDF3 />"+
                    "<TEST />"+
                    "<ForceBank>FRP</ForceBank>"+
                    "<NoCardList />"+
                    "<ClientAcc></ClientAcc>"+
                    //"<ClientAcc>"+firstname+" "+lastname+"</ClientAcc>"+
                    "<RegName></RegName>"+
                  //  "<RegName>"+firstname+" "+lastname+"</RegName>"+
                    "</Transaction>";*/

            String xml ="<Transaction hash=\""+secretWord+"\">" +
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>" +
                   // "<ForceBank>FRP</ForceBank>"+
                    "<ForceBank>"+forcePaymentTagValue+"</ForceBank>"+
                    "<ActionType>5</ActionType>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<Value>"+Amount+"</Value>" +
                    "<Curr>"+currencyCode+"</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>"+trackingID+"</ORef>" +
                    "<Email>"+Email+"</Email>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+ country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                    "" + testString + "" +
                    /*"<TEST />"+*/
                    "</Transaction>";

            transactionLogger.error(" XML  processCapture request ====>"+trackingID+" is===>"+xml);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass",Pass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));
            transactionLogger.error(" json processCapture request ====>"+trackingID+" is===>"+jsonObject);
            String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN_URL"),jsonObject.toString());
            transactionLogger.error("Result of GpayPaymentzGateway processCapture ===>"+trackingID+" is===>"+result);
            if (functions.isValueNull(result) && functions.isJSONValid(result)){
                String tokenResult = "";
                String errorMsg = "";
                String token = "";
                String transactionURL = "";
                JSONObject resultReader = new JSONObject(result);
                if (resultReader!= null){
                    if (resultReader.has("Result")  && functions.isValueNull(resultReader.getString("Result"))){
                        tokenResult = resultReader.getString("Result");
                    }
                    if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg"))){
                        errorMsg = resultReader.getString("ErrorMsg");
                    }
                    if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL"))){
                        transactionURL = resultReader.getString("BaseURL");
                    }
                    if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token"))){
                        token = resultReader.getString("Token");
                    }
                    if (tokenResult.equalsIgnoreCase("OK")){
                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(transactionURL+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response capture code---" + postMethod.getStatusCode());
                        postMethod.releaseConnection();
                        transactionLogger.error("inside if ");
                        TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                        if("capturesuccess".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                            transactionLogger.error("inside  if capture condition ");
                            commResponseVO.setStatus("success");
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                            commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                            return commResponseVO;

                        }
                        else{
                            commResponseVO.setStatus("pending");
                        }
                    }else if (tokenResult.equalsIgnoreCase("NOTOK")){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setRemark(errorMsg);

                    }
                    else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (Exception e){
            transactionLogger.error("GpayPaymentzGateway processCapture exception ---"+e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.error("Entering into processRefund of GpayPaymentzGateway :::::");
        transactionLogger.error("Entering into processRefund of GpayPaymentzGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String MerchID          = gatewayAccount.getMerchantId();
        String Pass             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String profileId        = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord       = gatewayAccount.getFRAUD_FTP_PATH();
        String forcePaymentTagValue="";

        String Amount = transDetailsVO.getAmount();
        Boolean isTest=gatewayAccount.isTest();
        String Addr    = "";
        String Email   = "";
        String mobile  = "";
        String state   = "";
        String street  = "";
        String zip     = "";
        String city    = "";

        String CardHName = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        String RegCountry = "";
        String country="";
        try{
            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                mobile    = commAddressDetailsVO.getPhone();
            }
            else{
                mobile="9999999999";
            }
          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length()!=3){
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>"+country);
            }
            else if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>"+country);
            }

            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>"+RegCountry);
            }
            // String Language = commAddressDetailsVO.getLanguage();
            String UDF1=transDetailsVO.getOrderId();
            if(UDF1.equals(trackingID)){
                UDF1=transDetailsVO.getMerchantOrderId();
            }
            String testString = "";
            if (isTest){
                testString = "<TEST />";
            }


            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue=gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue="FRP";
            }

            transactionLogger.debug("UDF1------" + UDF1);
            String UDF2 = "";
            String UDF3 = "";
            String Address =Addr+", "+Addr+", "+city+", "+zip+", "+state;
            String redirectionUrl =rb.getString("GPAY_FRONTEND")+trackingID+"_refund";
            String statusUrl =rb.getString("GPAY_BACKEND")+trackingID+"_refund";
            String setPreviousTransactionId = transDetailsVO.getPreviousTransactionId();
            transactionLogger.error("setPreviousTransactionId======>"+setPreviousTransactionId);

           String xml ="<Transaction hash=\""+secretWord+"\">" +
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>" +
                   // "<ForceBank>FRP</ForceBank>"+
                    "<ForceBank>"+ forcePaymentTagValue+"</ForceBank>"+
                    "<ActionType>12</ActionType>"+
                    "<PspID>" + setPreviousTransactionId + "</PspID>" +
                    "<Value>"+Amount+"</Value>" +
                    "<Curr>"+currencyCode+"</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>"+trackingID+"</ORef>" +
                    "<Email>"+Email+"</Email>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+ country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                   "" + testString + "" +
                 /*   "<TEST />"+*/
                    "</Transaction>";
            transactionLogger.error(" XML Request of Gpay processRefund ====>"+trackingID+" is===>"+xml);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass",Pass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));
            transactionLogger.error(" json request for Gpay processRefund ====>"+trackingID+" is===>"+jsonObject);
            String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN_URL"),jsonObject.toString());
            transactionLogger.error("Result of GpayPaymentzGateway processRefund ===>"+trackingID+" is===>"+result);
            if (functions.isValueNull(result) && functions.isJSONValid(result)){
                String tokenResult = "";
                String errorMsg = "";
                String token = "";
                String transactionURL = "";
                JSONObject resultReader = new JSONObject(result);
                if (resultReader!= null){
                    if (resultReader.has("Result")  && functions.isValueNull(resultReader.getString("Result"))){
                        tokenResult = resultReader.getString("Result");
                    }
                    if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg"))){
                        errorMsg = resultReader.getString("ErrorMsg");
                    }
                    if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL"))){
                        transactionURL = resultReader.getString("BaseURL");
                    }
                    if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token"))){
                        token = resultReader.getString("Token");
                    }
                    if (tokenResult.equalsIgnoreCase("OK")){

                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(transactionURL+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response Refund code---"+postMethod.getStatusCode());
                        postMethod.releaseConnection();
                        transactionLogger.error("inside if ");
                        TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                        if("Reversed".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                            transactionLogger.error("inside  if reversed condition ");
                            commResponseVO.setStatus("success");
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                            commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                            return commResponseVO;

                        }
                        else{
                            commResponseVO.setStatus("pending");
                        }
                    }else if (tokenResult.equalsIgnoreCase("NOTOK")){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setRemark(errorMsg);

                    }
                    else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (Exception e){
            transactionLogger.error("GpayPaymentzGateway processRefund exception ---"+e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.error("Entering into processVoid of GpayPaymentzGateway :::::");
        transactionLogger.error("Entering into processVoid of GpayPaymentzGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency      = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String MerchID              =gatewayAccount.getMerchantId();
        String Pass                 =gatewayAccount.getFRAUD_FTP_PASSWORD();
        String profileId            =gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord           =gatewayAccount.getFRAUD_FTP_PATH();
        String forcePaymentTagValue ="";

        String Amount        = transDetailsVO.getAmount();
        Boolean isTest=gatewayAccount.isTest();
        String Addr           ="";
        String Email          ="";
        String mobile         ="";
        String state          ="";
        String street         ="";
        String zip            ="";
        String city           ="";


        String CardHName = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();

        try{

            String country="";
            String RegCountry = "";
            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
            if(functions.isValueNull(commAddressDetailsVO.getPhone())){
                mobile    = commAddressDetailsVO.getPhone();
            }
            else{
                mobile="9999999999";
            }

          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length()!=3){
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>"+country);
            }
            else if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>"+country);
            }

            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>"+RegCountry);
            }


            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue=gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue="FRP";
            }

            String TransID      = transDetailsVO.getPreviousTransactionId();
            String UDF1=transDetailsVO.getOrderId();

            if(UDF1.equals(trackingID)){
                UDF1=transDetailsVO.getMerchantOrderId();
            }
            String testString = "";
            if (isTest){
                testString = "<TEST />";
            }
            transactionLogger.debug("UDF1------"+UDF1);
            String UDF2 = "";
            String UDF3 = "";
            String Address=Addr+", "+Addr+", "+city+", "+zip+", "+state;
            String redirectionUrl =rb.getString("GPAY_FRONTEND")+trackingID+"_cancel";
            String statusUrl =rb.getString("GPAY_BACKEND")+trackingID+"_cancel";

           /* String xml ="<Transaction hash="+secretWord+">"+
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>"+
                    "<ActionType>9</ActionType>"+
                    "<Value>"+Amount+"</Value>"+
                    "<Curr>"+currencyCode+"</Curr>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<Lang>en</Lang>"+
                    "<ORef>"+trackingID+"</ORef>"+
                    "<Email>"+Email+"</Email>"+
                    "<MobileNo>"+mobile+"</MobileNo>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+RegCountry+"</RegCountry>"+
                    "<Country>"+country+"</Country>"+
                    "<UDF1></UDF1>"+
                    "<UDF2></UDF2>"+
                    "<UDF3 />"+
                    "<TEST />"+
                    "<ForceBank>FRP</ForceBank>"+
                    "<NoCardList />"+
                    "<ClientAcc></ClientAcc>"+
                    //"<ClientAcc>"+firstname+" "+lastname+"</ClientAcc>"+
                    //"<RegName>"+firstname+" "+lastname+"</RegName>"+
                    "<RegName></RegName>"+
                    "</Transaction>";*/
            String xml ="<Transaction hash=\""+secretWord+"\">" +
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+profileId+"</ProfileID>" +
                   // "<ForceBank>FRP</ForceBank>"+
                    "<ForceBank>"+ forcePaymentTagValue +"</ForceBank>"+
                    "<ActionType>9</ActionType>"+
                    "<PspID>" + TransID + "</PspID>" +
                    "<Value>"+Amount+"</Value>" +
                    "<Curr>"+currencyCode+"</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>"+trackingID+"</ORef>" +
                    "<Email>"+Email+"</Email>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+ country +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                    "" + testString + "" +
                   /* "<TEST />"+*/
                    "</Transaction>";

            transactionLogger.error(" XML Request of Gpay processVoid ====>"+trackingID+" is===>"+xml);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass",Pass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));
            transactionLogger.error(" json request for Gpay processVoid====>"+trackingID+" is===>"+jsonObject);
            String result = GpayUtils.doGetHTTPSURLConnectionClient(rb.getString("XML_TOKEN_URL"),jsonObject.toString());
            transactionLogger.error("Result of GpayPaymentzGateway processVoid ===>"+trackingID+" is===>"+result);
            if (functions.isValueNull(result) && functions.isJSONValid(result)){
                String tokenResult = "";
                String errorMsg = "";
                String token = "";
                String transactionURL = "";
                JSONObject resultReader = new JSONObject(result);
                if (resultReader!= null){
                    if (resultReader.has("Result")  && functions.isValueNull(resultReader.getString("Result"))){
                        tokenResult = resultReader.getString("Result");
                    }
                    if (resultReader.has("ErrorMsg") && functions.isValueNull(resultReader.getString("ErrorMsg"))){
                        errorMsg = resultReader.getString("ErrorMsg");
                    }
                    if (resultReader.has("BaseURL") && functions.isValueNull(resultReader.getString("BaseURL"))){
                        transactionURL = resultReader.getString("BaseURL");
                    }
                    if (resultReader.has("Token") && functions.isValueNull(resultReader.getString("Token"))){
                        token = resultReader.getString("Token");
                    }
                    if (tokenResult.equalsIgnoreCase("OK")){
                        TransactionManager transactionManager = new TransactionManager();
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(transactionURL+token);

                        httpClient.executeMethod(postMethod);
                        transactionLogger.error("Response void code---" + postMethod.getStatusCode());
                        postMethod.releaseConnection();
                        transactionLogger.error("inside if ");
                        TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
                        if("authcancelled".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                            transactionLogger.error("inside  if void condition ");
                            commResponseVO.setStatus("success");
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setTransactionId(updatedTransactionDetailsVO.getPaymentId());
                            commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                            commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                            commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                            return commResponseVO;

                        }
                        else{
                            commResponseVO.setStatus("pending");
                        }
                    }else if (tokenResult.equalsIgnoreCase("NOTOK")){
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(errorMsg);
                        commResponseVO.setRemark(errorMsg);

                    }
                    else {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (Exception e){
            transactionLogger.error("GpayPaymentzGateway processVoid exception ---"+e);
        }
        return commResponseVO;
    }
    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        logger.debug("Entering into processPayout of GpayPaymentzGateway:::::");
        transactionLogger.debug("Entering into processPayout of GpayPaymentzGateway:::::");
        GpayUtils gpayUtils=new GpayUtils();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID              = gatewayAccount.getMerchantId();
        String MerchPass            = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String ProfileId            = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord           = gatewayAccount.getFRAUD_FTP_PATH();
        String forcePaymentTagValue ="";

        // String Country=RBTemplate.getString(commAddressDetailsVO.getCountry());
        String orderId = transactionDetailsVO.getOrderId();
        String Addr           = "";
        String setPreviousTransactionId = transactionDetailsVO.getPaymentId();

        boolean isTest = gatewayAccount.isTest();
        String Email="";
        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
        {
            Email=commAddressDetailsVO.getEmail();
        }
        String Amount=transactionDetailsVO.getAmount();

        String state          = "";
        String street         = "";
        String zip            = "";
        String city           = "";

        String country="";
        String RegCountry = "";

        try{
            if(functions.isValueNull(commAddressDetailsVO.getEmail())){
                Email     = commAddressDetailsVO.getEmail();
            }
            else{
                Email="customer@gmail.comm";
            }
          /*  if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                street        = commAddressDetailsVO.getStreet();
            }*/

            if(functions.isValueNull(commAddressDetailsVO.getStreet())){
                Addr        = commAddressDetailsVO.getStreet().replaceAll("\\,", " ");
            }
            else{
                Addr        = "Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCity())){
                city            = commAddressDetailsVO.getCity();
            }
            else{
                city            ="Mumbai";
            }
            if(functions.isValueNull(commAddressDetailsVO.getState())){
                state            = commAddressDetailsVO.getState();
            }
            else{
                state           ="MH";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country         = commAddressDetailsVO.getCountry();
            }
            else {
                country         ="IND";
            }
            if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
                zip         = commAddressDetailsVO.getZipCode();
            }
            else {
                zip         ="500068";
            }
            if(functions.isValueNull(commAddressDetailsVO.getCountry()) && commAddressDetailsVO.getCountry().length()!=3){
                country = RBTemplate.getString(commAddressDetailsVO.getCountry());
                transactionLogger.error("country inside if====>"+country);
            }
            else if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                country = commAddressDetailsVO.getCountry();
                transactionLogger.error("country inside else====>"+country);
            }
            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                RegCountry = commAddressDetailsVO.getCountry();
                transactionLogger.error("RegCountry========>"+RegCountry);
            }
            String UDF1=transactionDetailsVO.getOrderId();
            if( UDF1 != null && UDF1.equals(trackingID)){
                UDF1=transactionDetailsVO.getMerchantOrderId();
            }

            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE()))
            {
                forcePaymentTagValue=gatewayAccount.getFRAUD_FTP_SITE();
            }
            else{
                forcePaymentTagValue="FRP";
            }

            String Address       =Addr+", "+Addr+", "+city+", "+zip+", "+state;
            String UDF2="";
            String UDF3="";
            String redirectionUrl="";
            String statusUrl="";
            String failedUrl="";

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                redirectionUrl=rb.getString("GPAY_FRONTEND")+trackingID+"_payout";
                statusUrl=rb.getString("GPAY_BACKEND")+trackingID+"_payout";
            }
            else
            {
                redirectionUrl=rb.getString("GPAY_FRONTEND")+trackingID+"_payout";
                statusUrl=rb.getString("GPAY_BACKEND")+trackingID+"_payout";

            }
            String testString = "";
            String URL="";
            if (isTest){
                testString = "<TEST />";
                URL=rb.getString("XML_TOKEN_URL");

            }else{
                URL=rb.getString("XML_TOKEN_URL");
            }
            String xml ="<Transaction hash=\""+secretWord+"\">" +
                    "<RedirectionURL>"+redirectionUrl+"</RedirectionURL>"+
                    "<status_url urlEncode=\"true\">"+statusUrl+"</status_url>"+
                    "<FailedRedirectionURL>"+redirectionUrl+"</FailedRedirectionURL>"+
                    "<ProfileID>"+ProfileId+"</ProfileID>" +
                    //"<ForceBank>FRP</ForceBank>"+
                    "<ForceBank>"+forcePaymentTagValue+"</ForceBank>"+
                    "<ActionType>13</ActionType>"+
                    "<PspID>" + setPreviousTransactionId + "</PspID>" +
                    "<Value>"+Amount+"</Value>" +
                    "<Curr>"+currencyCode+"</Curr>" +
                    "<Lang>en</Lang>" +
                    "<ORef>"+trackingID+"</ORef>" +
                    "<Email>"+Email+"</Email>"+
                    "<Address>"+Address+"</Address>"+
                    "<RegCountry>"+ RegCountry +"</RegCountry>"+
                    "<UDF1>"+UDF1+"</UDF1>" +
                    "<UDF2></UDF2>" +
                    "<UDF3></UDF3>" +
                    "" + testString + "" +
                    /*"<TEST />"+*/
                    "</Transaction>";

            transactionLogger.error("Payout XML Request------- for --------->" + trackingID + "--" +xml);
            transactionLogger.error("PSPID Previous Transaction Id ====================="+setPreviousTransactionId);


            JSONObject jsonObject=new JSONObject();

            jsonObject.put("MerchID",MerchID);
            jsonObject.put("MerchPass", MerchPass);
            jsonObject.put("XMLParam", URLEncoder.encode(xml,"UTF-8"));

            transactionLogger.error("Payout JSON Request--------- for ------->" + trackingID + "--"+jsonObject.toString());


            String response = gpayUtils.doGetHTTPSURLConnectionClient(URL, jsonObject.toString());


            transactionLogger.error("Payout  Response------- for --------->" + trackingID + "--"+response);


            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {
                String result="";
                String baseUrl="";
                String token="";
                String errorMsg="";
                JSONObject jsonReader=new JSONObject(response);

                if(jsonReader != null)
                {
                    if(jsonReader.has("Result") && functions.isValueNull(jsonReader.getString("Result")))
                    {
                        result=jsonReader.getString("Result");
                    }
                    if (jsonReader.has("ErrorMsg") && functions.isValueNull(jsonReader.getString("ErrorMsg"))){
                        errorMsg = jsonReader.getString("ErrorMsg");
                    }
                    if(jsonReader.has("BaseURL") && functions.isValueNull(jsonReader.getString("BaseURL")))
                    {
                        baseUrl=jsonReader.getString("BaseURL");
                    }
                    if(jsonReader.has("Token") && functions.isValueNull(jsonReader.getString("Token")))
                    {
                        token=jsonReader.getString("Token");
                    }
                    if (result.equalsIgnoreCase("OK"))
                    {

                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription(errorMsg);
                        comm3DResponseVO.setUrlFor3DRedirect(baseUrl+token);
                        transactionLogger.error("UrlFor3DRedirect Gpay processPayout ===>"+comm3DResponseVO.getUrlFor3DRedirect());
                        HttpClient httpClient = new HttpClient();
                        PostMethod postMethod = new PostMethod(comm3DResponseVO.getUrlFor3DRedirect());

                        httpClient.executeMethod(postMethod);
                        String responsesucess = new String(postMethod.getResponseBody());
                        transactionLogger.error("Response Payout after backend ---" + responsesucess);
                        transactionLogger.error("Response Payout code---"+postMethod.getStatusCode());
                        postMethod.releaseConnection();
                        return comm3DResponseVO;

                    }
                    else if (result.equalsIgnoreCase("NOTOK")){
                        comm3DResponseVO.setStatus("Failed");
                        comm3DResponseVO.setTransactionStatus("Failed");
                        comm3DResponseVO.setDescription(errorMsg);
                        comm3DResponseVO.setRemark(errorMsg);

                    }
                    else {
                        comm3DResponseVO.setStatus("pending");
                        comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        comm3DResponseVO.setTransactionStatus("pending");
                    }
                }
            }
            else {
                comm3DResponseVO.setStatus("Failed");
                comm3DResponseVO.setDescription("Payout Failed");
            }


        }
        catch(Exception e)
        {
            transactionLogger.error("GpayPaymentzGateway processPayout exception ---"+e);
        }
        return comm3DResponseVO;
    }
    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.debug("Entering into processInquiry of GpayPaymentzGateway:::::");
        transactionLogger.debug("Entering into processInquiry of GpayPaymentzGateway:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String merchID      = gatewayAccount.getMerchantId(); //GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchPass    = gatewayAccount.getPassword(); //GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId      = trackingID;
        String _3DTicket="";

        try
        {
            String _3DInquiryRequest="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "  <soap12:Body>" +
                    "    <getTransactionStatus xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>"+merchID+"</MerchID>" +
                    "      <MerchPass>"+merchPass+"</MerchPass>" +
                    "      <ORef>"+orderId+"</ORef>" +
                    "    </getTransactionStatus>" +
                    "  </soap12:Body>" +
                    "</soap12:Envelope>";


            transactionLogger.error("Inquiry Request--for--" +trackingID+ "--" + _3DInquiryRequest);

            String inquiryResponse = GpayUtils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), _3DInquiryRequest);
            transactionLogger.error("Inquiry Response--for--" +trackingID + "--"+inquiryResponse);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(inquiryResponse))
            {

                HashMap<String,String> map = (HashMap) GpayUtils.readGpayInquiryXMLResponse(inquiryResponse);
                if (map != null || map.size() != 0)
                {
                    result=map.get("Result");
                    transactionLogger.error("result--->"+result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED"))
                        {
                            transactionLogger.error("Inside result--->"+result);

                            commResponseVO.setStatus("success");
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(result);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }else if(result.equals("PENDING"))
                        {
                            transactionLogger.error("Inside PENDING--->"+result);
                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark(result);
                        }
                        else if(result.equals("ENROLLED"))
                        {
                            transactionLogger.error("Inside ENROLLED--->"+result);
                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark(result);
                        }else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescription(result);
                        }
                    }
                    else
                    {

                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(result);
                        commResponseVO.setDescription("Transaction Failed");
                    }

                    commResponseVO.setMerchantId(merchID);
                    commResponseVO.setMerchantOrderId(map.get("ORef"));
                    commResponseVO.setTransactionId(map.get("pspid"));
                    commResponseVO.setAuthCode(map.get("AuthCode"));
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(map.get("Value"));
                    commResponseVO.setCurrency(map.get("Currency"));
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(map.get("Result"));
                }else
                {

                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setTransactionId(paymentid);
                }
            }else{

                commResponseVO.setStatus("pending");
                commResponseVO.setRemark("Transaction Pending");
                commResponseVO.setDescription("Transaction Pending");
                commResponseVO.setTransactionId(paymentid);
            }

        }catch (Exception e)
        {
            logger.error("Inquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GpayPaymentzGateway.class.getName(), "processQuery()", null, "common", "Remote Exception while refunding transaction via Gpay", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.debug("Entering into processPayoutInquiry of GpayPaymentzGateway:::::");
        transactionLogger.debug("Entering into processPayoutInquiry of GpayPaymentzGateway:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String merchID = gatewayAccount.getMerchantId(); //GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchPass = gatewayAccount.getPassword(); //GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId = trackingID;
        String _3DTicket="";

        try
        {
            String _3DInquiryRequest="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "  <soap12:Body>" +
                    "    <getTransactionStatus xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>"+merchID+"</MerchID>" +
                    "      <MerchPass>"+merchPass+"</MerchPass>" +
                    "      <ORef>"+orderId+"</ORef>" +
                    "    </getTransactionStatus>" +
                    "  </soap12:Body>" +
                    "</soap12:Envelope>";


            transactionLogger.error("Payout Inquiry Request--for--" + trackingID + "--" + _3DInquiryRequest);

            String inquiryResponse = GpayUtils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), _3DInquiryRequest);
            transactionLogger.error("Payout Inquiry Response--for--" + trackingID + "--"+inquiryResponse);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(inquiryResponse))
            {


                HashMap<String,String> map = (HashMap) GpayUtils.readGpayInquiryXMLResponse(inquiryResponse);
                if (map != null || map.size() != 0)
                {
                    result=map.get("Result");
                    transactionLogger.error("result--->"+result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED"))
                        {
                            transactionLogger.error("Inside result--->"+result);

                            commResponseVO.setStatus("success");
                            commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(result);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else if(result.equals("PENDING"))
                        {
                            transactionLogger.error("Inside ENROLLED--->"+result);

                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark(result);
                        }
                        else if(result.equals("ENROLLED"))
                        {
                            transactionLogger.error("Inside ENROLLED--->"+result);

                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark(result);
                        }else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescription(result);
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(result);
                        commResponseVO.setDescription("Transaction Failed");
                    }

                    commResponseVO.setMerchantId(merchID);
                    commResponseVO.setMerchantOrderId(map.get("ORef"));
                    commResponseVO.setTransactionId(map.get("pspid"));
                    commResponseVO.setAuthCode(map.get("AuthCode"));
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(map.get("Value"));
                    commResponseVO.setCurrency(map.get("Currency"));
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(map.get("Result"));
                }else
                {

                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setTransactionId(paymentid);
                }
            }else{

                commResponseVO.setStatus("pending");
                commResponseVO.setRemark("Transaction Pending");
                commResponseVO.setDescription("Transaction Pending");
                commResponseVO.setTransactionId(paymentid);
            }

        }catch (Exception e)
        {
            logger.error("Inquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GpayPaymentzGateway.class.getName(), "processPayoutQuery()", null, "common", "Remote Exception while refunding transaction via Gpay", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
}


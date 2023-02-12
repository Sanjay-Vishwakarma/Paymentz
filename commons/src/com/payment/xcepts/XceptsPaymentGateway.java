package com.payment.xcepts;


import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-05-14.
 */
public class XceptsPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE             = "xcepts";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.Xcepts");
    private static TransactionLogger transactionlogger  = new TransactionLogger(XceptsPaymentGateway.class.getName());
    // sale = 1, preauth = 4, refund = 2, Capture = 5, Void = 9, inquiry = 15
    // todo REDIRECT_URL = https://staging.paymentz.com/transaction/XFrontendServlet?trackingId=          //  configure it at bank side
    // todo NOTIFY_URL = https://staging.paymentz.com/transaction/XBackendServlet?trackingId=             //  configure it at bank side

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public XceptsPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside Xcepts processAuthentication() ----------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String paymentmethod                                = GatewayAccountService.getPaymentMode(commTransactionDetailsVO.getPaymentType());
        
        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String card          = "";
        String cvv2          = "";
        String expYear       = "";
        String expMonth      = "";
        String member        = "";
        String currencyCode  = "";
        String address       = "";
        String city          = "";
        String statecode     = "";
        String zip           = "";
        String countryCode   = "";
        String email         = "";
        String amount        = "";
        String trackid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingID;
            udf1         = trackingID;       // should be present if transaction was 3D whenever payId / transid is not received
            action       = "4";
            card         = commCardDetailsVO.getCardNum();
            cvv2         = commCardDetailsVO.getcVV();
            expYear      = commCardDetailsVO.getExpYear();
            expMonth     = commCardDetailsVO.getExpMonth();
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            member       = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
            address      = commAddressDetailsVO.getStreet();
            city         = commAddressDetailsVO.getCity();
            statecode    = commAddressDetailsVO.getState();
            zip          = commAddressDetailsVO.getZipCode();
            countryCode  = commAddressDetailsVO.getCountry();
            email        = commAddressDetailsVO.getEmail();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }


            if (isTest){
                requestURL = RB.getString("TEST_SALE_URL");
            }else {
                requestURL = RB.getString("LIVE_SALE_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+card+"</card>" +
                    "<cvv2>"+cvv2+"</cvv2>" +
                    "<expYear>"+expYear+"</expYear>" +
                    "<expMonth>"+expMonth+"</expMonth>" +
                    "<member>"+member+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+email+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            String requestLOG = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+functions.maskingPan(card)+"</card>" +
                    "<cvv2>"+functions.maskingNumber(cvv2)+"</cvv2>" +
                    "<expYear>"+functions.maskingNumber(expYear)+"</expYear>" +
                    "<expMonth>"+functions.maskingNumber(expMonth)+"</expMonth>" +
                    "<member>"+functions.maskingFirstName(commAddressDetailsVO.getFirstname())+ " " + functions.maskingLastName(commAddressDetailsVO.getLastname())+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+functions.getEmailMasking(email)+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processAuthentication() Request: " +trackingID+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ requestLOG);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processAuthentication() Response: " + trackingID + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processAuthentication() xmlReadedResponse: " + trackingID + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String targetUrl      = "";
            String payId          = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";
            String billingDescriptor            = "";
            String dynamic_billing_descriptor   = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");
                
                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("targetUrl") && functions.isValueNull(xmlReadedResponse.get("targetUrl")))
                {
                    targetUrl = xmlReadedResponse.get("targetUrl");
                    requestHM.put("targetUrl", xmlReadedResponse.get("targetUrl"));
                }

                if (xmlReadedResponse.containsKey("payId") && functions.isValueNull(xmlReadedResponse.get("payId")))
                {
                    payId = xmlReadedResponse.get("payId");
                    requestHM.put("payId",xmlReadedResponse.get("payId"));
                }

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("dynamic_billing_descriptor") && functions.isValueNull(xmlReadedResponse.get("dynamic_billing_descriptor")))
                    dynamic_billing_descriptor = xmlReadedResponse.get("dynamic_billing_descriptor");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(targetUrl) && functions.isValueNull(payId))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(targetUrl + payId);
                    comm3DResponseVO.setRequestMap(requestHM);
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                }
                else if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("Non-3D");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }

                    if (functions.isValueNull(billingDescriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                    else if (functions.isValueNull(dynamic_billing_descriptor))
                        comm3DResponseVO.setDescriptor(dynamic_billing_descriptor);
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setErrorCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingID+" Exception >>>" + e );
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside Xcepts processSale()---------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String paymentmethod                                = GatewayAccountService.getPaymentMode(commTransactionDetailsVO.getPaymentType());
        
        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String card          = "";
        String cvv2          = "";
        String expYear       = "";
        String expMonth      = "";
        String member        = "";
        String currencyCode  = "";
        String address       = "";
        String city          = "";
        String statecode     = "";
        String zip           = "";
        String countryCode   = "";
        String email         = "";
        String amount        = "";
        String trackid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingID;
            udf1         = trackingID;        // should be present if transaction was 3D whenever payId / transid is not received
            action       = "1";
            card         = commCardDetailsVO.getCardNum();
            cvv2         = commCardDetailsVO.getcVV();
            expYear      = commCardDetailsVO.getExpYear();
            expMonth     = commCardDetailsVO.getExpMonth();
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            member       = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
            address      = commAddressDetailsVO.getStreet();
            city         = commAddressDetailsVO.getCity();
            statecode    = commAddressDetailsVO.getState();
            zip          = commAddressDetailsVO.getZipCode();
            countryCode  = commAddressDetailsVO.getCountry();
            email        = commAddressDetailsVO.getEmail();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_SALE_URL");
            }else {
                requestURL = RB.getString("LIVE_SALE_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+card+"</card>" +
                    "<cvv2>"+cvv2+"</cvv2>" +
                    "<expYear>"+expYear+"</expYear>" +
                    "<expMonth>"+expMonth+"</expMonth>" +
                    "<member>"+member+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+email+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            String requestLOG = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+functions.maskingPan(card)+"</card>" +
                    "<cvv2>"+functions.maskingNumber(cvv2)+"</cvv2>" +
                    "<expYear>"+functions.maskingNumber(expYear)+"</expYear>" +
                    "<expMonth>"+functions.maskingNumber(expMonth)+"</expMonth>" +
                    "<member>"+functions.maskingFirstName(commAddressDetailsVO.getFirstname())+ " " + functions.maskingLastName(commAddressDetailsVO.getLastname())+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+functions.getEmailMasking(email)+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processSale() Request: " +trackingID+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ requestLOG);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processSale() Response: " + trackingID + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processSale() xmlReadedResponse: " + trackingID + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String targetUrl      = "";
            String payId          = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";
            String billingDescriptor            = "";
            String dynamic_billing_descriptor   = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("targetUrl") && functions.isValueNull(xmlReadedResponse.get("targetUrl")))
                {
                    targetUrl = xmlReadedResponse.get("targetUrl");
                    requestHM.put("targetUrl",xmlReadedResponse.get("targetUrl"));
                }

                if (xmlReadedResponse.containsKey("payId") && functions.isValueNull(xmlReadedResponse.get("payId")))
                {
                    payId = xmlReadedResponse.get("payId");
                    requestHM.put("payId",xmlReadedResponse.get("payId"));
                }

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("dynamic_billing_descriptor") && functions.isValueNull(xmlReadedResponse.get("dynamic_billing_descriptor")))
                    dynamic_billing_descriptor = xmlReadedResponse.get("dynamic_billing_descriptor");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(targetUrl) && functions.isValueNull(payId))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(targetUrl + payId);
                    comm3DResponseVO.setRequestMap(requestHM);
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                }
                else if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("Non-3D");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }

                    if (functions.isValueNull(billingDescriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                    else if (functions.isValueNull(dynamic_billing_descriptor))
                        comm3DResponseVO.setDescriptor(dynamic_billing_descriptor);
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setErrorCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch(Exception e)
        {
            transactionlogger.error(trackingID+" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Inside Xcepts processInquiry() ------>");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest      = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String currencyCode  = "";
        String amount        = "";
        String trackid       = "";
        String transid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingID;
            udf1         = trackingID;      // should be present if transaction was 3D whenever payId / transid is not received
            action       = "15";
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            transid      = commTransactionDetailsVO.getPreviousTransactionId();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_INQUIRY_URL");
            }else {
                requestURL = RB.getString("LIVE_INQUIRY_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<transid>"+transid+"</transid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processInquiry() Request: " +trackingID+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ request);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processInquiry() Response: " + trackingID + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processInquiry() xmlReadedResponse: " + trackingID + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String targetUrl      = "";
            String payId          = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";
            String billingDescriptor            = "";
            String dynamic_billing_descriptor   = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("targetUrl") && functions.isValueNull(xmlReadedResponse.get("targetUrl")))
                    targetUrl = xmlReadedResponse.get("targetUrl");

                if (xmlReadedResponse.containsKey("payId") && functions.isValueNull(xmlReadedResponse.get("payId")))
                    payId = xmlReadedResponse.get("payId");

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("dynamic_billing_descriptor") && functions.isValueNull(xmlReadedResponse.get("dynamic_billing_descriptor")))
                    dynamic_billing_descriptor = xmlReadedResponse.get("dynamic_billing_descriptor");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }

                    if (functions.isValueNull(billingDescriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                    else if (functions.isValueNull(dynamic_billing_descriptor))
                        comm3DResponseVO.setDescriptor(dynamic_billing_descriptor);
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setErrorCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                XceptsUtils.updateMainTableEntry(tranid,"",trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingID+" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside Xcepts processRefund() ---------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String currencyCode  = "";
        String countryCode   = "";
        String amount        = "";
        String trackid       = "";
        String transid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingID;
            udf1         = trackingID;      // should be present if transaction was 3D whenever payId / transid is not received
            action       = "2";
            currencyCode = commTransactionDetailsVO.getCurrency();
            countryCode  = commAddressDetailsVO.getCountry();
            amount       = commTransactionDetailsVO.getAmount();
            transid      = commTransactionDetailsVO.getPreviousTransactionId();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_REFUND_URL");
            }else {
                requestURL = RB.getString("LIVE_REFUND_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<transid>"+transid+"</transid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processRefund() Request: " +trackingID+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ request);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processRefund() Response: " + trackingID + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processRefund() xmlReadedResponse: " + trackingID + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";
            String billingDescriptor            = "";
            String dynamic_billing_descriptor   = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");

                if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("dynamic_billing_descriptor") && functions.isValueNull(xmlReadedResponse.get("dynamic_billing_descriptor")))
                    dynamic_billing_descriptor = xmlReadedResponse.get("dynamic_billing_descriptor");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }

                    if (functions.isValueNull(billingDescriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                    else if (functions.isValueNull(dynamic_billing_descriptor))
                        comm3DResponseVO.setDescriptor(dynamic_billing_descriptor);
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingID);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingID+" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processCapture(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("inside Xcepts processCapture() --------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String currencyCode  = "";
        String amount        = "";
        String trackid       = "";
        String transid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingId;
            udf1         = trackingId;      // should be present if transaction was 3D whenever payId / transid is not received
            action       = "5";
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            transid      = commTransactionDetailsVO.getPreviousTransactionId();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_CAPTURE_URL");
            }else {
                requestURL = RB.getString("LIVE_CAPTURE_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<transid>"+transid+"</transid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processCapture() Request: " +trackingId+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ request);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processCapture() Response: " + trackingId + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processCapture() xmlReadedResponse: " + trackingId + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingId+" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }

        return comm3DResponseVO;
    }

    public GenericResponseVO processVoid(String trackingId, GenericRequestVO requestVO)throws PZTechnicalViolationException
    {
        transactionlogger.error("inside Xcepts processVoid() ---------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String currencyCode  = "";
        String amount        = "";
        String trackid       = "";
        String transid       = "";
        String udf1          = "";
        String response      = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();

        try
        {
            trackid      = trackingId;
            udf1         = trackingId;      // should be present if transaction was 3D whenever payId / transid is not received
            action       = "9";
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            transid      = commTransactionDetailsVO.getPreviousTransactionId();

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_VOID_URL");
            }else {
                requestURL = RB.getString("LIVE_VOID_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<transid>"+transid+"</transid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "</request>";

            transactionlogger.error("processVoid() Request: " + trackingId + " " + " RequestURL:" + requestURL + " RequestParameters:" + request);
            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processVoid() Response: " + trackingId + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processVoid() xmlReadedResponse: " + trackingId + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingId +" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingId,GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processRecurring() of Xcepts......");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        Functions functions                 = new Functions();

        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                       = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String paymentmethod                                = "CC"; //commTransactionDetailsVO.getPaymentType();

        boolean isTest       = gatewayAccount.isTest();
        String is3DSupported = gatewayAccount.get_3DSupportAccount();
        String terminalid    = "";
        String password      = "";
        String requestURL    = "";
        String action        = "";
        String currencyCode  = "";
        String countryCode   = "";
        String card          = "";
        String cvv2          = "";
        String expYear       = "";
        String expMonth      = "";
        String member        = "";
        String address       = "";
        String city          = "";
        String statecode     = "";
        String zip           = "";
        String email         = "";
        String expDate       = "";
        String paymenttype              = "";
        String subscriptiontype         = "";
        String paymentcycle             = "";
        String paymentdays              = "";
        String noOfRecurringpayments    = "";
        String paymentstartdate         = "";
        String recurringpaymentamount   = "";
        String amount       = "";
        String trackid      = "";
        String udf1         = "";
        String response     = "";
        HashMap<String,String> xmlReadedResponse = new HashMap<>();
        LocalDate today;

        try
        {
            today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            expDate = commCardDetailsVO.getExpMonth();
            if (functions.isValueNull(commCardDetailsVO.getExpMonth()) && functions.isValueNull(commCardDetailsVO.getExpYear()) && commCardDetailsVO.getExpMonth().length() < 8 && commCardDetailsVO.getExpYear().length() < 8)
            {
                expYear      = commCardDetailsVO.getExpYear();
                expMonth     = commCardDetailsVO.getExpMonth();
            }
            else if (functions.isValueNull(expDate) || functions.isValueNull(PzEncryptor.decryptExpiryDate(expDate)))
            {
                if (expDate.length() > 8)
                {
                    expDate = PzEncryptor.decryptExpiryDate(commCardDetailsVO.getExpMonth());
                    if (expDate.split("/").length > 0)
                    {
                        expMonth = expDate.split("/")[0];
                        expYear  = expDate.split("/")[1];
                    }
                }
                else if (expDate.length() < 8)
                {
                    expDate = commCardDetailsVO.getExpMonth();
                    if (expDate.split("/").length > 0)
                    {
                        expMonth = expDate.split("/")[0];
                        expYear  = expDate.split("/")[1];
                    }
                }
            }

            card = commCardDetailsVO.getCardNum();
            if (card.length() > 30)
            {
                card         = PzEncryptor.decryptPAN(commCardDetailsVO.getCardNum());
            }

            trackid      = trackingId;
            udf1         = trackingId;        // should be present if transaction was 3D whenever payId / transid is not received
            action       = "1";
            cvv2         = PzEncryptor.decryptCVV(commTransactionDetailsVO.getCvv());
            currencyCode = commTransactionDetailsVO.getCurrency();
            amount       = commTransactionDetailsVO.getAmount();
            member       = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
            address      = commAddressDetailsVO.getStreet();
            city         = commAddressDetailsVO.getCity();
            statecode    = commAddressDetailsVO.getState();
            zip          = commAddressDetailsVO.getZipCode();
            countryCode  = commAddressDetailsVO.getCountry();
            email        = commAddressDetailsVO.getEmail();
            paymenttype              = "R";
            subscriptiontype         = "S";
            paymentcycle             = "D";
            paymentdays              = "1";
            noOfRecurringpayments    = "5";
            paymentstartdate         = today.plusDays(1).format(formatter);     //20/05/2022
            recurringpaymentamount   = commTransactionDetailsVO.getAmount();

            transactionlogger.error(trackingId + " paymentstartdate: " + paymentstartdate);

            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                terminalid    = gatewayAccount.getMerchantId();
                password      = gatewayAccount.getFRAUD_FTP_USERNAME();
            }
            else
            {
                terminalid    = gatewayAccount.getFRAUD_FTP_PATH();
                password      = gatewayAccount.getFRAUD_FTP_PASSWORD();
            }

            if (isTest){
                requestURL = RB.getString("TEST_RECURRING_URL");
            }else {
                requestURL = RB.getString("LIVE_RECURRING_URL");
            }

            String request = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+card+"</card>" +
                    "<cvv2>"+cvv2+"</cvv2>" +
                    "<expYear>"+expYear+"</expYear>" +
                    "<expMonth>"+expMonth+"</expMonth>" +
                    "<member>"+member+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+email+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "<paymenttype>"+paymenttype+"</paymenttype>" +
                    "<paymentmethod>"+paymentmethod+"</paymentmethod>" +
                    "<subscriptiontype>"+subscriptiontype+"</subscriptiontype>" +
                    "<paymentcycle>"+paymentcycle+"</paymentcycle>" +
                    "<paymentdays>"+paymentdays+"</paymentdays>" +
                    "<noOfRecurringpayments>"+noOfRecurringpayments+"</noOfRecurringpayments>" +
                    "<paymentstartdate>"+paymentstartdate+"</paymentstartdate>" +
                    "<recurringpaymentamount>"+recurringpaymentamount+"</recurringpaymentamount>" +
                    "</request>";

            transactionlogger.error("commMerchantVO.getIsService()5 " +commMerchantVO.getIsService());

            String requestLOG = "<request>" +
                    "<terminalid>"+terminalid+"</terminalid>" +
                    "<password>"+password+"</password>" +
                    "<action>"+action+"</action>" +
                    "<card>"+functions.maskingPan(card)+"</card>" +
                    "<cvv2>"+functions.maskingNumber(cvv2)+"</cvv2>" +
                    "<expYear>"+functions.maskingNumber(expYear)+"</expYear>" +
                    "<expMonth>"+functions.maskingNumber(expMonth)+"</expMonth>" +
                    "<member>"+functions.maskingFirstName(commAddressDetailsVO.getFirstname())+ " " + functions.maskingLastName(commAddressDetailsVO.getLastname())+"</member>" +
                    "<currencyCode>"+currencyCode+"</currencyCode>" +
                    "<address>"+address+"</address>" +
                    "<city>"+city+"</city>" +
                    "<statecode>"+statecode+"</statecode>" +
                    "<zip>"+zip+"</zip>" +
                    "<CountryCode>"+countryCode+"</CountryCode>" +
                    "<email>"+functions.getEmailMasking(email)+"</email>" +
                    "<amount>"+amount+"</amount>" +
                    "<trackid>"+trackid+"</trackid>" +
                    "<udf1>"+udf1+"</udf1>" +
                    "<paymenttype>"+paymenttype+"</paymenttype>" +
                    "<paymentmethod>"+paymentmethod+"</paymentmethod>" +
                    "<subscriptiontype>"+subscriptiontype+"</subscriptiontype>" +
                    "<paymentcycle>"+paymentcycle+"</paymentcycle>" +
                    "<paymentdays>"+paymentdays+"</paymentdays>" +
                    "<noOfRecurringpayments>"+noOfRecurringpayments+"</noOfRecurringpayments>" +
                    "<paymentstartdate>"+paymentstartdate+"</paymentstartdate>" +
                    "<recurringpaymentamount>"+recurringpaymentamount+"</recurringpaymentamount>" +
                    "</request>";

            transactionlogger.error("processRecurring() Request: " +trackingId+ " " + " RequestURL:"+ requestURL + " RequestParameters:"+ requestLOG);

            response = XceptsUtils.doPostHTTPUrlConnection(requestURL, request);
            transactionlogger.error("processRecurring() Response: " + trackingId + " " + response);

            xmlReadedResponse = XceptsUtils.readXMLResponse(response);
            transactionlogger.error("processRecurring() xmlReadedResponse: " + trackingId + " " + xmlReadedResponse);

            HashMap<String,String> requestHM = new HashMap<>();
            String result         = "";
            String responsecode   = "";
            String authcode       = "";
            String RRN            = "";
            String ECI            = "";
            String tranid         = "";
            String Rtrackid       = "";
            String Rterminalid    = "";
            String threedreason   = "";
            String Ramount        = "";
            String targetUrl      = "";
            String payId          = "";
            String Rcurrency      = "";
            String signature      = "";
            String subscriptionId = "";
            String Rudf1 = "";
            String Rudf2 = "";
            String Rudf3 = "";
            String Rudf4 = "";
            String Rudf5 = "";
            String billingDescriptor            = "";
            String dynamic_billing_descriptor   = "";

            if (xmlReadedResponse != null)
            {
                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("responsecode") && functions.isValueNull(xmlReadedResponse.get("responsecode")))
                    responsecode = xmlReadedResponse.get("responsecode");

                if (xmlReadedResponse.containsKey("authcode") && functions.isValueNull(xmlReadedResponse.get("authcode")))
                    authcode = xmlReadedResponse.get("authcode");

                if (xmlReadedResponse.containsKey("RRN") && functions.isValueNull(xmlReadedResponse.get("RRN")))
                    RRN = xmlReadedResponse.get("RRN");

                if (xmlReadedResponse.containsKey("ECI") && functions.isValueNull(xmlReadedResponse.get("ECI")))
                    ECI = xmlReadedResponse.get("ECI");

                if (xmlReadedResponse.containsKey("tranid") && functions.isValueNull(xmlReadedResponse.get("tranid")))
                    tranid = xmlReadedResponse.get("tranid");
                else if (xmlReadedResponse.containsKey("transid") && functions.isValueNull(xmlReadedResponse.get("transid")))
                    tranid = xmlReadedResponse.get("transid");

                if (xmlReadedResponse.containsKey("trackid") && functions.isValueNull(xmlReadedResponse.get("trackid")))
                    Rtrackid = xmlReadedResponse.get("trackid");

                if (xmlReadedResponse.containsKey("result") && functions.isValueNull(xmlReadedResponse.get("result")))
                    result = xmlReadedResponse.get("result");

                if (xmlReadedResponse.containsKey("terminalid") && functions.isValueNull(xmlReadedResponse.get("terminalid")))
                    Rterminalid = xmlReadedResponse.get("terminalid");

                if (xmlReadedResponse.containsKey("threedreason") && functions.isValueNull(xmlReadedResponse.get("threedreason")))
                    threedreason = xmlReadedResponse.get("threedreason");

                if (xmlReadedResponse.containsKey("amount") && functions.isValueNull(xmlReadedResponse.get("amount")))
                    Ramount = xmlReadedResponse.get("amount");

                if (xmlReadedResponse.containsKey("targetUrl") && functions.isValueNull(xmlReadedResponse.get("targetUrl")))
                {
                    targetUrl = xmlReadedResponse.get("targetUrl");
                    requestHM.put("targetUrl",xmlReadedResponse.get("targetUrl"));
                }

                if (xmlReadedResponse.containsKey("payId") && functions.isValueNull(xmlReadedResponse.get("payId")))
                {
                    payId = xmlReadedResponse.get("payId");
                    requestHM.put("payId",xmlReadedResponse.get("payId"));
                }

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("dynamic_billing_descriptor") && functions.isValueNull(xmlReadedResponse.get("dynamic_billing_descriptor")))
                    dynamic_billing_descriptor = xmlReadedResponse.get("dynamic_billing_descriptor");

                if (xmlReadedResponse.containsKey("currency") && functions.isValueNull(xmlReadedResponse.get("currency")))
                    Rcurrency = xmlReadedResponse.get("currency");

                if (xmlReadedResponse.containsKey("billingDescriptor") && functions.isValueNull(xmlReadedResponse.get("billingDescriptor")))
                    billingDescriptor = xmlReadedResponse.get("billingDescriptor");

                if (xmlReadedResponse.containsKey("signature") && functions.isValueNull(xmlReadedResponse.get("signature")))
                    signature = xmlReadedResponse.get("signature");

                if (xmlReadedResponse.containsKey("subscriptionId") && functions.isValueNull(xmlReadedResponse.get("subscriptionId")))
                    subscriptionId = xmlReadedResponse.get("subscriptionId");

                if (xmlReadedResponse.containsKey("udf1") && functions.isValueNull(xmlReadedResponse.get("udf1")))
                    Rudf1 = xmlReadedResponse.get("udf1");

                if (xmlReadedResponse.containsKey("udf2") && functions.isValueNull(xmlReadedResponse.get("udf2")))
                    Rudf2 = xmlReadedResponse.get("udf2");

                if (xmlReadedResponse.containsKey("udf3") && functions.isValueNull(xmlReadedResponse.get("udf3")))
                    Rudf3 = xmlReadedResponse.get("udf3");

                if (xmlReadedResponse.containsKey("udf4") && functions.isValueNull(xmlReadedResponse.get("udf4")))
                    Rudf4 = xmlReadedResponse.get("udf4");

                if (xmlReadedResponse.containsKey("udf5") && functions.isValueNull(xmlReadedResponse.get("udf5")))
                    Rudf5 = xmlReadedResponse.get("udf5");


                if (functions.isValueNull(result) && ("Successful".equalsIgnoreCase(result) || "SUCCESS".equalsIgnoreCase(result)))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setTransactionId(tranid);
                    comm3DResponseVO.setThreeDVersion("3Dv1");
                    if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }

                    if (functions.isValueNull(billingDescriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                    else if (functions.isValueNull(dynamic_billing_descriptor))
                        comm3DResponseVO.setDescriptor(billingDescriptor);
                }
                else if (functions.isValueNull(result) && "Unsuccessful".equalsIgnoreCase(result))
                {
                    comm3DResponseVO.setStatus("Failed");
                    comm3DResponseVO.setTransactionStatus("Failed");
                    if (functions.isValueNull(threedreason))
                    {
                        comm3DResponseVO.setRemark(threedreason);
                        comm3DResponseVO.setDescription(threedreason);
                    }
                    else if (functions.isValueNull(Rudf5))
                    {
                        comm3DResponseVO.setRemark(Rudf5);
                        comm3DResponseVO.setDescription(Rudf5);
                    }
                    else
                    {
                        comm3DResponseVO.setRemark(result);
                        comm3DResponseVO.setDescription(result);
                    }
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }

                comm3DResponseVO.setMerchantId(terminalid);
                comm3DResponseVO.setTransactionId(tranid);
                comm3DResponseVO.setThreeDVersion("3Dv1");
                comm3DResponseVO.setAmount(Ramount);
                comm3DResponseVO.setCurrency(Rcurrency);
                comm3DResponseVO.setAuthCode(authcode);
                comm3DResponseVO.setBankCode(responsecode);
                comm3DResponseVO.setBankDescription(Rudf5);
                comm3DResponseVO.setBankRefNo(Rtrackid);
                comm3DResponseVO.setRrn(RRN);
                comm3DResponseVO.setEci(ECI);
                comm3DResponseVO.setResponseHashInfo(subscriptionId);
                XceptsUtils.updateMainTableEntry(tranid,"",trackingId);
            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            }
        }
        catch (Exception e)
        {
            transactionlogger.error(trackingId+" Exception >>>" + e);
            PZExceptionHandler.raiseTechnicalViolationException(XceptsPaymentGateway.class.getName(), "processRecurring()", null, "common", "Technical Exception while cancelling transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }
    
}

package com.payment.ems.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.endeavourmpi.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Admin on 1/24/2018.
 */
public class EMSPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "newems";
    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.ems");
    private static TransactionLogger transactionlogger = new TransactionLogger(EMSPaymentGateway.class.getName());
    private static String url = "https://test.ipg-online.com:443/ipgapi/services";
    Functions functions= new Functions();

    public EMSPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        try
        {

            /*EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
            ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
            paresDecodeRequestVO.setMassageID("68793");
            paresDecodeRequestVO.setPares("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");
            paresDecodeRequestVO.setTrackid("68793");


            ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);*/

            String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "<SOAP-ENV:Header />\n" +
                    "<SOAP-ENV:Body>\n" +
                    "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                    "<v1:Transaction>\n" +
                    "<v1:CreditCardTxType>\n" +
                    "<v1:StoreId>230995073</v1:StoreId>\n" +
                    "<v1:Type>sale</v1:Type>\n" +
                    "</v1:CreditCardTxType>\n" +
                    "<v1:CreditCardData>\n" +
                    "<v1:CardNumber>4018490000000013</v1:CardNumber>\n" +//5453010000070789
                    "<v1:ExpMonth>12</v1:ExpMonth>\n" + //Dec = 12
                    "<v1:ExpYear>30</v1:ExpYear>\n" + //2020 = 20
                    "<v1:CardCodeValue>123</v1:CardCodeValue>\n" +
                    "<v1:Brand>VISA</v1:Brand>\n" +
                    "</v1:CreditCardData>\n" +
                    "<v1:CreditCard3DSecure>\n" +
                    "<v1:VerificationResponse>Y</v1:VerificationResponse>\n" +
                    "<v1:PayerAuthenticationResponse>Y</v1:PayerAuthenticationResponse>\n" +
                    "<v1:AuthenticationValue>AAACB4NlgBgkBxAAOWWAAAAAAAA=</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                    "<v1:XID>ZmZkMWY2ODVkYjQyNDFkOGJhNjI=</v1:XID>\n" +
                    "</v1:CreditCard3DSecure>\n" +
                    "<v1:Payment>\n" +
                    "<v1:ChargeTotal>1.00</v1:ChargeTotal>\n" +
                    "<v1:Currency>978</v1:Currency>\n" +
                    "</v1:Payment>\n" +
                    "<v1:TransactionDetails>\n" +
                    "<v1:MerchantTransactionId>7887687</v1:MerchantTransactionId>\n" +
                    "</v1:TransactionDetails>\n" +
                    "<v1:Billing>\n" +
                    "<v1:Name>Thomas Jefferson</v1:Name>\n" +
                    "<v1:CustomerID>76786UY</v1:CustomerID>\n" +
                    "<v1:Address1>Malad</v1:Address1>\n" +
                    "<v1:City>Mumbai</v1:City>\n" +
                    "<v1:State>MH</v1:State>\n" +
                    "<v1:Zip>400064</v1:Zip>\n" +
                    "<v1:Country>IN</v1:Country>\n" +
                    "<v1:Phone>987986875645</v1:Phone>\n" +
                    "<v1:Email>test@gmail.com</v1:Email>\n" +
                    "</v1:Billing>\n" +
                    "</v1:Transaction>\n" +
                    "</ipgapi:IPGApiOrderRequest>\n" +
                    "</SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>\n";

            String userID = "WS230995073._.1";
            String password = "KcB5b2\">Qw";
            String credentials = userID +":"+ password;
            transactionlogger.error("cred---"+credentials);

            String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

            String queryResponse = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest,"2750",true);
            System.out.println("Confirmation Response---"+queryResponse);

        }
        catch (Exception e)
        {
            transactionlogger.error("Exception...", e);
        }

    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionlogger.error("Entering processAuth of EMS...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EMSResponseVO commResponseVO = new EMSResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();

        EMSUtils emsUtils = new EMSUtils();

        String reject3DCard = commRequestVO.getReject3DCard();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String is3dSupported = GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";
        String MpiMid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //"Test";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        String cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        String termUrl = "";
        transactionlogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionlogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionlogger.error("from RB----"+termUrl);
        }

        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        String customerid = commRequestVO.getCustomerId();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try
        {
            if ("Y".equals(is3dSupported))
            {
                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                enrollmentRequestVO.setMid(MpiMid);
                enrollmentRequestVO.setName(customerName);
                enrollmentRequestVO.setPan(cardNo);
                enrollmentRequestVO.setExpiry(emsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()));
                enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));
                enrollmentRequestVO.setAmount(emsUtils.getCentAmount(genericTransDetailsVO.getAmount()));
                enrollmentRequestVO.setDesc(genericTransDetailsVO.getOrderDesc());
                enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                enrollmentRequestVO.setAccept("en-us");
                enrollmentRequestVO.setTrackid(trackingID);

                EnrollmentResponseVO enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                String result = enrollmentResponseVO.getResult();
                String avr = enrollmentResponseVO.getAvr();

                if ("Enrolled".equals(result) && "Y".equals(avr))
                {
                    if ("Y".equals(reject3DCard))
                    {
                        transactionlogger.error("rejecting 3d card as per configuration ");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("3D Enrolled Card");
                        commResponseVO.setRemark("3D Enrolled Card");
                        commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
                        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
                        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
                        return commResponseVO;
                    }
                    else
                    {
                        transactionlogger.error("3D:card enrolled flow");
                        String PAReq = enrollmentResponseVO.getPAReq();
                        String acsUrl = enrollmentResponseVO.getAcsUrl();
                        String trackId = enrollmentResponseVO.getTrackId();
                        acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");

                        String status = "pending3DConfirmation";
                        commResponseVO.setPaReq(PAReq);
                        commResponseVO.setUrlFor3DRedirect(acsUrl);
                        commResponseVO.setRedirectMethod("POST");
                        commResponseVO.setTerURL(termUrl+trackId);
                        commResponseVO.setMd(customerid+"@"+ PzEncryptor.encryptCVV(cvv));
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescriptor(displayName);
                        commResponseVO.setTransactionType("Auth");
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setDescription("Card Not Enrolled For 3D");
                    commResponseVO.setRemark(result);
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
            }
            else
            {
                String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "<SOAP-ENV:Header />\n" +
                        "<SOAP-ENV:Body>\n" +
                        "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                        "<v1:Transaction>\n" +
                        "<v1:CreditCardTxType>\n" +
                        "<v1:StoreId>" + storeId + "</v1:StoreId>\n" +
                        "<v1:Type>preAuth</v1:Type>\n" +
                        "</v1:CreditCardTxType>\n" +
                        "<v1:CreditCardData>\n" +
                        "<v1:CardNumber>" + cardNo + "</v1:CardNumber>\n" +
                        "<v1:ExpMonth>" + expMonth + "</v1:ExpMonth>\n" + //Dec = 12
                        "<v1:ExpYear>" + expYear + "</v1:ExpYear>\n" + //2020 = 20
                        "<v1:CardCodeValue>" + cvv + "</v1:CardCodeValue>\n" +
                        "<v1:Brand>" + EMSUtils.getCardType(genericCardDetailsVO.getCardType()) + "</v1:Brand>\n" +
                        "</v1:CreditCardData>\n" +
                        "<v1:Payment>\n" +
                        "<v1:ChargeTotal>" + amount + "</v1:ChargeTotal>\n" +
                        "<v1:Currency>" + String.valueOf(currencyId) + "</v1:Currency>\n" +
                        "</v1:Payment>\n" +
                        "<v1:TransactionDetails>\n" +
                        "<v1:MerchantTransactionId>" + trackingID + "</v1:MerchantTransactionId>\n" +
                        "</v1:TransactionDetails>\n" +
                        "<v1:Billing>\n" +
                        "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                        "<v1:Name>" + customerName + "</v1:Name>\n" +
                        "<v1:Address1>" + address1 + "</v1:Address1>\n" +
                        "<v1:City>" + city + "</v1:City>\n" +
                        "<v1:State>" + state + "</v1:State>\n" +
                        "<v1:Zip>" + zip + "</v1:Zip>\n" +
                        "<v1:Country>" + country + "</v1:Country>\n" +
                        "<v1:Phone>" + phone + "</v1:Phone>\n" +
                        "<v1:Email>" + email + "</v1:Email>\n" +
                        "</v1:Billing>\n" +
                        "</v1:Transaction>\n" +
                        "</ipgapi:IPGApiOrderRequest>\n" +
                        "</SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>\n";

                String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "<SOAP-ENV:Header />\n" +
                        "<SOAP-ENV:Body>\n" +
                        "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                        "<v1:Transaction>\n" +
                        "<v1:CreditCardTxType>\n" +
                        "<v1:StoreId>" + storeId + "</v1:StoreId>\n" +
                        "<v1:Type>preAuth</v1:Type>\n" +
                        "</v1:CreditCardTxType>\n" +
                        "<v1:CreditCardData>\n" +
                        "<v1:CardNumber>" + functions.maskingPan(cardNo) + "</v1:CardNumber>\n" +
                        "<v1:ExpMonth>" + functions.maskingNumber(expMonth) + "</v1:ExpMonth>\n" + //Dec = 12
                        "<v1:ExpYear>" + functions.maskingNumber(expYear) + "</v1:ExpYear>\n" + //2020 = 20
                        "<v1:CardCodeValue>" + functions.maskingNumber(cvv) + "</v1:CardCodeValue>\n" +
                        "<v1:Brand>" + EMSUtils.getCardType(genericCardDetailsVO.getCardType()) + "</v1:Brand>\n" +
                        "</v1:CreditCardData>\n" +
                        "<v1:Payment>\n" +
                        "<v1:ChargeTotal>" + amount + "</v1:ChargeTotal>\n" +
                        "<v1:Currency>" + String.valueOf(currencyId) + "</v1:Currency>\n" +
                        "</v1:Payment>\n" +
                        "<v1:TransactionDetails>\n" +
                        "<v1:MerchantTransactionId>" + trackingID + "</v1:MerchantTransactionId>\n" +
                        "</v1:TransactionDetails>\n" +
                        "<v1:Billing>\n" +
                        "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                        "<v1:Name>" + customerName + "</v1:Name>\n" +
                        "<v1:Address1>" + address1 + "</v1:Address1>\n" +
                        "<v1:City>" + city + "</v1:City>\n" +
                        "<v1:State>" + state + "</v1:State>\n" +
                        "<v1:Zip>" + zip + "</v1:Zip>\n" +
                        "<v1:Country>" + country + "</v1:Country>\n" +
                        "<v1:Phone>" + phone + "</v1:Phone>\n" +
                        "<v1:Email>" + email + "</v1:Email>\n" +
                        "</v1:Billing>\n" +
                        "</v1:Transaction>\n" +
                        "</ipgapi:IPGApiOrderRequest>\n" +
                        "</SOAP-ENV:Body>\n" +
                        "</SOAP-ENV:Envelope>\n";

                transactionlogger.error("Request EMS---" +trackingID + "--" +  xmlRequestlog);

                String credentials = userID + ":" + password;
                transactionlogger.error("cred EMS---" + trackingID + "--" + credentials);

                String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

                String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
                transactionlogger.error("Response EMS---" + trackingID + "--" + response);

                commResponseVO = emsUtils.readSoapResponse(response);
            }
            commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException:::::" + e);
            PZExceptionHandler.raiseTechnicalViolationException(EMSPaymentGateway.class.getName(), "processAuth()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        } return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionlogger.error("Entering processSale of EMS...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EMSResponseVO commResponseVO = new EMSResponseVO();

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        EndeavourMPIGateway endeavourMPIGateway = new EndeavourMPIGateway();
        Functions functions = new Functions();

        EMSUtils emsUtils = new EMSUtils();

        String termUrl = "";
        transactionlogger.error("host url----"+commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionlogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL_3DS");
            transactionlogger.error("from RB----"+termUrl);
        }

        String reject3DCard = commRequestVO.getReject3DCard();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String is3dSupported = GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";
        String MpiMid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //"Test";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        String cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();


        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        String customerid = commRequestVO.getCustomerId();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        try
        {
            if(functions.isValueNull(genericTransDetailsVO.getEci()) && functions.isValueNull(genericTransDetailsVO.getXid()) && functions.isValueNull(genericTransDetailsVO.getVerificationId()))
            {
                transactionlogger.error("eci EMS---" + genericTransDetailsVO.getEci());
                transactionlogger.error("xid EMS---"+genericTransDetailsVO.getXid());
                transactionlogger.error("cavv EMS---"+genericTransDetailsVO.getVerificationId());

                String confirmationReq = "";
                confirmationReq = emsUtils.direct3DsConfirmationRequest(commRequestVO,trackingID,storeId);

                transactionlogger.error("Direct3DsConfirmation EMS---" + confirmationReq);

                String credentials = userID + ":" + password;
                transactionlogger.error("cred EMS---" + credentials);

                String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

                String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, confirmationReq, accountId, isTest);
                transactionlogger.error("Direct3DsConfirmation EMS---" + response);

                commResponseVO = emsUtils.readSoapResponse(response);
                commResponseVO.setEci(genericTransDetailsVO.getEci());
                commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
                commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
                return commResponseVO;

            }
            else
            {
                if ("Y".equals(is3dSupported))
                {
                    EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                    enrollmentRequestVO.setMid(MpiMid);
                    enrollmentRequestVO.setName(customerName);
                    enrollmentRequestVO.setPan(cardNo);
                    enrollmentRequestVO.setExpiry(emsUtils.getCardExpiry(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()));
                    enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));
                    enrollmentRequestVO.setAmount(emsUtils.getCentAmount(genericTransDetailsVO.getAmount()));
                    enrollmentRequestVO.setDesc(genericTransDetailsVO.getOrderDesc());
                    enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                    enrollmentRequestVO.setAccept("en-us");
                    enrollmentRequestVO.setTrackid(trackingID);

                    EnrollmentResponseVO enrollmentResponseVO = endeavourMPIGateway.processEnrollment(enrollmentRequestVO);

                    String result = enrollmentResponseVO.getResult();
                    String avr = enrollmentResponseVO.getAvr();

                    transactionlogger.debug("reject3DCard-----" + reject3DCard);

                    if ("Enrolled".equals(result) && "Y".equals(avr))
                    {
                        if ("Y".equals(reject3DCard))
                        {
                            transactionlogger.error("rejecting 3d card as per configuration ");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }
                        else
                        {
                            transactionlogger.error("3D:card enrolled flow");
                            String PAReq = enrollmentResponseVO.getPAReq();
                            String acsUrl = enrollmentResponseVO.getAcsUrl();
                            String trackId = enrollmentResponseVO.getTrackId();
                            acsUrl = java.net.URLDecoder.decode(acsUrl, "UTF-8");

                            String status = "pending3DConfirmation";
                            commResponseVO.setPaReq(PAReq);
                            commResponseVO.setUrlFor3DRedirect(acsUrl);
                            commResponseVO.setRedirectMethod("POST");
                            commResponseVO.setTerURL(termUrl+trackId);
                            commResponseVO.setMd(customerid + "@" + PzEncryptor.encryptCVV(cvv));
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setTransactionType("Sale");
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("Card Not Enrolled For 3D");
                        commResponseVO.setRemark(result);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                else
                {

                    String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<SOAP-ENV:Header />\n" +
                            "<SOAP-ENV:Body>\n" +
                            "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                            "<v1:Transaction>\n" +
                            "<v1:CreditCardTxType>\n" +
                            "<v1:StoreId>" + storeId + "</v1:StoreId>\n" +
                            "<v1:Type>sale</v1:Type>\n" +
                            "</v1:CreditCardTxType>\n" +
                            "<v1:CreditCardData>\n" +
                            "<v1:CardNumber>" + cardNo + "</v1:CardNumber>\n" +
                            "<v1:ExpMonth>" + expMonth + "</v1:ExpMonth>\n" + //Dec = 12
                            "<v1:ExpYear>" + expYear + "</v1:ExpYear>\n" + //2020 = 20
                            "<v1:CardCodeValue>" + cvv + "</v1:CardCodeValue>\n" +
                            "<v1:Brand>" + EMSUtils.getCardType(genericCardDetailsVO.getCardType()) + "</v1:Brand>\n" +
                            "</v1:CreditCardData>\n" +
                            "<v1:Payment>\n" +
                            "<v1:ChargeTotal>" + amount + "</v1:ChargeTotal>\n" +
                            "<v1:Currency>" + String.valueOf(currencyId) + "</v1:Currency>\n" +
                            "</v1:Payment>\n" +
                            "<v1:TransactionDetails>\n" +
                            "<v1:MerchantTransactionId>" + trackingID + "</v1:MerchantTransactionId>\n" +
                            "</v1:TransactionDetails>\n" +
                            "<v1:Billing>\n" +
                            "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                            "<v1:Name>" + customerName + "</v1:Name>\n" +
                            "<v1:Address1>" + address1 + "</v1:Address1>\n" +
                            "<v1:City>" + city + "</v1:City>\n" +
                            "<v1:State>" + state + "</v1:State>\n" +
                            "<v1:Zip>" + zip + "</v1:Zip>\n" +
                            "<v1:Country>" + country + "</v1:Country>\n" +
                            "<v1:Phone>" + phone + "</v1:Phone>\n" +
                            "<v1:Email>" + email + "</v1:Email>\n" +
                            "</v1:Billing>\n" +
                            "</v1:Transaction>\n" +
                            "</ipgapi:IPGApiOrderRequest>\n" +
                            "</SOAP-ENV:Body>\n" +
                            "</SOAP-ENV:Envelope>\n";

                    String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                            "<SOAP-ENV:Header />\n" +
                            "<SOAP-ENV:Body>\n" +
                            "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                            "<v1:Transaction>\n" +
                            "<v1:CreditCardTxType>\n" +
                            "<v1:StoreId>" + storeId + "</v1:StoreId>\n" +
                            "<v1:Type>sale</v1:Type>\n" +
                            "</v1:CreditCardTxType>\n" +
                            "<v1:CreditCardData>\n" +
                            "<v1:CardNumber>" + functions.maskingPan(cardNo) + "</v1:CardNumber>\n" +
                            "<v1:ExpMonth>" + functions.maskingNumber(expMonth) + "</v1:ExpMonth>\n" + //Dec = 12
                            "<v1:ExpYear>" + functions.maskingNumber(expYear) + "</v1:ExpYear>\n" + //2020 = 20
                            "<v1:CardCodeValue>" + functions.maskingNumber(cvv) + "</v1:CardCodeValue>\n" +
                            "<v1:Brand>" + EMSUtils.getCardType(genericCardDetailsVO.getCardType()) + "</v1:Brand>\n" +
                            "</v1:CreditCardData>\n" +
                            "<v1:Payment>\n" +
                            "<v1:ChargeTotal>" + amount + "</v1:ChargeTotal>\n" +
                            "<v1:Currency>" + String.valueOf(currencyId) + "</v1:Currency>\n" +
                            "</v1:Payment>\n" +
                            "<v1:TransactionDetails>\n" +
                            "<v1:MerchantTransactionId>" + trackingID + "</v1:MerchantTransactionId>\n" +
                            "</v1:TransactionDetails>\n" +
                            "<v1:Billing>\n" +
                            "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                            "<v1:Name>" + customerName + "</v1:Name>\n" +
                            "<v1:Address1>" + address1 + "</v1:Address1>\n" +
                            "<v1:City>" + city + "</v1:City>\n" +
                            "<v1:State>" + state + "</v1:State>\n" +
                            "<v1:Zip>" + zip + "</v1:Zip>\n" +
                            "<v1:Country>" + country + "</v1:Country>\n" +
                            "<v1:Phone>" + phone + "</v1:Phone>\n" +
                            "<v1:Email>" + email + "</v1:Email>\n" +
                            "</v1:Billing>\n" +
                            "</v1:Transaction>\n" +
                            "</ipgapi:IPGApiOrderRequest>\n" +
                            "</SOAP-ENV:Body>\n" +
                            "</SOAP-ENV:Envelope>\n";
                    transactionlogger.error("Request EMS---" + trackingID + "--" + xmlRequestlog);

                    String credentials = userID + ":" + password;
                    transactionlogger.error("cred EMS---" + trackingID + "--" + credentials);

                    String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

                    String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
                    transactionlogger.error("Response EMS---" + trackingID + "--" + response);

                    commResponseVO = emsUtils.readSoapResponse(response);
                }

            }
            commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        }catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException:::::" + e);
            PZExceptionHandler.raiseTechnicalViolationException(EMSPaymentGateway.class.getName(), "processAuth()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionlogger.debug("Enter in processRefund of EMS");

        EMSUtils emsUtils = new EMSUtils();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String amount = commTransactionDetailsVO.getAmount();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));


        String rRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:Type>return</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+currencyId+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:OrderId>"+commTransactionDetailsVO.getPreviousTransactionId()+"</v1:OrderId>\n"+
                "</v1:TransactionDetails>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n"+
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" +trackingID + "--" +  rRequest);

        String credentials = userID +":"+ password;
        transactionlogger.error("cred EMS---" + trackingID + "--" + credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, rRequest,accountId,isTest);
        transactionlogger.error("Response EMS---" + trackingID + "--" + response);

        commResponseVO = emsUtils.readSoapResponse(response);

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionlogger.debug("Enter in processCapture of EMS");

        EMSUtils emsUtils = new EMSUtils();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String amount = commTransactionDetailsVO.getAmount();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));


        String cRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:Type>postAuth</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+currencyId+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:OrderId>"+commTransactionDetailsVO.getPreviousTransactionId()+"</v1:OrderId>\n"+
                "</v1:TransactionDetails>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n"+
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" + trackingID + "--" + cRequest);

        String credentials = userID +":"+ password;
        transactionlogger.error("cred EMS---" +trackingID + "--" +  credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, cRequest,accountId,isTest);
        transactionlogger.error("Response EMS---" +trackingID + "--" +  response);

        commResponseVO = emsUtils.readSoapResponse(response);

        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionlogger.debug("Enter in processVoid of EMS");

        EMSUtils emsUtils = new EMSUtils();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String amount = commTransactionDetailsVO.getAmount();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));


        String vRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:Type>void</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+currencyId+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:OrderId>"+commTransactionDetailsVO.getPreviousTransactionId()+"</v1:OrderId>\n"+
                "</v1:TransactionDetails>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n"+
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" + trackingID + "--" + vRequest);

        String credentials = userID +":"+ password;
        transactionlogger.error("cred EMS---" + trackingID + "--" + credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, vRequest,accountId,isTest);
        transactionlogger.error("Response EMS---" +trackingID + "--" +  response);

        commResponseVO = emsUtils.readSoapResponse(response);

        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = reqVO.getCardDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();

        transactionlogger.debug("Enter in processPayout of EMS");

        EMSUtils emsUtils = new EMSUtils();

        String descriptor = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String accountid = GatewayAccountService.getGatewayAccount(accountId).toString();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String cardNo = commCardDetailsVO.getCardNum();
        String expMonth = commCardDetailsVO.getExpMonth();
        //String expYear = commCardDetailsVO.getExpYear();
        String expYear = commCardDetailsVO.getExpYear().substring(commCardDetailsVO.getExpYear().length() - 2);

        String amount = commTransactionDetailsVO.getAmount();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));


        String pRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:Type>credit</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "</v1:CreditCardData>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+currencyId+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n"+
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        String pRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:Type>credit</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+functions.maskingPan(cardNo)+"</v1:CardNumber>\n" +
                "<v1:ExpMonth>"+functions.maskingNumber(expMonth)+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+functions.maskingNumber(expYear)+"</v1:ExpYear>\n" + //2020 = 20
                "</v1:CreditCardData>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+currencyId+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n"+
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" + trackingID + "--" + pRequestlog);

        String credentials = userID +":"+ password;
        transactionlogger.error("cred EMS---" + trackingID + "--" + credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, pRequest,accountId,isTest);
        transactionlogger.error("Response EMS---" + response);

        commResponseVO = emsUtils.readSoapResponse(response);

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Enter's into proccessQuery---");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        EMSResponseVO commResponseVO=null;
        EMSUtils emsUtils = new EMSUtils();
        Functions functions= new Functions();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";
        String mid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId(); //"KcB5b2\">Qw";
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String credentials = userID +":"+ password;
        transactionlogger.error("cred EMS---" + credentials);
        String orderId=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String queryRequest="";

        if (functions.isValueNull(orderId))
        {
            queryRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "<SOAP-ENV:Header />\n" +
                    "<SOAP-ENV:Body>\n" +
                    "<ipgapi:IPGApiActionRequest xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\" xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\" xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\">\n" +
                    "<a1:Action>\n"+
                    "<a1:InquiryOrder>\n" +
                    "<a1:OrderId>"+orderId+"</a1:OrderId>\n"+
                    "</a1:InquiryOrder>\n" +
                    "</a1:Action>\n"+
                    "</ipgapi:IPGApiActionRequest>\n"+
                    "</SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>\n";

        }else{

            queryRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\" xmlns:a1=\"http://ipg-online.com/ipgapi/schemas/a1\" xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\">\n" +
                    "<SOAP-ENV:Header />\n" +
                    "<SOAP-ENV:Body>\n" +
                    "<ipgapi:IPGApiActionRequest>\n" +
                    "<a1:Action>\n" +
                    "<a1:InquiryTransaction>\n" +
                    "<a1:MerchantTransactionId>"+trackingID+"</a1:MerchantTransactionId>\n" +
                    "</a1:InquiryTransaction>\n" +
                    "</a1:Action>\n" +
                    "</ipgapi:IPGApiActionRequest>\n" +
                    "</SOAP-ENV:Body>\n" +
                    "</SOAP-ENV:Envelope>\n";


        }
        transactionlogger.error("queryRequest-----"+trackingID + "--" + queryRequest);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String queryResponse = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, queryRequest,accountId,isTest);

        transactionlogger.error("queryResponse-----"+trackingID + "--" + queryResponse);

        commResponseVO = emsUtils.readSoapResponse(queryResponse.replaceAll("\\n","").trim().toString());

        commResponseVO.setMerchantId(mid);
        commResponseVO.setAmount(commRequestVO.getTransDetailsVO().getAmount());
        commResponseVO.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
        commResponseVO.setResponseTime(commRequestVO.getTransDetailsVO().getResponsetime());
        commResponseVO.setDescription(commRequestVO.getTransDetailsVO().getOrderDesc());

        return commResponseVO;
    }

    public GenericResponseVO processCommon3DAuthConfirmation(String trackingid, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Entering processCommon3DAuthConfirmation of EMS...");

        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        EMSResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String cvv="";
        String customerid="";
       if(functions.isValueNull(commRequestVO.getMd())){
           String MD=commRequestVO.getMd();
           String data[]=MD.split("@");
           customerid=data[0];
           cvv= PzEncryptor.decryptCVV(data[1]);
            genericCardDetailsVO.setcVV(cvv);
             commRequestVO.setCustomerId(customerid);
         }

        EMSUtils emsUtils = new EMSUtils();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
         cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        customerid = commRequestVO.getCustomerId();
        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingid);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingid);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getSignature())){
            Sign=paresDecodeResponseVO.getSignature();
            if("Valid".equalsIgnoreCase(Sign)){
                paRes="Y";
            }else {
                paRes="N";
            }
        }
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>preAuth</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+cvv+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>preAuth</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+functions.maskingPan(cardNo)+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+functions.maskingNumber(expMonth)+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+functions.maskingNumber(expYear)+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+functions.maskingNumber(cvv)+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" + trackingid + "--" + xmlRequestlog);

        String credentials = userID + ":" + password;
        transactionlogger.error("cred EMS---" + trackingid + "--" + credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
        transactionlogger.error("Response EMS---" +trackingid + "--" +  response);

        commResponseVO = emsUtils.readSoapResponse(response);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        return commResponseVO;
    }

    public GenericResponseVO process3DAuthConfirmation(String trackingid, GenericRequestVO requestVO,String PaRes) throws PZTechnicalViolationException
    {
        transactionlogger.error("Entering process3DAuthConfirmation of EMS...");

        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EMSResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        EMSUtils emsUtils = new EMSUtils();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        String cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        String customerid = commRequestVO.getCustomerId();
        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingid);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setTrackid(trackingid);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getSignature())){
            Sign=paresDecodeResponseVO.getSignature();
            if("Valid".equalsIgnoreCase(Sign)){
                paRes="Y";
            }else {
                paRes="N";
            }
        }
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>preAuth</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+cvv+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>preAuth</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+functions.maskingPan(cardNo)+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+functions.maskingNumber(expMonth)+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+functions.maskingNumber(expYear)+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+functions.maskingNumber(cvv)+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" + trackingid + "--" + xmlRequestlog);

        String credentials = userID + ":" + password;
        transactionlogger.error("cred EMS---" +trackingid + "--" +  credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
        transactionlogger.error("Response EMS---" +trackingid + "--" +  response);

        commResponseVO = emsUtils.readSoapResponse(response);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());

        return commResponseVO;
    }

    public GenericResponseVO process3DSaleConfirmation(String trackingid, GenericRequestVO requestVO,String PaRes) throws PZTechnicalViolationException
    {
        transactionlogger.error("Entering process3DAuthConfirmation of EMS...");

        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EMSResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        EMSUtils emsUtils = new EMSUtils();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        String cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        String customerid = commRequestVO.getCustomerId();
        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingid);
        paresDecodeRequestVO.setPares(PaRes);
        paresDecodeRequestVO.setTrackid(trackingid);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getSignature())){
            Sign=paresDecodeResponseVO.getSignature();
            if("Valid".equalsIgnoreCase(Sign)){
                paRes="Y";
            }else {
                paRes="N";
            }
        }
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>sale</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+cvv+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>sale</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+functions.maskingPan(cardNo)+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+functions.maskingNumber(expMonth)+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+functions.maskingNumber(expYear)+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+functions.maskingNumber(cvv)+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" +trackingid + "--" +  xmlRequestlog);

        String credentials = userID + ":" + password;
        transactionlogger.error("cred EMS---" +trackingid + "--" +  credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
        transactionlogger.error("Response EMS---" +trackingid + "--" +  response);

        commResponseVO = emsUtils.readSoapResponse(response);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        return commResponseVO;
    }
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingid, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Entering processCommon3DSaleConfirmation of EMS...");

        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        EMSResponseVO commResponseVO = null;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String cvv="";
        String customerid="";
        if(functions.isValueNull(commRequestVO.getMd())){
            String MD=commRequestVO.getMd();
            String data[]=MD.split("@");
            customerid=data[0];
            cvv= PzEncryptor.decryptCVV(data[1]);
            genericCardDetailsVO.setcVV(cvv);
            commRequestVO.setCustomerId(customerid);
        }

        EMSUtils emsUtils = new EMSUtils();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();

        String storeId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String userID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME(); //"WS230995073._.1";
        String password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD(); //"KcB5b2\">Qw";

        String cardNo = genericCardDetailsVO.getCardNum();
        String expMonth = genericCardDetailsVO.getExpMonth();
        String expYear = genericCardDetailsVO.getExpYear().substring(genericCardDetailsVO.getExpYear().length() - 2);
        cvv = genericCardDetailsVO.getcVV();
        String amount = genericTransDetailsVO.getAmount();

        customerid = commRequestVO.getCustomerId();
        String customerName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String address1 = addressDetailsVO.getStreet();
        String city = addressDetailsVO.getCity();
        String state = addressDetailsVO.getState();
        String country = addressDetailsVO.getCountry();
        String phone = addressDetailsVO.getPhone();
        String email = addressDetailsVO.getEmail();
        String zip = addressDetailsVO.getZipCode();
        int currencyId = Integer.parseInt(CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency()));

        ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
        paresDecodeRequestVO.setMassageID(trackingid);
        paresDecodeRequestVO.setPares(commRequestVO.getPaRes());
        paresDecodeRequestVO.setTrackid(trackingid);

        EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
        ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

        String XID="";
        String CAVV="";
        String VaRes="";
        String Sign="";
        String paRes="";
        String ECI="";

        if(functions.isValueNull(paresDecodeResponseVO.getXid())){
            XID=paresDecodeResponseVO.getXid();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getCavv())){
            CAVV=paresDecodeResponseVO.getCavv();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getStatus())){
            VaRes=paresDecodeResponseVO.getStatus();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getEci())){
            ECI=paresDecodeResponseVO.getEci();
        }
        if(functions.isValueNull(paresDecodeResponseVO.getSignature())){
            Sign=paresDecodeResponseVO.getSignature();
            if("Valid".equalsIgnoreCase(Sign)){
                paRes="Y";
            }else {
                paRes="N";
            }
        }
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>sale</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+cardNo+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+expMonth+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+expYear+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+cvv+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";
        String xmlRequestlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "<SOAP-ENV:Header />\n" +
                "<SOAP-ENV:Body>\n" +
                "<ipgapi:IPGApiOrderRequest xmlns:v1=\"http://ipg-online.com/ipgapi/schemas/v1\" xmlns:ipgapi=\"http://ipg-online.com/ipgapi/schemas/ipgapi\">\n" +
                "<v1:Transaction>\n" +
                "<v1:CreditCardTxType>\n" +
                "<v1:StoreId>"+storeId+"</v1:StoreId>\n" +
                "<v1:Type>sale</v1:Type>\n" +
                "</v1:CreditCardTxType>\n" +
                "<v1:CreditCardData>\n" +
                "<v1:CardNumber>"+functions.maskingPan(cardNo)+"</v1:CardNumber>\n" +//5453010000070789
                "<v1:ExpMonth>"+functions.maskingNumber(expMonth)+"</v1:ExpMonth>\n" + //Dec = 12
                "<v1:ExpYear>"+functions.maskingNumber(expYear)+"</v1:ExpYear>\n" + //2020 = 20
                "<v1:CardCodeValue>"+functions.maskingNumber(cvv)+"</v1:CardCodeValue>\n" +
                "<v1:Brand>"+EMSUtils.getCardType(genericCardDetailsVO.getCardType())+"</v1:Brand>\n" +
                "</v1:CreditCardData>\n" +
                "<v1:CreditCard3DSecure>\n" +
                "<v1:VerificationResponse>"+VaRes+"</v1:VerificationResponse>\n" +
                "<v1:PayerAuthenticationResponse>"+paRes+"</v1:PayerAuthenticationResponse>\n" +
                "<v1:AuthenticationValue>"+CAVV+"</v1:AuthenticationValue>\n" +//AAACAjYYkBgIAggAFhiQAAAAAAA=
                "<v1:XID>"+XID+"</v1:XID>\n" +
                "</v1:CreditCard3DSecure>\n" +
                "<v1:Payment>\n" +
                "<v1:ChargeTotal>"+amount+"</v1:ChargeTotal>\n" +
                "<v1:Currency>"+String.valueOf(currencyId)+"</v1:Currency>\n" +
                "</v1:Payment>\n" +
                "<v1:TransactionDetails>\n" +
                "<v1:MerchantTransactionId>"+trackingid+"</v1:MerchantTransactionId>\n" +
                "</v1:TransactionDetails>\n" +
                "<v1:Billing>\n" +
                "<v1:Name>"+customerName+"</v1:Name>\n" +
                "<v1:CustomerID>" + customerid + "</v1:CustomerID>\n" +
                "<v1:Address1>"+address1+"</v1:Address1>\n" +
                "<v1:City>"+city+"</v1:City>\n" +
                "<v1:State>"+state+"</v1:State>\n" +
                "<v1:Zip>"+zip+"</v1:Zip>\n" +
                "<v1:Country>"+country+"</v1:Country>\n" +
                "<v1:Phone>"+phone+"</v1:Phone>\n" +
                "<v1:Email>"+email+"</v1:Email>\n" +
                "</v1:Billing>\n" +
                "</v1:Transaction>\n" +
                "</ipgapi:IPGApiOrderRequest>\n" +
                "</SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>\n";

        transactionlogger.error("Request EMS---" +trackingid + "--" +  xmlRequest);

        String credentials = userID + ":" + password;
        transactionlogger.error("cred EMS---" + trackingid + "--" + credentials);

        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        String response = EMSUtils.doPostHTTPSURLConnectionClient(encodedCredentials, xmlRequest, accountId, isTest);
        transactionlogger.error("Response EMS---" +trackingid+"---"+ response);

        commResponseVO = emsUtils.readSoapResponse(response);
        commResponseVO.setEci(ECI);
        commResponseVO.setCurrency(genericTransDetailsVO.getCurrency());
        commResponseVO.setTmpl_Amount(addressDetailsVO.getTmpl_amount());
        commResponseVO.setTmpl_Currency(addressDetailsVO.getTmpl_currency());
        return commResponseVO;
    }
}

package com.payment.Oculus;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.dao.TransactionDAO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import javax.xml.soap.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by admin on 21-09-2016.
 */
public class OculusPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OculusPaymentGateway.class.getName());
    private static Logger log = new Logger(OculusPaymentGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Oculus");

    public static final String GATEWAY_TYPE = "Oculus";

    private final static String URL = "https://test.oculusgateway.ge/api/api.asmx";

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public OculusPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        Functions functions = new Functions();
        transactionLogger.debug("Entering processAuthentication of OculusPaymentGateway..");
        OculusUtils oculusUtils = new OculusUtils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String message = "";
        String transactionId = "";
        String processorApprovalCode = "";
        String errorCode = "";
        String token = "";
        String resAmount = "";
        /*String termUrl = "";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }*/
        //AuthHeader Parameter
        String UserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME(); //TransactworldUser
        String Password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //!uZQpQTnE$Q8Rr&T
        transactionLogger.debug("username----" + UserName);
        transactionLogger.debug("pasddd----" + Password);

        //Credit-Card Parameter -ServiceSecurity
        String MCSAccountID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String ServiceUserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String ServicePassword = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String API_KEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        //TokenData Parameter
        int TokenType = 0;
        String CardNumber = genericCardDetailsVO.getCardNum();
        int CardType = getCardtype(CardNumber);
        String ExpirationMonth = genericCardDetailsVO.getExpMonth();
        String ExpirationYear = genericCardDetailsVO.getExpYear();
        String FirstName = addressDetailsVO.getFirstname();
        String LastName = addressDetailsVO.getLastname();
        String CardHolderName = "";
        if (functions.isValueNull(FirstName) && functions.isValueNull(LastName))
            CardHolderName = FirstName + " " + LastName;
        else if (functions.isValueNull(FirstName))
            CardHolderName = FirstName;
        else
            CardHolderName = LastName;

        //TransactionData Parameter
        String Amount = genericTransDetailsVO.getAmount();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
        String CurrencyCode = currency;
        String EmailAddress = "";
        String CountryCode = "";
        String ZipCode = "";
        if (functions.isValueNull(addressDetailsVO.getZipCode()))
        {
            ZipCode = addressDetailsVO.getZipCode();
        }
        String StreetAddress = "";
        if (functions.isValueNull(addressDetailsVO.getStreet()))
        {
            StreetAddress = addressDetailsVO.getStreet();
        }
        String City = "";
        if (functions.isValueNull(addressDetailsVO.getCity()))
        {
            City = addressDetailsVO.getCity();
        }
        String State = "";
        if (functions.isValueNull(addressDetailsVO.getState()))
        {
            State = addressDetailsVO.getState();
        }
        String CVV = "";
        if (functions.isValueNull(genericCardDetailsVO.getcVV()))
        {
            CVV = genericCardDetailsVO.getcVV();
        }
        String Phone = "";
        if (functions.isValueNull(addressDetailsVO.getPhone()))
        {
            Phone = addressDetailsVO.getPhone();
        }
        String Country = "";
        if (functions.isValueNull(addressDetailsVO.getCountry()))
        {
            Country = addressDetailsVO.getCountry();
        }

        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String url = "";
        try
        {
            if (isTest)
            {
                url = RB.getString("TEST_URL");
            }
            else
            {
                url = RB.getString("LIVE_URL");
            }

            String prefix = "myc";
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPMessage soapMessageLog = messageFactory.createMessage();
            // SOAP Envelope
            SOAPEnvelope envelope = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessage);
            SOAPEnvelope envelopeLog = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessageLog);
            //Header
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPHeader soapHeaderLog = envelopeLog.getHeader();
            SOAPElement AuthHeader = soapHeader.addChildElement("AuthHeader", prefix);
            SOAPElement UserNameElement = AuthHeader.addChildElement("UserName", prefix);
            UserNameElement.addTextNode(UserName);
            SOAPElement PasswordElement = AuthHeader.addChildElement("Password", prefix);
            PasswordElement.addTextNode(Password);

//            soapHeaderLog.addChildElement(UserNameElement);
//            soapHeaderLog.addChildElement(PasswordElement);
            String APIkEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
            transactionLogger.error("APIKEY::::::::::" + APIkEY);
/*            if (isTest)
            {
            }
            else
            {
                transactionLogger.error("INSIDE LIVE APIKEY::::::::::" + APIkEY);
                SOAPElement APIkeyElement = AuthHeader.addChildElement("ApiKey", prefix);
                APIkeyElement.addTextNode(APIkEY);
                soapHeaderLog.addChildElement(APIkeyElement);
            }*/

            SOAPElement APIkeyElement = AuthHeader.addChildElement("ApiKey", prefix);
            APIkeyElement.addTextNode(APIkEY);
//            soapHeaderLog.addChildElement(APIkeyElement);
            soapHeaderLog.addChildElement(AuthHeader);

            //Body
            SOAPBody soapBody = envelope.getBody();
            SOAPBody soapBodyLog = envelopeLog.getBody();
            SOAPElement CreditAuth_SoapBodyElem = soapBody.addChildElement("CreditAuth_Soap", prefix);
            SOAPElement CreditAuth_SoapBodyElemLog = soapBodyLog.addChildElement("CreditAuth_Soap", prefix);
            SOAPElement creditCardElement = CreditAuth_SoapBodyElem.addChildElement("creditCardAuth", prefix);
            SOAPElement creditCardElementLog = CreditAuth_SoapBodyElemLog.addChildElement("creditCardAuth", prefix);
            //ServiceSecurity SOAP Element
            SOAPElement serviceSecurityElement = creditCardElement.addChildElement("ServiceSecurity", prefix);
            SOAPElement serviceUserNameElement = serviceSecurityElement.addChildElement("ServiceUserName", prefix);
            serviceUserNameElement.addTextNode(ServiceUserName);
            SOAPElement servicePasswordElement = serviceSecurityElement.addChildElement("ServicePassword", prefix);
            servicePasswordElement.addTextNode(ServicePassword);
            SOAPElement MCSAccountIDElement = serviceSecurityElement.addChildElement("MCSAccountID", prefix);
            MCSAccountIDElement.addTextNode(MCSAccountID);
            if(!isTest)
            {
                SOAPElement API_KEYElement = serviceSecurityElement.addChildElement("API_KEY", prefix);
                API_KEYElement.addTextNode(API_KEY);
            }
            creditCardElementLog.addChildElement(serviceSecurityElement);
            //CreditCard SOAP Element
            SOAPElement tokenDataElement = creditCardElement.addChildElement("TokenData", prefix);
            SOAPElement tokenDataElementLog = creditCardElementLog.addChildElement("TokenData", prefix);
            //TokenData SOAP Element
            SOAPElement cardHolderNameElement = tokenDataElement.addChildElement("CardHolderName", prefix);
            cardHolderNameElement.addTextNode(CardHolderName);
            SOAPElement cardHolderNameElementLog = tokenDataElementLog.addChildElement("CardHolderName", prefix);
            cardHolderNameElementLog.addTextNode(functions.maskingFirstName(FirstName) + " " + functions.maskingLastName(LastName));
            tokenDataElementLog.addChildElement(cardHolderNameElementLog);


            SOAPElement cardNumberElement = tokenDataElement.addChildElement("CardNumber", prefix);
            cardNumberElement.addTextNode(CardNumber);
            SOAPElement cardNumberElementLog = creditCardElementLog.addChildElement("CardNumber", prefix);
            cardNumberElementLog.addTextNode(functions.maskingPan(CardNumber));
            tokenDataElementLog.addChildElement(cardNumberElementLog);

            SOAPElement cardTypeElement = tokenDataElement.addChildElement("CardType", prefix);
            cardTypeElement.addTextNode(String.valueOf(CardType));
            tokenDataElementLog.addChildElement(cardTypeElement);

            SOAPElement expirationMonthElement = tokenDataElement.addChildElement("ExpirationMonth", prefix);
            expirationMonthElement.addTextNode(ExpirationMonth);
            SOAPElement expirationMonthElementLog = creditCardElementLog.addChildElement("ExpirationMonth", prefix);
            expirationMonthElementLog.addTextNode(functions.maskingNumber(ExpirationMonth));
            tokenDataElementLog.addChildElement(expirationMonthElementLog);

            SOAPElement expirationYearElement = tokenDataElement.addChildElement("ExpirationYear", prefix);
            expirationYearElement.addTextNode(ExpirationYear);
            SOAPElement expirationYearElementLog = creditCardElementLog.addChildElement("ExpirationYear", prefix);
            expirationYearElementLog.addTextNode(functions.maskingNumber(ExpirationYear));
            tokenDataElementLog.addChildElement(expirationYearElementLog);

            SOAPElement CVVElement = tokenDataElement.addChildElement("CVV", prefix);
            CVVElement.addTextNode(CVV);
            SOAPElement CVVElementLog = creditCardElementLog.addChildElement("CVV", prefix);
            CVVElementLog.addTextNode(functions.maskingNumber(CVV));
            tokenDataElementLog.addChildElement(CVVElementLog);

            SOAPElement StreetAddressElement = tokenDataElement.addChildElement("StreetAddress", prefix);
            StreetAddressElement.addTextNode(StreetAddress);
            tokenDataElementLog.addChildElement(StreetAddressElement);

            SOAPElement CityElement = tokenDataElement.addChildElement("City", prefix);
            CityElement.addTextNode(City);
            tokenDataElementLog.addChildElement(CityElement);

            SOAPElement StateElement = tokenDataElement.addChildElement("State", prefix);
            StateElement.addTextNode(State);
            tokenDataElementLog.addChildElement(StateElement);

            SOAPElement ZipCodeElement = tokenDataElement.addChildElement("ZipCode", prefix);
            ZipCodeElement.addTextNode(ZipCode);
            tokenDataElementLog.addChildElement(ZipCodeElement);

            SOAPElement CountryElement = tokenDataElement.addChildElement("Country", prefix);
            CountryElement.addTextNode(Country);
            tokenDataElementLog.addChildElement(CountryElement);
            //TransactionData SOAP Element
            SOAPElement transactionDataElement = creditCardElement.addChildElement("TransactionData", prefix);
            SOAPElement transactionDataElementlog = creditCardElementLog.addChildElement("TransactionData", prefix);

            SOAPElement amountElement = transactionDataElement.addChildElement("Amount", prefix);
            amountElement.addTextNode(Amount);
            transactionDataElementlog.addChildElement(amountElement);

           /*   SOAPElement gatewayIDElement=transactionDataElement.addChildElement("GatewayID",prefix);
            gatewayIDElement.addTextNode(GatewayID);
          */

            SOAPElement CardHolderNameElement = transactionDataElement.addChildElement("CardHolderName", prefix);
            SOAPElement CardHolderNameElementlog = transactionDataElementlog.addChildElement("CardHolderName", prefix);
            CardHolderNameElement.addTextNode(CardHolderName);
            CardHolderNameElementlog.addTextNode(functions.maskingFirstName(FirstName) + " " + functions.maskingLastName(LastName));

            transactionDataElementlog.addChildElement(CardHolderNameElementlog);

            SOAPElement EmailAddressElement = transactionDataElement.addChildElement("EmailAddress", prefix);
            EmailAddressElement.addTextNode(EmailAddress);
            transactionDataElementlog.addChildElement(EmailAddressElement);

            SOAPElement PhoneElement = transactionDataElement.addChildElement("Phone", prefix);
            PhoneElement.addTextNode(Phone);
            transactionDataElementlog.addChildElement(PhoneElement);

            SOAPElement CurrencyCodeElement = transactionDataElement.addChildElement("CurrencyCode", prefix);
            CurrencyCodeElement.addTextNode(CurrencyCode);
            transactionDataElementlog.addChildElement(CurrencyCodeElement);

            //SOAPElement CountryCodeElement = transactionDataElement.addChildElement("CountryCode", prefix);
            //CountryCodeElement.addTextNode(CountryCode);
            //transactionDataElementlog.addChildElement(CountryCodeElement);

            SOAPElement TicketNumberElement = transactionDataElement.addChildElement("TicketNumber", prefix);
            TicketNumberElement.addTextNode(trackingID);
            transactionDataElementlog.addChildElement(TicketNumberElement);
            //tokenDataElementLog.addChildElement(transactionDataElement);
            /*SOAPElement MCSTransactionIDElement=transactionDataElement.addChildElement("MCSTransactionID",prefix);
            MCSTransactionIDElement.addTextNode(MCSTransactionID);
            SOAPElement purchaseCardTaxAmountElement=transactionDataElement.addChildElement("PurchaseCardTaxAmount",prefix);
            purchaseCardTaxAmountElement.addTextNode(PurchaseCardTaxAmount);
*/
            transactionLogger.error("Auth Request-" + trackingID + "-->" + oculusUtils.convertToString(soapMessageLog));
//            String response = oculusUtils.call(soapMessage, "https://MyCardStorage.com/CreditAuth_Soap", url);
            HashMap<String, String> responseMap = oculusUtils.doPostOkhttp3Connection(soapMessage, "https://MyCardStorage.com/CreditAuth_Soap", url,"AUTH",trackingID);
            transactionLogger.error("Auth Response-" + trackingID + "-->" + responseMap);

//            HashMap<String, String> responseMap = oculusUtils.readResponseSoap(response, "AUTH");
            String status = "";
            String AuthType = "";
            String ChallengeURL = "";
            String ResultCode = "";
            message = oculusUtils.getErrorMessage(errorCode);
            String ChallengeKey = "";
            String CompleteChallengeURL="";
            String XID="";
            String MCSTransactionID = "";
            String DCCDate = "";
            String MerchantCountryCode = "";
            String MerchantCurrencyCode = "";
            String DCCCountryCode = "";
            String DCCCurrencyCode = "";
            String DCCExchangeRate = "";
            String DCCAmount = "";

            if (!functions.isValueNull(message))
                message = responseMap.get("ResultDetail");
            if (responseMap != null)
            {
                errorCode = responseMap.get("ResultCode");
                if ("0".equals(errorCode) || "4".equals(errorCode))
                {
                    AuthType = responseMap.get("AdditionalAuthType");
                    transactionLogger.error("------AuthType----" + AuthType);

                    ChallengeURL = responseMap.get("ChallengeURL");
                    ChallengeKey = responseMap.get("ChallengeKey");
                    CompleteChallengeURL=responseMap.get("CompleteChallengeURL");
                    XID=responseMap.get("XID");
                    transactionLogger.error("------afterChallengeURL----" + ChallengeURL);
                    transactionLogger.error("------afterChallengeKey----" + ChallengeKey);
                    transactionLogger.error("------afterChallengeURL----" + CompleteChallengeURL);
                    transactionLogger.error("------afterChallengeKey----" + XID);

                    if (functions.isValueNull(AuthType) && AuthType.equals("2: Challenge3DS"))
                    {
                        status = "pending3DConfirmation";
                        commResponseVO.setThreeDVersion("3Dv2");
                        commResponseVO.setUrlFor3DRedirect(ChallengeURL);
                        commResponseVO.setCreq(ChallengeKey);
                        //commResponseVO.setTerURL(termUrl);
                    }
                    else if (functions.isValueNull(AuthType) && AuthType.equals("1: Frictionless3DS"))
                    {
                        status = "success";
                        commResponseVO.setThreeDVersion("3Dv2");
                        commResponseVO.setDescription("Approved");
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                    else
                    {
                        status = "success";
                        transactionLogger.error("------status----" + status);
                        commResponseVO.setDescription("Approved");
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                }
                else if ("2".equals(errorCode))//Changes for 96: Unauthorized response
                {
                    status = "failed";
                    commResponseVO.setDescription(message);
                }
                else
                {
                    status = "failed";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Declined");
                }
                transactionId = responseMap.get("MCSTransactionID");
                processorApprovalCode = responseMap.get("ProcessorApprovalCode");
                DCCDate = responseMap.get("DCCDate");
                MerchantCountryCode = responseMap.get("MerchantCountryCode");
                MerchantCurrencyCode = responseMap.get("MerchantCurrencyCode");
                DCCCountryCode = responseMap.get("DCCCountryCode");
                DCCCurrencyCode = responseMap.get("DCCCurrencyCode");
                DCCExchangeRate = responseMap.get("DCCExchangeRate");
                DCCAmount = responseMap.get("DCCAmount");
                Amount = responseMap.get("Amount");
                token = responseMap.get("Token");
                resAmount = responseMap.get("Amount");
            }
            else
            {
                status = "pending";
                message = "Transaction is pending";
                commResponseVO.setDescription("Transaction is pending");
            }
            commResponseVO.setStatus(status);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setAuthCode(processorApprovalCode);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(message);
            commResponseVO.setAmount(resAmount);
            commResponseVO.setResponseHashInfo(token);
            commResponseVO.setWalletAmount(DCCAmount);
            commResponseVO.setWalletCurrecny(DCCCurrencyCode);

        }

        catch (SOAPException e)
        {
            log.error("SOAPException---", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processSale of OculusPaymentGateway");
        Functions functions = new Functions();
        OculusUtils oculusUtils = new OculusUtils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        //CommResponseVO commResponseVO = new CommResponseVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String message = "";
        String transactionId = "";
        String processorApprovalCode = "";
        String errorCode = "";
        String token = "";
        String resAmount = "";


        //AuthHeader Parameter
        String UserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME(); //TransactworldUser
        String Password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //!uZQpQTnE$Q8Rr&T
        transactionLogger.debug("username----" + UserName);
        transactionLogger.debug("pasddd----" + Password);

        //Credit-Card Parameter -ServiceSecurity
        String MCSAccountID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String ServiceUserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String ServicePassword = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String API_KEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

       /* String termUrl = "";

        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }*/

        //TokenData Parameter
        int TokenType = 0;
        String CardNumber = genericCardDetailsVO.getCardNum();
        int CardType = getCardtype(CardNumber);
        String ExpirationMonth = genericCardDetailsVO.getExpMonth();
        String ExpirationYear = genericCardDetailsVO.getExpYear();
        String FirstName = addressDetailsVO.getFirstname();
        String LastName = addressDetailsVO.getLastname();
        String CardHolderName = "";
        if (functions.isValueNull(FirstName) && functions.isValueNull(LastName))
            CardHolderName = FirstName + " " + LastName;
        else if (functions.isValueNull(FirstName))
            CardHolderName = FirstName;
        else
            CardHolderName = LastName;
        String ZipCode = "";
        if (functions.isValueNull(addressDetailsVO.getZipCode()))
        {
            ZipCode = addressDetailsVO.getZipCode();
        }
        String StreetAddress = "";
        if (functions.isValueNull(addressDetailsVO.getStreet()))
        {
            StreetAddress = addressDetailsVO.getStreet();
        }
        String City = "";
        if (functions.isValueNull(addressDetailsVO.getCity()))
        {
            City = addressDetailsVO.getCity();
        }
        String State = "";
        if (functions.isValueNull(addressDetailsVO.getState()))
        {
            State = addressDetailsVO.getState();
        }
        String CVV = "";
        if (functions.isValueNull(genericCardDetailsVO.getcVV()))
        {
            CVV = genericCardDetailsVO.getcVV();
        }
        String Phone = "";
        if (functions.isValueNull(addressDetailsVO.getPhone()))
        {
            Phone = addressDetailsVO.getPhone();
        }
        String Country = "";
        if (functions.isValueNull(addressDetailsVO.getCountry()))
        {
            Country = addressDetailsVO.getCountry();
        }
        //TransactionData Parameter
        String Amount = genericTransDetailsVO.getAmount();
        String GatewayID = "3";
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
        String CurrencyCode = currency;
        String EmailAddress = "";
        if (functions.isValueNull(addressDetailsVO.getEmail()))
        {
            EmailAddress = addressDetailsVO.getEmail();
        }
        String CountryCode = "";
        if (functions.isValueNull(addressDetailsVO.getCountry()))
        {
            CountryCode = addressDetailsVO.getCountry();
        }

        String MCSTransactionID = "0";
        String PurchaseCardTaxAmount = "0";
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String url = "";
        try
        {
            if (isTest)
            {
                url = RB.getString("TEST_URL");
            }
            else
            {
                url = RB.getString("LIVE_URL");
            }
            transactionLogger.error("URL---->" + url);
            String prefix = "myc";
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPMessage soapMessageLog = messageFactory.createMessage();
            // SOAP Envelope
            SOAPEnvelope envelope = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessage);
            SOAPEnvelope envelopeLog = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessageLog);
            //Header
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPHeader soapHeaderLog = envelopeLog.getHeader();
            SOAPElement AuthHeader = soapHeader.addChildElement("AuthHeader", prefix);
            SOAPElement UserNameElement = AuthHeader.addChildElement("UserName", prefix);
            UserNameElement.addTextNode(UserName);
            SOAPElement PasswordElement = AuthHeader.addChildElement("Password", prefix);
            PasswordElement.addTextNode(Password);
            soapHeaderLog.addChildElement(UserNameElement);
            soapHeaderLog.addChildElement(PasswordElement);
            String APIkEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
            transactionLogger.error("APIKEY::::::::::" + APIkEY);
            if (isTest)
            {
            }
            else
            {
                transactionLogger.error("INSIDE LIVE APIKEY::::::::::" + APIkEY);
                SOAPElement APIkeyElement = AuthHeader.addChildElement("ApiKey", prefix);
                APIkeyElement.addTextNode(APIkEY);
                soapHeaderLog.addChildElement(APIkeyElement);
            }
            //soapHeaderLog.addChildElement(AuthHeader);

            //Body
            SOAPBody soapBody = envelope.getBody();
            SOAPBody soapBodyLog = envelopeLog.getBody();
            SOAPElement CreditSale_SoapBodyElem = soapBody.addChildElement("CreditSale_Soap", prefix);
            SOAPElement CreditSale_SoapBodyElemLog = soapBodyLog.addChildElement("CreditSale_Soap", prefix);
            SOAPElement creditCardElement = CreditSale_SoapBodyElem.addChildElement("creditCardSale", prefix);
            SOAPElement creditCardElementLog = CreditSale_SoapBodyElemLog.addChildElement("creditCardSale", prefix);
            //ServiceSecurity SOAP Element
            SOAPElement serviceSecurityElement = creditCardElement.addChildElement("ServiceSecurity", prefix);
            SOAPElement serviceUserNameElement = serviceSecurityElement.addChildElement("ServiceUserName", prefix);
            serviceUserNameElement.addTextNode(ServiceUserName);
            SOAPElement servicePasswordElement = serviceSecurityElement.addChildElement("ServicePassword", prefix);
            servicePasswordElement.addTextNode(ServicePassword);
            SOAPElement MCSAccountIDElement = serviceSecurityElement.addChildElement("MCSAccountID", prefix);
            MCSAccountIDElement.addTextNode(MCSAccountID);
            if(!isTest)
            {
                SOAPElement API_KEYElement = serviceSecurityElement.addChildElement("API_KEY", prefix);
                API_KEYElement.addTextNode(API_KEY);
            }


            creditCardElementLog.addChildElement(serviceSecurityElement);

            //CreditCard SOAP Element
            SOAPElement tokenDataElement = creditCardElement.addChildElement("TokenData", prefix);
            SOAPElement tokenDataElementLog = creditCardElementLog.addChildElement("TokenData", prefix);
            //TokenData SOAP Element
            SOAPElement cardHolderNameElement = tokenDataElement.addChildElement("CardHolderName", prefix);
            cardHolderNameElement.addTextNode(CardHolderName);
            SOAPElement cardHolderNameElementLog = tokenDataElementLog.addChildElement("CardHolderName", prefix);
            cardHolderNameElementLog.addTextNode(functions.maskingFirstName(FirstName) + " " + functions.maskingLastName(LastName));
            tokenDataElementLog.addChildElement(cardHolderNameElementLog);

            SOAPElement cardNumberElement = tokenDataElement.addChildElement("CardNumber", prefix);
            cardNumberElement.addTextNode(CardNumber);
            SOAPElement cardNumberElementLog = creditCardElementLog.addChildElement("CardNumber", prefix);
            cardNumberElementLog.addTextNode(functions.maskingPan(CardNumber));
            tokenDataElementLog.addChildElement(cardNumberElementLog);

            SOAPElement cardTypeElement = tokenDataElement.addChildElement("CardType", prefix);
            cardTypeElement.addTextNode(String.valueOf(CardType));
            tokenDataElementLog.addChildElement(cardTypeElement);

            SOAPElement expirationMonthElement = tokenDataElement.addChildElement("ExpirationMonth", prefix);
            expirationMonthElement.addTextNode(ExpirationMonth);
            SOAPElement expirationMonthElementLog = creditCardElementLog.addChildElement("ExpirationMonth", prefix);
            expirationMonthElementLog.addTextNode(functions.maskingNumber(ExpirationMonth));
            tokenDataElementLog.addChildElement(expirationMonthElementLog);

            SOAPElement expirationYearElement = tokenDataElement.addChildElement("ExpirationYear", prefix);
            expirationYearElement.addTextNode(ExpirationYear);
            SOAPElement expirationYearElementLog = creditCardElementLog.addChildElement("ExpirationYear", prefix);
            expirationYearElementLog.addTextNode(functions.maskingNumber(ExpirationYear));
            tokenDataElementLog.addChildElement(expirationYearElementLog);

            SOAPElement CVVElement = tokenDataElement.addChildElement("CVV", prefix);
            CVVElement.addTextNode(CVV);
            SOAPElement CVVElementLog = creditCardElementLog.addChildElement("CVV", prefix);
            CVVElementLog.addTextNode(functions.maskingNumber(CVV));
            tokenDataElementLog.addChildElement(CVVElementLog);


            SOAPElement StreetAddressElement = tokenDataElement.addChildElement("StreetAddress", prefix);
            StreetAddressElement.addTextNode(StreetAddress);
            tokenDataElementLog.addChildElement(StreetAddressElement);

            SOAPElement CityElement = tokenDataElement.addChildElement("City", prefix);
            CityElement.addTextNode(City);
            tokenDataElementLog.addChildElement(CityElement);

            SOAPElement StateElement = tokenDataElement.addChildElement("State", prefix);
            StateElement.addTextNode(State);
            tokenDataElementLog.addChildElement(StateElement);

            SOAPElement ZipCodeElement = tokenDataElement.addChildElement("ZipCode", prefix);
            ZipCodeElement.addTextNode(ZipCode);
            tokenDataElementLog.addChildElement(ZipCodeElement);

            //SOAPElement CountryElement = tokenDataElement.addChildElement("Country", prefix);
            //CountryElement.addTextNode(Country);
            //tokenDataElementLog.addChildElement(CountryElement);


            //TransactionData SOAP Element


            SOAPElement transactionDataElement = creditCardElement.addChildElement("TransactionData", prefix);
            SOAPElement transactionDataElementlog = creditCardElementLog.addChildElement("TransactionData", prefix);

            SOAPElement amountElement = transactionDataElement.addChildElement("Amount", prefix);
            amountElement.addTextNode(Amount);
            transactionDataElementlog.addChildElement(amountElement);
             /*  SOAPElement gatewayIDElement=transactionDataElement.addChildElement("GatewayID",prefix);
            gatewayIDElement.addTextNode(GatewayID);
           */
            SOAPElement CardHolderNameElement = transactionDataElement.addChildElement("CardHolderName", prefix);
            SOAPElement CardHolderNameElementlog = transactionDataElementlog.addChildElement("CardHolderName", prefix);
            CardHolderNameElement.addTextNode(CardHolderName);
            CardHolderNameElementlog.addTextNode(functions.maskingFirstName(FirstName) + " " + functions.maskingLastName(LastName));

            transactionDataElementlog.addChildElement(CardHolderNameElementlog);

            SOAPElement EmailAddressElement = transactionDataElement.addChildElement("EmailAddress", prefix);
            EmailAddressElement.addTextNode(EmailAddress);
            transactionDataElementlog.addChildElement(EmailAddressElement);

            SOAPElement PhoneElement = transactionDataElement.addChildElement("Phone", prefix);
            PhoneElement.addTextNode(Phone);
            transactionDataElementlog.addChildElement(PhoneElement);

            SOAPElement CurrencyCodeElement = transactionDataElement.addChildElement("CurrencyCode", prefix);
            CurrencyCodeElement.addTextNode(CurrencyCode);
            transactionDataElementlog.addChildElement(CurrencyCodeElement);

            //SOAPElement CountryCodeElement = transactionDataElement.addChildElement("CountryCode", prefix);
            //CountryCodeElement.addTextNode(CountryCode);
            //transactionDataElementlog.addChildElement(CountryCodeElement);

            SOAPElement TicketNumberElement = transactionDataElement.addChildElement("TicketNumber", prefix);
            TicketNumberElement.addTextNode(trackingID);
            transactionDataElementlog.addChildElement(TicketNumberElement);

            // tokenDataElementLog.addChildElement(transactionDataElement);


        /*    SOAPElement MCSTransactionIDElement=transactionDataElement.addChildElement("MCSTransactionID",prefix);
            MCSTransactionIDElement.addTextNode(MCSTransactionID);
            SOAPElement purchaseCardTaxAmountElement=transactionDataElement.addChildElement("PurchaseCardTaxAmount",prefix);
            purchaseCardTaxAmountElement.addTextNode(PurchaseCardTaxAmount);*/

            //log masking start

            //log masking end

            transactionLogger.error("Sale Request-" + trackingID + "-->" + oculusUtils.convertToString(soapMessageLog));

//            String response = oculusUtils.call(soapMessage, "https://MyCardStorage.com/CreditSale_Soap", url);
            HashMap<String, String> responseMap = oculusUtils.doPostOkhttp3Connection(soapMessage, "https://MyCardStorage.com/CreditSale_Soap", url, "SALE",trackingID);
            transactionLogger.error("Sale Response-" + trackingID + "-->" + responseMap);

//            HashMap<String, String> responseMap = oculusUtils.readResponseSoap(response, "SALE");
            String status = "";
            String ResultCode = "";
            String ChallengeURL = "";
            String ChallengeKey = "";
            String XID="";
            String CompleteChallengeURL="";
            String AuthType = "";
            String DCCDate = "";
            String MerchantCountryCode = "";
            String MerchantCurrencyCode = "";
            String DCCCountryCode = "";
            String DCCCurrencyCode = "";
            String DCCExchangeRate = "";
            String DCCAmount = "";
            message = oculusUtils.getErrorMessage(errorCode);
            if (!functions.isValueNull(message))
                message = responseMap.get("ResultDetail");
            if (responseMap != null)
            {
                errorCode = responseMap.get("ResultCode");
                transactionLogger.error("------IsActive----" + responseMap.get("IsActive"));

                if ("0".equals(errorCode) || "4".equals(errorCode))
                {
                    AuthType = responseMap.get("AdditionalAuthType");
                    transactionLogger.error("------AuthType----" + AuthType);
                    ChallengeURL = responseMap.get("ChallengeURL");
                    ChallengeKey = responseMap.get("ChallengeKey");
                    CompleteChallengeURL=responseMap.get("CompleteChallengeURL");
                    XID=responseMap.get("XID");
                    transactionLogger.error("------afterChallengeURL----" + ChallengeURL);
                    transactionLogger.error("------afterChallengeKey----" + ChallengeKey);
                    transactionLogger.error("------afterChallengeURL----" + CompleteChallengeURL);
                    transactionLogger.error("------afterChallengeKey----" + XID);
                    if (functions.isValueNull(AuthType) && AuthType.equals("2: Challenge3DS"))
                    {
                        status = "pending3DConfirmation";
                        commResponseVO.setThreeDVersion("3Dv2");
                        commResponseVO.setUrlFor3DRedirect(ChallengeURL);
                        commResponseVO.setCreq(ChallengeKey);
                        commResponseVO.setMd(XID);
                        commResponseVO.setTerURL(CompleteChallengeURL);
                        //commResponseVO.setTerURL(termUrl);
                    }
                    else if (functions.isValueNull(AuthType) &&  AuthType.equals("1: Frictionless3DS"))
                    {
                        status = "pending";
                        commResponseVO.setThreeDVersion("3Dv2");
                        commResponseVO.setDescription("Approved");
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                    else
                    {
                        status = "success";
                        transactionLogger.error("------status----" + status);
                        commResponseVO.setDescription("Approved");
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                }
                else if ("2".equals(errorCode))//Changes for 96: Unauthorized response
                {
                    status = "failed";
                    commResponseVO.setDescription(message);
                    //    commResponseVO.setRemark(message);
                }
                else
                {
                    status = "failed";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Declined");
                }
                transactionId = responseMap.get("MCSTransactionID");
                processorApprovalCode = responseMap.get("ProcessorApprovalCode");


                token = responseMap.get("Token");
                resAmount = responseMap.get("Amount");
                DCCDate = responseMap.get("DCCDate");
                MerchantCountryCode = responseMap.get("MerchantCountryCode");
                MerchantCurrencyCode = responseMap.get("MerchantCurrencyCode");
                DCCCountryCode = responseMap.get("DCCCountryCode");
                DCCCurrencyCode = responseMap.get("DCCCurrencyCode");
                DCCExchangeRate = responseMap.get("DCCExchangeRate");
                DCCAmount = responseMap.get("DCCAmount");
                Amount = responseMap.get("Amount");
            }
            else
            {
                status = "pending";
                message = "Transaction is pending";
                commResponseVO.setDescription("Transaction is pending");
            }
            commResponseVO.setStatus(status);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(String.valueOf(transactionId));
            commResponseVO.setAuthCode(processorApprovalCode);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(message);
            commResponseVO.setAmount(resAmount);
            commResponseVO.setResponseHashInfo(token);
            commResponseVO.setWalletAmount(DCCAmount);
            commResponseVO.setWalletCurrecny(DCCCurrencyCode);

        }

        catch (SOAPException e)
        {
            log.error("SOAPException---", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processRefund of OculusPaymentGateway");

        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        OculusUtils oculusUtils = new OculusUtils();

        TransactionDAO transactionDAO = new TransactionDAO();
        TransactionDetailsVO transactionDetailsVO = transactionDAO.getDetailFromCommon(trackingID);
        String captureAmount = transactionDetailsVO.getCaptureAmount();
        String currency = transactionDetailsVO.getCurrency();
        String conversionAmount = transactionDetailsVO.getWalletAmount();

        transactionLogger.error("captureAmount ===== " + captureAmount);
        transactionLogger.error("currency ===== " + currency);
        transactionLogger.error("conversionAmount ===== " + conversionAmount);

        //AuthHeader Parameter
        String UserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME(); //TransactworldUser
        String Password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //!uZQpQTnE$Q8Rr&T
        transactionLogger.debug("username----" + UserName);
        transactionLogger.debug("pasddd----" + Password);

        //Credit-Card Parameter -ServiceSecurity
        String MCSAccountID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String ServiceUserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String ServicePassword = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String APIkEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
        transactionLogger.error("APIKEY::::::::::" + APIkEY);


        //TransactionData Parameter
        String MCSTransactionID = commTransactionDetailsVO.getPreviousTransactionId();
        String Amount = commTransactionDetailsVO.getAmount();
        String ReferenceNumber = "PURCHASE";
        String status = "";
        String errorCode = "";
        String transactionId = "";
        String processorApprovalCode = "";
        String message = "";
        String token = "";
        String resAmount = "";

        transactionLogger.error("Amount ===== " + Amount);

        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String url = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try
        {
            if(Amount.equals(captureAmount))
            {
                if(!"BRL".equals(currency))
                {
                    Amount = conversionAmount;
                    transactionLogger.error("new refund amount ===== " + Amount);
                }

                if (isTest)
                {
                    url = RB.getString("TEST_URL");
                }
                else
                {
                    url = RB.getString("LIVE_URL");
                }
                transactionLogger.error("URL---->" + url);
                String prefix = "myc";
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage soapMessage = messageFactory.createMessage();

                // SOAP Envelope
                SOAPEnvelope envelope = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessage);
                //Header
                SOAPHeader soapHeader = envelope.getHeader();
                SOAPElement AuthHeader = soapHeader.addChildElement("AuthHeader", prefix);
                SOAPElement UserNameElement = AuthHeader.addChildElement("UserName", prefix);
                UserNameElement.addTextNode(UserName);
                SOAPElement PasswordElement = AuthHeader.addChildElement("Password", prefix);
                PasswordElement.addTextNode(Password);
//                SOAPElement APIkeyElement = AuthHeader.addChildElement("ApiKey", prefix);
//                APIkeyElement.addTextNode(APIkEY);

                //Body
                SOAPBody soapBody = envelope.getBody();
                SOAPElement CreditCredit_SoapBodyElem = soapBody.addChildElement("CreditCredit_Soap", prefix);
                SOAPElement creditCardCreditElement = CreditCredit_SoapBodyElem.addChildElement("creditCardCredit", prefix);
                //ServiceSecurity SOAP Element
                SOAPElement serviceSecurityElement = creditCardCreditElement.addChildElement("ServiceSecurity", prefix);
                SOAPElement serviceUserNameElement = serviceSecurityElement.addChildElement("ServiceUserName", prefix);
                serviceUserNameElement.addTextNode(ServiceUserName);
                SOAPElement servicePasswordElement = serviceSecurityElement.addChildElement("ServicePassword", prefix);
                servicePasswordElement.addTextNode(ServicePassword);
                SOAPElement MCSAccountIDElement = serviceSecurityElement.addChildElement("MCSAccountID", prefix);
                MCSAccountIDElement.addTextNode(MCSAccountID);

                SOAPElement transactionDataElement = creditCardCreditElement.addChildElement("TransactionData", prefix);
                SOAPElement amountElement = transactionDataElement.addChildElement("Amount", prefix);
                amountElement.addTextNode(String.valueOf(Amount));
           /* SOAPElement gatewayIDElement=transactionDataElement.addChildElement("GatewayID",prefix);
            gatewayIDElement.addTextNode(GatewayID);*/
                SOAPElement ticketNumberElement = transactionDataElement.addChildElement("TicketNumber", prefix);
                ticketNumberElement.addTextNode(trackingID);
          /*  SOAPElement ReferenceNumberElement=transactionDataElement.addChildElement("ReferenceNumber",prefix);
            ReferenceNumberElement.addTextNode(ReferenceNumber);
          */
                SOAPElement MCSTransactionIDElement = transactionDataElement.addChildElement("MCSTransactionID", prefix);
                MCSTransactionIDElement.addTextNode(MCSTransactionID);

                transactionLogger.error("Refund Request-" + trackingID + "-->" + oculusUtils.convertToString(soapMessage));

//                String response = oculusUtils.call(soapMessage, "https://MyCardStorage.com/CreditCredit_Soap", url);
                HashMap<String, String> responseMap= oculusUtils.doPostOkhttp3Connection(soapMessage, "https://MyCardStorage.com/CreditCredit_Soap", url,"REFUND",trackingID);
                transactionLogger.error("Refund Response-" + trackingID + "-->" + responseMap);

                Functions functions = new Functions();
//                HashMap<String, String> responseMap = oculusUtils.readResponseSoap(response, "REFUND");
                String ResultCode = "";

                if (responseMap != null)
                {
                    errorCode = responseMap.get("ResultCode");
                    if ("0".equals(errorCode))
                    {
                        status = "success";
                        transactionLogger.error("------status----" + status);
                        commResponseVO.setDescription("Approved");
                    }
                    else
                    {
                        status = "failed";
                        transactionLogger.error("------status----" + status);
                        commResponseVO.setDescription("Declined");
                    }
                    transactionId = responseMap.get("MCSTransactionID");
                    processorApprovalCode = responseMap.get("ProcessorApprovalCode");

                    message = oculusUtils.getErrorMessage(errorCode);
                    if (!functions.isValueNull(message))
                        message = responseMap.get("ResultDetail");
                    token = responseMap.get("Token");
                    resAmount = responseMap.get("Amount");

                }
                else
                {
                    status = "pending";
                    message = "Transaction is pending";
                    commResponseVO.setDescription("Transaction is pending");
                }
                commResponseVO.setStatus(status);

                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionId(String.valueOf(transactionId));
                commResponseVO.setAuthCode(processorApprovalCode);
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setRemark(message);
                commResponseVO.setAmount(resAmount);
                commResponseVO.setResponseHashInfo(token);
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark("Partial refund is not supported");
                commResponseVO.setDescription("Declined");
            }
        }

        catch (SOAPException e)
        {
            log.error("SOAPException---", e);
        }

        return commResponseVO;


    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processCapture of OculusPaymentGateway");

        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String prefix = "myc";
        String MCSAccountID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        //AuthHeader Parameter
        String UserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME(); //TransactworldUser
        String Password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //!uZQpQTnE$Q8Rr&T
        transactionLogger.debug("username----" + UserName);
        transactionLogger.debug("pasddd----" + Password);
        //ServiceSecurity Parameter - CreditCard
        String ServiceUserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String ServicePassword = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String API_KEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        //TransactionData Parameter
        String Amount = commTransactionDetailsVO.getAmount();
        BigDecimal tnx_amount = new BigDecimal(Functions.round(Double.parseDouble(Amount), 2));
        int GatewayID = 3;
        String status = "";
        String errorCode = "";
        String transactionId = "";
        String processorApprovalCode = "";
        String message = "";
        String token = "";
        String resAmount = "";
        String ReferenceNumber = "AUTH";
        String TicketNumber = "0";
        String MCSTransactionID = commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("MCSTransactionID---" + MCSTransactionID);
        OculusUtils oculusUtils = new OculusUtils();
        String url = "";
        try
        {
            if (isTest)
            {
                url = RB.getString("TEST_URL");
            }
            else
            {
                url = RB.getString("LIVE_URL");
            }
            transactionLogger.error("URL---->" + url);
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            // SOAP Envelope
            SOAPEnvelope envelope = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessage);
            //Header
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPElement AuthHeader = soapHeader.addChildElement("AuthHeader", prefix);
            SOAPElement UserNameElement = AuthHeader.addChildElement("UserName", prefix);
            UserNameElement.addTextNode(UserName);
            SOAPElement PasswordElement = AuthHeader.addChildElement("Password", prefix);
            PasswordElement.addTextNode(Password);
            //Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement CreditCapture_SoapBodyElem = soapBody.addChildElement("CreditCapture_Soap", prefix);
            SOAPElement creditCardCaptureElement = CreditCapture_SoapBodyElem.addChildElement("creditCardCapture", prefix);
            //ServiceSecurity SOAP Element
            SOAPElement serviceSecurityElement = creditCardCaptureElement.addChildElement("ServiceSecurity", prefix);
            SOAPElement serviceUserNameElement = serviceSecurityElement.addChildElement("ServiceUserName", prefix);
            serviceUserNameElement.addTextNode(ServiceUserName);
            SOAPElement servicePasswordElement = serviceSecurityElement.addChildElement("ServicePassword", prefix);
            servicePasswordElement.addTextNode(ServicePassword);
            SOAPElement MCSAccountIDElement = serviceSecurityElement.addChildElement("MCSAccountID", prefix);
            MCSAccountIDElement.addTextNode(MCSAccountID);
            if(!isTest)
            {
                SOAPElement API_KEYElement = serviceSecurityElement.addChildElement("API_KEY", prefix);
                API_KEYElement.addTextNode(API_KEY);
            }

            SOAPElement transactionDataElement = creditCardCaptureElement.addChildElement("TransactionData", prefix);
            SOAPElement amountElement = transactionDataElement.addChildElement("Amount", prefix);
            amountElement.addTextNode(String.valueOf(Amount));
           /* SOAPElement gatewayIDElement=transactionDataElement.addChildElement("GatewayID",prefix);
            gatewayIDElement.addTextNode(GatewayID);*/
            SOAPElement ticketNumberElement = transactionDataElement.addChildElement("TicketNumber", prefix);
            ticketNumberElement.addTextNode(trackingID);
            SOAPElement ReferenceNumberElement = transactionDataElement.addChildElement("ReferenceNumber", prefix);
            ReferenceNumberElement.addTextNode(ReferenceNumber);
            SOAPElement MCSTransactionIDElement = transactionDataElement.addChildElement("MCSTransactionID", prefix);
            MCSTransactionIDElement.addTextNode(MCSTransactionID);

            transactionLogger.error("Capture Request-" + trackingID + "-->" + oculusUtils.convertToString(soapMessage));
//            String response = oculusUtils.call(soapMessage, "https://MyCardStorage.com/CreditCapture_Soap", url);
            HashMap<String, String> responseMap = oculusUtils.doPostOkhttp3Connection(soapMessage, "https://MyCardStorage.com/CreditCapture_Soap", url,"CAPTURE",trackingID);

            transactionLogger.error("Capture Response-" + trackingID + "-->" + responseMap);

            Functions functions = new Functions();
//            HashMap<String, String> responseMap = oculusUtils.readResponseSoap(response, "CAPTURE");


            if (responseMap != null)
            {
                errorCode = responseMap.get("ResultCode");
                if ("0".equals(errorCode))
                {
                    status = "success";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Approved");
                }
                else
                {
                    status = "failed";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Declined");
                }
                transactionId = responseMap.get("MCSTransactionID");
                processorApprovalCode = responseMap.get("ProcessorApprovalCode");
                message = oculusUtils.getErrorMessage(errorCode);
                if (!functions.isValueNull(message))
                    message = responseMap.get("ResultDetail");
                token = responseMap.get("Token");
                resAmount = responseMap.get("Amount");
            }
            else
            {
                status = "fail";
                commResponseVO.setDescription("Declined");
            }


            commResponseVO.setStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            commResponseVO.setTransactionType("capture");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(String.valueOf(transactionId));
            commResponseVO.setAuthCode(processorApprovalCode);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(message);
            commResponseVO.setAmount(resAmount);
            commResponseVO.setResponseHashInfo(token);

            // commResponseVO.setDescription(responseDocument.getResult().getResultDetail());
        }

        catch (SOAPException e)
        {
            log.error("SOAPException---", e);
        }

        return commResponseVO;

    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("Entering processVoid of OculusPaymentGateway");

        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        String Amount = commTransactionDetailsVO.getAmount();

        //AuthHeader Parameter
        String UserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME(); //TransactworldUser
        String Password = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); //!uZQpQTnE$Q8Rr&T
        transactionLogger.debug("username----" + UserName);
        transactionLogger.debug("pasddd----" + Password);

        //ServiceSecurity Parameter
        String ServiceUserName = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String ServicePassword = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String API_KEY = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        //TransactionData Parameter
        int GatewayID = 3;
        String ReferenceNumber = "AUTH";
        String status = "";
        String errorCode = "";
        String transactionId = "";
        String processorApprovalCode = "";
        String message = "";
        String token = "";
        String resAmount = "";
        String TicketNumber = "0";
        String url = "";
        OculusUtils oculusUtils = new OculusUtils();

        try
        {
            if (isTest)
            {
                url = RB.getString("TEST_URL");
            }
            else
            {
                url = RB.getString("LIVE_URL");
            }
            transactionLogger.error("URL---->" + url);
            String prefix = "myc";
            String MCSAccountID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

            String MCSTransactionID = commTransactionDetailsVO.getPreviousTransactionId();

            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();

            // SOAP Envelope
            SOAPEnvelope envelope = oculusUtils.createEnvelope(prefix, "https://MyCardStorage.com/", soapMessage);
            //Header
            SOAPHeader soapHeader = envelope.getHeader();
            SOAPElement AuthHeader = soapHeader.addChildElement("AuthHeader", prefix);
            SOAPElement UserNameElement = AuthHeader.addChildElement("UserName", prefix);
            UserNameElement.addTextNode(UserName);
            SOAPElement PasswordElement = AuthHeader.addChildElement("Password", prefix);
            PasswordElement.addTextNode(Password);
            //Body
            SOAPBody soapBody = envelope.getBody();
            SOAPElement CreditVoid_SoapBodyElem = soapBody.addChildElement("CreditVoid_Soap", prefix);
            SOAPElement creditCardVoidElement = CreditVoid_SoapBodyElem.addChildElement("creditCardVoid", prefix);
            //ServiceSecurity SOAP Element
            SOAPElement serviceSecurityElement = creditCardVoidElement.addChildElement("ServiceSecurity", prefix);
            SOAPElement serviceUserNameElement = serviceSecurityElement.addChildElement("ServiceUserName", prefix);
            serviceUserNameElement.addTextNode(ServiceUserName);
            SOAPElement servicePasswordElement = serviceSecurityElement.addChildElement("ServicePassword", prefix);
            servicePasswordElement.addTextNode(ServicePassword);
            SOAPElement MCSAccountIDElement = serviceSecurityElement.addChildElement("MCSAccountID", prefix);
            MCSAccountIDElement.addTextNode(MCSAccountID);
            if(!isTest)
            {
                SOAPElement API_KEYElement = serviceSecurityElement.addChildElement("API_KEY", prefix);
                API_KEYElement.addTextNode(API_KEY);
            }


            SOAPElement transactionDataElement = creditCardVoidElement.addChildElement("TransactionData", prefix);
            SOAPElement amountElement = transactionDataElement.addChildElement("Amount", prefix);
            amountElement.addTextNode(String.valueOf(Amount));
           /* SOAPElement gatewayIDElement=transactionDataElement.addChildElement("GatewayID",prefix);
            gatewayIDElement.addTextNode(GatewayID);*/
            SOAPElement ticketNumberElement = transactionDataElement.addChildElement("TicketNumber", prefix);
            ticketNumberElement.addTextNode(trackingID);
            SOAPElement ReferenceNumberElement = transactionDataElement.addChildElement("ReferenceNumber", prefix);
            ReferenceNumberElement.addTextNode(ReferenceNumber);
            SOAPElement MCSTransactionIDElement = transactionDataElement.addChildElement("MCSTransactionID", prefix);
            MCSTransactionIDElement.addTextNode(MCSTransactionID);

            transactionLogger.error("Void Request-" + trackingID + "-->" + oculusUtils.convertToString(soapMessage));

//            String response = oculusUtils.call(soapMessage, "https://MyCardStorage.com/CreditVoid_Soap", url);
            HashMap<String, String> responseMap = oculusUtils.doPostOkhttp3Connection(soapMessage, "https://MyCardStorage.com/CreditVoid_Soap", url,"CANCEL",trackingID);
            transactionLogger.error("Void Response-" + trackingID + "-->" + responseMap);

            Functions functions = new Functions();
//            HashMap<String, String> responseMap = oculusUtils.readResponseSoap(response, "CANCEL");

            if (responseMap != null)
            {
                errorCode = responseMap.get("ResultCode");
                if ("0".equals(errorCode))
                {
                    status = "success";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Approved");
                }
                else
                {
                    status = "failed";
                    transactionLogger.error("------status----" + status);
                    commResponseVO.setDescription("Declined");
                }
                transactionId = responseMap.get("MCSTransactionID");
                processorApprovalCode = responseMap.get("ProcessorApprovalCode");
                message = oculusUtils.getErrorMessage(errorCode);
                if (!functions.isValueNull(message))
                    message = responseMap.get("ResultDetail");
                token = responseMap.get("Token");
                resAmount = responseMap.get("Amount");
            }
            else
            {
                status = "pending";
                message = "Transaction is pending";
                commResponseVO.setDescription("Transaction is pending");
            }
            commResponseVO.setStatus(status);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(String.valueOf(transactionId));
            commResponseVO.setAuthCode(processorApprovalCode);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(message);
            commResponseVO.setAmount(resAmount);
            commResponseVO.setResponseHashInfo(token);

        }

        catch (SOAPException e)
        {
            log.error("SOAPException----", e);
        }

        return commResponseVO;

    }

    public int getCardtype(String ccNum) throws NumberFormatException
    {
        //ccNum.trim();
        int length = ccNum.trim().length();
        int cardType = 0;
        String type = "";
        if (ccNum.startsWith("4"))
        {
            cardType = 4;
        }
        else if ((ccNum.startsWith("51") || ccNum.startsWith("52") || ccNum.startsWith("53") || ccNum.startsWith("54") || ccNum.startsWith("55") || ccNum.startsWith("22") || ccNum.startsWith("23") || ccNum.startsWith("24") || ccNum.startsWith("25") || ccNum.startsWith("26") || ccNum.startsWith("27")))
        {
            cardType = 3;
        }
        else if ((ccNum.startsWith("34") || ccNum.startsWith("37")))
        {
            cardType = 1;
        }
        else if ((ccNum.startsWith("6011") || ccNum.startsWith("622126") || ccNum.startsWith("644") || ccNum.startsWith("65")))
        {
            cardType = 2;
        }
        return cardType;
    }

}

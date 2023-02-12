package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.SocketTimeoutException;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class LibertyPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(LibertyPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LibertyPaymentGateway.class.getName());
    //Configuration
    public static final String GATEWAY_TYPE = "Liberty";
    public static final String GATEWAY_URL = "https://libertypay.ge:5443/Exec";

    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.LibertyServlet");
    public static final String pkcs12file = rb.getString("testKeyCert");
    private static final String trustStorePassword = rb.getString("testPassword");

    public LibertyPaymentGateway(String accountId)
    {
         this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.info("Inside   LibertyPaymentGateway  ::::::::");
        transactionLogger.info("Inside   LibertyPaymentGateway  ::::::::");
        log.info("Inside   processAuthentication  ::::::::");
        transactionLogger.info("Inside   processAuthentication  ::::::::");

        validateForSale(trackingID, requestVO);

        CommResponseVO responseVO = new CommResponseVO();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(account.getMerchantId()==null || account.getMerchantId().equals(""))
            {
                log.info("MerchantId not configured");
                transactionLogger.info("MerchantId not configured");

                PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processAuthentication()",null,"common","Merchant Id not configured while Authenticating the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while Authenticating the transaction",new Throwable("Merchant Id not configured while Authenticating the transaction"));
            }

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();
            Double amount =Double.parseDouble(genericTransDetailsVO.getAmount())*100;

            String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
            String createRequest = "<TKKPG>\n" +
                    "<Request>\n" +
                    "<Operation>CreateOrder</Operation>\n" +
                    "<Order>\n" +
                    "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                    "<Amount>"+amount.intValue()+"</Amount>\n" +
                    "<Currency>"+currencyCode+"</Currency> \n" +
                    "<Description>"+genericTransDetailsVO.getOrderDesc()+"</Description>\n" +
                    "<ApproveURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=APPROVED</ApproveURL>\n" +
                    "<CancelURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=CANCELED</CancelURL>\n" +
                    "<DeclineURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=DECLINED</DeclineURL>\n" +
                    "<OrderType>PreAuth</OrderType>\n"+
                    "<AddParams><SenderName>"+genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname()+"</SenderName>\n" +
                    "<ResidentCityInLatin>"+genericAddressDetailsVO.getCity()+"</ResidentCityInLatin>\n" +
                    "<Address>"+genericAddressDetailsVO.getStreet()+"</Address>\n" +
                    "<email>"+genericAddressDetailsVO.getEmail()+"</email><phone>"+genericAddressDetailsVO.getPhone()+"</phone></AddParams></Order> </Request></TKKPG>";
            String createResponse = doPostURLConnection(GATEWAY_URL, createRequest);

            Map<String, String> map = convertXmlToMap(createResponse);
            String responseCode ="";
            if("00".equals(map.get("Status"))){
                String purchaseRequest= "<TKKPG>\n" +
                        "<Request>\n" +
                        "<Operation>Purchase</Operation>\n" +
                        "<Order><Merchant>"+account.getMerchantId()+"</Merchant><OrderID>"+map.get("OrderID")+"</OrderID></Order>\n" +
                        "<SessionID>"+map.get("SessionID")+"</SessionID>\n" +
                        "<Amount>"+genericTransDetailsVO.getAmount()+"</Amount>\n" +
                        "<Currency>"+currencyCode+"</Currency>\n" +
                        "<PAN>"+genericCardDetailsVO.getCardNum()+"</PAN>\n" +
                        "<ExpDate>"+genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2)+"</ExpDate>\n" +
                        "<CVV2>"+genericCardDetailsVO.getcVV()+"</CVV2>\n" +
                        "<IP>170.113.38.56</IP>\n" +
                        "</Request>\n" +
                        "</TKKPG>";

                String purchaseResponse = doPostURLConnection(GATEWAY_URL, purchaseRequest);

                String[] token = purchaseResponse.split("\"ResponseCode\" value=\"");

                responseCode = token[1].substring(0, 3);

            }
            if (responseCode!=null && "001".equals(responseCode)) {
                responseVO.setStatus("success");
                responseVO.setTransactionStatus("APPROVED");
                responseVO.setDescription("Transaction successful.");
                responseVO.setTransactionId(map.get("OrderID"));
                responseVO.setDescriptor(map.get("SessionID"));
                responseVO.setErrorCode(responseCode);
            }
            else {
                responseVO.setStatus("fail");
                responseVO.setErrorCode(responseCode);
            }


        return responseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.info("Inside   LibertyPaymentGateway  ::::::::");
        transactionLogger.info("Inside   LibertyPaymentGateway  ::::::::");
        log.info("Inside   processSale  ::::::::");
        transactionLogger.info("Inside   processSale  ::::::::");

        validateForSale(trackingID, requestVO);

        CommResponseVO responseVO = new CommResponseVO();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(account.getMerchantId()==null || account.getMerchantId().equals(""))
            {
                PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processSale()",null,"common","Merchant Id Configuration not provided for account Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id Configuration not provided for account Id::"+accountId,new Throwable("Merchant Id Configuration not provided for account Id::"+accountId));
            }

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();
            Double amount =Double.parseDouble(genericTransDetailsVO.getAmount())*100;

            String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
            String createRequest = "<TKKPG>\n" +
                    "<Request>\n" +
                    "<Operation>CreateOrder</Operation>\n" +
                    "<Order>\n" +
                    "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                    "<Amount>"+amount.intValue()+"</Amount>\n" +
                    "<Currency>"+currencyCode+"</Currency> \n" +
                    "<Description>"+genericTransDetailsVO.getOrderDesc()+"</Description>\n" +
                    "<ApproveURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=APPROVED</ApproveURL>\n" +
                    "<CancelURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=CANCELED</CancelURL>\n" +
                    "<DeclineURL>https://mof.libertybank.ge/IA00001/index.php?STATUS=DECLINED</DeclineURL>\n" +
                    "<AddParams><SenderName>"+genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname()+"</SenderName>\n" +
                    "<ResidentCityInLatin>"+genericAddressDetailsVO.getCity()+"</ResidentCityInLatin>\n" +
                    "<Address>"+genericAddressDetailsVO.getStreet()+"</Address>\n" +
                    "<email>"+genericAddressDetailsVO.getEmail()+"</email><phone>"+genericAddressDetailsVO.getPhone()+"</phone></AddParams></Order> </Request></TKKPG>";
            String createResponse = doPostURLConnection(GATEWAY_URL, createRequest);

            Map<String, String> map = convertXmlToMap(createResponse);
            String responseCode ="";
            if("00".equals(map.get("Status"))){
                String purchaseRequest= "<TKKPG>\n" +
                        "<Request>\n" +
                        "<Operation>Purchase</Operation>\n" +
                        "<Order><Merchant>"+account.getMerchantId()+"</Merchant><OrderID>"+map.get("OrderID")+"</OrderID></Order>\n" +
                        "<SessionID>"+map.get("SessionID")+"</SessionID>\n" +
                        "<Amount>"+amount.intValue()+"</Amount>\n" +
                        "<Currency>"+currencyCode+"</Currency>\n" +
                        "<PAN>"+genericCardDetailsVO.getCardNum()+"</PAN>\n" +
                        "<ExpDate>"+genericCardDetailsVO.getExpMonth()+genericCardDetailsVO.getExpYear().substring(2)+"</ExpDate>\n" +
                        "<CVV2>"+genericCardDetailsVO.getcVV()+"</CVV2>\n" +
                        "<IP>170.113.38.56</IP>\n" +
                        "</Request>\n" +
                        "</TKKPG>";

                String purchaseResponse = doPostURLConnection(GATEWAY_URL, purchaseRequest);

                String[] token = purchaseResponse.split("\"ResponseCode\" value=\"");
                responseCode = token[1].substring(0,3);

            }
            if (responseCode!=null && "001".equals(responseCode)) {
                responseVO.setStatus("success");
                responseVO.setTransactionStatus("APPROVED");
                responseVO.setDescription("Transaction successful.");
                responseVO.setTransactionId(map.get("OrderID"));
                responseVO.setDescriptor(map.get("SessionID"));
                responseVO.setErrorCode(responseCode);
            }
            else {
                responseVO.setStatus("fail");
                responseVO.setErrorCode(responseCode);
            }


        return responseVO;
    }

    /**
            *
            * @param trackingID
            * @param requestVO
            * @return
            * @throws com.directi.pg.SystemError
            */
           public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZDBViolationException, PZTechnicalViolationException
           {

               log.info("  Inside   LibertyPaymentGateway  ::::::::");
               transactionLogger.info("  Inside   LibertyPaymentGateway  ::::::::");
               log.info("  Inside   processRefund  ::::::::");
               transactionLogger.info("  Inside   processRefund  ::::::::");

               validateForRefund(trackingID, requestVO);

               CommResponseVO responseVO = new CommResponseVO();
                   GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                   if(account.getMerchantId()==null || account.getMerchantId().equals(""))
                   {
                       PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processRefund()",null,"common","Merchant Id not configured while Refunding the transaction",PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while Refunding the transaction",new Throwable("Merchant Id not configured while Refunding the transaction"));
                   }

                   CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
                   GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
                   Double amount =Double.parseDouble(genericTransDetailsVO.getAmount())*100;

                   String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
                   String orderSession = getOrderIdSessionId(trackingID);
                   String[] token = orderSession.split(",");
                   String orderId = token[0];
                   String sessionId = token[1];
                   String refundRequest= "<TKKPG>\n" +
                           "<Request>\n" +
                           "<Operation>Refund</Operation>\n" +
                           "<Order>\n" +
                           "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                           "<OrderID>"+orderId+"</OrderID>\n" +
                           "</Order>\n" +
                           "<SessionID>"+sessionId+"</SessionID>\n" +
                           "<Refund>\n" +
                           "<Amount>"+amount.intValue()+"</Amount>\n" +
                           "<Currency>"+currencyCode+"</Currency>\n" +
                           "</Refund>\n" +
                           "</Request>\n" +
                           "</TKKPG>";

                   String refundResponse = doPostURLConnection(GATEWAY_URL, refundRequest);

                   String responseCode = convertXmlToMap(refundResponse).get("Status");

               if (responseCode!=null && "00".equals(responseCode)) {
                   responseVO.setStatus("success");
                   responseVO.setTransactionStatus("APPROVED");
                   responseVO.setDescription("Transaction successful.");
                   responseVO.setErrorCode(responseCode);
                   responseVO.setAmount(amount.intValue()+"");
               }
               else {
                   responseVO.setStatus("fail");
                   responseVO.setErrorCode(responseCode);
               }


               return responseVO;

        }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        log.info("  Inside   LibertyPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   LibertyPaymentGateway  ::::::::");
        log.info("  Inside   processCapture  ::::::::");
        transactionLogger.info("  Inside   processCapture  ::::::::");

        validateForRefund(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        CommResponseVO responseVO = new CommResponseVO();
        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processCapture()",null,"common","Merchant ID is not Configured while Capturing transaction ,accountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant ID is not Configured while Capturing transaction ,accountID::"+accountId,new Throwable("Merchant ID is not Configured while Capturing transaction ,accountID::"+accountId));
        }

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();

            String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
            String orderSession = getOrderIdSessionId(trackingID);
            String[] token = orderSession.split(",");
            String orderId = token[0];
            String sessionId = token[1];
            Double amount =Double.parseDouble(genericTransDetailsVO.getAmount())*100;

            String responseCode = "";
            String completeRequest= "<TKKPG>\n" +
                    "<Request>\n" +
                    "<Operation>Completion</Operation>\n" +
                    "<Order>\n" +
                    "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                    "<OrderID>"+orderId+"</OrderID>\n" +
                    "</Order>\n" +
                    "<SessionID>"+sessionId+"</SessionID>\n" +
                    "<Amount>"+amount.intValue()+"</Amount>\n" +
                    "<Currency>"+currencyCode+"</Currency>\n" +
                    "<Description>Cancelled</Description>\n" +
                    "</Request>\n" +
                    "</TKKPG>";

            String completeResponse = doPostURLConnection(GATEWAY_URL, completeRequest);

            String[] value = completeResponse.split("\"ResponseCode\" value=\"");
            responseCode = value[1].substring(0,3);

        if (responseCode!=null && "001".equals(responseCode)) {
            responseVO.setStatus("success");
            responseVO.setTransactionStatus("APPROVED");
            responseVO.setDescription("Transaction successful.");
            responseVO.setErrorCode(responseCode);
            responseVO.setAmount(amount.intValue()+"");
        }
        else {
            responseVO.setStatus("fail");
            responseVO.setErrorCode(responseCode);
        }

    return responseVO;

    }


    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {

        log.info("  Inside   LibertyPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   LibertyPaymentGateway  ::::::::");
        log.info("  Inside   processCancel  ::::::::");
        transactionLogger.info("  Inside   processCancel  ::::::::");

        validateForRefund(trackingID, requestVO);

        CommResponseVO responseVO = new CommResponseVO();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(account.getMerchantId()==null || account.getMerchantId().equals(""))
            {
                PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processVoid()",null,"common","Merchant ID not configured while Cancelling the transation,AccountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant ID not configured while Cancelling the transation,AccountID::"+accountId,new Throwable("Merchant ID not configured while Cancelling the transation,AccountID::"+accountId));
            }

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();

            String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
            String orderSession = getOrderIdSessionId(trackingID);
            String[] token = orderSession.split(",");
            String orderId = token[0];
            String sessionId = token[1];
            String refundRequest= "<TKKPG>\n" +
                    "<Request>\n" +
                    "<Operation>Reverse</Operation>\n" +
                    "<Order>\n" +
                    "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                    "<OrderID>"+orderId+"</OrderID>\n" +
                    "</Order>\n" +
                    "<SessionID>"+sessionId+"</SessionID>\n" +
                    "</Request>\n" +
                    "</TKKPG>";

            String refundResponse = doPostURLConnection(GATEWAY_URL, refundRequest);

            String responseCode = convertXmlToMap(refundResponse).get("Status");

            if (responseCode!=null && "00".equals(responseCode)) {
                responseVO.setStatus("success");
                responseVO.setTransactionStatus("APPROVED");
                responseVO.setDescription("Transaction successful.");
                responseVO.setErrorCode(responseCode);
            }
            else {
                responseVO.setStatus("fail");
                responseVO.setErrorCode(responseCode);
            }


        return responseVO;

    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        log.info("  Inside   LibertyPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   LibertyPaymentGateway  ::::::::");
        log.info("  Inside   processInquiry  ::::::::");
        transactionLogger.info("  Inside   processInquiry  ::::::::");

        CommResponseVO responseVO = new CommResponseVO();
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            if(account.getMerchantId()==null || account.getMerchantId().equals(""))
            {
                PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"processInquiry()",null,"common","Merchant Id not configured while Inqurying transaction,AccountID::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while Inqurying transaction,AccountID::"+accountId,new Throwable("Merchant Id not configured while Inqurying transaction,AccountID::"+accountId));
            }

            CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;
            CommTransactionDetailsVO transDetailsVO = comRequestVO.getTransDetailsVO();
            String orderSession = getOrderIdSessionId(transDetailsVO.getOrderId());
            String[] token = orderSession.split(",");
            String orderId = token[0];
            String sessionId = token[1];

            String inquiryRequest = "<TKKPG>\n" +
                    "<Request>\n" +
                    "<Operation>GetOrderStatus</Operation>\n" +
                    "<Order>\n" +
                    "<Merchant>"+account.getMerchantId()+"</Merchant>\n" +
                    "<OrderID>"+orderId+"</OrderID>\n" +
                    "</Order>\n" +
                    "<SessionID>"+sessionId+"</SessionID>\n" +
                    "</Request>\n" +
                    "</TKKPG>";

            String refundResponse = doPostURLConnection(GATEWAY_URL, inquiryRequest);

            String responseCode = convertXmlToMap(refundResponse).get("Status");

            if (responseCode!=null && "00".equals(responseCode)) {
                responseVO.setStatus("success");
                responseVO.setTransactionStatus("APPROVED");
                responseVO.setDescription("Transaction successful.");
                responseVO.setTransactionId(orderId);
                responseVO.setErrorCode(responseCode);
                responseVO.setMerchantId(account.getMerchantId());

            }
            else {
                responseVO.setStatus("fail");
                responseVO.setErrorCode(responseCode);
            }


        return responseVO;

    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("LibertyPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    /**
        * Check the input values (to be updated later for 3D Secure case )
        * @param trackingID
        * @param requestVO
        * @throws com.directi.pg.SystemError
        */
 private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
 {
     if(trackingID ==null || trackingID.equals(""))
     {
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while Refunding OR Capturing OR Cancelling transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while Refunding transaction",new Throwable("Tracking Id not provided while Refunding transaction"));
     }

     if(requestVO ==null)
     {
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while Refunding OR Capturing OR Cancelling transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while Refunding transaction",new Throwable("Request  not provided while Refunding transaction"));
     }

     CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;



     CommTransactionDetailsVO commTransactionDetailsVO = comRequestVO.getTransDetailsVO();
     if(commTransactionDetailsVO ==null)
     {
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  not provided while Refunding OR Capturing OR Cancelling transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while Refunding transaction",new Throwable("TransactionDetails  not provided while Refunding transaction"));
     }
     if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while Refunding OR Capturing OR Cancelling transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while Refunding transaction",new Throwable("Amount not provided while Refunding transaction"));
     }

     if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
     {
        PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction ID not provided while Refunding OR Capturing OR Cancelling transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction ID not provided while Refunding transaction",new Throwable("Previous Transaction ID not provided while Refunding transaction"));
     }

 }

    /**
     * Check the input values (to be updated later for 3D Secure case )
     * @param trackingID
     * @param requestVO
     * @throws com.directi.pg.SystemError
     */
    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(trackingID ==null || trackingID.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id Not provided while placing transaction",new Throwable("Tracking Id Not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  Not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  Not provided while placing transaction",new Throwable("Request  Not provided while placing transaction"));
        }

        CommRequestVO comRequestVO  =    (CommRequestVO)requestVO;



        GenericTransDetailsVO genericTransDetailsVO = comRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  Not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  Not provided while placing transaction",new Throwable("TransactionDetails  Not provided while placing transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
           PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount Not provided while placing transaction",new Throwable("Amount Not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= comRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","AddressDetails  Not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"AddressDetails  Not provided while placing transaction",new Throwable("AddressDetails  Not provided while placing transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"First Name Not provided while placing transaction",new Throwable("First Name Not provided while placing transaction"));
        }
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Last Name Not provided while placing transaction",new Throwable("Last Name Not provided while placing transaction"));

        }

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Email ID Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email Id Not provided while placing transaction",new Throwable("Email ID Not provided while placing transaction"));
        }


        if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","IP Address Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"IP Address Not provided while placing transaction",new Throwable("IP Address Not provided while placing transaction"));
        }


        //Address Details
       if(genericAddressDetailsVO.getStreet()==null || genericAddressDetailsVO.getStreet().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Street Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Street Not provided while placing transaction",new Throwable("Street Not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getCity()==null || genericAddressDetailsVO.getCity().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","City Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"City Not provided while placing transaction",new Throwable("City Not provided while placing transaction"));
       }


       if(genericAddressDetailsVO.getCountry()==null || genericAddressDetailsVO.getCountry().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Country Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Country Not provided while placing transaction",new Throwable("Country Not provided while placing transaction"));
       }

       if(genericAddressDetailsVO.getState()==null || genericAddressDetailsVO.getState().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","State Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"State Not provided while placing transaction",new Throwable("State Not provided while placing transaction"));
       }

        if(genericAddressDetailsVO.getZipCode()==null || genericAddressDetailsVO.getZipCode().equals(""))
       {
           errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
           if (errorCodeListVO.getListOfError().isEmpty())
               errorCodeListVO.addListOfError(errorCodeVO);
          PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","ZIP Code Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Zip Code Not provided while placing transaction",new Throwable("Zip Code Not provided while placing transaction"));
       }


        GenericCardDetailsVO genericCardDetailsVO= comRequestVO.getCardDetailsVO();

        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
             PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  Not provided while placing transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  Not provided while placing transaction",new Throwable("CardDetails  Not provided while placing transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO Not provided while placing transaction",new Throwable("Card NO Not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO provided is Invalid while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO provided is Invalid while placing transaction",new Throwable("Card NO provided is Invalid while placing transaction"));
        }



        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id Not provided while placing transaction",new Throwable("Tracking Id Not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month Not provided while placing transaction",new Throwable("Expiry Month Not provided while placing transaction"));
        }


        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(LibertyPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year Not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year Not provided while placing transaction",new Throwable("Expiry Year Not provided while placing transaction"));
        }

    }

    public String doPostURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String responseXML = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;



        try {


            TrustManager trm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                //@Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException
                {
                }

                //@Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }
            };

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream keyInput = fullStream (pkcs12file);
            keyStore.load(keyInput, trustStorePassword.toCharArray());
            keyInput.close();
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, trustStorePassword.toCharArray());
            //trustManagerFactory.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trm }, new SecureRandom());

            SSLContext.setDefault(context);


            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            responseXML = new String(post.getResponseBody());

        }
        catch (CertificateException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"doPostURLConnection()",null,"common","Technical Exception while placing", PZTechnicalExceptionEnum.CERTIFICATE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (UnrecoverableKeyException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Key while placing", PZTechnicalExceptionEnum.UNRECOVERABLE_KEY_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Exception while placing", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (KeyStoreException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Exception while placing", PZTechnicalExceptionEnum.KEY_STORE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (KeyManagementException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Exception while placing", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (HttpException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Exception while placing", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"doPostURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "doPostURLConnection()", null, "common", "Technical Exception while placing", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return responseXML;
    }

    private static InputStream fullStream ( String fname ) throws IOException
    {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }

    public static Map<String, String> convertXmlToMap(String xmlResponse) throws PZTechnicalViolationException
    {
        Map<String, String> map = new HashMap<String, String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();


            document = builder.parse(new InputSource(new StringReader(xmlResponse)));


        Element data = document.getDocumentElement();
        NodeList list1 = data.getChildNodes();
        Node node1 = list1.item(0);
        if ("Response".equals(node1.getNodeName())){
            NodeList list2 = node1.getChildNodes();
            for (int i = 0; i < list2.getLength(); i++)
            {
                Node node2 = list2.item(i);
                if ("Order".equals(node2.getNodeName())){
                    NodeList list3 = node2.getChildNodes();
                    for (int j = 0; j < list3.getLength(); j++)
                    {
                        Node node3 = list3.item(j);
                        map.put(node3.getNodeName(),node3.getTextContent());
                    }
                }
                else {
                    map.put(node2.getNodeName(),node2.getTextContent());
                }
            }
        }
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(),"convertXmlToMap()",null,"common","Technical Exception while placing transaction",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "convertXmlToMap()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.SAXEXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(LibertyPaymentGateway.class.getName(), "convertXmlToMap()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return map;
    }

    private String getOrderIdSessionId (String trackingId) throws PZDBViolationException
    {
        Connection con = null;
        String orderSession = null;
        try
        {
            con = Database.getConnection();
            String sql = "select responsetransactionid, responsedescriptor from transaction_common_details where trackingid =? and (status = 'capturesuccess' or status ='authsuccessful')";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, trackingId);
            orderSession = null;
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                orderSession = rs.getString("responsetransactionid") + "," + rs.getString("responsedescriptor");
            }
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(LibertyPaymentGateway.class.getName(), "getOrderIdSessionId()", null, "common", "Sql Exception while getting previous transaction details", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(LibertyPaymentGateway.class.getName(), "getOrderIdSessionId()", null, "common", "Sql Exception while getting previous transaction details", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, systemError.getMessage(), systemError.getCause());
        }
        finally {
            Database.closeConnection(con);
        }
        return orderSession;
    }
}

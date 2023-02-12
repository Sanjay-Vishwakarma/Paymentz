package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.credorax.core.CredoraxRequestVO;
import com.payment.credorax.core.CredoraxResponseVO;
import com.payment.dvg_payment.DVGPaymentGateway;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.request.PZInquiryRequest;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class CredoraxPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(CredoraxPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CredoraxPaymentGateway.class.getName());
    //Configuration
    public static final String GATEWAY_TYPE = "Credorax";
   Functions functions = new Functions();
    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.CredoraxServlet");
    public static final String SECRET_KEY = rb.getString("securityKey");
    public static final String APPROVE = rb.getString("echo");
    private final static String URL_TEST = rb.getString("testUrl");

    /**
     * Character encoding. Credorax API currently supports only ASCII
     */
    private static final String CHARSET = rb.getString("charset");


    public CredoraxPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return "3.5";
    }


    /**
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws com.directi.pg.SystemError
     */
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        log.info("Inside   CredoraxPaymentGateway  ::::::::");
        transactionLogger.info("Inside   CredoraxPaymentGateway  ::::::::");
        log.info("Inside   processSale  ::::::::");
        transactionLogger.info("Inside   processSale  ::::::::");

        validateForSale(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(),"processSale()",null,"Common","Merchant Id not configured for gatewayAccount with Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured for gatewayAccount with Id",new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }

        CommRequestVO credoraxRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = credoraxRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= credoraxRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= credoraxRequestVO.getCardDetailsVO();

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", account.getMerchantId());
        // Oparation - Sale
        parameters.put("O", "1");
        // Request ID
        parameters.put("a1", trackingID);
        // Billing amount $10.07
        parameters.put("a4", (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"");
        // Card Number
        parameters.put("b1", genericCardDetailsVO.getCardNum());
        // Card type ID - VISA
        //parameters.put("b2", "1");
        // Card expiration month
        parameters.put("b3", genericCardDetailsVO.getExpMonth());
        // Card expiration year
        parameters.put("b4", genericCardDetailsVO.getExpYear().substring(2));
        // Card secure code
        parameters.put("b5", genericCardDetailsVO.getcVV());
        // Cardholder Name
        parameters.put("c1", genericAddressDetailsVO.getFirstname());
        // email address
        parameters.put("c3", genericAddressDetailsVO.getEmail());
        // IP address
        parameters.put("d1", genericAddressDetailsVO.getIp());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", APPROVE);



        // All submission parameters goes here
        Map<String, String> parametersLog = new HashMap<String, String>();
        // Merchant code
        parametersLog.put("M", account.getMerchantId());
        // Oparation - Sale
        parametersLog.put("O", "1");
        // Request ID
        parametersLog.put("a1", trackingID);
        // Billing amount $10.07
        parametersLog.put("a4", (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"");
        // Card Number
        parametersLog.put("b1", functions.maskingPan(genericCardDetailsVO.getCardNum()));
        // Card type ID - VISA
        //parameters.put("b2", "1");
        // Card expiration month
        parametersLog.put("b3", functions.maskingNumber(genericCardDetailsVO.getExpMonth()));
        // Card expiration year
        parametersLog.put("b4", functions.maskingNumber(genericCardDetailsVO.getExpYear().substring(2)));
        // Card secure code
        parametersLog.put("b5", functions.maskingNumber(genericCardDetailsVO.getcVV()));
        // Cardholder Name
        parametersLog.put("c1", genericAddressDetailsVO.getFirstname());
        // email address
        parametersLog.put("c3", genericAddressDetailsVO.getEmail());
        // IP address
        parametersLog.put("d1", genericAddressDetailsVO.getIp());
        // echo, if empty transaction will be not approved in integration environment
        parametersLog.put("d2", APPROVE);

        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();

        try
        {
            String request = getPostUrlParameters(parameters);
            String requestLog = getPostUrlParameters(parametersLog);

            log.debug("credorax sale request---"+requestLog);

            result = doPostHTTPSURLConnection(URL_TEST, request);

            log.debug("credorax sale response---"+result);

            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }


            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }

            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch(UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null,ue.getMessage(), ue.getCause());
        }
        return credoraxResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        log.info("Inside   CredoraxPaymentGateway  ::::::::");
        transactionLogger.info("Inside   CredoraxPaymentGateway  ::::::::");
        log.info("Inside   processAuthentication  ::::::::");
        transactionLogger.info("Inside   processAuthentication  ::::::::");

        validateForSale(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(),"processAuthentication()",null,"Common","Merchant Id not configured for gatewayAccount with Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured for gatewayAccount with Id",new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }

        CommRequestVO credoraxRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = credoraxRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= credoraxRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= credoraxRequestVO.getCardDetailsVO();

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", account.getMerchantId());
        // Oparation - Sale
        parameters.put("O", "2");
        // Request ID
        parameters.put("a1", trackingID);
        // Billing amount $10.07
        parameters.put("a4", (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"");
        // Card Number
        parameters.put("b1", genericCardDetailsVO.getCardNum());
        // Card type ID - VISA
        //parameters.put("b2", "1");
        // Card expiration month
        parameters.put("b3", genericCardDetailsVO.getExpMonth());
        // Card expiration year
        parameters.put("b4", genericCardDetailsVO.getExpYear().substring(2));
        // Card secure code
        parameters.put("b5", genericCardDetailsVO.getcVV());
        // Cardholder Name
        parameters.put("c1", genericAddressDetailsVO.getFirstname());
        // email address
        parameters.put("c3", genericAddressDetailsVO.getEmail());
        // IP address
        parameters.put("d1", genericAddressDetailsVO.getIp());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", APPROVE);


        // All submission parameters goes here
        Map<String, String> parametersLog = new HashMap<String, String>();
        // Merchant code
        parametersLog.put("M", account.getMerchantId());
        // Oparation - Sale
        parametersLog.put("O", "2");
        // Request ID
        parametersLog.put("a1", trackingID);
        // Billing amount $10.07
        parametersLog.put("a4", (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"");
        // Card Number
        parametersLog.put("b1", functions.maskingPan(genericCardDetailsVO.getCardNum()));
        // Card type ID - VISA
        //parameters.put("b2", "1");
        // Card expiration month
        parametersLog.put("b3", functions.maskingNumber(genericCardDetailsVO.getExpMonth()));
        // Card expiration year
        parametersLog.put("b4", functions.maskingNumber(genericCardDetailsVO.getExpYear().substring(2)));
        // Card secure code
        parametersLog.put("b5", functions.maskingNumber(genericCardDetailsVO.getcVV()));
        // Cardholder Name
        parametersLog.put("c1", genericAddressDetailsVO.getFirstname());
        // email address
        parametersLog.put("c3", genericAddressDetailsVO.getEmail());
        // IP address
        parametersLog.put("d1", genericAddressDetailsVO.getIp());
        // echo, if empty transaction will be not approved in integration environment
        parametersLog.put("d2", APPROVE);

        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();

        try
        {
            String request = getPostUrlParameters(parameters);
            String requestLog = getPostUrlParameters(parametersLog);

            log.debug("credorax auth request---"+requestLog);

            result = doPostHTTPSURLConnection(URL_TEST, request);

            log.debug("credorax auth response---"+result);
            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }

            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }
            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch(UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null,ue.getMessage(), ue.getCause());
        }

        return credoraxResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {

        log.info("  Inside   CredoraxPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   CredoraxPaymentGateway  ::::::::");
        log.info("  Inside   processRefund  ::::::::");
        transactionLogger.info("  Inside   processRefund  ::::::::");

        validateForRefund(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processAuthentication()", null, "Common", "Merchant Id not configured for gatewayAccount with Id::" + accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Merchant Id not configured for gatewayAccount with Id", new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }
        CredoraxRequestVO credoraxRequestVO  =    (CredoraxRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = credoraxRequestVO.getTransDetailsVO();
        //GenericAddressDetailsVO genericAddressDetailsVO= credoraxRequestVO.getAddressDetailsVO();

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", account.getMerchantId());
        // Oparation - Sale
        parameters.put("O", "5");
        // Request ID
        parameters.put("a1", commTransactionDetailsVO.getDetailId());
        //parameters.put("a4", "20000");
        parameters.put("d1", credoraxRequestVO.getAddressDetailsVO().getIp());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", APPROVE);

        parameters.put("g2", commTransactionDetailsVO.getPreviousTransactionId());
        parameters.put("g3", credoraxRequestVO.getResponseAuthCode());
        parameters.put("g4", commTransactionDetailsVO.getOrderId());
        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();
        try
        {
            String request = getPostUrlParameters(parameters);

            result = doPostHTTPSURLConnection(URL_TEST, request);

            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }

            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }
            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch(UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processAuth()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null ,ue.getMessage(), ue.getCause());
        }
        return credoraxResponseVO;

    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        log.info("  Inside   CredoraxPaymentGateway  ::::::::");
        log.info("  Inside   processCapture  ::::::::");

        validateForRefund(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(),"processCapture()",null,"Common","Merchant Id not configured for gatewayAccount with Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured for gatewayAccount with Id",new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }
        CredoraxRequestVO credoraxRequestVO  =    (CredoraxRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = credoraxRequestVO.getTransDetailsVO();
        //GenericAddressDetailsVO genericAddressDetailsVO= credoraxRequestVO.getAddressDetailsVO();

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", account.getMerchantId());
        // Oparation - Sale
        parameters.put("O", "3");
        // Request ID
        parameters.put("a1", commTransactionDetailsVO.getDetailId());
        parameters.put("d1", credoraxRequestVO.getAddressDetailsVO().getIp());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", APPROVE);

        parameters.put("g2", commTransactionDetailsVO.getPreviousTransactionId());
        parameters.put("g3", credoraxRequestVO.getResponseAuthCode());
        parameters.put("g4", commTransactionDetailsVO.getOrderId());
        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();
        try
        {
            String request = getPostUrlParameters(parameters);

            result = doPostHTTPSURLConnection(URL_TEST, request);

            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }


            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }
            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch(UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CredoraxUtills.java", "processCapture()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null,ue.getMessage(), ue.getCause());
        }
        return credoraxResponseVO;

    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {

        log.info("  Inside   CredoraxPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   CredoraxPaymentGateway  ::::::::");
        log.info("  Inside   processVoid  ::::::::");
        transactionLogger.info("  Inside   processVoid  ::::::::");

        validateForRefund(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(),"processVoid()",null,"Common","Merchant Id not configured for gatewayAccount with Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured for gatewayAccount with Id",new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }
        CredoraxRequestVO credoraxRequestVO  =    (CredoraxRequestVO)requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = credoraxRequestVO.getTransDetailsVO();
        //GenericAddressDetailsVO genericAddressDetailsVO= credoraxRequestVO.getAddressDetailsVO();

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", account.getMerchantId());
        // Oparation - Sale
        parameters.put("O", "4");
        // Request ID
        parameters.put("a1", commTransactionDetailsVO.getDetailId());
        parameters.put("d1", credoraxRequestVO.getAddressDetailsVO().getIp());
        // echo, if empty transaction will be not approved in integration environment
        parameters.put("d2", APPROVE);

        parameters.put("g2", commTransactionDetailsVO.getPreviousTransactionId());
        parameters.put("g3", credoraxRequestVO.getResponseAuthCode());
        parameters.put("g4", commTransactionDetailsVO.getOrderId());
        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();
        try
        {
            String request = getPostUrlParameters(parameters);

            result = doPostHTTPSURLConnection(URL_TEST, request);
            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }

            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }
            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch(UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processAuth()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null, ue.getMessage(), ue.getCause());
        }
        return credoraxResponseVO;

    }

    public GenericResponseVO processInquiry(PZInquiryRequest requestVO) throws PZTechnicalViolationException
    {
        log.info("  Inside   CredoraxPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   CredoraxPaymentGateway  ::::::::");
        log.info("  Inside   processInquiry  ::::::::");
        transactionLogger.info("  Inside   processInquiry  ::::::::");

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            log.info("MerchantId not configured");
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(),"processInquiry()",null,"Common","Merchant Id not configured for gatewayAccount with Id::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured for gatewayAccount with Id",new Throwable("Merchant Id not configured for gatewayAccount with Id"));
        }

        // All submission parameters goes here
        Map<String, String> parameters = new HashMap<String, String>();
        // Merchant code
        parameters.put("M", "80379315");
        // Oparation - Sale
        parameters.put("O", "101");
        // Request ID
        parameters.put("a1", "10942374345");
        parameters.put("d2", APPROVE);
        parameters.put("g4", "418");
        String result = null;
        CredoraxResponseVO credoraxResponseVO = new CredoraxResponseVO();
        try
        {
            String request = getPostUrlParameters(parameters);

            result = doPostHTTPSURLConnection(URL_TEST, request);
            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = createResultMap(result);
            }

            if(responseMap.get("z2")!=null && responseMap.get("z2").equals("0"))
            {
                credoraxResponseVO.setStatus("success");
            }else
            {
                credoraxResponseVO.setStatus("fail");
            }
            credoraxResponseVO.setResponseTime(URLDecoder.decode(responseMap.get("T"),"ASCII"));
            credoraxResponseVO.setTransactionId(responseMap.get("z1"));
            credoraxResponseVO.setErrorCode(responseMap.get("z2"));
            credoraxResponseVO.setDescription(URLDecoder.decode(responseMap.get("z3"),"ASCII"));
            // Setting Authcode
            credoraxResponseVO.setResponseAuthCode(responseMap.get("z4"));

        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CredoraxPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return credoraxResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("CredoraxPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    private String generateMD5Key(String message) throws SystemError
    {
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            log.info("MD5 generation failed");
            throw new SystemError("MD5 generation failed");
        }
        md.update(message.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracing Id not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracing Id not provided while placing transaction",new Throwable("Tracing Id not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request not provided while refunding transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request not provided while placing transaction",new Throwable("Request not provided while placing transaction"));
        }

        CommRequestVO credoraxRequestVO  =    (CommRequestVO)requestVO;



        CommTransactionDetailsVO commTransactionDetailsVO = credoraxRequestVO.getTransDetailsVO();
        if(commTransactionDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  Id not refunding while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails not provided while placing transaction",new Throwable("TransactionDetails not provided while placing transaction"));
        }
        if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while refundling transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction ID not provided while refunding transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while placing transaction",new Throwable("Previous Transaction Id not provided while placing transaction"));
        }

    }
    public static String doPostHTTPSURLConnection(String strURL, String req)throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse="";
        try
        {

            URL url = new URL(strURL);
            URLConnection connection = null;
            try
            {
                connection = url.openConnection();
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
            }

            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("CredoraxPaymentGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }

            if(connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection)connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers for content type and length
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
            in.close();


        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CredoraxPaymentGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CredoraxPaymentGateway.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CredoraxPaymentGateway.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CredoraxPaymentGateway.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }
        finally {
            if (outSW != null) {
                try {
                    outSW.close();
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
        if (strResponse == null)
            return "";
        else
            return strResponse;
    }

    public static Map createResultMap(String result){
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer tok = new StringTokenizer(result, "&", false);
        String[] params = result.split("&");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            String[] keyValue = token.split("=");
            if (keyValue.length==2){
                map.put(keyValue[0],keyValue[1]);
            }
        }
        return map;
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
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Tracking Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Tracking Id not provided while placing transaction", new Throwable("Tracking Id not provided while placing transaction"));
            }

        if (requestVO == null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Request  not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Request not provided while placing transaction", new Throwable("Request not provided while placing transaction"));
        }

        CommRequestVO credoraxRequestVO = (CommRequestVO) requestVO;


        GenericTransDetailsVO genericTransDetailsVO = credoraxRequestVO.getTransDetailsVO();
        if (genericTransDetailsVO == null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Transaction Details not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Transaction Details not provided while placing transaction", new Throwable("Transaction Details not provided while placing transaction"));
        }
        if (genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Amount not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Amount not provided while placing transaction", new Throwable("Amount not provided while placing transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO = credoraxRequestVO.getAddressDetailsVO();

        if (genericAddressDetailsVO == null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "AddressDetails not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "AddressDetails not provided while placing transaction", new Throwable("AddressDetails not provided while placing transaction"));
        }
        //User Details
        if (genericAddressDetailsVO.getFirstname() == null || genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "First Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "First Name not provided while placing transaction", new Throwable("First Name not provided while placing transaction"));

        }
        if (genericAddressDetailsVO.getLastname() == null || genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Last Name not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Lst Name not provided while placing transaction", new Throwable("Last Name not provided while placing transaction"));

        }

        if (genericAddressDetailsVO.getEmail() == null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Email Id not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Email Id not provided while placing transaction", new Throwable("Email Id not provided while placing transaction"));
        }


        if (genericAddressDetailsVO.getIp() == null || genericAddressDetailsVO.getIp().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Ip Address not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Ip Address not provided while placing transaction", new Throwable("IpAddress not provided while placing transaction"));
        }
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String addressValidation = account.getAddressValidation();
        //Address Details
        if (addressValidation.equalsIgnoreCase("Y"))
        {
            if (genericAddressDetailsVO.getStreet() == null || genericAddressDetailsVO.getStreet().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Street not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Street not provided while placing transaction", new Throwable("Street not provided while placing transaction"));
            }

            if (genericAddressDetailsVO.getCity() == null || genericAddressDetailsVO.getCity().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "City not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "City not provided while placing transaction", new Throwable("City not provided while placing transaction"));
            }


            if (genericAddressDetailsVO.getCountry() == null || genericAddressDetailsVO.getCountry().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Country not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Country not provided while placing transaction", new Throwable("Country not provided while placing transaction"));
            }

            if (genericAddressDetailsVO.getState() == null || genericAddressDetailsVO.getState().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "State not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "State not provided while placing transaction", new Throwable("State not provided while placing transaction"));
            }

            if (genericAddressDetailsVO.getZipCode() == null || genericAddressDetailsVO.getZipCode().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Zip Code not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Zip Code not provided while placing transaction", new Throwable("Zip Code not provided while placing transaction"));
            }
        }

        GenericCardDetailsVO genericCardDetailsVO = credoraxRequestVO.getCardDetailsVO();

        if (genericCardDetailsVO == null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "CardDetails not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "CardDetails not provided while placing transaction", new Throwable("CardDetails not provided while placing transaction"));
        }
        //Card Details

        if (genericCardDetailsVO.getCardNum() == null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Card Num not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Card Num not provided while placing transaction", new Throwable("Card Num not provided while placing transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if (ccnum != null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Card Num provided is not valid while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Card Num provided is not valid while placing transaction", new Throwable("Card Num provided invalid while placing transaction"));
        }


        if (genericCardDetailsVO.getcVV() == null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "CVV not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "CVV not provided while placing transaction", new Throwable("CVV not provided while placing transaction"));
        }


        if (genericCardDetailsVO.getExpMonth() == null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Expiry Month not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Expiry Month not provided while placing transaction", new Throwable("Expiry Month not provided while placing transaction"));
        }


        if (genericCardDetailsVO.getExpYear() == null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(CredoraxPaymentGateway.class.getName(), "validateForSale()", null, "Common", "Expiry Year not provided while placing transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Expiry Year not provided while placing transaction", new Throwable("Expiry Year not provided while placing transaction"));
        }

    }

    /**
     * Utility method for building URL encoded request parameters
     * @param map
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    String getPostUrlParameters(Map<String, String> map) throws UnsupportedEncodingException
    {
        // Use TreeMap to sort parameters in alpha numeric order
        TreeMap<String, String> sortedMap = new TreeMap<String, String>(map);
        String md5 = doGetPackageSignature(sortedMap.values());
        StringBuffer buff = new StringBuffer();
        buff.append("K=").append(URLEncoder.encode(md5, CHARSET));
        for (String key : sortedMap.keySet()) {
            String value = sortedMap.get(key);
            buff.append('&').append(key).append('=').append(URLEncoder.encode(value, CHARSET));
        }
        return buff.toString();
    }
    String doGetPackageSignature(Iterable<String> sorted) {
        MessageDigest algorithm;
        try {
            StringBuffer b = new StringBuffer();
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            Charset charset = Charset.forName(CHARSET);
            for (String value : sorted) {
                algorithm.update(value.getBytes(CHARSET));
                b.append(value);
            }
            // append secret key to the end
            algorithm.update(SECRET_KEY.getBytes(CHARSET));
            b.append(SECRET_KEY);
            return new String(getHexString(algorithm.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hex string utility
     */

    static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2',	(byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7',	(byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c',	(byte) 'd', (byte) 'e', (byte) 'f' };

    static String getHexString(byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, CHARSET);
    }

}
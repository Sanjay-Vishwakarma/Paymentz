package com.payment.websecpay.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/29/13
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class WSecPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "WSec";

    private static final String RES = "RES"; //authorize
    private static final String SAL = "SAL"; //sale
    private static String accessURL = "https://www.pagamentsegur.com/";

    private static final String SAS = "DES"; //capture
    private static final String DES = "DES"; //void auth
    private static final String RFD = "RFD"; //refund
    private static String maintenanceURL = "https://www.pagamentsegur.com/backoffice/tools/maintenance.php";

    private static Logger logger = new Logger(WSecPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WSecPaymentGateway.class.getName());
    private static String statusURL = "https://www.pagamentsegur.com/backoffice/tools/status.php";


    public WSecPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public WSecPaymentGateway()
    {

    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        WSecResponseVO wSecResponseVO = null;
        try
        {
            WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = wSecRequestVO.getCommMerchantVO();
            GenericTransDetailsVO transDetailsVO = wSecRequestVO.getTransDetailsVO();
            String amountReceived = transDetailsVO.getAmount();
            CommCardDetailsVO cardDetailsVO = wSecRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = wSecRequestVO.getAddressDetailsVO();

            String merchantKey = wSecRequestVO.getKey();
            String url = wSecRequestVO.getSiteUrl();

            int txnPaise = (new BigDecimal(amountReceived).multiply(new BigDecimal(100.00))).intValue();


            HashMap<String, String> requestParams = new HashMap<String, String>();


            String NumSite = commMerchantAccountVO.getMerchantId();
            String orderID = trackingID;
            String amount = String.valueOf(txnPaise);
            String currency = transDetailsVO.getCurrency();
            String CARDNO = cardDetailsVO.getCardNum();
            String operation = SAL;


            requestParams.put("NumSite", commMerchantAccountVO.getMerchantId());
            requestParams.put("Password", commMerchantAccountVO.getPassword());
            requestParams.put("EMAIL", addressDetailsVO.getEmail());
            requestParams.put("PaymentMethod", "CB");
            requestParams.put("orderID", trackingID);
            requestParams.put("Operation", "SAL");
            requestParams.put("amount", String.valueOf(txnPaise));
            requestParams.put("currency", transDetailsVO.getCurrency());
            requestParams.put("IP", addressDetailsVO.getIp());
            requestParams.put("CustLastName", addressDetailsVO.getLastname());
            requestParams.put("CustFirstName", addressDetailsVO.getFirstname());
            requestParams.put("CustAddress1", addressDetailsVO.getStreet());
            requestParams.put("CustZIP", addressDetailsVO.getZipCode());
            requestParams.put("CustCity", addressDetailsVO.getCity());
            requestParams.put("CustCountry", addressDetailsVO.getCountry());
            requestParams.put("CustTel", addressDetailsVO.getPhone());
            requestParams.put("CARDNO", cardDetailsVO.getCardNum());
            requestParams.put("ED", cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear().substring(2));
            requestParams.put("CVC", cardDetailsVO.getcVV());
            requestParams.put("Signature", getSignature(orderID, amount, currency, NumSite, operation, CARDNO, merchantKey));

            NCResponse response = processRequest(requestParams, url);
            String status;
            String description;
            String error;

            if (response.getStatus().equalsIgnoreCase("OK") || response.getStatus().equalsIgnoreCase("5") || response.getStatus().equalsIgnoreCase("9"))
            {
                status = "success";
                description = response.getStatus() + " : " + response.getNcStatus();
                error = response.getNcError();
            }
            else
            {

                status = "fail";
                description = response.getNcError() + ":" + response.getNcErrorPlus() + ":" + response.getStatus();
                error = response.getNcError();

            }
            wSecResponseVO = new WSecResponseVO();
            wSecResponseVO.setStatus(status);
            wSecResponseVO.setDescription(description);
            wSecResponseVO.setErrorCode(error);
            wSecResponseVO.setMerchantOrderId(response.getOrderID());
            wSecResponseVO.setTransactionId(response.getPayID());
            wSecResponseVO.setIpCountry(response.getIpcty());
            wSecResponseVO.setCardCountry(response.getCccty());


        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processSale()", null, "common", "No SuchAlgorithm exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processSale()", null, "common", "IO exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return wSecResponseVO;
    }


    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        WSecResponseVO wSecResponseVO = null;
        try
        {
            WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = wSecRequestVO.getCommMerchantVO();
            GenericTransDetailsVO transDetailsVO = wSecRequestVO.getTransDetailsVO();
            String amountReceived = transDetailsVO.getAmount();
            CommCardDetailsVO cardDetailsVO = wSecRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = wSecRequestVO.getAddressDetailsVO();

            String merchantKey = wSecRequestVO.getKey();
            String url = wSecRequestVO.getSiteUrl();


            int txnPaise = (new BigDecimal(amountReceived).multiply(new BigDecimal(100.00))).intValue();

            HashMap<String, String> requestParams = new HashMap<String, String>();


            String NumSite = commMerchantAccountVO.getMerchantId();
            String orderID = trackingID;
            String amount = String.valueOf(txnPaise);
            String currency = transDetailsVO.getCurrency();
            String CARDNO = cardDetailsVO.getCardNum();
            String operation = RES;


            requestParams.put("NumSite", commMerchantAccountVO.getMerchantId());
            requestParams.put("Password", commMerchantAccountVO.getPassword());
            requestParams.put("EMAIL", addressDetailsVO.getEmail());
            requestParams.put("PaymentMethod", "CB");
            requestParams.put("orderID", trackingID);
            requestParams.put("Operation", "SAL");
            requestParams.put("amount", String.valueOf(txnPaise));
            requestParams.put("currency", transDetailsVO.getCurrency());
            requestParams.put("IP", addressDetailsVO.getIp());
            requestParams.put("CustLastName", addressDetailsVO.getLastname());
            requestParams.put("CustFirstName", addressDetailsVO.getFirstname());
            requestParams.put("CustAddress1", addressDetailsVO.getStreet());
            requestParams.put("CustZIP", addressDetailsVO.getZipCode());
            requestParams.put("CustCity", addressDetailsVO.getCity());
            requestParams.put("CustCountry", addressDetailsVO.getCountry());
            requestParams.put("CustTel", addressDetailsVO.getPhone());
            requestParams.put("CARDNO", cardDetailsVO.getCardNum());
            requestParams.put("ED", cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear().substring(2));
            requestParams.put("CVC", cardDetailsVO.getcVV());
            requestParams.put("Signature", getSignature(orderID, amount, currency, NumSite, operation, CARDNO, merchantKey));

            NCResponse response = processRequest(requestParams, url);
            String status;
            String description;
            String error;
            if (response.getStatus().equalsIgnoreCase("OK") || response.getStatus().equalsIgnoreCase("5") || response.getStatus().equalsIgnoreCase("9"))
            {
                status = "success";
                description = response.getStatus() + " : " + response.getNcStatus();
                error = response.getNcError();
            }
            else
            {
                status = "fail";
                description = response.getNcError() + ":" + response.getNcErrorPlus() + ":" + response.getStatus();
                error = response.getNcError();

            }
            wSecResponseVO = new WSecResponseVO();
            wSecResponseVO.setStatus(status);
            wSecResponseVO.setDescription(description);
            wSecResponseVO.setErrorCode(error);
            wSecResponseVO.setMerchantOrderId(response.getOrderID());
            wSecResponseVO.setTransactionId(response.getPayID());
            wSecResponseVO.setIpCountry(response.getIpcty());
            wSecResponseVO.setCardCountry(response.getCccty());


        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processAuthentication()", null, "common", "No SuchAlgorithm exception while authenticating transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processAuthentication()", null, "common", "IO exception while authenticating transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return wSecResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        WSecResponseVO wSecResponseVO = null;
        try
        {
            WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = wSecRequestVO.getCommMerchantVO();
            GenericTransDetailsVO transDetailsVO = wSecRequestVO.getTransDetailsVO();
            String amountReceived = transDetailsVO.getAmount();
            CommCardDetailsVO cardDetailsVO = wSecRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = wSecRequestVO.getAddressDetailsVO();

            String merchantKey = wSecRequestVO.getKey();
            String url = wSecRequestVO.getSiteUrl();

            int txnPaise = (new BigDecimal(amountReceived).multiply(new BigDecimal(100.00))).intValue();

            HashMap<String, String> requestParams = new HashMap<String, String>();


            String NumSite = commMerchantAccountVO.getMerchantId();
            String orderID = trackingID;
            String amount = String.valueOf(txnPaise);
            String currency = transDetailsVO.getCurrency();
            String CARDNO = cardDetailsVO.getCardNum();
            String operation = SAL;


            requestParams.put("NumSite", commMerchantAccountVO.getMerchantId());
            requestParams.put("Password", commMerchantAccountVO.getPassword());
            requestParams.put("EMAIL", addressDetailsVO.getEmail());
            requestParams.put("PaymentMethod", "CB");
            requestParams.put("orderID", trackingID);
            requestParams.put("Operation", "SAL");
            requestParams.put("amount", String.valueOf(txnPaise));
            requestParams.put("currency", transDetailsVO.getCurrency());
            requestParams.put("IP", addressDetailsVO.getIp());
            requestParams.put("CustLastName", addressDetailsVO.getLastname());
            requestParams.put("CustFirstName", addressDetailsVO.getFirstname());
            requestParams.put("CustAddress1", addressDetailsVO.getStreet());
            requestParams.put("CustZIP", addressDetailsVO.getZipCode());
            requestParams.put("CustCity", addressDetailsVO.getCity());
            requestParams.put("CustCountry", addressDetailsVO.getCountry());
            requestParams.put("CustTel", addressDetailsVO.getPhone());
            requestParams.put("CARDNO", cardDetailsVO.getCardNum());
            requestParams.put("ED", cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear().substring(2));
            requestParams.put("CVC", cardDetailsVO.getcVV());
            requestParams.put("Signature", getSignature(orderID, amount, currency, NumSite, operation, CARDNO, merchantKey));

            NCResponse response = processRequest(requestParams, maintenanceURL);
            String status;
            String description;
            String error;
            if (response.getStatus().equalsIgnoreCase("OK"))
            {
                status = "success";
                description = response.getStatus() + " : " + response.getNcStatus();
                error = response.getNcError();
            }
            else
            {

                status = "fail";
                description = response.getNcError() + ":" + response.getNcErrorPlus() + ":" + response.getStatus();
                error = response.getNcError();

            }
            wSecResponseVO = new WSecResponseVO();
            wSecResponseVO.setStatus(status);
            wSecResponseVO.setDescription(description);
            wSecResponseVO.setErrorCode(error);
            wSecResponseVO.setMerchantOrderId(response.getOrderID());
            wSecResponseVO.setTransactionId(response.getPayID());
            wSecResponseVO.setIpCountry(response.getIpcty());
            wSecResponseVO.setCardCountry(response.getCccty());


        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processVoid()", null, "common", "No SuchAlgorithm exception while cancelling transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processVoid()", null, "common", "IO exception while placing cancelling", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return wSecResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        WSecResponseVO wSecResponseVO = null;
        try
        {
            WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = wSecRequestVO.getCommMerchantVO();
            CommTransactionDetailsVO transDetailsVO = wSecRequestVO.getTransDetailsVO();
            CommAddressDetailsVO addressDetailsVO = wSecRequestVO.getAddressDetailsVO();
            CommCardDetailsVO cardDetailsVO = ((WSecRequestVO) requestVO).getCardDetailsVO();
            String amountReceived = wSecRequestVO.getTransDetailsVO().getAmount();
            int txnPaise = (new BigDecimal(amountReceived).multiply(new BigDecimal(100.00))).intValue();

            String merchantKey = wSecRequestVO.getKey();
            String url = wSecRequestVO.getSiteUrl();

            HashMap<String, String> requestParams = new HashMap<String, String>();

            String NumSite = commMerchantAccountVO.getMerchantId();
            String orderID = trackingID;
            String amount = String.valueOf(txnPaise);
            String currency = transDetailsVO.getCurrency();
            String CARDNO = cardDetailsVO.getCardNum();
            String operation = RFD;


            requestParams.put("NumSite", commMerchantAccountVO.getMerchantId());
            requestParams.put("Password", commMerchantAccountVO.getPassword());
            requestParams.put("PaymentMethod", "CB");
            requestParams.put("orderID", trackingID);
            requestParams.put("Operation", operation);
            requestParams.put("amount", String.valueOf(txnPaise));
            requestParams.put("currency", transDetailsVO.getCurrency());
            requestParams.put("IP", addressDetailsVO.getIp());

            requestParams.put("CustLastName", addressDetailsVO.getLastname());
            requestParams.put("CustFirstName", addressDetailsVO.getFirstname());
            requestParams.put("CustAddress1", addressDetailsVO.getStreet());
            requestParams.put("CustZIP", addressDetailsVO.getZipCode());
            requestParams.put("CustCity", addressDetailsVO.getCity());
            requestParams.put("CustCountry", addressDetailsVO.getCountry());
            requestParams.put("CustTel", addressDetailsVO.getPhone());
            requestParams.put("CARDNO", cardDetailsVO.getCardNum());
            requestParams.put("ED", cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear().substring(2));
            requestParams.put("CVC", cardDetailsVO.getcVV());
            requestParams.put("Signature", getSignature(orderID, amount, currency, NumSite, operation, CARDNO, merchantKey));

            NCResponse response = processRequest(requestParams, maintenanceURL);
            String status;
            String description;
            String error;
            if (response.getStatus().equalsIgnoreCase("OK"))
            {
                status = "success";
                description = response.getStatus() + " : " + response.getNcStatus();
                error = response.getNcError();
            }
            else
            {

                status = "fail";
                description = response.getNcError() + ":" + response.getNcErrorPlus() + ":" + response.getStatus();
                error = response.getNcError();

            }
            wSecResponseVO = new WSecResponseVO();
            wSecResponseVO.setStatus(status);
            wSecResponseVO.setDescription(description);
            wSecResponseVO.setErrorCode(error);
            wSecResponseVO.setMerchantOrderId(response.getOrderID());
            wSecResponseVO.setTransactionId(response.getPayID());
            wSecResponseVO.setIpCountry(response.getIpcty());
            wSecResponseVO.setCardCountry(response.getCccty());


        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processRefund()", null, "common", "No SuchAlgorithm exception while Refunding transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processRefund()", null, "common", "IO exception while placing Refunding", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return wSecResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        WSecResponseVO wSecResponseVO = null;
        try
        {
            WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
            CommMerchantVO commMerchantAccountVO = wSecRequestVO.getCommMerchantVO();
            GenericTransDetailsVO transDetailsVO = wSecRequestVO.getTransDetailsVO();
            String amountReceived = transDetailsVO.getAmount();
            CommCardDetailsVO cardDetailsVO = wSecRequestVO.getCardDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = wSecRequestVO.getAddressDetailsVO();

            String merchantKey = wSecRequestVO.getKey();
            String url = wSecRequestVO.getSiteUrl();


            int txnPaise = (new BigDecimal(amountReceived).multiply(new BigDecimal(100.00))).intValue();

            HashMap<String, String> requestParams = new HashMap<String, String>();


            String NumSite = commMerchantAccountVO.getMerchantId();
            String orderID = trackingID;
            String amount = String.valueOf(txnPaise);
            String currency = transDetailsVO.getCurrency();
            String CARDNO = cardDetailsVO.getCardNum();
            String operation = SAL;


            requestParams.put("NumSite", commMerchantAccountVO.getMerchantId());
            requestParams.put("Password", commMerchantAccountVO.getPassword());
            requestParams.put("EMAIL", addressDetailsVO.getEmail());
            requestParams.put("PaymentMethod", "CB");
            requestParams.put("orderID", trackingID);
            requestParams.put("Operation", "SAL");
            requestParams.put("amount", String.valueOf(txnPaise));
            requestParams.put("currency", transDetailsVO.getCurrency());
            requestParams.put("IP", addressDetailsVO.getIp());
            requestParams.put("CustLastName", addressDetailsVO.getLastname());
            requestParams.put("CustFirstName", addressDetailsVO.getFirstname());
            requestParams.put("CustAddress1", addressDetailsVO.getStreet());
            requestParams.put("CustZIP", addressDetailsVO.getZipCode());
            requestParams.put("CustCity", addressDetailsVO.getCity());
            requestParams.put("CustCountry", addressDetailsVO.getCountry());
            requestParams.put("CustTel", addressDetailsVO.getPhone());
            requestParams.put("CARDNO", cardDetailsVO.getCardNum());
            requestParams.put("ED", cardDetailsVO.getExpMonth() + "/" + cardDetailsVO.getExpYear().substring(2));
            requestParams.put("CVC", cardDetailsVO.getcVV());
            requestParams.put("Signature", getSignature(orderID, amount, currency, NumSite, operation, CARDNO, merchantKey));

            NCResponse response = processRequest(requestParams, maintenanceURL);
            String status;
            String description;
            String error;
            if (response.getStatus().equalsIgnoreCase("OK"))
            {
                status = "success";
                description = response.getStatus() + " : " + response.getNcStatus();
                error = response.getNcError();
            }
            else
            {

                status = "fail";
                description = response.getNcError() + ":" + response.getNcErrorPlus() + ":" + response.getStatus();
                error = response.getNcError();

            }
            wSecResponseVO = new WSecResponseVO();
            wSecResponseVO.setStatus(status);
            wSecResponseVO.setDescription(description);
            wSecResponseVO.setErrorCode(error);
            wSecResponseVO.setMerchantOrderId(response.getOrderID());
            wSecResponseVO.setTransactionId(response.getPayID());
            wSecResponseVO.setIpCountry(response.getIpcty());
            wSecResponseVO.setCardCountry(response.getCccty());


        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processCapture()", null, "common", "No SuchAlgorithm exception while capturing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(WSecPaymentGateway.class.getName(), "processCapture()", null, "common", "IO exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        return wSecResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO)
    {
        return null;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }

    private String getSignature(String orderId, String amount, String currency, String numSite, String operation, String cardno, String merchantKey) throws NoSuchAlgorithmException
    {
        String str = orderId + amount + currency + numSite + operation + cardno + merchantKey;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedSignature = getString(messageDigest.digest(str.getBytes()));
        return generatedSignature;
    }


    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    private NCResponse processRequest(HashMap<String, String> requestParamMap, String requestUrl) throws IOException
    {

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(requestUrl);

        NameValuePair[] params = new NameValuePair[requestParamMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : requestParamMap.entrySet())
        {

            params[i] = new NameValuePair(entry.getKey(), entry.getValue());
            i++;
        }
        post.setRequestBody(params);
        httpClient.executeMethod(post);
        String responseXML = new String(post.getResponseBody());
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.alias("ncresponse", NCResponse.class);
        logger.debug(responseXML);
        transactionLogger.debug(responseXML);
        NCResponse response = (NCResponse) xstream.fromXML(responseXML);



        return response;
    }


    public static void main(String[] args)
    {

    }


}

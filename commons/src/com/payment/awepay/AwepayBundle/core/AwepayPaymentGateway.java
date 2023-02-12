package com.payment.awepay.AwepayBundle.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.awepay.AwepayBundle.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.net.URLDecoder;
import java.util.ResourceBundle;

/**
 * Created by Admin on 11/27/2018.
 */
public class AwepayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AwepayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "awepay";
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.awepay");

    public AwepayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public static void main(String[] args) {   }

    @Override
    public String getMaxWaitDays() { return null; }


    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Awepay_Payment sale = new Awepay_Payment();

        // Set Auth
        sale.setSid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        sale.setRCode(GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH());

        // Set Billing Details
        sale.setFirstName(commAddressDetailsVO.getFirstname());
        sale.setLastName(commAddressDetailsVO.getLastname());
        sale.setEmail(commAddressDetailsVO.getEmail());
        sale.setPhoneNumber(commAddressDetailsVO.getPhone());
        sale.setBillingAddress(commAddressDetailsVO.getStreet());
        sale.setBillingCity(commAddressDetailsVO.getCity());
        sale.setBillingState(commAddressDetailsVO.getState());
        sale.setBillingPostCode(commAddressDetailsVO.getZipCode());
        sale.setBillingCountry(commAddressDetailsVO.getCountry());
        sale.setCustomerIPAddress(commAddressDetailsVO.getIp());

 /*       sale.setUsername(GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME());
        sale.setPassword(GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD());*/

        // Optionally set shipping details, using setShippingXxx()

        // Set Card Information
        sale.setCardName(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());
        sale.setCardNumber(commCardDetailsVO.getCardNum());
        sale.setCardExpiryMonth(commCardDetailsVO.getExpMonth()); // Preferrable set the month by 2 digits, zero padded. The month will be zero padded automatically.
        sale.setCardExpiryYear(commCardDetailsVO.getExpYear()); // Preferrably set the full 4 digit year, 2 digits will be prepended with "20" automatically
        sale.setCardCVV(commCardDetailsVO.getcVV());


        // Set Tracking Codes/Merchant Reference
        //sale.setReference1(trackingID);
        //sale.setWID("1234");
        sale.setTID(trackingID);
        sale.setPostbackUrl(RB.getString("POST_BACK_URL")+trackingID+"");


        // Set cart information
        sale.addItem(commTransactionDetailsVO.getMerchantOrderId(), "1", commTransactionDetailsVO.getAmount()); // name, quantity, amount per unit, [sku], [description]  --- 2 x 3.50 gives a total cost of $7.00

        // 3D Parameters and callback urls
        sale.setMD(trackingID); // Use a unique id - preferably the current session id - request.getSession().getId()
        sale.setRedirectUrl(RB.getString("REDIRECT_URL")+trackingID+""); // The url to return to upon 3D completion
        sale.setUserAgent("Apache-HttpClient/4.3.6 (java 1.5)"); // request.getHeader("User-Agent")
        sale.setBrowserAccepts("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"); // request.getHeader("Accept")

        Gson gObj = new Gson();
        transactionLogger.error("Sale Request --- " + gObj.toJson(sale));

        if (!sale.call())
        {
            transactionLogger.debug(sale.getErrors());
        }
        else
        {
            AwepayJavaBundle.displayResult(sale);

            String transactionId = sale.getTransactionId();
            String status = sale.getStatus();
            String errorCode = (sale.getResponseErrorCode() == null || sale.getResponseErrorCode().isEmpty() ? "No" : sale.getResponseErrorCode());
            String errorMessage = (sale.getResponseErrorMessage() == null || sale.getResponseErrorMessage().isEmpty() ? "No" : sale.getResponseErrorMessage());
            String threeDForm = "";

            transactionLogger.debug("Sale Transaction ID: " + transactionId);
            transactionLogger.debug("Sale Status: " + status);
            transactionLogger.debug("Sale Error Code: " + errorCode);
            transactionLogger.debug("Sale Error Message: " + errorMessage);
            transactionLogger.debug("Sale 3d Form: " + threeDForm);

            if(sale.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_APPROVED))
            {
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                commResponseVO.setStatus("Success");
                commResponseVO.setTransactionId(transactionId);
            }
            else  if (sale.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_3D))
            {
                threeDForm = sale.get3dForm();
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                transactionLogger.debug("3d html---"+URLDecoder.decode(threeDForm));
                commResponseVO.setStatus("Pending3dConfirmation");
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(threeDForm));
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setErrorCode(errorCode);
            }
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Awepay_Preauth preauth = new Awepay_Preauth();

        // Set Auth
        preauth.setSid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        preauth.setRCode(GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH());

        // Set Billing Details
        preauth.setFirstName(commAddressDetailsVO.getFirstname());
        preauth.setLastName(commAddressDetailsVO.getLastname());
        preauth.setEmail(commAddressDetailsVO.getEmail());
        preauth.setPhoneNumber(commAddressDetailsVO.getPhone());
        preauth.setBillingAddress(commAddressDetailsVO.getStreet());
        preauth.setBillingCity(commAddressDetailsVO.getCity());
        preauth.setBillingState(commAddressDetailsVO.getState());
        preauth.setBillingPostCode(commAddressDetailsVO.getZipCode());
        preauth.setBillingCountry(commAddressDetailsVO.getCountry());
        preauth.setCustomerIPAddress(commAddressDetailsVO.getIp());

        // Optionally set shipping details, using setShippingXxx()

        // Set Card Information
        preauth.setCardName(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());
        preauth.setCardNumber(commCardDetailsVO.getCardNum());
        preauth.setCardExpiryMonth(commCardDetailsVO.getExpMonth()); // Preferrable set the month by 2 digits, zero padded. The month will be zero padded automatically.
        preauth.setCardExpiryYear(commCardDetailsVO.getExpYear()); // Preferrably set the full 4 digit year, 2 digits will be prepended with "20" automatically
        preauth.setCardCVV(commCardDetailsVO.getcVV());

        // Set Tracking Codes/Merchant Reference
        //preauth.setReference1(trackingID);
        preauth.setTID(trackingID);
        preauth.setPostbackUrl(RB.getString("POST_BACK_URL")+trackingID+"");

        // Set cart information
        preauth.addItem(commTransactionDetailsVO.getMerchantOrderId(), "1", commTransactionDetailsVO.getAmount()); // name, quantity, amount per unit, [sku], [description]  --- 2 x 3.50 gives a total cost of $7.00

        // 3D Parameters and callback urls
        preauth.setMD(trackingID); // Use a unique id - preferably the current session id - request.getSession().getId()
        preauth.setRedirectUrl(RB.getString("REDIRECT_URL")+trackingID+""); // The url to return to upon 3D completion
        preauth.setUserAgent("Apache-HttpClient/4.3.6 (java 1.5)"); // request.getHeader("User-Agent")
        preauth.setBrowserAccepts("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"); // request.getHeader("Accept")

        Gson gObj = new Gson();
        transactionLogger.error("Preauth Request --- " + gObj.toJson(preauth));

        if (!preauth.call())
        {
          transactionLogger.debug(preauth.getErrors());
        }
        else
        {
            AwepayJavaBundle.displayResult(preauth);

            String transactionId = preauth.getTransactionId();
            String status = preauth.getStatus();
            String errorCode = (preauth.getResponseErrorCode() == null || preauth.getResponseErrorCode().isEmpty() ? "No" : preauth.getResponseErrorCode());
            String errorMessage = (preauth.getResponseErrorMessage() == null || preauth.getResponseErrorMessage().isEmpty() ? "No" :preauth.getResponseErrorMessage());
            String threeDForm = "";


            transactionLogger.debug("Preauth Transaction ID: " + transactionId);
            transactionLogger.debug("Preauth Status: " + status);
            transactionLogger.debug("Preauth Error Code: " + errorCode);
            transactionLogger.debug("Preauth Error Message: " + errorMessage);
            transactionLogger.debug("Preauth 3d Form: " + threeDForm);

            if(preauth.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_APPROVED))
            {
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                commResponseVO.setStatus("Success");
                commResponseVO.setTransactionId(transactionId);
            }
            else if(preauth.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_3D))
            {
                threeDForm = preauth.get3dForm();
                transactionLogger.debug("3d html---"+URLDecoder.decode(threeDForm));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                commResponseVO.setStatus("Pending3dConfirmation");
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(threeDForm));
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setErrorCode(errorCode);
            }

        }



        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Awepay_Settlement settlement = new Awepay_Settlement();

        // Set Auth
        settlement.setSid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        settlement.setRCode(GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH());

        // Set preauth reference
     transactionLogger.debug("in capture ----- "+ commTransactionDetailsVO.getPreviousTransactionId());
        settlement.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());

        if (!settlement.call())
        {
           transactionLogger.debug(settlement.getErrors());
        }
        else
        {
            AwepayJavaBundle.displayResult(settlement);

            String transactionId = settlement.getTransactionId();
            String status = settlement.getStatus();
            String errorCode = (settlement.getResponseErrorCode() == null || settlement.getResponseErrorCode().isEmpty() ? "No" :settlement.getResponseErrorCode());
            String errorMessage = (settlement.getResponseErrorMessage() == null || settlement.getResponseErrorMessage().isEmpty() ? "No" :settlement.getResponseErrorMessage());

            transactionLogger.debug("Capture Transaction ID: " + transactionId);
            transactionLogger.debug("Capture Status: " + status);
            transactionLogger.debug("Capture Error Code: " + errorCode);
            transactionLogger.debug("Capture Error Message: " + errorMessage);

            if(settlement.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_APPROVED))
            {
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                commResponseVO.setStatus("Success");
                commResponseVO.setTransactionId(transactionId);
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setErrorCode(errorCode);
            }

        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Awepay_Refund refund = new Awepay_Refund();

        // Set Auth
        refund.setSid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        refund.setRCode(GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH());

        // Set payment/preauth/settlement reference
        refund.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
        refund.setAmount(commTransactionDetailsVO.getAmount());
        refund.setReason(commTransactionDetailsVO.getOrderDesc());

        if (!refund.call())
        {
           transactionLogger.debug(refund.getErrors());
        }
        else
        {
            AwepayJavaBundle.displayResult(refund);

            String transactionId = refund.getTransactionId();
            String status = refund.getStatus();
            String errorCode = (refund.getResponseErrorCode() == null || refund.getResponseErrorCode().isEmpty() ? "No" :refund.getResponseErrorCode());
            String errorMessage = (refund.getResponseErrorMessage() == null || refund.getResponseErrorMessage().isEmpty() ? "No" :refund.getResponseErrorMessage());

            transactionLogger.debug("Refund Transaction ID: " + transactionId);
            transactionLogger.debug("Refund Status: " + status);
            transactionLogger.debug("Refund Error Code: " + errorCode);
            transactionLogger.debug("Refund Error Message: " + errorMessage);

            if(refund.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_APPROVED))
            {
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                commResponseVO.setStatus("Success");
                commResponseVO.setTransactionId(transactionId);
            }
            else if(refund.getStatus().equalsIgnoreCase("PEND") || refund.getStatus().equalsIgnoreCase("PENDING"))
            {
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                commResponseVO.setStatus("Pending");
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setErrorCode(errorCode);
            }

        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        Awepay_Refund refund = new Awepay_Refund();

        // Set Auth
        refund.setSid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
        refund.setRCode(GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH());

        // Set payment/preauth/settlement reference
        refund.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
        refund.setAmount(commTransactionDetailsVO.getAmount());
        refund.setReason(commTransactionDetailsVO.getOrderDesc());

        if (!refund.call())
        {
          transactionLogger.debug(refund.getErrors());
        }
        else
        {
            AwepayJavaBundle.displayResult(refund);

            String transactionId = refund.getTransactionId();
            String status = refund.getStatus();
            String errorCode = (refund.getResponseErrorCode() == null || refund.getResponseErrorCode().isEmpty() ? "No" :refund.getResponseErrorCode());
            String errorMessage = (refund.getResponseErrorMessage() == null || refund.getResponseErrorMessage().isEmpty() ? "No" :refund.getResponseErrorMessage());

            transactionLogger.debug("Void Transaction ID: " + transactionId);
            transactionLogger.debug("Void Status: " + status);
            transactionLogger.debug("Void Error Code: " + errorCode);
            transactionLogger.debug("Void Error Message: " + errorMessage);

            if(refund.getStatus().equalsIgnoreCase(Awepay_ApiBase.STATUS_APPROVED))
            {
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                commResponseVO.setStatus("Success");
                commResponseVO.setTransactionId(transactionId);
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark(errorMessage);
                commResponseVO.setDescription(errorMessage);
                commResponseVO.setErrorCode(errorCode);
            }

        }

        return commResponseVO;
    }
}

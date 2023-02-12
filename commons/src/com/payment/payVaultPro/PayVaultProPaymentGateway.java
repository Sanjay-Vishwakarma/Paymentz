package com.payment.payVaultPro;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Jeet Gupta on 12/10/18.
 */
public class PayVaultProPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="pvp";
    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.PayVaultPro");
    TransactionLogger transactionLogger= new TransactionLogger(PayVaultProPaymentGateway.class.getName());
    public PayVaultProPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.debug("---Inside Process Sale------");

        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String merchantId=gatewayAccount.getMerchantId();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else{
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----" + termUrl);
        }
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transactionDetailsVO.getCurrency())) {
            currency = transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }
        String paymentMode="";
        String paymentType=GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType().toString());
        if(paymentType.equals("CC"))
        {
            paymentMode="CreditCards";
        }
        else if(paymentType.equals("DC"))
        {
            paymentMode="DebitCards";
        }
        try
        {
            transactionLogger.debug("inside 3D process sale");
            if ("Y".equals(is3dSupported) || "O".equals(is3dSupported))
            {
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectLog = new JSONObject();

                jsonObject.put("mid", merchantId);
                jsonObject.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObject.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("email", commAddressDetailsVO.getEmail());
                jsonObject.put("phone", commAddressDetailsVO.getPhone());
                jsonObject.put("city", commAddressDetailsVO.getCity());
                jsonObject.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObject.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObject.put("paymentmode", paymentMode);
                jsonObject.put("orderid", trackingID);
                jsonObject.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObject.put("amount", transactionDetailsVO.getAmount());
                jsonObject.put("cardnum", commCardDetailsVO.getCardNum());
                jsonObject.put("expiryyear", commCardDetailsVO.getExpYear());
                jsonObject.put("expirymonth", commCardDetailsVO.getExpMonth());
                jsonObject.put("cardcvv", commCardDetailsVO.getcVV());
                jsonObject.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("domainname", "3dsy");
                jsonObject.put("paymenttype", "sale");
                jsonObject.put("response_url", termUrl+trackingID);

                jsonObjectLog.put("mid", merchantId);
                jsonObjectLog.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObjectLog.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("email", commAddressDetailsVO.getEmail());
                jsonObjectLog.put("phone", commAddressDetailsVO.getPhone());
                jsonObjectLog.put("city", commAddressDetailsVO.getCity());
                jsonObjectLog.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObjectLog.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObjectLog.put("paymentmode", paymentMode);
                jsonObjectLog.put("orderid", trackingID);
                jsonObjectLog.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObjectLog.put("amount", transactionDetailsVO.getAmount());
                jsonObjectLog.put("cardnum", functions.maskingPan(commCardDetailsVO.getCardNum()));
                jsonObjectLog.put("expiryyear", functions.maskingNumber(commCardDetailsVO.getExpYear()));
                jsonObjectLog.put("expirymonth", functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                jsonObjectLog.put("cardcvv", functions.maskingNumber(commCardDetailsVO.getcVV()));
                jsonObjectLog.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("domainname", "3dsy");
                jsonObjectLog.put("paymenttype", "sale");
                jsonObjectLog.put("response_url", termUrl+trackingID);

                transactionLogger.debug("sale request---"+trackingID+"--" + jsonObjectLog.toString());

                String saleResponse = "";
                if (isTest)
                {
                    saleResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_SALE_URL"), jsonObject.toString());
                }
                else
                {
                    saleResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_SALE_URL"), jsonObject.toString());
                }
                transactionLogger.error("---sale response--"+trackingID+"-- " + saleResponse);

                if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
                {
                    String status = "";
                    String code = "";
                    String message = "";
                    String amount = "";
                    String authcode = "";
                    String transactionid = "";
                    String orderid = "";
                    String redirecturl = "";

                    JSONObject jsonobj1 = new JSONObject(saleResponse);
                    if (jsonobj1 != null)
                    {
                        if (jsonobj1.has("status"))
                        {
                            status = jsonobj1.getString("status");
                        }
                        if (jsonobj1.has("code"))
                        {
                            code = jsonobj1.getString("code");
                        }
                        if (jsonobj1.has("message"))
                        {
                            message = jsonobj1.getString("message");
                        }
                        if (jsonobj1.has("amount"))
                        {
                            amount = jsonobj1.getString("amount");
                        }
                        if (jsonobj1.has("authcode"))
                        {
                            authcode = jsonobj1.getString("authcode");
                        }
                        if (jsonobj1.has("transactionid"))
                        {
                            transactionid = jsonobj1.getString("transactionid");
                        }
                        if (jsonobj1.has("orderid"))
                        {
                            orderid = jsonobj1.getString("orderid");
                        }
                        if (jsonobj1.has("redirecturl"))
                        {
                            redirecturl = jsonobj1.getString("redirecturl");
                        }

                       transactionLogger.error("status-" + status + "code-" + code + "message-" + message + "amount-" + amount + "authcode-" + authcode + "transactionid-" + transactionid + "orderid-" + orderid + "redirecturl-" + redirecturl);

                        if (status.equalsIgnoreCase("success") && code.equals("0"))
                        {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(redirecturl);
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                        }
                        if (functions.isValueNull(amount))
                        {
                            commResponseVO.setAmount(amount);
                        }
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setAuthCode(authcode);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());

                    }
                }
            }
            else
            {
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectLog = new JSONObject();

                jsonObject.put("mid", merchantId);
                jsonObject.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObject.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("email", commAddressDetailsVO.getEmail());
                jsonObject.put("phone", commAddressDetailsVO.getPhone());
                jsonObject.put("city", commAddressDetailsVO.getCity());
                jsonObject.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObject.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObject.put("paymentmode", paymentMode);
                jsonObject.put("orderid", trackingID);
                jsonObject.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObject.put("amount", transactionDetailsVO.getAmount());
                jsonObject.put("cardnum", commCardDetailsVO.getCardNum());
                jsonObject.put("expiryyear", commCardDetailsVO.getExpYear());
                jsonObject.put("expirymonth", commCardDetailsVO.getExpMonth());
                jsonObject.put("cardcvv", commCardDetailsVO.getcVV());
                jsonObject.put("cardholder",commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("domainname", "3dsn");
                jsonObject.put("paymenttype", "sale");
                jsonObject.put("response_url", termUrl);

                jsonObjectLog.put("mid", merchantId);
                jsonObjectLog.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObjectLog.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("email", commAddressDetailsVO.getEmail());
                jsonObjectLog.put("phone", commAddressDetailsVO.getPhone());
                jsonObjectLog.put("city", commAddressDetailsVO.getCity());
                jsonObjectLog.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObjectLog.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObjectLog.put("paymentmode", paymentMode);
                jsonObjectLog.put("orderid", trackingID);
                jsonObjectLog.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObjectLog.put("amount", transactionDetailsVO.getAmount());
                jsonObjectLog.put("cardnum", functions.maskingPan(commCardDetailsVO.getCardNum()));
                jsonObjectLog.put("expiryyear", functions.maskingNumber(commCardDetailsVO.getExpYear()));
                jsonObjectLog.put("expirymonth", functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                jsonObjectLog.put("cardcvv", functions.maskingNumber(commCardDetailsVO.getcVV()));
                jsonObjectLog.put("cardholder",commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("domainname", "3dsn");
                jsonObjectLog.put("paymenttype", "sale");
                jsonObjectLog.put("response_url", termUrl);

                transactionLogger.error("Non 3d_sale_request---"+trackingID+"--" + jsonObjectLog.toString());

                String saleResponse = "";
                if (isTest)
                {
                    saleResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_SALE_URL"), jsonObject.toString());
                }
                else
                {
                    saleResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_SALE_URL"), jsonObject.toString());
                }

                transactionLogger.error("---Non 3d_sale_response--"+trackingID+"--- " + saleResponse);

                if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
                {
                    String resStatus = "";
                    String status = "";
                    String code = "";
                    String message = "";
                    String amount = "";
                    String authcode = "";
                    String transactionid = "";
                    String orderid = "";
                    String redirecturl = "";

                    JSONObject jsonobj1 = new JSONObject(saleResponse);
                    if (jsonobj1 != null)
                    {
                        if (jsonobj1.has("status"))
                        {
                            resStatus = jsonobj1.getString("status");
                        }
                        if (jsonobj1.has("code"))
                        {
                            code = jsonobj1.getString("code");
                        }
                        if (jsonobj1.has("message"))
                        {
                            message = jsonobj1.getString("message");
                        }
                        if (jsonobj1.has("amount"))
                        {
                            amount = jsonobj1.getString("amount");
                        }
                        else
                        {
                            amount=transactionDetailsVO.getAmount();
                        }
                        if (jsonobj1.has("authcode"))
                        {
                            authcode = jsonobj1.getString("authcode");
                        }
                        if (jsonobj1.has("transactionid"))
                        {
                            transactionid = jsonobj1.getString("transactionid");
                        }
                        if (jsonobj1.has("orderid"))
                        {
                            orderid = jsonobj1.getString("orderid");
                        }
                        if (jsonobj1.has("redirecturl"))
                        {
                            redirecturl = jsonobj1.getString("redirecturl");
                        }

                        transactionLogger.error("resStatus-" + resStatus + "--code-" + code + "--message-" + message + "-amount-" + amount + "-authcode-" + authcode + "-transactionid-" + transactionid + "--orderid-" + orderid + "--redirecturl-" + redirecturl);

                        if (resStatus.equalsIgnoreCase("success") && code.equals("0"))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                        }
                       if (functions.isValueNull(amount))
                        {
                            commResponseVO.setAmount(amount);
                        }
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setAuthCode(authcode);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("---Inside Process Authentication-----");

        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String merchantId=gatewayAccount.getMerchantId();

        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else{
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----" + termUrl);
        }

        String paymentMode="";
        paymentMode=GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType().toString());
        if (paymentMode.equals("CC"))
        {
            paymentMode="CreditCards";
        }
        else if(paymentMode.equals("DC"))
        {
            paymentMode="DebitCards";
        }

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transactionDetailsVO.getCurrency())) {
            currency = transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount())) {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency())) {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }
        try
        {
            transactionLogger.error("inside 3D process Authentication ");
            if ("Y".equals(is3dSupported) || "O".equals(is3dSupported))
            {
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectLog = new JSONObject();

                jsonObject.put("mid", merchantId);
                jsonObject.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObject.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("email", commAddressDetailsVO.getEmail());
                jsonObject.put("phone", commAddressDetailsVO.getPhone());
                jsonObject.put("city", commAddressDetailsVO.getCity());
                jsonObject.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObject.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObject.put("paymentmode", paymentMode);
                jsonObject.put("orderid", trackingID);
                jsonObject.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObject.put("amount", transactionDetailsVO.getAmount());
                jsonObject.put("cardnum", commCardDetailsVO.getCardNum());
                jsonObject.put("expiryyear", commCardDetailsVO.getExpYear());
                jsonObject.put("expirymonth", commCardDetailsVO.getExpMonth());
                jsonObject.put("cardcvv", commCardDetailsVO.getcVV());
                jsonObject.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("domainname", "3dsy");
                jsonObject.put("paymenttype", "authorize");
                jsonObject.put("response_url", termUrl+trackingID);

                jsonObjectLog.put("mid", merchantId);
                jsonObjectLog.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObjectLog.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("email", commAddressDetailsVO.getEmail());
                jsonObjectLog.put("phone", commAddressDetailsVO.getPhone());
                jsonObjectLog.put("city", commAddressDetailsVO.getCity());
                jsonObjectLog.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObjectLog.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObjectLog.put("paymentmode", paymentMode);
                jsonObjectLog.put("orderid", trackingID);
                jsonObjectLog.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObjectLog.put("amount", transactionDetailsVO.getAmount());
                jsonObjectLog.put("cardnum", functions.maskingPan(commCardDetailsVO.getCardNum()));
                jsonObjectLog.put("expiryyear", functions.maskingNumber(commCardDetailsVO.getExpYear()));
                jsonObjectLog.put("expirymonth", functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                jsonObjectLog.put("cardcvv", functions.maskingNumber(commCardDetailsVO.getcVV()));
                jsonObjectLog.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("domainname", "3dsy");
                jsonObjectLog.put("paymenttype", "authorize");
                jsonObjectLog.put("response_url", termUrl+trackingID);

                transactionLogger.error(" 3D_Auth request---"+trackingID+"--" + jsonObjectLog.toString());

                String authResponse = "";
                if (isTest)
                {
                    authResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_AUTH_URL"), jsonObject.toString());
                }
                else
                {
                    authResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_AUTH_URL"), jsonObject.toString());
                }
                transactionLogger.error("---3D_Auth Response--"+trackingID+"-- " + authResponse);

                if (functions.isValueNull(authResponse) && authResponse.contains("{"))
                {
                    String status = "";
                    String code = "";
                    String message = "";
                    String amount = "";
                    String authcode = "";
                    String transactionid = "";
                    String orderid = "";
                    String redirecturl = "";

                    JSONObject jsonobj1 = new JSONObject(authResponse);
                    if (jsonobj1 != null)
                    {
                        if (jsonobj1.has("status"))
                        {
                            status = jsonobj1.getString("status");
                        }
                        if (jsonobj1.has("code"))
                        {
                            code = jsonobj1.getString("code");
                        }
                        if (jsonobj1.has("message"))
                        {
                            message = jsonobj1.getString("message");
                        }
                        if (jsonobj1.has("amount"))
                        {
                            amount = jsonobj1.getString("amount");
                        }
                        if (jsonobj1.has("authcode"))
                        {
                            authcode = jsonobj1.getString("authcode");
                        }
                        if (jsonobj1.has("transactionid"))
                        {
                            transactionid = jsonobj1.getString("transactionid");
                        }
                        if (jsonobj1.has("orderid"))
                        {
                            orderid = jsonobj1.getString("orderid");
                        }
                        if (jsonobj1.has("redirecturl"))
                        {
                            redirecturl = jsonobj1.getString("redirecturl");
                        }

                        transactionLogger.error("status-" + status + "code-" + code + "message-" + message + "amount-" + amount + "-authcode-" + authcode + "transactionid-" + transactionid + "orderid-" + orderid + "redirecturl-" + redirecturl);

                        if (status.equalsIgnoreCase("success") && code.equals("0"))
                        {
                            commResponseVO.setStatus("pending3DConfirmation");
                            commResponseVO.setUrlFor3DRedirect(redirecturl);
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                        }
                        if (functions.isValueNull(amount))
                        {
                            commResponseVO.setAmount(amount);
                        }
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setAuthCode(authcode);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                    }
                }
            }
            else
            {
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectLog = new JSONObject();

                jsonObject.put("mid", gatewayAccount.getMerchantId());
                jsonObject.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObject.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("email", commAddressDetailsVO.getEmail());
                jsonObject.put("phone", commAddressDetailsVO.getPhone());
                jsonObject.put("city", commAddressDetailsVO.getCity());
                jsonObject.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObject.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObject.put("paymentmode", paymentMode);
                jsonObject.put("orderid", trackingID);
                jsonObject.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObject.put("amount", transactionDetailsVO.getAmount());
                jsonObject.put("cardnum", commCardDetailsVO.getCardNum());
                jsonObject.put("expiryyear", commCardDetailsVO.getExpYear());
                jsonObject.put("expirymonth", commCardDetailsVO.getExpMonth());
                jsonObject.put("cardcvv", commCardDetailsVO.getcVV());
                jsonObject.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObject.put("domainname", "3dsn");
                jsonObject.put("paymenttype", "authorize");
                jsonObject.put("response_url", termUrl);


                jsonObjectLog.put("mid", gatewayAccount.getMerchantId());
                jsonObjectLog.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
                jsonObjectLog.put("fullname", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("email", commAddressDetailsVO.getEmail());
                jsonObjectLog.put("phone", commAddressDetailsVO.getPhone());
                jsonObjectLog.put("city", commAddressDetailsVO.getCity());
                jsonObjectLog.put("countrycode", commAddressDetailsVO.getCountry());
                jsonObjectLog.put("postalcode", commAddressDetailsVO.getZipCode());
                jsonObjectLog.put("paymentmode", paymentMode);
                jsonObjectLog.put("orderid", trackingID);
                jsonObjectLog.put("currencycode", transactionDetailsVO.getCurrency());
                jsonObjectLog.put("amount", transactionDetailsVO.getAmount());
                jsonObjectLog.put("cardnum", functions.maskingPan(commCardDetailsVO.getCardNum()));
                jsonObjectLog.put("expiryyear", functions.maskingNumber(commCardDetailsVO.getExpYear()));
                jsonObjectLog.put("expirymonth", functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                jsonObjectLog.put("cardcvv", functions.maskingNumber(commCardDetailsVO.getcVV()));
                jsonObjectLog.put("cardholder", commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());
                jsonObjectLog.put("domainname", "3dsn");
                jsonObjectLog.put("paymenttype", "authorize");
                jsonObjectLog.put("response_url", termUrl);

                transactionLogger.error(" Non3D_Auth request---"+trackingID+"--" + jsonObjectLog.toString());

                String authResponse = "";
                if (isTest)
                {
                    authResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_AUTH_URL"), jsonObject.toString());
                }
                else
                {
                    authResponse = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_AUTH_URL"), jsonObject.toString());
                }
                transactionLogger.error("---Non3D_Auth response--"+trackingID+"--" + authResponse);

                if (functions.isValueNull(authResponse) && authResponse.contains("{"))
                {
                    String status = "";
                    String code = "";
                    String message = "";
                    String amount = "";
                    String authcode = "";
                    String transactionid = "";
                    String orderid = "";
                    String redirecturl = "";

                    JSONObject jsonobj1 = new JSONObject(authResponse);
                    if (jsonobj1 != null)
                    {
                        if (jsonobj1.has("status"))
                        {
                            status = jsonobj1.getString("status");
                        }
                        if (jsonobj1.has("code"))
                        {
                            code = jsonobj1.getString("code");
                        }
                        if (jsonobj1.has("message"))
                        {
                            message = jsonobj1.getString("message");
                        }
                        if (jsonobj1.has("amount"))
                        {
                            amount = jsonobj1.getString("amount");
                        }
                        if (jsonobj1.has("authcode"))
                        {
                            authcode = jsonobj1.getString("authcode");
                        }
                        if (jsonobj1.has("transactionid"))
                        {
                            transactionid = jsonobj1.getString("transactionid");
                        }
                        if (jsonobj1.has("orderid"))
                        {
                            orderid = jsonobj1.getString("orderid");
                        }
                        if (jsonobj1.has("redirecturl"))
                        {
                            redirecturl = jsonobj1.getString("redirecturl");
                        }

                        transactionLogger.error("status-" + status + "code-" + code + "message-" + message + "amount-" + amount + "authcode-" + authcode + "transactionid-" + transactionid + "orderid-" + orderid + "redirecturl-" + redirecturl);

                        if (status.equalsIgnoreCase("success") && code.equals("0"))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                        }
                        if (functions.isValueNull(amount))
                        {
                            commResponseVO.setAmount(amount);
                        }
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setAuthCode(authcode);
                        commResponseVO.setTransactionId(transactionid);
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                        commResponseVO.setMerchantId(merchantId);
                    }
                }
            }
            commResponseVO.setCurrency(currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("---inside processCapture---");
        Functions functions =new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

         try
         {
            JSONObject jsonObject= new JSONObject();
            jsonObject.put("mid",gatewayAccount.getMerchantId());
            jsonObject.put("serverkey",gatewayAccount.getFRAUD_FTP_PASSWORD());
            jsonObject.put("transactionid",transactionDetailsVO.getPreviousTransactionId());
            jsonObject.put("amount",transactionDetailsVO.getAmount());

            transactionLogger.error("capture request---" + jsonObject.toString());

            String captureResponce="";
            if (isTest)
            {
                captureResponce=PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_CAPTURE_URL"), jsonObject.toString());
            }
            else
            {
                captureResponce=PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_CAPTURE_URL"), jsonObject.toString());
            }

            transactionLogger.error("capture response---"+ captureResponce);

            if(functions.isValueNull(captureResponce)&& captureResponce.contains("{"))
            {
                String status="";
                String code="";
                String message="";
                String orderid="";
                String transactionid="";
                String redirecturl="";
                String amount="";
                String authcode="";

                JSONObject jsonobj=new JSONObject(captureResponce);

                if (jsonobj !=null)
                {
                    if(jsonobj.has("status"))
                    {
                        status=jsonobj.getString("status");
                    }
                    if(jsonobj.has("code"))
                    {
                        code=jsonobj.getString("code");
                    }
                    if(jsonobj.has("message"))
                    {
                        message=jsonobj.getString("message");
                    }
                    if (jsonobj.has("orderid"))
                    {
                        orderid=jsonobj.getString("orderid");
                    }
                    if (jsonobj.has("transactionid"))
                    {
                        transactionid=jsonobj.getString("transactionid");
                    }
                    if (jsonobj.has("redirecturl"))
                    {
                        redirecturl=jsonobj.getString("redirecturl");
                    }
                    if (jsonobj.has("amount"))
                    {
                        amount = jsonobj.getString("amount");
                    }
                    if(jsonobj.has("authcode"))
                    {
                        authcode=jsonobj.getString("authcode");
                    }

                  transactionLogger.error("status-"+status+"code-"+code+"message-"+message+"orderid-"+orderid+"-transactionid-"+transactionid+"-redirecturl-"+redirecturl + "-amount-"+amount+"-authcode-"+authcode);

                    if (status.equalsIgnoreCase("success")&& code.equals("0"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                   if (functions.isValueNull(amount))
                    {
                        commResponseVO.setAmount(amount);
                    }
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setAuthCode(authcode);
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                }
            }

        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processCapture()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("---inside Process Void  ------------");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        try
        {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("mid", gatewayAccount.getMerchantId());
            jsonObject.put("serverkey", gatewayAccount.getFRAUD_FTP_PASSWORD());
            jsonObject.put("transactionid", transactionDetailsVO.getPreviousTransactionId());

            transactionLogger.error("Request parameter of processVoid---" + jsonObject.toString());

            String responceVoid = "";
            if (isTest)
            {
                responceVoid = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_VOID_URL"), jsonObject.toString());
            }
            else
            {
                responceVoid = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_VOID_URL"), jsonObject.toString());
            }
            transactionLogger.error("Response of ProcessVoid---" + responceVoid);

            if (functions.isValueNull(responceVoid) && responceVoid.contains("{"))
            {
                String status = "";
                String code = "";
                String message = "";
                String orderid = "";
                String transactionid = "";
                String redirecturl = "";
                String amount = "";
                String authcode = "";

                JSONObject jsonObj = new JSONObject(responceVoid);

                if (jsonObj != null)
                {
                    if (jsonObj.has("status"))
                    {
                        status = jsonObj.getString("status");
                    }
                    if (jsonObj.has("code"))
                    {
                        code = jsonObj.getString("code");
                    }
                    if (jsonObj.has("message"))
                    {
                        message = jsonObj.getString("message");
                    }
                    if (jsonObj.has("orderid"))
                    {
                        orderid = jsonObj.getString("orderid");
                    }
                    if (jsonObj.has("transactionid"))
                    {
                        transactionid = jsonObj.getString("transactionid");
                    }
                    if (jsonObj.has("redirecturl"))
                    {
                        redirecturl = jsonObj.getString("redirecturl");
                    }
                    if (jsonObj.has("amount"))
                    {
                        amount = jsonObj.getString("amount");
                    }
                    if (jsonObj.has("authcode"))
                    {
                        authcode = jsonObj.getString("authcode");
                    }
                    transactionLogger.error("status-" + status + "code-" + code + "message-" + message + "orderid-" + orderid + "transactionid-" + transactionid + "redirecturl-" + redirecturl + "amount-" + amount + "authcode-" + authcode);

                    if (status.equalsIgnoreCase("success") && code.equals("0"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    if (functions.isValueNull(amount))
                    {
                        commResponseVO.setAmount(amount);
                    }
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setAuthCode(authcode);
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO ;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("---inside Process Refund---");
        Functions functions =new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        try{
            JSONObject jsonObject= new JSONObject();
            jsonObject.put("mid",gatewayAccount.getMerchantId());
            jsonObject.put("serverkey",gatewayAccount.getFRAUD_FTP_PASSWORD());
            jsonObject.put("transactionid",transactionDetailsVO.getPreviousTransactionId());
            jsonObject.put("amount",transactionDetailsVO.getAmount());
            jsonObject.put("reason","Test Refund");

            transactionLogger.error("Refund Request---"+jsonObject.toString());

            String refundResp="";
            if (isTest)
            {
                refundResp=PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_REFUND_URL"), jsonObject.toString());
            }else
            {
                refundResp=PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_REFUND_URL"), jsonObject.toString());
            }
            transactionLogger.error("Refund Response---"+refundResp);

            if (functions.isValueNull(refundResp) && refundResp.contains("{"))
            {
                String status="";
                String code="";
                String message="";
                String orderid="";
                String transactionid="";
                String redirecturl="";
                String amount="";
                String authcode="";

                JSONObject json= new JSONObject(refundResp);
                if (json !=null)
                {
                    if (json.has("status"))
                    {
                        status=json.getString("status");
                    }
                    if (json.has("code"))
                    {
                        code=json.getString("code");
                    }
                    if (json.has("message"))
                    {
                        message=json.getString("message");
                    }
                    if (json.has("orderid"))
                    {
                        orderid=json.getString("orderid");
                    }
                    if (json.has("transactionid"))
                    {
                        transactionid = json.getString("transactionid");
                    }
                    if (json.has("redirecturl"))
                    {
                        redirecturl=json.getString("redirecturl");
                    }
                    if (json.has("amount"))
                    {
                        amount=json.getString("amount");
                    }
                    if (json.has("authcode"))
                    {
                        authcode=json.getString("authcode");
                    }

                    transactionLogger.error("status-" + status + "code-" + code + "message-" + message + "orderid-" + orderid + "transactionid-" + transactionid + "-redirecturl-" + redirecturl + "-amount-" + amount + "-authcode-" + authcode);

                    if (status.equalsIgnoreCase("success") && code.equals("0"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    if (functions.isValueNull(amount))
                    {
                        commResponseVO.setAmount(amount);
                    }

                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setAuthCode(authcode);
                    commResponseVO.setTransactionId(transactionid);
                }
            }

        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("inside process Inquiry");

        Functions functions =new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        try
        {
            JSONObject jsonObject= new JSONObject();

            jsonObject.put("mid",gatewayAccount.getMerchantId());
            jsonObject.put("serverkey",gatewayAccount.getFRAUD_FTP_PASSWORD());
            jsonObject.put("transactionid", transactionDetailsVO.getPreviousTransactionId());

            transactionLogger.error("Inquiry request---"+jsonObject.toString());

            String inquiryResp="";
             if (isTest)
             {
                 inquiryResp = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("TEST_INQUIRY_URL"), jsonObject.toString());
             }
            else
             {
                 inquiryResp = PayVaultProUtils.doPostHttpUrlConnection(RB.getString("LIVE_INQUIRY_URL"), jsonObject.toString());
             }

            transactionLogger.error("Inquiry response---"+inquiryResp);

            if (functions.isValueNull(inquiryResp) && inquiryResp.contains("{"))
            {
                String status="";
                String code="";
                String message="";
                String id_merchant="";
                String authcode="";
                String transactionid="";
                String orderid="";
                String paymenttype="";
                String txnamount="";
                String txnstatus="";
                String currencycode="";
                String originalamount="";

                JSONObject obj = new JSONObject(inquiryResp);
                if (obj !=null)
                {
                    if (obj.has("status"))
                    {
                        status = obj.getString("status");
                    }
                    if (obj.has("code"))
                    {
                        code = obj.getString("code");
                    }
                    if (obj.has("message"))
                    {
                        message = obj.getString("message");
                    }
                    if (obj.has("id_merchant"))
                    {
                        id_merchant = obj.getString("id_merchant");
                    }
                    if (obj.has("authcode"))
                    {
                        authcode = obj.getString("authcode");
                    }
                    if (obj.has("transactionid"))
                    {
                        transactionid = obj.getString("transactionid");
                    }
                    if (obj.has("orderid"))
                    {
                        orderid = obj.getString("orderid");
                    }
                    if (obj.has("paymenttype"))
                    {
                        paymenttype = obj.getString("paymenttype");
                    }
                    if (obj.has("txnamount"))
                    {
                        txnamount = obj.getString("txnamount");
                    }
                    if (obj.has("txnstatus"))
                    {
                        txnstatus = obj.getString("txnstatus");
                    }
                    if (obj.has("currencycode"))
                    {
                        currencycode = obj.getString("currencycode");
                    }
                    if (obj.has("originalamount"))
                    {
                        originalamount = obj.getString("originalamount");
                    }
                    transactionLogger.error("status-"+status+"-code-"+code+"-message-"+message+"-id_merchant-"+id_merchant+"-authcode-"+authcode+"-transactionid-"+transactionid+"-orderid-"+orderid+"-paymenttype-"+paymenttype+"-txnamount-"+txnamount+"-txnstatus-"+txnstatus+"-currencycode-"+currencycode+"-originalamount-"+originalamount);

                    if (status.equalsIgnoreCase("success") && code.equals("0"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("Transaction Successful");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionStatus("Transaction Failed");
                    }
                    if (functions.isValueNull(originalamount))
                    {
                        commResponseVO.setAmount(originalamount);
                    }

                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);
                    commResponseVO.setAuthCode(authcode);
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setCurrency(currencycode);
                    commResponseVO.setResponseTime("-");
                    commResponseVO.setMerchantId(id_merchant);
                   commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayVaultProPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
}

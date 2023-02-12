package com.payment.powercash21;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONException;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by Admin on 1/6/2022.
 */
public class PowerCash21PaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PowerCash21PaymentGateway.class.getName());

    private static Logger log = new Logger(PowerCash21PaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "powercash21";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PowerCash21");
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.3CharCountryList");


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PowerCash21PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("*******Inside PowerCash21PaymentGateway processAuthentication()**********");
        log.error("*******Inside PowerCash21PaymentGateway processAuthentication()**********");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();

        boolean isTest = gatewayAccount.isTest();

        String url_return = RB.getString("url_return") + trackingID;
        String notification_url = RB.getString("notification_url") + trackingID;
        String REQUEST_URL = "";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String amount = transactionDetailsVO.getAmount();
        String cardholder_name = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
        String ccn = commCardDetailsVO.getCardNum();
        String city = commAddressDetailsVO.getCity();



        String country="";
        if(commAddressDetailsVO.getCountry().length()!=3){
            country = RBTemplate.getString(commAddressDetailsVO.getCountry());
           transactionLogger.error("country inside if====>"+country);
        }
        else{
            country = commAddressDetailsVO.getCountry();
            transactionLogger.error("country inside else====>"+country);
        }


        String currency = transactionDetailsVO.getCurrency();
        String customerip = commAddressDetailsVO.getIp();
        String cvc_code = commCardDetailsVO.getcVV();
        String email = commAddressDetailsVO.getEmail();
        String exp_month = commCardDetailsVO.getExpMonth();
        String exp_year = commCardDetailsVO.getExpYear();
        String external_id = trackingID;//"6558646";
        String firstname = commAddressDetailsVO.getFirstname();
        String lastname = commAddressDetailsVO.getLastname();
        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID

        String orderid = trackingID;
        //String payment_method  = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());// 1
        String payment_method = "1";
        String signature = "";
        String state = commAddressDetailsVO.getState();
        String street = commAddressDetailsVO.getStreet();
        String zip = commAddressDetailsVO.getZipCode();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String custom1 = "test";
        String tmpl_amount = "";
        String tmpl_currency = "";

        if (isTest)
        {
            REQUEST_URL = RB.getString("TEST_AUTH_URL");
        }
        else
        {
            REQUEST_URL = RB.getString("TEST_AUTH_URL");
        }


        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }

        transactionLogger.error("AUTH_URL in processAuthentication =====> " + REQUEST_URL);
        transactionLogger.error("url_return in processAuthentication =====> " + url_return);
        transactionLogger.error("notification_url in processAuthentication =====> " + notification_url);
        transactionLogger.error("is3dSupported  ----" + is3dSupported);
        transactionLogger.error("isTest in processAuthentication  ===== " + isTest);


        StringBuffer reqforAuth = new StringBuffer();
        StringBuffer reqforAuthLog = new StringBuffer();
        try
        {
            TreeMap<String, String> request = new TreeMap<String, String>();

            request.put("amount", amount);
            request.put("cardholder_name", cardholder_name);
            request.put("ccn", ccn);
            request.put("city", city);
            request.put("country", country);
            request.put("currency", currency);
            request.put("custom1", custom1);
            request.put("customerip", customerip);
            request.put("cvc_code", cvc_code);
            request.put("email", email);
            request.put("exp_month", exp_month);
            request.put("exp_year", exp_year);
            request.put("external_id", external_id);
            request.put("firstname", firstname);
            request.put("lastname", lastname);
          //  request.put("merchantid", merchantid);
            request.put("orderid", orderid);
            request.put("payment_method", payment_method);
            request.put("state", state);
            request.put("street", street);
            request.put("zip", zip);


            TreeMap<String,String> requestLog=new TreeMap<String,String>();

            requestLog.put("amount", amount);
            requestLog.put("cardholder_name", functions.maskingFirstName(commAddressDetailsVO.getFirstname()) + " " + functions.maskingLastName(commAddressDetailsVO.getLastname()));
            requestLog.put("ccn", functions.maskingPan(ccn));
            requestLog.put("city", city);
            requestLog.put("country", country);
            requestLog.put("currency",currency);
            requestLog.put("custom1", custom1);
            requestLog.put("customerip", customerip);
            requestLog.put("cvc_code", functions.maskingNumber(cvc_code));
            requestLog.put("email", email);
            requestLog.put("exp_month", functions.maskingNumber(exp_month));
            requestLog.put("exp_year",functions.maskingNumber(exp_year));
            requestLog.put("external_id",external_id);
            requestLog.put("firstname", firstname);
            requestLog.put("lastname", lastname);
         //   requestLog.put("merchantid", merchantid);
            requestLog.put("orderid",orderid);
            requestLog.put("payment_method", payment_method);
            requestLog.put("signature", signature);
            requestLog.put("state",state);
            requestLog.put("street",street);
            requestLog.put("zip", zip);
            String signatureStr = "";
            if (is3dSupported.equalsIgnoreCase("Y"))
            {

                request.put("merchantid", merchantid3D);
                request.put("url_return", url_return);
                request.put("notification_url", notification_url);
                requestLog.put("merchantid", merchantid3D);

            }
            else{
                request.put("merchantid", merchantid);
                requestLog.put("merchantid", merchantid);
            }



            signature = PowerCash21Utils.getSignature(request, secret);
            transactionLogger.error("Signature==============>" + signature);


            request.put("signature", signature);

            for (String key : request.keySet())
            {
                if (reqforAuth.length() > 0)
                {
                    reqforAuth.append("&" + key + "=" + request.get(key));
                }
                else
                {
                    reqforAuth.append(key + "=" + request.get(key));

                }
            }

            for (String key : requestLog.keySet())
            {
                if (reqforAuthLog.length() > 0)
                {
                    reqforAuthLog.append("&" + key + "=" + requestLog.get(key));
                }
                else
                {
                    reqforAuthLog.append(key + "=" + requestLog.get(key));

                }
            }

         //   transactionLogger.error("Authentication  reqforAuth for Tracking Id======>" + trackingID + "----Is====" + request);
            transactionLogger.error("Authentication  reqforAuth for Tracking Id======>" + trackingID + "----Is====" + reqforAuthLog);


            String response = powerCash21Utils.doHttpPostConnection(REQUEST_URL, reqforAuth.toString());

            transactionLogger.error("Authentication Response for Tracking Id======>" + trackingID + "----Is====" + response);


            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);


            for (Map.Entry<String, String> m : saleres.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());
            }


            String transactionid = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";
            String url_3ds = "";

            if (saleres != null)
            {
                transactionid = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");
                url_3ds = saleres.get("url_3ds");


                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setCurrency(resCurrency);

                }
                else if ("2000".equalsIgnoreCase(status) && functions.isValueNull(url_3ds))
                {
                    transactionLogger.error("url_3ds for 3D Is=========>" + url_3ds);
                    String decodeurl = URLDecoder.decode(url_3ds);
                    transactionLogger.error("decodeurl for 3D Is=========>" + decodeurl);
                    transactionLogger.error("decodeurl for 3D Is=========>" + decodeurl + "Status====>" + status);
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(decodeurl);
                    commResponseVO.setRedirectUrl(decodeurl);
                    transactionLogger.error("URLFOR3DREdirect powercash21 processSale =====>" + commResponseVO.getRedirectUrl());
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid);

                }
                else if("-999".equalsIgnoreCase(status) || "-998".equalsIgnoreCase(status)|| "-997".equalsIgnoreCase(status)
                        || "-125".equalsIgnoreCase(status)  || "-117".equalsIgnoreCase(status) || "-119".equalsIgnoreCase(status)
                        || "-121".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errormessage);
                    commResponseVO.setErrorCode(status);
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
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("*******Inside PowerCash21PaymentGateway processSale()**********");
        log.error("*******Inside PowerCash21PaymentGateway processSale()**********");


        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();

        boolean isTest = gatewayAccount.isTest();

        String url_return = RB.getString("url_return") + trackingID;
        String notification_url = RB.getString("notification_url") + trackingID;
        String REQUEST_URL = "";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String amount = transactionDetailsVO.getAmount();
        String cardholder_name = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
        String ccn = commCardDetailsVO.getCardNum();
        String city = commAddressDetailsVO.getCity();
        String country="";
        if(commAddressDetailsVO.getCountry().length()!=3){
            country = RBTemplate.getString(commAddressDetailsVO.getCountry());
            transactionLogger.error("country inside if====>"+country);
        }
        else{
            country = commAddressDetailsVO.getCountry();
            transactionLogger.error("country inside else====>"+country);
        }
       // String country = RBTemplate.getString(commAddressDetailsVO.getCountry());
        //String country = commAddressDetailsVO.getCountry();
        String currency = transactionDetailsVO.getCurrency();
        String customerip = commAddressDetailsVO.getIp();
        String cvc_code = commCardDetailsVO.getcVV();
        String email = commAddressDetailsVO.getEmail();
        String exp_month = commCardDetailsVO.getExpMonth();
        String exp_year = commCardDetailsVO.getExpYear();
        String external_id =trackingID;// "6558646";
        String firstname = commAddressDetailsVO.getFirstname();
        String lastname = commAddressDetailsVO.getLastname();
        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID

        String orderid = trackingID;
        //String payment_method  = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());// 1
        String payment_method = "1";
        String signature = "";
        String state = commAddressDetailsVO.getState();
        String street = commAddressDetailsVO.getStreet();
        String zip = commAddressDetailsVO.getZipCode();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String recurring_id="INIT";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String custom1 = "test";


        if (isTest)
        {
            REQUEST_URL = RB.getString("TEST_SALE_URL");
        }
        else
        {
            REQUEST_URL = RB.getString("TEST_SALE_URL");
        }

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }
        transactionLogger.error("Sale_URL in processSale =====> " + REQUEST_URL);
        transactionLogger.error("is3dSupported  ----" + is3dSupported);
        transactionLogger.error("isTest in processSale  ===== " + isTest);

        transactionLogger.error("merchantid3D  ----" + merchantid3D);
        transactionLogger.error("merchantid  ----" + merchantid);
        transactionLogger.error("country  ----" + country);

        StringBuffer reqforSale = new StringBuffer();
        StringBuffer reqforSaleLog = new StringBuffer();
        Map<String, String> map = new HashMap<>();
        try
        {
            TreeMap<String, String> request = new TreeMap<String, String>();

            request.put("amount", amount);
            request.put("cardholder_name", cardholder_name);
            request.put("ccn", ccn);
            request.put("city", city);
            request.put("country", country);
            request.put("currency", currency);
            request.put("custom1", custom1);
            request.put("customerip", customerip);
            request.put("cvc_code", cvc_code);
            request.put("email", email);
            request.put("exp_month", exp_month);
            request.put("exp_year", exp_year);
            request.put("external_id", external_id);
            request.put("firstname", firstname);
            request.put("lastname", lastname);
           // request.put("merchantid", merchantid);
            request.put("orderid", orderid);
            request.put("payment_method", payment_method);
            request.put("recurring_id", recurring_id);
            request.put("state", state);
            request.put("street", street);
            request.put("zip", zip);


            TreeMap<String,String> requestLog=new TreeMap<String,String>();

            requestLog.put("amount", amount);
            requestLog.put("cardholder_name", functions.maskingFirstName(commAddressDetailsVO.getFirstname()) + " " + functions.maskingLastName(commAddressDetailsVO.getLastname()));
            requestLog.put("ccn", functions.maskingPan(ccn));
            requestLog.put("city", city);
            requestLog.put("country", country);
            requestLog.put("currency",currency);
            requestLog.put("custom1", custom1);
            requestLog.put("customerip", customerip);
            requestLog.put("cvc_code", functions.maskingNumber(cvc_code));
            requestLog.put("email", email);
            requestLog.put("exp_month", functions.maskingNumber(exp_month));
            requestLog.put("exp_year",functions.maskingNumber(exp_year));
            requestLog.put("external_id",external_id);
            requestLog.put("firstname", firstname);
            requestLog.put("lastname", lastname);
          //  requestLog.put("merchantid", merchantid);
            requestLog.put("orderid",orderid);
            requestLog.put("payment_method", payment_method);
            requestLog.put("recurring_id", recurring_id);
            requestLog.put("signature", signature);
            requestLog.put("state",state);
            requestLog.put("street",street);
            requestLog.put("zip", zip);
            String signatureStr = "";
            if (is3dSupported.equalsIgnoreCase("Y"))
            {

                request.put("merchantid", merchantid3D);
                request.put("url_return", url_return);
                request.put("notification_url", notification_url);
                requestLog.put("merchantid", merchantid3D);

            }
            else{
                request.put("merchantid", merchantid);
                requestLog.put("merchantid", merchantid);
            }


            signature = PowerCash21Utils.getSignature(request, secret);
            transactionLogger.error("Signature==============>" + signature);


            request.put("signature", signature);

            for (String key : request.keySet())
            {
                if (reqforSale.length() > 0)
                {
                    reqforSale.append("&" + key + "=" + request.get(key));
                }
                else
                {
                    reqforSale.append(key + "=" + request.get(key));

                }
            }
            for (String key : requestLog.keySet())
            {
                if (reqforSaleLog.length() > 0)
                {
                    reqforSaleLog.append("&" + key + "=" + requestLog.get(key));
                }
                else
                {
                    reqforSaleLog.append(key + "=" + requestLog.get(key));

                }
            }
          //  transactionLogger.error("Sale Request for Tracking Id======>" +trackingID+ "----Is====" + request);
            transactionLogger.error("Sale Request for Tracking Id======>" +trackingID+ "----Is====" + reqforSaleLog);

            String response = powerCash21Utils.doHttpPostConnection(REQUEST_URL, reqforSale.toString());

            transactionLogger.error("sale Response for Tracking Id======>" + trackingID + " ----Is====" + response);


            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);


            for (Map.Entry<String, String> m : saleres.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());

            }


            String transactionid = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";
            String url_3ds = "";

            if (saleres != null)
            {
                transactionid = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");
                url_3ds = saleres.get("url_3ds");

                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription("Successful");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setCurrency(resCurrency);

                }
                else if ("2000".equalsIgnoreCase(status) && functions.isValueNull(url_3ds))
                {
                    transactionLogger.error("url_3ds for 3D Is=========>" + url_3ds);
                    String decodeurl = URLDecoder.decode(url_3ds);
                    transactionLogger.error("decodeurl for 3D Is=========>" + decodeurl);
                    transactionLogger.error("decodeurl for 3D Is=========>" + decodeurl + "Status====>" + status);
                    commResponseVO.setStatus("pending3DConfirmation");
                    commResponseVO.setUrlFor3DRedirect(decodeurl);
                    commResponseVO.setRedirectUrl(decodeurl);
                    transactionLogger.error("URLFOR3DREdirect powercash21 processSale =====>" + commResponseVO.getRedirectUrl());
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);



                }
                else if("-999".equalsIgnoreCase(status) || "-998".equalsIgnoreCase(status)|| "-997".equalsIgnoreCase(status)
                        || "-125".equalsIgnoreCase(status)  || "-117".equalsIgnoreCase(status) || "-119".equalsIgnoreCase(status)
                        || "-121".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errormessage);
                    commResponseVO.setErrorCode(status);
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
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("*******Inside PowerCash21PaymentGateway processCapture()**********");
        log.error("*******Inside PowerCash21PaymentGateway processCapture()**********");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String amount = transactionDetailsVO.getAmount();

        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        boolean isTest = gatewayAccount.isTest();

        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID
        String transactionid = transactionDetailsVO.getPreviousTransactionId();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185

        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String signature = "";
        String REQUEST_URL = "";
        String response ="";

        transactionLogger.error("CAPTURE_URL in processCapture =====> " + REQUEST_URL);
        transactionLogger.error("isTest in processCapture  ===== " + isTest);

        String dbamount= powerCash21Utils.getDBAmount(trackingID);
        transactionLogger.error("dbamount in processCapture  ===== " + dbamount);
        try
        {

            if(dbamount.equals(amount))
            {

                if (isTest)
                {
                    REQUEST_URL = RB.getString("TEST_CAPTURE_URL");
                }
                else
                {
                    REQUEST_URL = RB.getString("TEST_CAPTURE_URL");
                }

                transactionLogger.error("CAPTURE_URL in processCapture =====> " + REQUEST_URL);
                if (is3dSupported.equalsIgnoreCase("Y"))
                {
                    signature = PowerCash21Utils.hashMac(merchantid3D + transactionid + secret);
                }
                else{
                    signature = PowerCash21Utils.hashMac(merchantid + transactionid + secret);
                }
                transactionLogger.error("signature in powercash ===== " + signature);


                StringBuffer request = new StringBuffer();
                if (is3dSupported.equalsIgnoreCase("Y"))
                {
                    request.append(
                            "merchantid=" + merchantid3D

                    );
                }
                else{
                    request.append(
                            "merchantid=" + merchantid);
                }
                request.append("&transactionid=" + transactionid
                        + "&signature=" + signature);
                transactionLogger.error("Powercash capture request for trackingid===== "+trackingID+"-----" + request);
                response = PowerCash21Utils.doHttpPostConnection(REQUEST_URL, request.toString());
                transactionLogger.error("capture response  for trackingid====="+trackingID+"=====>"+response);
            }
            else
            {
                if (isTest)
                {
                    REQUEST_URL = RB.getString("TEST_PARTIAL_CAPTURE_URL");
                }
                else
                {
                    REQUEST_URL = RB.getString("TEST_PARTIAL_CAPTURE_URL");
                }
                transactionLogger.error("PARTIAL CAPTURE_URL in processCapture =====> " + REQUEST_URL);
                if (is3dSupported.equalsIgnoreCase("Y"))
                {
                    signature = PowerCash21Utils.hashMac(amount + merchantid3D + transactionid + secret);
                }else{
                    signature = PowerCash21Utils.hashMac(amount + merchantid + transactionid + secret);
                }
                transactionLogger.error("signature in powercash ===== " + signature);

                StringBuffer parcialcapturequest = new StringBuffer();
                if (is3dSupported.equalsIgnoreCase("Y"))
                {
                    parcialcapturequest.append("amount=" + amount
                            + "&merchantid=" + merchantid3D
                            + "&transactionid=" + transactionid
                            + "&signature=" + signature);
                }
                else{
                    parcialcapturequest.append("amount=" + amount
                            + "&merchantid=" + merchantid
                            + "&transactionid=" + transactionid
                            + "&signature=" + signature);
                }

                transactionLogger.error("Powercash partial capture request for trackingid===== "+trackingID+"-----" + parcialcapturequest);

                response = PowerCash21Utils.doHttpPostConnection(REQUEST_URL, parcialcapturequest.toString());
                transactionLogger.error("Partial Capture Response for Tracking Id======>"+trackingID+ "---Is===>" + response);
            }
          //  transactionLogger.error("Capture Response for Tracking Id======>"+trackingID+ "---Is===>" + response);


            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);


            for (Map.Entry<String, String> m : saleres.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());

            }


            String transactionid1 = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";

            if (saleres != null)
            {
                transactionid1 = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");

                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setCurrency(resCurrency);
                    commResponseVO.setTransactionId(transactionid1);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);
                    commResponseVO.setCurrency(resCurrency);

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

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("*******Inside PowerCash21PaymentGateway processRefund()**********");
        log.error("*******Inside PowerCash21PaymentGateway processRefund()**********");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID
        String transactionid = transactionDetailsVO.getPreviousTransactionId();
        String amount = transactionDetailsVO.getAmount();
        String currency = transactionDetailsVO.getCurrency();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String signature = "";
        String REQUEST_URL = "";
        if (isTest)
        {
            REQUEST_URL = RB.getString("TEST_REFUND_URL");
        }
        else
        {
            REQUEST_URL = RB.getString("TEST_REFUND_URL");
        }

        transactionLogger.error("REFUND_URL in processRefund =====> " + REQUEST_URL);
        transactionLogger.error("isTest in processRefund  ===== " + isTest);


        try
        {
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(amount + currency + merchantid3D + transactionid + secret);
            }else
            {
                signature = powerCash21Utils.hashMac(amount + currency + merchantid + transactionid + secret);

            }
            transactionLogger.error("Signature==============>" + signature);
            StringBuffer request = new StringBuffer();
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append("amount=" + amount
                        + "&currency=" + currency
                        + "&merchantid=" + merchantid3D
                        + "&signature=" + signature
                        + "&transactionid=" + transactionid);
            }
            else
            {
                request.append("amount=" + amount
                        + "&currency=" + currency
                        + "&merchantid=" + merchantid
                        + "&signature=" + signature
                        + "&transactionid=" + transactionid);
            }
            transactionLogger.error("Refund Request for Tracking Id======>"+trackingID + "---Is==>" + request);

            String response = powerCash21Utils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionLogger.error("Refund Response for Tracking Id======>"+trackingID + "----Is===>" + response);


            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);


            for (Map.Entry<String, String> m : saleres.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());

            }


            String transactionid1 = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";

            if (saleres != null)
            {
                transactionid1 = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");

                if (("0".equalsIgnoreCase(status) && !functions.isValueNull(errormessage)) ||("2000".equalsIgnoreCase(status) && "pending".equalsIgnoreCase(errormessage)))
                {
                    commResponseVO.setStatus("success");
                    //commResponseVO.setRemark(errormessage);
                   // commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);

                }
                else if("-114".equalsIgnoreCase(status)){
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setTransactionStatus("pending");
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
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("*******Inside PowerCash21PaymentGateway processVoid()**********");
        log.error("*******Inside PowerCash21PaymentGateway processVoid()**********");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        boolean isTest = gatewayAccount.isTest();
        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID
        String transactionid = transactionDetailsVO.getPreviousTransactionId();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String signature = "";
        String REQUEST_URL = "";

        if (isTest)
        {
            REQUEST_URL = RB.getString("TEST_CANCEL_URL");
        }
        else
        {
            REQUEST_URL = RB.getString("TEST_CANCEL_URL");
        }

        transactionLogger.error("CANCEL_URL in processVoid =====> " + REQUEST_URL);
        transactionLogger.error("isTest in processVoid  ===== " + isTest);


        try
        {
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(merchantid3D + transactionid + secret);
            }
            else{
                signature = powerCash21Utils.hashMac(merchantid + transactionid + secret);
            }
            transactionLogger.error("Signature==============>" + signature);
            StringBuffer request = new StringBuffer();
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append(
                        "merchantid=" + merchantid3D

                );
            }
            else{
                request.append(
                        "merchantid=" + merchantid);
            }
            request.append("&transactionid=" + transactionid
                    + "&signature=" + signature);


            transactionLogger.error("Cancel Request for Tracking Id======>" +trackingID+ "----Is===>" + request);

            String response = powerCash21Utils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionLogger.error("Cancel Response for Tracking Id======>"+trackingID + "----Is==>" + response);

            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);


            for (Map.Entry<String, String> m : saleres.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());

            }


            String transactionid1 = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";

            if (saleres != null)
            {
                transactionid1 = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");

                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);

                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionId(transactionid1);
                    commResponseVO.setRemark(errormessage);
                    //commResponseVO.setDescription("SYS:3D Authentication Pending " + errmsg);
                    commResponseVO.setErrorCode(status);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("SYS:Transaction Declined");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;
    }


    //RECURRING
    public GenericResponseVO processRebilling(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {

        transactionLogger.error("*******Inside PowerCash21PaymentGateway processRebilling()**********");
        log.error("*******Inside PowerCash21PaymentGateway processRebilling()**********");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        GenericTransDetailsVO transDetailsVO=commRequestVO.getTransDetailsVO();
        boolean isTest = gatewayAccount.isTest();

        String url_return = RB.getString("url_return") + trackingId;
        String notification_url = RB.getString("notification_url") + trackingId;
        String REQUEST_URL = "";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String amount = transactionDetailsVO.getAmount();
        String city = commAddressDetailsVO.getCity();

        String country="";
        if(commAddressDetailsVO.getCountry().length()!=3){
            country = RBTemplate.getString(commAddressDetailsVO.getCountry());
            transactionLogger.error("country inside if====>"+country);
        }
        else{
            country = commAddressDetailsVO.getCountry();
            transactionLogger.error("country inside else====>"+country);
        }

        //String country = RBTemplate.getString(commAddressDetailsVO.getCountry());
        //String country = commAddressDetailsVO.getCountry();
        String currency = transactionDetailsVO.getCurrency();
        String customerip = commAddressDetailsVO.getIp();
        String email = commAddressDetailsVO.getEmail();
        String external_id =trackingId;// "6558646";
        String firstname = commAddressDetailsVO.getFirstname();
        String lastname = commAddressDetailsVO.getLastname();
        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D=gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID

        String orderid = trackingId;
        String payment_method = "1";
        String signature = "";
        String state = commAddressDetailsVO.getState();
        String street = commAddressDetailsVO.getStreet();
        String zip = commAddressDetailsVO.getZipCode();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String tmpl_amount = "";
        String tmpl_currency = "";
        String recurring_id=transDetailsVO.getPaymentid();

        if (isTest)
        {
            REQUEST_URL = RB.getString("TEST_RECURRING_URL");
        }
        else
        {
            REQUEST_URL = RB.getString("TEST_RECURRING_URL");
        }

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = commAddressDetailsVO.getTmpl_currency();
        }
        transactionLogger.error("Rcurring_URL in processRecurring =====> " + REQUEST_URL);
        transactionLogger.error("is3dSupported  ----" + is3dSupported);
        transactionLogger.error("isTest in processRecurring  ===== " + isTest);



        Map<String, String> map = new HashMap<>();
        try
        {
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(amount + city + country + currency + customerip + email + external_id + firstname + lastname + merchantid3D + orderid + payment_method + recurring_id + state + street + zip + secret);
            }
            else {
                signature = powerCash21Utils.hashMac(amount + city + country + currency + customerip + email + external_id + firstname + lastname + merchantid + orderid + payment_method + recurring_id + state + street + zip + secret);

            }
            transactionLogger.error("Signature==============>" + signature);
            StringBuffer request = new StringBuffer();

            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append("amount=" + amount
                                + "&city=" + city
                                + "&country=" + country
                                + "&currency=" + currency
                                + "&customerip=" + customerip
                                + "&email=" + email
                                + "&external_id=" + external_id
                                + "&firstname=" + firstname
                                + "&lastname=" + lastname
                                + "&merchantid=" + merchantid3D
                                + "&orderid=" + orderid
                                + "&payment_method=" + payment_method
                                + "&recurring_id=" + recurring_id
                                + "&signature=" + signature
                                + "&state=" + state
                                + "&street=" + street
                                + "&zip=" + zip
                );
            }
            else{
                request.append("amount=" + amount
                        + "&city=" + city
                        + "&country=" + country
                        + "&currency=" + currency
                        + "&customerip=" + customerip
                        + "&email=" + email
                        + "&external_id=" + external_id
                        + "&firstname=" + firstname
                        + "&lastname=" + lastname
                        + "&merchantid=" + merchantid
                        + "&orderid=" + orderid
                        + "&payment_method=" + payment_method
                        + "&recurring_id=" + recurring_id
                        + "&signature=" + signature
                        + "&state=" + state
                        + "&street=" + street
                        + "&zip=" + zip);
            }




            transactionLogger.error("Recurring Request for Tracking Id======>" + trackingId + "---Is====" + request);

            String response = powerCash21Utils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionLogger.error("Recurring Response for Tracking Id======>" + trackingId + "---Is====" + response);


            HashMap<String, String> saleres = powerCash21Utils.readresponsep21(response);



            String transactionid = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";
            String url_3ds = "";

            if (saleres != null)
            {
                transactionid = saleres.get("transactionid");
                status = saleres.get("status");
                errormessage = saleres.get("errormessage");
                errmsg = saleres.get("errmsg");
                resAmount = saleres.get("amount");
                resCurrency = saleres.get("currency");
                resOrderid = saleres.get("orderid");
                resPayment_method = saleres.get("payment_method");
                ccn_four = saleres.get("ccn_four");
                card_type = saleres.get("card_type");
                cardholder = saleres.get("cardholder");
                user_id = saleres.get("user_id");
                url_3ds = saleres.get("url_3ds");

                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid);
                   /* commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setCurrency(resCurrency);
*/
                }
                else if ("2000".equalsIgnoreCase(status))
                {

                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid);

                }
                else if("-999".equalsIgnoreCase(status) || "-998".equalsIgnoreCase(status)|| "-997".equalsIgnoreCase(status)
                        || "-125".equalsIgnoreCase(status)  || "-117".equalsIgnoreCase(status) || "-119".equalsIgnoreCase(status)
                        || "-121".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errormessage);
                    commResponseVO.setErrorCode(status);
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
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions=new Functions();
        CommAddressDetailsVO commAddressDetailsVO   = commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        PowerCash21Utils powerCash21Utils=new PowerCash21Utils();
        boolean isTest = gatewayAccount.isTest();
        String REQUEST_URL = "";
        String amount = transactionDetailsVO.getAmount();
        String ccn = commCardDetailsVO.getCardNum();
        String city = commAddressDetailsVO.getCity();

        String country="";
        if(commAddressDetailsVO.getCountry().length()!=3){
            country = RBTemplate.getString(commAddressDetailsVO.getCountry());
            transactionLogger.error("country inside if====>"+country);
        }
        else{
            country = commAddressDetailsVO.getCountry();
            transactionLogger.error("country inside else====>"+country);
        }


      //  String country = RBTemplate.getString(commAddressDetailsVO.getCountry());
      //  String country = commAddressDetailsVO.getCountry();
        String currency = transactionDetailsVO.getCurrency();
        String email = commAddressDetailsVO.getEmail();
        String external_id = trackingID;//"6558646";
        String firstname = commAddressDetailsVO.getFirstname();
        String lastname = commAddressDetailsVO.getLastname();
        String merchantid = gatewayAccount.getMerchantId();//NON-3D MID

        String merchantid3D      =gatewayAccount.getFRAUD_FTP_USERNAME();//3D MID
        String orderid           = trackingID;
        String payment_method    = "1";
        String signature         = "";
        String state             = commAddressDetailsVO.getState();
        String street              = commAddressDetailsVO.getStreet();
        String zip              = commAddressDetailsVO.getZipCode();
        String transactionid    =transactionDetailsVO.getPaymentId();
        String secret           = gatewayAccount.getFRAUD_FTP_PASSWORD();//b185
        String is3dSupported    = gatewayAccount.get_3DSupportAccount();


        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_PAYOUT_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("TEST_PAYOUT_URL");
            }
            transactionLogger.error("REQUEST_URL=====>" + REQUEST_URL);
            transactionLogger.error("amount=====>" + amount);
            transactionLogger.error("country=====>" + country);
            transactionLogger.error("city=====>" + city);
            transactionLogger.error("currency=====>" + currency);
            transactionLogger.error("email=====>" + email);
            transactionLogger.error("external_id=====>" + external_id);
            transactionLogger.error("firstname =====>" + firstname );
            transactionLogger.error("lastname ======>" + lastname);
            transactionLogger.error("merchantid=====>" + merchantid);
            transactionLogger.error("merchantid3D  =====>" + merchantid3D  );
            transactionLogger.error("orderid       =====>" + orderid       );
            transactionLogger.error("payment_method=====>" + payment_method);
            transactionLogger.error("signature     =====>" + signature     );
            transactionLogger.error("state         =====>" + state         );
            transactionLogger.error("street        =====>" + street        );
            transactionLogger.error("zip           =====>" + zip           );
            transactionLogger.error("transactionid =====>" + transactionid );
            transactionLogger.error("secret        =====>" + secret        );
            transactionLogger.error("is3dSupported =====>" + is3dSupported );


            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(amount + city + country + currency + email + external_id + firstname + lastname + merchantid3D + orderid + payment_method + state + street + transactionid + zip + secret);
            }
            else{
                signature = powerCash21Utils.hashMac(amount + city + country + currency + email + external_id + firstname + lastname + merchantid + orderid + payment_method + state + street + transactionid + zip + secret);

            }
            transactionLogger.error("signature in powercash ===== " + signature);
            StringBuffer request = new StringBuffer();
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append("amount=" + amount
                                + "&city=" + city
                                + "&country=" + country
                                + "&currency=" + currency
                                + "&email=" + email
                                + "&external_id=" + external_id
                                + "&firstname=" + firstname
                                + "&lastname=" + lastname
                                + "&merchantid=" + merchantid3D
                                + "&orderid=" + orderid
                                + "&payment_method=" + payment_method
                                + "&signature=" + signature
                                + "&state=" + state
                                + "&street=" + street
                                + "&transactionid=" + transactionid
                                + "&zip=" + zip
                );
            }
            else {
                request.append("amount=" + amount
                                + "&city=" + city
                                + "&country=" + country
                                + "&currency=" + currency
                                + "&email=" + email
                                + "&external_id=" + external_id
                                + "&firstname=" + firstname
                                + "&lastname=" + lastname
                                + "&merchantid=" + merchantid
                                + "&orderid=" + orderid
                                + "&payment_method=" + payment_method
                                + "&signature=" + signature
                                + "&state=" + state
                                + "&street=" + street
                                + "&transactionid=" + transactionid
                                + "&zip=" + zip
                );
            }


            transactionLogger.error("Payout Request for Tracking Id======>"+ trackingID+"---Is===> "+request);

            String response =powerCash21Utils.doHttpPostConnection(REQUEST_URL, request.toString());

            transactionLogger.error("Payout Response for Tracking Id======>"+trackingID+ "---Is ===>"+response);

            HashMap<String, String> payoutRes=powerCash21Utils.readresponsep21(response);
            for (Map.Entry<String, String> m : payoutRes.entrySet())
            {
                System.out.println("Key=" + m.getKey() + " Value=" + m.getValue());

            }


            String transactionid1 = "";
            String status = "";
            String errormessage = "";
            String errmsg = "";
            String resAmount = "";
            String resCurrency = "";
            String resOrderid = "";
            String resPayment_method = "";
            String ccn_four = "";
            String card_type = "";
            String cardholder = "";
            String user_id = "";

            if (payoutRes != null)
            {
                transactionid1      = payoutRes.get("transactionid");
                status              = payoutRes.get("status");
                errormessage        = payoutRes.get("errormessage");
                errmsg              = payoutRes.get("errmsg");
                resAmount           = payoutRes.get("amount");
                resCurrency         = payoutRes.get("currency");
                resOrderid          = payoutRes.get("orderid");
                resPayment_method   = payoutRes.get("payment_method");
                ccn_four            = payoutRes.get("ccn_four");
                card_type           = payoutRes.get("card_type");
                cardholder          = payoutRes.get("cardholder");
                user_id             = payoutRes.get("user_id");

                if (("0".equalsIgnoreCase(status) && !functions.isValueNull(errormessage)) ||("2000".equalsIgnoreCase(status) && "pending".equalsIgnoreCase(errormessage)))
                {
                    commResponseVO.setStatus("success");
                   // commResponseVO.setRemark(errormessage);
                   // commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);
                    commResponseVO.setCurrency(resCurrency);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark(errormessage);
                    commResponseVO.setDescription(errmsg);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(resPayment_method);
                    commResponseVO.setErrorCode(status);
                    commResponseVO.setTransactionId(transactionid1);
                    commResponseVO.setCurrency(resCurrency);

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
        catch (Exception e)
        {
            transactionLogger.error("processQuery Exception e--for--" + trackingID + "--", e);
        }

        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();
        String REQUEST_URL = "";
        String merchantid = gatewayAccount.getMerchantId();
        String merchantid3d = gatewayAccount.getFRAUD_FTP_USERNAME();

        String orderid = trackingID;
        String transactionid = transactionDetailsVO.getPreviousTransactionId();
        String type = "orderid";
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String signature = "";
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL");
            }

            transactionLogger.error("REQUEST_URL=====>" + REQUEST_URL);


            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(merchantid3d + orderid + type + secret);
            }
            else{
                signature = powerCash21Utils.hashMac(merchantid + orderid + type + secret);
            }
            transactionLogger.error("signature in powercash ===== " + signature);



            StringBuffer request = new StringBuffer();
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append(
                        "merchantid=" + merchantid3d

                );


            }
            else{

                request.append(
                        "merchantid=" + merchantid);
            }

            request.append("&orderid=" + orderid
                            + "&type=" + type
                            + "&signature=" + signature
            );


            transactionLogger.error("Payout Inquery request =====for Tracking Id===" + trackingID + "===>" + request);



            String response = powerCash21Utils.doPostHTTPSURLConnectionClient(REQUEST_URL, request.toString());
            transactionLogger.error("Payout Inquery response =====for Tracking Id====" + trackingID + "===>" + response);

            Map<String, String> inquiryres = powerCash21Utils.readSoapResponse(response);


            if (inquiryres != null)
            {
                transactionLogger.error("inquiryres map --- " + inquiryres);
                String error_message = inquiryres.get("error_message");
                String process_time = inquiryres.get("process_time");
                String status = inquiryres.get("status");
                String transaction_id = inquiryres.get("transaction_id");
                String amount = inquiryres.get("amount");
                String creation_date = inquiryres.get("creation_date");
                String currency = inquiryres.get("currency");
                String custom1 = inquiryres.get("custom1");
                String error_code = inquiryres.get("error_code");
                String order_id = inquiryres.get("order_id");
                String restype = inquiryres.get("type");


                if (("0".equalsIgnoreCase(status) && !functions.isValueNull(error_message)) ||("2000".equalsIgnoreCase(status) && "pending".equalsIgnoreCase(error_message)))

                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(error_message);
                    commResponseVO.setDescription(error_message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(restype);
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setResponseTime(creation_date);
                    if (is3dSupported.equalsIgnoreCase("Y"))
                    {
                        commResponseVO.setMerchantId(merchantid3d);
                    }
                    else{
                        commResponseVO.setMerchantId(merchantid);
                    }

                    commResponseVO.setAuthCode(error_code);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setBankTransactionDate(creation_date);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setRemark(error_message);
                    commResponseVO.setDescription("Transaction is Pending" + error_message);
                    commResponseVO.setTransactionType(restype);
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setResponseTime(creation_date);
                    commResponseVO.setMerchantId(merchantid);
                    commResponseVO.setAuthCode(error_code);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setBankTransactionDate(creation_date);

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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        PowerCash21Utils powerCash21Utils = new PowerCash21Utils();
        Functions functions = new Functions();
        boolean isTest = gatewayAccount.isTest();
        String REQUEST_URL = "";
        String merchantid = gatewayAccount.getMerchantId();
        String merchantid3d = gatewayAccount.getFRAUD_FTP_USERNAME();
        String orderid = trackingID;
        String transactionid = transactionDetailsVO.getPreviousTransactionId();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();

        String type = "orderid";


        String signature = "";
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        try
        {
            if (isTest)
            {
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("TEST_INQUIRY_URL");
            }
            transactionLogger.error("REQUEST_URL=====>" + REQUEST_URL);

            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                signature = powerCash21Utils.hashMac(merchantid3d + orderid + type + secret);
            }
            else{
                signature = powerCash21Utils.hashMac(merchantid + orderid + type + secret);
            }
            transactionLogger.error("signature in powercash ===== " + signature);



            StringBuffer request = new StringBuffer();
            if (is3dSupported.equalsIgnoreCase("Y"))
            {
                request.append(
                        "merchantid=" + merchantid3d

                );


            }
            else{


                request.append(
                        "merchantid=" + merchantid);
            }

            request.append("&orderid=" + orderid
                                + "&type=" + type
                                + "&signature=" + signature
                );


           transactionLogger.error("Inquery request =====for Tracking Id===" + trackingID + "===>" + request);


            String response = powerCash21Utils.doPostHTTPSURLConnectionClient(REQUEST_URL, request.toString());
            transactionLogger.error("Inquery response =====for Tracking Id====" + trackingID + "===>" + response);

            Map<String, String> inquiryres = powerCash21Utils.readSoapResponse(response);


            if (inquiryres != null)
            {
                transactionLogger.error("inquiryres map --- " + inquiryres);
                String error_message = inquiryres.get("error_message");
                String process_time = inquiryres.get("process_time");
                String status = inquiryres.get("status");
                String transaction_id = inquiryres.get("transaction_id");
                String amount = inquiryres.get("amount");
                String creation_date = inquiryres.get("creation_date");
                String currency = inquiryres.get("currency");
                String custom1 = inquiryres.get("custom1");
                String error_code = inquiryres.get("error_code");
                String order_id = inquiryres.get("order_id");
                String restype = inquiryres.get("type");


                if ("0".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(error_message);
                    commResponseVO.setDescription(error_message);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(restype);
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setResponseTime(creation_date);
                    if (is3dSupported.equalsIgnoreCase("Y"))
                    {
                    commResponseVO.setMerchantId(merchantid3d);
                    }
                    else{
                        commResponseVO.setMerchantId(merchantid);
                    }

                    commResponseVO.setAuthCode(error_code);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setBankTransactionDate(creation_date);

                }
                else if ("2000".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setRemark(error_message);
                    commResponseVO.setDescription("Transaction is Pending / " + error_message);
                    commResponseVO.setTransactionType(restype);
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setResponseTime(creation_date);
                    commResponseVO.setMerchantId(merchantid);
                    commResponseVO.setAuthCode(error_code);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setBankTransactionDate(creation_date);

                }
                else if("-999".equalsIgnoreCase(status) || "-998".equalsIgnoreCase(status)|| "-997".equalsIgnoreCase(status)
                        || "-125".equalsIgnoreCase(status) || "-117".equalsIgnoreCase(status) || "-119".equalsIgnoreCase(status)
                        || "-121".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionId(transactionid);
                    commResponseVO.setRemark(error_message);
                    commResponseVO.setDescription(error_message);
                    commResponseVO.setErrorCode(status);
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return commResponseVO;

    }
}
package com.payment.emexpay;

import com.directi.pg.EmexpayLogger;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.emexpay.vo.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by suneeta on 12/8/2017.
 */
public class EmexpayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "emexpay";
    TransactionLogger transactionLogger = new TransactionLogger(EmexpayPaymentGateway.class.getName());
    //EmexpayLogger emexpayLogger=new EmexpayLogger(EmexpayPaymentGateway.class.getName());

    ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.emexpay");
    Functions functions = new Functions();

    public EmexpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        TransactionLogger transactionLogger = new TransactionLogger(EmexpayPaymentGateway.class.getName());

        // System.out.println("getAmount---"+em.getAmount("10.22"));

        try
        {
            String shopid = "2692";
            //String shopid = "3190";
            String secretKey = "ee37341268848d9612417ea32ec13494b34a65687a2ce40bbeae750b29b75b6a";
            //String secretKey = "1c3a653126abbf17fa60cd47e86aa37ff6485ddc02b0ef29a111a9b6ca08df0d";

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            EmexpayUtils e = new EmexpayUtils();

            //String queryResponse= NetellerUtils.doGetHTTPSURLConnectionClient("https://paymentgateway.emexpay.com/transactions/tracking_id/1082856", "Basic", clientCredentials);
            //String queryResponse= NetellerUtils.doGetHTTPSURLConnectionClient("https://paymentgateway.emexpay.com/transactions/4757086-5aa6e246ee", "Basic", clientCredentials);

            String queryResponse= e.newHttpGet("https://paymentgateway.emexpay.com/transactions/tracking_id/1082856", "Basic", clientCredentials);
            //System.out.println("queryResponse---"+queryResponse);
            JSONObject jsonObject = new JSONObject(queryResponse);

            if(jsonObject.getJSONObject("transaction").getString("message").equalsIgnoreCase("Unsuccessful"))
                transactionLogger.debug("transaction declined");
            else
                transactionLogger.debug(jsonObject.getJSONObject("transaction").getString("message"));
                transactionLogger.debug("UID--"+jsonObject.getJSONObject("transaction").getString("uid"));

            EmexJsonVO emexJsonVO = new EmexJsonVO();
            request request = new request();

            credit_card creditCard = new credit_card();
            creditCard.setNumber("4444333322221111");
            creditCard.setVerification_value("123");
            creditCard.setExp_month("12");
            creditCard.setExp_year("2019");
            creditCard.setHolder("John Doe");

            billing_address billingAddress = new billing_address();
            billingAddress.setFirst_name("John");
            billingAddress.setLast_name("Doe");
            billingAddress.setAddress("Malad");
            billingAddress.setCity("Mumbai");
            billingAddress.setCountry("IN");
            billingAddress.setState("MH");
            billingAddress.setZip("400064");

            customer customer = new customer();
            customer.setIp("127.0.0.1");
            customer.setEmail("john@gmail.com");

            request.setAmount("50.00");
            request.setCurrency("EUR");
            request.setDescription("TestTran123");
            request.setTracking_id("52410");
            request.setLanguage("en");
            request.setTest(false);

            request.setBillingAddress(billingAddress);
            request.setCreditCard(creditCard);
            request.setCustomer(customer);

            emexJsonVO.setRequest(request);

            Gson gson = new Gson();
            String json = gson.toJson(emexJsonVO);
            transactionLogger.debug(json);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception EmexpayPaymentGateway :::",e);
        }


    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("----eneter's into processAuthentication------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantAccountVO = new CommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayVO emexpayVOlog = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String isCurrencyConversion = commRequestVO.getCurrencyConversion();
        String conversionCurrency = commRequestVO.getConversionCurrency();

        String transactionAmount = commTransactionDetailsVO.getAmount();
        String transactionCurrency = commTransactionDetailsVO.getCurrency();
        String terminalId = commTransactionDetailsVO.getTerminalId();

        transactionLogger.error("isCurrencyConversion:" + isCurrencyConversion);
        transactionLogger.error("conversionCurrency:" + conversionCurrency);
        transactionLogger.error("terminalId:" + terminalId);

        if ("Y".equals(isCurrencyConversion))
        {
            Double exchangeRate = null;
            if (functions.isValueNull(conversionCurrency))
            {
                exchangeRate = emexpayUtils.getExchangeRate(transactionCurrency, conversionCurrency);
            }
            else
            {
                transactionLogger.error("rejecting transaction because conversion currency has not defined");
                String gatewayMessage = "Conversion currency has not been defined";
                emexpayVO.setStatus("fail");
                emexpayVO.setRemark(gatewayMessage);
                emexpayVO.setTransactionType("sale");
                emexpayVO.setDescription(gatewayMessage);
                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                return emexpayVO;
            }
            if (exchangeRate != null)
            {
                transactionAmount = Functions.round(Double.valueOf(exchangeRate) * Double.valueOf(commTransactionDetailsVO.getAmount()), 2);
                transactionCurrency = conversionCurrency;
                TerminalManager terminalManager = new TerminalManager();
                if (functions.isValueNull(terminalId))
                {
                    TerminalVO terminalVO = terminalManager.getMemberTerminalfromTerminal(terminalId);
                    TerminalVO terminalVO1 = terminalManager.getMemberTerminalDetailsForTerminalChange(terminalVO.getMemberId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId(), transactionCurrency);
                    if (terminalVO1 != null)
                    {
                        GatewayAccount gatewayAccount1 = GatewayAccountService.getGatewayAccount(terminalVO1.getAccountId());
                        emexpayUtils.changeTerminalInfo(transactionAmount, transactionCurrency, String.valueOf(gatewayAccount1.getAccountId()), gatewayAccount1.getMerchantId(), commTransactionDetailsVO.getAmount(), commTransactionDetailsVO.getCurrency(), trackingID, terminalVO1.getTerminalId());
                        commTransactionDetailsVO.setAmount(transactionAmount);
                    }
                    else
                    {
                        transactionLogger.error("rejecting transaction because " + transactionCurrency + " terminal not defined");
                        String gatewayMessage = transactionCurrency + " terminal not defined";
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        return emexpayVO;
                    }
                }
                else
                {
                    transactionLogger.error("rejecting transaction because invalid terminal configuration ");
                    String gatewayMessage = "Invalid terminal configuration";
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    return emexpayVO;
                }

            }
            else
            {
                transactionLogger.error("rejecting transaction because exchange rates not found");
                String gatewayMessage = "Exchange rate has not been defined";
                emexpayVO.setStatus("fail");
                emexpayVO.setRemark(gatewayMessage);
                emexpayVO.setTransactionType("auth");
                emexpayVO.setDescription(gatewayMessage);
                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                return emexpayVO;

            }
            transactionLogger.error("FromCurrency:" + commTransactionDetailsVO.getCurrency());
            transactionLogger.error("transactionCurrency:" + transactionCurrency);
            transactionLogger.error("ExChangeRate:" + exchangeRate);
            transactionLogger.error("transactionAmount:" + transactionAmount);
        }

        String termUrl = "";
        if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/EMXPFrontEndServlet?trackingId=";
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from resource bundle----"+termUrl);
        }

        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();
            /*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();*/

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String is3DSupported = gatewayAccount.get_3DSupportAccount();

            if ("Y".equals(is3DSupported))
            {
                EmexJsonVO emexJsonVO = new EmexJsonVO();
                EmexJsonVO emexJsonVOlog = new EmexJsonVO();
                request request = new request();
                request requestlog = new request();

                credit_card creditCard = new credit_card();
                creditCard.setNumber(commCardDetailsVO.getCardNum());
                creditCard.setVerification_value(commCardDetailsVO.getcVV());
                creditCard.setExp_month(commCardDetailsVO.getExpMonth());
                creditCard.setExp_year(commCardDetailsVO.getExpYear());
                creditCard.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                credit_card creditCardlog = new credit_card();
                creditCardlog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
                creditCardlog.setVerification_value(functions.maskingNumber(commCardDetailsVO.getcVV()));
                creditCardlog.setExp_month(functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                creditCardlog.setExp_year(functions.maskingNumber(commCardDetailsVO.getExpYear()));
                creditCardlog.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                request.setCreditCard(creditCard);
                requestlog.setCreditCard(creditCardlog);


                billing_address billingAddress = new billing_address();
                billingAddress.setFirst_name(commAddressDetailsVO.getFirstname());
                billingAddress.setLast_name(commAddressDetailsVO.getLastname());
                billingAddress.setAddress(commAddressDetailsVO.getStreet());
                billingAddress.setCity(commAddressDetailsVO.getCity());
                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    billingAddress.setCountry(commAddressDetailsVO.getCountry());
                }else {
                    billingAddress.setCountry(commCardDetailsVO.getCountry_code_A2());
                }
                billingAddress.setState(commAddressDetailsVO.getState());
                billingAddress.setZip(commAddressDetailsVO.getZipCode());

                customer customer = new customer();
                if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                    customer.setIp(commAddressDetailsVO.getCardHolderIpAddress());
                else
                    customer.setIp(commAddressDetailsVO.getIp());
                customer.setEmail(commAddressDetailsVO.getEmail());

                request.setAmount(getAmount(transactionAmount));
                requestlog.setAmount(getAmount(transactionAmount));
                request.setCurrency(transactionCurrency);
                requestlog.setCurrency(transactionCurrency);
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    request.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    request.setDescription(commTransactionDetailsVO.getOrderDesc());
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    requestlog.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    requestlog.setDescription(commTransactionDetailsVO.getOrderDesc());

                request.setTracking_id(trackingID);
                request.setLanguage("en");
                request.setReturn_url(termUrl+trackingID);
                request.setTest(isTest);

                request.setBillingAddress(billingAddress);
                request.setCreditCard(creditCard);
                request.setCustomer(customer);

                requestlog.setTracking_id(trackingID);
                requestlog.setLanguage("en");
                requestlog.setReturn_url(termUrl+trackingID);
                requestlog.setTest(isTest);

                requestlog.setBillingAddress(billingAddress);
                requestlog.setCreditCard(creditCard);
                requestlog.setCustomer(customer);

                emexJsonVO.setRequest(request);
                emexJsonVOlog.setRequest(requestlog);

                Gson gson = new Gson();
                String authRequest = gson.toJson(emexJsonVO);
                String authRequestlog = gson.toJson(emexJsonVOlog);

                transactionLogger.error("authRequest-----"+trackingID + "--" + authRequestlog);
                String authResponse = "";

                if (isTest)
                {
                    authResponse = emexpayUtils.newHttpPost(RB.getString("TEST_AUTH_URL"), authRequest, "Basic", clientCredentials);
                }
                else
                {
                    authResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_AUTH_URL"), authRequest, "Basic", clientCredentials);
                }
                transactionLogger.error("authResponse----" +trackingID + "--" +  authResponse);

                String acs_url="";
                String pa_req="";
                String md="";
                String pa_res_url="";
                String authCode="";
                String ve_status = "";
                String status_3d = "";
                String message_3d = "";
                String bank_code = "";

                if (functions.isValueNull(authResponse) && authResponse.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(authResponse);
                    if (jsonObject.has("transaction"))
                    {
                        String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                        String uid = jsonObject.getJSONObject("transaction").getString("uid");
                        String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                        String message = jsonObject.getJSONObject("transaction").getString("message");


                        String rrn = "";
                        String ref_id = "";
                        String gateway_id = "";
                        String gateway_message = "";
                        if(jsonObject.getJSONObject("transaction").has("authorization"))
                        {
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("bank_code"))
                                bank_code = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("bank_code");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("rrn"))
                                rrn = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("rrn");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("ref_id"))
                                ref_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("ref_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("gateway_id"))
                                gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("gateway_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("message"))
                                gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("message");
                        }

                        if(jsonObject.getJSONObject("transaction").has("three_d_secure_verification"))
                        {
                            ve_status = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("ve_status");
                            status_3d = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("status");
                            message_3d = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("message");
                        }

                        if ("incomplete".equalsIgnoreCase(responseStatus))
                        {

                            if("Y".equals(ve_status))
                            {

                                acs_url = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("acs_url");
                                pa_req = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("pa_req");
                                md = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("md");
                                pa_res_url = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("pa_res_url");

                                emexpayVO.setUrlFor3DRedirect(acs_url);
                                emexpayVO.setPaReq(pa_req);
                                emexpayVO.setMd(md + "=" + pa_res_url);
                                emexpayVO.setStatus("pending3DConfirmation");
                                emexpayVO.setRemark(message_3d);
                                emexpayVO.setTerURL(RB.getString("TERM_URL") + trackingID);
                                emexpayVO.setVe_status(ve_status);
                                emexpayVO.setTransactionId(uid);
                                emexpayVO.setTransactionStatus(status_3d);
                                emexpayVO.setTransactionType(transactionType);
                                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                            else if(functions.isValueNull(jsonObject.getJSONObject("transaction").getString("redirect_url")))
                            {
                                emexpayVO.setUrlFor3DRedirect(jsonObject.getJSONObject("transaction").getString("redirect_url"));
                                emexpayVO.setStatus("pending3DConfirmation");
                                emexpayVO.setTerURL(RB.getString("TERM_URL") + trackingID);
                                emexpayVO.setTransactionId(uid);
                                emexpayVO.setTransactionStatus(responseStatus);
                                emexpayVO.setTransactionType(transactionType);
                                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                        }
                        else if ("N".equals(ve_status) || "successful".equalsIgnoreCase(status_3d) || "successful".equalsIgnoreCase(responseStatus))
                        {
                            authCode = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("auth_code");
                            String billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("billing_descriptor");
                            String holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                            String token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                            String stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                            String brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");
                            String address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                            String country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                            String city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                            String zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                            String state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");

                            if(functions.isValueNull(bank_code))
                            {
                                if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                                {
                                    emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setErrorCode(bank_code);
                                }
                                else
                                {
                                    emexpayVO.setRemark(message);
                                    emexpayVO.setDescription(message);
                                    emexpayVO.setErrorCode(bank_code);
                                }
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                            }
                            emexpayVO.setStatus("success");
                            emexpayVO.setVe_status(ve_status);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionStatus(status_3d);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setDescriptor(billinDescp);
                            emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                            emexpayVO.setHolder_name(holder_name);
                            emexpayVO.setToken(token);
                            emexpayVO.setStamp(stamp);
                            emexpayVO.setBrand(brand);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                        else if ("failed".equalsIgnoreCase(responseStatus) || "E".equals(ve_status))
                        {
                            if(functions.isValueNull(bank_code))
                            {
                                if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                                {
                                    emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setErrorCode(bank_code);
                                    emexpayVO.setErrorName(getErrorName(bank_code));
                                }
                                else
                                {
                                    emexpayVO.setRemark(message);
                                    emexpayVO.setDescription(message);
                                    emexpayVO.setErrorCode(bank_code);
                                    emexpayVO.setErrorName(getErrorName(bank_code));
                                }
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorName(getErrorName(bank_code));
                            }
                            emexpayVO.setStatus("fail");
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                    }
                    else if (jsonObject.has("response"))
                    {
                        String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
            }
            else
            {
                EmexJsonVO emexJsonVO = new EmexJsonVO();
                EmexJsonVO emexJsonVOlog = new EmexJsonVO();
                request request = new request();
                request requestlog = new request();

                credit_card creditCard = new credit_card();
                creditCard.setNumber(commCardDetailsVO.getCardNum());
                creditCard.setVerification_value(commCardDetailsVO.getcVV());
                creditCard.setExp_month(commCardDetailsVO.getExpMonth());
                creditCard.setExp_year(commCardDetailsVO.getExpYear());
                creditCard.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                credit_card creditCardlog = new credit_card();
                creditCardlog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
                creditCardlog.setVerification_value(functions.maskingNumber(commCardDetailsVO.getcVV()));
                creditCardlog.setExp_month(functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                creditCardlog.setExp_year(functions.maskingNumber(commCardDetailsVO.getExpYear()));
                creditCardlog.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                billing_address billingAddress = new billing_address();
                billingAddress.setFirst_name(commAddressDetailsVO.getFirstname());
                billingAddress.setLast_name(commAddressDetailsVO.getLastname());
                billingAddress.setAddress(commAddressDetailsVO.getStreet());
                billingAddress.setCity(commAddressDetailsVO.getCity());
                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    billingAddress.setCountry(commAddressDetailsVO.getCountry());
                }else {
                    billingAddress.setCountry(commCardDetailsVO.getCountry_code_A2());
                }
                billingAddress.setState(commAddressDetailsVO.getState());
                billingAddress.setZip(commAddressDetailsVO.getZipCode());

                customer customer = new customer();
                if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                    customer.setIp(commAddressDetailsVO.getCardHolderIpAddress());
                else
                    customer.setIp(commAddressDetailsVO.getIp());
                customer.setEmail(commAddressDetailsVO.getEmail());

                request.setAmount(getAmount(transactionAmount));
                request.setCurrency(transactionCurrency);
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    request.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    request.setDescription(commTransactionDetailsVO.getOrderDesc());
                request.setTracking_id(trackingID);
                request.setTest(isTest);

                request.setBillingAddress(billingAddress);
                request.setCreditCard(creditCard);
                request.setCustomer(customer);

                requestlog.setTracking_id(trackingID);
                requestlog.setTest(isTest);

                requestlog.setBillingAddress(billingAddress);
                requestlog.setCreditCard(creditCardlog);
                requestlog.setCustomer(customer);

                emexJsonVO.setRequest(request);
                emexJsonVOlog.setRequest(requestlog);

                Gson gson = new Gson();
                String authRequest = gson.toJson(emexJsonVO);
                String authRequestlog = gson.toJson(emexJsonVOlog);

                transactionLogger.error("authRequest-----"+trackingID + "--" + authRequestlog);
                String authResponse = "";

                if (isTest)
                {
                    authResponse = emexpayUtils.newHttpPost(RB.getString("TEST_AUTH_URL"), authRequest, "Basic", clientCredentials);
                }
                else
                {
                    authResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_AUTH_URL"), authRequest, "Basic", clientCredentials);
                }
                transactionLogger.error("authResponse----" +trackingID + "--" +  authResponse);

                String bank_code = "";
                String rrn = "";
                String ref_id = "";
                String gateway_id = "";
                String gateway_message = "";
                if (functions.isValueNull(authResponse) && authResponse.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(authResponse);

                    if (jsonObject.has("transaction"))
                    {
                        String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                        String uid = jsonObject.getJSONObject("transaction").getString("uid");
                        String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                        String message = jsonObject.getJSONObject("transaction").getString("message");

                        if(jsonObject.getJSONObject("transaction").has("authorization"))
                        {
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("bank_code"))
                                bank_code = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("bank_code");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("rrn"))
                                rrn = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("rrn");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("ref_id"))
                                ref_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("ref_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("gateway_id"))
                                gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("gateway_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("message"))
                                gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("message");
                        }
                        if(functions.isValueNull(bank_code))
                        {
                            if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                            {
                                emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setErrorCode(bank_code);
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorCode(bank_code);
                            }
                        }
                        else
                        {
                            emexpayVO.setRemark(message);
                            emexpayVO.setDescription(message);
                        }

                        if ("successful".equalsIgnoreCase(responseStatus))
                        {
                            String billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("billing_descriptor");
                            String holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                            String token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                            String stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                            String brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");
                            String address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                            String country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                            String city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                            String zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                            String state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");
                            String authCode = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("auth_code");

                            emexpayVO.setStatus("success");
                            emexpayVO.setRemark(message);
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setDescription(message);
                            emexpayVO.setDescriptor(billinDescp);
                            emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                            emexpayVO.setHolder_name(holder_name);
                            emexpayVO.setToken(token);
                            emexpayVO.setStamp(stamp);
                            emexpayVO.setBrand(brand);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setErrorCode(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        }
                        else
                        {
                            emexpayVO.setStatus("fail");
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setErrorName(getErrorName(bank_code));
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setErrorCode(bank_code);
                        }

                    }else if (jsonObject.has("response"))
                    {
                        String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        return emexpayVO;

    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("----eneter's into processSale------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String isCurrencyConversion = commRequestVO.getCurrencyConversion();
        String conversionCurrency = commRequestVO.getConversionCurrency();

        String transactionAmount = commTransactionDetailsVO.getAmount();
        String transactionCurrency = commTransactionDetailsVO.getCurrency();
        String terminalId = commTransactionDetailsVO.getTerminalId();

        transactionLogger.error("isCurrencyConversion:" + isCurrencyConversion);
        transactionLogger.error("conversionCurrency:" + conversionCurrency);
        transactionLogger.error("terminalId:" + terminalId);

        if ("Y".equals(isCurrencyConversion))
        {
            Double exchangeRate = null;
            if (functions.isValueNull(conversionCurrency))
            {
                exchangeRate = emexpayUtils.getExchangeRate(transactionCurrency, conversionCurrency);
            }
            else
            {
                transactionLogger.error("rejecting transaction because conversion currency has not defined");
                String gatewayMessage = "Conversion currency has not been defined";
                emexpayVO.setStatus("fail");
                emexpayVO.setRemark(gatewayMessage);
                emexpayVO.setTransactionType("sale");
                emexpayVO.setDescription(gatewayMessage);
                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                return emexpayVO;
            }
            if (exchangeRate != null)
            {
                transactionAmount = Functions.round(Double.valueOf(exchangeRate) * Double.valueOf(commTransactionDetailsVO.getAmount()), 2);
                transactionCurrency = conversionCurrency;
                TerminalManager terminalManager = new TerminalManager();
                if (functions.isValueNull(terminalId))
                {
                    TerminalVO terminalVO = terminalManager.getMemberTerminalfromTerminal(terminalId);
                    TerminalVO terminalVO1 = terminalManager.getMemberTerminalDetailsForTerminalChange(terminalVO.getMemberId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId(), transactionCurrency);
                    if (terminalVO1 != null)
                    {
                        GatewayAccount gatewayAccount1 = GatewayAccountService.getGatewayAccount(terminalVO1.getAccountId());
                        emexpayUtils.changeTerminalInfo(transactionAmount, transactionCurrency, String.valueOf(gatewayAccount1.getAccountId()), gatewayAccount1.getMerchantId(), commTransactionDetailsVO.getAmount(), commTransactionDetailsVO.getCurrency(), trackingID, terminalVO1.getTerminalId());
                        commTransactionDetailsVO.setAmount(transactionAmount);
                    }
                    else
                    {
                        transactionLogger.error("rejecting transaction because " + transactionCurrency + " terminal not defined");
                        String gatewayMessage = transactionCurrency + " terminal not defined";
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        return emexpayVO;
                    }
                }
                else
                {
                    transactionLogger.error("rejecting transaction because invalid terminal configuration ");
                    String gatewayMessage = "Invalid terminal configuration";
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    return emexpayVO;
                }

            }
            else
            {
                transactionLogger.error("rejecting transaction because exchange rates not found");
                String gatewayMessage = "Exchange rate has not been defined";
                emexpayVO.setStatus("fail");
                emexpayVO.setRemark(gatewayMessage);
                emexpayVO.setTransactionType("sale");
                emexpayVO.setDescription(gatewayMessage);
                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                return emexpayVO;

            }
            transactionLogger.error("FromCurrency:" + commTransactionDetailsVO.getCurrency());
            transactionLogger.error("transactionCurrency:" + transactionCurrency);
            transactionLogger.error("ExChangeRate:" + exchangeRate);
            transactionLogger.error("transactionAmount:" + transactionAmount);
        }

        String termUrl = "";
        if (functions.isValueNull(commMerchantAccountVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantAccountVO.getHostUrl()+"/transaction/EMXPFrontEndServlet?trackingId=";
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from resource bundle----"+termUrl);
        }


        try
        {
           /* DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();*/

            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String is3DSupported = gatewayAccount.get_3DSupportAccount();

            if ("Y".equals(is3DSupported))
            {
                EmexJsonVO emexJsonVO = new EmexJsonVO();
                EmexJsonVO emexJsonVOlog = new EmexJsonVO();
                request request = new request();
                request requestlog = new request();

                credit_card creditCard = new credit_card();
                creditCard.setNumber(commCardDetailsVO.getCardNum());
                creditCard.setVerification_value(commCardDetailsVO.getcVV());
                creditCard.setExp_month(commCardDetailsVO.getExpMonth());
                creditCard.setExp_year(commCardDetailsVO.getExpYear());
                creditCard.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                credit_card creditCardlog = new credit_card();
                creditCardlog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
                creditCardlog.setVerification_value(functions.maskingNumber(commCardDetailsVO.getcVV()));
                creditCardlog.setExp_month(functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                creditCardlog.setExp_year(functions.maskingNumber(commCardDetailsVO.getExpYear()));
                creditCardlog.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());


                transactionLogger.debug("bin country name-----"+commCardDetailsVO.getCountry_code_A2());

                billing_address billingAddress = new billing_address();
                billingAddress.setFirst_name(commAddressDetailsVO.getFirstname());
                billingAddress.setLast_name(commAddressDetailsVO.getLastname());
                billingAddress.setAddress(commAddressDetailsVO.getStreet());
                billingAddress.setCity(commAddressDetailsVO.getCity());
                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    billingAddress.setCountry(commAddressDetailsVO.getCountry());
                }else {
                    billingAddress.setCountry(commCardDetailsVO.getCountry_code_A2());
                }
                billingAddress.setState(commAddressDetailsVO.getState());
                billingAddress.setZip(commAddressDetailsVO.getZipCode());

                customer customer = new customer();
                if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                    customer.setIp(commAddressDetailsVO.getCardHolderIpAddress());
                else
                    customer.setIp(commAddressDetailsVO.getIp());
                customer.setEmail(commAddressDetailsVO.getEmail());

                request.setAmount(getAmount(transactionAmount));
                requestlog.setAmount(getAmount(transactionAmount));
                request.setCurrency(transactionCurrency);
                requestlog.setCurrency(transactionCurrency);
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    request.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    request.setDescription(commTransactionDetailsVO.getOrderDesc());
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    requestlog.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    requestlog.setDescription(commTransactionDetailsVO.getOrderDesc());
                request.setTracking_id(trackingID);
                request.setLanguage("en");
                request.setReturn_url(termUrl+trackingID);
                request.setTest(isTest);

                request.setBillingAddress(billingAddress);
                request.setCreditCard(creditCard);
                request.setCustomer(customer);

                requestlog.setTracking_id(trackingID);
                requestlog.setLanguage("en");
                requestlog.setReturn_url(termUrl+trackingID);
                requestlog.setTest(isTest);

                requestlog.setBillingAddress(billingAddress);
                requestlog.setCreditCard(creditCardlog);
                requestlog.setCustomer(customer);

                emexJsonVO.setRequest(request);
                emexJsonVOlog.setRequest(requestlog);

                Gson gson = new Gson();
                String saleRequest = gson.toJson(emexJsonVO);
                String saleRequestlog = gson.toJson(emexJsonVOlog);

                transactionLogger.error("SaleRequest 3D-----"+trackingID + "--" + saleRequestlog);
                String saleResponse = "";

                if (isTest)
                {
                    saleResponse = emexpayUtils.newHttpPost(RB.getString("TEST_SALE_URL"), saleRequest, "Basic", clientCredentials);
                }
                else
                {
                    saleResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_SALE_URL"), saleRequest, "Basic", clientCredentials);
                }
                transactionLogger.error("saleResponse 3D----" + saleResponse);

                String acs_url="";
                String pa_req="";
                String md="";
                String pa_res_url="";
                String authCode="";
                String ve_status = "";
                String status_3d = "";
                String message_3d = "";
                String bank_code = "";

                if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(saleResponse);
                    if (jsonObject.has("transaction"))
                    {

                        String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                        String uid = jsonObject.getJSONObject("transaction").getString("uid");
                        String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                        String message = jsonObject.getJSONObject("transaction").getString("message");
                        //String description = jsonObject.getJSONObject("transaction").getString("description");

                        String rrn = "";
                        String ref_id = "";
                        String gateway_id = "";
                        String gateway_message = "";
                        if(jsonObject.getJSONObject("transaction").has("payment"))
                        {
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("bank_code"))
                                bank_code = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("bank_code");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("rrn"))
                                rrn = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("rrn");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("ref_id"))
                                ref_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("ref_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("gateway_id"))
                                gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("gateway_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("message"))
                                gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("message");
                        }
                        if(jsonObject.getJSONObject("transaction").has("three_d_secure_verification"))
                        {
                            ve_status = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("ve_status");
                            status_3d = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("status");
                            message_3d = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("message");
                        }

                        if ("incomplete".equalsIgnoreCase(responseStatus))
                        {
                            if("Y".equals(ve_status))
                            {
                                acs_url = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("acs_url");
                                pa_req = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("pa_req");
                                md = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("md");
                                pa_res_url = jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("pa_res_url");

                                emexpayVO.setUrlFor3DRedirect(acs_url);
                                emexpayVO.setPaReq(pa_req);
                                emexpayVO.setMd(md +"="+pa_res_url);
                                emexpayVO.setStatus("pending3DConfirmation");
                                emexpayVO.setRemark(message_3d);
                                emexpayVO.setTerURL(RB.getString("TERM_URL")+trackingID);
                                emexpayVO.setVe_status(ve_status);
                                emexpayVO.setTransactionId(uid);
                                emexpayVO.setTransactionStatus(status_3d);
                                emexpayVO.setTransactionType(transactionType);
                                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                            else if(functions.isValueNull(jsonObject.getJSONObject("transaction").getString("redirect_url")))
                            {
                                emexpayVO.setUrlFor3DRedirect(jsonObject.getJSONObject("transaction").getString("redirect_url"));
                                emexpayVO.setStatus("pending3DConfirmation");
                                emexpayVO.setTerURL(RB.getString("TERM_URL") + trackingID);
                                emexpayVO.setTransactionId(uid);
                                emexpayVO.setTransactionStatus(responseStatus);
                                emexpayVO.setTransactionType(transactionType);
                                emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            }
                        }
                        else if("N".equals(ve_status) || "successful".equalsIgnoreCase(status_3d) || "successful".equalsIgnoreCase(responseStatus))
                        {
                            authCode = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("auth_code");
                            String billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("billing_descriptor");
                            String holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                            String token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                            String stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                            String brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");
                            String address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                            String country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                            String city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                            String zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                            String state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");

                            if(functions.isValueNull(bank_code))
                            {
                                if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                                {
                                    emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setErrorCode(bank_code);
                                }
                                else
                                {
                                    emexpayVO.setRemark(message);
                                    emexpayVO.setDescription(message);
                                    emexpayVO.setErrorCode(bank_code);
                                }
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                            }
                            emexpayVO.setStatus("success");
                            emexpayVO.setVe_status(ve_status);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionStatus(status_3d);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setDescriptor(billinDescp);
                            emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                            emexpayVO.setHolder_name(holder_name);
                            emexpayVO.setToken(token);
                            emexpayVO.setStamp(stamp);
                            emexpayVO.setBrand(brand);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        }
                        else if("failed".equalsIgnoreCase(responseStatus) || "E".equals(ve_status))
                        {
                            if(functions.isValueNull(bank_code))
                            {
                                if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                                {
                                    emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                    emexpayVO.setErrorCode(bank_code);
                                    emexpayVO.setErrorName(getErrorName(bank_code));
                                }
                                else
                                {
                                    emexpayVO.setRemark(message);
                                    emexpayVO.setDescription(message);
                                    emexpayVO.setErrorCode(bank_code);
                                    emexpayVO.setErrorName(getErrorName(bank_code));
                                }
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorName(getErrorName(bank_code));
                            }
                            emexpayVO.setStatus("fail");
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setErrorCode(bank_code);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        }
                    }
                    else if (jsonObject.has("response"))
                    {
                        String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }

            }
            else
            {
                EmexJsonVO emexJsonVO = new EmexJsonVO();
                EmexJsonVO emexJsonVOlog = new EmexJsonVO();
                request request = new request();
                request requestlog = new request();

                credit_card creditCard = new credit_card();
                creditCard.setNumber(commCardDetailsVO.getCardNum());
                creditCard.setVerification_value(commCardDetailsVO.getcVV());
                creditCard.setExp_month(commCardDetailsVO.getExpMonth());
                creditCard.setExp_year(commCardDetailsVO.getExpYear());
                creditCard.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                credit_card creditCardlog = new credit_card();
                creditCardlog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
                creditCardlog.setVerification_value(functions.maskingNumber(commCardDetailsVO.getcVV()));
                creditCardlog.setExp_month(functions.maskingNumber(commCardDetailsVO.getExpMonth()));
                creditCardlog.setExp_year(functions.maskingNumber(commCardDetailsVO.getExpYear()));
                creditCardlog.setHolder(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());

                billing_address billingAddress = new billing_address();
                billingAddress.setFirst_name(commAddressDetailsVO.getFirstname());
                billingAddress.setLast_name(commAddressDetailsVO.getLastname());
                billingAddress.setAddress(commAddressDetailsVO.getStreet());
                billingAddress.setCity(commAddressDetailsVO.getCity());
                if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                    billingAddress.setCountry(commAddressDetailsVO.getCountry());
                }else {
                    billingAddress.setCountry(commCardDetailsVO.getCountry_code_A2());
                }
                billingAddress.setState(commAddressDetailsVO.getState());
                billingAddress.setZip(commAddressDetailsVO.getZipCode());

                customer customer = new customer();
                if(functions.isValueNull(commAddressDetailsVO.getCardHolderIpAddress()))
                    customer.setIp(commAddressDetailsVO.getCardHolderIpAddress());
                else
                    customer.setIp(commAddressDetailsVO.getIp());
                customer.setEmail(commAddressDetailsVO.getEmail());

                request.setAmount(getAmount(transactionAmount));
                requestlog.setAmount(getAmount(transactionAmount));
                request.setCurrency(transactionCurrency);
                requestlog.setCurrency(transactionCurrency);
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    request.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    request.setDescription(commTransactionDetailsVO.getOrderDesc());
                if(functions.isValueNull(commTransactionDetailsVO.getMerchantOrderId()))
                    requestlog.setDescription(commTransactionDetailsVO.getMerchantOrderId());
                else
                    requestlog.setDescription(commTransactionDetailsVO.getOrderDesc());
                request.setTracking_id(trackingID);
                request.setTest(isTest);

                request.setBillingAddress(billingAddress);
                request.setCreditCard(creditCard);
                request.setCustomer(customer);

                emexJsonVO.setRequest(request);

                requestlog.setTracking_id(trackingID);
                requestlog.setTest(isTest);

                requestlog.setBillingAddress(billingAddress);
                requestlog.setCreditCard(creditCard);
                requestlog.setCustomer(customer);

                emexJsonVOlog.setRequest(requestlog);

                Gson gson = new Gson();
                String saleRequest = gson.toJson(emexJsonVO);
                String saleRequestlog = gson.toJson(emexJsonVOlog);

                transactionLogger.error("saleResponse Non 3D-----"+trackingID + "--" + saleRequestlog);
                String saleResponse = "";
                if (isTest)
                {
                    saleResponse = emexpayUtils.newHttpPost(RB.getString("TEST_SALE_URL"), saleRequest, "Basic", clientCredentials);
                }
                else
                {
                    saleResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_SALE_URL"), saleRequest, "Basic", clientCredentials);
                }
                transactionLogger.error("saleResponse Non 3D-------" + saleResponse);

                String bank_code = "";
                String rrn = "";
                String ref_id = "";
                String gateway_id = "";
                String gateway_message = "";

                if (functions.isValueNull(saleResponse) && saleResponse.contains("{"))
                {
                    JSONObject jsonObject = new JSONObject(saleResponse);

                    if (jsonObject.has("transaction"))
                    {
                        String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                        String uid = jsonObject.getJSONObject("transaction").getString("uid");
                        String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                        String message = jsonObject.getJSONObject("transaction").getString("message");
                        //String description = jsonObject.getJSONObject("transaction").getString("description");

                        if(jsonObject.getJSONObject("transaction").has("payment"))
                        {
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("bank_code"))
                                bank_code = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("bank_code");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("rrn"))
                                rrn = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("rrn");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("ref_id"))
                                ref_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("ref_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("gateway_id"))
                                gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("gateway_id");
                            if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("message"))
                                gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("message");
                        }
                        if(functions.isValueNull(bank_code))
                        {
                            if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                            {
                                emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setErrorCode(bank_code);
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorCode(bank_code);
                            }
                        }
                        else
                        {
                            emexpayVO.setRemark(message);
                            emexpayVO.setDescription(message);
                        }
                        if ("successful".equalsIgnoreCase(responseStatus))
                        {
                            String authCode = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("auth_code");
                            String billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("billing_descriptor");
                            String holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                            String token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                            String stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                            String brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");

                            String address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                            String country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                            String city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                            String zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                            String state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");

                            emexpayVO.setStatus("success");
                            emexpayVO.setRemark(message);
                            emexpayVO.setAuthCode(authCode);
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setDescription(message);
                            emexpayVO.setDescriptor(billinDescp);
                            emexpayVO.setHolder_name(holder_name);
                            emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                            emexpayVO.setToken(token);
                            emexpayVO.setStamp(stamp);
                            emexpayVO.setBrand(brand);
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setMessage(gateway_message);
                            emexpayVO.setErrorCode(bank_code);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        }
                        else
                        {
                            emexpayVO.setStatus("fail");
                            emexpayVO.setTransactionId(uid);
                            emexpayVO.setTransactionType(transactionType);
                            emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            emexpayVO.setBank_code(bank_code);
                            emexpayVO.setErrorName(getErrorName(bank_code));
                            emexpayVO.setRrn(rrn);
                            emexpayVO.setRef_id(ref_id);
                            emexpayVO.setGateway_id(gateway_id);
                            emexpayVO.setErrorCode(bank_code);
                            emexpayVO.setMessage(gateway_message);
                        }
                    }
                    else if (jsonObject.has("response"))
                    {
                        String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(gatewayMessage);
                        emexpayVO.setTransactionType("sale");
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setDescription(gatewayMessage);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("----eneter's into processCapture------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();

        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String captureRequest = "{\"request\": {\"parent_uid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\",\"amount\": \"" + getAmount(commTransactionDetailsVO.getAmount()) + "\"}}";

            transactionLogger.error("captureRequest----" + trackingID + "--" + captureRequest);
            String captureResponse = "";
            if (isTest)
            {
                captureResponse = emexpayUtils.newHttpPost(RB.getString("TEST_CAPTURE_URL"), captureRequest, "Basic", clientCredentials);
            }
            else
            {
                captureResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_CAPTURE_URL"), captureRequest, "Basic", clientCredentials);
            }
            transactionLogger.error("captureResponse----" + trackingID + "--" + captureResponse);
            if (functions.isValueNull(captureResponse) && captureResponse.contains("{"))
            {

                JSONObject jsonObject = new JSONObject(captureResponse);
                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("success");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }

                }
                if (jsonObject.has("response"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    Date date = new Date();
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
            }

        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processCapture()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;

    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("----eneter's into processPayout------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();
            String token = "";

            String clientPass = shopid + ":" + secretKey;
            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            if (commTransactionDetailsVO.getCustomerBankId() != null)
            {
                token = commTransactionDetailsVO.getCustomerBankId();
            }
            else
            {
                token = commCardDetailsVO.getAccountNumber();
            }

            EmexJsonVO emexJsonVO = new EmexJsonVO();
            credit_card creditCard = new credit_card();
            request request = new request();
            creditCard.setToken(token);

            request.setCreditCard(creditCard);
            request.setAmount(getAmount(commTransactionDetailsVO.getAmount()));
            request.setCurrency(commTransactionDetailsVO.getCurrency());
            request.setDescription(commTransactionDetailsVO.getOrderDesc());
            request.setTracking_id(trackingId);
            request.setLanguage("en");
            request.setTest(isTest);

            emexJsonVO.setRequest(request);

            Gson gson = new Gson();
            String creditRequest = gson.toJson(emexJsonVO);

            transactionLogger.error("creditRequest-----" +trackingId + "--" +  creditRequest);
            String creditResponse = "";
            if (isTest)
            {
                creditResponse = emexpayUtils.newHttpPost(RB.getString("TEST_CREDIT_URL"), creditRequest, "Basic", clientCredentials);
            }
            else
            {
                creditResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_CREDIT_URL"), creditRequest, "Basic", clientCredentials);
            }
            transactionLogger.error("creditResponse-----" +trackingId + "--" +  creditResponse);
            if (functions.isValueNull(creditResponse) && creditResponse.contains("{")) ;
            {
                JSONObject jsonObject = new JSONObject(creditResponse);

                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("success");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }

                }
                if (jsonObject.has("response"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    Date date = new Date();
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
            }


        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processPayout()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("----eneter's into processRefund------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String refundRequest = "{\"request\": {\"parent_uid\": \"" + commTransactionDetailsVO.getPreviousTransactionId() + "\",\"amount\":\"" + getAmount(commTransactionDetailsVO.getAmount()) + "\",\"reason\":\"" + commTransactionDetailsVO.getOrderDesc() + "\"}}";
            transactionLogger.error("refundRequest-------" + trackingID + "--" + refundRequest);
            String refundResponse = "";
            if (isTest)
            {
                refundResponse = emexpayUtils.newHttpPost(RB.getString("TEST_REFUND_URL"), refundRequest, "Basic", clientCredentials);
            }
            else
            {
                refundResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_REFUND_URL"), refundRequest, "Basic", clientCredentials);
            }
            transactionLogger.error("refundResponce-----" +trackingID + "--" +  refundResponse);
            if (functions.isValueNull(refundResponse) && refundResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(refundResponse);

                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("success");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                if (jsonObject.has("response"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    Date date = new Date();
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processQuery for TrackingID-----"+trackingID);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String paymentId = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
            boolean isTest = gatewayAccount.isTest();
            transactionLogger.debug("paymentId-----" + paymentId);

            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));
            String queryResponse = "";

            if (paymentId == null || paymentId.equalsIgnoreCase("null") || paymentId.equals(""))
            {
                transactionLogger.error("-----inside Query By trackingid-----"+trackingID);
                if (isTest)
                {
                    queryResponse = emexpayUtils.newHttpGet(RB.getString("TEST_QUERY_URL") + trackingID, "Basic", clientCredentials);
                }
                else
                {
                    queryResponse = emexpayUtils.newHttpGet(RB.getString("LIVE_QUERY_URL") + trackingID, "Basic", clientCredentials);
                }
            }
            else
            {
                transactionLogger.error("-----inside Query By UID-----"+paymentId);
                if (isTest)
                {
                    queryResponse = emexpayUtils.newHttpGet(RB.getString("TEST_QUERY_URL_BY_UID") + paymentId, "Basic", clientCredentials);
                }
                else
                {
                    queryResponse = emexpayUtils.newHttpGet(RB.getString("LIVE_QUERY_URL_BY_UID") + paymentId, "Basic", clientCredentials);
                }

            }

            transactionLogger.error("queryResponse for trackingid==="+trackingID+"-----" + queryResponse);

            String billingDesc="";
            String message = "";
            String bank_code="";

            String pzTrackingId = "";
            String uid = "";
            String responseStatus = "";
            String amount = "";
            String currecy = "";
            String type = "";

            if (functions.isValueNull(queryResponse)&& queryResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(queryResponse);

                if (jsonObject.has("transaction"))
                {
                    if(jsonObject.getJSONObject("transaction").has("authorization"))
                    {
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("billing_descriptor"))
                            billingDesc=jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("billing_descriptor");

                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("bank_code"))
                            bank_code=jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("bank_code");

                    }
                    else if(jsonObject.getJSONObject("transaction").has("payment"))
                    {
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("billing_descriptor"))
                            billingDesc=jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("billing_descriptor");

                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("bank_code"))
                            bank_code=jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("bank_code");

                    }
                    uid = jsonObject.getJSONObject("transaction").getString("uid");
                    responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    amount = jsonObject.getJSONObject("transaction").getString("amount");
                    currecy = jsonObject.getJSONObject("transaction").getString("currency");
                    type = jsonObject.getJSONObject("transaction").getString("type");
                    pzTrackingId = jsonObject.getJSONObject("transaction").getString("tracking_id");

                    if(jsonObject.getJSONObject("transaction").getString("message").equalsIgnoreCase("Unsuccessful"))
                        message = "Transaction Declined";
                    else
                        message = jsonObject.getJSONObject("transaction").getString("message");

                    if(functions.isValueNull(bank_code))
                    {
                        if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                        {
                            emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                            emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                            emexpayVO.setErrorCode(bank_code);
                        }
                        else
                        {
                            emexpayVO.setRemark(message);
                            emexpayVO.setDescription(message);
                            emexpayVO.setErrorCode(bank_code);
                        }
                    }
                    else
                    {
                        emexpayVO.setRemark(message);
                        emexpayVO.setDescription(message);
                        emexpayVO.setErrorCode(bank_code);
                    }

                    String paid_at = jsonObject.getJSONObject("transaction").getString("paid_at");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("success");
                        emexpayVO.setDescriptor(billingDesc);
                        emexpayVO.setTransactionType(type);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setBankTransactionDate(paid_at);
                        emexpayVO.setAmount(amount);
                        emexpayVO.setCurrency(currecy);
                        emexpayVO.setMerchantId(shopid);
                        emexpayVO.setTransactionStatus(responseStatus);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }
                    else if("incomplete".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("incomplete");
                        emexpayVO.setDescriptor(billingDesc);
                        emexpayVO.setTransactionType(type);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setBankTransactionDate(paid_at);
                        emexpayVO.setAmount(amount);
                        emexpayVO.setCurrency(currecy);
                        emexpayVO.setMerchantId(shopid);
                        emexpayVO.setTransactionStatus(responseStatus);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setTransactionType(type);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setBankTransactionDate(paid_at);
                        emexpayVO.setAmount(amount);
                        emexpayVO.setCurrency(currecy);
                        emexpayVO.setMerchantId(shopid);
                        emexpayVO.setTransactionStatus(responseStatus);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }
                }
                if (jsonObject.has("response"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    Date date = new Date();
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setErrorName(getErrorName(bank_code));
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
            }

        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processQuery()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("----eneter's into processVoid------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest = gatewayAccount.isTest();
            String clientPass = shopid + ":" + secretKey;
            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String voidRequest = "{\"request\": {\"parent_uid\":\"" + commTransactionDetailsVO.getPreviousTransactionId() + "\"," +
                    "\"amount\":" + getAmount(commTransactionDetailsVO.getAmount()) + "}}";
            transactionLogger.error("voidRequest------" + trackingID + "--" + voidRequest);
            String voidResponse = "";
            if (isTest)
            {
                voidResponse = emexpayUtils.newHttpPost(RB.getString("TEST_VOID_URL"), voidRequest, "Basic", clientCredentials);
            }
            else
            {
                voidResponse = emexpayUtils.newHttpPost(RB.getString("LIVE_VOID_URL"), voidRequest, "Basic", clientCredentials);
            }
            transactionLogger.error("voidResponse------" + trackingID + "--" + voidResponse);
            if (functions.isValueNull(voidResponse) && voidResponse.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(voidResponse);
                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        emexpayVO.setStatus("success");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    }
                }
                if (jsonObject.has("response"))
                {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    Date date = new Date();
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("sale");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processVoid()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return emexpayVO;

    }

    public GenericResponseVO process3DAuthConfirmation(String trackingid, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----enter's into process3DAuthConfirmation-----");
        EmexpayRequestVO emexpayRequestVO = (EmexpayRequestVO) requestVO;
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try
        {

            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String secondRequest = "{\n" +
                    "    \"md\": \"" + emexpayRequestVO.getMD() + "\",\n" +
                    "    \"pa_res\": \"" + emexpayRequestVO.getPaRes() + "\"\n" +
                    "}";

            transactionLogger.error("secondRequest-----" +trackingid + "--" +  secondRequest);

            String secondResponse = emexpayUtils.newHttpPost(emexpayRequestVO.getPa_res_url(), secondRequest, "Basic", clientCredentials);

            transactionLogger.error("secondResponse-----" + trackingid + "--" + secondResponse);

            if (functions.isValueNull(secondResponse)  && secondResponse.contains("{"))
            {
                String eci="";
                String pzTrackingId = "";
                String bank_code = "";

                JSONObject jsonObject = new JSONObject(secondResponse);
                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    pzTrackingId = jsonObject.getJSONObject("transaction").getString("tracking_id");
                    transactionLogger.error("Trackingid---"+pzTrackingId);


                    String rrn = "";
                    String ref_id = "";
                    String gateway_id = "";
                    String gateway_message = "";
                    String authCode="";
                    String billinDescp="";
                    String holder_name="";
                    String token="";
                    String stamp="";
                    String brand="";
                    String address="";
                    String country="";
                    String city="";
                    String zip="";
                    String state="";

                    if(jsonObject.getJSONObject("transaction").has("authorization"))
                    {
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("bank_code"))
                            bank_code = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("bank_code");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("rrn"))
                            rrn = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("rrn");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("ref_id"))
                            ref_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("ref_id");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("gateway_id"))
                            gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("gateway_id");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("message"))
                            gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("message");
                    }

                    if(jsonObject.getJSONObject("transaction").has("three_d_secure_verification")){

                         eci=jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("eci");
                    }

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("auth_code"))
                         authCode = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("auth_code");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("authorization").has("billing_descriptor"))
                         billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("authorization").getString("billing_descriptor");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("holder"))
                         holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("token"))
                         token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("stamp"))
                         stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("brand"))
                         brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("address"))
                         address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("country"))
                         country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("city"))
                         city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("zip"))
                         zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("state"))
                         state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");
                        if(functions.isValueNull(bank_code))
                        {
                            if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                            {
                                emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setErrorCode(bank_code);
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorCode(bank_code);
                            }
                        }
                        else
                        {
                            emexpayVO.setRemark(message);
                            emexpayVO.setDescription(message);
                        }
                        emexpayVO.setStatus("success");
                        emexpayVO.setAuthCode(authCode);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setDescriptor(billinDescp);
                        emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                        emexpayVO.setHolder_name(holder_name);
                        emexpayVO.setToken(token);
                        emexpayVO.setStamp(stamp);
                        emexpayVO.setBrand(brand);
                        emexpayVO.setBank_code(bank_code);
                        emexpayVO.setRrn(rrn);
                        emexpayVO.setRef_id(ref_id);
                        emexpayVO.setGateway_id(gateway_id);
                        emexpayVO.setMessage(gateway_message);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setEci(eci);
                        emexpayVO.setMerchantOrderId(pzTrackingId);

                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setBank_code(bank_code);
                        emexpayVO.setRrn(rrn);
                        emexpayVO.setRef_id(ref_id);
                        emexpayVO.setGateway_id(gateway_id);
                        emexpayVO.setMessage(gateway_message);
                        emexpayVO.setErrorCode(bank_code);
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setEci(eci);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }

                }else if (jsonObject.has("response"))
                {
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setTransactionType("authorization");
                    emexpayVO.setErrorName(getErrorName(bank_code));
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "process3DAuthConfirmation()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        return emexpayVO;
    }

    public GenericResponseVO process3DSaleConfirmation(String tracking, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----enter's into process3DSaleConfirmation-----");
        EmexpayRequestVO emexpayRequestVO = (EmexpayRequestVO) requestVO;
        EmexpayVO emexpayVO = new EmexpayVO();
        EmexpayUtils emexpayUtils = new EmexpayUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        try
        {
            String shopid = gatewayAccount.getMerchantId();
            String secretKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String clientPass = shopid + ":" + secretKey;

            String clientCredentials = new String(Base64.encodeBase64(clientPass.getBytes()));

            String secondRequest = "{\n" +
                    "    \"md\": \"" + emexpayRequestVO.getMD() + "\",\n" +
                    "    \"pa_res\": \"" + emexpayRequestVO.getPaRes() + "\"\n" +
                    "}";

            transactionLogger.error("secondRequest-----" + tracking+ "--" + secondRequest);

            String secondResponse = emexpayUtils.newHttpPost(emexpayRequestVO.getPa_res_url(), secondRequest, "Basic", clientCredentials);

            transactionLogger.error("secondResponse-----" + tracking+ "--" + secondResponse);

            if (functions.isValueNull(secondResponse)  && secondResponse.contains("{"))
            {
                String eci="";
                String pzTrackingId = "";
                String bank_code = "";
                JSONObject jsonObject = new JSONObject(secondResponse);

                if (jsonObject.has("transaction"))
                {
                    String responseStatus = jsonObject.getJSONObject("transaction").getString("status");
                    String uid = jsonObject.getJSONObject("transaction").getString("uid");
                    String transactionType = jsonObject.getJSONObject("transaction").getString("type");
                    String message = jsonObject.getJSONObject("transaction").getString("message");
                    String description = jsonObject.getJSONObject("transaction").getString("description");
                    pzTrackingId = jsonObject.getJSONObject("transaction").getString("tracking_id");
                    transactionLogger.error("Trackingid---"+pzTrackingId);


                    String rrn = "";
                    String ref_id = "";
                    String gateway_id = "";
                    String gateway_message = "";
                    String authCode="";
                    String billinDescp="";
                    String holder_name="";
                    String token="";
                    String stamp="";
                    String brand="";
                    String address="";
                    String country="";
                    String city="";
                    String zip="";
                    String state="";
                    if(jsonObject.getJSONObject("transaction").has("payment"))
                    {
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("bank_code"))
                            bank_code = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("bank_code");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("rrn"))
                            rrn = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("rrn");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("ref_id"))
                            ref_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("ref_id");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("gateway_id"))
                            gateway_id = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("gateway_id");
                        if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("message"))
                            gateway_message = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("message");
                    }

                    if(jsonObject.getJSONObject("transaction").has("three_d_secure_verification")){

                        eci=jsonObject.getJSONObject("transaction").getJSONObject("three_d_secure_verification").getString("eci");
                        transactionLogger.debug("eci----"+eci);
                    }
                    if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("auth_code"))
                        authCode = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("auth_code");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("payment").has("billing_descriptor"))
                        billinDescp = jsonObject.getJSONObject("transaction").getJSONObject("payment").getString("billing_descriptor");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("holder"))
                        holder_name = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("holder");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("token"))
                        token = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("token");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("stamp"))
                        stamp = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("stamp");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("credit_card").has("brand"))
                        brand = jsonObject.getJSONObject("transaction").getJSONObject("credit_card").getString("brand");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("address"))
                        address = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("address");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("country"))
                        country = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("country");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("city"))
                        city = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("city");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("zip"))
                        zip = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("zip");
                    if(jsonObject.getJSONObject("transaction").getJSONObject("billing_address").has("state"))
                        state = jsonObject.getJSONObject("transaction").getJSONObject("billing_address").getString("state");

                    if ("successful".equalsIgnoreCase(responseStatus))
                    {
                        if(functions.isValueNull(bank_code))
                        {
                            if(functions.isValueNull(EmexPayErrorCode.getDescription(bank_code)))
                            {
                                emexpayVO.setRemark(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setDescription(EmexPayErrorCode.getDescription(bank_code));
                                emexpayVO.setErrorCode(bank_code);
                            }
                            else
                            {
                                emexpayVO.setRemark(message);
                                emexpayVO.setDescription(message);
                                emexpayVO.setErrorCode(bank_code);
                            }
                        }
                        else
                        {
                            emexpayVO.setRemark(message);
                            emexpayVO.setDescription(message);
                        }
                        emexpayVO.setStatus("success");
                        emexpayVO.setAuthCode(authCode);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setDescriptor(billinDescp);
                        emexpayVO.setHolder_name(holder_name);
                        emexpayVO.setAddress_details(address + "," + city + "," + state + "," + country + "," + zip);
                        emexpayVO.setToken(token);
                        emexpayVO.setStamp(stamp);
                        emexpayVO.setBrand(brand);
                        emexpayVO.setBank_code(bank_code);
                        emexpayVO.setRrn(rrn);
                        emexpayVO.setRef_id(ref_id);
                        emexpayVO.setGateway_id(gateway_id);
                        emexpayVO.setMessage(gateway_message);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setEci(eci);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }
                    else
                    {
                        emexpayVO.setStatus("fail");
                        emexpayVO.setRemark(message);
                        emexpayVO.setTransactionId(uid);
                        emexpayVO.setTransactionType(transactionType);
                        emexpayVO.setDescription(description);
                        emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        emexpayVO.setEci(eci);
                        emexpayVO.setBank_code(bank_code);
                        emexpayVO.setErrorName(getErrorName(bank_code));
                        emexpayVO.setRrn(rrn);
                        emexpayVO.setRef_id(ref_id);
                        emexpayVO.setGateway_id(gateway_id);
                        emexpayVO.setMessage(gateway_message);
                        emexpayVO.setErrorCode(bank_code);
                        emexpayVO.setMerchantOrderId(pzTrackingId);
                    }
                }
                else if (jsonObject.has("response"))
                {
                    String gatewayMessage = jsonObject.getJSONObject("response").getString("message");
                    emexpayVO.setStatus("fail");
                    emexpayVO.setRemark(gatewayMessage);
                    emexpayVO.setErrorName(getErrorName(bank_code));
                    emexpayVO.setTransactionType("payment");
                    emexpayVO.setDescription(gatewayMessage);
                    emexpayVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                }

            }
        }catch(JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("EmexpayPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return emexpayVO;
    }

    public String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;

        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    private String getErrorName(String errorCode){
        switch (errorCode)
        {
            case "551":
                return "REJECTED_NOT_SUFFICIENT_FUND";
            case "620":
                return "REJECTED_INVALID_CARDHOLDER_NAME";
            case "625":
                return "REJECTED_3D_SECURE_CHECK_FAILED";
            case "331":
                return "REJECTED_VELOCITY_CHECK_FAILED_TRANSACTION_EXCEEDS_LEVEL_TERMINAL";
            case"562":
                return "REJECTED_RESTRICTED_CARD";
            case "604":
                return "REJECTED_INVALID_CARD";
            case "557":
                return "REJECTED_TRANSACTION_NOT_PERMITTED_TO_CARDHLDER";
            case "603":
                return "REJECTED_TRANSACTION_TIME_OUT";
            case "5N7":
                return "REJECTED_CVV2_FAILURE";
            case "594":
                return "REJECTED_DUPLICATE_TRANSACTION";
            case "561":
                return "REJECTED_EXCEEEDS_WITHDRAWAL_LIMIT";
            case "5U2":
                return "REJECTED_CARD_EXPIRED";
            case "513":
                return "REJECTED_INVALID_AMOUNT";
            case "512":
                return "REJECTED_INVALID_TRANS";
            case "541":
                return "REJECTED_LOST_CARD";
            case "5A7":
                return "REJECTED_PENDING_TRANSACTION";
            case "543":
                return "REJECTED_STOLEN_CARD";
            case "601":
                return "REJECTED_SYSTEM_ERROR";
            default:
                return "TRANSACTION_REJECTED";
        }
    }
}
package com.payment.vouchermoney;

import com.directi.pg.ActionEntry;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.PaymentManager;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by nikita on 6/30/2017.
 */
public class VoucherMoneyPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "voucher";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.VoucherMoney");
    public static String merchantSecureKey = "u5QGKfiGMAmz62vTr10HcvXu/cMXONIawvBHr0tePE013S352lGqukDPVP3jQcniWQXm/NepCk0R1v/qjgkIUg=="; //merchant system to payment
    public static String paymentSecureKey = "cP8L0euGtEJn7gYia2fthfVsFLA3Jg7SvTpuXSwTrNO6xWpoLk3VUqHakGeZkUr1PgGLwUzHmzOgyO+eJZ93xw=="; //payment system to merchant
    private static TransactionLogger transactionLogger = new TransactionLogger(VoucherMoneyPaymentGateway.class.getName());

    public VoucherMoneyPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static String writeValueAsString(Object obj)
    {
        String valueAsString = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            valueAsString = objectMapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e)
        {
            //System.out.println("Problem with processing json from Object: " + obj.toString());
            throw new RuntimeException("Problem with processing json", e);
        }
        return valueAsString;
    }

    public static String generateAutoSubmitForm(String url)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"" + url + "").append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
        String url = "https://staging-merchant-api.vouchermoney.com/merchant-api/create-withdrawal";
        String merchantId = "1023";
        String merchantId2 = "1024";
        String merchantId3 = "3";
        //  String Url2="https://domainofpaymentsystem.com/merchant-api/create-withdrawal";
        // String Url2="https://staging-merchant-api.vouchermoney.com/merchant-api/create-withdrawal";
        String Url2 ="https://staging-payment.vouchermoney.com/merchant-api/create-withdrawal";
        String Url3 = "https://staging-payment.vouchermoney.com/merchant-api/create-withdrawal/generate";
        //    String Url4="https://staging-test-merchant.vouchermoney.com/merchant-test/withdrawal";

        Date date = new Date();
        String datetime = date.getTime() + "";
/*
        String saleRequest = "{\"requestDate\":\"" + datetime + "\"," +
                "\"paymentReference\":\"12632885532\"," +
                "\"integrationType\":\"REDIRECT\"," +

                "\"customerId\":\"541237\"," +
                "\"minimalDepositAmount\":\"1\"," +
                "\"customerCurrency\":\"GBP\"" +
                "}";

        String saleRequest1 = "{\n" +
                "\"merchantId\": \"3\",\n" +
                "\"paymentReference\" : \"556654\",\n" +
                "\"integrationType\" : \"IFRAME\",\n" +
                "\"amount\": \"10\",\n" +
                "\"customerId\": \"2655\",\n" +
                "\"currencyCode\": \"GBP\",\n" +
                "\"countryCode\": \"IN\",\n" +
                "\"expirationDate\": \"20180109\",\n" +
                "\"successURL\": \"http://localhost:8081/transaction/VoucherMoneyFrontEndServlet/withdrawal-success\",\n" +
                "\"failureURL\": \"http://localhost:8081/transaction/VoucherMoneyBackEndServlet/withdrawal-failure\"\n" +
                "}";

        //  String saleRequest3 = {"amount":"10.00","integrationType":"POPUP","merchantId":1023,"countryCode":"US","paymentReference":"216524","customerId":1235,"successUrl":"http://localhost:8081/transaction/VoucherMoneyFrontEndServlet/withdrawal-success","currencyCode":"USD","expirationDate":"20180901","failureUrl":"http://localhost:8081/transaction/VoucherMoneyBackEndServlet/withdrawal-failure"}


        String payout = "{" +
                "\"amount\":\"10.00\"," +
                "\"integrationType\":\"POPUP\"," +
                "\"merchantId\":1023," +
                "\"countryCode\":\"US\"," +
                "\"paymentReference\":\"216524\"," +
                "\"customerId\":1235," +
                "\"successUrl\":\"http://localhost:8081/transaction/VoucherMoneyFrontEndServlet/withdrawal-success\"," +
                "\"currencyCode\":\"USD\"," +
                "\"expirationDate\":\"20180901\"," +
                "\"failureUrl\":\"http://localhost:8081/transaction/VoucherMoneyBackEndServlet/withdrawal-failure\"" +
                "}";


        Map<String, Object> requestMap = new HashMap();
        requestMap.put("amount", "10.00");
        requestMap.put("paymentReference", "51755112");
        requestMap.put("currencyCode", "USD");
        requestMap.put("countryCode", "US");
        requestMap.put("merchantGeneratorId", 1023);
        requestMap.put("customerId", 12312);
        requestMap.put("expirationDate", "20180901");
        // requestMap.put("integrationType","POPUP");
        //   requestMap.put("successUrl","http://www.google.com");
        //   requestMap.put("failureUrl","http://www.yahoo.com");*/

        Map<String, Object> requestMap1 = new HashMap<String, Object>();
        requestMap1.put("amount","200.00");
        requestMap1.put("integrationType","POPUP");
        requestMap1.put("merchantId",1023);
        requestMap1.put("countryCode","GB");
        requestMap1.put("paymentReference","55155");
        requestMap1.put("customerId",75412);
        requestMap1.put("successUrl","https://staging.pz.com/transaction/VoucherMoneyFrontEndServlet/withdrawal-success");
        requestMap1.put("customerCurrencyCode","IRT");
        requestMap1.put("voucherCurrencyCode","GBP");
        requestMap1.put("expirationDate","20180612");
        requestMap1.put("failureUrl","https://staging.pz.com/transaction/VoucherMoneyBackEndServlet/withdrawal-failure");
        requestMap1.put("withdrawalType","cancel");


        String stringToSign = writeValueAsString(requestMap1);


        //System.out.println("merchantSecureKey::::" + merchantSecureKey);
        String signature = VoucherMoneyUtils.signature(stringToSign, merchantSecureKey);


       /* System.out.println("stringToSign::::::::::::" + stringToSign);
        System.out.println("signature::::::::::::" + signature);
        System.out.println("Url2::::::::::::" + Url2);*/


        // String hmac = voucherMoneyUtils.signature(stringToSign, merchantSecureKey);
        //System.out.println("hmac----" + hmac);

        //byte[] bytes = hmac.getBytes("UTF-8");
        //String signature = Base64.encode(bytes);
        //System.out.println("encodedBytes---"+signature);

        String response = voucherMoneyUtils.doPostHTTPSURLConnectionClient(Url2, stringToSign, signature, merchantId);
        //  String response = voucherMoneyUtils.doPostHTTPSURLConnectionClient(Url3, saleRequest4, hmac, merchantId);
        //  String response1 = TestManager.dopost(Url2, saleRequest3, hmac, merchantId);
        //System.out.println("response-----" + response);

        /*String paymentformurl = "https://staging-payment.vouchermoney.com/payment/1023/12347/90167";
        String confirmPayment = "{ \"paymentReference\" : \"12347\",\n" +
                "\"customerId\": \"541237\",\n" +
                "\"amount\": 1,\n" +
                "\"currency\": \"RUB\",\n" +
                "\"amountInCustomerCurrency\":1,\n" +
                "\"customerCurrency\" : \"GBP\"\n" +
                "}";
        //System.out.println("confirmPayment---"+confirmPayment);

        String response1 = voucherMoneyUtils.doPostHTTPSURLConnectionClient(url,confirmPayment,"",merchantId);
        //System.out.println("response1----"+response1);


        String json = "{ \"paymentReference\" : \"12347\",\n" +
                "\"customerId\": \"541237\",\n" +
                "\"amount\": 1,\n" +
                "\"currency\": \"RUB\",\n" +
                "\"amountInCustomerCurrency\":1,\n" +
                "\"customerCurrency\" : \"GBP\"\n" +
                "}";

        JSONObject jsonObject = new JSONObject(json);
        jsonObject.get("paymentReference");
        jsonObject.get("customerId");
        jsonObject.get("amount");
        jsonObject.get("currency");
        jsonObject.get("amountInCustomerCurrency");
        jsonObject.get("customerCurrency");

        System.out.println("jsonOnjet----"+jsonObject);*/
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("VoucherMoneyPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Functions functions=new Functions();
        transactionLogger.error("Entering processSale in VoucherMoneyPaymentGateway:::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        VoucherMoneyResponse voucherMoneyResponse = new VoucherMoneyResponse();
        GenericTransDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getPassword();
        String customerId = genericAddressDetailsVO.getCustomerid();
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            currency=transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=genericAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=genericAddressDetailsVO.getTmpl_currency();
        }
        boolean isTest = gatewayAccount.isTest();
        Date date = new Date();
        String datetime = date.getTime() + "";

        String successUrl = RB.getString("SUCCESS_URL") + trackingID;
        String failureUrl = RB.getString("DECLINE_URL") + trackingID;

        String createPayment = "{\"requestDate\":\"" + datetime + "\"," +
                "\"paymentReference\":\"" + trackingID + "\"," +
                "\"integrationType\":\"REDIRECT\"," +
                "\"successUrl\":\"" + successUrl + "\"," +
                "\"failureUrl\":\"" + failureUrl + "\"," +
                "\"customerId\":\"" + customerId + "\"," +
                //"\"minimalDepositAmount\":\"\"," +
                "\"customerCurrency\":\"" + genericAddressDetailsVO.getTmpl_currency()+ "\"" +
                "}";

        transactionLogger.error("-----step-1 request----" + createPayment);
        String signature = voucherMoneyUtils.signature(createPayment, password);
        transactionLogger.error("-----signature-----" + signature);

        String response = "";
        if (isTest){
            response = voucherMoneyUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"), createPayment, signature, merchantId);
        }
        else{
            response = voucherMoneyUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), createPayment, signature, merchantId);
        }
        transactionLogger.error("-----step-1 response-----" + response);

        String status = "";
        String remark="";


        if (response != null){
            try{
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject!=null){
                    if ("OK".equals(jsonObject.getString("responseStatus"))){
                        status = "success";
                        voucherMoneyResponse.setPaymentFormUrl(jsonObject.getString("paymentFormUrl"));
                        voucherMoneyResponse.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        remark="Transaction Successful";
                    }
                    else{
                        status = "fail";
                        remark=jsonObject.getString("responseMessage");
                        voucherMoneyResponse.setErrorCode(jsonObject.getString("responseCode"));
                    }
                }
            }
            catch (JSONException je){
                status = "fail";
                remark="Internal server error";
            }
        }
        else{
            status = "fail";
            remark="Internal server error";
        }
        voucherMoneyResponse.setStatus(status);
        voucherMoneyResponse.setTransactionStatus(status);
        voucherMoneyResponse.setTransactionType("sale");
        voucherMoneyResponse.setRemark(status);
        voucherMoneyResponse.setResponseTime(datetime);
        voucherMoneyResponse.setRemark(remark);
        voucherMoneyResponse.setCurrency(currency);
        voucherMoneyResponse.setTmpl_Amount(tmpl_amount);
        voucherMoneyResponse.setTmpl_Currency(tmpl_currency);
        return voucherMoneyResponse;
    }

    @Override
    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entered into processPayout:::::");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());

        String successUrl = "";
        String failureUrl = "";

        VoucherMoneyUtils voucherMoneyUtils=new VoucherMoneyUtils();
        VoucherMoneyResponse voucherMoneyResponse = new VoucherMoneyResponse();
        String merchantId = gatewayAccount.getMerchantId();
        String password=gatewayAccount.getPassword();
        boolean isTest = gatewayAccount.isTest();
       // String customerId = commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String customerId ="";
        String payoutType="";
        if(functions.isValueNull(transactionDetailsVO.getPayoutType())){
            payoutType=transactionDetailsVO.getPayoutType();
        }
        String paymentReference="";

        if(payoutType.equalsIgnoreCase("cancel")){
            paymentReference=transactionDetailsVO.getPreviousTransactionId();
            successUrl=RB.getString("SUCCESS_URL") + paymentReference;
            failureUrl=RB.getString("DECLINE_URL") + paymentReference;
        }else {
            paymentReference=trackingID;
            successUrl=RB.getString("SUCCESS_URL") + paymentReference;
            failureUrl=RB.getString("DECLINE_URL") + paymentReference;
        }
        if(commRequestVO.getTransDetailsVO().getCustomerId()!=null)
        {
            customerId = commRequestVO.getTransDetailsVO().getCustomerId();
        }

        transactionLogger.debug("customerid----"+customerId);

        transactionLogger.debug("trackingid id in gateway----"+transactionDetailsVO.getPreviousTransactionId());

        try
        {
            Map<String, Object> mapRequest = new HashMap();
            mapRequest.put("amount", "" + commAddressDetailsVO.getTmpl_amount() + "");
            mapRequest.put("integrationType", "POPUP");
            mapRequest.put("merchantId", Integer.parseInt(merchantId));
            mapRequest.put("countryCode", "" + voucherMoneyUtils.get2CharCountryCode(commAddressDetailsVO.getCountry()) + "");
            mapRequest.put("paymentReference", "" + paymentReference + "");
            mapRequest.put("customerId", customerId);
            mapRequest.put("successUrl", "" + successUrl + "");
            mapRequest.put("customerCurrencyCode", "" + commAddressDetailsVO.getTmpl_currency() + "");
            mapRequest.put("voucherCurrencyCode", "" + transactionDetailsVO.getCurrency() + "");
            mapRequest.put("expirationDate", "" + VoucherMoneyUtils.voucherExpDate(commAddressDetailsVO.getBirthdate()) + "");
            mapRequest.put("failureUrl", "" + failureUrl + "");
            mapRequest.put("withdrawalType", "" + payoutType + "");

            String payoutRequest = VoucherMoneyUtils.writeValueAsString(mapRequest);
            transactionLogger.error("-----payout request-----" + payoutRequest);
            String signature = VoucherMoneyUtils.signature(payoutRequest, password);
            transactionLogger.error("-----signature-----" + signature);

            String payoutResponse = "";
            if (isTest)
            {
                payoutResponse = VoucherMoneyUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL"), payoutRequest, signature, merchantId);
            }
            else
            {
                payoutResponse = VoucherMoneyUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_PAYOUT_URL"), payoutRequest, signature, merchantId);
            }
            transactionLogger.error("-----payout response-----" + payoutResponse);

            String successMessage = "";
            String errorMessage = "";
            String message = "";
            String status = "";
            String description = "";
            String newVoucherSerialNumber = null;
            String newVoucherAmount = null;
            String merchantUserCommission = "";
            String commissionCurrency = "";

            if (functions.isValueNull(payoutResponse) && payoutResponse.contains("{"))
            {
                boolean newVoucherGenerated = false;
                JSONObject jsonObject = new JSONObject(payoutResponse);
                if (jsonObject.has("successMessage"))
                {
                    successMessage = jsonObject.getString("successMessage");
                }
                Object object1 = null;
                if (jsonObject.has("newVoucherGenerated"))
                    object1 = jsonObject.get("newVoucherGenerated");
                if (object1 != null)
                {
                    newVoucherGenerated = Boolean.parseBoolean((String.valueOf(object1)));
                }
                if (jsonObject.has("errorMessage"))
                {
                    errorMessage = jsonObject.getString("errorMessage");
                }
                if (jsonObject.has("message"))
                {
                    message = jsonObject.getString("message");
                }
                if(jsonObject.has("merchantUsersCommission"))
                {
                    merchantUserCommission = jsonObject.getString("merchantUsersCommission");
                }
                if(jsonObject.has("commissionCurrency"))
                {
                    commissionCurrency = jsonObject.getString("commissionCurrency");
                }

                transactionLogger.debug("successMessage-----" + successMessage);
                transactionLogger.debug("errorMessage-----" + errorMessage);
                transactionLogger.debug("newVoucherGenerated-----" + newVoucherGenerated);

                if (newVoucherGenerated)
                {
                    Object object = jsonObject.get("newVoucherSerialNumber");
                    Object newVoucherAmountObj = jsonObject.get("newVoucherAmount");
                    if (object != null && !object.toString().equals("null"))
                    {
                        transactionLogger.debug("inside newVoucherGenerated---" + newVoucherSerialNumber);
                        newVoucherSerialNumber = (String) object;
                        newVoucherAmount = String.valueOf(newVoucherAmountObj);
                        status = "success";
                        description = "Payout Successful";
                        voucherMoneyResponse.setStatus(status);
                        voucherMoneyResponse.setRemark(successMessage);
                        voucherMoneyResponse.setNewVoucherSerialNumber(newVoucherSerialNumber);
                        voucherMoneyResponse.setAmount(newVoucherAmount);
                        voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
                        voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());

                        if(functions.isValueNull(merchantUserCommission))
                        {
                            voucherMoneyResponse.setMerchantUsersCommission(merchantUserCommission);
                            description += " Commission Amount "+merchantUserCommission+" "+commissionCurrency;
                        }
                        if(functions.isValueNull(commissionCurrency))
                            voucherMoneyResponse.setCommissionCurrency(commissionCurrency);

                        voucherMoneyResponse.setDescription(description);

                    }
                }
                else if (functions.isValueNull(successMessage) && !functions.isValueNull(errorMessage))
                {
                    transactionLogger.debug("inside-----successMessage-----" + successMessage);

                    if (successMessage.equalsIgnoreCase("Successfully cancelled"))
                    {

                        status = "success";
                        description = "Payout Cancel Successful";
                    }
                    else
                    {
                        status = "pending";
                        description = "Payout Pending";
                    }
                    voucherMoneyResponse.setStatus(status);
                    voucherMoneyResponse.setRemark(successMessage);
                    voucherMoneyResponse.setDescription(description);
                    voucherMoneyResponse.setAmount(transactionDetailsVO.getAmount());

                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
                        voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
                        voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());
                }
                else if (functions.isValueNull(errorMessage) && !functions.isValueNull(successMessage))
                {
                    transactionLogger.debug("inside errorMessage-----" + errorMessage);

                    if (errorMessage.equalsIgnoreCase("Cancel failed"))
                    {

                        status = "fail";
                        description = "Payout Cancel Failed";
                    }
                    else
                    {
                        status = "fail";
                        description = "Payout Failed";
                    }
                    voucherMoneyResponse.setStatus(status);
                    voucherMoneyResponse.setRemark(errorMessage);
                    voucherMoneyResponse.setDescription(description);
                    voucherMoneyResponse.setAmount(transactionDetailsVO.getAmount());
                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
                        voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
                        voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());
                }
                else if (functions.isValueNull(message))
                {
                    transactionLogger.debug("inside message-----" + message);
                    status = "fail";
                    voucherMoneyResponse.setStatus(status);
                    voucherMoneyResponse.setRemark(message);
                    voucherMoneyResponse.setDescription(message);
                    voucherMoneyResponse.setAmount(transactionDetailsVO.getAmount());
                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
                        voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
                    if(functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
                        voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());
                }

            }
            else
            {
                status = "fail";
                description = "Server Error";
                voucherMoneyResponse.setStatus(status);
                voucherMoneyResponse.setRemark(description);
                voucherMoneyResponse.setDescription(description);
                voucherMoneyResponse.setAmount(transactionDetailsVO.getAmount());
                if(functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
                    voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
                if(functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
                    voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());
            }
            //voucherMoneyResponse.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
            //voucherMoneyResponse.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());

        }
        catch (JSONException e){
            PZExceptionHandler.raiseTechnicalViolationException(VoucherMoneyPaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return voucherMoneyResponse;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for VM:::::");
        CommRequestVO commRequestVO = null;
        String html = "";
        VoucherMoneyUtils voucherMoneyUtils = new VoucherMoneyUtils();
        PaymentManager paymentManager=new PaymentManager();
        VoucherMoneyResponse transRespDetails = null;
        ActionEntry actionEntry=new ActionEntry();
        actionEntry.actionEntryExtensionforVM(commonValidatorVO);

        commRequestVO = voucherMoneyUtils.getVoucherMoneyRequestVO(commonValidatorVO);

        transRespDetails = (VoucherMoneyResponse) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

        if (transRespDetails != null)
        {
            transactionLogger.debug("status----" + transRespDetails.getStatus());
            if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
            {
                paymentManager.updatePaymentIdForCommon(transRespDetails, commonValidatorVO.getTrackingid());
                html = voucherMoneyUtils.generateAutoSubmitForm(transRespDetails.getPaymentFormUrl());
            }
            /*else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
            {
                paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
            }
            else
            {

            }*/
        }
        return html;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

}







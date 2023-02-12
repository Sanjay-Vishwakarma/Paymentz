package com.payment.cleveland;

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
import com.payment.Ecospend.EcospendUtils;
import com.payment.Enum.PZProcessType;
import com.payment.apcofastpayv6.ApcoFastPayV6Utils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by Jyoti on 10/14/2021.
 */
public class ClevelandPaymentGateway extends AbstractPaymentGateway
{
/*
    public static void main(String[] args)
    {
        String MechId="15";
        String REQUEST_URL="https://api1.adataprotect.com/api/transaction/creates/payments";
        try{
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("payer_id","15");
            jsonObject.put("owner","JONY JONSON");
            jsonObject.put("card_number","4242424242424242");
            jsonObject.put("cvv","314");
            jsonObject.put("validity","07/22");
            jsonObject.put("amount","32.87");
            jsonObject.put("currency","USD");
            jsonObject.put("t_number","49301634");
            jsonObject.put("callback_url","https://www.google.com");

            transaction.logger("JSON Request===============>" +jsonObject);

            String response= clevelandUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, jsonObject.toString());

            System.out.print("JSON  Response============>"+response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
*/
    private static Logger log=new Logger(ClevelandPaymentGateway.class.getName());

    private static TransactionLogger transactionlogger  = new TransactionLogger(ClevelandPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "cleveland";
    ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.cleveland");

    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public ClevelandPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of ClevelandPaymentGateway......");
        log.error("Entering processSale of ClevelandPaymentGateway......");

        ClevelandUtils clevelandUtils=new ClevelandUtils();
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        String URL=RB.getString("URL");
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String REQUEST_URL = "";
        boolean isTest     = gatewayAccount.isTest();
        String PAY_ID      = gatewayAccount.getMerchantId();
        String CardHName   =commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        String  CARD_NUMBER    = commCardDetailsVO.getCardNum();
        String  CVV            = commCardDetailsVO.getcVV();
        String expYear=commCardDetailsVO.getExpYear();
        String  CARD_EXP_DT    = commCardDetailsVO.getExpMonth()+"/"+expYear.substring(2,4);
        String  AMOUNT         = transactionDetailsVO.getAmount();
        String  CURRENCY_CODE=transactionDetailsVO.getCurrency();
        String  t_number=trackingID;
        String tmpl_amount="";
        String tmpl_currency="";

        String CardHName1   = functions.maskingFirstName(commAddressDetailsVO.getFirstname())+" "+functions.maskingLastName(commAddressDetailsVO.getLastname());
        String  CARD_NUMBER1    = functions.maskingPan(commCardDetailsVO.getCardNum());
        String  CVV1       = functions.maskingNumber(commCardDetailsVO.getcVV());

        String  CARD_EXP_DT1   = functions.maskingExpiry(commCardDetailsVO.getExpMonth()+"/"+expYear.substring(2,4));

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }



        String callback_url=RB.getString("CLEVELAND_RU")+trackingID;

       //String callback_url="https://www.google.com";

       /* if(functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            CURRENCY_CODE=transactionDetailsVO.getCurrency();
        }*/

        if(isTest){
            REQUEST_URL = RB.getString("URL");
        }else{
            REQUEST_URL = RB.getString("LIVE_SALE_URL");
        }
        transactionlogger.error("isTest()============================>>>>>>>>"+isTest);
        transactionlogger.error("EXPIRY YEAR============================>>>>>>>>"+CARD_EXP_DT);

        try{
            JSONObject jsonObject=new JSONObject();

            jsonObject.put("payer_id",PAY_ID);
            jsonObject.put("owner",CardHName);
            jsonObject.put("card_number",CARD_NUMBER);
            jsonObject.put("cvv",CVV);
            jsonObject.put("validity",CARD_EXP_DT);
            jsonObject.put("amount",AMOUNT);
            jsonObject.put("currency",CURRENCY_CODE);
            jsonObject.put("t_number",t_number);
            jsonObject.put("callback_url",callback_url);


            JSONObject jsonObject1=new JSONObject();

            jsonObject1.put("payer_id",PAY_ID);
            jsonObject1.put("owner",CardHName1);
            jsonObject1.put("card_number",CARD_NUMBER1);
            jsonObject1.put("cvv",CVV1);
            jsonObject1.put("validity",CARD_EXP_DT1);
            jsonObject1.put("amount",AMOUNT);
            jsonObject1.put("currency",CURRENCY_CODE);
            jsonObject1.put("t_number",t_number);
            jsonObject1.put("callback_url",callback_url);


            transactionlogger.error("JSON Request for trackingid=========>" +trackingID+ " is" + jsonObject1);

            String response= clevelandUtils.doGetHTTPSURLConnectionClient(REQUEST_URL, jsonObject.toString());

            transactionlogger.error("JSON  Response for trackingid=========>" +trackingID+ " is"  + response);


            if (functions.isValueNull(response) && response.contains("{"))
            {
                transactionlogger.error("===========> INSIDE  JSON  RESPONSE============>");
                String status = "";
                String transactionId = "";
                String redirectUrl="";
                String code="";
                String message="";
                JSONObject jsonReader = new JSONObject(response);


                if (jsonReader != null)
                {

                        if (jsonReader.has("result")) {
                        transactionlogger.error("===========> INSIDE RESULT JSON  RESPONSE============>");
                        JSONObject jsonTransactionDetail = jsonReader.getJSONObject("result");
                        if (jsonTransactionDetail != null) {
                            transactionlogger.error("===========> INSIDE RESULT TRANSACTIONID JSON  RESPONSE============>");
                            if (functions.isValueNull(jsonTransactionDetail.getString("transaction"))) {
                                transactionId = jsonTransactionDetail.getString("transaction");
                                transactionlogger.error("===========> INSIDE RESULT TRANSACTIONID JSON  RESPONSE============>"+transactionId);

                            }
                        }
                        if (jsonTransactionDetail != null) {
                            transactionlogger.error("===========> INSIDE RESULT STATUS JSON  RESPONSE============>");
                            if (functions.isValueNull(jsonTransactionDetail.getString("status"))) {

                                status = jsonTransactionDetail.getString("status");
                                transactionlogger.error("===========> INSIDE RESULT STATUS JSON  RESPONSE============>"+status);
                            }
                        }
                        if (jsonTransactionDetail != null) {
                            transactionlogger.error("===========> INSIDE RESULT REDIRECTURL URL JSON  RESPONSE============>");
                                if (jsonTransactionDetail.has("redirect_url")){
                                    if(functions.isValueNull(jsonTransactionDetail.getString("redirect_url")))
                                    {
                                        redirectUrl = jsonTransactionDetail.getString("redirect_url");
                                    }
                                    transactionlogger.error("===========> INSIDE RESULT REDIRECTURL URL JSON  RESPONSE ============>"+redirectUrl);
                                }

                        }
                    }


                    if(jsonReader.has("code") && functions.isValueNull(jsonReader.getString("code"))){
                        code = jsonReader.getString("code");

                    }
                    if(jsonReader.has("message") && functions.isValueNull(jsonReader.getString("message")))
                    {
                        message=jsonReader.getString(("message"));
                    }



                    if(status.equalsIgnoreCase("1"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending ");
                        commResponseVO.setRemark("pending");
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else if(status.equalsIgnoreCase("2"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark("success");
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setCurrency(CURRENCY_CODE);
                    }
                    else if(status.equalsIgnoreCase("3") || code.equalsIgnoreCase("400") || code.equalsIgnoreCase("500"))
                    {
                        if(functions.isValueNull(message))
                        {
                            commResponseVO.setDescription(message);

                        }
                        else
                        {
                            commResponseVO.setDescription("failed");
                        }
                        commResponseVO.setStatus("failed");
                        //commResponseVO.setDescription("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(message);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());


                    }else if(status.equalsIgnoreCase("5"))
                    {
                        transactionlogger.error("===========> INSIDE RESULT STATUS PENDING3DCONFIRMATION  RESPONSE============>"+status);
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setUrlFor3DRedirect(redirectUrl);
                        commResponseVO.setErrorCode(status);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        transactionlogger.error("UrlFor3DRedirect cleveland processSale ===>" + commResponseVO.getUrlFor3DRedirect());
                        transactionlogger.error("===========> INSIDE RESULT STATUS transaction id  RESPONSE============>"+transactionId);


                    }
                    else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                }
                else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }

            else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            transactionlogger.error("ClevelandPaymentGateway ProcessSale() Exception=============>"+e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {

        transactionlogger.error("Inside processInquiry of ClevelandPaymentGateway---");
        log.error("Inside processInquiry of ClevelandPaymentGateway---");
        ClevelandUtils clevelandUtils = new ClevelandUtils();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        String PAY_ID      = gatewayAccount.getMerchantId();
        String  AMOUNT         = commTransactionDetailsVO.getAmount();
        String  CURRENCY_CODE  = commTransactionDetailsVO.getCurrency();
        String  t_number=trackingID;
        String TransID = commTransactionDetailsVO.getPreviousTransactionId();
        //String status = commTransactionDetailsVO.getPrevTransactionStatus();
        try
        {


            transactionlogger.error("isTest()============================>>>>>>>>" + isTest);
            transactionlogger.error("TransID============================>>>>>>>>" + TransID);
            //transactionlogger.error("status()============================>>>>>>>>" + status);


            String inqueryRequest = RB.getString("INQUERY_URL") + TransID + "/status";

            transactionlogger.error("INQUERY REQUEST for trackingid=========>>>>>>>>>" +trackingID+ " is" + inqueryRequest);

            String response = clevelandUtils.doGetHTTPSURLConnectionClient(inqueryRequest);
            transactionlogger.error("INQUERY RESPONSE for trackingid=========>>>>>>>>>>" +trackingID+ " is"  + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                transactionlogger.error("===========> INSIDE  JSON  RESPONSE============>");
                String status = "";

                String redirectUrl="";
                String code="";
                String message="";
                JSONObject jsonReader = new JSONObject(response);


                if (jsonReader != null)
                {

                    if (jsonReader.has("result")) {
                        transactionlogger.error("===========> INSIDE RESULT JSON  RESPONSE============>");
                        JSONObject jsonTransactionDetail = jsonReader.getJSONObject("result");
                        if (jsonTransactionDetail != null)
                        {
                            transactionlogger.error("===========> INSIDE RESULT STATUS JSON  RESPONSE============>");
                            if (functions.isValueNull(jsonTransactionDetail.getString("status"))) {

                                status = jsonTransactionDetail.getString("status");
                                transactionlogger.error("===========> INSIDE RESULT STATUS JSON  RESPONSE============>"+status);
                            }

                       /* if (jsonTransactionDetail != null) {*/

                            if (jsonTransactionDetail.has("redirect_url")){
                                if(functions.isValueNull(jsonTransactionDetail.getString("redirect_url")))
                                {
                                    redirectUrl = jsonTransactionDetail.getString("redirect_url");
                                }
                                transactionlogger.error("===========> INSIDE RESULT REDIRECTURL URL JSON  RESPONSE ============>"+redirectUrl);
                            }

                        }
                    }

                    if(jsonReader.has("code") && functions.isValueNull(jsonReader.getString("code"))){
                        code = jsonReader.getString("code");

                    }
                    if(jsonReader.has("message") && functions.isValueNull(jsonReader.getString("message")))
                    {
                        message=jsonReader.getString(("message"));
                    }
                    if(status.equalsIgnoreCase("1"))
                    {
                        transactionlogger.error("===========> INSIDE RESULT STATUS IN_PROCESS  RESPONSE============>"+status);
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending ");
                        commResponseVO.setRemark("pending");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setMerchantId(PAY_ID);
                        commResponseVO.setTransactionId(TransID);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        commResponseVO.setAmount(AMOUNT);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                    }
                    else if(status.equalsIgnoreCase("2"))
                    {
                        transactionlogger.error("===========> INSIDE RESULT STATUS APPROVED  RESPONSE============>"+status);
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark("success");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setMerchantId(PAY_ID);
                        commResponseVO.setTransactionId(TransID);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        commResponseVO.setAmount(AMOUNT);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                    }
                    else if(status.equalsIgnoreCase("3") || code.equalsIgnoreCase("404") || code.equalsIgnoreCase("500") || code.equalsIgnoreCase("400"))
                    {
                        transactionlogger.error("===========> INSIDE RESULT STATUS DECLINED  RESPONSE============>"+status);
                        if(functions.isValueNull(message))
                        {
                            commResponseVO.setDescription(message);

                        }
                        else
                        {
                            commResponseVO.setDescription("failed");
                        }
                        commResponseVO.setStatus("failed");
                      //  commResponseVO.setDescription("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(message);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setMerchantId(PAY_ID);
                        commResponseVO.setTransactionId(TransID);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        commResponseVO.setAmount(AMOUNT);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                    }else if(status.equalsIgnoreCase("5"))
                    {
                        transactionlogger.error("===========> INSIDE RESULT STATUS PENDING3DCONFIRMATION  RESPONSE============>"+status);
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(redirectUrl);
                        transactionlogger.error("UrlFor3DRedirect cleveland processSale ===>" + commResponseVO.getUrlFor3DRedirect());
                        //String html=clevelandUtils.generateAutoSubmitForm(commResponseVO);

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setMerchantId(PAY_ID);
                        commResponseVO.setTransactionId(TransID);
                        commResponseVO.setCurrency(CURRENCY_CODE);
                        commResponseVO.setAmount(AMOUNT);
                        commResponseVO.setAuthCode(status);
                        commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                        commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());


                        return commResponseVO;

                    }
                    else{
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");

                    }

                }
                else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");

            }

        }
        catch (Exception e){
            transactionlogger.error("ClevelandPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ClevelandPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
}

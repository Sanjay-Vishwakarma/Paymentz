package com.payment.aamarpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONArray;
import org.json.JSONObject;


import java.util.*;

/**
 * Created by Admin on 10/17/2021.
 */

public class AamarPayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger = new TransactionLogger(AamarPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE     = "aamarpay";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.aamarpay");

    public AamarPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("AamarPayPaymentGateway  accountid ------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AamarPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of AamarPayPaymentGateway......");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
       // AamarPayResponseVO commResponseVO                 = new AamarPayResponseVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        Functions functions                             = new Functions();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        AamarPayUtils aamarPayUtils                     = new AamarPayUtils();
        boolean isTest                                  =  gatewayAccount.isTest();;

        String app_id       = gatewayAccount.getMerchantId();
        String store_id     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String signature_key= gatewayAccount.getFRAUD_FTP_PASSWORD();//"dbb74894e82415a2f7ff0ec3a97e4194";

        String tran_id      = trackingID;
        String cus_name     = "";
        String cus_email    = "";
        String cus_add1     = "";
        String cus_add2     = "";
        String cus_city     = "";
        String cus_state    = "";
        String cus_postcode = "";
        String cus_country  = "";
        String cus_phone    = "";
        String amount       = transactionDetailsVO.getAmount();
        String currency     = transactionDetailsVO.getCurrency();
        String desc         = tran_id;

        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String payBrand     = transactionDetailsVO.getCardType();
        String RETURN_URL    = RB.getString("RETURN_URL");

        String REQUEST_URL   = "";
        String REDIRECT_URL  = "";
        String success_url   = "";
        String fail_url      = "";
        String cancel_url    = "";

        StringBuffer requestStringBuffer            = new StringBuffer();
        List<AamarPayResponseVO> paymentList        = null;
        try
        {
            success_url = RETURN_URL + trackingID+"&status=success";
            fail_url    = RETURN_URL + trackingID+"&status=fail";
            cancel_url  = RETURN_URL + trackingID+"&status=cancel";
          //  cardType     = aamarPayUtils.getPaymentType(cardType);

            if (isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }
            else {
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }


            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                cus_name = commAddressDetailsVO.getFirstname() + " "+commAddressDetailsVO.getLastname();
            }
            else{
                cus_name = "Customer";
            }

            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                cus_email = commAddressDetailsVO.getEmail();
            }
            else{
                cus_email = "customer@gmail.com";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCity()))
            {
                cus_city = commAddressDetailsVO.getCity();
            }
            else{
                cus_city = "Delhi";
            }

            if(functions.isValueNull(commAddressDetailsVO.getState()))
            {
                cus_state = commAddressDetailsVO.getState();
            }
            else{
                cus_state = "New Delhi";
            }

            if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            {
                cus_postcode = commAddressDetailsVO.getZipCode();
            }
            else{
                cus_postcode = "110017";
            }

            if(functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                cus_country = commAddressDetailsVO.getCountry();
            }
            else{
                cus_country = "India";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    cus_phone = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(cus_phone.length()>10){

                    cus_phone  = cus_phone.substring(cus_phone.length() - 10);
                }
                else{
                    cus_phone = commAddressDetailsVO.getPhone();
                }
            }
            else{
                cus_phone = "9999999999";
            }

            if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            {
                cus_add1 = commAddressDetailsVO.getStreet();
            }
            else{
                cus_add1 = "Rajiv Chowk";
            }

            cus_add2 = cus_city + " " + cus_postcode +" " + cus_state +" "+cus_country;

            requestStringBuffer.append("app_id="+ app_id);
            requestStringBuffer.append("&signature_key="+ signature_key);
            requestStringBuffer.append("&store_id="+ store_id);
            requestStringBuffer.append("&tran_id="+ tran_id);
            requestStringBuffer.append("&cus_name="+ cus_name);
            requestStringBuffer.append("&cus_phone="+ cus_phone);
            requestStringBuffer.append("&cus_email="+ cus_email);
            requestStringBuffer.append("&cus_add1="+ cus_add1);
            requestStringBuffer.append("&cus_add2="+ cus_add2);
            requestStringBuffer.append("&cus_city="+ cus_city);
            requestStringBuffer.append("&amount="+ amount);
            requestStringBuffer.append("&currency="+ currency);
            requestStringBuffer.append("&desc="+ desc);
            requestStringBuffer.append("&success_url="+ success_url);
            requestStringBuffer.append("&fail_url="+ fail_url);
            requestStringBuffer.append("&cancel_url="+ cancel_url);

            transactionlogger.error("REQUEST_URL "+trackingID + " "+REQUEST_URL );
            transactionlogger.error("operationType "+trackingID + " "+payBrand );
            transactionlogger.error("SaleRequest "+trackingID + " "+requestStringBuffer.toString() );

            String responseString = aamarPayUtils.doPostHTTPSURLConnectionClient(REQUEST_URL,requestStringBuffer.toString());

            transactionlogger.error("SaleResponse "+trackingID + " "+responseString );

            if(functions.isValueNull(responseString) && aamarPayUtils.isJSONValid(responseString)){
                String track        = "";
                String storeinfo    = "";
                String error        = "";
                String cards        = "";
                JSONObject storeinfoJson = null;
                JSONArray cardsArray     = null;
                String card_type         = null;
                String img_medium        = null;
                String optiontype        = null;

                JSONObject resJsonObject = new JSONObject(responseString);

                if(resJsonObject != null){

                    if(resJsonObject.has("track")){
                        track = resJsonObject.getString("track");
                    }
                    if(resJsonObject.has("error")){
                        error = resJsonObject.getString("error");
                    }

                    if(resJsonObject.has("storeinfo")){
                        storeinfo = resJsonObject.getString("storeinfo");
                        storeinfoJson = new JSONObject(storeinfo);
                    }
                    if(resJsonObject.has("error")){
                        error = resJsonObject.getString("error");
                    }

                    if(resJsonObject.has("cards") ){
                        cardsArray = resJsonObject.getJSONArray("cards");
                        int length = cardsArray.length();
                        paymentList = new ArrayList();

                        /*for (int i = 0; i < length; i++)
                        {
                            if (cardsArray.getJSONObject(i).getString("optiontype").equalsIgnoreCase("MFS")
                                && cardsArray.getJSONObject(i).getString("card_type").equalsIgnoreCase(payBrand))
                            {
                                AamarPayResponseVO comm3DResponseVO12 = new AamarPayResponseVO();
                                REDIRECT_URL    = cardsArray.getJSONObject(i).getString("url");
                                card_type       = cardsArray.getJSONObject(i).getString("sdk_text");
                                img_medium      = cardsArray.getJSONObject(i).getString("img_medium");
                                optiontype      = cardsArray.getJSONObject(i).getString("optiontype");


                                comm3DResponseVO12.setUrl(REDIRECT_URL);
                                comm3DResponseVO12.setCard_type(card_type);
                                comm3DResponseVO12.setImg_medium(img_medium);
                                comm3DResponseVO12.setOptiontype(optiontype);
                                paymentList.add(comm3DResponseVO12);
                            }
                        }*/
                        for (int i = 0; i < length; i++)
                        {
                            String resCard_type = cardsArray.getJSONObject(i).getString("card_type");
                            if(resCard_type.contains("-")){
                                resCard_type = resCard_type.replace("-"," ");
                            }
                            transactionlogger.error("resCard_type "+resCard_type);
                            if (cardsArray.getJSONObject(i).getString("optiontype").equalsIgnoreCase("MFS")
                                    && resCard_type.equalsIgnoreCase(payBrand))
                            {
                                AamarPayResponseVO comm3DResponseVO12 = new AamarPayResponseVO();
                                REDIRECT_URL    = cardsArray.getJSONObject(i).getString("url");
                                card_type       = cardsArray.getJSONObject(i).getString("sdk_text");
                                img_medium      = cardsArray.getJSONObject(i).getString("img_medium");
                                optiontype      = cardsArray.getJSONObject(i).getString("optiontype");
                                transactionlogger.error("inside for  "+trackingID + " REDIRECT_URL"+responseString );

                            }
                        }
                    }

                    //commResponseVO.setRequestMap(requestMap);

                /*    if(paymentList != null && paymentList.size() > 0){
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setPaymentList(paymentList);
                        commResponseVO.setCard_type(cardType);
                    }*/

                      if(functions.isValueNull(error)){
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionStatus("fail");
                        commResponseVO.setDescription(error);
                        commResponseVO.setRemark(error);
                      }
                        transactionlogger.error("set commresponse REDIRECT_URL" +REDIRECT_URL);
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(REDIRECT_URL);
                        return commResponseVO;
                    }
                      else{
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionStatus("fail");
                        commResponseVO.setDescription("Invalid response");
                        commResponseVO.setRemark("Invalid response");
                      }
                    } else{
                        commResponseVO.setStatus("fail");
                        commResponseVO.setTransactionStatus("fail");
                        commResponseVO.setDescription("Invalid response");
                        commResponseVO.setRemark("Invalid response");
                    }


        }
       catch (Exception e)
        {
            transactionlogger.error("Exception-------------->",e);
        }

        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredirect in AirPay ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        AamarPayUtils aamarPayUtils             = new AamarPayUtils();
        CommRequestVO commRequestVO         = aamarPayUtils.getAamarPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        AamarPayPaymentProcess aamarPayPaymentProcess = new AamarPayPaymentProcess();
        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = aamarPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect Aamar form -- >>"+html);
            }
            transactionlogger.error("paymentid--------------------->"+transRespDetails.getTransactionId());


        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in AamarPayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of AamarPay---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        AamarPayUtils aamarPayUtils             = new AamarPayUtils();

        String app_id       = gatewayAccount.getMerchantId();
        String store_id     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String signature_key= gatewayAccount.getFRAUD_FTP_PASSWORD();

        String  request_id      = trackingId;
        String  type            = "json";

        String inquiry_res = "";
        String REQUEST_URL = "";
        try
        {

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }

            StringBuffer requestString      =  new StringBuffer();
           requestString.append("request_id="+request_id);
           requestString.append("&store_id="+store_id);
           requestString.append("&signature_key="+signature_key);
           requestString.append("&type="+type);

            REQUEST_URL = REQUEST_URL + requestString.toString();

            transactionlogger.error("inquiry is -- >"+REQUEST_URL);
            transactionlogger.error("inquiry req is --> "+trackingId+" "+requestString.toString());

            inquiry_res = aamarPayUtils.doPostQueryParamHTTPSURLConnectionClient(REQUEST_URL);
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);


            if(functions.isValueNull(inquiry_res) && aamarPayUtils.isJSONValid(inquiry_res)){

                JSONObject responseObject = new JSONObject(inquiry_res);
                String pg_txnid         = "";
                String bank_trxid       = "";
                String amount           = "";
                String pay_status       = "";
                String status_code      = "";
                String approval_code    = "";
                String date_processed   = "";
                String status_title     = "";
                String payment_type     = "";
                String currency     = "";
                String verify_status     = "";


                if(responseObject.has("pg_txnid")){
                    pg_txnid = responseObject.getString("pg_txnid");
                }
                if(responseObject.has("amount")){
                    amount = responseObject.getString("amount");
                }
                if(responseObject.has("pay_status")){
                    pay_status = responseObject.getString("pay_status");
                }

                if(responseObject.has("verify_status")){
                    verify_status = responseObject.getString("verify_status");
                }
                if(responseObject.has("status_code")){
                    status_code = responseObject.getString("status_code");
                }
                if(responseObject.has("approval_code")){
                    approval_code = responseObject.getString("approval_code");
                }
                if(responseObject.has("date_processed")){
                    date_processed = responseObject.getString("date_processed");
                }
                if(responseObject.has("status_title")){
                    status_title = responseObject.getString("status_title");
                }
                if(responseObject.has("payment_type")){
                    payment_type = responseObject.getString("payment_type");
                }
                if(responseObject.has("currency")){
                    currency = responseObject.getString("currency");
                }
                if(responseObject.has("bank_trxid")){
                    bank_trxid = responseObject.getString("bank_trxid");
                }


                if (responseObject != null)
                {

                    if (pay_status.equalsIgnoreCase("Successful") && status_code.equalsIgnoreCase("2") )
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status_title);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(pg_txnid);
                        commResponseVO.setBankTransactionDate(date_processed);
                        commResponseVO.setResponseHashInfo(bank_trxid);
                        commResponseVO.setResponseTime(date_processed);

                    }
                    else if("Not-Available".equalsIgnoreCase(pay_status) && status_title.equalsIgnoreCase("Payment Not Yet Received"))
                    {
                        commResponseVO.setStatus("PENDING");
                        commResponseVO.setDescription("PENDING");
                        commResponseVO.setTransactionStatus("PENDING");
                        commResponseVO.setRemark(status_title);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(pg_txnid);
                        commResponseVO.setBankTransactionDate(date_processed);
                        commResponseVO.setResponseHashInfo(bank_trxid);
                        commResponseVO.setResponseTime(date_processed);
                    }
                   /* else if(pay_status.equalsIgnoreCase("Not-Available") && status_code.equalsIgnoreCase("0"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status_title);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setTransactionId(pg_txnid);
                        commResponseVO.setBankTransactionDate(date_processed);
                        commResponseVO.setResponseHashInfo(bank_trxid);
                        commResponseVO.setResponseTime(date_processed);
                    }*/
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }

                    commResponseVO.setTransactionType(payment_type);
                    commResponseVO.setErrorCode(approval_code);
                    commResponseVO.setAuthCode(approval_code);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setMerchantId(app_id);

                    if(functions.isValueNull(payment_type)){
                        aamarPayUtils.updateTransaction(trackingId,payment_type);
                    }
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }else {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }catch (Exception e)
        {
            transactionlogger.error("AamarPayPaymentGateway processQuery JSONException--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside Aamar process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);

        try
        {


            commResponseVO.setStatus("pending");
            commResponseVO.setTransactionStatus("pending");
            commResponseVO.setRemark("Payout is Pending, Please check the status after some time.");
            commResponseVO.setDescription("Payout is Pending, Please check the status after some time.");

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayout ----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside AamarPay processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                    = false;
        AamarPayUtils aamarPayUtils   = new AamarPayUtils();
        String client_id                  = trackingId;
        String hostURL                    = "";
        Functions functions               = new Functions();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        String tokenString              = gatewayAccount.getFRAUD_FTP_PATH();

        try
        {
            transactionlogger.error("isTest---------->" + isTest);
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            String request_id       ="";
            String param1           = trackingId ;
            String param2           = "";
            String mobile_number    = "";

            HashMap<String,String> hashMap = aamarPayUtils.getTransactionBankDetails(trackingId);

            if(hashMap.containsKey("bankbatchid")){//
                request_id = hashMap.get("bankbatchid");
            }
            if(hashMap.containsKey("sysbatchid")){
                param2 = hashMap.get("sysbatchid");
            }

            String requestString =  hostURL +"?request_id="+request_id+"&param1="+param1+"&param2="+param2+"&mobile_number="+mobile_number;

            transactionlogger.error("payoutInquiryRequest------> "+ trackingId+" " +requestString);

            String responeString    = aamarPayUtils.doPostQueryHTTPSURLConnectionClient(requestString,tokenString);

            transactionlogger.error("payoutInquiryResponse------> "+ trackingId +" "+ responeString);

            if(functions.isValueNull(responeString))
            {
                JSONObject payoutResponse   = new JSONObject(responeString);
                JSONArray jsonArray         = new JSONArray();

                String txnStatus     = "";
                String resAmount     = "";
                String transactionId = "";
                String resParam1 = "";
                String resParam2 = "";

                if(payoutResponse.has("data")){
                    jsonArray =  payoutResponse.getJSONArray("data");
                }

                if(jsonArray.length() > 0){
                 JSONObject jsonData = (JSONObject) jsonArray.get(0);

                    if(jsonData.has("status")){
                        txnStatus = jsonData.getString("status");
                    }
                    if(jsonData.has("amount")){
                        resAmount= jsonData.getString("amount");
                    }
                    if(jsonData.has("param1")){
                        resParam1 = jsonData.getString("param1");
                    }
                    if(jsonData.has("param2")){
                        resParam2= jsonData.getString("param2");
                    }
                    if(jsonData.has("request_id")){
                        request_id= jsonData.getString("request_id");
                    }
                }

                if (txnStatus.equalsIgnoreCase("Successful"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setAmount(resAmount);
                    commResponseVO.setTransactionId(request_id);
                }
                else if (txnStatus.equalsIgnoreCase("failed"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setAmount(resAmount);
                    commResponseVO.setTransactionId(request_id);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayoutInquiry JSONException-->" ,e );
        }

        return commResponseVO;

    }
}



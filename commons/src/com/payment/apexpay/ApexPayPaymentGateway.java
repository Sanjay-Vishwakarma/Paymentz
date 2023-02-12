package com.payment.apexpay;

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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class ApexPayPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(ApexPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "apexpay";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.apexpay");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.APBANKS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public ApexPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of ApexPayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        ApexPayUtils apexPayUtils                   = new ApexPayUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest     = gatewayAccount.isTest();
        String PAY_ID      = gatewayAccount.getMerchantId();
        String SALT        = gatewayAccount.getFRAUD_FTP_USERNAME();
        String SECRETE_KEY = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String RETURN_URL  = RB.getString("APEXPAY_RU")+trackingID;
        String REQUEST_URL = "";

        String CUST_EMAIL  = "";
        String CUST_NAME   = "";
        String ENCDATA     = "";
        String PAYER_ADDRESS ="";

        String  ORDER_ID       = trackingID;
        String  CARD_NUMBER    = commCardDetailsVO.getCardNum();
        String  CARD_EXP_DT    = commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String  CVV            = commCardDetailsVO.getcVV();
        String  PAYMENT_TYPE   = apexPayUtils.getPaymentType(payment_Card);//CARD/WL/NB
        String  MOP_TYPE       = "";
        String  AMOUNT         = ApexPayUtils.getAmount(transactionDetailsVO.getAmount());
        String  CURRENCY_CODE  = "356";
        String  CUST_PHONE     = "9999999999";
        String  HASH           = "";
        PAYER_ADDRESS = commRequestVO.getCustomerId();//UPI
        Map<String, String> requestMap = new HashMap<String, String>();
        try
        {
            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                CUST_NAME   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else {
                CUST_NAME  = "customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
                CUST_EMAIL   = commAddressDetailsVO.getEmail();
            else {
                CUST_EMAIL  = "customer@gmail.com";
            }

            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    CUST_PHONE = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if(CUST_PHONE.length()>10){

                    CUST_PHONE=CUST_PHONE.substring(CUST_PHONE.length() - 10);
                }
                else{
                    CUST_PHONE = commAddressDetailsVO.getPhone();
                }

            }else {
                CUST_PHONE  = "9999999999";
            }


            if(isTest){
                REQUEST_URL = RB.getString("TEST_SALE_URL");
            }else{
                REQUEST_URL = RB.getString("LIVE_SALE_URL");
            }

            requestMap.put("PAY_ID",PAY_ID);
            requestMap.put("ORDER_ID" ,ORDER_ID);
            requestMap.put("PAYMENT_TYPE", PAYMENT_TYPE);
            requestMap.put("AMOUNT",AMOUNT);
            requestMap.put("CURRENCY_CODE",CURRENCY_CODE);
            requestMap.put("CUST_EMAIL",CUST_EMAIL);
            requestMap.put("CUST_PHONE",CUST_PHONE);
            requestMap.put("RETURN_URL",RETURN_URL);

            if("CARD".equalsIgnoreCase(PAYMENT_TYPE))
            {
                requestMap.put("CARD_NUMBER", CARD_NUMBER);
                requestMap.put("CARD_EXP_DT",CARD_EXP_DT);
                requestMap.put("CARD_HOLDER_NAME",CUST_NAME);
                requestMap.put("CVV", CVV);

                MOP_TYPE = apexPayUtils.getPaymentBrand(payment_brand);
            }
            else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
            {
                MOP_TYPE    = payment_brand;
            }
            else if("UP".equalsIgnoreCase(PAYMENT_TYPE))
            {
                MOP_TYPE    = PAYMENT_TYPE;
                requestMap.put("PAYER_ADDRESS",PAYER_ADDRESS);
            }
            else{
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");

                return commResponseVO ;
           }
            requestMap.put("MOP_TYPE", MOP_TYPE);
            HASH                    = apexPayUtils.generateCheckSum(requestMap, SALT);
            String requestString    = apexPayUtils.generateCheckSum2(requestMap,HASH);
            transactionlogger.error("final requestString ---> "+trackingID+" "+requestString);
            ENCDATA                 = apexPayUtils.encryptAES(requestString,SECRETE_KEY);
            requestMap = new HashMap<>();
            requestMap.put("PAY_ID",PAY_ID);
            requestMap.put("ENCDATA",ENCDATA);
            transactionlogger.error("final Request--->" + trackingID + " " + requestMap);

            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setRequestMap(requestMap);

            transactionlogger.error("final requestMap ---> "+trackingID+" "+requestMap);
        }catch (Exception e){
            transactionlogger.error("ApexPayPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in ApexPay ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        ApexPayUtils apexPayUtils                         = new ApexPayUtils();
        ApexPayPaymentProcess apexPayPaymentProcess = new ApexPayPaymentProcess();
        CommRequestVO commRequestVO     = apexPayUtils.getApexPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

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
                html = apexPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect ApexPay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in ApexPayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of ApexPay---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        ApexPayUtils apexPayUtils                         = new ApexPayUtils();
        Map<String, String> hashMap         = new HashMap<String,String>();
        JSONObject parameters               = new JSONObject();

        String SALT             = gatewayAccount.getFRAUD_FTP_USERNAME();
        String PAY_ID           = gatewayAccount.getMerchantId();
        String AMOUNT           = ApexPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String TXNTYPE          = "STATUS";
        String CURRENCY_CODE    = "356";
        String HASH             = "";
        String REQUEST_URL      = "";
        String ORDER_ID         = "";
        String RRN              = "";


        String inquiry_res = "";
        try
        {
            ORDER_ID     = trackingId;

            hashMap.put("PAY_ID",PAY_ID);
            hashMap.put("ORDER_ID",ORDER_ID);
            hashMap.put("AMOUNT",AMOUNT);
            hashMap.put("CURRENCY_CODE",CURRENCY_CODE);
            hashMap.put("TXNTYPE",TXNTYPE);

            HASH = ApexPayUtils.generateCheckSum(hashMap, SALT);
            hashMap.put("HASH",HASH);

            transactionlogger.error("isTest  --- >"+isTest);

            for (String key : hashMap.keySet()) {
                parameters.put(key, hashMap.get(key));
            }

            if (isTest)
            {
                REQUEST_URL = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                REQUEST_URL = RB.getString("INQUIRY_LIVE_URL");
            }
            transactionlogger.error("inquiry is -- >"+REQUEST_URL);
            transactionlogger.error("inquiry req is --> "+trackingId+" "+parameters.toString());

            inquiry_res = apexPayUtils.doGetHTTPSURLConnectionClient(REQUEST_URL,parameters.toString());
            transactionlogger.error("inquiry res is -- > "+trackingId+" "+inquiry_res);

            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String status           ="";
                String responseCode     ="";
                String transactionId    ="";
                String txnType          ="";
                String approvedAmount   ="";
                String currency         ="";
                String dateTime         ="";
                String Description      ="";
                String payId            ="";
                String orderId          ="";

                JSONObject jsonObject = new JSONObject(inquiry_res);

                if (jsonObject != null)
                {
                    if (jsonObject.has("RESPONSE_DATE_TIME"))
                    {
                        dateTime = jsonObject.getString("RESPONSE_DATE_TIME");
                    }
                    if (jsonObject.has("RESPONSE_CODE"))
                    {
                        responseCode = jsonObject.getString("RESPONSE_CODE");
                    }
                    if (jsonObject.has("TXNTYPE"))
                    {
                        txnType = jsonObject.getString("TXNTYPE");
                    }
                    if(jsonObject.has("CURRENCY_CODE"))
                    {
                        currency = jsonObject.getString("CURRENCY_CODE");
                        if(functions.isValueNull(currency)){
                            currency = CurrencyCodeISO4217.getAlphaCurrencyCode(currency);
                        }
                    }
                    if (jsonObject.has("STATUS"))
                    {
                        status = jsonObject.getString("STATUS");
                    }
                    if (jsonObject.has("TXN_ID"))
                    {
                        transactionId = jsonObject.getString("TXN_ID");
                    }
                    if (jsonObject.has("ORDER_ID"))
                    {
                        orderId = jsonObject.getString("ORDER_ID");
                    }
                    if (jsonObject.has("AMOUNT"))
                    {
                        approvedAmount = jsonObject.getString("AMOUNT");
                        approvedAmount = String.format("%.2f", Double.parseDouble(approvedAmount)/100);
                        transactionlogger.error("Inquiry formated Response amount-- "+trackingId+" -->"+approvedAmount);
                    }
                    if (jsonObject.has("PAY_ID"))
                    {
                        payId = jsonObject.getString("PAY_ID");
                    }

                    if (jsonObject.has("RESPONSE_MESSAGE")){

                        Description = jsonObject.getString("RESPONSE_MESSAGE");
                    }
                    if (jsonObject.has("RRN")){

                        RRN  = jsonObject.getString("RRN");
                        if("NA".equalsIgnoreCase(RRN)){
                            RRN = "";
                        }
                    }

                    if (status.equals("Captured"))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);
                        commResponseVO.setRrn(RRN);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setErrorCode(responseCode);
                    }
                    else if(status.equals("Pending") || status.equals("Enrolled")||status.equals("Sent to Bank") )
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);
                    }
                    else if(status.equals("Declined") || status.equals("Rejected")||"Failed".equalsIgnoreCase(status)||"CANCELLED".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);
                        commResponseVO.setRrn(RRN);
                        commResponseVO.setResponseHashInfo(transactionId);
                        commResponseVO.setErrorCode(responseCode);
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
            // no response set pending
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }catch (JSONException e)
        {
            transactionlogger.error("ApexPayPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("ApexPayPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside ApexPay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        ApexPayUtils apexPayUtils                           = new ApexPayUtils();

        String hostURL          = "";
        String bankName         = "";
        String Client_id        = trackingId;
        String beneficiaryName  = "";
        String accountNumber    = "";
        String type             = "";
        String remittanceAmount = "";
        String IFSCCode         = "";
        String MobileNumber     = "9999999999";
        String api_token        = gatewayAccount.getFRAUD_FTP_PATH();
        String  HASH            = "";
        String payoutKey        = gatewayAccount.getFRAUD_FTP_SITE();
        JSONObject jsonObject   = new JSONObject();

        try
        {
            if (isTest)
            {
                hostURL  = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                String tempbeneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();

                if(tempbeneficiaryName.contains("-")||tempbeneficiaryName.contains("_")||tempbeneficiaryName.contains(",")){
                    beneficiaryName = tempbeneficiaryName.replaceAll("-","").replaceAll("_","").replaceAll(",","");
                }
                else {
                    beneficiaryName = tempbeneficiaryName;
                }

            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                IFSCCode = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                remittanceAmount = commTransactionDetailsVO.getAmount();
                remittanceAmount = String.format("%.0f", Double.parseDouble(remittanceAmount));
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                type = commTransactionDetailsVO.getBankTransferType();
            }
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-")){
                    MobileNumber  = commAddressDetailsVO.getPhone().split("-")[1];
                }
                else{
                    MobileNumber  = commAddressDetailsVO.getPhone();
                }
            }

            bankName = beneficiaryName.trim();

            String strForHash   = MobileNumber + IFSCCode + remittanceAmount + accountNumber + beneficiaryName + Client_id + type + payoutKey;

            HASH = apexPayUtils.getPayOutHash(strForHash);

            jsonObject.put("hash",HASH);
            jsonObject.put("api_token",api_token);
            jsonObject.put("RemittanceAmount",remittanceAmount);
            jsonObject.put("Bank",bankName);
            jsonObject.put("MobileNumber",MobileNumber);
            jsonObject.put("IFSCCode",IFSCCode);
            jsonObject.put("AccountNumber",accountNumber);
            jsonObject.put("BeneficiaryName",bankName);
            jsonObject.put("client_id",Client_id);
            jsonObject.put("type",type);

           // transactionlogger.error("apexpayPayFinalRequest hostURL------> "+hostURL);
         //   transactionlogger.error("apexpayPayFinalRequest strForHash------> "+strForHash);
            transactionlogger.error("apexpayPayFinalRequest payout1------> trackingid---->"+trackingId + jsonObject.toString());

            String responeString = apexPayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

            transactionlogger.error("apexpayPayFinalResponse  payout1------> trackingid---->"+trackingId+ responeString);


            String MSG               = "";
            String message           = "";
            String RRN               = "";
            String beneficiaryNam    = "";
            String transactionId     = "";
            String txtStatus         = "";

                if(functions.isValueNull(responeString) && responeString.contains("{"))
                {
                    JSONObject payoutResponse = new JSONObject(responeString);


                    if (payoutResponse.has("MSG") && functions.isValueNull(payoutResponse.getString("MSG")))
                    {
                        MSG = payoutResponse.getString("MSG");
                    }
                    /*if (payoutResponse.has("message") && functions.isValueNull(payoutResponse.getString("message")))
                    {
                        message = payoutResponse.getString("message");
                    }*/

                    if (payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS")))
                    {
                        txtStatus = payoutResponse.getString("STATUS");
                    }
                    if (payoutResponse.has("RRN") && functions.isValueNull(payoutResponse.getString("RRN")))
                    {
                        RRN = payoutResponse.getString("RRN");
                    }

                    if (txtStatus.equalsIgnoreCase("SUCCESS"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark(MSG);
                        commResponseVO.setDescription(MSG);

                        apexPayUtils.updateRRNMainTableEntry(RRN,trackingId);
                    }
                    else if (txtStatus.equalsIgnoreCase("PENDING"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(MSG);
                        commResponseVO.setDescription(MSG);

                    }
                    else if (txtStatus.equalsIgnoreCase("FAILED") || txtStatus.equalsIgnoreCase("REFUNDED") )
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        if(txtStatus.equalsIgnoreCase("REFUNDED")){
                            commResponseVO.setRemark("failed");
                            commResponseVO.setDescription("failed");
                        }else{
                            commResponseVO.setRemark(MSG);
                            commResponseVO.setDescription(MSG);
                        }

                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

                    }
                    transactionlogger.error("---transactionId--->" + transactionId + "------" + "RRN---> " + RRN);
                    commResponseVO.setTransactionId(RRN);
                    commResponseVO.setResponseHashInfo(RRN);
                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");

                }

            String CALL_EXECUTE_AFTER       = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL    = RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC          = RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed          = RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncApexPayPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

        }
        catch (Exception e)
        {
            transactionlogger.error("processPayout ----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside ApexPay processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        String api_token                  = "";
        boolean isTest                    = false;
        JSONObject jsonObject             = new JSONObject();
        ApexPayUtils apexPayUtils   = new ApexPayUtils();
        String client_id                  = trackingId;
        String hostURL                    = "";
        Functions functions               = new Functions();
        try
        {
            api_token = gatewayAccount.getFRAUD_FTP_PATH();
            client_id = trackingId;
            isTest    = gatewayAccount.isTest();
            transactionlogger.error("isTest---------->" + isTest);
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }

            jsonObject.put("api_token",api_token);
            jsonObject.put("client_id",client_id);

            transactionlogger.error("payoutInquiryRequest------> "+ trackingId+" " +jsonObject.toString());
            String responeString    = apexPayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

            transactionlogger.error("payoutInquiryResponse------> "+ trackingId +" "+ responeString);
            if(functions.isValueNull(responeString))
            {
                JSONObject payoutResponse   = new JSONObject(responeString);

                String MSG           = "";
                String txnStatus     = "";
                String transactionId = "";

                if(payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS"))){
                    txnStatus    = payoutResponse.getString("STATUS");
                }
                if(payoutResponse.has("MSG") && functions.isValueNull(payoutResponse.getString("MSG"))){
                    MSG = payoutResponse.getString("MSG");
                }
                if(payoutResponse.has("opid") && functions.isValueNull(payoutResponse.getString("opid"))){
                    transactionId   = payoutResponse.getString("opid");
                }


                if (txnStatus.equalsIgnoreCase("SUCCESS"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(MSG);
                    commResponseVO.setDescription(MSG);
                }
                else if (txnStatus.equalsIgnoreCase("PENDING"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(MSG);
                    commResponseVO.setDescription(MSG);
                }
                else if (txnStatus.equalsIgnoreCase("FAILED") || txnStatus.equalsIgnoreCase("REFUNDED"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    if(txnStatus.equalsIgnoreCase("REFUNDED")){
                        commResponseVO.setRemark("failed");
                        commResponseVO.setDescription("failed");
                    }else{
                        commResponseVO.setRemark(MSG);
                        commResponseVO.setDescription(MSG);
                    }

                }
                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                }
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setResponseHashInfo(transactionId);
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

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ApexPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }


}
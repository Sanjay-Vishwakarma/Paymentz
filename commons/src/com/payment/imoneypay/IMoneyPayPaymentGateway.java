package com.payment.imoneypay;

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

import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class IMoneyPayPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(IMoneyPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "imoneypay";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.imoneypay");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.IMBANKS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public IMoneyPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of IMoneyPayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        IMoneypPayUtils iMoneypPayUtils                 = new IMoneypPayUtils();
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
        String RETURN_URL  = RB.getString("IMONEYPAY_RU")+trackingID;
        String REQUEST_URL = "";

        String CUST_EMAIL  = "";
        String CUST_NAME   = "";
        String ENCDATA     = "";
        String PAYER_ADDRESS ="";
        String  CARD_HOLDER_NAME = "";

        String  ORDER_ID       = trackingID;
        String  CARD_NUMBER    = commCardDetailsVO.getCardNum();
        String  CARD_EXP_DT    = commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String  CVV            = commCardDetailsVO.getcVV();
        String  PAYMENT_TYPE   = iMoneypPayUtils.getPaymentType(payment_Card);//CARD/WL/NB
        String  MOP_TYPE       = "";
        String  AMOUNT         = IMoneypPayUtils.getAmount(transactionDetailsVO.getAmount());
        String  CURRENCY_CODE  = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        String  CUST_PHONE     = "9999999999";
        String  HASH           = "";
        String customerId      = "";
        transactionlogger.error("vpa requestVO--- " +commRequestVO.getCustomerId());
        PAYER_ADDRESS = commRequestVO.getCustomerId();
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

                MOP_TYPE = iMoneypPayUtils.getPaymentBrand(payment_brand);
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
            HASH                    = iMoneypPayUtils.generateCheckSum(requestMap, SALT);
            String requestString    = iMoneypPayUtils.generateCheckSum2(requestMap,HASH);
            transactionlogger.error("final requestString ---> "+trackingID+" "+requestString);
            ENCDATA                 = iMoneypPayUtils.encryptAES(requestString,SECRETE_KEY);
            requestMap = new HashMap<>();
            requestMap.put("PAY_ID",PAY_ID);
            requestMap.put("ENCDATA",ENCDATA);
            transactionlogger.error("final Request--->" + trackingID + " " + requestMap);

            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setRequestMap(requestMap);

            transactionlogger.error("final requestMap ---> "+trackingID+" "+requestMap);
        }catch (Exception e){
            transactionlogger.error("IMoneyPayPaymentGateway ---------------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in IMoneyPay ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        IMoneypPayUtils iMoneypPayUtils                         = new IMoneypPayUtils();
        IMoneyPayPaymentProcess iMoneyPayPaymentProcess = new IMoneyPayPaymentProcess();
        CommRequestVO commRequestVO     = iMoneypPayUtils.getIMoneyPayRequestVO(commonValidatorVO);
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
                html = iMoneyPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect imoney form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in IMoneyPayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of IMoneyPay---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        IMoneypPayUtils iMoneypPayUtils                         = new IMoneypPayUtils();
        Map<String, String> hashMap         = new HashMap<String,String>();
        JSONObject parameters               = new JSONObject();

        String SALT             = gatewayAccount.getFRAUD_FTP_USERNAME();
        String PAY_ID           = gatewayAccount.getMerchantId();
        String AMOUNT           = IMoneypPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String TXNTYPE          = "STATUS";
        String CURRENCY_CODE    = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
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

            HASH = IMoneypPayUtils.generateCheckSum(hashMap,SALT);
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

            inquiry_res = iMoneypPayUtils.doGetHTTPSURLConnectionClient(REQUEST_URL,parameters.toString());
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

                    if (responseCode.equals("000")&&"Captured".equalsIgnoreCase(status))
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
                    else if(responseCode.equals("006") || responseCode.equals("026") )
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
                    else if(responseCode.equals("004") || responseCode.equals("002")||responseCode.equals("007") ||"Failed".equalsIgnoreCase(status) ||"Cancelled".equalsIgnoreCase(status))
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
            transactionlogger.error("IMoneyPayPaymentGateway processQuery JSONException--->",e);
        }catch (Exception e){
            transactionlogger.error("IMoneyPayPaymentGateway processQuery Exception--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside IMonyPay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        IMoneypPayUtils iMoneypPayUtils                     = new IMoneypPayUtils();

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

            String strForHash   = MobileNumber + IFSCCode + remittanceAmount + accountNumber + bankName + Client_id + type + payoutKey;

            HASH = iMoneypPayUtils.getPayOutHash(strForHash);

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

            transactionlogger.error("imoneyPayFinalRequest hostURL------> "+hostURL);
            transactionlogger.error("imoneyPayFinalRequest strForHash------> "+strForHash);
            transactionlogger.error("imoneyPayFinalRequest payout1------> trackingid---->"+trackingId + jsonObject.toString());

            String responeString = iMoneypPayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

            transactionlogger.error("imoneyPayFinalResponse  payout1------> trackingid---->"+trackingId+ responeString);


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
                    if (payoutResponse.has("message") && functions.isValueNull(payoutResponse.getString("message")))
                    {
                        message = payoutResponse.getString("message");
                    }

                    if (payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS")))
                    {
                        txtStatus = payoutResponse.getString("STATUS");
                    }
                    if (payoutResponse.has("RRN") && functions.isValueNull(payoutResponse.getString("RRN")))
                    {
                        RRN = payoutResponse.getString("RRN");
                    }
                    if (payoutResponse.has("Name") && functions.isValueNull(payoutResponse.getString("Name")))
                    {
                        beneficiaryNam = payoutResponse.getString("Name");
                    }


                    if (txtStatus.equalsIgnoreCase("SUCCESS"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark(MSG+" "+message);
                        commResponseVO.setDescription(MSG+" "+message);
                        commResponseVO.setFullname(beneficiaryNam);

                        iMoneypPayUtils.updateRRNMainTableEntry(RRN,trackingId);
                    }
                    else if (txtStatus.equalsIgnoreCase("PENDING"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(MSG+" "+message);
                        commResponseVO.setDescription(MSG+" "+message);

                    }
                    else if (txtStatus.equalsIgnoreCase("FAILED") || txtStatus.equalsIgnoreCase("REFUNDED") )
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        if(transactionId.contains("FAIL") || transactionId.contains("Invalid")){
                            commResponseVO.setRemark(transactionId);
                            commResponseVO.setDescription(transactionId);
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
                new AsyncIMoneyPayPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
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
        transactionlogger.error("-----inside IMoneyPay processPayout inquiry------->");

        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        String api_token                  = "";
        boolean isTest                    = false;
        JSONObject jsonObject             = new JSONObject();
        IMoneypPayUtils iMoneypPayUtils   = new IMoneypPayUtils();
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
            String responeString    = iMoneypPayUtils.doGetHTTPSURLConnectionClient(hostURL, jsonObject.toString());

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
                    if(transactionId.contains("FAIL") || transactionId.contains("Invalid")){
                        commResponseVO.setRemark(transactionId);
                        commResponseVO.setDescription(transactionId);
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
        PZGenericConstraint genConstraint = new PZGenericConstraint("IMoneyPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    /*public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionlogger.error("Inside processRefund ---");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();

        Functions functions             = new Functions();
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);
        String PAY_ID                      = gatewayAccount.getMerchantId();
        boolean isTest                  = gatewayAccount.isTest();
        String salt                     = gatewayAccount.getFRAUD_FTP_USERNAME();
        String amount                   = LetzPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currencyCode             = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String inquiryUrl               = "";
        String bankTransactionDate      = "";
        String responseCode             = "";
        String rrn                      = "";
        String transactionId            = "";
        String message                  = "";
        String resStatus                = "";
        String PG_REF_NUM               = "";
        JSONObject parameters           = new JSONObject();
        Map<String, String> hashMap     = new HashMap<String,String>();
        try
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_INQUIRY_URL");
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL");
            }

            hashMap.put("PAY_ID", PAY_ID);
            hashMap.put("ORDER_ID", trackingID);
            hashMap.put("AMOUNT", amount);
            hashMap.put("REFUND_FLAG", "C");
            hashMap.put("PG_REF_NUM", commTransactionDetailsVO.getPaymentId());
            hashMap.put("REFUND_ORDER_ID", "RF_"+commRequestVO.getCount()+"_"+trackingID);
            hashMap.put("TXNTYPE", "REFUND");
            hashMap.put("CURRENCY_CODE", currencyCode);

            String HASH  = IMoneypPayUtils.generateCheckSum(hashMap, salt);

            for (String key : hashMap.keySet()) {
                parameters.put(key, hashMap.get(key));
            }
            parameters.put("HASH",HASH);

            transactionlogger.error("hashStr--->"+HASH);

            transactionlogger.error("Refund Request--"+trackingID+"-->"+parameters);
            String response = IMoneypPayUtils.doGetHTTPSURLConnectionClient(inquiryUrl, parameters.toString());
            transactionlogger.error("Refund Response--"+trackingID+"-->"+response);

            if(functions.isValueNull(response))
            {
                JSONObject responseJson=new JSONObject(response);
                if(responseJson.has("RESPONSE_DATE_TIME"))
                    bankTransactionDate=responseJson.getString("RESPONSE_DATE_TIME");
                if(responseJson.has("RESPONSE_CODE"))
                    responseCode=responseJson.getString("RESPONSE_CODE");
                if(responseJson.has("TXN_ID"))
                    transactionId=responseJson.getString("TXN_ID");
                if(responseJson.has("RRN"))
                    rrn=responseJson.getString("RRN");
                if(responseJson.has("RESPONSE_MESSAGE"))
                    message=responseJson.getString("RESPONSE_MESSAGE");
                if(responseJson.has("STATUS"))
                    resStatus=responseJson.getString("STATUS");
                if(responseJson.has("PG_REF_NUM"))
                    PG_REF_NUM=responseJson.getString("PG_REF_NUM");

                if("000".equalsIgnoreCase(responseCode) && "Captured".equalsIgnoreCase(resStatus))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else if("006".equalsIgnoreCase(responseCode) || "026".equalsIgnoreCase(responseCode)|| "032".equalsIgnoreCase(responseCode))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                }else{
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                }
                commResponseVO.setBankTransactionDate(bankTransactionDate);
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setRemark(message);
                commResponseVO.setDescription(message);
                commResponseVO.setRrn(rrn);
                commResponseVO.setResponseHashInfo(PG_REF_NUM);
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("JSONException---" + trackingID + "--->",e);
        }


        return commResponseVO;
    }*/
}
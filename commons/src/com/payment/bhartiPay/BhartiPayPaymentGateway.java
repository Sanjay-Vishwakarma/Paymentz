package com.payment.bhartiPay;


import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.safexpay.AsyncSafexPayPayoutQueryService;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class BhartiPayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger = new TransactionLogger(BhartiPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "bhartipay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.BhartiPay");
    private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.BPBANKS");
    CommonValidatorVO commonValidatorVO =new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BhartiPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of BhartiPayGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        BhartiPayUtils bhartiPayUtils = new BhartiPayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand= transactionDetailsVO.getCardType();
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();

        String CURRENCY_CODE= CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        transactionlogger.error("CURRENCY_CODE is-------------->"+CURRENCY_CODE);
        String AMOUNT=BhartiPayUtils.getAmount(transactionDetailsVO.getAmount());
        transactionlogger.error("AMOUNT is-------------->"+AMOUNT);
        String CUST_EMAIL=commAddressDetailsVO.getEmail();
        transactionlogger.error("CUST_EMAIL is-------------->"+CUST_EMAIL);
        String CUST_NAME="";
        if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
          CUST_NAME=commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        else {
             CUST_NAME=" ";
        }
        String trackingidName="trackingid";
        transactionlogger.error("CUST_NAME is-------------->"+CUST_NAME);
        String ORDER_ID=trackingidName+trackingID;
        transactionlogger.error("ORDER_ID is-------------->"+ORDER_ID);
        String PAY_ID=gatewayAccount.getMerchantId();
        transactionlogger.error("PAY_ID is-------------->"+PAY_ID);
        String PAYMENT_TYPE = bhartiPayUtils.getPaymentType(payment_Card);
        transactionlogger.error("PAYMENT_TYPE is-------------->"+PAYMENT_TYPE);
        String CARD_NUMBER=commCardDetailsVO.getCardNum();
        String CARD_EXP_DT=commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String CVV=commCardDetailsVO.getcVV();
        String RETURN_URL= RB.getString("BHARTIPAY_RU")+trackingID;
        transactionlogger.error("RETURN_URL is-------------->"+RETURN_URL);
        String TXNTYPE="SALE";
        transactionlogger.error("TXNTYPE is-------------->"+TXNTYPE);
        String SALT=gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("SALT is-------------->"+SALT);
        String processorName = commonValidatorVO.getProcessorName();
        transactionlogger.error("processorName--" + processorName);
        String payment_url="";
        String HASH ="";
        String MOP_TYPE = payment_brand;




        Map<String, String> map = new HashMap<String, String>();
        map.put("AMOUNT",AMOUNT);
        map.put("CURRENCY_CODE", CURRENCY_CODE);
        map.put("CUST_EMAIL", CUST_EMAIL);
        map.put("CUST_NAME",CUST_NAME );
        map.put("ORDER_ID", ORDER_ID);// rrn
        map.put("PAY_ID", PAY_ID);// static
        //map.put("PAYMENT_TYPE", PAYMENT_TYPE);

        if(functions.isValueNull(payment_Card)){
           map.put("MERCHANT_PAYMENT_TYPE", PAYMENT_TYPE);
        }

        if("CC".equalsIgnoreCase(PAYMENT_TYPE)||"DC".equalsIgnoreCase(PAYMENT_TYPE))
        {
            transactionlogger.error("inside CC & DC condition (map)-------------->"+PAYMENT_TYPE);

            //map.put("CARD_NUMBER", CARD_NUMBER);
            //map.put("CARD_EXP_DT", CARD_EXP_DT);// rrn
            //map.put("CVV", CVV);// static
            if (isTest){
                payment_url=RB.getString("TEST_SALE_URL");
                transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);
            }
            else {
                payment_url=RB.getString("LIVE_SALE_URL");
                transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);

            }
        }
        else if("NB".equalsIgnoreCase(PAYMENT_TYPE) || "WL".equalsIgnoreCase((PAYMENT_TYPE)) || "UP".equalsIgnoreCase((PAYMENT_TYPE)))
        {
            transactionlogger.error("inside netbanking  and Wallets MOB_TYPE-------------------------->"+payment_brand);
            //map.put("MOP_TYPE", MOP_TYPE);
            if (isTest){
                payment_url=RB.getString("TEST_SALE_URL");
                transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


            }
            else {
                payment_url=RB.getString("LIVE_SALE_URL");
                transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);

            }

        }
        map.put("RETURN_URL", RETURN_URL);// callback -
        map.put("TXNTYPE",TXNTYPE);// statc

        try
        {
            HASH  = BhartiPayUtils.generateCheckSum(map, SALT);// static
            map.put("HASH",HASH);
            transactionlogger.error("hash is-------------->"+HASH );
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("-----NoSuchAlgorithmException bharatipay processSale-----", e);
        }

        transactionlogger.error("request is-------------->"+map);

        commResponseVO.setStatus("pending3DConfirmation");
        commResponseVO.setUrlFor3DRedirect(payment_url);
        commResponseVO.setRequestMap(map);
       bhartiPayUtils.updateOrderid(ORDER_ID ,trackingID);
        return commResponseVO;

    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Bhartipay ---- ");
        String html = "";
        PaymentManager paymentManager = new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        BhartiPayUtils bhartiPayUtils = new BhartiPayUtils();
        BhartiPayPaymentProcess bhartiPayPaymentProcess = new BhartiPayPaymentProcess();

        String paymentMode = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO = bhartiPayUtils.getBhartiPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

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
                html = bhartiPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect bhartipay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in BhartiPaypaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of BhartipayGateway---");
        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommRequestVO reqVO                                 = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = reqVO.getTransDetailsVO();
        //OneRoadUtils oneRoadUtils = new OneRoadUtils();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        Functions functions                                 = new Functions();
        BhartiPayUtils bhartiPayUtils                       = new BhartiPayUtils();

        String PAY_ID           = gatewayAccount.getMerchantId();
        transactionlogger.error("PAY_ID is -- >"+PAY_ID);
        String trackingidName   = "trackingid";
        String ORDER_ID         = trackingidName+trackingId;
        transactionlogger.error("ORDER_ID is --- >"+ORDER_ID);
        String req              = "PAY_ID="+PAY_ID+"&ORDER_ID="+ORDER_ID;
        transactionlogger.error("inquiry req is -->"+req);

        String inquiry_res      = "";

        if (isTest)
        {
            transactionlogger.error("inside test req of inquiry is -- >"+RB.getString("INQUIRY_TEST_URL"));
            inquiry_res = bhartiPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_TEST_URL")+req);
        }
        else
        {
            transactionlogger.error("inside live req of inquiry is -- >"+RB.getString("INQUIRY_LIVE_URL"));
            inquiry_res = bhartiPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE_URL")+req);

        }
        transactionlogger.error("inquiry res is -- >"+inquiry_res);

        try
        {
            if (functions.isValueNull(inquiry_res)&& inquiry_res.contains("{"))
            {
                String status           = "";
                String responseCode     = "";
                String transactionId    = "";
                String txnType          = "";
                String approvedAmount   = "";
                String currency         = "";
                String dateTime         = "";
                String Description      = "";
                String payId            = "";
                String orderId          = "";

                JSONObject jsonObject = new JSONObject(inquiry_res);

                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                        transactionlogger.error("status"+status);
                    }
                    if (jsonObject.has("responseCode"))
                    {
                        responseCode = jsonObject.getString("responseCode");
                        transactionlogger.error("responseCode"+responseCode);
                    }
                    if (jsonObject.has("transactionId"))
                    {
                        transactionId = jsonObject.getString("transactionId");
                        transactionlogger.error("transactionId"+transactionId);
                    }
                    if (jsonObject.has("txnType"))
                    {
                        txnType = jsonObject.getString("txnType");
                    }
                    if (jsonObject.has("approvedAmount"))
                    {
                        approvedAmount = jsonObject.getString("approvedAmount");
                    }
                    if(jsonObject.has("currency"))
                    {
                        currency = jsonObject.getString("currency");
                    }
                    if (jsonObject.has("dateTime"))
                    {
                        dateTime = jsonObject.getString("dateTime");
                    }
                    if (jsonObject.has("payId"))
                    {
                        payId = jsonObject.getString("payId");
                        transactionlogger.error("payId --"+payId);
                    }
                    if (jsonObject.has("orderId"))
                    {
                        orderId = jsonObject.getString("orderId");
                        transactionlogger.error("orderId -- >"+orderId);
                    }
                    if (jsonObject.has("Description"))
                    Description     = jsonObject.getString("Description");
                    if (responseCode.equals("000"))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);

                    }

                    else if(responseCode.equals("006")&&!status.equalsIgnoreCase("Cancelled"))
                    {

                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("pending");
                        bhartiPayUtils.updateMainTableEntry(status,trackingId);
                    }
                    else
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);

                    }

                }
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setDescription("Inquiry response not found");  // set inquiry response not found
                commResponseVO.setTransactionStatus("Inquiry response not found"); // set inquir
            }
        }catch (JSONException e)
        {
            transactionlogger.error("-----JSONException bharatipay processQuery-----",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside bharatipay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();

        String hostURL          = "";
        String aggregatorId     = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String cryptyKey        = gatewayAccount.getFRAUD_FTP_PATH();;
        String beneName         = "";
        String accountNo        = "";
        String ifscCode         = "";
        String bankName         = "";
        String amount           = "0.00";
        String transferType     = "";
        String merchantTransId  = trackingId;

        JSONObject jsonObject   = new JSONObject();
        try
        {
            if (isTest)
            {
                hostURL         = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                beneName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accountNo = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                ifscCode = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                bankName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount = commTransactionDetailsVO.getAmount();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                transferType = commTransactionDetailsVO.getBankTransferType();
            }

            transactionlogger.error("bharatipayResuwsData---beneName---> " + beneName);
            transactionlogger.error("bharatipayResuwsData---accountNo---> " + accountNo);
            transactionlogger.error("bharatipayResuwsData---ifscCode---> " + ifscCode);
            transactionlogger.error("bharatipayResuwsData---bankName---> " + bankName);
            transactionlogger.error("bharatipayResuwsData---amount---> " + amount);
            transactionlogger.error("bharatipayResuwsData---transferType---> " + transferType);
            transactionlogger.error("bharatipayResuwsData---hostURL---> " + hostURL);

            jsonObject.put("beneName", beneName);
            jsonObject.put("accountNo", accountNo);
            jsonObject.put("ifscCode", ifscCode);
            jsonObject.put("amount", amount);
            jsonObject.put("transferType", transferType);
            jsonObject.put("merchantTransId", merchantTransId);
            jsonObject.put("bankName", bankName);

            transactionlogger.error("bharatipayFinalRequest------> " + jsonObject.toString());
            String encryptedRequest = BhartiPayUtils.encryptPayout(jsonObject.toString(), cryptyKey);

            jsonObject = new JSONObject();
            jsonObject.put("aggregatorId", aggregatorId);
            jsonObject.put("request", encryptedRequest);

            transactionlogger.error("bharatipayFinalRequestEncrypted------> " + jsonObject.toString());
            String responeString = BhartiPayUtils.doPostHttpUrlConnection(hostURL, jsonObject.toString());
            transactionlogger.error("bharatipayResponse------> " + responeString);
            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncBhartiPayPayoutService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }

            String description       = "";
            String code              = "";
            String message           = "";
            String authcode          = "";
            String responseOrderid   = "";
            String beneficiaryName   = "";
            String accountNumber     = "";
            String IFSCCodeRes       = "";
            String transferStatus    = "";
            String transactionId     = "";
            String paymentId         = "";
            String finalResponse     = "";

            JSONObject payoutResponse   = new JSONObject(responeString);

            JSONObject beneficiary      = new JSONObject();
            JSONObject moneyRemittance  = new JSONObject();

            if(payoutResponse.has("response")){

                String responseString   = payoutResponse.getString("response");
                transactionlogger.error("responseString------> " + responseString);
                String finalDencryptResponse   = BhartiPayUtils.dencryptPayout(responseString, cryptyKey);
                transactionlogger.error("bharatipayfinalDencryptResponse------> " + finalDencryptResponse);

                payoutResponse    = new JSONObject(finalDencryptResponse);

                if (payoutResponse.has("code"))
                {
                    code = payoutResponse.getString("code");
                    transactionlogger.error("code-->" + code);
                }

                if (payoutResponse.has("beneficiary"))
                {
                    beneficiary     = payoutResponse.getJSONObject("beneficiary");

                    if (beneficiary.has("beneficiaryName"))
                    {
                        beneficiaryName   = beneficiary.getString("beneficiaryName");
                    }

                    if (beneficiary.has("accountNo"))
                    {
                        accountNumber   = beneficiary.getString("accountNo");
                    }

                    if (beneficiary.has("ifscCode"))
                    {
                        IFSCCodeRes   = beneficiary.getString("ifscCode");
                    }
                }
                transactionlogger.error("bharatipayBeneficiary--- beneficiaryName ---> " + beneficiaryName+" ---accountNumber---> " + accountNumber +"---accountNumber---> " + accountNumber + "---IFSCCodeRes---> " + IFSCCodeRes);

                if (payoutResponse.has("moneyRemittance"))
                {

                    moneyRemittance     = payoutResponse.getJSONObject("moneyRemittance");

                    if (moneyRemittance.has("transferStatus"))
                    {
                        transferStatus = moneyRemittance.getString("transferStatus");
                    }
                    if (moneyRemittance.has("transId"))
                    {
                        transactionId = moneyRemittance.getString("transId");
                    }
                    if (moneyRemittance.has("paymentId"))
                    {
                        paymentId = moneyRemittance.getString("paymentId");
                    }
                }
                if (payoutResponse.has("message"))
                {
                    message = payoutResponse.getString("message");
                    transactionlogger.error("message-->" + message);
                }
            }
            transactionlogger.error("bharatipayMoneyRemittance--- transferStatus ---> " + transferStatus+"---transactionId--->"+transactionId+"------"+"paymentId---> " + paymentId);

            if (transferStatus.equalsIgnoreCase("SUCCESS"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark(transferStatus);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(IFSCCodeRes);
                commResponseVO.setFullname(beneficiaryName);
                commResponseVO.setBankaccount(accountNumber);
            }
            else if (transferStatus.equalsIgnoreCase("PENDING") || transferStatus.equalsIgnoreCase("PROCESSING"))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark(transferStatus);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(IFSCCodeRes);
                commResponseVO.setFullname(beneficiaryName);
                commResponseVO.setBankaccount(accountNumber);
                BhartiPayUtils.updatePayoutTransaction(trackingId,transactionId,paymentId);
            }
            else  if (transferStatus.equalsIgnoreCase("FAILED"))
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(transferStatus);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(IFSCCodeRes);
                commResponseVO.setFullname(beneficiaryName);
                commResponseVO.setBankaccount(accountNumber);
            }
            transactionlogger.error("---transactionId--->"+transactionId+"------"+"paymentId---> " + paymentId);
            commResponseVO.setTransactionId(paymentId);
            commResponseVO.setResponseHashInfo(transactionId);
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside bharatipay process payout inquiry------->");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        BhartiPayUtils bhartiPayUtils=new BhartiPayUtils();
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String aggregatorId                                 = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String cryptyKey                                    = gatewayAccount.getFRAUD_FTP_PATH();
        boolean isTest                                      = gatewayAccount.isTest();
        String merchantTransId                               = "";
        JSONObject jsonObject                               = new JSONObject();
        transactionlogger.error("trackingId---------->" + trackingId);
        transactionlogger.error("isTest---------->" + isTest);
        String hostURL              = "";
        try
        {
            merchantTransId        = trackingId;
            if (isTest)
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");

            }

            jsonObject.put("transid",trackingId);
            transactionlogger.error("hostURL---->"+hostURL);
            transactionlogger.error("cryptyKey---->"+cryptyKey);
            transactionlogger.error("bharatipayPayoutInqueryData---->"+jsonObject.toString());

            String encryptedRequest = BhartiPayUtils.encryptPayout(jsonObject.toString(), cryptyKey);

            jsonObject  = new JSONObject();
            jsonObject.put("aggregatorId",aggregatorId);
            jsonObject.put("request", encryptedRequest);

            transactionlogger.error("requestJson------> " + jsonObject.toString());
            String responeString    = BhartiPayUtils.doPostHttpUrlConnection(hostURL, jsonObject.toString());
            transactionlogger.error("responeString------> " + responeString);


            JSONObject payoutResponse   = new JSONObject(responeString);

            String response         = "";
            String responseMessage  = "";
            String code             = "";
            String txnAmount        = "";
            String txnStatus        = "";
            String bankRRN          = "";

            if(payoutResponse.has("response")){

                String responseStr   = payoutResponse.getString("response");
                transactionlogger.error("bharatipayPayoutInqueryResponseStr------> " + responseStr);

                String finalDencryptResponse   = BhartiPayUtils.dencryptPayout(responseStr, cryptyKey);
                transactionlogger.error("bharatipayPayoutInqueryDencryptResponse------> " + finalDencryptResponse);

                payoutResponse = new JSONObject(finalDencryptResponse);

                if(payoutResponse.has("status")){
                    txnStatus       = payoutResponse.getString("status");
                }
                if(payoutResponse.has("transId")){
                    merchantTransId = payoutResponse.getString("transId");
                }
                if(payoutResponse.has("amount")){
                    txnAmount = payoutResponse.getString("amount");
                }
                if(payoutResponse.has("response")){
                    response = payoutResponse.getString("response");
                }
                if(payoutResponse.has("message")){
                    responseMessage = payoutResponse.getString("message");
                }
                if(payoutResponse.has("code")){
                    code    = payoutResponse.getString("code");
                }
                if(payoutResponse.has("BankRRN")){
                    bankRRN = payoutResponse.getString("BankRRN");
                }
                if(payoutResponse.has("status")){
                    txnStatus = payoutResponse.getString("status");
                }

            }

            transactionlogger.error("response------> "+response+" -----txnStatus------> " + txnStatus+"---txnAmount-----> "+txnAmount+" --------" +
                    "responseMessage-----> "+responseMessage+" ----merchantTransId-----> "+merchantTransId);

            if (txnStatus.equalsIgnoreCase("SUCCESS"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark(txnStatus);
                commResponseVO.setDescription(responseMessage);
                commResponseVO.setErrorCode(code);


            }
            else if (txnStatus.equalsIgnoreCase("PENDING") || txnStatus.equalsIgnoreCase("PROCESSING"))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark(txnStatus);
                commResponseVO.setDescription(txnStatus);
                commResponseVO.setErrorCode(code);
                bhartiPayUtils.updatePendingPayoutTransaction(trackingId, txnStatus);
            }
            else  if (txnStatus.equalsIgnoreCase("FAILED"))
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(txnStatus);
                commResponseVO.setDescription(txnStatus);
                commResponseVO.setErrorCode(code);
            }
            String transactionid= BhartiPayUtils.getPaymentid(trackingId);
            commResponseVO.setTransactionId(transactionid);
           // commResponseVO.setResponseHashInfo(transactionid);

        }
        catch (Exception e)
        {
            transactionlogger.error(" JSONException-->" ,e );
        }

        return commResponseVO;

    }



}


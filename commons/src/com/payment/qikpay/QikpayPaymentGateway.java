package com.payment.qikpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.QikPayGatewayLogger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.PaymentManager;
import com.payment.bhartiPay.BhartiPayPaymentProcess;
import com.payment.bhartiPay.BhartiPayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.verve.AsyncVervePayPayoutQueryService;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class QikpayPaymentGateway  extends AbstractPaymentGateway
{
    private static QikPayGatewayLogger transactionlogger  = new QikPayGatewayLogger(QikpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "qikpay";
    public static final String CARD                     = "CARD";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.qikpay");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.QPBANKS");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public QikpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of QikpayPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        QikPayUtils qikPayUtils                         = new QikPayUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest          = gatewayAccount.isTest();
        String CURRENCY_CODE    = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        String AMOUNT           = QikPayUtils.getAmount(transactionDetailsVO.getAmount());
        String CUST_EMAIL       = "";
        String CUST_NAME        = "";

        if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            CUST_NAME   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
        else {
            CUST_NAME  = " ";
        }

        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            CUST_EMAIL   = commAddressDetailsVO.getEmail();
        else {
            CUST_EMAIL  = "customer@gmail.com";
        }


        String ORDER_ID     = trackingID;
        String PAY_ID       = gatewayAccount.getMerchantId();
        String PAYMENT_TYPE = qikPayUtils.getPaymentType(payment_Card);
        String CARD_NUMBER  = commCardDetailsVO.getCardNum();
        String CARD_EXP_DT  = commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String CVV          = commCardDetailsVO.getcVV();
        //String RETURN_URL   = RB.getString("BHARTIPAY_RU")+trackingID;
        String RETURN_URL   = RB.getString("BHARTIPAY_RU")+trackingID;
        String TXNTYPE      = "SALE";
        String SALT                 = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String support3DAccount     = gatewayAccount.get_3DSupportAccount();
        String PG_SALT              = gatewayAccount.getFRAUD_FTP_USERNAME();
       // String processorName        = commonValidatorVO.getProcessorName();
        String crypto_Key           = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String payment_url          = RB.getString("LIVE_SALE_URL");
        String HASH                 = "";
        String MOP_TYPE             = "";
        String CUST_PHONE           = "";
        String UPI ="";
        String customerId ="";
        transactionlogger.error("vpa requestVO--- " +commRequestVO.getCustomerId());

        UPI=commRequestVO.getCustomerId();

        transactionlogger.error("vpa UPI--- " + UPI);
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        transactionlogger.error("supportAccount is-------------->"+support3DAccount);
      //  transactionlogger.error("PG_SALT is-------------->"+PG_SALT);
        transactionlogger.error("processorName-- " + payment_brand);
        //transactionlogger.error("crypto_Key ----> " + crypto_Key);

        transactionlogger.error("isTest ----> " + isTest);
        transactionlogger.error("TXNTYPE is-------------->"+TXNTYPE);
     //   transactionlogger.error("CARD_NUMBER is-------------->"+CARD_NUMBER);
        //transactionlogger.error("CARD_EXP_DT is-------------->"+CARD_EXP_DT);
        transactionlogger.error("RETURN_URL is-------------->"+RETURN_URL);
       // transactionlogger.error("SALT is-------------->"+SALT);
        transactionlogger.error("PAYMENT_TYPE is-------------->"+PAYMENT_TYPE);
        transactionlogger.error("PAY_ID is-------------->"+PAY_ID);
        transactionlogger.error("CUST_NAME is-------------->"+CUST_NAME);
        transactionlogger.error("ORDER_ID is-------------->"+ORDER_ID);
        transactionlogger.error("AMOUNT is-------------->"+AMOUNT);
        transactionlogger.error("CURRENCY_CODE is-------------->"+CURRENCY_CODE);

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


            try
            {
                Map<String, String> requestS2SMap = new HashMap<String, String>();

                requestS2SMap.put("PAY_ID", PAY_ID);
                requestS2SMap.put("ORDER_ID", ORDER_ID);
                requestS2SMap.put("RETURN_URL", RETURN_URL);
                requestS2SMap.put("CURRENCY_CODE", CURRENCY_CODE);
                requestS2SMap.put("AMOUNT", AMOUNT);
                requestS2SMap.put("PAYMENT_TYPE", PAYMENT_TYPE);

                requestS2SMap.put("CUST_EMAIL", CUST_EMAIL);
                requestS2SMap.put("CUST_PHONE", CUST_PHONE);

                if("CC".equalsIgnoreCase(PAYMENT_TYPE) || "DC".equalsIgnoreCase(PAYMENT_TYPE))
                {
                    transactionlogger.error("inside CC & DC condition (map)-------------->"+PAYMENT_TYPE);
                    requestS2SMap.put("CUST_NAME", CUST_NAME);
                    requestS2SMap.put("CARD_NUMBER", CARD_NUMBER);
                    requestS2SMap.put("CARD_EXP_DT", CARD_EXP_DT);
                    requestS2SMap.put("CVV", CVV);

                   MOP_TYPE= qikPayUtils.getPaymentBrand(payment_brand);

                }
                else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
                {
                    MOP_TYPE=payment_brand;

                }
                else if("UP".equalsIgnoreCase(PAYMENT_TYPE))
                {
                    MOP_TYPE=PAYMENT_TYPE;
                    requestS2SMap.put("UPI", UPI);
                }
                else{
                //todo need to failed
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Incorrect Request");
                    commResponseVO.setRemark("Incorrect Request");
                    return commResponseVO ;
               }
                transactionlogger.error("MOP_TYPE ----> " + MOP_TYPE);
                requestS2SMap.put("MOP_TYPE", MOP_TYPE);
                HASH = qikPayUtils.generateCheckSum(requestS2SMap, SALT);
                transactionlogger.error("HASH--->" + HASH);
                requestS2SMap.put("HASH",HASH);
                // String iv2 = "B6B9B57E71DF7B52";


                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setUrlFor3DRedirect(payment_url);
                commResponseVO.setRequestMap(requestS2SMap);
                if("UPI".equalsIgnoreCase(payment_Card)){
                    transactionlogger.error(" insise UPI condition--->" + UPI);
                    qikPayUtils.updateTransaction(trackingID,UPI);

                }

                else if ("NBI".equalsIgnoreCase(payment_Card)&&functions.isValueNull(MOP_TYPE))
                {
                    HashMap<String, String> hashMap = new HashMap<>();
                    Enumeration<String> banks = RB_NB.getKeys();
                    String str11 = "";

                    while (banks.hasMoreElements())
                    {
                        String key = banks.nextElement();
                        str11 = RB_NB.getString(key);
                        hashMap.put(str11, key);
                    }
                    String bankName = hashMap.get(MOP_TYPE);
                    transactionlogger.debug("processor Bank Name --------->" + bankName);
                    customerId=bankName;

                    qikPayUtils.updateTransaction(trackingID,customerId);
                }


                transactionlogger.error("requestS2SMap---------------> "+requestS2SMap);
            }catch (Exception e){
                transactionlogger.error("BhartiPayS2S ---------------> ",e);
            }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Qikpay ---- ");
        String html                         = "";
        PaymentManager paymentManager       = new PaymentManager();
        Comm3DResponseVO transRespDetails   = null;
        QikPayUtils qikPayUtils                         = new QikPayUtils();
       QikPayPaymentProcess qikPayPaymentProcess=new QikPayPaymentProcess();
        String paymentMode              = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO     = qikPayUtils.getQikPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount   = GatewayAccountService.getGatewayAccount(accountId);

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
                html = qikPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect Qikpay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in QikpayPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of QikPayGateway---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        QikPayUtils qikPayUtils                                 = new QikPayUtils();
        String PAY_ID                                           = "";
        String ORDER_ID                                         = "";


        String inquiry_res="";
        try
        {
            PAY_ID       = gatewayAccount.getMerchantId();
            ORDER_ID     = trackingId;
            transactionlogger.error("isTest  --- >"+isTest);
            transactionlogger.error("PAY_ID is --- >"+PAY_ID);
            transactionlogger.error("ORDER_ID is --- >"+ORDER_ID);

            String reqestData = "PAY_ID="+PAY_ID+"&ORDER_ID="+ORDER_ID;
            transactionlogger.error("inquiry req is -->"+reqestData);
            if (isTest)
            {
                transactionlogger.error("inside test req of inquiry is -- >"+RB.getString("INQUIRY_TEST_URL"));
                inquiry_res = qikPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_TEST_URL")+reqestData);
            }
            else
            {
                transactionlogger.error("inside live req of inquiry is -- >"+RB.getString("INQUIRY_LIVE_URL"));
                inquiry_res = qikPayUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY_LIVE_URL")+reqestData);

            }
            transactionlogger.error("inquiry res is -- >"+inquiry_res);


            if (functions.isValueNull(inquiry_res)&& inquiry_res.contains("{"))
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
                String resNotFoundRemark ="";

                JSONObject jsonObject = new JSONObject(inquiry_res);

                if (jsonObject !=null)
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
                        if(functions.isValueNull(currency)){
                            currency = qikPayUtils.getCurrency(currency);
                        }
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
                    if (jsonObject.has("Description")){

                        Description     = jsonObject.getString("Description");
                    }
                    if (jsonObject.has("transactionId")){

                        transactionId   = jsonObject.getString("transactionId");
                    }
                    if (jsonObject.has("message")){

                        resNotFoundRemark = jsonObject.getString("message");
                    }

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

                    else if(responseCode.equals("006"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setAmount(approvedAmount);
                        qikPayUtils.updateMainTableEntry(status,trackingId);
                    }
                    //todo need to check failed status
                    else if(responseCode.equals("004")||responseCode.equals("002")||responseCode.equals("007")||"Failed".equalsIgnoreCase(status))
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
                    else if("Tranasction not Found".equalsIgnoreCase(resNotFoundRemark)&&"null".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setRemark(resNotFoundRemark);
                        commResponseVO.setTransactionStatus("Failed");
                       /* commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(payId);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);*/
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
            transactionlogger.error("JSONException--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside qikpay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        QikPayUtils qikPayUtils = new QikPayUtils();

        String hostURL          = "";
        String bankName         = "";
        String Client_id        = trackingId;
        String beneficiaryName  = "";
        String accountNumber    = "";
        String type             = "";
        String remittanceAmount = "0.00";
        String IFSCCode         = "";
        String MobileNumber     = "9999999999";
        String api_token        = gatewayAccount.getFRAUD_FTP_PATH();

        JSONObject jsonObject   = new JSONObject();
        try
        {
            if (isTest)
            {
                hostURL         = RB.getString("PAYOUT_LIVE_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
               String tempbeneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();
                if(tempbeneficiaryName.contains("-")||tempbeneficiaryName.contains("_")||tempbeneficiaryName.contains(",")){
                    beneficiaryName=tempbeneficiaryName.replaceAll("-","").replaceAll("_","").replaceAll(",","");
                }
                else {
                    beneficiaryName=tempbeneficiaryName;
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

            bankName = beneficiaryName;

            transactionlogger.error("beneficiaryName---> " + beneficiaryName);
            transactionlogger.error("accountNo---> " + accountNumber);
            transactionlogger.error("ifscCode---> " + IFSCCode);
            transactionlogger.error("bankName---> " + bankName);
            transactionlogger.error("amount---> " + remittanceAmount);
            transactionlogger.error("transferType---> " + type);
            transactionlogger.error("hostURL---> " + hostURL);
            transactionlogger.error("MobileNumber---> " + MobileNumber);
            transactionlogger.error("api_token---> " + api_token);


            jsonObject.put("api_token", api_token);
            jsonObject.put("client_id", Client_id);
            jsonObject.put("BeneficiaryName", beneficiaryName);
            jsonObject.put("AccountNumber", accountNumber);
            jsonObject.put("type", type);
            jsonObject.put("BankName", bankName);
            jsonObject.put("RemittanceAmount", remittanceAmount);
            jsonObject.put("IFSCCode", IFSCCode);
            jsonObject.put("MobileNumber", MobileNumber);

            transactionlogger.error("qikPayFinalRequest payout------> trackingid---->"+trackingId + jsonObject.toString());

            String responeString = qikPayUtils.doPostHttpUrlConnection(hostURL, jsonObject.toString());

            transactionlogger.error("qikPayFinalResponse  payout------> trackingid---->"+trackingId+ responeString);

            String description       = "";
            String code              = "";
            String message           = "";
            String RRN          = "";
            String responseOrderid   = "";
            String beneficiaryNam   = "";
            String accountNo     = "";
            String IFSCCodeRes       = "";
            String transferStatus    = "";
            String transactionId     = "";
            String txtStatus         = "";
            String finalResponse     = "";

                if(functions.isValueNull(responeString)&&responeString.contains("{"))
            {
                    JSONObject payoutResponse = new JSONObject(responeString);


                    if (payoutResponse.has("MSG") && functions.isValueNull(payoutResponse.getString("MSG")))
                    {
                        message = payoutResponse.getString("MSG");
                        transactionlogger.error("message-->" + message);
                    }

                    if (payoutResponse.has("STATUS") && functions.isValueNull(payoutResponse.getString("STATUS")))
                    {
                        txtStatus = payoutResponse.getString("STATUS");
                        transactionlogger.error("txtStatus-->" + txtStatus);
                    }
                    if (payoutResponse.has("RRN") && functions.isValueNull(payoutResponse.getString("RRN")))
                    {
                        RRN = payoutResponse.getString("RRN");
                        transactionlogger.error("RRN-->" + RRN);
                    }

                    transactionlogger.error("Qikpay --- message ---> " + message + "---txtStatus---> " + txtStatus + "---RRN---> " + RRN);


                    if (txtStatus.equalsIgnoreCase("SUCCESS"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(code);
                        commResponseVO.setIfsc(IFSCCodeRes);
                        commResponseVO.setFullname(beneficiaryNam);
                        commResponseVO.setBankaccount("");
                    }
                    else if (txtStatus.equalsIgnoreCase("PENDING") || txtStatus.equalsIgnoreCase("PROCESSING"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescription(message);

                        // BhartiPayUtils.updatePayoutTransaction(trackingId,paymentId);
                    }
                    else if (txtStatus.equalsIgnoreCase("FAILED"))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(transferStatus + "-" + message);
                        commResponseVO.setDescription(message);

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
            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncQikPayPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
            }
        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException----trackingid---->"+trackingId+"--->",e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processPayoutInquiry(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside qikpay process payout inquiry------->");
        CommRequestVO commRequestVO       = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO   = new Comm3DResponseVO();
        GatewayAccount gatewayAccount     = GatewayAccountService.getGatewayAccount(accountId);
        String api_token                  = "";
        boolean isTest                    = false;
        JSONObject jsonObject             = new JSONObject();
        QikPayUtils qikPayUtils           = new QikPayUtils();
        String client_id                  = "";
        String hostURL                    = "";
        Functions functions=new Functions();
        try
        {
            api_token       = gatewayAccount.getFRAUD_FTP_PATH();
            client_id       = trackingId;
            isTest          = gatewayAccount.isTest();
            transactionlogger.error("api_token---------->" + api_token);
            transactionlogger.error("client_id---------->" + client_id);
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

            transactionlogger.error("qikpayPayoutInqueryRequestStr------payout inquiry------> trackingid---->"+trackingId +jsonObject.toString());
            String responeString    = qikPayUtils.doPostHttpUrlConnection(hostURL, jsonObject.toString());

            transactionlogger.error("qikpayPayoutInqueryResponseStr------payout inquiry------> trackingid---->"+trackingId + responeString);
            if(functions.isValueNull(responeString)&&responeString.contains("{"))
            {
              JSONObject payoutResponse   = new JSONObject(responeString);

            String responseMessage  = "";
            String txnStatus        = "";
            String transactionId    = "";

            if(payoutResponse.has("STATUS")&&functions.isValueNull(payoutResponse.getString("STATUS"))){
                txnStatus       = payoutResponse.getString("STATUS");
            }
            if(payoutResponse.has("message")&&functions.isValueNull(payoutResponse.getString("message"))){
                responseMessage = payoutResponse.getString("message");
            }
            if(payoutResponse.has("opid")&&functions.isValueNull(payoutResponse.getString("opid"))){
                transactionId = payoutResponse.getString("opid");
            }

            transactionlogger.error("txnStatus------> "+txnStatus+" -----responseMessage------> " + responseMessage+"---transactionId-----> "+transactionId);

            if (txnStatus.equalsIgnoreCase("SUCCESS"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark(responseMessage);
                commResponseVO.setDescription(responseMessage);

            }
            else if (txnStatus.equalsIgnoreCase("PENDING") || txnStatus.equalsIgnoreCase("PROCESSING")||txnStatus.contains("pending")||txnStatus.contains("PENDING"))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark(responseMessage);
                commResponseVO.setDescription(responseMessage);

            }
            else if (txnStatus.equalsIgnoreCase("FAILED")||txnStatus.contains("FAIL")||txnStatus.contains("fail")||txnStatus.equalsIgnoreCase("REFUNDED"))
            {
                if(txnStatus.equalsIgnoreCase("REFUNDED")){
                    responseMessage="Failed";
                }
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(responseMessage);
                commResponseVO.setDescription(responseMessage);
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
            transactionlogger.error(" JSONException-->" ,e );
        }

        return commResponseVO;

    }
}
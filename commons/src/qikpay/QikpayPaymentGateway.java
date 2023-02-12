package qikpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 3/8/2021.
 */
public class QikpayPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(QikpayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "qikpay";
    public static final String CARD                     = "CARD";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.qikPay");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.QPBANKS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

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
        String CUST_EMAIL       = commAddressDetailsVO.getEmail();
        String CUST_NAME        = "";

        if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            CUST_NAME   = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
        else {
            CUST_NAME  = " ";
        }


        String ORDER_ID     = trackingID;
        String PAY_ID       = gatewayAccount.getMerchantId();
        String PAYMENT_TYPE = qikPayUtils.getPaymentType(payment_Card);
        String CARD_NUMBER  = commCardDetailsVO.getCardNum();
        String CARD_EXP_DT  = commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String CVV          = commCardDetailsVO.getcVV();
        String RETURN_URL   = RB.getString("BHARTIPAY_RU")+trackingID;
        String TXNTYPE      = "SALE";
        String SALT                 = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String support3DAccount     = gatewayAccount.get_3DSupportAccount();
        String PG_SALT              = gatewayAccount.getFRAUD_FTP_USERNAME();
        String processorName        = commonValidatorVO.getProcessorName();
        String crypto_Key           = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String payment_url          = RB.getString("LIVE_SALE_URL");
        String HASH                 = "";
        String MOP_TYPE             = "";
        String CUST_PHONE           = "";
        String card_Type = commonValidatorVO.getPaymentBrand();
        String UPI ="";
        transactionlogger.error("vpa --- " + commonValidatorVO.getVpa_address());
        transactionlogger.error("vpa requestVO--- " +commRequestVO.getCustomerId());

            UPI=commRequestVO.getCustomerId();



        transactionlogger.error("vpa UPI--- " + UPI);
        transactionlogger.error("card_type --- " + card_Type);
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        transactionlogger.error("supportAccount is-------------->"+support3DAccount);
        transactionlogger.error("PG_SALT is-------------->"+PG_SALT);
        transactionlogger.error("processorName-- " + payment_brand);
        transactionlogger.error("crypto_Key ----> " + crypto_Key);

        transactionlogger.error("isTest ----> " + isTest);
        transactionlogger.error("TXNTYPE is-------------->"+TXNTYPE);
        transactionlogger.error("CARD_NUMBER is-------------->"+CARD_NUMBER);
        transactionlogger.error("CARD_EXP_DT is-------------->"+CARD_EXP_DT);
        transactionlogger.error("RETURN_URL is-------------->"+RETURN_URL);
        transactionlogger.error("CVV is-------------->"+CVV);
        transactionlogger.error("SALT is-------------->"+SALT);
        transactionlogger.error("PAYMENT_TYPE is-------------->"+PAYMENT_TYPE);
        transactionlogger.error("PAY_ID is-------------->"+PAY_ID);
        transactionlogger.error("CUST_NAME is-------------->"+CUST_NAME);
        transactionlogger.error("ORDER_ID is-------------->"+ORDER_ID);
        transactionlogger.error("AMOUNT is-------------->"+AMOUNT);
        transactionlogger.error("CURRENCY_CODE is-------------->"+CURRENCY_CODE);

        if(functions.isValueNull(commAddressDetailsVO.getPhone())) {
            CUST_PHONE  = commAddressDetailsVO.getPhone();
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
              //  requestS2SMap.put("CUST_NAME", CUST_NAME);
                requestS2SMap.put("CUST_EMAIL", CUST_EMAIL);
                requestS2SMap.put("CUST_PHONE", CUST_PHONE);

                if("CC".equalsIgnoreCase(PAYMENT_TYPE) || "DC".equalsIgnoreCase(PAYMENT_TYPE))
                {
                    transactionlogger.error("inside CC & DC condition (map)-------------->"+PAYMENT_TYPE);
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
                transactionlogger.error("MOP_TYPE ----> " + MOP_TYPE);
                requestS2SMap.put("MOP_TYPE", MOP_TYPE);
                HASH = qikPayUtils.generateCheckSum(requestS2SMap, SALT);
                transactionlogger.error("HASH--->" + HASH);
                requestS2SMap.put("HASH",HASH);
                // String iv2 = "B6B9B57E71DF7B52";


                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setUrlFor3DRedirect(payment_url);
                commResponseVO.setRequestMap(requestS2SMap);

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

                    if (responseCode.equals("000"))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("Success");
                    }

                    else if(responseCode.equals("006"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("pending");
                        qikPayUtils.updateMainTableEntry(status,trackingId);
                    }
                    else
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setRemark(status);
                        commResponseVO.setTransactionStatus("Failed");
                    }

                    commResponseVO.setAmount(approvedAmount);
                    commResponseVO.setMerchantId(payId);
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setAuthCode(responseCode);
                    commResponseVO.setBankTransactionDate(dateTime);
                    commResponseVO.setTransactionType(txnType);

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
            transactionlogger.error("JSONException--->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside qikpay process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
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
                hostURL         = RB.getString("PAYOUT_TEST_URL");
            }
            else
            {
                hostURL = RB.getString("PAYOUT_LIVE_URL");
            }

            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                beneficiaryName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accountNumber = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                IFSCCode = commTransactionDetailsVO.getBankIfsc();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                bankName = commTransactionDetailsVO.getCustomerBankAccountName();
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
            jsonObject.put("Client_id", Client_id);
            jsonObject.put("BeneficiaryName", beneficiaryName);
            jsonObject.put("AccountNumber", accountNumber);
            jsonObject.put("type", type);
            jsonObject.put("BankName", bankName);
            jsonObject.put("RemittanceAmount", remittanceAmount);
            jsonObject.put("IFSCCode", IFSCCode);
            jsonObject.put("MobileNumber", MobileNumber);

            transactionlogger.error("qikPayFinalRequest------> " + jsonObject.toString());

            String responeString = qikPayUtils.doPostHttpUrlConnection(hostURL, jsonObject.toString());
            transactionlogger.error("qikPayFinalResponse------> " + responeString);

            String description       = "";
            String code              = "";
            String message           = "";
            String authcode          = "";
            String responseOrderid   = "";
            String beneficiaryNam   = "";
            String accountNo     = "";
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
                commResponseVO.setFullname(beneficiaryNam);
                commResponseVO.setBankaccount(accountNo);
            }
            else if (transferStatus.equalsIgnoreCase("PENDING") || transferStatus.equalsIgnoreCase("PROCESSING"))
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setTransactionStatus("pending");
                commResponseVO.setRemark(transferStatus);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(IFSCCodeRes);
                commResponseVO.setFullname(beneficiaryNam);
                commResponseVO.setBankaccount(accountNo);
               // BhartiPayUtils.updatePayoutTransaction(trackingId,paymentId);
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark(transferStatus);
                commResponseVO.setDescription(message);
                commResponseVO.setErrorCode(code);
                commResponseVO.setIfsc(IFSCCodeRes);
                commResponseVO.setFullname(beneficiaryNam);
                commResponseVO.setBankaccount(accountNo);
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

}
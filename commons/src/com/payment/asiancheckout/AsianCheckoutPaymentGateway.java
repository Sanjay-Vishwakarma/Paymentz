package com.payment.asiancheckout;

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
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/3/2021.
 */
public class AsianCheckoutPaymentGateway extends AbstractPaymentGateway
{


    private static TransactionLogger transactionlogger      = new TransactionLogger(AsianCheckoutPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE               = "asianchck";
    private final static ResourceBundle RB                  = LoadProperties.getProperty("com.directi.pg.asiancheckout");
    private final static ResourceBundle RB_NB               = LoadProperties.getProperty("com.directi.pg.ACBANKS");
    CommonValidatorVO commonValidatorVO                     = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public AsianCheckoutPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of AsianCheckoutPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        AsianCheckoutUtils asianCheckoutUtils           = new AsianCheckoutUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        boolean isTest                                  = gatewayAccount.isTest();

        String CURRENCY_CODE        = "356";
        String AMOUNT               = asianCheckoutUtils.getAmount(transactionDetailsVO.getAmount());
        String CUST_EMAIL           = commAddressDetailsVO.getEmail();
        String CUST_NAME            ="";

        if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            CUST_NAME   = commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        else {
            CUST_NAME   =" ";
        }

        String ORDER_ID     = trackingID;
        String PAY_ID       = gatewayAccount.getMerchantId();
        String SALT         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String CryptoKey    = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String PAYMENT_TYPE = asianCheckoutUtils.getPaymentTypeS2S(payment_Card);
        String CARD_NUMBER  = commCardDetailsVO.getCardNum();
        String CARD_EXP_DT  = commCardDetailsVO.getExpMonth()+""+commCardDetailsVO.getExpYear();
        String CVV          = commCardDetailsVO.getcVV();
        String RETURN_URL   = RB.getString("BHARTIPAY_RU")+trackingID;
        String payment_url  = RB.getString("LIVE_SALE_URL");
        String HASH         = "";
        String MOP_TYPE     = "";
        String ENCDATA      = "";
        Map<String, String> map   = new HashMap<String, String>();
        Map<String, String> finalRequestmap = new HashMap<String, String>();
        String VPA = commRequestVO.getCustomerId();

            try
            {
                map.put("AMOUNT",AMOUNT);
                map.put("CURRENCY_CODE", CURRENCY_CODE);
                map.put("CUST_EMAIL", CUST_EMAIL);
                map.put("CUST_NAME",CUST_NAME );
                map.put("ORDER_ID", ORDER_ID);
                map.put("PAY_ID", PAY_ID);
                map.put("PAYMENT_TYPE", PAYMENT_TYPE);
                map.put("RETURN_URL", RETURN_URL);
            if("CARD".equalsIgnoreCase(PAYMENT_TYPE))
            {
                map.put("CARD_HOLDER_NAME", CUST_NAME);
                map.put("CARD_NUMBER", CARD_NUMBER);
                map.put("CARD_EXP_DT", CARD_EXP_DT);
                map.put("CVV", CVV);
            }
            else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
            {
                MOP_TYPE = payment_brand;
                map.put("MOP_TYPE", MOP_TYPE);
            }
            else if("UP".equalsIgnoreCase(PAYMENT_TYPE)){
                MOP_TYPE    = VPA;
                map.put("VPA", MOP_TYPE);
            }
            else{

                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                return commResponseVO ;
            }
            //step 1
            HASH  = asianCheckoutUtils.generateCheckSum(map, SALT);
            //step 2
            String requestforEncrypt = asianCheckoutUtils.generateCheckSum2(map, HASH);
            //step 3
            ENCDATA = asianCheckoutUtils.encrypt1(requestforEncrypt,CryptoKey,PAY_ID) ;
            transactionlogger.error("ENCDATA  asianCheckout----trackingid--"+trackingID+"--->"+ENCDATA );
            finalRequestmap.put("PAY_ID",PAY_ID);
            finalRequestmap.put("ENCDATA",ENCDATA);

        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException--->" + e);
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception--->" + e);
        }

        transactionlogger.error("sale payment_url is-------------->"+payment_url);

        commResponseVO.setStatus("pending3DConfirmation");
        commResponseVO.setUrlFor3DRedirect(payment_url);
        commResponseVO.setRequestMap(finalRequestmap);
        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in AsianCheckoutPaymentGateway ---- ");
        String html                                                 = "";
        PaymentManager paymentManager                               = new PaymentManager();
        Comm3DResponseVO transRespDetails                           = null;
        AsianCheckoutUtils asianCheckoutUtils                       = new AsianCheckoutUtils();
        AsianCheckoutPaymentProcess asianCheckoutPaymentProcess     = new AsianCheckoutPaymentProcess();
        CommRequestVO commRequestVO                                 = asianCheckoutUtils.getAsianCheckoutRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount                               = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                              = gatewayAccount.isTest();
        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = asianCheckoutPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect asianCheckout form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in asianCheckoutpaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of AsianCheckoutPaymentGateway---");

        CommResponseVO commResponseVO                       = new CommResponseVO();
        CommRequestVO reqVO                                 = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        Functions functions                                 = new Functions();
        AsianCheckoutUtils asianCheckoutUtils               = new AsianCheckoutUtils();
        Map<String, String> requestHM                       = new HashMap<String, String>();
        JSONObject jsonRequestObject                        = new JSONObject();

        String ORDER_ID         = trackingId;
        String PAY_ID           = gatewayAccount.getMerchantId();
        String SALT             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String CURRENCY_CODE    =  "356";
        String TXNTYPE          = "STATUS";
        String AMOUNT           = asianCheckoutUtils.getAmount(commTransactionDetailsVO.getAmount());
        String HASH             = "";
        String req              = "";
        String inquiryUrl       = "";
        String inquiryResponse  = "";

        transactionlogger.error("PAY_ID is -- >"+PAY_ID);
        transactionlogger.error("ORDER_ID is --- >"+ORDER_ID);
        transactionlogger.error("TXNTYPE is --- >"+TXNTYPE);
        transactionlogger.error("AMOUNT is --- >"+AMOUNT);
        transactionlogger.error("CURRENCY_CODE is --- >"+CURRENCY_CODE);

        try
        {
            requestHM.put("ORDER_ID",ORDER_ID);
            requestHM.put("PAY_ID",PAY_ID);
            requestHM.put("AMOUNT",AMOUNT);
            requestHM.put("TXNTYPE",TXNTYPE);
            requestHM.put("CURRENCY_CODE", CURRENCY_CODE);

            transactionlogger.error("requestHM --- >"+requestHM.toString());

            HASH  = asianCheckoutUtils.generateCheckSum(requestHM, SALT);

            jsonRequestObject.put("ORDER_ID",ORDER_ID);
            jsonRequestObject.put("PAY_ID",PAY_ID);
            jsonRequestObject.put("AMOUNT",AMOUNT);
            jsonRequestObject.put("TXNTYPE",TXNTYPE);
            jsonRequestObject.put("CURRENCY_CODE",CURRENCY_CODE);
            jsonRequestObject.put("HASH",HASH);

            if (isTest)
            {
                inquiryUrl = RB.getString("INQUIRY_TEST_URL");
            }
            else
            {
                inquiryUrl = RB.getString("INQUIRY_LIVE_URL");
            }

            transactionlogger.error("inquiry requestUrl -- >"+RB.getString("INQUIRY_TEST_URL"));
            transactionlogger.error("inquiry requested Data is --> "+trackingId+" "+jsonRequestObject.toString());

            inquiryResponse = asianCheckoutUtils.doPostHttpUrlConnection(inquiryUrl,jsonRequestObject.toString());

            transactionlogger.error("inquiry responseData is --> "+trackingId+" "+inquiryResponse);

            if (functions.isValueNull(inquiryResponse)&& inquiryResponse.contains("{"))
            {
                String status               = "";
                String responseCode         = "";
                String orderId              = "";
                String RESPONSE_DATE_TIME   = "";
                String RESPONSE_CODE        = "";
                String TXN_ID               = "";
                String MOP_TYPE             = "";
                String CARD_MASK            = "";
                String ACQ_ID               = "";
                String TXN_TYPE             = "";
                String TXN_CURRENCY_CODE    = "";
                String RRN                  = "";
                String SURCHARGE_FLAG       = "";
                String PAYMENT_TYPE         = "";
                String PG_TXN_MESSAGE       = "";
                String STATUS               = "";
                String PG_REF_NUM           = "";
                String TXN_AMOUNT           = "";
                String RESPONSE_MESSAGE     = "";
                String ORIG_TXN_ID          = "";
                String TXN_TOTAL_AMOUNT     = "";
                String CUST_NAME            = "";
                String IS_STATUS_FINAL      = "";
                String TXN_PAY_ID           = "";

                JSONObject jsonObject = new JSONObject(inquiryResponse);

                if (jsonObject != null)
                {
                    if (jsonObject.has("PAYMENT_TYPE"))
                    {
                        PAYMENT_TYPE = jsonObject.getString("PAYMENT_TYPE");
                    }
                    if (jsonObject.has("RESPONSE_CODE"))
                    {
                        RESPONSE_CODE = jsonObject.getString("RESPONSE_CODE");
                    }
                    if (jsonObject.has("MOP_TYPE"))
                    {
                        MOP_TYPE = jsonObject.getString("MOP_TYPE");
                    }
                    if (jsonObject.has("ACQ_ID"))
                    {
                        ACQ_ID = jsonObject.getString("ACQ_ID");
                    }
                    if (jsonObject.has("ORIG_TXN_ID"))
                    {
                        ORIG_TXN_ID = jsonObject.getString("ORIG_TXN_ID");
                    }
                    if (jsonObject.has("PG_REF_NUM"))
                    {
                        PG_REF_NUM = jsonObject.getString("PG_REF_NUM");
                    }
                    if (jsonObject.has("PG_TXN_MESSAGE"))
                    {
                        PG_TXN_MESSAGE = jsonObject.getString("PG_TXN_MESSAGE");
                    }
                    if (jsonObject.has("SURCHARGE_FLAG"))
                    {
                        SURCHARGE_FLAG = jsonObject.getString("SURCHARGE_FLAG");
                    }
                    if (jsonObject.has("TXN_ID"))
                    {
                        TXN_ID = jsonObject.getString("TXN_ID");
                    }
                    if (jsonObject.has("TXNTYPE"))
                    {
                        TXN_TYPE = jsonObject.getString("TXNTYPE");
                    }
                    if (jsonObject.has("AMOUNT"))
                    {
                        TXN_AMOUNT = jsonObject.getString("AMOUNT");
                    }
                    if (jsonObject.has("RRN"))
                    {
                        RRN = jsonObject.getString("RRN");
                    }
                    if (jsonObject.has("TOTAL_AMOUNT"))
                    {
                        TXN_TOTAL_AMOUNT = jsonObject.getString("TOTAL_AMOUNT");
                    }
                    if(jsonObject.has("STATUS"))
                    {
                        STATUS = jsonObject.getString("STATUS");
                    }
                    if(jsonObject.has("CURRENCY_CODE"))
                    {
                        TXN_CURRENCY_CODE = jsonObject.getString("CURRENCY_CODE");
                    }
                    if (jsonObject.has("RESPONSE_DATE_TIME"))
                    {
                        RESPONSE_DATE_TIME = jsonObject.getString("RESPONSE_DATE_TIME");
                    }
                    if (jsonObject.has("PAY_ID"))
                    {
                        TXN_PAY_ID = jsonObject.getString("PAY_ID");
                    }
                    if (jsonObject.has("orderId"))
                    {
                        orderId = jsonObject.getString("orderId");
                    }
                    if (jsonObject.has("IS_STATUS_FINAL")){
                        IS_STATUS_FINAL = jsonObject.getString("IS_STATUS_FINAL");
                    }
                    if (jsonObject.has("RESPONSE_MESSAGE")){
                        RESPONSE_MESSAGE = jsonObject.getString("RESPONSE_MESSAGE");
                    }

                    TXN_AMOUNT =  asianCheckoutUtils.getDecimalAmount(TXN_AMOUNT);

                    if (RESPONSE_CODE.equals("000")&&(STATUS.equalsIgnoreCase("Captured")||STATUS.equalsIgnoreCase("Settled")||STATUS.equalsIgnoreCase("Approved")))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(RESPONSE_MESSAGE);
                        commResponseVO.setAmount(TXN_AMOUNT);
                        commResponseVO.setMerchantId(TXN_PAY_ID);
                        commResponseVO.setTransactionId(TXN_ID);
                        commResponseVO.setCurrency(TXN_CURRENCY_CODE);
                        commResponseVO.setAuthCode(RESPONSE_CODE);
                        commResponseVO.setBankTransactionDate(RESPONSE_DATE_TIME);
                        commResponseVO.setTransactionType(TXN_TYPE);
                        commResponseVO.setRrn(RRN);

                    }
                    else if(RESPONSE_CODE.equals("026") && STATUS.equalsIgnoreCase("Sent to Bank"))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setAmount(TXN_AMOUNT);
                        commResponseVO.setRemark(STATUS);
                        commResponseVO.setAuthCode(RESPONSE_CODE);
                        commResponseVO.setTransactionId(TXN_ID);
                        asianCheckoutUtils.updateMainTableEntry(STATUS, trackingId);
                    }
                    else if(RESPONSE_CODE.equals("004")|| RESPONSE_CODE.equals("007"))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(STATUS);
                        commResponseVO.setAmount(TXN_AMOUNT);
                        commResponseVO.setMerchantId(TXN_PAY_ID);
                        commResponseVO.setTransactionId(TXN_ID);
                        commResponseVO.setCurrency(TXN_CURRENCY_CODE);
                        commResponseVO.setAuthCode(RESPONSE_CODE);
                        commResponseVO.setBankTransactionDate(RESPONSE_DATE_TIME);
                        commResponseVO.setTransactionType(TXN_TYPE);
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
                }else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                }
        }catch (JSONException e)
        {
            transactionlogger.error("AsianCheckout processQuery JSONException is --- >", e);
        }
        catch (Exception e)
        {
            transactionlogger.error("AsianCheckout processQuery Exception is --- >",e);
        }
        return commResponseVO;
    }
}
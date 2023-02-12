package com.payment.easypaymentz;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.fraud.fourstop.MultipartUtility;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Admin on 6/23/2021.
 */
public class EasyPaymentzPaymentGateway  extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger  = new TransactionLogger(EasyPaymentzPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE             = "epaymentz";
    public static final String CARD                     = "CARD";
    private final static ResourceBundle RB              = LoadProperties.getProperty("com.directi.pg.easypaymentz");
    private final static ResourceBundle RB_NB           = LoadProperties.getProperty("com.directi.pg.EPBANKS");
    CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public EasyPaymentzPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of EasyPaymentzPaymentGateway......");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                 = new Comm3DResponseVO();
        Functions functions                             = new Functions();

        EasyPaymentzUtils easyPaymentzUtils             = new EasyPaymentzUtils();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand                            = transactionDetailsVO.getCardType();
        String payment_Card                             = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest          = gatewayAccount.isTest();
        String CURRENCY_CODE    = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        String AMOUNT           = EasyPaymentzUtils.getAmount(transactionDetailsVO.getAmount());
        String CUST_EMAIL       = "";
        String CUST_NAME        = "";
        try
        {
            if(functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            {
                CUST_NAME = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            }else {
                CUST_NAME  = "customer";
            }
            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            {
                CUST_EMAIL = commAddressDetailsVO.getEmail();
            }else {
                CUST_EMAIL  = "customer@gmail.com";
            }

            String ORDER_ID             = trackingID;
            String merchantId           = gatewayAccount.getMerchantId();
            String PAYMENT_TYPE         = easyPaymentzUtils.getPaymentType(payment_Card);
            String CARD_NUMBER          = commCardDetailsVO.getCardNum();
            String card_expiryYear      = commCardDetailsVO.getExpYear();
            String card_expiryMonth     = commCardDetailsVO.getExpMonth();
            String CVV                  = commCardDetailsVO.getcVV();
            String RETURN_URL           = RB.getString("EASYPAY_RU")+URLEncoder.encode("trackingId=","UTF-8")+trackingID;
            String TXNTYPE              = "SALE";
            String secretKey            = gatewayAccount.getFRAUD_FTP_PATH();
            String SALT                 = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String appId                = gatewayAccount.getFRAUD_FTP_USERNAME();
            String domainURL = gatewayAccount.getFRAUD_FTP_SITE();
            String payment_url          = "";
            String MOP_TYPE             = "";
            String CUST_PHONE           = "";
            String UPI                  = commRequestVO.getCustomerId();
            String customerId           = "";

            if(functions.isValueNull(commAddressDetailsVO.getPhone())) {

                if(commAddressDetailsVO.getPhone().contains("-"))
                {
                    CUST_PHONE = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                else{
                    CUST_PHONE = commAddressDetailsVO.getPhone();

                }
                if(CUST_PHONE.length()>10){

                    CUST_PHONE=CUST_PHONE.substring(CUST_PHONE.length() - 10);
                }
                else{
                    CUST_PHONE = commAddressDetailsVO.getPhone();

                }

            }

            if(isTest){
                payment_url= RB.getString("TEST_SALE_URL");
            }
            else
            {
                if(functions.isValueNull(domainURL)){
                    payment_url="https://"+domainURL+RB.getString("SALE_LIVE_END_URL");
                }
                else {
                    payment_url=RB.getString("LIVE_SALE_URL");
                }
                 //   payment_url=RB.getString("LIVE_SALE_URL");


            }

            Map<String, String> parameters = new HashMap<String, String>();

            parameters.put("orderAmount", AMOUNT);
            parameters.put("orderCurrency", "INR");
            parameters.put("orderNote", trackingID);
            parameters.put("customerName", CUST_NAME);
            parameters.put("customerEmail", CUST_EMAIL);
            parameters.put("customerPhone", CUST_PHONE);
            parameters.put("appId", appId);
            parameters.put("orderId", trackingID);
            parameters.put("returnUrl", RETURN_URL);

            if("CC".equalsIgnoreCase(PAYMENT_TYPE) || "DC".equalsIgnoreCase(PAYMENT_TYPE))
            {
                parameters.put("paymentOption", "card");
                parameters.put("card_number", CARD_NUMBER);
                parameters.put("card_expiryYear", card_expiryYear);
                parameters.put("card_expiryMonth", card_expiryMonth);
                parameters.put("card_cvv", CVV);
                parameters.put("card_holder", CUST_NAME);
            }
            else if("NB".equalsIgnoreCase(PAYMENT_TYPE))
            {
                parameters.put("paymentOption", "nb");
                parameters.put("paymentCode", payment_brand);
            }
            else if("UP".equalsIgnoreCase(PAYMENT_TYPE))
            {
                parameters.put("paymentOption", "upi");
                parameters.put("upi_vpa", UPI);

            }
            else if("WL".equalsIgnoreCase(PAYMENT_TYPE))
            {
                parameters.put("paymentOption", "wallet");
                parameters.put("paymentCode", payment_brand);
            }
            else
            {   commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                return commResponseVO ;
            }

            String data = EasyPaymentzUtils.encryptSignature(secretKey, parameters);

            transactionlogger.error("data-->"+data);
            transactionlogger.error("parameters-->"+parameters.toString());
            transactionlogger.error("processorName-- " + payment_brand);
            transactionlogger.error("AMOUNT is----->"+AMOUNT);
            transactionlogger.error("ORDER_ID is---->"+ORDER_ID+" vpa UPI->" + UPI+" CUST_NAME is-->"+CUST_NAME);

            String encdata = URLEncoder.encode(EasyPaymentzUtils.encryptInputData(data, SALT, secretKey.substring(0, 16)), "UTF-8");

            transactionlogger.error("encdata -->"+encdata);

            parameters = new HashMap<String, String>();
            parameters.put("merchantId",merchantId);
            parameters.put("encryptedData",encdata);

            transactionlogger.error("final request -->" + " trackingID " + parameters.toString());

            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(payment_url);
            commResponseVO.setRequestMap(parameters);


        }catch (Exception e){
            transactionlogger.error("EasyPaymentzPaymentGateway -------> ",e);
        }

        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredirect in EasyPaymentzPaymentGateway ---- ");
        String html                         = "";
        PaymentManager paymentManager       = new PaymentManager();
        Comm3DResponseVO transRespDetails   = null;
        EasyPaymentzUtils easyPaymentzUtils = new EasyPaymentzUtils();
        String paymentMode                  = commonValidatorVO.getPaymentMode();
        EasyPaymentzPaymentProcess easyPaymentzPaymentProcess = new EasyPaymentzPaymentProcess();

        CommRequestVO commRequestVO     = easyPaymentzUtils.getEasyPayRequestVO(commonValidatorVO);
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
                html = easyPaymentzPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect easyPaymentz form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in easyPaymentzPaymentGateway---", e);
        }
        return html;
    }

/*    public GenericResponseVO processQuery(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of easyPaymentz---");
        CommResponseVO commResponseVO                           = new CommResponseVO();
        CommRequestVO reqVO                                     = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO       = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                           = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                          = gatewayAccount.isTest();
        Functions functions                                     = new Functions();
        EasyPaymentzUtils easyPaymentzUtils                     = new EasyPaymentzUtils();
        String ORDER_ID                     = "";
        String inquiryUrl                   = "";
        String inquiry_res                  = "";
        String token                        = "";
        Map<String, String> parameters      = new LinkedHashMap<String, String>();
        Map<String, Object> claims          = new HashMap<>();
        Map<String, Object> header          = new HashMap<>();

        String MERCHANT_APP_ID      = gatewayAccount.getFRAUD_FTP_USERNAME();
        String MERCHANT_SECRET_KEY  = gatewayAccount.getFRAUD_FTP_PATH();
        String MERCHANT_ID          = gatewayAccount.getMerchantId();
        String currency             = gatewayAccount.getCurrency();
        try
        {
            transactionlogger.error("isTest -- >"+isTest);

            ORDER_ID     = trackingId;
            parameters.put("orderId", ORDER_ID);

            String post_data    = parameters.toString();
            String checksum     = EasyPaymentzUtils.generateCheckSum(parameters,MERCHANT_SECRET_KEY);

            long nowMillis      = System.currentTimeMillis();
            Date now            = new Date(nowMillis);

            claims.put("appId",MERCHANT_APP_ID);
            claims.put("checkSum",checksum);
            claims.put("iat", nowMillis);

            header.put("typ", "JWT");
            header.put("alg", "HS256");

            token =  EasyPaymentzUtils.createJWTToken(claims,header ,MERCHANT_SECRET_KEY);

            if (isTest)
            {
                inquiryUrl = RB.getString("INQUIRY_TEST_URL")+ORDER_ID;
            }
            else
            {
                inquiryUrl = RB.getString("INQUIRY_LIVE_URL")+ORDER_ID;
            }

            transactionlogger.error("Inquiry token -- >"+trackingId+"----->"+token);
            transactionlogger.error("post_data -- >"+post_data);
            transactionlogger.error("checksum -- >"+checksum);
            transactionlogger.error("requetURL -- >"+inquiryUrl);
            transactionlogger.error("claims -- >"+claims.toString());
            transactionlogger.error("header -- >"+header.toString());

            inquiry_res =  EasyPaymentzUtils.doPostHTTPSURLConnectionClient(post_data, inquiryUrl, token);

            transactionlogger.error("inquiryRes "+trackingId+"-----> "+inquiry_res);

            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String status           ="";
                String responseCode     ="";
                String transactionId    ="";
                String txnType          ="";
                String approvedAmount   ="";
                String dateTime         ="";
                String Description      ="";
                String orderId          ="";
                String merchantId       ="";

                JSONArray responseArray     = new JSONArray(inquiry_res);
                JSONObject jsonObject       = null;

                for (int i = 0; i < responseArray.length(); i++)
                {
                    jsonObject = responseArray.getJSONObject(i);
                }

                if (jsonObject !=null)
                {
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                    }
                    if (jsonObject.has("merchantId"))
                    {
                        merchantId = jsonObject.getString("merchantId");
                    }
                    if (jsonObject.has("merchantOrderId"))
                    {
                        orderId = jsonObject.getString("merchantOrderId");
                    }
                    if (jsonObject.has("paymentMode"))
                    {
                        txnType = jsonObject.getString("paymentMode");
                    }
                    if (jsonObject.has("amount"))
                    {
                        approvedAmount = jsonObject.getString("amount");
                    }
                    if (jsonObject.has("txtPGTime"))
                    {
                        dateTime = jsonObject.getString("txtPGTime");
                    }
                    if (jsonObject.has("orderID"))
                    {
                        transactionId = jsonObject.getString("orderID");
                    }
                    if (jsonObject.has("txtMsg"))
                    {
                        Description     = jsonObject.getString("txtMsg");
                    }

                    if ("success".equalsIgnoreCase(status)||"Captured".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status + "-" + Description);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);
                        commResponseVO.setMerchantId(MERCHANT_ID);
                    }
                    else if("pending".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(status + "-" + Description);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setMerchantId(MERCHANT_ID);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);

                        easyPaymentzUtils.updateMainTableEntry(status, trackingId);
                    }
                    else if("Failed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setRemark(status+"-"+Description);
                        commResponseVO.setAmount(approvedAmount);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setAuthCode(responseCode);
                        commResponseVO.setBankTransactionDate(dateTime);
                        commResponseVO.setTransactionType(txnType);
                        commResponseVO.setMerchantId(MERCHANT_ID);
                    }
                    else
                    {
                        // no response set pending
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                        commResponseVO.setTransactionStatus("pending");
                    }
                }
                else
                {
                    // no response set pending
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else
            {
                // no response set pending
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
    }*/
public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
{
    transactionlogger.error("Inside processInquiry of easypayemtz Gateway---");
    CommResponseVO commResponseVO                       = new CommResponseVO();
    CommRequestVO reqVO                                 = (CommRequestVO) requestVO;
    CommTransactionDetailsVO commTransactionDetailsVO   = reqVO.getTransDetailsVO();
    GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
    EasyPaymentzUtils easyPaymentzUtils                 = new EasyPaymentzUtils();
    boolean isTest          = gatewayAccount.isTest();
    Functions functions     = new Functions();
    String PAY_ID           = "";
    String ORDER_ID         = trackingId;
    String appId            = gatewayAccount.getFRAUD_FTP_USERNAME().toLowerCase();
    String domainURL        = gatewayAccount.getFRAUD_FTP_SITE();
    String secretKey        = EasyPaymentzUtils.generateCheckSum2(gatewayAccount.getFRAUD_FTP_PATH()).toLowerCase();
    String orderStatus = "";
    String requestAmount = "";
    String status = "";
    String txStatus = "";
    String utr = "";
    String txMsg = "";
    String referenceId = "";
    String paymentMode = "";
    String orderCurrency = "";
    String orderId = "";
    String paymentDetailsPaymentMode = "";
    String cardNumber = "";
    String cardCountry = "";
    String cardScheme = "";
    String authIdCode = "";
    String response = "";
    String inquiry_res = "";
    try
    {
        transactionlogger.error("isTest  --- >" + isTest);
        transactionlogger.error("PAY_ID is --- >" + PAY_ID);
        transactionlogger.error("appId is --- >" + appId);
        transactionlogger.error("secretKey is --- >" + secretKey);
        String inquiryReqestData = "";

        String url = "";

        if (isTest)
        {
            url=RB.getString("INQUIRY_TEST_URL");
            transactionlogger.error("inside test req of inquiry is -- >" + RB.getString("INQUIRY_TEST_URL"));

        }
        else
        {
           if(functions.isValueNull(domainURL)){
               url="https://"+domainURL+RB.getString("INQUIRY_LIVE_END_URL");
           }
            else {
               url=RB.getString("INQUIRY_LIVE_URL");
           }
           //
            transactionlogger.error("inside live req of inquiry is -- >" + url);

        }
        response=    easyPaymentzUtils.doMultipartBodyConnection(url,trackingId,secretKey,appId);

        transactionlogger.error("inquiry response is trackingId  --> "+trackingId +" "+ response);

        String resString = "";
        String amount = "";
        String txtMsg = "";
        String orderID = "";
        String merchantId = "";
        boolean jsonObjectIsValid=false;
        jsonObjectIsValid= functions.isJSONValid(response);
        if(jsonObjectIsValid)
        {
            try{
                String statusNotFound="";
                String exception="";
                String statusCode="";
                JSONObject jsonres=new JSONObject(response);
                transactionlogger.error("JSONException cashfree notfound transaction inquiry-->"+jsonres);

                if (jsonres.has("status"))
                    statusNotFound = jsonres.getString("status");
                if (jsonres.has("exception"))
                    exception = jsonres.getString("exception");
                if (jsonres.has("statusCode"))
                    statusCode = jsonres.getString("statusCode");
                if("404".equalsIgnoreCase(statusCode)&&"ORDER_ID_NOT_FOUND".equalsIgnoreCase(exception)){
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setRemark("Failed-NF");
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setAmount(requestAmount);
                    commResponseVO.setTransactionId(trackingId);
                    //  commResponseVO.setResponseHashInfo(orderID);
                }
                else {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }

            }
            catch (Exception ee){
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

        }
        else
        {

            JSONArray responseArray = new JSONArray(response);
            transactionlogger.error("array length  is -->" + responseArray.length());
            for (int i = 0; i < responseArray.length(); i++)
            {
                resString = responseArray.getString(i);
                transactionlogger.error("resString is -->" + resString);
                JSONObject jsonres = new JSONObject(resString);

                if (jsonres.has("merchantId"))
                    merchantId = jsonres.getString("merchantId");
                if (jsonres.has("amount"))
                    amount = jsonres.getString("amount");
                if (jsonres.has("status"))
                    status = jsonres.getString("status");
                if (jsonres.has("txtMsg"))
                    txtMsg = jsonres.getString("txtMsg");
                if (jsonres.has("orderID"))
                    orderID = jsonres.getString("orderID");
            }


            if (functions.isValueNull(status))
            {
                transactionlogger.error("inside if easypayemntz inquiry response trackingId-->" + trackingId + "response " + inquiry_res);

                if ("SUCCESS".equalsIgnoreCase(status) || "Success".equalsIgnoreCase(status))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setDescription("Success");
                    commResponseVO.setRemark(txtMsg);
                    commResponseVO.setTransactionStatus("Success");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(orderID);
                    commResponseVO.setResponseHashInfo(orderID);
                    //commResponseVO.setCurrency(orderCurrency);
                }

                else if (status.equalsIgnoreCase("FAILED"))
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setRemark(txtMsg);
                    commResponseVO.setTransactionStatus("Failed");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(orderID);
                    commResponseVO.setResponseHashInfo(orderID);

                }

                else
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("pending");
                    commResponseVO.setRemark(txMsg);
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setAmount(amount);
                    commResponseVO.setTransactionId(orderID);
                    //commResponseVO.setCurrency(orderCurrency);
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
    }
    catch (JSONException e)
    {
        transactionlogger.error("JSONException cashfree notfound transaction inquiry-->",e);

            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
            commResponseVO.setTransactionStatus("pending");
    }

    return commResponseVO;
}



    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionlogger.error("-----inside EasyPaymentz process payout-----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();

        String hostURL              = "";
        String merchantId           = gatewayAccount.getMerchantId();
        String MERCHANT_SECRET_KEY  = gatewayAccount.getFRAUD_FTP_PATH();

        String phonenumber		= "9999999999";
        String orderid			= trackingId;
        String amount			= "";
        String bankaccount		= "";
        String ifsc				= "";
        String purpose			= "SALARY_DISBURSEMENT";
        String beneficiaryName	= "";
        String requestType		= "";

        JSONObject jsonObject   = new JSONObject();
        try
        {
            if (isTest)
            {
                hostURL         = RB.getString("PAYOUT_TEST_URL");
            }else
            {
                hostURL         = RB.getString("PAYOUT_LIVE_URL");
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
                bankaccount     = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankIfsc()))
            {
                ifsc = commTransactionDetailsVO.getBankIfsc();
            }

            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount = commTransactionDetailsVO.getAmount();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankTransferType()))
            {
                requestType = commTransactionDetailsVO.getBankTransferType();
            }
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {
                if(commAddressDetailsVO.getPhone().contains("-")){
                    phonenumber  = commAddressDetailsVO.getPhone().split("-")[1];
                }
                else{
                    phonenumber  = commAddressDetailsVO.getPhone();
                }
            }

            jsonObject.put("phonenumber", phonenumber);
            jsonObject.put("orderid", orderid);
            jsonObject.put("amount", amount);
            jsonObject.put("bankaccount", bankaccount);
            jsonObject.put("ifsc", ifsc);
            jsonObject.put("purpose", purpose);
            jsonObject.put("beneficiaryName", beneficiaryName);
            jsonObject.put("requestType", requestType);

            transactionlogger.error("easyPaymentzPayFinalRequest payout------> trackingid---->"+trackingId +":::"+ jsonObject.toString());

            String responeString = EasyPaymentzUtils.doHttpPostConnection(hostURL, jsonObject.toString(), merchantId, MERCHANT_SECRET_KEY);

            transactionlogger.error("EasyPaymentzFinalResponse  payout------> trackingid---->"+trackingId+":::"+ responeString);

            String code                 = "";
            String message              = "";
            String beneficiaryNam       = "";
            String accountNo            = "";
            String IFSCCodeRes          = "";
            String transactionId        = "";
            String txtStatus            = "";

            if(functions.isValueNull(responeString) && responeString.contains("{"))
            {
                JSONObject payoutResponse = new JSONObject(responeString);

                if (payoutResponse.has("status") && functions.isValueNull(payoutResponse.getString("status")))
                {
                    txtStatus = payoutResponse.getString("status");
                }

                if (payoutResponse.has("message") && functions.isValueNull(payoutResponse.getString("message")))
                {
                    message = payoutResponse.getString("message");
                }

                if (payoutResponse.has("orderid") && functions.isValueNull(payoutResponse.getString("orderid")))
                {
                    transactionId = payoutResponse.getString("orderid");
                }

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
                else if (txtStatus.equalsIgnoreCase("PENDING") || txtStatus.equalsIgnoreCase("ACCEPTED")||txtStatus.contains("PENDING")||txtStatus.contains("pending"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(message);
                    commResponseVO.setDescription(message);

                }
                else if (txtStatus.equalsIgnoreCase("FAILED") || txtStatus.equalsIgnoreCase("FAILURE"))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(txtStatus + "-" + message);
                    commResponseVO.setDescription(message);

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
            String CALL_EXECUTE_AFTER=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_AFTER");
            String CALL_EXECUTE_INTERVAL=RB.getString("PAYOUT_INQUIRY_CALL_EXECUTE_INTERVAL");
            String MAX_EXECUTE_SEC=RB.getString("PAYOUT_INQUIRY_MAX_EXECUTE_SEC");
            String isThreadAllowed=RB.getString("THREAD_CALL");
            transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
            if("Y".equalsIgnoreCase(isThreadAllowed)){
                transactionlogger.error("isThreadAllowed ------->"+isThreadAllowed);
                new AsyncEPaymentzPayoutQueryService(trackingId,accountId,"",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
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
        transactionlogger.error("-----inside EasyPaymentz payout inquiry------->");

        CommRequestVO commRequestVO         = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO     = new Comm3DResponseVO();
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                      = false;
        String orderId                      = "";
        String requestURL                      = "";
        Functions functions                 = new Functions();
        JSONObject parameters               = new JSONObject();

        String MERCHANT_SECRET_KEY  = gatewayAccount.getFRAUD_FTP_PATH();
        String merchantId           = gatewayAccount.getMerchantId();
        String currency             = gatewayAccount.getCurrency();

        try
        {
            orderId         = trackingId;
            isTest          = gatewayAccount.isTest();

            parameters.put("orderId",trackingId);

            if (isTest)
            {
                requestURL = RB.getString("PAYOUT_INQUIRY_TEST_URL");
            }
            else
            {
                requestURL = RB.getString("PAYOUT_INQUIRY_LIVE_URL");

            }

            transactionlogger.error("requestURL-->"+ trackingId+"  "+requestURL);
            transactionlogger.error("EasyPayoutInqueryReq-------> "+trackingId +parameters.toString());

            String responeString    = EasyPaymentzUtils.doHttpPostConnection(requestURL, parameters.toString(), merchantId, MERCHANT_SECRET_KEY);

            transactionlogger.error("EasyPayoutInqueryRes----> "+trackingId + responeString);

            if(functions.isValueNull(responeString) && responeString.contains("{"))
            {
                JSONObject payoutResponse   = new JSONObject(responeString);

                String responseMessage  = "";
                String txnStatus        = "";
                String transactionId    = "";
                String orderid          = "";
                String amount           = "";
                String statusCode           = "";

                if(payoutResponse.has("status")&&functions.isValueNull(payoutResponse.getString("status"))){
                    txnStatus       = payoutResponse.getString("status");
                }
                if(payoutResponse.has("message")&&functions.isValueNull(payoutResponse.getString("message"))){
                    responseMessage = payoutResponse.getString("message");
                }
                if(payoutResponse.has("orderid")&&functions.isValueNull(payoutResponse.getString("orderid"))){
                    transactionId = payoutResponse.getString("orderid");
                }
                if(payoutResponse.has("amount")&&functions.isValueNull(payoutResponse.getString("amount"))){
                    amount  = payoutResponse.getString("amount");
                }
                if(payoutResponse.has("statusCode")&&functions.isValueNull(payoutResponse.getString("statusCode"))){
                    statusCode  = payoutResponse.getString("statusCode");
                }

                if (txnStatus.equalsIgnoreCase("SUCCESS"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setCurrency(currency);
                }
                else if (txnStatus.equalsIgnoreCase("PENDING") || txnStatus.equalsIgnoreCase("ACCEPTED")||txnStatus.contains("PENDING")||txnStatus.contains("pending"))
                {
                    commResponseVO.setStatus("pending");
                    commResponseVO.setTransactionStatus("pending");
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setCurrency(currency);
                }
                else if (txnStatus.equalsIgnoreCase("FAILED") || txnStatus.equalsIgnoreCase("FAILURE")||(txnStatus.equalsIgnoreCase("false")&&statusCode.equalsIgnoreCase("404")))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark(txnStatus);
                    commResponseVO.setDescription(txnStatus);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setCurrency(currency);
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
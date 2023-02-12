package com.payment.onepay;


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
 import com.payment.payaidpayments.PayaidPaymentProcess;
 import com.payment.payaidpayments.PayaidPaymentUtils;
 import com.payment.validators.vo.CommonValidatorVO;
 import org.codehaus.jettison.json.JSONException;
 import org.codehaus.jettison.json.JSONObject;
 import org.eclipse.persistence.internal.sessions.DirectCollectionChangeRecord;
 import org.eclipse.persistence.jpa.jpql.parser.DateTime;
 import org.json.JSONArray;


 import java.io.UnsupportedEncodingException;
 import java.security.NoSuchAlgorithmException;
 import java.text.SimpleDateFormat;
 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.ResourceBundle;

/**
 * Created by Admin on 27/12/2021.
 */
public class OnePayPaymentGateway extends AbstractPaymentGateway
{
    TransactionLogger transactionlogger = new TransactionLogger(OnePayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "onepay";
    public static final String CARD = "CARD";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.onepay");
    private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.OPBANKS");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public OnePayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of OnePayPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        OnepayUtils onePayUtils = new OnepayUtils();
        OnepayPaymentProcess onepayPaymentProcess = new OnepayPaymentProcess();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand = transactionDetailsVO.getCardType();
        String payment_Card = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest = gatewayAccount.isTest();
        String currency = "INR";
        String amount = transactionDetailsVO.getAmount();
        String email = "";
        String name = "";
        String zip_code = "";
        String city = "";
        String bank_code = "";
        String card_holder_name = "";
        String custMobile = "";
        String custMail;
        String udf1;
        String udf2;
        String udf3;
        String udf4;
        String udf5;
        String merchantId = gatewayAccount.getMerchantId();

        try
        {

            if (functions.isValueNull(commAddressDetailsVO.getEmail()))
                custMail = commAddressDetailsVO.getEmail();
            else
            {
                custMail = "customer@gmail.com";
            }
            if (functions.isValueNull(commAddressDetailsVO.getStreet()))
            {
                udf1 = commAddressDetailsVO.getStreet();
            }
            else
            {
                udf1 = "Delhi";
            }

            if (functions.isValueNull(commAddressDetailsVO.getCity()))
            {
                udf2 = commAddressDetailsVO.getCity();
            }
            else
            {
                udf2 = "Delhi";
            }
            if (functions.isValueNull(commAddressDetailsVO.getState()))
            {
                udf3 = commAddressDetailsVO.getState();
            }
            else
            {
                udf3 = "Delhi";
            }
            if (functions.isValueNull(commAddressDetailsVO.getCountry()))
            {
                udf4 = commAddressDetailsVO.getCountry();
            }
            else
            {
                udf4 = "INDIA";
            }
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
                udf5 = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();
            else
            {
                udf5 = " ";
            }

            String apiKey = gatewayAccount.getFRAUD_FTP_USERNAME();
            String secret_key = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String txnId = trackingID;
            String country = "IND";
            String CARDNO = commCardDetailsVO.getCardNum();
            String EXPIRY = commCardDetailsVO.getExpMonth() + "" + commCardDetailsVO.getExpYear();
            String CVV = commCardDetailsVO.getcVV();
            String txnType = "REDIRECT";
            String returnURL = "";//RB.getString("return_url") + trackingID;
            String productId = "DEFAULT";
            String isMultiSettlement = "0";
            String instrumentId = "";
            String CARDNAME = commCardDetailsVO.getCardHolderFirstName() + " " + commCardDetailsVO.getCardHolderSurname();
            String SAVECARDFLAG = "N";
            String cardDetails = "NA";
            String cardType = "NA";
            String payment_url = RB.getString("LIVE_SALE_URL");
            String channelId = "0";
            transactionlogger.error("vpa requestVO--- " + commRequestVO.getCustomerId());
            String UPI = commRequestVO.getCustomerId();

            if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())){
                returnURL = "https://checkout." + gatewayAccount.getFRAUD_FTP_SITE() + RB.getString("HOSTURL")+trackingID;
                //  return_url = RB.getString("return_url")+trackingID;
            }else{
                returnURL = RB.getString("return_url")+trackingID;
            }
            if (functions.isValueNull(commAddressDetailsVO.getPhone()))
            {

                if (commAddressDetailsVO.getPhone().contains("-"))
                {
                    custMobile = commAddressDetailsVO.getPhone().split("\\-")[1];
                }
                if (custMobile.length() > 10)
                {

                    custMobile = custMobile.substring(custMobile.length() - 10);
                }
                else
                {
                    custMobile = commAddressDetailsVO.getPhone();
                }

            }
            else
            {
                custMobile = "9999999999";
            }
            //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = "";
            String strDate = formatter.format(date);

            String payment_Mode = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
            instrumentId = onePayUtils.getPaymentMethod(payment_Mode);

            JSONObject jsonObjectRequestData = new JSONObject();
            jsonObjectRequestData.put("dateTime", strDate);
            jsonObjectRequestData.put("amount", amount);
            jsonObjectRequestData.put("isMultiSettlement", isMultiSettlement);
            jsonObjectRequestData.put("custMobile", custMobile);
            jsonObjectRequestData.put("apiKey", apiKey);
            jsonObjectRequestData.put("productId", productId);
            jsonObjectRequestData.put("instrumentId", instrumentId);
            if ("DC".equalsIgnoreCase(payment_Mode))
            {
                transactionlogger.error("inside CC & DC condition (map)-------------->" + payment_Mode);
                cardType = onePayUtils.getCardType(payment_brand);
                cardDetails=CARDNAME + "|" + CARDNO + "|" + CVV + "|" + EXPIRY + "|" + SAVECARDFLAG;

            }
            else if ("NBI".equalsIgnoreCase(payment_Mode))
            {
                cardDetails = transactionDetailsVO.getCustomerBankId();
                transactionlogger.error("inside NBI condition (map)-------------->" + payment_Mode + "  cardDetails " + bank_code);

            }
            else if ("UP".equalsIgnoreCase(payment_Mode))
            {
                cardDetails = commRequestVO.getCustomerId();
                transactionlogger.error("inside UPI condition (map)-------------->" + payment_Mode+"  cardDetails "+bank_code);
            }
            else
            {
                //todo need to failed
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                return commResponseVO;
            }

            jsonObjectRequestData.put("cardType", cardType);
            jsonObjectRequestData.put("udf5", udf5);
            jsonObjectRequestData.put("txnType", txnType);
            jsonObjectRequestData.put("udf3", udf3);
            jsonObjectRequestData.put("udf4", udf4);
            jsonObjectRequestData.put("udf1", udf1);
            jsonObjectRequestData.put("udf2", udf2);
            jsonObjectRequestData.put("merchantId", merchantId);
            jsonObjectRequestData.put("cardDetails",cardDetails);
            jsonObjectRequestData.put("custMail", custMail);
            jsonObjectRequestData.put("returnURL", returnURL);
            jsonObjectRequestData.put("channelId", channelId);
            jsonObjectRequestData.put("txnId", txnId);


            transactionlogger.error("Sale onepay request for" + trackingID + " -- " + jsonObjectRequestData);
            String reqData_Hashed = OnepayUtils.encrypt1(jsonObjectRequestData.toString(), secret_key);
            transactionlogger.error("Sale onepay encrypted request for" + trackingID + " -- " + reqData_Hashed);
            HashMap<String, String> requestMap = new HashMap();
            requestMap.put("merchantId", merchantId);
            requestMap.put("reqData", reqData_Hashed);
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(payment_url);
            commResponseVO.setRequestMap(requestMap);

        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException::::", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException:::", e);
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception:::", e);
        }


        return commResponseVO;

    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in OnePayPaymentGateway ");
        OnepayPaymentProcess onepayPaymentProcess = new OnepayPaymentProcess();
        String html = "";
        Comm3DResponseVO transRespDetails = null;
        OnepayUtils onepayUtils = new OnepayUtils();
        String paymentMode = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO = onepayUtils.getOnepayPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {

                html = onepayPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                transactionlogger.error("automatic redirect OnepayPaymentGateway form -- >>" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in OnepayPaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of OnepayGateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        OnepayUtils onepayUtils = new OnepayUtils();
        String api_key = "";
        String txnId = "";
        String hash = "";
        String reqestData = "";
        String salt = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String merchantId = gatewayAccount.getMerchantId();

        String inquiry_res = "";
        try
        {
            api_key = gatewayAccount.getMerchantId();
            txnId = trackingId;
            String inquiryReqString = "merchantId=" + merchantId + "&txnId=" + txnId;


            transactionlogger.error("inquiry req for" + trackingId + " -->" + inquiryReqString);
            if (isTest)
            {
                transactionlogger.error("inside test req of inquiry is -- >" + RB.getString("INQUIRY_TEST_URL"));
                inquiry_res = onepayUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_TEST_URL") + inquiryReqString);
            }
            else
            {
                transactionlogger.error("inside live req of inquiry is -- >" + RB.getString("INQUIRY_LIVE_URL"));
                inquiry_res = onepayUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_LIVE_URL") + inquiryReqString);

            }
            transactionlogger.error("inquiry response  -- >" + inquiry_res);


            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String error_desc = "";
                String response_code = "";
                String authorization_staus = "";
                String responseCode = "";
                String payment_mode = "";
                String merchant_id = "";
                String txn_id = "";
                String pg_ref_id = "";
                String trans_status = "";
                String txn_amount = "";
                String resp_code = "";
                String resp_message = "";
                String bank_ref_id = "";

                String response_message = "";
                String orderId = "";
                String resNotFoundRemark = "";
                String resString = "";

                try
                {


                    transactionlogger.error("resString for " + trackingId + " -->" + inquiry_res);
                    JSONObject jsonObject = new JSONObject(inquiry_res);

                    if (jsonObject != null)
                    {
                        if (jsonObject.has("txn_id"))
                        {
                            txn_id = jsonObject.getString("txn_id");
                            transactionlogger.error("transaction_id" + txn_id);
                        }
                        if (jsonObject.has("payment_mode"))
                        {
                            payment_mode = jsonObject.getString("payment_mode");
                        }
                        if (jsonObject.has("resp_message"))
                        {
                            resp_message = jsonObject.getString("resp_message");
                        }
                        if (jsonObject.has("resp_code"))
                        {
                            resp_code = jsonObject.getString("resp_code");
                        }
                        if (jsonObject.has("merchant_id"))
                        {
                            merchant_id = jsonObject.getString("merchant_id");
                        }
                        if (jsonObject.has("txn_amount"))
                        {
                            txn_amount = jsonObject.getString("txn_amount");
                        }
                        if (jsonObject.has("pg_ref_id"))
                        {
                            pg_ref_id = jsonObject.getString("pg_ref_id");
                        }
                        if (jsonObject.has("trans_status"))
                        {
                            trans_status = jsonObject.getString("trans_status");
                        }

                        if (jsonObject.has("bank_ref_id"))
                        {
                            bank_ref_id = jsonObject.getString("bank_ref_id");
                        }


                             if ("00000".equalsIgnoreCase(resp_code) && "Ok".equalsIgnoreCase(trans_status))
                            {
                                transactionlogger.error("inside if success " + resp_code);
                                commResponseVO.setStatus("success");
                                commResponseVO.setTransactionStatus("success");
                                commResponseVO.setDescription("success");
                                commResponseVO.setRemark(resp_message);
                                commResponseVO.setAmount(txn_amount);
                                commResponseVO.setTransactionId(bank_ref_id);
                                commResponseVO.setAuthCode(pg_ref_id);
                                commResponseVO.setErrorCode(resp_code);
                                //qikPayUtils.updateMainTableEntry(status,trackingId);
                            }


                            else if ("to".equalsIgnoreCase(trans_status))
                            {
                                commResponseVO.setStatus("pending");
                                commResponseVO.setTransactionStatus("pending");
                                commResponseVO.setDescription("pending");
                                commResponseVO.setRemark("pending");
                                commResponseVO.setAmount(txn_amount);
                                commResponseVO.setErrorCode(resp_code);
                                //qikPayUtils.updateMainTableEntry(status,trackingId);
                            }

                            else if ("FFFFF".equalsIgnoreCase(resp_code)&&"F".equalsIgnoreCase(trans_status))                          {

                                commResponseVO.setStatus("Failed");

                                commResponseVO.setDescription("Failed");
                                commResponseVO.setRemark(resp_message);
                                commResponseVO.setTransactionStatus("Failed");
                                commResponseVO.setTransactionId(bank_ref_id);
                                commResponseVO.setErrorCode(resp_code);

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
                catch (JSONException e)
                {
                    transactionlogger.error("JSONException onepay inquiry response  -- >" ,e);
                }
                return commResponseVO;
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception onepay inquiry response  -- >", e);
        }
        return commResponseVO;
    }


    public static void main(String[] args)
    {
        try
        {
            String payment_url= "https://onepaypgtest.in/onepayVAS/payprocessor";
            String Inquiry_URL="https://onepaypgtest.in/onepayVAS/getTxnDetails?";

            OnepayUtils onePayUtils = new OnepayUtils();
            CommResponseVO commResponseVO= new CommResponseVO();

            String trackingID="123456";
            String strDate="2018-08-01 13:20:33" ;
            String amount = "10.00";
            String isMultiSettlement = "0";
            String apiKey = "oG8MG7vR8hy1id6Ac3if1hY4hF1Wo6JA";
            String secret_key="oG8MG7vR8hy1id6Ac3if1hY4hF1Wo6JA";
            String productId = "DEFAULT";
            String merchantId = "M0000802";
            String returnURL = "https://test.dsgintl.net/transaction/QikPayFrontEndServlet?trackingId";
            String channelId = "0";
            String txnId = "123456";
            String instrumentId = onePayUtils.getPaymentMethod("DC");
            String cardType = onePayUtils.getCardType("VISA");
            String udf4="";
            String udf5="";
            String udf3="";
            String udf1="";
            String udf2="";
            String custMail="cust@gmail.com";
            JSONObject jsonObjectRequestData = new JSONObject();

         //   jsonObjectRequestData.put("cardType", "Mastercard");

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = "";
             strDate = formatter.format(date);
            jsonObjectRequestData.put("dateTime", strDate);
            jsonObjectRequestData.put("amount", amount);
            jsonObjectRequestData.put("isMultiSettlement", isMultiSettlement);
            jsonObjectRequestData.put("apiKey", "oG8MG7vR8hy1id6Ac3if1hY4hF1Wo6JA");
            jsonObjectRequestData.put("productId", productId);
            jsonObjectRequestData.put("txnType", "REDIRECT");
            jsonObjectRequestData.put("udf3",udf3);
            jsonObjectRequestData.put("udf1",udf1);
            jsonObjectRequestData.put("udf2",udf2);
            jsonObjectRequestData.put("custMail",custMail);
            jsonObjectRequestData.put("merchantId", "M0000802");
            jsonObjectRequestData.put("returnURL", returnURL);
            jsonObjectRequestData.put("channelId", channelId);
            jsonObjectRequestData.put("txnId", txnId);
            jsonObjectRequestData.put("udf4", udf4);
            jsonObjectRequestData.put("udf5", udf5);
            jsonObjectRequestData.put("instrumentId", instrumentId);
            String bank_code="";

            String payment_Mode="DC";
            if ("DC".equalsIgnoreCase(payment_Mode))
            {
                System.out.println("inside CC & DC condition (map)-------------->" + payment_Mode);
                jsonObjectRequestData.put("cardType", cardType);
                //jsonObjectRequestData.put("cardDetails", CARDNAME + "|" + CARDNO + "|" + CVV + "|" + EXPIRY + "|" + SAVECARDFLAG;);
                jsonObjectRequestData.put("cardDetails", "Smarita" + "|" + "4444333322221111" + "|" + "123" + "|" + "12/2030" + "|" + "N");
            }
            else if ("NBI".equalsIgnoreCase(payment_Mode))
            {
                cardType = "NA";
                bank_code = "HDFC";
                jsonObjectRequestData.put("cardType", cardType);
                jsonObjectRequestData.put("cardDetails", bank_code);

            }
            else if ("UP".equalsIgnoreCase(payment_Mode))
            {
                cardType = "NA";
                bank_code = "test@upi";
                jsonObjectRequestData.put("cardType", cardType);
                jsonObjectRequestData.put("cardDetails", bank_code);
            }
            else
            {
                //todo need to failed
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                System.out.print(commResponseVO); ;
            }
            System.out.println("Sale request for" + trackingID + " -- " + jsonObjectRequestData);

            String reqData_Hashed = OnepayUtils.encrypt1(jsonObjectRequestData.toString(), secret_key);
            System.out.println("Encrypted data ==="+reqData_Hashed);
            HashMap<String, String> requestMap = new HashMap();
            requestMap.put("merchantId", merchantId);
            requestMap.put("reqData", reqData_Hashed);


            String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +payment_url+"\">" ;
            Iterator keys=requestMap.keySet().iterator();
            while (keys.hasNext())
            {
                String key= (String) keys.next();
                form+="<input type=\"hidden\" name=\""+key+"\"  value=\"" + requestMap.get(key)+"\">";

            }
            form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

            System.out.println("onepayPaymentPro9cess Form---" + form.toString());
            String reqdcrypt = OnepayUtils.decrypt1(reqData_Hashed, secret_key);
            System.out.println("reqdcrypt ---" + reqdcrypt);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }


}


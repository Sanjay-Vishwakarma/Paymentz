package com.payment.payaidpayments;
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
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.imoneypay.AsyncIMoneyPayPayoutQueryService;
import com.payment.imoneypay.IMoneypPayUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.tomcat.jni.Time;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by Admin on 23/12/2021.
 */
public class PayaidPaymentGateway extends AbstractPaymentGateway
{
    TransactionLogger transactionlogger = new TransactionLogger(PayaidPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "PayaidPay";
    public static final String CARD = "CARD";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payaid");
    private final static ResourceBundle RB_NB = LoadProperties.getProperty("com.directi.pg.PABANKS");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public PayaidPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of PayaidPaymentGateway......");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();

        PayaidPaymentUtils payaidPaymentUtils = new PayaidPaymentUtils();
        PayaidPaymentProcess payaidPaymentProcess = new PayaidPaymentProcess();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String payment_brand = transactionDetailsVO.getCardType();
        String payment_Mode = GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());

        boolean isTest = gatewayAccount.isTest();
        String currency = "INR";
        String amount = transactionDetailsVO.getAmount();
        String email = "";
        String name = "";
        String zip_code = "";
        String city = "";
        String bank_code = "";
        String card_holder_name="";

        if (functions.isValueNull(commAddressDetailsVO.getFirstname()) && functions.isValueNull(commAddressDetailsVO.getLastname()))
            name = commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname();

        else
        {
            name = " ";
        }

        if (functions.isValueNull(commAddressDetailsVO.getEmail()))
            email = commAddressDetailsVO.getEmail();
        else
        {
            email = "customer@gmail.com";
        }

        if (functions.isValueNull(commAddressDetailsVO.getCity()))
        {
            city = commAddressDetailsVO.getCity();
        }
        else
        {
            city = "Delhi";
        }
        if (functions.isValueNull(commAddressDetailsVO.getZipCode()))
        {
            zip_code = commAddressDetailsVO.getZipCode();
        }
        else
        {
            zip_code = "400100";
        }
        String cardType = GatewayAccountService.getCardType(transactionDetailsVO.getCardType());
        bank_code = payaidPaymentUtils.getPaymentType(cardType);

        String card_number = "";
        String expiry_date = commCardDetailsVO.getExpMonth() + "" + commCardDetailsVO.getExpYear();
        String CVV = commCardDetailsVO.getcVV();
        if (functions.isValueNull(commCardDetailsVO.getCardNum()))
        {
            card_number = commCardDetailsVO.getCardNum();
        }
        else
        {
            card_number = "";
        }
        if (functions.isValueNull(commCardDetailsVO.getExpMonth())&&functions.isValueNull(commCardDetailsVO.getExpYear()))
        {
            expiry_date = commCardDetailsVO.getExpMonth() + "" + commCardDetailsVO.getExpYear();
        }
        else
        {
            expiry_date = "";
        }
        if (functions.isValueNull(commCardDetailsVO.getcVV()))
        {
            CVV = commCardDetailsVO.getcVV();
        }
        else
        {
            CVV = "";
        }
        String api_key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String salt = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String order_id = trackingID;
        String country = "IND";
        card_holder_name="";
        String PAY_ID = gatewayAccount.getMerchantId();

        String return_url = RB.getString("return_url") + trackingID;

        String TXNTYPE = "SALE";
        String support3DAccount = gatewayAccount.get_3DSupportAccount();
        String crypto_Key = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String payment_url = RB.getString("LIVE_SALE_URL");
        String hash = "";
        String phone = "";
        String UPI = "";
        String customerId = "";
        String description = trackingID;
        String cvv="";
        String payer_virtual_address=commRequestVO.getCustomerId();;
        transactionlogger.error("vpa requestVO--- " + commRequestVO.getCustomerId());

        if(functions.isValueNull(gatewayAccount.getFRAUD_FTP_SITE())){
            return_url = "https://checkout." + gatewayAccount.getFRAUD_FTP_SITE() + RB.getString("HOSTURL")+trackingID;
          //  return_url = RB.getString("return_url")+trackingID;
        }else{
            return_url = RB.getString("return_url")+trackingID;
        }
        if (functions.isValueNull(commAddressDetailsVO.getPhone()))
        {

            if (commAddressDetailsVO.getPhone().contains("-"))
            {
                phone = commAddressDetailsVO.getPhone().split("\\-")[1];
            }
            if (phone.length() > 10)
            {

                phone = phone.substring(phone.length() - 10);
            }
            else
            {
                phone = commAddressDetailsVO.getPhone();
            }

        }
        else
        {
            phone = "9999999999";
        }

        try
        {
            HashMap<String, String> requestMap = new HashMap();
            String hash_data ="";
            if ("DC".equalsIgnoreCase(payment_Mode))
            {
                card_holder_name=name;
            }
            String [] hash_columns = {amount,api_key,bank_code,card_holder_name,card_number,city,country,currency,cvv,description,email,expiry_date,name,order_id,payer_virtual_address,phone,return_url,zip_code};

            hash_data = salt;

            for( int i = 0; i < hash_columns.length; i++)
            {
                if(hash_columns[i].length() > 0 ){
                    hash_data += '|' + hash_columns[i];
                }
            }
            transactionlogger.error("hash_data--- " + hash_data);
            hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            requestMap.put("hash", hash);
            requestMap.put("amount", amount);
            requestMap.put("api_key", api_key);
            requestMap.put("city", city);
            requestMap.put("country", country);
            requestMap.put("currency", currency);
            requestMap.put("description", description);
            requestMap.put("email", email);
            requestMap.put("name", name);
            requestMap.put("order_id", order_id);
            requestMap.put("phone", phone);
            requestMap.put("return_url", return_url);
            requestMap.put("zip_code", zip_code);
            if ("DC".equalsIgnoreCase(payment_Mode))
            {
                transactionlogger.error("inside CC & DC condition (map)-------------->" + payment_Mode);
                requestMap.put("card_holder_name", card_holder_name);
                requestMap.put("card_number", card_number);
                requestMap.put("expiry_date", expiry_date);
                requestMap.put("CVV", CVV);
                requestMap.put("bank_code", bank_code);
            }
            else if ("NB".equalsIgnoreCase(payment_Mode))
            {
                bank_code = transactionDetailsVO.getCustomerBankCode();
                requestMap.put("bank_code", bank_code);

            }
            else if ("UPI".equalsIgnoreCase(payment_Mode))
            {
                requestMap.put("bank_code", bank_code);
                requestMap.put("payer_virtual_address", payer_virtual_address);
            }
            else
            {
                //todo need to failed
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription("Incorrect Request");
                commResponseVO.setRemark("Incorrect Request");
                return commResponseVO;
            }

            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(payment_url);
            commResponseVO.setRequestMap(requestMap);

        }
        catch (Exception e)
        {
            transactionlogger.error("Exception PayAidPaymentGateway ---------------> ", e);
        }

        return commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in PayaidPaymentGateway ---- ");
        String html = "";
        PaymentManager paymentManager = new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        PayaidPaymentUtils payaidPaymentUtils = new PayaidPaymentUtils();
        PayaidPaymentProcess payaidPaymentProcess = new PayaidPaymentProcess();
        String paymentMode = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO = payaidPaymentUtils.getPayaidPaymentRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = payaidPaymentProcess.get3DConfirmationForm(commonValidatorVO, transRespDetails);
                transactionlogger.error("automatic redirect PayaidPaymentGateway form -- >>" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in PayAidPaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processQuery(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processInquiry of PayaidGateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        PayaidPaymentUtils payaidUtils = new PayaidPaymentUtils();
        String api_key = "";
        String order_id = "";
        String hash = "";
        String reqestData = "";
        String salt = gatewayAccount.getFRAUD_FTP_PASSWORD();


        String inquiry_res = "";
        try
        {
            api_key = gatewayAccount.getFRAUD_FTP_USERNAME();
            order_id = trackingId;
            String hash_data = salt + "|" + api_key + "|" + order_id;
            hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            reqestData = "{\"api_key\": \"" + api_key + "\", \"order_id\": \"" + order_id + "\", \"hash\": \"" + hash + "\"}";


            transactionlogger.error("inquiry req for" +trackingId+" -->" + reqestData);
            if (isTest)
            {
                transactionlogger.error("inside test req of inquiry is -- >" + RB.getString("INQUIRY_TEST_URL"));
                inquiry_res = payaidUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_TEST_URL"), reqestData);
            }
            else
            {
                transactionlogger.error("inside live req of inquiry is -- >" + RB.getString("INQUIRY_LIVE_URL"));
                inquiry_res = payaidUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_LIVE_URL"), reqestData);

            }
            transactionlogger.error("inquiry response  -- >" + inquiry_res);


            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String error_desc = "";
                String response_code = "";
                String authorization_staus = "";
                String responseCode = "";
                String transactionId = "";
                String txnType = "";
                String approvedAmount = "";
                String currency = "";
                String dateTime = "";
                String Description = "";
                String response_message = "";
                String payId = "";
                String orderId = "";
                String resNotFoundRemark = "";
                String resString = "";


                String transaction_id = "";
                String amount = "";

                JSONObject jsonObject2 = new JSONObject(inquiry_res);
                JSONArray responseArray = jsonObject2.getJSONArray("data");
                for (int i = 0; i < responseArray.length(); i++)
                {
                    resString = responseArray.getString(i);

                }
                JSONObject jsonObject = new JSONObject(resString);

                    if (jsonObject != null)
                    {
                        if (jsonObject.has("transaction_id"))
                        {
                            transaction_id = jsonObject.getString("transaction_id");
                            transactionlogger.error("transaction_id ::" + transaction_id);
                        }
                        if (jsonObject.has("amount"))
                        {
                            amount = jsonObject.getString("amount");
                            transactionlogger.error("amount ::" + amount);
                        }
                        if (jsonObject.has("response_message"))
                        {
                            response_message = jsonObject.getString("response_message");
                        }
                        if (jsonObject.has("response_code"))
                        {
                            response_code = jsonObject.getString("response_code");
                        }
                        if (jsonObject.has("error_desc"))
                        {
                            error_desc = jsonObject.getString("error_desc");
                        }

                        if ("0".equalsIgnoreCase(response_code)&&"SUCCESS".equalsIgnoreCase(response_message))
                        {
                            transactionlogger.error("inside if success ::" + response_message);
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescription("success");
                            commResponseVO.setRemark(response_message);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setTransactionId(transaction_id);
                            commResponseVO.setErrorCode(response_code);
                            //qikPayUtils.updateMainTableEntry(status,trackingId);
                        }

                        else if ("1006".equalsIgnoreCase(response_code))
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("pending");
                            commResponseVO.setRemark(response_message);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setErrorCode(response_code);
                            //qikPayUtils.updateMainTableEntry(status,trackingId);
                        }
                        //todo need to check failed status
                        else if (response_code.equalsIgnoreCase("1009")||"1000".equalsIgnoreCase(response_code) || response_code.equals("1014") || response_code.equals("1015") ||response_code.equals("1023")||response_code.equals("1024")||response_code.equals("9999")||response_code.equals("1025")|| response_code.equals("1026") || response_code.equals("1027") || response_code.equals("1029") || response_code.equals("1042") || response_code.equals("1043") || response_code.equals("1072") || response_code.equals("1073") || response_code.equals("1073"))
                        {
                            commResponseVO.setStatus("Failed");
                            commResponseVO.setDescription("Failed");
                            commResponseVO.setRemark(response_message);
                            commResponseVO.setTransactionStatus("Failed");
                            commResponseVO.setTransactionId(transaction_id);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setErrorCode(response_code);

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

        }
        catch (JSONException e)
        {
            transactionlogger.error("JSONException-->" ,e);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException-->", e);
        }

        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException-->", e);
        }
        return commResponseVO;
    }




    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionlogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        CommResponseVO commResponseVO =  new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String api_key =gatewayAccount.getMerchantId();;
        String salt=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String transaction_id=transactionDetailsVO.getPreviousTransactionId();
        String amount=transactionDetailsVO.getAmount();
        String description=commRequestVO.getTransDetailsVO().getOrderDesc();
        PayaidPaymentUtils payaidPaymentUtils= new PayaidPaymentUtils();
        String merchant_refund_id = trackingID+"_"+ Time.now();

        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("api_key", api_key);
            jsonObject.put("transaction_id", transaction_id);
            jsonObject.put("amount", amount);
            jsonObject.put("description", description);
            jsonObject.put("merchant_refund_id",merchant_refund_id);
            String hash_data = salt + "|" + amount + "|" + api_key + "|"+description+"|"+merchant_refund_id ;
            String hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            jsonObject.put("hash",hash);



            transactionlogger.error("refundRequest for "+trackingID+"-----" + jsonObject.toString());

            String refundResponse = "";
            if (isTest)
            {
                transactionlogger.error("inside PayaidPay Test Refund --->");
                refundResponse = payaidPaymentUtils.doPostHttpUrlConnection(RB.getString("REFUND_TEST_URL"), jsonObject.toString());
            }
            else
            {
                transactionlogger.error("inside PayaidPay Live Refund --->");
                refundResponse = payaidPaymentUtils.doPostHttpUrlConnection(RB.getString("REFUND_LIVE_URL"), jsonObject.toString());
            }
            transactionlogger.error("refundResponse for "+trackingID+"-----" + refundResponse);
            if (functions.isValueNull(refundResponse) && refundResponse.contains("{"))
            {
                JSONObject jsonObject1 = new JSONObject(refundResponse);
                if (jsonObject1 != null)
                {
                    String transactionId = "";
                    String txn_date = "";
                    String Response_refund_amount = "";
                    String currency = "";
                    String status = "";
                    String res_code = "";
                    String res_message = "";
                    String error_details = "";
                    String refund_id="";
                    String refund_reference_no="";

                    if (jsonObject1.has("transaction_id"))
                    {
                        transaction_id = jsonObject1.getString("transaction_id");
                    }
                    if (jsonObject1.has("refund_id"))
                    {
                        refund_id = jsonObject1.getString("refund_id");
                    }
                    if (jsonObject1.has("refund_reference_no"))
                    {
                        refund_reference_no = jsonObject1.getString("refund_reference_no");
                    }
                    if (jsonObject1.has("merchant_refund_id"))
                    {
                        merchant_refund_id = jsonObject1.getString("merchant_refund_id");
                    }

                    if (res_code.equalsIgnoreCase("0"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionStatus("Successful");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setDescription(error_details);
                        commResponseVO.setTransactionStatus("Failed");
                    }
                    commResponseVO.setRemark(res_message);
                    commResponseVO.setBankTransactionDate(txn_date);
                    commResponseVO.setTransactionId(transactionId);
                    commResponseVO.setAmount(Response_refund_amount);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setErrorCode(res_code);
                    commResponseVO.setTransactionType(status);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("Failed");
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayaidPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return commResponseVO;
    }


    public GenericResponseVO processRefundInquiry(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Inside processRefundInquiry of PayaidGateway---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        Functions functions = new Functions();
        PayaidPaymentUtils payaidUtils = new PayaidPaymentUtils();
        String api_key = "";
        String transaction_id = "";
        String merchant_order_id ="";
        String hash = "";
        String reqestData = "";
        String salt = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String inquiry_res = "";

        try
        {
            api_key = gatewayAccount.getMerchantId();
            merchant_order_id = trackingId;
            String hash_data = salt + "|" + api_key + "|" + merchant_order_id +"|"+transaction_id;
            hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            reqestData = "{\"api_key\": \"" + api_key + "\", \"merchant_order_id\": \"" + merchant_order_id + "\", \"transaction_id\": \"\" + transaction_id + \"\", \"hash\": \"" + hash + "\"}";


            transactionlogger.error("inquiry req for" +trackingId+" -->" + reqestData);
            if (isTest)
            {
                transactionlogger.error("inside test req of inquiry is -- >" + RB.getString("REFUND_INQUIRY_TEST_URL"));
                inquiry_res = payaidUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_TEST_URL"), reqestData);
            }
            else
            {
                transactionlogger.error("inside live req of inquiry is -- >" + RB.getString("REFUND_INQUIRY_LIVE_URL"));
                inquiry_res = payaidUtils.doPostHttpUrlConnection(RB.getString("INQUIRY_LIVE_URL"), reqestData);

            }
            transactionlogger.error("inquiry response  -- >" + inquiry_res);


            if (functions.isValueNull(inquiry_res) && inquiry_res.contains("{"))
            {
                String error_desc = "";
                String response_code = "";
                String authorization_staus = "";
                String responseCode = "";
                String transactionId = "";
                String txnType = "";
                String approvedAmount = "";
                String currency = "";
                String dateTime = "";
                String Description = "";
                String response_message = "";
                String payId = "";
                String orderId = "";
                String resNotFoundRemark = "";
                String resString = "";
                String amount = "";
                String refund_amount, transaction_amount,refund_id,refund_reference_no="";


                //JSONArray responseArray = new JSONArray(inquiry_res);
                //transactionlogger.error("array length  is -->" + responseArray.length());
                //for (int i = 0; i < responseArray.length(); i++)
                {
                    //  resString = responseArray.getString(i);
                    transactionlogger.error("resString for "+trackingId+" -->" + resString);
                    JSONObject jsonObject = new JSONObject(resString);

                    if (jsonObject != null)
                    {
                        if (jsonObject.has("transaction_id"))
                        {
                            transaction_id = jsonObject.getString("transaction_id");
                            transactionlogger.error("transaction_id" + transaction_id);
                        }
                        if (jsonObject.has("merchant_order_id"))
                        {
                            merchant_order_id = jsonObject.getString("merchant_order_id");
                            transactionlogger.error("merchant_order_id" + merchant_order_id);
                        }
                        if (jsonObject.has("refund_amount"))
                        {
                            refund_amount = jsonObject.getString("refund_amount");
                        }
                        if (jsonObject.has("transaction_amount"))
                        {
                            transaction_amount = jsonObject.getString("transaction_amount");
                        }
                        if (jsonObject.has("refund_id"))
                        {
                            refund_id = jsonObject.getString("refund_id");
                        }
                        if (jsonObject.has("refund_reference_no"))
                        {
                            refund_reference_no = jsonObject.getString("refund_reference_no");
                        }

                        else if (response_code.equals("0"))
                        {
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescription("success");
                            commResponseVO.setRemark(error_desc);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setTransactionId(transaction_id);
                            commResponseVO.setErrorCode(response_code);
                            //qikPayUtils.updateMainTableEntry(status,trackingId);
                        }


                        else if (response_code.equals("1006"))
                        {
                            commResponseVO.setStatus("pending");
                            commResponseVO.setDescription("pending");
                            commResponseVO.setRemark(error_desc);
                            commResponseVO.setAmount(approvedAmount);
                            commResponseVO.setErrorCode(response_code);
                            //qikPayUtils.updateMainTableEntry(status,trackingId);
                        }
                        //todo need to check failed status
                        else if (response_code.equals("1009")||response_code.equals("1000") || response_code.equals("1014") || response_code.equals("1015") ||response_code.equals("1023")||response_code.equals("1024")||response_code.equals("9999")||response_code.equals("1025")|| response_code.equals("1026") || response_code.equals("1027") || response_code.equals("1029") || response_code.equals("1042") || response_code.equals("1043") || response_code.equals("1072") || response_code.equals("1073") || response_code.equals("1073"))
                        {
                            commResponseVO.setStatus("Failed");
                            commResponseVO.setDescription("Failed");
                            commResponseVO.setRemark(error_desc+response_message);
                            commResponseVO.setTransactionStatus("Failed");
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setErrorCode(response_code);

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
            }
            // no response set pending
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
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
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
        String merchant_reference_number        = trackingId;
        String Amount        = commTransactionDetailsVO.getAmount();
        String beneficiaryName  = "";
        String accountNumber    = "";
        String type             = "";
        String remittanceAmount = "";
        String hash = "";
        String IFSCCode         = "";
        String MobileNumber     = "9999999999";
        String api_key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String salt = gatewayAccount.getFRAUD_FTP_PASSWORD();
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

       //     String hash_data = salt + "|" + api_key + "|" + order_id;
         //   hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
         //   reqestData = "{\"api_key\": \"" + api_key + "\", \"order_id\": \"" + order_id + "\", \"hash\": \"" + hash + "\"}";

        //    jsonObject.put("hash",HASH);
       //     jsonObject.put("api_token",api_token);
            jsonObject.put("RemittanceAmount",remittanceAmount);
            jsonObject.put("Bank",bankName);
            jsonObject.put("MobileNumber",MobileNumber);
            jsonObject.put("IFSCCode",IFSCCode);
            jsonObject.put("AccountNumber",accountNumber);
            jsonObject.put("BeneficiaryName",bankName);
       //     jsonObject.put("client_id",Client_id);
            jsonObject.put("type",type);

            transactionlogger.error("imoneyPayFinalRequest hostURL------> "+hostURL);
     //       transactionlogger.error("imoneyPayFinalRequest strForHash------> "+strForHash);
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




    public static void main(String[] args)
    {
        PayaidPaymentProcess payaidPaymentProcess = new PayaidPaymentProcess();
        PayaidPaymentUtils payaidPaymentUtils=new PayaidPaymentUtils();
        try
        {
            String api_key = "87d8645f-9067-47e7-9477-5faa63beba80";  //paying test
            String salt = "f88023ff4de313d84a849f7deee3e0fd80d78d4f";//paying test
           //  api_key = "87d8645f-9067-47e7-9477-5faa63beba80";  //payout
           //  salt = "f88023ff4de313d84a849f7deee3e0fd80d78d4f";//payout
            String country = "IND";
            String description = "cgcgvvgh";
            String currency = "INR";
            String order_id = "cgcgvvgh";
            String amount = "10.00";
            String email = "dev.p@paymentz.com";
            String name = "Smarita";
            String zip_code = "412345";
            String city = "Mumbai";
            String bank_code = "VISD";
            String phone = "1234567890";
            String return_url = "https://test.dsgintl.net/transaction/QikPayFrontEndServlet?trackingId";
            String PAYMENT_TYPE = "UPI";
            String card_holder_name = "";
            String cvv = "";
            String expiry_date = "";
            String card_number = "";
            String payer_virtual_address = "";
            String hash_data = "";

            HashMap<String, String> requestMap = new HashMap();
            //String [] hash_columns = {"address_line_1", "address_line_2", "amount", "api_key", "city", "country", "currency", "description", "email", "mode", "name", "order_id", "phone", "return_url", "state", "udf1", "udf2", "udf3", "udf4", "udf5", "zip_code"};

            requestMap.put("amount", amount);
            requestMap.put("api_key", api_key);
            requestMap.put("city", city);
            requestMap.put("country", country);
            requestMap.put("currency", currency);
            requestMap.put("description", description);
            requestMap.put("email", email);
            requestMap.put("name", name);
            requestMap.put("order_id", order_id);
            requestMap.put("phone", phone);
            requestMap.put("return_url", return_url);
            requestMap.put("zip_code", zip_code);
            //  requestMap.put("card_holder_name", card_holder_name);

            if ("DC".equalsIgnoreCase(PAYMENT_TYPE))
            {
                card_holder_name = "dev pandey";
                cvv = "123";
                expiry_date = "12/2023";
                card_number = "4444333322221111";
                requestMap.put("card_holder_name", card_holder_name);
                requestMap.put("card_number", card_number);
                requestMap.put("expiry_date", expiry_date);
                requestMap.put("cvv", cvv);
                requestMap.put("bank_code", bank_code);

            }
            else if ("NB".equalsIgnoreCase(PAYMENT_TYPE))
            {
                bank_code = "KKBN";
                requestMap.put("bank_code", bank_code);

            }
            else if ("UPI".equalsIgnoreCase(PAYMENT_TYPE))
            {
                // payer_virtual_address = commRequestVO.getCustomerId();
                payer_virtual_address = "8169642947@apl";
                bank_code = "UPIU";
                requestMap.put("bank_code", bank_code);
                requestMap.put("payer_virtual_address", payer_virtual_address);
            }
            String[] hash_columns = {amount, api_key, bank_code, card_holder_name, card_number, city, country, currency, cvv, description, email, expiry_date, name, order_id, payer_virtual_address, phone, return_url, zip_code};

            hash_data = salt;

            for (int i = 0; i < hash_columns.length; i++)
            {
                if (hash_columns[i].length() > 0)
                {
                    hash_data += '|' + hash_columns[i];
                }

            }
            // hash_data = salt + "|" + amount + "|" + api_key + "|"+bank_code+"|"+card_holder_name+"|"+card_number+"|"+city +"|" +country + "|"+currency + "|"+cvv+"|"+description+"|" + email + "|"+expiry_date+"|" + name + "|" + order_id + "|" + phone +"|" + payer_virtual_address +  "|"+ return_url + "|" + zip_code;
            System.out.println("hash_data-->" + hash_data);

            String hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            requestMap.put("hash", hash);

            String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" + " https://mystore.payaidpayments.com/v2/paymentseamlessrequest" + "\">";
            Iterator keys = requestMap.keySet().iterator();
            while (keys.hasNext())
            {
                String key = (String) keys.next();
                form += "<input type=\"hidden\" name=\"" + key + "\"  value=\"" + requestMap.get(key) + "\">";

            }
            form += "</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";
            System.out.println("PayAidPaymentProcess Form---" + form.toString());

            String order_id2 = "25728008";

            String hash_data2 = salt + "|" + api_key + "|" + order_id;
            String hash2 = PayaidPaymentUtils.getHashCodeFromString(hash_data2);
            String reqestData = "{\"api_key\": \"" + api_key + "\", \"order_id\": \"" + order_id2 + "\", \"hash\": \"" + hash2 + "\"}";

            PayaidPaymentUtils payaidUtils = new PayaidPaymentUtils();

            String inquiry_res = payaidUtils.doPostHttpUrlConnection("https://mystore.payaidpayments.com/v2/paymentstatus", reqestData);
            System.out.println("inq" +inquiry_res);

            String resString = "";
            JSONObject jsonObject2 = new JSONObject(inquiry_res);
            jsonObject2.getString("data");
            System.out.println("data-->" +jsonObject2 );
            JSONArray responseArray = jsonObject2.getJSONArray("data");

            System.out.println("array length  is -->" + responseArray.length());
            for (int i = 0; i < responseArray.length(); i++)
            {
                resString = responseArray.getString(i);

            }
            System.out.println("resString for 2 -->" + resString);
            JSONObject jsonObject = new JSONObject(resString);
            jsonObject.getString("transaction_id");
            System.out.println("transaction_id-->" + jsonObject.getString("transaction_id"));

            //payout
             hash_data = "";
          /*  String bankName = "BOI";
            String accountNumber = "348760968684";
            String IFSCCode = "BARB0CHAKAL";
            String merchant_reference_number = "3243434";
            String type = "IMPS";
            String url = "https://mystore.payaidpayments.com/v3/fundtransfer";
             hash_data=salt+"|"+amount+"|"+bankName+"|"+accountNumber+"|"+api_key+"|"+bankName+"|"+bankName+"|"+IFSCCode +"|" +merchant_reference_number+"|"+type ;


            String hash = PayaidPaymentUtils.getHashCodeFromString(hash_data);
            String reqestData ="{\n" +
                    "   \"payOutBean\":{\n" +
                    "      \"Amount\":\""+amount+"\",\n" +
                    "      \"account_name\":\""+bankName+"\",\n" +
                    "      \"account_number \":\""+accountNumber +"\",\n" +
                    "      \"api_key\":\""+api_key+"\",\n" +
                    "      \"bank_branch\":\""+bankName+"\",\n" +
                    "      \"bank_name\":\""+bankName+"\",\n" +
                    "      \"Hash\":\""+hash+"\",\n" +
                    "      \"merchant_reference_number\":\""+merchant_reference_number+"\",\n" +
                    "       \"transfer_type\":\""+type+"\",\n" +
                    "      \"ifsc_code\":\""+IFSCCode+"\",\n" +
                    "   }\n" +
                    "}";

         String   inquiry_res = payaidPaymentUtils.doPostHttpUrlConnection(url, reqestData);
            System.out.println("inquiry_res-->"+inquiry_res);
*/
        }
        catch (Exception e)
        {
          //  transactionlogger.error("JSONException-->", e);


        }
    }
    }

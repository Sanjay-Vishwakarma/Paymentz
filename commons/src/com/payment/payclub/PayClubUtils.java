package com.payment.payclub;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 19-Jul-18.
 */
public class PayClubUtils
{
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(PayClubUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayClubUtils.class.getName());

    public static String sendPOST(String POST_URL,String POST_PARAMS)
    {
        transactionLogger.debug("Inside sendPOST ---");
        StringBuffer response = new StringBuffer();
        try
        {
            URL obj = new URL(POST_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            System.setProperty("https.protocols", "TLSv1.2");
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
                in.close();
            }
            else
            {
                transactionLogger.debug("POST request not worked");
            }
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException---->",e);
        }
        return response.toString();
    }

    public Comm3DResponseVO readSalesJsonResponse(String jsonResponseString)throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside readSalesJsonResponse in PayClubUtils---");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        try
        {
            JSONObject jObject = new JSONObject(jsonResponseString);

            String transactionNum   = "";
            String signMsg          = "";
            String remark           = "";
            String mid              = "";
            String currency         = "";
            String responceCode     = "";
            String orderNumber      = "";
            String amount           = "";
            String payResult        = "";
            String payInfo          = "";
            String p_billAddress    = "";

            if(jObject.has("p_trans_num")){
                transactionNum=jObject.getString("p_trans_num");
            }
            if(jObject.has("p_signmsg")){
                signMsg=jObject.getString("p_signmsg");
            }
            if(jObject.has("p_remark")){
                remark=jObject.getString("p_remark");
            }
            if(jObject.has("p_mid")){
                mid=jObject.getString("p_mid");
            }
            if(jObject.has("p_currency")){
                currency=jObject.getString("p_currency");
            }
            if(jObject.has("p_responseCode")){
                responceCode=jObject.getString("p_responseCode");
            }
            if(jObject.has("p_order_num")){
                orderNumber=jObject.getString("p_order_num");
            }
            if(jObject.has("p_amount")){
                amount=jObject.getString("p_amount");
            }
            if(jObject.has("p_pay_result")){
                payResult=jObject.getString("p_pay_result");
            }
            if(jObject.has("p_pay_info")){
                payInfo=jObject.getString("p_pay_info");
            }
            if(jObject.has("p_billAddress")){
                p_billAddress=jObject.getString("p_billAddress");
            }

            transactionLogger.debug("p_trans_num ---"+transactionNum);
            transactionLogger.debug("p_signmsg ---"+signMsg);
            transactionLogger.debug("p_remark ---"+remark);
            transactionLogger.debug("p_mid ---"+mid);
            transactionLogger.debug("p_currency ---"+currency);
            transactionLogger.debug("p_responseCode ---"+responceCode);
            transactionLogger.debug("p_order_num ---"+orderNumber);
            transactionLogger.debug("p_amount ---"+amount);
            transactionLogger.debug("p_pay_result ---"+payResult);
            transactionLogger.debug("p_pay_info ---"+payInfo);
            transactionLogger.error("p_billAddress(descriptor) ---"+ p_billAddress);


            if(payResult.equals("1"))
            {
                commResponseVO.setStatus("success");
            }
            else if(payResult.equals("0"))
            {
                commResponseVO.setStatus("fail");
            }
            else if(payResult.equals("-1"))
            {
                commResponseVO.setStatus("Processing");
            }

            commResponseVO.setTransactionId(transactionNum);
            commResponseVO.setRemark(payInfo);
            commResponseVO.setDescription(payInfo);
            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(currency);
            commResponseVO.setMerchantId(mid);
            commResponseVO.setDescriptor(p_billAddress);
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayClubUtils.java","getPayClubResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public CommResponseVO readRefundJsonResponse(String jsonResponseString)throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside readRefundJsonResponse in PayClubUtils ---");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            JSONObject jObject = new JSONObject(jsonResponseString);

            String mid              = "";
            String transactionNum   = "";
            String orderNo          = "";
            String currency         = "";
            String amount           = "";
            String errorCode        = "";
            String refundTime       = "";
            String description      = "";


            if(jObject.has("p_mid"))
            {
                mid = jObject.getString("p_mid");
            }
            if(jObject.has("p_trans_num"))
            {
                transactionNum = jObject.getString("p_trans_num");
            }
            if(jObject.has("p_order_num"))
            {
                orderNo = jObject.getString("p_order_num");
            }
            if(jObject.has("p_currency"))
            {
                currency = jObject.getString("p_currency");
            }
            if(jObject.has("p_amount"))
            {
                amount = jObject.getString("p_amount");
            }
            if(jObject.has("p_code"))
            {
                errorCode = jObject.getString("p_code");
            }
            if(jObject.has("p_trans_date"))
            {
                refundTime = jObject.getString("p_trans_date");
            }
            if(jObject.has("p_desc"))
            {
                description = jObject.getString("p_desc");
            }

            transactionLogger.error("p_mid ---" + mid);
            transactionLogger.error("p_trans_num ---" + transactionNum);
            transactionLogger.error("orderNo ---" + orderNo);
            transactionLogger.error("currency ---" + currency);
            transactionLogger.error("amount ---" + amount);
            transactionLogger.error("errorCode ---" + errorCode);
            transactionLogger.error("refundTime ---" + refundTime);
            transactionLogger.error("description ---" + description);

            if(errorCode.equals("00"))
            {
                commResponseVO.setStatus("Success");
                commResponseVO.setDescription("Success");
                commResponseVO.setTransactionStatus("Success");
            }
//
//            else if(errorCode.equals("24"))
//            {
//                commResponseVO.setStatus("pending");
//                commResponseVO.setTransactionStatus("pending");
//            }
            else if(errorCode.equals("13"))
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setTransactionStatus("Failed");
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }

            commResponseVO.setMerchantId(mid);
            commResponseVO.setTransactionId(transactionNum);
            commResponseVO.setDescription(description);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setBankTransactionDate(refundTime);
            commResponseVO.setAmount(amount);
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayClubUtils.java","getPayClubResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public CommResponseVO readInquiryJsonResponse(String jsonResponseString)throws PZTechnicalViolationException
    {
        transactionLogger.debug("Inside readInquiryJsonResponse in PayClubUtils ---");
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        try
        {
            JSONObject jObject = new JSONObject(jsonResponseString);

            String desc             = "";
            String mid              = "";
            String accountNumber    = "";
            String orderNumber      = "";
            String transactionNum   = "";
            String currency         = "";
            String amount           = "";
            String queryResult      = "";
            String transDate        = "";
            String p_billAddress    = "";

            if(jObject.has("p_desc")){
                desc=jObject.getString("p_desc");   // only comes when p_query_result=2
            }
            if(jObject.has("p_mid")){
                mid=jObject.getString("p_mid");
            }
            if(jObject.has("p_account_num")){
                accountNumber=jObject.getString("p_account_num");
            }
            if(jObject.has("p_order_num")){
                orderNumber=jObject.getString("p_order_num");
            }
            if(jObject.has("p_trans_num")){
                transactionNum=jObject.getString("p_trans_num");
            }
            if(jObject.has("p_currency")){
                currency=jObject.getString("p_currency");
            }
            if(jObject.has("p_amount")){
                amount=jObject.getString("p_amount");
            }
            if(jObject.has("p_query_result")){
                queryResult=jObject.getString("p_query_result");
            }
            if(jObject.has("p_trans_date")){
                transDate=jObject.getString("p_trans_date");
            }
            if(jObject.has("p_billAddress")){
                p_billAddress=jObject.getString("p_billAddress");
            }

            transactionLogger.error("p_desc ---"+desc);
            transactionLogger.error("p_mid ---"+mid);
            transactionLogger.error("p_account_num ---"+accountNumber);
            transactionLogger.error("p_order_num ---"+orderNumber);
            transactionLogger.error("p_trans_num ---" + transactionNum);
            transactionLogger.error("p_currency ---"+currency);
            transactionLogger.error("p_amount ---"+amount);
            transactionLogger.error("p_query_result ---"+queryResult);
            transactionLogger.error("p_billAddress ---"+p_billAddress);

            if(queryResult.equals("1"))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setRemark("-");  // no remark from query response in success case
                commResponseVO.setDescription("-"); // no description from query responce in success case
                commResponseVO.setDescriptor(p_billAddress);
            }
            else if(queryResult.equals("0") || queryResult.equals("2"))
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setTransactionStatus("fail");
                commResponseVO.setRemark(desc);
                commResponseVO.setDescription(desc);
            }
            else if(queryResult.equals("-1"))
            {
                commResponseVO.setStatus("Processing");
                commResponseVO.setTransactionStatus("Processing");
            }
            if(functions.isValueNull(transDate))
                commResponseVO.setBankTransactionDate(transDate);
            else
                commResponseVO.setBankTransactionDate("-");
            commResponseVO.setMerchantId(mid);
            commResponseVO.setTransactionId(transactionNum);
            commResponseVO.setCurrency(currency);
            commResponseVO.setAmount(amount);
            commResponseVO.setAuthCode(queryResult);
            commResponseVO.setTransactionType("Inquiry");
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayClubUtils.java","getPayClubResponseVOInquiry()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }

        return commResponseVO;
    }

    public static String SHA256forSales(String MERCHANT_ID, String ACCOUNT_NUMBER, String ORDER_NO, String T_CURRENCY, String T_AMOUNT,String SIGN_KEY) throws PZTechnicalViolationException
    {
        String sha = MERCHANT_ID.trim() + ACCOUNT_NUMBER.trim() + ORDER_NO.trim() + T_CURRENCY.trim() + T_AMOUNT.trim()+ SIGN_KEY;
        sha.trim();
        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest    = MessageDigest.getInstance("SHA-256");
            byte[] hash             = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    public static String SHA256forRefund(String MERCHANT_ID, String ACCOUNT_NUMBER, String ORDERNUMBER, String REFUND_TYPE, String SIGN_KEY) throws PZTechnicalViolationException
    {
        String sha = MERCHANT_ID.trim() + ACCOUNT_NUMBER.trim() + ORDERNUMBER.trim() + REFUND_TYPE.trim() + SIGN_KEY;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest    = MessageDigest.getInstance("SHA-256");
            byte[] hash             = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubPaymentGateway.class.getName(), "SHA256forRefund()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static String SHA256forInquiry(String MERCHANT_ID, String ACCOUNT_NUMBER,String SIGN_KEY) throws PZTechnicalViolationException
    {
        String sha = MERCHANT_ID.trim() + ACCOUNT_NUMBER.trim() + SIGN_KEY;
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubUtils.class.getName(), "SHA256forInquiry()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PayClubUtils.class.getName(), "SHA256forInquiry()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return hexString.toString().toLowerCase().trim();
    }

    public static boolean getAmountComparison(String refundAmount, String previousTransactionAmountAmount)
    {
        boolean result  = false;
        float rfAmount  = Float.valueOf(refundAmount);
        float previousAmount = Float.valueOf(previousTransactionAmountAmount);
        if (rfAmount == previousAmount)
        {
            result = true;
        }
        return result;
    }

    public static CommRequestVO getpayclubRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());


        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());

        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());


        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }

    public static void main(String[] args)
    {
        PayClubUtils payClubUtils=new PayClubUtils();
        try
        {
            System.out.println("Hash-->"+payClubUtils.SHA256forSales("81094", "40001800", "14823", "USD", "1.00", "0fb80L6P0j80v22"));
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
    }


}



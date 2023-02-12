package com.payment.paygsmile;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Created by Admin on 2022-01-22.
 */
public class PayGSmileGatewayUtils
{

    private static TransactionLogger transactionLogger  = new TransactionLogger(PayGSmileGatewayUtils.class.getName());
    static Functions functions                          = new Functions();
    private final static String charset                 = "UTF-8";

    private static Stack<MessageDigest> stack       = new Stack<MessageDigest>();
    private final static String separator           = "&";
    private final static String equator             = "=";
    private final static String hashingAlgo         = "SHA-256";
    private static final Charset ASCII              = Charset.forName("UTF-8");


    public static String getAmount(String amount)
    {
        double amt          = Double.parseDouble(amount);
        double roundOff     = Math.round(amt);
        int value           = (int) roundOff;
        amount              = String.valueOf(value);
        return amount.toString();
    }

    public static void updateMainTableEntry(String transactionId, String transaction_mode, String trackingid)
    {
        Connection connection = null;
        try
        {
            String addParam="";
            if(functions.isValueNull(transaction_mode)){
               addParam=",transaction_mode='"+transaction_mode+"'";
            }
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid='"+transactionId+"' "+addParam+" WHERE trackingid="+trackingid;
            Database.executeUpdate(updateQuery1, connection);
        }

        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static String doPostHTTPSURLConnection(String REQUEST_URL,String request, String encodedCredentials) throws PZTechnicalViolationException
    {
        HttpClient httpClient   =   new HttpClient();
        PostMethod postMethod   =   new PostMethod(REQUEST_URL);

        postMethod.addRequestHeader("content-type", "application/json");
        postMethod.addRequestHeader("Authorization", "Basic "+encodedCredentials);

        postMethod.setRequestBody(request);

        String body = "";
        try{
            httpClient.executeMethod(postMethod);
            body    =  new String(postMethod.getResponseBody());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return body;
    }

    public CommRequestVO getOmniPayPaymentRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO                 = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO       = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO     = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO             = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO                   =new CommMerchantVO();
        CommDeviceDetailsVO commDeviceDetailsVO         = new CommDeviceDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);
        return commRequestVO;
    }

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("167".equalsIgnoreCase(paymentMode))
            payBrand = "PicPay";
        if("169".equalsIgnoreCase(paymentMode))
            payBrand = "PIX";
        if("176".equalsIgnoreCase(paymentMode))
            payBrand = "AME";
        if("177".equalsIgnoreCase(paymentMode))
            payBrand = "Paypal";
        if("178".equalsIgnoreCase(paymentMode))
            payBrand = "Todito";
        if("179".equalsIgnoreCase(paymentMode))
            payBrand = "TPaga";
        if("180".equalsIgnoreCase(paymentMode))
            payBrand = "Mach";
        if("181".equalsIgnoreCase(paymentMode))
            payBrand = "Vita";
        if("182".equalsIgnoreCase(paymentMode))
            payBrand = "DepositExpress";
        if("183".equalsIgnoreCase(paymentMode))
            payBrand = "SPEI";
        if("184".equalsIgnoreCase(paymentMode))
            payBrand = "CoDi";
        if("185".equalsIgnoreCase(paymentMode))
            payBrand = "PSE";
        if("186".equalsIgnoreCase(paymentMode))
            payBrand = "Khipu";
        if("187".equalsIgnoreCase(paymentMode))
            payBrand = "Pagpeffectivo";
        if("188".equalsIgnoreCase(paymentMode))
            payBrand = "Efecty";
        if("189".equalsIgnoreCase(paymentMode))
            payBrand = "SuRed";
        if("190".equalsIgnoreCase(paymentMode))
            payBrand = "Gana";
        if("194".equalsIgnoreCase(paymentMode))
            payBrand = "BankTransfer";
        if("195".equalsIgnoreCase(paymentMode))
            payBrand = "Rapipago";
        if("196".equalsIgnoreCase(paymentMode))
            payBrand = "PagoFacil";
        if("197".equalsIgnoreCase(paymentMode))
            payBrand = "Baloto";
        if("198".equalsIgnoreCase(paymentMode))
            payBrand = "Lottery";
        if("199".equalsIgnoreCase(paymentMode))
            payBrand = "Cash";
        if("200".equalsIgnoreCase(paymentMode))
            payBrand = "OXXO";
        if("201".equalsIgnoreCase(paymentMode))
            payBrand = "OXXOPay";
        if("202".equalsIgnoreCase(paymentMode))
            payBrand = "Pago46";
        if("168".equalsIgnoreCase(paymentMode))
            payBrand = "Boleto";
        return payBrand;
    }

    public static String getSign(Map<String, String> params, String authKey) {
        String param = sortParam(params) + authKey;
        transactionLogger.error("param >>>>>>>> "+param);
        return sha256(param);
    }

    public static String sha256(String str) {
        String encodeStr = "";
        try {
            MessageDigest digest    = MessageDigest.getInstance(hashingAlgo);
            byte[] encodedhash      = digest.digest(str.getBytes(ASCII));
            encodeStr               = bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("algorithm not supported");
        }
        return encodeStr;
    }

    public static String sortParam(Map<String, String> params) {
        try {
            Map<String, String> map = new TreeMap<>(params);

            StringBuilder sb = new StringBuilder();
            for (String k : map.keySet()) {
                String v = map.get(k);
                if (v != null && v.length() > 0) {
                    sb.append(k).append(equator).append(v).append(separator);
                }
            }

            if (sb.length() <= 0) {
                return "";
            }

            return sb.subSequence(0, sb.length() - 1).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String doPayoutPostHTTPSURLConnection(String AppId, String authorization, String REQUEST_URL,String request) throws PZTechnicalViolationException
    {
        HttpClient httpClient   =   new HttpClient();
        PostMethod postMethod   =   new PostMethod(REQUEST_URL);

        postMethod.addRequestHeader("content-type", "application/json");
        postMethod.addRequestHeader("AppId", AppId);
        postMethod.addRequestHeader("Authorization", authorization);

        postMethod.setRequestBody(request);

        String body = "";
        try{
            httpClient.executeMethod(postMethod);
            body    =  new String(postMethod.getResponseBody());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return body;
    }

    public static void updatePaymentId(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            transactionLogger.error("payg updateOrderid--->"+ps2);

            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static boolean  isFailed(String code){
        boolean isFailed           = false;
        ArrayList<String> codeList = new ArrayList<>();

        codeList.add("400");
        codeList.add("4004103");
        codeList.add("4001001");
        codeList.add("5001102");
        codeList.add("4001000");
        codeList.add("4004003");
        codeList.add("5001000");
        codeList.add("4001003");

        if(codeList.contains(code)){
            isFailed = true;
        }
        return  isFailed;
    }

    public static boolean  isCartTypeForBankTransfer(String method,String payment_brand){
        boolean isFailed                    = false;

        if(method.equalsIgnoreCase("BANKTRANSFER") && payment_brand.equalsIgnoreCase("BankTransfer") ){
            isFailed = true;
        }else if(method.equalsIgnoreCase("BANKTRANSFER") && payment_brand.equalsIgnoreCase("PSE") ){
            isFailed = true;
        }else if(method.equalsIgnoreCase("BANKTRANSFER") && payment_brand.equalsIgnoreCase("DepositExpress") ){
            isFailed = true;
        }
        return  isFailed;
    }




}

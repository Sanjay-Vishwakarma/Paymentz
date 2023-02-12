package com.payment.LetzPay;

import com.directi.pg.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Base64;

/**
 * Created by Vivek on 3/16/2021.
 */
public class LetzPayUtils
{
    private static LetzPayGatewayLogger transactionLogger=new LetzPayGatewayLogger(LetzPayUtils.class.getName());
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }
    public static String convertMapToSpring(TreeMap<String,String> map,String salt,String hash)
    {
        Functions functions=new Functions();
        StringBuffer s=new StringBuffer();
        int i=1;
        for (Map.Entry entry:map.entrySet())
        {
            String key= (String) entry.getKey();
            String value= (String) entry.getValue();
            if(i!=map.size())
                s.append(key+"="+value+"~");
            else
                s.append(key+"="+value);
            i++;

        }
        if(functions.isValueNull(salt))
            s.append(salt);
        if(functions.isValueNull(hash))
            s.append("~HASH="+hash);
        return s.toString();
    }

    public static String getHash(String input) throws PZTechnicalViolationException
    {
        String response = null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input.getBytes());

            response = new String(Hex.encodeHex(messageDigest.digest()));
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException --->",e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "getHash()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return response.toUpperCase().substring(0,32);
    }
    public static String hashSHA256(String plainText) throws PZTechnicalViolationException
    {
        StringBuffer cipherText=new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainText.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) cipherText.append('0');
                cipherText.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException --->",e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "hashSHA256()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException --->",e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "hashSHA256()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return cipherText.toString().toUpperCase();
    }
    public static String encryptAES(String message, String key) throws PZTechnicalViolationException
    {
        try
        {
            Key keyObj = new SecretKeySpec(key.getBytes(), "AES");;
            String ivString=key.substring(0,16);
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES" + "/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

            byte[] encValue = cipher.doFinal(message.getBytes("UTF-8"));

            Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();
            String base64EncodedData = base64Encoder.encodeToString(encValue);
            return base64EncodedData;
        }
        catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.ILLEGAL_BLOCK_SIZE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            transactionLogger.error("InvalidAlgorithmParameterException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return "";

    }
    public static String decryptAES(String data,String key) throws PZTechnicalViolationException
    {
        try {
            String ivString=key.substring(0,16);
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("UTF-8"));
            Key keyObj = new SecretKeySpec(key.getBytes(), "AES");;
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);
            byte[] decodedData = Base64.getDecoder().decode(data);
            byte[] decValue = cipher.doFinal(decodedData);

            return new String(decValue);

        } catch (NoSuchPaddingException e)
        {
            transactionLogger.error("NoSuchPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.ILLEGAL_BLOCK_SIZE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            transactionLogger.error("InvalidAlgorithmParameterException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return "";
    }
    public CommRequestVO getCommRequestVO(CommonValidatorVO commonValidatorVO)
    {
        Functions functions=new Functions();
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        transDetailsVO.setVpaAddress(commonValidatorVO.getVpa_address());
        transactionLogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public static HashMap<String,String> getHashMap(String response,String key) throws PZTechnicalViolationException
    {
        HashMap<String, String> hashMap = new HashMap<>();
            key = getHash(key);
            response = decryptAES(response, key);
            transactionLogger.error("response--->" + response);
            if (response.contains("~"))
            {
                String[] res = response.split("~");
                for (String s : res)
                {
                    if (s.contains("="))
                    {
                        String[] field = s.split("=");
                        if (field.length == 2)
                        {
                            hashMap.put(field[0], field[1]);
                        }
                        else if (field.length == 1)
                        {
                            hashMap.put(field[0], "");
                        }
                    }
                }
            }
        return hashMap;
    }
    public static Boolean updateTransaction (String trackingid, String  customerId ){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
            transactionLogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("systemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;
    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException --->", he);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io){
            transactionLogger.error("IOException --->", io);
            PZExceptionHandler.raiseTechnicalViolationException(LetzPayUtils.class.getName(), "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static HashMap<String,String> getTransactionDetails(String trackingId) throws PZDBViolationException
    {
        HashMap<String,String> hashMap=new HashMap<>();
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;

        try{
            con=Database.getConnection();
            String query="select bankaccount,amount from transaction_safexpay_details where trackingId=?";
            ps=con.prepareStatement(query);
            ps.setString(1,trackingId);
            rs=ps.executeQuery();
            while (rs.next())
            {
                hashMap.put("bankaccount",rs.getString("bankaccount"));
                hashMap.put("amount",rs.getString("amount"));
            }
        }catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return hashMap;
    }
}

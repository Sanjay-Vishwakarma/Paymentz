package com.payment.apexpay;

import com.directi.pg.Database;
import com.directi.pg.LoadProperties;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Admin on 3/8/2021.
 */
public class ApexPayUtils
{

    private final static ResourceBundle BPBANKSID           = LoadProperties.getProperty("com.directi.pg.IMBANKS");
    private static TransactionLogger transactionlogger      = new TransactionLogger(ApexPayUtils.class.getName());

    private static Stack<MessageDigest> stack       = new Stack<MessageDigest>();
    private final static String separator           = "~";
    private final static String equator             = "=";
    private final static String hashingAlgo         = "SHA-256";
    private static final Charset ASCII              = Charset.forName("UTF-8");

    // Hash calculation from request map
    public static String generateCheckSum(Map<String, String> parameters, String secretKey) throws NoSuchAlgorithmException
    {

        Map<String, String> treeMap = new TreeMap<String, String>(parameters);
        StringBuilder allFields     = new StringBuilder();

        for (String key : treeMap.keySet()) {

            allFields.append(separator);
            allFields.append(key);
            allFields.append(equator);
            allFields.append(treeMap.get(key));
        }

        allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
        allFields.append(secretKey);
        transactionlogger.error("generateCheckSum ----->> " + allFields.toString());
        return getHash(allFields.toString()).toUpperCase();
    }

    public static String generateCheckSum2(Map<String, String> parameters, String secretKey)
            throws NoSuchAlgorithmException {
        Map<String, String> treeMap = new TreeMap<String, String>(parameters);

        StringBuilder allFields = new StringBuilder();
        for (String key : treeMap.keySet()) {
            allFields.append(separator);
            allFields.append(key);
            allFields.append(equator);
            allFields.append(treeMap.get(key));

        }

        allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
        allFields.append(separator);
        allFields.append("HASH");
        allFields.append(equator);
        allFields.append(secretKey);

        transactionlogger.error("allFieldsWithHASH ---> "+allFields);
        return allFields.toString();
    }


    public static String generateAutoSubmitForm(String actionUrl,String msgData,String paymentMethod)
    {
        //transactionlogger.error("urll---" + actionUrl);

        String hiddenValue = "";
        if(paymentMethod.equalsIgnoreCase("UP"))
            hiddenValue = "<input type='hidden' name='txtPayCategory' value='UPI'>";

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +actionUrl+ "\">" +
                "<input type=hidden name=msg id=msg value=\""+msgData+"\">"+
                hiddenValue+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        return form.toString();
    }
    public CommRequestVO getApexPayRequestVO(CommonValidatorVO commonValidatorVO)
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
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String getBankName(String BankCode ){
        String BankName="";
        try
        {
            BankName = BPBANKSID.getString(BankCode);
        }catch (Exception e)
        {
            transactionlogger.error("Exception--->",e);
        }

        return BankName;
    }


    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "CARD";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "CARD";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "NB";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "WL";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UP";

        return payMode;
    }


    public String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VI";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MS";
        if("23".equalsIgnoreCase(paymentMode))
            payBrand = "RU";
        if("37".equalsIgnoreCase(paymentMode))
            payBrand = "MS";
       return payBrand;
    }

    // Response hash validation
    public static boolean validateResponseChecksum(Map<String, String> responseParameters, String secretKey,
                                                   String responseHash) throws NoSuchAlgorithmException {
        boolean flag = false;
        String generatedHash = generateCheckSum(responseParameters, secretKey);
        if (generatedHash.equals(responseHash)) {
            flag = true;
        }
        return flag;
    }

    // Generate hash from the supplied string
    public static String getHash(String input) throws NoSuchAlgorithmException {
    String response = null;

    MessageDigest messageDigest = provide();
    messageDigest.update(input.getBytes());
    consume(messageDigest);

    response = new String(Hex.encodeHex(messageDigest.digest()));

    return response.toUpperCase();
}// getSHA256Hex()

    private static MessageDigest provide() throws NoSuchAlgorithmException {
        MessageDigest digest = null;

        try {
            digest = stack.pop();
        } catch (EmptyStackException emptyStackException) {
            digest = MessageDigest.getInstance(hashingAlgo);
        }
        return digest;
    }

    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

    public static String doGetHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("ApexPayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("ApexPayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }



    public void updateMainTableEntry(String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, remark);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }


    public void updateOrderid(String requestorderid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET authorization_code=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, requestorderid);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return isUpdate;

    }

    public void updateRRNMainTableEntry(String RRN, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET rrn=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, RRN);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }

    public static String encryptAES(String message, String key) throws PZTechnicalViolationException
    {
        try
        {
            Key keyObj          = new SecretKeySpec(key.getBytes(), "AES");;
            String ivString     = key.substring(0,16);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
            Cipher cipher       = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

            byte[] encValue                 = cipher.doFinal(message.getBytes("UTF-8"));
            Base64.Encoder base64Encoder    = Base64.getEncoder().withoutPadding();
            String base64EncodedData        = base64Encoder.encodeToString(encValue);
            return base64EncodedData;
        }
        catch (NoSuchPaddingException e)
        {
            transactionlogger.error("NoSuchPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (BadPaddingException e)
        {
            transactionlogger.error("BadPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            transactionlogger.error("IllegalBlockSizeException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.ILLEGAL_BLOCK_SIZE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidKeyException e)
        {
            transactionlogger.error("InvalidKeyException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            transactionlogger.error("InvalidAlgorithmParameterException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "encryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return "";

    }


    public static String decryptAES(String data,String key) throws PZTechnicalViolationException
    {
        try {
            String ivString     = key.substring(0,16);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
            Key keyObj          = new SecretKeySpec(key.getBytes(), "AES");;
            Cipher cipher       = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);

            byte[] decodedData  = Base64.getDecoder().decode(data);
            byte[] decValue     = cipher.doFinal(decodedData);

            return new String(decValue);

        } catch (NoSuchPaddingException e)
        {
            transactionlogger.error("NoSuchPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (BadPaddingException e)
        {
            transactionlogger.error("BadPaddingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.BAD_PADDING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IllegalBlockSizeException e)
        {
            transactionlogger.error("IllegalBlockSizeException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.ILLEGAL_BLOCK_SIZE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidKeyException e)
        {
            transactionlogger.error("InvalidKeyException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.INVALID_KEY_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (InvalidAlgorithmParameterException e)
        {
            transactionlogger.error("InvalidAlgorithmParameterException --->", e);
            PZExceptionHandler.raiseTechnicalViolationException(ApexPayUtils.class.getName(), "decryptAES()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return "";
    }

    // Generate hash from the supplied string
    public static String getPayOutHash(String input) throws NoSuchAlgorithmException {
        String response = null;

        MessageDigest messageDigest = provide();
        messageDigest.update(input.getBytes());
        consume(messageDigest);

        response = new String(Hex.encodeHex(messageDigest.digest()));

        return response;
    }// getSHA256Hex()

}

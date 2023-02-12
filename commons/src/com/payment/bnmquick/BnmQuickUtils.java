package com.payment.bnmquick;

import com.directi.pg.*;
import com.manager.vo.TerminalVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * Created by Admin on 3/8/2021.
 */
public class BnmQuickUtils
{

    private final static ResourceBundle BPBANKSID           = LoadProperties.getProperty("com.directi.pg.PUBANKS");
    private static TransactionLogger transactionlogger      = new TransactionLogger(BnmQuickUtils.class.getName());
    public static final String ALGO                         = "AES";
    private static Stack<MessageDigest> stack       = new Stack<MessageDigest>();
    private final static String HASH_ALGORITHM      = "SHA-512";
    public static final String CHARSETNAME          = "UTF-8";
    Functions functions=new Functions();

    public static String  getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "credit";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DebitCard";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "Netbanking";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UPI";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "Wallet";

        return payMode;
    }


    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VISA";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MAST";
        if("23".equalsIgnoreCase(paymentMode))
            payBrand = "RUPAY";
       return payBrand;
    }


    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }
    public static String getDoubleAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        String amt      = d.format(dObj2);
        return amt;
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


    public static void updateOrderid(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            transactionlogger.error("BnmQuick updateOrderid--->"+ps2);

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

    public CommRequestVO getBnmQuickPaymentRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();
        TerminalVO terminalVO=new TerminalVO();
        terminalVO=commonValidatorVO.getTerminalVO();
        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getTerminalVO().getAutoRedirectRequest())){
        commRequestVO.setAutoRedirectFlag(commonValidatorVO.getTerminalVO().getAutoRedirectRequest());
    }

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        dObj2           = dObj2 * 100;
        String amt      = d.format(dObj2);
        return amt;
    }

    public static String doPostHTTPSURLConnectionClient(String url,String request,String Token) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Token", Token);

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BnmQuickUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BnmQuickUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }


    public void updatePayGTransctionId(String transctionId,String paymentid ,String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET authorization_code=?,paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transctionId);
            ps2.setString(2, paymentid);
            ps2.setString(3, trackingid);
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

    public String encrypt1(String data, String generatedKey, String payId) throws Exception {
        try {

            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance("AES" + "/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

            byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));

            java.util.Base64.Encoder base64Encoder    = java.util.Base64.getEncoder().withoutPadding();
            String base64EncodedData        = base64Encoder.encodeToString(encValue);

            return base64EncodedData;
        } catch (Exception e) {
            throw new Exception("Error during encryption process");
        }
    }

    public String decrypt1(String data,String generatedKey, String payId) throws Exception {
        try {
            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);

            byte[] decodedData  = java.util.Base64.getDecoder().decode(data);
            byte[] decValue     = cipher.doFinal(decodedData);

            return new String(decValue);

        } catch (Exception e) {
            throw new Exception("Error during decryption process");

        }
    }

    public static String getCardNumber(String str,int start,int end){
        String mask= "";
        if(str !=null && !(str.isEmpty()) && (str.length()>=10)) {
            mask=str.substring(start, end);
        }
        return mask;
    }

    public static String  geDummyMobileNo()
    {  String randomMobileNo="";
        int num1, num2, num3; //3 numbers in area code
        int set2, set3; //sequence 2 and 3 of the phone number
        int low = 7;
        int high = 9;

        Random generator = new Random();

        num1 = generator.nextInt(high - low) + low;
        num2 = generator.nextInt(high - low) + low;
        num3 = generator.nextInt(high - low) + low;

        set2 = generator.nextInt(643) + 100;

        set3 = generator.nextInt(8999) + 1000;

        transactionlogger.error(num1 + "" + num2 + "" + num3 + set2 + set3);
        return randomMobileNo =num1 + "" + num2 + "" + num3 + set2 + set3;
    }


}
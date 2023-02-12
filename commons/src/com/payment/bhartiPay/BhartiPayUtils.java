package com.payment.bhartiPay;

import com.directi.pg.*;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpStatus;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;



import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.*;

public class BhartiPayUtils
{
    private final static ResourceBundle BPBANKSID = LoadProperties.getProperty("com.directi.pg.BPBANKSID");
    private static TransactionLogger transactionlogger = new TransactionLogger(BhartiPayUtils.class.getName());

    private static Stack<MessageDigest> stack = new Stack<MessageDigest>();
    private final static String separator = "~";
    private final static String equator = "=";
    private final static String hashingAlgo = "SHA-256";
    private static final Charset ASCII = Charset.forName("UTF-8");

    // Hash calculation from request map
    public static String generateCheckSum(Map<String, String> parameters, String secretKey)
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
        allFields.append(secretKey);

        System.out.println("allFields--->"+allFields);
        return getHash(allFields.toString());
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
        //allFields.append(secretKey);
        allFields.append(separator);
        allFields.append("HASH");
        allFields.append(equator);
        allFields.append(secretKey);

        transactionlogger.error("allFields--->" + allFields);
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
    public CommRequestVO getBhartiPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
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

    public String getPaymentTypeS2S(String paymentMode)
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
    public String getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "CC";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DC";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "NB";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "WL";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UP";

        return payMode;
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
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }

    public String doGetHTTPSURLConnectionClient(String url) throws PZTechnicalViolationException
    {
        //transactionlogger.error("url--->" + url);
        SSLUtils.setupSecurityProvider();
        SSLUtils.trustAllHttpsCertificates();

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        String result = "";
        try
        {
            method.setRequestHeader("Accept", "application/json");
            //  method.setRequestHeader("Authorization", authType+ " " +encodedCredentials);
            method.setRequestHeader("Cache-Control", "no-cache");

            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK)
            {
                //transactionlogger.debug("Method failed: " + method.getStatusLine());
            }

            byte[] response = method.getResponseBody();
            //transactionlogger.error("Response-----" + response.toString());
            result = new String(response);


        }
        catch (HttpException he)
        {
            //transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            //transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("OneRoadUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            method.releaseConnection();
        }
        if (result == null)
            return "";
        else
            return result;
    }

    public static String encrypt(String data,String key){
/* String plainCipher = hashString;
//String plainCipher = "amount=100~card_exp_dt=012021~card_number=4000000000000002~currency_code=356~cust_email=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";
*/
        String ciphertext ="";
        try{


            byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
            byte [] keyBytes = key.getBytes(ASCII);

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(data.getBytes());

            ciphertext = new String(Base64.encodeBase64(result));

            transactionlogger.error("ciphertext--->"+ciphertext);

        }catch (Exception e){
            transactionlogger.error("Exception--->",e);
        }
        return ciphertext;
    }

    public static String dencrypt(String data,String key){
        String finalresult = "";
        try{
            byte [] cipherBytes = Base64.decodeBase64(data);
            byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
            byte [] keyBytes = key.getBytes(ASCII);

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(cipherBytes);

            finalresult = new String(result);


        }catch (Exception e){
            transactionlogger.error("Exception---->",e);
        }
        return finalresult;
    }

   /* public static String encrypt2(String data,String skey){
        String finalresult = "";
    try{

        //String plainCipher = hashString;
        //String plainCipher = "AMOUNT=100~CARD_EXP_DT=012021~CARD_NUMBER=4000000000000002~CURRENCY_CODE=356~CUST_EMAIL=test@bhartipay.com~CUST_NAME=Test Merchant~CUST_PHONE=9999999999~CVV=123~ORDER_ID=BHARTID3012201304~PAYMENT_TYPE=CARD~PAY_ID=2001141020561000~RETURN_URL=http://new.rs.bp.h.teamat.work/response.php~HASH=3B4A0992F799FC857F121C8515E06FCAF8AD4C9BCC6CAA82002899406E84CACA";
        byte [] iv = "B6B9B57E71DF7B52".getBytes(ASCII);
       // byte [] keyBytes = "B6B9B57E71DF7B52B17D191D36259E0B".getBytes(ASCII);
        Key key= mainUtils.makeKey(skey);

       // SecretKey aesKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(data.getBytes());
        // System.out.println(Hex.encodeHexString(result));
        finalresult = new String(Base64.encodeBase64(result));

        System.out.println("ciphertext--->"+finalresult);
        //   String decodedText = com.sun.xml.internal.messaging.saaj.util.Base64.base64Decode(String.);

    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
        return finalresult;
    }*/

    static String doPostHttpUrlConnection(String url ,String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            System.setProperty("https.protocols", "TLSv1.2");
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");
           post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            post.setRequestHeader("IPIMEI","192.168.1.8");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
            System.out.println("response-->" + result);
        }
        catch (HttpException e)
        {
            transactionlogger.error("HttpException--->",e);

        }
        catch (IOException e)
        {
            transactionlogger.error("HttpException--->", e);
        }finally
        {
            post.releaseConnection();
        }


        return result;
    }

    public void updateMainTableEntry(String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
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
            transactionlogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }


    public void updateOrderid(String requestorderid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String updateQuery1 = "UPDATE transaction_common SET authorization_code=? WHERE trackingid=?";
            PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
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
            transactionlogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }


    public static String encryptPayout(String data,String key){
        String ciphertext ="";
        try{

            byte [] iv = "fedcba9876543210".getBytes(ASCII);
            byte [] keyBytes = Base64.decodeBase64(key.getBytes(ASCII));

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(data.getBytes());

            ciphertext = new String(Base64.encodeBase64(result));

        }catch (Exception e){
            transactionlogger.error("Exception--->", e);
        }
        return ciphertext;
    }

    public static String dencryptPayout(String data,String key){
        String finalresult = "";
        try
        {
            byte [] cipherBytes = Base64.decodeBase64(data);
            byte [] iv = "fedcba9876543210".getBytes(ASCII);
            byte [] keyBytes = Base64.decodeBase64(key.getBytes(ASCII));

            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

            byte[] result = cipher.doFinal(cipherBytes);

            finalresult = new String(result);


        }catch (Exception e){
            transactionlogger.error("Exception--->", e);
        }
        return finalresult;
    }

    public static Boolean updatePayoutTransaction (String trackingid,String txtid, String  paymentid ){

        transactionlogger.error("in side  update Payout Transaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set paymentid= ? ,authorization_code=? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, paymentid);
            psUpdateTransaction.setString(2, txtid);
            psUpdateTransaction.setString(3, trackingid);
            transactionlogger.error("payout transaction common query----"+psUpdateTransaction);
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
            transactionlogger.error("SystemError ----",systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

    public static String getPaymentid (String trackingid ){


        String paymentid="";
        transactionlogger.error("in side get Payout Transactionid----------->");
        Connection con = null;
        PreparedStatement psselectTransaction = null;
        boolean isUpdate=false;
        try
        {

            con = Database.getConnection();
            String select = "SELECT paymentid from transaction_common where trackingid= ?";
            psselectTransaction = con.prepareStatement(select.toString());
            psselectTransaction.setString(1, trackingid);
            transactionlogger.error("get payout transactionid ----" + psselectTransaction);
            psselectTransaction.executeQuery();
            ResultSet rs = psselectTransaction.executeQuery();
            if (rs.next())
            {

                paymentid = rs.getString("paymentid");
            }
        }
        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError--->", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psselectTransaction);
            Database.closeConnection(con);
        }
        return paymentid;

    }

    public static Boolean updatePendingPayoutTransaction (String trackingid,String  remark ){

        transactionlogger.error("in side  update Payout Transaction remark----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set remark=? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, remark);
            psUpdateTransaction.setString(2, trackingid);
            transactionlogger.error("payout transaction common query remark----"+psUpdateTransaction);
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
            transactionlogger.error("SystemError--->", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }

}

package com.payment.payFluid;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.google.gson.Gson;

import javax.crypto.*;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Admin on 12/3/2020.
 */
public class PayFluidUtils
{

    private static final String CONTENT_CHARSET = "UTF-8";

    // HMAC
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // This is an array for creating hex chars
    static final char[] HEX_TABLE = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private final static TransactionLogger transactionLogger = new TransactionLogger(PayFluidUtils.class.getName());
    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, UnsupportedEncodingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes("UTF-8"));
    }

    public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(
                    Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            transactionLogger.error("NoSuchAlgorithmException--->", e);
        } catch (InvalidKeySpecException e) {
            transactionLogger.error("InvalidKeySpecException--->", e);
        } catch (IllegalArgumentException e){
            transactionLogger.error("IllegalArgumentException--->", e);
        }
        return publicKey;
    }

    public static boolean checkValidBase64(String base64Str){
        String regCompr = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
        return base64Str.matches(regCompr);
    }

    public static String fetchCurDate(String format){
        return new SimpleDateFormat(format, Locale.US).format(new Date());
    }



    static String doPostHttpUrlConnection(String url ,String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
            transactionLogger.error("response-->" + result);
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--------->" + e);

        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--------->"+e);
        }


        return result;
    }


    public static String[] sendPOST(String jSonString, String curDate, String url, String id,
                                    String loginParam, String RSAPublicKey)
            throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException,
            BadPaddingException, IOException, NoSuchAlgorithmException {

        String[] keyResponseVals = new String[2];
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        // con.setRequestProperty("User-Agent", USER_AGENT);
        id = Base64.getEncoder().encodeToString(id.getBytes());
        System.out.println("id base64---->"+id);
        con.setRequestProperty("id", id);
        String strToEncrypt = loginParam + "." + curDate;
        String encryptedString = Base64.getEncoder().encodeToString(encrypt(strToEncrypt, RSAPublicKey));
        // System.out.println(encryptedString);
        con.setRequestProperty("apiKey", encryptedString);
        System.out.println("Apikey---->"+encryptedString);
        con.setRequestProperty("Content-Type", "Application/json; charset=UTF-8");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        // Write data to
        os.write(jSonString.getBytes());
        os.flush();
        os.close();

        // For POST only - END
        int responseCode = con.getResponseCode();
        // informUser("POST Response Code :: " + responseCode);
        String kek = con.getHeaderField("kek");
        System.out.println("kek---->"+kek);
        keyResponseVals[0] = kek;
        // informUser("Response "+kek);
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            // informUser(response.toString
            keyResponseVals[1] = response.toString();
            System.out.println("sendpost response----->"+response.toString());
        } else {
            System.out.println("POST request not worked");
        }
        System.out.println("inside sendpost return keyResponseVals--->"+keyResponseVals);
        return keyResponseVals;
    }

    static String doPostHttpUrlConnectionJson(String url ,String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
            transactionLogger.error("response-->" + result);
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException--------->" + e);

        }
        catch (IOException e)
        {
            transactionLogger.error("IOException--------->"+e);
        }


        return result;
    }

     static String prepAmount(double amount){
        String strVal = String.valueOf(amount);
        if(amount%1 == 0){
            return strVal.split("\\.")[0];
        }
        return strVal;
    }

  // ************************************************************************

    public static String getSHA256(String input) {
        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            transactionLogger.error("Exception--->", e);
        }

        return toReturn;
    }

    public static String get_SHA_256_with_secret(String passwordToHash, String secret) {
        byte[] salt = Base64.getDecoder().decode(secret.getBytes());
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            transactionLogger.error("NoSuchAlgorithmException--->",e);
        }
        return generatedPassword;
    }

    //encryption
    public static String sha256WithKey(String data, String secret) {
        String sign = "";
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac.getAlgorithm());
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(CONTENT_CHARSET));
            sign = Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            transactionLogger.error("NoSuchAlgorithmException  PayFluidUtils  sha256WithKey-->",ex);
        } catch (UnsupportedEncodingException ex) {
            transactionLogger.error("UnsupportedEncodingException  PayFluidUtils  sha256WithKey-->", ex);
        } catch (InvalidKeyException ex) {
            transactionLogger.error("InvalidKeyException  PayFluidUtils  sha256WithKey-->", ex);        }
        return sign;
    }

    public static String hashMac256WithKey(String text, String secretKey) {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            // throw an exception or pick a different encryption method
            //throw new SignatureException("error building signature, no such algorithm in device " + HASH_ALGORITHM);
        } catch (InvalidKeyException e) {
            //throw new SignatureException( "error building signature, invalid key " + HASH_ALGORITHM);
        }
        return null;
    }

    private static final String HASH_ALGORITHM = "HmacSHA256";

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    public static String getSHA512(String input) {

        String toReturn = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            toReturn = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            transactionLogger.error("Exception--->", e);
        }

        return toReturn;
    }

    public static String getMac(String data, String secret) {
        String sign = "";
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac.getAlgorithm());
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(CONTENT_CHARSET));
            sign = Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            transactionLogger.error("NoSuchAlgorithmException  getMac-->",ex);
        } catch (UnsupportedEncodingException ex) {
            transactionLogger.error("UnsupportedEncodingException  getMac-->" ,ex);
        } catch (InvalidKeyException ex) {
            transactionLogger.error("InvalidKeyException  getMac-->" ,ex);
        }
        return sign;
    }

    public static boolean verifyMac(String data, String secret, String mac) {
        String calculatedMac = getMac(data, secret);
        if (calculatedMac.equals(mac)) {
            return true;
        }
        return false;
    }

    //BT method to do sha
    public static String getShaBT(String keyy, String data) {
        try {
            byte[] b = fromHexString(keyy, 0, keyy.length());

            SecretKey key = new SecretKeySpec(b, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(key);

            mac.update(data.getBytes("ISO-8859-1"));
            byte[] arr = mac.doFinal();

            String hashValue = hex(arr);
            return hashValue;
        } catch (Exception ex) {
            transactionLogger.error("Exception  getShaBT-->", ex);
        }
        return null;
    }

    public static byte[] fromHexString(String s, int offset, int length) {
        if ((length % 2) != 0) {
            System.out.println("nullllll");
            return null;
        }
        System.out.println("not nulllll");
        byte[] byteArray = new byte[length / 2];
        int j = 0;
        int end = offset + length;
        for (int i = offset; i < end; i += 2) {
            int high_nibble = Character.digit(s.charAt(i), 16);
            int low_nibble = Character.digit(s.charAt(i + 1), 16);
            if (high_nibble == -1 || low_nibble == -1) {
                // illegal format
                return null;
            }
            byteArray[j++] = (byte) (((high_nibble << 4) & 0xf0) | (low_nibble & 0x0f));
        }
        return byteArray;
    }

    static String hex(byte[] input) {
        // create a StringBuffer 2x the size of the hash array
        StringBuffer sb = new StringBuffer(input.length * 2);

        // retrieve the byte array data, convert it to hex
        // and add it to the StringBuffer
        for (int i = 0; i < input.length; i++) {
            sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
            sb.append(HEX_TABLE[input[i] & 0xf]);
        }
        return sb.toString();
    }

    public static String hmacDigestSimple(String data, String keyy) {
        byte[] mac = null;
        String hashValue = "";
        try {

            byte[] b = (keyy).getBytes("UTF-8");
            SecretKey key = new SecretKeySpec(b, "HmacSHA256");
            Mac m = Mac.getInstance("HmacSHA256");
            m.init(key);

            //m.update(data.getBytes("ISO-8859-1"));
            m.update(data.getBytes("UTF-8"));
            mac = m.doFinal();

            hashValue = hex(mac);
        } catch (Exception e) {
            //l.error(e, e);
        }
        return hashValue;
    }


    public Boolean updateTransaction (String trackingid, String paymentid){

        transactionLogger.error("in side  updateTransaction----------->");
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con= Database.getConnection();
            String update = "update transaction_common set paymentid = ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1,paymentid);
            psUpdateTransaction.setString(2,trackingid);
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
            transactionLogger.error("SystemError--->",systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeConnection(con);
        }
        return isUpdate;

    }
    public CommRequestVO getVervePayRequestVO(CommonValidatorVO commonValidatorVO)
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
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        // transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
       // transactionlogger.error(" VervePayutils  vpa--->" + commonValidatorVO.getVpa_address());

        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentMode());


        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }


}

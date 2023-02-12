package com.payment.flwBarter;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

/**
 * Created by Balaji on 16-Jan-20.
 */
public class FlwBarterUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FlwBarterUtils.class.getName());
    public String getKeyEncryption(String seedKey)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hashedString = md.digest(seedKey.getBytes("utf-8"));
            byte[] subHashString = toHexStr(Arrays.copyOfRange(hashedString, hashedString.length - 12, hashedString.length)).getBytes("utf-8");
            String subSeedKey = seedKey.replace("FLWSECK-", "");
            subSeedKey = subSeedKey.substring(0, 12);   // subSeedKey is the key
            byte[] combineArray = new byte[24];
            System.arraycopy(subSeedKey.getBytes(), 0, combineArray, 0, 12);
            System.arraycopy(subHashString, subHashString.length - 12, combineArray, 12, 12);
            return new String(combineArray);
        }
        catch (NoSuchAlgorithmException ex)
        {
            transactionLogger.error("Exception in FlwBarterUtils :: ",ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            transactionLogger.error("Exception in FlwBarterUtils :: " , ex);
        }
        return null;
    }

    public String toHexStr(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < bytes.length; i++ ){
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }
    public static String encryptData(String message, String _encryptionKey)
    {
        try
        {
            final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (NoSuchPaddingException e)
        {
           transactionLogger.error("NoSuchPaddingException---->",e);
        }
        catch (BadPaddingException e)
        {
            transactionLogger.error("BadPaddingException---->", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException---->", e);
        }
        catch (IllegalBlockSizeException e)
        {
            transactionLogger.error("IllegalBlockSizeException---->", e);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException---->", e);
        }
        catch (InvalidKeyException e)
        {
            transactionLogger.error("InvalidKeyException---->", e);
        }
        return "";

    }
    public static String doPostHTTPSURLConnectionClient(String strURL,String apiKey,String req) throws PZTechnicalViolationException
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("publickey",apiKey);
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException--->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException--->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
    public static HashMap<String,String> checkCardPresetInDB(String cardNumber){
        Connection con = null;
        HashMap<String, String> userDetails = new HashMap<>();
        try
        {
            //get details from Db and return hashmap
            con = Database.getConnection();
            String query = "select email,password from flw_barter_user where cardnumber = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,cardNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                userDetails.put("userType","existing");
                userDetails.put("email",rs.getString("email"));
                userDetails.put("password",rs.getString("password"));
            }else{
                userDetails.put("userType","new");
            }
        }
        catch (SQLException e)
        {
            transactionLogger.error("checkCardPresetInDB SQLException ---", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("checkCardPresetInDB SystemError ---", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return userDetails;
    }
    public static String masking(String str){
        if(str !=null && !(str.isEmpty()) && (str.length()>=10)) {
            String firstFour=str.substring(0, 6);
            String lastFour=str.substring(str.length() - 4);
            String maskStr=str.substring(6,str.length() - 4);
            String mask="";
            for (int i=0;i<maskStr.length();i++)
            {
                mask+="*";
            }
            return firstFour+mask+lastFour;
        }
        return str;
    }
    public void addNewUserEntry(String encryptedCard,String maskedCard,String email,String password,String phone)
    {
        transactionLogger.error("inside addNewUserEntry()");
        transactionLogger.error("encryptedCard----"+encryptedCard);
        Connection con = null;
        try{
            con = Database.getConnection();
            String query = "insert into flw_barter_user (encriptedcard,cardnumber,email,password,phone) values (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,encryptedCard);
            ps.setString(2,maskedCard);
            ps.setString(3,email);
            ps.setString(4,password);
            ps.setString(5,phone);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            transactionLogger.error("addNewUserEntry SQLException ---" , e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("addNewUserEntry SystemError ---", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public static String doGetHTTPSURLConnectionClient(String strURL,String publickey) throws PZTechnicalViolationException
    {
        String result = "";
        GetMethod post = new GetMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("publickey",publickey);
            post.addRequestHeader("Content-Type", "application/json");
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException ::::::::::", he);
        }
        catch (IOException io){
            transactionLogger.error("IOException ::::::::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }
}

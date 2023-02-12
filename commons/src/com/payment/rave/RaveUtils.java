package com.payment.rave;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Suraj on 10/26/2017.
 */
public class RaveUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger = new TransactionLogger(RaveUtils.class.getName());

    public static String toHexStr(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < bytes.length; i++ ){
            builder.append(String.format("%02x", bytes[i]));
        }
        return builder.toString();
    }

    public static String getKey(String seedKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] hashedString = md.digest(seedKey.getBytes("utf-8"));
            byte[] subHashString = toHexStr(Arrays.copyOfRange(hashedString, hashedString.length - 12, hashedString.length)).getBytes("utf-8");
            String subSeedKey = seedKey.replace("FLWSECK-", "");
            subSeedKey = subSeedKey.substring(0, 12);
            byte[] combineArray = new byte[24];
            System.arraycopy(subSeedKey.getBytes(), 0, combineArray, 0, 12);
            System.arraycopy(subHashString, subHashString.length - 12, combineArray, 12, 12);
            return new String(combineArray);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getGlobal().log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String encryptData(String message, String _encryptionKey)  {
        try {
            final byte[] digestOfPassword = _encryptionKey.getBytes("utf-8");
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            final SecretKey key = new SecretKeySpec( keyBytes , "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] plainTextBytes = message.getBytes("utf-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);
            return Base64.getEncoder().encodeToString(cipherText);

        } catch (Exception e) {

            transactionLogger.error("Exception---->",e);
            return "";
        }
    }

    public String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        try
        {
            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");

            // Send post request
            con.setDoOutput(true);
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            int responseCode=con.getResponseCode();
            BufferedReader in=null;
            if(responseCode==500 || responseCode==400){
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            else{
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }
            in.close();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("RaveUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("RaveUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return result.toString();
    }

    public String doPostHTTPSURLConnectionFromData(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        try
        {
            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("content-type", "application/json");

            con.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            if(con.getResponseCode() !=400){
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
            else{
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    result.append(inputLine);
                }
                in.close();
            }
        }
       catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("RaveUtils.java", "doPostHTTPSURLConnectionFromData()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("RaveUtils.java","doPostHTTPSURLConnectionFromData()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return result.toString();
    }
}


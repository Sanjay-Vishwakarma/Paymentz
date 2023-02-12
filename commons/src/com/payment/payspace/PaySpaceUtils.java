package com.payment.payspace;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Admin on 7/14/17.
 */
public class PaySpaceUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySpaceUtils.class.getName());

    public static  String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        transactionLogger.error(" strURL:::"+strURL);
        StringBuffer result = new StringBuffer();
        try
        {
            URL obj = new URL(strURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
             // Send post request
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(req);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                result.append(inputLine);
            }
            in.close();
        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySpaceUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaySpaceUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return result.toString();
    }

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    public String getMD5Hash(String customerEmailId,String cardNumber,String password)throws NoSuchAlgorithmException{
        String reversedFirstSix="";
        String custMail="";
        String card_number=cardNumber.substring(0,6);
        String card_number1=card_number.substring(card_number.length()-4);
        card_number=card_number+card_number1;
        char[] c=card_number.toCharArray();
        for (int i=c.length-1;i>=0;i--){
            String d=String.valueOf(c[i]);
            reversedFirstSix=reversedFirstSix+d;
        }
        char[] a = customerEmailId.toUpperCase().toCharArray();
        for (int i = a.length-1; i>=0; i--){
            String str = String.valueOf(a[i]);
            custMail=custMail+str;
        }
        String md5String=(custMail+password.toUpperCase()+reversedFirstSix).trim();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return getString(messageDigest.digest(md5String.getBytes()));
    }

    public String getMD5HashForOp(String customerEmailId,String transactionId,String cardNumber,String password)throws NoSuchAlgorithmException{
        String reversedFirstSix="";
        String custMail="";
        String card_number=cardNumber.substring(0,6);
        String card_number1=card_number.substring(card_number.length()-4);
        card_number=card_number+card_number1;
        char[] c=card_number.toCharArray();
        for (int i=c.length-1;i>=0;i--){
            String d=String.valueOf(c[i]);
            reversedFirstSix=reversedFirstSix+d;
        }
        char[] a = customerEmailId.toUpperCase().toCharArray();
        for (int i = a.length-1; i>=0; i--){
            String str = String.valueOf(a[i]);
            custMail=custMail+str;
        }
        String md5String=(custMail+password.toUpperCase()+transactionId.toUpperCase()+reversedFirstSix).trim();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return getString(messageDigest.digest(md5String.getBytes()));
    }
}

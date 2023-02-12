package com.payment.iMerchantPay;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
/**
 * Created by Admin on 6/26/18.
 */
public class iMerchantPayUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(iMerchantPayUtils.class.getName());

    public static  String doPostHTTPSURLConnectionClient( String req,String strURL) throws PZTechnicalViolationException
    {
        transactionLogger.error(" strURL:::"+strURL);
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con=null;
        try
        {
            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
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
            PZExceptionHandler.raiseTechnicalViolationException("iMerchantPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (MalformedURLException me)
        {

            PZExceptionHandler.raiseTechnicalViolationException("iMerchantPayUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }catch (IOException io){

            PZExceptionHandler.raiseTechnicalViolationException("iMerchantPayUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            if (con !=null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public static String bytesToHex(byte[] b) {
        char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < b.length; j++) {
            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static String generatePSign(String input) {
        String pSign = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(input.getBytes());
            byte[] output = md.digest();
            pSign = bytesToHex(output);
        } catch (Exception e) {
            transactionLogger.debug("Exception: " + e);
        }
        return pSign;
    }

    public static String getBirthday(String birthday)
    {
        String year = birthday.substring(0, 4);
        String month = birthday.substring(4, 6);
        String day = birthday.substring(6, 8);
        birthday = year + "-" + month + "-" + day;
        return birthday;
    }
}

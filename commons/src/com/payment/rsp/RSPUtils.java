package com.payment.rsp;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by admin on 5/21/2018.
 */
public class RSPUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger = new TransactionLogger(RSPUtils.class.getName());
    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException
    {
        StringBuffer result = new StringBuffer();
        URL obj;
        HttpURLConnection con=null;
        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1.2");

            obj = new URL(strURL);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
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
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("RSPUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, io.getMessage(), io.getCause());
        }
        finally
        {
            if(con !=null)
            {
                con.disconnect();
            }
        }
        return result.toString();
    }

    public static String getMD5HashForSale(String reqType, String reqMid, String reqAccountId, String reqUsername, String reqPassword, String reqTrackId, String secretKey) throws NoSuchAlgorithmException
    {
        String md5String = reqType + reqMid + reqAccountId + reqUsername + reqPassword + reqTrackId + secretKey;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return getString(messageDigest.digest(md5String.getBytes()));
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

    public HashMap readResponse(String responseData)
    {
        String responseDataArr[] = responseData.split("&");
        HashMap hashMap = new HashMap();
        Functions functions = new Functions();
        for (String value : responseDataArr)
        {
            String keyValueArr[] = value.split("=", 2);
            String key = "";
            String keyValue = "";
            if (functions.isValueNull(keyValueArr[0]))
            {
                key = keyValueArr[0];
                if (keyValueArr.length > 1)
                {
                    if (functions.isValueNull(keyValueArr[1]))
                    {
                        keyValue = keyValueArr[1];
                    }
                }
            }
            hashMap.put(key, keyValue);
        }
        return hashMap;
    }

    public String getRSPCardTypeName(String cardType)
    {
        String value = "";
        if ("VISA".equals(cardType))
        {
            value = "Visa";
        }
        else if ("MC".equals(cardType))
        {
            value = "MasterCard";
        }
        else if ("JCB".equals(cardType))
        {
            value = "JCB";
        }
        else if ("AMEX".equals(cardType))
        {
            value = "AMEX";
        }
        else if ("DISC".equals(cardType))
        {
            value = "Discover";
        }
        else if ("DINER".equals(cardType))
        {
            value = "Diners";
        }
        return value;
    }

    public Double getExchangeRate(String fromCurrency, String toCurrency)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Double exchangeRate = null;
        try
        {
            conn = Database.getConnection();
            String query = "select exchange_rate from currency_exchange_rates where from_currency=? and to_currency=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, fromCurrency);
            stmt.setString(2, toCurrency);
            resultSet = stmt.executeQuery();
            if (resultSet.next())
            {
                exchangeRate = resultSet.getDouble("exchange_rate");
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return exchangeRate;
    }

    public boolean changeTerminalInfo(String amount, String currency, String accountId, String fromId, String templateAmount, String templateCurrency, String trackingId, String terminalId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        int k = 0;
        boolean result = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_common set amount=?,currency=?,accountid=?,fromid=?,templateamount=?,templatecurrency=?,terminalId=? where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, amount);
            stmt.setString(2, currency);
            stmt.setString(3, accountId);
            stmt.setString(4, fromId);
            stmt.setString(5, templateAmount);
            stmt.setString(6, templateCurrency);
            stmt.setString(7, terminalId);
            stmt.setString(8, trackingId);
            k = stmt.executeUpdate();
            if (k > 0)
            {
                result = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return result;
    }
}

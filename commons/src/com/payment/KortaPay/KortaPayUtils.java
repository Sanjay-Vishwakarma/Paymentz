package com.payment.KortaPay;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.request.PZRefundRequest;
import com.payment.secureTrading.SecureTradingUtils;
import com.payment.validators.vo.CommonValidatorVO;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by Jitendra on 04-Jan-19.
 */
public class KortaPayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(KortaPayUtils.class.getName());
    public static String doHttpPostConnection(String operationUrl,String req, String type, String credentials) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", type + "" + credentials);

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
             bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            transactionLogger.error("-----Exception-----",e);
            PZExceptionHandler.raiseTechnicalViolationException("KortaPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public static Map<String, String> getQueryMap(String response)
    {
        String[] params = response.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug(name+":::"+value);
            }
        }
        return map;
    }

    public static String getCardExpiry(String cardExpiryYear , String cardExpiryMonth)
    {
        return cardExpiryYear.substring(2,4)+cardExpiryMonth;
    }

   /* public static void updateKortaDetails(String status,String amount,String responsetransactionid, String trackingid) throws PZDBViolationException
    {
        Connection con =null;
        PreparedStatement  psUpdateTransaction=null;
        try
        {
            con = Database.getConnection();
            String update = "UPDATE transaction_kortapay_details SET status=?, amount=? , responsetransactionid=? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update);
            psUpdateTransaction.setString(1,status );
            psUpdateTransaction.setString(2,amount );
            psUpdateTransaction.setString(3,responsetransactionid );
            psUpdateTransaction.setString(4,trackingid );
            psUpdateTransaction.executeUpdate();
            transactionLogger.debug("updateKortaDetails query----"+psUpdateTransaction);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("KortaPayutils.java", "updateKortaDetails()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("KortaPayutils.java", "updateKortaDetails()", null, "transaction", "SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(psUpdateTransaction);
        }
    }*/

   /* public static String checkKortaDetails(String trackingId)
    {
        String queryResult="";
        Connection con =null;
        ResultSet resultSet = null;
        PreparedStatement psUpdateTransaction = null;

        try
        {
            con = Database.getConnection();
            String update = "select status,amount,responsetransactionid from transaction_kortapay_details where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update);
            psUpdateTransaction.setString(1, trackingId);
            resultSet = psUpdateTransaction.executeQuery();
            if (resultSet.next())
            {
                queryResult="success";
            }
            else
            {
                queryResult="failed";
            }
            transactionLogger.debug("checkKortaDetails  query----"+psUpdateTransaction);
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeConnection(con);
            Database.closePreparedStatement(psUpdateTransaction);
            Database.closeResultSet(resultSet);
        }
        return queryResult;
    }*/


  /*  public static String insertForKorta(String status,String remainigAmount,String uniqueResponseTransId,String trackingID)
    {
        String queryResult="";
        Connection con = null;
        PreparedStatement p=null;
        try
        {
            con = Database.getConnection();
            String query="insert into transaction_kortapay_details(trackingid,status,amount,responsetransactionid) values(?,?,?,?)";
            p=con.prepareStatement(query);
            p.setString(1,trackingID);
            p.setString(2,status);
            p.setString(3,remainigAmount);
            p.setString(4,uniqueResponseTransId);

            int num = p.executeUpdate();
            transactionLogger.debug("insertForKorta Query-----"+p.toString());
            if (num == 1){
                queryResult="success";
            }
            else{
                queryResult="failed";
            }
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeConnection(con);
            Database.closePreparedStatement(p);
        }
        return queryResult;
    }*/

    public static Boolean TimeDifference(String transactionDate)
    {
        Boolean result=false;
        DecimalFormat crunchifyFormatter = new DecimalFormat("###,###");
        try
        {
            //1.Getting current Time
            Calendar calendar=Calendar.getInstance();
            Date date=calendar.getTime();
            Timestamp currentDate= new Timestamp(date.getTime());

            transactionLogger.debug("currentDate-----" + currentDate.toString());
            //2.Getting transaction Time
            long unixSeconds = Long.parseLong(transactionDate);
            date = new java.util.Date(unixSeconds*1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String transDate=sdf.format(date);

            transactionLogger.debug("transDate-----" + transDate);

            //3.Calculating
            Date d1=sdf.parse(currentDate.toString());
            Date d2=sdf.parse(transDate);

            long diff=d1.getTime()-d2.getTime();

            int diffhours = (int) (diff / (24*60*60*1000));
            transactionLogger.debug("difference between hours: " + crunchifyFormatter.format(diffhours));

            int dayCount=Integer.parseInt(crunchifyFormatter.format(diffhours));

            if(dayCount>=1){
                result=true;
            }
        }
        catch (ParseException e)
        {
            transactionLogger.error("SQLException::::::",e);
        }

        return result;
    }
}

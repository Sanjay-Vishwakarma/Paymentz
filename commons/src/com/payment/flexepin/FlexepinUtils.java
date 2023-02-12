package com.payment.flexepin;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by Admin on 2022-06-30.
 */
public class FlexepinUtils
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(FlexepinUtils.class.getName());
    static Functions functions                          = new Functions();

    public static boolean isJSONValid(String jsonData)
    {
        try {
            new JSONObject(jsonData);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static boolean isJSONARRAYValid(String arrayData)
    {
        try
        {
            new JSONArray(arrayData);
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    public static void updateMainTableEntry(String transactionId, String transaction_mode,String authorization_code, String trackingid)
    {
        Connection connection = null;
        try
        {
            String addParam= "";
            if(functions.isValueNull(transaction_mode))
                addParam += ",transaction_mode='"+transaction_mode+"'";
            if(functions.isValueNull(authorization_code))
                addParam += ",authorization_code='"+authorization_code+"'";

            connection              = Database.getConnection();
            if (functions.isValueNull(transactionId))
            {
                String updateQuery1     = "UPDATE transaction_common SET paymentid='"+transactionId+"' "+addParam+" WHERE trackingid="+trackingid;
                Database.executeUpdate(updateQuery1, connection);
            }
        }

        catch (SystemError s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static void updateBillingDescriptor(String billingDesc,String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            if (functions.isValueNull(trackingid) && functions.isValueNull(billingDesc))
            {
                String updateQuery1     = "UPDATE transaction_common_details SET responsedescriptor='"+billingDesc+"' WHERE status='capturesuccess' and trackingid="+trackingid;
                Database.executeUpdate(updateQuery1, connection);
            }
        }
        catch (Exception s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static void updateCaptureEntryforPayout(HashMap<String,String> hashMap,String responseStatus, String amount, String trackingid)
    {
        Connection connection               = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String addParam = "";

            if (functions.isValueNull(hashMap.get("pin")))
                addParam += ", voucher='"+hashMap.get("pin")+"'";

            if (functions.isValueNull(responseStatus))
                addParam += ", status='"+responseStatus+"'";

            if (functions.isValueNull(hashMap.get("expiry")))
                addParam += ", expiry='"+hashMap.get("expiry")+"'";

            if (functions.isValueNull(amount))
                addParam += ", amount='"+amount+"'";

            if (functions.isValueNull(hashMap.get("currency")))
                addParam += ", currency='"+hashMap.get("currency")+"'";

            if (functions.isValueNull(hashMap.get("trans_no")))
                addParam += ", paymentid='"+hashMap.get("trans_no")+"'";

            if (functions.isValueNull(hashMap.get("ean")))
                addParam += ", ean='" + hashMap.get("ean")+"'";

            if (functions.isValueNull(hashMap.get("voucherStatus")))
                addParam += ", voucherStatus='"+hashMap.get("voucherStatus")+"'";

            if (functions.isValueNull(hashMap.get("serial")))
                addParam += ", serial='"+hashMap.get("serial")+"'";


            connection          = Database.getConnection();
            if (functions.isValueNull(trackingid))
            {
                String updateQuery  = "UPDATE transaction_flexepin_details SET timestamp ='"+functions.getTimestamp()+"' "+addParam+"  WHERE trackingid=?" ;
                preparedStatement   = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, trackingid);

                preparedStatement.executeUpdate();
            }

            transactionLogger.error("update FlexepinVoucher details query ------> "+preparedStatement);
        }

        catch (Exception s)
        {
            transactionLogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public static HashMap getTimeRange(String trackingid)
    {
        HashMap<String,String> map = new HashMap<>();
        Connection connection      = null;
        ResultSet resultSet        = null;
        try
        {
            connection   = Database.getRDBConnection();
            String query = "select timestamp from transaction_common where trackingId = '"+ trackingid +"'";
            resultSet    = Database.executeQuery(query,connection);
            while (resultSet.next())
            {
                map.put("timestamp",resultSet.getString("timestamp"));
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while getting timestamp: " +e);
        }
        finally
        {
            Database.closeConnection(connection);
        }

        return map;
    }

    public static String doGetHTTPUrlConnection(String requestURL, String AUTHENTICATION, String trackingid)
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod   = new GetMethod(requestURL);
        try
        {
            getMethod.addRequestHeader("Content-Type","application/json");
            getMethod.addRequestHeader("AUTHENTICATION", "HMAC "+AUTHENTICATION);
            httpClient.executeMethod(getMethod);

            Header[] requestHeaders =  getMethod.getRequestHeaders();
            transactionLogger.error(trackingid + " request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  getMethod.getResponseHeaders();
            transactionLogger.error(trackingid + " response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header2.getName() + " : " + header2.getValue());
            }

            response = new String(getMethod.getResponseBody());
            transactionLogger.error(trackingid+ " response code: " + getMethod.getStatusCode());
        }
        catch (Exception he)
        {
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he);
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getMessage());
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getCause());
        }
        finally
        {
            getMethod.releaseConnection();
        }

        return response;
    }

    public static String doPutHTTPUrlConnection(String requestURL, String AUTHENTICATION, String data, String trackingid)
    {
        String response       = "";
        HttpClient httpClient = new HttpClient();
        PutMethod putMethod   = new PutMethod(requestURL);
        try
        {
            putMethod.addRequestHeader("Content-Type","application/json");
            putMethod.addRequestHeader("AUTHENTICATION", "HMAC "+AUTHENTICATION);
            StringRequestEntity entity = new StringRequestEntity(data, "application/json", "UTF-8");
            putMethod.setRequestEntity(entity);
            httpClient.executeMethod(putMethod);

            Header[] requestHeaders =  putMethod.getRequestHeaders();
            transactionLogger.error(trackingid + " request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  putMethod.getResponseHeaders();
            transactionLogger.error(trackingid + " response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingid +"--->"+ header2.getName() + " : " + header2.getValue());
            }

            response = new String(putMethod.getResponseBody());
            transactionLogger.error(trackingid+ " response code: " + putMethod.getStatusCode());
        }
        catch (Exception he)
        {
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he);
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getMessage());
            transactionLogger.error(trackingid + "Exception while doGetHTTPUrlConnection: "+ he.getCause());
        }
        finally
        {
            putMethod.releaseConnection();
        }

        return response;
    }

    public  static  byte[] generateHmacSHA256(String algorithm, byte[] key, byte[] message)
    {
        Mac mac = null;
        try
        {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key,algorithm));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while cretaing hmac --> " + e);
        }
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] hashInBytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHmac256Signature(byte[] key, String message)
    {
        byte[] bytes = generateHmacSHA256("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes);
    }
}

package com.payment.Ecommpay;

import com.directi.pg.*;
import com.directi.pg.Base64;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;


/**
 * Created by Admin on 6/15/2020.
 */
public class EcommpayUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EcommpayUtils.class.getName());

    public static String getCentAmount(String amount)//For USD
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public static String getJPYAmount(String amount)//for JPY
    {
        double amt = Double.parseDouble(amount);
        double roundOff = Math.round(amt);
        int value = (int) roundOff;
        amount = String.valueOf(value);
        return amount.toString();
    }

    public static String getKWDSupportedAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2 = dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }

    public static String getSignature(List list, String privateKey)
    {
        Collections.sort(list);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++)
        {
            sb.append(list.get(i));
            if (i < list.size() - 1)
            {
                sb.append("|");
            }
        }

        String signature = HmacSHA512(sb.toString(), privateKey);
        transactionLogger.error("signature =" + signature);

        return signature;
    }

    public static String HmacSHA512(String message, String key)
    {
        MessageDigest md = null;
        try
        {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);


            byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));


            String hex = Base64.encode(raw);
            return hex; //step 6
        }
        catch (Exception e)
        {
            System.out.println("exception " + e);
            return null;
        }

    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String req) throws PZTechnicalViolationException

    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException :::::",he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException :::::", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String calculateSignature(String request, String secretKey) throws JSONException
    {
        Functions functions = new Functions();
        String hash = "";
        StringBuffer requestString = new StringBuffer();
        JSONObject jsonObject = new JSONObject(request);
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            Object o = jsonObject.get(key);
            if (o instanceof JSONObject)
            {
                JSONObject innerJson1 = (JSONObject) jsonObject.get(key);
                Iterator i = innerJson1.keys();
                while (i.hasNext())
                {
                    String key2 = (String) i.next();
                    Object o2 = innerJson1.get(key2);
                    if (o2 instanceof JSONObject)
                    {
                        JSONObject innerJson2 = (JSONObject) innerJson1.get(key);
                        Iterator i2 = innerJson2.keys();
                        while (i2.hasNext())
                        {
                            String key3 = (String) i2.next();
                            Object o3 = innerJson2.get(key3);
                            if (o3 instanceof JSONObject)
                            {
                                JSONObject innerJson3 = (JSONObject) innerJson2.get(key);
                                Iterator i3 = innerJson3.keys();
                                while (i3.hasNext())
                                {
                                    String key4 = (String) i3.next();
                                    Object value = innerJson2.get(key4);
                                    if (value instanceof Boolean)
                                    {
                                        if ((boolean) value)
                                            value = 1;
                                        else
                                            value = 0;
                                    }
                                    requestString.append(key + ":" + key2 + ":" + key3 + ":" + key4 + ":" + value + ";");
                                }

                            }
                            else
                            {
                                Object value = innerJson2.get(key3);
                                if (value instanceof Boolean)
                                {
                                    if ((boolean) value)
                                        value = 1;
                                    else
                                        value = 0;
                                }
                                requestString.append(key + ":" + key2 + ":" + key3 + ":" + value + ";");
                            }
                        }

                    }
                    else
                    {
                        Object value = innerJson1.get(key2);
                        if (value instanceof Boolean)
                        {
                            if ((boolean) value)
                                value = 1;
                            else
                                value = 0;
                        }
                        requestString.append(key + ":" + key2 + ":" + value + ";");
                    }
                }

            }
            else
            {
                Object value = jsonObject.get(key);
                if (value instanceof Boolean)
                {
                    if ((boolean) value)
                        value = 1;
                    else
                        value = 0;
                }
                requestString.append(key + ":" + value + ";");
            }
        }
        String reqString = requestString.substring(0, requestString.length() - 1);
        transactionLogger.error("requestString-->" + reqString);
        hash = HmacSHA512(reqString, secretKey);

        return hash;
    }

    public static String getResponseHashInfoQuery(String trackingId)
    {
        String recurringId = "";
        Connection con= null;
        try
        {
            con = Database.getConnection();
            String query = "SELECT responsehashinfo FROM transaction_common_details WHERE trackingId=? AND status IN ('capturesuccess','authsuccessful')";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,trackingId);

            ResultSet rs =ps.executeQuery();
            if(rs.next())
            {
                recurringId=rs.getString("responsehashinfo");
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError->",systemError);
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
      return recurringId;
    }
    public static boolean insertThreeDsData(String trackingId,String acs_url,String pa_req,String md,String term_url)
    {
        Connection con= null;
        boolean isInserted=false;
        try
        {
            con = Database.getConnection();
            String query = "INSERT INTO transaction_ecommpay_details (`trackingid`,`type`,`acs_url`,`md`,`pa_req`,`term_url`) VALUES (?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,trackingId);
            ps.setString(2,"3D");
            ps.setString(3,acs_url);
            ps.setString(4,md);
            ps.setString(5,pa_req);
            ps.setString(6,term_url);
            transactionLogger.error("insertThreeDsData---->"+ps);
            int i=ps.executeUpdate();
            if(i>0)
            {
                isInserted=true;
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError->",systemError);
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isInserted;
    }
    public static HashMap<String,String> get3DsData(String trackingId)
    {
        Connection con= null;
        HashMap<String,String> hashMap=new HashMap<>();
        try
        {
            con = Database.getConnection();
            String query = "SELECT `acs_url`,`md`,`pa_req`,`term_url` FROM transaction_ecommpay_details WHERE trackingId=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,trackingId);
            transactionLogger.error("get3DsData---->"+ps);
            ResultSet rs =ps.executeQuery();
            if(rs.next())
            {
                hashMap.put("acs_url",rs.getString("acs_url"));
                hashMap.put("md",rs.getString("md"));
                hashMap.put("pa_req",rs.getString("pa_req"));
                hashMap.put("term_url",rs.getString("term_url"));
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError->",systemError);
        }

        catch (SQLException e)
        {
            transactionLogger.error("SQLException->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return hashMap;
    }
}
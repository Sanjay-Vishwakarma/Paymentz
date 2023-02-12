package com.payment.payeezy;

import com.directi.pg.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Base64;

/**
 * Created by Admin on 8/23/2019.
 */
public class PayeezyUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PayeezyUtils.class.getName());

    public static String doPostHTTPSURLConnectionClient(String url,String request, Map encriptedKey)throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside doPostHTTPSURLConnectionClient---"+url);
        String result = "";
        try
        {
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(url);
            /*post.addRequestHeader("apikey", apikey);
            post.addRequestHeader("token", token);
            post.addRequestHeader("Authorization", authorizaton);*/

            Iterator<String> iter = encriptedKey.keySet().iterator();
            while (iter.hasNext())
            {
                String key = iter.next();
                if ("payload".equals(key))
                    continue;
                post.addRequestHeader(key, String.valueOf(encriptedKey.get(key)));
            }

            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response= new String(post.getResponseBody());
            result=response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException----",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException----",io);
        }
        return result;
    }


    public static String getConversionAmountFromShare(String baseCurrency, String targetCurrency, String amount)
    {
        String result = null;

        try {
             ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.payeezy");
            String apiURL = rb.getString("authenticationUrl");
            String shareUsername = rb.getString("shareUsername");
            String sharePassword = rb.getString("sharePassword");
            String authorizationToken = encodeBase(shareUsername, sharePassword);
            transactionLogger.error("Authorization URL:" + apiURL);
            transactionLogger.error("Authorization Token:" + authorizationToken);

            String responsetoken = getShareAuthentication(apiURL, authorizationToken);


            transactionLogger.error("Response:" + responsetoken);


            if (responsetoken!=null) {

                String conversionAmt = rb.getString("conversionAmountUrl")+baseCurrency+"/"+targetCurrency+"/"+amount;
                transactionLogger.error("conversionAmountUrl-----"+conversionAmt);

                String response = getShareConversionAmount(conversionAmt, responsetoken);
                transactionLogger.error("Response------"+response);

                if(response!=null) {
                    result = response;
                }
                else {
                    transactionLogger.error("Exception from Share Engine");
                }
            }
        }
        catch(Exception ex)
        {
            transactionLogger.error("Exception--->",ex);
        }
        return result;
    }

    //SHARE ENGINE AUTHENTICATION
    public static String getShareAuthentication(String apiURL, String accessToken) throws Exception {

        String result = null;

        URL url = new URL(apiURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic " +accessToken);
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(60 * 1000);


        DataOutputStream wr= new DataOutputStream(urlConnection.getOutputStream());
        wr.flush();
        wr.close();

        int responseCode = urlConnection.getResponseCode();
        InputStream is;

        transactionLogger.error("Response Code:" + responseCode);
        if (responseCode >= 400)
        {
            is= urlConnection.getErrorStream();
            transactionLogger.error("Connection OK");
            transactionLogger.error("InputStream:" + is);
        }
        else
        {
            is= urlConnection.getInputStream();
            transactionLogger.error("else inputstream:" + is);
        }

        result = IOUtils.toString(is);
        transactionLogger.error("Result IS:" + result);

        JSONObject jsonObject=new JSONObject(result);
        String m= jsonObject.getString("token");
        transactionLogger.error("Token is:" + m);

        return m;
    }

    //encode method
    public static String encodeBase(String username, String password)throws Exception
    {
        String authenticate = username+ ":" +password;
        String encodeFormat = Base64.getEncoder().encodeToString(authenticate.getBytes());
        return encodeFormat;
    }

    public static String getShareConversionAmount(String apiURL, String accessToken) throws Exception {

        transactionLogger.error("APIURL---"+apiURL+"--AccessToken---"+accessToken);

        String result = null;

        URL url = new URL(apiURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setConnectTimeout(60 * 1000);

        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        int responseCode = urlConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0)
                return null;

            result = buffer.toString();
            transactionLogger.error("Result-----"+result);

        }
        return result;
    }

    public static HashMap<String,String> getTerminalConversionDetails(String terminalID)
    {
        Connection con=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap<String,String> hashMap=null;

        try
        {
            con=Database.getConnection();
            String query="SELECT currency_conversion,conversion_currency FROM member_account_mapping WHERE terminalid=?";
            ps=con.prepareStatement(query);
            ps.setString(1,terminalID);
            rs=ps.executeQuery();
            transactionLogger.error("Query---"+ps);
            if(rs.next())
            {
                hashMap= new HashMap<>();
                hashMap.put("currency_conversion", rs.getString("currency_conversion"));
                hashMap.put("conversion_currency",rs.getString("conversion_currency"));
            }
        }catch (Exception e)
        {
            transactionLogger.error("Exception in getTerminalConversionDetails---",e);
        }
        finally
        {
            Database.closeConnection(con);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return hashMap;
    }
}

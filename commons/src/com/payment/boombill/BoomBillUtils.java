package com.payment.boombill;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.httpclient.Header;

import java.io.IOException;
import java.text.DecimalFormat;


/**
 * Created by Admin on 2022-01-22.
 */
public class BoomBillUtils
{

    private static TransactionLogger transactionLogger = new TransactionLogger(BoomBillUtils.class.getName());
    static Functions functions = new Functions();

    static String doPostHttpUrlConnection(String Request_url, String data, String Header, String trackingId) throws Exception
    {

        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(Request_url);
        String result= "";
        try
        {
            postMethod.addRequestHeader("Content-Type", "application/json");
            postMethod.addRequestHeader("Access-Control-Allow-Origin", "api");
            postMethod.addRequestHeader("APPKEY", "AAAA01g3ydw:APA91bEbx53P3ItBerUV5oDviGfI3IdKIpzJCBEeFk0iuyrUyPr-9X6QqDl_h6OJxUbQAJiv8gik_UGJiVrbH7eQLUj59w_RCCwzs_Rgp-Dt3FsyYx3gs4gJ50T7XP62Zhkic73a6mbu");
            postMethod.addRequestHeader("Header-Token", Header);

            postMethod.setRequestBody(data);
            httpClient.executeMethod(postMethod);
            result = new String(postMethod.getResponseBody());

            Header[] requestHeaders =  postMethod.getRequestHeaders();
            transactionLogger.error(trackingId+" "+"request Headers[]:");
            for(Header header1 : requestHeaders)
            {
                transactionLogger.error(trackingId+" "+header1.getName() + " : " + header1.getValue());
            }

            Header[] responseHeaders =  postMethod.getResponseHeaders();
            transactionLogger.error(trackingId+" "+"response Headers[]:");
            for(Header header2 : responseHeaders)
            {
                transactionLogger.error(trackingId+" "+header2.getName() + " : " + header2.getValue());
            }

        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BoomBillUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BoomBillUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return result;

    }

    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VISA";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MASTERCARD";
        return payBrand;
    }


    public static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    public static boolean isJSONARRAYValid(String test)
    {
        try
        {
            new JSONArray(test);
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }
}
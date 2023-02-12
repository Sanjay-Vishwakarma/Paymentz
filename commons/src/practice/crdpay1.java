package practice;

import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.transaction.vo.restVO.RequestVO.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Admin on 6/9/2020.
 */
class crdpay1
{
    public static void main(String[] args)
    {
        sale();
    }

    public static void sale()
    {
        try
        {
            String password = "3BkvPO4g72dZ";
            String terminal_code = "14791";
            String tokenUrl = "https://sandbox.cardpay.com/api/auth/token";
            String tokenReq = "grant_type=password&password=" + password + "&terminal_code=" + terminal_code;
            String tokenRes = doPostHTTPSURLConnectionClient(tokenUrl, tokenReq);
            System.out.println("tokenRes--->" + tokenRes);
            JSONObject jsonObject = new JSONObject(tokenRes);
            String access_token = jsonObject.getString("access_token");
            String saleUrl = "https://sandbox.cardpay.com/api/payments";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String trackingId = "124";
            String request = "{" +
                    "  \"request\": {" +
                    "    \"id\": " + trackingId + "," +
                    "    \"time\": \"" + dateFormat.format(new Date()) + "\"" +
                    "  }," +
                    "  \"card_account\": {" +
                    "    \"billing_address\": {" +
                    "      \"addr_line_1\": \"450 W.\"," +
                    "      \"addr_line_2\": \"33 Street\"," +
                    "      \"city\": \"New York\"," +
                    "      \"country\": \"USA\"," +
                    "      \"state\": \"NY\"," +
                    "      \"zip\": \"02108\"" +
                    "    }," +
                    "    \"card\": {" +
                    "      \"acct_type\": \"01\"," +
                    "      \"expiration\": \"11/2022\"," +
                    "      \"holder\": \"John Smith\"," +
                    "      \"pan\": 4000000000000002," +
                    "      \"security_code\": 555" +
                    "    }" +
                    "  }," +
                    "  \"customer\": {" +
                    "    \"birth_date\": \"2019-04-22\"," +
                    "    \"email\": \"customer@example.com\"," +
                    "    \"full_name\": \"John Smith\"," +
                    "    \"home_phone\": \"+1 111111111\"," +
                    "    \"id\": 33282392389," +
                    "    \"identity\": \"DNI\"," +
                    "    \"ip\": \"192.168.1.3\"," +
                    "    \"living_address\": {" +
                    "      \"address\": \"1600 Pennsylvania Ave NW\"," +
                    "      \"city\": \"Washington\"," +
                    "      \"country\": \"US\"," +
                    "      \"state\": \"NY\"," +
                    "      \"zip\": 10001" +
                    "    }," +
                    "    \"locale\": \"en\"," +
                    "    \"phone\": \"+1 111111111\"," +
                    "    \"work_phone\": \"+1 111111111\"" +
                    "  }," +
                    "  \"merchant_order\": {" +
                    "    \"description\": \"Example order\"," +
                    "    \"id\": \"" + trackingId + "\"" +
                    "  }," +
                    "  \"payment_data\": {" +
                    "    \"amount\": 100," +
                    "    \"currency\": \"USD\"" +
                    "  }," +
                    "  \"payment_method\": \"BANKCARD\"," +
                    "  \"return_urls\": {" +
                    "    \"return_url\": \"http://localhost:8081/transaction/Common3DFrontEndServlet.java\"" +
                    "  }" +
                    "}";

            System.out.println("Request-->" + request);
            String response = doPostHTTPSURLConnectionClient(saleUrl, request, access_token);
            System.out.println("response-->" + response);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static String doPostHTTPSURLConnectionClient(String strURL,String req,String access_token) throws PZTechnicalViolationException

    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {    HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Bearer "+access_token);
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            he.printStackTrace();
        }
        catch (IOException io){
            io.printStackTrace();
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnectionClient(String strURL, String req)
    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(req);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            System.out.println("HttpException --->" + e);
        }
        catch (IOException e)
        {
            System.out.println("IOException --->" + e);
        }
        finally
        {
            post.releaseConnection();
        }

        return result;
    }
}

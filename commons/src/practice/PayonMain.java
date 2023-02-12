package practice;

/**
 * Created by Admin on 6/13/2020.
 */

import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payonOppwa.PayonOppwaUtils;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PayonMain
{
        public static void sale3D(){
               String entityId="8ac7a4c97743be7101774440e23903ee";
                String amount="12.00";
                String currency="EUR";
                String paymentBrand="VISA";
                String paymentType="DB";
                String merchantTransactionId="order99234";
                String transactionCategory="EC";
                String cardnumber="4000000000000010";
                //String cardnumber="4111111111111111";
                String cardexpiryMonth="12";
                String cardexpiryYear="2025";
                String cardcvv="123";
                String  cardholder="John Smith";
                String  shopperResultUrl="https://merchant.org";
                String  customerip="192.168.0.1";
                String customerbrowseracceptHeader="text/html";
                String customerbrowserscreenColorDepth="48";
                String customerbrowserjavaEnabled="false";
                String customerbrowserlanguage="de";
                String customerbrowserscreenHeight="1200";
                String customerbrowserscreenWidth="1600";
                String customerbrowsertimezone="60";
                String customerbrowserchallengeWindow="4";
                String customerbrowseruserAgent="Mozilla/4.0 (MSIE 6.0; Windows NT 5.0)";
                String testMode="EXTERNAL";
                String termUrl= "localhost:8081/transaction/NPOFrontEndServlet?trackingId=";
                String testUrl="https://test.oppwa.com//v1/payments";
                String authorizationToken="OGFjN2E0Yzk3NzQzYmU3MTAxNzc0NDNkN2I2ODAzYTN8WmFaOFRSaDg1Wg==";

                try
                {
                    String sale3DRequest = ""
                            + "entityId=" + entityId
                            + "&amount=" + amount
                            + "&currency=" + currency
                            + "&paymentBrand=" + paymentBrand
                            + "&paymentType=" + paymentType
                            + "&merchantTransactionId=" + merchantTransactionId
                            + "&transactionCategory=" + transactionCategory
                            + "&card.number=" + cardnumber
                            + "&card.holder=" + cardholder
                            + "&card.expiryMonth=" + cardexpiryMonth
                            + "&card.expiryYear=" + cardexpiryYear
                            + "&card.cvv=" + cardcvv
                            + "&shopperResultUrl=" + shopperResultUrl
                            + "&customer.ip=" + customerip
                            + "&customer.browser.acceptHeader=" + customerbrowseracceptHeader
                            + "&customer.browser.screenColorDepth=" + customerbrowserscreenColorDepth
                            + "&customer.browser.javaEnabled=" + customerbrowserjavaEnabled
                            + "&customer.browser.language=" + customerbrowserlanguage
                            + "&customer.browser.screenHeight=" + customerbrowserscreenHeight
                            + "&customer.browser.screenWidth=" + customerbrowserscreenWidth
                            + "&customer.browser.timezone=" + customerbrowsertimezone
                            + "&customer.browser.challengeWindow=" + customerbrowserchallengeWindow
                            + "&customer.browser.userAgent=" + customerbrowseruserAgent
                            + "&testMode=" + testMode;

                    System.out.println("3D Request--------" + sale3DRequest);


// transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                    String response = "";
                    String name = "";
                    String value = "";
                    String message = "";
                    String url_in_response = "";
                    PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();
                    response = payonOppwaUtils.doPostHTTPSURLConnectionClient(testUrl, authorizationToken, sale3DRequest);
                    System.out.println("3D Response---------" + response);
                    try
                    {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject redirect=jsonObject.getJSONObject("redirect");

                        if(jsonObject.has("redirect"))
                        {
                            if(redirect.has("url"))
                            {
                                url_in_response=redirect.getString("url");
                            }
                            if(redirect.has("parameters"))
                            {
                                name=redirect.getString("parameters");

                                org.json.JSONArray jsonArray = redirect.getJSONArray("parameters");
                                if (jsonArray != null)
                                {
                                    for (int i = 0; i < jsonArray.length(); i++)
                                    {
                                        JSONObject parameters = jsonArray.getJSONObject(i);
                                        if (i != 0)
                                        {
                                            name =parameters.getString("name");
                                            value=parameters.getString("value");
                                        }

                                    }
                                }
                            }
                        System.out.println("url_in_response--->"+url_in_response);
                        System.out.println("name inside parameters --->"+name);
                        System.out.println("value inside parameters --->"+value);

                    }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }





                }
                catch (PZTechnicalViolationException e)
                {
                        e.printStackTrace();
                }

        }

public static void sale(){

        // ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payon-oppwa");
        String merchantId="8ac7a4c77279a71001727f1c67bd133e";
        String transactionAmount="1.00";
        String transactionCurrency="EUR";
        String cardType="VISA";
        String reqType="DB";
        String trackingID="12344";
        String customerCardNumber="4711100000000000";
        String firstName="Göran";
        String lastName="";
        String customerCardExpiryMonth="12";
        String customerCardExpiryYear="2021";
        String customerCvv="123";
        String customeremail="abc123@gmail.com";
        String hostIPAddress="192.168.1.1";
        String customerCity="Mumbai";
        String customerStreet="@#$%%";
        String customerState="MH";
        String customerZipCode="400074";
        String customer_birthDate="1995-05-12";
        String WalletId="";
        String termUrl= "localhost:8081/transaction/NPOFrontEndServlet?trackingId=";
        String country="IN";
        String authorizationToken="OGE4Mjk0MTc1M2NjOGJkODAxNTNlNTM4ZmJkNzJhNDV8QXM2SG5LMmZwNw==";
        String testUrl="https://test.oppwa.com//v1/payments";
        try
        {
            String saleRequest = ""
                    + "authentication.entityId=" + merchantId
                    + "&amount=" + transactionAmount
                    + "&currency=" + transactionCurrency
                    + "&paymentBrand=" + cardType
                    + "&paymentType=" + reqType
                    + "&merchantTransactionId=" + trackingID
                    + "&card.number=" + customerCardNumber
                    + "&card.holder=" + firstName+" "+lastName
                    + "&card.expiryMonth=" + customerCardExpiryMonth
                    + "&card.expiryYear=" + customerCardExpiryYear
                    + "&card.cvv=" + customerCvv
                    + "&customer.givenName=" + firstName
                    + "&customer.surname=" + lastName
                    + "&customer.email=" + customeremail
                    + "&customer.ip=" + hostIPAddress
                    + "&billing.city=" + customerCity
                    + "&billing.street1=" + customerStreet
                    + "&billing.state=" + customerState
                    + "&billing.postcode=" + customerZipCode
                    + "&billing.country=" + country
                    + "&customer.birthDate=" + customer_birthDate
                    + "&customParameters[WalletId]=" + WalletId
                    + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8");

            System.out.println("request-------" + saleRequest);


// transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
            String response="";
            PayonOppwaUtils payonOppwaUtils=new PayonOppwaUtils();
            response = payonOppwaUtils.doPostHTTPSURLConnectionClient(testUrl,authorizationToken,saleRequest);
            System.out.println("response---------" + response);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {

        //sale();
        sale3D();
    }

}




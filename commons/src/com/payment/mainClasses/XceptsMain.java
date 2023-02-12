package com.payment.mainClasses;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Admin on 5/13/2022.
 */
public class XceptsMain
{
    public static void main(String[] args)
    {

    }

    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-type","application/json");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            result=new String (post.getResponseBody());

        }catch (Exception e)
        {
        e.printStackTrace();
    }
        return result;
    }
    public static void sale()
    {
        String terminalid="TransactWolrdX";
        String password ="Errt34%2rmy$";
        String action="1";
        String card="4000000000001091";
        String cvv2="185";
        String expYear="2023";
        String expMonth="06";
        String member="Seferali tt";
        String currencyCode="USD";
        String address="No 6, Broadstreet";
        String city="NY";
        String statecode="MNK";
        String zip="45637";
        String CountryCode="US";
        String email="test111@gmail.com";
        String amount="1";
        String trackid="16523503712MUI4BDASU";


        String xml ="<request>\n" +
                "<terminalid>"+terminalid+"</terminalid>\n" +
                "<password>"+password+"</password>\n" +
                "<action>"+action+"</action>\n" +
                "<card>"+card+"</card>\n" +
                "<cvv2>"+cvv2+"</cvv2>\n" +
                "<expYear>"+expYear+"</expYear>\n" +
                "<expMonth>"+expMonth+"</expMonth>\n" +
                "<member>"+member+"</member>\n" +
                "<currencyCode>"+currencyCode+"</currencyCode>\n" +
                "<address>"+address+"</address>\n" +
                "<city>"+city+"</city>\n" +
                "<statecode>"+statecode+"</statecode>\n" +
                "<zip>"+zip+"</zip>\n" +
                "<CountryCode>"+CountryCode+"</CountryCode>\n" +
                "<email>"+email+"</email>\n" +
                "<amount>"+amount+"</amount>\n" +
                "<trackid>"+trackid+"</trackid>\n" +
                "</request>";


        String result = doHttpPostConnection("https://test.upaywise.com/paymentgateway/payments/performXmlTransaction","");

    }
}

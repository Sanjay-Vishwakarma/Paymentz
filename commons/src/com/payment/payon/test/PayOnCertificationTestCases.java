package com.payment.payon.test;

import com.payment.payon.core.message.*;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/27/12
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnCertificationTestCases
{

    private String username = "68176b4415224fc1af4962fe794cc84f";
    private String password = "yoUEPlMfyoEh";
    private String url = "https://test.ppipe.net/connectors/gateway";

    Request request;

    @Before
    public void setUp()
    {
        request = new Request();

    }


    @Test
    public void testCaseA1()
    {

        //10 EUR Debit followed by reversal
        try
        {
            /*Identification identification = new Identification();
            identification.setShortID(RandomStringUtils.randomNumeric(RandomUtils.nextInt(12) + 4));
            identification.setUUID(RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(24) + 8));*/


            MerchantAccount merchantAccount = new MerchantAccount();
            merchantAccount.setType("WORLDLINE");
            merchantAccount.setMerchantID("123456");
            merchantAccount.setMerchantName("Test Merchant");
            merchantAccount.setUsername("27564426");
            merchantAccount.setKey("0014");
            merchantAccount.setTerminalID("75500001");
/*


            Payment payment = new Payment();
            payment.setType("DB");
            payment.setAmount("10.00");
            payment.setCurrency("EUR");
            payment.setDescriptor("Certification Transaction Test case A1");


            CreditCardAccount creditCardAccount = new CreditCardAccount();
            creditCardAccount.setBrand("VISA");
            creditCardAccount.setNumber("4012888888881881");
            creditCardAccount.setVerification("456");
            CCDate ccDate = new CCDate();
            ccDate.setMonth("01");
            ccDate.setYear("2015");
            creditCardAccount.setExpiry(ccDate);

            Customer customer = new Customer();
            Name name = new Name();
            name.setFamily("Certification");
            name.setGiven("Certification");
            customer.setName(name);

            Contact contact = new Contact();
            contact.setIp("2.2.2.2");
            contact.setEmail("test@gmail.com");
            customer.setContact(contact);

            Address address = new Address();
            address.setCountry("IN");
            address.setCity("Mumbai");
            address.setState("MH");
            address.setZip("400069");
            address.setStreet("Mindspace St 7");
            customer.setAddress(address);*/

            RequestTransaction requestTransaction = new RequestTransaction();
            /*requestTransaction.setIdentification(identification);*/
            requestTransaction.setMerchantAccount(merchantAccount);
            /*requestTransaction.setPayment(payment);
            requestTransaction.setCreditCardAccount(creditCardAccount);
            requestTransaction.setCustomer(customer)*/;

            Request request = new Request();
            request.setTransaction(requestTransaction);

            new PayOnCertificationTestCases().processRequest(merchantAccount);
        }
        catch (Exception e)
        {
            fail();
        }


    }


    private Response processRequest(MerchantAccount request) throws Exception
    {

        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("", "com.payment.payon.core");
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(request));

        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod("https://test.ctpe.io/payment/ctpe");
        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        /*UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
        httpClient.getState().setCredentials(authScope, usernamePasswordCredentials);*/
        post.setDoAuthentication(true);
        post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        //System.out.println("Request XML = " + xmlOut);

        NameValuePair[] params = {new NameValuePair("requestXML", "<MerchantAccount type=\"WORLDLINE\">\n" +
                "  <MerchantID>123456</MerchantID>\n" +
                "  <MerchantName>Test Merchant</MerchantName>\n" +
                "  <Key>0014</Key>\n" +
                "  <Username>27564426</Username>\n" +
                "  <TerminalID>75500001</TerminalID>\n" +
                "</MerchantAccount>")};
        post.setRequestBody(params);
        httpClient.executeMethod(post);
        String responseXML = new String(post.getResponseBody());
        XStream xstream1 = new XStream();
        xstream1.autodetectAnnotations(true);
        xstream1.alias("Response", Response.class);
        //System.out.println("Response XML = " + responseXML);
        Response response = (Response) xstream1.fromXML(responseXML);

       /* System.out.println("response = " + response);
        System.out.println("Response Code = " + response.getResponseTransaction().getProcessing().getReturnresp().getCode());
        System.out.println("Response Message = " + response.getResponseTransaction().getProcessing().getReturnresp().getMessage());*/

        return response;
    }


}

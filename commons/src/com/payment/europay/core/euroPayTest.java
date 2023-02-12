package com.payment.europay.core;

import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.payon.core.message.Transaction;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Before;
import org.junit.Test;

import java.awt.datatransfer.StringSelection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/6/13
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class euroPayTest
{
    private EuroPayPaymentGateway euroPayPaymentGateway;
    private static TransactionLogger transactionLogger = new TransactionLogger(euroPayTest.class.getName());
    @Before
    public void setUp() throws SystemError
    {
        euroPayPaymentGateway = new EuroPayPaymentGateway();

        euroPayPaymentGateway.setServiceKey("f3f3abdb4ca0db2b4ba76deaee9544e44a6861b643e3804f83771435");
        euroPayPaymentGateway.setRoutingKey("173e0f75d663be1fe1f4883110bd592114254aea0bb4c245272e60b6");

    }

//    @Test
    public void test1()
    {

      transactions_response response = new transactions_response();

      EuroPayRequestVO euroPayRequestVO = new EuroPayRequestVO();
      EuroPayIdentity euroPayIdentity  = new EuroPayIdentity();

      euroPayIdentity.setIp("115.247.16.28");
      euroPayIdentity.setEmail("john_jenny.doe@mac.com");

      EuroPayAddress euroPayAddress = new EuroPayAddress();
      euroPayAddress.setType("private");
      euroPayAddress.setCountry("US");
      euroPayAddress.setCity("New Orleans");
      euroPayAddress.setState("LA");
      euroPayAddress.setStreet("1184 Maple St Ext");
      euroPayAddress.setZip("70122");

      EuroPayPayment euroPayPayment = new EuroPayPayment();
      euroPayPayment.setUsageL1("Reference test first line");
      euroPayPayment.setUsageL2("Reference test second line");
      euroPayPayment.setAmount("1750");
      euroPayPayment.setCurrency("USD");

      EuroPayCustomer euroPayCustomer = new EuroPayCustomer();
      euroPayCustomer.setFirstName("Jenny");
      euroPayCustomer.setLastName("Doe");
      euroPayCustomer.setSalutation("MRS");
      euroPayCustomer.setBirthDay("08");
      euroPayCustomer.setBirthMonth("06");
      euroPayCustomer.setBirthYear("1965");
      euroPayCustomer.setEuroPayAddress(euroPayAddress);
      euroPayCustomer.setEuroPayIdentity(euroPayIdentity);

      EuroPayCard euroPayCard = new EuroPayCard();
      euroPayCard.setBrand("VISA");
      euroPayCard.setOwnerLastName("Arnold");
      euroPayCard.setOwnerFirstName("Frank");
      euroPayCard.setExpireMonth("06");
      euroPayCard.setExpireYear("2017");
      euroPayCard.setVerification("956");
      euroPayCard.setNumber("4543474002249996");

      EuroPayTransactions euroPayTransactions = new EuroPayTransactions();
      euroPayTransactions.setEuroPayPayment(euroPayPayment);
      euroPayTransactions.setEuroPayCustomer(euroPayCustomer);
      euroPayTransactions.setEuroPayCard(euroPayCard);
      euroPayTransactions.setMethod("CC");
      euroPayTransactions.setType("DB");
      euroPayTransactions.setMcTxDate("Thursday, 15-Aug-13 17:24:42 UTC");
      euroPayTransactions.setMcTxId("OAYPKaiaaa");



      String  amount = "12";
      String newamount ;
      //System.out.println(amount);
      newamount = amount.replace(".","");

        //amount.re
       /* System.out.println("after replacmet");
        System.out.println(newamount);*/

       amount.replaceAll(".","--");
        //System.out.println(amount);


      TransactionsRequest transactionsRequest = new TransactionsRequest();
      transactionsRequest.setAccountId("1");
      transactionsRequest.setMode("sync");
      transactionsRequest.setEuroPayTransactions(euroPayTransactions);


        try
            {
                //response = euroPayPaymentGateway.processSaleRequest(transactionsRequest);

                RefundRequest refundRequest = new RefundRequest();

                refundRequest.setTxId("12qw12");
                refundRequest.setAmount(12345);
                response = euroPayPaymentGateway.processRefundRequest(refundRequest);
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception :::::::",e);
            }


    }

//    public  static void main(String[] args) throws ParseException
//    {
////        Date dt = new Date();
////        String str = dt.toString();
//        //SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//
//        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
//	   //get current date time with Date()
//	   Date date = new Date();
//	   System.out.println(dateFormat.format(date));
//    }



      @Test
     public void testQuery() throws Exception
      {
          XStream xstream = new XStream();
          xstream.autodetectAnnotations(true);
          xstream.aliasPackage("","com.payment.europay.core");

          transactions_response response = new transactions_response();

          //System.out.println(" response " + response.toString());

          try
          {
              // String responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><transactions_response version=\"1.0.2-202-2013-08-09_18-16-04\" code=\"400.500.000\">" +
                //       "<searchResults terminalName=\"\" partnerName=\"\" merchantName=\"Transactworld Ltd.\" currency=\"USD\" creditAmountFloat=\"11.0\" debitAmountFloat=\"0.0\" creditAmount=\"1100\" debitAmount=\"0\" channelName=\"Andaz VISA MC 37\" message=\"\" reference=\"Reference Line 1 Reference Line 2\" code=\"800.900.100\" endStamp=\"1376766398711\" startStamp=\"1376766395264\" type=\"DB\" method=\"CC\" destination=\"Andaz via EPS\" mcTxId=\"108767\" txId=\"00001G6\"><authorization serviceIP=\"115.247.0.9\" serviceKey=\"f3f3abdb4ca0db2b4ba76deaee9544e44a6861b643e3804f83771435\" routingKey=\"173e0f75d663be1fe1f4883110bd592114254aea0bb4c245272e60b6\"/><token ccBrand=\"VISA\" ownerLastName=\"latst\" ownerFirstName=\"tetst\" expireMonth=\"1\" expireYear=\"2014\" lead6=\"411111\" trail4=\"1122\" panAlias=\"9080122011001121122\" value=\"c73ad1fa6c069d6d5cf433cfac502665ba1e02fec2e1cabfe68e338a\"/>" +
                  //     "</searchResults></transactions_response>";

                      // "</searchResults></transactions_response>";
                       //"<transactionLog created=\"1376766395270\" type=\"CUSTOMER_DETAILS\" log=\"{&quot;addresses&quot;:[{&quot;city&quot;:&quot;city&quot;,&quot;zip&quot;:&quot;123123&quot;,&quot;street&quot;:&quot;test ad&quot;,&quot;state&quot;:&quot;Maharashtra&quot;,&quot;country&quot;:&quot;IN&quot;,&quot;type&quot;:&quot;Private&quot;}],&quot;identity&quot;:{&quot;email&quot;:&quot;aatest@test.com&quot;,&quot;ip&quot;:&quot;191.162.98.89&quot;},&quot;firstName&quot;:&quot;test&quot;,&quot;lastName&quot;:&quot;last&quot;,&quot;salutation&quot;:&quot;Salutation&quot;,&quot;birthYear&quot;:&quot;1985&quot;,&quot;birthMonth&quot;:&quot;08&quot;,&quot;birthDay&quot;:&quot;06&quot;}\" logId=\"11230\"/><transactionLog created=\"1376766398704\" type=\"CONNECTOR_RESPONSE\" log=\"&quot;{\\&quot;status\\&quot;:\\&quot;declined\\&quot;,\\&quot;customer_id\\&quot;:347102,\\&quot;customer_account_id\\&quot;:346715,\\&quot;transaction_id\\&quot;:1124686,\\&quot;original_transaction_id\\&quot;:1124686,\\&quot;message\\&quot;:\\&quot;declined-transaction\\&quot;,\\&quot;raw_message\\&quot;:\\&quot;\\u003c?xml version\\u003d\\\\\\&quot;1.0\\\\\\&quot; encoding\\u003d\\\\\\&quot;UTF-8\\\\\\&quot;?\\u003e\\\\n\\u003cresponse\\u003e\\\\n\\\\n\\\\n\\u003coperation\\u003e01\\u003c\\\\/operation\\u003e\\u003cresultCode\\u003e1\\u003c\\\\/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c\\\\/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c\\\\/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c\\\\/currency\\u003e\\u003camount\\u003e11.00\\u003c\\\\/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c\\\\/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c\\\\/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c\\\\/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c\\\\/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c\\\\/billingDescriptor\\u003e\\\\n\\\\n\\u003c\\\\/response\\u003e\\&quot;,\\&quot;results\\&quot;:null,\\&quot;pass_through\\&quot;:null}&quot;\" logId=\"11231\"/><transactionLog created=\"1376766398709\" type=\"DESTINATION_RESPONSE\" log=\"&quot;\\u003c?xml version\\u003d\\&quot;1.0\\&quot; encoding\\u003d\\&quot;UTF-8\\&quot;?\\u003e\\n\\u003cresponse\\u003e\\n\\n\\n\\u003coperation\\u003e01\\u003c/operation\\u003e\\u003cresultCode\\u003e1\\u003c/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c/currency\\u003e\\u003camount\\u003e11.00\\u003c/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c/billingDescriptor\\u003e\\n\\n\\u003c/response\\u003e&quot;\" logId=\"11232\"/><transactionLog created=\"1376766398716\" type=\"GATE_RESPONSE\" log=\"{&quot;txId&quot;:&quot;00001G6&quot;,&quot;mcTxId&quot;:&quot;108767&quot;,&quot;code&quot;:&quot;800.900.100&quot;,&quot;message&quot;:&quot;\\u003c?xml version\\u003d\\&quot;1.0\\&quot; encoding\\u003d\\&quot;UTF-8\\&quot;?\\u003e\\n\\u003cresponse\\u003e\\n\\n\\n\\u003coperation\\u003e01\\u003c/operation\\u003e\\u003cresultCode\\u003e1\\u003c/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c/currency\\u003e\\u003camount\\u003e11.00\\u003c/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c/billingDescriptor\\u003e\\n\\n\\u003c/response\\u003e&quot;,&quot;startDate&quot;:&quot;2013-08-17T19:06:35+0000&quot;,&quot;startStamp&quot;:1376766395264,&quot;endDate&quot;:&quot;2013-08-17T19:06:38+0000&quot;,&quot;endStamp&quot;:1376766398711,&quot;deltaMsec&quot;:3447}\" logId=\"11233\"/><transactionLog created=\"1376813019485\" type=\"GATE_RESPONSE\" log=\"{&quot;processResults&quot;:[{&quot;txId&quot;:&quot;00001G6&quot;,&quot;code&quot;:&quot;100.400.304&quot;,&quot;message&quot;:&quot;Attempting to refund declined transaction&quot;}],&quot;code&quot;:&quot;100.400.304&quot;,&quot;version&quot;:&quot;1.0.2-202-2013-08-09_18-16-04&quot;}\" logId=\"11254\"/></searchResults></transactions_response>";
                String responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><transactions_response version=\"1.0.2-202-2013-08-09_18-16-04\" code=\"400.500.000\"><searchResults terminalName=\"\" partnerName=\"\" merchantName=\"Transactworld Ltd.\" currency=\"USD\" creditAmountFloat=\"11.0\" debitAmountFloat=\"0.0\" creditAmount=\"1100\" debitAmount=\"0\" channelName=\"Andaz VISA MC 37\" message=\"\" reference=\"Reference Line 1 Reference Line 2\" code=\"800.900.100\" endStamp=\"1376766398711\" startStamp=\"1376766395264\" type=\"DB\" method=\"CC\" destination=\"Andaz via EPS\" mcTxId=\"108767\" txId=\"00001G6\"/></transactions_response>";
              //String responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><transactions_response version=\"1.0.2-202-2013-08-09_18-16-04\" code=\"400.500.001\"><processResults deltaMsec=\"3447\" endStamp=\"1376766398711\" endDate=\"2013-08-17T19:06:38+0000\" startStamp=\"1376766395264\" startDate=\"2013-08-17T19:06:35+0000\" message=\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#xA;&lt;response&gt;&#xA;&#xA;&#xA;&lt;operation&gt;01&lt;/operation&gt;&lt;resultCode&gt;1&lt;/resultCode&gt;&lt;merNo&gt;10352&lt;/merNo&gt;&lt;billNo&gt;1124686&lt;/billNo&gt;&lt;currency&gt;USD&lt;/currency&gt;&lt;amount&gt;11.00&lt;/amount&gt;&lt;dateTime&gt;20130818030612&lt;/dateTime&gt;&lt;paymentOrderNo&gt;103526640862&lt;/paymentOrderNo&gt;&lt;remark&gt;Payment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)&lt;/remark&gt;&lt;md5Info&gt;4A78C4F84A905C34D775D41D48899ABF&lt;/md5Info&gt;&lt;billingDescriptor&gt;&lt;/billingDescriptor&gt;&#xA;&#xA;&lt;/response&gt;\" code=\"800.900.100\" mcTxId=\"108767\" txId=\"00001G6\"/></transactions_response>";
//              //String responseXML = "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?>\n" +
//                      "<transactions_response version=\\\"1.0.2-202-2013-08-09_18-16-04\\\" code=\\\"400.500.000\\\">\n" +
//                      "<searchResults terminalName=\\\"\\\" partnerName=\\\"\\\" merchantName=\\\"Transactworld Ltd.\\\" currency=\\\"USD\\\" \n" +
//                      "creditAmountFloat=\\\"11.0\\\" debitAmountFloat=\\\"0.0\\\" \n" +
//                      "creditAmount=\\\"1100\\\" debitAmount=\\\"0\\\" channelName=\\\"Andaz VISA MC 37\\\" \n" +
//                      "message=\\\"\\\" reference=\\\"Reference Line 1 Reference Line 2\\\" code=\\\"800.900.100\\\" endStamp=\\\"1376766398711\\\" \n" +
//                      "startStamp=\\\"1376766395264\\\" type=\\\"DB\\\" method=\\\"CC\\\" destination=\\\"Andaz via EPS\\\"\n" +
//                      "mcTxId=\\\"108767\\\" txId=\\\"00001G6\\\">\n" +
//                      "<authorization serviceIP=\\\"115.247.0.9\\\" serviceKey=\\\"f3f3abdb4ca0db2b4ba76deaee9544e44a6861b643e3804f83771435\\\" \n" +
//                      "routingKey=\\\"173e0f75d663be1fe1f4883110bd592114254aea0bb4c245272e60b6\\\"/>\n" +
//                      "<token ccBrand=\\\"VISA\\\" ownerLastName=\\\"latst\\\" ownerFirstName=\\\"tetst\\\" expireMonth=\\\"1\\\" expireYear=\\\"2014\\\" \n" +
//                      "lead6=\\\"411111\\\" trail4=\\\"1122\\\" panAlias=\\\"9080122011001121122\\\" value=\\\"c73ad1fa6c069d6d5cf433cfac502665ba1e02fec2e1cabfe68e338a\\\"/></searchResults></transactions_response>";


              XStream responseXstream = new XStream();
              responseXstream.processAnnotations(transactions_response.class);
              //responseXstream.alias("transactions_response",transactions_response.class);
              //responseXstream.alias("transactions_response/searchResults",transactions_response.class);
              response = (transactions_response) xstream.fromXML(responseXML);

          }catch (Exception e)
          {
              transactionLogger.error("Exception :::::::",e);
              response.setErrorCode("0");
              response.setDescription(e.toString());
          }

          /*System.out.println(" code " + response.getCode());
          System.out.println("version " + response.getVersion());*/


          SearchResults sr = response.getSearchResults();
          //System.out.println(" search " + sr);

//          //List<TransactionLog> tl = sr.getTransactionLog();
//
//          String msg;
//
//          for(TransactionLog tlogs: tl)
//          {
//              String type = tlogs.getType();
//              if(type.equalsIgnoreCase("CONNECTOR_RESPONSE"))
//              {
//                  msg = tlogs.log;
//                  System.out.println(" Msg : " + msg.toString());
//              }
//              //System.out.println("Tlogs " + tlogs.);
//          }
          //System.out.println(response.getProcessResults().getCode());
//        System.out.println(response.getProcessResults().getCode());
      }


}



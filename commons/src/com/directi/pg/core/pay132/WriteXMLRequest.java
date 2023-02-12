package com.directi.pg.core.pay132;

import com.directi.pg.Logger;

import java.util.Map;
import java.util.TreeMap;
import java.io.StringReader;

import static com.directi.pg.core.pay132.Elements.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 5, 2012
 * Time: 9:08:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class WriteXMLRequest
{
     private static Logger log = new Logger(com.directi.pg.core.paylineVoucher.WriteXMLRequest.class.getName());


    /**
         *
         * @param authMap
         * @return
         */
          public static String createAuthReq(Map<String, String> authMap ) {

            StringBuffer authXML = new StringBuffer();

            authXML.append("<AXOPRequest>");

            authXML.append("<MerchantID>"+authMap.get(ELEM_MERCHANT_ID)+"</MerchantID>");
            authXML.append("<TransactionType>SALE</TransactionType>");
            authXML.append("<UserDetail>");
            authXML.append("<CustomerFirstName>"+authMap.get(ELEM_FIRST_NAME)+"</CustomerFirstName>");
            authXML.append("<CustomerLastName>"+authMap.get(ELEM_LAST_NAME)+"</CustomerLastName>");
            authXML.append("<ContactNumber>"+authMap.get(ELEM_PHONE)+"</ContactNumber>");
            authXML.append("<EmailAddress>"+authMap.get(ELEM_EMAIL)+"</EmailAddress>");
            authXML.append("<CustomerIP>"+authMap.get(ELEM_IP)+"</CustomerIP>");
            authXML.append("</UserDetail>");

            authXML.append("<TransactionDetail>");
            authXML.append("<ProductName>"+authMap.get(ELEM_PRODUCT_NAME)+"</ProductName>");
            authXML.append("<OrderNumber>"+authMap.get(ELEM_ORDER_NUMBER)+"</OrderNumber>");
            authXML.append("<Currency>"+authMap.get(ELEM_CURRENCY)+"</Currency>");
            authXML.append("<TotalAmount>"+authMap.get(ELEM_TOTAL_AMOUNT)+"</TotalAmount>");
            authXML.append("</TransactionDetail>");

            authXML.append("<CreditCardDetails>");
            authXML.append("<CardType>"+authMap.get(ELEM_CARD_TYPE)+"</CardType>");
            authXML.append("<CardNumber>"+authMap.get(ELEM_CARD_NUM)+"</CardNumber>");
            authXML.append("<CVV>"+authMap.get(ELEM_CVV)+"</CVV>");
            authXML.append("<CCExpiryMonth>"+authMap.get(ELEM_MONTH)+"</CCExpiryMonth>");
            authXML.append("<CCExpiryYear>"+authMap.get(ELEM_YEAR)+"</CCExpiryYear>");
            authXML.append("</CreditCardDetails>");

            authXML.append("<BillingAddress>");
            authXML.append("<Address1>"+authMap.get(ELEM_ADDRESS1)+"</Address1>");
            authXML.append("<Address2>"+authMap.get(ELEM_ADDRESS2)+"</Address2>");
            authXML.append("<Country>"+authMap.get(ELEM_COUNTRY)+"</Country>");
            authXML.append("<City>"+authMap.get(ELEM_CITY)+"</City>");
            authXML.append("<State>"+authMap.get(ELEM_STATE)+"</State>");
            authXML.append("<ZipCode>"+authMap.get(ELEM_ZIP)+"</ZipCode>");
            authXML.append("</BillingAddress>");

            authXML.append("</AXOPRequest>");


            return authXML.toString();
          }

    public static String createRefundReq(Map<String, String> authMap ) {

                StringBuffer authXML = new StringBuffer();

                authXML.append("<AXOPRequest>");
                authXML.append("<MerchantID>"+authMap.get(ELEM_MERCHANT_ID)+"</MerchantID>");
                authXML.append("<TransactionType>CREDIT</TransactionType>");
                authXML.append("<UserDetail>");
                authXML.append("<CustomerIP>"+authMap.get(ELEM_IP)+"</CustomerIP>");
                authXML.append("</UserDetail>");

                authXML.append("<TransactionDetail>");
                authXML.append("<OrderReferenceNumber>"+authMap.get(ELEM_ORDER_REF_NUMBER)+"</OrderReferenceNumber>");
                authXML.append("<OrderNumber>"+authMap.get(ELEM_ORDER_NUMBER)+"</OrderNumber>");
                authXML.append("<TotalAmount>"+authMap.get(ELEM_TOTAL_AMOUNT)+"</TotalAmount>");
                authXML.append("</TransactionDetail>");

                authXML.append("</AXOPRequest>");


                return authXML.toString();
              }



     public static Document createDocumentFromString(String xmlString ) {

           Document doc = null;

          try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

              // root elements
              doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
          }
         catch (ParserConfigurationException pce) {
		        log.error("ParserConfigurationException---->",pce);
	     }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error("Exception---->", e);
        }

       return doc;

    }   

    public static void main(String[] a)
       {
           //Preparing map for authentication request
           Map<String, String> authMap = new TreeMap<String, String>();

           authMap.put(ELEM_MERCHANT_ID,"1.0");
           authMap.put(ELEM_PRODUCT_NAME,"Goods");
           authMap.put(ELEM_ORDER_NUMBER,"1");
           authMap.put(ELEM_TOTAL_AMOUNT,"20.77");
           authMap.put(ELEM_CURRENCY,"USD");
           authMap.put(ELEM_FIRST_NAME,"Raj");
           authMap.put(ELEM_LAST_NAME,"Agarwal");
           authMap.put(ELEM_PHONE,"9321536456");
           authMap.put(ELEM_EMAIL,"joannefarley@mail.com");
           authMap.put(ELEM_IP,"96.236.7.223");
           authMap.put(ELEM_ADDRESS1,"");
           authMap.put(ELEM_ADDRESS2,"42 Lake Road");
           authMap.put(ELEM_CITY,"Clemons");
           authMap.put(ELEM_COUNTRY,"US");
           authMap.put(ELEM_STATE,"New York");
           authMap.put(ELEM_ZIP,"12819");
           authMap.put(ELEM_CARD_TYPE,"VISA");
           authMap.put(ELEM_CARD_NUM,"");
           authMap.put(ELEM_CVV,"");
           authMap.put(ELEM_MONTH,"");
           authMap.put(ELEM_YEAR,"");





           String request = WriteXMLRequest.createAuthReq(authMap);

           System.out.println(request);
       }

}

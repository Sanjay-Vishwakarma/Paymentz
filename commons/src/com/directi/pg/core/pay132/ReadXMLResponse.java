package com.directi.pg.core.pay132;

import static com.directi.pg.core.pay132.Elements.*;

import com.directi.pg.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.TreeMap;
import java.util.Set;


import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 5, 2012
 * Time: 9:50:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReadXMLResponse
{
    private static Logger log = new Logger(ReadXMLResponse.class.getName());

     public static Map<String, String> ReadXMLResponseForAuth(Document doc) {

           Map<String, String> responseMap  = new TreeMap<String, String>();
          try {



            NodeList nList = doc.getElementsByTagName("Responses");
            responseMap.put(ELEM_RESPONSE,getTagValue(ELEM_RESPONSE,((Element) nList.item(0))));
            responseMap.put(ELEM_MERCHANT_ID,getTagValue(ELEM_MERCHANT_ID,((Element) nList.item(0))));
            responseMap.put(ELEM_TRANSACTION_ID,getTagValue(ELEM_TRANSACTION_ID,((Element) nList.item(0))));
            responseMap.put(ELEM_ORDER_NUMBER,getTagValue(ELEM_ORDER_NUMBER,((Element) nList.item(0))));
            responseMap.put(ELEM_AMOUNT,getTagValue(ELEM_AMOUNT,((Element) nList.item(0))));
            responseMap.put(ELEM_TRANSACTION_DATE_TIME,getTagValue(ELEM_TRANSACTION_DATE_TIME,((Element) nList.item(0))));
            responseMap.put(ELEM_ERROR_NO,getTagValue(ELEM_ERROR_NO,((Element) nList.item(0))));
            responseMap.put(ELEM_ERROR_DESCRIPTION,getTagValue(ELEM_ERROR_DESCRIPTION,((Element) nList.item(0))));


          }
          catch (Exception e) {
            log.error("Exception----->",e);
          }

        return responseMap;
      }



    private static String getTagValue(String sTag, Element eElement) {

           NodeList nlList = null;
           String value  ="";
           if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
           {
                nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
           }
           if(nlList!=null && nlList.item(0)!=null)
           {
               Node nValue = (Node) nlList.item(0);
               value =	nValue.getNodeValue();

           }

           return value;

     }

     private static String getAttributeValue(Element eElement, String sTag) {


           String value  ="";

           if(eElement!=null)
           {

               value =	eElement.getAttribute(sTag);

           }

           return value;

     }



    public static void main(String[] a)
      {


          StringBuffer response = new StringBuffer();

          response.append("<132211Response>");
          response.append("<Response>Success</Response>");
          response.append("<MerchantID>1</MerchantID>");
          response.append("<TransactionId>1</TransactionId>");
          response.append("<OrderNumber>1</OrderNumber>");
          response.append("<Amount>10</Amount>");
          response.append("<TransactionDate>10-10-2012</TransactionDate>");
          response.append("</132211Response>");

          String responseXml =(response.toString()).replaceAll("132211Response","Responses");
          Document doc =null;



          try{

            doc = com.directi.pg.core.pay132.WriteXMLRequest.createDocumentFromString(responseXml);



            /* // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File("C:\\file.xml"));

            // Output to console for testing
             StreamResult result = new StreamResult(System.out);

             transformer.transform(source, result);
*/
          } catch (Exception e) {
            }


       Map<String, String> responseMap =  ReadXMLResponseForAuth(doc);

       Set<String>  keys = responseMap.keySet();
       log.debug("  ");
       for(String key : keys)
       {
           log.debug("  "+key+"="+responseMap.get(key));
       }


         //Error response
          StringBuffer response2 = new StringBuffer();

                   response2.append("<132211Response>");
                   response2.append("<Response>Failed</Response>");
                   response2.append("<MerchantID>{MERCHANT_ID}</MerchantID>");
                   response2.append("<ErrorCode>{ERROR_NO}</ErrorCode>");
                   response2.append("<ErrorDesc>{ERROR_DESCRIPTION} </ErrorDesc>");
                   response2.append("</132211Response>");


                   Document doc2 =null;

                   String responseXml2 =(response2.toString()).replaceAll("132211Response","Responses");

                   try{
                     doc2 = com.directi.pg.core.pay132.WriteXMLRequest.createDocumentFromString(responseXml2);





                   } catch (Exception e) {
                     }


                Map<String, String> responseMap2 =  ReadXMLResponseForAuth(doc2);

                Set<String>  keys2 = responseMap2.keySet();
                log.debug("  ");
                for(String key : keys2)
                {
                    log.debug("  "+key+"="+responseMap2.get(key));
                }


      }






}

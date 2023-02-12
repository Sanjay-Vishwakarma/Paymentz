package com.directi.pg.core.paylineVoucher;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import static com.directi.pg.core.paylineVoucher.Elements.*;

import com.directi.pg.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 28, 2012
 * Time: 10:00:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReadXMLResponse
{
    private static Logger logger=new Logger(ReadXMLResponse.class.getName());


    public static Map<String, String> ReadXMLResponseForAuth(Document doc) {


           Map<String, String> responseMap  = new TreeMap<String, String>();
          try {


            NodeList nList = doc.getElementsByTagName(ELEM_RESPONSE);

            responseMap.put(ATTR_VERSION,getAttributeValue((Element) nList.item(0),ATTR_VERSION));

            nList = doc.getElementsByTagName(ELEM_TRANSACTION);

            responseMap.put(ATTR_MODE,getAttributeValue((Element) nList.item(0),ATTR_MODE));

            responseMap.put(ATTR_CHANNEL,getAttributeValue((Element) nList.item(0),ATTR_CHANNEL));

            nList = doc.getElementsByTagName(ELEM_IDENTIFICATION);

            responseMap.put(ELEM_SHORTID,getTagValue(ELEM_SHORTID,((Element) nList.item(0))));

            responseMap.put(ELEM_UNIQUEID,getTagValue(ELEM_UNIQUEID,((Element) nList.item(0))));

            responseMap.put(ELEM_TRANSACTIONID,getTagValue(ELEM_TRANSACTIONID,((Element) nList.item(0))));

            responseMap.put(ELEM_REFERENCEID,getTagValue(ELEM_REFERENCEID,((Element) nList.item(0))));  

            nList = doc.getElementsByTagName(ELEM_PAYMENT);

            responseMap.put(ATTR_PAYMENTCODE,getAttributeValue((Element) nList.item(0),ATTR_CODE));

            nList = doc.getElementsByTagName(ELEM_CLEARING);

            responseMap.put(ELEM_AMOUNT,getTagValue(ELEM_AMOUNT,((Element) nList.item(0))));

            responseMap.put(ELEM_CURRENCY,getTagValue(ELEM_CURRENCY,((Element) nList.item(0))));

            responseMap.put(ELEM_DESCRIPTOR,getTagValue(ELEM_DESCRIPTOR,((Element) nList.item(0))));
            responseMap.put(ELEM_FX_RATE,getTagValue(ELEM_FX_RATE,((Element) nList.item(0))));
            responseMap.put(ELEM_FX_SOURCE,getTagValue(ELEM_FX_SOURCE,((Element) nList.item(0))));
            responseMap.put(ELEM_FX_DATE,getTagValue(ELEM_FX_DATE,((Element) nList.item(0))));

            nList = doc.getElementsByTagName(ELEM_PROCESSING);

            responseMap.put(ATTR_PROCESSINGCODE,getAttributeValue((Element) nList.item(0),ATTR_CODE));

            responseMap.put(ELEM_TIMESTAMP,getTagValue(ELEM_TIMESTAMP,((Element) nList.item(0))));

            responseMap.put(ELEM_RESULT,getTagValue(ELEM_RESULT,((Element) nList.item(0))));

            responseMap.put(ELEM_STATUS,getTagValue(ELEM_STATUS,((Element) nList.item(0))));

            responseMap.put(ATTR_STATUSCODE,getAttributeValue((Element) doc.getElementsByTagName(ELEM_STATUS).item(0),ATTR_CODE));

            responseMap.put(ELEM_REASON,getTagValue(ELEM_REASON,((Element) nList.item(0))));

            responseMap.put(ATTR_REASONCODE,getAttributeValue((Element) doc.getElementsByTagName(ELEM_REASON).item(0),ATTR_CODE));

            responseMap.put(ELEM_RETURN,getTagValue(ELEM_RETURN,((Element) nList.item(0))));

            responseMap.put(ATTR_RETURNCODE,getAttributeValue((Element) doc.getElementsByTagName(ELEM_RETURN).item(0),ATTR_CODE));

            responseMap.put(ELEM_RISK,getTagValue(ELEM_RISK,((Element) nList.item(0))));

            responseMap.put(ATTR_SCORE,getAttributeValue((Element) doc.getElementsByTagName(ELEM_RISK).item(0),ATTR_SCORE));

          } catch (Exception e) {
              logger.error("Exception---->",e);
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
        Logger logger = new Logger(ReadXMLResponse.class.getName());

        StringBuffer response = new StringBuffer();

        response.append("<Response version=\"1.0\">");
        response.append("<Transaction mode=\"INTEGRATOR_TEST\" channel=\"d225a9fe02636170010263617594000f\">");
        response.append("<Identification>");
        response.append("<ShortID>8367.2038.4674</ShortID>");
        response.append("<UniqueID>ff80808139b9b3730139bdc75afd48a1</UniqueID>");
        response.append("<TransactionID>11111111111</TransactionID>");
        response.append("</Identification>");
        response.append("<Payment code=\"VA.DB\">");
        response.append("<Clearing>");
        response.append("<Amount>21.00</Amount>");
        response.append("<Currency>USD</Currency>");
        response.append("<Descriptor>8367.2038.4674 DEFAULT </Descriptor>");
        response.append("<FxRate>1.0</FxRate>");
        response.append("<FxSource>INTERN</FxSource>");
        response.append("<FxDate>2012-09-13 03:58:42</FxDate>");
        response.append("</Clearing>");
        response.append("</Payment>");
        response.append("<Processing code=\"VA.DB.90.00\">");
        response.append("<Timestamp>2007-10-09 12:24:55</Timestamp>");
        response.append("<Result>ACK</Result>");
        response.append("<Status code=\"90\">NEW</Status>");
        response.append("<Reason code=\"00\">Successful Processing</Reason>");
        response.append("<Return code=\"000.100.112\">Request successfully processed</Return>");
        response.append("<Risk score=\"0\" />");
        response.append("</Processing>");
        response.append("</Transaction>");
        response.append("</Response>");


        Document doc =null;



        try{
          doc = WriteXMLRequest.createDocumentFromString(response.toString());
           


           // write the content into xml file
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
          DOMSource source = new DOMSource(doc);
          //StreamResult result = new StreamResult(new File("C:\\file.xml"));

          // Output to console for testing
           StreamResult result = new StreamResult(System.out);

           transformer.transform(source, result);

        } catch (Exception e) {
          }


     Map<String, String> responseMap =  ReadXMLResponseForAuth(doc);

     Set<String>  keys = responseMap.keySet();
        logger.debug("  ");
     for(String key : keys)
     {
         logger.debug("  "+key+"="+responseMap.get(key));
     }

    }













}

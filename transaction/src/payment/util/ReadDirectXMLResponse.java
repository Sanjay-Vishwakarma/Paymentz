package payment.util;

import com.directi.pg.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/8/14
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadDirectXMLResponse
{
    private static Logger logger = new Logger(ReadDirectXMLResponse.class.getName());
    public static Map<String, String> ReadXMLResponseForSales(String str) {

        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            NodeList nList = doc.getElementsByTagName("Response");
            responseMap.put("action",getAttributeValue((Element) nList.item(0),"action"));

            responseMap.put("orderid",getTagValue("orderid",((Element) nList.item(0))));
            responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
            responseMap.put("statusdescription",getTagValue("statusdescription",((Element) nList.item(0))));
            responseMap.put("trackingid",getTagValue("trackingid",((Element) nList.item(0))));
            responseMap.put("amount",getTagValue("amount",((Element) nList.item(0))));
            responseMap.put("newchecksum",getTagValue("newchecksum",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            logger.error("Exception in ReadDirectXMLResponse :::::: ",e);
        }
        return responseMap;
    }

    public static Map<String, String> ReadXMLResponseForRefund(String str) {

        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            NodeList nList = doc.getElementsByTagName("Response");
            responseMap.put("action",getAttributeValue((Element) nList.item(0),"action"));

            responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
            responseMap.put("statusdescription",getTagValue("statusdescription",((Element) nList.item(0))));
            responseMap.put("trackingid",getTagValue("trackingid",((Element) nList.item(0))));
            responseMap.put("refundamount",getTagValue("refundamount",((Element) nList.item(0))));
            responseMap.put("newchecksum",getTagValue("newchecksum",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            logger.error("Exception in ReadXMLResponseForRefund ReadDirectXMLResponse :::::: ", e);
        }
        return responseMap;
    }

    public static Map<String, String> ReadXMLResponseForStatus(String str) {

        Document doc = createDocumentFromString(str);

        Map<String, String> responseMap  = new HashMap<String, String>();
        try
        {
            NodeList nList = doc.getElementsByTagName("Response");
            responseMap.put("action",getAttributeValue((Element) nList.item(0),"action"));

            responseMap.put("orderid",getTagValue("orderid",((Element) nList.item(0))));
            responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
            responseMap.put("statusdescription",getTagValue("statusdescription",((Element) nList.item(0))));
            responseMap.put("trackingid",getTagValue("trackingid",((Element) nList.item(0))));
            responseMap.put("authamount",getTagValue("authamount",((Element) nList.item(0))));
            responseMap.put("captureamount",getTagValue("captureamount",((Element) nList.item(0))));
            responseMap.put("newchecksum",getTagValue("newchecksum",((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            logger.error("Exception in ReadXMLResponseForStatus ReadDirectXMLResponse :::::: ", e);
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

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value  ="";
        if(eElement!=null)
        {
            value =	eElement.getAttribute(sTag);
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString ) {

        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            logger.error("ParserConfigurationException in ReadDirectXMLResponse :::::: ", pce);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            logger.error("Exception in createDocumentFromString ReadDirectXMLResponse :::::: ", e);
        }
        return doc;
    }

    public static void main(String[] a)
    {
        StringBuffer readXml = new StringBuffer();

        /*readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<Response action=\"sale\">");
        readXml.append("<orderid>11111</orderid>");
        readXml.append("<status>success</status>");
        readXml.append("<statusdescription>asdf324f</statusdescription>");
        readXml.append("<trackingid>1927</trackingid>");
        readXml.append("<amount>30.50</amount>");
        readXml.append("<newchecksum>ewqerqw23413YG786876yghj</newchecksum>");
        readXml.append("</Response>");*/

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<Response action=\"refund\">");
        readXml.append("<status>success</status>");
        readXml.append("<statusdescription>asdf324f</statusdescription>");
        readXml.append("<trackingid>1927</trackingid>");
        readXml.append("<refundamount>30.50</refundamount>");
        readXml.append("<newchecksum>ewqerqw23413YG786876yghj</newchecksum>");
        readXml.append("</Response>");


        /*readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<Response action=\"status\">");
        readXml.append("<orderid>11111</orderid>");
        readXml.append("<status>success</status>");
        readXml.append("<statusdescription>asdf324f</statusdescription>");
        readXml.append("<trackingid>1927</trackingid>");
        readXml.append("<authamount>30.50</authamount>");
        readXml.append("<captureamount>30.50</captureamount>");
        readXml.append("<newchecksum>ewqerqw23413YG786876yghj</newchecksum>");
        readXml.append("</Response>");*/

        Document doc =null;



        /* try{
            doc = createDocumentFromString(response.toString());



            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File("C:\\file.xml"));

            // Output to console for testing
            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        //Map<String, String> responseMap =  ReadXMLResponseForSales(readXml.toString());
        Map<String, String> responseMap =  ReadXMLResponseForRefund(readXml.toString());
       // Map<String, String> responseMap =  ReadXMLResponseForStatus(readXml.toString());

        Set<String> keys = responseMap.keySet();
        System.out.println("  ");
        for(String key : keys)
        {
            System.out.println("  "+key+"="+responseMap.get(key));
        }

    }


}

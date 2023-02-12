package payment.util;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadXMLRequestInvoice
{
   private static Logger log = new Logger(ReadXMLRequestInvoice.class.getName());
   private static TransactionLogger transactionLogger = new TransactionLogger(ReadXMLRequestInvoice.class.getName());



   public static String getAction(Document doc)
     {

         NodeList nList=doc.getElementsByTagName("invoice");

         String action=getAttributeValue((Element) nList.item(0),"action");

         return action;
     }

    public static Hashtable<String,String> readRequestForGenerate(Document doc)
    {
        Hashtable hash=new Hashtable();
        try
        {

            NodeList nList=doc.getElementsByTagName("invoice");

            String action=getAttributeValue((Element) nList.item(0),"action");

            String type=getAttributeValue((Element) nList.item(0),"type");

            String version=getAttributeValue((Element) nList.item(0),"version");

            if("generate".equals(action))
            {

                hash.put("action",action);

                hash.put("type",type);

                hash.put("version",version);

                hash.put("memberid",getTagValue("memberid",((Element) nList.item(0))));

                hash.put("orderid",getTagValue("orderid",((Element) nList.item(0))));

                hash.put("amount",getTagValue("amount",((Element) nList.item(0))));

                hash.put("orderdesc",getTagValue("orderdescription",((Element) nList.item(0))));

                hash.put("custname",getTagValue("custname",((Element) nList.item(0))));

                hash.put("paymodeid",getTagValue("paymodetype",((Element) nList.item(0))));

                hash.put("currency",getTagValue("currency",((Element) nList.item(0))));

                hash.put("country",getTagValue("countrycode",((Element) nList.item(0))));

                hash.put("city",getTagValue("city",((Element) nList.item(0))));

                hash.put("state",getTagValue("state",((Element) nList.item(0))));

                hash.put("zipcode",getTagValue("zip",((Element) nList.item(0))));

                hash.put("address",getTagValue("street",((Element) nList.item(0))));

                hash.put("phone",getTagValue("telno",((Element) nList.item(0))));

                hash.put("phonecc",getTagValue("telnocc",((Element) nList.item(0))));

                hash.put("custemail",getTagValue("emailaddr",((Element) nList.item(0))));

                hash.put("redirecturl",getTagValue("redirecturl",((Element) nList.item(0))));

                hash.put("checksum",getTagValue("checksum",((Element) nList.item(0))));

            }

        }
        catch (Exception e)
        {
            log.error(e);
            transactionLogger.error(e);
        }


        return hash;
    }


    public static Hashtable<String,String> readRequestForCancel(Document doc)
    {
        Hashtable hash=new Hashtable();
        try
        {

            NodeList nList=doc.getElementsByTagName("invoice");

            String action=getAttributeValue((Element) nList.item(0),"action");

            String type=getAttributeValue((Element) nList.item(0),"type");

            String version=getAttributeValue((Element) nList.item(0),"version");

            hash.put("action",action);

            hash.put("type",type);

            hash.put("version",version);

            hash.put("memberid",getTagValue("memberid",((Element) nList.item(0))));

            hash.put("invoiceno",getTagValue("invoiceno",((Element) nList.item(0))));

            hash.put("cancelreason",getTagValue("cancelreason",((Element) nList.item(0))));

            hash.put("redirecturl",getTagValue("redirecturl",((Element) nList.item(0))));

            hash.put("checksum",getTagValue("checksum",((Element) nList.item(0))));


        }
        catch (Exception e)
        {

            log.error(e);
            transactionLogger.error(e);

        }


        return hash;
    }

    public static Hashtable<String,String> readRequestForRegenerate(Document doc)throws ParserConfigurationException
    {
        Hashtable hash=new Hashtable();
        try
        {

            NodeList nList=doc.getElementsByTagName("invoice");

            String action=getAttributeValue((Element) nList.item(0),"action");

            String type=getAttributeValue((Element) nList.item(0),"type");

            String version=getAttributeValue((Element) nList.item(0),"version");

            hash.put("action",action);

            hash.put("type",type);

            hash.put("version",version);

            hash.put("memberid",getTagValue("memberid",((Element) nList.item(0))));

            hash.put("invoiceno",getTagValue("invoiceno",((Element) nList.item(0))));

            hash.put("redirecturl",getTagValue("redirecturl",((Element) nList.item(0))));

            hash.put("checksum",getTagValue("checksum",((Element) nList.item(0))));


        }
        catch (Exception e)
        {

            log.error(e);
            transactionLogger.error(e);

        }


        return hash;
    }

    public static Hashtable<String,String> readRequestForRemind(Document doc)throws ParserConfigurationException
    {
        Hashtable hash=new Hashtable();
        try
        {

            NodeList nList=doc.getElementsByTagName("invoice");

            String action=getAttributeValue((Element) nList.item(0),"action");

            String type=getAttributeValue((Element) nList.item(0),"type");

            String version=getAttributeValue((Element) nList.item(0),"version");

            hash.put("action",action);

            hash.put("type",type);

            hash.put("version",version);

            hash.put("memberid",getTagValue("memberid",((Element) nList.item(0))));

            hash.put("invoiceno",getTagValue("invoiceno",((Element) nList.item(0))));

            hash.put("redirecturl",getTagValue("redirecturl",((Element) nList.item(0))));

            hash.put("checksum",getTagValue("checksum",((Element) nList.item(0))));

        }
        catch (Exception e)
        {
            log.error(e);
            transactionLogger.error(e);
        }

        return hash;
    }



    private static String getTagValue(String sTag, Element eElement)
    {

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

    public static Document createDocumentFromString(String xmlString )
    {

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
            transactionLogger.error("ParserConfigurationException In ReadXMLRequestInvoice ",pce);
        }
        catch (Exception e)
        {
            // Auto-generated catch block
            transactionLogger.error("ParserConfigurationException In ReadXMLRequestInvoice ",e);
        }

        return doc;

    }


}

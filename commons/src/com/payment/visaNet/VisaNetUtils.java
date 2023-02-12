package com.payment.visaNet;

import com.directi.pg.Logger;
import com.manager.enums.PZTransactionCurrency;
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

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 5/30/15
 * Time: 01:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisaNetUtils
{
    private static Logger logger=new Logger(VisaNetUtils.class.getName());
    public HashMap readVisaNetResponse(String responseXML)
    {
        Document doc = null;
        HashMap hashMap=new HashMap();
        doc = createDocumentFromString(responseXML);
        NodeList nList = doc.getElementsByTagName("pedido");
        for (int i= 0; i< nList.getLength(); i++)
        {
            Node fstNode = nList.item(i);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) fstNode;
                NodeList fstElement = element.getElementsByTagName("campo");
                for(int j=0;j<fstElement.getLength();j++)
                {
                    String value="";
                    String idName="";

                    Element ele = (Element) fstElement.item(j);
                    NodeList fstNm = ele.getChildNodes();

                    idName=getAttributeValue((Element) fstElement.item(j), "id");
                    if(fstNm.getLength()>0 && fstNm.item(0)!=null)
                    {
                        value=((Node) fstNm.item(0)).getNodeValue();
                    }
                    hashMap.put(idName,value);
                }
            }

        }

        nList = doc.getElementsByTagName("mensajes");
        for (int i= 0; i< nList.getLength(); i++)
        {
            Node fstNode = nList.item(i);
            if (fstNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) fstNode;
                NodeList fstElement = element.getElementsByTagName("mensaje");
                for(int j=0;j<fstElement.getLength();j++)
                {
                    String value="";
                    String idName="";

                    Element ele = (Element) fstElement.item(j);
                    NodeList fstNm = ele.getChildNodes();

                    idName=getAttributeValue((Element) fstElement.item(j), "id");
                    if(fstNm.getLength()>0 && fstNm.item(0)!=null)
                    {
                        value=((Node) fstNm.item(0)).getNodeValue();
                    }
                    hashMap.put(idName,value);
                }
            }
        }
        return hashMap;
    }
    public String getVisaNetCurrencyCode(PZTransactionCurrency status)
    {
        String currencyCode="";
        switch(status)
        {
            case USD:
                currencyCode ="840";
                break;
            case PEN:
                currencyCode="604";
                break;
            default:
                break;
        }
        return currencyCode;
    }
    public PZTransactionCurrency getPZTransactionCurrency(String currency)
    {
        PZTransactionCurrency transactionCurrency=null;

        if(PZTransactionCurrency.USD.toString().equals(currency))        {
            transactionCurrency=PZTransactionCurrency.USD;
        }
        if(PZTransactionCurrency.PEN.toString().equals(currency))        {
            transactionCurrency=PZTransactionCurrency.PEN;
        }
        return  transactionCurrency;

    }
    public static Document createDocumentFromString(String xmlString )
    {

        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce) {
            logger.error("ParserConfigurationException--->",pce);
        }
        catch (Exception e){
            logger.error("Exception--->",e);
        }
        return doc;
    }
    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value  ="";
        if(eElement!=null)
        {
            value =	eElement.getAttribute(sTag);
            eElement.getAttributeNode(value);
        }
        return value;
    }
}

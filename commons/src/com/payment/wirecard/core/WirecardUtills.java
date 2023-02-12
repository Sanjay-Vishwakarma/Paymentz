package com.payment.wirecard.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Admin on 16-Aug-18.
 */
public class WirecardUtills
{
    private static Logger log = new Logger(WirecardUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WirecardUtills.class.getName());
    public static Comm3DResponseVO ReadSalesResponse(String str) throws PZTechnicalViolationException
    {
        Document doc = createDocumentFromString(str);

        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();

        doc.getDocumentElement().getNodeName();
        NodeList nList = doc.getElementsByTagName("DIVITIA_BXML");
        nList = doc.getElementsByTagName("W_RESPONSE");

        System.out.println("GuWID---"+getTagValue("StatusType", ((Element) nList.item(0))));
        System.out.println("AcsUrl---"+getTagValue("AcsUrl", ((Element) nList.item(0))));

        //Y-Enrolled
        //N-Not Enrolled
        //U-Ineligible
        //E-Error

        comm3DResponseVO.setStatus(getTagValue("StatusType", ((Element) nList.item(0))));
        comm3DResponseVO.setPaReq(getTagValue("PAReq", ((Element) nList.item(0))));
        comm3DResponseVO.setUrlFor3DRedirect(getTagValue("AcsUrl", ((Element) nList.item(0))));

        /*responseMap.put("merchant-account-id", getTagValue("merchant-account-id", ((Element) nList.item(0))));
        responseMap.put("transaction-id", getTagValue("transaction-id", ((Element) nList.item(0))));
        responseMap.put("request-id", getTagValue("request-id", ((Element) nList.item(0))));
        responseMap.put("transaction-type", getTagValue("transaction-type", ((Element) nList.item(0))));
        responseMap.put("transaction-state", getTagValue("transaction-state", ((Element) nList.item(0))));
        responseMap.put("transaction-type", getTagValue("transaction-type", ((Element) nList.item(0))));
        responseMap.put("statuses", getTagValue("statuses", ((Element) nList.item(0))));
        responseMap.put("card-token", getTagValue("card-token", ((Element) nList.item(0))));
        responseMap.put("parent-transaction-id", getTagValue("parent-transaction-id", ((Element) nList.item(0))));
        responseMap.put("completion-time-stamp", getTagValue("completion-time-stamp", ((Element) nList.item(0))));
        responseMap.put("status code", getTagValue("status code", ((Element) nList.item(0))));
        responseMap.put("description", getTagValue("description", ((Element) nList.item(0))));
        responseMap.put("pareq", getTagValue("pareq", ((Element) nList.item(0))));
        responseMap.put("acs-url", getTagValue("acs-url", ((Element) nList.item(0))));
        responseMap.put("authorization-code", getTagValue("authorization-code", ((Element) nList.item(0))));*/
        return comm3DResponseVO;
    }

    public static void main(String[] args)
    {
        try
        {
            WirecardUtills w = new WirecardUtills();
            String readS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<WIRECARD_BXML xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"wirecard.xsd\">\n" +
                    "\t<W_RESPONSE>\n" +
                    "\t\t<W_JOB>\n" +
                    "\t\t\t<JobID>71788987</JobID>\n" +
                    "\t\t\t<FNC_CC_ENROLLMENT_CHECK>\n" +
                    "\t\t\t\t<FunctionID>71788</FunctionID>\n" +
                    "\t\t\t\t<CC_TRANSACTION>\n" +
                    "\t\t\t\t\t<TransactionID>71788</TransactionID>\n" +
                    "\t\t\t\t\t<PROCESSING_STATUS>\n" +
                    "\t\t\t\t\t\t<GuWID>C805173153442602181048</GuWID>\n" +
                    "\t\t\t\t\t\t<AuthorizationCode></AuthorizationCode>\n" +
                    "\t\t\t\t\t\t<StatusType>Y</StatusType>\n" +
                    "\t\t\t\t\t\t<FunctionResult>ACK</FunctionResult>\n" +
                    "\t\t\t\t\t\t<TimeStamp>2018-08-16 15:27:01</TimeStamp>\n" +
                    "\t\t\t\t\t</PROCESSING_STATUS>\n" +
                    "\t\t\t\t\t<THREE-D_SECURE>\n" +
                    "\t\t\t\t\t\t<PAReq>eJxtk9tum0AQhu/zFIj7sLNrjtayUVy3qtW6jRLnoNzRZRSIDDgL1E6fvgN2DjYgIe3MfrD//jMjL3bF2vqLps6rMra5A7aFpa7SvHyK7dvVt/PQtuomKdNkXZUY269Y2xfqTK4ygzi/Qd0aVGcWPXKJdZ08oZWnsT3hPIq8IHQBAhF5ruty395zPXt1eY0vH3GfO6hQJMIRkr2Fx9ASjc6SsjlO91uJfpktfikuJq7nS3YIh1yBZjFXHgBwekGyfWIIlkmB6j43qBOTWnMsKusmqzaS9RtDXldt2ZhXFQo6/i0YYq1Zq6xpNlPGttutsz0c4OiqkKzbPL4xG7+yvGq7dD0mZJenavn82Kzumx+/H65/6oe72z9f7zL9PJs9/lvEknXE8Ls0aVAJ4CGE3Le4NxXBFKgUfX7E8KK7owLHI7f36yG0Oci83AMe+X2SGjGyNYa6kJx0CX+PhiDuNtSV9A8S+b4+sW/cJ/nl+2gL6YZaAbgXCB/ciU8tDAEIWoKYCA5+1DVWD42qyaniPALey8lPyy/Z51NJ2scUdGXux4dmix0P139sded0</PAReq>\n" +
                    "\t\t\t\t\t\t<AcsUrl>https://c3-test.wirecard.com/acssim/app/bank</AcsUrl>\n" +
                    "\t\t\t\t\t</THREE-D_SECURE>\n" +
                    "\t\t\t\t</CC_TRANSACTION>\n" +
                    "\t\t\t</FNC_CC_ENROLLMENT_CHECK>\n" +
                    "\t\t</W_JOB>\n" +
                    "\t</W_RESPONSE>\n" +
                    "</WIRECARD_BXML>";

            ReadSalesResponse(readS);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(sTag) != null && eElement.getElementsByTagName(sTag).item(0) != null)
        {
            nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null)
        {
            Node nValue = (Node) nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value = "";
        if (eElement != null)
        {
            value = eElement.getAttribute(sTag);
        }
        return value;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of WirecardUtills", pce);
            transactionLogger.error("Exception in createDocumentFromString of WirecardUtills", pce);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString WirecardUtills", e);
            transactionLogger.error("Exception in createDocumentFromString WirecardUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            log.error("Exception in createDocumentFromString WirecardUtills", e);
            transactionLogger.error("Exception in createDocumentFromString WirecardUtills", e);
            PZExceptionHandler.raiseTechnicalViolationException("WirecardUtills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;
    }
}

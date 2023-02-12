/*
package practice;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
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

*/
/**
 * Created by Admin on 4/20/2020.
 *//*

public class api_specs_wdout_main_Utils
{
    private static Logger log = new Logger(JPBankTransfer_Utils.class.getName());

    private static TransactionLogger transactionLogger = new TransactionLogger(JPBankTransfer_Utils.class.getName());
    Functions functions =new Functions();
    public JPBankTransferVO readResponse(String response) throws PZTechnicalViolationException

    {
        transactionLogger.error("Inside readResponse ---");
        JPBankTransferVO jpBankTransferVO = new JPBankTransferVO();
        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList nList = doc.getElementsByTagName("item");
            //     getTagValue("item", ((Element) nList.item(0)));
            jpBankTransferVO.setShitenNm(getTagValue("shitenNm", ((Element) nList.item(0))));
            jpBankTransferVO.setBankName(getTagValue("bankName", ((Element) nList.item(0))));
            jpBankTransferVO.setShitenName(getTagValue("shitenName", ((Element) nList.item(0))));
            jpBankTransferVO.setKouzaType(getTagValue("kouzaType", ((Element) nList.item(0))));
            jpBankTransferVO.setKouzaNm(getTagValue("kouzaNm", ((Element) nList.item(0))));
            jpBankTransferVO.setKouzaMeigi(getTagValue("kouzaMeigi", ((Element) nList.item(0))));
            jpBankTransferVO.setBid(getTagValue("bid", ((Element) nList.item(0))));
            jpBankTransferVO.setTel(getTagValue("tel", ((Element) nList.item(0))));
            jpBankTransferVO.setEmail(getTagValue("email", ((Element) nList.item(0))));
            jpBankTransferVO.setCompany(getTagValue("company", ((Element) nList.item(0))));
            jpBankTransferVO.setNameId(getTagValue("nameId", ((Element) nList.item(0))));
            jpBankTransferVO.setResult(getTagValue("result", ((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return jpBankTransferVO;
    }
    private Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside createDocumentFromString ---");
        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader(xmlString.toString().trim())));
        }
        catch (ParserConfigurationException pce)
        {
            log.error("Exception in createDocumentFromString of ECPUtils", pce);
            transactionLogger.error("Exception in createDocumentFromString of ECPUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            log.error("Exception in createDocumentFromString ECPUtils", e);
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            log.error("Exception in createDocumentFromString ECPUtils", e);
            transactionLogger.error("Exception in createDocumentFromString ECPUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("ECPUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return doc;

    }
    private  String getTagValue(String sTag, Element eElement)
    {
        // transactionLogger.error("Inside getTagValue ---");
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
}*/

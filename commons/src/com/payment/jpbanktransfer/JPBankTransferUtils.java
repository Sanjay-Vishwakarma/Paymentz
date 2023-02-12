package com.payment.jpbanktransfer;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.JPBankTransferVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sagar Sonar on 21-April-2020.
 */
public class JPBankTransferUtils
{
    private static Logger log = new Logger(JPBankTransferUtils.class.getName());

    private static TransactionLogger transactionLogger = new TransactionLogger(JPBankTransferUtils.class.getName());
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
            jpBankTransferVO.setStatus("Pending");
            jpBankTransferVO.setRemark("Bank Transfer Initiated");
            jpBankTransferVO.setDescription("Bank Transfer Initiated");
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return jpBankTransferVO;
    }

    public String getRedirectForm(CommResponseVO response3D)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        html.append("</form>");
        return html.toString();
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug(name+":::"+value);
            }
        }
        return map;
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
    public static String doHttpPostConnection(String url)
    {
        String result = "";
        PostMethod post = new PostMethod(url);

        try
        {
            HttpClient httpClient = new HttpClient();
            //post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            //post.setRequestBody(saleRequest);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("HttpException --->", e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException --->",e);
        }
        return result;
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }
    public static String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKRWAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---")
        ;
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }
    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside JPBank Transfer getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        Functions functions= new Functions();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
//        addressDetailsVO.setEmail(commonValidatorVO.getCustEmail());
        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());
        if(functions.isValueNull(genericAddressDetailsVO.getCustomerid()))
            addressDetailsVO.setCustomerid(genericAddressDetailsVO.getCustomerid());
        else
            addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }


}
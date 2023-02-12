package com.directi.pg.core.payon;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.payon.core.PayOnCardDetailsVO;
import com.payment.payon.core.PayOnMerchantAccountVO;
import com.payment.payon.core.PayOnResponseVO;
import com.payment.payon.core.PayOnVBVDetailsVO;
import com.payment.payon.core.message.*;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
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

/**
 * Created with IntelliJ IDEA.
 * User: Roshan
 * Date: 11/25/17
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnUtils
{
    public final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger = new TransactionLogger(PayOnUtils.class.getName());

    private static String getChildAttributeValue(NodeList list, String sTag)
    {
        String value  ="";
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                value =  getAttributeValue(element,sTag);
            }
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

    public PayOnResponseVO getPayonResponseVOI(String xmlResponseString)throws PZTechnicalViolationException
    {
        String status="fail";
        String statusdescription="testDescription";
        PayOnResponseVO payOnResponseVO= new PayOnResponseVO();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        NodeList list=null;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayOnUtils.java", "getPayonResponseVOI()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayOnUtils.java","getPayonResponseVOI()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayOnUtils.java","getPayonResponseVOI()",null,"common","Technical Exception:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }

        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("response", rootElement);
        list = document.getElementsByTagName("Identification");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                payOnResponseVO.setShortId(getTagValue("ShortID", element));
                payOnResponseVO.setUUID(getTagValue("UUID", element));
                payOnResponseVO.setTransactionId(getTagValue("UUID", element));
            }
        }
        list = document.getElementsByTagName("Processing");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                payOnResponseVO.setDescription(getTagValue("Return", element));
                payOnResponseVO.setConnectorTxID1(getTagValue("ConnectorTxID1", element));
                payOnResponseVO.setConnectorTxID2(getTagValue("ConnectorTxID2", element));
                payOnResponseVO.setConnectorTxID3(getTagValue("ConnectorTxID3", element));
                payOnResponseVO.setRequestTimestamp(getChildAttributeValue(document.getElementsByTagName("Processing"),"requestTimestamp"));
                payOnResponseVO.setResponseTime(getChildAttributeValue(document.getElementsByTagName("Processing"),"responseTimestamp"));

            }
        }

        list = document.getElementsByTagName("ConnectorReceived");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;
                payOnResponseVO.setConnectorMessage(getTagValue("Returned", element));
                payOnResponseVO.setBody(getTagValue("Body", element));
            }
        }
        payOnResponseVO.setStatusDesc(statusdescription);
        payOnResponseVO.setConnectorCode(getChildAttributeValue(document.getElementsByTagName("Returned"),"code"));
        payOnResponseVO.setReturnCode(getChildAttributeValue(document.getElementsByTagName("Return"),"code"));
        if("000.000.000".equals(getChildAttributeValue(document.getElementsByTagName("Return"),"code").trim()) || "00".equals(getChildAttributeValue(document.getElementsByTagName("Returned"),"code").trim()) || "Successful".equals(document.getElementsByTagName("Returned")))
        {
            payOnResponseVO.setStatus("success");
        }
        else
        {
            payOnResponseVO.setStatus(status);
        }
        payOnResponseVO.setDescription(payOnResponseVO.getDescription()+ " | " +payOnResponseVO.getConnectorMessage());
        return payOnResponseVO;
    }

    private String getTagValue(String sTag, Element eElement)
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
        return value.trim();
    }
    public Authentication populateAuthenticationDetails(PayOnVBVDetailsVO payOnVBVDetailsVO)
    {
        Authentication authentication= null;

        if(payOnVBVDetailsVO !=null)
        {
            authentication = new Authentication();
            authentication.setEci(payOnVBVDetailsVO.getEci());
            authentication.setVerification(payOnVBVDetailsVO.getVerification());
            authentication.setXid(payOnVBVDetailsVO.getXid());
        }
        return authentication;
    }
    public Customer populateCustomerDetails(GenericAddressDetailsVO addressDetailsVO)
    {
        Name name = new Name();
        name.setFamily(addressDetailsVO.getLastname());
        name.setGiven(addressDetailsVO.getFirstname());

        Address address = new Address();
        address.setCity(addressDetailsVO.getCity());
        address.setCountry(addressDetailsVO.getCountry());
        address.setState(addressDetailsVO.getState());
        address.setStreet(addressDetailsVO.getStreet());
        address.setZip(addressDetailsVO.getZipCode());

        Contact contact = new Contact();
        contact.setEmail(addressDetailsVO.getEmail());
        contact.setIp(addressDetailsVO.getIp());
        contact.setPhone(addressDetailsVO.getPhone());

        Customer customer = new Customer();
        customer.setAddress(address);
        customer.setName(name);
        customer.setContact(contact);
        return customer;
    }
    public CreditCardAccount populateCreditCardDetails(PayOnCardDetailsVO payOnCardDetailsVO)
    {
        CreditCardAccount creditCardAccount = new CreditCardAccount();
        creditCardAccount.setNumber(payOnCardDetailsVO.getCardNum());

        String cardBrand = payOnCardDetailsVO.getCardType();
        if(payOnCardDetailsVO.getCardType()!=null && "MC".equalsIgnoreCase(payOnCardDetailsVO.getCardType()))
        {
            cardBrand = "MASTER";
        }
        creditCardAccount.setBrand(cardBrand);
        creditCardAccount.setHolder(payOnCardDetailsVO.getCardHolderFirstName()+" "+payOnCardDetailsVO.getCardHolderSurname());
        creditCardAccount.setVerification(payOnCardDetailsVO.getcVV());


        String startMonth = payOnCardDetailsVO.getStartMonth();
        String startYear = payOnCardDetailsVO.getStartYear();
        if (startMonth != null && startYear != null)
        {
            CCDate startDate = new CCDate();
            startDate.setMonth(startMonth);
            startDate.setYear(startYear);
            creditCardAccount.setStart(startDate);
        }

        CCDate expDate = new CCDate();
        expDate.setMonth(payOnCardDetailsVO.getExpMonth());
        expDate.setYear(payOnCardDetailsVO.getExpYear());
        creditCardAccount.setExpiry(expDate);

        return creditCardAccount;
    }
    public MerchantAccount populateMerchantAccount(PayOnMerchantAccountVO payOnMerchantAccountVO)
    {
        MerchantAccount merchantAccount = new MerchantAccount();
        if(payOnMerchantAccountVO.getConnector()!=null)
        {
            if (payOnMerchantAccountVO.getConnector().equals("worldline"))
            {
                merchantAccount.setType(payOnMerchantAccountVO.getType());
                merchantAccount.setMerchantName(payOnMerchantAccountVO.getMerchantname());
                merchantAccount.setMerchantID(payOnMerchantAccountVO.getMerchantId());
                merchantAccount.setKey(payOnMerchantAccountVO.getKey());
                merchantAccount.setTerminalID(payOnMerchantAccountVO.getTerminalID());
                merchantAccount.setUsername(payOnMerchantAccountVO.getUsername());
            }
            else if(payOnMerchantAccountVO.getConnector().equals("ems"))
            {
                merchantAccount.setType(payOnMerchantAccountVO.getType());
                merchantAccount.setMerchantID(payOnMerchantAccountVO.getMerchantId());
                merchantAccount.setKey(payOnMerchantAccountVO.getKey());
                merchantAccount.setUsername(payOnMerchantAccountVO.getUsername());
                merchantAccount.setPassword(payOnMerchantAccountVO.getPayOnPassword());
                merchantAccount.setUploadUser(payOnMerchantAccountVO.getUploadUser());
                merchantAccount.setIBAN(payOnMerchantAccountVO.getIBAN());
                merchantAccount.setMerchantName(payOnMerchantAccountVO.getMerchantname());
            }
            else if(payOnMerchantAccountVO.getConnector().equalsIgnoreCase("CONCARDIS"))
            {
                merchantAccount.setType("CONCARDIS");
                merchantAccount.setMerchantID(payOnMerchantAccountVO.getMerchantId());
                merchantAccount.setTerminalID(payOnMerchantAccountVO.getTerminalID());
            }
            else if(payOnMerchantAccountVO.getConnector().equalsIgnoreCase("CATELLA"))
            {
                merchantAccount.setType(payOnMerchantAccountVO.getType());
                merchantAccount.setMerchantID(payOnMerchantAccountVO.getMerchantId());
                merchantAccount.setMerchantName(payOnMerchantAccountVO.getMerchantname());
                merchantAccount.setKey(payOnMerchantAccountVO.getKey());
                merchantAccount.setPassword(payOnMerchantAccountVO.getPayOnPassword());
            }
        }
        else
        {
            transactionLogger.error("Invalid Connector");  //TODO throw error
        }
        return merchantAccount;
    }
    public PayOnResponseVO processRequest(Request request,String url,String username,String password) throws IOException, PZTechnicalViolationException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("", "com.payment.payon.core.message");
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(request));
        //System.out.println(xmlOut);
        transactionLogger.error(xmlOut);
        HttpClient httpClient = new HttpClient();
        PostMethod post = new PostMethod(url);
        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
        httpClient.getState().setCredentials(authScope, usernamePasswordCredentials);
        post.setDoAuthentication(true);
        post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        NameValuePair[] params = {new NameValuePair("requestXML", xmlOut.toString())};
        post.setRequestBody(params);
        httpClient.executeMethod(post);
        String responseXML = new String(post.getResponseBody());
        transactionLogger.error("resonseXML:::::: " +responseXML);
        PayOnUtils payOnUtils=new PayOnUtils();
        PayOnResponseVO payOnResponseVO= payOnUtils.getPayonResponseVOI(responseXML);
        return payOnResponseVO;
    }

}

package com.payment.apcofastpayv6;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
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

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Admin on 9/16/2021.
 */
public class ApcoFastPayV6Utils
{
    private final static String charset = "UTF-8";
    private static TransactionLogger transactionLogger=new TransactionLogger(ApcoFastPayV6Utils.class.getName());
    public static String doPostHTTPSURLConnectionClient(String url,String request) throws PZTechnicalViolationException
    {

        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.setRequestHeader("Content-Type", "application/json");

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(commResponseVO.getUrlFor3DRedirect()).append("\" method=\"POST\">\n");
        html.append("</form>\n");
        return html.toString();
    }
    public CommRequestVO getIMoneyPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionLogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
    public static Map<String, String> readFastPayV6RedirectionXMLResponse(String str) throws Exception
    {
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("Transaction");
        responseMap.put("Result", getTagValue("Result", ((Element) nList.item(0))));
        responseMap.put("ORef", getTagValue("ORef", ((Element) nList.item(0))));
        responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList.item(0))));
        responseMap.put("pspid", getTagValue("pspid", ((Element) nList.item(0))));
        responseMap.put("Currency", getTagValue("Currency", ((Element) nList.item(0))));
        responseMap.put("Value", getTagValue("Value", ((Element) nList.item(0))));
        responseMap.put("Source", getTagValue("Source", ((Element) nList.item(0))));
        responseMap.put("Email", getTagValue("Email", ((Element) nList.item(0))));
        responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList.item(0))));
        responseMap.put("ISOResp", getTagValue("ISOResp", ((Element) nList.item(0))));
        responseMap.put("CardHName", getTagValue("CardHName", ((Element) nList.item(0))));
        responseMap.put("CardNum", getTagValue("CardNum", ((Element) nList.item(0))));

        return responseMap;
    }
    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce){
            transactionLogger.error("Exception in createDocumentFromString of ApcoFastPayV6Utills", pce);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString ApcoFastPayV6Utills", e);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString ApcoFastPayV6Utills", e);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utills.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }
    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value = "";
        if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null){
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();
        }
        if (nlList != null && nlList.item(0) != null){
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }
    /*public static Map<String, String> readGlobalgatePayoutResponse(String str) throws Exception
    {

        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList = null;
        nList = doc.getElementsByTagName("PayoutResult");

        responseMap.put("Status", getTagValue("Status", ((Element) nList.item(0))));
        responseMap.put("ErrorMsg", getTagValue("ErrorMsg", ((Element) nList.item(0))));

        return  responseMap;

    }*/
    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside fastpayv6utils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setState(genericAddressDetailsVO.getState());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result=null;
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        URLConnection conn=null;
        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try{
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
                conn.setRequestProperty("Content-Type", "text/xml");
            }
            catch (SSLHandshakeException io){
                transactionLogger.error("SSLHandshakeException --->",io);
                PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if(conn instanceof HttpURLConnection){
                ((HttpURLConnection)conn).setRequestMethod("POST");
            }
            assert conn != null;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException --->",ue);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me){
            transactionLogger.error("MalformedURLException --->",me);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe){
            transactionLogger.error("ProtocolException --->",pe);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex){
            transactionLogger.error("IOException --->",ex);
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e){
            transactionLogger.error("PZConstraintViolationException --->",e);
        }
        finally{
            if (out != null){
                try{
                    out.close();
                }
                catch (IOException e){
                    transactionLogger.error("IOException finally out.close --->",e);
                    PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null){
                try{
                    in.close();
                } catch (IOException e){
                    transactionLogger.error("IOException finally in.close --->",e);
                    PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<FASTPAY> bufList = new LinkedList<FASTPAY>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new FASTPAY(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<FASTPAY> p = bufList.listIterator(); p.hasNext(); ){
                FASTPAY b = p.next();
                for (int i = 0; i < b.size; ){
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie){
            PZExceptionHandler.raiseTechnicalViolationException("ApcoFastPayV6Utils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }
    static class FASTPAY
    {
        public byte buf[];
        public int size;

        public FASTPAY(byte b[], int s){
            buf = b;
            size = s;
        }
    }
    public static Map<String, String> readFastpayV6InquiryXMLResponse(String str) throws Exception
    {
        Functions functions=new Functions();
        Document doc = createDocumentFromString(str);
        Map<String, String> responseMap = new HashMap<String, String>();
        doc.getDocumentElement().getNodeName();
        NodeList nList,nList2 = null;
        nList = doc.getElementsByTagName("Table1");
        System.out.println("nList value=========>"+nList.getLength());
/*
        for(int i=0; i<=nList.getLength(); i++){
            nList2=null;
            String response=getTagValue("Response", ((Element) nList.item(i)));
            Document doc2 = createDocumentFromString(response);
            nList2 = doc2.getElementsByTagName("TransactionResult");

            System.out.println("i===========>"+i);
            System.out.println("response="+getTagValue("pspid", ((Element) nList2.item(0))));

        }*/
        if(nList!=null)
        {

            if(nList.getLength()>1){
                String response=getTagValue("Response", ((Element) nList.item(nList.getLength()-1)));
                if(functions.isValueNull(response))
                {

                    Document doc2 = createDocumentFromString(response);
                    nList2 = doc2.getElementsByTagName("TransactionResult");
                    responseMap.put("Result", getTagValue("Result", ((Element) nList2.item(0))));
                    responseMap.put("ORef", getTagValue("ORef", ((Element) nList2.item(0))));
                    responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList2.item(0))));
                    responseMap.put("pspid", getTagValue("pspid", ((Element) nList2.item(0))));
                    responseMap.put("Currency", getTagValue("Currency", ((Element) nList2.item(0))));
                    responseMap.put("Value", getTagValue("Value", ((Element) nList2.item(0))));
                    responseMap.put("Source", getTagValue("Source", ((Element) nList2.item(0))));
                    responseMap.put("Email", getTagValue("Email", ((Element) nList2.item(0))));
                    responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList2.item(0))));
                }

            }
            else
            {
                String response = getTagValue("Response", ((Element) nList.item(0)));
                if (functions.isValueNull(response))
                {

                    Document doc2 = createDocumentFromString(response);
                    nList2 = doc2.getElementsByTagName("TransactionResult");
                    responseMap.put("Result", getTagValue("Result", ((Element) nList2.item(0))));
                    responseMap.put("ORef", getTagValue("ORef", ((Element) nList2.item(0))));
                    responseMap.put("AuthCode", getTagValue("AuthCode", ((Element) nList2.item(0))));
                    responseMap.put("pspid", getTagValue("pspid", ((Element) nList2.item(0))));
                    responseMap.put("Currency", getTagValue("Currency", ((Element) nList2.item(0))));
                    responseMap.put("Value", getTagValue("Value", ((Element) nList2.item(0))));
                    responseMap.put("Source", getTagValue("Source", ((Element) nList2.item(0))));
                    responseMap.put("Email", getTagValue("Email", ((Element) nList2.item(0))));
                    responseMap.put("ExtendedErr", getTagValue("ExtendedErr", ((Element) nList2.item(0))));
                }
            }
        }


        return responseMap;
    }


}

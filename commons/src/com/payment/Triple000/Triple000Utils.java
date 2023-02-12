package com.payment.Triple000;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
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
import java.util.HashMap;

/**
 * Created by Admin on 10/14/2020.
 */
public class Triple000Utils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(Triple000Utils.class.getName());
    private static HashMap<String, String> cardTypeMap = new HashMap<String, String>();

    static
    {
        cardTypeMap.put("VISA","1");
        cardTypeMap.put("MC","2");
        cardTypeMap.put("JCB","3");
        cardTypeMap.put("AMEX","4");
        cardTypeMap.put("EMT","5");
        cardTypeMap.put("EMTS","6");
        cardTypeMap.put("Triple000","7");
    }

    public static String getCardTypeCode(String CardType)
    {
        String CardTypeCode="";
        CardTypeCode=cardTypeMap.get(CardType);
        return CardTypeCode;
    }

    public  Document createDocumentFromString(String xmlString ) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce)
        {
            transactionLogger.error("Exception in createDocumentFromString of Triple000Utils",pce);
            PZExceptionHandler.raiseTechnicalViolationException("Triple000Utils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString Triple000Utils",e);
            PZExceptionHandler.raiseTechnicalViolationException("Triple000Utils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString Triple000Utils",e);
            PZExceptionHandler.raiseTechnicalViolationException("Triple000Utils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    private static String getTagValue(String trackID, Element eElement)
    {
        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(trackID)!=null && eElement.getElementsByTagName(trackID).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(trackID).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();
        }
        return value;
    }

    public HashMap  readTriple000XMLResponse(String response,String trackingID,String type)
    {
        Comm3DResponseVO comm3DResponseVO= new Comm3DResponseVO();
        Functions functions = new Functions();
        HashMap<String,String> responseMap=new HashMap<>();
        try
        {
            Document doc = createDocumentFromString(response);
               // Element rootElement = doc.getDocumentElement();
            NodeList nList = doc.getElementsByTagName("PSPTransaction");
            String responseStatus = getTagValue("error_code", (Element) nList.item(0));
            String message = getTagValue("message", (Element) nList.item(0));
            NodeList nListData = doc.getElementsByTagName("data");
            responseMap.put("operator_id",getTagValue("operator_id", (Element) nListData.item(0)));
            responseMap.put("ext_id",getTagValue("ext_id", (Element) nListData.item(0)));
            responseMap.put("tran_id",getTagValue("tran_id", (Element) nListData.item(0)));
            responseMap.put("card_type",getTagValue("card_type", (Element) nListData.item(0)));
            responseMap.put("currency",getTagValue("currency", (Element) nListData.item(0)));
            responseMap.put("trxn_amount",getTagValue("trxn_amount", (Element) nListData.item(0)));
            responseMap.put("auth_type",getTagValue("auth_type", (Element) nListData.item(0)));
            responseMap.put("html_data",getTagValue("html_data", (Element) nListData.item(0)));
            responseMap.put("error_code",responseStatus);
            responseMap.put("message",message);
            transactionLogger.error("Triple000 responseMap---->"+responseMap);
                //Status - 0

            if (functions.isValueNull(responseStatus) && responseStatus.equals("0"))
            {
                comm3DResponseVO.setStatus("success");
                comm3DResponseVO.setRemark(message);
                comm3DResponseVO.setDescription(message);
            }
            else if(functions.isValueNull(responseStatus) && responseStatus.equals("3D"))
            {
                comm3DResponseVO.setStatus("pending3DConfirmation");
                comm3DResponseVO.setRemark(message);
                comm3DResponseVO.setDescription(message);
            }else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark(message);
                comm3DResponseVO.setDescription(message);
            }

            // comm3DResponseVO.setStatus((getTagValue("status", (Element) nList.item(0))));
            comm3DResponseVO.setTransactionId(responseMap.get("tran_id"));
            comm3DResponseVO.setBankTransactionDate(getTagValue("resp_time", (Element) nList.item(0)));
            comm3DResponseVO.setUrlFor3DRedirect(responseMap.get("html_data"));
            comm3DResponseVO.setErrorCode(responseStatus);


        }catch (Exception e){
            transactionLogger.error("Triple000 Exception e--"+trackingID+"->",e);
        }
        return responseMap;
    }


    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        transactionLogger.error("Triple000Utils URL --->" + url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            transactionLogger.error("Triple000Utils HttpException --->" , e);
        }
        catch (IOException e)
        {
            transactionLogger.error("Triple000Utils IOException --->" , e);
        }
        catch(Exception e)
        {

            transactionLogger.error("Triple000Utils Exception --->" , e);
        }

        return result;
    }
    public static CommRequestVO getTriple000RequestVO(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error(":::Inside getTriple000RequestVO in Triple000Utils:::");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());


        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCustomerid(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transactionLogger.error("CardType---"+commonValidatorVO.getCardType());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }

}

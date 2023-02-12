package com.payment.nestpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.PaymentzEncryptor;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CurrencyCodeISO4217;
import com.payment.ems.core.EMSResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/4/2018.
 */
public class NestPayUtils
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(NestPayUtils.class.getName());
    private  static ResourceBundle RB                   = LoadProperties.getProperty("com.directi.pg.nestpay");

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        transactionLogger.error("strURL:::::" + strURL);
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            post.addRequestHeader("Content-Type", "application/xml");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException :::: ",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException :::: ", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static NestPayResponseVO readSoapResponse(String response,String descriptor) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        NestPayResponseVO nestPayResponseVO = new NestPayResponseVO();
        Document doc = createDocumentFromString(response);
        NodeList nList = doc.getElementsByTagName("CC5Response");

        NodeList nList1=doc.getElementsByTagName("Extra");

        String orderid="";
        String status="";
        String authCode="";
        String transactionId="";
        String remark="";
        String date="";
        String errorCode="";
        String transType="";
        String HostRefNum="";
        String ProcReturnCode="";
        String SETTLEID="";
        String NUMBEROFINSTALLMENTS1="";
        String CARDISSUER="";
        String ADVICEDINSTALLMENTTYPE="";
        String DIGERTAKSITTUTARI1="";
        String INTERESTRATE2="";
        String DIGERTAKSITTUTARI2="";
        String TOTALAMOUNTDUE1="";
        String INTERESTRATE1="";
        String TOTALAMOUNTDUE2="";
        String NUMBEROFINSTALLMENTS2="";
        String ANNUALPERCENTAGE1="";
        String ANNUALPERCENTAGE2="";
        String INSTALLMENTTYPE="";
        String INSTALLMENTOPTION="";
        String CARDBRAND="";
        String NUMBEROFINSTALLMENTOPTIONS="";
        String ADVICEINSTALLMENTEXIST="";
        String INSTALLMENTFEE2="";
        String INSTALLMENTFEE1="";
        String NUMCODE="";
        String ILKTAKSITTUTARI2="";
        String ILKTAKSITTUTARI1="";

        orderid=getTagValue("OrderId", ((Element) nList.item(0)));
        status=getTagValue("Response", ((Element) nList.item(0)));
        authCode=getTagValue("AuthCode", ((Element) nList.item(0)));
        HostRefNum=getTagValue("HostRefNum", ((Element) nList.item(0)));
        ProcReturnCode=getTagValue("ProcReturnCode", ((Element) nList.item(0)));
        transactionId=getTagValue("TransId", ((Element) nList.item(0)));
        remark=getTagValue("ErrMsg", ((Element) nList.item(0)));
        date=getTagValue("TRXDATE",((Element) nList1.item(0)));
        errorCode=getTagValue("ERRORCODE",((Element) nList1.item(0)));
        SETTLEID=getTagValue("SETTLEID",((Element) nList1.item(0)));
        NUMBEROFINSTALLMENTS1=getTagValue("NUMBEROFINSTALLMENTS1",((Element) nList1.item(0)));
        CARDISSUER=getTagValue("CARDISSUER",((Element) nList1.item(0)));
        ADVICEDINSTALLMENTTYPE=getTagValue("ADVICEDINSTALLMENTTYPE",((Element) nList1.item(0)));
        DIGERTAKSITTUTARI1=getTagValue("DIGERTAKSITTUTARI1",((Element) nList1.item(0)));
        INTERESTRATE2=getTagValue("INTERESTRATE2",((Element) nList1.item(0)));
        DIGERTAKSITTUTARI2=getTagValue("DIGERTAKSITTUTARI2",((Element) nList1.item(0)));
        TOTALAMOUNTDUE1=getTagValue("TOTALAMOUNTDUE1",((Element) nList1.item(0)));
        INTERESTRATE1=getTagValue("INTERESTRATE1",((Element) nList1.item(0)));
        TOTALAMOUNTDUE2=getTagValue("TOTALAMOUNTDUE2",((Element) nList1.item(0)));
        NUMBEROFINSTALLMENTS2=getTagValue("NUMBEROFINSTALLMENTS2",((Element) nList1.item(0)));
        ANNUALPERCENTAGE1=getTagValue("ANNUALPERCENTAGE1",((Element) nList1.item(0)));
        ANNUALPERCENTAGE2=getTagValue("ANNUALPERCENTAGE2",((Element) nList1.item(0)));
        INSTALLMENTTYPE=getTagValue("INSTALLMENTTYPE",((Element) nList1.item(0)));
        INSTALLMENTOPTION=getTagValue("INSTALLMENTOPTION",((Element) nList1.item(0)));
        CARDBRAND=getTagValue("CARDBRAND",((Element) nList1.item(0)));
        NUMBEROFINSTALLMENTOPTIONS=getTagValue("NUMBEROFINSTALLMENTOPTIONS",((Element) nList1.item(0)));
        ADVICEINSTALLMENTEXIST=getTagValue("ADVICEINSTALLMENTEXIST",((Element) nList1.item(0)));
        INSTALLMENTFEE2=getTagValue("INSTALLMENTFEE2",((Element) nList1.item(0)));
        INSTALLMENTFEE1=getTagValue("INSTALLMENTFEE1",((Element) nList1.item(0)));
        NUMCODE=getTagValue("NUMCODE",((Element) nList1.item(0)));
        ILKTAKSITTUTARI2=getTagValue("ILKTAKSITTUTARI2",((Element) nList1.item(0)));
        ILKTAKSITTUTARI1=getTagValue("ILKTAKSITTUTARI1",((Element) nList1.item(0)));

        transactionLogger.debug("SETTLEID-----"+SETTLEID);
        transactionLogger.debug("NUMBEROFINSTALLMENTS2-----"+NUMBEROFINSTALLMENTS2);
        transactionLogger.debug("CARDISSUER-----"+CARDISSUER);
        transactionLogger.debug("ANNUALPERCENTAGE2-----"+ANNUALPERCENTAGE2);
        transactionLogger.debug("TOTALAMOUNTDUE1-----"+TOTALAMOUNTDUE1);
        transactionLogger.debug("NUMBEROFINSTALLMENTS2-----"+SETTLEID);
        transactionLogger.debug("INSTALLMENTOPTION-----"+INSTALLMENTOPTION);
        transactionLogger.debug("ADVICEINSTALLMENTEXIST-----"+ADVICEINSTALLMENTEXIST);
        transactionLogger.debug("ILKTAKSITTUTARI1-----"+ILKTAKSITTUTARI1);





        if(!functions.isValueNull(authCode)){
            authCode=getTagValue("AUTH_CODE",((Element) nList1.item(0)));
        }
        if(!functions.isValueNull(date)){
            date=getTagValue("HOSTDATE",((Element) nList1.item(0)));
        }
        if(functions.isValueNull(transType)){
            if(transType.equals("A")){
                nestPayResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }else if(transType.equals("S")){
                nestPayResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }else if(transType.equals("C")){
                nestPayResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }else if(transType.equals("R")){
                nestPayResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }else if(transType.equals("V")){
                nestPayResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }
        }

        if("Approved".equalsIgnoreCase(status)){
            nestPayResponseVO.setStatus("success");
            nestPayResponseVO.setRemark("Transaction Successful");
            nestPayResponseVO.setDescription(status);
            nestPayResponseVO.setDescriptor(descriptor);
        }else {
            nestPayResponseVO.setStatus("fail");
            nestPayResponseVO.setRemark(remark);
            nestPayResponseVO.setDescription(status);
        }
        nestPayResponseVO.setResponse(status);
        nestPayResponseVO.setErrMsg(remark);
        nestPayResponseVO.setOrderId(orderid);
        nestPayResponseVO.setERRORCODE(errorCode);
        nestPayResponseVO.setTransId(transactionId);
        nestPayResponseVO.setTRXDATE(date);
        nestPayResponseVO.setHostRefNum(HostRefNum);
        nestPayResponseVO.setProcReturnCode(ProcReturnCode);
        nestPayResponseVO.setSETTLEID(SETTLEID);
        nestPayResponseVO.setNUMBEROFINSTALLMENTS1(NUMBEROFINSTALLMENTS1);
        nestPayResponseVO.setCARDISSUER(CARDISSUER);
        nestPayResponseVO.setADVICEDINSTALLMENTTYPE(ADVICEDINSTALLMENTTYPE);
        nestPayResponseVO.setDIGERTAKSITTUTARI1(DIGERTAKSITTUTARI1);
        nestPayResponseVO.setINTERESTRATE2(INTERESTRATE2);
        nestPayResponseVO.setDIGERTAKSITTUTARI2(DIGERTAKSITTUTARI2);
        nestPayResponseVO.setTOTALAMOUNTDUE1(TOTALAMOUNTDUE1);
        nestPayResponseVO.setINTERESTRATE1(INTERESTRATE1);
        nestPayResponseVO.setTOTALAMOUNTDUE2(TOTALAMOUNTDUE2);
        nestPayResponseVO.setNUMBEROFINSTALLMENTS2(NUMBEROFINSTALLMENTS2);
        nestPayResponseVO.setANNUALPERCENTAGE1(ANNUALPERCENTAGE1);
        nestPayResponseVO.setANNUALPERCENTAGE2(ANNUALPERCENTAGE2);
        nestPayResponseVO.setINSTALLMENTTYPE(INSTALLMENTTYPE);
        nestPayResponseVO.setINSTALLMENTOPTION(INSTALLMENTOPTION);
        nestPayResponseVO.setCARDBRAND(CARDBRAND);
        nestPayResponseVO.setNUMBEROFINSTALLMENTOPTIONS(NUMBEROFINSTALLMENTOPTIONS);
        nestPayResponseVO.setADVICEINSTALLMENTEXIST(ADVICEINSTALLMENTEXIST);
        nestPayResponseVO.setINSTALLMENTFEE2(INSTALLMENTFEE2);
        nestPayResponseVO.setINSTALLMENTFEE1(INSTALLMENTFEE1);
        nestPayResponseVO.setNUMCODE(NUMCODE);
        nestPayResponseVO.setILKTAKSITTUTARI2(ILKTAKSITTUTARI2);
        nestPayResponseVO.setILKTAKSITTUTARI1(ILKTAKSITTUTARI1);
        nestPayResponseVO.setTransactionStatus(status);
        nestPayResponseVO.setMerchantOrderId(orderid);
        nestPayResponseVO.setTransactionId(transactionId);
        nestPayResponseVO.setErrorCode(errorCode);
        nestPayResponseVO.setAuthCode(authCode);
        nestPayResponseVO.setBankTransactionDate(date);

        return nestPayResponseVO;
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

    public static Document createDocumentFromString(String xmlString ) throws PZTechnicalViolationException
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
            transactionLogger.error("Exception in createDocumentFromString of NestPayUtils",pce);
            PZExceptionHandler.raiseTechnicalViolationException("NestPayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString NestPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("NestPayUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString NestPayUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("NestPayUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        return document;
    }

    public static String SHA1(String clientid, String oid, String amount, String okurl, String failurl, String trantype, String installment,String rnd, String storekey) throws PZTechnicalViolationException
    {
        String h="";
        String sha = clientid.trim() + oid.trim() + amount.trim() + okurl.trim() + failurl.trim() + trantype.trim() +installment.trim() + rnd.trim() + storekey.trim();
        sha.trim();

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = sha.getBytes(("UTF-8"));
            md.update(bytes);
            byte[] hash = md.digest();
            h = (new BASE64Encoder()).encode(hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(NestPayPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return h.toString().trim();
    }
}

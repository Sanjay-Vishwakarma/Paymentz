package com.payment.ecomprocessing;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Balaji Sawant on 26-Sep-19.
 */
public class ECPUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ECPUtils.class.getName());
    private static Logger log = new Logger(ECPUtils.class.getName());
    Functions functions =new Functions();

    public String doPostHTTPSURLConnectionClient(String strURL, String req, String authType, String encodedCredentials) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside doPostHTTPSURLConnectionClient --- ");
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Authorization", authType + " " + encodedCredentials);
            post.addRequestHeader("Content-Type", "text/xml");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.setRequestBody(req);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException he)
        {
            transactionLogger.error("HttpException-----", he);
        }
        catch (IOException io)
        {
            transactionLogger.error("IOException-----", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public  String getAmount(String amount)
    {
        transactionLogger.debug("formatting amount for Currency ---");
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }


    public  String getToken(String currency,String is3DSupported,Boolean isTest,String accountId)
    {
        transactionLogger.error("Inside getToken ---");
        Connection con=null;
        String isTestNew="N";
        if (isTest)
            isTestNew="Y";

        String token = null;
        try
        {
            con = Database.getConnection();
            String query = "";
            if (is3DSupported.equalsIgnoreCase("Y"))
            {
                query = "SELECT 3Dtoken as token FROM gateway_accounts_ecp WHERE currency=? AND accountid=?";
            }
            else
            {
                query = "SELECT non3Dtoken as token FROM gateway_accounts_ecp WHERE currency=? AND accountid=?";
            }
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,currency);
            ps.setString(2, accountId);

            transactionLogger.error("query---"+ps);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                token=rs.getString("token");
            }
        }
        catch(Exception e){
            transactionLogger.error("Exception while getting token  ------"+ e);
        }finally
        {
            Database.closeConnection(con);
        }
        return token;
    }
    public  String checkPreviousTransaction3D_Non3D(String trackingId)
    {
        transactionLogger.error("Inside getTransaction3D_Non3D ---");
        transactionLogger.error("tracking id --------"+trackingId);
        Connection con=null;
            String is3D="N";
        try{
            con= Database.getConnection();
            String query = "SELECT STATUS FROM transaction_common_details WHERE trackingid = ? AND STATUS = '3D_authstarted'";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, trackingId);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                is3D= "Y";
            }else{
                is3D= "N";
            }
        }
        catch(Exception e){
            transactionLogger.error("Exception  ---"+ e);
        }finally
        {
            Database.closeConnection(con);
        }
        transactionLogger.error("is 3D -------"+is3D);
        return is3D;
    }

    public  String getJPYSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for JPY Currency ---");
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        int amt =(int) Math.round(dObj2);
        return amt+"";
    }

    public  String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---");
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }

    public  Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
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


    public  Map<String, String> readSoapResponse(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readSoapResponse ---");
        Map<String, String> responseMap = new HashMap<String, String>();

        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList nList = doc.getElementsByTagName("payment_response");

            responseMap.put("transaction_type",getTagValue("transaction_type",((Element) nList.item(0))));
            responseMap.put("status",getTagValue("status",((Element) nList.item(0))));
            responseMap.put("authorization_code",getTagValue("authorization_code",((Element) nList.item(0))));
            responseMap.put("unique_id",getTagValue("unique_id",((Element) nList.item(0))));
            responseMap.put("transaction_id",getTagValue("transaction_id",((Element) nList.item(0))));
            responseMap.put("response_code",getTagValue("response_code",((Element) nList.item(0))));
            responseMap.put("technical_message",getTagValue("technical_message",((Element) nList.item(0))));
            responseMap.put("message",getTagValue("message",((Element) nList.item(0))));
            responseMap.put("mode",getTagValue("mode",((Element) nList.item(0))));
            responseMap.put("timestamp",getTagValue("timestamp",((Element) nList.item(0))));
            responseMap.put("descriptor",getTagValue("descriptor",((Element) nList.item(0))));
            responseMap.put("amount",getTagValue("amount",((Element) nList.item(0))));
            responseMap.put("currency",getTagValue("currency",((Element) nList.item(0))));
            responseMap.put("sent_to_acquirer",getTagValue("sent_to_acquirer",((Element) nList.item(0))));
            responseMap.put("redirect_url",getTagValue("redirect_url",((Element) nList.item(0))));
            responseMap.put("transaction_date",getTagValue("transaction_date",((Element) nList.item(0))));
            responseMap.put("auth_code",getTagValue("auth_code",((Element) nList.item(0))));

        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
      /*  for (Map.Entry<String,String> entry:responseMap.entrySet())
        {
            String key=entry.getKey();
            String value=entry.getValue();
            transactionLogger.debug("key line :::::"+key);
            transactionLogger.debug("value line :::::"+value);
        }*/
        return responseMap;
    }

    public List<EcpResponseVo> readCardPresentResponse(String response){
        transactionLogger.error("Inside readSoapResponse ---");
        List<EcpResponseVo> list = new ArrayList<>();
        try{
            Document doc = createDocumentFromString(response.trim());
            NodeList processed_transaction_response = doc.getElementsByTagName("processed_transaction_response");
            for (int i = 0; i < processed_transaction_response.getLength(); i++)
            {
                EcpResponseVo ecpResponseVo = new EcpResponseVo();
                ecpResponseVo.setMerchantNumber(getTagValue("merchant_number", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setBatchNumber(getTagValue("batch_number", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setTransactionDate(getTagValue("transaction_date", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setPostDate(getTagValue("post_date", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setAmount(getTagValue("amount", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setArn(getTagValue("arn", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setCardScheme(getTagValue("card_scheme", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setServiceTypeDesc(getTagValue("service_type_desc", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setCardBrand(getTagValue("card_brand", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setCardNumber(getTagValue("card_number", ((Element) processed_transaction_response.item(i))));

                ecpResponseVo.setBinCountry(getTagValue("bin_country", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setMerchantCountry(getTagValue("merchant_country", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setAreaOfEvent(getTagValue("area_of_event", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setCurrency(getTagValue("currency", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setCrossRate(getTagValue("cross_rate", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setAuthCode(getTagValue("auth_code", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setOriginalTransactionUniqueId(getTagValue("unique_id", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setTransactionType(getTagValue("type", ((Element) processed_transaction_response.item(i))));

                ecpResponseVo.setCardPresent(getTagValue("card_present", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setDepositSlipNumber(getTagValue("deposit_slip_number", ((Element) processed_transaction_response.item(i))));
                ecpResponseVo.setBatchSlipNumber(getTagValue("batch_slip_number", ((Element) processed_transaction_response.item(i))));
                list.add(ecpResponseVo);
            }
        }catch (Exception e){
            transactionLogger.error(e);
        }
        return list;
    }

    public  EcpResponseVo readChargebackResponse(String response) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readChargebackResponse ---");
        EcpResponseVo ecpResponseVo=new EcpResponseVo();
        try
        {
            Document doc = createDocumentFromString(response.trim());
            NodeList chargeback = doc.getElementsByTagName("chargeback_response");

            ecpResponseVo.setType(getTagValue("type", ((Element) chargeback.item(0))));
            ecpResponseVo.setArn(getTagValue("arn", ((Element) chargeback.item(0))));
            ecpResponseVo.setPostDate(getTagValue("post_date",((Element) chargeback.item(0))));
            ecpResponseVo.setReasonCode(getTagValue("reason_code",((Element) chargeback.item(0))));
            ecpResponseVo.setReasonDescription(getTagValue("reason_descriptiond",((Element) chargeback.item(0))));
            ecpResponseVo.setAuthorizationCode(getTagValue("authorization_code",((Element) chargeback.item(0))));
            ecpResponseVo.setBatchNumber(getTagValue("batch_number",((Element) chargeback.item(0))));
            ecpResponseVo.setAmount(getTagValue("amount",((Element) chargeback.item(0))));
            ecpResponseVo.setCurrency(getTagValue("currency",((Element) chargeback.item(0))));
            ecpResponseVo.setChargebackAmount(getTagValue("chargeback_amount",((Element) chargeback.item(0))));
            ecpResponseVo.setChargebackCurrency(getTagValue("chargeback_currency",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalTransactionAmount(getTagValue("original_transaction_amount",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalTransactionCurrency(getTagValue("original_transaction_currency",((Element) chargeback.item(0))));
            ecpResponseVo.setMerchantSettlementAmount(getTagValue("merchant_settlement_amount",((Element) chargeback.item(0))));
            ecpResponseVo.setMerchantSettlementCurrency(getTagValue("merchant_settlement_currency",((Element) chargeback.item(0))));
            ecpResponseVo.setNetworkSettlementAmount(getTagValue("network_settlement_amount",((Element) chargeback.item(0))));
            ecpResponseVo.setNetworkSettlementCurrency(getTagValue("network_settlement_currency",((Element) chargeback.item(0))));
            ecpResponseVo.setMerchantDbaName(getTagValue("merchant_dba_name",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalType(getTagValue("original_type",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalPostDate(getTagValue("original_post_date",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalTransactionDate(getTagValue("original_transaction_date",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalSlip(getTagValue("original_slip",((Element) chargeback.item(0))));
            ecpResponseVo.setItemSlipNumber(getTagValue("item_slip_number",((Element) chargeback.item(0))));
            ecpResponseVo.setCardNumber(getTagValue("card_number",((Element) chargeback.item(0))));
            ecpResponseVo.setCardBrand(getTagValue("card_brand",((Element) chargeback.item(0))));
            ecpResponseVo.setCustomerEmail(getTagValue("customer_email",((Element) chargeback.item(0))));
            ecpResponseVo.setTransactionType(getTagValue("transaction_type",((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalTransactionUniqueId(getTagValue("original_transaction_unique_id",((Element) chargeback.item(0))));

           //FOR RETRIEVAL REQUEST
            ecpResponseVo.setMerchantNumber(getTagValue("merchant_number", ((Element) chargeback.item(0))));
            ecpResponseVo.setIssuerNumber(getTagValue("issuer_number", ((Element) chargeback.item(0))));
            ecpResponseVo.setOriginalBatchNumber(getTagValue("original_batch_number", ((Element) chargeback.item(0))));
            ecpResponseVo.setDescription(getTagValue("description", ((Element) chargeback.item(0))));
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return ecpResponseVo;
    }
    public  List<EcpResponseVo> readMultipleChargebackResponse(String responseString) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readMultipleChargebackResponse ---");
        List<EcpResponseVo> reponseList = new ArrayList<>();
        EcpResponseVo ecpResponseVo;
        try
        {
            Document doc = createDocumentFromString(responseString.trim());
            NodeList chargeback = doc.getElementsByTagName("chargeback_response");
            for (int i = 0; i < chargeback.getLength(); i++)
            {
                ecpResponseVo = new EcpResponseVo();

                ecpResponseVo.setType(getTagValue("type", ((Element) chargeback.item(i))));
                ecpResponseVo.setArn(getTagValue("arn", ((Element) chargeback.item(i))));
                ecpResponseVo.setPostDate(getTagValue("post_date",((Element) chargeback.item(i))));
                ecpResponseVo.setReasonCode(getTagValue("reason_code",((Element) chargeback.item(i))));
                ecpResponseVo.setReasonDescription(getTagValue("reason_description",((Element) chargeback.item(i))));
                ecpResponseVo.setAuthorizationCode(getTagValue("authorization_code",((Element) chargeback.item(i))));
                ecpResponseVo.setBatchNumber(getTagValue("batch_number",((Element) chargeback.item(i))));
                ecpResponseVo.setAmount(getTagValue("amount",((Element) chargeback.item(i))));
                ecpResponseVo.setCurrency(getTagValue("currency",((Element) chargeback.item(i))));
                ecpResponseVo.setChargebackAmount(getTagValue("chargeback_amount",((Element) chargeback.item(i))));
                ecpResponseVo.setChargebackCurrency(getTagValue("chargeback_currency",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalTransactionAmount(getTagValue("original_transaction_amount",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalTransactionCurrency(getTagValue("original_transaction_currency",((Element) chargeback.item(i))));
                ecpResponseVo.setMerchantSettlementAmount(getTagValue("merchant_settlement_amount",((Element) chargeback.item(i))));
                ecpResponseVo.setMerchantSettlementCurrency(getTagValue("merchant_settlement_currency",((Element) chargeback.item(i))));
                ecpResponseVo.setNetworkSettlementAmount(getTagValue("network_settlement_amount",((Element) chargeback.item(i))));
                ecpResponseVo.setNetworkSettlementCurrency(getTagValue("network_settlement_currency",((Element) chargeback.item(i))));
                ecpResponseVo.setMerchantDbaName(getTagValue("merchant_dba_name",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalType(getTagValue("original_type",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalPostDate(getTagValue("original_post_date",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalTransactionDate(getTagValue("original_transaction_date",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalSlip(getTagValue("original_slip",((Element) chargeback.item(i))));
                ecpResponseVo.setItemSlipNumber(getTagValue("item_slip_number",((Element) chargeback.item(i))));
                ecpResponseVo.setCardNumber(getTagValue("card_number",((Element) chargeback.item(i))));
                ecpResponseVo.setCardBrand(getTagValue("card_brand",((Element) chargeback.item(i))));
                ecpResponseVo.setCustomerEmail(getTagValue("customer_email",((Element) chargeback.item(i))));
                ecpResponseVo.setTransactionType(getTagValue("transaction_type",((Element) chargeback.item(i))));
                ecpResponseVo.setOriginalTransactionUniqueId(getTagValue("original_transaction_unique_id",((Element) chargeback.item(i))));

                reponseList.add(ecpResponseVo);
            }
        }catch(Exception e){
            transactionLogger.error("Exception  ---"+e);
        }
        return reponseList;
    }
    public  List<EcpResponseVo> readMultipleRetrievalResponse(String responseString) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside readMultipleRetrievalResponse ---");
        List<EcpResponseVo> reponseList = new ArrayList<>();
        try
        {
            Document doc = createDocumentFromString(responseString.trim());
            NodeList retrieval = doc.getElementsByTagName("retrieval_request_response");
            for (int i = 0; i < retrieval.getLength(); i++)
            {
                EcpResponseVo ecpResponseVo=new EcpResponseVo();

                ecpResponseVo.setOriginalTransactionUniqueId(getTagValue("original_transaction_unique_id", ((Element) retrieval.item(i))));
                ecpResponseVo.setType(getTagValue("type", ((Element) retrieval.item(i))));
                ecpResponseVo.setArn(getTagValue("arn", ((Element) retrieval.item(i))));
                ecpResponseVo.setPostDate(getTagValue("post_date", ((Element) retrieval.item(i))));
                ecpResponseVo.setReasonCode(getTagValue("reason_code", ((Element) retrieval.item(i))));
                ecpResponseVo.setReasonDescription(getTagValue("reason_description", ((Element) retrieval.item(i))));
                ecpResponseVo.setAuthorizationCode(getTagValue("authorization_code", ((Element) retrieval.item(i))));
                ecpResponseVo.setMerchantNumber(getTagValue("merchant_number", ((Element) retrieval.item(i))));
                ecpResponseVo.setIssuerNumber(getTagValue("issuer_number", ((Element) retrieval.item(i))));
                ecpResponseVo.setItemSlipNumber(getTagValue("item_slip_number", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalType(getTagValue("original_type", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalSlip(getTagValue("original_slip", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalBatchNumber(getTagValue("original_batch_number", ((Element) retrieval.item(i))));
                ecpResponseVo.setDescription(getTagValue("description", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalPostDate(getTagValue("original_post_date", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalTransactionDate(getTagValue("original_transaction_date", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalTransactionAmount(getTagValue("original_transaction_amount", ((Element) retrieval.item(i))));
                ecpResponseVo.setOriginalTransactionCurrency(getTagValue("original_transaction_currency", ((Element) retrieval.item(i))));
                ecpResponseVo.setMerchantSettlementAmount(getTagValue("merchant_settlement_amount", ((Element) retrieval.item(i))));
                ecpResponseVo.setMerchantSettlementCurrency(getTagValue("merchant_settlement_currency", ((Element) retrieval.item(i))));
                ecpResponseVo.setNetworkSettlementAmount(getTagValue("network_settlement_amount", ((Element) retrieval.item(i))));
                ecpResponseVo.setNetworkSettlementCurrency(getTagValue("network_settlement_currency", ((Element) retrieval.item(i))));
                ecpResponseVo.setCardNumber( getTagValue("card_number", ((Element) retrieval.item(i))));
                ecpResponseVo.setCustomerEmail(getTagValue("customer_email", ((Element) retrieval.item(i))));
                ecpResponseVo.setTransactionType(getTagValue("transaction_type", ((Element) retrieval.item(i))));

                reponseList.add(ecpResponseVo);
            }
        }catch(Exception e){
            transactionLogger.error("Exception  ---"+e);
        }
        return reponseList;
    }
public String checkNull(String input){
    if(functions.isValueNull(input)){
        return input;
    }else{
        return "";
    }
}


}

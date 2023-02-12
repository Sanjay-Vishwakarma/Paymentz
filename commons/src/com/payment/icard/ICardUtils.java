package com.payment.icard;

import com.directi.pg.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
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
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 3/26/2019.
 */
public class ICardUtils
{
    static ICardLogger transactionLogger= new ICardLogger(ICardUtils.class.getName());

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
            transactionLogger.error("Exception in createDocumentFromString of ICardUtils",pce);
            PZExceptionHandler.raiseTechnicalViolationException("ICardUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ICardUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("ICardUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.SAXEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            transactionLogger.error("Exception in createDocumentFromString ICardUtils",e);
            PZExceptionHandler.raiseTechnicalViolationException("ICardUtils.java","createDocumentFromString()",null,"common","Technical Exception Occurred. Please contact your Admin:::",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
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

    public Comm3DResponseVO readICardXMLRespone(String response,String trackingID,String type,String stan)
    {
        Comm3DResponseVO comm3DResponseVO= new Comm3DResponseVO();
        Functions functions = new Functions();
        HashMap<String,String> responseMap=new HashMap<>();
        try
        {
            Document doc = createDocumentFromString(response);
            //    Element rootElement = doc.getDocumentElement();
            NodeList nList = doc.getElementsByTagName("ipayin_response");
            String responseStatus = getTagValue("status", (Element) nList.item(0));
            String responseBankCode = getTagValue("resp_code", (Element) nList.item(0));

            transactionLogger.error("Status-----"+getTagValue("status", (Element) nList.item(0)));
            transactionLogger.error("Remark-----"+getTagValue("status_msg", (Element) nList.item(0)));
            transactionLogger.error("Description-----"+getTagValue("status_details", (Element) nList.item(0)));
            transactionLogger.error("TransactionID-----"+getTagValue("trn", (Element) nList.item(0)));
            transactionLogger.error("BankTransactionDate-----"+getTagValue("trndttm", (Element) nList.item(0)));
            transactionLogger.error("ResponseHashInfo-----"+getTagValue("approval", (Element) nList.item(0)));
            transactionLogger.error("ECI-----"+getTagValue("eci", (Element) nList.item(0)));
            transactionLogger.error("Response Code-----"+getTagValue("resp_code", (Element) nList.item(0)));
            transactionLogger.error("cvc2_result-----"+getTagValue("cvc2_result", (Element) nList.item(0)));
            transactionLogger.error("rrn-----"+getTagValue("rrn", (Element) nList.item(0)));

            String dttm=getTagValue("dttm", (Element) nList.item(0));
            responseMap.put("dttm",getTagValue("dttm", (Element) nList.item(0)));
            responseMap.put("command",getTagValue("command", (Element) nList.item(0)));
            responseMap.put("status",getTagValue("status", (Element) nList.item(0)));
            responseMap.put("status_msg",getTagValue("status_msg", (Element) nList.item(0)));
            responseMap.put("status_details",getTagValue("status_details", (Element) nList.item(0)));
            responseMap.put("resp_code",getTagValue("resp_code", (Element) nList.item(0)));
            responseMap.put("trn",getTagValue("trn", (Element) nList.item(0)));
            responseMap.put("trndttm",getTagValue("trndttm", (Element) nList.item(0)));
            responseMap.put("approval",getTagValue("approval", (Element) nList.item(0)));
            responseMap.put("resp_code",getTagValue("resp_code", (Element) nList.item(0)));
            responseMap.put("cvc2_result",getTagValue("cvc2_result", (Element) nList.item(0)));
            responseMap.put("rrn",getTagValue("rrn", (Element) nList.item(0)));
            boolean isInserted=this.updateSTANSeqBankResponseTimeData(trackingID, stan, type, responseMap);
            transactionLogger.error("is Stan details updated------->"+isInserted);
            //Status - 0
            String description=getBankResponceDescription(responseBankCode);
            transactionLogger.error("Type---->"+type);
            if("REFUND".equalsIgnoreCase(type))
            {
                if (functions.isValueNull(responseStatus) && responseStatus.equals("0") && (responseBankCode.equalsIgnoreCase("00") || responseBankCode.equalsIgnoreCase("58")))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setRemark((getTagValue("status_msg", ((Element) nList.item(0)))));
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(description);
                }
            }else
            {
                if (functions.isValueNull(responseStatus) && responseStatus.equals("0") && responseBankCode.equalsIgnoreCase("00"))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setRemark((getTagValue("status_msg", ((Element) nList.item(0)))));
                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(description);
                }
            }

           // comm3DResponseVO.setStatus((getTagValue("status", (Element) nList.item(0))));
            comm3DResponseVO.setDescription(description);
            comm3DResponseVO.setTransactionId((getTagValue("trn", (Element) nList.item(0))));
            comm3DResponseVO.setBankTransactionDate(getTagValue("trndttm", (Element) nList.item(0)));
            comm3DResponseVO.setResponseHashInfo(getTagValue("approval", (Element) nList.item(0)));
            comm3DResponseVO.setAuthCode(getTagValue("approval", (Element) nList.item(0)));
            comm3DResponseVO.setEci(getTagValue("eci", (Element) nList.item(0)));
            comm3DResponseVO.setRrn(getTagValue("rrn", (Element) nList.item(0)));
            comm3DResponseVO.setErrorCode(responseBankCode);


        }catch (Exception e){
            transactionLogger.error("Exception---",e);
        }
        return comm3DResponseVO;
    }

    public Comm3DResponseVO readICardXMLResponeForCapture(String response,String trackingID,String type,String stan)
    {
        Comm3DResponseVO comm3DResponseVO= new Comm3DResponseVO();
        Functions functions = new Functions();
        HashMap<String,String> responseMap=new HashMap<>();
        try
        {
            Document doc = createDocumentFromString(response);
            //    Element rootElement = doc.getDocumentElement();
            NodeList nList = doc.getElementsByTagName("ipayin_response");
            String responseStatus = getTagValue("status", (Element) nList.item(0));
            String responseBankCode = getTagValue("resp_code", (Element) nList.item(0));

            transactionLogger.error("Status-----"+getTagValue("status", (Element) nList.item(0)));
            transactionLogger.error("Remark-----"+getTagValue("status_msg", (Element) nList.item(0)));
            transactionLogger.error("Description-----"+getTagValue("status_details", (Element) nList.item(0)));
            transactionLogger.error("TransactionID-----"+getTagValue("trn", (Element) nList.item(0)));
            transactionLogger.error("BankTransactionDate-----"+getTagValue("trndttm", (Element) nList.item(0)));
            transactionLogger.error("ResponseHashInfo-----"+getTagValue("approval", (Element) nList.item(0)));
            transactionLogger.error("ECI-----"+getTagValue("eci", (Element) nList.item(0)));
            transactionLogger.error("Response Code-----"+getTagValue("resp_code", (Element) nList.item(0)));
            transactionLogger.error("cvc2_result-----"+getTagValue("cvc2_result", (Element) nList.item(0)));
            transactionLogger.error("rrn-----"+getTagValue("rrn", (Element) nList.item(0)));
            responseMap.put("dttm",getTagValue("dttm", (Element) nList.item(0)));
            responseMap.put("command",getTagValue("command", (Element) nList.item(0)));
            responseMap.put("status",getTagValue("status", (Element) nList.item(0)));
            responseMap.put("status_msg",getTagValue("status_msg", (Element) nList.item(0)));
            responseMap.put("status_details",getTagValue("status_details", (Element) nList.item(0)));
            responseMap.put("resp_code",getTagValue("resp_code", (Element) nList.item(0)));
            responseMap.put("trn",getTagValue("trn", (Element) nList.item(0)));
            responseMap.put("trndttm",getTagValue("trndttm", (Element) nList.item(0)));
            responseMap.put("approval",getTagValue("approval", (Element) nList.item(0)));
            responseMap.put("resp_code",getTagValue("resp_code", (Element) nList.item(0)));
            responseMap.put("cvc2_result",getTagValue("cvc2_result", (Element) nList.item(0)));
            responseMap.put("rrn",getTagValue("rrn", (Element) nList.item(0)));
            boolean isInserted=this.updateSTANSeqBankResponseTimeData(trackingID, stan, type, responseMap);
            transactionLogger.error("is Stan details updated------->"+isInserted);
            //Status - 0
            if (functions.isValueNull(responseStatus) && responseStatus.equals("0"))
            {
                comm3DResponseVO.setStatus("success");
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
            }

            // comm3DResponseVO.setStatus((getTagValue("status", (Element) nList.item(0))));
            comm3DResponseVO.setRemark((getTagValue("status_msg", ((Element) nList.item(0)))));
            comm3DResponseVO.setDescription((getTagValue("status_msg", (Element) nList.item(0))));
            comm3DResponseVO.setTransactionId((getTagValue("trn", (Element) nList.item(0))));
            comm3DResponseVO.setBankTransactionDate(getTagValue("trndttm", (Element) nList.item(0)));
            comm3DResponseVO.setResponseHashInfo(getTagValue("approval", (Element) nList.item(0)));
            if(functions.isValueNull(getTagValue("approval", (Element) nList.item(0))))
                comm3DResponseVO.setAuthCode(getTagValue("approval", (Element) nList.item(0)));
            comm3DResponseVO.setEci(getTagValue("eci",(Element) nList.item(0)));
            if(functions.isValueNull(getTagValue("rrn", (Element) nList.item(0))))
                comm3DResponseVO.setRrn(getTagValue("rrn", (Element) nList.item(0)));
            comm3DResponseVO.setErrorCode(responseStatus);


        }catch (Exception e){
            transactionLogger.error("Exception---",e);
        }
        return comm3DResponseVO;
    }

    public String getStatusRemark(String responseStatus)
    {
        String remark = "";
        Functions functions = new Functions();

        if(functions.isValueNull(responseStatus))
        {
            if (responseStatus.equals("0"))
            {
                remark = "Command completed successfully";       //Status_msg - 0
            }
            else if (functions.isValueNull("1"))
            {
                remark = "General error";     //Status_msg - 1
            }
            else if (functions.isValueNull("2"))
            {
                remark = "Database error";    //Status_msg - 2
            }
            else if (functions.isValueNull("3"))
            {
                remark = "Invalid input parameters";         //Status_msg - 3
            }
            else if (functions.isValueNull("4"))
            {
                remark = "Incoming data parse error";       //Status_msg - 4
            }
            else if (functions.isValueNull("5"))
            {
                remark = "Unsupported command";           //Status_msg - 5
            }
            else if (functions.isValueNull("6"))
            {
                remark = "Communication error";           //Status_msg - 6
            }
            else if (functions.isValueNull("7"))
            {
                remark = "Transaction not found";         //Status_msg - 7
            }
            else if (functions.isValueNull("8"))
            {
                remark = "Transaction Not Allowed";       //Status_msg - 8
            }
            else if (functions.isValueNull("9"))
            {
                remark = "Duplicated Transmission";       //Status_msg - 9
            }
            else
            {
                remark = "Fail";
            }
        }

        return remark;
    }

    public static String doPostHttpConnection(String request, String url){
        String result="";
        try{
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
          //  java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

            PostMethod postMethod= new PostMethod(url);
            postMethod.setRequestHeader("Content-Type","application/xml");
            postMethod.setRequestBody(request);

            HttpClient httpClient= new HttpClient();
            httpClient.executeMethod(postMethod);

            result= String.valueOf(postMethod.getResponseBody());
        }
        catch (HttpException he){
            transactionLogger.error("HttpException-----",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException-----",io);
        }
        return result;
    }

    public static String doSocketConnection(String xmlrequest,String ipaddress, int port) throws IOException
    {
        //String ipaddress="91.206.21.12";
        //int port =11122;
        InputStream is = null;
        OutputStream os = null;

        transactionLogger.error("Establishing Socket connection---");

        Socket s = new Socket(ipaddress, port);
        os = s.getOutputStream();
        is = s.getInputStream();

        String req = xmlrequest;

        transactionLogger.debug("socket req---" + req);

        os.write(req.getBytes());
        String res = new String(readAll(is));

        return res;
    }

    private static byte[] readAll(InputStream is) throws IOException
    {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        while (true) {
            int len = is.read(buf);
            if (len < 0) {
                break;
            }
            baos.write(buf, 0, len);
        }
        return baos.toByteArray();
    }

    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }
    public  static String getBankResponceDescription(String bankcode)
    {
        String message="";
        switch (bankcode)
        {
            case "00": message="Approved or completed successfully"; break;
            case "01": message="Refer to card issuer Call Issuer"; break;
            case "03" :message="Invalid merchant Decline"; break;
            case "04": message="Capture card Capture"; break;
            case "05": message="Do not honor Decline"; break;
            case "08": message="Honor with ID Approve"; break;
            case "10": message="Partial Approval Approve"; break;
            case "12": message="Invalid transaction Decline"; break;
            case "13": message="Invalid amount Decline"; break;
            case "14": message="Invalid card number Decline"; break;
            case "15":message=" Invalid issuer Decline"; break;
            case "30":message=" Format error Decline"; break;
            case "41": message="Lost card Capture"; break;
            case "43": message="Stolen card Capture"; break;
            case "51": message="Insufficient funds/over credit limit"; break;
            case "54": message="Expired card Decline"; break;
            case "55": message="Invalid PIN Decline"; break;
            case "57": message="Transaction not permitted to issuer/cardholder"; break;
            case "58": message="Transaction not permitted to acquirer/terminal"; break;
            case "61": message="Exceeds withdrawal amount limit"; break;
            case "62": message="Restricted card Decline"; break;
            case "63": message="Security violation Decline"; break;
            case "65": message="Exceeds withdrawal count limit"; break;
            case "67": message="Black listed Card"; break;
            case "70": message="Contact Card Issuer Call Issuer"; break;
            case "71": message="PIN Not Changed Decline"; break;
            case "75": message="Allowable number of PIN tries exceeded"; break;
            case "76": message="Invalid/nonexistent “To Account” specified"; break;
            case "77": message="Invalid/nonexistent “From Account” specified"; break;
            case "78": message="Invalid/nonexistent account specified (general)"; break;
            case "81": message="Domestic Debit Transaction Not Allowed (Regional use only)"; break;
            case "84": message="Invalid Authorization Life Cycle"; break;
            case "85": message="Not declined Valid for all zero amount transactions."; break;
            case "86": message="PIN Validation not possible Decline"; break;
            case "87": message="Purchase Amount Only, No Cash Back Allowed"; break;
            case "88": message="Cryptographic failure Decline"; break;
            case "89": message="Unacceptable PIN—Transaction"; break;
            case "91": message="Authorization Platform or issuer system inoperative"; break;
            case "92": message="Unable to route transaction Decline"; break;
            case "94": message="Duplicate transmission detected"; break;
            case "96": message="System error Decline"; break;
            case "N7": message="Decline for CVV2 failure"; break;
            case "1A": message="Additional customer authentication required"; break;
            default:message="System error Decline";break;
        }
        return message;
    }
    public static synchronized String getNextSTAN(String trackingId,String type)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String currentSTAN="";
        long previousSTAN=0;
        try
        {
                conn = Database.getConnection();
                String query = "select stan as stan from transaction_icard_details order by id desc limit 1";
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next())
                {
                    previousSTAN = rs.getLong("stan");
                }
                transactionLogger.error("previous STAN------->" + previousSTAN);
                currentSTAN = String.format("%06d", previousSTAN + 1);
                transactionLogger.error("Current STAN------->" + currentSTAN);
                query = "insert into transaction_icard_details(trackingid,stan,type) values(?,?,?)";
                stmt = conn.prepareStatement(query);
                stmt.setString(1, trackingId);
                stmt.setString(2, currentSTAN);
                stmt.setString(3, type);
                transactionLogger.error("ICARD Utils------------>" + stmt);
                int k = stmt.executeUpdate();
                if (k > 0)
                {
                    return currentSTAN;
                }

        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return currentSTAN;
    }
    public boolean updateSTANSeqBankResponseTimeData(String trackingId, String STAN, String type,HashMap<String,String> responseMap)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean inserted = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_icard_details set dttm=?,command=?,trn=?,trndttm=?,original_trn=?,status=?,status_msg=?,status_details=?,cvc2_result=?,rrn=?,approval=? where trackingid=? AND stan=? AND type=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, responseMap.get("dttm"));//Bank side transaction time
            stmt.setString(2, responseMap.get("command"));//Bank side command
            stmt.setString(3, responseMap.get("trn"));
            stmt.setString(4, responseMap.get("trndttm"));
            stmt.setString(5, responseMap.get("original_trn"));
            stmt.setString(6, responseMap.get("status"));
            stmt.setString(7, responseMap.get("status_msg"));
            stmt.setString(8, responseMap.get("status_details"));
            stmt.setString(9, responseMap.get("cvc2_result"));
            stmt.setString(10, responseMap.get("rrn"));
            stmt.setString(11, responseMap.get("approval"));
            stmt.setString(12, trackingId);
            stmt.setString(13, STAN);
            stmt.setString(14, type);
            transactionLogger.error("ICARD Utils updateSTANSeqBankResponseTimeData------------>" + stmt);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                inserted = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return inserted;
    }
    public synchronized String getTimeUsingTrackingid(String trackingid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String time="";
        try
        {
            conn = Database.getConnection();
            String query = "select dttm from transaction_icard_details where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingid);
            rs = stmt.executeQuery();
            if(rs.next()){
                time=rs.getString("dttm");
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        transactionLogger.error("dttm time of provided trackingid------->"+time);
        return time;
    }

}

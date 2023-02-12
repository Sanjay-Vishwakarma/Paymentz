package com.payment.errors;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.EcoreResponseVO;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/15/14
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionError
{
    static Logger log = new Logger(TransactionError.class.getName());



    public void displayErrors(Hashtable error, Hashtable otherdetails, HttpServletResponse res, String toid, PrintWriter pWriter)
    {
        StringBuffer sbuf = new StringBuffer();

        if (error!=null && error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");


            otherdetails.put("MESSAGE", sbuf.toString());

            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            try
            {
                res.setContentType("text/html");
                pWriter.println(Template.getError(toid, otherdetails));
                pWriter.flush();

            }
            catch (SystemError se)
            {
                log.error("Excpetion in WaitServlet", se);

            }
            return;
        }
    }

    public void displayErrors(Hashtable error, Hashtable otherdetails, HttpServletResponse res, String toid,String trackingid,String description,String order_description,  PrintWriter pWriter)
    {
        StringBuffer sbuf = new StringBuffer();

        if (error!=null && error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");


            otherdetails.put("MESSAGE", sbuf.toString());
            otherdetails.put("TRACKING_ID", trackingid);
            otherdetails.put("DESCRIPTION", description);
            otherdetails.put("ORDER_DESCRIPTION", order_description);
            // otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            try
            {
                res.setContentType("text/html");
                pWriter.println(Template.getError(toid, otherdetails));
                pWriter.flush();

            }
            catch (SystemError se)
            {
                log.error("Excpetion in WaitServlet", se);

            }
            return;
        }
    }


    public void displayErrorPage(Hashtable otherdetails,String errorMessage, HttpServletResponse res, String toid,String trackingid,String description,String order_description, PrintWriter pWriter)
    {


        otherdetails.put("TRACKING_ID", trackingid);
        otherdetails.put("DESCRIPTION", description);
        otherdetails.put("ORDER_DESCRIPTION", order_description);
        otherdetails.put("MESSAGE", errorMessage);
        try
        {
            res.setContentType("text/html");
            pWriter.println(Template.getError(toid, otherdetails));
            pWriter.flush();

        }
        catch (SystemError se)
        {
            log.error("Excpetion in WaitServlet", se);

        }
        return;

    }

   /* public void  failedTransactionForEcore(String trackingid, String amount, String action, String status, EcoreResponseVO responseVO)
    {
        Transaction transaction = new Transaction();
        ActionEntry entry = new ActionEntry();
        try
        {
            int actionEntry = entry.actionEntryForEcore(trackingid,null,action,status,responseVO);
            transaction.updateTransactionStatusEcore(status, trackingid);
        }
        catch(Exception e)
        {
            log.error(e.getMessage());
        }

        return;

    }
*/


   /* public void calCheckSumAndWriteStatus(PrintWriter pWriter, String trackingId, String description, BigDecimal amount, String status, String statusMsg, String key, String checksumAlgo)
    {
        String checkSum = null;
        String amountStr = "";
        if (amount != null)
        {
            amountStr = amount.toString();
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amountStr, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
        }
        *//*System.out.println(description + " : " + status + " : " + statusMsg + " : " + trackingId + " : " + amount + " : " + checkSum);
        HashMap responseMap = new HashMap();
        responseMap.put("orderid",description);
        responseMap.put("status",status);
        responseMap.put("statusdescription",statusMsg);
        responseMap.put("trackingid",trackingId);
        responseMap.put("amount",amount);
        responseMap.put("checkSum",checkSum);*//*

        //pWriter.write(WriteXMLResponse.writeSaleResponse(responseMap));
        pWriter.write(description + " : " + status + " : " + statusMsg + " : " + trackingId + " : " + amount + " : " + checkSum);
    }

    public void calCheckSumAndWriteStatus(PrintWriter pWriter, String description, String trackingId, String amount, String captureamount, String status, String statusMsg, String key, String checksumAlgo)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
        }
        pWriter.write(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamount + " : " + checkSum);
    }

    public void calCheckSumAndWriteStatus(PrintWriter pWriter, String trackingid, String amount, StringBuffer status, StringBuffer statusMsg, String key, String checksumAlgo)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(trackingid, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
        }
        pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + amount + ":" + checkSum);
    }
*/
    public void displayErrorsForCommon(Hashtable error, Hashtable otherdetails, String toid, Hashtable responseHash, String trackingid,String description,String order_description)
    {
        StringBuffer sbuf = new StringBuffer();

        if (error!=null && error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");
            sbuf.append("<a href=\"javascript:history.go(-1)\">back </></center>");
            otherdetails.put("MESSAGE", sbuf.toString());
            otherdetails.put("TRACKING_ID", trackingid);
            otherdetails.put("DESCRIPTION", description);
            otherdetails.put("ORDER_DESCRIPTION", order_description);
            responseHash.put("status", "error");
            try
            {
                responseHash.put("redirect", Template.getErrorPage(toid, otherdetails));
            }
            catch (SystemError systemError)
            {
                log.error("Exception while getting error template", systemError);  //To change body of catch statement use File | Settings | File Templates.
            }
            return ;
        }
    }

    public void displayErrorPageForCommon(Hashtable otherdetails,String errorMessage, String toid,String trackingid,String description,String order_description,Hashtable responseHash)
    {

        otherdetails.put("TRACKING_ID", trackingid);
        otherdetails.put("DESCRIPTION", description);
        otherdetails.put("ORDER_DESCRIPTION", order_description);
        otherdetails.put("MESSAGE", errorMessage);
        //otherdetails.put("RETRYBUTTON","<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");
        responseHash.put("status", "error");
        try
        {
            responseHash.put("redirect", Template.getErrorPage(toid, otherdetails));
        }
        catch (SystemError systemError)
        {
            log.error("Exception while getting error template", systemError);  //To change body of catch statement use File | Settings | File Templates.
        }

        return;

    }


}

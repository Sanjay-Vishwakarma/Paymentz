package com.payment.cashflow.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 10/2/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CashFlowUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(CashFlowAccounts.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CashFlowAccounts.class.getName());
    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;
        try
        {
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);


            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            //con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }

        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null,ex.getMessage(), ex.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    static class Cashflow
    {
        public byte buf[];
        public int size;

        public Cashflow(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Cashflow> bufList = new LinkedList<Cashflow>();
        int size = 0;
        String buffer=null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Cashflow(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Cashflow> p = bufList.listIterator(); p.hasNext();)
            {
                Cashflow b = p.next();
                for (int i = 0; i < b.size;)
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
        }

        return buffer;
    }

    public Hashtable spiltResponse(String url)
    {
        String[] temp = url.split("\\|");
        String value = "";
        Hashtable dataHash = new Hashtable();

        dataHash.put("authorised", temp[0]);
        dataHash.put("transactionId", temp[1]);
        dataHash.put("cvv", temp[2]);
        dataHash.put("authCode", temp[3]);
        dataHash.put("authRequest",temp[4]);

        return dataHash;
    }

    public static void main(String[] args)
    {
        CashFlowUtills cashFlowUtills = new CashFlowUtills();
        cashFlowUtills.spiltResponse("V|99E542FB4FD|000|V226|Invalid request");
    }
}

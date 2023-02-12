package com.payment.paspx;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 10/30/14
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaspxUtills
{
    public final static String charset = "UTF-8";
    private static Logger log = new Logger(PaspxUtills.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaspxUtills.class.getName());

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            URL url = new URL(strURL);

            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);//2min
                con.setReadTimeout(120000);
            }

            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null,io.getMessage(),io.getCause());
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
            PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,me.getMessage(),me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CashflowUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            log.error("exCeption------",ex);
            System.out.println("caught @IOException");
            PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    static class Paspx
    {
        public byte buf[];
        public int size;

        public Paspx(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }
    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Paspx> bufList = new LinkedList<Paspx>();
        String buffer=null;
        int size = 0;
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
                bufList.add(new Paspx(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Paspx> p = bufList.listIterator(); p.hasNext();)
            {
                Paspx b = p.next();
                for (int i = 0; i < b.size;)
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }

            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PaspxUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }

        return buffer;
    }

    public HashMap readHtmlResponse(String htmlData)
    {
        HashMap responseHash = new HashMap();

        Document doc = Jsoup.parse(htmlData);
        Elements tableElements = doc.select("table");
        Elements tableRowElements = tableElements.select(":not(thead) tr");
        for (int i = 0; i < tableRowElements.size(); i++)
        {
            Element row = tableRowElements.get(i);
            StringBuffer sb=new StringBuffer();
            Elements rowItems = row.select("td");
            for (int j = 0; j < rowItems.size(); j++)
            {
                String key=rowItems.get(j).text();
                String value=rowItems.get(++j).text();
                responseHash.put(key,value);
            }
        }
        return responseHash;
    }
}

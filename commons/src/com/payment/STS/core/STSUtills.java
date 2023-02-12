package com.payment.STS.core;

import com.directi.pg.Logger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/6/14
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSUtills
{
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(STSUtills.class.getName());

    public static String joinMapValue(Map<String, String> map, char connector)
    {
        StringBuffer b = new StringBuffer();
        int cnt = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                b.append(entry.getValue());
            }
            cnt++;
            if(cnt<map.size())
            {
                b.append(connector);
            }
        }
        return b.toString();
    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection con = null;

        try
        {
            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL.trim());

            try
            {
                con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
            }

            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
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
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (SocketTimeoutException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, ex.getMessage(), ex.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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
                    PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException
    {
        LinkedList<Sts> bufList = new LinkedList<Sts>();
        int size = 0;
        String buffer = null;
        try
        {
            byte buf[];
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Sts(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Sts> p = bufList.listIterator(); p.hasNext();) {
                Sts b = p.next();
                for (int i = 0; i < b.size;) {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf,charset);
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }

    static class Sts
    {
        public byte buf[];
        public int size;

        public Sts(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    public Hashtable getStringValues(String response) throws PZTechnicalViolationException
    {
        JSONObject json = null;
        Hashtable data = new Hashtable();
        try
        {
            json = new JSONObject(response);

            Iterator i = json.keys();

            while (i.hasNext())
            {
                String key = (String) i.next();
                data.put(key,String.valueOf(json.get(key)));
            }
        }
        catch (JSONException e)
        {
            log.error("=====JSONException======", e);
            PZExceptionHandler.raiseTechnicalViolationException("STSUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.JSON_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        return data;
    }

}

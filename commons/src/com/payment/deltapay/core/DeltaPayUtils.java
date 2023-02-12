package com.payment.deltapay.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 9/8/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeltaPayUtils
{


    public final static String charset = "UTF-8";

    private static Logger log = new Logger(DeltaPayUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DeltaPayUtils.class.getName());

    public static void main (String args[])
    {
        //String r="ID:4762f7040db956a94588c5bfb1785cd991bf69d1~Status:Failed~MerchantID:0123475989~Terminal:finestraspberryketone.com~ResultCode:908~ApprovalCode:-3~NameOnCard:test test~CardMasked:5413********0027";
        //getpayWorldResponseVO(r);
    }
    /**
     *
     * @param map
     * @param connector
     * @return
     */
    public static String joinMapValue(Map<String, String> map, char connector) {
        StringBuffer b = new StringBuffer();
        int cnt = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                b.append(entry.getValue());
                b.append("");
            }
            cnt++;
            if(cnt<map.size())
            {
                b.append(connector);
            }
        }
        return b.toString();
    }


    public static CommResponseVO getdeltaPayResponseVO(String ResponseString)
    {
        CommResponseVO deltaPayResVO=new CommResponseVO();
        Hashtable responseString =new Hashtable();
        if(ResponseString!=null || !("").equals(ResponseString))
        {
            String res[]=ResponseString.split("&");


            for(int i=0;i<res.length;i++)
            {
                String field=res[i];

                String temp[]=field.split("=");

                String response[]=new String[2];

                for(int x=0;x<temp.length;x++)
                {
                    response[x]=temp[x];
                }
                if(response[1]==null || response[1].equalsIgnoreCase(""))
                {
                    responseString.put(response[0],"");
                }
                else
                {
                    responseString.put(response[0],response[1]);
                }

            }
        }
            if(responseString!=null)
            {

                if(responseString.get(Elements.ELEM_STATUS)!=null && !responseString.get(Elements.ELEM_STATUS).equals(""))
                {
                    if(("APPROVED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("AUTHORIZED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("REFUND REQUEST").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("REFUNDED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("CAPTURED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("SETTLED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS))
                            ||("VOIDED").equalsIgnoreCase((String)responseString.get(Elements.ELEM_STATUS)))
                    {
                        deltaPayResVO.setStatus("success");
                        deltaPayResVO.setTransactionStatus((String)responseString.get(Elements.ELEM_STATUS));
                    }
                    else
                    {
                        deltaPayResVO.setStatus("fail");
                        deltaPayResVO.setTransactionStatus((String)responseString.get(Elements.ELEM_STATUS));
                    }
                }
                if(responseString.get(Elements.ELEM_TRANSACTION_NO)!=null && !responseString.get(Elements.ELEM_TRANSACTION_NO).equals(""))
                {
                    deltaPayResVO.setTransactionId((String)responseString.get(Elements.ELEM_TRANSACTION_NO));
                }
                if(responseString.get(Elements.ELEM_DESCRIPTION)!=null && !responseString.get(Elements.ELEM_DESCRIPTION).equals(""))
                {
                    deltaPayResVO.setDescription((String)responseString.get(Elements.ELEM_DESCRIPTION));
                }
                if(responseString.get(Elements.ELEM_STATUS_DESCRIPTION)!=null && !responseString.get(Elements.ELEM_STATUS_DESCRIPTION).equals(""))
                {
                    deltaPayResVO.setDescription((String)responseString.get(Elements.ELEM_STATUS_DESCRIPTION));
                }
                if(responseString.get(Elements.ELEM_DESCRIPTOR)!=null && !responseString.get(Elements.ELEM_DESCRIPTOR).equals(""))
                {
                    deltaPayResVO.setDescriptor((String)responseString.get(Elements.ELEM_DESCRIPTOR));
                }
                if(responseString.get(Elements.ELEM_REASON)!=null && !responseString.get(Elements.ELEM_REASON).equals(""))
                {
                    deltaPayResVO.setDescription((String)responseString.get(Elements.ELEM_REASON));
                }
                if(responseString.get(Elements.ELEM_APPROVAL_NO)!=null && !responseString.get(Elements.ELEM_APPROVAL_NO).equals(""))
                {
                    deltaPayResVO.setErrorCode((String)responseString.get(Elements.ELEM_APPROVAL_NO));
                }

            }

        return deltaPayResVO;
    }

    /**
     *
     * @param strURL
     * @param req
     * @return
     */
    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        String result = "";
        BufferedReader in=null;
        BufferedOutputStream out = null;


        try
        {
            URL url = new URL(strURL);
            URLConnection con =  url.openConnection();
            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            //con.setRequestProperty("Content-length",String.valueOf (strURL.length()));
            //con.setRequestProperty("Content-type","text/xml");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);
            out.close();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                result = result + decodedString;
            }
            in.close();

        }
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DeltaPayUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","MalFormed Url Exception while placing Transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DeltaPayUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","UnSupported Encoding Exception while placing Transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (ProtocolException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DeltaPayUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Protocol Exception while placing Transaction", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DeltaPayUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "IO Exception while placing Transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }



    public static String doPostHTTPSURLConnection_new(String strURL, String req) throws SystemError{
        String result = "";
        BufferedReader in=null;
        BufferedOutputStream out = null;

        try
        {

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);

            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            log.info("Response" + response);
            transactionLogger.info("Response" + response);
            result= response;


        } catch (Exception ex) {

            log.error("Exception during URL Connection= 2 ====" + ex);
            transactionLogger.error("Exception during URL Connection= 2 ====" + ex);

            throw new SystemError("Exception during URL Connection");


        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }



}

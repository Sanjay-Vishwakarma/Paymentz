package com.directi.pg.core.payworld;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.utils.SSLUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/24/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayWorldUtils
{

    public final static String charset = "UTF-8";

    private static Logger log = new Logger(PayWorldUtils.class.getName());

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


    public static CommResponseVO getpayWorldResponseVO(String ResponseString)
    {
        CommResponseVO payWorldresVO=new CommResponseVO();
        Hashtable responseString =new Hashtable();
        if(ResponseString!=null || !("").equals(ResponseString))
        {
            String res[]=ResponseString.split("~");
            for(int i=0;i<res.length;i++)
            {
                String field=res[i];
                String response[]=field.split(":");

                responseString.put(response[0],response[1]);
            }

            if(responseString!=null)
            {
                if(responseString.get("Status")!=null || !responseString.get("Status").equals(""))
                {
                    if(("success").equalsIgnoreCase((String)responseString.get("Status")))
                    {
                        payWorldresVO.setStatus("success");
                        //payWorldresVO.setDescription((String)responseString.get("Status"));
                        payWorldresVO.setTransactionStatus("success");
                    }
                    else
                    {
                        payWorldresVO.setStatus("fail");
                        //payWorldresVO.setDescription((String)responseString.get("Status"));
                        payWorldresVO.setTransactionStatus("fail");
                    }
                }
                if(responseString.get("ID")!=null || !responseString.get("ID").equals(""))
                {
                    payWorldresVO.setTransactionId((String)responseString.get("ID"));
                }
                if(responseString.get("ResultCode")!=null || !responseString.get("ResultCode").equals(""))
                {
                    payWorldresVO.setErrorCode((String)responseString.get("ResultCode"));
                }

            }
        }
        return payWorldresVO;
    }

    /**
     *
     *
     * @param strURL
     * @param map
     * @return
     */
    //method committed by nishant
/*    public static String doPostHTTPSURLConnection(String strURL, String req) throws SystemError
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

            con.setRequestProperty("Content-length",String.valueOf (strURL.length()));

            OutputStreamWriter outSW = new OutputStreamWriter(
                    con.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                result = result + decodedString;
            }
            in.close();

        } catch (Exception ex) {

            log.error("Exception during URL Connection= 2 ====" + ex);

            ex.printStackTrace();
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
    }*/
    //Method Committed by jignesh
    public static String doPostHTTPSURLConnection(String strURL, Map<String,String> map) throws PZTechnicalViolationException
    {
        String result = "";
        BufferedReader in=null;
        BufferedOutputStream out = null;
        NameValuePair[] data = null;

        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

            SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();

            data = getNamedValuePairArray(map);

            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(strURL);
            post.setRequestBody(data);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            log.error("Response" + response);
            result= response;

        }
        catch (HttpException he)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayWorldUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,he.getMessage(),he.getCause());

        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PayWorldUtils.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
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
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }
    private static NameValuePair[] getNamedValuePairArray(Map<String,String> mapInternal)
    {
        NameValuePair result[] = new NameValuePair[mapInternal.size()];
        int counter = 0;
        for (Iterator iterator = mapInternal.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            result[counter++] = new NameValuePair(key, (String) mapInternal.get(key));
        }
        /*
		if (addContentParams_) {
			result[counter++] = new NameValuePair("Content-Type", "application/x-www-form-urlencoded");
			result[counter++] = new NameValuePair("Content-Length",	(new StringBuilder()).append(params_.size()).toString());
		}*/
        return result;
    }
}


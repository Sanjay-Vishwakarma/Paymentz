package com.fraud.at;

import com.directi.pg.Logger;
import com.fraud.vo.PZFraudResponseVO;
//import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;  // Updated on 24-05-2019
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/12/14
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class AtUtils
{
    // character encoding
    public final static String charset = "UTF-8";
    private static Logger logger = new Logger(AtUtils.class.getName());

    private static String ReadByteStream(BufferedInputStream in) throws IOException {
        LinkedList<ConnectionUtilsBuf> bufList = new LinkedList<ConnectionUtilsBuf>();
        int size = 0;
        byte buf[];
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if (num == -1)
                break;
            size += num;
            bufList.add(new ConnectionUtilsBuf(buf, num));
        } while (true);
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<ConnectionUtilsBuf> p = bufList.listIterator(); p.hasNext();) {
            ConnectionUtilsBuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }

        return new String(buf,charset);
    }

    public static String[] getResArr(String str) {
        String regex = "(.*?cupReserved\\=)(\\{[^}]+\\})(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);

        String reserved = "";
        if (matcher.find()) {
            reserved = matcher.group(2);
        }

        String result = str.replaceFirst(regex, "$1$3");
        String[] resArr = result.split(",");
        for (int i = 0; i < resArr.length; i++) {
            if ("cupReserved=".equals(resArr[i])) {
                resArr[i] += reserved;
            }
        }
        return resArr;
    }

    public String createQueryStr(String[] valueVo, String[] keyVo) {

        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < keyVo.length; i++) {
            map.put(keyVo[i], valueVo[i]);
        }

        return joinMapValue(map, '&');
    }

    public String joinMapValue(Map<String, String> map, char connector)
    {
        StringBuffer b = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null)
            {
                b.append(entry.getValue());
            }
            b.append(connector);
        }
        return b.toString();
    }

    public String joinMapValueBySpecial(Map<String, String> map, char connector) {
        StringBuffer b = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {

            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                try {
                    b.append(java.net.URLEncoder.encode(entry.getValue(),charset));
                } catch (UnsupportedEncodingException e) {

                    logger.error("UnsupportedEncodingException---->",e);
                }
            }
            b.append(connector);
        }
        return b.toString();
    }

    public String doPostURLConnection(String strURL, String req) throws IOException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            URL url = new URL(strURL);
            URLConnection con = url.openConnection();
            /*		if (con instanceof HttpsURLConnection) {
                   ((HttpsURLConnection) con).setHostnameVerifier(new HostnameVerifier() {
                       @Override
                       public boolean verify(String hostname, SSLSession session) {
                           return true;
                       }
                   });
               }*/
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);
            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        }
        /* catch (Exception ex)
     {   *//*ex.printStackTrace();
            //log.error("Exception during URL Connection=  1" + ex);
            logger.error(ex);*//*
            throw new SystemError("There was an Error while posting data to Fraud Engine.");
        }*/
        finally
        {
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

    public String doPostHTTPSURLConnection(String strURL, String req) throws Exception{
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {

            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);

            con.setRequestMethod("POST");

            //con.setRequestProperty("Content-length",String.valueOf (req.length()));
            //con.setRequestProperty("Content-Type","application/x-www- form-urlencoded");
            //con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");

            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);
            out.close();

            //System.out.println("Resp Code:"+con.getResponseCode());
            //System.out.println("Resp Message:"+ con.getResponseMessage());

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        } catch (Exception ex) {

            //log.error("Exception during URL Connection= 2 ====" + ex);
            System.out.print(ex);

            throw new Exception("Exception during URL Connection");


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

    public PZFraudResponseVO getPZFraudResponseVO(ATResponseVO responseVO,List<String> errorList,String pzfraudtransid)
    {


        PZFraudResponseVO pzFraudResponseVO=new PZFraudResponseVO();
        pzFraudResponseVO.setRecommendation(responseVO.getRec());

        pzFraudResponseVO.setFsTransId(pzfraudtransid);
        pzFraudResponseVO.setScore(responseVO.getScore());
        if(responseVO.getStatus().equals("0"))
        {
            JSONObject jsonObject=responseVO.getJsonObject();
            pzFraudResponseVO.setResponseCode("0"); //Set Process Successfully
            pzFraudResponseVO.setStatus("Success");
            pzFraudResponseVO.setDescription("Thank you for using online fraud service with pz");
            if(jsonObject!=null && jsonObject.has("rules_triggered"))

            {
                try
                {
                    JSONArray arr =jsonObject.getJSONArray("rules_triggered");
                    if(arr.length()>0)
                    {
                        pzFraudResponseVO.setRulesTriggered(arr.toString());
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error while reading json object===="+e.getStackTrace());
                }

            }
        }
        else
        {
            pzFraudResponseVO.setResponseCode(responseVO.getStatus()); //Set ProcessFailed
            errorList.add(responseVO.getDescription());
            pzFraudResponseVO.setStatus("Failed");
            pzFraudResponseVO.setDescription(responseVO.getDescription());
        }
        pzFraudResponseVO.setErrorList(errorList);
        return pzFraudResponseVO;
    }
}
class ConnectionUtilsBuf {

    public byte buf[];
    public int size;

    public ConnectionUtilsBuf(byte b[], int s) {
        buf = b;
        size = s;
    }

}
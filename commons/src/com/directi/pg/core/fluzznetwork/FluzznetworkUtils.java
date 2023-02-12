package com.directi.pg.core.fluzznetwork;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 11/4/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class FluzznetworkUtils
{
    public final static String charset = "UTF-8";

    private static Logger log = new Logger(FluzznetworkUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FluzznetworkUtils.class.getName());

    public static String joinMapValue(Map<String, String> map, char connector) {
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


        try
        {
            URL url = new URL(strURL);
            URLConnection con = url.openConnection();

            if(con instanceof HttpURLConnection)
            {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoInput(true);
            con.setDoOutput(true);
            out = new BufferedOutputStream(con.getOutputStream());
            byte outBuf[] = req.getBytes(charset);
            out.write(outBuf);

            out.close();

            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);

        }
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FluzznetworkUtils.class.getName(),"doPostHTTPSURLConnection()",null,"common","MalFormed URL Exception while placing transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FluzznetworkUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Unsupported Encoding Exception while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (ProtocolException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FluzznetworkUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "Protocol Exception while placing transaction", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FluzznetworkUtils.class.getName(), "doPostHTTPSURLConnection()", null, "common", "IO Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
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


    /**
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String ReadByteStream(BufferedInputStream in) throws IOException {
        LinkedList<fluzznetworkBuf> bufList = new LinkedList<fluzznetworkBuf>();
        int size = 0;
        byte buf[];
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if (num == -1)
                break;
            size += num;
            bufList.add(new fluzznetworkBuf(buf, num));
        } while (true);
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<fluzznetworkBuf> p = bufList.listIterator(); p.hasNext();) {
            fluzznetworkBuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }

        return new String(buf,charset);
    }
     public static CommResponseVO getFluzznetworkResponseVOInquiry(String xmlResponseString)
{
CommResponseVO res= new CommResponseVO();
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = null;
String strValue = null;
NodeList list=null;
try
{
builder = factory.newDocumentBuilder();
}
catch (ParserConfigurationException e)
{
log.error("ParserConfigurationException---->",e);  //To change body of catch statement use File | Settings | File Templates.
}
Document document = null;
try
{
document = builder.parse(new InputSource(new StringReader(xmlResponseString)));
}
catch (SAXException e)
{
    log.error("SAXException---->", e);  //To change body of catch statement use File | Settings | File Templates.
}
catch (IOException e)
{
    log.error("IOException---->", e);  //To change body of catch statement use File | Settings | File Templates.
}

Element rootElement = document.getDocumentElement();
strValue = getTagValue("nm_response", rootElement);

list = document.getElementsByTagName("transaction");
for (int i = 0; i < list.getLength(); i++)
{
Node node = list.item(i);
if (node.getNodeType() == Node.ELEMENT_NODE)
{
    Element element = (Element) node;
    res.setTransactionId(getTagValue("transaction_id",element));
    /*res.setResult(getTagValue("result", element));
                res.setCode(getTagValue("code", element));
                res.setText(getTagValue("text", element));*/
            }
        }

        list = document.getElementsByTagName("action");
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) node;

                res.setTransactionStatus(getTagValue("response_text",element));
                res.setErrorCode(getTagValue("response_code",element));
                res.setAmount(getTagValue("amount",element));
                res.setTransactionType(getTagValue("action_type",element));

            }
        }


        return res;
    }
    public static Map<String,String> getResponseString(String resValue)
    {
        String res[]=resValue.split("&");
        Map<String,String> responseString= new TreeMap<String, String>();
        for(int i=0;i<res.length;i++)
        {
            String field=res[i];
            String temp[]=field.split("=");
            String response[]=new String[2];

            for(int x=0;x<temp.length;x++)
            {
                response[x]=temp[x];
            }
            if(response[1]!=null && !response[1].equalsIgnoreCase(""))
            {
                responseString.put(response[0],response[1]);
            }
            else
            {
                responseString.put(response[0],"");
            }
        }

        return responseString;
    }

    private static String getTagValue(String sTag, Element eElement) {

        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();

        }

        return value;

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
        String[] resArr = result.split("&");
        for (int i = 0; i < resArr.length; i++) {
            if ("cupReserved=".equals(resArr[i])) {
                resArr[i] += reserved;
            }
        }
        return resArr;
    }


}

class fluzznetworkBuf {

    public byte buf[];
    public int size;

    public fluzznetworkBuf(byte b[], int s) {
        buf = b;
        size = s;
    }
}

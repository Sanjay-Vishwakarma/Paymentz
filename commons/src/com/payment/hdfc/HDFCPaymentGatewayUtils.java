package com.payment.hdfc;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xjava.security.Cipher;
import cryptix.provider.key.RawSecretKey;
import cryptix.util.core.Hex;
import cryptix.provider.Cryptix;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 2022-03-08.
 */
public class HDFCPaymentGatewayUtils
{
    static TransactionLogger transactionLogger = new TransactionLogger(HDFCPaymentGatewayUtils.class.getName());

    public static String encryptText(String key, String valueToBeEncrypted)
    {
        String enc1 = "";
        String value = "";
        String encadd = "";
        String key1 = "";
        String key2 = "";
        String key3 = "";
        String checking = "";
        try 
        {
            key1 = alpha2Hex(key.substring(0, 8));
            key2 = alpha2Hex(key.substring(8, 16));
            key3 = alpha2Hex(key.substring(16, 24));

            if ((valueToBeEncrypted.length() % 8) != 0) 
            {
                valueToBeEncrypted = rightPadZeros(valueToBeEncrypted);
            }
            for (int i = 0; i < valueToBeEncrypted.length(); i = i + 8) 
            {
                value = valueToBeEncrypted.substring(i, i + 8);
                checking = checking + alpha2Hex(value);
                enc1 = getTripleHexValue(alpha2Hex(value), key1, key2, key3);
                encadd = encadd + enc1;
            }
            return encadd;
        } 
        catch (Exception e) 
        {
            transactionLogger.error("Exception While encrypting data : "+e);
        } 
        finally 
        {
            enc1 = null;
            value = null;
            encadd = null;
            key1 = null;
            key2 = null;
            key3 = null;
        }
        return "";
    }

    public static String alpha2Hex(String data) 
    {
        char[] alpha = data.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < alpha.length; i++) 
        {
            int count = Integer.toHexString(alpha[i]).toUpperCase().length();
            if (count <= 1) 
            {
                sb.append("0").append(Integer.toHexString(alpha[i]).toUpperCase());
            } else 
            {
                sb.append(Integer.toHexString(alpha[i]).toUpperCase());
            }
        }
        return sb.toString();
    }

    public static String rightPadZeros(String Str) 
    {
        if (null == Str) 
        {
            return null;
        }
        String PadStr = new String(Str);
        for (int i = Str.length(); (i % 8) != 0; i++) 
        {
            PadStr = PadStr + '^';
        }
        return PadStr;
    }

    public static String getTripleHexValue(final String pin, final String key1, final String key2, final String key3)
    {
        try 
        {
            Security.addProvider(new Cryptix());
            String encryptedKey = getHexValue(pin, key1);
            encryptedKey = getDexValue(encryptedKey, key2);
            encryptedKey = binary2hex(asciiChar2binary(encryptedKey)).toUpperCase();
            encryptedKey = getHexValue(encryptedKey, key3);
            return encryptedKey;
        } 
        catch (final Exception e) 
        {
            transactionLogger.error("Exception While getTripleHexValue : "+e);
        }
        return "";
    }

    public static String getHexValue(final String pin, final String key)
    {
        try 
        {
            Cipher des = null;
            RawSecretKey desKey = null;
            des = Cipher.getInstance("DES/ECB/NONE", "Cryptix");
            desKey = new RawSecretKey("DES", Hex.fromString(key));
            des.initEncrypt(desKey);
            final byte[] pinInByteArray = Hex.fromString(pin);
            final byte[] ciphertext = des.crypt(pinInByteArray);
            return (Hex.toString(ciphertext));
        }
        catch (final Exception e) 
        {
            transactionLogger.error("Exception while get Hex value: "+e);
        }
        return "";
    }

    public static String getDexValue(final String pin, final String key) 
    {
        byte[] ciphertext = null;
        byte[] pinInByteArray = null;
        try {
            Cipher des = null;
            RawSecretKey desKey = null;

            des = Cipher.getInstance("DES/ECB/NONE", "Cryptix");
            desKey = new RawSecretKey("DES", Hex.fromString(key));

            // des.initEncrypt(desKey);
            des.initDecrypt(desKey);
            pinInByteArray = Hex.fromString(pin);
            ciphertext = des.crypt(pinInByteArray);
        }
        catch (final Exception e)
        {
            transactionLogger.error("Exception while get DEX value: "+e);
        }
        return toString(ciphertext);
    }

    public static String toString(final byte[] temp)
    {
        final char ch[] = new char[temp.length];
        for (int i = 0; i < temp.length; i++)
        {
            ch[i] = (char) temp[i];
        }
        final String s = new String(ch);
        return s;
    }

    public static String asciiChar2binary(final String asciiString)
    {
        if (asciiString == null)
        {
            return null;
        }
        String binaryString = "";
        String temp = "";
        int intValue = 0;
        for (int i = 0; i < asciiString.length(); i++)
        {
            intValue = (int) asciiString.charAt(i);
            temp = "00000000" + Integer.toBinaryString(intValue);
            binaryString += temp.substring(temp.length() - 8);
        }
        return binaryString;
    }

    public static String binary2hex(final String binaryString)
    {
        if (binaryString == null) {
            return null;
        }
        String hexString = "";
        for (int i = 0; i < binaryString.length(); i += 8) {
            String temp = binaryString.substring(i, i + 8);
            int intValue = 0;
            for (int k = 0, j = temp.length() - 1; j >= 0; j--, k++)
            {
                intValue += Integer.parseInt("" + temp.charAt(j)) * Math.pow(2, k);
            }
            temp = "0" + Integer.toHexString(intValue);
            hexString += temp.substring(temp.length() - 2);
        }
        return hexString;
    }

    public static String decryptText(String key, String valueToBeDecrypted)
    {
        String key1 = "";
        String key2 = "";
        String key3 = "";
        try
        {
            key1 = alpha2Hex(key.substring(0, 8));
            key2 = alpha2Hex(key.substring(8, 16));
            key3 = alpha2Hex(key.substring(16, 24));

            String decryptedStr= getTripleDesValue(valueToBeDecrypted,key3,key2,key1);
            decryptedStr = hexToString(decryptedStr);

            if(decryptedStr.startsWith("<"))
                decryptedStr = decryptedStr.substring(0 , decryptedStr.lastIndexOf('>')+1);
            else
                decryptedStr= decryptedStr.substring(0 , decryptedStr.lastIndexOf('&')+1);

            return decryptedStr;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while decrypting data:-->"+e);
        }
        finally
        {
            key1 = null;
            key2 = null;
            key3 = null;
        }
        return null;
    }

    public static String getTripleDesValue(final String pin, final String key1,final String key2, final String key3)
    {
        String decryptedKey= null;
        try
        {
            Security.addProvider(new Cryptix());
            decryptedKey= getDexValue(pin, key1);
            decryptedKey = binary2hex(asciiChar2binary(decryptedKey)).toUpperCase();
            decryptedKey = getHexValue(decryptedKey, key2);
            decryptedKey = getDexValue(decryptedKey, key3);
            decryptedKey = binary2hex(asciiChar2binary(decryptedKey)).toUpperCase();
        }
        catch (final Exception e)
        {
            transactionLogger.error("Exception while getTripleDesValue:-->"+e);
        }
        return decryptedKey;
    }

    public static String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }

    public static Map<String, String> readHDFCxmlResponse(String str)
    {
        Map<String, String> responseMap = new HashMap<>();
        try
        {
            Document doc = createDocumentFromString("<manualNode>" + str + "</manualNode>"); // todo added because in response we not get any root node so to read response manually added this root node
            doc.getDocumentElement().getNodeName();

            NodeList nList = doc.getElementsByTagName("manualNode");

            responseMap.put("result", getTagValue("result", ((Element) nList.item(0))));
            responseMap.put("auth", getTagValue("auth", ((Element) nList.item(0))));
            responseMap.put("ref", getTagValue("ref", ((Element) nList.item(0))));
            responseMap.put("avr", getTagValue("avr", ((Element) nList.item(0))));
            responseMap.put("postdate", getTagValue("postdate", ((Element) nList.item(0))));
            responseMap.put("paymentid", getTagValue("paymentid", ((Element) nList.item(0))));
            responseMap.put("tranid", getTagValue("tranid", ((Element) nList.item(0))));
            responseMap.put("authRespCode", getTagValue("authRespCode", ((Element) nList.item(0))));
            responseMap.put("trackid", getTagValue("trackid", ((Element) nList.item(0))));
            responseMap.put("SiStatus", getTagValue("SiStatus", ((Element) nList.item(0))));
            responseMap.put("SihubID", getTagValue("SihubID", ((Element) nList.item(0))));
            responseMap.put("error_code_tag", getTagValue("error_code_tag", ((Element) nList.item(0))));
            responseMap.put("error_text", getTagValue("error_text", ((Element) nList.item(0))));
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while Reading XML response:--->"+e);
        }

        return responseMap;
    }

    public static Document createDocumentFromString(String xmlString) throws PZTechnicalViolationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (ParserConfigurationException pce){
            transactionLogger.error("Exception in createDocumentFromString of EasyPaymentUtils", pce);
            PZExceptionHandler.raiseTechnicalViolationException("HDFCPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, pce.getMessage(), pce.getCause());
        }
        catch (SAXException e){
            transactionLogger.error("Exception in createDocumentFromString EasyPaymentUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("HDFCPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.SAXEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            transactionLogger.error("Exception in createDocumentFromString EasyPaymentUtils", e);
            PZExceptionHandler.raiseTechnicalViolationException("HDFCPaymentGatewayUtils.java", "createDocumentFromString()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return document;
    }

    private static String getTagValue(String Tag, Element eElement)
    {
        NodeList nlList = null;
        String value    = "";
        if (eElement != null && eElement.getElementsByTagName(Tag) != null && eElement.getElementsByTagName(Tag).item(0) != null)
            nlList = eElement.getElementsByTagName(Tag).item(0).getChildNodes();

        if (nlList != null && nlList.item(0) != null)
        {
            Node nValue = nlList.item(0);
            value = nValue.getNodeValue();
        }
        return value;
    }
    
    public static String doPostHttpURLConnection(String urlwithQueryString) throws PZTechnicalViolationException
    {
        String response = "";
        PostMethod post = new PostMethod(urlwithQueryString);
        HttpClient httpClient = new HttpClient();
        try
        {
//            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            httpClient.executeMethod(post);
            response = new String(post.getResponseBody());
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("HDFCPaymentGatewayUtils.java", "doPostHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return response;
    }
    
}

package com.payment.unicredit;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import sun.misc.BASE64Decoder;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Admin on 12/29/2017.
 */
public class BOBS3DsecureImplementation
{
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.unicredit");

    private static TransactionLogger transactionlogger = new TransactionLogger(BOBS3DsecureImplementation.class.getName());

    private String transactionCodeStr;
    private String transactionTime;
    private String transactionAmountStr;
    private String terminalIDstr;
    private String orderID;
    private String orderDescription;
    private String protocolVersion;
    private String language;
    private String currency;
    //private boolean transactionStatusFlag = false;
    private boolean transactionStatusFlag = true;
    private boolean signatureFlag = false;
    private StringBuffer message;
    private String url;
    private String BOReq;

    String keyStore = RB.getString("Test_Merchant_JKS");
    String privateKey = RB.getString("Test_Merchant_PRIVATEKEY");
    String keyStore_password = RB.getString("TEST_JKS_PASSWORD");
    String privateKey_password = RB.getString("TEST_PRIVATEKEY_PASSWORD");

    String liveKeyStore = RB.getString("Live_Merchant_JKS");
    String livePrivateKey = RB.getString("Live_Merchant_PRIVATEKEY");
    String live_keyStore_password = RB.getString("LIVE_JKS_PASSWORD");
    String live_privateKey_password = RB.getString("LIVE_PRIVATEKEY_PASSWORD");

    private static void println(String s)
    {
        transactionlogger.debug(s);
    }

    public void setTransactionCode(String s)
    {
        transactionCodeStr = s;
    }

    public void setTransactionCode(int i)
    {
        transactionCodeStr = Integer.toString(i);

        if (transactionCodeStr.length() == 1)
        {
            transactionCodeStr+= "0";
        }
    }

    public void setTransactionAmount(float amount)
    {
        int i = (int) amount*100;
        //System.out.println("====1===="+i);
        setTransactionAmount(Integer.toString(i)) ;
    }

    public void setTransactionAmount(String s)
    {
        //System.out.println("===="+s);
        StringBuffer sb = new StringBuffer(s);
        if (sb.indexOf(".") != -1)
        {
            sb = sb.deleteCharAt(sb.indexOf("."));
        }
        transactionAmountStr = paddingString(sb.toString(), 12, '0', true);
    }

    public void setTerminalID(String s)
    {
        terminalIDstr = s;
    }

    public void setOrderID (String s)
    {
        orderID = paddingString(s, 15, ' ', false);
    }

    public void setOrderDescr(String s)
    {
        orderDescription = paddingString(s, 125, ' ', false);
    }

    public void setLanguage(String s)
    {
        language = s;
    }

    public void setCurrency(String s)
    {
        currency = s;
    }

    public void setProtocolVersion(String s)
    {
        protocolVersion = s;
    }

    public void setTransactionStatusFlag(boolean b)
    {
        transactionStatusFlag = b;
    }

    public void setBOR_URL(String s)
    {
        url = s;
    }

    public synchronized String paddingString(String s, int n, char c, boolean paddingLeft)
    {
        StringBuffer str = new StringBuffer(s);
        int strLength = str.length();
        if (n>0 && n>strLength)
        {
            for ( int i = 0; i <= n ; i ++ )
            {
                if (paddingLeft)
                {
                    if (i < n - strLength)
                        str.insert(0, c);
                }
                else
                {
                    if (i > strLength)
                        str.append(c);
                }
            }
        }
        return str.toString();
    }

    public String getBOReq() {
        return BOReq;
    }


    public void generateBOReq(Boolean isTest,String tranType)
    {
        //Base64.Encoder mimeEncoder = java.util.Base64.getMimeEncoder();
        int offset = 0;
        message = new StringBuffer();
        message.insert(offset, transactionCodeStr);
        offset += transactionCodeStr.length();
        transactionlogger.error("transaction code---" + transactionCodeStr.length() + "--" + transactionCodeStr);

        SimpleDateFormat date;
        date = new SimpleDateFormat("yyyyMMddHHmmss");
        transactionTime = date.format(new Date());
        message.insert(offset, date.format(new Date()));
        //message.insert(offset, "201802051706280");
        offset += transactionTime.length();
        transactionlogger.error("time---" + transactionTime.length() + "--" + transactionTime);

        message.insert(offset, transactionAmountStr);
        offset += transactionAmountStr.length();
        transactionlogger.error("transactionAmountStr---" + transactionAmountStr.length() + "--" + transactionAmountStr);

        message.insert(offset, terminalIDstr);
        offset += terminalIDstr.length();
        transactionlogger.error("terminalIDstr---" + terminalIDstr.length() + "--" + terminalIDstr);

        message.insert(offset, orderID);
        offset += orderID.length();
        transactionlogger.error("orderID---" + orderID.length() + orderID);

        message.insert(offset, orderDescription);
        offset += orderDescription.length();
        transactionlogger.error("orderDescription length---" + orderDescription.length() + "--" + orderDescription);

        message.insert(offset, language);
        offset += language.length();
        transactionlogger.error("language---" + language.length() + "--" + language);

        message.insert(offset, protocolVersion);
        offset += protocolVersion.length();
        transactionlogger.error("protocolVersion---" + protocolVersion.length() + "--" + protocolVersion);

        message.insert(offset, currency);
        offset += currency.length();
        transactionlogger.error("currency---" + currency.length() + "--" + currency);

        transactionlogger.error("message-1--" + message.toString());

        Signer signer = null;
        if(isTest)
        {
            signer = new Signer(keyStore,keyStore_password,privateKey,privateKey_password);
        }
        else
        {
            signer = new Signer(liveKeyStore,live_keyStore_password,livePrivateKey,live_privateKey_password);
        }

        //Signer signer = new Signer(keyStore,"changeit",privateKey,"changeit");

        byte [] sign = null;
        sign = signer.createSignature(message.toString().getBytes());

        String signature = new String(sign, StandardCharsets.ISO_8859_1);

        message.insert(offset, signature);

        String requestMessage = message.toString();

        String action = null;

        if (transactionCodeStr.equals("10") & tranType.equalsIgnoreCase("sale"))
        {
            action = "registerTransaction?eBorica=";
        }
        else
        {
            if (tranType.equalsIgnoreCase("inquiry"))
            {
                action = "transactionStatusReport?eBorica=";
            }
            else
            {
                action = "manageTransaction?eBorica=";
            }
        }

        BOReq = url;
        BOReq += action;

        String BASE64encodedMessage = "";

        BASE64encodedMessage = new sun.misc.BASE64Encoder().encode(requestMessage.getBytes(StandardCharsets.ISO_8859_1));
        BASE64encodedMessage=BASE64encodedMessage.replaceAll("\\r|\\n","");
        String URLencodedMessage = "";
        try
        {
            URLencodedMessage= URLEncoder.encode(BASE64encodedMessage, "windows-1252");
            transactionlogger.error("======URLencoded=======" + URLencodedMessage);
        }
        catch (UnsupportedEncodingException e)
        {
            transactionlogger.error("UnsupportedEncodingException---",e);
        }
        BOReq += URLencodedMessage;
        //verifyBOResp(URLencodedMessage,"EuroFootball_eBorica_LiveKey");
    }

    public static void main(String[] args)
    {
        try
        {
            /*String rMsg = "MTAyMDE4MDIwODE0NDk1MjAwMDAwMDAwMTIzNDYyMTYxMTkwVGVzdDEwMDkyNDQ4ICAgRGVzY3JpcHRpb24xMDEgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBFTjEuMGQTfMoHA75UUXxChE3a0cB4%2B07zTCVT66OV%2FHsAy3nh3dk93FefPKMwRoB5rCohqiHCG1%2BgboSSxSjTIgbj1RvfiyMKa3fqtFQMOCuz3T%2B8YVEX3k7av67iMzMUWsRva4wdTtqLmcy75waJxw%2FfzGo6V9rIZFUHhu2DjGODEOjc";
            String  Test_EuroFootball_eBorica_Test_0_f  = "";
            BOBS3DsecureImplementation bobs3DsecureImplementation = new BOBS3DsecureImplementation();
            bobs3DsecureImplementation.verifyBOResp(rMsg,"EuroFootball_eBorica_LiveKey");*/

            String rMsg = "MTAyMDE4MDIxNTIwNTUxMDAwMDAwMDAwNTAwMDYyMTYxMTkwNTQxNjAgICAgICAgICAgMDAxLjEEZmk4wClvu3tECFupUH5AscPyJavDxRvQvmIHcPgQk7jvPL26Hs9IDqec+6WFw8xMiAeKTz4oUNSHydk0kwhCjv9XorcT6XiNDeX4cQELGgN82kZQHPRn4wATEN/DOfFS0LmdIB9yQf55wPMMVh6VfS3A4jMxe/pWhc+C67Euaw==";
            BOBS3DsecureImplementation bobs3DsecureImplementation = new BOBS3DsecureImplementation();
            bobs3DsecureImplementation.verifyBOResp(rMsg,"EuroFootball_eBorica_LiveKey");
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }

    public boolean verifyBOResp(String BOResponseMessage,String publicKeyAPGW)
    {
        // manipulation of the $_GET["eBorica"] parameter

        try
        {
            String rMsg = BOResponseMessage;
            //Base64.Decoder mimeDecoder = java.util.Base64.getMimeDecoder();

            rMsg = URLDecoder.decode(rMsg, "windows-1252");
            //System.out.println("rMsg---"+rMsg);

            BASE64Decoder decoder = new BASE64Decoder();
            rMsg = new String(decoder.decodeBuffer(rMsg),StandardCharsets.ISO_8859_1);
            //System.out.println("requestMessage--2-"+rMsg);

            String transCode = rMsg.substring(0, 2);
            println("Response.TransactonCode = " + transCode);
            String transTime = rMsg.substring(2, 14);
            println("Response.TransactionTime = " + transTime);
            String amount = rMsg.substring(16, 28);
            println("Response.Amount = " + amount);
            String termID = rMsg.substring(28, 36);
            println("Response.TID = " + termID);
            String orderID = rMsg.substring(36, 51);
            println("Response.OredrID = " + orderID);
            String respCode = rMsg.substring(51, 53);
            println("Response.ResponseCode = " + respCode);
            String protVer = rMsg.substring(53, 56);
            println("Response.ProtocolVersion = " + protVer);

            String messageData = rMsg.substring(0, rMsg.length() - 128);

            /*System.out.println("messageData--3-length --"+messageData.length());
            System.out.println("messageData--3-"+messageData);*/

            String signatureOfmessage = rMsg.substring(rMsg.length() - 128);

            /*System.out.println("signatureOfmessage--3-length --"+signatureOfmessage.length());
            System.out.println("signatureOfmessage--3-"+signatureOfmessage);*/

            byte[] messageDataByte = messageData.getBytes();
            byte[] signatureOfmessageByte = signatureOfmessage.getBytes(StandardCharsets.ISO_8859_1);

            Signer signer= new Signer(liveKeyStore,"changeit",livePrivateKey,"changeit");
            signatureFlag = signer.verifySignature(messageDataByte, signatureOfmessageByte, publicKeyAPGW);

            if (signatureFlag)
            {
                println("Response.signatureValidation = TRUE");
            }
            else
            {
                println("Response.signatureValidation = FALSE");
            }
        }
        catch (Exception e)
        {
            signatureFlag = false;
            transactionlogger.error("Exception in verifysignature---",e);
        }
        return signatureFlag;
    }

}

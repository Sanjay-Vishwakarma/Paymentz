package com.payment.billdesk;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Admin on 8/9/2017.
 */
public class BillDeskUtils
{

    public final static String charset = "UTF-8";
    private static Logger log = new Logger(BillDeskUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BillDeskUtils.class.getName());
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
        URLConnection con = null;

        try {

          //  System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
           // java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                con = url.openConnection();
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
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
        } catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (UnsupportedEncodingException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally
        {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZTechnicalViolationException {
        LinkedList<BDBuf> bufList = new LinkedList<BDBuf>();
        int size = 0;
        byte buf[];
        String buffer = null;
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new BDBuf(buf, num));
            } while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<BDBuf> p = bufList.listIterator(); p.hasNext();)
            {
                BDBuf b = p.next();
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
            PZExceptionHandler.raiseTechnicalViolationException("QwipiUtills.java", "ReadByteStream()", null, "common", "Technical Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null, io.getMessage(), io.getCause());
        }
        return buffer;
    }

    public static String generateAutoSubmitForm(String actionUrl,String msgData)
    {
        transactionLogger.error("urll---" + actionUrl);
        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +actionUrl+ "\">" +
                "<input type=hidden name=msg id=msg value=\""+msgData+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";
        return form.toString();
    }

    public static String generateAutoSubmitForm(String actionUrl,String msgData,String paymentMethod)
    {
        transactionLogger.error("urll---" + actionUrl);

        String hiddenValue = "";
        if(paymentMethod.equalsIgnoreCase("UPI"))
            hiddenValue = "<input type='hidden' name='txtPayCategory' value='UPI'>";

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +actionUrl+ "\">" +
                "<input type=hidden name=msg id=msg value=\""+msgData+"\">"+
                hiddenValue+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        return form.toString();
    }
    Functions functions=new Functions();
    public CommRequestVO getBilldeskRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());

            //addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
            addressDetailsVO.setPhone("9999999999");
        transactionLogger.error("phone number----->"+addressDetailsVO.getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentMode());
        transactionLogger.error("paymentType is -----"+commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());


        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }

}
class BDBuf
{
    public byte buf[];
    public int size;

    public BDBuf(byte b[], int s)
    {
        buf = b;
        size = s;
    }
}
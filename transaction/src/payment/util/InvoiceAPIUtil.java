package payment.util;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/14
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceAPIUtil
{
    private static Logger log = new Logger(InvoiceAPIUtil.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InvoiceAPIUtil.class.getName());

    WriteXMLResponseInvoice WriteXMLResponse = new WriteXMLResponseInvoice();

    public String generateMD5ChecksumForInvoiceGenerate(String memberid, String amount, String orderid,String description, String emailaddr,String redirecturl, String key)
    {
       String generatedCheckSum=null;
       try
       {

           String str = memberid + "|" + amount + "|" + orderid + "|" + description + "|" + emailaddr + "|" + redirecturl+ "|" + key;

           MessageDigest messageDigest = MessageDigest.getInstance("MD5");

           generatedCheckSum = getString(messageDigest.digest(str.getBytes()));

       }
       catch (NoSuchAlgorithmException e)
       {

        log.error(e);
        transactionLogger.error(e);

       }

        return generatedCheckSum;

    }

    public String generateMD5ChecksumForInvoiceCancelRegenerateRemind(String memberid,String invoiceno,String redirecturl,String key) throws NoSuchAlgorithmException
    {
        String generatedCheckSum=null;

        try
        {

            String str = memberid + "|" + invoiceno + "|" + redirecturl + "|" + key;

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));


        }
        catch (NoSuchAlgorithmException e)
        {
            log.error(e);
            transactionLogger.error(e);
        }
        return generatedCheckSum;
    }

    private static String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);

        for (int i = 0; i < buf.length; i++)
        {

            int h = (buf[i] & 0xf0) >> 4;

            int l = (buf[i] & 0x0f);

            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));

            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));

        }

        return sb.toString();

    }

}

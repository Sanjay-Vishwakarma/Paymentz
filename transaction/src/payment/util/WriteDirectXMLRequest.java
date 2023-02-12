package payment.util;

import java.util.HashMap;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 4/28/14
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteDirectXMLRequest
{
    private static Logger log = new Logger(WriteDirectXMLRequest.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WriteDirectXMLRequest.class.getName());

    public static String createSalesRequest(HashMap<String, String> redeemMap )
    {
        StringBuffer salesXML = new StringBuffer();

        salesXML.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        salesXML.append("<Request action=\""+redeemMap.get("action")+"\" version=\""+redeemMap.get("version")+"\">");
        salesXML.append("<memberdetails>");
        salesXML.append("<toid>"+redeemMap.get("toid")+"</toid>");
        salesXML.append("<totype>"+redeemMap.get("totype")+"</totype>");
        salesXML.append("<redirecturl>"+redeemMap.get("redirecturl")+"</redirecturl>");
        salesXML.append("<checksum>"+redeemMap.get("checksum")+"</checksum>");
        salesXML.append("<ipaddr>"+redeemMap.get("ipaddr")+"</ipaddr>");
        salesXML.append("</memberdetails>");

        salesXML.append("<order>");
        salesXML.append("<description>"+redeemMap.get("description")+"</description>");
        salesXML.append("<orderdescription>"+redeemMap.get("orderdescription")+"</orderdescription>");
        salesXML.append("<amount>"+redeemMap.get("amount")+"</amount>");
        salesXML.append("</order>");

        salesXML.append("<customerdetails>");
        salesXML.append("<countrycode>"+redeemMap.get("countrycode")+"</countrycode>");
        salesXML.append("<city>"+redeemMap.get("city")+"</city>");
        salesXML.append("<state>"+redeemMap.get("state")+"</state>");
        salesXML.append("<zip>"+redeemMap.get("zip")+"</zip>");
        salesXML.append("<street>"+redeemMap.get("street")+"</street>");
        salesXML.append("<telnocc>"+redeemMap.get("telnocc")+"</telnocc>");
        salesXML.append("<telno>"+redeemMap.get("telno")+"</telno>");
        salesXML.append("<emailaddr>"+redeemMap.get("emailaddr")+"</emailaddr>");
        salesXML.append("<language>"+redeemMap.get("language")+"</language>");
        salesXML.append("</customerdetails>");

        salesXML.append("<carddetails>");
        salesXML.append("<cardnumber>"+redeemMap.get("cardnumber")+"</cardnumber>");
        salesXML.append("<cvv>"+redeemMap.get("cvv")+"</cvv>");
        salesXML.append("<expirymonth>"+redeemMap.get("expirymonth")+"</expirymonth>");
        salesXML.append("<expiryyear>"+redeemMap.get("expiryyear")+"</expiryyear>");
        salesXML.append("<cardholder_firstname>"+redeemMap.get("cardholder_firstname")+"</cardholder_firstname>");
        salesXML.append("<cardholder_firstname>"+redeemMap.get("cardholder_lastname")+"</cardholder_lastname>");
        salesXML.append("<cardholder_birthdate>"+redeemMap.get("cardholder_birthdate")+"</cardholder_birthdate>");
        salesXML.append("<cardholder_ssn>"+redeemMap.get("cardholder_ssn")+"</cardholder_ssn>");
        salesXML.append("</carddetails>");

        salesXML.append("<payment>");
        salesXML.append("<paymenttype>"+redeemMap.get("paymenttype")+"</paymenttype>");
        salesXML.append("<cardtype>"+redeemMap.get("cardtype")+"</cardtype>");
        salesXML.append("<terminalid>"+redeemMap.get("terminalid")+"</terminalid>");
        salesXML.append("</payment>");

        salesXML.append("</Request>");


        return salesXML.toString();
    }

    public static String createRefundRequest(HashMap<String, String> redeemMap )
    {

        StringBuffer refundXML = new StringBuffer();

        refundXML.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        refundXML.append("<Request action=\""+redeemMap.get("action")+"\" version=\""+redeemMap.get("version")+"\">");

        refundXML.append("<toid>"+redeemMap.get("toid")+"</toid>");
        refundXML.append("<trackingid>"+redeemMap.get("trackingid")+"</trackingid>");
        refundXML.append("<refundamount>"+redeemMap.get("refundamount")+"</refundamount>");
        refundXML.append("<reason>"+redeemMap.get("reason")+"</reason>");
        refundXML.append("<checksum>"+redeemMap.get("checksum")+"</checksum>");
        refundXML.append("<ipaddr>"+redeemMap.get("ipaddr")+"</ipaddr>");
        refundXML.append("</Request>");

        return refundXML.toString();
    }

    public static String createStatusRequest(HashMap<String, String> redeemMap )
    {

        StringBuffer statusXML = new StringBuffer();

        statusXML.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        statusXML.append("<Request action=\""+redeemMap.get("action")+"\" version=\""+redeemMap.get("version")+"\">");

        statusXML.append("<toid>"+redeemMap.get("toid")+"</toid>");
        statusXML.append("<trackingid>"+redeemMap.get("trackingid")+"</trackingid>");
        statusXML.append("<description>"+redeemMap.get("description")+"</description>");
        statusXML.append("<checksum>"+redeemMap.get("checksum")+"</checksum>");
        statusXML.append("<ipaddr>"+redeemMap.get("ipaddr")+"</ipaddr>");
        statusXML.append("</Request>");


        return statusXML.toString();
    }

    public static void main(String []a)
    {
        HashMap readXml = new HashMap();

        readXml.put("action","sales");
        readXml.put("version","2.0");
        readXml.put("toid","10103");
        readXml.put("totype","pz");
        readXml.put("redirecturl","www.pz.com");
        readXml.put("checksum","ddsdaf654SADF65sdf");
        readXml.put("ipaddr","127.0.0.1");

        readXml.put("description","success");
        readXml.put("orderdescription","sad5asd6");
        readXml.put("amount","30.00");

        readXml.put("countrycode","In");
        readXml.put("city","Mumbai");
        readXml.put("state","Mh");
        readXml.put("zip","640006");
        readXml.put("street","Malad");
        readXml.put("telnocc","091");
        readXml.put("telno","9874563210");
        readXml.put("emailaddr","abc@test.com");
        readXml.put("language","Eng");

        readXml.put("cardnumber","4444333322221111");
        readXml.put("cvv","123");
        readXml.put("expirymonth","05");
        readXml.put("expiryyear","2017");
        readXml.put("cardholder_firstname","John");
        readXml.put("cardholder_lastname","Deo");
        readXml.put("cardholder_birthdate","19802012");
        readXml.put("cardholder_ssn","1234");

        readXml.put("paymenttype","1");
        readXml.put("cardtype","1");
        readXml.put("terminalid","20");

        String doc2 = createSalesRequest(readXml);
        log.debug(doc2);
        transactionLogger.debug(doc2);

        readXml.put("action","refund");
        readXml.put("version","2.1");
        readXml.put("toid", "10103");
        readXml.put("trackingid", "1927");
        readXml.put("refundamount","20.00");
        readXml.put("reason","success");
        readXml.put("checksum", "sdfas6d5g4SDFASF");
        readXml.put("ipaddr", "127.0.0.1");

        String doc3 = createRefundRequest(readXml);
        log.debug(doc3);
        transactionLogger.debug(doc3);

        readXml.put("action","status");
        readXml.put("version","3.1");
        readXml.put("toid","10103");
        readXml.put("trackingid","1927");
        readXml.put("description","sda3sdf21");
        readXml.put("checksum","sdfas6d5g4SDFASF");
        readXml.put("ipaddr","127.0.0.1");

        String doc4 = createRefundRequest(readXml);
        log.debug(doc4);
        transactionLogger.debug(doc4);

    }


}

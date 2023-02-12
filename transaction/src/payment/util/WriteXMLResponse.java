package payment.util;

import com.directi.pg.Functions;
import com.directi.pg.Logger;

import java.util.HashMap;
import java.util.Map;
/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/8/14
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteXMLResponse
{
    private static Logger logger = new Logger(WriteXMLResponse.class.getName());

    public static String writeSaleResponse(HashMap<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();
        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        readXml.append("<response action=\"Sale\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<amount>"+resMap.get("amount")+"</amount>");
        readXml.append("<checksum>"+resMap.get("checksum")+"</checksum>");
        readXml.append("<billingdiscriptor>"+resMap.get("billingdiscriptor")+"</billingdiscriptor>");
        readXml.append("<token>"+resMap.get("token")+"</token>");
        readXml.append("<fraudscore>"+resMap.get("fraudscore")+"</fraudscore>");
        readXml.append("</response>");

        return readXml.toString();
    }
    public static String writeFullSaleResponse(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        WriteXMLResponse writeXMLResponse = new WriteXMLResponse();
        resMap = writeXMLResponse.checkNullResponse(resMap);

        /*String billingdescriptor = "";
        if(resMap.get("billingdescriptor")!=null)
        {
            billingdescriptor = resMap.get("billingdescriptor");
        }*/

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<response action=\"Sale\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<amount>"+resMap.get("amount")+"</amount>");
        readXml.append("<authcode>"+resMap.get("authcode")+"</authcode>");
        readXml.append("<resultcode>"+resMap.get("resultcode")+"</resultcode>");
        readXml.append("<resultdescription>"+resMap.get("resultdescription")+"</resultdescription>");
        readXml.append("<cardsource>"+resMap.get("cardsource")+"</cardsource>");
        readXml.append("<cardissuername>"+resMap.get("cardissuername")+"</cardissuername>");
        readXml.append("<eci>"+resMap.get("eci")+"</eci>");
        readXml.append("<cvvresult>"+resMap.get("cvvresult")+"</cvvresult>");
        readXml.append("<ecidescription>"+resMap.get("ecidescription")+"</ecidescription>");
        readXml.append("<banktransid>"+resMap.get("banktransid")+"</banktransid>");
        readXml.append("<banktransdate>"+resMap.get("banktransdate")+"</banktransdate>");
        readXml.append("<billingdescriptor>"+resMap.get("billingdiscriptor")+"</billingdescriptor>");
        readXml.append("<validationdescription>"+resMap.get("validationdescription")+"</validationdescription>");
        readXml.append("<cardcountrycode>"+resMap.get("cardcountrycode")+"</cardcountrycode>");
        readXml.append("<checksum>"+resMap.get("checksum")+"</checksum>");
        readXml.append("<token>"+resMap.get("token")+"</token>");
        readXml.append("<fraudscore>"+resMap.get("fraudscore")+"</fraudscore>");
        readXml.append("</response>");

        return readXml.toString();
    }
    public static String writeResponseForRefund(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        readXml.append("<response action=\"refund\" version=\"5.0\">");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<refundamount>"+resMap.get("amount")+"</refundamount>");
        readXml.append("<newchecksum>"+resMap.get("checkSum")+"</newchecksum>");
        readXml.append("</response>");

        return readXml.toString();
    }

    public static String writeStatusResponse(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<response action=\"status\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<authamount>"+resMap.get("authamount")+"</authamount>");
        readXml.append("<captureamount>"+resMap.get("captureamount")+"</captureamount>");
        readXml.append("<newchecksum>"+resMap.get("newchecksum")+"</newchecksum>");
        readXml.append("</response>");

        return readXml.toString();
    }

    public static String writeVoidResponse(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<response action=\"status\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<newchecksum>"+resMap.get("newchecksum")+"</newchecksum>");
        readXml.append("</response>");

        return readXml.toString();
    }

    public static String writeFullResponseForVoid(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        WriteXMLResponse writeXMLResponse = new WriteXMLResponse();
        resMap = writeXMLResponse.checkNullResponse(resMap);

        String authamount ="";
        String captureamount="";
        if(resMap.get("authamount")!=null)
        {
            authamount = resMap.get("authamount");
        }

        if(resMap.get("captureamount")!=null)
        {
            captureamount = resMap.get("captureamount");
        }

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<response action=\"status\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<authamount>"+authamount+"</authamount>");
        readXml.append("<captureamount>"+captureamount+"</captureamount>");
        readXml.append("<newchecksum>"+resMap.get("newchecksum")+"</newchecksum>");
        readXml.append("<bankstatus>"+resMap.get("bankstatus")+"</bankstatus>");
        readXml.append("<resultcode>"+resMap.get("resultcode")+"</resultcode>");
        readXml.append("<resultdescription>"+resMap.get("resultdescription")+"</resultdescription>");
        readXml.append("</response>");

        return readXml.toString();
    }
    public static String writeFullStatusResponse(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();
        WriteXMLResponse writeXMLResponse = new WriteXMLResponse();
        resMap = writeXMLResponse.checkNullResponse(resMap);

        String banktransid = "";
        if(resMap.get("banktransid")!=null)
        {
            banktransid = resMap.get("banktransid");
        }
        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        readXml.append("<response action=\"status\" version=\"5.0\">");
        readXml.append("<orderid>"+resMap.get("orderid")+"</orderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<authamount>"+resMap.get("authamount")+"</authamount>");
        readXml.append("<captureamount>"+resMap.get("captureamount")+"</captureamount>");
        readXml.append("<refundAmount>"+resMap.get("refundAmount")+"</refundAmount>");
        readXml.append("<newchecksum>"+resMap.get("newchecksum")+"</newchecksum>");
        readXml.append("<authcode>"+resMap.get("authcode")+"</authcode>");
        readXml.append("<resultcode>"+resMap.get("resultcode")+"</resultcode>");
        readXml.append("<resultdescription>" + resMap.get("resultdescription") + "</resultdescription>");
        readXml.append("<cardsource>"+resMap.get("cardsource")+"</cardsource>");
        readXml.append("<cardissuer>"+resMap.get("cardissuer")+"</cardissuer>");
        readXml.append("<eci>"+resMap.get("eci")+"</eci>");
        readXml.append("<ecidescription>"+resMap.get("ecidescription")+"</ecidescription>");
        readXml.append("<cvvresult>"+resMap.get("cvvresult")+"</cvvresult>");
        readXml.append("<banktransid>"+banktransid+"</banktransid>");
        readXml.append("<cardcountrycode>"+resMap.get("cardcountrycode")+"</cardcountrycode>");
        readXml.append("<validationdescription>"+resMap.get("validationdescription")+"</validationdescription>");
        readXml.append("<banktransdate>"+resMap.get("banktransdate")+"</banktransdate>");
        readXml.append("</response>");
        return readXml.toString();
    }


    public static String writeResponseForCapture(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        readXml.append("<response action=\"capture\" version=\"5.0\">");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<captureamount>"+resMap.get("captureamount")+"</captureamount>");
        readXml.append("<newchecksum>"+resMap.get("checkSum")+"</newchecksum>");
        readXml.append("</response>");

        return readXml.toString();
    }
    public static String writeFullResponseForCapture(Map<String,String> resMap)
    {
        StringBuffer readXml = new StringBuffer();

        WriteXMLResponse writeXMLResponse = new WriteXMLResponse();
        resMap = writeXMLResponse.checkNullResponse(resMap);

        String general1 = "";
        if(resMap.get("general1")!=null)
        {
            general1 = resMap.get("general1");
        }

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        readXml.append("<response action=\"capture\" version=\"5.0\">");
        readXml.append("<trackingid>"+resMap.get("trackingid")+"</trackingid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<captureamount>"+resMap.get("captureamount")+"</captureamount>");
        readXml.append("<newchecksum>"+resMap.get("checkSum")+"</newchecksum>");
        readXml.append("<bankstatus>"+resMap.get("bankstatus")+"</bankstatus>");
        readXml.append("<resultcode>"+resMap.get("resultcode")+"</resultcode>");
        readXml.append("<resultdescription>"+resMap.get("resultdescription")+"</resultdescription>");
        readXml.append("<general1>"+general1+"</general1>");
        readXml.append("</response>");
        return readXml.toString();
    }
    public static String writeXMLResponseForFetchCards(Map<String,String> resMap,String customerCards)
    {
        StringBuffer readXml= new StringBuffer();

        WriteXMLResponse writeXMLResponse = new WriteXMLResponse();
        resMap = writeXMLResponse.checkNullResponse(resMap);

        String partnerId = "";
        String cardHolderId = "";
        if(resMap.get("partnerid") != null)
            partnerId = resMap.get("partnerid");
        if(resMap.get("cardholderid") != null)
            cardHolderId = resMap.get("cardholderid");

        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
        readXml.append("<response action=\"fetchcards\" version=\"5.0\">");
        readXml.append("<partnerid>"+partnerId+"</partnerid>");
        readXml.append("<toid>"+resMap.get("toid")+"</toid>");
        readXml.append("<cardholderid>"+cardHolderId+"</cardholderid>");
        readXml.append("<status>"+resMap.get("status")+"</status>");
        readXml.append("<statusdescription>"+resMap.get("statusdescription")+"</statusdescription>");
        readXml.append("<checksum>"+resMap.get("checksum")+"</checksum>");
        readXml.append("<customercards>");
        readXml.append(customerCards);
        readXml.append("</customercards>");
        readXml.append("</response>");
        return readXml.toString();
    }
    public static void main(String []a)
    {
        HashMap readXml = new HashMap();

        readXml.put("action", "sales");
        readXml.put("orderid", "1111");
        readXml.put("status", "success");
        readXml.put("statusdescription", "dsdf876");
        readXml.put("trackingid", "1927");
        readXml.put("amount", "20.00");
        readXml.put("newchecksum", "sadfsdf654WEQR");

        String doc2 = writeSaleResponse(readXml);
        //System.out.println(doc2);

    }

    public static String writeXMLResponse(Map<String,String> resMap, String action)
    {
        StringBuffer readXml= new StringBuffer();
        readXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        readXml.append("<response action=\"" +action+ "\" version=\"5.0\">");
        for(String key : resMap.keySet())
        {
            readXml.append("<"+key+">" +resMap.get(key)+ "</" +key+ ">");
        }
        readXml.append("</response>");
        return readXml.toString();
    }

    public static String writeInnerXMLResponse(Map<String,String> resMap, String heading)
    {
        StringBuffer readXml= new StringBuffer();
        readXml.append("<" +heading+ ">");
        if(!resMap.isEmpty())
        {
            for (String key : resMap.keySet())
            {
                readXml.append("<" + key + ">" + resMap.get(key) + "</" + key + ">");
            }
        }
        else
            readXml.append("No Records Found");
        readXml.append("</" +heading+ ">");
        return readXml.toString();
    }

    public Map checkNullResponse(Map resMap)
    {
        String value = "";
        Functions f = new Functions();
        for(Object key : resMap.keySet())
        {
            logger.debug("value---" + key + "--" + resMap.get(key));
            if(f.isValueNull(String.valueOf(resMap.get(key))))
            {
                value = resMap.get(key).toString();
            }
            else
            {
                value = "";
            }
            resMap.put(key.toString(),value);
            logger.debug("value after---" + key + "--" + value);
        }
        return resMap;
    }
}

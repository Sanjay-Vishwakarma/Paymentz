package payment.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteXMLResponseInvoice
{


    public String writeResponseForGenerate(Hashtable hashtable)
    {

        StringBuffer responseXmlForGenerate=new StringBuffer();

        responseXmlForGenerate.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        responseXmlForGenerate.append("<invoice action=\"generate\" type=\"response\" version=\"1.0\">");

        responseXmlForGenerate.append("<memberid>"+hashtable.get("memberid")+"</memberid>");

        responseXmlForGenerate.append("<invoiceno>"+hashtable.get("invoiceno")+"</invoiceno>");

        responseXmlForGenerate.append("<amount>"+hashtable.get("amount")+"</amount>");

        responseXmlForGenerate.append("<orderid>"+hashtable.get("orderid")+"</orderid>");

        responseXmlForGenerate.append("<orderdesciption>"+hashtable.get("orderdesc")+"</orderdesciption>");

        responseXmlForGenerate.append("<emailaddr>"+hashtable.get("custemail")+"</emailaddr>");

        responseXmlForGenerate.append("<currency>"+hashtable.get("currency")+"</currency>");

        responseXmlForGenerate.append("<status>"+hashtable.get("status")+"</status>");

        responseXmlForGenerate.append("<date>"+hashtable.get("date")+"</date>");

        responseXmlForGenerate.append("<time>"+hashtable.get("time")+"</time>");

        responseXmlForGenerate.append("<error>"+hashtable.get("error")+"</error>");

        responseXmlForGenerate.append("</invoice>");

        return responseXmlForGenerate.toString();

    }

    public String writeResponseForCancel(Hashtable hashtable)
    {
        StringBuffer responseXmlForCancel=new StringBuffer();

        responseXmlForCancel.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        responseXmlForCancel.append("<invoice action=\"cancel\" type=\"response\" version=\"1.0\">");

        responseXmlForCancel.append("<memberid>"+hashtable.get("memberid")+"</memberid>");

        responseXmlForCancel.append("<invoiceno>"+hashtable.get("invoiceno")+"</invoiceno>");

        responseXmlForCancel.append("<status>"+hashtable.get("status")+"</status>");

        responseXmlForCancel.append("<emailaddr>"+hashtable.get("custemail")+"</emailaddr>");

        responseXmlForCancel.append("<error>"+hashtable.get("error")+"</error>");

        responseXmlForCancel.append("</invoice>");

        return responseXmlForCancel.toString();

    }

    public String writeResponseForRegenerate(Hashtable hashtable)
    {

        StringBuffer responseXmlForRegenarate=new StringBuffer();

        responseXmlForRegenarate.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        responseXmlForRegenarate.append("<invoice action=\"regenerate\" type=\"response\" version=\"1.0\">");

        responseXmlForRegenarate.append("<memberid>"+hashtable.get("memberid")+"</memberid>");

        responseXmlForRegenarate.append("<invoiceno>"+hashtable.get("invoiceno")+"</invoiceno>");

        responseXmlForRegenarate.append("<status>"+hashtable.get("status")+"</status>");

        responseXmlForRegenarate.append("<emailaddr>"+hashtable.get("custemail")+"</emailaddr>");

        responseXmlForRegenarate.append("<error>"+hashtable.get("error")+"</error>");

        responseXmlForRegenarate.append("</invoice>");

        return responseXmlForRegenarate.toString();

    }

    public String writeResponseForRemind(Hashtable hashtable)
    {

        StringBuffer responseXmlForRemind=new StringBuffer();

        responseXmlForRemind.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        responseXmlForRemind.append("<invoice action=\"remind\" type=\"response\" version=\"1.0\">");

        responseXmlForRemind.append("<memberid>"+hashtable.get("memberid")+"</memberid>");

        responseXmlForRemind.append("<invoiceno>"+hashtable.get("invoiceno")+"</invoiceno>");

        responseXmlForRemind.append("<status>"+hashtable.get("status")+"</status>");

        responseXmlForRemind.append("<emailaddr>"+hashtable.get("custemail")+"</emailaddr>");

        responseXmlForRemind.append("<error>"+hashtable.get("error")+"</error>");

        responseXmlForRemind.append("</invoice>");

        return responseXmlForRemind.toString();

    }

    public String writeResponseForCommonError(String action,String memberid,String invoiceno,String status)
    {

        StringBuffer requestXml=new StringBuffer();

        requestXml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        requestXml.append("<invoice action=\""+action+"\" type=\"response\" version=\"1.0\">");

        requestXml.append("<memberid>"+memberid+"</memberid>");

        requestXml.append("<invoiceno>"+invoiceno+"</invoiceno>");

        requestXml.append("<status>Failed</status>");

        requestXml.append("<error>"+status+"</error>");

        requestXml.append("</invoice>");

        return requestXml.toString();

    }




}

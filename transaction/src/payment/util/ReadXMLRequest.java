package payment.util;

import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectKitValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 5/8/14
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadXMLRequest
{
    private static Logger logger = new Logger(ReadXMLRequest.class.getName());

    public static DirectKitValidatorVO readXmlRequestForSale(String data)
    {
        DirectKitValidatorVO directKitValidatorVO=new DirectKitValidatorVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO=new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        Document doc = null;

        doc =  createDocumentFromString(data);

        NodeList nList = doc.getElementsByTagName("request");
        String version = getAttributeValue((Element) nList.item(0),"version");
        String action = getAttributeValue((Element) nList.item(0),"action");

        if(action!=null && !action.equals(""))
        {
            directKitValidatorVO.setAction("Sale");
            directKitValidatorVO.setVersion(version);

            if(action.equalsIgnoreCase("Sale"))
            {
                //memberdetails
                nList = doc.getElementsByTagName("memberdetails");
                merchantDetailsVO.setMemberId(getTagValue("toid",(Element) nList.item(0)));
                genericTransDetailsVO.setTotype(getTagValue("totype",(Element) nList.item(0)));
                genericTransDetailsVO.setRedirectUrl(getTagValue("redirecturl",(Element) nList.item(0)));
                genericTransDetailsVO.setChecksum(getTagValue("checksum",(Element) nList.item(0)));

                //order
                nList = doc.getElementsByTagName("order");
                genericTransDetailsVO.setAmount(getTagValue("amount",(Element) nList.item(0)));
                genericTransDetailsVO.setOrderDesc(getTagValue("orderdescription",(Element) nList.item(0)));
                genericTransDetailsVO.setOrderId(getTagValue("description",(Element) nList.item(0)));

                //customerdetails
                nList = doc.getElementsByTagName("customerdetails");
                genericAddressDetailsVO.setCountry(getTagValue("countrycode",(Element) nList.item(0)));
                genericAddressDetailsVO.setCity(getTagValue("city",(Element) nList.item(0)));
                genericAddressDetailsVO.setState(getTagValue("state",(Element) nList.item(0)));
                genericAddressDetailsVO.setZipCode(getTagValue("zip",(Element) nList.item(0)));
                genericAddressDetailsVO.setStreet(getTagValue("street",(Element) nList.item(0)));
                genericAddressDetailsVO.setPhone(getTagValue("telno",(Element) nList.item(0)));
                genericAddressDetailsVO.setEmail(getTagValue("emailaddr",(Element) nList.item(0)));
                genericAddressDetailsVO.setLanguage(getTagValue("language",(Element) nList.item(0)));
                genericAddressDetailsVO.setFirstname(getTagValue("firstname",(Element) nList.item(0)));
                genericAddressDetailsVO.setLastname(getTagValue("lastname",(Element) nList.item(0)));
                genericAddressDetailsVO.setBirthdate(getTagValue("birthdate",(Element) nList.item(0)));
                genericAddressDetailsVO.setSsn(getTagValue("ssn",(Element) nList.item(0)));
                genericAddressDetailsVO.setTelnocc(getTagValue("telnocc",(Element) nList.item(0)));
                genericAddressDetailsVO.setCardHolderIpAddress(getTagValue("cardholderipaddress", (Element) nList.item(0)));


                //carddetails
                nList = doc.getElementsByTagName("carddetails");
                genericCardDetailsVO.setCardNum(getTagValue("cardnumber",(Element) nList.item(0)));
                genericCardDetailsVO.setcVV(getTagValue("cvv",(Element) nList.item(0)));
                genericCardDetailsVO.setExpMonth(getTagValue("expirymonth",(Element) nList.item(0)));
                genericCardDetailsVO.setExpYear(getTagValue("expiryyear",(Element) nList.item(0)));
                genericCardDetailsVO.setCardHolderName(getTagValue("firstname",(Element) nList.item(0))+" "+getTagValue("lastname",(Element) nList.item(0)) );


                //carddetails
                nList = doc.getElementsByTagName("payment");
                directKitValidatorVO.setPaymentType(getTagValue("paymenttype",(Element) nList.item(0)));
                directKitValidatorVO.setCardType(getTagValue("cardtype",(Element) nList.item(0)));
                directKitValidatorVO.setTerminalId(getTagValue("terminalid",(Element) nList.item(0)));
                directKitValidatorVO.setCreateRegistration(getTagValue("createregistration",(Element) nList.item(0)));

                directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            }
        }


        return directKitValidatorVO;
    }

    public static DirectRefundValidatorVO readXmlRequestForRefund(String data)
    {
        DirectRefundValidatorVO directRefundValidatorVO = new DirectRefundValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        Document doc = null;

        doc =  createDocumentFromString(data);

        NodeList nList = doc.getElementsByTagName("request");
        String action = getAttributeValue((Element) nList.item(0),"action");
        String version = getAttributeValue((Element) nList.item(0),"version");

        if(action!=null && !action.equals(""))
        {
            directRefundValidatorVO.setActionType("refund");
            directRefundValidatorVO.setVersion(version);
            if(action.equalsIgnoreCase("refund"))
            {
                nList = doc.getElementsByTagName("request");

                merchantDetailsVO.setMemberId(getTagValue("toid",(Element) nList.item(0)));
                directRefundValidatorVO.setTrackingid(getTagValue("trackingid",(Element) nList.item(0)));
                directRefundValidatorVO.setRefundAmount(getTagValue("refundamount",(Element) nList.item(0)));
                directRefundValidatorVO.setRefundReason(getTagValue("reason",(Element) nList.item(0)));
                transDetailsVO.setChecksum(getTagValue("checksum",(Element) nList.item(0)));
                directRefundValidatorVO.setCheckSum(getTagValue("checksum",(Element) nList.item(0)));
            }
        }

        directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        directRefundValidatorVO.setTransDetailsVO(transDetailsVO);
        return directRefundValidatorVO;
    }

    public static  CommonValidatorVO readXmlRequestForStatus(String data)
    {
        //Hashtable requestMap = new Hashtable();

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        Document doc = null;

        doc =  createDocumentFromString(data);

        NodeList nList = doc.getElementsByTagName("request");
        String action = getAttributeValue((Element) nList.item(0),"action");
        String version = getAttributeValue((Element) nList.item(0),"version");

        if(action!=null && !action.equals(""))
        {
            commonValidatorVO.setActionType("status");
            commonValidatorVO.setVersion(version);
            if(action.equalsIgnoreCase("status"))
            {
                merchantDetailsVO.setMemberId(getTagValue("toid",(Element) nList.item(0)));
                commonValidatorVO.setTrackingid(getTagValue("trackingid",(Element) nList.item(0)));
                transDetailsVO.setOrderId(getTagValue("description",(Element) nList.item(0)));
                transDetailsVO.setChecksum(getTagValue("checksum",(Element) nList.item(0)));

            }
        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

            return commonValidatorVO;
    }

    public static CommonValidatorVO readXmlRequestForCapture(String data)
    {
        //Hashtable requestMap = new Hashtable();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        Document doc = null;
        doc =  createDocumentFromString(data);
        NodeList nList = doc.getElementsByTagName("request");

        String action = getAttributeValue((Element) nList.item(0),"action");
        String version = getAttributeValue((Element) nList.item(0),"version");

        if(action!=null && !action.equals(""))
        {
            commonValidatorVO.setActionType("capture");
            commonValidatorVO.setVersion(version);

            if(action.equalsIgnoreCase("capture"))
            {
                nList = doc.getElementsByTagName("request");

                merchantDetailsVO.setMemberId(getTagValue("toid", (Element) nList.item(0)));
                commonValidatorVO.setTrackingid(getTagValue("trackingid", (Element) nList.item(0)));
                transDetailsVO.setAmount(getTagValue("captureamount",(Element) nList.item(0)));
                transDetailsVO.setChecksum(getTagValue("checksum",(Element) nList.item(0)));
            }

        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
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

    private static String getAttributeValue(Element eElement, String sTag)
    {
        String value  ="";

        if(eElement!=null)
        {
            value =	eElement.getAttribute(sTag);
        }

        return value;
    }


    public static Document createDocumentFromString(String xmlString ) {

        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce) {
            logger.error("ParserConfigurationException In ReadXMLRequest :::::::",pce);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            logger.error("Exception In ReadXMLRequest :::::::", e);
        }
        return doc;
    }

    public static void main(String[] a)
    {


        StringBuffer request = new StringBuffer();

        /*request.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        request.append("<Request action=\"Sale\" version=\"2.0\">");

        request.append("<memberdetails>");
        request.append("<toid>10103</toid>");
        request.append("<totype>pz</totype>");
        request.append("<redirecturl>www.paymntz.com</redirecturl>");
        request.append("<checksum>KJGhfhgf54564ffdgfd</checksum>");
        request.append("<ipaddr>127.0.0.1</ipaddr>");
        request.append("</memberdetails>");

        request.append("<order>");
        request.append("<description>success</description>");
        request.append("<orderdescription>dsf54df</orderdescription>");
        request.append("<amount>20.00</amount>");
        request.append("</order>");

        request.append("<customerdetails>");
        request.append("<countrycode>IN</countrycode>");
        request.append("<city>Mumbai</city>");
        request.append("<state>Mh</state>");
        request.append("<zip>400064</zip>");
        request.append("<street>Malad</street>");
        request.append("<telnocc>091</telnocc>");
        request.append("<telno>9632587410</telno>");
        request.append("<emailaddr>abc@test.com</emailaddr>");
        request.append("<language>Eng</language>");
        request.append("</customerdetails>");

        request.append("<carddetails>");
        request.append("<cardnumber>4444333322221111</cardnumber>");
        request.append("<cvv>123</cvv>");
        request.append("<expirymonth>06</expirymonth>");
        request.append("<expiryyear>2017</expiryyear>");
        request.append("<firstname>Jhon</firstname>");
        request.append("<lastname>Deo</lastname>");
        request.append("<birthdate>19902012</birthdate>");
        request.append("<ssn>1234</ssn>");
        request.append("</carddetails>");

        request.append("<payment>");
        request.append("<paymenttype>1</paymenttype>");
        request.append("<cardtype>1</cardtype>");
        request.append("<terminalid>20</terminalid>");
        request.append("</payment>");

        request.append("</Request>");*/

        request.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");

        request.append("<Request action=\"Status\" version=\"2.0\">");
        request.append("<toid>10103</toid>");
        request.append("<trackingid>pz</trackingid>");
        request.append("<description>www.paymntz.com</description>");
        request.append("<checksum>KJGhfhgf54564ffdgfd</checksum>");
        request.append("<ipaddr>127.0.0.1</ipaddr>");

        request.append("</Request>");
        Document doc =null;



       /* try{
            doc = createDocumentFromString(response.toString());



            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File("C:\\file.xml"));

            // Output to console for testing
            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        /*Map<String, String> requestMap =  readXmlRequestForStatus(request.toString());

        Set<String> keys = requestMap.keySet();
        System.out.println("  ");
        for(String key : keys)
        {
            System.out.println("  "+key+"="+requestMap.get(key));
        }*/

    }



}

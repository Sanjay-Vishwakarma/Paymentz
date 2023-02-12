package com.directi.pg.core.paylineVoucher;


import java.io.File;
import java.io.StringWriter;
import java.io.StringReader;

import static com.directi.pg.core.paylineVoucher.Elements.*;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import com.directi.pg.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Aug 29, 2012
 * Time: 12:10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class WriteXMLRequest
{

     private static Logger log = new Logger(WriteXMLRequest.class.getName());

	public static Document createRedeemRequest(Map<String, String> redeemMap ) {

       Document doc = null;

	  try {

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

          // root elements
          doc = docBuilder.newDocument();
          //doc.setXmlVersion("1.0");
          Element rootElement = doc.createElement(ELEM_REQUEST);
          doc.appendChild(rootElement);

          // set attribute to request element
          Attr attr = doc.createAttribute(ATTR_VERSION);
          attr.setValue(redeemMap.get(ATTR_VERSION));
          rootElement.setAttributeNode(attr);


          // Header elements
          Element header = doc.createElement(ELEM_HEADER);
          rootElement.appendChild(header);

          // Security element of Header
          Element security = doc.createElement(ELEM_SECURITY);
          header.appendChild(security);

          // set attribute to Security element
          security.setAttribute(ATTR_SENDER,redeemMap.get(ATTR_SENDER));
          //security.setAttribute(ATTR_TOKEN,redeemMap.get(ATTR_TOKEN));


          // Transaction element
          Element transaction = doc.createElement(ELEM_TRANSACTION);
          rootElement.appendChild(transaction);

          // set attribute to Transaction element
          transaction.setAttribute(ATTR_MODE,redeemMap.get(ATTR_MODE));
          transaction.setAttribute(ATTR_CHANNEL,redeemMap.get(ATTR_CHANNEL));

          // Identification element of Transaction
          Element identification = doc.createElement(ELEM_IDENTIFICATION);
          transaction.appendChild(identification);

          // TransactionID element of Identification
          Element transactionID = doc.createElement(ELEM_TRANSACTIONID);
          transactionID.appendChild(doc.createTextNode(redeemMap.get(ELEM_TRANSACTIONID)));
          identification.appendChild(transactionID);

            // User element
            Element user = doc.createElement(ELEM_USER);
            transaction.appendChild(user);

            // set attribute to User element
            user.setAttribute(ATTR_LOGIN,redeemMap.get(ATTR_LOGIN));
            user.setAttribute(ATTR_PWD,redeemMap.get(ATTR_PWD));

            // Payment element
            Element payment = doc.createElement(ELEM_PAYMENT);
            transaction.appendChild(payment);

            // set attribute to Payment element
            payment.setAttribute(ATTR_CODE,redeemMap.get(ATTR_PAYMENTCODE));

            // Presentation element  of Payment
            Element presentation = doc.createElement(ELEM_PRESENTATION);
            payment.appendChild(presentation);

            // Amount element of Presentation
            Element amount = doc.createElement(ELEM_AMOUNT);
            amount.appendChild(doc.createTextNode(redeemMap.get(ELEM_AMOUNT)));
            presentation.appendChild(amount);

            // Currency element of Presentation
            Element currency = doc.createElement(ELEM_CURRENCY);
            currency.appendChild(doc.createTextNode(redeemMap.get(ELEM_CURRENCY)));
            presentation.appendChild(currency);


            // Account element  of Transaction
            Element account = doc.createElement(ELEM_ACCOUNT);
            transaction.appendChild(account);

            // Id element of Account
            Element id = doc.createElement(ELEM_ID);
            id.appendChild(doc.createTextNode(redeemMap.get(ELEM_ID)));
            account.appendChild(id);

            // Brand element of Account
            Element brand = doc.createElement(ELEM_BRAND);
            brand.appendChild(doc.createTextNode(redeemMap.get(ELEM_BRAND)));
            account.appendChild(brand);

            // Customer element  of Transaction
            Element customer = doc.createElement(ELEM_CUSTOMER);
            transaction.appendChild(customer);

            // Name element  of Customer
            Element name = doc.createElement(ELEM_NAME);
            customer.appendChild(name);

            // Family element of Name
            Element family = doc.createElement(ELEM_FAMILY);
            family.appendChild(doc.createTextNode(redeemMap.get(ELEM_FAMILY)));
            name.appendChild(family);

            // Given element of Name
            Element given = doc.createElement(ELEM_GIVEN);
            given.appendChild(doc.createTextNode(redeemMap.get(ELEM_GIVEN)));
            name.appendChild(given);

            // Company element of Name
            Element company = doc.createElement(ELEM_COMPANY);
            company.appendChild(doc.createTextNode(redeemMap.get(ELEM_COMPANY)));
            name.appendChild(company);

           // Salutation element of Name
            Element salutation = doc.createElement(ELEM_SALUTATION);
            salutation.appendChild(doc.createTextNode(redeemMap.get(ELEM_SALUTATION)));
            name.appendChild(salutation);

            // Title element of Name
            Element title = doc.createElement(ELEM_TITLE);
            title.appendChild(doc.createTextNode(redeemMap.get(ELEM_TITLE)));
            name.appendChild(title);


            // Contact element  of Customer
            Element contact = doc.createElement(ELEM_CONTECT);
            customer.appendChild(contact);

            // Email element of Contact
            Element email = doc.createElement(ELEM_EMAIL);
            email.appendChild(doc.createTextNode(redeemMap.get(ELEM_EMAIL)));
            contact.appendChild(email);

            // Ip element of Contact
            Element ip = doc.createElement(ELEM_IP);
            ip.appendChild(doc.createTextNode(redeemMap.get(ELEM_IP)));
            contact.appendChild(ip);

            // Mobile element of Contact
            Element mobile = doc.createElement(ELEM_MOBILE);
            mobile.appendChild(doc.createTextNode(redeemMap.get(ELEM_MOBILE)));
            contact.appendChild(mobile);

            // Address element  of Customer
            Element address = doc.createElement(ELEM_ADDRESS);
            customer.appendChild(address);

            // City element of Address
            Element city = doc.createElement(ELEM_CITY);
            city.appendChild(doc.createTextNode(redeemMap.get(ELEM_CITY)));
            address.appendChild(city);

            // Country element of Address
            Element country = doc.createElement(ELEM_COUNTRY);
            country.appendChild(doc.createTextNode(redeemMap.get(ELEM_COUNTRY)));
            address.appendChild(country);

            // State element of Address
            Element state = doc.createElement(ELEM_STATE);
            state.appendChild(doc.createTextNode(redeemMap.get(ELEM_STATE)));
            address.appendChild(state);

            // Street element of Address
            Element street = doc.createElement(ELEM_STREET);
            street.appendChild(doc.createTextNode(redeemMap.get(ELEM_STREET)));
            address.appendChild(street);

            // Zip element of Address
            Element zip = doc.createElement(ELEM_ZIP);
            zip.appendChild(doc.createTextNode(redeemMap.get(ELEM_ZIP)));
            address.appendChild(zip);





	  } catch (ParserConfigurationException pce) {

          log.error("===========inside WriteXMLReqauest  1==",pce);
	  }  catch (Exception e)
      {
          log.error("===========inside WriteXMLReqauest  2==",e);
      }

        log.debug("==666=="+doc);
        //doc.getDocumentElement().normalize();
        return doc;
	}


    /**
     * 
     * @param redeemMap
     * @return
     */
      public static String createRedeemReq(Map<String, String> redeemMap ) {

        StringBuffer authXML = new StringBuffer();
        authXML.append("<Request version=\""+redeemMap.get(ATTR_VERSION)+"\">");
        authXML.append("<Header>");
        authXML.append("<Security sender=\""+redeemMap.get(ATTR_SENDER)+"\" token=\"payon\" />");
        authXML.append("</Header>");
        authXML.append("<Transaction mode=\""+redeemMap.get(ATTR_MODE)+"\" channel=\""+redeemMap.get(ATTR_CHANNEL)+"\">");
        authXML.append("<Identification>");
        authXML.append("<TransactionID>"+redeemMap.get(ELEM_TRANSACTIONID)+"</TransactionID>");
        authXML.append("</Identification>");
        authXML.append("<User login=\""+redeemMap.get(ATTR_LOGIN)+"\" pwd=\""+redeemMap.get(ATTR_PWD)+"\" />");
        authXML.append("<Payment code=\""+redeemMap.get(ATTR_PAYMENTCODE)+"\">");
        authXML.append("<Presentation>");
        authXML.append("<Amount>"+redeemMap.get(ELEM_AMOUNT)+"</Amount>");
        authXML.append("<Currency>"+redeemMap.get(ELEM_CURRENCY)+"</Currency>");
        authXML.append("</Presentation>");
        authXML.append("</Payment>");
        authXML.append("<Account>");
        authXML.append("<Id>"+redeemMap.get(ELEM_ID)+"</Id>");
        authXML.append("<Brand>"+redeemMap.get(ELEM_BRAND)+"</Brand>");
        authXML.append("</Account>");
        authXML.append("<Customer>");
        authXML.append("<Name>");
        authXML.append("<Family>"+redeemMap.get(ELEM_FAMILY)+"</Family>");
        authXML.append("<Given>"+redeemMap.get(ELEM_GIVEN)+"</Given>");
        authXML.append("<Company>"+""+"</Company>");
        authXML.append("<Salutation>"+""+"</Salutation>");
        authXML.append("<Title>"+""+"</Title>");
        authXML.append("</Name>");
        authXML.append("<Contact>");
        authXML.append("<Email>"+redeemMap.get(ELEM_EMAIL)+"</Email>");
        authXML.append("<Ip>"+redeemMap.get(ELEM_IP)+"</Ip>");
        authXML.append("<Mobile>"+""+"</Mobile>");
        authXML.append("</Contact>");
        authXML.append("<Address>");
        authXML.append("<City>"+redeemMap.get(ELEM_CITY)+"</City>");
        authXML.append("<Country>"+redeemMap.get(ELEM_COUNTRY)+"</Country>");
         authXML.append("<State>"+redeemMap.get(ELEM_STATE)+"</State>");
        authXML.append("<Street>"+redeemMap.get(ELEM_STREET)+"</Street>");
        authXML.append("<zip>"+redeemMap.get(ELEM_ZIP)+"</zip>");
        authXML.append("</Address>");
         authXML.append("</Customer>");
        authXML.append("</Transaction>");
        authXML.append("</Request>");

       return authXML.toString();
      }

    /**
     * The Reversal voids a previous Debit (DB) or Preauthorization (PA) transaction.
     * Same as cancel or void
     * @param redeemMap
     * @return
     */
    public static String createReversalRequest(Map<String, String> redeemMap ) {

           StringBuffer reversalXML = new StringBuffer();

           reversalXML.append("<Request version=\""+redeemMap.get(ATTR_VERSION)+"\">");
           reversalXML.append("<Header>");
           reversalXML.append("<Security sender=\""+redeemMap.get(ATTR_SENDER)+"\" />");
           reversalXML.append("</Header>");
           reversalXML.append("<Transaction mode=\""+redeemMap.get(ATTR_MODE)+"\" channel=\""+redeemMap.get(ATTR_CHANNEL)+"\">");
           reversalXML.append("<Identification>");
           reversalXML.append("<TransactionID>"+redeemMap.get(ELEM_TRANSACTIONID)+"</TransactionID>");
           reversalXML.append("<ReferenceID>"+redeemMap.get(ELEM_REFERENCEID)+"</ReferenceID>");
           reversalXML.append("</Identification>");
           reversalXML.append("<User login=\""+redeemMap.get(ATTR_LOGIN)+"\" pwd=\""+redeemMap.get(ATTR_PWD)+"\" />");
           reversalXML.append("<Payment code=\"VA.RV\">");
           reversalXML.append("</Payment>");
           reversalXML.append("</Transaction>");
           reversalXML.append("</Request>");


            return reversalXML.toString();
        }

     /**
      *
      * @param redeemMap
      * @return
      */
     public static String createRefundRequest(Map<String, String> redeemMap ) {

           StringBuffer reversalXML = new StringBuffer();

           reversalXML.append("<Request version=\""+redeemMap.get(ATTR_VERSION)+"\">");
           reversalXML.append("<Header>");
           reversalXML.append("<Security sender=\""+redeemMap.get(ATTR_SENDER)+"\" />");
           reversalXML.append("</Header>");
           reversalXML.append("<Transaction mode=\""+redeemMap.get(ATTR_MODE)+"\" channel=\""+redeemMap.get(ATTR_CHANNEL)+"\">");
           reversalXML.append("<Identification>");
           reversalXML.append("<TransactionID>"+redeemMap.get(ELEM_TRANSACTIONID)+"</TransactionID>");
           reversalXML.append("<ReferenceID>"+redeemMap.get(ELEM_REFERENCEID)+"</ReferenceID>");
           reversalXML.append("</Identification>");
           reversalXML.append("<User login=\""+redeemMap.get(ATTR_LOGIN)+"\" pwd=\""+redeemMap.get(ATTR_PWD)+"\" />");
           reversalXML.append("<Payment code=\"VA.RF\">");
           reversalXML.append("<Presentation>");
           reversalXML.append("<Amount>"+redeemMap.get(ELEM_AMOUNT)+"</Amount>");
           reversalXML.append("<Currency>"+redeemMap.get(ELEM_CURRENCY)+"</Currency>");
           reversalXML.append("</Presentation>");
           reversalXML.append("</Payment>");
           reversalXML.append("</Transaction>");
           reversalXML.append("</Request>");




            return reversalXML.toString();
        }

    /**
          *
          * @param redeemMap
          * @return
          */
         public static String createRefundQuery(Map<String, String> redeemMap ) {

               StringBuffer reversalXML = new StringBuffer();

               reversalXML.append("<Request version=\""+redeemMap.get(ATTR_VERSION)+"\">");
               reversalXML.append("<Header>");
               reversalXML.append("<Security sender=\""+redeemMap.get(ATTR_SENDER)+"\" />");
               reversalXML.append("</Header>");
               reversalXML.append("<Query entity=\""+redeemMap.get(ATTR_CHANNEL)+"\" level=\"CHANNEL\" mode=\""+redeemMap.get(ATTR_MODE)+"\" type=\"STANDARD\">");
               reversalXML.append("<User login=\""+redeemMap.get(ATTR_LOGIN)+"\" pwd=\""+redeemMap.get(ATTR_PWD)+"\" />");
               reversalXML.append("<Period from=\"2006-03-04\" to=\"2006-03-04\"/>");
               reversalXML.append("<Types>");
               reversalXML.append("<Type code=\"RF\"/>");
               reversalXML.append("</Types>");
               reversalXML.append("</Query>");
               reversalXML.append("</Request>");
               return reversalXML.toString();
            }



    public static Document createDocumentFromString(String xmlString ) {

           Document doc = null;

          try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

              // root elements
              doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
          }
         catch (ParserConfigurationException pce) {
		        log.error("ParserConfigurationException--->",pce);
	     }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error("Exception--->",e);
        }

       return doc;

    }




    public static void main(String[] a)
    {
        //Preparing map for authentication request
        Map<String, String> authMap = new TreeMap<String, String>();

        authMap.put(ATTR_VERSION,"1.0");
        authMap.put(ATTR_SENDER,"8a9094ef10ea46390110fd2e15861146");
        authMap.put(ATTR_TOKEN,"payon");
        authMap.put(ATTR_MODE,"LIVE");
        authMap.put(ATTR_CHANNEL,"ff8081811c66bcdc011c6a29e8fc0dbe");
        authMap.put(ELEM_TRANSACTIONID,"1577716");
        authMap.put(ATTR_LOGIN,"8a9094ef10ea46390110fd2ea1ab1151");
        authMap.put(ATTR_PWD,"BMWMd44y");
        authMap.put(ATTR_CODE,"VA.DB");
        authMap.put(ELEM_AMOUNT,"21");
        authMap.put(ELEM_CURRENCY,"USD");
        authMap.put(ELEM_ID,"b1d4da97-bd32-460e-838e-4c27aeae0b3f");
        authMap.put(ELEM_BRAND,"PASTEANDPAY_V");
        authMap.put(ELEM_FAMILY,"Farley");
        authMap.put(ELEM_GIVEN,"Joanne");
        authMap.put(ELEM_COMPANY,"");
        authMap.put(ELEM_SALUTATION,"");
        authMap.put(ELEM_TITLE,"");
        authMap.put(ELEM_EMAIL,"joannefarley@mail.com");
        authMap.put(ELEM_IP,"96.236.7.223");
        authMap.put(ELEM_MOBILE,"");
        authMap.put(ELEM_CITY,"Clemons");
        authMap.put(ELEM_COUNTRY,"US");
        authMap.put(ELEM_STATE,"New York");
        authMap.put(ELEM_STREET,"42 Lake Road");

        authMap.put(ELEM_ZIP,"12819");


        //Document doc = WriteXMLRequest.createRedeemRequest(authMap);


        authMap.put(ELEM_REFERENCEID,"ff80808139b9b3730139bdc75afd48a1");


         /*Element root = doc.getDocumentElement();
        String s = root.toString();
        System.out.println(s);
*/
        String doc2 = WriteXMLRequest.createReversalRequest(authMap);
        //System.out.println(doc2);



        authMap.put(ELEM_REFERENCEID,"ff80808139b9b3730139bdc95caf48bb");

        //Document doc3 = WriteXMLRequest.createReversalRequest(authMap);



        /*try {
         // write the content into xml file
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer();
         
          DOMSource source = new DOMSource(doc);
          //StreamResult result = new StreamResult(new File("C:\\file.xml"));

          // Output to console for testing
           StreamResult result = new StreamResult(System.out);

           transformer.transform(source, result);

        }
        catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }*/


    }
}


package com.payment.paySafeCard.pscservice;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/10/15
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PSCSOAPClient
{
    private SOAPMessage soapMessage;
    private SOAPMessage soapResponse;

    public PSCSOAPClient() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("urn", "urn:pscservice");
    }

    public void doCall(String url) throws Exception {
        try {
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            SOAPMessage response = soapConnection.call(soapMessage, url);
            soapResponse = response;
            soapConnection.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public void addElement(String name, HashMap<String, String> kvp,
                           HashMap<String, ArrayList<HashMap<String, String>>> childElements) throws Exception {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElement = soapBody.addChildElement(name, "urn");
        for (Map.Entry<String, String> entry : kvp.entrySet()) {
            soapBodyElement.addChildElement(entry.getKey(), "urn").addTextNode(entry.getValue());
        }
        if (childElements != null && childElements.isEmpty() == false) {
            for (Entry<String, ArrayList<HashMap<String, String>>> entry : childElements.entrySet()) {
                for (int i = 0; i < childElements.get(entry.getKey()).size(); i++) {
                    SOAPElement element = soapBodyElement.addChildElement(entry.getKey(), "urn");
                    HashMap<String, String> elementMap = childElements.get(entry.getKey()).get(i);
                    element.addChildElement("key", "urn").addTextNode(elementMap.get("key"));
                    element.addChildElement("value", "urn").addTextNode(elementMap.get("value"));
                }
            }
        }
        soapMessage.saveChanges();
    }

    public String getElement(String name) throws Exception {
        NodeList nl = soapResponse.getSOAPBody().getChildNodes().item(0).getChildNodes().item(0).getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getLocalName().equals(name))
                return nl.item(i).getTextContent();
        }
        return null;
    }
}

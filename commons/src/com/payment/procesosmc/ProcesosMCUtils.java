package com.payment.procesosmc;

import com.directi.pg.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 5/20/15
 * Time: 05:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCUtils
{
    private static Logger logger=new Logger(ProcesosMCUtils.class.getName());
    public String getProcesosMCSpecificCardTypeName(String paymentCardType)
    {

        String cardType="";
        if("MC".equals(paymentCardType))
        {
            cardType="MC";
        }
        else if("DINER".equals(paymentCardType))
        {
            cardType="DN";
        }
        else if("AMEX".equals(paymentCardType))
        {
            cardType="AE";
        }
        return cardType;
    }

    public HashMap readResponseForAuth(String responseXML)
    {
        Document doc = null;
        HashMap hashMap=new HashMap();
        doc = createDocumentFromString(responseXML);
        NodeList nList = doc.getElementsByTagName("Autorizacion");

        hashMap.put("Marca", getTagValue("Marca", (Element) nList.item(0)));
        hashMap.put("Resultado", getTagValue("Resultado", (Element) nList.item(0)));
        hashMap.put("CodAutoriza", getTagValue("CodAutoriza", (Element) nList.item(0)));
        hashMap.put("ReferenciaPMP", getTagValue("ReferenciaPMP", (Element) nList.item(0)));
        hashMap.put("Cuotas", getTagValue("Cuotas", (Element) nList.item(0)));
        hashMap.put("FecPrimeraCuota", getTagValue("FecPrimeraCuota", (Element) nList.item(0)));
        hashMap.put("MonedaCuota", getTagValue("MonedaCuota", (Element) nList.item(0)));
        hashMap.put("MontoCuota", getTagValue("MontoCuota", (Element) nList.item(0)));
        hashMap.put("Moneda", getTagValue("Moneda", (Element) nList.item(0)));
        hashMap.put("Monto", getTagValue("Monto", (Element) nList.item(0)));
        hashMap.put("Referencia", getTagValue("Referencia", (Element) nList.item(0)));
        hashMap.put("Fecha", getTagValue("Fecha", (Element) nList.item(0)));
        hashMap.put("Hora", getTagValue("Hora", (Element) nList.item(0)));
        hashMap.put("CodRespuesta", getTagValue("CodRespuesta", (Element) nList.item(0)));
        hashMap.put("CodPais", getTagValue("CodPais", (Element) nList.item(0)));
        hashMap.put("Tarjeta", getTagValue("Tarjeta", (Element) nList.item(0)));
        hashMap.put("Mensaje", getTagValue("Mensaje", (Element) nList.item(0)));
        hashMap.put("IdTxn", getTagValue("IdTxn", (Element) nList.item(0)));
        hashMap.put("Comercio", getTagValue("Comercio", (Element) nList.item(0)));
        hashMap.put("DataEnc", getTagValue("DataEnc", (Element) nList.item(0)));
        hashMap.put("Firma", getTagValue("Firma", (Element) nList.item(0)));
        return hashMap;
    }
    public HashMap readResponseForDeposit(String responseXML)
    {
        Document doc = null;
        HashMap hashMap=new HashMap();
        doc = createDocumentFromString(responseXML);
        NodeList nList = doc.getElementsByTagName("Deposito");

        hashMap.put("CodAutorizacion", getTagValue("CodAutorizacion", (Element) nList.item(0)));
        hashMap.put("NumPedido", getTagValue("NumPedido", (Element) nList.item(0)));
        hashMap.put("Moneda", getTagValue("Moneda", (Element) nList.item(0)));
        hashMap.put("Monto", getTagValue("Monto", (Element) nList.item(0)));
        hashMap.put("NroLote", getTagValue("NroLote", (Element) nList.item(0)));
        hashMap.put("FechaTxn", getTagValue("Fecha", (Element) nList.item(0)));
        hashMap.put("HoraTxn", getTagValue("HoraTxn", (Element) nList.item(0)));
        hashMap.put("CodRespuesta", getTagValue("CodRespuesta", (Element) nList.item(0)));
        hashMap.put("MensajeResp", getTagValue("MensajeResp", (Element) nList.item(0)));
        hashMap.put("IdTxn", getTagValue("IdTxn", (Element) nList.item(0)));
        hashMap.put("Comercio", getTagValue("Comercio", (Element) nList.item(0)));
        return hashMap;
    }
    public HashMap readResponseForCancel(String responseXML)
    {
        Document doc = null;
        HashMap hashMap=new HashMap();
        doc = createDocumentFromString(responseXML);
        NodeList nList = doc.getElementsByTagName("Anulacion");

        hashMap.put("CodAutorizacion", getTagValue("CodAutorizacion", (Element) nList.item(0)));
        hashMap.put("NumPedido", getTagValue("NumPedido", (Element) nList.item(0)));
        hashMap.put("Moneda", getTagValue("Moneda", (Element) nList.item(0)));
        hashMap.put("Monto", getTagValue("Monto", (Element) nList.item(0)));
        hashMap.put("FechaTxn", getTagValue("Fecha", (Element) nList.item(0)));
        hashMap.put("HoraTxn", getTagValue("HoraTxn", (Element) nList.item(0)));
        hashMap.put("CodRespuesta", getTagValue("CodRespuesta", (Element) nList.item(0)));
        hashMap.put("MensajeResp", getTagValue("MensajeResp", (Element) nList.item(0)));
        hashMap.put("IdTxn", getTagValue("IdTxn", (Element) nList.item(0)));
        hashMap.put("Comercio", getTagValue("Comercio", (Element) nList.item(0)));
        return hashMap;
    }
    public HashMap readResponseForInquiry(String responseXML)
    {
        Document doc = null;
        HashMap hashMap=new HashMap();
        doc = createDocumentFromString(responseXML);
        NodeList nList = doc.getElementsByTagName("Consulta");

        hashMap.put("CodAutorizacion", getTagValue("CodAutorizacion", (Element) nList.item(0)));
        hashMap.put("NumPedido", getTagValue("NumPedido", (Element) nList.item(0)));
        hashMap.put("Monto", getTagValue("Monto", (Element) nList.item(0)));
        hashMap.put("FechaTxn", getTagValue("Fecha", (Element) nList.item(0)));
        hashMap.put("CodRespuesta", getTagValue("CodRespuesta", (Element) nList.item(0)));
        hashMap.put("MensajeResp", getTagValue("MensajeResp", (Element) nList.item(0)));
        hashMap.put("IdTxn", getTagValue("IdTxn", (Element) nList.item(0)));
        hashMap.put("Comercio", getTagValue("Comercio", (Element) nList.item(0)));
        hashMap.put("MensajeResp", getTagValue("MensajeResp", (Element) nList.item(0)));
        hashMap.put("Estado", getTagValue("Estado", (Element) nList.item(0)));
        hashMap.put("MensajeEstado", getTagValue("MensajeEstado", (Element) nList.item(0)));
        return hashMap;
    }
    public static Document createDocumentFromString(String xmlString )
    {
        Document doc = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(new InputSource(new StringReader( xmlString ) ));
        }
        catch (ParserConfigurationException pce) {
            logger.error("ParserConfigurationException---->",pce);
        }
        catch (Exception e){
            logger.error("Exception---->", e);
        }
        return doc;
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
    public String getPaymentFacilitatorCode(String subMerchantCode)
    {
        String facilitatorCode="";
        if("4001362".equals(subMerchantCode))
        {
            facilitatorCode="4000391";
        }
        else if("4007727".equals(subMerchantCode))
        {
            facilitatorCode="4007719";
        }
        return facilitatorCode;
    }
    public String getPaymentFacilitatorKey(String subMerchantCode)
    {
        String merchantKey="";
        if("4001362".equals(subMerchantCode))
        {
            merchantKey="cruPecreswayaD2bRapathaprAPupesw";
        }
        else if("4007727".equals(subMerchantCode))
        {
            merchantKey="n9yA9r4SedrUmuSeh4wRejEpAc7aHeCE";
        }
        return merchantKey;
    }
    public String getTransDate(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
        String transDate="";
        String dateArr[] =String.valueOf(dateFormat.format(date)).split(" ");
        transDate=dateArr[0];
        return transDate;

    }
    public String getTransTime(Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
        String transTime="";
        String dateArr[] =String.valueOf(dateFormat.format(date)).split(" ");
        transTime=dateArr[1];
        return transTime;

    }
    public String getExpiryYearLast2Digit(String expiryYear)
    {
        return expiryYear.substring(2);
    }
}

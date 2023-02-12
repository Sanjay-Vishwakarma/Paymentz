package com.payment.p4.vos.transactionBlock.identificationBlock;

import com.payment.p4.vos.transactionBlock.identificationBlock.extraBlock.ExtraData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name="Identification")
@XmlAccessorType(XmlAccessType.FIELD)
public class Identification
{
    @XmlElement(name = "TransactionID")
     String TransactionID;

    @XmlElement(name = "ShopperID")
     String ShopperID;

    @XmlElement(name = "InvoiceID")
     String InvoiceID;

    @XmlElement(name = "ReferenceID")
     String ReferenceID;

    @XmlElement(name = "UniqueID")
    String UniqueID;

    @XmlElement(name = "ShortID")
    String ShortID;

    @XmlElement(name = "ExtraData")
    ExtraData ExtraData;

    @XmlElement(name = "RegistrationUniqueID")
    String RegistrationUniqueID;



    public String getTransactionID()
    {
        return TransactionID;
    }

    public void setTransactionID(String transactionID)
    {
        TransactionID = transactionID;
    }

    public String getShopperID()
    {
        return ShopperID;
    }

    public void setShopperID(String shopperID)
    {
        ShopperID = shopperID;
    }

    public String getInvoiceID()
    {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID)
    {
        InvoiceID = invoiceID;
    }

    public String getReferenceID()
    {
        return ReferenceID;
    }

    public void setReferenceID(String referenceID)
    {
        ReferenceID = referenceID;
    }

    public String getUniqueID()
    {
        return UniqueID;
    }

    public void setUniqueID(String uniqueID)
    {
        UniqueID = uniqueID;
    }

    public String getShortID()
    {
        return ShortID;
    }

    public void setShortID(String shortID)
    {
        ShortID = shortID;
    }

    public ExtraData getExtraData()
    {
        return ExtraData;
    }

    public void setExtraData(ExtraData extraData)
    {
        ExtraData = extraData;
    }

    public String getRegistrationUniqueID()
    {
        return RegistrationUniqueID;
    }

    public void setRegistrationUniqueID(String registrationUniqueID)
    {
        RegistrationUniqueID = registrationUniqueID;
    }
}

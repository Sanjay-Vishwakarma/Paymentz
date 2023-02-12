package com.payment.p4.vos.transactionBlock.customerBlock.signatureBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10/7/2015.
 */
@XmlRootElement(name = "Signature")
@XmlAccessorType(XmlAccessType.FIELD)
public class Signature
{
    @XmlAttribute(name = "empty")
    String empty;

    public String getEmpty()
    {
        return empty;
    }

    public void setEmpty(String empty)
    {
        this.empty = empty;
    }
}

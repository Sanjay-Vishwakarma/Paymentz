package com.payment.p4.vos.transactionBlock.authorizationBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 16/11/2015.
 */
@XmlRootElement(name = "Authorization")
@XmlAccessorType(XmlAccessType.FIELD)
public class Authorization
{
    @XmlAttribute(name = "type")
    String type;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}

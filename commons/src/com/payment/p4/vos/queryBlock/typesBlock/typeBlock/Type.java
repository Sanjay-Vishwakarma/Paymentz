package com.payment.p4.vos.queryBlock.typesBlock.typeBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 30/10/2015.
 */
@XmlRootElement(name = "Type")
@XmlAccessorType(XmlAccessType.FIELD)
public class Type
{
    @XmlAttribute(name = "code")
    String code;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}

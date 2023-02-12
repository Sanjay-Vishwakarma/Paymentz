package com.payment.p4.vos.transactionBlock.identificationBlock.extraBlock;

import javax.xml.bind.annotation.*;

/**
 * Created by admin on 10/2/2015.
 */
@XmlRootElement(name="ExtraData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExtraData
{
    @XmlAttribute(name="name")
    String name;

    @XmlElement(name="Password")
    String Var;


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVar()
    {
        return Var;
    }

    public void setVar(String var)
    {
        Var = var;
    }
}

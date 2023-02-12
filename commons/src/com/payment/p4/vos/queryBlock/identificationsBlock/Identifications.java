package com.payment.p4.vos.queryBlock.identificationsBlock;

import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 28/10/2015.
 */
@XmlRootElement(name = "Identifications")
@XmlAccessorType(XmlAccessType.FIELD)
public class Identifications
{
    @XmlElement(name = "Identification")
    List<Identification> identification;

    public List<Identification> getIdentification()
    {
        return identification;
    }

    public void setIdentification(List<Identification> identification)
    {
        this.identification = identification;
    }
}

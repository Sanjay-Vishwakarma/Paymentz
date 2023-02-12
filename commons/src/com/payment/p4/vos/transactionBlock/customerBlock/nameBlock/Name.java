package com.payment.p4.vos.transactionBlock.customerBlock.nameBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket 10/2/2015.
 */
@XmlRootElement(name="Name")
@XmlAccessorType(XmlAccessType.FIELD)
public class Name
{
    @XmlElement(name = "Given")
    String Given;

    @XmlElement(name = "Family")
    String Family;

    @XmlElement(name = "Sex")
    String Sex;

    @XmlElement(name = "Birthdate")
    String Birthdate;

    public String getGiven()
    {
        return Given;
    }

    public void setGiven(String given)
    {
        Given = given;
    }

    public String getFamily()
    {
        return Family;
    }

    public void setFamily(String family)
    {
        Family = family;
    }

    public String getSex()
    {
        return Sex;
    }

    public void setSex(String sex)
    {
        Sex = sex;
    }

    public String getBirthdate()
    {
        return Birthdate;
    }

    public void setBirthdate(String birthdate)
    {
        Birthdate = birthdate;
    }
}

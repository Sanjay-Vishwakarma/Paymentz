package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 7/17/2018.
 */

@XmlRootElement(name="Recipient")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recipient
{
    @XmlElement(name="DateOfBirth")
    String DateOfBirth;

    @XmlElement(name="AccountNumber")
    String AccountNumber;

    @XmlElement(name="PostalCode")
    String PostalCode;

    @XmlElement(name="Surname")
    String Surname;

    public String getDateOfBirth()
    {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth)
    {
        this.DateOfBirth = dateOfBirth;
    }

    public String getAccountNumber()
    {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.AccountNumber = accountNumber;
    }

    public String getPostalCode()
    {
        return PostalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.PostalCode = postalCode;
    }

    public String getSurname()
    {
        return Surname;
    }

    public void setSurname(String surname)
    {
        this.Surname = surname;
    }
}

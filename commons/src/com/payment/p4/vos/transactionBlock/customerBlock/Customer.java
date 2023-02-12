package com.payment.p4.vos.transactionBlock.customerBlock;

import com.payment.p4.vos.transactionBlock.customerBlock.accountBlock.Account;
import com.payment.p4.vos.transactionBlock.customerBlock.addressBlock.Address;
import com.payment.p4.vos.transactionBlock.customerBlock.contactBlock.Contact;
import com.payment.p4.vos.transactionBlock.customerBlock.nameBlock.Name;
import com.payment.p4.vos.transactionBlock.customerBlock.signatureBlock.Signature;

import javax.xml.bind.annotation.*;

/**
 * Created by admin on 10/2/2015.
 */
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer
{

    @XmlAttribute(name="registration")
    String registration;

    @XmlElement(name="Account")
    Account Account;
    @XmlElement(name="Name")
    Name Name;
    @XmlElement(name="Address")
    Address Address;
    @XmlElement(name="Contact")
    Contact Contact;

    @XmlElement(name="Signature")
    Signature Signature;

    public String getRegistration()
    {
        return registration;
    }

    public void setRegistration(String registration)
    {
        this.registration = registration;
    }

    public Account getAccount()
    {
        return Account;
    }

    public void setAccount(Account account)
    {
        Account = account;
    }

    public Name getName()
    {
        return Name;
    }

    public void setName(Name name)
    {
        Name = name;
    }

    public Address getAddress()
    {
        return Address;
    }

    public void setAddress(Address address)
    {
        Address = address;
    }

    public Contact getContact()
    {
        return Contact;
    }

    public void setContact(Contact contact)
    {
        Contact = contact;
    }

    public Signature getSignature()
    {
        return Signature;
    }

    public void setSignature(Signature signature)
    {
        Signature = signature;
    }
}

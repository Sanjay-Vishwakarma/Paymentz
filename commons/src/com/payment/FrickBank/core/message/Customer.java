package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Customer
{
    @XStreamAlias("Name")
    private Name name;
    @XStreamAlias("Contact")
    private Contact contact;
    @XStreamAlias("Address")
    private Address address;

    public Name getName()
    {
        return name;
    }

    public void setName(Name name)
    {
        this.name = name;
    }

    public Contact getContact()
    {
        return contact;
    }

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }
}

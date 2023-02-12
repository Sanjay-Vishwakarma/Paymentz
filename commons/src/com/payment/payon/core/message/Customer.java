package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:38 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Customer")
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

    public MarketData getMarketData()
    {
        return marketData;
    }

    public void setMarketData(MarketData marketData)
    {
        this.marketData = marketData;
    }

    private MarketData marketData ;


}

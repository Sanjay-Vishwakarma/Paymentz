package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Name
{
    public void setFamily(String family)
    {
        this.family = family;
    }

    public void setGiven(String given)
    {
        this.given = given;
    }

    @XStreamAlias("Family")
    private String family="kosel";
    @XStreamAlias("Given")
    private String given= "bobby";
}

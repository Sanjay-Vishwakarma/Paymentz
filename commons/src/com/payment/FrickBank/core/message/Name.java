package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Name
{
    public String getSalutation()
    {
        return Salutation;
    }

    public void setSalutation(String salutation)
    {
        Salutation = salutation;
    }

    public String getTitle()
    {
        return Title;
    }

    public void setTitle(String title)
    {
        Title = title;
    }

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

    public String getCompany()
    {
        return Company;
    }

    public void setCompany(String company)
    {
        Company = company;
    }

    @XStreamAlias("Salutation")
    private String Salutation="Salutation";
    @XStreamAlias("Title")
    private String Title="Title";
    @XStreamAlias("Given")
    private String Given="Given";
    @XStreamAlias("Family")
    private String Family="Family";
    @XStreamAlias("Company")
    private String Company="Company";

}

package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 5, 2012
 * Time: 10:00:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineVoucherAddressVO extends GenericAddressDetailsVO
{


    private String company;
    private String salutation;
    private String title;
    private String mobile;


    /**
     *
     */
    public PayLineVoucherAddressVO()
    {
    }


    public PayLineVoucherAddressVO(String street, String city, String state, String country, String zipCode, String phone, String email, String firstname, String lastname, String ip, String company, String salutation, String title, String mobile)
    {
        super(street, city, state, country, zipCode, phone, email, firstname, lastname, ip);
        this.company = company;
        this.salutation = salutation;
        this.title = title;
        this.mobile = mobile;
    }


    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getSalutation()
    {
        return salutation;
    }

    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

   


}

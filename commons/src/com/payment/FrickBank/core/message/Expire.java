package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Expire
{
    public String getMonth()
    {
        return month;
    }

    public void setMonth(String month)
    {
        this.month = month;
    }

    public String getYear()
    {
        return year;
    }

    public void setYear(String year)
    {
        this.year = year;
    }

    @XStreamAsAttribute
    private String month="";
    @XStreamAsAttribute
    private String year="";
}

package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 8:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class CCDate
{
    public void setMonth(String month)
    {
        this.month = month;
    }

    public void setYear(String year)
    {
        this.year = year;
    }
    @XStreamAsAttribute
    private String month;
    @XStreamAsAttribute
    private String year;
}

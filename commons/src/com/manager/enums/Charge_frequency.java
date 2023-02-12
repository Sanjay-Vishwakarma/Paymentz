package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 7/28/14
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Charge_frequency
{
    Per_Transaction("Per_Transaction"),
    Yearly("Yearly"),
    Monthly("Monthly"),
    Weekly("Weekly"),
    One_Time("One_Time");

    private String frequency;

    Charge_frequency(String frequency)
    {
        this.frequency=frequency;
    }

    public String toString()
    {
        return frequency;
    }

}

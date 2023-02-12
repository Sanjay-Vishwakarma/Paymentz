package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 7/28/14
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Charge_unit
{
    FlatRate("FlatRate"),
    Percentage("Percentage");

    private String unit;


    Charge_unit(String unit)
    {
        this.unit=unit;
    }

    public String toString()
    {
        return unit;
    }
}

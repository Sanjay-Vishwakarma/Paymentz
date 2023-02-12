package com.manager.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/20/14
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public enum StatusChartsType
{
    SALES("sales"),
    REFUND("refund"),
    CHARGEBACK("chargeback");

    private String statusType;

    StatusChartsType(String statusType)
    {
        this.statusType=statusType;
    }

    public String toString()
    {
        return statusType;
    }
}

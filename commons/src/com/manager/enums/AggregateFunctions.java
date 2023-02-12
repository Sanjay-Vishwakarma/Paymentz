package com.manager.enums;

/**
 * Created by admin on 10/22/2015.
 */
public enum AggregateFunctions
{
    COUNT("COUNT"),
    MAX("MAX"),
    MIN("MIN"),
    SUM("SUM");


    private String aggregateFunction;

    AggregateFunctions(String aggregateFunction)
    {
        this.aggregateFunction=aggregateFunction;
    }

    public String toString()
    {
        return aggregateFunction;
    }

    public static AggregateFunctions getEnum(String value)
    {
        try
        {
            return AggregateFunctions.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}

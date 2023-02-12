package com.manager.enums;

/**
 * Created by admin on 10/22/2015.
 */
public enum ComparatorEnum
{
    OR("OR"),
    AND("AND");



    private String comparator;

    ComparatorEnum(String comparator)
    {
        this.comparator=comparator;
    }

    public String toString()
    {
        return comparator;
    }

    public static ComparatorEnum getEnum(String value)
    {
        try
        {
            return ComparatorEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}

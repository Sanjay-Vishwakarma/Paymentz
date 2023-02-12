package com.manager.enums;

/**
 * Created by admin on 11/15/2015.
 */
public enum DataType
{
    VARCHAR("ALPHANUMERIC"),
    INT("NUMERIC"),
    DECIMAL("DECIMAL"),
    ENUM("ENUM"),
    ;
    private String dataType;

    DataType(String dataType)
    {
        this.dataType=dataType;
    }

    public String dataType()
    {
        return dataType;
    }
}

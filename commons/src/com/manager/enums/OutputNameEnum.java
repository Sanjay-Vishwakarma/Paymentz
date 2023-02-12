package com.manager.enums;

/**
 * Created by Niket on 12/9/2015.
 */
public enum OutputNameEnum
{
    TRACKINGID("TRACKINGID"),
    ORDERID("ORDERID"),
    STATUS("STATUS"),
    STATUSDESCRIPTION("STATUSDESCRIPTION"),
    CHECKSUM("CHECKSUM"),
    AMOUNT("AMOUNT"),
    BILLINGDESCRIPTOR("BILLINGDESCRIPTOR"),
    ERROR("ERROR"),
    REDIRECTURL("REDIRECTURL"),
    CONTINUEPROCESSING("CONTINUEPROCESSING");

    private String outputName;

    OutputNameEnum(String outputName)
    {
        this.outputName=outputName;
    }

    public String toString()
    {
        return outputName;
    }

    public static OutputNameEnum getEnum(String value)
    {
        try
        {
            return OutputNameEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}

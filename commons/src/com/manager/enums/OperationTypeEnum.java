package com.manager.enums;

/**
 * Created by admin on 12/9/2015.
 */
public enum OperationTypeEnum
{
    EXECUTION("EXECUTION"),
    PRECONDITION("PRECONDITION"),
    POSTCONDITION_FAIL("POSTCONDITION_FAIL"),
    POSTCONDITION_PASS("POSTCONDITION_PASS");

    private String operationTypeEnum;

    OperationTypeEnum(String operationTypeEnum)
    {
        this.operationTypeEnum=operationTypeEnum;
    }

    public String toString()
    {
        return operationTypeEnum;
    }

    public static OperationTypeEnum getEnum(String value)
    {
        try
        {
            return OperationTypeEnum.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}

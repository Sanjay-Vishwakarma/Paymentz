package com.payment.exceptionHandler.errorcode.errorcodeEnum;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/14/15
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ErrorType
{
    VALIDATION("VALIDATION"),
    SYSCHECK("SYSCHECK"),
    REJECTED_TRANSACTION("REJECTED_TRANSACTION"),
    SUCCESSFUL_TRANSACTION("SUCCESSFUL_TRANSACTION"),
    REFERENCE_VALIDATION("REFERENCE_VALIDATION"),
    MAF_VALIDATION("MAF_VALIDATION");

    private String fieldName;

    ErrorType(String fieldName)
    {
        this.fieldName = fieldName;
    }


    @Override
    public String toString()
    {
        return fieldName;
    }

}

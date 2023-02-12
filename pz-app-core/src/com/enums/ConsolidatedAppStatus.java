package com.enums;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

/**
 * Created by admin on 10/1/2015.
 */
public enum ConsolidatedAppStatus
{
    GENERATED("GENERATED"),
    INVALIDATED("INVALIDATED"),
    APPROVED("APPROVED");

    private String consolidatedAppStatus;

    ConsolidatedAppStatus(String consolidatedAppStatus)
    {
        this.consolidatedAppStatus=consolidatedAppStatus;
    }

    public String toString()
    {
        return consolidatedAppStatus;
    }

    public static /*<E extends Enum<E>>*/ ConsolidatedAppStatus getEnum(String value) throws PZTechnicalViolationException
    {
        try
        {
           return ConsolidatedAppStatus.valueOf(value);

        } catch (IllegalArgumentException iae)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ConsolidatedAppStatus.class.getName(),"getEnum()",null,"common","Enum not there", PZTechnicalExceptionEnum.IOEXCEPTION,null,iae.getMessage(),iae.getCause());
        }
        return null;

    }

}


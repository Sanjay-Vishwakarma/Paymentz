package com.payment.exceptionHandler;

import com.payment.exceptionHandler.constraint.PZTechnicalConstraint;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZTechnicalViolationException extends PZGenericConstraintViolationException
{
    PZTechnicalConstraint pzTechnicalConstraint;

    public PZTechnicalViolationException(PZTechnicalConstraint technicalConstraint,String msg,Throwable cause)
    {
        super(msg,cause);
        pzTechnicalConstraint = technicalConstraint;
    }

    public PZTechnicalViolationException(PZTechnicalConstraint technicalConstraint,String msg)
    {
        super(msg);
        pzTechnicalConstraint = technicalConstraint;
    }
    public PZTechnicalConstraint getPzTechnicalConstraint()
    {
        return pzTechnicalConstraint;
    }

    public void setPzTechnicalConstraint(PZTechnicalConstraint pzTechnicalConstraint)
    {
        this.pzTechnicalConstraint = pzTechnicalConstraint;
    }

}

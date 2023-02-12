package com.payment.exceptionHandler;

import com.payment.exceptionHandler.constraint.PZConstraint;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZConstraintViolationException extends PZGenericConstraintViolationException
{
    PZConstraint pzConstraint;



    public PZConstraintViolationException(PZConstraint constViolation, String msg,Throwable cause)
    {
        super(msg,cause);
        pzConstraint = constViolation;
    }

    public PZConstraintViolationException(PZConstraint constViolation, String msg)
    {
        super(msg);
        pzConstraint = constViolation;

    }
    public PZConstraint getPzConstraint()
    {
        return pzConstraint;
    }

    public void setPzConstraint(PZConstraint pzConstraint)
    {
        this.pzConstraint = pzConstraint;
    }
}

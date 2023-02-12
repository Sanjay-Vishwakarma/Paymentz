package com.payment.exceptionHandler;

import com.payment.exceptionHandler.constraint.PZDBConstraint;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZDBViolationException extends PZGenericConstraintViolationException
{


    PZDBConstraint pzdbConstraint ;

    public PZDBViolationException(PZDBConstraint dbConstraint, String msg, Throwable cause)
    {
        super(msg,cause);
        pzdbConstraint = dbConstraint;
    }


    public PZDBViolationException(PZDBConstraint dbConstraint, String msg)
    {
        super(msg);
        pzdbConstraint = dbConstraint;
    }
    public PZDBConstraint getPzdbConstraint()
    {
        return pzdbConstraint;
    }

    public void setPzdbConstraint(PZDBConstraint pzdbConstraint)
    {
        this.pzdbConstraint = pzdbConstraint;
    }
}

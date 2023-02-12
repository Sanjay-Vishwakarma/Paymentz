package com.payment.exceptionHandler.constraint;

import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZConstraint extends PZGenericConstraint
{
    PZConstraintExceptionEnum pzConstEnum;

    public PZConstraint(String cls,String method, String property,String module,String msg, PZConstraintExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO)
    {
        super(cls,method,property,module,msg,errorCodeListVO);
        pzConstEnum = constEnum;
    }

    public PZConstraintExceptionEnum getPzConstEnum()
    {
        return pzConstEnum;
    }

    public void setPzConstEnum(PZConstraintExceptionEnum pzConstEnum)
    {
        this.pzConstEnum = pzConstEnum;
    }

    @Override
    public String toString()
    {
        return "PZConstraint{" +
                "pzConstEnum=" + pzConstEnum +
                "} " + super.toString();
    }
}

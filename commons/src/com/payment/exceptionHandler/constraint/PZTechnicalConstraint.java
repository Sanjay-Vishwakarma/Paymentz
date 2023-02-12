package com.payment.exceptionHandler.constraint;

import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 9:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZTechnicalConstraint extends PZGenericConstraint
{
    PZTechnicalExceptionEnum pzTechEnum;

    public PZTechnicalConstraint(String cls,String method,String property,String module,String msg, PZTechnicalExceptionEnum techEnum,ErrorCodeListVO errorCodeListVO)
    {
        super(cls,method,property,module,msg,errorCodeListVO);
        pzTechEnum = techEnum;
    }

    public PZTechnicalExceptionEnum getPzTechEnum()
    {
        return pzTechEnum;
    }

    public void setPzTechEnum(PZTechnicalExceptionEnum pzTechEnum)
    {
        this.pzTechEnum = pzTechEnum;
    }

    @Override
    public String toString()
    {
        return "PZTechnicalConstraint{" +
                "pzTechEnum=" + pzTechEnum +
                "} " + super.toString();
    }
}

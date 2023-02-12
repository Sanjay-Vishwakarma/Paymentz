package com.payment.exceptionHandler.constraint;

import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 8:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZDBConstraint extends PZGenericConstraint
{
    PZDBExceptionEnum pzDBEnum;

    public PZDBConstraint(String cls,String method,String property,String module,String msg, PZDBExceptionEnum dbEnum,ErrorCodeListVO errorCodeListVO)
    {
        super(cls,method,property,module, msg,errorCodeListVO);
        pzDBEnum = dbEnum;
    }

    public PZDBExceptionEnum getPzDBEnum()
    {
        return pzDBEnum;
    }

    public void setPzDBEnum(PZDBExceptionEnum pzDBEnum)
    {
        this.pzDBEnum = pzDBEnum;
    }

    @Override
    public String toString()
    {
        return "PZDBConstraint{" +
                "pzDBEnum=" + pzDBEnum +
                super.toString()+ "} " ;
    }
}

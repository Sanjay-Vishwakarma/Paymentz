package com.payment.exceptionHandler.constraint;

import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/20/14
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZGenericConstraint
{

    String className;
    String methodName;
    String propertyName;
    String moduleName;
    String message;
    ErrorCodeListVO errorCodeListVO;

    public PZGenericConstraint(String cls,String method,String property,String module,String msg,ErrorCodeListVO errorCodeListVO)
    {
        className = cls;
        methodName = method;
        propertyName = property;
        moduleName = module;
        message = msg;
        this.errorCodeListVO=errorCodeListVO;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    @Override
    public String toString()
    {
        return "PZGenericConstraint{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

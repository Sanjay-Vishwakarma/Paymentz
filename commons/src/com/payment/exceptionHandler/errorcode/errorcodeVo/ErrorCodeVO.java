package com.payment.exceptionHandler.errorcode.errorcodeVo;

import com.payment.exceptionHandler.errorcode.errorcodeEnum.*;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/14/15
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorCodeVO
{
    private ErrorType errorType;
    private ErrorName errorName;
    private String errorCode;
    private String errorDescription;
    private String errorReason;
    private String apiCode;
    private String apiDescription;

    public String getApiCode()
    {
        return apiCode;
    }

    public void setApiCode(String apiCode)
    {
        this.apiCode = apiCode;
    }

    public String getApiDescription()
    {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription)
    {
        this.apiDescription = apiDescription;
    }

    public String getErrorSubType()
    {
        return errorSubType;
    }

    public void setErrorSubType(String errorSubType)
    {
        this.errorSubType = errorSubType;
    }

    private String errorSubType;

    public ErrorType getErrorType()
    {
        return errorType;
    }

    public void setErrorType(ErrorType errorType)
    {
        this.errorType = errorType;
    }

    public ErrorName getErrorName()
    {
        return errorName;
    }

    public void setErrorName(ErrorName errorName)
    {
        this.errorName = errorName;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorDescription()
    {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription)
    {
        this.errorDescription = errorDescription;
    }

    public String getErrorReason()
    {
        return errorReason;
    }

    public void setErrorReason(String errorReason)
    {
        this.errorReason = errorReason;
    }
}

package com.payment.exceptionHandler;

import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.owasp.esapi.errors.ValidationException;

/**
 * Created by Admin on 4/20/2015.
 */
public class PZValidationException extends ValidationException
{
    private ErrorCodeVO errorCodeVO;

    protected PZValidationException()
    {
    }


    public PZValidationException(String userMessage, String logMessage,ErrorCodeVO errorCodeVO) {
        super(userMessage, logMessage);
        this.errorCodeVO=errorCodeVO;
    }

    public PZValidationException(String userMessage, String logMessage, Throwable cause,ErrorCodeVO errorCodeVO) {
        super(userMessage, logMessage, cause);
        this.errorCodeVO=errorCodeVO;
    }

    public PZValidationException(String userMessage, String logMessage, String context,ErrorCodeVO errorCodeVO) {
        super(userMessage, logMessage,context);
        this.errorCodeVO=errorCodeVO;

    }

    public PZValidationException(String userMessage, String logMessage, Throwable cause, String context,ErrorCodeVO errorCodeVO) {
        super(userMessage, logMessage, cause,context);
        this.errorCodeVO=errorCodeVO;

    }

    public ErrorCodeVO getErrorCodeVO()
    {
        return errorCodeVO;
    }

    public void setErrorCodeVO(ErrorCodeVO errorCodeVO)
    {
        this.errorCodeVO = errorCodeVO;
    }
}

package com.transaction.vo;

import javax.xml.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 6/1/15
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
/*@XmlType(name = "DirectTransactionErrorCodeVO",propOrder = {"errorCode","errorDescription","errorReason"})*/
@XmlRootElement(name = "DirectTransactionErrorCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectTransactionErrorCode
{
    @XmlElement(name = "errorCode")
    private String errorCode;
    @XmlElement(name = "errorDescription")
    private String errorDescription;
    @XmlElement(name = "errorReason")
    private String errorReason;

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

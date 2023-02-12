package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 8:00 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("PROCESSING_STATUS")
public class PROCESSING_STATUS
{
    @XStreamAlias("GuWID")
    String guWID;

    @XStreamAlias("AuthorizationCode")
    String authorizationCode;

    @XStreamAlias("Info")
    String info;

    @XStreamAlias("StatusType")
    String statusType;

    @XStreamAlias("ERROR")
    ERROR error;

    @XStreamAlias("FunctionResult")
    String functionResult;

    @XStreamAlias("TimeStamp")
    String timeStamp;

    public String getAuthorizationCode()
    {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        this.authorizationCode = authorizationCode;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String getStatusType()
    {
        return statusType;
    }

    public void setStatusType(String statusType)
    {
        this.statusType = statusType;
    }

    public String getGuWID()
    {
        return guWID;
    }

    public void setGuWID(String guWID)
    {
        this.guWID = guWID;
    }

    public String getFunctionResult()
    {
        return functionResult;
    }

    public void setFunctionResult(String functionResult)
    {
        this.functionResult = functionResult;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public ERROR getError()
    {
        return error;
    }

    public void setError(ERROR error)
    {
        this.error = error;
    }
}

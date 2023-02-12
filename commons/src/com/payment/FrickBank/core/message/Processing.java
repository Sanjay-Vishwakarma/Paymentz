package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Processing
{
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getTimestamp()
    {
        return Timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        Timestamp = timestamp;
    }

    public String getResult()
    {
        return Result;
    }

    public void setResult(String result)
    {
        Result = result;
    }

    @XStreamAsAttribute
    private String code="";
    @XStreamAlias("Timestamp")
    private String Timestamp="";
    @XStreamAlias("Result")
    private String Result="";

    public Status getStatus()
    {
        return Status;
    }

    public void setStatus(Status status)
    {
        Status = status;
    }

    public Reason getReason()
    {
        return Reason;
    }

    public void setReason(Reason reason)
    {
        Reason = reason;
    }

    public Return getReturn()
    {
        return Return;
    }

    public void setReturn(Return aReturn)
    {
        Return = aReturn;
    }

    @XStreamAlias("Status")
    private Status Status;
    @XStreamAlias("Reason")
    private Reason Reason;
    @XStreamAlias("Return")
    private Return Return;

    public ResponseRisk getRisk()
    {
        return risk;
    }

    public void setRisk(ResponseRisk risk)
    {
        this.risk = risk;
    }

    @XStreamAlias("Risk")
    private ResponseRisk risk;
}

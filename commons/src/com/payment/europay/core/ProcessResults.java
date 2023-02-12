package com.payment.europay.core;

import com.payment.common.core.CommResponseVO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/7/13
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("processResults")
public class ProcessResults
{
    @XStreamAlias("deltaMsec")
    @XStreamAsAttribute
    String deltaMsec;

    public String getMcTxId()
    {
        return mcTxId;
    }

    public void setMcTxId(String mcTxId)
    {
        this.mcTxId = mcTxId;
    }

    @XStreamAlias("mcTxId")
    @XStreamAsAttribute
    String mcTxId = "1234";

    public String getTxId()
    {
        return txId;
    }

    public void setTxId(String txId)
    {
        this.txId = txId;
    }

    @XStreamAlias("txId")
    @XStreamAsAttribute
    String txId;

    @XStreamAlias("endStamp")
    @XStreamAsAttribute
    String endStamp;

    @XStreamAlias("endDate")
    @XStreamAsAttribute
    String endDate;

    @XStreamAlias("startStamp")
    @XStreamAsAttribute
    String startStamp;

    @XStreamAlias("startDate")
    @XStreamAsAttribute
    String startDate;

    @XStreamAlias("message")
    @XStreamAsAttribute
    String message;

    @XStreamAlias("code")
    @XStreamAsAttribute
    String code;

    public String getDeltaMsec()
    {
        return deltaMsec;
    }

    public void setDeltaMsec(String deltaMsec)
    {
        this.deltaMsec = deltaMsec;
    }

    public String getEndStamp()
    {
        return endStamp;
    }

    public void setEndStamp(String endStamp)
    {
        this.endStamp = endStamp;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getStartStamp()
    {
        return startStamp;
    }

    public void setStartStamp(String startStamp)
    {
        this.startStamp = startStamp;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMcTxDate()
    {
        return mcTxDate;
    }

    public void setMcTxDate(String mcTxDate)
    {
        this.mcTxDate = mcTxDate;
    }

    @XStreamAlias("mcTxDate")
    @XStreamAsAttribute
    String mcTxDate;

}

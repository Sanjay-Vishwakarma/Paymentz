package com.payment.europay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 8/17/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("transactionLog")
public class TransactionLog
{
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLog()
    {
        return log;
    }

    public void setLog(String log)
    {
        this.log = log;
    }

    public String getLogId()
    {
        return logId;
    }

    public void setLogId(String logId)
    {
        this.logId = logId;
    }

    @XStreamAlias("type")
    @XStreamAsAttribute
    String type;

    @XStreamAsAttribute
    @XStreamAlias("log")
    String log;

    @XStreamAsAttribute
    @XStreamAlias("logId")
    String logId;
}

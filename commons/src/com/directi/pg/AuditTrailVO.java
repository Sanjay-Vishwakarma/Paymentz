package com.directi.pg;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: 12/12/14
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuditTrailVO
{
    String actionExecutorId;
    String actionExecutorName;
    String cbReason;

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }


    public String getCbReason()
    {
        return cbReason;
    }

    public void setCbReason(String cbReason)
    {
        this.cbReason = cbReason;
    }
}

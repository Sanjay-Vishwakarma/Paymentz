package com.manager.vo;

/**
 * Created by Admin on 10/10/2015.
 */
public class AgentCommissionVO extends CommissionVO
{
    String agentId;

    String actionExecutorId;
    String actionExecutorName;

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

    public String getAgentId()
    {
        return agentId;
    }
    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

}

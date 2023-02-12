package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/10/14
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentDetailsVO
{
    String agentId;
    String agentName;
    String contactPerson;

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getAgentName()
    {
        return agentName;
    }

    public void setAgentName(String agentName)
    {
        this.agentName = agentName;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }
}

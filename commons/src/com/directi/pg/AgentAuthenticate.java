package com.directi.pg;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 2/19/13
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class AgentAuthenticate implements Serializable
{
    public int agentid = -9999;
    public String activation = null;
    public String authenticate = null;
    public String address = null;
    public String telno = null;
    public String contactemails = null;
    public boolean isAgentInterface;
    public String template=null;
    public String agentname=null;
    public String logoname=null;
    public String partnerid = null;
}

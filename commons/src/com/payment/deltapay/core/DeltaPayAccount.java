package com.payment.deltapay.core;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 11/8/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeltaPayAccount
{
       int id;
       int accountid;
       String affiliate;
       String room_name;
       String agent_name;
       boolean live;


    public DeltaPayAccount(ResultSet rs) throws SQLException
    {

        id = rs.getInt("id");
        accountid = rs.getInt("accountid");
        affiliate = rs.getString("affiliate");
        room_name = rs.getString("room_name");
        agent_name = rs.getString("agent_name");
        live = rs.getBoolean("isLive");

    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAccountid()
    {
        return accountid;
    }

    public void setAccountid(int accountid)
    {
        this.accountid = accountid;
    }

    public String getAffiliate()
    {
        return affiliate;
    }

    public void setAffiliate(String affiliate)
    {
        this.affiliate = affiliate;
    }

    public String getRoom_name()
    {
        return room_name;
    }

    public void setRoom_name(String room_name)
    {
        this.room_name = room_name;
    }

    public String getAgent_name()
    {
        return agent_name;
    }

    public void setAgent_name(String agent_name)
    {
        this.agent_name = agent_name;
    }

    public boolean isLive()
    {
        return live;
    }

    public void setLive(boolean live)
    {
        this.live = live;
    }
}

package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericDeviceDetailsVO;

/**
 * Created by Admin on 12/20/2019.
 */
public class CommDeviceDetailsVO extends GenericDeviceDetailsVO
{
    String user_Agent;

    public String getUser_Agent()
    {
        return user_Agent;
    }

    public void setUser_Agent(String user_Agent)
    {
        this.user_Agent = user_Agent;
    }
}

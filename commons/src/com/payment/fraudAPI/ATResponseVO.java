package com.payment.fraudAPI;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/16/14
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATResponseVO
{

    Integer status;

    String description;

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}

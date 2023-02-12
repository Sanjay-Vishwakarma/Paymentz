package com.manager.vo;

/**
 * Created by Admin on 9/28/2020.
 */
public class ActivityTrackerVOs
{
    String id;
    String Interface;
    String user_name;
    String role;
    String action;
    String module_name;
    String lable_values;
    String description;
    String ip;
    String header;
    String partnerId;

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getInterface()
    {
        return Interface;
    }

    public void setInterface(String anInterface)
    {
        Interface = anInterface;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getModule_name()
    {
        return module_name;
    }

    public void setModule_name(String module_name)
    {
        this.module_name = module_name;
    }

    public String getLable_values()
    {
        return lable_values;
    }

    public void setLable_values(String lable_values)
    {
        this.lable_values = lable_values;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }


}

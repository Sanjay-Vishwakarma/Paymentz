package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="merchantManagement")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantManagement
{
    @XmlElement(name="userManagement")
    private String userManagement;

    public String getUserManagement()
    {
        return userManagement;
    }

    public void setUserManagement(String userManagement)
    {
        this.userManagement = userManagement;
    }

    @Override
    public String toString()
    {
        return "MerchantManagement{" +
                "userManagement='" + userManagement + '\'' +
                '}';
    }
}

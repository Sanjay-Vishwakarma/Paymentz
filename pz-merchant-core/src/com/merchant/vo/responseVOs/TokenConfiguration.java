package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="tokenConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenConfiguration
{
    @XmlElement(name="isTokenizationAllowed")
    private String isTokenizationAllowed;

    @XmlElement(name="isAddressDetailsRequired")
    private String isAddressDetailsRequired;

    @XmlElement(name="isCardEncryptionEnable")
    private String isCardEncryptionEnable;

    public String getIsTokenizationAllowed()
    {
        return isTokenizationAllowed;
    }

    public void setIsTokenizationAllowed(String isTokenizationAllowed)
    {
        this.isTokenizationAllowed = isTokenizationAllowed;
    }

    public String getIsAddressDetailsRequired()
    {
        return isAddressDetailsRequired;
    }

    public void setIsAddressDetailsRequired(String isAddressDetailsRequired)
    {
        this.isAddressDetailsRequired = isAddressDetailsRequired;
    }

    public String getIsCardEncryptionEnable()
    {
        return isCardEncryptionEnable;
    }

    public void setIsCardEncryptionEnable(String isCardEncryptionEnable)
    {
        this.isCardEncryptionEnable = isCardEncryptionEnable;
    }

    @Override
    public String toString()
    {
        return "TokenConfiguration{" +
                "isTokenizationAllowed='" + isTokenizationAllowed + '\'' +
                ", isAddressDetailsRequired='" + isAddressDetailsRequired + '\'' +
                ", isCardEncryptionEnable='" + isCardEncryptionEnable + '\'' +
                '}';
    }
}

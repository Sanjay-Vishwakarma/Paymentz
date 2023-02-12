package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="generalConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneralConfiguration
{
    @XmlElement(name="isActivation")
    private String isActivation;

    @XmlElement(name="hasPaid")
    private String hasPaid;

    @XmlElement(name="partnerId")
    private String partnerId;

    @XmlElement(name="agentId")
    private String agentId;

    @XmlElement(name="isMerchantInterfaceAccess")
    private String isMerchantInterfaceAccess;

    @XmlElement(name="blacklistTransactions")
    private String blacklistTransactions;

    @XmlElement(name="flightMode")
    private String flightMode;

    @XmlElement(name="isExcessCaptureAllowed")
    private String isExcessCaptureAllowed;

    public String getIsActivation()
    {
        return isActivation;
    }

    public void setIsActivation(String isActivation)
    {
        this.isActivation = isActivation;
    }

    public String getHasPaid()
    {
        return hasPaid;
    }

    public void setHasPaid(String hasPaid)
    {
        this.hasPaid = hasPaid;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getIsMerchantInterfaceAccess()
    {
        return isMerchantInterfaceAccess;
    }

    public void setIsMerchantInterfaceAccess(String isMerchantInterfaceAccess)
    {
        this.isMerchantInterfaceAccess = isMerchantInterfaceAccess;
    }

    public String getBlacklistTransactions()
    {
        return blacklistTransactions;
    }

    public void setBlacklistTransactions(String blacklistTransactions)
    {
        this.blacklistTransactions = blacklistTransactions;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
    }

    public String getIsExcessCaptureAllowed()
    {
        return isExcessCaptureAllowed;
    }

    public void setIsExcessCaptureAllowed(String isExcessCaptureAllowed)
    {
        this.isExcessCaptureAllowed = isExcessCaptureAllowed;
    }

    @Override
    public String toString()
    {
        return "GeneralConfiguration{" +
                "isActivation='" + isActivation + '\'' +
                ", hasPaid='" + hasPaid + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", isMerchantInterfaceAccess='" + isMerchantInterfaceAccess + '\'' +
                ", blacklistTransactions='" + blacklistTransactions + '\'' +
                ", flightMode='" + flightMode + '\'' +
                ", isExcessCaptureAllowed='" + isExcessCaptureAllowed + '\'' +
                '}';
    }
}

package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="tokenManagement")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenManagement
{
    @XmlElement(name="registrationHistory")
    private String registrationHistory;

    @XmlElement(name="registerCard")
    private String registerCard;

    public String getRegistrationHistory()
    {
        return registrationHistory;
    }

    public void setRegistrationHistory(String registrationHistory)
    {
        this.registrationHistory = registrationHistory;
    }

    public String getRegisterCard()
    {
        return registerCard;
    }

    public void setRegisterCard(String registerCard)
    {
        this.registerCard = registerCard;
    }

    @Override
    public String toString()
    {
        return "TokenManagement{" +
                "registrationHistory='" + registrationHistory + '\'' +
                ", registerCard='" + registerCard + '\'' +
                '}';
    }
}

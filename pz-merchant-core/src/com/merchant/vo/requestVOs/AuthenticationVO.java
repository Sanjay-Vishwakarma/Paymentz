package com.merchant.vo.requestVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 10-07-2017.
 */
@XmlRootElement(name="authentication")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthenticationVO
{


    @XmlElement(name="memberId")
    String memberId;


    @XmlElement(name="password")
    String password;


    @XmlElement(name="checksum")
    String checksum;


    @XmlElement(name="terminalId")
    String terminalId;


    @XmlElement(name="partnerId")
    String partnerId;


    @XmlElement(name="authToken")
    String authToken;


    @XmlElement(name="random")
    String random;

    @XmlElement(name="mode")
    String mode;

    @XmlElement(name="sKey")
    String sKey;


    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getRandom()
    {
        return random;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getsKey()
    {
        return sKey;
    }

    public void setsKey(String sKey)
    {
        this.sKey = sKey;
    }
}

package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
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

    @XmlElement(name="sKey")
    String sKey;

    @XmlElement(name="accountId")
    String accountId;


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

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getsKey()
    {
        return sKey;
    }

    public void setsKey(String sKey)
    {
        this.sKey = sKey;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }
}

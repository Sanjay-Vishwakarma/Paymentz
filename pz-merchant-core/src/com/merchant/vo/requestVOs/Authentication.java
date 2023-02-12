package com.merchant.vo.requestVOs;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 9/1/2016.
 */
@XmlRootElement(name="authentication")
@XmlAccessorType(XmlAccessType.FIELD)
public class Authentication
{
    @FormParam("authentication.memberId")
    String memberId;

    @FormParam("authentication.password")
    String password;

    @FormParam("authentication.checksum")
    String checksum;

    @FormParam("authentication.terminalId")
    String terminalId;

    @FormParam("authentication.partnerId")
    String partnerId;

    @FormParam("authentication.authToken")
    String authToken;

    @FormParam("authentication.sKey")
    String sKey;

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
}

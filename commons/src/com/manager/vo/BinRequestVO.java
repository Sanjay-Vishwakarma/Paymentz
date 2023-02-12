package com.manager.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Trupti on 7/25/2017.
 */
@XmlRootElement(name="binRequestVO")
@XmlAccessorType(XmlAccessType.FIELD)
public class BinRequestVO
{
    @XmlElement(name="firstsix")
    String firstsix;

    @XmlElement(name="username")
    String username;

    @XmlElement(name="password")
    String password;

    @XmlElement(name="userid")
    String userid;

    @XmlElement(name="random")
    String random;

    @XmlElement(name="checksum")
    String checksum;


    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getRandom()
    {
        return random;
    }

    public void setRandom(String random)
    {
        this.random = random;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }
}

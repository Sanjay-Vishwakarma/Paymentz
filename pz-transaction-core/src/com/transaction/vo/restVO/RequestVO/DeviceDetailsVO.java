package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Vivek on 12/20/2019.
 */
@XmlRootElement(name="deviceDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceDetailsVO
{
    @XmlElement(name="user_Agent")
    String user_Agent;

    @XmlElement(name="browserLanguage")
    String browserLanguage;

    @XmlElement(name="browserTimezoneOffset")
    String browserTimezoneOffset;

    @XmlElement(name="browserColorDepth")
    String browserColorDepth;

    @XmlElement(name="browserAcceptHeader")
    String browserAcceptHeader;

    @XmlElement(name="browserScreenHeight")
    String browserScreenHeight;

    @XmlElement(name="browserScreenWidth")
    String browserScreenWidth;

    @XmlElement(name="browserJavaEnabled")
    String browserJavaEnabled;

    public String getUser_Agent()
    {
        return user_Agent;
    }

    public void setUser_Agent(String user_Agent)
    {
        this.user_Agent = user_Agent;
    }

    public String getBrowserLanguage()
    {
        return browserLanguage;
    }

    public void setBrowserLanguage(String browserLanguage)
    {
        this.browserLanguage = browserLanguage;
    }

    public String getBrowserTimezoneOffset()
    {
        return browserTimezoneOffset;
    }

    public void setBrowserTimezoneOffset(String browserTimezoneOffset)
    {
        this.browserTimezoneOffset = browserTimezoneOffset;
    }

    public String getBrowserColorDepth()
    {
        return browserColorDepth;
    }

    public void setBrowserColorDepth(String browserColorDepth)
    {
        this.browserColorDepth = browserColorDepth;
    }

    public String getBrowserAcceptHeader()
    {
        return browserAcceptHeader;
    }

    public void setBrowserAcceptHeader(String browserAcceptHeader)
    {
        this.browserAcceptHeader = browserAcceptHeader;
    }

    public String getBrowserScreenHeight()
    {
        return browserScreenHeight;
    }

    public void setBrowserScreenHeight(String browserScreenHeight)
    {
        this.browserScreenHeight = browserScreenHeight;
    }

    public String getBrowserScreenWidth()
    {
        return browserScreenWidth;
    }

    public void setBrowserScreenWidth(String browserScreenWidth)
    {
        this.browserScreenWidth = browserScreenWidth;
    }

    public String getBrowserJavaEnabled()
    {
        return browserJavaEnabled;
    }

    public void setBrowserJavaEnabled(String browserJavaEnabled)
    {
        this.browserJavaEnabled = browserJavaEnabled;
    }
}

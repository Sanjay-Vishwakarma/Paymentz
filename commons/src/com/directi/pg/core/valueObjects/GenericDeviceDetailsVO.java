package com.directi.pg.core.valueObjects;

/**
 * Created by Admin on 12/20/2019.
 */
public class GenericDeviceDetailsVO extends GenericVO
{
    String user_Agent;
    String acceptHeader;
    String fingerprints;
    String browserLanguage;
    String browserTimezoneOffset;
    String browserColorDepth;
    String browserScreenHeight;
    String browserScreenWidth;
    String browserJavaEnabled;

    public String getUser_Agent()
    {
        return user_Agent;
    }

    public void setUser_Agent(String user_Agent)
    {
        this.user_Agent = user_Agent;
    }

    public String getAcceptHeader()
    {
        return acceptHeader;
    }

    public void setAcceptHeader(String acceptHeader)
    {
        this.acceptHeader = acceptHeader;
    }

    public String getFingerprints()
    {
        return fingerprints;
    }

    public void setFingerprints(String fingerprints)
    {
        this.fingerprints = fingerprints;
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

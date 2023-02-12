package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="whitelistingConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class WhitelistingConfiguration
{
    @XmlElement(name="isCardWhitelisted")
    private String isCardWhitelisted;

    @XmlElement(name="isIpWhitelisted")
    private String isIpWhitelisted;

    @XmlElement(name="isIPWhitelistedForAPIs")
    private String isIPWhitelistedForAPIs;

    @XmlElement(name="isDomainWhitelisted")
    private String isDomainWhitelisted;

    public String getIsCardWhitelisted()
    {
        return isCardWhitelisted;
    }

    public void setIsCardWhitelisted(String isCardWhitelisted)
    {
        this.isCardWhitelisted = isCardWhitelisted;
    }

    public String getIsIpWhitelisted()
    {
        return isIpWhitelisted;
    }

    public void setIsIpWhitelisted(String isIpWhitelisted)
    {
        this.isIpWhitelisted = isIpWhitelisted;
    }

    public String getIsIPWhitelistedForAPIs()
    {
        return isIPWhitelistedForAPIs;
    }

    public void setIsIPWhitelistedForAPIs(String isIPWhitelistedForAPIs)
    {
        this.isIPWhitelistedForAPIs = isIPWhitelistedForAPIs;
    }

    public String getIsDomainWhitelisted()
    {
        return isDomainWhitelisted;
    }

    public void setIsDomainWhitelisted(String isDomainWhitelisted)
    {
        this.isDomainWhitelisted = isDomainWhitelisted;
    }

    @Override
    public String toString()
    {
        return "WhitelistingConfiguration{" +
                "isCardWhitelisted='" + isCardWhitelisted + '\'' +
                ", isIpWhitelisted='" + isIpWhitelisted + '\'' +
                ", isIPWhitelistedForAPIs='" + isIPWhitelistedForAPIs + '\'' +
                ", isDomainWhitelisted='" + isDomainWhitelisted + '\'' +
                '}';
    }
}

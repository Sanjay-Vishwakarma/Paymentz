package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings
{
    @XmlElement(name="merchantProfile")
    private String merchantProfile;

    @XmlElement(name="organisationProfile")
    private String organisationProfile;

    @XmlElement(name="generateKey")
    private String generateKey;

    @XmlElement(name="merchantConfiguration")
    private String merchantConfiguration;

    @XmlElement(name="fraudRuleConfiguration")
    private String fraudRuleConfiguration;

    @XmlElement(name="whitelistDetails")
    private String whitelistDetails;

    @XmlElement(name="blockDetails")
    private String blockDetails;

    public String getMerchantProfile()
    {
        return merchantProfile;
    }

    public void setMerchantProfile(String merchantProfile)
    {
        this.merchantProfile = merchantProfile;
    }

    public String getOrganisationProfile()
    {
        return organisationProfile;
    }

    public void setOrganisationProfile(String organisationProfile)
    {
        this.organisationProfile = organisationProfile;
    }

    public String getGenerateKey()
    {
        return generateKey;
    }

    public void setGenerateKey(String generateKey)
    {
        this.generateKey = generateKey;
    }

    public String getMerchantConfiguration()
    {
        return merchantConfiguration;
    }

    public void setMerchantConfiguration(String merchantConfiguration)
    {
        this.merchantConfiguration = merchantConfiguration;
    }

    public String getFraudRuleConfiguration()
    {
        return fraudRuleConfiguration;
    }

    public void setFraudRuleConfiguration(String fraudRuleConfiguration)
    {
        this.fraudRuleConfiguration = fraudRuleConfiguration;
    }

    public String getWhitelistDetails()
    {
        return whitelistDetails;
    }

    public void setWhitelistDetails(String whitelistDetails)
    {
        this.whitelistDetails = whitelistDetails;
    }

    public String getBlockDetails()
    {
        return blockDetails;
    }

    public void setBlockDetails(String blockDetails)
    {
        this.blockDetails = blockDetails;
    }

    @Override
    public String toString()
    {
        return "Settings{" +
                "merchantProfile='" + merchantProfile + '\'' +
                ", organisationProfile='" + organisationProfile + '\'' +
                ", generateKey='" + generateKey + '\'' +
                ", merchantConfiguration='" + merchantConfiguration + '\'' +
                ", fraudRuleConfiguration='" + fraudRuleConfiguration + '\'' +
                ", whitelistDetails='" + whitelistDetails + '\'' +
                ", blockDetails='" + blockDetails + '\'' +
                '}';
    }
}

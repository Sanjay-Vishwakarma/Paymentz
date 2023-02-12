package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="transactionConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionConfiguration
{
    @XmlElement(name="isService")
    private String isService;

    @XmlElement(name="autoRedirect")
    private String autoRedirect;

    @XmlElement(name="vbv")
    private String vbv;

    @XmlElement(name="masterCardSupported")
    private String masterCardSupported;

    @XmlElement(name="autoSelectTerminal")
    private String autoSelectTerminal;

    @XmlElement(name="isPODRequired")
    private String isPODRequired;

    @XmlElement(name="isRestrictedTicket")
    private String isRestrictedTicket;

    @XmlElement(name="emiSupport")
    private String emiSupport;

    @XmlElement(name="isEmailLimitEnabled")
    private String isEmailLimitEnabled;

    @XmlElement(name="binService")
    private String binService;

    @XmlElement(name="supportSection")
    private String supportSection;

    @XmlElement(name="supportNumberNeeded")
    private String supportNumberNeeded;

    @XmlElement(name="cardWhitelistLevel")
    private String cardWhitelistLevel;

    @XmlElement(name="multiCurrencySupport")
    private String multiCurrencySupport;

    @XmlElement(name="ipValidationRequired")
    private String ipValidationRequired;

    @XmlElement(name="binRouting")
    private String binRouting;

    @XmlElement(name="personalInfoValidation")
    private String personalInfoValidation;

    @XmlElement(name="personalInfoDisplay")
    private String personalInfoDisplay;

    @XmlElement(name="restCheckoutPage")
    private String restCheckoutPage;

    @XmlElement(name="merchantOrderDetails")
    private String merchantOrderDetails;

    @XmlElement(name="marketPlace")
    private String marketPlace;

    @XmlElement(name="isUniqueOrderIdRequired")
    private String isUniqueOrderIdRequired;

    @XmlElement(name="cardExpiryDateCheck")
    private String cardExpiryDateCheck;

    @XmlElement(name="isOtpRequired")
    private String isOtpRequired;

    @XmlElement(name="isCardStorageRequired")
    private String isCardStorageRequired;

    public String getMultiCurrencySupport()
    {
        return multiCurrencySupport;
    }

    public void setMultiCurrencySupport(String multiCurrencySupport)
    {
        this.multiCurrencySupport = multiCurrencySupport;
    }

    public String getIsService()
    {
        return isService;
    }

    public void setIsService(String isService)
    {
        this.isService = isService;
    }

    public String getAutoRedirect()
    {
        return autoRedirect;
    }

    public void setAutoRedirect(String autoRedirect)
    {
        this.autoRedirect = autoRedirect;
    }

    public String getVbv()
    {
        return vbv;
    }

    public void setVbv(String vbv)
    {
        this.vbv = vbv;
    }

    public String getMasterCardSupported()
    {
        return masterCardSupported;
    }

    public void setMasterCardSupported(String masterCardSupported)
    {
        this.masterCardSupported = masterCardSupported;
    }

    public String getAutoSelectTerminal()
    {
        return autoSelectTerminal;
    }

    public void setAutoSelectTerminal(String autoSelectTerminal)
    {
        this.autoSelectTerminal = autoSelectTerminal;
    }

    public String getIsPODRequired()
    {
        return isPODRequired;
    }

    public void setIsPODRequired(String isPODRequired)
    {
        this.isPODRequired = isPODRequired;
    }

    public String getIsRestrictedTicket()
    {
        return isRestrictedTicket;
    }

    public void setIsRestrictedTicket(String isRestrictedTicket)
    {
        this.isRestrictedTicket = isRestrictedTicket;
    }

    public String getEmiSupport()
    {
        return emiSupport;
    }

    public void setEmiSupport(String emiSupport)
    {
        this.emiSupport = emiSupport;
    }

    public String getIsEmailLimitEnabled()
    {
        return isEmailLimitEnabled;
    }

    public void setIsEmailLimitEnabled(String isEmailLimitEnabled)
    {
        this.isEmailLimitEnabled = isEmailLimitEnabled;
    }

    public String getBinService()
    {
        return binService;
    }

    public void setBinService(String binService)
    {
        this.binService = binService;
    }

    public String getSupportNumberNeeded()
    {
        return supportNumberNeeded;
    }

    public void setSupportNumberNeeded(String supportNumberNeeded)
    {
        this.supportNumberNeeded = supportNumberNeeded;
    }

    public String getCardWhitelistLevel()
    {
        return cardWhitelistLevel;
    }

    public void setCardWhitelistLevel(String cardWhitelistLevel)
    {
        this.cardWhitelistLevel = cardWhitelistLevel;
    }

    public String getIpValidationRequired()
    {
        return ipValidationRequired;
    }

    public void setIpValidationRequired(String ipValidationRequired)
    {
        this.ipValidationRequired = ipValidationRequired;
    }

    public String getBinRouting()
    {
        return binRouting;
    }

    public void setBinRouting(String binRouting)
    {
        this.binRouting = binRouting;
    }

    public String getPersonalInfoValidation()
    {
        return personalInfoValidation;
    }

    public void setPersonalInfoValidation(String personalInfoValidation)
    {
        this.personalInfoValidation = personalInfoValidation;
    }

    public String getPersonalInfoDisplay()
    {
        return personalInfoDisplay;
    }

    public void setPersonalInfoDisplay(String personalInfoDisplay)
    {
        this.personalInfoDisplay = personalInfoDisplay;
    }

    public String getRestCheckoutPage()
    {
        return restCheckoutPage;
    }

    public void setRestCheckoutPage(String restCheckoutPage)
    {
        this.restCheckoutPage = restCheckoutPage;
    }

    public String getMerchantOrderDetails()
    {
        return merchantOrderDetails;
    }

    public void setMerchantOrderDetails(String merchantOrderDetails)
    {
        this.merchantOrderDetails = merchantOrderDetails;
    }

    public String getMarketPlace()
    {
        return marketPlace;
    }

    public void setMarketPlace(String marketPlace)
    {
        this.marketPlace = marketPlace;
    }

    public String getIsUniqueOrderIdRequired()
    {
        return isUniqueOrderIdRequired;
    }

    public void setIsUniqueOrderIdRequired(String isUniqueOrderIdRequired)
    {
        this.isUniqueOrderIdRequired = isUniqueOrderIdRequired;
    }

    public String getCardExpiryDateCheck()
    {
        return cardExpiryDateCheck;
    }

    public void setCardExpiryDateCheck(String cardExpiryDateCheck)
    {
        this.cardExpiryDateCheck = cardExpiryDateCheck;
    }

    public String getIsOtpRequired()
    {
        return isOtpRequired;
    }

    public void setIsOtpRequired(String isOtpRequired)
    {
        this.isOtpRequired = isOtpRequired;
    }

    public String getIsCardStorageRequired()
    {
        return isCardStorageRequired;
    }

    public void setIsCardStorageRequired(String isCardStorageRequired)
    {
        this.isCardStorageRequired = isCardStorageRequired;
    }

    public String getSupportSection()
    {
        return supportSection;
    }

    public void setSupportSection(String supportSection)
    {
        this.supportSection = supportSection;
    }

    @Override
    public String toString()
    {
        return "TransactionConfiguration{" +
                "isService='" + isService + '\'' +
                ", autoRedirect='" + autoRedirect + '\'' +
                ", vbv='" + vbv + '\'' +
                ", masterCardSupported='" + masterCardSupported + '\'' +
                ", autoSelectTerminal='" + autoSelectTerminal + '\'' +
                ", isPODRequired='" + isPODRequired + '\'' +
                ", isRestrictedTicket='" + isRestrictedTicket + '\'' +
                ", emiSupport='" + emiSupport + '\'' +
                ", isEmailLimitEnabled='" + isEmailLimitEnabled + '\'' +
                ", binService='" + binService + '\'' +
                ", supportSection='" + supportSection + '\'' +
                ", supportNumberNeeded='" + supportNumberNeeded + '\'' +
                ", cardWhitelistLevel='" + cardWhitelistLevel + '\'' +
                ", multiCurrencySupport='" + multiCurrencySupport + '\'' +
                ", ipValidationRequired='" + ipValidationRequired + '\'' +
                ", binRouting='" + binRouting + '\'' +
                ", personalInfoValidation='" + personalInfoValidation + '\'' +
                ", personalInfoDisplay='" + personalInfoDisplay + '\'' +
                ", restCheckoutPage='" + restCheckoutPage + '\'' +
                ", merchantOrderDetails='" + merchantOrderDetails + '\'' +
                ", marketPlace='" + marketPlace + '\'' +
                ", isUniqueOrderIdRequired='" + isUniqueOrderIdRequired + '\'' +
                ", cardExpiryDateCheck='" + cardExpiryDateCheck + '\'' +
                ", isOtpRequired='" + isOtpRequired + '\'' +
                ", isCardStorageRequired='" + isCardStorageRequired + '\'' +
                '}';
    }
}

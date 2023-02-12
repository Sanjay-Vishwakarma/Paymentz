package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="invoizerConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoizerConfiguration
{

    @XmlElement(name="isVirtualCheckoutAllowed")
    private String isVirtualCheckoutAllowed;

    @XmlElement(name="isPhoneRequired")
    private String isPhoneRequired;

    @XmlElement(name="isEmailRequired")
    private String isEmailRequired;

    @XmlElement(name="isShareAllowed")
    private String isShareAllowed;

    @XmlElement(name="isSignatureAllowed")
    private String isSignatureAllowed;

    @XmlElement(name="isSaveReceiptAllowed")
    private String isSaveReceiptAllowed;

    @XmlElement(name="defaultLanguage")
    private String defaultLanguage;

    public String getIsVirtualCheckoutAllowed()
    {
        return isVirtualCheckoutAllowed;
    }

    public void setIsVirtualCheckoutAllowed(String isVirtualCheckoutAllowed)
    {
        this.isVirtualCheckoutAllowed = isVirtualCheckoutAllowed;
    }

    public String getIsPhoneRequired()
    {
        return isPhoneRequired;
    }

    public void setIsPhoneRequired(String isPhoneRequired)
    {
        this.isPhoneRequired = isPhoneRequired;
    }

    public String getIsEmailRequired()
    {
        return isEmailRequired;
    }

    public void setIsEmailRequired(String isEmailRequired)
    {
        this.isEmailRequired = isEmailRequired;
    }

    public String getIsShareAllowed()
    {
        return isShareAllowed;
    }

    public void setIsShareAllowed(String isShareAllowed)
    {
        this.isShareAllowed = isShareAllowed;
    }

    public String getIsSignatureAllowed()
    {
        return isSignatureAllowed;
    }

    public void setIsSignatureAllowed(String isSignatureAllowed)
    {
        this.isSignatureAllowed = isSignatureAllowed;
    }

    public String getIsSaveReceiptAllowed()
    {
        return isSaveReceiptAllowed;
    }

    public void setIsSaveReceiptAllowed(String isSaveReceiptAllowed)
    {
        this.isSaveReceiptAllowed = isSaveReceiptAllowed;
    }

    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage)
    {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public String toString()
    {
        return "InvoizerConfiguration{" +
                "isVirtualCheckoutAllowed='" + isVirtualCheckoutAllowed + '\'' +
                ", isPhoneRequired='" + isPhoneRequired + '\'' +
                ", isEmailRequired='" + isEmailRequired + '\'' +
                ", isShareAllowed='" + isShareAllowed + '\'' +
                ", isSignatureAllowed='" + isSignatureAllowed + '\'' +
                ", isSaveReceiptAllowed='" + isSaveReceiptAllowed + '\'' +
                ", defaultLanguage='" + defaultLanguage + '\'' +
                '}';
    }
}

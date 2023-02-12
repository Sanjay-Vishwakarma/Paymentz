package com.manager.vo.userProfileVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 22/8/15.
 */
@XmlRootElement(name="userSetting")
@XmlAccessorType(XmlAccessType.FIELD)
public class MerchantVO
{
    @XmlElement(name = "memberId")
    private String memberId;

    @XmlElement(name = "key")
    private String key;

    @XmlElement(name = "currency")
    private String currency;

    @XmlElement(name = "companyName")
    private String companyName;

    @XmlElement(name = "contactEmail")
    private String contactEmail;

    @XmlElement(name = "addressVerification")
    private String addressVerification;

    @XmlElement(name = "autoRedirect")
    private String autoRedirect;

    @XmlElement(name = "defaultMode")
    private String defaultMode;

    @XmlElement(name = "riskProfile")
    private String riskProfile;

    @XmlElement(name = "businessProfile")
    private String businessProfile;

    @XmlElement(name = "offlineTransactionURL")
    private String offlineTransactionURL;

    @XmlElement(name = "onlineTransactionURL")
    private String onlineTransactionURL;

    @XmlElement(name = "onlineThreshold")
    private int onlineThreshold;

    @XmlElement(name = "offlineThreshold")
    private int offlineThreshold;

    @XmlElement(name = "partnerId")
    private String partnerId;

    @XmlElement(name = "addressDetailDisplay")
    private String addressDetailDisplay;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddressVerification() {
        return addressVerification;
    }

    public void setAddressVerification(String addressVerification) {
        this.addressVerification = addressVerification;
    }

    public String getAutoRedirect() {
        return autoRedirect;
    }

    public void setAutoRedirect(String autoRedirect) {
        this.autoRedirect = autoRedirect;
    }

    public String getDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(String defaultMode) {
        this.defaultMode = defaultMode;
    }

    public String getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(String riskProfile) {
        this.riskProfile = riskProfile;
    }

    public String getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(String businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getOfflineTransactionURL() {
        return offlineTransactionURL;
    }

    public void setOfflineTransactionURL(String offlineTransactionURL) {
        this.offlineTransactionURL = offlineTransactionURL;
    }

    public String getOnlineTransactionURL() {
        return onlineTransactionURL;
    }

    public void setOnlineTransactionURL(String onlineTransactionURL) {
        this.onlineTransactionURL = onlineTransactionURL;
    }

    public int getOnlineThreshold() {
        return onlineThreshold;
    }

    public void setOnlineThreshold(int onlineThreshold) {
        this.onlineThreshold = onlineThreshold;
    }

    public int getOfflineThreshold() {
        return offlineThreshold;
    }

    public void setOfflineThreshold(int offlineThreshold) {
        this.offlineThreshold = offlineThreshold;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getAddressDetailDisplay()
    {
        return addressDetailDisplay;
    }

    public void setAddressDetailDisplay(String addressDetailDisplay)
    {
        this.addressDetailDisplay = addressDetailDisplay;
    }
}

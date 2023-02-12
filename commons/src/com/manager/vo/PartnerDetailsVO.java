package com.manager.vo;

import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 10/9/14
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDetailsVO extends CommonValidatorVO
{
    //please arrange the variables as if it is in the table
    String partnerId;
    String logoName;
    String companyName;
    String contactPerson;
    String activation;
    String template;
    String partnertemplate;
    String partnerName;
    String contact_emails;
    String address;
    String country;
    String telno;
    String isPharma ;
    String isValidateEmail;
    String daily_amount_limit;
    String monthly_amount_limit;
    String daily_card_limit;
    String weekly_card_limit;
    String monthly_card_limit;
    String check_limit;
    String fraudemailid;
    String isIpWhitelisted;
    String modify_merchant_details;
    String modify_company_details;
    String responseType;
    String responseLength;
    //String partnerEmailId;
    String supportMailId;
    String adminMailId;
    String salesMailId;
    String billingMailId;
    String notifyEmailId;
    String partnerKey;
    String isTokenizationAllowed;
    String isMerchantRequiredForCardRegistration;
    String isAddressRequiredForTokenTransaction;
    String isMerchantRequiredForCardholderRegistration;
    String isCardEncryptionEnable;
    String isIpWhiteListedCheckForAPIs;

    String mainContactName;
    String salesContactName;
    String billingContactName;
    String notifyContactName;
    String fraudContactName;
    String chargebackContactName;
    String refundContactName;
    String techContactName;



    String chargebackMailId;
    String refundMailId;
    String techMailId;
    String splitPaymentAllowed;

    String hostUrl;
    String city;
    String state;
    String zip;
    String faxNo;

    String reportingCurrency;
    String flightMode;
    String clkey;
    String ipWhitelistInvoice;
    String addressValidationInvoice;
    String addressvalidation;

    String defaultTheme;
    String currentTheme;

    String headPanelFont_color;
    String bodyPanelFont_color;
    String panelHeading_color;
    String panelBody_color;
    String templateBackGround_color;
    String bodyBgColor;
    String bodyFgColor;
    String navigationFontColor;
    String textboxColor;
    String iconColor;
    String timerColor;
    String boxShadow;
    String reportFileBGColor;
    String processorPartnerId;
    String monthlyMinCommissionModule;
    String partnerShortName;
    String profitShareCommissionModule;
    String NEW_CHECKOUT_FOOTER_FONT_COLOR="";
    String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR="";
    String privacyurl;
    String termsurl;
    String checkoutInvoice;
    String login;
    String exportTransactionCron;
    String organizationName;
    String supporturl;
    String documentationurl;
    String logoHeight;
    String logoWidth;
    String isVerifyMerchantotp;
    /*public String getPartnerEmailId()
    {
        return partnerEmailId;
    }

    public void setPartnerEmailId(String partnerEmailId)
    {
        this.partnerEmailId = partnerEmailId;
    }
*/

    public String getAddressvalidation()
    {
        return addressvalidation;
    }

    public void setAddressvalidation(String addressvalidation)
    {
        this.addressvalidation = addressvalidation;
    }

    public String getIpWhitelistInvoice()
    {
        return ipWhitelistInvoice;
    }

    public void setIpWhitelistInvoice(String ipWhitelistInvoice)
    {
        this.ipWhitelistInvoice = ipWhitelistInvoice;
    }

    public String getAddressValidationInvoice()
    {
        return addressValidationInvoice;
    }

    public void setAddressValidationInvoice(String addressValidationInvoice)
    {
        this.addressValidationInvoice = addressValidationInvoice;
    }

    public String getIsIpWhiteListedCheckForAPIs()
    {
        return isIpWhiteListedCheckForAPIs;
    }

    public void setIsIpWhiteListedCheckForAPIs(String isIpWhiteListedCheckForAPIs)
    {
        this.isIpWhiteListedCheckForAPIs = isIpWhiteListedCheckForAPIs;
    }

    public String getSupportMailId()
    {
        return supportMailId;
    }

    public void setSupportMailId(String suportMailId)
    {
        this.supportMailId = suportMailId;
    }

    public String getAdminMailId()
    {
        return adminMailId;
    }

    public void setAdminMailId(String adminMailId)
    {
        this.adminMailId = adminMailId;
    }

    public String getSalesMailId()
    {
        return salesMailId;
    }

    public void setSalesMailId(String salesMailId)
    {
        this.salesMailId = salesMailId;
    }

    public String getBillingMailId()
    {
        return billingMailId;
    }

    public void setBillingMailId(String billingMailId)
    {
        this.billingMailId = billingMailId;
    }

    public String getNotifyEmailId()
    {
        return notifyEmailId;
    }

    public void setNotifyEmailId(String notifyEmailId)
    {
        this.notifyEmailId = notifyEmailId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getLogoName()
    {
        return logoName;
    }

    public void setLogoName(String logoName)
    {
        this.logoName = logoName;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getActivation()
    {
        return activation;
    }

    public void setActivation(String activation)
    {
        this.activation = activation;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getContact_emails()
    {
        return contact_emails;
    }

    public void setContact_emails(String contact_emails)
    {
        this.contact_emails = contact_emails;
    }

    public String getChargebackMailId()
    {
        return chargebackMailId;
    }

    public void setChargebackMailId(String chargebackMailId)
    {
        this.chargebackMailId = chargebackMailId;
    }

    public String getRefundMailId()
    {
        return refundMailId;
    }

    public void setRefundMailId(String refundMailId)
    {
        this.refundMailId = refundMailId;
    }

    public String getTechMailId()
    {
        return techMailId;
    }

    public void setTechMailId(String techMailId)
    {
        this.techMailId = techMailId;
    }

    public String getMainContactName()
    {
        return mainContactName;
    }

    public void setMainContactName(String mainContactName)
    {
        this.mainContactName = mainContactName;
    }

    public String getSalesContactName()
    {
        return salesContactName;
    }

    public void setSalesContactName(String salesContactName)
    {
        this.salesContactName = salesContactName;
    }

    public String getBillingContactName()
    {
        return billingContactName;
    }

    public void setBillingContactName(String billingContactName)
    {
        this.billingContactName = billingContactName;
    }

    public String getNotifyContactName()
    {
        return notifyContactName;
    }

    public void setNotifyContactName(String notifyContactName)
    {
        this.notifyContactName = notifyContactName;
    }

    public String getFraudContactName()
    {
        return fraudContactName;
    }

    public void setFraudContactName(String fraudContactName)
    {
        this.fraudContactName = fraudContactName;
    }

    public String getChargebackContactName()
    {
        return chargebackContactName;
    }

    public void setChargebackContactName(String chargebackContactName)
    {
        this.chargebackContactName = chargebackContactName;
    }

    public String getRefundContactName()
    {
        return refundContactName;
    }

    public void setRefundContactName(String refundContactName)
    {
        this.refundContactName = refundContactName;
    }

    public String getTechContactName()
    {
        return techContactName;
    }

    public void setTechContactName(String techContactName)
    {
        this.techContactName = techContactName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getPharma()
    {
        return isPharma;
    }

    public void setPharma(String pharma)
    {
        isPharma = pharma;
    }

    public String getValidateEmail()
    {
        return isValidateEmail;
    }

    public void setValidateEmail(String validateEmail)
    {
        isValidateEmail = validateEmail;
    }

    public String getDaily_amount_limit()
    {
        return daily_amount_limit;
    }

    public void setDaily_amount_limit(String daily_amount_limit)
    {
        this.daily_amount_limit = daily_amount_limit;
    }

    public String getMonthly_amount_limit()
    {
        return monthly_amount_limit;
    }

    public void setMonthly_amount_limit(String monthly_amount_limit)
    {
        this.monthly_amount_limit = monthly_amount_limit;
    }

    public String getDaily_card_limit()
    {
        return daily_card_limit;
    }

    public void setDaily_card_limit(String daily_card_limit)
    {
        this.daily_card_limit = daily_card_limit;
    }

    public String getWeekly_card_limit()
    {
        return weekly_card_limit;
    }

    public void setWeekly_card_limit(String weekly_card_limit)
    {
        this.weekly_card_limit = weekly_card_limit;
    }

    public String getMonthly_card_limit()
    {
        return monthly_card_limit;
    }

    public void setMonthly_card_limit(String monthly_card_limit)
    {
        this.monthly_card_limit = monthly_card_limit;
    }

    public String getCheck_limit()
    {
        return check_limit;
    }

    public void setCheck_limit(String check_limit)
    {
        this.check_limit = check_limit;
    }

    public String getFraudemailid()
    {
        return fraudemailid;
    }

    public void setFraudemailid(String fraudemailid)
    {
        this.fraudemailid = fraudemailid;
    }

    public String getIpWhitelisted()
    {
        return isIpWhitelisted;
    }

    public void setIpWhitelisted(String ipWhitelisted)
    {
        isIpWhitelisted = ipWhitelisted;
    }

    public String getModify_merchant_details()
    {
        return modify_merchant_details;
    }

    public void setModify_merchant_details(String modify_merchant_details)
    {
        this.modify_merchant_details = modify_merchant_details;
    }
    public String getModify_company_details()
    {
        return modify_company_details;
    }
    public void setModify_company_details(String modify_company_details)
    {
        this.modify_company_details = modify_company_details;
    }
    public String getResponseType()
    {
        return responseType;
    }
    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }
    public String getResponseLength()
    {
        return responseLength;
    }
    public void setResponseLength(String responseLength)
    {
        this.responseLength = responseLength;
    }

    public String getPartnerKey()
    {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey)
    {
        this.partnerKey = partnerKey;
    }

    public String getIsTokenizationAllowed()
    {
        return isTokenizationAllowed;
    }

    public void setIsTokenizationAllowed(String isTokenizationAllowed)
    {
        this.isTokenizationAllowed = isTokenizationAllowed;
    }

    public String getIsMerchantRequiredForCardRegistration()
    {
        return isMerchantRequiredForCardRegistration;
    }

    public void setIsMerchantRequiredForCardRegistration(String isMerchantRequiredForCardRegistration)
    {
        this.isMerchantRequiredForCardRegistration = isMerchantRequiredForCardRegistration;
    }

    public String getIsAddressRequiredForTokenTransaction()
    {
        return isAddressRequiredForTokenTransaction;
    }

    public void setIsAddressRequiredForTokenTransaction(String isAddressRequiredForTokenTransaction)
    {
        this.isAddressRequiredForTokenTransaction = isAddressRequiredForTokenTransaction;
    }

    public String getIsMerchantRequiredForCardholderRegistration()
    {
        return isMerchantRequiredForCardholderRegistration;
    }

    public void setIsMerchantRequiredForCardholderRegistration(String isMerchantRequiredForCardholderRegistration)
    {
        this.isMerchantRequiredForCardholderRegistration = isMerchantRequiredForCardholderRegistration;
    }

    public String getIsCardEncryptionEnable()
    {
        return isCardEncryptionEnable;
    }

    public void setIsCardEncryptionEnable(String isCardEncryptionEnable)
    {
        this.isCardEncryptionEnable = isCardEncryptionEnable;
    }

    public String getSplitPaymentAllowed()
    {
        return splitPaymentAllowed;
    }

    public void setSplitPaymentAllowed(String splitPaymentAllowed)
    {
        this.splitPaymentAllowed = splitPaymentAllowed;
    }

    public String getHostUrl()
    {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl)
    {
        this.hostUrl = hostUrl;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getFaxNo()
    {
        return faxNo;
    }

    public void setFaxNo(String faxNo)
    {
        this.faxNo = faxNo;
    }

    public String getReportingCurrency()
    {
        return reportingCurrency;
    }

    public void setReportingCurrency(String reportingCurrency)
    {
        this.reportingCurrency = reportingCurrency;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
    }

    public String getClkey()
    {
        return clkey;
    }

    public void setClkey(String clkey)
    {
        this.clkey = clkey;
    }

    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    public void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    public String getCurrentTheme()
    {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme)
    {
        this.currentTheme = currentTheme;
    }

    public String getHeadPanelFont_color()
    {
        return headPanelFont_color;
    }

    public void setHeadPanelFont_color(String headPanelFont_color)
    {
        this.headPanelFont_color = headPanelFont_color;
    }

    public String getBodyPanelFont_color()
    {
        return bodyPanelFont_color;
    }

    public void setBodyPanelFont_color(String bodyPanelFont_color)
    {
        this.bodyPanelFont_color = bodyPanelFont_color;
    }

    public String getPanelHeading_color()
    {
        return panelHeading_color;
    }

    public void setPanelHeading_color(String panelHeading_color)
    {
        this.panelHeading_color = panelHeading_color;
    }

    public String getPanelBody_color()
    {
        return panelBody_color;
    }

    public void setPanelBody_color(String panelBody_color)
    {
        this.panelBody_color = panelBody_color;
    }

    public String getTemplateBackGround_color()
    {
        return templateBackGround_color;
    }

    public void setTemplateBackGround_color(String templateBackGround_color)
    {
        this.templateBackGround_color = templateBackGround_color;
    }

    public String getBodyBgColor()
    {
        return bodyBgColor;
    }

    public void setBodyBgColor(String bodyBgColor)
    {
        this.bodyBgColor = bodyBgColor;
    }

    public String getBodyFgColor()
    {
        return bodyFgColor;
    }

    public void setBodyFgColor(String bodyFgColor)
    {
        this.bodyFgColor = bodyFgColor;
    }

    public String getNavigationFontColor()
    {
        return navigationFontColor;
    }

    public void setNavigationFontColor(String navigationFontColor)
    {
        this.navigationFontColor = navigationFontColor;
    }

    public String getTextboxColor()
    {
        return textboxColor;
    }

    public void setTextboxColor(String textboxColor)
    {
        this.textboxColor = textboxColor;
    }

    public String getIconColor()
    {
        return iconColor;
    }

    public void setIconColor(String iconColor)
    {
        this.iconColor = iconColor;
    }

    public String getTimerColor()
    {
        return timerColor;
    }

    public void setTimerColor(String timerColor)
    {
        this.timerColor = timerColor;
    }

    public String getBoxShadow()
    {
        return boxShadow;
    }

    public void setBoxShadow(String boxShadow)
    {
        this.boxShadow = boxShadow;
    }

    public String getPartnertemplate()
    {
        return partnertemplate;
    }

    public void setPartnertemplate(String partnertemplate)
    {
        this.partnertemplate = partnertemplate;
    }

    public String getReportFileBGColor()
    {
        return reportFileBGColor;
    }

    public void setReportFileBGColor(String reportFileBGColor)
    {
        this.reportFileBGColor = reportFileBGColor;
    }

    public String getProcessorPartnerId()
    {
        return processorPartnerId;
    }

    public void setProcessorPartnerId(String processorPartnerId)
    {
        this.processorPartnerId = processorPartnerId;
    }

    public String getMonthlyMinCommissionModule()
    {
        return monthlyMinCommissionModule;
    }

    public void setMonthlyMinCommissionModule(String monthlyMinCommissionModule)
    {
        this.monthlyMinCommissionModule = monthlyMinCommissionModule;
    }

    public String getPartnerShortName()
    {
        return partnerShortName;
    }

    public void setPartnerShortName(String partnerShortName)
    {
        this.partnerShortName = partnerShortName;
    }

    public String getProfitShareCommissionModule()
    {
        return profitShareCommissionModule;
    }

    public void setProfitShareCommissionModule(String profitShareCommissionModule)
    {
        this.profitShareCommissionModule = profitShareCommissionModule;
    }

    public String getTermsurl()
    {
        return termsurl;
    }

    public void setTermsurl(String termsurl)
    {
        this.termsurl = termsurl;
    }

    public String getPrivacyurl()
    {
        return privacyurl;
    }

    public void setPrivacyurl(String privacyurl)
    {
        this.privacyurl = privacyurl;
    }

    public String getCheckoutInvoice()
    {
        return checkoutInvoice;
    }

    public void setCheckoutInvoice(String checkoutInvoice)
    {
        this.checkoutInvoice = checkoutInvoice;
    }

    public String getLogin() {return login;}

    public void setLogin(String login) {this.login = login;}

    public String getExportTransactionCron() {return exportTransactionCron;}

    public void setExportTransactionCron(String exportTransactionCron) {this.exportTransactionCron = exportTransactionCron;}

    public String getOrganizationName()
    {
        return organizationName;
    }

    public void setOrganizationName(String organizationName)
    {
        this.organizationName = organizationName;
    }

    public String getSupporturl() { return supporturl; }

    public void setSupporturl(String supporturl) { this.supporturl = supporturl; }

    public String getDocumentationurl() { return documentationurl; }

    public void setDocumentationurl(String documentationurl) { this.documentationurl = documentationurl; }

    public String getNEW_CHECKOUT_FOOTER_FONT_COLOR()
    {
        return NEW_CHECKOUT_FOOTER_FONT_COLOR;
    }

    public void setNEW_CHECKOUT_FOOTER_FONT_COLOR(String NEW_CHECKOUT_FOOTER_FONT_COLOR)
    {
        this.NEW_CHECKOUT_FOOTER_FONT_COLOR = NEW_CHECKOUT_FOOTER_FONT_COLOR;
    }

    public String getNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR()
    {
        return NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR;
    }

    public void setNEW_CHECKOUT_FOOTER_BACKGROUND_COLOR(String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR)
    {
        this.NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR = NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR;
    }

    public String getLogoHeight()
    {
        return logoHeight;
    }

    public void setLogoHeight(String logoHeight)
    {
        this.logoHeight = logoHeight;
    }

    public String getLogoWidth()
    {
        return logoWidth;
    }

    public void setLogoWidth(String logoWidth)
    {
        this.logoWidth = logoWidth;
    }

    public String getIsVerifyMerchantotp()
    {
        return isVerifyMerchantotp;
    }

    public void setIsVerifyMerchantotp(String isVerifyMerchantotp)
    {
        this.isVerifyMerchantotp = isVerifyMerchantotp;
    }
}
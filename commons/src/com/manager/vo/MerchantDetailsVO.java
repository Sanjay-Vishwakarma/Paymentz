package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/1/14
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantDetailsVO
{
    String memberId;
    String mitigationMail;
    String transactionID;

    public String getTransactionID()
    {
        return transactionID;
    }

    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getCheckoutTimerFlag()
    {
        return checkoutTimerFlag;
    }

    public void setCheckoutTimerFlag(String checkoutTimerFlag)
    {
        this.checkoutTimerFlag = checkoutTimerFlag;
    }

    public String getCheckoutTimerTime()
    {
        return checkoutTimerTime;
    }

    public void setCheckoutTimerTime(String checkoutTimerTime)
    {
        this.checkoutTimerTime = checkoutTimerTime;
    }

    String hrAlertProof;
    String dataMismatchProof;
    String company_name;
    String contact_persons;
    String contact_emails;
    String address;
    String city;
    String state;
    String zip;
    String country;
    String telNo;
    String faxNo;
    String notifyEmail;
    String brandName;
    String siteName;
    String reserves;
    String aptPrompt;
    String taxPer;
    String checksumAlgo;
    String accountId;
    String reversalCharge;
    String withdrawalCharge;
    String chargeBackCharge;
    String autoSelectTerminal;
    String isIpWhiteListed;
    String isService;
    String chargePer;
    String fixamount;
    String custReminderMail;
    String cardTransLimit;
    String cardCheckLimit;
    String key;
    String checkLimit;
    String activation;
    String partnerId;
    String partnerName;
    String agentId;
    String isPharma;
    String iswhitelisted;
    String isrefund;
    String refunddailylimit;
    String isRestrictedTicket;
    String isTokenizationAllowed;
    String isPartnerLogoFlag;
    String isSisaLogoFlag;
    String isMerchantLogo;
    String merchantLogoName;
    String card_velocity_check;
    //Added For Online Fraud Checking
    double fraudMaxScoreAllowed;
    double fraudAutoReversalScore;
    String logoName;
    String isPoweredBy;
    String autoRedirect;
    String isBlacklistTransaction;
    String responseType;
    String responseLength;
    String addressValidation;
    String flightMode;
    String emailSent;
    String onlineFraudCheck;
    String isExcessCaptureAllowed;
    String addressDeatails;
    String isRecurring;
    String isManualRecurring;
    String login;
    String registrationDate;
    String contactNumber;
    String isBackOfficeAccess;
    String headPanelFont_color;
    String bodyPanelFont_color;
    String panelHeading_color;
    String panelBody_color;
    String templateBackGround_color;
    String isAddressDetailsRequired;
    String template;
    String splitPaymentAllowed;
    String currency;
    String refundAllowedDays;
    String chargebackAllowedDays;
    String isCardEncryptionEnable;
    String newPassword;
    String conPassword;
    String website;
    String isIpWhiteListedCheckForAPIs;
    String isEmailLimitEnabled;
    String password;
    String otp;
    String ipWhitelistInvoice;
    String addressValidationInvoice;
    String binService;
    String expDateOffset;
    String partnerSiteUrl;
    String role;
    String bodyBgColor;
    String bodyFgColor;
    String navigationFontColor;
    String textboxColor;
    String iconColor;
    String timerColor;
    String boxShadow;
    String merchantDomain;
    String partnerDomain;
    String etoken;
    String ismobileverified;
    String defaultTheme;
    String currentTheme;
    String emailverified;
    String isMobileVerified;
    String icon="";
    String supportSection = "";
    String tcUrl = "";
    String cardWhitelistLevel;
    String multiCurrencySupport = "";
    String partnerSupportContactNumber;
    String hostUrl;
    String partnertemplate;
    String isRefundEmailSent;
    String privacyPolicyUrl;
    String partnerTcUrl;
    String partnerPrivacyUrl;
    String lastTransactionDate;
    String dynamicDescriptor;
    String binRouting;
    String vbvLogo;
    String masterSecureLogo;
    String chargebackMailsend;
    String personalInfoDisplay;
    String personalInfoValidation;
    String hostedPaymentPage;
    String consentFlag;
    String securityLogo;
    String multipleRefund;
    String blacklistCountryIp;
    String blacklistCountryBin;
    String blacklistCountryBinIp;
    String emiSupport;
    String bankCardLimit;
    String partialRefund;
    String internalFraudCheck;
    String isMerchantRequiredForCardRegistration;
    String merchantOrderDetailsDisplay;
    String limitRouting;
    String marketPlace;
    String notificationUrl;
    String memberGuid;
    String merchantAccountType;
    String virtualCheckout;
    String isMobileRequiredForVC;
    String isEmailRequiredForVC;
    String isCvvStore;
    String actionExecutorId;
    String actionExecutorName;
    String userid;
    String checkoutTimerFlag;
    String checkoutTimerTime;
    String ispurchase_inquiry_blacklist;
    String ispurchase_inquiry_refund;
    String isfraud_determined_refund;
    String isfraud_determined_blacklist;
    String isdispute_initiated_refund;
    String isdispute_initiated_blacklist;
    String isstop_payment_blacklist;
    String isexception_file_listing_blacklist;
    String reconciliationNotification;
    String isUniqueOrderIdRequired;
    String chargebackNotification;
    String transactionNotification;
    String payout_amount_limit_check;
    String successReconMail;
    String refundReconMail;
    String chargebackReconMail;
    String payoutReconMail;
    String isMerchantLogoBO;
    String cardExpiryDateCheck;
    String payoutRouting;
    String payoutNotification;
    String supportNoNeeded;
    String vpaAddressAmountLimitCheck;
    String vpaAddressLimitCheck;
    String bankAccountAmountLimitCheck;
    String bankAccountLimitCheck;
    String isDomainWhitelisted;
    String isShareAllowed;
    String isSignatureAllowed;
    String isSaveReceiptAllowed;
    String defaultLanguage;
    String customerIpLimitCheck;
    String customerIpAmountLimitCheck;
    String customerNameLimitCheck;
    String customerNameAmountLimitCheck;
    String customerEmailLimitCheck;
    String customerEmailAmountLimitCheck;
    String customerPhoneLimitCheck;
    String customerPhoneAmountLimitCheck;
    String NEW_CHECKOUT_BODYNFOOTER_COLOR="";
    String NEW_CHECKOUT_HEADERBACKGROUND_COLOR="";
    String NEW_CHECKOUT_NAVIGATIONBAR_COLOR="";
    String NEW_CHECKOUT_BUTTON_FONT_COLOR="";
    String NEW_CHECKOUT_HEADER_FONT_COLOR="";
    String NEW_CHECKOUT_FULLBACKGROUND_COLOR="";
    String NEW_CHECKOUT_LABEL_FONT_COLOR="";
    String NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR="";
    String NEW_CHECKOUT_BUTTON_COLOR="";
    String NEW_CHECKOUT_ICON_COLOR="";
    String NEW_CHECKOUT_TIMER_COLOR="";
    String NEW_CHECKOUT_BOX_SHADOW="";
    String NEW_CHECKOUT_FOOTER_FONT_COLOR="";
    String NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR="";
    String isOTPRequired;
    String smsOtp;
    String emailOtp;
    String isIgnorePaymode;
    String customerTransactionSuccessfulMail="";
    String amountLimitCheckTerminalLevel="";
    String payoutPersonalInfoValidation;

    public String getPayoutPersonalInfoValidation()
    {
        return payoutPersonalInfoValidation;
    }

    public void setPayoutPersonalInfoValidation(String payoutPersonalInfoValidation)
    {
        this.payoutPersonalInfoValidation = payoutPersonalInfoValidation;
    }

    public String getIsIgnorePaymode()
    {
        return isIgnorePaymode;
    }

    public void setIsIgnorePaymode(String isIgnorePaymode)
    {
        this.isIgnorePaymode = isIgnorePaymode;
    }

    public String getSmsOtp()
    {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp)
    {
        this.smsOtp = smsOtp;
    }

    public String getEmailOtp()
    {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp)
    {
        this.emailOtp = emailOtp;
    }
    String isCardStorageRequired;

    public String getNEW_CHECKOUT_BODYNFOOTER_COLOR()
    {
        return NEW_CHECKOUT_BODYNFOOTER_COLOR;
    }

    public void setNEW_CHECKOUT_BODYNFOOTER_COLOR(String NEW_CHECKOUT_BODYNFOOTER_COLOR)
    {
        this.NEW_CHECKOUT_BODYNFOOTER_COLOR = NEW_CHECKOUT_BODYNFOOTER_COLOR;
    }

    public String getNEW_CHECKOUT_HEADERBACKGROUND_COLOR()
    {
        return NEW_CHECKOUT_HEADERBACKGROUND_COLOR;
    }

    public void setNEW_CHECKOUT_HEADERBACKGROUND_COLOR(String NEW_CHECKOUT_HEADERBACKGROUND_COLOR)
    {
        this.NEW_CHECKOUT_HEADERBACKGROUND_COLOR = NEW_CHECKOUT_HEADERBACKGROUND_COLOR;
    }

    public String getNEW_CHECKOUT_NAVIGATIONBAR_COLOR()
    {
        return NEW_CHECKOUT_NAVIGATIONBAR_COLOR;
    }

    public void setNEW_CHECKOUT_NAVIGATIONBAR_COLOR(String NEW_CHECKOUT_NAVIGATIONBAR_COLOR)
    {
        this.NEW_CHECKOUT_NAVIGATIONBAR_COLOR = NEW_CHECKOUT_NAVIGATIONBAR_COLOR;
    }

    public String getNEW_CHECKOUT_BUTTON_FONT_COLOR()
    {
        return NEW_CHECKOUT_BUTTON_FONT_COLOR;
    }

    public void setNEW_CHECKOUT_BUTTON_FONT_COLOR(String NEW_CHECKOUT_BUTTON_FONT_COLOR)
    {
        this.NEW_CHECKOUT_BUTTON_FONT_COLOR = NEW_CHECKOUT_BUTTON_FONT_COLOR;
    }

    public String getNEW_CHECKOUT_HEADER_FONT_COLOR()
    {
        return NEW_CHECKOUT_HEADER_FONT_COLOR;
    }

    public void setNEW_CHECKOUT_HEADER_FONT_COLOR(String NEW_CHECKOUT_HEADER_FONT_COLOR)
    {
        this.NEW_CHECKOUT_HEADER_FONT_COLOR = NEW_CHECKOUT_HEADER_FONT_COLOR;
    }

    public String getNEW_CHECKOUT_FULLBACKGROUND_COLOR()
    {
        return NEW_CHECKOUT_FULLBACKGROUND_COLOR;
    }

    public void setNEW_CHECKOUT_FULLBACKGROUND_COLOR(String NEW_CHECKOUT_FULLBACKGROUND_COLOR)
    {
        this.NEW_CHECKOUT_FULLBACKGROUND_COLOR = NEW_CHECKOUT_FULLBACKGROUND_COLOR;
    }

    public String getNEW_CHECKOUT_LABEL_FONT_COLOR()
    {
        return NEW_CHECKOUT_LABEL_FONT_COLOR;
    }

    public void setNEW_CHECKOUT_LABEL_FONT_COLOR(String NEW_CHECKOUT_LABEL_FONT_COLOR)
    {
        this.NEW_CHECKOUT_LABEL_FONT_COLOR = NEW_CHECKOUT_LABEL_FONT_COLOR;
    }

    public String getNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR()
    {
        return NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR;
    }

    public void setNEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR(String NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR)
    {
        this.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR = NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR;
    }

    public String getNEW_CHECKOUT_BUTTON_COLOR()
    {
        return NEW_CHECKOUT_BUTTON_COLOR;
    }

    public void setNEW_CHECKOUT_BUTTON_COLOR(String NEW_CHECKOUT_BUTTON_COLOR)
    {
        this.NEW_CHECKOUT_BUTTON_COLOR = NEW_CHECKOUT_BUTTON_COLOR;
    }

    public String getNEW_CHECKOUT_ICON_COLOR()
    {
        return NEW_CHECKOUT_ICON_COLOR;
    }

    public void setNEW_CHECKOUT_ICON_COLOR(String NEW_CHECKOUT_ICON_COLOR)
    {
        this.NEW_CHECKOUT_ICON_COLOR = NEW_CHECKOUT_ICON_COLOR;
    }

    public String getNEW_CHECKOUT_TIMER_COLOR()
    {
        return NEW_CHECKOUT_TIMER_COLOR;
    }

    public void setNEW_CHECKOUT_TIMER_COLOR(String NEW_CHECKOUT_TIMER_COLOR)
    {
        this.NEW_CHECKOUT_TIMER_COLOR = NEW_CHECKOUT_TIMER_COLOR;
    }

    public String getNEW_CHECKOUT_BOX_SHADOW()
    {
        return NEW_CHECKOUT_BOX_SHADOW;
    }

    public void setNEW_CHECKOUT_BOX_SHADOW(String NEW_CHECKOUT_BOX_SHADOW)
    {
        this.NEW_CHECKOUT_BOX_SHADOW = NEW_CHECKOUT_BOX_SHADOW;
    }

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

    public String getCustomerNameLimitCheck()
    {
        return customerNameLimitCheck;
    }

    public void setCustomerNameLimitCheck(String customerNameLimitCheck)
    {
        this.customerNameLimitCheck = customerNameLimitCheck;
    }

    public String getCustomerNameAmountLimitCheck()
    {
        return customerNameAmountLimitCheck;
    }

    public void setCustomerNameAmountLimitCheck(String customerNameAmountLimitCheck)
    {
        this.customerNameAmountLimitCheck = customerNameAmountLimitCheck;
    }

    public String getCustomerEmailLimitCheck()
    {
        return customerEmailLimitCheck;
    }

    public void setCustomerEmailLimitCheck(String customerEmailLimitCheck)
    {
        this.customerEmailLimitCheck = customerEmailLimitCheck;
    }

    public String getCustomerEmailAmountLimitCheck()
    {
        return customerEmailAmountLimitCheck;
    }

    public void setCustomerEmailAmountLimitCheck(String customerEmailAmountLimitCheck)
    {
        this.customerEmailAmountLimitCheck = customerEmailAmountLimitCheck;
    }

    public String getCustomerPhoneLimitCheck()
    {
        return customerPhoneLimitCheck;
    }

    public void setCustomerPhoneLimitCheck(String customerPhoneLimitCheck)
    {
        this.customerPhoneLimitCheck = customerPhoneLimitCheck;
    }

    public String getCustomerPhoneAmountLimitCheck()
    {
        return customerPhoneAmountLimitCheck;
    }

    public void setCustomerPhoneAmountLimitCheck(String customerPhoneAmountLimitCheck)
    {
        this.customerPhoneAmountLimitCheck = customerPhoneAmountLimitCheck;
    }

    public String getCustomerIpAmountLimitCheck()
    {
        return customerIpAmountLimitCheck;
    }

    public void setCustomerIpAmountLimitCheck(String customerIpAmountLimitCheck)
    {
        this.customerIpAmountLimitCheck = customerIpAmountLimitCheck;
    }

    public String getCustomerIpLimitCheck()
    {
        return customerIpLimitCheck;
    }

    public void setCustomerIpLimitCheck(String customerIpLimitCheck)
    {
        this.customerIpLimitCheck = customerIpLimitCheck;
    }

    public String getPayoutNotification()
    {
        return payoutNotification;
    }

    public void setPayoutNotification(String payoutNotification)
    {
        this.payoutNotification = payoutNotification;
    }
    public String getChargebackNotification()
    {
        return chargebackNotification;
    }

    public void setChargebackNotification(String chargebackNotification)
    {
        this.chargebackNotification = chargebackNotification;
    }

    public String getTransactionNotification()
    {
        return transactionNotification;
    }

    public void setTransactionNotification(String transactionNotification)
    {
        this.transactionNotification = transactionNotification;
    }
    public String getMemberGuid()
    {
        return memberGuid;
    }

    public void setMemberGuid(String memberGuid)
    {
        this.memberGuid = memberGuid;
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

    public String getIsEmailLimitEnabled()
    {
        return isEmailLimitEnabled;
    }

    public void setIsEmailLimitEnabled(String isEmailLimitEnabled)
    {
        this.isEmailLimitEnabled = isEmailLimitEnabled;
    }

    public String getIsIpWhiteListedCheckForAPIs()
    {
        return isIpWhiteListedCheckForAPIs;
    }

    public void setIsIpWhiteListedCheckForAPIs(String isIpWhiteListedCheckForAPIs)
    {
        this.isIpWhiteListedCheckForAPIs = isIpWhiteListedCheckForAPIs;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getNewPassword()
    {
        return newPassword;
    }

    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }

    public String getConPassword()
    {
        return conPassword;
    }

    public void setConPassword(String conPassword)
    {
        this.conPassword = conPassword;
    }

    public String getRefundAllowedDays()
    {
        return refundAllowedDays;
    }

    public void setRefundAllowedDays(String refundAllowedDays)
    {
        this.refundAllowedDays = refundAllowedDays;
    }

    public String getChargebackAllowedDays()
    {
        return chargebackAllowedDays;
    }

    public void setChargebackAllowedDays(String chargebackAllowedDays)
    {
        this.chargebackAllowedDays = chargebackAllowedDays;
    }

    public String getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate)
    {
        this.registrationDate = registrationDate;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getIsRecurring()
    {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring)
    {
        this.isRecurring = isRecurring;
    }

    public String getIsManualRecurring()
    {
        return isManualRecurring;
    }

    public void setIsManualRecurring(String isManualRecurring)
    {
        this.isManualRecurring = isManualRecurring;
    }

    public String getAddressDeatails()
    {
        return addressDeatails;
    }

    public void setAddressDeatails(String addressDeatails)
    {
        this.addressDeatails = addressDeatails;
    }

    public String getPartnerLogoFlag()
    {
        return isPartnerLogoFlag;
    }

    public void setPartnerLogoFlag(String partnerLogoFlag)
    {
        isPartnerLogoFlag = partnerLogoFlag;
    }

    public String getSisaLogoFlag()
    {
        return isSisaLogoFlag;
    }

    public void setSisaLogoFlag(String sisaLogoFlag)
    {
        isSisaLogoFlag = sisaLogoFlag;
    }

    public String getMerchantLogo()
    {
        return isMerchantLogo;
    }

    public void setMerchantLogo(String merchantLogo)
    {
        isMerchantLogo = merchantLogo;
    }

    public String getMerchantLogoName()
    {
        return merchantLogoName;
    }

    public void setMerchantLogoName(String merchantLogoName)
    {
        this.merchantLogoName = merchantLogoName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getLogoName()
    {
        return logoName;
    }

    public void setLogoName(String logoName)
    {
        this.logoName = logoName;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getPoweredBy()
    {
        return isPoweredBy;
    }

    public void setPoweredBy(String poweredBy)
    {
        isPoweredBy = poweredBy;
    }

    public String getAutoRedirect()
    {
        return autoRedirect;
    }

    public void setAutoRedirect(String autoRedirect)
    {
        this.autoRedirect = autoRedirect;
    }


    public String getService()
    {
        return isService;
    }

    public void setService(String service)
    {
        isService = service;
    }

    public String getChargePer()
    {
        return chargePer;
    }

    public void setChargePer(String chargePer)
    {
        this.chargePer = chargePer;
    }

    public String getFixamount()
    {
        return fixamount;
    }

    public void setFixamount(String fixamount)
    {
        this.fixamount = fixamount;
    }

    public String getCustReminderMail()
    {
        return custReminderMail;
    }

    public void setCustReminderMail(String custReminderMail)
    {
        this.custReminderMail = custReminderMail;
    }

    public String getCardTransLimit()
    {
        return cardTransLimit;
    }

    public void setCardTransLimit(String cardTransLimit)
    {
        this.cardTransLimit = cardTransLimit;
    }

    public String getCardCheckLimit()
    {
        return cardCheckLimit;
    }

    public void setCardCheckLimit(String cardCheckLimit)
    {
        this.cardCheckLimit = cardCheckLimit;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getCheckLimit()
    {
        return checkLimit;
    }

    public void setCheckLimit(String checkLimit)
    {
        this.checkLimit = checkLimit;
    }

    public String getActivation()
    {
        return activation;
    }

    public void setActivation(String activation)
    {
        this.activation = activation;
    }


    public String getAutoSelectTerminal()
    {
        return autoSelectTerminal;
    }

    public void setAutoSelectTerminal(String autoSelectTerminal)
    {
        this.autoSelectTerminal = autoSelectTerminal;
    }

    public String getIpWhiteListed()
    {
        return isIpWhiteListed;
    }

    public void setIpWhiteListed(String ipWhiteListed)
    {
        isIpWhiteListed = ipWhiteListed;
    }



    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getMitigationMail()
    {
        return mitigationMail;
    }

    public void setMitigationMail(String mitigationMail)
    {
        this.mitigationMail = mitigationMail;
    }

    public String getHrAlertProof()
    {
        return hrAlertProof;
    }

    public void setHrAlertProof(String hrAlertProof)
    {
        this.hrAlertProof = hrAlertProof;
    }

    public String getDataMismatchProof()
    {
        return dataMismatchProof;
    }

    public void setDataMismatchProof(String dataMismatchProof)
    {
        this.dataMismatchProof = dataMismatchProof;
    }

    public String getCompany_name()
    {
        return company_name;
    }

    public void setCompany_name(String company_name)
    {
        this.company_name = company_name;
    }

    public String getContact_persons()
    {
        return contact_persons;
    }

    public void setContact_persons(String contact_persons)
    {
        this.contact_persons = contact_persons;
    }

    public String getContact_emails()
    {
        return contact_emails;
    }

    public void setContact_emails(String contact_emails)
    {
        this.contact_emails = contact_emails;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getTelNo()
    {
        return telNo;
    }

    public void setTelNo(String telNo)
    {
        this.telNo = telNo;
    }

    public String getFaxNo()
    {
        return faxNo;
    }

    public void setFaxNo(String faxNo)
    {
        this.faxNo = faxNo;
    }

    public String getNotifyEmail()
    {
        return notifyEmail;
    }

    public void setNotifyEmail(String notifyEmail)
    {
        this.notifyEmail = notifyEmail;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    public String getReserves()
    {
        return reserves;
    }

    public void setReserves(String reserves)
    {
        this.reserves = reserves;
    }

    public String getAptPrompt()
    {
        return aptPrompt;
    }

    public void setAptPrompt(String aptPrompt)
    {
        this.aptPrompt = aptPrompt;
    }

    public String getTaxPer()
    {
        return taxPer;
    }

    public void setTaxPer(String taxPer)
    {
        this.taxPer = taxPer;
    }

    public String getChecksumAlgo()
    {
        return checksumAlgo;
    }

    public void setChecksumAlgo(String checksumAlgo)
    {
        this.checksumAlgo = checksumAlgo;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getReversalCharge()
    {
        return reversalCharge;
    }

    public void setReversalCharge(String reversalCharge)
    {
        this.reversalCharge = reversalCharge;
    }

    public String getWithdrawalCharge()
    {
        return withdrawalCharge;
    }

    public void setWithdrawalCharge(String withdrawalCharge)
    {
        this.withdrawalCharge = withdrawalCharge;
    }

    public String getChargeBackCharge()
    {
        return chargeBackCharge;
    }

    public void setChargeBackCharge(String chargeBackCharge)
    {
        this.chargeBackCharge = chargeBackCharge;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getPharma()
    {
        return isPharma;
    }

    public void setPharma(String pharma)
    {
        isPharma = pharma;
    }

    public String getIswhitelisted()
    {
        return iswhitelisted;
    }

    public void setIswhitelisted(String iswhitelisted)
    {
        this.iswhitelisted = iswhitelisted;
    }

    public String getIsrefund()
    {
        return isrefund;
    }

    public void setIsrefund(String isrefund)
    {
        this.isrefund = isrefund;
    }

    public String getRefunddailylimit()
    {
        return refunddailylimit;
    }

    public void setRefunddailylimit(String refunddailylimit)
    {
        this.refunddailylimit = refunddailylimit;
    }

    public double getFraudMaxScoreAllowed()
    {
        return fraudMaxScoreAllowed;
    }

    public void setFraudMaxScoreAllowed(double fraudMaxScoreAllowed)
    {
        this.fraudMaxScoreAllowed = fraudMaxScoreAllowed;
    }

    public double getFraudAutoReversalScore()
    {
        return fraudAutoReversalScore;
    }

    public void setFraudAutoReversalScore(double fraudAutoReversalScore)
    {
        this.fraudAutoReversalScore = fraudAutoReversalScore;
    }

    public String getIsRestrictedTicket()
    {
        return isRestrictedTicket;
    }

    public void setIsRestrictedTicket(String isRestrictedTicket)
    {
        this.isRestrictedTicket = isRestrictedTicket;
    }

    public String getIsTokenizationAllowed()
    {
        return isTokenizationAllowed;
    }
    public void setIsTokenizationAllowed(String isTokenizationAllowed)
    {
        this.isTokenizationAllowed = isTokenizationAllowed;
    }

    public String getIsIpWhiteListed()
    {
        return isIpWhiteListed;
    }

    public void setIsIpWhiteListed(String isIpWhiteListed)
    {
        this.isIpWhiteListed = isIpWhiteListed;
    }

    public String getIsService()
    {
        return isService;
    }

    public void setIsService(String isService)
    {
        this.isService = isService;
    }

    public String getIsPharma()
    {
        return isPharma;
    }

    public void setIsPharma(String isPharma)
    {
        this.isPharma = isPharma;
    }

    public String getIsPartnerLogoFlag()
    {
        return isPartnerLogoFlag;
    }

    public void setIsPartnerLogoFlag(String isPartnerLogoFlag)
    {
        this.isPartnerLogoFlag = isPartnerLogoFlag;
    }

    public String getIsSisaLogoFlag()
    {
        return isSisaLogoFlag;
    }

    public void setIsSisaLogoFlag(String isSisaLogoFlag)
    {
        this.isSisaLogoFlag = isSisaLogoFlag;
    }

    public String getIsMerchantLogo()
    {
        return isMerchantLogo;
    }

    public void setIsMerchantLogo(String isMerchantLogo)
    {
        this.isMerchantLogo = isMerchantLogo;
    }

    public String getIsPoweredBy()
    {
        return isPoweredBy;
    }

    public void setIsPoweredBy(String isPoweredBy)
    {
        this.isPoweredBy = isPoweredBy;
    }

    public String getIsBlacklistTransaction()
    {
        return isBlacklistTransaction;
    }

    public void setIsBlacklistTransaction(String isBlacklistTransaction)
    {
        this.isBlacklistTransaction = isBlacklistTransaction;
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
    public String getAddressValidation()
    {
        return addressValidation;
    }

    public void setAddressValidation(String addressValidation)
    {
        this.addressValidation = addressValidation;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
    }

    public String getEmailSent() {return emailSent;}

    public void setEmailSent(String emailSent)
    {
        this.emailSent = emailSent;
    }

    public String getOnlineFraudCheck()
    {
        return onlineFraudCheck;
    }

    public void setOnlineFraudCheck(String onlineFraudCheck)
    {
        this.onlineFraudCheck = onlineFraudCheck;
    }

    public String getSplitPaymentAllowed()
    {
        return splitPaymentAllowed;
    }

    public void setSplitPaymentAllowed(String splitPaymentAllowed)
    {
        this.splitPaymentAllowed = splitPaymentAllowed;
    }

    public String getIsExcessCaptureAllowed()
    {
        return isExcessCaptureAllowed;
    }

    public void setIsExcessCaptureAllowed(String isExcessCaptureAllowed)
    {
        this.isExcessCaptureAllowed = isExcessCaptureAllowed;
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

    public String getTemplateBackGround_color()
    {
        return templateBackGround_color;
    }

    public void setTemplateBackGround_color(String templateBackGround_color)
    {
        this.templateBackGround_color = templateBackGround_color;
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

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getIsAddressDetailsRequired()
    {
        return isAddressDetailsRequired;
    }

    public void setIsAddressDetailsRequired(String isAddressDetailsRequired)
    {
        this.isAddressDetailsRequired = isAddressDetailsRequired;
    }
    public String getLastTransactionDate()
    {
        return getLastTransactionDate();
    }
    public void setLastTransactionDate(String lastTransactionDate)
    {
        this.lastTransactionDate = lastTransactionDate;
    }

    public String getContactNumber()
    {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber)
    {
        this.contactNumber = contactNumber;
    }

    public String getIsBackOfficeAccess()
    {
        return isBackOfficeAccess;
    }

    public void setIsBackOfficeAccess(String isBackOfficeAccess)
    {
        this.isBackOfficeAccess = isBackOfficeAccess;
    }

    public String getIsCardEncryptionEnable()
    {
        return isCardEncryptionEnable;
    }

    public void setIsCardEncryptionEnable(String isCardEncryptionEnable)
    {
        this.isCardEncryptionEnable = isCardEncryptionEnable;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getOtp()
    {
        return otp;
    }

    public void setOtp(String otp)
    {
        this.otp = otp;
    }

    public String getBinService()
    {
        return binService;
    }

    public void setBinService(String binService)
    {
        this.binService = binService;
    }

    public String getPartnerSiteUrl()
    {
        return partnerSiteUrl;
    }

    public void setPartnerSiteUrl(String partnerSiteUrl)
    {
        this.partnerSiteUrl = partnerSiteUrl;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
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

    public String getExpDateOffset()
    {
        return expDateOffset;
    }

    public void setExpDateOffset(String expDateOffset)
    {
        this.expDateOffset = expDateOffset;
    }

    public String getMerchantDomain()
    {
        return merchantDomain;
    }

    public void setMerchantDomain(String merchantDomain)
    {
        this.merchantDomain = merchantDomain;
    }

    public String getPartnerDomain()
    {
        return partnerDomain;
    }

    public void setPartnerDomain(String partnerDomain)
    {
        this.partnerDomain = partnerDomain;
    }


    public String getEtoken()
    {
        return etoken;
    }

    public void setEtoken(String etoken)
    {
        this.etoken = etoken;
    }


    public String getIsmobileverified()
    {
        return ismobileverified;
    }

    public void setIsmobileverified(String ismobileverified)
    {
        this.ismobileverified = ismobileverified;
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


    public String getEmailverified()
    {
        return emailverified;
    }

    public void setEmailverified(String emailverified)
    {
        this.emailverified = emailverified;
    }

    public String getIsMobileVerified()
    {
        return isMobileVerified;
    }

    public void setIsMobileVerified(String isMobileVerified)
    {
        this.isMobileVerified = isMobileVerified;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public String getSupportSection()
    {
        return supportSection;
    }

    public void setSupportSection(String supportSection)
    {
        this.supportSection = supportSection;
    }

    public String getTcUrl()
    {
        return tcUrl;
    }

    public void setTcUrl(String tcUrl)
    {
        this.tcUrl = tcUrl;
    }

    public String getCardWhitelistLevel()
    {
        return cardWhitelistLevel;
    }

    public void setCardWhitelistLevel(String cardWhitelistLevel)
    {
        this.cardWhitelistLevel = cardWhitelistLevel;
    }

    public String getMultiCurrencySupport()
    {
        return multiCurrencySupport;
    }

    public void setMultiCurrencySupport(String multiCurrencySupport)
    {
        this.multiCurrencySupport = multiCurrencySupport;
    }

    public String getDynamicDescriptor()
    {
        return dynamicDescriptor;
    }

    public void setDynamicDescriptor(String dynamicDescriptor)
    {
        this.dynamicDescriptor = dynamicDescriptor;
    }

    public String getPartnerSupportContactNumber()
    {
        return partnerSupportContactNumber;
    }

    public void setPartnerSupportContactNumber(String partnerSupportContactNumber)
    {
        this.partnerSupportContactNumber = partnerSupportContactNumber;
    }

    public String getHostUrl()
    {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl)
    {
        this.hostUrl = hostUrl;
    }

    public String getPartnertemplate()
    {
        return partnertemplate;
    }

    public void setPartnertemplate(String partnertemplate)
    {
        this.partnertemplate = partnertemplate;
    }

    public String getIsRefundEmailSent()
    {
        return isRefundEmailSent;
    }

    public void setIsRefundEmailSent(String isRefundEmailSent)
    {
        this.isRefundEmailSent = isRefundEmailSent;
    }

    public String getPrivacyPolicyUrl()
    {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl)
    {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getPartnerTcUrl()
    {
        return partnerTcUrl;
    }

    public void setPartnerTcUrl(String partnerTcUrl)
    {
        this.partnerTcUrl = partnerTcUrl;
    }

    public String getPartnerPrivacyUrl()
    {
        return partnerPrivacyUrl;
    }

    public void setPartnerPrivacyUrl(String partnerPrivacyUrl)
    {
        this.partnerPrivacyUrl = partnerPrivacyUrl;
    }

    public String getBinRouting()
    {
        return binRouting;
    }

    public void setBinRouting(String binRouting)
    {
        this.binRouting = binRouting;
    }

    public String getVbvLogo()
    {
        return vbvLogo;
    }

    public void setVbvLogo(String vbvLogo)
    {
        this.vbvLogo = vbvLogo;
    }

    public String getMasterSecureLogo()
    {
        return masterSecureLogo;
    }

    public void setMasterSecureLogo(String masterSecureLogo)
    {
        this.masterSecureLogo = masterSecureLogo;
    }

    public String getChargebackMailsend()
    {
        return chargebackMailsend;
    }

    public void setChargebackMailsend(String chargebackMailsend)
    {
        this.chargebackMailsend = chargebackMailsend;
    }

    public String getPersonalInfoDisplay()
    {
        return personalInfoDisplay;
    }

    public void setPersonalInfoDisplay(String personalInfoDisplay)
    {
        this.personalInfoDisplay = personalInfoDisplay;
    }

    public String getPersonalInfoValidation()
    {
        return personalInfoValidation;
    }

    public void setPersonalInfoValidation(String personalInfoValidation)
    {
        this.personalInfoValidation = personalInfoValidation;
    }

    public String getHostedPaymentPage()
    {
        return hostedPaymentPage;
    }

    public void setHostedPaymentPage(String hostedPaymentPage)
    {
        this.hostedPaymentPage = hostedPaymentPage;
    }

    public String getConsentFlag()
    {
        return consentFlag;
    }

    public void setConsentFlag(String consentFlag)
    {
        this.consentFlag = consentFlag;
    }

    public String getSecurityLogo()
    {
        return securityLogo;
    }

    public void setSecurityLogo(String securityLogo)
    {
        this.securityLogo = securityLogo;
    }

    public String getMultipleRefund()
    {
        return multipleRefund;
    }

    public void setMultipleRefund(String multipleRefund)
    {
        this.multipleRefund = multipleRefund;
    }

    public String getBlacklistCountryIp()
    {
        return blacklistCountryIp;
    }

    public void setBlacklistCountryIp(String blacklistCountryIp)
    {
        this.blacklistCountryIp = blacklistCountryIp;
    }

    public String getBlacklistCountryBin()
    {
        return blacklistCountryBin;
    }

    public void setBlacklistCountryBin(String blacklistCountryBin)
    {
        this.blacklistCountryBin = blacklistCountryBin;
    }

    public String getBlacklistCountryBinIp()
    {
        return blacklistCountryBinIp;
    }

    public void setBlacklistCountryBinIp(String blacklistCountryBinIp)
    {
        this.blacklistCountryBinIp = blacklistCountryBinIp;
    }
    public String getEmiSupport()
    {
        return emiSupport;
    }

    public void setEmiSupport(String emiSupport)
    {
        this.emiSupport = emiSupport;
    }

    public String getBankCardLimit()
    {
        return bankCardLimit;
    }

    public void setBankCardLimit(String bankCardLimit)
    {
        this.bankCardLimit = bankCardLimit;
    }

    public String getPartialRefund()
    {
        return partialRefund;
    }

    public void setPartialRefund(String partialRefund)
    {
        this.partialRefund = partialRefund;
    }

    public String getInternalFraudCheck()
    {
        return internalFraudCheck;
    }

    public void setInternalFraudCheck(String internalFraudCheck)
    {
        this.internalFraudCheck = internalFraudCheck;
    }

    public String getIsMerchantRequiredForCardRegistration()
    {
        return isMerchantRequiredForCardRegistration;
    }

    public void setIsMerchantRequiredForCardRegistration(String isMerchantRequiredForCardRegistration)
    {
        this.isMerchantRequiredForCardRegistration = isMerchantRequiredForCardRegistration;
    }

    public String getCard_velocity_check()
    {
        return card_velocity_check;
    }

    public void setCard_velocity_check(String card_velocity_check)
    {
        this.card_velocity_check = card_velocity_check;
    }

    public String getMerchantOrderDetailsDisplay()
    {
        return merchantOrderDetailsDisplay;
    }

    public void setMerchantOrderDetailsDisplay(String merchantOrderDetailsDisplay)
    {
        this.merchantOrderDetailsDisplay = merchantOrderDetailsDisplay;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getLimitRouting()
    {
        return limitRouting;
    }

    public void setLimitRouting(String limitRouting)
    {
        this.limitRouting = limitRouting;
    }

    public String getMarketPlace()
    {
        return marketPlace;
    }

    public void setMarketPlace(String marketPlace)
    {
        this.marketPlace = marketPlace;
    }

    public String getMerchantAccountType()
    {
        return merchantAccountType;
    }

    public void setMerchantAccountType(String merchantAccountType)
    {
        this.merchantAccountType = merchantAccountType;
    }

    public String getVirtualCheckout()
    {
        return virtualCheckout;
    }

    public void setVirtualCheckout(String virtualCheckout)
    {
        this.virtualCheckout = virtualCheckout;
    }

    public String getIsMobileRequiredForVC()
    {
        return isMobileRequiredForVC;
    }

    public void setIsMobileRequiredForVC(String isMobileRequiredForVC)
    {
        this.isMobileRequiredForVC = isMobileRequiredForVC;
    }

    public String getIsEmailRequiredForVC()
    {
        return isEmailRequiredForVC;
    }

    public void setIsEmailRequiredForVC(String isEmailRequiredForVC)
    {
        this.isEmailRequiredForVC = isEmailRequiredForVC;
    }

    public String getIsCvvStore()
    {
        return isCvvStore;
    }

    public void setIsCvvStore(String isCvvStore)
    {
        this.isCvvStore = isCvvStore;
    }

    public String getIspurchase_inquiry_blacklist()
    {
        return ispurchase_inquiry_blacklist;
    }

    public void setIspurchase_inquiry_blacklist(String ispurchase_inquiry_blacklist)
    {
        this.ispurchase_inquiry_blacklist = ispurchase_inquiry_blacklist;
    }

    public String getIspurchase_inquiry_refund()
    {
        return ispurchase_inquiry_refund;
    }

    public void setIspurchase_inquiry_refund(String ispurchase_inquiry_refund)
    {
        this.ispurchase_inquiry_refund = ispurchase_inquiry_refund;
    }

    public String getIsfraud_determined_refund()
    {
        return isfraud_determined_refund;
    }

    public void setIsfraud_determined_refund(String isfraud_determined_refund)
    {
        this.isfraud_determined_refund = isfraud_determined_refund;
    }

    public String getIsfraud_determined_blacklist()
    {
        return isfraud_determined_blacklist;
    }

    public void setIsfraud_determined_blacklist(String isfraud_determined_blacklist)
    {
        this.isfraud_determined_blacklist = isfraud_determined_blacklist;
    }

    public String getIsdispute_initiated_refund()
    {
        return isdispute_initiated_refund;
    }

    public void setIsdispute_initiated_refund(String isdispute_initiated_refund)
    {
        this.isdispute_initiated_refund = isdispute_initiated_refund;
    }

    public String getIsdispute_initiated_blacklist()
    {
        return isdispute_initiated_blacklist;
    }

    public void setIsdispute_initiated_blacklist(String isdispute_initiated_blacklist)
    {
        this.isdispute_initiated_blacklist = isdispute_initiated_blacklist;
    }

    public String getIsstop_payment_blacklist()
    {
        return isstop_payment_blacklist;
    }

    public void setIsstop_payment_blacklist(String isstop_payment_blacklist)
    {
        this.isstop_payment_blacklist = isstop_payment_blacklist;
    }

    public String getIsexception_file_listing_blacklist()
    {
        return isexception_file_listing_blacklist;
    }

    public void setIsexception_file_listing_blacklist(String isexception_file_listing_blacklist)
    {
        this.isexception_file_listing_blacklist = isexception_file_listing_blacklist;
    }

    public String getReconciliationNotification()
    {
        return reconciliationNotification;
    }

    public void setReconciliationNotification(String reconciliationNotification)
    {
        this.reconciliationNotification = reconciliationNotification;
    }

    public String getIsUniqueOrderIdRequired()
    {
        return isUniqueOrderIdRequired;
    }

    public void setIsUniqueOrderIdRequired(String isUniqueOrderIdRequired)
    {
        this.isUniqueOrderIdRequired = isUniqueOrderIdRequired;
    }


    public String getPayout_amount_limit_check()
    {
        return payout_amount_limit_check;
    }

    public void setPayout_amount_limit_check(String payout_amount_limit_check)
    {
        this.payout_amount_limit_check = payout_amount_limit_check;
    }

    public String getSuccessReconMail()
    {
        return successReconMail;
    }

    public void setSuccessReconMail(String successReconMail)
    {
        this.successReconMail = successReconMail;
    }

    public String getRefundReconMail()
    {
        return refundReconMail;
    }

    public void setRefundReconMail(String refundReconMail)
    {
        this.refundReconMail = refundReconMail;
    }

    public String getChargebackReconMail()
    {
        return chargebackReconMail;
    }

    public void setChargebackReconMail(String chargebackReconMail)
    {
        this.chargebackReconMail = chargebackReconMail;
    }

    public String getPayoutReconMail()
    {
        return payoutReconMail;
    }

    public void setPayoutReconMail(String payoutReconMail)
    {
        this.payoutReconMail = payoutReconMail;
    }

    public String getIsMerchantLogoBO()
    {
        return isMerchantLogoBO;
    }

    public void setIsMerchantLogoBO(String isMerchantLogoBO)
    {
        this.isMerchantLogoBO = isMerchantLogoBO;
    }

    public String getCardExpiryDateCheck()
    {
        return cardExpiryDateCheck;
    }

    public void setCardExpiryDateCheck(String cardExpiryDateCheck)
    {
        this.cardExpiryDateCheck = cardExpiryDateCheck;
    }

    public String getPayoutRouting()
    {
        return payoutRouting;
    }

    public void setPayoutRouting(String payoutRouting)
    {
        this.payoutRouting = payoutRouting;
    }

    public String getSupportNoNeeded() { return supportNoNeeded; }

    public void setSupportNoNeeded(String supportNoNeeded) { this.supportNoNeeded = supportNoNeeded; }

    public String getVpaAddressAmountLimitCheck()
    {
        return vpaAddressAmountLimitCheck;
    }

    public void setVpaAddressAmountLimitCheck(String vpaAddressAmountLimitCheck)
    {
        this.vpaAddressAmountLimitCheck = vpaAddressAmountLimitCheck;
    }

    public String getVpaAddressLimitCheck()
    {
        return vpaAddressLimitCheck;
    }

    public void setVpaAddressLimitCheck(String vpaAddressLimitCheck)
    {
        this.vpaAddressLimitCheck = vpaAddressLimitCheck;
    }

    public String getBankAccountAmountLimitCheck()
    {
        return bankAccountAmountLimitCheck;
    }

    public void setBankAccountAmountLimitCheck(String bankAccountAmountLimitCheck)
    {
        this.bankAccountAmountLimitCheck = bankAccountAmountLimitCheck;
    }

    public String getBankAccountLimitCheck()
    {
        return bankAccountLimitCheck;
    }

    public void setBankAccountLimitCheck(String bankAccountLimitCheck)
    {
        this.bankAccountLimitCheck = bankAccountLimitCheck;
    }

    public String getIsDomainWhitelisted()
    {
        return isDomainWhitelisted;
    }

    public void setIsDomainWhitelisted(String isDomainWhitelisted)
    {
        this.isDomainWhitelisted = isDomainWhitelisted;
    }

    public String getIsShareAllowed() { return isShareAllowed; }

    public void setIsShareAllowed(String isShareAllowed) { this.isShareAllowed = isShareAllowed; }

    public String getIsSignatureAllowed() { return isSignatureAllowed; }

    public void setIsSignatureAllowed(String isSignatureAllowed) { this.isSignatureAllowed = isSignatureAllowed;}

    public String getIsSaveReceiptAllowed() { return isSaveReceiptAllowed; }

    public void setIsSaveReceiptAllowed(String isSaveReceiptAllowed) { this.isSaveReceiptAllowed = isSaveReceiptAllowed; }

    public String getDefaultLanguage() { return defaultLanguage; }

    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }

    public String getIsOTPRequired()
    {
        return isOTPRequired;
    }

    public void setIsOTPRequired(String isOTPRequired)
    {
        this.isOTPRequired = isOTPRequired;
    }

    public String getIsCardStorageRequired()
    {
        return isCardStorageRequired;
    }

    public void setIsCardStorageRequired(String isCardStorageRequired)
    {
        this.isCardStorageRequired = isCardStorageRequired;
    }

    public String getCustomerTransactionSuccessfulMail()
    {
        return customerTransactionSuccessfulMail;
    }

    public void setCustomerTransactionSuccessfulMail(String customerTransactionSuccessfulMail)
    {
        this.customerTransactionSuccessfulMail = customerTransactionSuccessfulMail;
    }

    public String getAmountLimitCheckTerminalLevel()
    {
        return amountLimitCheckTerminalLevel;
    }

    public void setAmountLimitCheckTerminalLevel(String amountLimitCheckTerminalLevel)
    {
        this.amountLimitCheckTerminalLevel = amountLimitCheckTerminalLevel;
    }
}
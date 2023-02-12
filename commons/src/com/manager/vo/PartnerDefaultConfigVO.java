package com.manager.vo;

/**
 * Created by Mahima on 3/12/2018.
 */
public class PartnerDefaultConfigVO
{
    String activation;
    String merchantInterfaceAccess;
    String isExcessCaptureAllowed;
    String flightMode;
    String isService;
    String autoRedirect;
    String mastercardSupported;
    String binService;
    String dashboardAccess;
    String accountingAccess;
    String settingAccess;
    String transactionsAccess;
    String invoicingAccess;
    String virtualTerminalAccess;
    String merchantMgtAccess;
    String isAppManagerActivate;
    String template;
    String hasPaid;
    String hrAlertProof;
    String dataMismatchProof;
    String vbv ;
    String custReminderMail;
    String modifyMerchantDetails;
    String modifyCompanyDetails;
    String hrparameterised;
    String isPharma;
    String isValidateEmail;
    String dailyAmountLimit;
    String monthlyAmountLimit;
    String dailyCardLimit;
    String weeklyCardLimit;
    String monthlyCardLimit;
    String checkLimit;
    String isPoweredBy;
    String invoiceTemplate;
    String isWhiteListed;
    String isMerchantLogo;
    String isRefund;
    String isPodRequired;
    String isIpWhiteListed;
    String autoSelectTerminal;
    String weeklyAmountLimit;
    String isPciLogo;
    String isCardRegistrationAllowed;
    String isRecurring;
    String isRestrictedTicket;
    String isTokenizationAllowed;
    String tokenValidDays;
    String isAddressDetailsRequired;
    String blacklistTransaction;
    String onlineFraudCheck;
    String merchantEmailSent;
    String isSplitPayment;
    String splitPaymentType;
    String refundAllowedDays;
    String chargebackAllowedDays;
    String isCardEncryptionEnable;
    String isRestWhiteListed;
    String merchantSmsActivation;
    String customerSmsActivation;
    String emailLimitEnabled;
    String ipWhiteListInvoice;
    String addressValidationInvoice;
    String expDateOffset;
    String isEmailVerified;
    String supportSection;
    String cardWhiteListLevel;
    String settingsFraudRuleConfigAccess;
    String settingsMerchantConfigAccess;
    String accountsAccountSummaryAccess;
    String accountsChargesSummaryAccess;
    String accountsTransactionSummaryAccess;
    String accountsReportsSummaryAccess;
    String settingsMerchantProfileAccess;
    String settingsOrganisationProfileAccess;
    String settingsCheckoutPageAccess;
    String settingsGenerateKeyAccess;
    String settingsInvoiceConfigAccess;
    String transMgtTransactionAccess;
    String transMgtCaptureAccess;
    String transMgtReversalAccess;
    String transMgtPayoutAccess;
    String invoiceGenerateAccess;
    String invoiceHistoryAccess;
    String tokenMgtRegistrationHistoryAccess;
    String tokenMgtRegisterCardAccess;
    String merchantMgtUserManagementAccess;
    String cardCheckLimit;
    String cardTransactionLimit;
    String dailyCardAmountLimit;
    String monthlyCardAmountLimit;
    String multiCurrencySupport;
    String refundDailyLimit;
    String maxScoreAllowed;
    String maxScoreAutoReversal;
    String weeklyCardAmountLimit;
    String ipValidationRequired;
    String isPartnerLogo;
    String binRouting;
    String vbvLogo;
    String masterSecureLogo;
    String personalInfoDisplay;
    String personalInfoValidation;
    String hostedPaymentPage;
    String chargebackMailSend;
    String settingWhiteListDetails;
    String settingBlacklistDetails;
    String emiConfiguration;
    String emiSupport;
    String blacklistCountryIp;
    String blacklistCountryBin;
    String blacklistCountryBinIp;
    String internalFraudCheck;
    String limitRouting;
    String checkoutTimer;
    String checkoutTimerTime;
    String marketplace;
    String cardVelocityCheck;
    String merchantOrderDetails;
    String isSecurityLogo;
    String rejectedTransaction;
    String virtualCheckOut;
    String isVirtualCheckoutAllowed;
    String isMobileAllowedForVC;
    String isEmailAllowedForVC;
    String isCvvStore;
    String actionExecutorId;
    String actionExecutorName;
    String isMultipleRefund;
    String reconciliationNotification;
    String transactionNotification;
    String refundNotification;
    String chargebackNotification;

    //Merchant Notification Mail
    String merchantRegistrationMail;
    String merchantChangePassword;
    String merchantChangeProfile;
    String transactionSuccessfulMail;
    String transactionFailMail;
    String transactionCapture;
    String transactionPayoutSuccess;
    String transactionPayoutFail;
    String refundMail;
    String chargebackMail;
    String transactionInvoice;
    String cardRegistration;
    String payoutReport;
    String monitoringAlertMail;
    String monitoringSuspensionMail;
    String highRiskRefunds;
    String fraudFailedTxn;
    String dailyFraudReport;
    //Customer Notification Mail
    String customerTransactionSuccessfulMail;
    String customerTransactionFailMail;
    String customerTransactionPayoutSuccess;
    String customerTransactionPayoutFail;
    String customerRefundMail;
    String customerTokenizationMail;
    String isUniqueOrderIdRequired;
    String emailTemplateLang;
    String successReconMail;
    String refundReconMail;
    String chargebackReconMail;
    String payoutReconMail;
    String isMerchantLogoBO;
    String cardExpiryDateCheck;
    String payoutNotification;
    String inquiryNotification;
    String supportNoNeeded;
    String vpaAddressLimitCheck;
    String vpaAddressDailyCount;
    String vpaAddressAmountLimitCheck;
    String vpaAddressDailyAmountLimit;
    String payoutBankAccountNoLimitCheck;
    String bankAccountNoDailyCount;
    String payoutBankAccountNoAmountLimitCheck;
    String bankAccountNoDailyAmountLimit;

    String isShareAllowed;
    String isSignatureAllowed;
    String isSaveReceiptAllowed;
    String defaultLanguage;
    String isDomainWhitelisted;
    String customerIpLimitCheck;
    String customerIpDailyCount;
    String customerIpAmountLimitCheck;
    String customerIpDailyAmountLimit;

    String customerNameLimitCheck;
    String customerNameDailyCount;
    String customerNameAmountLimitCheck;
    String customerNameDailyAmountLimit;

    String customerEmailLimitCheck;
    String customerEmailDailyCount;
    String customerEmailAmountLimitCheck;
    String customerEmailDailyAmountLimit;

    String customerPhoneLimitCheck;
    String customerPhoneDailyCount;
    String customerPhoneAmountLimitCheck;
    String customerPhoneDailyAmountLimit;
    String paybylink;
    String transMgtPayoutTransaction;
    String isOTPRequired;
    String isCardStorageRequired;
    String vpaAddressMonthlyCount;
    String vpaAddressMonthlyAmountLimit;
    String customerEmailMonthlyCount;
    String customerEmailMonthlyAmountLimit;
    String customerPhoneMonthlyCount;
    String customerPhoneMonthlyAmountLimit;
    String bankAccountNoMonthlyCount;
    String bankAccountNoMonthlyAmountLimit;

    String customerIpMonthlyCount;
    String customerIpMonthlyAmountLimit;
    String customerNameMonthlyCount;
    String customerNameMonthlyAmountLimit;
    String merchantCheckoutConfig;
    String merchant_verify_otp;


    public String getIsUniqueOrderIdRequired()
    {
        return isUniqueOrderIdRequired;
    }

    public void setIsUniqueOrderIdRequired(String isUniqueOrderIdRequired)
    {
        this.isUniqueOrderIdRequired = isUniqueOrderIdRequired;
    }
    public String getIsPartialRefund()
    {
        return isPartialRefund;
    }

    public void setIsPartialRefund(String isPartialRefund)
    {
        this.isPartialRefund = isPartialRefund;
    }

    public String getIsMultipleRefund()
    {
        return isMultipleRefund;
    }

    public void setIsMultipleRefund(String isMultipleRefund)
    {
        this.isMultipleRefund = isMultipleRefund;
    }

    String isPartialRefund;

    public String getVirtualCheckOut()
    {
        return virtualCheckOut;
    }

    public void setVirtualCheckOut(String virtualCheckOut)
    {
        this.virtualCheckOut = virtualCheckOut;
    }

    public String getActivation()
    {
        return activation;
    }

    public void setActivation(String activation)
    {
        this.activation = activation;
    }

    public String getMerchantInterfaceAccess()
    {
        return merchantInterfaceAccess;
    }

    public void setMerchantInterfaceAccess(String merchantInterfaceAccess)
    {
        this.merchantInterfaceAccess = merchantInterfaceAccess;
    }

    public String getIsExcessCaptureAllowed()
    {
        return isExcessCaptureAllowed;
    }

    public void setIsExcessCaptureAllowed(String isExcessCaptureAllowed)
    {
        this.isExcessCaptureAllowed = isExcessCaptureAllowed;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
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

    public String getMastercardSupported()
    {
        return mastercardSupported;
    }

    public void setMastercardSupported(String mastercardSupported)
    {
        this.mastercardSupported = mastercardSupported;
    }

    public String getBinService()
    {
        return binService;
    }

    public void setBinService(String binService)
    {
        this.binService = binService;
    }

    public String getDashboardAccess()
    {
        return dashboardAccess;
    }

    public void setDashboardAccess(String dashboardAccess)
    {
        this.dashboardAccess = dashboardAccess;
    }

    public String getAccountingAccess()
    {
        return accountingAccess;
    }

    public void setAccountingAccess(String accountingAccess)
    {
        this.accountingAccess = accountingAccess;
    }

    public String getSettingAccess()
    {
        return settingAccess;
    }

    public void setSettingAccess(String settingAccess)
    {
        this.settingAccess = settingAccess;
    }

    public String getTransactionsAccess()
    {
        return transactionsAccess;
    }

    public void setTransactionsAccess(String transactionsAccess)
    {
        this.transactionsAccess = transactionsAccess;
    }

    public String getInvoicingAccess()
    {
        return invoicingAccess;
    }

    public void setInvoicingAccess(String invoicingAccess)
    {
        this.invoicingAccess = invoicingAccess;
    }

    public String getVirtualTerminalAccess()
    {
        return virtualTerminalAccess;
    }

    public void setVirtualTerminalAccess(String virtualTerminalAccess)
    {
        this.virtualTerminalAccess = virtualTerminalAccess;
    }

    public String getMerchantMgtAccess()
    {
        return merchantMgtAccess;
    }

    public void setMerchantMgtAccess(String merchantMgtAccess)
    {
        this.merchantMgtAccess = merchantMgtAccess;
    }

    public String getIsAppManagerActivate()
    {
        return isAppManagerActivate;
    }

    public void setIsAppManagerActivate(String isAppManagerActivate)
    {
        this.isAppManagerActivate = isAppManagerActivate;
    }

    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getHasPaid()
    {
        return hasPaid;
    }

    public void setHasPaid(String hasPaid)
    {
        this.hasPaid = hasPaid;
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

    public String getVbv()
    {
        return vbv;
    }

    public void setVbv(String vbv)
    {
        this.vbv = vbv;
    }

    public String getCustReminderMail()
    {
        return custReminderMail;
    }

    public void setCustReminderMail(String custReminderMail)
    {
        this.custReminderMail = custReminderMail;
    }

    public String getModifyMerchantDetails()
    {
        return modifyMerchantDetails;
    }

    public void setModifyMerchantDetails(String modifyMerchantDetails)
    {
        this.modifyMerchantDetails = modifyMerchantDetails;
    }

    public String getModifyCompanyDetails()
    {
        return modifyCompanyDetails;
    }

    public void setModifyCompanyDetails(String modifyCompanyDetails)
    {
        this.modifyCompanyDetails = modifyCompanyDetails;
    }

    public String getHrparameterised()
    {
        return hrparameterised;
    }

    public void setHrparameterised(String hrparameterised)
    {
        this.hrparameterised = hrparameterised;
    }

    public String getIsPharma()
    {
        return isPharma;
    }

    public void setIsPharma(String isPharma)
    {
        this.isPharma = isPharma;
    }

    public String getIsValidateEmail()
    {
        return isValidateEmail;
    }

    public void setIsValidateEmail(String isValidateEmail)
    {
        this.isValidateEmail = isValidateEmail;
    }

    public String getDailyAmountLimit()
    {
        return dailyAmountLimit;
    }

    public void setDailyAmountLimit(String dailyAmountLimit)
    {
        this.dailyAmountLimit = dailyAmountLimit;
    }

    public String getMonthlyAmountLimit()
    {
        return monthlyAmountLimit;
    }

    public void setMonthlyAmountLimit(String monthlyAmountLimit)
    {
        this.monthlyAmountLimit = monthlyAmountLimit;
    }

    public String getDailyCardLimit()
    {
        return dailyCardLimit;
    }

    public void setDailyCardLimit(String dailyCardLimit)
    {
        this.dailyCardLimit = dailyCardLimit;
    }

    public String getWeeklyCardLimit()
    {
        return weeklyCardLimit;
    }

    public void setWeeklyCardLimit(String weeklyCardLimit)
    {
        this.weeklyCardLimit = weeklyCardLimit;
    }

    public String getMonthlyCardLimit()
    {
        return monthlyCardLimit;
    }

    public void setMonthlyCardLimit(String monthlyCardLimit)
    {
        this.monthlyCardLimit = monthlyCardLimit;
    }

    public String getCheckLimit()
    {
        return checkLimit;
    }

    public void setCheckLimit(String checkLimit)
    {
        this.checkLimit = checkLimit;
    }

    public String getIsPoweredBy()
    {
        return isPoweredBy;
    }

    public void setIsPoweredBy(String isPoweredBy)
    {
        this.isPoweredBy = isPoweredBy;
    }

    public String getInvoiceTemplate()
    {
        return invoiceTemplate;
    }

    public void setInvoiceTemplate(String invoiceTemplate)
    {
        this.invoiceTemplate = invoiceTemplate;
    }

    public String getIsWhiteListed()
    {
        return isWhiteListed;
    }

    public void setIsWhiteListed(String isWhiteListed)
    {
        this.isWhiteListed = isWhiteListed;
    }

    public String getIsMerchantLogo()
    {
        return isMerchantLogo;
    }

    public void setIsMerchantLogo(String isMerchantLogo)
    {
        this.isMerchantLogo = isMerchantLogo;
    }

    public String getIsRefund()
    {
        return isRefund;
    }

    public void setIsRefund(String isRefund)
    {
        this.isRefund = isRefund;
    }

    public String getIsPodRequired()
    {
        return isPodRequired;
    }

    public void setIsPodRequired(String isPodRequired)
    {
        this.isPodRequired = isPodRequired;
    }

    public String getIsIpWhiteListed()
    {
        return isIpWhiteListed;
    }

    public void setIsIpWhiteListed(String isIpWhiteListed)
    {
        this.isIpWhiteListed = isIpWhiteListed;
    }

    public String getAutoSelectTerminal()
    {
        return autoSelectTerminal;
    }

    public void setAutoSelectTerminal(String autoSelectTerminal)
    {
        this.autoSelectTerminal = autoSelectTerminal;
    }

    public String getWeeklyAmountLimit()
    {
        return weeklyAmountLimit;
    }

    public void setWeeklyAmountLimit(String weeklyAmountLimit)
    {
        this.weeklyAmountLimit = weeklyAmountLimit;
    }

    public String getIsPciLogo()
    {
        return isPciLogo;
    }

    public void setIsPciLogo(String isPciLogo)
    {
        this.isPciLogo = isPciLogo;
    }

    public String getIsCardRegistrationAllowed()
    {
        return isCardRegistrationAllowed;
    }

    public void setIsCardRegistrationAllowed(String isCardRegistrationAllowed)
    {
        this.isCardRegistrationAllowed = isCardRegistrationAllowed;
    }

    public String getIsRecurring()
    {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring)
    {
        this.isRecurring = isRecurring;
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

    public String getTokenValidDays()
    {
        return tokenValidDays;
    }

    public void setTokenValidDays(String tokenValidDays)
    {
        this.tokenValidDays = tokenValidDays;
    }

    public String getIsAddressDetailsRequired()
    {
        return isAddressDetailsRequired;
    }

    public void setIsAddressDetailsRequired(String isAddressDetailsRequired)
    {
        this.isAddressDetailsRequired = isAddressDetailsRequired;
    }

    public String getBlacklistTransaction()
    {
        return blacklistTransaction;
    }

    public void setBlacklistTransaction(String blacklistTransaction)
    {
        this.blacklistTransaction = blacklistTransaction;
    }

    public String getOnlineFraudCheck()
    {
        return onlineFraudCheck;
    }

    public void setOnlineFraudCheck(String onlineFraudCheck)
    {
        this.onlineFraudCheck = onlineFraudCheck;
    }

    public String getMerchantEmailSent()
    {
        return merchantEmailSent;
    }

    public void setMerchantEmailSent(String merchantEmailSent)
    {
        this.merchantEmailSent = merchantEmailSent;
    }

    public String getIsSplitPayment()
    {
        return isSplitPayment;
    }

    public void setIsSplitPayment(String isSplitPayment)
    {
        this.isSplitPayment = isSplitPayment;
    }

    public String getSplitPaymentType()
    {
        return splitPaymentType;
    }

    public void setSplitPaymentType(String splitPaymentType)
    {
        this.splitPaymentType = splitPaymentType;
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

    public String getIsCardEncryptionEnable()
    {
        return isCardEncryptionEnable;
    }

    public void setIsCardEncryptionEnable(String isCardEncryptionEnable)
    {
        this.isCardEncryptionEnable = isCardEncryptionEnable;
    }

    public String getIsRestWhiteListed()
    {
        return isRestWhiteListed;
    }

    public void setIsRestWhiteListed(String isRestWhiteListed)
    {
        this.isRestWhiteListed = isRestWhiteListed;
    }

    public String getMerchantSmsActivation()
    {
        return merchantSmsActivation;
    }

    public void setMerchantSmsActivation(String merchantSmsActivation)
    {
        this.merchantSmsActivation = merchantSmsActivation;
    }

    public String getCustomerSmsActivation()
    {
        return customerSmsActivation;
    }

    public void setCustomerSmsActivation(String customerSmsActivation)
    {
        this.customerSmsActivation = customerSmsActivation;
    }

    public String getEmailLimitEnabled()
    {
        return emailLimitEnabled;
    }

    public void setEmailLimitEnabled(String emailLimitEnabled)
    {
        this.emailLimitEnabled = emailLimitEnabled;
    }

    public String getIpWhiteListInvoice()
    {
        return ipWhiteListInvoice;
    }

    public void setIpWhiteListInvoice(String ipWhiteListInvoice)
    {
        this.ipWhiteListInvoice = ipWhiteListInvoice;
    }

    public String getAddressValidationInvoice()
    {
        return addressValidationInvoice;
    }

    public void setAddressValidationInvoice(String addressValidationInvoice)
    {
        this.addressValidationInvoice = addressValidationInvoice;
    }

    public String getExpDateOffset()
    {
        return expDateOffset;
    }

    public void setExpDateOffset(String expDateOffset)
    {
        this.expDateOffset = expDateOffset;
    }

    public String getIsEmailVerified()
    {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified)
    {
        this.isEmailVerified = isEmailVerified;
    }

    public String getSupportSection()
    {
        return supportSection;
    }

    public void setSupportSection(String supportSection)
    {
        this.supportSection = supportSection;
    }

    public String getCardWhiteListLevel()
    {
        return cardWhiteListLevel;
    }

    public void setCardWhiteListLevel(String cardWhiteListLevel)
    {
        this.cardWhiteListLevel = cardWhiteListLevel;
    }

    public String getSettingsFraudRuleConfigAccess()
    {
        return settingsFraudRuleConfigAccess;
    }

    public void setSettingsFraudRuleConfigAccess(String settingsFraudRuleConfigAccess)
    {
        this.settingsFraudRuleConfigAccess = settingsFraudRuleConfigAccess;
    }

    public String getSettingsMerchantConfigAccess()
    {
        return settingsMerchantConfigAccess;
    }

    public void setSettingsMerchantConfigAccess(String settingsMerchantConfigAccess)
    {
        this.settingsMerchantConfigAccess = settingsMerchantConfigAccess;
    }

    public String getAccountsAccountSummaryAccess()
    {
        return accountsAccountSummaryAccess;
    }

    public void setAccountsAccountSummaryAccess(String accountsAccountSummaryAccess)
    {
        this.accountsAccountSummaryAccess = accountsAccountSummaryAccess;
    }

    public String getAccountsChargesSummaryAccess()
    {
        return accountsChargesSummaryAccess;
    }

    public void setAccountsChargesSummaryAccess(String accountsChargesSummaryAccess)
    {
        this.accountsChargesSummaryAccess = accountsChargesSummaryAccess;
    }

    public String getAccountsTransactionSummaryAccess()
    {
        return accountsTransactionSummaryAccess;
    }

    public void setAccountsTransactionSummaryAccess(String accountsTransactionSummaryAccess)
    {
        this.accountsTransactionSummaryAccess = accountsTransactionSummaryAccess;
    }

    public String getAccountsReportsSummaryAccess()
    {
        return accountsReportsSummaryAccess;
    }

    public void setAccountsReportsSummaryAccess(String accountsReportsSummaryAccess)
    {
        this.accountsReportsSummaryAccess = accountsReportsSummaryAccess;
    }

    public String getSettingsMerchantProfileAccess()
    {
        return settingsMerchantProfileAccess;
    }

    public void setSettingsMerchantProfileAccess(String settingsMerchantProfileAccess)
    {
        this.settingsMerchantProfileAccess = settingsMerchantProfileAccess;
    }

    public String getSettingsOrganisationProfileAccess()
    {
        return settingsOrganisationProfileAccess;
    }

    public void setSettingsOrganisationProfileAccess(String settingsOrganisationProfileAccess)
    {
        this.settingsOrganisationProfileAccess = settingsOrganisationProfileAccess;
    }

    public String getSettingsCheckoutPageAccess()
    {
        return settingsCheckoutPageAccess;
    }

    public void setSettingsCheckoutPageAccess(String settingsCheckoutPageAccess)
    {
        this.settingsCheckoutPageAccess = settingsCheckoutPageAccess;
    }

    public String getSettingsGenerateKeyAccess()
    {
        return settingsGenerateKeyAccess;
    }

    public void setSettingsGenerateKeyAccess(String settingsGenerateKeyAccess)
    {
        this.settingsGenerateKeyAccess = settingsGenerateKeyAccess;
    }

    public String getSettingsInvoiceConfigAccess()
    {
        return settingsInvoiceConfigAccess;
    }

    public void setSettingsInvoiceConfigAccess(String settingsInvoiceConfigAccess)
    {
        this.settingsInvoiceConfigAccess = settingsInvoiceConfigAccess;
    }

    public String getTransMgtTransactionAccess()
    {
        return transMgtTransactionAccess;
    }

    public void setTransMgtTransactionAccess(String transMgtTransactionAccess)
    {
        this.transMgtTransactionAccess = transMgtTransactionAccess;
    }

    public String getTransMgtCaptureAccess()
    {
        return transMgtCaptureAccess;
    }

    public void setTransMgtCaptureAccess(String transMgtCaptureAccess)
    {
        this.transMgtCaptureAccess = transMgtCaptureAccess;
    }

    public String getTransMgtReversalAccess()
    {
        return transMgtReversalAccess;
    }

    public void setTransMgtReversalAccess(String transMgtReversalAccess)
    {
        this.transMgtReversalAccess = transMgtReversalAccess;
    }

    public String getTransMgtPayoutAccess()
    {
        return transMgtPayoutAccess;
    }

    public void setTransMgtPayoutAccess(String transMgtPayoutAccess)
    {
        this.transMgtPayoutAccess = transMgtPayoutAccess;
    }

    public String getInvoiceGenerateAccess()
    {
        return invoiceGenerateAccess;
    }

    public void setInvoiceGenerateAccess(String invoiceGenerateAccess)
    {
        this.invoiceGenerateAccess = invoiceGenerateAccess;
    }

    public String getInvoiceHistoryAccess()
    {
        return invoiceHistoryAccess;
    }

    public void setInvoiceHistoryAccess(String invoiceHistoryAccess)
    {
        this.invoiceHistoryAccess = invoiceHistoryAccess;
    }

    public String getTokenMgtRegistrationHistoryAccess()
    {
        return tokenMgtRegistrationHistoryAccess;
    }

    public void setTokenMgtRegistrationHistoryAccess(String tokenMgtRegistrationHistoryAccess)
    {
        this.tokenMgtRegistrationHistoryAccess = tokenMgtRegistrationHistoryAccess;
    }

    public String getTokenMgtRegisterCardAccess()
    {
        return tokenMgtRegisterCardAccess;
    }

    public void setTokenMgtRegisterCardAccess(String tokenMgtRegisterCardAccess)
    {
        this.tokenMgtRegisterCardAccess = tokenMgtRegisterCardAccess;
    }

    public String getMerchantMgtUserManagementAccess()
    {
        return merchantMgtUserManagementAccess;
    }

    public void setMerchantMgtUserManagementAccess(String merchantMgtUserManagementAccess)
    {
        this.merchantMgtUserManagementAccess = merchantMgtUserManagementAccess;
    }

    public String getCardCheckLimit()
    {
        return cardCheckLimit;
    }

    public void setCardCheckLimit(String cardCheckLimit)
    {
        this.cardCheckLimit = cardCheckLimit;
    }

    public String getCardTransactionLimit()
    {
        return cardTransactionLimit;
    }

    public void setCardTransactionLimit(String cardTransactionLimit)
    {
        this.cardTransactionLimit = cardTransactionLimit;
    }

    public String getDailyCardAmountLimit()
    {
        return dailyCardAmountLimit;
    }

    public void setDailyCardAmountLimit(String dailyCardAmountLimit)
    {
        this.dailyCardAmountLimit = dailyCardAmountLimit;
    }

    public String getMonthlyCardAmountLimit()
    {
        return monthlyCardAmountLimit;
    }

    public void setMonthlyCardAmountLimit(String monthlyCardAmountLimit)
    {
        this.monthlyCardAmountLimit = monthlyCardAmountLimit;
    }

    public String getMultiCurrencySupport()
    {
        return multiCurrencySupport;
    }

    public void setMultiCurrencySupport(String multiCurrencySupport)
    {
        this.multiCurrencySupport = multiCurrencySupport;
    }

    public String getRefundDailyLimit()
    {
        return refundDailyLimit;
    }

    public void setRefundDailyLimit(String refundDailyLimit)
    {
        this.refundDailyLimit = refundDailyLimit;
    }

    public String getMaxScoreAllowed()
    {
        return maxScoreAllowed;
    }

    public void setMaxScoreAllowed(String maxScoreAllowed)
    {
        this.maxScoreAllowed = maxScoreAllowed;
    }

    public String getMaxScoreAutoReversal()
    {
        return maxScoreAutoReversal;
    }

    public void setMaxScoreAutoReversal(String maxScoreAutoReversal)
    {
        this.maxScoreAutoReversal = maxScoreAutoReversal;
    }

    public String getWeeklyCardAmountLimit()
    {
        return weeklyCardAmountLimit;
    }

    public void setWeeklyCardAmountLimit(String weeklyCardAmountLimit)
    {
        this.weeklyCardAmountLimit = weeklyCardAmountLimit;
    }

    public String getIpValidationRequired()
    {
        return ipValidationRequired;
    }

    public void setIpValidationRequired(String ipValidationRequired)
    {
        this.ipValidationRequired = ipValidationRequired;
    }

    public String getIsPartnerLogo()
    {
        return isPartnerLogo;
    }

    public void setIsPartnerLogo(String isPartnerLogo)
    {
        this.isPartnerLogo = isPartnerLogo;
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

    public String getChargebackMailSend()
    {
        return chargebackMailSend;
    }

    public void setChargebackMailSend(String chargebackMailSend)
    {
        this.chargebackMailSend = chargebackMailSend;
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

    public String getSettingWhiteListDetails()
    {
        return settingWhiteListDetails;
    }

    public void setSettingWhiteListDetails(String settingWhiteListDetails)
    {
        this.settingWhiteListDetails = settingWhiteListDetails;
    }

    public String getSettingBlacklistDetails()
    {
        return settingBlacklistDetails;
    }

    public void setSettingBlacklistDetails(String settingBlacklistDetails)
    {
        this.settingBlacklistDetails = settingBlacklistDetails;
    }

    public String getEmiConfiguration()
    {
        return emiConfiguration;
    }

    public void setEmiConfiguration(String emiConfiguration)
    {
        this.emiConfiguration = emiConfiguration;
    }

    public String getEmiSupport()
    {
        return emiSupport;
    }

    public void setEmiSupport(String emiSupport)
    {
        this.emiSupport = emiSupport;
    }

    public String getBlackListCountryIp()
    {
        return blacklistCountryIp;
    }

    public void setBlackListCountryIp(String blacklistCountryIp)
    {
        this.blacklistCountryIp = blacklistCountryIp;
    }

    public String getBlackListCountryBin()
    {
        return blacklistCountryBin;
    }

    public void setBlackListCountryBin(String blacklistCountryBin)
    {
        this.blacklistCountryBin = blacklistCountryBin;
    }

    public String getBlackListCountryBinIp()
    {
        return blacklistCountryBinIp;
    }

    public void setBlackListCountryBinIp(String blacklistCountryBinIp) {this.blacklistCountryBinIp = blacklistCountryBinIp;}

    public String getInternalFraudCheck()
    {
        return internalFraudCheck;
    }

    public void setInternalFraudCheck(String internalFraudCheck)
    {
        this.internalFraudCheck = internalFraudCheck;
    }

    public String getLimitRouting()
    {
        return limitRouting;
    }

    public void setLimitRouting(String limitRouting)
    {
        this.limitRouting = limitRouting;
    }

    public String getCheckoutTimer()
    {
        return checkoutTimer;
    }

    public void setCheckoutTimer(String checkoutTimer)
    {
        this.checkoutTimer = checkoutTimer;
    }

    public String getCheckoutTimerTime()
    {
        return checkoutTimerTime;
    }

    public void setCheckoutTimerTime(String checkoutTimerTime)
    {
        this.checkoutTimerTime = checkoutTimerTime;
    }

    public String getMarketplace()
    {
        return marketplace;
    }

    public void setMarketplace(String marketplace)
    {
        this.marketplace = marketplace;
    }

    public String getCardVelocityCheck()
    {
        return cardVelocityCheck;
    }

    public void setCardVelocityCheck(String cardVelocityCheck)
    {
        this.cardVelocityCheck = cardVelocityCheck;
    }

    public String getMerchantOrderDetails()
    {
        return merchantOrderDetails;
    }

    public void setMerchantOrderDetails(String merchantOrderDetails)
    {
        this.merchantOrderDetails = merchantOrderDetails;
    }

    public String getIsSecurityLogo()
    {
        return isSecurityLogo;
    }

    public void setIsSecurityLogo(String isSecurityLogo)
    {
        this.isSecurityLogo = isSecurityLogo;
    }

    public String getRejectedTransaction()
    {
        return rejectedTransaction;
    }

    public void setRejectedTransaction(String rejectedTransaction)
    {
        this.rejectedTransaction = rejectedTransaction;
    }

    public String getIsVirtualCheckoutAllowed()
    {
        return isVirtualCheckoutAllowed;
    }

    public void setIsVirtualCheckoutAllowed(String isVirtualCheckoutAllowed)
    {
        this.isVirtualCheckoutAllowed = isVirtualCheckoutAllowed;
    }

    public String getIsMobileAllowedForVC()
    {
        return isMobileAllowedForVC;
    }

    public void setIsMobileAllowedForVC(String isMobileAllowedForVC)
    {
        this.isMobileAllowedForVC = isMobileAllowedForVC;
    }

    public String getIsEmailAllowedForVC()
    {
        return isEmailAllowedForVC;
    }

    public void setIsEmailAllowedForVC(String isEmailAllowedForVC)
    {
        this.isEmailAllowedForVC = isEmailAllowedForVC;
    }

    public String getIsCvvStore()
    {
        return isCvvStore;
    }

    public void setIsCvvStore(String isCvvStore)
    {
        this.isCvvStore = isCvvStore;
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

    public String getReconciliationNotification()
    {
        return reconciliationNotification;
    }

    public void setReconciliationNotification(String reconciliationNotification)
    {
        this.reconciliationNotification = reconciliationNotification;
    }

    public String getTransactionNotification()
    {
        return transactionNotification;
    }

    public void setTransactionNotification(String transactionNotification)
    {
        this.transactionNotification = transactionNotification;
    }

    public String getRefundNotification()
    {
        return refundNotification;
    }

    public void setRefundNotification(String refundNotification)
    {
        this.refundNotification = refundNotification;
    }

    public String getChargebackNotification()
    {
        return chargebackNotification;
    }

    public void setChargebackNotification(String chargebackNotification)
    {
        this.chargebackNotification = chargebackNotification;
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

    public String getMerchantRegistrationMail()
    {
        return merchantRegistrationMail;
    }

    public void setMerchantRegistrationMail(String merchantRegistrationMail)
    {
        this.merchantRegistrationMail = merchantRegistrationMail;
    }

    public String getMerchantChangePassword()
    {
        return merchantChangePassword;
    }

    public void setMerchantChangePassword(String merchantChangePassword)
    {
        this.merchantChangePassword = merchantChangePassword;
    }

    public String getMerchantChangeProfile()
    {
        return merchantChangeProfile;
    }

    public void setMerchantChangeProfile(String merchantChangeProfile)
    {
        this.merchantChangeProfile = merchantChangeProfile;
    }

    public String getTransactionSuccessfulMail()
    {
        return transactionSuccessfulMail;
    }

    public void setTransactionSuccessfulMail(String transactionSuccessfulMail)
    {
        this.transactionSuccessfulMail = transactionSuccessfulMail;
    }

    public String getTransactionFailMail()
    {
        return transactionFailMail;
    }

    public void setTransactionFailMail(String transactionFailMail)
    {
        this.transactionFailMail = transactionFailMail;
    }

    public String getTransactionCapture()
    {
        return transactionCapture;
    }

    public void setTransactionCapture(String transactionCapture)
    {
        this.transactionCapture = transactionCapture;
    }

    public String getTransactionPayoutSuccess()
    {
        return transactionPayoutSuccess;
    }

    public void setTransactionPayoutSuccess(String transactionPayoutSuccess)
    {
        this.transactionPayoutSuccess = transactionPayoutSuccess;
    }

    public String getTransactionPayoutFail()
    {
        return transactionPayoutFail;
    }

    public void setTransactionPayoutFail(String transactionPayoutFail)
    {
        this.transactionPayoutFail = transactionPayoutFail;
    }

    public String getRefundMail()
    {
        return refundMail;
    }

    public void setRefundMail(String refundMail)
    {
        this.refundMail = refundMail;
    }

    public String getChargebackMail()
    {
        return chargebackMail;
    }

    public void setChargebackMail(String chargebackMail)
    {
        this.chargebackMail = chargebackMail;
    }

    public String getTransactionInvoice()
    {
        return transactionInvoice;
    }

    public void setTransactionInvoice(String transactionInvoice)
    {
        this.transactionInvoice = transactionInvoice;
    }

    public String getCardRegistration()
    {
        return cardRegistration;
    }

    public void setCardRegistration(String cardRegistration)
    {
        this.cardRegistration = cardRegistration;
    }

    public String getPayoutReport()
    {
        return payoutReport;
    }

    public void setPayoutReport(String payoutReport)
    {
        this.payoutReport = payoutReport;
    }

    public String getMonitoringAlertMail()
    {
        return monitoringAlertMail;
    }

    public void setMonitoringAlertMail(String monitoringAlertMail)
    {
        this.monitoringAlertMail = monitoringAlertMail;
    }

    public String getMonitoringSuspensionMail()
    {
        return monitoringSuspensionMail;
    }

    public void setMonitoringSuspensionMail(String monitoringSuspensionMail)
    {
        this.monitoringSuspensionMail = monitoringSuspensionMail;
    }

    public String getHighRiskRefunds()
    {
        return highRiskRefunds;
    }

    public void setHighRiskRefunds(String highRiskRefunds)
    {
        this.highRiskRefunds = highRiskRefunds;
    }

    public String getFraudFailedTxn()
    {
        return fraudFailedTxn;
    }

    public void setFraudFailedTxn(String fraudFailedTxn)
    {
        this.fraudFailedTxn = fraudFailedTxn;
    }

    public String getDailyFraudReport()
    {
        return dailyFraudReport;
    }

    public void setDailyFraudReport(String dailyFraudReport)
    {
        this.dailyFraudReport = dailyFraudReport;
    }

    public String getCustomerTransactionSuccessfulMail()
    {
        return customerTransactionSuccessfulMail;
    }

    public void setCustomerTransactionSuccessfulMail(String customerTransactionSuccessfulMail)
    {
        this.customerTransactionSuccessfulMail = customerTransactionSuccessfulMail;
    }

    public String getCustomerTransactionFailMail()
    {
        return customerTransactionFailMail;
    }

    public void setCustomerTransactionFailMail(String customerTransactionFailMail)
    {
        this.customerTransactionFailMail = customerTransactionFailMail;
    }

    public String getCustomerTransactionPayoutSuccess()
    {
        return customerTransactionPayoutSuccess;
    }

    public void setCustomerTransactionPayoutSuccess(String customerTransactionPayoutSuccess)
    {
        this.customerTransactionPayoutSuccess = customerTransactionPayoutSuccess;
    }

    public String getCustomerTransactionPayoutFail()
    {
        return customerTransactionPayoutFail;
    }

    public void setCustomerTransactionPayoutFail(String customerTransactionPayoutFail)
    {
        this.customerTransactionPayoutFail = customerTransactionPayoutFail;
    }

    public String getCustomerRefundMail()
    {
        return customerRefundMail;
    }

    public void setCustomerRefundMail(String customerRefundMail)
    {
        this.customerRefundMail = customerRefundMail;
    }

    public String getCustomerTokenizationMail()
    {
        return customerTokenizationMail;
    }

    public void setCustomerTokenizationMail(String customerTokenizationMail)
    {
        this.customerTokenizationMail = customerTokenizationMail;
    }

    public String getEmailTemplateLang()
    {
        return emailTemplateLang;
    }

    public void setEmailTemplateLang(String emailTemplateLang)
    {
        this.emailTemplateLang = emailTemplateLang;
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

    public String getIsMerchantLogoBO() { return  isMerchantLogoBO; }

    public  void setIsMerchantLogoBO(String isMerchantLogoBO)
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

    public String getPayoutNotification()
    {
        return payoutNotification;
    }

    public void setPayoutNotification(String payoutNotification)
    {
        this.payoutNotification= payoutNotification;
    }

    public String getInquiryNotification()
    {
        return inquiryNotification;
    }
    public void setInquiryNotification(String inquiryNotification)
    {
        this.inquiryNotification=inquiryNotification;
    }

    public String getVpaAddressLimitCheck()
    {
        return vpaAddressLimitCheck;
    }
    public void setVpaAddressLimitCheck(String vpaAddressLimitCheck)
    {
        this.vpaAddressLimitCheck= vpaAddressLimitCheck;
    }
    public String getVpaAddressDailyCount()
    {
        return vpaAddressDailyCount;
    }
    public void setVpaAddressDailyCount(String vpaAddressDailyCount)
    {
        this.vpaAddressDailyCount=vpaAddressDailyCount;
    }
    public String getVpaAddressAmountLimitCheck()
    {
        return vpaAddressAmountLimitCheck;
    }
    public void setVpaAddressAmountLimitCheck(String vpaAddressAmountLimitCheck)
    {
       this.vpaAddressAmountLimitCheck= vpaAddressAmountLimitCheck;
    }
    public String getVpaAddressDailyAmountLimit()
    {
        return vpaAddressDailyAmountLimit;
    }
    public void setVpaAddressDailyAmountLimit(String vpaAddressDailyAmountLimit)
    {
        this.vpaAddressDailyAmountLimit= vpaAddressDailyAmountLimit;
    }
    public String getPayoutBankAccountNoLimitCheck()
    {
        return payoutBankAccountNoLimitCheck;
    }
    public void setPayoutBankAccountNoLimitCheck(String payoutBankAccountNoLimitCheck)
    {
        this.payoutBankAccountNoLimitCheck= payoutBankAccountNoLimitCheck;
    }
    public String getBankAccountNoDailyCount()
    {
        return bankAccountNoDailyCount;
    }
    public void setBankAccountNoDailyCount(String bankAccountNoDailyCount)
    {
        this.bankAccountNoDailyCount= bankAccountNoDailyCount;
    }
    public String getPayoutBankAccountNoAmountLimitCheck()
    {
        return payoutBankAccountNoAmountLimitCheck;
    }
    public void setPayoutBankAccountNoAmountLimitCheck(String payoutBankAccountNoAmountLimitCheck)
    {
        this.payoutBankAccountNoAmountLimitCheck= payoutBankAccountNoAmountLimitCheck;
    }
    public String getBankAccountNoDailyAmountLimit()
    {
        return bankAccountNoDailyAmountLimit;
    }
    public void setBankAccountNoDailyAmountLimit(String bankAccountNoDailyAmountLimit)
    {
        this.bankAccountNoDailyAmountLimit= bankAccountNoDailyAmountLimit;
    }
    public String getSupportNoNeeded() { return supportNoNeeded; }

    public void setSupportNoNeeded(String supportNoNeeded) { this.supportNoNeeded = supportNoNeeded; }

    public String getIsShareAllowed() { return isShareAllowed; }

    public void setIsShareAllowed(String isShareAllowed) { this.isShareAllowed = isShareAllowed; }

    public String getIsSignatureAllowed() { return isSignatureAllowed;}

    public void setIsSignatureAllowed(String isSignatureAllowed) { this.isSignatureAllowed = isSignatureAllowed; }

    public String getIsSaveReceiptAllowed() { return isSaveReceiptAllowed; }

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

    public String getIsDomainWhitelisted()
    {
        return isDomainWhitelisted;
    }

    public void setIsDomainWhitelisted(String isDomainWhitelisted)
    {
        this.isDomainWhitelisted = isDomainWhitelisted;
    }

    public String getCustomerIpLimitCheck()
    {
        return customerIpLimitCheck;
    }

    public void setCustomerIpLimitCheck(String customerIpLimitCheck)
    {
        this.customerIpLimitCheck = customerIpLimitCheck;
    }

    public String getCustomerIpDailyCount()
    {
        return customerIpDailyCount;
    }

    public void setCustomerIpDailyCount(String customerIpDailyCount)
    {
        this.customerIpDailyCount = customerIpDailyCount;
    }

    public String getCustomerIpAmountLimitCheck()
    {
        return customerIpAmountLimitCheck;
    }

    public void setCustomerIpAmountLimitCheck(String customerIpAmountLimitCheck)
    {
        this.customerIpAmountLimitCheck = customerIpAmountLimitCheck;
    }

    public String getCustomerIpDailyAmountLimit()
    {
        return customerIpDailyAmountLimit;
    }

    public void setCustomerIpDailyAmountLimit(String customerIpDailyAmountLimit)
    {
        this.customerIpDailyAmountLimit = customerIpDailyAmountLimit;
    }

    public String getCustomerNameLimitCheck()
    {
        return customerNameLimitCheck;
    }

    public void setCustomerNameLimitCheck(String customerNameLimitCheck)
    {
        this.customerNameLimitCheck = customerNameLimitCheck;
    }

    public String getCustomerNameDailyCount()
    {
        return customerNameDailyCount;
    }

    public void setCustomerNameDailyCount(String customerNameDailyCount)
    {
        this.customerNameDailyCount = customerNameDailyCount;
    }

    public String getCustomerNameAmountLimitCheck()
    {
        return customerNameAmountLimitCheck;
    }

    public void setCustomerNameAmountLimitCheck(String customerNameAmountLimitCheck)
    {
        this.customerNameAmountLimitCheck = customerNameAmountLimitCheck;
    }

    public String getCustomerNameDailyAmountLimit()
    {
        return customerNameDailyAmountLimit;
    }

    public void setCustomerNameDailyAmountLimit(String customerNameDailyAmountLimit)
    {
        this.customerNameDailyAmountLimit = customerNameDailyAmountLimit;
    }

    public String getCustomerEmailLimitCheck()
    {
        return customerEmailLimitCheck;
    }

    public void setCustomerEmailLimitCheck(String customerEmailLimitCheck)
    {
        this.customerEmailLimitCheck = customerEmailLimitCheck;
    }

    public String getCustomerEmailDailyCount()
    {
        return customerEmailDailyCount;
    }

    public void setCustomerEmailDailyCount(String customerEmailDailyCount)
    {
        this.customerEmailDailyCount = customerEmailDailyCount;
    }

    public String getCustomerEmailAmountLimitCheck()
    {
        return customerEmailAmountLimitCheck;
    }

    public void setCustomerEmailAmountLimitCheck(String customerEmailAmountLimitCheck)
    {
        this.customerEmailAmountLimitCheck = customerEmailAmountLimitCheck;
    }

    public String getCustomerEmailDailyAmountLimit()
    {
        return customerEmailDailyAmountLimit;
    }

    public void setCustomerEmailDailyAmountLimit(String customerEmailDailyAmountLimit)
    {
        this.customerEmailDailyAmountLimit = customerEmailDailyAmountLimit;
    }

    public String getCustomerPhoneLimitCheck()
    {
        return customerPhoneLimitCheck;
    }

    public void setCustomerPhoneLimitCheck(String customerPhoneLimitCheck)
    {
        this.customerPhoneLimitCheck = customerPhoneLimitCheck;
    }

    public String getCustomerPhoneDailyCount()
    {
        return customerPhoneDailyCount;
    }

    public void setCustomerPhoneDailyCount(String customerPhoneDailyCount)
    {
        this.customerPhoneDailyCount = customerPhoneDailyCount;
    }

    public String getCustomerPhoneAmountLimitCheck()
    {
        return customerPhoneAmountLimitCheck;
    }

    public void setCustomerPhoneAmountLimitCheck(String customerPhoneAmountLimitCheck)
    {
        this.customerPhoneAmountLimitCheck = customerPhoneAmountLimitCheck;
    }

    public String getCustomerPhoneDailyAmountLimit()
    {
        return customerPhoneDailyAmountLimit;
    }

    public void setCustomerPhoneDailyAmountLimit(String customerPhoneDailyAmountLimit)
    {
        this.customerPhoneDailyAmountLimit = customerPhoneDailyAmountLimit;
    }

    public String getPaybylink()
    {
        return paybylink;
    }

    public void setPaybylink(String paybylink)
    {
        this.paybylink = paybylink;
    }

    public String getTransMgtPayoutTransaction()
    {
        return transMgtPayoutTransaction;
    }
    public void setTransMgtPayoutTransaction(String transMgtPayoutTransaction)
    {
        this.transMgtPayoutTransaction = transMgtPayoutTransaction;
    }
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
    public String getVpaAddressMonthlyCount()
    {
        return vpaAddressMonthlyCount;
    }

    public void setVpaAddressMonthlyCount(String vpaAddressMonthlyCount)
    {
        this.vpaAddressMonthlyCount = vpaAddressMonthlyCount;
    }

    public String getVpaAddressMonthlyAmountLimit()
    {
        return vpaAddressMonthlyAmountLimit;
    }

    public void setVpaAddressMonthlyAmountLimit(String vpaAddressMonthlyAmountLimit)
    {
        this.vpaAddressMonthlyAmountLimit = vpaAddressMonthlyAmountLimit;
    }

    public String getCustomerEmailMonthlyCount()
    {
        return customerEmailMonthlyCount;
    }

    public void setCustomerEmailMonthlyCount(String customerEmailMonthlyCount)
    {
        this.customerEmailMonthlyCount = customerEmailMonthlyCount;
    }

    public String getCustomerEmailMonthlyAmountLimit()
    {
        return customerEmailMonthlyAmountLimit;
    }

    public void setCustomerEmailMonthlyAmountLimit(String customerEmailMonthlyAmountLimit)
    {
        this.customerEmailMonthlyAmountLimit = customerEmailMonthlyAmountLimit;
    }

    public String getCustomerPhoneMonthlyCount()
    {
        return customerPhoneMonthlyCount;
    }

    public void setCustomerPhoneMonthlyCount(String customerPhoneMonthlyCount)
    {
        this.customerPhoneMonthlyCount = customerPhoneMonthlyCount;
    }

    public String getCustomerPhoneMonthlyAmountLimit()
    {
        return customerPhoneMonthlyAmountLimit;
    }

    public void setCustomerPhoneMonthlyAmountLimit(String customerPhoneMonthlyAmountLimit)
    {
        this.customerPhoneMonthlyAmountLimit = customerPhoneMonthlyAmountLimit;
    }

    public String getBankAccountNoMonthlyCount()
    {
        return bankAccountNoMonthlyCount;
    }

    public void setBankAccountNoMonthlyCount(String bankAccountNoMonthlyCount)
    {
        this.bankAccountNoMonthlyCount = bankAccountNoMonthlyCount;
    }

    public String getBankAccountNoMonthlyAmountLimit()
    {
        return bankAccountNoMonthlyAmountLimit;
    }

    public void setBankAccountNoMonthlyAmountLimit(String bankAccountNoMonthlyAmountLimit)
    {
        this.bankAccountNoMonthlyAmountLimit = bankAccountNoMonthlyAmountLimit;
    }

    public String getCustomerIpMonthlyCount()
    {
        return customerIpMonthlyCount;
    }

    public void setCustomerIpMonthlyCount(String customerIpMonthlyCount)
    {
        this.customerIpMonthlyCount = customerIpMonthlyCount;
    }

    public String getCustomerIpMonthlyAmountLimit()
    {
        return customerIpMonthlyAmountLimit;
    }

    public void setCustomerIpMonthlyAmountLimit(String customerIpMonthlyAmountLimit)
    {
        this.customerIpMonthlyAmountLimit = customerIpMonthlyAmountLimit;
    }

    public String getCustomerNameMonthlyCount()
    {
        return customerNameMonthlyCount;
    }

    public void setCustomerNameMonthlyCount(String customerNameMonthlyCount)
    {
        this.customerNameMonthlyCount = customerNameMonthlyCount;
    }

    public String getCustomerNameMonthlyAmountLimit()
    {
        return customerNameMonthlyAmountLimit;
    }

    public void setCustomerNameMonthlyAmountLimit(String customerNameMonthlyAmountLimit)
    {
        this.customerNameMonthlyAmountLimit = customerNameMonthlyAmountLimit;
    }

    public String getMerchantCheckoutConfig()
    {
        return merchantCheckoutConfig;
    }
    public void setMerchantCheckoutConfig(String merchantCheckoutConfig)
    {
        this.merchantCheckoutConfig = merchantCheckoutConfig;
    }

    public String getMerchant_verify_otp()
    {
        return merchant_verify_otp;
    }

    public void setMerchant_verify_otp(String merchant_verify_otp)
    {
        this.merchant_verify_otp = merchant_verify_otp;
    }
}
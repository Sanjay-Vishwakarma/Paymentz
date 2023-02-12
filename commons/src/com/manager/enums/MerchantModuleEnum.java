package com.manager.enums;

/**
 * Created by admin on 4/25/2016.
 */
public enum MerchantModuleEnum
{
    DASHBOARD,
    ACCOUNT_DETAILS,
        ACCOUNT_SUMMARY,
        CHARGES_SUMMARY,
        TRANSACTION_SUMMARY,
        REPORTS_SUMMARY,
    SETTINGS,
        MERCHANT_PROFILE,
        ORGANISATION_PROFILE,
        CHECKOUT_PAGE,
        VIEW_KEY,
        GENERATE_KEY,
        MERCHANT_CONFIG,
        MERCHANT_CHECKOUT_CONFIG,
        FRAUD_RULE_CONFIG,
        INVOICE_CONFIGURATION,
        WHITELIST_DETAILS,
        BLACKLIST_DETAILS,
    TRANSACTION_MANAGEMENT,
        TRANSACTIONS,
        CAPTURE,
        REVERSAL,
        PAYOUT,
        PAYOUT_TRANSACTIONS,
    REJECTED_TRANSACTION,
    INVOICE,
        GENERATE_INVOICE,
        INVOICE_HISTORY,
    EMI_CONFIGURATION,
    PAYBYLINK,
    //Need to be review
    TOKEN_MANAGEMENT,
            REGISTRATION_HISTORY,
            REGISTER_CARD,
    VIRTUAL_TERMINAL,
    VIRTUAL_CHECKOUT,
    MERCHANT_MANAGEMENT,
            USER_MANAGEMENT,
    MERCHANT_APPLICATION,
    RECURRING_MODULE,
    CHANGE_PASSWORD;
}
package com.manager.enums;

/**
 * Created by Admin on 9/28/2020.
 */
public enum ActivityLogParameters
{

    ADMIN("Admin"),
    PARTNER("Partner"),
    MERCHANT("Merchant"),
    SUBMERCHANT("submerchant"),
    AGENT("Agent"),
    SUPERPARTNER("superpartner"),
    CHILEDSUPERPARTNER("childsuperpartner"),
    SUBPARTNER("subpartner"),

    ADD("Add"),
    EDIT("Edit"),
    DELETE("Delete"),
    VIEW("View"),
    FGTPASS("Forgot Password"),
    SETTLED("Settle Transactions"),
    CAPTURED("Capture Transactions"),
    LOGIN("Login"),
    OTP_VALIDATION("OTP Validation"),

    ACCOUNTID("Account Id"),
    MEMBERID("Member Id"),
    PGTYPEID("Pgtype Id"),
    TERMINALID("Terminal Id"),
    PARTNERID("Partner Id"),
    AGENTID("Agent Id"),
    GATEWAY("Gateway"),
    USER_CREATED("Admin User Created"),
    LOGIN_SUCCESS("Merchant Login Successful"),
    LOGIN_FAIL("Merchant Login Invalid"),
    LOGIN_DISABLE("Merchant Login Failed"),
    OTP_VALIDATION_SUCCESS("OTP Validation Successful"),
    OTP_VALIDATION_FAIL("OTP Validation Failed"),

    ADD_NEW_ADMIN("Add New Admin"),
    LOGIN_MERCHANT("Login Merchant"),
    GATWAY_ACCOUNT_MASTER("Gateway Accounts Master"),
    MERCHANT_CONFIGURATION("Merchant Configuration"),
    GATEWAY_MASTER("Gateway Master"),
    COMMERCIALS_LIMITS("Commercials & Limits"),
    BANK_TRANSACTION_REPORT("Bank Transaction Report"),
    MERCHANT_ACCOUNT_MAPPING("Merchant Account Mapping"),
    MERCHANT_FRAUD_SETTING("Merchant Fraud Setting"),
    MERCHANT_LIMIT_MANAGEMENT("Merchant Limit Management"),
    MERCHANT_MAIL_SETTING("Merchant Mail Setting"),
    MERCHANT_BACKOFFICE_ACCESS("Merchant Backoffice Access"),
    MERCHANT_TRANSACTION_SETTING("Merchant Transaction Setting"),
    PARTNER_DEFAULT_CONFIGURATION("Partner Default Configuration"),
    PARTNER_MASTER("Partner Master");
    public String activitylogparameters;

    ActivityLogParameters(String activitylogparameters)
    {
        this.activitylogparameters = activitylogparameters;
    }

    public String toString()
    {
        return activitylogparameters;
    }

    public static ActivityLogParameters getEnum(String value)
    {
        try
        {
            return ActivityLogParameters.valueOf(value);
        } catch (IllegalArgumentException iae)
        {
            return null;
        }
    }



}
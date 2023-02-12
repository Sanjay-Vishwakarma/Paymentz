package com.payment.Mail;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/30/14
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public enum MailEventEnum
{
    PARTNER_REGISTRATION,   //Partner Registration

    PARTNERS_MERCHANT_REGISTRATION,

    AGENT_REGISTRATION,

    PARTNER_CHANGE_PASSWORD,

    PARTNER_FORGOT_PASSWORD,

    PARTNER_USER_FORGOT_PASSWORD,

    PARTNER_CHANGE_IN_PROFILE_DETAILS,

    GENERATING_INVOICE_BY_MERCHANT,

    GENERATING_INVOICE_BY_MERCHANT_WITH_CREDENTIAL,

    CANCELED_INVOICE,

    CAPTURE_TRANSACTION,

    PARTNERS_MERCHANT_SALE_TRANSACTION,

    PARTNERS_MERCHANT_CHANGE_PASSWORD,

    PARTNERS_MERCHANT_FORGOT_PASSWORD,

    AGENT_FORGOT_PASSWORD,

    PARTNERS_MERCHANT_CHANGE_IN_PROFILE_DETAILS,

    CHARGEBACK_TRANSACTION,

    RECON_TRANSACTION,

    FRAUD_TRANSACTION_MARKED,

    REFUND_TRANSACTION,

    PAYOUT_TRANSACTION,

    AGENT_CHANGE_PASSWORD,

    ROLLING_REVERSE_TRANSACTION_DETAILS,

    BILLING_DESCRIPTOR_CHANGE_INTIMATION,

    ALERT_WEEKLY_CHARGEBACK_SUMMERY_REPORT,

    ALERT_DAILY_STATUS_SUMMERY_REPORT, //partner transaction mail to cardholder

    MEMBER_NOTIFY_MAIL,

    HIGH_RISK_FRAUD_TRANSACTION_INTIMATION,

    HIGH_RISK_REFUND_TRANSACTION_INTIMATION,

    MEMBER_DAILY_STATUS_FRAUD_REPORT,

    BANK_CONNECTION_CHECKING_REPORT,

    ADMIN_FAILED_TRANSACTION_NOTIFICATION,

    ADMIN_SETTLEMENT_REPORT,

    ADMIN_STATUS_SYNCRONIZATION_CRON,

    ADMIN_AUTHSTARTED_CRON_REPORT,

    ADMIN_MARKEDFORREVERSAL_CRON_REPORT,

    EXCEPTION_DETAILS,
    REJECTED_TRANSACTION,

    MERCHANT_PAYOUT_ALERT_MAIL,

    PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION,

    ADMIN_MAIL,

    FRAUDRULE_CHANGE_INTIMATION,
    
    Apply_Modification_Mail,

    BANK_APPLICATION_URL_NOTIFICATION,

    PARTNERS_MERCHANT_USER_FORGOT_PASSWORD,

    ADMIN_CUSTOMERSUPPORT_FORGOT_PASSWORD,

    PARTNERS_LEVEL_CARD_REGISTRATION,

    MERCHANTS_LEVEL_CARD_REGISTRATION,

    FRAUD_NOTIFICATION,

    //Merchant Monitoring Alert and Action
    MERCHANT_MONITORING_ALERT_TO_ADMIN,
    MERCHANT_CB_MONITORING_ALERT_TO_ADMIN,
    MERCHANT_RF_MONITORING_ALERT_TO_ADMIN,
    MERCHANT_FRAUD_MONITORING_ALERT_TO_ADMIN,
    MERCHANT_TECH_MONITORING_ALERT_TO_ADMIN,

    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_SALES,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_CB,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_RF,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_FRAUD,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_ADMIN_TECH,

    MERCHANT_MONITORING_SUSPENSION_TO_ADMIN,
    MERCHANT_CB_MONITORING_SUSPENSION_TO_ADMIN,
    MERCHANT_RF_MONITORING_SUSPENSION_TO_ADMIN,
    MERCHANT_FRAUD_MONITORING_SUSPENSION_TO_ADMIN,
    MERCHANT_TECH_MONITORING_SUSPENSION_TO_ADMIN,

    MERCHANT_MONITORING_ALERT_TO_AGENT,
    MERCHANT_MONITORING_ALERT_TO_AGENT_CB,
    MERCHANT_MONITORING_ALERT_TO_AGENT_RF,
    MERCHANT_MONITORING_ALERT_TO_AGENT_FRAUD,
    MERCHANT_MONITORING_ALERT_TO_AGENT_TECH,

    MERCHANT_MONITORING_SUSPENSION_TO_AGENT,
    MERCHANT_MONITORING_SUSPENSION_TO_AGENT_CB,
    MERCHANT_MONITORING_SUSPENSION_TO_AGENT_RF,
    MERCHANT_MONITORING_SUSPENSION_TO_AGENT_FRAUD,
    MERCHANT_MONITORING_SUSPENSION_TO_AGENT_TECH,

    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_SALES,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_CB,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_RF,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_FRAUD,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_AGENT_TECH,

    MERCHANT_MONITORING_ALERT_TO_PARTNER,
    MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_SALES,

    MERCHANT_MONITORING_ALERT_TO_MERCHANT,
    MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_SALES,
    MERCHANT_CB_MONITORING_ALERT_TO_MERCHANT,
    MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_CB,
    MERCHANT_RF_MONITORING_ALERT_TO_MERCHANT,
    MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_RF,
    MERCHANT_MONITORING_ALERT_TO_MERCHANT_FRAUD,
    MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_FRAUD,
    MERCHANT_MONITORING_ALERT_TO_MERCHANT_TECH,
    MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_TECH,

    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_SALES,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_CB,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_RF,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_FRAUD,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_MERCHANT_TECH,

    MERCHANT_CB_MONITORING_ALERT_TO_PARTNER,
    MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_CB,
    MERCHANT_RF_MONITORING_ALERT_TO_PARTNER,
    MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_RF,
    MERCHANT_MONITORING_ALERT_TO_PARTNER_FRAUD,
    MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_FRAUD,
    MERCHANT_MONITORING_ALERT_TO_PARTNER_TECH,
    MERCHANT_MONITORING_SUSPENSION_TO_PARTNER_TECH,
    MERCHANT_MONITORING_ATTACHMENT_TO_ADMIN,

    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_SALES,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_CB,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_RF,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_FRAUD,
    MERCHANT_MONITORING_ATTACHMENT_ALERT_TO_PARTNER_TECH,

    MERCHANT_SIGNUP_OTP,
    PARTNERS_MERCHANT_USER_REGISTRATION,
    PARTNERS_MERCHANT_USER_CHANGE_PASSWORD,
    PARTNER_USER_REGISTRATION,
    PARTNER_USER_CHANGE_PASSWORD,
    RESEND_VERIFY_EMAIL,
    MERCHANT_APPLICATION_FORM_STATUS_CHANGE,
    ADMIN_AUTHFAILED_CRON_REPORT,
    ADMIN_PAYOUTSTARTED_CRON_REPORT,
    ADMIN_PAYOUTFAILED_CRON_REPORT
    ;
}
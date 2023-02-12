package com.payment.accesslogaction;

import org.apache.commons.net.smtp.SMTPReply;

/**
 * Created by Trupti on 5/26/2016.
 */
public class AdminLogAction
{
    public static final String ADMIN_LOGIN = "Admin Login";
    public static final String MEMBER_SIGNUP = "New Member sign up";
    public static final String MEMBER_DETAIL_LIST = "Merchant Details List";
    public static final String INVOICE = "Merchant Invoice";
    public static final String INVOICE_DETAILS = "Merchant Invoice Details";
    public static final String REMIND_INVOICE = "Remind Merchant Invoice";
    public static final String CANCEL_INVOICE = "Cancel Merchant Invoice";
    public static final String WIRELIST = "Merchant Wire List";
    public static final String AGENT_LIST = "Merchant Agent List";
    public static final String ADD_NEW_AGENT = "Add new Agent";
    public static final String MERCHANT_USER_LIST = "Merchant's User List";
    public static final String VIEW_MERCHANT_USER_DETAILS = "View Merchants User's Details";
    public static final String EDIT_MERCHANT_USER_DETAILS = "Edit Merchant User's Details";
    public static final String DELETE_MERCHANT_USER = "Delete Merchant's User";
    public static final String ADD_NEW_USER = "Add New Merchant's User";
    public static final String TREMINAL_ALLOCATION = "Terminal Allocation for Merchant's User";
    public static final String MERCHANT_INTIMATION_MAIL = "Merchant Intimation Mail";
    public static final String MERCHANT_MONITORING = "Merchant Monitoring";
    public static final String NEW_MONITORING_PARAMETER = "Add New Monitoring Parameter";
    public static final String MONITORING_PARAMETER_MASTER = "Monitoring Parameter Master List";
    public static final String MONITORING_EDIT_DETAILS = "Merchant Monitoring Edit Details";
    public static final String MERCHANT_MONITORING_SETTINGS = "Merchant Monitoring Settings";
    public static final String MONITORING_PARAMETERS_UPDATE = "Update Monitoring Parameters";
    public static final String QWIPI_INQUIRY = "Qwipi Inquiry";
    public static final String QWIPI_RECONCILATION_LIST = "Qwipi Reconcilation List";
    public static final String QWIPI_RECONCILATION_PROCESS = "Qwipi Reconcilation Process";
    public static final String QWIPI_REFUND_LIST = "Qwipi Refund List";
    public static final String QWIPI_REFUND_TRANSACTION = "Qwipi Refund Transaction";
    public static final String QWIPI_DETAIL_LIST = "Qwipi Detail list";
    public static final String QWIPI_CHARGEBACK_LIST = "Qwipi Chargeback List";
    public static final String QWIPI_CHARGEBACK_TRANSACTION = "Qwipi Chargeback Transaction";
    public static final String QWIPI_VERIFY_ORDER_REFUND_ALERT_LIST = "Qwipi verify Order Refund Alert List";
    public static final String ECORE_ENQUIRY = "Ecore Enquiry";
    public static final String ECORE_RECONCILATION_LIST = "Ecore Reconcilation List";
    public static final String ECORE_RECONCILATION_PROCESS = "Ecore Reconcilation Process";
    public static final String ECORE_REFUND_LIST = "Ecore Refund List";
    public static final String ECORE_REFUND_TRANSACTION = "Ecore Refund Transaction";
    public static final String ECORE_CHARGEBACK_LIST = "Ecore Chargeback List";
    public static final String GATEWAY_ACCOUNT_LIST = "Gateway Account Details List";
    public static final String GATEWAY_ACCOUNT_DETAILS = "Gateway Account Details";
    public static final String ADD_GATEWAY_ACCOUNT = "Add New Gateway Account";
    public static final String ADD_PARTNER_ACCOUNT = "Add New Partner Account Mapping";
    public static final String ADMIN_CHANGE_PSWD = "Admin change Password";
    public static final String MEMBER_MAPPING_DETAILS = "Member Mapping details";
    public static final String MEMBER_MAPPING = "Member Terminal Mapping";
    public static final String COMPLIANCE = "Compliance";
    public static final String WHITELIST_DETAILS = "whiteList Details";
    public static final String UPLOAD_FILE_FOR_WHITELIST = "Upload card file For Whitelisting";
    public static final String IP_WHITELIST = "Ip WhiteList Module";
    public static final String LIST_GATEWAY_TYPE_DETAILS = "List Gateway Type Details";
    public static final String VIEW_GATEWAY_TYPE_DETAILS = "View Gateway Type Details";
    public static final String ADD_BANK_AGENT_MAPPING = "Add Bank agent mapping";
    public static final String ADD_BANK_PARTNER_MAPPING = "Add Bank Partner Mapping";
    public static final String MAIL_MAPPING_LIST = "Mail Mapping list";
    public static final String ADD_MAIL_MAPPING = "Add mail mapping details";
    public static final String UPDATE_MAIL_DETAILS = "Update Mail Mapping Details";
    public static final String EXPORT_RESERVE_FILE = "Export Reserve File";
    public static final String BLOCKED_MERCHANT_LIST = "Blocked Merchant List";
    public static final String CHARGE_MASTER_LIST = "Charge Master List";
    public static final String GATEWAY_ACCOUNT_CHARGES_LIST = "Gateway Account Charges List";
    public static final String MODIFY_GATEWAY_ACCOUNT_CHARGES = "Modify Gateway Account Charges";
    public static final String ADD_GATEWAY_ACCOUNT_CHARGES = "Create Gateway Account Charges";
    public static final String ADD_MEMBER_ACCOUNT_CHARGES = "Create Member Account Charges";
    public static final String MEMBER_ACCOUNT_CHARGES = "Create Member Account Charges list";
    public static final String UPDATE_MEMBER_ACCOUNT_CHARGES = "Update Member Account Charges list";
    public static final String LIST_MERCHANT_RANDOM_CHARGES = " Merchant Random Charges list";
    public static final String ACTION_MERCHANT_RANDOM_CHARGES = "Action Merchant Random Charges list";
    public static final String BANK_WIRE_MANAGER_LIST = "Bank Wire Manager List";
    public static final String UPDATE_BANK_WIRE_MANAGER = "Update Bank Wire Manager Details";
    public static final String VIEW_OR_EDIT_BANK_WIRE_MANAGER = "View or Edit Bank Wire Manager";
    public static final String WIRE_LIST = "Wire List";
    public static final String MANAGE_MERCHANT_RANDOM_CHARGE = "Manage merchant random charge";
    public static final String PARTNER_MERCHANT_DETAILS = "Partner Merchant Details";
    public static final String PARTNER_MERCHANT_TRANS_SUMMARY = "Partner Merchant trans Summary";
    public static final String VIEW_PARTNER_DETAILS = "View Partner Details";
    public static final String LIST_PARTNER_DETAILS = "List Partner Details";
    public static final String NEW_PARTNER = "New Partner Details";
    public static final String VIEW_PARTNER_FRAUD_ACCOUNTS_DETAILS = "View Partner Fraud Accounts Details";
    public static final String VIEW_PARTNER_BANK_DETAILS = "View Partner Bank Details";
    public static final String NEW_PARTNER_LOGO = "New Partner Logo";
    public static final String PARTNER_WIRE_LIST = "Partner Wire List";
    public static final String AGENT_WIRE_LIST = "Agent Wire List";
    public static final String ACTION_AGENT_WIRE_MANAGER = "Action Agent Wire Manager Details";
    public static final String UPDATE_PARTNER_WIRE = "Update Partner Wire Details";
    public static final String ACTION_PARTNER_WIRE = "Action Partner Wire Details";
    public static final String EDIT_PARTNER_DETAILS = "Edit Partner Details";
    public static final String AGENT_MERCHANT_TRANSACTION_SUMMARY = "Agent Merchant Transaction Summary Details";
    public static final String AGENT_MERCHANT_DETAILS = "Agent Merchant Details";
    public static final String VIEW_AGENT_DETAILS = "View Agent Details";
    public static final String NEW_AGENT = "New Agent Details";
    public static final String LIST_AGENT_DETAILS = "List Agent Details";
    public static final String EXPORT_SHIPPING_TRANSACTION = "Export Shipping Transaction Details";
    public static final String EDIT_AGENT_DETAILS = "Edit Agent Details";
    public static final String UPDATE_POD_DETAILS = "Update POD Details";
    public static final String EDIT_SHIPPING_DETAIL_LIST = "Edit Shipping Details List";
    public static final String SHIPPING_DETAIL_LIST = "Shipping Details List";
    public static final String SEARCH_CSE = "Search Details";
    public static final String EDIT_CSE_DETAILS = "Edit CSE Details";
    public static final String CUSTSUPP_SIGNUP = "CustSupp Signup Detail";
    public static final String CSE_VIEW_UPDATE = "View Update CSE details";
    public static final String ADMIN_TRANSACTION_DETAILS = "Admin Transaction details";
    public static final String EXPORT_TRANSACTIONS = "Export Transaction details";
    public static final String TRANSACTION_DETAILS = " Transaction details";
    public static final String BLACK_LIST_DETAILS = " Black List details";
    public static final String BLACK_LIST_CARD = " Black List Card details";
    public static final String COMMON_DO_REVERSE = " Common Do details";
    public static final String WHITE_LIST_DETAILS = " White List details";
    public static final String COMMON_REFUND_LIST = " Common Refund List details";
    public static final String BLACK_LIST_NAME = " Black List name details";
    public static final String BLACK_LIST_COUNTRY = " Black List Country details";
    public static final String BLACK_LIST_EMAIL = " Black List Email details";
    public static final String BLACK_LIST_IP = " Black List IP details";
    public static final String ACTION_FRAUD_SYSTEM = "Action Fraud System details";
    public static final String ACTION_MEMBER_FRAUD_SYSTEM = "Action Member Fraud System details";
    public static final String LIST_MEMBER_FRAUD_SYSTEM = " List Member Fraud System details";
    public static final String MANAGE_MEMBER_FRAUD_SYSTEM = "Manage member Fraud System details";
    public static final String LIST_FRAUD_SYSTEM = "List Fraud System  details";
    public static final String FRAUD_TRANSACTION_LIST = "Fraud Transaction List details";
    public static final String REVERSE_FRAUD_TRANSACTION = "Reverse Fraud Transaction details";
    public static final String FRAUD_TRANSACTION_REVERSE_LIST = "Fraud Transaction Reverse details";
    public static final String ACTION_FRAUD_RULE_LIST = "Action Fraud Rule List";
    public static final String FRAUD_RULE_LIST = "Fraud Rule List";
    public static final String ADD_NEW_FRAUD_RULE = "Add New Fraud details";
    public static final String ACTION_FRAUD_SYSTEM_ACCOUNT_RULE = "Action Fraud System Account Rule";
    public static final String FRAUD_SYSTEM_ACCOUNT_RULE_LIST = "Fraud System Account Rule List";
    public static final String MANAGE_FRAUD_SYSTEM_ACCOUNT_RULE = "Manage Fraud System Account Rule";
    public static final String FRAUD_SYSTEM_SUB_ACCOUNT_RULE_lIST = " Fraud System Sub Account Rule List";
    public static final String MANAGE_FRAUD_SYSTEM_SUB_ACCOUNT_RULE = "Manage Fraud System Sub Account Rule";
    public static final String ACTION_FRAUD_SYSTEM_SUB_ACCOUNT_RULE = "Action Fraud System Sub Account Rule";
    public static final String MANAGE_ACCOUNT_FRAUD_RULE = "Manage Account Fraud Rule";
    public static final String ACCOUNT_FRAUD_RULE_LIST = "Account Fraud Rule List ";
    public static final String ACTION_FRAUD_SYSTEM_ACCOUNT = "Action Fraud system Account";
    public static final String FRAUD_SYSTEM_ACCOUNT_LIST = "Fraud System Account List";
    public static final String MANAGE_PARTNER_FRAUD_SYSTEM_ACCOUNT = "Manage Partner Fraud System Account Details ";
    public static final String MANAGE_FRAUD_SYSTEM_ACCOUNT = "Manage Fraud System Account Details";
    public static final String ACTION_FRAUD_SYSTEM_SUB_ACCOUNT = "Action Fraud System Sub Account";
    public static final String FRAUD_SYSTEM_SUB_ACCOUNT_LIST = "Fraud System Account Sub Account List";
    public static final String MANAGE_FRAUD_SYSTEM_ACCOUNT_SUB_ACCOUNT = "Manage Fraud System Account Sub Account";
    public static final String MANAGE_MERCHANT_FRAUD_ACCOUNT = "Manage Merchant Fraud Account";
    public static final String FRAUD_SYSTEM_MERCHANT_SUB_ACCOUNT_LIST = "Fraud System Merchant Sub Account List ";
    public static final String ACTION_FRAUD_SYSTEM_MERCHANT_ACCOUNT = "Action Fraud System Merchant Account ";
    public static final String ADD_NEW_FRAUD_SYSTEM = "Add New Fraud System ";
    public static final String FRAUD_RULE_SEND_INTIMATION = "Fraud Rule Send Intimation";
    public static final String FRAUD_RULE_CHANGE_INTIMATION_LIST = "Fraud Rule Chane Intimation List";
    public static final String POPULATE_APPLICATION = "Populate Application";
    public static final String APP_MANAGER_STATUS = "App Manager Status";
    public static final String SPEED_OPTION = "Spped Option";
    public static final String VIEW_KYC_DOCUMENT = "View KYC Document";
    public static final String NAVIGATION = "Navigation";
    public static final String LIST_OF_APP_MEMEBER = "List of App Member";
    public static final String UPLOAD_SERVLET = "Upload Servlet";
    public static final String CREATE_BANK_APLICATION = "Create Bank Application";
    public static final String BANK_DETAIL_LIST = "Bank Detail List";
    public static final String GENERATED_CONSOLIDATED_APPLICATION = "Generated Consolidated Application";
    public static final String COSOLIDATED_APPLICATION = "Consolidated Application";
    public static final String MANAGE_BANK_APP = "Manage Bank App";
    public static final String SEND_MAIL_TO_BANK = "Send Mail To Bank";
    public static final String NEW_ADMIN_SIGN_UP = "New Admin Sign Up";
    public static final String ADMIN_DETAILS_LIST = "Admin Detail List";
    public static final String RISK_RULE_DETAILS = "Risk Rule Details";
    public static final String SINGLE_RISK_RULE_DETAILS = "Single Risk Rule Details";
    public static final String ADD_RISK_RULE_DETAILS = "Add Risk Rule Details";
    public static final String BUSINESS_RULE_DETAILS = "Business Rule Details";
    public static final String SINGLE_BUSINESS_RULE_DETAILS = "Single Business Rule Details";
    public static final String ADD_BUSINESS_RULE_DETAILS = "Add Business Rule Details";
    public static final String ADMIN_NEW_ADMIN_MODULE = "Admin New Admin Module";
    public static final String ADMIN_MODULE_LIST = "Admin Module List";
    public static final String ACTION_ADMIN_MODULE = "Action Admin Module";
    public static final String ADMIN_MODULE_MAPPING_LIST = "Admin Module Mapping List";
    public static final String MANAGE_ADMIN_MODULE_MAPPING = "Manage Admin Module Mapping";
    public static final String MERCHANT_TRANSMAILLIST = "Merchant Trans Mail List";
    public static final String BANK_TRANSACTION_REPORT = "Bank Transaction Report";
    public static final String GENERATE_WIRE_SERVLET = "Generate Wire Servlet";
    public static final String GENERATE_BANK_AGENT_PAYOUT_REPORT = "Generate Bank Agent payout Report";
    public static final String GENERATE_PAYOUT_SERVLET = "Generate Payout Servlet";
    public static final String GENERATE_AGENT_PAYOUT_SERVLET = "Generate Agent Payout Servlet";
    public static final String GENERATE_BANK_PARTNER_PAYOUT_REPORT = "Generate Bank Partner Report";
    public static final String GENERATE_PARTNER_PAYOUT_SERVLET = "Generate Partner Payout Servlet";
    public static final String UNBLOCKED_ACCOUNT = "Unblock Account";




















}

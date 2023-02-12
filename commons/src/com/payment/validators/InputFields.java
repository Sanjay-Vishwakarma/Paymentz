package com.payment.validators;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 4/3/14
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
public enum InputFields
{
    //For Transaction
    RULEID("ruleid"),
    RULENAME("rulename"),
    RULEDES("ruledescription"),
    RULEGRP("rulegroup"),
    SCORE("score"),
    TOID("toid"),
    TOTYPE("totype"),
    AMOUNT("amount"),
    DESCRIPTION("description"),
    ORDERDESCRIPTION("orderdescription"),
    TMPL_EMAILADDR("TMPL_emailaddr"),
    TMPL_CITY("TMPL_city"),
    TMPL_STREET("TMPL_street"),
    TMPL_ZIP("TMPL_zip"),
    TMPL_STATE("TMPL_state"),
    TMPL_TELNO("TMPL_telno"),
    TMPL_TELNOCC("TMPL_telnocc"),
    TMPL_AMOUNT("TMPL_AMOUNT"),
    TMPL_CURRENCY("TMPL_CURRENCY"),
    WALLET_ID("WALLET_ID"),
    WALLET_AMOUNT("WALLET_AMOUNT"),
    WALLET_CURRENCY("WALLET_CURRENCY"),
    TMPL_COUNTRY("TMPL_COUNTRY"),
    INVOICE_NO("INVOICE_NO"),
    INVOICE_EXPIRATIONPERIOD("INVOICE_EXPIRATIONPERIOD"),
    REDIRECT_URL("redirecturl"),
    SMS_CODE("SMS_CODE"),
    PAYMENT_PROVIDER("PAYMENT_PROVIDER"),
    VPA_ADDRESS("VPA_ADDRESS"),
    BANK_ACCOUNT_NAME("BANK_ACCOUNT_NAME"),
    TRANSFER_TYPE("TRANSFER_TYPE"),
    BANK_IFSC("BANK_IFSC"),
    BANK_ACCOUNT_NUMBER("BANK_ACCOUNT_NUMBER"),
    PARNET_BANKWIREID("PARNET_BANKWIREID"),
    BRANCH_NAME("BRANCH_NAME"),
    BANK_CODE("BANK_CODE"),
    BRANCH_CODE("BRANCH_CODE"),
    ACCOUNT_TYPE("ACCOUNT_TYPE"),


    //for VirtualConfirm
    MID("mid"),
    OID("oid"),
    TOTAL("total"),
    CARDTYPE("cardtype"),
    CARD_TYPE("cardtype"),
    CARD_ISSUING_BANK("issuing_bank"),
    BITKEY("bitkey"),
    CHECKSUMALGORITHM("checksumAlgorithm"),
    B_ADDRESS("b_address"),
    B_CITY("b_city"),
    B_ZIPCODE("b_zipcode"),
    B_STATE("b_state"),
    COUNTRYCODE("countrycode"),
    PHONE1("phone1"),
    PHONE2("phone2"),
    PAYMENTYPE("paymenttype"),
    ACCOUNTID("ACCOUNTID"),

    TOID_CAPS("TOID"),
    DESCRIPTION_CAPS("DESCRIPTION"),
    TRACKINGID_CAPS("TRACKING_ID"),
    ACCOUNTID_CAPS("ACCOUNTID"),

    TERMINALID("terminalid"),

    //for XXXWaitServlet
    LANGUAGE_CAPS("LANGUAGE"),
    COUNTRY("country"),
    DAY("DAY"),
    MONTH("MONTH"),
    YEAR("YEAR"),
    SSN("ssn"),
    FIRSTNAME("firstname"),
    LASTNAME("lastname"),
    FIRSTNAME_WC("firstname_wc"),
    LASTNAME_WC("lastname_wc"),
    EMAILADDR("emailaddr"),
    PAN("PAN"),
    CVV("ccid"),
    STREET("street"),
    CITY("city"),
    STATE("state"),
    ZIP("zip"),
    TELNO("telno"),
    TELCC("telnocc"),
    EXPIRE_MONTH("EXPIRE_MONTH"),
    EXPIRE_YEAR("EXPIRE_YEAR"),
    NAME("NAMECP"),
    CCCP("CCCP"),
    ADDRCP("ADDRCP"),
    CARDTYPE_CAPS("CARDTYPE"),
    TMPL_VERSION("TMPL_VERSION"),
    CUSTOMERID("CUSTOMERID"),
    CUSTOMER_ID("customerid"),
    CUSTOMER_BANK_ID("CUSTOMER_BANK_ID"),
    PAYOUT_TYPE("PAYOUT_TYPE"),

    ACCACTIVATIONDATE("accActivationDate"),
    ACCCHANGEDATE("accChangeDate"),
    ACCPWCHANGEDATE("accPwChnageDate"),
    ADDRESSUSEDATE("addressUseDate"),
    PAYMENTACCACTIVATIONDATE("paymentAccActivationDate"),

    //For PayDR
    TRACKINGID("Ref"),
    TRACKING_ID("TrackingId"),
    TRANS_STATUS("Result"),
    CARDHOLDER("CARDHOLDER"),

    //for PayVoucher
    ORDER_DESC("ORDER_DESCRIPTION"),

    //for JetonVoucher
    VOUCHER_NUMBER("vouchernumber"),
    SECURITY_CODE("securityCode"),
    EXP_YEAR("expiry_year"),
    EXP_MONTH("expiry_month"),



    //for UGSPay
    NAME_SMALL("name"),

    //for MyMonedero
    STATUS("status"),
    STATUS_LIST("status"),
    MESSAGE("message"),
    CHECKSUM("checksum"),
    TRACKING_ID_REF("ref"),
    CHECKSUM_CAPS("CHECKSUM"),
    TMPL_COUNTRYCODE("TMPL_COUNTRYCODE"),

    //for partner
    USERNAME("username"),
    PASSWORD("passwd"),
    WL_INVOICE_COMPANY_NAME("partnerNameForWLInvoice"),
    COMPANY_NAME("company_name"),
    CONTACT_PERSON("contact_persons"),
    CONTACT_EMAIL("contact_emails"),
    PROCESSOR_PARTNERID("processor_partnerid"),

    //todo country, telno(pikup from defined above)
    SITENAME("sitename"),
    SUPPORT_MAIL_ID("supportmailid"),
    ADMIN_MAIL_ID("adminmailid"),
    SUPPORT_URL("supporturl"),
    DOCUMENTATION_URL("documentationurl"),
    HOST_URL("hosturl"),
    SALES_EMAIL("salesemail"),
    BILLING_EMAIL("billingemail"),
    NOTIFY_EMAIL("notifyemail"),
    COMPANY_FROM_ADDRESS("companyfromaddress"),
    SUPPORT_FROM_ADDRESS("supportfromaddress"),
    SMTP_HOST("smtp_host"),
    SMTP_PORT("smtp_port"),
    SMTP_USER("smtp_user"),
    SMTP_PASSWORD("smtp_password"),
    PARTNERID("partnerid"),
    AUTHTOKEN("AUTHTOKEN"),
    ADDRESS("address"),
    CONTACT_CCEMAIL("contact_ccmailid"),
    SALES_CCEMAIL("sales_ccemailid"),
    BILLING_CCEMAIL("billing_ccemailid"),
    NOTIFY_CCEMAIL("notify_ccemailid"),
    FRAUD_CCEMAIL("fraud_ccemailid"),
    CB_CCEMAIL("chargeback_ccemailid"),
    REFUND_CCEMAIL("refund_ccemailid"),
    TECH_CCEMAIL("technical_ccemailid"),
    PRIVACY_URL("privacyUrl"),
    PARTNERLIST("partnerlist"),

    //for SingleCallXXX
    CARDNUMBER("cardnumber"),
    CVV_SMALL("cvv"),
    EXPIRY_YEAR("expiry_year"),
    EXPIRY_MONTH("expiry_month") ,
    BIRTHDATE("birthdate"),
    LANGUAGE("language"),
    TRACKINGID_SMALL("trackingid"),
    CAPTUREAMOUNT("captureamount"),
    REASON("reason"),
    REFUNDAMOUNT("refundamount"),
    CARDHOLDERIP("cardholderipaddress"),
    DEVICE("device"),

    //for commonpaymentprocess for Direct Kit
    //todo not yet added in InputValidator.java
    /*ACCOUNTID_SMALL("accountid"),
    FROMID_SMALL("fromid"),
    FROMTYPE_SMALL("fromtype"),
    REDIRECT_URL("redirecturl"),
    CURRENCY("currency"),
    HEADER("header"),
    CCNUM("ccnum"),
    CARDHOLDER_SMALL("cardholder"),
    EXPIRY_DATE("expdate"),
    EMAIL("email"),
    ISSERVICE("isservice"),
    CARD_TRANSACTION_LIMIT("card_transaction_limit"),
    CARD_CHECK_LIMIT("card_check_limit"),
    EXP_MONTH("expmonth"),
    EXP_YEAR("expyear"),
    MACHINEID("machineid"),

    CHARG("charge"),
    TAX_PERCENTAGE("taxpercentage"),*/
    BRANDNAME("brandname"),
    MIDDLENAME("middlename"),
    NOTIFICATIONURL("notificationUrl"),

    IPADDRESS("ipAddress"),
    MERCHANTID("merchantid"),
    AGENTID("agentid"),

    //validation starts for ICICI Module
    //MemberDetails.java
    MEMBERID("memberid"),
    ACTIVATION("activation"),
    ICICI("icici"),
    RESERVES("reserves"),
    CHARGEPER("chargeper"),
    FROMDATE("fdate"),
    TODATE("tdate"),
    FROMMONTH("fmonth"),
    TOMONTH("tmonth"),
    FROMYEAR("fyear"),
    TOYEAR("tyear"),

    //blocked email
    PAGENO("SPageno"),
    RECORDS("SRecords"),
    EMAIL("email"),
    //block domain
    DOMAIN("domain"),
    //Transactions Details
    NAME_TRA("name"),
    CARDNAME_SMALL("name"),
    DESC("desc"),
    ORDERDESC("orderdesc"),
    TRACKINGID_TRA("STrackingid"),
    COMMASEPRATED_TRACKINGID_TRA("STrackingid"),
    GATEWAY("gateway"),
    GATEWAY1("pgtypeid"),
    GATEWAY_NAME("name"),
    STARTTIME("starttime"),
    ENDTIME("endtime"),
    //MemberDetails
    YEAR_SMALL("year"),
    MONTH_SMALL("month"),
    COMPANY("company"),
    //Transaction Details
    ACCOUNTID_SMALL("accountid"),
    FIRSTFOURCCNUM("firstfourofccnum"),
    LASTFOURCCNUM("lastfourofccnum"),
    //change password
    OLD_PASSWORD("oldpwd"),
    NEW_PASSWORD("newpwd"),
    CONFIRM_PASSWORD("confirmpwd"),
    //send mail
    MAILTYPE("mailtype"),
    //Unblock Account
    LOGIN("login"),
    //invoice
    ORDERID("orderid"),
    INVOICENO("invoiceno"),
    INV("inv"),
    LATEFEE("latefee"),
    //QwipiInquiry
    FROMID("fromid"),
    //QwipiReconciliationList
    PAYMENTNUMBER("paymentnumber"),
    //EcoreReconciliationProcess
    ECOREAMOUNT("amount_"),
    ECOREPAYMENTORDERNUMBER("ecorePaymentOrderNumber_"),
    ECOREREMARK("remark_"),
    ECORESTATUS("status_"),
    ECOREDESC("description_"),
    ECOREDBPAYMENT("dbpaymentnumber_"),
    ECOREREFUNDAMOUNT("refundamount_"),
    //commonRefund
    PAYMENTID("paymentid"),
    PURCHASE_IDENTIFIER("PURCHASE_IDENTIFIER"),
    AUTHORIZATION_CODE("AUTHORIZATION_CODE"),
    RRN("RRN"),
    ARN("ARN"),
    STAN("STAN"),
    //compliance
    CCNUM("ccnum"),
    //whitelist module
    FIRST_SIX("firstsix"),
    LAST_FOUR("lastfour"),
    //Charge Master
    CHARGEID("chargeid"),
    CHARGENAME("chargename"),
    ISINPUTREQ("isinputrequired"),
    MAPPINGID("mappingid"),
    PAYMODE("paymode"),
    CHARGEVALUE("chargevalue"),
    CHARGETYPE("chargetype"),
    MCHARGEVALUE("mchargevalue"),
    ACHARGEVALUE("achargevalue"),
    PCHARGEVALUE("pchargevalue"),
    //Gateway Master
    CURRENCY("currency"),
    MERCHANT_ID("MERCHANT_ID"),
    EXP_DATE_OFFSET("EXP_DATE_OFFSET"),
    INITIAL("INITIAL"),
    QUANTITY("QUANTITY"),
    QUANTITY_TOTAL("QUANTITY_TOTAL"),
    DEFAULT_QUANTITY("QUANTITY"),
    PRODUCT_AMOUNT("PRODUCT_AMOUNT"),
    PRODUCT_TOTAL("PRODUCT_TOTAL"),
    DEFAULT_PRODUCT_AMOUNT("PRODUCT_AMOUNT"),
    PRODUCT_DESCRIPTION("PRODUCT_DESCRIPTION"),
    DEFAULT_PRODUCT_DESCRIPTION("PRODUCT_DESCRIPTION"),
    DEFAULT_PRODUCT_UNIT("DEFAULT_PRODUCT_UNIT"),
    PGTYPEID("pgtypeid"),
    ACTION("action"),
    CHARGEPERCENTAGE("chargepercentage"),
    TAXPER("taxpercentage"),
    WITHDRAWCHARGE("withdrawalcharge"),
    REVERSECHARGE("reversalcharge"),
    CHARGEBACKCHARGE("chargebackcharge"),
    CHARGESACCOUNT("chargesaccount"),
    TAXACCOUNT("taxaccount"),
    HIGHRISKAMOUNT("highriskamount"),
    GATEWAY_TABLE("gateway_table_name"),
    ADDRESS_SMALL("address"),
    PARTNER_ID("partnerId"),
    LOGONAME("logoName"),
    FIRSTNAME_REST("FIRSTNAME_REST"),
    LASTNAME_REST("LASTNAME_REST"),
    //Gateway Master
    ALIASNAME("aliasname"),
    DISPLAYNAME("displayname"),
    ISMASTERCARDSUPPORTED("ismastercardsupported"),
    SHORTNAME("shortname"),
    SITE("site"),
    PATH("path"),
    CHARGEBACKPATH("chargebackpath"),
    ISCVVREQUIRED("iscvvrequired"),
    MONTHLYCARDLIMIT("monthlycardlimit"),
    DAILYAMOUNTLIMIT("dailyamountlimit"),
    MONTHLYAMOUNTLIMIT("monthlyamountlimit"),
    DAILYCARDLIMIT("dailycardlimit"),
    WEEKLYCARDLIMIT("weeklycardlimit"),
    MINTRANSACTIONLIMIT("mintransactionamount"),
    MAXTRANSACTIONLIMIT("maxtransactionamount"),
    DAILYCARDAMOUNTLIMIT("dailycardamountlimit"),
    WEEKLYCARDAMOUNTLIMIT("weeklycardamountlimit"),
    MONTHLYCARDAMOUNTLIMIT("monthlycardamountlimit"),
    COLUMN_NAME("columnnames"),
    GATEWAY_TABLENAME("gatewaytablename"),
    //ActionHistory
    SEARCH_TYPE("searchType"),
    SEARCH_ID("SearchId"),
    GATEWAY_TYPE("gatewayType"),
    //AdminChargebackReverseList
    SDESCRIPTION("SDescription"),
    STRACKINGID("STrakingid"),
    SCAPTUREID("SCaptureid"),
    //AdminDoProofReceived
    ICICITRANSEID("icicitransid"),
    //AdminDoReverseTransaction
    REFUNDID("refundid"),
    REFUND_CODE("refundcode"),
    REFUND_RECEIPT_NO("refundreceiptno"),
    //chargeback report
    SEARCHTYPE_SMALL("searchtype"),
    //DoChargebackTransaction
    DATE_SMALL("date"),
    CB_REF_NO("cbrefnumber"),
    CB_AMOUNT("cbamount"),
    CB_REASON("cbreason"),
    //ExceptionalTransaction
    SEARCH_STATUS("searchstatus"),
    //listAgent
    AGENT_ID("agentid"),
    AGENT_NAME("agentname"),
    PARTNER_NAME("partnerName"),
    //login
    PASSWORD_FULL("password"),
    //RawCheck
    MERCHANTID_CAPS("MERCHANTID"),
    //SetAccount
    DAILY_AMT_LIMIT("daily_amount_limit"),
    MONTHLY_AMT_LIMIT("monthly_amount_limit"),
    DAILY_CARD_LIMIT("daily_card_limit"),
    WEEKLY_CARD_LIMIT("weekly_card_limit"),
    MONTHLY_CARD_LIMIT("monthly_card_limit"),
    DAILY_CARD_AMT_LIMIT("daily_card_amount_limit"),
    WEEKLY_CARD_AMT_LIMIT("weekly_card_amount_limit"),
    MONTHLY_CARD_AMT_LIMIT("monthly_card_amount_limit"),

    DAILY_AVG_TICKET("daily_avg_ticket"),
    WEEKLY_AVG_TICKET("weekly_avg_ticket"),
    MONTHLY_AVG_TICKET("monthly_avg_ticket"),

    DAILY_AMOUNT_LIMIT_CHECK("daily_amount_limit_check"),
    WEEKLY_AMOUNT_LIMIT_CHECK("weekly_amount_limit_check"),
    MONTHLY_AMOUNT_LIMIT_CHECK("monthly_amount_limit_check"),
    DAILY_CARD_LIMIT_CHECK("daily_card_limit_check"),
    WEEKLY_CARD_LIMIT_CHECK("weekly_card_limit_check"),
    MONTHLY_CARD_LIMIT_CHECK("monthly_card_limit_check"),
    DAILY_CARD_AMOUNT_LIMIT_CHECK("daily_card_amount_limit_check"),
    WEEKLY_CARD_AMOUNT_LIMIT_CHECK("weekly_card_amount_limit_check"),
    MONTHLY_CARD_AMOUNT_LIMIT_CHECK("monthly_card_amount_limit_check"),

    MIN_TRANSE_AMT("min_trans_amount"),
    MAX_TRANSE_AMT("max_trans_amount"),
    MIN_PAYOUT_AMOUNT("min_payout_amount"),
    IS_ACTIVE("isActive"),
    PRIORITY("priority"),
    IS_TEST("isTest"),
    ADDRESS_DETAILS("addressDetails"),
    ADDRESS_VALIDATION("addressValidation"),
    WEEKLY_AMT_LIMIT("weekly_amount_limit"),
    CARD_DETAIL_REQUIRED("cardDetailRequired"),

    //form here

    DAILY_APPROVAL_RATIO("daily_approval_ratio"),
    WEEKLY_APPROVAL_RATIO("weekly_approval_ratio"),
    MONTHLY_APPROVAL_RATIO("monthly_approval_ratio"),

    INACTIVE_PERIOD("inactive_period_threshold"),
    FIRST_SUBMISSION("first_submission_threshold"),
    MANUAL_CAPTURE_THRESHOLD("manual_capture_threshold"),

    DAILY_CHARGEBACK_RATIO("daily_cb_ratio"),
    WEEKLY_CHARGEBACK_RATIO("weekly_cb_ratio"),
    MONTHLY_CHARGEBACK_RATIO("monthly_cb_ratio"),

    DAILY_CHARGEBACK_RATIO_AMOUNT("daily_cb_ratio_amount"),
    WEEKLY_CHARGEBACK_RATIO_AMOUNT("weekly_cb_ratio_amount"),
    AMOUNT_MONTHLY_CHARGEBACK_RATIO("monthly_cb_ratio_amount"),

    DAILY_REFUND_RATIO("daily_rf_ratio"),
    WEEKLY_REFUND_RATIO("weekly_rf_ratio"),
    MONTHLY_REFUND_RATIO("monthly_rf_ratio"),

    DAILY_REFUND_RATIO_AMOUNT("daily_rf_ratio_amount"),
    WEEKLY_REFUND_RATIO_AMOUNT("weekly_rf_ratio_amount"),
    MONTHLY_REFUND_RATIO_AMOUNT("monthly_rf_ratio_amount"),

    DAILY_AVGTICKET_AMOUNT("daily_avgticket_threshold"),
    WEEKLY_AVGTICKET_AMOUNT("weekly_avgticket_threshold"),
    MONTHLY_AVGTICKET_AMOUNT("monthly_avgticket_threshold"),

    DAILY_QUARTERLY_AVGTICKET_THRESHOLD("daily_vs_quarterly_avgticket_threshold"),
    CHARGEBACK_INCOUNT_FORALERT("alert_cbcount_threshold"),
    CHARGEBACK_INCOUNT("suspend_cbcount_threshold"),

    MONTHLY_CREDIT_AMOUNT("priormonth_rf_vs_currentmonth_sales_threshold"),
    SAME_CARDAMOUNT_THRESHOLD("samecard_cardamount_threshold"),
    RESUME_PROCESSING_ALERT("resume_processing_alert"),
    DAILY_AVGTICKET_PERCENTAGE_THRESHOLD("daily_avgticket_percentage_threshold"),
    DAILY_CB_RATIO_SUSPENSION("daily_cb_ratio_suspension"),
    DAILY_CB_AMOUNT_RATIO_SUSPENSION("daily_cb_amount_ratio_suspension"),
    SAMECARD_SAMEAMOUNT_CONSEQUENCE_THRESHOLD("samecard_sameamount_consequence_threshold"),
    SAMECARD_CONSEQUENTLY_THRESHOLD("samecard_consequently_threshold"),

    ///////////////////////

    MIN_TRANSACTION_AMOUNT("min_transaction_amount"),
    MAX_TRANSACTION_AMOUNT("max_transaction_amount"),

    CHARGE_PER("chargePercentage"),

    FIX_APPROVE_CHARGE("fixApprovalCharge"),
    FIX_DECLINE_CHARGE("fixDeclinedCharge"),
    TAX_PER("taxper"),
    REVERSE_PERCENTAGE("reservePercentage"),
    FRAUDE_VARIFICATION_CHARGE("fraudVerificationCharge"),
    ANNUAL_CHARGE("annualCharge"),
    SETUP_CHARGE("setupCharge"),
    FX_CLERANCE_CHARGE_PER("fxClearanceChargePercentage"),
    MONTHLY_GATEWAY_CHARGE("monthlyGatewayCharge"),
    MONTHLY_ACC_MN_CHARGE("monthlyAccountMntCharge"),
    REPORT_CHARGE("reportCharge"),
    FRAUDULENT_CHARGE("fraudulentCharge"),
    AUTO_REPRESENTATION_CHARGE("autoRepresentationCharge"),
    INTERCHANGE_PLUS_CHARGE("interchangePlusCharge"),
    //editGatewayAccountsDetails
    OLD_DISPLAYNAME("0ld_displayname"),
    CHARGEBACK_PATH("chargeback_path"),
    ISCVV_REQ("isCVVrequired"),
    //CrawlController
    SEARCH_DOMAIN("SearchURL"),
    SEARCH_WORD("SearchWord"),
    //ipAddress for vt
    IPADDR("ipaddr"),

    CUSTNAME("custname"),
    //customer support
    PASSWORD2("password"),
    DATASOURCE("table"),
    SHIPPINGPODBATCH("Shippingbno"),
    SHIPPINGPOD("Shippingid"),
    PHONENO("phoneno"),
    REMARKS("remark"),
    SHIPPINGSTATUS("shippingstatus"),
    CSEEid("Eid"),
    CSEname("Ename"),
    LASTFOURCC("lastfourofccnum"),
    FIRSTSIXCC("firstfourofccnum"),
    FIRSTSIX("firstsix"),
    BIN("bin"),
    LASTFOUR("lastfour"),

    INVOICE_CANCEL_REASON("cancelreason"),

    //for merchant Report
    PAID("paid"),
    FDATE("fromdate"),
    TDATE("todate"),
    FILENAME("file"),
    SMALL_ACTION("action"),
    PID("pid"),
    FIRST_SIXS("firstsix"),
    LAST_FOURS("lastfour"),
    //For Wiremanager Entry ...
    MINUS_AMOUNT("balanceamount"),
    DATE_TIME("firstDate"),
    COMMA_SEPRATED_NUM("trackingid"),
    BANKRECEIVEDID("bankreceivedid"),
    SETTLEMENTDATE("settlementdate"),
    SETTLEMENTIME("settlementtime"),
    EXPECTED_STARTDATE("expected_startDate"),
    EXPECTED_ENDDATE("expected_endDate"),
    ACTUAL_SARTDATE("actual_startDate"),
    ACTUAL_ENDDATE("actual_endDate"),
    SETTLEMENTCYCLEID("settlementcycleid"),
    ROLLINGRESERVEDATEUPTO("rollingreservedateupto"),
    ISSETTLEMENTCRONEXECCUTED("issettlementcron"),
    ISPAYOUCRONEXECUTED("ispayoutcron"),
    ISDAYLIGHT("isdaylight"),
    ISPAID("isPaid"),
    BANKMERCHANTID("bankmerchantid"),
    //For Fraud System Master
    FSID("fsid"),
    FSNAME("fsname"),
    BANKWIREMANGERID("bankwiremangerid"),
    PARENT_BANKWIREID("parent_bankwireId"),
    PROCESSINGAMOUNT("processingamount"),
    GROSSAMOUNT("grossamount"),
    NETFINALAMOUNT("netfinalamout"),
    UNPAIDAMOUNT("unpaidamount"),
    ISROLLINGRESERVERELAEASEWIRE("isrollingreservereleasewire"),
    DECLINEDCOVEREDDATEUPTO("declinedcoveredupto"),
    CHARGEBACKCOVEREDDATEUPTO("chargebackcoveredupto"),
    REVERSEDCOVEREDDATEUPTO("reversedcoveredupto"),
    SETTLEMENTREPORTFILE("settlement_report_file"),
    SETTLEMENTTRANSACTIONFILE("settlement_transaction_file"),
    //temporary has to be removed
    EXPECTED_STARTTIME("expected_startTime"),
    EXPECTED_ENDTIME("expected_endTime"),
    ACTUAL_STARTTIME("actual_startTime"),
    ACTUAL_ENDTIME("actual_endTime"),
    ROLLINGRELEASETIME("rollingRelease_Time"),
    DECLINECOVERTIME("declinedcoveredtime"),
    CHARGEBACKCOVEREDTIME("chargebackcoveredtime"),
    REVERSECOVEREDTIME("reversedcoveredtime"),
    TIMEDIFFNORMAL("timedifferencenormal"),
    TIMEDIFFDAYLIGHT("timedifferencedaylight"),

    HIGH_RISK_AMT("aptprompt"),
    MAX_SCORE_ALLOWED("maxscoreallowed"),
    MAX_SCORE_REVERSEL("maxscoreautoreversal"),
    REFUND_DAILY_LIMIT("refunddailylimit"),
    CHARGETECHNAME("chargeTechName"),
    FRAUDEMAIL("fraudemail"),
    IDEAL_BANK_CODE("senderBankCode"),
    BANKIP("bankip"),
    APPTOID("apptoid"),
    APPLICATIONID("applicationid"),
    BANK_SMALL("bank"),
    ARCHIVE_SMALL("archive"),
    RBID("rbid"),
    RBIDS("recurring_subscription_id"),
    TRACKINGS("trackingid"),
    RESERVEFIELD1("reservedField1"),
    RESERVEFIELD2("reservedField2"),
    TOKENVALIDDAYS("tokenvaliddays"),
    RISK_RULE_NAME("riskRuleName"),
    RISK_RULE_LABEL("riskRuleLabel"),
    RISK_RULE_DESCRIPTION("riskRuleDescription"),
    BUSINESS_RULE_NAME("businessRuleName"),
    BUSINESS_RULE_LABEL("businessRuleLabel"),
    BUSINESS_RULE_DESCRIPTION("businessRuleDescription"),
    BUSINESS_RULE_OPERATOR("businessRuleOperator"),
    RISKPROFILEID("profileid"),
    RISKPROFILENAME("riskProfileName"),
    RISKRULEID("ruleid"),
    BUSINESSRULEID("businessRuleId"),
    RISKSCORE("score"),
    RISK_ISAPPLICABLE("isApplicable"),
    BUSINESSPROFILEID("profileid"),
    BUSINESSPROFILENAME("businessProfileName"),
    Bank_EmailID("bank_emailid"),
    COUNTOFROW("countOfRow"),
    USERID("userid"),
    ONLINEPROCESSINGURL("onlineProcessingUrl"),
    OFFLINEPROCESSINGURL("offlineProcessingUrl"),
    ONLINETHRESHOLD("onlineThreshold"),
    OFFLINETHRESHOLD("offlinethreshold"),
    DEFAULTMODE("defaultMode"),
    BUSINESSPROFILE_ID("businessProfile"),
    BACKGROUND("background"),
    FOREGROUND("foreground"),
    FONT("font"),
    MERCHANTLOGO("merchantlogo"),
    CONSOLIDATEDID("consolidatedId"),
    RULETYPE("ruleType"),
    QUERY("businessRuleQuery"),
    BUSINESS_RULE_OPERATOR_QUERY("businessRuleOperatorQuery"),
    REGEX("regex"),

    //paymitco
    ACCOUNTNUMBER("accountnumber"),
    ROUTINGNUMBER("routingnumber"),
    ACCOUNTTYPE("ACCOUNTTYPE"),
    CHECKNUMBER("CHECKNUMBER"),

    //BANK Account Information
    IBAN("iban"),
    BIC("bic"),
    MANDATEID("mandateId"),
    ACCOUNT_HOLDER("ACCOUNT_HOLDER"),

    PAYMENTBRAND("PAYMENTBRAND"),
    PAYMENTMODE("PAYMENTMODE"),
    PAYON_BIRTHDATE("birthdateWithDash"),
    REFERENCE_CURRENCY("StrictString"),
    REFERENCE_AMOUNT("tenDigitAmount"),
    REFERENCE_TRACKINGID("Numbers"),
    TRANSACTION_TYPE("transactiontype"),
    TRANS_TYPE("TRANS_TYPE"),
    TRANSACTION_DATE("TRANSACTION_DATE"),
    RECURRINGTYPE("RECURRINGTYPE"),
    CREATEREGISTRATION("CREATEREGISTRATION"),

    SALES_CONTACTPERSON("sales_contactperson"),
    BILLING_CONTACTPERSON("billing_contactperson"),
    NOTIFY_CONTACTPERSON("notify_contactperson"),
    FRAUD_CONTACTPERSON("fraud_contactperson"),
    ACTION_TYPE("ACTION_TYPE"),

    //SMS Gateway
    SMS_USER("sms_user"),
    SMS_PASSWORD("sms_password"),
    FROM_SMS("from_sms"),

    //Contractual Partner
    BANK_NAME("bankName"),
    CONTRACTUAL_PARTNER_NAME("contractualpartname"),
    CONTRACTUAL_PARTNER_ID("contractualpartid"),

    //Monitoring Partner Master
    MONITOINGPARAID("monitoing_para_id"),
    MONITOINGPARANAME("monitoing_para_name"),
    MONITOINGONCHANNEL("monitoing_onchannel"),

    MAINCONTACT_CC("maincontact_ccmailid"),
    MAINCONTACT_PHONE("maincontact_phone"),
    CBCONTACT_NAME("cbcontact_name"),
    CBCONTACT_MAIL("cbcontact_mailid"),
    CBCONTACT_CC("cbcontact_ccmailid"),
    CBCONTACT_PHONE("cbcontact_phone"),
    REFUNDCONTACT_NAME("refundcontact_name"),
    REFUNDCONTACT_MAIL("refundcontact_mailid"),
    REFUNDCONTACT_CC("refundcontact_ccmailid"),
    REFUNDCONTACT_PHONE("refundcontact_phone"),
    SALESCONTACT_NAME("salescontact_name"),
    SALESCONTACT_MAIL("salescontact_mailid"),
    SALESCONTACT_CC("salescontact_ccmailid"),
    SALESCONTACT_PHONE("salescontact_phone"),
    FRAUDCONTACT_NAME("fraudcontact_name"),
    FRAUDCONTACT_MAIL("fraudcontact_mailid"),
    FRAUDCONTACT_CC("fraudcontact_ccmailid"),
    FRAUDCONTACT_PHONE("fraudcontact_phone"),
    TECHCONTACT_NAME("technicalcontact_name"),
    TECHCONTACT_MAIL("technicalcontact_mailid"),
    TECHCONTACT_CC("technicalcontact_ccmailid"),
    TECHCONTACT_PHONE("technicalcontact_phone"),
    BILLINGCONTACT_NAME("billingcontact_name"),
    BILLINGCONTACT_MAIL("billingcontact_mailid"),
    BILLINGCONTACT_CC("billingcontact_ccmailid"),
    BILLINGCONTACT_PHONE("billingcontact_phone"),
    WEBSITE("WEBSITE"),
    GENDER("GENDER"),
    EMICOUNT("emiCount"),

    //get invoice

    START ("start"),
    END ("end"),
    GST ("gst"),

    //OTP
    COUNTRY_OTP("country_otp"),

    //get token from secret key

    PARTNER_KEY("key"),


    REFUND_CURRENCY ("refund_currency"),

    CLIENT_TRANSACTION_ID ("client_transaction_id"),
    CALL_TYPE ("call_type"),
    SUPERPARTNER_ID("superadminid"),

    PAYMENTTERMS("paymentterms"),
    UNIT("unit"),
    DEFAULTUNIT("defaultunit"),
    VPA_ADDRESS_DAILY_COUNT("vpaAddressDailyCount"),
    CUSTOMER_IP_DAILY_COUNT("customerIpDailyCount"),
    CUSTOMER_NAME_DAILY_COUNT("customerNameDailyCount"),
    CUSTOMER_EMAIL_DAILY_COUNT("customerEmailDailyCount"),
    CUSTOMER_PHONE_DAILY_COUNT("customerPhoneDailyCount"),

    VPA_ADDRESS_DAILY_AMOUNT_LIMIT("vpaAddressDailyAmountLimit"),
    CUSTOMER_IP_DAILY_AMOUNT_LIMIT("customerIpDailyAmountLimit"),
    CUSTOMER_NAME_DAILY_AMOUNT_LIMIT("customerNameDailyAmountLimit"),
    CUSTOMER_EMAIL_DAILY_AMOUNT_LIMIT("customerEmailDailyAmountLimit"),
    CUSTOMER_PHONE_DAILY_AMOUNT_LIMIT("customerPhoneDailyAmountLimit"),

    VPA_ADDRESS_MONTHLY_COUNT("vpaAddressMonthlyCount"),
    VPA_ADDRESS_MONTHLY_AMOUNT_LIMIT("vpaAddressMonthlyAmountLimit"),
    CUSTOMER_EMAIL_MONTHLY_COUNT("customerEmailMonthlyCount"),
    CUSTOMER_EMAIL_MONTHLY_AMOUNT_LIMIT("customerEmailMonthlyAmountLimit"),
    CUSTOMER_PHONE_MONTHLY_COUNT("customerPhoneMonthlyCount"),
    CUSTOMER_PHONE_MONTHLY_AMOUNT_LIMIT("customerPhoneMonthlyAmountLimit"),
    BANK_ACCOUNT_NO_MONTHLY_COUNT("bankAccountNoMonthlyCount"),
    BANK_ACCOUNT_NO_DAILY_COUNT("bankAccountNoDailyCount"),
    BANK_ACCOUNT_NO_MONTHLY_AMOUNT_LIMIT("bankAccountNoMonthlyAmountLimit"),
    BANK_ACCOUNT_NO_DAILY_AMOUNT_LIMIT("bankAccountNoDailyAmountLimit"),

    CUSTOMER_IP_MONTHLY_COUNT("customerIpMonthlyCount"),
    CUSTOMER_IP_MONTHLY_AMOUNT_LIMIT("customerIpMonthlyAmountLimit"),
    CUSTOMER_NAME_MONTHLY_COUNT("customerNameMonthlyCount"),
    CUSTOMER_NAME_MONTHLY_AMOUNT_LIMIT("customerNameMonthlyAmountLimit"),

    BANK_ACCOUNT_NO("bankaccount"),

    PAYMODEID("paymodeid"),
    CARDTYPEID("ctype1")
    ;

    private String fieldName;

    InputFields(String fieldName)
    {
        this.fieldName = fieldName;
    }


    @Override
    public String toString()
    {
        return fieldName;
    }

}
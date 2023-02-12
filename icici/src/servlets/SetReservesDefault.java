import com.directi.pg.*;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/*
Created by IntelliJ IDEA.
User: Mahima
Date: 27/02/18
Time: 3:22 PM
To change this template use File | Settings | File Templates.
*/
public class SetReservesDefault extends HttpServlet
{
    private static Logger logger = new Logger(SetReservesDefault.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        String errorMsg = "";
        String EOL = "<br>";
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder chargeBackMessage = new StringBuilder();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();


        String partnerId = req.getParameter("partnerid");
        String isValidateEmail = req.getParameter("is_validate_email");
        String customerReminderMail = req.getParameter("cust_reminder_mail");
        String dailyAmountLimit = req.getParameter("daily_amount_limit");
        String monthlyAmountLimit = req.getParameter("monthly_amount_limit");
        String dailyCardLimit = req.getParameter("daily_card_limit");
        String weeklyCardLimit = req.getParameter("weekly_card_limit");
        String monthlyCardLimit = req.getParameter("monthly_card_limit");
        String isRefund = req.getParameter("is_refund");
        String personalInfoDisplay = req.getParameter("personal_info_display");
        String personalInfoValidation = req.getParameter("personal_info_validation");
        String hostedPaymentPage = req.getParameter("hosted_payment_page");
        String consent = req.getParameter("consent");
        String refundDailyLimit = req.getParameter("refund_daily_limit");
        String active = req.getParameter("activation");
        String merchantInterfaceAccess = req.getParameter("merchant_interface_access");
        String isPharma = req.getParameter("is_pharma");
        String hasPaid = req.getParameter("haspaid");
        String isService = req.getParameter("isservice");
        String hrAlertProof = req.getParameter("hralertproof");
        String autoRedirect = req.getParameter("auto_redirect");
        String vbv = req.getParameter("vbv");
        String hrParameterised = req.getParameter("hrparameterised");
        String checkLimit = req.getParameter("check_limit");
        String invoiceTemplate = req.getParameter("invoice_template");
        String isPoweredBy = req.getParameter("is_powered_by");
        String template = req.getParameter("template");
        String isWhiteListed = req.getParameter("iswhitelisted");
        String masterCardSupported = req.getParameter("mastercard_supported");
        String isPODRequired = req.getParameter("is_pod_required");
        String dailyCardAmountLimit = req.getParameter("daily_card_amount_limit");
        String weeklyCardAmountLimit = req.getParameter("weekly_card_amount_limit");
        String monthlyCardAmountLimit = req.getParameter("monthly_card_amount_limit");
        String cardCheckLimit = req.getParameter("card_check_limit");
        String cardTransactionLimit = req.getParameter("card_transaction_limit");
        String autoSelectTerminal = req.getParameter("auto_select_terminal");
        String maxScoreAllowed = req.getParameter("max_score_allowed");
        String maxScoreReversal = req.getParameter("max_score_auto_reversal");
        String weeklyAmountLimit = req.getParameter("weekly_amount_limit");
        String isAppManagerActivate = req.getParameter("is_appmanager_activate");
        String isCardRegistrationAllowed = req.getParameter("is_card_registration_allowed");
        String isRecurring = req.getParameter("is_recurring");
        String isRestrictedTicket = req.getParameter("is_restricted_ticket");
        String isTokenizationAllowed = req.getParameter("is_tokenization_allowed");
        String tokenValidDays = req.getParameter("token_valid_days");
        String isAddressDetailsRequired = req.getParameter("is_address_details_required");
        String isCardEncryptionEnable = req.getParameter("is_card_encryption_enable");
        String blacklistTransaction = req.getParameter("blacklist_transaction");
        String flightMode = req.getParameter("flight_mode");
        /*String emailSent = req.getParameter("merchant_email_sent");*/
        String fraudCheck = req.getParameter("online_fraud_check");
        String splitPayment = req.getParameter("is_split_payment");
        String splitPaymentType = req.getParameter("split_payment_type");
        String isExcessCaptureAllowed = req.getParameter("is_excesscapture_allowed");
        String refundAllowedDays = req.getParameter("refundallowed_days");
        String chargebackAllowedDays = req.getParameter("chargeback_allowed_days");
        String isRestWhiteListed = req.getParameter("is_rest_whitelisted");
        String merchantSmsActivation = req.getParameter("merchant_sms_activation");
        String customerSmsActivation = req.getParameter("customer_sms_activation");
        String isEmailLimitEnabled = req.getParameter("email_limit_enabled");
        String isIpWhiteListed = req.getParameter("is_ip_whitelisted");
        String binService = req.getParameter("bin_service");
        String expDateOffset = req.getParameter("exp_date_offset");
        String supportSection = req.getParameter("support_section");
        String supportNoNeeded = req.getParameter("supportNoNeeded");
        String cardWhiteListLevel = req.getParameter("card_whitelist_level");
        String multiCurrencySupport = req.getParameter("multi_Currency_support");
        String dashBoard = req.getParameter("dashboard_access");
        String accounting = req.getParameter("accounting_access");
        String settings = req.getParameter("setting_access");
        String transactionManagement = req.getParameter("transactions_access");
        String invoicing = req.getParameter("invoicing_access");
        String virtualTerminal = req.getParameter("virtualterminal_access");
        String merchantManagement = req.getParameter("merchantmgt_access");
        String settingsMerchantConfigAccess = req.getParameter("settings_merchant_config_access");
        String settingsFraudRuleConfigAccess = req.getParameter("settings_fraudrule_config_access");
        String accountsAccountSummaryAccess = req.getParameter("accounts_account_summary_access");
        String accountsChargesSummaryAccess = req.getParameter("accounts_charges_summary_access");
        String accountsTransactionSummaryAccess = req.getParameter("accounts_transaction_summary_access");
        String accountsReportSummaryAccess = req.getParameter("accounts_reports_summary_access");
        String settingsMerchantProfileAccess = req.getParameter("settings_merchant_profile_access");
        String settingsOrganisationProfileAccess = req.getParameter("settings_organisation_profile_access");
        /*String settingsCheckoutPageAccess = req.getParameter("settings_checkout_page_access");*/
        String settingsGenerateKeyAccess = req.getParameter("settings_generate_key_access");
        String settingsInvoiceConfigAccess = req.getParameter("settings_invoice_config_access");
        String transMgtTransactionAccess = req.getParameter("transmgt_transaction_access");
        String transMgtCaptureAccess = req.getParameter("transmgt_capture_access");
        String transMgtReversalAccess = req.getParameter("transmgt_reversal_access");
        String transMgtPayoutAccess = req.getParameter("transmgt_payout_access");
        String invoiceGenerateAccess = req.getParameter("invoice_generate_access");
        String invoiceHistoryAccess = req.getParameter("invoice_history_access");
        String tokenMgtRegistrationHistoryAccess = req.getParameter("tokenmgt_registration_history_access");
        String tokenMgtRegisterCardAccess = req.getParameter("tokenmgt_register_card_access");
        String merchantMgtUserManagementAccess = req.getParameter("merchantmgt_user_management_access");
        String isPCILogo = req.getParameter("is_pci_logo");
        String isIpWhiteListInvoice = req.getParameter("ip_whitelist_invoice");
        String ipValidationRequired = req.getParameter("ip_validation_required");
        String isPartnerLogo = req.getParameter("is_partner_logo");
        /*String isRefundMailSent = req.getParameter("isRefundEmailSent");*/
        String binRouting = req.getParameter("binRouting");
        String vbvLogo = req.getParameter("vbvLogo");
        String masterSecureLogo = req.getParameter("masterSecureLogo");
        /*String chargebackMailSend = req.getParameter("chargebackEmail");*/
        String isSecurityLogo = req.getParameter("isSecurityLogo");
        String isMultipleRefund = req.getParameter("isMultipleRefund");
        String settingsWhitelistDetails = req.getParameter("settings_whitelist_details");
        String settingsBlacklistDetails = req.getParameter("settings_blacklist_details");
        //String emiConfiguration=req.getParameter("emi_configuration");
        String emiSupport = req.getParameter("emiSupport");
        String isPartialRefund = req.getParameter("isPartialRefund");
        String internalFraudCheck = req.getParameter("internalFraudCheck");
        String card_velocity_check = req.getParameter("card_velocity_check");
        String merchant_order_details = req.getParameter("merchant_order_details");
        String limitRouting = req.getParameter("limitRouting");
        String checkoutTimer = req.getParameter("checkoutTimer");
        String checkoutTimerTime = req.getParameter("checkoutTimerTime");
        String marketplace = req.getParameter("marketplace");
        String rejected_transaction = req.getParameter("rejected_transaction");
        //String isMerchantKey = req.getParameter("isMerchantKey");
        String virtual_checkout = req.getParameter("virtual_checkout");
        String isVirtualCheckoutAllowed = req.getParameter("isVirtualCheckoutAllowed");
        String isMobileAllowedForVC = req.getParameter("isMobileAllowedForVC");
        String isEmailAllowedForVC = req.getParameter("isEmailAllowedForVC");
        //String isCvvStore = req.getParameter("isCvvStore");
        String reconciliationNotification=req.getParameter("reconciliationNotification");
        String transactionNotification=req.getParameter("transactionNotification");
        String refundNotification=req.getParameter("refundNotification");
        String chargebackNotification=req.getParameter("chargebackNotification");
        String ispurchase_inquiry_blacklist = req.getParameter("ispurchase_inquiry_blacklist");
        String ispurchase_inquiry_refund = req.getParameter("ispurchase_inquiry_refund");
        String isfraud_determined_refund = req.getParameter("isfraud_determined_refund");
        String isfraud_determined_blacklist = req.getParameter("isfraud_determined_blacklist");
        String isdispute_initiated_refund = req.getParameter("isdispute_initiated_refund");
        String isdispute_initiated_blacklist = req.getParameter("isdispute_initiated_blacklist");
        String isstop_payment_blacklist = req.getParameter("isstop_payment_blacklist");
        String isexception_file_listing_blacklist = req.getParameter("isexception_file_listing_blacklist");

        //Merchant Notification Mail
        String merchantRegistrationMail= req.getParameter("merchantRegistrationMail");//done
        String merchantChangePassword=req.getParameter("merchantChangePassword");//done
        String merchantChangeProfile=req.getParameter("merchantChangeProfile");//done
        String transactionSuccessfulMail=req.getParameter("transactionSuccessfulMail");//done PARTNERS_MERCHANT_SALE_TRANSACTION
        String transactionFailMail=req.getParameter("transactionFailMail");
        String transactionCapture=req.getParameter("transactionCapture");//done
        String transactionPayoutSuccess=req.getParameter("transactionPayoutSuccess");//done PAYOUT_TRANSACTION
        String transactionPayoutFail=req.getParameter("transactionPayoutFail");
        String refundMail=req.getParameter("refundMail");//done
        String chargebackMail=req.getParameter("chargebackMail");//done
        String transactionInvoice=req.getParameter("transactionInvoice");//done GENERATING_INVOICE_BY_MERCHANT
        String cardRegistration=req.getParameter("cardRegistration");//done
        String payoutReport=req.getParameter("payoutReport");//done MERCHANT_PAYOUT_ALERT_MAIL
        String monitoringAlertMail=req.getParameter("monitoringAlertMail");//done
        String monitoringSuspensionMail=req.getParameter("monitoringSuspensionMail");//done MERCHANT_MONITORING_SUSPENSION_TO_MERCHANT_SALES
        String highRiskRefunds=req.getParameter("highRiskRefunds");//done
        String fraudFailedTxn=req.getParameter("fraudFailedTxn");//done PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION
        String dailyFraudReport=req.getParameter("dailyFraudReport");//done MEMBER_DAILY_STATUS_FRAUD_REPORT
        //Customer Notification Mail
        String customerTransactionSuccessfulMail=req.getParameter("customerTransactionSuccessfulMail");
        String customerTransactionFailMail=req.getParameter("customerTransactionFailMail");
        String customerTransactionPayoutSuccess=req.getParameter("customerTransactionPayoutSuccess");
        String customerTransactionPayoutFail=req.getParameter("customerTransactionPayoutFail");
        String customerRefundMail=req.getParameter("customerRefundMail");
        String customerTokenizationMail=req.getParameter("customerTokenizationMail");
        String isUniqueOrderIdRequired=req.getParameter("isUniqueOrderIdRequired");
        String emailTemplateLang=req.getParameter("emailTemplateLang");
        String Login=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String onchangedValues = req.getParameter("onchangedvalue");

        String successReconMail = req.getParameter("successReconMail");
        String refundReconMail = req.getParameter("refundReconMail");
        String chargebackReconMail = req.getParameter("chargebackReconMail");
        String payoutReconMail = req.getParameter("payoutReconMail");
        String isMerchantLogoBO = req.getParameter("isMerchantLogoBO");
        String cardExpiryDateCheck = req.getParameter("cardExpiryDateCheck");
        String payoutNotification= req.getParameter("payoutNotification");
        String inquiryNotification=req.getParameter("inquiryNotification");
        String vpaAddressLimitCheck = req.getParameter("vpaAddressLimitCheck");
        String vpaAddressDailyAmountLimit = req.getParameter("vpaAddressDailyAmountLimit");
        String vpaAddressDailyCount = req.getParameter("vpaAddressDailyCount");
        String vpaAddressAmountLimitCheck = req.getParameter("vpaAddressAmountLimitCheck");
        String payoutBankAccountNoLimitCheck = req.getParameter("payoutBankAccountNoLimitCheck");
        String bankAccountNoDailyCount = req.getParameter("bankAccountNoDailyCount");
        String payoutBankAccountNoAmountLimitCheck = req.getParameter("payoutBankAccountNoAmountLimitCheck");
        String bankAccountNoDailyAmountLimit = req.getParameter("bankAccountNoDailyAmountLimit");
        String isShareAllowed= req.getParameter("isShareAllowed");
        String isSignatureAllowed= req.getParameter("isSignatureAllowed");
        String isSaveReceiptAllowed= req.getParameter("isSaveReceiptAllowed");
        String defaultLanguage= req.getParameter("defaultLanguage");
        String isDomainWhitelisted= req.getParameter("isDomainWhitelisted");
        String customerIpLimitCheck= req.getParameter("customerIpLimitCheck");
        String customerIpDailyCount= req.getParameter("customerIpDailyCount");
        String customerIpAmountLimitCheck= req.getParameter("customerIpAmountLimitCheck");
        String customerIpDailyAmountLimit= req.getParameter("customerIpDailyAmountLimit");
        String customerNameLimitCheck= req.getParameter("customerNameLimitCheck");
        String customerNameDailyCount= req.getParameter("customerNameDailyCount");
        String customerNameAmountLimitCheck= req.getParameter("customerNameAmountLimitCheck");
        String customerNameDailyAmountLimit= req.getParameter("customerNameDailyAmountLimit");
        String customerEmailLimitCheck= req.getParameter("customerEmailLimitCheck");
        String customerEmailDailyCount= req.getParameter("customerEmailDailyCount");
        String customerEmailAmountLimitCheck= req.getParameter("customerEmailAmountLimitCheck");
        String customerEmailDailyAmountLimit= req.getParameter("customerEmailDailyAmountLimit");
        String customerPhoneLimitCheck= req.getParameter("customerPhoneLimitCheck");
        String customerPhoneDailyCount= req.getParameter("customerPhoneDailyCount");
        String customerPhoneAmountLimitCheck= req.getParameter("customerPhoneAmountLimitCheck");
        String customerPhoneDailyAmountLimit= req.getParameter("customerPhoneDailyAmountLimit");
        String paybylink= req.getParameter("paybylink");
        String isOTPRequired= req.getParameter("isOTPRequired");
        String isCardStorageRequired= req.getParameter("isCardStorageRequired");

        String vpaAddressMonthlyCount           = req.getParameter("vpaAddressMonthlyCount");
        String vpaAddressMonthlyAmountLimit     = req.getParameter("vpaAddressMonthlyAmountLimit");
        String customerEmailMonthlyCount        = req.getParameter("customerEmailMonthlyCount");
        String customerEmailMonthlyAmountLimit  = req.getParameter("customerEmailMonthlyAmountLimit");
        String customerPhoneMonthlyCount        = req.getParameter("customerPhoneMonthlyCount");
        String customerPhoneMonthlyAmountLimit  = req.getParameter("customerPhoneMonthlyAmountLimit");
        String bankAccountNoMonthlyCount        = req.getParameter("bankAccountNoMonthlyCount");
        String bankAccountNoMonthlyAmountLimit  = req.getParameter("bankAccountNoMonthlyAmountLimit");
        String customerIpMonthlyCount           = req.getParameter("customerIpMonthlyCount");
        String customerIpMonthlyAmountLimit     = req.getParameter("customerIpMonthlyAmountLimit");
        String customerNameMonthlyCount         = req.getParameter("customerNameMonthlyCount");
        String customerNameMonthlyAmountLimit   = req.getParameter("customerNameMonthlyAmountLimit");
        String merchant_verify_otp   = req.getParameter("merchant_verify_otp");
        String generateview   = req.getParameter("generateview");


        //TODO:Need to add validation support

        int updRecs = 0;
        Functions functions = new Functions();
        if (functions.isValueNull(partnerId))
        {
            Connection cn = null;
            PreparedStatement pstmt = null;
            if (!functions.isValueNull(cardCheckLimit))
            {
                cardCheckLimit = "0";
            }
            if (!functions.isValueNull(cardTransactionLimit))
            {
                cardTransactionLimit = "0";
            }
            if (!functions.isValueNull(checkLimit))
            {
                checkLimit = "0";
            }
            if (!functions.isValueNull(refundDailyLimit))
            {
                refundDailyLimit = "0";
            }

            //Merchant Notification Settings flag
            if(merchantRegistrationMail==null)
                merchantRegistrationMail="N";

            if(merchantChangePassword==null)
                merchantChangePassword="N";

            if(merchantChangeProfile==null)
                merchantChangeProfile="N";

            if(transactionSuccessfulMail==null)
                transactionSuccessfulMail="N";

            if(transactionFailMail==null)
                transactionFailMail="N";

            if(transactionCapture==null)
                transactionCapture="N";

            if(transactionPayoutSuccess==null)
                transactionPayoutSuccess="N";

            if(transactionPayoutFail==null)
                transactionPayoutFail="N";

            if(refundMail==null)
                refundMail="N";

            if(chargebackMail==null)
                chargebackMail="N";

            if(transactionInvoice==null)
                transactionInvoice="N";

            if(cardRegistration==null)
                cardRegistration="N";

            if(payoutReport==null)
                payoutReport="N";

            if(monitoringAlertMail==null)
                monitoringAlertMail="N";

            if(monitoringSuspensionMail==null)
                monitoringSuspensionMail="N";

            if(highRiskRefunds==null)
                highRiskRefunds="N";

            if(fraudFailedTxn==null)
                fraudFailedTxn="N";

            if(dailyFraudReport==null)
                dailyFraudReport="N";

            if(customerTransactionSuccessfulMail==null)
                customerTransactionSuccessfulMail="N";

            if(customerTransactionFailMail==null)
                customerTransactionFailMail="N";

            if(customerTransactionPayoutSuccess==null)
                customerTransactionPayoutSuccess="N";

            if(customerTransactionPayoutFail==null)
                customerTransactionPayoutFail="N";

            if(customerRefundMail==null)
                customerRefundMail="N";

            if(customerTokenizationMail==null)
                customerTokenizationMail="N";

            if(isUniqueOrderIdRequired==null)
                isUniqueOrderIdRequired="N";

            if(emailTemplateLang==null)
                emailTemplateLang="EN";

            if(successReconMail==null)
                successReconMail="N";

            if(refundReconMail==null)
                refundReconMail="N";

            if(chargebackReconMail==null)
                chargebackReconMail="N";

            if(payoutReconMail==null)
                payoutReconMail="N";

            if(cardExpiryDateCheck==null)
                cardExpiryDateCheck="N";
            if(isDomainWhitelisted==null)
                isDomainWhitelisted="N";

            if (!ESAPI.validator().isValidInput("daily_amount_limit", req.getParameter("daily_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Daily Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("monthly_amount_limit", req.getParameter("monthly_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Monthly Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("weekly_amount_limit", req.getParameter("weekly_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid weekly Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("daily_card_limit", req.getParameter("daily_card_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Daily Card Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("weekly_card_limit", req.getParameter("weekly_card_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Weekly card Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("monthly_card_limit", req.getParameter("monthly_card_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Monthly Card Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("daily_card_amount_limit", req.getParameter("daily_card_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Daily Card Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("weekly_card_amount_limit", req.getParameter("weekly_card_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid weekly card amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("monthly_card_amount_limit", req.getParameter("monthly_card_amount_limit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid monthly card amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("max_score_allowed", req.getParameter("max_score_allowed"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Max Score allowed" + EOL;
            }
            if (!ESAPI.validator().isValidInput("max_score_auto_reversal", req.getParameter("max_score_auto_reversal"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Max Score Reversal" + EOL;
            }
            if (!ESAPI.validator().isValidInput("token_valid_days", req.getParameter("token_valid_days"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Token Valid Days" + EOL;
            }
            if (!ESAPI.validator().isValidInput("vpaAddressDailyCount", req.getParameter("vpaAddressDailyCount"), "Numbers", 9, true))
            {
                errorMsg = errorMsg + "Invalid VPA Address Daily Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerIpDailyCount", req.getParameter("customerIpDailyCount"), "Numbers", 9, true))
            {
                errorMsg = errorMsg + "Invalid Customer Ip Daily Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerNameDailyCount", req.getParameter("customerNameDailyCount"), "Numbers", 9, true))
            {
                errorMsg = errorMsg + "Invalid Customer Name Daily Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerEmailDailyCount", req.getParameter("customerEmailDailyCount"), "Numbers", 9, true))
            {
                errorMsg = errorMsg + "Invalid Customer Email Daily Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerPhoneDailyCount", req.getParameter("customerPhoneDailyCount"), "Numbers", 9, true))
            {
                errorMsg = errorMsg + "Invalid Customer Phone Daily Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("vpaAddressDailyAmountLimit", req.getParameter("vpaAddressDailyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid VPA Address Daily Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerIpDailyAmountLimit", req.getParameter("customerIpDailyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Ip Daily Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerNameDailyAmountLimit", req.getParameter("customerNameDailyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Name Daily Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerEmailDailyAmountLimit", req.getParameter("customerEmailDailyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Email Daily Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerPhoneDailyAmountLimit", req.getParameter("customerPhoneDailyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Phone Daily Amount Limit" + EOL;
            }

            if (!ESAPI.validator().isValidInput("vpaAddressMonthlyCount", req.getParameter("vpaAddressMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid VPA Address Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("vpaAddressMonthlyAmountLimit", req.getParameter("vpaAddressMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid VPA Address Monthly Amount limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerEmailMonthlyCount", req.getParameter("customerEmailMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Email Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerEmailMonthlyAmountLimit", req.getParameter("customerEmailMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Email Monthly Amount limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerPhoneMonthlyCount", req.getParameter("customerPhoneMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Phone Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerPhoneMonthlyAmountLimit", req.getParameter("customerPhoneMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Phone Monthly Amount Limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("bankAccountNoMonthlyCount", req.getParameter("bankAccountNoMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Bank AccountNo Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("bankAccountNoMonthlyAmountLimit", req.getParameter("bankAccountNoMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Bank AccountNo Monthly Amount limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerIpMonthlyCount", req.getParameter("customerIpMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Ip Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerIpMonthlyAmountLimit", req.getParameter("customerIpMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Ip Monthly Amount limit" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerNameMonthlyCount", req.getParameter("customerNameMonthlyCount"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Name Monthly Count" + EOL;
            }
            if (!ESAPI.validator().isValidInput("customerNameMonthlyAmountLimit", req.getParameter("customerNameMonthlyAmountLimit"), "Numbers", 10, true))
            {
                errorMsg = errorMsg + "Invalid Customer Name Monthly Amount Limit" + EOL;
            }
            /*if (!ESAPI.validator().isValidInput("checkoutTimerTime ", req.getParameter("checkoutTimerTime"), "checkoutTimer", 5, false)||req.getParameter("checkoutTimerTime").equals("0:00")||req.getParameter("checkoutTimerTime").equals("00:00")||req.getParameter("checkoutTimerTime").equals("00:0"))
            {
                errorMsg = errorMsg + "Invalid Checkout Timer Time,Accepts only in [mm:ss]" + EOL;
            }*/

            if (errorMsg.length() > 0)
            {
                String redirectpage = "/partnerpreference.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("cbmessage", errorMsg);
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            try
            {
                cn = Database.getConnection();
                String query = "UPDATE partner_default_configuration SET is_validate_email=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,is_refund=?,refund_daily_limit=?,activation=?,merchant_interface_access=?,is_pharma=?,haspaid=?,isservice=?,hralertproof=?,auto_redirect=?,vbv=?,hrparameterised=?,check_limit=?,invoice_template=?,is_powered_by=?,template=?,iswhitelisted=?,mastercard_supported=?,is_pod_required=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,card_check_limit=?,card_transaction_limit=?,auto_select_terminal=?,max_score_allowed=?,max_score_auto_reversal=?,weekly_amount_limit=?,is_appmanager_activate=?,is_card_registration_allowed=?,is_recurring=?,is_restricted_ticket=?,is_tokenization_allowed=?,token_valid_days=?,is_address_details_required=?,is_card_encryption_enable=?,blacklist_transaction=?,flight_mode=?,online_fraud_check=?,is_split_payment=?,split_payment_type=?, is_excesscapture_allowed=?, refundallowed_days=?, chargeback_allowed_days=?, is_rest_whitelisted=?, merchant_sms_activation=?, customer_sms_activation=?, email_limit_enabled=?, is_ip_whitelisted=?, bin_service=?, exp_date_offset = ?, support_section = ?, card_whitelist_level = ?, multi_Currency_support = ?, dashboard_access=?, accounting_access=?, setting_access=?, transactions_access=?, invoicing_access=?, virtualterminal_access=?,merchantmgt_access=?,settings_merchant_config_access=?,settings_fraudrule_config_access=?,accounts_account_summary_access=?,accounts_charges_summary_access=?,accounts_transaction_summary_access=?,accounts_reports_summary_access=?,settings_merchant_profile_access=?,settings_organisation_profile_access=?,settings_checkout_page_access=?,settings_generate_key_access=?,settings_invoice_config_access=?,transmgt_transaction_access=?,transmgt_capture_access=?,transmgt_reversal_access=?,transmgt_payout_access=?,invoice_generate_access=?,invoice_history_access=?,tokenmgt_registration_history_access=?,tokenmgt_register_card_access=?,merchantmgt_user_management_access=?, is_pci_logo=?,ip_whitelist_invoice=?,ip_validation_required=?,is_partner_logo=?,binRouting=?,personal_info_display=?,personal_info_validation=?,hosted_payment_page=?,vbvLogo=?,masterSecureLogo=?,consent=?,isSecurityLogo=?,isMultipleRefund=?,settings_whitelist_details=?,settings_blacklist_details=?,emiSupport=?,isPartialRefund=?,internalFraudCheck=?,card_velocity_check=?,merchant_order_details=?,limitRouting=?,checkoutTimer=?,checkoutTimerTime=?,marketplace=?,rejected_transaction=?,virtual_checkout=?,isVirtualCheckoutAllowed=?,isMobileAllowedForVC=?,isEmailAllowedForVC=?,reconciliationNotification=?,transactionNotification=?,refundNotification=?,chargebackNotification=?,ispurchase_inquiry_blacklist=?,ispurchase_inquiry_refund=?,isfraud_determined_refund=?,isfraud_determined_blacklist=?,isdispute_initiated_refund=?,isdispute_initiated_blacklist=?,isstop_payment_blacklist=?,isexception_file_listing_blacklist=?,merchantRegistrationMail=?,merchantChangePassword=?,merchantChangeProfile=?,transactionSuccessfulMail=?,transactionFailMail=?,transactionCapture=?,transactionPayoutSuccess=?,transactionPayoutFail=?,refundMail=?,chargebackMail=?,transactionInvoice=?,cardRegistration=?,payoutReport=?,monitoringAlertMail=?,monitoringSuspensionMail=?,highRiskRefunds=?,fraudFailedTxn=?,dailyFraudReport=?,customerTransactionSuccessfulMail=?,customerTransactionFailMail=?,customerTransactionPayoutSuccess=?,customerTransactionPayoutFail=?,customerRefundMail=?,customerTokenizationMail=?,isUniqueOrderIdRequired=?,emailTemplateLang=?,successReconMail=?,refundReconMail=?,chargebackReconMail=?,payoutReconMail=?,isMerchantLogoBO=?,cardExpiryDateCheck=?,payoutNotification=?,supportNoNeeded=?,vpaAddressLimitCheck=?,vpaAddressDailyCount=?,vpaAddressAmountLimitCheck=?,vpaAddressDailyAmountLimit=?,payoutBankAccountNoLimitCheck=?,bankAccountNoDailyCount=?,payoutBankAccountNoAmountLimitCheck=?,bankAccountNoDailyAmountLimit=?,isShareAllowed=?,isSignatureAllowed=?,isSaveReceiptAllowed=?,defaultLanguage=?,isDomainWhitelisted=?,customerIpLimitCheck=?,customerIpDailyCount=?,customerIpAmountLimitCheck=?,customerIpDailyAmountLimit=?,customerNameLimitCheck=?,customerNameDailyCount=?,customerNameAmountLimitCheck=?,customerNameDailyAmountLimit=?,customerEmailLimitCheck=?,customerEmailDailyCount=?,customerEmailAmountLimitCheck=?,customerEmailDailyAmountLimit=?,customerPhoneLimitCheck=?,customerPhoneDailyCount=?,customerPhoneAmountLimitCheck=?,customerPhoneDailyAmountLimit=? ,inquiryNotification=?,paybylink=?,isOTPRequired=?,isCardStorageRequired=?,vpaAddressMonthlyCount=?,vpaAddressMonthlyAmountLimit=?,customerEmailMonthlyCount=?,customerEmailMonthlyAmountLimit=?,customerPhoneMonthlyCount=?,customerPhoneMonthlyAmountLimit=?,bankAccountNoMonthlyCount=?,bankAccountNoMonthlyAmountLimit=?,customerIpMonthlyCount=?,customerIpMonthlyAmountLimit=?,customerNameMonthlyCount=?,customerNameMonthlyAmountLimit=?,merchant_verify_otp=?,generateview=? WHERE partnerId=?";
                pstmt = cn.prepareStatement(query);
                pstmt.setString(1, isValidateEmail);
                /*pstmt.setString(2, customerReminderMail);*/
                pstmt.setString(2, dailyAmountLimit);
                pstmt.setString(3, monthlyAmountLimit);
                pstmt.setString(4, dailyCardLimit);
                pstmt.setString(5, weeklyCardLimit);
                pstmt.setString(6, monthlyCardLimit);
                pstmt.setString(7, isRefund);
                pstmt.setString(8, refundDailyLimit);
                pstmt.setString(9, active);
                pstmt.setString(10, merchantInterfaceAccess);
                pstmt.setString(11, isPharma);
                pstmt.setString(12, hasPaid);
                pstmt.setString(13, isService);
                pstmt.setString(14, hrAlertProof);
                pstmt.setString(15, autoRedirect);
                pstmt.setString(16, vbv);
                pstmt.setString(17, hrParameterised);
                pstmt.setString(18, checkLimit);
                pstmt.setString(19, invoiceTemplate);
                pstmt.setString(20, isPoweredBy);
                pstmt.setString(21, template);
                pstmt.setString(22, isWhiteListed);
                pstmt.setString(23, masterCardSupported);
                pstmt.setString(24, isPODRequired);
                pstmt.setString(25, dailyCardAmountLimit);
                pstmt.setString(26, weeklyCardAmountLimit);
                pstmt.setString(27, monthlyCardAmountLimit);
                pstmt.setString(28, String.valueOf(cardCheckLimit));
                pstmt.setString(29, String.valueOf(cardTransactionLimit));
                pstmt.setString(30, autoSelectTerminal);
                pstmt.setString(31, maxScoreAllowed);
                pstmt.setString(32, maxScoreReversal);
                pstmt.setString(33, weeklyAmountLimit);
                pstmt.setString(34, isAppManagerActivate);
                pstmt.setString(35, isCardRegistrationAllowed);
                pstmt.setString(36, isRecurring);
                pstmt.setString(37, isRestrictedTicket);
                pstmt.setString(38, isTokenizationAllowed);
                pstmt.setString(39, tokenValidDays);
                pstmt.setString(40, isAddressDetailsRequired);
                pstmt.setString(41, isCardEncryptionEnable);
                pstmt.setString(42, blacklistTransaction);
                pstmt.setString(43, flightMode);
                /*pstmt.setString(45, emailSent);*/
                pstmt.setString(44, fraudCheck);
                pstmt.setString(45, splitPayment);
                pstmt.setString(46, splitPaymentType);
                pstmt.setString(47, isExcessCaptureAllowed);
                pstmt.setString(48, refundAllowedDays);
                pstmt.setString(49, chargebackAllowedDays);
                pstmt.setString(50, isRestWhiteListed);
                pstmt.setString(51, merchantSmsActivation);
                pstmt.setString(52, customerSmsActivation);
                pstmt.setString(53, isEmailLimitEnabled);
                pstmt.setString(54, isIpWhiteListed);
                pstmt.setString(55, binService);
                pstmt.setString(56, expDateOffset);
                pstmt.setString(57, supportSection);
                pstmt.setString(58, cardWhiteListLevel);
                pstmt.setString(59, multiCurrencySupport);
                pstmt.setString(60, dashBoard);
                pstmt.setString(61, accounting);
                pstmt.setString(62, settings);
                pstmt.setString(63, transactionManagement);
                pstmt.setString(64, invoicing);
                pstmt.setString(65, virtualTerminal);
                pstmt.setString(66, merchantManagement);
                pstmt.setString(67, settingsMerchantConfigAccess);
                pstmt.setString(68, settingsFraudRuleConfigAccess);
                pstmt.setString(69, accountsAccountSummaryAccess);
                pstmt.setString(70, accountsChargesSummaryAccess);
                pstmt.setString(71, accountsTransactionSummaryAccess);
                pstmt.setString(72, accountsReportSummaryAccess);
                pstmt.setString(73, settingsMerchantProfileAccess);
                pstmt.setString(74, settingsOrganisationProfileAccess);
                pstmt.setString(75, "N");
                pstmt.setString(76, settingsGenerateKeyAccess);
                pstmt.setString(77, settingsInvoiceConfigAccess);
                pstmt.setString(78, transMgtTransactionAccess);
                pstmt.setString(79, transMgtCaptureAccess);
                pstmt.setString(80, transMgtReversalAccess);
                pstmt.setString(81, transMgtPayoutAccess);
                pstmt.setString(82, invoiceGenerateAccess);
                pstmt.setString(83, invoiceHistoryAccess);
                pstmt.setString(84, tokenMgtRegistrationHistoryAccess);
                pstmt.setString(85, tokenMgtRegisterCardAccess);
                pstmt.setString(86, merchantMgtUserManagementAccess);
                pstmt.setString(87, isPCILogo);
                pstmt.setString(88, isIpWhiteListInvoice);
                pstmt.setString(89, ipValidationRequired);
                pstmt.setString(90, isPartnerLogo);
                /*pstmt.setString(92, isRefundMailSent);*/
                pstmt.setString(91, binRouting);
                pstmt.setString(92, personalInfoDisplay);
                pstmt.setString(93, personalInfoValidation);
                pstmt.setString(94, hostedPaymentPage);
                pstmt.setString(95, vbvLogo);
                pstmt.setString(96, masterSecureLogo);
                pstmt.setString(97, consent);
                /*pstmt.setString(100, chargebackMailSend);*/
                pstmt.setString(98, isSecurityLogo);
                pstmt.setString(99, isMultipleRefund);
                pstmt.setString(100, settingsWhitelistDetails);
                pstmt.setString(101, settingsBlacklistDetails);
                //pstmt.setString(106,emiConfiguration);
                pstmt.setString(102, emiSupport);
                pstmt.setString(103, isPartialRefund);
                pstmt.setString(104, internalFraudCheck);
                pstmt.setString(105, card_velocity_check);
                pstmt.setString(106, merchant_order_details);
                pstmt.setString(107, limitRouting);
                pstmt.setString(108, checkoutTimer);
                pstmt.setString(109, checkoutTimerTime);
                pstmt.setString(110, marketplace);
                pstmt.setString(111, rejected_transaction);
                //pstmt.setString(115, isMerchantKey);
                pstmt.setString(112, virtual_checkout);
                pstmt.setString(113, isVirtualCheckoutAllowed);
                pstmt.setString(114, isMobileAllowedForVC);
                pstmt.setString(115, isEmailAllowedForVC);
                //pstmt.setString(120, isCvvStore);
                pstmt.setString(116, reconciliationNotification);
                pstmt.setString(117, transactionNotification);
                pstmt.setString(118, refundNotification);
                pstmt.setString(119, chargebackNotification);
                pstmt.setString(120, ispurchase_inquiry_blacklist);
                pstmt.setString(121, ispurchase_inquiry_refund);
                pstmt.setString(122, isfraud_determined_refund);
                pstmt.setString(123, isfraud_determined_blacklist);
                pstmt.setString(124, isdispute_initiated_refund);
                pstmt.setString(125, isdispute_initiated_blacklist);
                pstmt.setString(126, isstop_payment_blacklist);
                pstmt.setString(127, isexception_file_listing_blacklist);

                pstmt.setString(128, merchantRegistrationMail);
                pstmt.setString(129, merchantChangePassword);
                pstmt.setString(130, merchantChangeProfile);
                pstmt.setString(131, transactionSuccessfulMail);
                pstmt.setString(132, transactionFailMail);
                pstmt.setString(133, transactionCapture);
                pstmt.setString(134, transactionPayoutSuccess);
                pstmt.setString(135, transactionPayoutFail);
                pstmt.setString(136, refundMail);
                pstmt.setString(137, chargebackMail);
                pstmt.setString(138, transactionInvoice);
                pstmt.setString(139, cardRegistration);
                pstmt.setString(140, payoutReport);
                pstmt.setString(141, monitoringAlertMail);
                pstmt.setString(142, monitoringSuspensionMail);
                pstmt.setString(143, highRiskRefunds);
                pstmt.setString(144, fraudFailedTxn);
                pstmt.setString(145, dailyFraudReport);
                pstmt.setString(146, customerTransactionSuccessfulMail);
                pstmt.setString(147, customerTransactionFailMail);
                pstmt.setString(148, customerTransactionPayoutSuccess);
                pstmt.setString(149, customerTransactionPayoutFail);
                pstmt.setString(150, customerRefundMail);
                pstmt.setString(151, customerTokenizationMail);
                pstmt.setString(152, isUniqueOrderIdRequired);
                pstmt.setString(153, emailTemplateLang);
                pstmt.setString(154, successReconMail);
                pstmt.setString(155, refundReconMail);
                pstmt.setString(156, chargebackReconMail);
                pstmt.setString(157, payoutReconMail);
                pstmt.setString(158, isMerchantLogoBO);
                pstmt.setString(159, cardExpiryDateCheck);
                pstmt.setString(160,payoutNotification);
                pstmt.setString(161,supportNoNeeded);
                pstmt.setString(162,vpaAddressLimitCheck);
                pstmt.setString(163, vpaAddressDailyCount);
                pstmt.setString(164, vpaAddressAmountLimitCheck);
                pstmt.setString(165, vpaAddressDailyAmountLimit);
                pstmt.setString(166, payoutBankAccountNoLimitCheck);
                pstmt.setString(167, bankAccountNoDailyCount);
                pstmt.setString(168, payoutBankAccountNoAmountLimitCheck);
                pstmt.setString(169, bankAccountNoDailyAmountLimit);
                pstmt.setString(170,isShareAllowed);
                pstmt.setString(171,isSignatureAllowed);
                pstmt.setString(172,isSaveReceiptAllowed);
                pstmt.setString(173,defaultLanguage);
                pstmt.setString(174,isDomainWhitelisted);
                pstmt.setString(175,customerIpLimitCheck);
                pstmt.setString(176,customerIpDailyCount);
                pstmt.setString(177,customerIpAmountLimitCheck);
                pstmt.setString(178,customerIpDailyAmountLimit);
                pstmt.setString(179,customerNameLimitCheck);
                pstmt.setString(180,customerNameDailyCount);
                pstmt.setString(181,customerNameAmountLimitCheck);
                pstmt.setString(182,customerNameDailyAmountLimit);
                pstmt.setString(183,customerEmailLimitCheck);
                pstmt.setString(184,customerEmailDailyCount);
                pstmt.setString(185,customerEmailAmountLimitCheck);
                pstmt.setString(186,customerEmailDailyAmountLimit);
                pstmt.setString(187,customerPhoneLimitCheck);
                pstmt.setString(188,customerPhoneDailyCount);
                pstmt.setString(189, customerPhoneAmountLimitCheck);
                pstmt.setString(190,customerPhoneDailyAmountLimit);
                pstmt.setString(191,inquiryNotification);
                pstmt.setString(192,paybylink);
                pstmt.setString(193,isOTPRequired);
                pstmt.setString(194,isCardStorageRequired);
                pstmt.setString(195, vpaAddressMonthlyCount);
                pstmt.setString(196, vpaAddressMonthlyAmountLimit);
                pstmt.setString(197, customerEmailMonthlyCount);
                pstmt.setString(198, customerEmailMonthlyAmountLimit);
                pstmt.setString(199, customerPhoneMonthlyCount);
                pstmt.setString(200, customerPhoneMonthlyAmountLimit);
                pstmt.setString(201, bankAccountNoMonthlyCount);
                pstmt.setString(202, bankAccountNoMonthlyAmountLimit);
                pstmt.setString(203, customerIpMonthlyCount);
                pstmt.setString(204, customerIpMonthlyAmountLimit);
                pstmt.setString(205, customerNameMonthlyCount);
                pstmt.setString(206, customerNameMonthlyAmountLimit);
                pstmt.setString(207, merchant_verify_otp);
                pstmt.setString(208, generateview);
                pstmt.setString(209, partnerId);
                logger.debug("result " + pstmt.toString());
                logger.debug("Creating Activity for Partner Default Configuration");
                String remoteAddr = Functions.getIpAddress(req);
                int serverPort = req.getServerPort();
                String servletPath = req.getServletPath();
                String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                if(functions.isValueNull(onchangedValues))
                {
                    activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                    activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                    activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                    activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                    activityTrackerVOs.setModule_name(ActivityLogParameters.PARTNER_DEFAULT_CONFIGURATION.toString());
                    activityTrackerVOs.setLable_values(onchangedValues);
                    activityTrackerVOs.setDescription(ActivityLogParameters.PARTNERID.toString() + "-" + partnerId);
                    activityTrackerVOs.setIp(remoteAddr);
                    activityTrackerVOs.setHeader(header);
                    try
                    {
                        AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                        asyncActivityTracker.asyncActivity(activityTrackerVOs);
                    }
                    catch (Exception e)
                    {
                        logger.error("Exception while AsyncActivityLog::::", e);
                    }
                }
                int result = pstmt.executeUpdate();
                if (result > 0)
                {
                    updRecs++;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception:::::", e);
                req.setAttribute("error", errorMsg);
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(cn);

            }
        }
        sSuccessMessage.append(updRecs + " Records Updated");
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/partnerpreference.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
}
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.FraudTransactionDAO;
import com.manager.enums.ActivityLogParameters;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.Mail.AsynchronousMailService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SetReserves extends HttpServlet
{
    private static TransactionLogger transactionLogger      = new TransactionLogger(SetReserves.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.debug("Entering in SetReserves");
        HttpSession session = req.getSession();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions function  = new Functions();
        transactionLogger.debug("success");
        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        ActivityTrackerVOs activityTrackerVOs           = new ActivityTrackerVOs();

        String EOL      = "<br>";
        String errorMsg = "";

        if (!Admin.isLoggedIn(session))
        {   transactionLogger.debug("member is logout ");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        errorMsg         = validateParameters(req);
        if (!ESAPI.validator().isValidInput("notificationurl ", req.getParameter("notificationurl"), "SafeString", 100, true)){
            errorMsg    = errorMsg + "Invalid Notification Url." + EOL;
        }
        if (!ESAPI.validator().isValidInput("termsurl ",req.getParameter("termsurl"), "SafeString", 100, true)){
            errorMsg    = errorMsg + "Invalid Terms Url." + EOL;
        }
        if (!ESAPI.validator().isValidInput("privacyPolicyUrl ",req.getParameter("privacyPolicyUrl"), "SafeString", 100, true)){
            errorMsg    = errorMsg + "Invalid Privacy Policy Url." + EOL;
        }
        /*if (!ESAPI.validator().isValidInput("checkoutTimerTime ", req.getParameter("checkoutTimerTime"), "checkoutTimer", 5, false)||req.getParameter("checkoutTimerTime").equals("0:00")||req.getParameter("checkoutTimerTime").equals("00:00")||req.getParameter("checkoutTimerTime").equals("00:0"))
        {
                errorMsg = errorMsg + "Invalid Checkout Timer Time,Accepts only in [mm:ss]" + EOL;
        }*/

        if (function.isValueNull(req.getParameter("success_url")) && !ESAPI.validator().isValidInput("success_url",req.getParameter("success_url"),"URL",255,true))
        {
            errorMsg = errorMsg + "Invalid success_url"+ EOL;
        }

        if (function.isValueNull(req.getParameter("failed_url")) && !ESAPI.validator().isValidInput("failed_url",req.getParameter("failed_url"),"URL",255,true))
        {
            errorMsg = errorMsg + "Invalid failed_url"+ EOL;
        }

        if (function.isValueNull(req.getParameter("payout_success_url")) && !ESAPI.validator().isValidInput("payout_success_url",req.getParameter("payout_success_url"),"URL",255,true))
        {
            errorMsg = errorMsg + "Invalid payout_success_url" +EOL;
        }

        if (function.isValueNull(req.getParameter("payout_failed_url")) && !ESAPI.validator().isValidInput("payout_failed_url",req.getParameter("payout_failed_url"),"URL",255,true))
        {
            errorMsg = errorMsg + "Invalid payout_failed_url"+ EOL;
        }

        if (function.isValueNull(req.getParameter("totalPayoutAmount")) && !ESAPI.validator().isValidInput("totalPayoutAmount",req.getParameter("totalPayoutAmount"),"Numbers",14,true))
        {
            errorMsg = errorMsg + "Invalid Total Payout Amount.";
        }

        if(!function.isEmptyOrNull(errorMsg))
        {
            String redirectpage     = "/memberpreference.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd    = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }
        else
        {
            String memberids[]          = req.getParameterValues("memberid");
            String aptprompts[]         = req.getParameterValues("aptprompt");
            String accountIds[]         = req.getParameterValues("accountids");
            String isValidateEmail[]    = req.getParameterValues("isValidateEmail");
            /*String custremindermail[] = req.getParameterValues("custremindermail");*/
            String daily_amount_limit   = req.getParameter("daily_amount_limit");
            String monthly_amount_limit = req.getParameter("monthly_amount_limit");
            String daily_card_limit     = req.getParameter("daily_card_limit");
            String weekly_card_limit    = req.getParameter("weekly_card_limit");
            String monthly_card_limit   = req.getParameter("monthly_card_limit");
            String daily_payout_amount_limit    = req.getParameter("daily_payout_amount_limit");
            String weekly_payout_amount_limit   = req.getParameter("weekly_payout_amount_limit");
            String monthly_payout_amount_limit  = req.getParameter("monthly_payout_amount_limit");
            String payout_amount_limit_check    = req.getParameter("payout_amount_limit_check");
            String isrefund                     = req.getParameter("isrefund");
            String isMultipleRefund             = req.getParameter("isMultipleRefund");
            String personalInfoDisplay          = req.getParameter("personal_info_display");
            String personalInfoValidation       = req.getParameter("personal_info_validation");
            String hostedPaymentPage            = req.getParameter("hosted_payment_page");
            String consent                      = req.getParameter("consent");
            String refunddailylimit             = req.getParameter("refunddailylimit");
            String active                       = req.getParameter("activation");
            String icici                        = req.getParameter("icici");
            StringBuilder query                 = new StringBuilder();
            String ispharma                     = req.getParameter("isPharma");
            StringBuilder sSuccessMessage       = new StringBuilder();
            StringBuilder sErrorMessage         = new StringBuilder();
            String haspaid                      = req.getParameter("haspaid");
            String isservice                    = req.getParameter("isservice");
            String hralertproof                 = req.getParameter("hralertproof");
            String autoredirect                 = req.getParameter("autoredirect");
            String vbv                          = req.getParameter("vbv");
            String hrparameterised              = req.getParameter("hrparameterised");
            String partnerId                    = req.getParameter("partnerId");
            String agentid                      = req.getParameter("agentId");
            String check_limit                  = req.getParameter("check_limit");
            String invoicetemplate              = req.getParameter("invoicetemplate");
            String isPoweredBy                  = req.getParameter("isPoweredBy");
            String template                     = req.getParameter("template");
            String iswhitelisted                = req.getParameter("iswhitelisted");
            String masterCardSupported          = req.getParameter("masterCardSupported");
            String isipwhitelist                = req.getParameter("isipwhitelisted");
            String isPODRequired                = req.getParameter("isPODRequired");
            String daily_card_amount_limit      = req.getParameter("daily_card_amount_limit");
            String weekly_card_amount_limit     = req.getParameter("weekly_card_amount_limit");
            String monthly_card_amount_limit    = req.getParameter("monthly_card_amount_limit");
            String card_check_limit             = req.getParameter("card_check_limit");
            String card_transaction_limit       = req.getParameter("card_transaction_limit");
            String autoSelectTerminal           = req.getParameter("autoSelectTerminal");
            String maxScoreAllowed              = req.getParameter("maxscoreallowed");
            String maxScoreReversal             = req.getParameter("maxscoreautoreversal");
            String weekly_amount_limit          = req.getParameter("weekly_amount_limit");
            String isappmanageractivate         = req.getParameter("isappmanageractivate");
            String iscardregistrationallowed    = req.getParameter("iscardregistrationallowed");
            String isRecurring                  = req.getParameter("is_recurring");
            String isRestrictedTicket           = req.getParameter("isRestrictedTicket");
            String isTokenizationAllowed        = req.getParameter("isTokenizationAllowed");
            String tokenValidDays               = req.getParameter("tokenvaliddays");
            String isAddrDetailsRequired        = req.getParameter("isAddrDetailsRequired");
            String isCardEncryptionEnable       = req.getParameter("isCardEncryptionEnable");
            String blacklistTransaction         = req.getParameter("blacklistTransaction");
            String flightMode                   = req.getParameter("flightMode");
            /*String emailSent= req.getParameter("emailSent");*/
            String fraudCheck                   = req.getParameter("onlineFraudCheck");
            String splitPayment                 = req.getParameter("isSplitPayment");
            String splitPaymentType             = req.getParameter("splitPaymentType");
            String isExcessCaptureAllowed       = req.getParameter("isExcessCaptureAllowed");
            String refundAllowedDays            = req.getParameter("refundallowed_days");
            String chargebackAllowedDays        = req.getParameter("chargebackallowed_days");
            String is_rest_whitelisted          = req.getParameter("is_rest_whitelisted");
            String smsActivation                = req.getParameter("smsactivation");
            String cSmsActivation               = req.getParameter("customersmsactivation");
            String isEmailLilmitenabled         = req.getParameter("emailLimitEnabled");
            String ip_whitelist_invoice         = req.getParameter("ip_whitelist_invoice");
            String binService                   = req.getParameter("binService");
            String expDateOffset                = req.getParameter("expDateOffset");
            String supportSection               = req.getParameter("supportSection");
            String supportNoNeeded               = req.getParameter("supportNoNeeded");
            String card_whitelist_level         = req.getParameter("card_whitelist_level");
            String multiCurrencySupport         = req.getParameter("multiCurrencySupport");
            String dashBoard                    = req.getParameter("dashboard_access");
            String accounting                   = req.getParameter("accounting_access");
            String settings                     = req.getParameter("setting_access");
            String transactionManagement        = req.getParameter("transactions_access");
            String invoicing                    = req.getParameter("invoicing_access");
            String virtualTerminal              = req.getParameter("virtualterminal_access");
            String merchantManagement           = req.getParameter("merchantmgt_access");
            String settingsMerchantConfigAccess         = req.getParameter("settings_merchant_config_access");
            String settingsFraudRuleConfigAccess        = req.getParameter("settings_fraudrule_config_access");
            String accountsAccountSummaryAccess         = req.getParameter("accounts_account_summary_access");
            String accountsChargesSummaryAccess         = req.getParameter("accounts_charges_summary_access");
            String accountsTransactionSummaryAccess     = req.getParameter("accounts_transaction_summary_access");
            String accountsReportSummaryAccess          = req.getParameter("accounts_reports_summary_access");
            String settingsMerchantProfileAccess        = req.getParameter("settings_merchant_profile_access");
            String settingsOrganisationProfileAccess    = req.getParameter("settings_organisation_profile_access");
            String settingsGenerateKeyAccess            = req.getParameter("settings_generate_key_access");
            String settingsInvoiceConfigAccess          = req.getParameter("settings_invoice_config_access");
            String transMgtTransactionAccess            = req.getParameter("transmgt_transaction_access");
            String transMgtCaptureAccess                = req.getParameter("transmgt_capture_access");
            String transMgtReversalAccess               = req.getParameter("transmgt_reversal_access");
            String transMgtPayoutAccess                 = req.getParameter("transmgt_payout_access");
            String transmgt_payout_transactions         = req.getParameter("transmgt_payout_transactions");
            String invoiceGenerateAccess                = req.getParameter("invoice_generate_access");
            String invoiceHistoryAccess                 = req.getParameter("invoice_history_access");
            String tokenMgtRegistrationHistoryAccess    = req.getParameter("tokenmgt_registration_history_access");
            String tokenMgtRegisterCardAccess           = req.getParameter("tokenmgt_register_card_access");
            String merchantMgtUserManagementAccess      = req.getParameter("merchantmgt_user_management_access");
            String ispcilogo                            = req.getParameter("ispcilogo");
            String notificationUrl                      = req.getParameter("notificationurl");
            String termsUrl                             = req.getParameter("termsurl");
            String ipValidationRequired                 = req.getParameter("ip_validation_required");
            String isPartnerLogo                        = req.getParameter("ispartnerlogo");
            String ismerchantlogo                       = req.getParameter("ismerchantlogo");
            /*String isRefundEmailSent=req.getParameter("isRefundEmailSent");*/
            String isPartialRefund                      = req.getParameter("isPartialRefund");
            String privacyPolicyUrl                     = req.getParameter("privacyPolicyUrl");
            String binRouting                           = req.getParameter("binRouting");
            String vbvLogo                              = req.getParameter("vbvLogo");
            String masterSecureLogo                     = req.getParameter("masterSecureLogo");
            /*String chargebackMailSend=req.getParameter("chargebackEmail");*/
            String isSecurityLogo                       = req.getParameter("isSecurityLogo");
            String settingsWhitelistDetails             = req.getParameter("settings_whitelist_details");
            String settingsBlacklistDetails             = req.getParameter("settings_blacklist_details");
            String emiConfiguration                     = "N";
            String emiSupport                           = req.getParameter("emiSupport");
            String internalFraudCheck                   = req.getParameter("internalFraudCheck");
            String card_velocity_check                  = req.getParameter("card_velocity_check");
            String merchant_order_details               = req.getParameter("merchant_order_details");
            String limitRouting                         = req.getParameter("limitRouting");
            String checkoutTimer                        = req.getParameter("checkoutTimer");
            String checkoutTimerTime                    = req.getParameter("checkoutTimerTime");
            String marketplace                          = req.getParameter("marketplace");
            String rejected_transaction                 = req.getParameter("rejected_transaction");
            String virtual_checkout                     = req.getParameter("virtual_checkout");
            String isVirtualCheckoutAllowed             = req.getParameter("isVirtualCheckoutAllowed");
            String isMobileAllowedForVC                 = req.getParameter("isMobileAllowedForVC");
            String isEmailAllowedForVC                  = req.getParameter("isEmailAllowedForVC");
            String isCvvStore                           = req.getParameter("isCvvStore");
            String reconciliationNotification           = req.getParameter("reconciliationNotification");
            String transactionNotification              = req.getParameter("transactionNotification");
            String refundNotification                   = req.getParameter("refundNotification");
            String chargebackNotification               = req.getParameter("chargebackNotification");
            String ispurchase_inquiry_blacklist         = req.getParameter("ispurchase_inquiry_blacklist");
            String ispurchase_inquiry_refund            = req.getParameter("ispurchase_inquiry_refund");
            String isfraud_determined_refund            = req.getParameter("isfraud_determined_refund");
            String isfraud_determined_blacklist         = req.getParameter("isfraud_determined_blacklist");
            String isdispute_initiated_refund           = req.getParameter("isdispute_initiated_refund");
            String isdispute_initiated_blacklist        = req.getParameter("isdispute_initiated_blacklist");
            String isstop_payment_blacklist             = req.getParameter("isstop_payment_blacklist");
            String isexception_file_listing_blacklist   = req.getParameter("isexception_file_listing_blacklist");
            String paybylink                            =req.getParameter("paybylink");

            //Merchant Notification Mail
            String merchantRegistrationMail         = req.getParameter("merchantRegistrationMail");
            String merchantChangePassword           = req.getParameter("merchantChangePassword");
            String merchantChangeProfile            = req.getParameter("merchantChangeProfile");
            String transactionSuccessfulMail        = req.getParameter("transactionSuccessfulMail");
            String transactionFailMail              = req.getParameter("transactionFailMail");
            String transactionCapture               = req.getParameter("transactionCapture");
            String transactionPayoutSuccess         = req.getParameter("transactionPayoutSuccess");
            String transactionPayoutFail            = req.getParameter("transactionPayoutFail");
            String refundMail                       = req.getParameter("refundMail");
            String chargebackMail                   = req.getParameter("chargebackMail");
            String transactionInvoice               = req.getParameter("transactionInvoice");
            String cardRegistration                 = req.getParameter("cardRegistration");
            String payoutReport                     = req.getParameter("payoutReport");
            String monitoringAlertMail              = req.getParameter("monitoringAlertMail");
            String monitoringSuspensionMail         = req.getParameter("monitoringSuspensionMail");
            String highRiskRefunds                  = req.getParameter("highRiskRefunds");
            String fraudFailedTxn                   = req.getParameter("fraudFailedTxn");
            String dailyFraudReport                 = req.getParameter("dailyFraudReport");
            //Customer Notification Mail
            String customerTransactionSuccessfulMail    = req.getParameter("customerTransactionSuccessfulMail");
            String customerTransactionFailMail          = req.getParameter("customerTransactionFailMail");
            String customerTransactionPayoutSuccess     = req.getParameter("customerTransactionPayoutSuccess");
            String customerTransactionPayoutFail        = req.getParameter("customerTransactionPayoutFail");
            String customerRefundMail                   = req.getParameter("customerRefundMail");
            String customerTokenizationMail             = req.getParameter("customerTokenizationMail");
            String isUniqueOrderIdRequired              = req.getParameter("isUniqueOrderIdRequired");
            String Login                                = (String)session.getAttribute("username");
            String actionExecutorId                     = (String)session.getAttribute("merchantid");
            String onchangedValues                      = req.getParameter("onchangedvalue");
            String emailTemplateLang                    = req.getParameter("emailTemplateLang");

            String successReconMail     = req.getParameter("successReconMail");
            String refundReconMail      = req.getParameter("refundReconMail");
            String chargebackReconMail  = req.getParameter("chargebackReconMail");
            String payoutReconMail      = req.getParameter("payoutReconMail");
            String isMerchantLogoBO     = req.getParameter("isMerchantLogoBO");
            String cardExpiryDateCheck  = req.getParameter("cardExpiryDateCheck");
            String payoutLimitRouting   = req.getParameter("payout_routing");
            String payoutNotification = req.getParameter("payoutNotification");
            String inquiryNotification = req.getParameter("inquiryNotification");
            transactionLogger.debug("inquir from setreserve "+inquiryNotification);
            String vpaAddressLimitCheck         = req.getParameter("vpaAddressLimitCheck");
            String vpaAddressDailyAmountLimit   = req.getParameter("vpaAddressDailyAmountLimit");
            String vpaAddressDailyCount         = req.getParameter("vpaAddressDailyCount");
            String vpaAddressAmountLimitCheck   = req.getParameter("vpaAddressAmountLimitCheck");

            String payoutBankAccountNoLimitCheck        = req.getParameter("payoutBankAccountNoLimitCheck");
            String payoutBankAccountNoAmountLimitCheck  = req.getParameter("payoutBankAccountNoAmountLimitCheck");
            String bankAccountNoDailyCount              = req.getParameter("bankAccountNoDailyCount");
            String bankAccountNoDailyAmountLimit        = req.getParameter("bankAccountNoDailyAmountLimit");
            String isShareAllowed                       = req.getParameter("isShareAllowed");
            String isSignatureAllowed                   = req.getParameter("isSignatureAllowed");
            String isSaveReceiptAllowed                 = req.getParameter("isSaveReceiptAllowed");
            String defaultLanguage          = req.getParameter("defaultLanguage");
            String isDomainWhitelisted      = req.getParameter("isDomainWhitelisted");
            String customerIpLimitCheck     = req.getParameter("customerIpLimitCheck");
            String customerIpDailyCount     = req.getParameter("customerIpDailyCount");
            String customerIpAmountLimitCheck   = req.getParameter("customerIpAmountLimitCheck");
            String customerIpDailyAmountLimit   = req.getParameter("customerIpDailyAmountLimit");
            String customerNameLimitCheck       = req.getParameter("customerNameLimitCheck");
            String customerNameDailyCount       = req.getParameter("customerNameDailyCount");
            String customerNameAmountLimitCheck = req.getParameter("customerNameAmountLimitCheck");
            String customerNameDailyAmountLimit = req.getParameter("customerNameDailyAmountLimit");
            String customerEmailLimitCheck      = req.getParameter("customerEmailLimitCheck");
            String customerEmailDailyCount      = req.getParameter("customerEmailDailyCount");
            String customerEmailAmountLimitCheck    = req.getParameter("customerEmailAmountLimitCheck");
            String customerEmailDailyAmountLimit    = req.getParameter("customerEmailDailyAmountLimit");
            String customerPhoneLimitCheck          = req.getParameter("customerPhoneLimitCheck");
            String customerPhoneDailyCount          = req.getParameter("customerPhoneDailyCount");
            String customerPhoneAmountLimitCheck    = req.getParameter("customerPhoneAmountLimitCheck");
            String customerPhoneDailyAmountLimit    = req.getParameter("customerPhoneDailyAmountLimit");
            String isOTPRequired                    = req.getParameter("isOTPRequired");
            String isCardStorageRequired            = req.getParameter("isCardStorageRequired");
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
            String isIgnorePaymode                  = req.getParameter("isIgnorePaymode");
            String checkoutConfig                  = req.getParameter("checkout_config");
            String daily_card_limit_check          = req.getParameter("daily_card_limit_check");
            String monthly_card_limit_check       = req.getParameter("monthly_card_limit_check");

            String weekly_card_limit_check        =   req.getParameter("weekly_card_limit_check");
            String success_url                    = req.getParameter("success_url");
            String failed_url                     = req.getParameter("failed_url");
            String payout_success_url             = req.getParameter("payout_success_url");
            String payout_failed_url              = req.getParameter("payout_failed_url");
            String success_url_check              = req.getParameter("success_url_check");
            String failed_url_check               = req.getParameter("failed_url_check");
            String payout_success_url_check       = req.getParameter("payout_success_url_check");
            String payout_failed_url_check        = req.getParameter("payout_failed_url_check");
            String totalPayoutAmount              = req.getParameter("totalPayoutAmount");
            String payoutPersonalInfoValidation   = req.getParameter("payoutPersonalInfoValidation");
            String upi_support_invoice            =req.getParameter("upi_support_invoice");
            String upi_qr_support_invoice            =req.getParameter("upi_qr_support_invoice");
            String paybylink_support_invoice            =req.getParameter("paybylink_support_invoice");
            String AEPS_support_invoice            =req.getParameter("AEPS_support_invoice");
            String merchant_verify_otp            =req.getParameter("merchant_verify_otp");
            String generateview            =req.getParameter("generateview");


            res.setContentType("text/html");
            PrintWriter out             = res.getWriter();
            int updRecs                 = 0;
            Connection cn               =null;
            PreparedStatement pstmt     = null;
            PreparedStatement pstmt1    = null;
            PreparedStatement pstmt2    = null;
            ResultSet rs                = null;
            if (memberids != null)
            {
                try
                {
                    for (int i = 0; i < 1; i++)
                    {
                        GatewayAccount account      = GatewayAccountService.getGatewayAccount(accountIds[i]);
                        String reserve              = "", aptprompt = "", fixamount = "", accountID = "";
                        String chargeper            = "",reversalCharge="",withdrawalCharge="",chargebackCharge="",taxPercentage="";
                        String isValidEmail         = "N";
                        String isCustEmail          = "N";
                        /*String custrememail="N";*/
                        aptprompt                   = getValidPercentage(aptprompts[i]);
                        isValidEmail                = isValidateEmail[i];
                        /*custrememail = custremindermail[i];*/
                        accountID                   =  Functions.checkStringNull (accountIds[i]);
                        if(card_check_limit == null)
                        {
                            card_check_limit    = "0";
                        }
                        if(card_transaction_limit == null)
                        {
                            card_transaction_limit  = "0";
                        }
                        if(isrefund == null)
                        {
                            isrefund    = "N";
                        }
                        if(isMultipleRefund == null)
                        {
                            isMultipleRefund = "N";
                        }
                        if(personalInfoDisplay  == null)
                        {
                            personalInfoDisplay  = "Y";
                        }
                        if(personalInfoValidation == null)
                        {
                            personalInfoValidation="Y";
                        }
                        if(hostedPaymentPage == null)
                        {
                            hostedPaymentPage = "N";
                        }
                        if(consent == null)
                        {
                            consent = "Y";
                        }
                        if(refunddailylimit == null)
                        {
                            refunddailylimit    = "0";
                        }
                        if(isValidEmail == null)
                        {
                            isValidEmail    = "N";
                        }
                        /*if(custrememail==null)
                        {
                            custrememail="N";
                        }*/
                        if(active == null)
                        {
                            active = "T";
                        }
                        if(haspaid==null)
                        {
                            haspaid="N";
                        }
                        if(isservice == null)
                        {
                            isservice ="N";
                        }
                        if(icici == null)
                        {
                            icici = "N";
                        }
                        if(hralertproof == null)
                        {
                            hralertproof    = "N";
                        }
                        if(autoredirect == null)
                        {
                            autoredirect    = "N";
                        }
                        if(isPODRequired == null)
                        {
                            isPODRequired   = "N";
                        }
                        if(vbv == null)
                        {
                            vbv = "N";
                        }
                        if(hrparameterised == null)
                        {
                            hrparameterised = "N";
                        }
                        if(invoicetemplate == null)
                        {
                            invoicetemplate = "Y";
                        }
                        if(check_limit  == null)
                        {
                            check_limit = "0";
                        }
                        if(masterCardSupported == null)
                        {
                            masterCardSupported = "N";
                        }
                        if(iswhitelisted == null)
                        {
                            iswhitelisted   = "N";
                        }
                        if (smsActivation == null)
                        {
                            smsActivation = "N";
                        }
                        if (cSmsActivation == null)
                        {
                            cSmsActivation = "N";
                        }
                        if (isEmailLilmitenabled == null)
                        {
                            isEmailLilmitenabled = "N";
                        }
                        if (ip_whitelist_invoice == null)
                        {
                            ip_whitelist_invoice = "Y";
                        }
                        if(!function.isValueNull(notificationUrl)){
                            notificationUrl ="";
                        }
                        if(!function.isValueNull(termsUrl)){
                            termsUrl    = "";
                        }
                        if(!function.isValueNull(privacyPolicyUrl)){
                            privacyPolicyUrl    = "";
                        }
                        /*if (isRefundEmailSent == null)
                        {
                            isRefundEmailSent = "Y";
                        }*/
                        if(isPartialRefund == null)
                        {
                            isPartialRefund = "N";
                        }
                        if (isSecurityLogo == null)
                        {
                            isSecurityLogo = "N";
                        }
                        if (card_velocity_check == null)
                        {
                            card_velocity_check = "N";
                        }
                        if (monthly_card_limit_check == null)
                        {
                            monthly_card_limit_check = "N";
                        }
                        if (daily_card_limit_check == null)
                        {
                            daily_card_limit_check = "N";
                        }
                        if (limitRouting == null)
                        {
                            limitRouting = "N";
                        }
                        if(merchant_order_details ==null)
                        {
                            merchant_order_details = "N";
                        }

                        /*if (chargebackMailSend == null)
                        {
                            chargebackMailSend = "Y";
                        }*/

                        if (checkoutTimer == null)
                        {
                            checkoutTimer="Y";
                        }
                        if (checkoutTimerTime == null)
                        {
                            checkoutTimerTime="10.00";
                        }
                        if (marketplace == null)
                        {
                            marketplace = "N";
                        }
                        if (rejected_transaction == null)
                        {
                            rejected_transaction = "N";
                        }
                        if (paybylink == null)
                        {
                            paybylink = "N";
                        }
                        if (virtual_checkout == null)
                        {
                            virtual_checkout = "N";
                        }
                        if (isVirtualCheckoutAllowed == null)
                        {
                            isVirtualCheckoutAllowed = "N";
                        }
                        if (isMobileAllowedForVC == null)
                        {
                            isMobileAllowedForVC = "N";
                        }
                        if (isEmailAllowedForVC == null)
                        {
                            isEmailAllowedForVC = "N";
                        }
                        if (isCvvStore == null)
                        {
                            isCvvStore = "N";
                        }if (ispurchase_inquiry_blacklist == null)
                    {
                        ispurchase_inquiry_blacklist = "N";
                    }
                        if (ispurchase_inquiry_refund == null)
                        {
                            ispurchase_inquiry_refund = "N";
                        }
                        if (isfraud_determined_refund == null)
                        {
                            isfraud_determined_refund = "N";
                        }
                        if (isfraud_determined_blacklist == null)
                        {
                            isfraud_determined_blacklist = "N";
                        }
                        if (isdispute_initiated_refund == null)
                        {
                            isdispute_initiated_refund = "N";
                        }
                        if (isdispute_initiated_blacklist == null)
                        {
                            isdispute_initiated_blacklist = "N";
                        }
                        if (isstop_payment_blacklist == null)
                        {
                            isstop_payment_blacklist = "N";
                        }
                        if (isexception_file_listing_blacklist == null)
                        {
                            isexception_file_listing_blacklist = "N";
                        }
                        if(accountID != null)
                        {
                            if (aptprompt== null)
                                aptprompt = String.valueOf(account.getHighRiskAmount());
                            if (daily_card_amount_limit== null) { daily_card_amount_limit = "1000.00";}
                            if (weekly_card_amount_limit== null) { weekly_card_amount_limit = "5000.00";}
                            if (monthly_card_amount_limit== null) { monthly_card_amount_limit = "10000.00";}

                        }

                        if (daily_payout_amount_limit== null) { daily_payout_amount_limit = "1000.00";}
                        if (weekly_payout_amount_limit== null) { weekly_payout_amount_limit = "5000.00";}
                        if (monthly_payout_amount_limit== null) { monthly_payout_amount_limit = "10000.00";}

                        if(aptprompt==null)
                        {
                            aptprompt="0";
                        }

                        if(emailTemplateLang==null)
                        {
                            emailTemplateLang = "EN";
                        }

                        if(successReconMail==null)
                            successReconMail="N";

                        if(refundReconMail==null)
                            refundReconMail="N";

                        if(chargebackReconMail==null)
                            chargebackReconMail="N";

                        if(payoutReconMail==null)
                            payoutReconMail="N";

                        //Merchant Notification Settings flag

                        if(payout_amount_limit_check==null)
                            payout_amount_limit_check="N";

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

                        if(cardExpiryDateCheck==null)
                            cardExpiryDateCheck="N";

                        if(isDomainWhitelisted==null)
                            isDomainWhitelisted="N";

                        if(isIgnorePaymode==null){
                            isIgnorePaymode = "N";
                        }
                        if(checkoutConfig==null){
                            checkoutConfig = "N";
                        }


                        String activation = function.getActivation(memberids[i]);
                        cn= Database.getConnection();
                        query.append("update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid  set aptprompt= ?,accountid=?,isPharma=?,isValidateEmail=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,activation=?,icici=?,haspaid=?,isservice=?,hralertproof=?,autoredirect=?,vbv=?,hrparameterised=?,partnerId=?,check_limit=?,masterCardSupported=?,invoicetemplate=?,isPoweredBy=?,template=?,iswhitelisted=?,card_transaction_limit=?,card_check_limit=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,isrefund=?,refunddailylimit=? ,agentId=?, isIpWhitelisted=?,autoSelectTerminal=?,isPODRequired=?,maxScoreAllowed=?,maxScoreAutoReversal=?,weekly_amount_limit=?,isappmanageractivate=?,iscardregistrationallowed=?,is_recurring=?,isRestrictedTicket=?,isTokenizationAllowed=?,tokenvaliddays=?,isAddrDetailsRequired=?,blacklistTransaction=?,flightMode=?,onlineFraudCheck=?,isSplitPayment=?,splitPaymentType=?,isExcessCaptureAllowed=?,refundallowed_days=?,chargebackallowed_days=?,isCardEncryptionEnable=?,is_rest_whitelisted=?,smsactivation=?,customersmsactivation=?,emailLimitEnabled=?,ip_whitelist_invoice=?, binService=?, expDateOffset = ?,supportSection = ?,card_whitelist_level = ?,multiCurrencySupport = ?,dashboard_access=?,accounting_access=?,setting_access=?,transactions_access=?,invoicing_access=?,virtualterminal_access=?,merchantmgt_access=?,settings_merchant_config_access=?,settings_fraudrule_config_access=?, accounts_account_summary_access=?,accounts_charges_summary_access=?,accounts_transaction_summary_access=?,accounts_reports_summary_access=?,settings_merchant_profile_access=?,settings_organisation_profile_access=?, settings_checkout_page_access=?,settings_generate_key_access=?,settings_invoice_config_access=?,transmgt_transaction_access=?,transmgt_capture_access=?,transmgt_reversal_access=?,transmgt_payout_access=?,invoice_generate_access=?,invoice_history_access=?,tokenmgt_registration_history_access=?,tokenmgt_register_card_access=?,merchantmgt_user_management_access=?,ispcilogo=?,notificationUrl=?,termsUrl=?,ip_validation_required=?,ispartnerlogo=?,ismerchantlogo=?,privacyPolicyUrl=?,binRouting=?,personal_info_display=?,personal_info_validation=?,hosted_payment_page=?,vbvLogo=?,masterSecureLogo=?,consent=?,isMultipleRefund=?,isSecurityLogo=?,settings_whitelist_details=?,settings_blacklist_details=?,emi_configuration=?,emiSupport=?,isPartialRefund=?,internalFraudCheck=?,card_velocity_check=?,merchant_order_details=?,limitRouting=?,checkoutTimer=?,checkoutTimerTime=?,marketplace=?,rejected_transaction=?,virtual_checkout=?,isVirtualCheckoutAllowed=?,isMobileAllowedForVC=?,isEmailAllowedForVC=?,isCvvStore=?,reconciliationNotification=?,transactionNotification=?,refundNotification=?,chargebackNotification=?,ispurchase_inquiry_blacklist=?,ispurchase_inquiry_refund=?,isfraud_determined_refund=?,isfraud_determined_blacklist=?,isdispute_initiated_refund=?,isdispute_initiated_blacklist=?,isstop_payment_blacklist=?,isexception_file_listing_blacklist=?,merchantRegistrationMail=?,merchantChangePassword=?,merchantChangeProfile=?,transactionSuccessfulMail=?,transactionFailMail=?,transactionCapture=?,transactionPayoutSuccess=?,transactionPayoutFail=?,refundMail=?,chargebackMail=?,transactionInvoice=?,cardRegistration=?,payoutReport=?,monitoringAlertMail=?,monitoringSuspensionMail=?,highRiskRefunds=?,fraudFailedTxn=?,dailyFraudReport=?,customerTransactionSuccessfulMail=?,customerTransactionFailMail=?,customerTransactionPayoutSuccess=?,customerTransactionPayoutFail=?,customerRefundMail=?,customerTokenizationMail=?,isUniqueOrderIdRequired=?,daily_payout_amount_limit=?,weekly_payout_amount_limit=?,monthly_payout_amount_limit=?,payout_amount_limit_check=?,emailTemplateLang=?,successReconMail=?,refundReconMail=?,chargebackReconMail=?,payoutReconMail=?,isMerchantLogoBO=?,cardExpiryDateCheck=?,payoutRouting=?,payoutNotification=?,supportNoNeeded=?,vpaAddressLimitCheck=?,vpaAddressDailyCount=?,vpaAddressAmountLimitCheck=?,vpaAddressDailyAmountLimit=?,payoutBankAccountNoLimitCheck=?,bankAccountNoDailyCount=?,payoutBankAccountNoAmountLimitCheck=?,bankAccountNoDailyAmountLimit=?,isShareAllowed=?,isSignatureAllowed=?,isSaveReceiptAllowed=?,defaultLanguage=?,isDomainWhitelisted=?,customerIpLimitCheck=?,customerIpDailyCount=?,customerIpAmountLimitCheck=?,customerIpDailyAmountLimit=?,customerNameLimitCheck=?,customerNameDailyCount=?,customerNameAmountLimitCheck=?,customerNameDailyAmountLimit=?, customerEmailLimitCheck=?,customerEmailDailyCount=?,customerEmailAmountLimitCheck=?,customerEmailDailyAmountLimit=?,customerPhoneLimitCheck=?,customerPhoneDailyCount=?,customerPhoneAmountLimitCheck=?,customerPhoneDailyAmountLimit=?,inquiryNotification=?,paybylink=?,transmgt_payout_transactions=?,isOTPRequired=?,isCardStorageRequired=?,vpaAddressMonthlyCount=?,vpaAddressMonthlyAmountLimit=?,customerEmailMonthlyCount=?,customerEmailMonthlyAmountLimit=?,customerPhoneMonthlyCount=?,customerPhoneMonthlyAmountLimit=?,bankAccountNoMonthlyCount=?,bankAccountNoMonthlyAmountLimit=?,customerIpMonthlyCount=?,customerIpMonthlyAmountLimit=?,customerNameMonthlyCount=?,customerNameMonthlyAmountLimit=?,mc.IsIgnorePaymode=?,mc.checkout_config=?,m.daily_card_limit_check=?,m.monthly_card_limit_check=?,weekly_card_limit_check=?,success_url=?,failed_url=?,payout_success_url=?,payout_failed_url=?,success_url_check=?,failed_url_check=?,payout_success_url_check=?,payout_failed_url_check=?, totalPayoutAmount = ?,payoutPersonalInfoValidation=?,upi_support_invoice = ?,upi_qr_support_invoice = ?,paybylink_support_invoice = ?, AEPS_support_invoice = ?,mc.merchant_verify_otp=?,generateview=?");
                        /*query.append("update members m JOIN merchant_configuration mc ON m.memberid=mc.memberid  set aptprompt= ?,accountid=?,isPharma=?,isValidateEmail=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,custremindermail=?,activation=?,icici=?,haspaid=?,isservice=?,hralertproof=?,autoredirect=?,vbv=?,hrparameterised=?,partnerId=?,check_limit=?,masterCardSupported=?,invoicetemplate=?,isPoweredBy=?,template=?,iswhitelisted=?,card_transaction_limit=?,card_check_limit=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,isrefund=?,refunddailylimit=? ,agentId=?, isIpWhitelisted=?,autoSelectTerminal=?,isPODRequired=?,maxScoreAllowed=?,maxScoreAutoReversal=?,weekly_amount_limit=?,isappmanageractivate=?,iscardregistrationallowed=?,is_recurring=?,isRestrictedTicket=?,isTokenizationAllowed=?,tokenvaliddays=?,isAddrDetailsRequired=?,blacklistTransaction=?,flightMode=?,emailSent=?,onlineFraudCheck=?,isSplitPayment=?,splitPaymentType=?,isExcessCaptureAllowed=?,refundallowed_days=?,chargebackallowed_days=?,isCardEncryptionEnable=?,is_rest_whitelisted=?,smsactivation=?,customersmsactivation=?,emailLimitEnabled=?,ip_whitelist_invoice=?, binService=?, expDateOffset = ?,supportSection = ?,card_whitelist_level = ?,multiCurrencySupport = ?,dashboard_access=?,accounting_access=?,setting_access=?,transactions_access=?,invoicing_access=?,virtualterminal_access=?,merchantmgt_access=?,settings_merchant_config_access=?,settings_fraudrule_config_access=?, accounts_account_summary_access=?,accounts_charges_summary_access=?,accounts_transaction_summary_access=?,accounts_reports_summary_access=?,settings_merchant_profile_access=?,settings_organisation_profile_access=?, settings_checkout_page_access=?,settings_generate_key_access=?,settings_invoice_config_access=?,transmgt_transaction_access=?,transmgt_capture_access=?,transmgt_reversal_access=?,transmgt_payout_access=?,invoice_generate_access=?,invoice_history_access=?,tokenmgt_registration_history_access=?,tokenmgt_register_card_access=?,merchantmgt_user_management_access=?,ispcilogo=?,notificationUrl=?,termsUrl=?,ip_validation_required=?,ispartnerlogo=?,ismerchantlogo=?,isRefundEmailSent=?,privacyPolicyUrl=?,binRouting=?,personal_info_display=?,personal_info_validation=?,hosted_payment_page=?,vbvLogo=?,masterSecureLogo=?,consent=?,chargebackEmail=?,isMultipleRefund=?,isSecurityLogo=?,settings_whitelist_details=?,settings_blacklist_details=?,emi_configuration=?,emiSupport=?,isPartialRefund=?,internalFraudCheck=?,card_velocity_check=?,merchant_order_details=?,limitRouting=?,checkoutTimer=?,checkoutTimerTime=?,marketplace=?,rejected_transaction=?,virtual_checkout=?,isVirtualCheckoutAllowed=?,isMobileAllowedForVC=?,isEmailAllowedForVC=?,isCvvStore=?,reconciliationNotification=?,transactionNotification=?,refundNotification=?,chargebackNotification=?,ispurchase_inquiry_blacklist=?,ispurchase_inquiry_refund=?,isfraud_determined_refund=?,isfraud_determined_blacklist=?,isdispute_initiated_refund=?,isdispute_initiated_blacklist=?,isstop_payment_blacklist=?,isexception_file_listing_blacklist=?,merchantRegistrationMail=?,merchantChangePassword=?,merchantChangeProfile=?,transactionSuccessfulMail=?,transactionFailMail=?,transactionCapture=?,transactionPayoutSuccess=?,transactionPayoutFail=?,refundMail=?,chargebackMail=?,transactionInvoice=?,cardRegistration=?,payoutReport=?,monitoringAlertMail=?,monitoringSuspensionMail=?,highRiskRefunds=?,fraudFailedTxn=?,dailyFraudReport=?,customerTransactionSuccessfulMail=?,customerTransactionFailMail=?,customerTransactionPayoutSuccess=?,customerTransactionPayoutFail=?,customerRefundMail=?,customerTokenizationMail=?,isUniqueOrderIdRequired=?,daily_payout_amount_limit=?,weekly_payout_amount_limit=?,monthly_payout_amount_limit=?,payout_amount_limit_check=?,emailTemplateLang=?,successReconMail=?,refundReconMail=?,chargebackReconMail=?,payoutReconMail=?,isMerchantLogoBO=?,cardExpiryDateCheck=?");*/
                        if (!activation.equals("Y") && active.equals("Y"))
                        {
                            query.append(" ,activation_date = ");
                            query.append("UNIX_TIMESTAMP(NOW())");
                        }
                        query.append(" where m.memberid=?");
                        pstmt= cn.prepareStatement(query.toString());
                        pstmt.setString(1, aptprompt);
                        pstmt.setString(2,accountID);
                        pstmt.setString(3,ispharma);
                        pstmt.setString(4,isValidEmail);
                        pstmt.setString(5,daily_amount_limit);
                        pstmt.setString(6,monthly_amount_limit);
                        pstmt.setString(7,daily_card_limit);
                        pstmt.setString(8,weekly_card_limit);
                        pstmt.setString(9,monthly_card_limit);
                        /*pstmt.setString(10,custrememail);*/
                        pstmt.setString(10,active);
                        pstmt.setString(11,icici);
                        pstmt.setString(12,haspaid);
                        pstmt.setString(13,isservice);
                        pstmt.setString(14,hralertproof);
                        pstmt.setString(15,autoredirect);
                        pstmt.setString(16,vbv);
                        pstmt.setString(17,hrparameterised);
                        pstmt.setString(18,partnerId);
                        pstmt.setString(19,check_limit);
                        pstmt.setString(20,masterCardSupported);
                        pstmt.setString(21,invoicetemplate);
                        pstmt.setString(22,isPoweredBy);
                        pstmt.setString(23,template);
                        pstmt.setString(24,iswhitelisted);
                        pstmt.setString(25,String.valueOf(card_transaction_limit));
                        pstmt.setString(26,String.valueOf(card_check_limit));
                        pstmt.setString(27,daily_card_amount_limit);
                        pstmt.setString(28,weekly_card_amount_limit);
                        pstmt.setString(29,monthly_card_amount_limit);
                        pstmt.setString(30,isrefund);
                        pstmt.setString(31,refunddailylimit);
                        pstmt.setString(32,agentid);
                        pstmt.setString(33,isipwhitelist);
                        pstmt.setString(34,autoSelectTerminal);
                        pstmt.setString(35,isPODRequired);
                        pstmt.setString(36,maxScoreAllowed);
                        pstmt.setString(37,maxScoreReversal);
                        pstmt.setString(38,weekly_amount_limit);
                        pstmt.setString(39,isappmanageractivate);
                        pstmt.setString(40,iscardregistrationallowed);
                        pstmt.setString(41,isRecurring);
                        pstmt.setString(42,isRestrictedTicket);
                        pstmt.setString(43,isTokenizationAllowed);
                        pstmt.setString(44,tokenValidDays);
                        pstmt.setString(45,isAddrDetailsRequired);
                        pstmt.setString(46,blacklistTransaction);
                        pstmt.setString(47,flightMode);
                        /*pstmt.setString(49,emailSent);*/
                        pstmt.setString(48,fraudCheck);
                        pstmt.setString(49,splitPayment);
                        pstmt.setString(50,splitPaymentType);
                        pstmt.setString(51,isExcessCaptureAllowed);
                        pstmt.setString(52,refundAllowedDays);
                        pstmt.setString(53,chargebackAllowedDays);
                        pstmt.setString(54,isCardEncryptionEnable);
                        pstmt.setString(55,is_rest_whitelisted);
                        pstmt.setString(56, smsActivation);
                        pstmt.setString(57, cSmsActivation);
                        pstmt.setString(58, isEmailLilmitenabled);
                        pstmt.setString(59, ip_whitelist_invoice);
                        pstmt.setString(60, binService);
                        pstmt.setString(61, expDateOffset);
                        pstmt.setString(62, supportSection);
                        pstmt.setString(63, card_whitelist_level);
                        pstmt.setString(64, multiCurrencySupport);
                        pstmt.setString(65, dashBoard);
                        pstmt.setString(66, accounting);
                        pstmt.setString(67, settings);
                        pstmt.setString(68, transactionManagement);
                        pstmt.setString(69, invoicing);
                        pstmt.setString(70, virtualTerminal);
                        pstmt.setString(71, merchantManagement);
                        pstmt.setString(72, settingsMerchantConfigAccess);
                        pstmt.setString(73, settingsFraudRuleConfigAccess);
                        pstmt.setString(74, accountsAccountSummaryAccess);
                        pstmt.setString(75, accountsChargesSummaryAccess);
                        pstmt.setString(76, accountsTransactionSummaryAccess);
                        pstmt.setString(77, accountsReportSummaryAccess);
                        pstmt.setString(78, settingsMerchantProfileAccess);
                        pstmt.setString(79, settingsOrganisationProfileAccess);
                        pstmt.setString(80, "N");
                        pstmt.setString(81, settingsGenerateKeyAccess);
                        pstmt.setString(82, settingsInvoiceConfigAccess);
                        pstmt.setString(83, transMgtTransactionAccess);
                        pstmt.setString(84, transMgtCaptureAccess);
                        pstmt.setString(85, transMgtReversalAccess);
                        pstmt.setString(86, transMgtPayoutAccess);
                        pstmt.setString(87, invoiceGenerateAccess);
                        pstmt.setString(88, invoiceHistoryAccess);
                        pstmt.setString(89, tokenMgtRegistrationHistoryAccess);
                        pstmt.setString(90, tokenMgtRegisterCardAccess);
                        pstmt.setString(91,merchantMgtUserManagementAccess);
                        pstmt.setString(92,ispcilogo);
                        pstmt.setString(93,notificationUrl);
                        pstmt.setString(94,termsUrl);
                        pstmt.setString(95,ipValidationRequired);
                        pstmt.setString(96,isPartnerLogo);
                        pstmt.setString(97,ismerchantlogo);
                        /*pstmt.setString(100,isRefundEmailSent);*/
                        pstmt.setString(98,privacyPolicyUrl);
                        pstmt.setString(99,binRouting);
                        pstmt.setString(100,personalInfoDisplay);
                        pstmt.setString(101,personalInfoValidation);
                        pstmt.setString(102,hostedPaymentPage);
                        pstmt.setString(103,vbvLogo);
                        pstmt.setString(104,masterSecureLogo);
                        pstmt.setString(105,consent);
                        /*pstmt.setString(109,chargebackMailSend);*/
                        pstmt.setString(106,isMultipleRefund);
                        pstmt.setString(107,isSecurityLogo);
                        pstmt.setString(108,settingsWhitelistDetails);
                        pstmt.setString(109,settingsBlacklistDetails);
                        pstmt.setString(110,emiConfiguration);
                        pstmt.setString(111,emiSupport);
                        pstmt.setString(112,isPartialRefund);
                        pstmt.setString(113,internalFraudCheck);
                        pstmt.setString(114,card_velocity_check);
                        pstmt.setString(115,merchant_order_details);
                        pstmt.setString(116,limitRouting);
                        pstmt.setString(117,checkoutTimer);
                        pstmt.setString(118,checkoutTimerTime);
                        pstmt.setString(119, marketplace);
                        pstmt.setString(120, rejected_transaction);
                        pstmt.setString(121, virtual_checkout);
                        pstmt.setString(122, isVirtualCheckoutAllowed);
                        pstmt.setString(123, isMobileAllowedForVC);
                        pstmt.setString(124, isEmailAllowedForVC);
                        pstmt.setString(125, isCvvStore);
                        pstmt.setString(126, reconciliationNotification);
                        pstmt.setString(127, transactionNotification);
                        pstmt.setString(128, refundNotification);
                        pstmt.setString(129, chargebackNotification);
                        pstmt.setString(130, ispurchase_inquiry_blacklist);
                        pstmt.setString(131, ispurchase_inquiry_refund);
                        pstmt.setString(132, isfraud_determined_refund);
                        pstmt.setString(133, isfraud_determined_blacklist);
                        pstmt.setString(134, isdispute_initiated_refund);
                        pstmt.setString(135, isdispute_initiated_blacklist);
                        pstmt.setString(136, isstop_payment_blacklist);
                        pstmt.setString(137, isexception_file_listing_blacklist);
                        pstmt.setString(138, merchantRegistrationMail);
                        pstmt.setString(139, merchantChangePassword);
                        pstmt.setString(140, merchantChangeProfile);
                        pstmt.setString(141, transactionSuccessfulMail);
                        pstmt.setString(142, transactionFailMail);
                        pstmt.setString(143, transactionCapture);
                        pstmt.setString(144, transactionPayoutSuccess);
                        pstmt.setString(145, transactionPayoutFail);
                        pstmt.setString(146, refundMail);
                        pstmt.setString(147, chargebackMail);
                        pstmt.setString(148, transactionInvoice);
                        pstmt.setString(149, cardRegistration);
                        pstmt.setString(150, payoutReport);
                        pstmt.setString(151, monitoringAlertMail);
                        pstmt.setString(152, monitoringSuspensionMail);
                        pstmt.setString(153, highRiskRefunds);
                        pstmt.setString(154, fraudFailedTxn);
                        pstmt.setString(155, dailyFraudReport);
                        pstmt.setString(156, customerTransactionSuccessfulMail);
                        pstmt.setString(157, customerTransactionFailMail);
                        pstmt.setString(158, customerTransactionPayoutSuccess);
                        pstmt.setString(159, customerTransactionPayoutFail);
                        pstmt.setString(160, customerRefundMail);
                        pstmt.setString(161, customerTokenizationMail);
                        pstmt.setString(162, isUniqueOrderIdRequired);
                        pstmt.setString(163, daily_payout_amount_limit);
                        pstmt.setString(164, weekly_payout_amount_limit);
                        pstmt.setString(165, monthly_payout_amount_limit);
                        pstmt.setString(166, payout_amount_limit_check);
                        pstmt.setString(167, emailTemplateLang);
                        pstmt.setString(168, successReconMail);
                        pstmt.setString(169, refundReconMail);
                        pstmt.setString(170, chargebackReconMail);
                        pstmt.setString(171, payoutReconMail);
                        pstmt.setString(172, isMerchantLogoBO);
                        pstmt.setString(173, cardExpiryDateCheck);
                        pstmt.setString(174,payoutLimitRouting);
                        pstmt.setString(175,payoutNotification);
                        pstmt.setString(176,supportNoNeeded);
                        pstmt.setString(177,vpaAddressLimitCheck);
                        pstmt.setString(178,vpaAddressDailyCount);
                        pstmt.setString(179,vpaAddressAmountLimitCheck);
                        pstmt.setString(180,vpaAddressDailyAmountLimit);
                        pstmt.setString(181,payoutBankAccountNoLimitCheck);
                        pstmt.setString(182, bankAccountNoDailyCount);
                        pstmt.setString(183,payoutBankAccountNoAmountLimitCheck);
                        pstmt.setString(184, bankAccountNoDailyAmountLimit);
                        pstmt.setString(185, isShareAllowed);
                        pstmt.setString(186, isSignatureAllowed);
                        pstmt.setString(187, isSaveReceiptAllowed);
                        pstmt.setString(188, defaultLanguage);
                        pstmt.setString(189, isDomainWhitelisted);
                        pstmt.setString(190, customerIpLimitCheck);
                        pstmt.setString(191, customerIpDailyCount);
                        pstmt.setString(192, customerIpAmountLimitCheck);
                        pstmt.setString(193, customerIpDailyAmountLimit);
                        pstmt.setString(194, customerNameLimitCheck);
                        pstmt.setString(195, customerNameDailyCount);
                        pstmt.setString(196, customerNameAmountLimitCheck);
                        pstmt.setString(197, customerNameDailyAmountLimit);
                        pstmt.setString(198, customerEmailLimitCheck);
                        pstmt.setString(199, customerEmailDailyCount);
                        pstmt.setString(200, customerEmailAmountLimitCheck);
                        pstmt.setString(201, customerEmailDailyAmountLimit);
                        pstmt.setString(202, customerPhoneLimitCheck);
                        pstmt.setString(203, customerPhoneDailyCount);
                        pstmt.setString(204, customerPhoneAmountLimitCheck);
                        pstmt.setString(205, customerPhoneDailyAmountLimit);
                        pstmt.setString(206, inquiryNotification);
                        pstmt.setString(207, paybylink);
                        pstmt.setString(208, transmgt_payout_transactions);
                        pstmt.setString(209, isOTPRequired);
                        pstmt.setString(210, isCardStorageRequired);
                        pstmt.setString(211, vpaAddressMonthlyCount);
                        pstmt.setString(212, vpaAddressMonthlyAmountLimit);
                        pstmt.setString(213, customerEmailMonthlyCount);
                        pstmt.setString(214, customerEmailMonthlyAmountLimit);
                        pstmt.setString(215, customerPhoneMonthlyCount);
                        pstmt.setString(216, customerPhoneMonthlyAmountLimit);
                        pstmt.setString(217, bankAccountNoMonthlyCount);
                        pstmt.setString(218, bankAccountNoMonthlyAmountLimit);
                        pstmt.setString(219, customerIpMonthlyCount);
                        pstmt.setString(220, customerIpMonthlyAmountLimit);
                        pstmt.setString(221, customerNameMonthlyCount);
                        pstmt.setString(222, customerNameMonthlyAmountLimit);
                        pstmt.setString(223, isIgnorePaymode);
                        pstmt.setString(224, checkoutConfig);
                        pstmt.setString(225, daily_card_limit_check);
                        pstmt.setString(226, monthly_card_limit_check);
                        pstmt.setString(227, weekly_card_limit_check);
                        pstmt.setString(228, success_url);
                        pstmt.setString(229, failed_url);
                        pstmt.setString(230, payout_success_url);
                        pstmt.setString(231, payout_failed_url);
                        pstmt.setString(232, success_url_check);
                        pstmt.setString(233, failed_url_check);
                        pstmt.setString(234, payout_success_url_check);
                        pstmt.setString(235, payout_failed_url_check);
                        pstmt.setString(236, totalPayoutAmount);
                        pstmt.setString(237, payoutPersonalInfoValidation);
                        pstmt.setString(238, upi_support_invoice);
                        pstmt.setString(239, upi_qr_support_invoice);
                        pstmt.setString(240, paybylink_support_invoice);
                        pstmt.setString(241, AEPS_support_invoice);
                        pstmt.setString(242, merchant_verify_otp);
                        pstmt.setString(243, generateview);
                        transactionLogger.error("weekly card limit +++ "+pstmt);

                        pstmt.setString(244, memberids[i]);

                        transactionLogger.error("Query::::"+pstmt);
                        int result = pstmt.executeUpdate();
                        if (result > 0)
                        {
                            updRecs++;
                            FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
                            String remoteAddr = Functions.getIpAddress(req);
                            int serverPort = req.getServerPort();
                            String servletPath = req.getServletPath();
                            String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
                            if(function.isValueNull(onchangedValues))
                            {
                                activityTrackerVOs.setInterface(ActivityLogParameters.ADMIN.toString());
                                activityTrackerVOs.setUser_name(Login + "-" + actionExecutorId);
                                activityTrackerVOs.setRole(ActivityLogParameters.ADMIN.toString());
                                activityTrackerVOs.setAction(ActivityLogParameters.EDIT.toString());
                                activityTrackerVOs.setModule_name(ActivityLogParameters.MERCHANT_CONFIGURATION.toString());
                                activityTrackerVOs.setLable_values(onchangedValues);
                                activityTrackerVOs.setDescription(ActivityLogParameters.MEMBERID.toString() + "-" + memberids[i]);
                                activityTrackerVOs.setIp(remoteAddr);
                                activityTrackerVOs.setHeader(header);
                                try
                                {
                                    AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                                    asyncActivityTracker.asyncActivity(activityTrackerVOs);
                                }
                                catch (Exception e)
                                {
                                    transactionLogger.error("Exception while AsyncActivityLog::::", e);
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    transactionLogger.error("Error while set reserves :",e);
                    req.setAttribute("error",errorMsg);
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closePreparedStatement(pstmt);
                    Database.closePreparedStatement(pstmt1);
                    Database.closePreparedStatement(pstmt2);
                    Database.closeConnection(cn);
                }
            }
            sSuccessMessage.append(updRecs + " Records Updated");
            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            String redirectpage = "/memberpreference.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
        }
    }
    private String getValidPercentage(String paramValue)
    {
        String returnVal = getValidAmount(paramValue);
        if(returnVal != null)
        {
            returnVal =  "" + new BigDecimal(returnVal).multiply(new BigDecimal("100"));
        }
        return returnVal;
    }
    private String getValidAmount(String paramValue)
    {
        if ("N/A".equals(paramValue))
        {
            return null;
        }
        String returnVal = Functions.checkStringNull(paramValue);
        if(returnVal == null )
        {
            returnVal = "0";
        }
        else
        {
            try
            {
                returnVal = "" + new BigDecimal(returnVal);
            }
            catch(NumberFormatException ne)
            {  transactionLogger.error("Number Formet Exception",ne);

                returnVal = "0";
            }
        }
        return returnVal;
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<br>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.DAILY_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_AMT_LIMIT);

        inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT);

        inputFieldsListOptional.add(InputFields.HIGH_RISK_AMT);

        inputFieldsListOptional.add(InputFields.DAILY_CARD_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMT_LIMIT);

        inputFieldsListOptional.add(InputFields.MAX_SCORE_ALLOWED);
        inputFieldsListOptional.add(InputFields.MAX_SCORE_REVERSEL);
        inputFieldsListOptional.add(InputFields.REFUND_DAILY_LIMIT);
        inputFieldsListOptional.add(InputFields.TOKENVALIDDAYS);
        inputFieldsListOptional.add(InputFields.VPA_ADDRESS_DAILY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_IP_DAILY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_NAME_DAILY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_EMAIL_DAILY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_PHONE_DAILY_COUNT);
        inputFieldsListOptional.add(InputFields.VPA_ADDRESS_DAILY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_IP_DAILY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_NAME_DAILY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_EMAIL_DAILY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_PHONE_DAILY_AMOUNT_LIMIT);

        inputFieldsListOptional.add(InputFields.VPA_ADDRESS_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.VPA_ADDRESS_MONTHLY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_EMAIL_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_EMAIL_MONTHLY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_PHONE_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_PHONE_MONTHLY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.BANK_ACCOUNT_NO_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.BANK_ACCOUNT_NO_MONTHLY_AMOUNT_LIMIT);

        inputFieldsListOptional.add(InputFields.CUSTOMER_IP_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_IP_MONTHLY_AMOUNT_LIMIT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_NAME_MONTHLY_COUNT);
        inputFieldsListOptional.add(InputFields.CUSTOMER_NAME_MONTHLY_AMOUNT_LIMIT);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

}
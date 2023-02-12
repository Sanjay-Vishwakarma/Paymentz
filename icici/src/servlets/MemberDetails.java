import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class MemberDetails extends HttpServlet
{
    private static Logger logger = new Logger(MemberDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");
        String errormsg                 = "";
        StringBuilder sSuccessMessage   = new StringBuilder();
        StringBuilder sErrorMessage     = new StringBuilder();

        int start       = 0; // start index
        int end         = 0; // end index
        int pageno      = 1;
        int records     = 15;
        Functions functions = new Functions();
        errormsg        = errormsg + validateParameters(req);
        if(functions.isValueNull(errormsg)){
            req.setAttribute("error", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        String memberid = req.getParameter("memberid");
        req.setAttribute("memberid",memberid);

        String month    = req.getParameter("month");
        String year     = req.getParameter("year");

        int newmonth = 0;
        if (month != null)
            newmonth = Integer.parseInt(month);
        if (newmonth != 0)
        {
            if (newmonth < 10)
                month = "0" + newmonth;
            else
                month = "" + newmonth;
        }

        pageno  = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start   = (pageno - 1) * records;
        end     = records;
        Date d  = new Date();
        logger.error("MemberDetails start time-->"+d.getTime());

        Hashtable hash      = null;
        Codec me            = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query  = new StringBuffer("SELECT company_name,contact_emails,m.memberid,invoicetemplate,isPoweredBy,template,activation,icici,aptprompt,reserves,chargeper,fixamount,reserve_reason,accountid,reversalcharge,withdrawalcharge,chargebackcharge,taxper,isPharma,isValidateEmail,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,activation,icici,haspaid,isservice,hralertproof,autoredirect,vbv,hrparameterised,masterCardSupported,check_limit,partnerid,iswhitelisted,card_transaction_limit,card_check_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,isrefund,refunddailylimit,agentId,isIpWhitelisted,autoSelectTerminal,isPODRequired,maxScoreAllowed,maxScoreAutoReversal,weekly_amount_limit,isappmanageractivate,iscardregistrationallowed,is_recurring,isRestrictedTicket,isTokenizationAllowed,tokenvaliddays,isAddrDetailsRequired,blacklistTransaction,flightMode,onlineFraudCheck,isSplitPayment,splitPaymentType,isExcessCaptureAllowed,ispcilogo,refundallowed_days,chargebackallowed_days,isCardEncryptionEnable,is_rest_whitelisted,smsactivation,customersmsactivation,emailLimitEnabled,ip_whitelist_invoice,binService,expDateOffset,supportSection,card_whitelist_level,multiCurrencySupport,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,settings_merchant_config_access,settings_fraudrule_config_access,merchantmgt_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,transmgt_payout_transactions,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,merchantmgt_user_management_access,notificationUrl,termsUrl,ip_validation_required,ispartnerlogo,ismerchantlogo,privacyPolicyUrl,binRouting,vbvLogo,masterSecureLogo,personal_info_display,personal_info_validation,hosted_payment_page,consent,isSecurityLogo,isMultipleRefund,settings_whitelist_details,settings_blacklist_details,emi_configuration,emiSupport,isPartialRefund,internalFraudCheck,card_velocity_check,merchant_order_details,limitRouting,checkoutTimer,checkoutTimerTime,marketplace,rejected_transaction,virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification,ispurchase_inquiry_blacklist,ispurchase_inquiry_refund,isfraud_determined_refund,isfraud_determined_blacklist,isdispute_initiated_refund,isdispute_initiated_blacklist,isstop_payment_blacklist,isexception_file_listing_blacklist,merchantRegistrationMail,merchantChangePassword,merchantChangeProfile,transactionSuccessfulMail,transactionFailMail,transactionCapture,transactionPayoutSuccess,transactionPayoutFail,refundMail,chargebackMail,transactionInvoice,cardRegistration,payoutReport,monitoringAlertMail,monitoringSuspensionMail,highRiskRefunds,fraudFailedTxn,dailyFraudReport,customerTransactionSuccessfulMail,customerTransactionFailMail,customerTransactionPayoutSuccess,customerTransactionPayoutFail,customerRefundMail,customerTokenizationMail,isUniqueOrderIdRequired,daily_payout_amount_limit,weekly_payout_amount_limit,monthly_payout_amount_limit,payout_amount_limit_check,emailTemplateLang,successReconMail,refundReconMail,chargebackReconMail,payoutReconMail,isMerchantLogoBO,supportNoNeeded,mc.cardExpiryDateCheck,mc.payoutRouting,payoutNotification,inquiryNotification,mc.vpaAddressLimitCheck,mc.vpaAddressDailyCount,mc.vpaAddressAmountLimitCheck,mc.vpaAddressDailyAmountLimit,mc.payoutBankAccountNoLimitCheck,mc.bankAccountNoDailyCount,mc.payoutBankAccountNoAmountLimitCheck,mc.bankAccountNoDailyAmountLimit,isShareAllowed,isSignatureAllowed,isSaveReceiptAllowed,defaultLanguage,isDomainWhitelisted,mc.customerIpLimitCheck,mc.customerIpDailyCount,mc.customerIpAmountLimitCheck,mc.customerIpDailyAmountLimit,mc.customerNameLimitCheck,mc.customerNameDailyCount,mc.customerNameAmountLimitCheck,mc.customerNameDailyAmountLimit,mc.customerEmailLimitCheck,mc.customerEmailDailyCount,mc.customerEmailAmountLimitCheck,mc.customerEmailDailyAmountLimit,mc.customerPhoneLimitCheck,mc.customerPhoneDailyCount,mc.customerPhoneAmountLimitCheck,mc.customerPhoneDailyAmountLimit,paybylink,mc.isOTPRequired,mc.isCardStorageRequired,mc.vpaAddressMonthlyCount,mc.vpaAddressMonthlyAmountLimit,mc.customerEmailMonthlyCount,mc.customerEmailMonthlyAmountLimit,mc.customerPhoneMonthlyCount,mc.customerPhoneMonthlyAmountLimit,mc.bankAccountNoMonthlyCount,mc.bankAccountNoMonthlyAmountLimit,mc.customerIpMonthlyCount,mc.customerIpMonthlyAmountLimit,mc.customerNameMonthlyCount,mc.customerNameMonthlyAmountLimit,mc.IsIgnorePaymode,mc.checkout_config,mc.success_url,mc.failed_url,mc.payout_success_url,mc.payout_failed_url,mc.success_url_check,mc.failed_url_check,mc.payout_success_url_check,mc.payout_failed_url_check,m.daily_card_limit_check,m.monthly_card_limit_check,m.weekly_card_limit_check, mc.totalPayoutAmount,mc.payoutPersonalInfoValidation,mc.upi_support_invoice,mc.upi_qr_support_invoice,mc.paybylink_support_invoice,mc.AEPS_support_invoice,mc.merchant_verify_otp,mc.generateview FROM members AS m JOIN merchant_configuration AS mc ON m.memberid=mc.memberid WHERE m.memberid=?");
        //StringBuffer query =new StringBuffer("SELECT company_name,contact_emails,m.memberid,invoicetemplate,isPoweredBy,template,activation,icici,aptprompt,reserves,chargeper,fixamount,reserve_reason,accountid,reversalcharge,withdrawalcharge,chargebackcharge,taxper,isPharma,isValidateEmail,custremindermail,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,activation,icici,haspaid,isservice,hralertproof,autoredirect,vbv,hrparameterised,masterCardSupported,check_limit,partnerid,iswhitelisted,card_transaction_limit,card_check_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,isrefund,refunddailylimit,agentId,isIpWhitelisted,autoSelectTerminal,isPODRequired,maxScoreAllowed,maxScoreAutoReversal,weekly_amount_limit,isappmanageractivate,iscardregistrationallowed,is_recurring,isRestrictedTicket,isTokenizationAllowed,tokenvaliddays,isAddrDetailsRequired,blacklistTransaction,flightMode,emailSent,onlineFraudCheck,isSplitPayment,splitPaymentType,isExcessCaptureAllowed,ispcilogo,refundallowed_days,chargebackallowed_days,isCardEncryptionEnable,is_rest_whitelisted,smsactivation,customersmsactivation,emailLimitEnabled,ip_whitelist_invoice,binService,expDateOffset,supportSection,card_whitelist_level,multiCurrencySupport,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,settings_merchant_config_access,settings_fraudrule_config_access,merchantmgt_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,merchantmgt_user_management_access,notificationUrl,termsUrl,ip_validation_required,ispartnerlogo,ismerchantlogo,isRefundEmailSent,privacyPolicyUrl,binRouting,vbvLogo,masterSecureLogo,chargebackEmail,personal_info_display,personal_info_validation,hosted_payment_page,consent,isSecurityLogo,isMultipleRefund,settings_whitelist_details,settings_blacklist_details,emi_configuration,emiSupport,isPartialRefund,internalFraudCheck,card_velocity_check,merchant_order_details,limitRouting,checkoutTimer,checkoutTimerTime,marketplace,rejected_transaction,virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification,ispurchase_inquiry_blacklist,ispurchase_inquiry_refund,isfraud_determined_refund,isfraud_determined_blacklist,isdispute_initiated_refund,isdispute_initiated_blacklist,isstop_payment_blacklist,isexception_file_listing_blacklist FROM members AS m JOIN merchant_configuration AS mc ON m.memberid=mc.memberid WHERE m.memberid=?");
        StringBuffer countquery = new StringBuffer("select count(*) from members where memberid=?");
        query.append(" order by memberid asc LIMIT " + start + "," + end);

        String str      = "select partnerId,partnerName from partners";
        String count    = "select count(*) from partners";

        String agentstr     = "select agentId,agentName from agents";
        String agentcount   = "select count(*) from agents";

        Connection con      = null;
        PreparedStatement p = null;
        PreparedStatement p1= null;
        ResultSet rs        = null;
        ResultSet rs1       = null;
        try
        {
            Functions f = new Functions();
            //con = Database.getConnection();
            con         = Database.getRDBConnection();
            if (f.isValueNull(memberid))
            {
                p       = con.prepareStatement(query.toString());
                p.setString(1,memberid);
                Date d2 = new Date();
                logger.error("member execute start time-->"+d2.getTime());
                hash    = Database.getHashFromResultSet(p.executeQuery());
                p1      = con.prepareStatement(countquery.toString());
                p1.setString(1,memberid);
                logger.debug("query::::"+p);
                rs = p1.executeQuery();
                logger.error("member execute end time-->"+new Date().getTime());
                logger.error("member execute Diff time-->"+(new Date().getTime()-d2.getTime()));
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");
                d2  = new Date();
                logger.error("gateway execute start time-->"+d2.getTime());
                req.setAttribute("accoutIDwiseMerchantHash", GatewayAccountService.getMerchantDetails());
                logger.error("gateway execute end time-->"+new Date().getTime());
                logger.error("gateway execute Diff time-->"+(new Date().getTime()-d2.getTime()));
                if (totalrecords > 0)
                    hash.put("records", "" + (hash.size() - 2));

                d2  = new Date();
                logger.error("partner execute start time-->"+d2.getTime());
                Hashtable hash1 = Database.getHashFromResultSet(Database.executeQuery(str,con));
                rs1             = Database.executeQuery(count,con);
                logger.error("partner execute end time-->"+new Date().getTime());
                logger.error("partner execute Diff time-->"+(new Date().getTime()-d2.getTime()));
                rs1.next();
                int totalrecords1 = rs1.getInt(1);

                hash1.put("totalrecords1", "" + totalrecords1);
                hash1.put("records1", "0");

                if (totalrecords1 > 0)
                    hash1.put("records1", "" + (hash1.size() - 2));
                req.setAttribute("partners", hash1);
                d2  = new Date();
                logger.error("agent execute start time-->"+d2.getTime());
                Hashtable agenthash1    = Database.getHashFromResultSet(Database.executeQuery(agentstr,con));
                ResultSet agentrs1      = Database.executeQuery(agentcount,con);
                logger.error("agent execute end time-->"+new Date().getTime());
                logger.error("agent execute Diff time-->"+(new Date().getTime()-d2.getTime()));
                agentrs1.next();
                int totalagentrecords1 = agentrs1.getInt(1);
                agenthash1.put("totalrecords2", "" + totalagentrecords1);
                agenthash1.put("records2", "0");
                if (totalagentrecords1 > 0)
                    agenthash1.put("records2", "" + (agenthash1.size() - 2));
                req.setAttribute("agents", agenthash1);
            }
            else
            {
                hash = new Hashtable();
                hash.put("records", "0");
                hash.put("totalrecords", "0");
            }

            hash.put("month", "" + month);
            hash.put("year", "" + year);
            req.setAttribute("memberdetails", hash);
            logger.error("MemberDeatils end time-->" + new Date().getTime());
            logger.error("MemberDeatils diff time-->"+(new Date().getTime()-d.getTime()));

        }
        catch (SystemError se)
        {
            logger.error("SystemError::::", se);
            sErrorMessage.append("Internal error while processing your request");
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal error while processing your request");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                logger.error("SQL Exception::::",e);
                sErrorMessage.append("Internal error while processing your request");
            }
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error", errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/memberpreference.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage();
                }
            }
        }
        return error;
    }
}
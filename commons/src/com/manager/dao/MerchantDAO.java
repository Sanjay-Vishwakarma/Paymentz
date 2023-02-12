package com.manager.dao;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.DateManager;
import com.manager.enums.TemplatePreference;
import com.manager.vo.InputDateVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO;
import com.manager.vo.memeberConfigVOS.MemberAccountMappingVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/1/14
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantDAO
{
    private static Logger           logger              = new Logger(MerchantDAO.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(MerchantDAO.class.getName());
    private static Functions functions                  = new Functions();
    DateManager dateManager= new DateManager();
    boolean isLogEnabled                                = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));
    public MerchantDetailsVO getMemberDetails(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO   merchantDetailsVO   = new MerchantDetailsVO();
        Connection          conn                = null;
        PreparedStatement   pstmt               = null;
        ResultSet           res                 = null;
        try
        {
            conn = Database.getRDBConnection();
            if (isLogEnabled)
            {
                logger.debug("Entering getMember Details");
            }
            String query = "SELECT p.responseLength,p.responseType,p.flightMode as pflightMode,m.*,FROM_UNIXTIME(m.dtstamp) AS signupdate,p.hosturl,p.partnerName,p.logoName,p.default_theme,p.current_theme,p.iconName, p.telno as ptelno,p.bankCardLimit,p.template as partnertemplate,m.internalFraudCheck,mc.limitRouting,mc.marketPlace, m.notificationUrl,mc.isCvvStore,mc.ispurchase_inquiry_blacklist,mc.ispurchase_inquiry_refund,mc.isfraud_determined_refund,mc.isfraud_determined_blacklist,mc.isdispute_initiated_refund,mc.isdispute_initiated_blacklist,mc.isstop_payment_blacklist,mc.isexception_file_listing_blacklist,mc.reconciliationNotification,mc.chargebackNotification,mc.isUniqueOrderIdRequired ,mc.daily_payout_amount_limit,mc.weekly_payout_amount_limit,mc.monthly_payout_amount_limit, mc.payout_amount_limit_check,mc.successReconMail,mc.refundReconMail,mc.chargebackReconMail,mc.payoutReconMail,mc.isMerchantLogoBO,mc.cardExpiryDateCheck,mc.chargebackMail,mc.transactionNotification,mc.payoutRouting,mc.payoutNotification,mc.vpaAddressLimitCheck,mc.vpaAddressAmountLimitCheck, mc.payoutBankAccountNoLimitCheck, mc.payoutBankAccountNoAmountLimitCheck,mc.customerIpLimitCheck,mc.customerIpAmountLimitCheck,mc.customerNameLimitCheck,mc.customerNameAmountLimitCheck,mc.customerEmailLimitCheck,mc.customerEmailAmountLimitCheck,mc.customerPhoneLimitCheck,mc.customerPhoneAmountLimitCheck,mc.isOTPRequired,mc.isCardStorageRequired,mc.IsIgnorePaymode,mc.payoutPersonalInfoValidation FROM members AS m,partners AS p,merchant_configuration AS mc WHERE m.memberid=? AND m.memberid=mc.memberid AND m.partnerId=p.partnerId";


            pstmt       = conn.prepareStatement(query);
            pstmt.setString(1,toid);
            if (isLogEnabled)
            {
                logger.debug("merchant Details loaded" + pstmt);
            }
            res = pstmt.executeQuery();
            if (res.next())
            {
                merchantDetailsVO.setResponseLength(res.getString("responseLength"));
                merchantDetailsVO.setResponseType(res.getString("responseType"));
                merchantDetailsVO.setMitigationMail(res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof(res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof(res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name(res.getString("company_name"));
                merchantDetailsVO.setContact_persons(res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails(res.getString("contact_emails"));
                merchantDetailsVO.setAddress(res.getString("address"));
                merchantDetailsVO.setVbvLogo(res.getString("vbv"));
                merchantDetailsVO.setCity(res.getString("city"));
                merchantDetailsVO.setState(res.getString("state"));
                merchantDetailsVO.setZip(res.getString("zip"));
                merchantDetailsVO.setCountry(res.getString("country"));
                merchantDetailsVO.setTelNo(res.getString("telno"));
                merchantDetailsVO.setFaxNo(res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail(res.getString("notifyemail"));
                merchantDetailsVO.setBrandName(res.getString("brandname"));
                merchantDetailsVO.setSiteName(res.getString("sitename"));
                merchantDetailsVO.setReserves(res.getString("reserves"));
                merchantDetailsVO.setAptPrompt(res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer(res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo(res.getString("checksumalgo"));
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(res.getString("clkey"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                merchantDetailsVO.setReversalCharge(res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(res.getString("memberid"));
                merchantDetailsVO.setAutoRedirect(res.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setPharma(res.getString("isPharma"));
                merchantDetailsVO.setIswhitelisted(res.getString("iswhitelisted"));
                merchantDetailsVO.setIsrefund(res.getString("isrefund"));
                merchantDetailsVO.setRefunddailylimit(res.getString("refunddailylimit"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setMerchantLogo(res.getString("ismerchantlogo"));
                merchantDetailsVO.setMerchantLogoName(res.getString("merchantlogoname"));
                merchantDetailsVO.setPartnerLogoFlag(res.getString("ispartnerlogo"));
                merchantDetailsVO.setSisaLogoFlag(res.getString("ispcilogo"));
                merchantDetailsVO.setIsRestrictedTicket(res.getString("isRestrictedTicket"));
                merchantDetailsVO.setIsTokenizationAllowed(res.getString("isTokenizationAllowed"));
                merchantDetailsVO.setIsBlacklistTransaction(res.getString("blacklistTransaction"));
                merchantDetailsVO.setIsAddressDetailsRequired(res.getString("isAddrDetailsRequired"));
                merchantDetailsVO.setAddressValidation(res.getString("addressValidation"));
                merchantDetailsVO.setPartnerName(res.getString("partnerName"));
                merchantDetailsVO.setLogoName(res.getString("logoName"));
                merchantDetailsVO.setFlightMode(res.getString("pflightMode"));
                merchantDetailsVO.setIsExcessCaptureAllowed(res.getString("isExcessCaptureAllowed"));
                merchantDetailsVO.setOnlineFraudCheck(res.getString("onlineFraudCheck"));
                merchantDetailsVO.setEmailSent(res.getString("emailSent"));
                merchantDetailsVO.setSplitPaymentAllowed(res.getString("isSplitPayment"));
                merchantDetailsVO.setLogin(res.getString("login"));
                merchantDetailsVO.setRegistrationDate(res.getString("signupdate"));
                merchantDetailsVO.setIsBackOfficeAccess(res.getString("icici"));
                merchantDetailsVO.setContactNumber(res.getString("telno"));
                merchantDetailsVO.setRefundAllowedDays(res.getString("refundallowed_days"));
                merchantDetailsVO.setChargebackAllowedDays(res.getString("chargebackallowed_days"));
                merchantDetailsVO.setIsCardEncryptionEnable(res.getString("isCardEncryptionEnable"));
                merchantDetailsVO.setIsIpWhiteListedCheckForAPIs(res.getString("is_rest_whitelisted"));
                merchantDetailsVO.setIpWhitelistInvoice(res.getString("ip_whitelist_invoice"));
                merchantDetailsVO.setAddressValidationInvoice(res.getString("address_validation_invoice"));
                merchantDetailsVO.setBinService(res.getString("binService"));
                merchantDetailsVO.setExpDateOffset(res.getString("expDateOffset"));
                merchantDetailsVO.setDefaultTheme(res.getString("default_theme"));
                merchantDetailsVO.setCurrentTheme(res.getString("current_theme"));
                merchantDetailsVO.setIcon(res.getString("iconName"));
                merchantDetailsVO.setCardWhitelistLevel(res.getString("card_whitelist_level"));
                merchantDetailsVO.setMultiCurrencySupport(res.getString("multiCurrencySupport"));
                merchantDetailsVO.setPartnerSupportContactNumber(res.getString("ptelno"));
                merchantDetailsVO.setHostUrl(res.getString("hosturl"));
                merchantDetailsVO.setIsRefundEmailSent(res.getString("isRefundEmailSent"));
                merchantDetailsVO.setChargebackMailsend(res.getString("chargebackMail"));
                merchantDetailsVO.setBinRouting(res.getString("binRouting"));
                merchantDetailsVO.setPersonalInfoDisplay(res.getString("personal_info_display"));
                merchantDetailsVO.setPersonalInfoValidation(res.getString("personal_info_validation"));
                merchantDetailsVO.setHostedPaymentPage(res.getString("hosted_payment_page"));
                merchantDetailsVO.setSecurityLogo(res.getString("isSecurityLogo"));
                merchantDetailsVO.setMultipleRefund(res.getString("isMultipleRefund"));
                merchantDetailsVO.setBlacklistCountryIp(res.getString("blacklist_country_ip"));
                merchantDetailsVO.setBlacklistCountryBin(res.getString("blacklist_country_bin"));
                merchantDetailsVO.setBlacklistCountryBinIp(res.getString("blacklist_country_bin_ip"));
                merchantDetailsVO.setEmiSupport(res.getString("emiSupport"));
                merchantDetailsVO.setBankCardLimit(res.getString("bankCardLimit"));
                merchantDetailsVO.setPartialRefund(res.getString("isPartialRefund"));
                merchantDetailsVO.setAddressDeatails(res.getString("addressDetails"));
                merchantDetailsVO.setBlacklistCountryIp(res.getString("blacklist_country_ip"));
                merchantDetailsVO.setBlacklistCountryBin(res.getString("blacklist_country_bin"));
                merchantDetailsVO.setBlacklistCountryBinIp(res.getString("blacklist_country_bin_ip"));
                merchantDetailsVO.setEmiSupport(res.getString("emiSupport"));
                merchantDetailsVO.setBankCardLimit(res.getString("bankCardLimit"));
                merchantDetailsVO.setInternalFraudCheck(res.getString("internalFraudCheck"));
                merchantDetailsVO.setCard_velocity_check(res.getString("card_velocity_check"));
                merchantDetailsVO.setMerchantOrderDetailsDisplay(res.getString("merchant_order_details"));
                merchantDetailsVO.setLimitRouting(res.getString("limitRouting"));
                merchantDetailsVO.setIsEmailLimitEnabled(res.getString("emailLimitEnabled"));
                merchantDetailsVO.setMarketPlace(res.getString("marketplace"));
                merchantDetailsVO.setNotificationUrl(res.getString("notificationUrl"));
                merchantDetailsVO.setConsentFlag(res.getString("consent"));
                merchantDetailsVO.setIsCvvStore(res.getString("isCvvStore"));
                merchantDetailsVO.setActionExecutorId(res.getString("actionExecutorId"));
                merchantDetailsVO.setActionExecutorName(res.getString("actionExecutorName"));
                merchantDetailsVO.setIspurchase_inquiry_blacklist(res.getString("ispurchase_inquiry_blacklist"));
                merchantDetailsVO.setIspurchase_inquiry_refund(res.getString("ispurchase_inquiry_refund"));
                merchantDetailsVO.setIsfraud_determined_refund(res.getString("isfraud_determined_refund"));
                merchantDetailsVO.setIsfraud_determined_blacklist(res.getString("isfraud_determined_blacklist"));
                merchantDetailsVO.setIsdispute_initiated_refund(res.getString("isdispute_initiated_refund"));
                merchantDetailsVO.setIsdispute_initiated_blacklist(res.getString("isdispute_initiated_blacklist"));
                merchantDetailsVO.setIsstop_payment_blacklist(res.getString("isstop_payment_blacklist"));
                merchantDetailsVO.setIsexception_file_listing_blacklist(res.getString("isexception_file_listing_blacklist"));
                merchantDetailsVO.setReconciliationNotification(res.getString("reconciliationNotification"));
                merchantDetailsVO.setIsUniqueOrderIdRequired(res.getString("isUniqueOrderIdRequired"));
                merchantDetailsVO.setPayout_amount_limit_check(res.getString("payout_amount_limit_check"));
                merchantDetailsVO.setChargebackNotification(res.getString("chargebackNotification"));
                merchantDetailsVO.setSuccessReconMail(res.getString("successReconMail"));
                merchantDetailsVO.setRefundReconMail(res.getString("refundReconMail"));
                merchantDetailsVO.setChargebackReconMail(res.getString("chargebackReconMail"));
                merchantDetailsVO.setPayoutReconMail(res.getString("payoutReconMail"));
                merchantDetailsVO.setIsMerchantLogoBO(res.getString("isMerchantLogoBO"));
                merchantDetailsVO.setCardExpiryDateCheck(res.getString("cardExpiryDateCheck"));
                merchantDetailsVO.setTransactionNotification(res.getString("transactionNotification"));
                merchantDetailsVO.setPayoutRouting(res.getString("payoutRouting"));
                merchantDetailsVO.setTemplate(res.getString("template"));
                merchantDetailsVO.setPartnertemplate(res.getString("partnertemplate"));
                merchantDetailsVO.setPayoutNotification(res.getString("payoutNotification"));
                merchantDetailsVO.setVpaAddressLimitCheck(res.getString("vpaAddressLimitCheck"));
                merchantDetailsVO.setVpaAddressAmountLimitCheck(res.getString("vpaAddressAmountLimitCheck"));
                merchantDetailsVO.setBankAccountLimitCheck(res.getString("payoutBankAccountNoLimitCheck"));
                merchantDetailsVO.setBankAccountAmountLimitCheck(res.getString("payoutBankAccountNoAmountLimitCheck"));
                merchantDetailsVO.setCustomerIpLimitCheck(res.getString("customerIpLimitCheck"));
                merchantDetailsVO.setCustomerIpAmountLimitCheck(res.getString("customerIpAmountLimitCheck"));
                merchantDetailsVO.setCustomerNameLimitCheck(res.getString("customerNameLimitCheck"));
                merchantDetailsVO.setCustomerNameAmountLimitCheck(res.getString("customerNameAmountLimitCheck"));
                merchantDetailsVO.setCustomerEmailLimitCheck(res.getString("customerEmailLimitCheck"));
                merchantDetailsVO.setCustomerEmailAmountLimitCheck(res.getString("customerEmailAmountLimitCheck"));
                merchantDetailsVO.setCustomerPhoneLimitCheck(res.getString("customerPhoneLimitCheck"));
                merchantDetailsVO.setCustomerPhoneAmountLimitCheck(res.getString("customerPhoneAmountLimitCheck"));
                merchantDetailsVO.setIsOTPRequired(res.getString("isOTPRequired"));
                merchantDetailsVO.setIsCardStorageRequired(res.getString("isCardStorageRequired"));
                merchantDetailsVO.setIsIgnorePaymode(res.getString("IsIgnorePaymode"));
                merchantDetailsVO.setPrivacyPolicyUrl(res.getString("privacyPolicyUrl"));
                merchantDetailsVO.setTcUrl(res.getString("termsUrl"));
                merchantDetailsVO.setPayoutPersonalInfoValidation(res.getString("payoutPersonalInfoValidation"));

            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return merchantDetailsVO;
    }

    public MerchantDetailsVO getMemberAndPartnerDetails(String merchantid,String toType) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;

        try
        {
            conn = Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            String query = "SELECT m.*,p.partnerId,p.partnerName,p.logoName,p.responseType,p.responseLength,p.template as partnertemplate, p.siteurl,p.domain as pdomain , p.telno as ptelno, p.hosturl ,p.termsUrl as pTcUrl ,p.privacyUrl as pPrivacyUrl,p.bankCardLimit,p.isMerchantRequiredForCardRegistration,mc.limitRouting,mc.marketplace,mc.isCvvStore,mc.checkoutTimer,mc.checkoutTimerTime,mc.isUniqueOrderIdRequired,mc.cardExpiryDateCheck,mc.isDomainWhitelisted,mc.customerIpLimitCheck,mc.customerIpAmountLimitCheck,mc.customerNameLimitCheck,mc.customerNameAmountLimitCheck,mc.customerEmailLimitCheck,mc.customerEmailAmountLimitCheck,mc.customerPhoneLimitCheck,mc.customerPhoneAmountLimitCheck,mc.isOTPRequired,mc.isCardStorageRequired  FROM members AS m,partners AS p,merchant_configuration AS mc WHERE m.memberid=? AND m.memberid=mc.memberid AND m.partnerId=p.partnerId AND p.partnerName=?";

            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantid);
            pstmt.setString(2,toType);
            res = pstmt.executeQuery();
            if (res.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setMitigationMail(res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof(res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof(res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name(res.getString("company_name"));
                merchantDetailsVO.setContact_persons(res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails(res.getString("contact_emails"));
                merchantDetailsVO.setAddress(res.getString("address"));
                merchantDetailsVO.setCity(res.getString("city"));
                merchantDetailsVO.setState(res.getString("state"));
                merchantDetailsVO.setZip(res.getString("zip"));
                merchantDetailsVO.setCountry(res.getString("country"));
                merchantDetailsVO.setTelNo(res.getString("telno"));
                merchantDetailsVO.setFaxNo(res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail(res.getString("notifyemail"));
                merchantDetailsVO.setBrandName(res.getString("brandname"));
                merchantDetailsVO.setSiteName(res.getString("sitename"));
                merchantDetailsVO.setReserves(res.getString("reserves"));
                merchantDetailsVO.setAptPrompt(res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer(res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo(res.getString("checksumalgo"));
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(res.getString("clkey"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCurrency(res.getString("currency"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                merchantDetailsVO.setReversalCharge(res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(merchantid);
                merchantDetailsVO.setAutoRedirect(res.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setPharma(res.getString("isPharma"));
                merchantDetailsVO.setTemplate(res.getString("template"));
                merchantDetailsVO.setPartnertemplate(res.getString("partnertemplate"));
                merchantDetailsVO.setIswhitelisted(res.getString("iswhitelisted"));
                merchantDetailsVO.setIsrefund(res.getString("isrefund"));
                merchantDetailsVO.setRefunddailylimit(res.getString("refunddailylimit"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setMerchantLogo(res.getString("ismerchantlogo"));
                merchantDetailsVO.setMerchantLogoName(res.getString("merchantlogoname"));
                merchantDetailsVO.setPartnerLogoFlag(res.getString("ispartnerlogo"));
                merchantDetailsVO.setSisaLogoFlag(res.getString("ispcilogo"));
                merchantDetailsVO.setIsRestrictedTicket(res.getString("isRestrictedTicket"));
                merchantDetailsVO.setIsTokenizationAllowed(res.getString("isTokenizationAllowed"));
                merchantDetailsVO.setIsBlacklistTransaction(res.getString("blacklistTransaction"));
                merchantDetailsVO.setAddressValidation(res.getString("addressValidation"));
                merchantDetailsVO.setFlightMode(res.getString("flightMode"));
                merchantDetailsVO.setResponseLength(res.getString("responseLength"));
                merchantDetailsVO.setResponseType(res.getString("responseType"));
                merchantDetailsVO.setPartnerName(res.getString("partnerName"));
                merchantDetailsVO.setLogoName(res.getString("logoName"));
                merchantDetailsVO.setEmailSent(res.getString("emailSent"));
                merchantDetailsVO.setOnlineFraudCheck(res.getString("onlineFraudCheck"));
                merchantDetailsVO.setSplitPaymentAllowed(res.getString("isSplitPayment"));
                merchantDetailsVO.setIsExcessCaptureAllowed(res.getString("isExcessCaptureAllowed"));
                merchantDetailsVO.setLogin(res.getString("login"));
                merchantDetailsVO.setIsBackOfficeAccess(res.getString("icici"));
                merchantDetailsVO.setContactNumber(res.getString("telno"));
                merchantDetailsVO.setIsCardEncryptionEnable(res.getString("isCardEncryptionEnable"));
                merchantDetailsVO.setIsEmailLimitEnabled(res.getString("emailLimitEnabled"));
                merchantDetailsVO.setBinService(res.getString("binService"));
                merchantDetailsVO.setPartnerSiteUrl(res.getString("siteurl"));
                merchantDetailsVO.setCountry(res.getString("country"));
                if(res.getString("domain")!=null && res.getString("pdomain")!=null)
                {
                    merchantDetailsVO.setMerchantDomain(res.getString("domain") + "," + res.getString("pdomain"));
                }
                if(!functions.isValueNull(res.getString("pdomain")) && functions.isValueNull(res.getString("domain")))
                {//partner domain null and merchant domain not null
                    merchantDetailsVO.setMerchantDomain(res.getString("domain"));
                }
                if(functions.isValueNull(res.getString("pdomain")) && !functions.isValueNull(res.getString("domain")))
                {//partner domain not null and merchant domain null
                    merchantDetailsVO.setMerchantDomain(res.getString("pdomain"));
                }
                merchantDetailsVO.setSupportSection(res.getString("supportSection"));
                merchantDetailsVO.setSupportNoNeeded(res.getString("supportNoNeeded"));
                merchantDetailsVO.setTcUrl(res.getString("termsUrl"));
                merchantDetailsVO.setPrivacyPolicyUrl(res.getString("privacyPolicyUrl"));
                merchantDetailsVO.setPartnerTcUrl(res.getString("pTcUrl"));
                merchantDetailsVO.setPartnerPrivacyUrl(res.getString("pPrivacyUrl"));
                merchantDetailsVO.setMultiCurrencySupport(res.getString("multiCurrencySupport"));
                merchantDetailsVO.setPartnerSupportContactNumber(res.getString("ptelno"));
                merchantDetailsVO.setHostUrl(res.getString("hosturl"));
                merchantDetailsVO.setBinRouting(res.getString("binRouting"));
                merchantDetailsVO.setPersonalInfoDisplay(res.getString("personal_info_display"));
                merchantDetailsVO.setPersonalInfoValidation(res.getString("personal_info_validation"));
                merchantDetailsVO.setHostedPaymentPage(res.getString("hosted_payment_page"));
                merchantDetailsVO.setVbvLogo(res.getString("vbvLogo"));
                merchantDetailsVO.setMasterSecureLogo(res.getString("masterSecureLogo"));
                merchantDetailsVO.setChargebackMailsend(res.getString("chargebackEmail"));
                merchantDetailsVO.setConsentFlag(res.getString("consent"));
                merchantDetailsVO.setSecurityLogo(res.getString("isSecurityLogo"));
                merchantDetailsVO.setMultipleRefund(res.getString("isMultipleRefund"));
                merchantDetailsVO.setBlacklistCountryIp(res.getString("blacklist_country_ip"));
                merchantDetailsVO.setBlacklistCountryBin(res.getString("blacklist_country_bin"));
                merchantDetailsVO.setBlacklistCountryBinIp(res.getString("blacklist_country_bin_ip"));
                merchantDetailsVO.setEmiSupport(res.getString("emiSupport"));
                merchantDetailsVO.setBankCardLimit(res.getString("bankCardLimit"));
                merchantDetailsVO.setIsMerchantRequiredForCardRegistration(res.getString("isMerchantRequiredForCardRegistration"));
                merchantDetailsVO.setInternalFraudCheck(res.getString("internalFraudCheck"));
                merchantDetailsVO.setCard_velocity_check(res.getString("card_velocity_check"));
                merchantDetailsVO.setMerchantOrderDetailsDisplay(res.getString("merchant_order_details"));
                merchantDetailsVO.setLimitRouting(res.getString("limitRouting"));
                merchantDetailsVO.setIsAddressDetailsRequired(res.getString("isAddrDetailsRequired"));
                merchantDetailsVO.setMarketPlace(res.getString("marketplace"));
                merchantDetailsVO.setIsCvvStore(res.getString("isCvvStore"));
                merchantDetailsVO.setCheckoutTimerFlag(res.getString("checkoutTimer"));
                merchantDetailsVO.setCheckoutTimerTime(res.getString("checkoutTimerTime"));
                merchantDetailsVO.setCardWhitelistLevel(res.getString("card_whitelist_level"));
                merchantDetailsVO.setIsUniqueOrderIdRequired(res.getString("isUniqueOrderIdRequired"));
                merchantDetailsVO.setCardExpiryDateCheck(res.getString("cardExpiryDateCheck"));
                merchantDetailsVO.setIsDomainWhitelisted(res.getString("isDomainWhitelisted"));
                merchantDetailsVO.setCustomerIpLimitCheck(res.getString("customerIpLimitCheck"));
                merchantDetailsVO.setCustomerIpAmountLimitCheck(res.getString("customerIpAmountLimitCheck"));
                merchantDetailsVO.setCustomerNameLimitCheck(res.getString("customerNameLimitCheck"));
                merchantDetailsVO.setCustomerNameAmountLimitCheck(res.getString("customerNameAmountLimitCheck"));
                merchantDetailsVO.setCustomerEmailLimitCheck(res.getString("customerEmailLimitCheck"));
                merchantDetailsVO.setCustomerEmailAmountLimitCheck(res.getString("customerEmailAmountLimitCheck"));
                merchantDetailsVO.setCustomerPhoneLimitCheck(res.getString("customerPhoneLimitCheck"));
                merchantDetailsVO.setCustomerPhoneAmountLimitCheck(res.getString("customerPhoneAmountLimitCheck"));
                merchantDetailsVO.setIsOTPRequired(res.getString("isOTPRequired"));
                merchantDetailsVO.setIsCardStorageRequired(res.getString("isCardStorageRequired"));


            }
            logger.debug("merchant Details loaded----"+pstmt);
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");

        return merchantDetailsVO;
    }

    public MerchantDetailsVO getMemberDetails(String merchantId,String agentId) throws Exception
    {
        MerchantDetailsVO merchantDetailsVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            String query = "SELECT * FROM members AS m JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE mam.memberid =? AND mam.agentid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantId);
            pstmt.setString(2,agentId);
            res = pstmt.executeQuery();
            logger.debug("Old merchant Details is loading ");
            if (res.next())
            {
                merchantDetailsVO=new MerchantDetailsVO();
                merchantDetailsVO.setMitigationMail( res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof( res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof( res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name( res.getString("company_name"));
                merchantDetailsVO.setContact_persons( res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails( res.getString("contact_emails"));
                merchantDetailsVO.setAddress( res.getString("address"));
                merchantDetailsVO.setCity( res.getString("city"));
                merchantDetailsVO.setState( res.getString("state"));
                merchantDetailsVO.setZip( res.getString("zip"));
                merchantDetailsVO.setCountry( res.getString("country"));
                merchantDetailsVO.setTelNo( res.getString("telno"));
                merchantDetailsVO.setFaxNo( res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail( res.getString("notifyemail"));
                merchantDetailsVO.setBrandName( res.getString("brandname"));
                merchantDetailsVO.setSiteName( res.getString("sitename"));
                merchantDetailsVO.setReserves( res.getString("reserves"));
                merchantDetailsVO.setAptPrompt( res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer( res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo( res.getString("checksumalgo"));
                String accountId = res.getString("accountid");
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(res.getString("clkey"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCurrency(res.getString("currency"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                if (accountId != null)
                {
                    merchantDetailsVO.setAccountId( accountId);
                }
                merchantDetailsVO.setReversalCharge( res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge( res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge( res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(res.getString("memberid"));
                merchantDetailsVO.setAgentId(res.getString("agentid"));
                merchantDetailsVO.setAutoRedirect(res.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setOnlineFraudCheck(res.getString("onlineFraudCheck"));
                merchantDetailsVO.setCard_velocity_check(res.getString("card_velocity_check"));
            }

            logger.debug("merchant Details loaded");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ", e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");

        return merchantDetailsVO;
    }
    public MerchantDetailsVO getMemberDetailsByPartnerId(String merchantId,String partnerId) throws Exception
    {
        MerchantDetailsVO merchantDetailsVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            String query = "select * from members where memberid =? and partnerid=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,merchantId);
            pstmt.setString(2,partnerId);
            res = pstmt.executeQuery();
            logger.debug("Old merchant Details is loading---- "+pstmt);
            if (res.next())
            {
                merchantDetailsVO=new MerchantDetailsVO();
                merchantDetailsVO.setMitigationMail( res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof( res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof( res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name( res.getString("company_name"));
                merchantDetailsVO.setContact_persons( res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails( res.getString("contact_emails"));
                merchantDetailsVO.setAddress( res.getString("address"));
                merchantDetailsVO.setCity( res.getString("city"));
                merchantDetailsVO.setState( res.getString("state"));
                merchantDetailsVO.setZip( res.getString("zip"));
                merchantDetailsVO.setCountry( res.getString("country"));
                merchantDetailsVO.setTelNo( res.getString("telno"));
                merchantDetailsVO.setFaxNo( res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail( res.getString("notifyemail"));
                merchantDetailsVO.setBrandName( res.getString("brandname"));
                merchantDetailsVO.setSiteName( res.getString("sitename"));
                merchantDetailsVO.setReserves( res.getString("reserves"));
                merchantDetailsVO.setAptPrompt( res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer( res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo( res.getString("checksumalgo"));
                String accountId = res.getString("accountid");
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(res.getString("clkey"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCurrency(res.getString("currency"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                if (accountId != null)
                {
                    merchantDetailsVO.setAccountId( accountId);
                }
                merchantDetailsVO.setReversalCharge( res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(res.getString("memberid"));
                merchantDetailsVO.setAgentId(res.getString("agentid"));
                merchantDetailsVO.setAutoRedirect(res.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setIsCardEncryptionEnable(res.getString("isCardEncryptionEnable"));
                merchantDetailsVO.setActionExecutorId(res.getString("actionExecutorId"));
                merchantDetailsVO.setActionExecutorName(res.getString("actionExecutorName"));

            }
            logger.debug("merchant Details loaded----");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");

        return merchantDetailsVO;
    }
    public List<MerchantDetailsVO> getAllMemberDetails() throws Exception
    {
        List<MerchantDetailsVO> merchantDetailsVOList = new ArrayList<MerchantDetailsVO>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            String query = "select * from members order by memberid";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            logger.debug("Old merchant Details is loading ");
            while (res.next())
            {
                MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();
                merchantDetailsVO.setMitigationMail( res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof( res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof( res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name( res.getString("company_name"));
                merchantDetailsVO.setContact_persons( res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails( res.getString("contact_emails"));
                merchantDetailsVO.setAddress( res.getString("address"));
                merchantDetailsVO.setCity( res.getString("city"));
                merchantDetailsVO.setState( res.getString("state"));
                merchantDetailsVO.setZip( res.getString("zip"));
                merchantDetailsVO.setCountry( res.getString("country"));
                merchantDetailsVO.setTelNo( res.getString("telno"));
                merchantDetailsVO.setFaxNo( res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail( res.getString("notifyemail"));
                merchantDetailsVO.setBrandName( res.getString("brandname"));
                merchantDetailsVO.setSiteName( res.getString("sitename"));
                merchantDetailsVO.setReserves( res.getString("reserves"));
                merchantDetailsVO.setAptPrompt( res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer( res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo( res.getString("checksumalgo"));
                String accountId = res.getString("accountid");
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(res.getString("clkey"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                if (accountId != null)
                {
                    merchantDetailsVO.setAccountId( accountId);
                }
                merchantDetailsVO.setReversalCharge( res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(res.getString("memberid"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setOnlineFraudCheck(res.getString("onlineFraudCheck"));
                merchantDetailsVOList.add(merchantDetailsVO);
            }

            logger.debug("merchant Details loaded");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");
        return merchantDetailsVOList;
    }

    public void getPartnerDetails(String partnerId,MerchantDetailsVO merchantDetailsVO)
    {
        Connection connection = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT partnerName,logoName FROM partners WHERE partnerId = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1,partnerId);
            rs = ps.executeQuery();
            if (rs.next())
            {
                merchantDetailsVO.setPartnerName(rs.getString("partnerName"));
                merchantDetailsVO.setLogoName(rs.getString("logoName"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in getPartnerDetails",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException in getPartnerDetails",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(connection);
        }
    }
    public List<MemberAccountMappingVO> getMemberAccountMappingDetails(TerminalVO terminalVo) throws PZDBViolationException
    {
        int counter=1;
        Connection connection = null;
        PreparedStatement psmemberAccountMappingDetails=null;
        ResultSet rsmemberAccountMappingDetails=null;
        List<MemberAccountMappingVO> memberAccountMappingVOList = new ArrayList<MemberAccountMappingVO>();
        try
        {
            connection=Database.getRDBConnection();
            StringBuilder query = new StringBuilder("Select * from member_account_mapping where  ");
            if(functions.isValueNull(terminalVo.getMemberId()))
            {
                query.append("  memberid =?");
                logger.debug(" memberId=="+terminalVo.getMemberId());
            }
            if(functions.isValueNull(terminalVo.getTerminalId()))
            {
                query.append(" and terminalid =?");
                logger.debug(" terminalid=="+terminalVo.getTerminalId());
            }
            if(functions.isValueNull(terminalVo.getAccountId()))
            {
                query.append(" and accountid =?");
                logger.debug(" accountid=="+terminalVo.getAccountId());
            }
            if(functions.isValueNull(terminalVo.getCardTypeId()))
            {
                query.append(" and cardtypeid =?");
                logger.debug(" cardtypeid=="+terminalVo.getCardTypeId());
            }
            if(functions.isValueNull(terminalVo.getPaymodeId()))
            {
                query.append(" and paymodeid =?");
                logger.debug(" paymodeid=="+terminalVo.getPaymodeId());
            }
            logger.debug("Query :::"+query.toString());
            psmemberAccountMappingDetails = connection.prepareStatement(query.toString());
            if(functions.isValueNull(terminalVo.getMemberId()))
            {
                psmemberAccountMappingDetails.setString(counter, terminalVo.getMemberId());
                counter++;
            }
            if(functions.isValueNull(terminalVo.getTerminalId()))
            {
                psmemberAccountMappingDetails.setString(counter,terminalVo.getTerminalId());
                counter++;
            }
            if(functions.isValueNull(terminalVo.getAccountId()))
            {
                psmemberAccountMappingDetails.setString(counter,terminalVo.getAccountId());
                counter++;
            }
            if(functions.isValueNull(terminalVo.getCardTypeId()))
            {
                psmemberAccountMappingDetails.setString(counter,terminalVo.getCardTypeId());
                counter++;
            }
            if(functions.isValueNull(terminalVo.getPaymodeId()))
            {
                psmemberAccountMappingDetails.setString(counter,terminalVo.getPaymodeId());
                counter++;
            }
            rsmemberAccountMappingDetails = psmemberAccountMappingDetails.executeQuery();
            while(rsmemberAccountMappingDetails.next())
            {
                MemberAccountMappingVO memberAccountMappingVO = new MemberAccountMappingVO();
                memberAccountMappingVO.setMemberid(rsmemberAccountMappingDetails.getString("memberid"));
                memberAccountMappingVO.setAccountid(rsmemberAccountMappingDetails.getString("accountid"));
                memberAccountMappingVO.setPaymodeid(rsmemberAccountMappingDetails.getString("paymodeid"));
                memberAccountMappingVO.setCardtypeid(rsmemberAccountMappingDetails.getString("cardtypeid"));
                memberAccountMappingVO.setTerminalid(rsmemberAccountMappingDetails.getString("terminalid"));
                memberAccountMappingVO.setActive(rsmemberAccountMappingDetails.getString("isActive"));
                memberAccountMappingVO.setPriority(rsmemberAccountMappingDetails.getString("priority"));
                memberAccountMappingVO.setTest(rsmemberAccountMappingDetails.getString("isTest"));
                memberAccountMappingVO.setTaxper(rsmemberAccountMappingDetails.getInt("taxper"));
                memberAccountMappingVO.setReversalcharge(rsmemberAccountMappingDetails.getInt("reversalcharge"));
                memberAccountMappingVO.setWithdrawalcharge(rsmemberAccountMappingDetails.getInt("withdrawalcharge"));
                memberAccountMappingVO.setChargebackcharge(rsmemberAccountMappingDetails.getInt("chargebackcharge"));
                memberAccountMappingVO.setReservePercentage(rsmemberAccountMappingDetails.getInt("reservePercentage"));
                memberAccountMappingVO.setFraudVerificationCharge(rsmemberAccountMappingDetails.getInt("fraudVerificationCharge"));
                memberAccountMappingVO.setAnnualCharge(rsmemberAccountMappingDetails.getInt("annualCharge"));
                memberAccountMappingVO.setSetupCharge(rsmemberAccountMappingDetails.getInt("setupCharge"));
                memberAccountMappingVO.setFxClearanceChargePercentage(rsmemberAccountMappingDetails.getInt("fxClearanceChargePercentage"));
                memberAccountMappingVO.setMonthlyGatewayCharge(rsmemberAccountMappingDetails.getInt("monthlyGatewayCharge"));
                memberAccountMappingVO.setMonthlyAccountMntCharge(rsmemberAccountMappingDetails.getInt("monthlyAccountMntCharge"));
                memberAccountMappingVO.setReportCharge(rsmemberAccountMappingDetails.getInt("reportCharge"));
                memberAccountMappingVO.setFraudulentCharge(rsmemberAccountMappingDetails.getInt("fraudulentCharge"));
                memberAccountMappingVO.setAutoRepresentationCharge(rsmemberAccountMappingDetails.getInt("autoRepresentationCharge"));
                memberAccountMappingVO.setInterchangePlusCharge(rsmemberAccountMappingDetails.getDouble("interchangePlusCharge"));
                memberAccountMappingVO.setDaily_card_amount_limit(rsmemberAccountMappingDetails.getDouble("daily_card_amount_limit"));
                memberAccountMappingVO.setWeekly_card_amount_limit(rsmemberAccountMappingDetails.getDouble("weekly_card_amount_limit"));
                memberAccountMappingVO.setMonthly_card_amount_limit(rsmemberAccountMappingDetails.getDouble("monthly_card_amount_limit"));
                memberAccountMappingVO.setDaily_amount_limit(rsmemberAccountMappingDetails.getDouble("daily_amount_limit"));
                memberAccountMappingVO.setMonthly_amount_limit(rsmemberAccountMappingDetails.getDouble("monthly_amount_limit"));
                memberAccountMappingVO.setDaily_card_limit(rsmemberAccountMappingDetails.getDouble("daily_card_limit"));
                memberAccountMappingVO.setWeekly_card_limit(rsmemberAccountMappingDetails.getDouble("weekly_card_limit"));
                memberAccountMappingVO.setMonthly_card_limit(rsmemberAccountMappingDetails.getDouble("monthly_card_limit"));
                memberAccountMappingVO.setChargePercentage(rsmemberAccountMappingDetails.getDouble("chargePercentage"));
                memberAccountMappingVO.setFixApprovalCharge(rsmemberAccountMappingDetails.getDouble("fixApprovalCharge"));
                memberAccountMappingVO.setFixDeclinedCharge(rsmemberAccountMappingDetails.getDouble("fixDeclinedCharge"));
                memberAccountMappingVO.setMin_transaction_amount(rsmemberAccountMappingDetails.getDouble("min_transaction_amount"));
                memberAccountMappingVO.setMax_transaction_amount(rsmemberAccountMappingDetails.getDouble("max_transaction_amount"));
                memberAccountMappingVO.setWeekly_amount_limit(rsmemberAccountMappingDetails.getDouble("weekly_amount_limit"));
                memberAccountMappingVO.setDaily_avg_ticket(rsmemberAccountMappingDetails.getDouble("daily_avg_ticket"));
                memberAccountMappingVO.setWeekly_avg_ticket(rsmemberAccountMappingDetails.getDouble("weekly_avg_ticket"));
                memberAccountMappingVO.setMonthly_avg_ticket(rsmemberAccountMappingDetails.getDouble("monthly_avg_ticket"));
                memberAccountMappingVO.setMonthly_avg_ticket(rsmemberAccountMappingDetails.getDouble("monthly_avg_ticket"));
                memberAccountMappingVO.setRiskRuleActivation(rsmemberAccountMappingDetails.getString("riskruleactivation"));
                memberAccountMappingVO.setMin_payout_amount(rsmemberAccountMappingDetails.getDouble("min_payout_amount"));
                memberAccountMappingVO.setSettlementCurrency(rsmemberAccountMappingDetails.getString("settlement_currency"));
                memberAccountMappingVO.setIsCardWhitelisted(rsmemberAccountMappingDetails.getString("isCardWhitelisted"));
                memberAccountMappingVO.setIsEmailWhitelisted(rsmemberAccountMappingDetails.getString("isEmailWhitelisted"));
                memberAccountMappingVO.setIsTokenizationAllowed(rsmemberAccountMappingDetails.getString("isTokenizationActive"));
                memberAccountMappingVO.setAddressDetails(rsmemberAccountMappingDetails.getString("addressDetails"));
                memberAccountMappingVO.setAddressValidation(rsmemberAccountMappingDetails.getString("addressValidation"));
                memberAccountMappingVO.setBinRouting(rsmemberAccountMappingDetails.getString("binRouting"));
                memberAccountMappingVO.setEmiSupport(rsmemberAccountMappingDetails.getString("emi_support"));
                memberAccountMappingVOList.add(memberAccountMappingVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in getMemberAccountMappingDetails",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberAccountMappingDetails()", null, "Common", "System Error while connecting to the member_account_mapping table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception in getMemberAccountMappingDetails",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberAccountMappingDetails()", null, "Common", "Sql exception due tyo incorrect query to the member_account_mapping table::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rsmemberAccountMappingDetails);
            Database.closePreparedStatement(psmemberAccountMappingDetails);
            Database.closeConnection(connection);
        }
        return memberAccountMappingVOList;
    }

    public void updateMerchantWire(MerchantWireVO merchantWireVO)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        try
        {
            conn = Database.getConnection();
            String query1 = "update merchant_wiremanager set settledate=? ,amount=? ,balanceamount=? ,netfinalamount=?,status=?,unpaidamount=? where settledid=? ";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, merchantWireVO.getSettleDate());
            pstmt.setDouble(2, merchantWireVO.getAmount());
            pstmt.setDouble(3, merchantWireVO.getBalanceAmount());
            pstmt.setDouble(4, merchantWireVO.getNetFinalAmount());
            pstmt.setString(5, merchantWireVO.getStatus());
            pstmt.setDouble(6, merchantWireVO.getUnpaidAmount());
           /* pstmt.setString(7, merchantWireVO.getPayoutCurrency());
            pstmt.setDouble(8, merchantWireVO.getPayoutAmount());
            pstmt.setString(9, merchantWireVO.getPayerBankDetails());
            pstmt.setString(10, merchantWireVO.getReceiverBankDetails());
            pstmt.setString(11, merchantWireVO.getPaymentConfirmation());
            pstmt.setString(12, merchantWireVO.getPaymentReceiptDate());
            pstmt.setString(13, merchantWireVO.getRemark());
            pstmt.setString(14, merchantWireVO.getConversionRate());*/
            pstmt.setString(7, merchantWireVO.getSettledId());
            logger.error("pstmt Update------->"+pstmt);
            int k=pstmt.executeUpdate();
            if (k == 1)
            {
                merchantWireVO.setUpdated(true);
            }
            else
            {
                merchantWireVO.setUpdated(false);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::" + systemError.getMessage());
        }
        catch (SQLException se)
        {
            logger.error("SQLException:::::" + se.getMessage());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }

    public boolean updateMerchantWireForAllTerminal(MerchantWireVO merchantWireVO)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        boolean result=false;
        try
        {
            conn = Database.getConnection();
            String query1 = "update merchant_wiremanager set settledate=? ,amount=? ,balanceamount=? ,netfinalamount=?,status=?,unpaidamount=? where toid=? AND accountid=?";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1, merchantWireVO.getSettleDate());
            pstmt.setDouble(2, merchantWireVO.getAmount());
            pstmt.setDouble(3, merchantWireVO.getBalanceAmount());
            pstmt.setDouble(4, merchantWireVO.getNetFinalAmount());
            pstmt.setString(5, merchantWireVO.getStatus());
            pstmt.setDouble(6, merchantWireVO.getUnpaidAmount());
            /*pstmt.setString(7, merchantWireVO.getPayoutCurrency());
            pstmt.setDouble(8, merchantWireVO.getPayoutAmount());
            pstmt.setString(9, merchantWireVO.getPayerBankDetails());
            pstmt.setString(10, merchantWireVO.getReceiverBankDetails());
            pstmt.setString(11, merchantWireVO.getPaymentConfirmation());
            pstmt.setString(12, merchantWireVO.getPaymentReceiptDate());
            pstmt.setString(13, merchantWireVO.getRemark());
            pstmt.setString(14, merchantWireVO.getConversionRate());
            */
            pstmt.setString(7, merchantWireVO.getMemberId());
            pstmt.setString(8, merchantWireVO.getAccountId());
            //System.out.println("pstmt for updateAll------->"+pstmt);
            int k=pstmt.executeUpdate();
            if (k > 1)
            {
                result=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::" , systemError);
        }
        catch (SQLException se)
        {
            logger.error("SQLException:::::" , se);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }


    public Set<MerchantDetailsVO> getPartnerMembers(String partnerId)
    {
        Set<MerchantDetailsVO> merchantDetailsVOSet = new HashSet<MerchantDetailsVO>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs =null;
        String query=null;
        try
        {
            conn = Database.getRDBConnection();
            query= "select * from members  where partnerid=? and activation=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,partnerId);
            pstmt.setString(2, "Y");
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();
                merchantDetailsVO.setCompany_name( rs.getString("company_name"));
                merchantDetailsVO.setContact_persons( rs.getString("contact_persons"));
                merchantDetailsVO.setContact_emails( rs.getString("contact_emails"));
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVOSet.add(merchantDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::"+systemError);
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return merchantDetailsVOSet;
    }

    public Set<MerchantDetailsVO> getAgentMembers(String agentId)
    {
        Set<MerchantDetailsVO> merchantDetailsVOSet = new HashSet<MerchantDetailsVO>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs =null;
        String query=null;
        try
        {
            conn = Database.getRDBConnection();
            query= "SELECT mam.memberid,company_name,contact_persons,contact_emails FROM members AS m JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE mam.agentid=? and m.activation=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1,agentId);
            pstmt.setString(2,"Y");
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();
                merchantDetailsVO.setCompany_name( rs.getString("company_name"));
                merchantDetailsVO.setContact_persons( rs.getString("contact_persons"));
                merchantDetailsVO.setContact_emails( rs.getString("contact_emails"));
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVOSet.add(merchantDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::::::"+systemError);
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return merchantDetailsVOSet;
    }
    public boolean isTokenizationAllowed(String memberId)throws PZDBViolationException
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet = null;
        boolean isTokenizationAllowed=false;
        try
        {
            conn = Database.getRDBConnection();
            String query1 = "select * from members where memberid=? and isTokenizationAllowed=?";
            pstmt = conn.prepareStatement(query1);
            pstmt.setString(1,memberId);
            pstmt.setString(2,"Y");
            resultSet=pstmt.executeQuery();
            if (resultSet.next())
            {
                isTokenizationAllowed=true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error in isTokenizationAllowed",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "isTokenizationAllowed()", null, "Common", "System Error while connecting to the member_account_mapping table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception in isTokenizationAllowed",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "isTokenizationAllowed()", null, "Common", "Sql exception due tyo incorrect query to the member_account_mapping table::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return isTokenizationAllowed;

    }
    public MerchantFraudAccountVO getMerchantFraudServiceConfigurationDetails (String memberId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        MerchantFraudAccountVO merchantFraudAccountVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT merchantfraudserviceid,memberid,fssubaccountid,isactive,isVisible,FROM_UNIXTIME(dtstamp) ").append("AS creationon,TIMESTAMP as lastupdated FROM merchant_fssubaccount_mappping WHERE memberid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                merchantFraudAccountVO=new MerchantFraudAccountVO();
                merchantFraudAccountVO.setMerchantFraudAccountId(rs.getString("merchantfraudserviceid"));
                merchantFraudAccountVO.setMemberId(rs.getString("memberid"));
                merchantFraudAccountVO.setFsSubAccountId(rs.getString("fssubaccountid"));
                merchantFraudAccountVO.setIsActive(rs.getString("isactive"));
                merchantFraudAccountVO.setCreationOn(rs.getString("creationon"));
                merchantFraudAccountVO.setLastUpdated(rs.getString("lastupdated"));
                merchantFraudAccountVO.setIsVisible(rs.getString("isvisible"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting merchant fraud account details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantFraudServiceConfigurationDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting merchant fraud account details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantFraudServiceConfigurationDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantFraudAccountVO;
    }

    public FraudAccountDetailsVO getFraudAccountDetails(String memberId)
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        FraudAccountDetailsVO accountDetailsVO=new FraudAccountDetailsVO();
        try
        {
            con= Database.getRDBConnection();
            StringBuffer query =new StringBuffer("SELECT mfsm.memberid,mfsm.isonlinefraudcheck,fsam.accountname,fsam.password,fsam.isTest,fsam.fsid,fsasm.subaccountname,fsasm.subusername,fsasm.subpwd FROM merchant_fssubaccount_mappping AS mfsm JOIN fsaccount_subaccount_mapping AS fsasm  ON mfsm.fssubaccountid=fsasm.fssubaccountid JOIN fraudsystem_account_mapping AS fsam ON fsasm.fsaccountid=fsam.fsaccountid AND mfsm.memberid=? ");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                accountDetailsVO.setFraudSystemMerchantId(rs.getString("accountname"));
                accountDetailsVO.setPassword(rs.getString("password"));
                accountDetailsVO.setSubMerchantId(rs.getString("subaccountname"));
                accountDetailsVO.setUserName(rs.getString("subusername"));
                accountDetailsVO.setUserNumber(rs.getString("subpwd"));
                accountDetailsVO.setIsTest(rs.getString("isTest"));
                accountDetailsVO.setFraudSystemId(rs.getString("fsid"));
                accountDetailsVO.setIsOnlineFraudCheck(rs.getString("isonlinefraudcheck"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting  fraud account details", e);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getFraudAccountDetails()", null, "Common", "Sql exception while connecting to fraudsystem_account_mapping table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting  fraud account details::", systemError);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getFraudAccountDetails()", null, "Common", "Sql exception while connecting to fraudsystem_account_mapping table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return accountDetailsVO;
    }

    /**
     * Get all the members for taht partner
     * @param partnerId
     * @return
     */
    public Map<String,MerchantDetailsVO> getALLMerchantForPartner(String partnerId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement psGetAllMember= null;
        ResultSet rs=null;
        MerchantDetailsVO merchantDetailsVO=null;
        Map<String,MerchantDetailsVO> merchantDetailsVOMap = new HashMap<String, MerchantDetailsVO>();
        try
        {
            con =Database.getRDBConnection();
            String query="Select m.*,p.partnerName,p.logoName from members as m join partners as p on m.partnerId=p.partnerId where p.partnerId=?";
            psGetAllMember=con.prepareStatement(query);
            psGetAllMember.setString(1,partnerId);
            rs=psGetAllMember.executeQuery();
            logger.debug("query from getAllMerchantForPartner()----"+psGetAllMember);
            while(rs.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setMitigationMail(rs.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof(rs.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof(rs.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name(rs.getString("company_name"));
                merchantDetailsVO.setContact_persons(rs.getString("contact_persons"));
                merchantDetailsVO.setContact_emails(rs.getString("contact_emails"));
                merchantDetailsVO.setAddress(rs.getString("address"));
                merchantDetailsVO.setCity(rs.getString("city"));
                merchantDetailsVO.setState(rs.getString("state"));
                merchantDetailsVO.setZip(rs.getString("zip"));
                merchantDetailsVO.setCountry(rs.getString("country"));
                merchantDetailsVO.setTelNo(rs.getString("telno"));
                merchantDetailsVO.setFaxNo(rs.getString("faxno"));
                merchantDetailsVO.setNotifyEmail(rs.getString("notifyemail"));
                merchantDetailsVO.setBrandName(rs.getString("brandname"));
                merchantDetailsVO.setSiteName(rs.getString("sitename"));
                merchantDetailsVO.setReserves(rs.getString("reserves"));
                merchantDetailsVO.setAptPrompt(rs.getString("aptprompt"));
                merchantDetailsVO.setTaxPer(rs.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo(rs.getString("checksumalgo"));
                //String accountId = res.getString("accountid");
                merchantDetailsVO.setAutoSelectTerminal(rs.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(rs.getString("isIpWhitelisted"));
                merchantDetailsVO.setKey(rs.getString("clkey"));
                merchantDetailsVO.setActivation(rs.getString("activation"));
                merchantDetailsVO.setCheckLimit(rs.getString("check_limit"));
                merchantDetailsVO.setService(rs.getString("isservice"));
                merchantDetailsVO.setChargePer(rs.getString("chargeper"));
                merchantDetailsVO.setFixamount(rs.getString("fixamount"));
                merchantDetailsVO.setCurrency(rs.getString("currency"));
                merchantDetailsVO.setCustReminderMail(rs.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(rs.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(rs.getString("card_check_limit"));
                merchantDetailsVO.setReversalCharge(rs.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(rs.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(rs.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(rs.getString("memberid"));
                merchantDetailsVO.setAutoRedirect(rs.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(rs.getString("isPoweredBy"));
                merchantDetailsVO.setPartnerId(rs.getString("partnerId"));
                merchantDetailsVO.setPharma(rs.getString("isPharma"));
                merchantDetailsVO.setIswhitelisted(rs.getString("iswhitelisted"));
                merchantDetailsVO.setIsrefund(rs.getString("isrefund"));
                merchantDetailsVO.setRefunddailylimit(rs.getString("refunddailylimit"));
                merchantDetailsVO.setFraudMaxScoreAllowed(rs.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(rs.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setMerchantLogo(rs.getString("ismerchantlogo"));
                merchantDetailsVO.setMerchantLogoName(rs.getString("merchantlogoname"));
                merchantDetailsVO.setPartnerLogoFlag(rs.getString("ispartnerlogo"));
                merchantDetailsVO.setSisaLogoFlag(rs.getString("ispcilogo"));
                merchantDetailsVO.setAddressValidation(rs.getString("addressValidation"));
                //not allowed in dao to make the call to  Dao method
                getPartnerDetails(rs.getString("partnerId"),merchantDetailsVO);
                merchantDetailsVO.setIsRestrictedTicket(rs.getString("isRestrictedTicket"));
                merchantDetailsVO.setIsTokenizationAllowed(rs.getString("isTokenizationAllowed"));
                merchantDetailsVO.setIsBlacklistTransaction(rs.getString("blacklistTransaction"));
                merchantDetailsVO.setPartnerName(rs.getString("partnerName"));
                merchantDetailsVO.setLogoName(rs.getString("logoName"));
                merchantDetailsVOMap.put(merchantDetailsVO.getMemberId(),merchantDetailsVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting  Memeber details",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getALLMerchantForPartner()", null, "Common", "Sql exception while connecting to members table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting  Member details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getALLMerchantForPartner()", null, "Common", "Sql exception while connecting to members table", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psGetAllMember);
            Database.closeConnection(con);
        }
        return merchantDetailsVOMap;
    }

    //User Profile Partner
    public MerchantDetailsVO getALLUserMerchantForPartner(String partnerId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement psGetAllMember= null;
        ResultSet rs=null;
        MerchantDetailsVO merchantDetailsVO=null;
        Map<String,MerchantDetailsVO> merchantDetailsVOMap = new HashMap<String, MerchantDetailsVO>();
        try
        {
            con =Database.getRDBConnection();
            String query="SELECT * FROM partners WHERE partnerId=?";
            psGetAllMember=con.prepareStatement(query);
            psGetAllMember.setString(1,partnerId);
            rs=psGetAllMember.executeQuery();
            logger.debug("query from getAllMerchantForPartner()----"+psGetAllMember);
            while(rs.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setKey(rs.getString("clkey"));
                merchantDetailsVO.setAutoRedirect(rs.getString("autoRedirect"));
                merchantDetailsVO.setAddressValidation(rs.getString("addressvalidation"));
                merchantDetailsVO.setAddressDeatails(rs.getString("addressdetaildisplay"));
                merchantDetailsVO.setCompany_name(rs.getString("partnerName"));
                merchantDetailsVO.setContact_emails(rs.getString("contact_emails"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SQL Exception while getting  Memeber details",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getALLMerchantForPartner()", null, "Common", "Sql exception while connecting to members table" , PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting  Member details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getALLMerchantForPartner()", null, "Common", "Sql exception while connecting to members table" , PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(psGetAllMember);
            Database.closeConnection(con);
        }
        return merchantDetailsVO;
    }

    public List<MerchantWireVO> getAgentMerchantWireReport(String agentid, String terminalid, InputDateVO dateVO, String memberid,String status, PaginationVO paginationVO) throws PZDBViolationException
    {
        MerchantWireVO merchantWireVO = null;
        List<MerchantWireVO> merchantWireVOList = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer qry = new StringBuffer("SELECT settledid,settledate,firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,currency,status,settlementreportfilepath,settledtransactionfilepath,markedfordeletion,mw.TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate' FROM merchant_wiremanager AS mw JOIN merchant_agent_mapping AS mm ON mw.toid=mm.memberid WHERE markedfordeletion='N' AND agentid=?");
            StringBuffer countQry = new StringBuffer("SELECT count(*) FROM merchant_wiremanager AS mw JOIN merchant_agent_mapping AS mm ON mw.toid=mm.memberid WHERE markedfordeletion='N' AND agentid=?");
            if(functions.isValueNull(dateVO.getFdtstamp()))
            {
                qry.append(" AND wirecreationtime >= "+dateVO.getFdtstamp());
                countQry.append(" AND wirecreationtime >= "+dateVO.getFdtstamp());
            }
            if(functions.isValueNull(dateVO.getTdtstamp()))
            {
                qry.append(" AND wirecreationtime<= "+dateVO.getTdtstamp());
                countQry.append(" AND wirecreationtime<= "+dateVO.getTdtstamp());
            }
            if(functions.isValueNull(memberid))
            {
                qry.append(" AND toid='"+memberid+"'");
                countQry.append(" AND toid='"+memberid+"'");
            }
            if(functions.isValueNull(terminalid))
            {
                qry.append(" AND terminalid='"+terminalid+"'");
                countQry.append(" AND terminalid='"+terminalid+"'");
            }
            if(functions.isValueNull(status))
            {
                qry.append(" AND status='"+status+"'");
                countQry.append(" AND status='"+status+"'");
            }
            qry.append(" ORDER BY toid DESC");
            ps = conn.prepareStatement(qry.toString());
            ps.setString(1,agentid);
            logger.debug("AgentMerchantWireReport QUERY------------->" + ps);
            rs = ps.executeQuery();
            while (rs.next())
            {
                merchantWireVO = new MerchantWireVO();
                merchantWireVO.setSettledId(rs.getString("settledid"));
                merchantWireVO.setMemberId(rs.getString("toid"));
                merchantWireVO.setAccountId(rs.getString("accountid"));
                merchantWireVO.setTerminalId(rs.getString("terminalid"));
                merchantWireVO.setNetFinalAmount(rs.getDouble("netfinalamount"));
                merchantWireVO.setUnpaidAmount(rs.getDouble("unpaidamount"));
                merchantWireVO.setCurrency(rs.getString("currency"));
                merchantWireVO.setStatus(rs.getString("status"));
                merchantWireVO.setSettlementStartDate(rs.getString("firstdate"));
                merchantWireVO.setSettlementEndDate(rs.getString("lastdate"));
                merchantWireVO.setWireCreationDate(rs.getString("wirecreationdate"));
                merchantWireVO.setSettleDate(rs.getString("settledate"));
                merchantWireVO.setReportFileName(rs.getString("settlementreportfilepath"));
                merchantWireVOList.add(merchantWireVO);
            }
            ps = conn.prepareStatement(countQry.toString());
            ps.setString(1,agentid);
            rs = ps.executeQuery();
            if(rs.next())
            {
                paginationVO.setTotalRecords(rs.getInt(1));
            }
        }
        catch (SystemError e)
        {
            logger.error("SystemError while getting  merchant wire details", e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getAgentMerchantWireReport()", null, "Common", "Sql exception while connecting to merchant_wiremanager,members,merchant_agent_mapping tables", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getting  merchant wire details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getAgentMerchantWireReport()", null, "Common", "Sql exception while connecting to merchant_wiremanager,members,merchant_agent_mapping tables", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return merchantWireVOList;
    }

    public MerchantDetailsVO getMemberDetailsByLogin(String login) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet res           = null;
        try
        {
            conn            = Database.getRDBConnection();
            logger.debug("Entering getMember Details");
            String query    = "SELECT m.*,p.partnerName,p.logoName,p.partnerId,mc.isVirtualCheckoutAllowed,mc.isMobileAllowedForVC,mc.isEmailAllowedForVC FROM members AS m,partners AS p,merchant_configuration AS mc WHERE m.login=? AND m.partnerId=p.partnerId AND m.memberId=mc.memberId";
            pstmt           = conn.prepareStatement(query);
            pstmt.setString(1,login);
            res = pstmt.executeQuery();
            logger.debug("Old merchant Details is loading ");
            if (res.next())
            {
                merchantDetailsVO.setMitigationMail(res.getString("mitigationmail"));
                merchantDetailsVO.setHrAlertProof(res.getString("hralertproof"));
                merchantDetailsVO.setDataMismatchProof(res.getString("datamismatchproof"));
                merchantDetailsVO.setCompany_name(res.getString("company_name"));
                merchantDetailsVO.setContact_persons(res.getString("contact_persons"));
                merchantDetailsVO.setContact_emails(res.getString("contact_emails"));
                merchantDetailsVO.setAddress(res.getString("address"));
                merchantDetailsVO.setCity(res.getString("city"));
                merchantDetailsVO.setState(res.getString("state"));
                merchantDetailsVO.setZip(res.getString("zip"));
                merchantDetailsVO.setCountry(res.getString("country"));
                merchantDetailsVO.setTelNo(res.getString("telno"));
                merchantDetailsVO.setFaxNo(res.getString("faxno"));
                merchantDetailsVO.setNotifyEmail(res.getString("notifyemail"));
                merchantDetailsVO.setBrandName(res.getString("brandname"));
                merchantDetailsVO.setSiteName(res.getString("sitename"));
                merchantDetailsVO.setReserves(res.getString("reserves"));
                merchantDetailsVO.setAptPrompt(res.getString("aptprompt"));
                merchantDetailsVO.setTaxPer(res.getString("taxper"));
                merchantDetailsVO.setChecksumAlgo(res.getString("checksumalgo"));
                merchantDetailsVO.setAutoSelectTerminal(res.getString("autoSelectTerminal"));
                merchantDetailsVO.setIpWhiteListed(res.getString("isIpWhitelisted"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setCheckLimit(res.getString("check_limit"));
                merchantDetailsVO.setService(res.getString("isservice"));
                merchantDetailsVO.setChargePer(res.getString("chargeper"));
                merchantDetailsVO.setFixamount(res.getString("fixamount"));
                merchantDetailsVO.setCurrency(res.getString("currency"));
                merchantDetailsVO.setCustReminderMail(res.getString("custremindermail"));
                merchantDetailsVO.setCardTransLimit(res.getString("card_transaction_limit"));
                merchantDetailsVO.setCardCheckLimit(res.getString("card_check_limit"));
                merchantDetailsVO.setReversalCharge(res.getString("reversalcharge"));
                merchantDetailsVO.setWithdrawalCharge(res.getString("withdrawalcharge"));
                merchantDetailsVO.setChargeBackCharge(res.getString("chargebackcharge"));
                merchantDetailsVO.setMemberId(res.getString("memberid"));
                merchantDetailsVO.setAutoRedirect(res.getString("autoredirect"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setPharma(res.getString("isPharma"));
                merchantDetailsVO.setIswhitelisted(res.getString("iswhitelisted"));
                merchantDetailsVO.setIpWhitelistInvoice(res.getString("ip_whitelist_invoice"));
                merchantDetailsVO.setIsrefund(res.getString("isrefund"));
                merchantDetailsVO.setRefunddailylimit(res.getString("refunddailylimit"));
                merchantDetailsVO.setFraudMaxScoreAllowed(res.getDouble("maxScoreAllowed"));
                merchantDetailsVO.setFraudAutoReversalScore(res.getDouble("maxScoreAutoReversal"));
                merchantDetailsVO.setMerchantLogo(res.getString("ismerchantlogo"));
                merchantDetailsVO.setMerchantLogoName(res.getString("merchantlogoname"));
                merchantDetailsVO.setPartnerLogoFlag(res.getString("ispartnerlogo"));
                merchantDetailsVO.setSisaLogoFlag(res.getString("ispcilogo"));
                merchantDetailsVO.setIsRestrictedTicket(res.getString("isRestrictedTicket"));
                merchantDetailsVO.setIsTokenizationAllowed(res.getString("isTokenizationAllowed"));
                merchantDetailsVO.setIsBlacklistTransaction(res.getString("blacklistTransaction"));
                merchantDetailsVO.setIsAddressDetailsRequired(res.getString("isAddrDetailsRequired"));
                merchantDetailsVO.setPartnerName(res.getString("partnerName"));
                merchantDetailsVO.setLogoName(res.getString("logoName"));
                merchantDetailsVO.setFlightMode(res.getString("flightMode"));
                merchantDetailsVO.setIsExcessCaptureAllowed(res.getString("isExcessCaptureAllowed"));
                merchantDetailsVO.setOnlineFraudCheck(res.getString("onlineFraudCheck"));
                merchantDetailsVO.setEmailSent(res.getString("emailSent"));
                merchantDetailsVO.setSplitPaymentAllowed(res.getString("isSplitPayment"));
                merchantDetailsVO.setLogin(res.getString("login"));
                merchantDetailsVO.setEmailverified(res.getString("isemailverified"));
                merchantDetailsVO.setPartnerId(res.getString("partnerId"));
                merchantDetailsVO.setActivation(res.getString("activation"));
                merchantDetailsVO.setVirtualCheckout(res.getString("isVirtualCheckoutAllowed"));
                merchantDetailsVO.setIsMobileRequiredForVC(res.getString("isMobileAllowedForVC"));
                merchantDetailsVO.setIsEmailRequiredForVC(res.getString("isEmailAllowedForVC"));
                merchantDetailsVO.setPartialRefund(res.getString("isPartialRefund"));
                merchantDetailsVO.setMultipleRefund(res.getString("isMultipleRefund"));
                merchantDetailsVO.setIsShareAllowed(res.getString("isShareAllowed"));
                merchantDetailsVO.setIsSignatureAllowed(res.getString("isSignatureAllowed"));
                merchantDetailsVO.setIsSaveReceiptAllowed(res.getString("isSaveReceiptAllowed"));
                merchantDetailsVO.setDefaultLanguage(res.getString("defaultLanguage"));
            }
            logger.debug("merchant Details loaded");
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetails()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        logger.debug("Leaving getMemberDetails");

        return merchantDetailsVO;
    }
    public Set<MerchantDetailsVO> getAllOldProcessingMerchants() throws Exception
    {
        Set<MerchantDetailsVO> set=new HashSet();
        MerchantDetailsVO merchantDetailsVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "select memberid,from_unixtime(dtstamp) as signupdatetime from members where activation='Y'";
            pstmt= conn.prepareStatement(query);
            rs= pstmt.executeQuery();
            SimpleDateFormat targetFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while(rs.next())
            {
                String signupDate=rs.getString("signupdatetime");
                long day=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(signupDate)),targetFormat.format(new Date()));
                if(day>90)
                {
                    merchantDetailsVO=new MerchantDetailsVO();
                    merchantDetailsVO.setMemberId(rs.getString("memberid"));
                    merchantDetailsVO.setRegistrationDate(rs.getString("signupdatetime"));
                    set.add(merchantDetailsVO);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving MerchantDAO throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getAllOldProcessingMerchants()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getAllOldProcessingMerchants()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return set;
    }
    public boolean stopMerchantActivation(String memberId)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        boolean status=false;
        try
        {
            connection = Database.getConnection();
            String query= "update members set isactivation='N' WHERE toid=?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, memberId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                status=true;
            }
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception :::::", e);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(), "updateBinDetailsSuccessful()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            //PZExceptionHandler.raiseDBViolationException(TransactionDAO.class.getName(),"updateBinDetailsSuccessful()",null,"Common","SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return status;
    }
    public List<MerchantDetailsVO> getNewAllMerchants()throws PZDBViolationException
    {
        List<MerchantDetailsVO> merchantDetailsVOs=new ArrayList();
        MerchantDetailsVO merchantDetailsVO=null;
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,activation,contact_emails,FROM_UNIXTIME(dtstamp) as registrationon FROM members;";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            while(res.next())
            {
                String signupDate=res.getString("registrationon");
                long dy=Functions.DATEDIFF(targetFormat.format(targetFormat.parse(signupDate)),targetFormat.format(new Date()));
                if(dy<90)
                {
                    merchantDetailsVO=new MerchantDetailsVO();
                    merchantDetailsVO.setMemberId(res.getString("memberid"));
                    merchantDetailsVO.setActivation(res.getString("activation"));
                    merchantDetailsVO.setContact_emails(res.getString("contact_emails"));
                    merchantDetailsVO.setRegistrationDate(res.getString("registrationon"));
                    merchantDetailsVOs.add(merchantDetailsVO);
                }
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving MerchantDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchants()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getNewAllMerchants()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as Exception :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getNewAllMerchant()", null, "Common", "SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  merchantDetailsVOs;
    }

    public LinkedHashMap<Integer,String> listAllMember()
    {
        LinkedHashMap<Integer,String> merchantMap=new LinkedHashMap<Integer, String>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,company_name FROM members ORDER BY memberid DESC";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while(res.next())
            {
                merchantMap.put(res.getInt("memberid"),res.getString("company_name"));
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving MerchantDAO throwing System Exception as SystemError :::: ", systemError);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchants()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as SystemError :::: ", e);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchants()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as Exception :::: ", e);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  merchantMap;
    }

    public LinkedHashMap<Integer,String> listAllMembers()
    {
        LinkedHashMap<Integer,String> merchantMap=new LinkedHashMap<Integer, String>();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet res=null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT memberid,contact_persons FROM members ORDER BY memberid DESC";
            pstmt= conn.prepareStatement(query);
            res = pstmt.executeQuery();
            while(res.next())
            {
                merchantMap.put(res.getInt("memberid"),res.getString("contact_persons"));
            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving MerchantDAO throwing System Exception as SystemError :::: ", systemError);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchants()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as SystemError :::: ", e);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchants()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        catch (Exception e)
        {
            logger.error("Leaving MerchantDAO throwing SQL Exception as Exception :::: ", e);
            //PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getNewAllMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return  merchantMap;
    }
    public boolean authenticateMemberViaKey(String loginName,String key, String partnerid) throws PZDBViolationException
    {
        Connection con          = null;
        ResultSet resultSet     = null;
        PreparedStatement preparedStatement = null;
        try
        {
            con             = Database.getRDBConnection();
            String query    = "Select login,clkey from members where login=? and BINARY clkey=? and partnerid=?" ;

            preparedStatement=con.prepareStatement(query);

            preparedStatement.setString(1,loginName);
            preparedStatement.setString(2,key);
            preparedStatement.setString(3,partnerid);

            resultSet=preparedStatement.executeQuery();
            logger.debug("clkey query----"+preparedStatement);

            if(resultSet.next())
            {
                return true;
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "authenticateMemberViaKey()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "authenticateMemberViaKey()", null, "Common", "SQLException while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return false;
    }
    public boolean authenticateMemberViaChecksum(String memberId,String checksum,String random) throws PZDBViolationException, PZTechnicalViolationException
    {
        Connection con = null;
        ResultSet resultSet =null;
        PreparedStatement preparedStatement = null;
        try
        {
            con =Database.getRDBConnection();
            String query="Select MEM.memberid as memberid,PAR.clkey as clkey from members as MEM JOIN partners as PAR on MEM.partnerId=PAR.partnerId where MEM.memberid=?" ;

            preparedStatement=con.prepareStatement(query);

            preparedStatement.setString(1,memberId);

            resultSet=preparedStatement.executeQuery();

            if(resultSet.next())
            {
                if(functions.generateMD5ChecksumAPIBased(memberId,resultSet.getString("clkey"),random).equals(checksum))
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"authenticateMemberViaChecksum()",null,"Common","System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"authenticateMemberViaChecksum()",null,"Common","SQLException while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Leaving Merchants throwing System NoSuchAlgorithm Exception as System Error :::: ",e);
            PZExceptionHandler.raiseTechnicalViolationException(MerchantDAO.class.getName(), "authenticateMemberViaChecksum()", null, "Common", "Internal Issue while Generating Checksum", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(con);
        }
        return false;
    }

    public TreeMap<Integer,String> getMemberDetailsForRejectedTransaction()
    {
        TreeMap<Integer, String> memberid = new TreeMap<Integer, String>();
        Connection con=null;
        try
        {
            con=Database.getRDBConnection();
            String qry="select memberid,company_name from members ORDER BY memberid ASC";
            PreparedStatement pstmt= con.prepareStatement(qry);
            ResultSet rs=pstmt.executeQuery();
            while(rs.next())
            {
                memberid.put(rs.getInt("memberid"),rs.getString("company_name"));
            }
        }
        catch(Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return memberid;
    }

    //new
    public TreeMap<Integer,String> getMemberDetailsFortoken()
    {
        TreeMap<Integer, String> memberid = new TreeMap<Integer, String>();
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con=Database.getRDBConnection();
            String qry="select memberid,company_name from members WHERE is_rest_whitelisted='Y'";
            pstmt= con.prepareStatement(qry);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                memberid.put(rs.getInt("memberid"),rs.getString("company_name"));
            }
        }
        catch(Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberid;
    }
    //new for card
    public TreeMap<Integer,String> getMemberDetailsForcard()
    {
        TreeMap<Integer, String> memberid = new TreeMap<Integer, String>();
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con=Database.getRDBConnection();
            String qry="select memberid,company_name from members WHERE is_rest_whitelisted='N'";
            pstmt= con.prepareStatement(qry);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                memberid.put(rs.getInt("memberid"),rs.getString("company_name"));
            }
        }
        catch(Exception e)
        {
            logger.error("error", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberid;
    }
    //new
    public LinkedHashMap<Integer, String>getPartnerMembersDetail(String partnerid)
    {
        LinkedHashMap<Integer, String> memberid = new LinkedHashMap<Integer, String>();
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con=Database.getRDBConnection();
            String qry="select memberid,company_name from members where partnerId=? ORDER BY memberid ASC";
            pstmt= con.prepareStatement(qry);
            pstmt.setString(1,partnerid);
            rs=pstmt.executeQuery();
            while(rs.next())
            {
                memberid.put(rs.getInt("memberid"),rs.getString("company_name"));
            }
        }
        catch(Exception e)
        {
            logger.error("error",e);
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return memberid;
    }

    public Hashtable listMembers(String memberid,String partnername,String company_name, String sitename, String activation, String icici, String fixamount, String fdtstamp, String tdtstamp, int records, int pageno, String ignoredates, String perfectmatch, String contact_emails, String contact_persons, String login,String domain)
    {
        logger.debug("Entering listMembers");
        Hashtable hash = null;
        Functions functions = new Functions();
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        Connection cn = null;
        ResultSet rs=null;
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("select members.memberid, members.login,members.company_name,members.sitename,members.activation,members.activation_date,members.icici,members.dtstamp,members.contact_emails,members.contact_persons,members.domain, partners.`partnerName` FROM members JOIN partners ON(members.`partnerId` = partners.`partnerId`) where 1=1 ");
            StringBuffer countquery = new StringBuffer("select count(*) from members JOIN partners ON(members.`partnerId` = partners.`partnerId`) where 1=1 ");
            StringBuffer condQuery = new StringBuffer();
            if (functions.isValueNull(memberid))
                condQuery.append(" and memberid='" + ESAPI.encoder().encodeForSQL(me,memberid)+"'");

            if (functions.isValueNull(company_name))
            {
                if (perfectmatch != null && perfectmatch.endsWith("No"))
                {
                    condQuery.append(" and members.company_name like '%" + ESAPI.encoder().encodeForSQL(me, company_name) + "%'");
                }
                else
                {
                    condQuery.append(" and members.company_name='" + ESAPI.encoder().encodeForSQL(me, company_name) + "'");
                }
                logger.debug("query company..."+query);
            }

            if (functions.isValueNull(sitename)){
                if (perfectmatch != null && perfectmatch.endsWith("No"))
                {
                    condQuery.append(" and members.sitename like '%" + ESAPI.encoder().encodeForSQL(me,sitename) + "%'");
                }
                else
                {
                    condQuery.append(" and members.sitename='" + ESAPI.encoder().encodeForSQL(me,sitename) + "'");
                }
                logger.debug("query sitename..."+query);
            }
            if (functions.isValueNull(activation))
                condQuery.append(" and members.activation='" + ESAPI.encoder().encodeForSQL(me,activation) + "'");

            if (functions.isValueNull(icici))
                condQuery.append(" and members.icici='" + ESAPI.encoder().encodeForSQL(me,icici) + "'");

            if(ignoredates == null){
                if (functions.isValueNull(fdtstamp))
                    condQuery.append(" and members.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));

                if (functions.isValueNull(tdtstamp))
                    condQuery.append(" and members.dtstamp <= " + ESAPI.encoder().encodeForSQL(me,tdtstamp));
            }

            if(functions.isValueNull(contact_emails))
                condQuery.append(" and members.contact_emails ='" + ESAPI.encoder().encodeForSQL(me,contact_emails) + "'");

            if(functions.isValueNull(contact_persons))
                condQuery.append(" and members.contact_persons ='" + ESAPI.encoder().encodeForSQL(me,contact_persons) + "'");

            if(functions.isValueNull(partnername))
                query.append(" and partners.`partnerName` ='" + ESAPI.encoder().encodeForSQL(me,partnername) + "'");

            if(functions.isValueNull(login))
                condQuery.append(" and members.login ='" + login + "'");

            if(functions.isValueNull(domain))
                condQuery.append(" and members.domain ='" + ESAPI.encoder().encodeForSQL(me,domain) + "'");


            query.append(condQuery);
            query.append(" order by memberid DESC ");
            query.append(" limit " + start + "," + end);

            countquery.append(condQuery);
            logger.debug("query..."+query);

            cn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), cn));
            rs = Database.executeQuery(countquery.toString(), cn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        return hash;
    }

    public Hashtable getMerchantDetails(String memberid)
    {
        Hashtable hash = null;
        String query="";
        Connection con = null;
        PreparedStatement pstmt=null;
        try
        {
            con = Database.getRDBConnection();
            query = "select login,memberid, company_name, contact_persons,contact_emails,notifyemail,sitename,brandname,telno,faxno,address,city,state,zip,country,activation,icici,date_format(from_unixtime(dtstamp),'%d-%m-%Y') as actDate,date_format(from_unixtime(unix_timestamp(timestmp)),'%d-%m-%Y') as modDate from members where memberid =?";
            pstmt=con.prepareStatement(query);
            pstmt.setString(1,memberid);
            hash = Database.getHashFromResultSet(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            logger.error("SQLException----",se);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }

        int mId=0;
        try
        {
            mId=Integer.parseInt(memberid);
            hash.put("balance", new TransactionEntry(mId).getBalance());
        }
        catch (Exception e)
        {
            logger.error("Exception----",e);
        }
        return hash;
    }

    public String getMemberDetailsForForgetPassword(String login)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        String isMerchantInterfaceAccess = "";
        try
        {
            conn = Database.getRDBConnection();
            String query = "select icici from members where login = '"+ login +"'";
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                isMerchantInterfaceAccess = rs.getString("icici");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return isMerchantInterfaceAccess;
    }

    public String getMerchantKey(String memberId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        String memberKey = "";
        try
        {
            // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            String query = "select clkey from members where memberid = "+memberId;
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                memberKey = rs.getString("clkey");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return memberKey;
    }

    public Map<String, Object> getSavedMemberTemplateDetails(String memberId) throws PZDBViolationException
    {
        Connection con              = null;
        ResultSet resultSet         = null;
        PreparedStatement pstmt     = null;
        Map<String, Object> merchantDetailsVOMap = new HashMap<String, Object>();
        logger.debug("Inside saved Template Details:::");
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from template_preferences where memberid= ? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, memberId);
            resultSet       = pstmt.executeQuery();
            while (resultSet.next())
            {
                if (TemplatePreference.getEnum(resultSet.getString("name")) != null && !TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (TemplatePreference.getEnum(resultSet.getString("name")) != null && TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value"));
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOMap;
    }

    public boolean insertTemplatePreferences(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "insert into template_preferences(memberid,name,value) values (?,?,?)";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() > 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, memberId);
                        pstmt.setString(2, merchantTemplateNameKeyPair.getKey());
                        if (TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(merchantTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(3, (byte[]) merchantTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(3, merchantTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.addBatch();

                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean updateTemplatePreferences(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "update template_preferences set value=? where name=? and memberid=?";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        if (TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(merchantTemplateNameKeyPair.getKey()))
                        {
                            pstmt.setBytes(1, (byte[]) merchantTemplateNameKeyPair.getValue());
                        }
                        else
                        {
                            pstmt.setString(1, merchantTemplateNameKeyPair.getValue().toString());
                        }
                        pstmt.setString(2, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(3, memberId);
                        pstmt.addBatch();
                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean deleteMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String query = null;
        try
        {
            conn = Database.getConnection();
            query = "DELETE FROM template_preferences WHERE name=? AND memberid=?";
            pstmt = conn.prepareStatement(query);
            if (merchantTemplateInformation != null && merchantTemplateInformation.size() >= 0)
            {
                for (Map.Entry<String, Object> merchantTemplateNameKeyPair : merchantTemplateInformation.entrySet())
                {
                    if (merchantTemplateNameKeyPair.getValue() != null && merchantTemplateNameKeyPair.getKey() != null)
                    {
                        pstmt.setString(1, merchantTemplateNameKeyPair.getKey());
                        pstmt.setString(2, memberId);
                        pstmt.addBatch();

                    }
                }
                int[] k = pstmt.executeBatch();
                if (k.length > 0)
                {
                    return true;
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving insertMemberTemplate throwing SQL Exception as System Error :::: ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "insertTemplatePreferences()", null, "Common", "System error while connecting to  table ", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }


    //****Merchant Dashboard Methods****//
    public Hashtable<String,String> getTotalSalesAmount (String merchantId, String currency, String payBrand, String payMode, DateVO dateVOs,String dashboard_value,String status)
    {
        logger.error("ENtering getTotalSalesAmount method....");
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        String tableName = "transaction_common";
        Hashtable<String, String> hashtableforSales = new Hashtable<>();
        String salescount="";
        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            // for lastweek dtstamp conversion
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");

            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

            //String query = "SELECT SUM(amount) AS sumAmount FROM transaction_common AS tc WHERE tc.Status IN ('markedforreversal' ,'capturesuccess','authsuccessful','chargeback','settled','reversed') AND toid ='" + merchantId + "'";
            query.append("SELECT (SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) + SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END)) AS Amount,COUNT(Amount) as salesCount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE  toid ='" + merchantId + "'");

            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append( "AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append( "AND pt.paymodeid = ?");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVOs.getDateLabel()))
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else
            {
                String datestartdtstmp = "";
                String dateenddtstmp   = "";
                if (functions.isValueNull(dateVOs.getStartDate()) && dateVOs.getStartDate().split(" ").length > 0)
                {
                    String[] datetimeArray2 = dateVOs.getStartDate().split(" ");
                    String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                    datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
                }
                if (functions.isValueNull(dateVOs.getEndDate()) && dateVOs.getEndDate().split(" ").length > 0)
                {
                    String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                    String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                    dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                }
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            logger.debug("QUERY for getTotalSalesAmount " + pstmt.toString());
            Date date4= new Date();
            logger.error("getTotalSalesAmount query starts####### " + date4.getTime());
            rs = pstmt.executeQuery();
            logger.error("getTotalSalesAmount query ends######### "+new Date().getTime());
            logger.error("getTotalSalesAmount query diff######### "+(new Date().getTime()-date4.getTime()));
            logger.error("getTotalSalesAmount query##### "+pstmt.toString());
            while( rs.next())
            {
                String Amount = rs.getString("Amount");
                amount = getValidValue(Amount);
                salescount= rs.getString("salesCount");

                if (functions.isValueNull(amount) || functions.isValueNull(salescount))
                {
                    hashtableforSales.put(amount, salescount);
                    logger.error("hashtableforSales ::::: "+hashtableforSales);
                }
            }

            /*Float Captureamount = rs.getFloat("Captureamount");
            Float TotalAmount = (Amount + Captureamount);*/
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getTotalSalesAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtableforSales;
    }

    public HashMap<String,String> getTotalAmountAndCount(String merchantId, String currency, String payBrand, String payMode,DateVO dateVOs,String dashboard_value,String status)
    {
        logger.error("Entering in getTotalAmountAndCount method.....");
        Connection connection = null;
        ResultSet rs=null;
        HashMap<String,String> hashforStatus= new HashMap<>();
        StringBuilder query = new StringBuilder();
        String tableName ="transaction_common";
        int refundCount=0;
        int settledcount=0;
        int chargebackcount=0;
        int payoutsuccesscount=0;
        int payoutfailedcount=0;
        int declinedcount=0;
        int captureCount = 0;
        int sumfordecaCount=0;
        double sumdeclineamount= 0.0d;
        int counter=1;
        PreparedStatement pstmt= null;

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection= Database.getConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");

            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

            query.append("Select tc.Status AS STATUS,SUM(CASE WHEN STATUS='capturesuccess' THEN captureamount END)AS capSum,COUNT(CASE WHEN STATUS='capturesuccess' THEN captureamount END) AS cap,");
            query.append("SUM(CASE WHEN STATUS='reversed' THEN refundamount END) AS revSum,COUNT(CASE WHEN STATUS='reversed' THEN refundamount END) AS ref, ");
            query.append("SUM(CASE WHEN STATUS='settled' THEN captureamount END) AS settledSum,COUNT(CASE WHEN STATUS='settled' THEN captureamount END) AS settledCount, ");
            query.append("SUM(CASE WHEN STATUS='chargeback' THEN amount END) AS chargebackSum,COUNT(CASE WHEN STATUS='chargeback' THEN amount END) AS chargebackCount, ");
            query.append("SUM(CASE WHEN STATUS='payoutsuccessful' THEN payoutamount END) AS payoutSucSum,COUNT(CASE WHEN STATUS='payoutsuccessful' THEN payoutamount END) AS payoutSucCount, ");
            query.append("SUM(CASE WHEN STATUS='payoutfailed' THEN payoutamount END) AS payoutFailSum,COUNT(CASE WHEN STATUS='payoutfailed' THEN payoutamount END) AS payoutFailCount, ");
            query.append("SUM(CASE WHEN STATUS IN('authfailed','failed') THEN amount END)as declinedAmount,COUNT(CASE WHEN STATUS IN('authfailed','failed') THEN amount END) as declinedCount ");
           // query.append("SUM(CASE WHEN STATUS IN('authfailed','failed') THEN amount END) AS declinedAmount, COUNT(CASE WHEN STATUS IN('authfailed','failed')THEN amount END) as declinedCount ");
            query.append("from "+tableName+" as tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE toid='"+merchantId+"'");
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency = ?");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append( "AND ct.cardtypeid = ?");
            }
            if (functions.isValueNull(payMode))
            {
                query.append( "AND pt.paymodeid = ?");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVOs.getDateLabel()))
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY STATUS");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS");
            }
            else
            {
                if(dateVOs != null && functions.isValueNull(dateVOs.getStartDate()))
                {
                    String dateenddtstmp = "";
                    String datestartdtstmp = "";
                    if (dateVOs.getStartDate().split(" ").length > 0)
                    {
                        String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                        String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                        datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
                    }
                    if (dateVOs.getEndDate().split(" ").length > 0)
                    {
                        String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                        String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                        dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                    }
                    query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY STATUS");
                }
            }

            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }

            logger.error("Query for getTotalAmountAndCount#### "+pstmt.toString());
            Date date5= new Date();
            logger.error("getTotalAmountAndCount  query starts######## "+date5.getTime());
            rs=pstmt.executeQuery();
            logger.error("getTotalAmountAndCount  query ends######## "+new Date().getTime());
            logger.error("getTotalAmountAndCount query difference######### "+(new Date().getTime()-date5.getTime()));

            while(rs.next())
            {
                String Amount = rs.getString("capSum");
                captureCount = rs.getInt("cap");
                if (functions.isValueNull(Amount) && captureCount!=0)
                {
                    hashforStatus.put("capturesuccess", Amount + "_" + Integer.toString(captureCount));
                }

                String refundAmount = rs.getString("revSum");
                refundCount = rs.getInt("ref");
                if (functions.isValueNull(refundAmount) && refundCount!=0)
                {
                    hashforStatus.put("reversed", refundAmount + "_" + Integer.toString(refundCount));
                }
                String settledAmount= rs.getString("settledSum");
                settledcount= rs.getInt("settledCount");
                if (functions.isValueNull(settledAmount) && settledcount!=0)
                {
                    hashforStatus.put("settled", settledAmount+"_"+ Integer.toString(settledcount));
                }
                String chargebackamount= rs.getString("chargebackSum");
                chargebackcount= rs.getInt("chargebackCount");
                if (functions.isValueNull(chargebackamount) && chargebackcount!=0)
                {
                    hashforStatus.put("chargeback",chargebackamount+"_"+ Integer.toString(chargebackcount));
                }
                String payoutsuccessamount= rs.getString("payoutSucSum");
                payoutsuccesscount= rs.getInt("payoutSucCount");
                if (functions.isValueNull(payoutsuccessamount) && payoutsuccesscount!=0)
                {
                    hashforStatus.put("payoutsuccessful",payoutsuccessamount+"_"+ Integer.toString(payoutsuccesscount));
                }
                String payoutfailedamount= rs.getString("payoutFailSum");
                payoutfailedcount= rs.getInt("payoutFailCount");
                if (functions.isValueNull(payoutfailedamount) && payoutfailedcount!=0)
                {
                    hashforStatus.put("payoutfailed", payoutfailedamount+"_"+ Integer.toString(payoutfailedcount));
                }
               // String declinedAmount= rs.getString("declinedAmount");
                double declinedAmount= rs.getDouble("declinedAmount");
                declinedcount= rs.getInt("declinedCount");
                sumfordecaCount= sumfordecaCount + declinedcount;
                sumdeclineamount= sumdeclineamount+ declinedAmount;
            }

            if (sumfordecaCount!= 0 || sumdeclineamount!= 0.0d)
            {
                hashforStatus.put("declined", String.valueOf(sumdeclineamount)+"_"+Integer.toString(sumfordecaCount));
            }
            logger.error("hashforStatus ::::: " + hashforStatus);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::: ",systemError);
        }
        catch (Exception e)
        {
            logger.error("SQLException:::: ",e);
        }
        return hashforStatus;
    }

   /* public Hashtable<String,String> getTotalSettledAmount(String merchantId, String currency, String payBrand, String payMode)
    {
        logger.error("Entering in getTotalSettledAmount method.....");
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        Hashtable<String,String> hashforsettled= new Hashtable<>();
        String settledcount="";

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";


        try
        {
            connection = Database.getRDBConnection();
            //String query = "SELECT SUM(amount) AS settledAmount FROM transaction_common AS tc WHERE tc.Status ='Settled' AND toid ='" + merchantId + "'";
            query.append("SELECT SUM(captureamount) AS settledAmount, COUNT(captureamount) as settledCount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status ='Settled' AND toid ='" + merchantId + "'");
            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append(" AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append( " AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append( " AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            logger.debug("QUERY for getTotalSettledAmount " +pstmt.toString());
            Date date5= new Date();
            logger.error("getTotalSettledAmount query starts######## " + date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getTotalSettledAmount query ends######## "+new Date().getTime());
            logger.error("getTotalSettledAmount query diff######## "+(new Date().getTime()-date5.getTime()));
            logger.error("getTotalSettledAmount query ######## "+pstmt.toString());
            while(rs.next())
            {
                String amt = rs.getString("settledAmount");
                amount = getValidValue((amt));
                settledcount = rs.getString("settledCount");

                if (functions.isValueNull(amount) || functions.isValueNull(settledcount))
                {
                    hashforsettled.put(amount, settledcount);
                    logger.error("hashforsettled from MerchantDAO::  "+hashforsettled);
                }
            }

            //double amt = rs.getInt(1);
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getTotalSettledAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashforsettled;
    }*/


    /*  public Hashtable<String,String> getTotalTotalRefundAmount(String merchantId, String currency, String payBrand, String payMode)
      {
          logger.error("Entering getTotalTotalRefundAmount method;;;;");
          String amount = "";
          Connection connection = null;
          PreparedStatement pstmt=null;
          ResultSet rs=null;
          int counter = 1;
          StringBuilder query = new StringBuilder();
          Hashtable<String,String> hashtableforRefund= new Hashtable<>();
          String refundCount="";

          String tableName ="transaction_common";

          if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
              tableName = "transaction_card_present";


          try
          {
              connection = Database.getRDBConnection();
              //query.append("SELECT SUM(amount) AS settledAmount FROM transaction_common AS tc JOIN  payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN  card_type AS ct  ON ct.cardtypeid=tc.cardtypeid  WHERE  tc.Status ='Settled'  AND toid ='" + merchantId + "'");
              query.append("SELECT SUM(refundamount) AS refundAmount, COUNT(refundamount) as refundcount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status IN ('reversed' ,'partialrefund') AND toid ='" + merchantId + "'");
              if (functions.isValueNull(currency))//currency != null && currency != "")
              {
                  query.append("AND currency = ? ");
              }
              if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
              {
                  query.append("AND ct.cardtypeid = ? ");
              }
              if (functions.isValueNull(payMode))//payMode != null && payMode != "")
              {
                  query.append("AND pt.paymodeid = ? ");
              }
              pstmt = connection.prepareStatement(query.toString());
              if (functions.isValueNull(currency))
              {
                  pstmt.setString(counter, currency);
                  counter++;

              }
              if (functions.isValueNull(payBrand))
              {
                  pstmt.setString(counter, payBrand);
                  counter++;

              }
              if (functions.isValueNull(payMode))
              {
                  pstmt.setString(counter, payMode);
                  counter++;

              }
              logger.debug("QUERY for getTotalTotalRefundAmount " +pstmt.toString());
              Date date5= new Date();
              logger.error("getTotalTotalRefundAmount query starts########## "+date5.getTime());
              rs = pstmt.executeQuery();
              logger.error("getTotalTotalRefundAmount query ends####### "+new Date().getTime());
              logger.error("getTotalTotalRefundAmount query diff####### "+(new Date().getTime()-date5.getTime()));
              logger.error("getTotalTotalRefundAmount query ####### "+pstmt.toString());
              while(rs.next())
              {
                  String amt = rs.getString("refundAmount");
                  amount = getValidValue((amt));
                  refundCount= rs.getString("refundcount");
                  if (functions.isValueNull(amount) || functions.isValueNull(refundCount))
                  {
                      hashtableforRefund.put(amount, refundCount);
                      logger.error("hashtableforRefund from MerchantDAO:: "+hashtableforRefund);
                  }
              }

              // double amt = rs.getInt(1);

          }
          catch (Exception e)
          {
              logger.error("Exception while getting getTotalTotalRefundAmount info BOX data ", e);
          }
          {
              Database.closeResultSet(rs);
              Database.closePreparedStatement(pstmt);
              Database.closeConnection(connection);
          }
          return hashtableforRefund;
      }
  */
   /* public Hashtable<String,String> getTotalChargebackAmount(String merchantId, String currency, String payBrand, String payMode)
    {
        logger.error("Entering getTotalChargebackAmount method........");
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        Hashtable<String,String> hashtableforchargeback= new Hashtable<>();
        String chargebackCount="";

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            //query.append("SELECT SUM(amount) AS chargebackAmount FROM transaction_common AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid  AND tc.Status ='chargeback' AND toid ='" + merchantId + "'");
            query.append("SELECT SUM(amount) AS chargebackAmount, COUNT(amount) as chargebackcount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.Status ='chargeback' AND toid ='" + merchantId + "'");
            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append("AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;

            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;

            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;

            }
            logger.debug("QUERY for getTotalChargeBackAmount " +pstmt.toString());
            Date date5= new Date();
            logger.error("getTotalChargeBackAmount query starts######### " + date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getTotalChargeBackAmount query ends######## "+new Date().getTime());
            logger.error("getTotalChargeBackAmount query diff######## "+(new Date().getTime()-date5.getTime()));
            logger.error("getTotalChargeBackAmount query ######## "+pstmt.toString());
            while(rs.next())
            {
                String amt = rs.getString("chargebackAmount");
                amount = getValidValue((amt));
                chargebackCount= rs.getString("chargebackcount");
                if (functions.isValueNull(amount) || functions.isValueNull(chargebackCount))
                {
                    hashtableforchargeback.put(amount, chargebackCount);
                    logger.error("hashtableforchargeback from MerchantDAO:::: "+hashtableforchargeback);
                }
            }

            //double amt = rs.getInt(1);

        }
        catch (Exception e)
        {
            logger.error("Exception while getting getTotalChargebackAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtableforchargeback;
    }
*/
   /* public Hashtable<String,String> getWithdrawAmount(String merchantId, String currency, String payBrand, String payMode,DateVO dateVOs,String dashboard_value)
    {
        logger.error("Entering getWithdrawAmount method;;;;;;;");
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        Hashtable<String,String> hashtablewithdraw= new Hashtable<>();
        String withdrawcount= "";

        try
        {
            connection = Database.getRDBConnection();
            DateVO currentweek= dateManager.getDateRangeNew("current_week");
            query.append("SELECT SUM(mw.netfinalamount) AS amt, COUNT(mw.netfinalamount) as count FROM merchant_wiremanager mw JOIN payment_type AS pt ON mw.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=mw.cardtypeid WHERE toid ='"+ merchantId +"'");
            query.append(" AND settlementcycle_no IN (SELECT MAX(settlementcycle_no) FROM merchant_wiremanager WHERE  toid ='"+ merchantId +"'");
            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append(" AND currency = '"+ currency +"' ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append(" AND cardtypeid = '"+ payBrand +"' ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append(" AND paymodeid = '"+ payMode +"'");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND FROM_UNIXTIME(wirecreationtime) BETWEEN '"+currentweek.getStartDate()+"' AND '"+currentweek.getEndDate()+ "' GROUP BY accountid)");
            }
            else
            {
                query.append(" AND FROM_UNIXTIME(wirecreationtime) BETWEEN '" + dateVOs.getStartDate() + "' AND '" + dateVOs.getEndDate() + "' GROUP BY accountid)");
            }
            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append(" AND mw.currency = ? ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append(" AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append(" AND pt.paymodeid = ? ");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;

            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;

            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;

            }
            Date date5= new Date();
            logger.error("getWithdrawAmount query starts####### "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getWithdrawAmount query ends######## "+new Date().getTime());
            logger.error("getWithdrawAmount query diff######## "+(new Date().getTime()-date5.getTime()));
            logger.error("getWithdrawAmount query ######## "+pstmt.toString());
            while( rs.next())
            {
                String amt = rs.getString("amt");
                amount = getValidValue(amt);
                withdrawcount= rs.getString("count");

                if (functions.isValueNull(amount) || functions.isValueNull(withdrawcount))
                {
                    hashtablewithdraw.put(amount, withdrawcount);
                    logger.error("hashtablewithdraw from MerchantDAO::::: "+hashtablewithdraw);
                }
            }
            logger.debug("QUERY for getWithdrawAmount " +pstmt.toString());

            //double amt = rs.getInt(1);
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getWithdrawAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtablewithdraw;
    }
*/
   /* public Hashtable<String,String> getTotalDeclinedAmount(String merchantId, String currency, String payBrand, String payMode,DateVO dateVOs,String dashboard_value,String status)
    {
        logger.error("Entering getTotalDeclinedAmount method......");
        String amount = "";
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        Hashtable<String,String> hashtablefordeclined= new Hashtable<>();
        String declinedcount= "";

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("last_seven_days");
            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);
            System.out.println(("lastweekstartdtstmp getTotalDeclinedAmount +++ "+lastweekstartdtstmp));

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);
            System.out.println(("lastweekenddtstmp getTotalDeclinedAmount+++ " + lastweekenddtstmp));
            //query.append("SELECT SUM(amount) AS declinedAmount FROM transaction_common AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND tc.status IN ('authfailed','failed') AND toid ='" + merchantId + "'");
            query.append("SELECT SUM(amount) AS declinedAmount, COUNT(amount) as declinedCount FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status IN ('authfailed','failed') AND toid ='" + merchantId + "'");
            if (functions.isValueNull(currency))//currency != null && currency != "")
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))//payBrand != null && payBrand != "")
            {
                query.append("AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))//payMode != null && payMode != "")
            {
                query.append("AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVOs.getDateLabel()))
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
                System.out.println(("datestartdtstmp getTotalDeclinedAmount+++ "+datestartdtstmp));

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                System.out.println(("dateenddtstmp getTotalDeclinedAmount+++ "+dateenddtstmp));
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"'");
            }
            else
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
                System.out.println(("datestartdtstmp getTotalDeclinedAmount+++ "+datestartdtstmp));

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                System.out.println(("dateenddtstmp getTotalDeclinedAmount+++ "+dateenddtstmp));
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'");
            }
            pstmt = connection.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            logger.debug("QUERY for getTotalDeclinedAmount " +pstmt.toString());
            Date date5= new Date();
            logger.error("getTotalDeclinedAmount query starts############ " + date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getTotalDeclinedAmount query ends####### "+new Date().getTime());
            logger.error("getTotalDeclinedAmount query diff####### "+(new Date().getTime()-date5.getTime()));
            logger.error("getTotalDeclinedAmount query ######### "+pstmt.toString());
            while(rs.next())
            {
                String amt = rs.getString("declinedAmount");
                amount = getValidValue(amt);
                declinedcount= rs.getString("declinedCount");
                if (functions.isValueNull(amount) || functions.isValueNull(declinedcount))
                {
                    hashtablefordeclined.put(amount, declinedcount);
                    logger.error("hashtablefordeclined from MerchantDAO::: "+hashtablefordeclined);
                }
            }

            // double amt = rs.getInt(1);

        }
        catch (Exception e)
        {
            logger.error("Exception while getting getTotalDeclinedAmount info BOX data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashtablefordeclined;
    }
*/
   /* public Hashtable<String,String> getTotalCaptureAmount(String merchantId, String currency, String payBrand, String payMode)
    {
        logger.error("entering in getTotalCaptureAmount method....");
        String captureamount="";
        String capturecount="";
        Connection con= null;
        PreparedStatement pstmt= null;
        ResultSet rs= null;
        int counter=1;
        StringBuilder query= new StringBuilder();
        Hashtable<String, String> hashtableCaptureSuccess = new Hashtable<>();

        String tableName="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            con= Database.getRDBConnection();
            query.append("Select COUNT(captureamount) as captureCount,SUM(captureamount) as captureAmount from "+tableName+" as tc join payment_type as pt ON tc.paymodeid=pt.paymodeid join card_type as ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status='capturesuccess' and toid='"+merchantId+"'");

            if (functions.isValueNull(currency))
            {
                query.append(" AND currency=?");
            }
            if (functions.isValueNull(payMode))
            {
                query.append(" AND pt.paymodeid= ?");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append(" AND ct.cardtypeid= ?");
            }
            pstmt= con.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter,currency);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            Date date5= new Date();
            logger.error("getTotalCaptureAmount query starts##### "+date5.getTime());
            rs= pstmt.executeQuery();
            logger.error("getTotalCaptureAmount query ends###### "+new Date().getTime());
            logger.error("getTotalCaptureAmount query diff###### "+(new Date().getTime()-date5.getTime()));
            logger.error("getTotalCaptureAmount query ###### "+pstmt.toString());
            while(rs.next())
            {
                String amt= rs.getString("captureAmount");
                captureamount= getValidValue(amt);
                capturecount= rs.getString("captureCount");

                if (functions.isValueNull(captureamount) || functions.isValueNull(capturecount))
                {
                    hashtableCaptureSuccess.put(captureamount,rs.getString("captureCount"));
                    logger.error("hashtableCaptureSuccess from merchantdao 1::: " + hashtableCaptureSuccess);
                }
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while getTotalCaptureAmount::: ",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLException while getTotalCaptureAmount::: ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return hashtableCaptureSuccess;

    }*/

   /* public Hashtable<String,String> getTotalPayoutSuccessAmount(String merchantId, String currency, String payBrand, String payMode)
    {
        String payoutSuccessamount="";
        int counter=1;
        Connection con= null;
        ResultSet rs= null;
        PreparedStatement pstmt= null;
        StringBuilder query= new StringBuilder();
        Hashtable<String, String> hashtablePayoutSuccessful = new Hashtable<>();

        String tableName= "transaction_common";
        if (functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName="transaction_card_present";
        try
        {
            con= Database.getRDBConnection();
            query.append("Select COUNT(payoutamount) as countPayoutSuccess, SUM(payoutamount) as totalPayoutSuccess from "+tableName+" as tc join payment_type as pt ON tc.paymodeid=pt.paymodeid join card_type as ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status='payoutsuccessful' and toid='"+merchantId+"'");

            if (functions.isValueNull(currency))
            {
                query.append(" AND currency=?");
            }
            if (functions.isValueNull(payMode))
            {
                query.append(" AND pt.paymodeid= ?");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append(" AND ct.cardtypeid= ?");
            }
            pstmt= con.prepareStatement(query.toString());
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter,currency);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter,payMode);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter,payBrand);
                counter++;
            }
            Date date5= new Date();
            logger.error("getTotalPayoutSuccessAmount query starts###### "+date5.getTime());
            rs=pstmt.executeQuery();
            logger.error("getTotalPayoutSuccessAmount query ends###### "+new Date().getTime());
            logger.error("getTotalPayoutSuccessAmount query diff##### "+(new Date().getTime()-date5.getTime()));
            logger.error("getTotalPayoutSuccessAmount query######## "+pstmt.toString());
            while(rs.next())
            {
                String amt= rs.getString("totalPayoutSuccess");
                payoutSuccessamount= getValidValue(amt);
                String payoutsuccesscount= rs.getString("countPayoutSuccess");

                if (functions.isValueNull(payoutSuccessamount) && functions.isValueNull(payoutsuccesscount))
                {
                    hashtablePayoutSuccessful.put(payoutSuccessamount,rs.getString("countPayoutSuccess"));
                    logger.error("hashtablePayoutSuccessful from MerchantDAO::::::"+hashtablePayoutSuccessful);
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getTotalPayoutSuccessAmount..", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return  hashtablePayoutSuccessful;
    }*/

    /* public Hashtable<String,String> getTotalPayoutFailedAmount(String merchantId, String currency, String payBrand, String payMode)
     {
         String payoutfailed="";
         int counter=1;
         Connection con= null;
         PreparedStatement pstmt= null;
         ResultSet rs= null;
         StringBuilder query= new StringBuilder();
         Hashtable<String,String> hashPayoutFailed= new Hashtable<String,String>();

         String tableName="transaction_common";
         if (functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
             tableName= "transaction_card_present";
         try
         {
             con=Database.getRDBConnection();
             query.append("Select COUNT(payoutamount) as payoutFailedCount, SUM(payoutamount) as payoutFailedAmount from "+tableName+" as tc join payment_type as pt ON tc.paymodeid=pt.paymodeid join card_type as ct ON ct.cardtypeid=tc.cardtypeid WHERE tc.status='payoutfailed' and toid='"+merchantId+"'");

             if (functions.isValueNull(currency))
             {
                 query.append(" AND currency=?");
             }
             if (functions.isValueNull(payMode))
             {
                 query.append(" AND pt.paymodeid= ?");
             }
             if (functions.isValueNull(payBrand))
             {
                 query.append(" AND ct.cardtypeid= ?");
             }
             pstmt= con.prepareStatement(query.toString());
             if (functions.isValueNull(currency))
             {
                 pstmt.setString(counter,currency);
                 counter++;
             }
             if (functions.isValueNull(payMode))
             {
                 pstmt.setString(counter,payMode);
                 counter++;
             }
             if (functions.isValueNull(payBrand))
             {
                 pstmt.setString(counter,payBrand);
                 counter++;
             }
             Date date5= new Date();
             logger.error("getTotalPayoutFailedAmount query starts####### "+date5.getTime());
             rs= pstmt.executeQuery();
             logger.error("getTotalPayoutFailedAmount query ends###### "+new Date().getTime());
             logger.error("getTotalPayoutFailedAmount query diff###### "+(new Date().getTime()-date5.getTime()));
             logger.error("getTotalPayoutFailedAmount query###### "+pstmt.toString());
             while(rs.next())
             {
                 String amt= rs.getString("payoutFailedAmount");
                 payoutfailed= getValidValue(amt);
                 String count= rs.getString("payoutFailedCount");

                 if (functions.isValueNull(payoutfailed) && functions.isValueNull(count))
                 {
                     hashPayoutFailed.put(payoutfailed, count);
                     logger.error("hashPayoutFailed from MerchantDAO::::: "+hashPayoutFailed);
                 }
             }
         }
         catch (Exception e)
         {
             logger.error("Exception while getting getTotalPayoutFailedAmount.. ", e);
         }
         finally
         {
             Database.closeResultSet(rs);
             Database.closePreparedStatement(pstmt);
             Database.closeConnection(con);
         }
         return hashPayoutFailed;
     }*/
    public HashMap<String, String> getSalesPerCurrencyChartForMerchant(String merchantId, String currency, String payBrand, String payMode, DateVO dateVOs,String dashboard_value,String status)
    {
        logger.error("Entering getSalesPerCurrencyChartForMerchant method......");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();

        HashMap<String, String> hashMapSalesPerCurrencyChart = new HashMap<>();

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

            if (functions.isValueNull(dashboard_value)  ||  (functions.isValueNull(dateVOs.getDateLabel())) && !functions.isValueNull(status))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ");
            }
            else if (functions.isValueNull(status) && "sales".equalsIgnoreCase(status))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ");
            }
            else if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
            {
                query.append("SELECT (UPPER(currency)) AS currencyName ,SUM(CASE WHEN tc.Status  = 'payoutsuccessful' THEN payoutamount ELSE 0 END ) AS PayoutAmount ");
            }
            query.append("FROM " + tableName + " AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE  toid ='" + merchantId + "'");
            if (functions.isValueNull(currency))
            {
                query.append(" AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append( " AND ct.cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append( " AND pt.paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+ lastweekstartdtstmp +"' AND '"+ lastweekenddtstmp+"' GROUP BY currency ");
            }
            else if ((functions.isValueNull(dateVOs.getDateLabel())) && (!functions.isValueNull(status) || functions.isValueNull(status)))
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY currency ");
            }
            else  if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+ lastweekstartdtstmp+"' AND '"+ lastweekenddtstmp+"' GROUP BY currency ");
            }

            pstmt = connection.prepareStatement(query.toString());
            logger.debug("QUERY for getSalesPerCurrencyChartForMerchant " +pstmt.toString());

            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            //pstmt.setString(1, partnerName);
            Date date5= new Date();
            logger.error("getSalesPerCurrencyChartForMerchant query starts######### "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getSalesPerCurrencyChartForMerchant query ends####### "+new Date().getTime());
            logger.error("getSalesPerCurrencyChartForMerchant query diff####### "+(new Date().getTime()-date5.getTime()));
            logger.error("getSalesPerCurrencyChartForMerchant query ####### "+pstmt.toString());
            while (rs.next())
            {
                Float amount = 0.0f;
                Float Captureamount = 0.0f;
                Float TotalAmount = 0.0f;
                Float PayoutAmount = 0.0f;

                if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                {
                    PayoutAmount = rs.getFloat("PayoutAmount");
                    hashMapSalesPerCurrencyChart.put(rs.getString("currencyName"), Float.toString(PayoutAmount));
                }
                else
                {
                    amount = rs.getFloat("Amount");
                    Captureamount = rs.getFloat("Captureamount");
                    TotalAmount = (amount + Captureamount);
                    hashMapSalesPerCurrencyChart.put(rs.getString("currencyName"), Float.toString(TotalAmount));
                }

            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getSalesPerCurrencyChartForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapSalesPerCurrencyChart;
    }

    public HashMap<String, String> getValidStatusChartForMerchant(String merchantId, String currency, String payBrand, String payMode,DateVO dateVOs,String dashboard_value,String status)
    {
        logger.error("Entering getValidStatusChartForMerchant method.....");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        int counter = 1;
        StringBuilder query = new StringBuilder();
        HashMap<String, String> hashMapValidStatusChart = new HashMap<>();

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";

        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

            // query.append("SELECT STATUS AS statusName ,COUNT(STATUS) AS countStatus FROM "+tableName+" AS tc JOIN payment_type AS pt ON tc.paymodeid=pt.paymodeid JOIN card_type AS ct ON ct.cardtypeid=tc.cardtypeid WHERE toid ='" + merchantId + "'");
            query.append("SELECT STATUS AS statusName ,COUNT(STATUS) AS countStatus FROM "+tableName+" AS tc  WHERE toid ='" + merchantId + "'");
            if (functions.isValueNull(currency))
            {
                query.append("AND currency = ? ");
            }
            if (functions.isValueNull(payBrand))
            {
                query.append("AND cardtypeid = ? ");
            }
            if (functions.isValueNull(payMode))
            {
                query.append("AND paymodeid = ? ");
            }
            if (functions.isValueNull(dashboard_value))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS ");
            }
            else if (functions.isValueNull(status) && functions.isValueNull(dateVOs.getDateLabel()))
            {
                String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                query.append(" AND dtstamp BETWEEN '"+datestartdtstmp+"' AND '"+dateenddtstmp+"' GROUP BY STATUS ");

            }
            else if (functions.isValueNull(status))
            {
                query.append(" AND dtstamp BETWEEN '"+lastweekstartdtstmp+"' AND '"+lastweekenddtstmp+"' GROUP BY STATUS ");
            }
            else
            {
                String datestartdtstmp = "";
                String dateenddtstmp = "";
                if(functions.isValueNull(dateVOs.getStartDate()) && dateVOs.getStartDate().split(" ").length >0)
                {
                    String datetimeArray2[] = dateVOs.getStartDate().split(" ");
                    String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                    datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);
                }
                if(functions.isValueNull(dateVOs.getEndDate()) && dateVOs.getEndDate().split(" ").length >0)
                {
                    String datetimeArray3[] = dateVOs.getEndDate().split(" ");
                    String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                    dateenddtstmp = Functions.converttomillisec(mm3, datetimeArray3[0].split("-")[2], datetimeArray3[0].split("-")[0], datetimeArray3[1].split(":")[0], datetimeArray3[1].split(":")[1], datetimeArray3[1].split(":")[2]);
                }
                query.append(" AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "' GROUP BY STATUS ");

            }
            pstmt = connection.prepareStatement(query.toString());
            logger.debug("QUERY for getValidStatusChartForMerchant " +pstmt.toString());
            //pstmt.setString(1, merchantId);
            if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, currency);
                counter++;
            }
            if (functions.isValueNull(payBrand))
            {
                pstmt.setString(counter, payBrand);
                counter++;
            }
            if (functions.isValueNull(payMode))
            {
                pstmt.setString(counter, payMode);
                counter++;
            }
            Date date5= new Date();
            logger.error("getValidStatusChartForMerchant query starts######## "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getValidStatusChartForMerchant query ends######## " + new Date().getTime());
            logger.error("getValidStatusChartForMerchant query diff######## "+(new Date().getTime()-date5.getTime()));
            logger.error("getValidStatusChartForMerchant query ######## "+pstmt.toString());
            while (rs.next())
            {
                hashMapValidStatusChart.put(rs.getString("statusName"), rs.getString("countStatus"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getValidStatusChartForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapValidStatusChart;
    }
    public  String getMonthlyTransactionDetailsForMerchant(String merchantId, DateVO dateVO, String currency, String payBrand, String payMode)
    {
        logger.error("Entering getMonthlyTransactionDetailsForMerchant method....");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        JSONObject obj = new JSONObject();

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";


        try
        {
            connection = Database.getRDBConnection();
            //String query = "SELECT (UPPER(currency)) AS currencyName, SUM(amount) AS transamount FROM transaction_common AS tc WHERE tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND  toid =?";
            String query = "SELECT (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount FROM "+tableName+" AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =?";
            query = query + " AND FROM_UNIXTIME(dtstamp) BETWEEN '" + dateVO.getStartDate().split(" ")[0] + "' AND '" + dateVO.getEndDate().split(" ")[0] + "'";

            if (functions.isValueNull(currency))
            {
                query = query + " AND currency = '" + currency + "'";
            }
            if (functions.isValueNull(payBrand))
            {
                query = query + " AND ct.cardtypeid = '" + payBrand + "'";
            }
            if (functions.isValueNull(payMode))
            {
                query = query + " AND pt.paymodeid = '" + payMode + "'";
            }
            query = query + " GROUP BY currency";

            pstmt = connection.prepareStatement(query);
            logger.debug("QUERY for getMonthlyTransactionDetailsForMerchant " + pstmt.toString());
            pstmt.setString(1, merchantId);
            Date date5= new Date();
            logger.error("getMonthlyTransactionDetailsForMerchant query starts######## "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getMonthlyTransactionDetailsForMerchant query ends###### " + new Date().getTime());
            logger.error("getMonthlyTransactionDetailsForMerchant query diff###### " + (new Date().getTime() - date5.getTime()));
            logger.error("getMonthlyTransactionDetailsForMerchant query ###### "+pstmt);
            while (rs.next())
            {
                Float amount = rs.getFloat("Amount");
                Float Captureamount = rs.getFloat("Captureamount");
                Float TotalAmount = (amount + Captureamount);
                if(TotalAmount != 0.0)
                {
                    obj.put(rs.getString("currencyName"), Float.toString(TotalAmount));
                }

            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getMonthlyTransactionDetailsForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return obj.toString().replaceAll("\\}", "").replaceAll("\\{", "");
    }

    public  StringBuffer getMonthlyTransactionDetailsForMerchantNew(String merchantId, DateVO dateVO, String currency, String payBrand, String payMode,String dashboard_value,String status)
    {
        logger.error("Entering getMonthlyTransactionDetailsForMerchant method....");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        JSONObject obj = new JSONObject();
        StringBuffer jsonBuffer = new StringBuffer();
        String query2="";

        String tableName ="transaction_common";

        if(functions.isValueNull(payMode) && payMode.equalsIgnoreCase("32"))
            tableName = "transaction_card_present";
        String label = "";
        String lCurrency = "";
        String finalValue ="";

        try
        {
            connection = Database.getRDBConnection();
            DateVO lastweek= dateManager.getDateRangeNew("Last seven days");
            String datetimeArray[] = lastweek.getStartDate().split(" ");
            String mm = String.valueOf(Integer.parseInt(datetimeArray[0].split("-")[1]) - 1);
            String lastweekstartdtstmp = Functions.converttomillisec(mm,datetimeArray[0].split("-")[2],datetimeArray[0].split("-")[0],datetimeArray[1].split(":")[0],datetimeArray[1].split(":")[1],datetimeArray[1].split(":")[2]);

            String datetimeArray1[] = lastweek.getEndDate().split(" ");
            String mm1 = String.valueOf(Integer.parseInt(datetimeArray1[0].split("-")[1]) - 1);
            String lastweekenddtstmp = Functions.converttomillisec(mm1,datetimeArray1[0].split("-")[2],datetimeArray1[0].split("-")[0],datetimeArray1[1].split(":")[0],datetimeArray1[1].split(":")[1],datetimeArray1[1].split(":")[2]);

            if ((functions.isValueNull(dashboard_value)) ||(functions.isValueNull(dateVO.getDateLabel()) && "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel())  || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel())))
            {
                query2 = "SELECT DATE(from_unixtime(dtstamp)) as lDate,";
            }
            else if ((functions.isValueNull(dateVO.getDateLabel()) &&  "Last six months".equalsIgnoreCase(dateVO.getDateLabel())))
            {
                query2 = "SELECT  DATE_FORMAT(FROM_UNIXTIME(dtstamp),'%Y-%m') as lDate,";
            }
            else
            {
                query2 = "SELECT DATE(from_unixtime(dtstamp)) as lDate,";
            }

            if (functions.isValueNull(dashboard_value) || (functions.isValueNull(dateVO.getDateLabel()) || "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last six months".equalsIgnoreCase(dateVO.getDateLabel()) ) && !functions.isValueNull(status)  )
            {
                query2 = query2 + " (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ";

                query2= query2+ " FROM " + tableName + " AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =? ";

                if (functions.isValueNull(dashboard_value))
                {
                    query2 = query2 + " AND dtstamp BETWEEN '" + lastweekstartdtstmp + "' AND '" + lastweekenddtstmp + "'";
                }
                else
                {
                    String datetimeArray2[] = dateVO.getStartDate().split(" ");
                    String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                    String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                    String datetimeArray3[] = dateVO.getEndDate().split(" ");
                    String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                    String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                    query2 = query2 + " AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" + dateenddtstmp + "'";
                }
            }

            else if ((functions.isValueNull(status) && "sales".equalsIgnoreCase(status)))
            {
                query2 = query2 + " (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ";

                query2= query2+ " FROM " + tableName + " AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =? ";

                if(functions.isValueNull(dateVO.getDateLabel()))
                {
                    String datetimeArray2[] = dateVO.getStartDate().split(" ");
                    String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                    String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                    String datetimeArray3[] = dateVO.getEndDate().split(" ");
                    String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                    String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                    query2 = query2 + " AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" +dateenddtstmp + "'";
                }
                else{
                    query2 = query2 + " AND dtstamp BETWEEN '" + lastweekstartdtstmp + "' AND '" + lastweekenddtstmp + "'";
                }
            }
            else if ( (functions.isValueNull(status) && "payout".equalsIgnoreCase(status)) )
            {
                query2 = query2 + " (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'payoutsuccessful' THEN payoutamount ELSE 0 END ) AS payoutAmount";
                query2= query2+ " FROM " + tableName + " AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =? ";

                if(functions.isValueNull(dateVO.getDateLabel()))
                {
                    String datetimeArray2[] = dateVO.getStartDate().split(" ");
                    String mm2 = String.valueOf(Integer.parseInt(datetimeArray2[0].split("-")[1]) - 1);
                    String datestartdtstmp = Functions.converttomillisec(mm2,datetimeArray2[0].split("-")[2],datetimeArray2[0].split("-")[0],datetimeArray2[1].split(":")[0],datetimeArray2[1].split(":")[1],datetimeArray2[1].split(":")[2]);

                    String datetimeArray3[] = dateVO.getEndDate().split(" ");
                    String mm3 = String.valueOf(Integer.parseInt(datetimeArray3[0].split("-")[1]) - 1);
                    String dateenddtstmp = Functions.converttomillisec(mm3,datetimeArray3[0].split("-")[2],datetimeArray3[0].split("-")[0],datetimeArray3[1].split(":")[0],datetimeArray3[1].split(":")[1],datetimeArray3[1].split(":")[2]);
                    query2 = query2 + " AND dtstamp BETWEEN '" + datestartdtstmp + "' AND '" +dateenddtstmp + "'";
                }
                else{
                    query2 = query2 + " AND dtstamp BETWEEN '" + lastweekstartdtstmp + "' AND '" + lastweekenddtstmp + "'";
                }

            }
            else
            {
                query2 = query2 + " (UPPER(currency)) AS currencyName, SUM(CASE WHEN tc.Status  = 'authsuccessful' THEN amount ELSE 0 END ) AS Amount,SUM(CASE WHEN tc.Status IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed') THEN captureamount ELSE 0 END) AS Captureamount ";
                query2= query2+ " FROM " + tableName + " AS tc, payment_type AS pt , card_type AS ct  WHERE tc.paymodeid=pt.paymodeid AND ct.cardtypeid=tc.cardtypeid AND toid =? ";
                query2 = query2 + " AND dtstamp BETWEEN '" + lastweekstartdtstmp + "' AND '" + lastweekenddtstmp + "'";
            }
            if (functions.isValueNull(currency))
            {
                query2 = query2 + " AND currency = '" + currency + "'";
            }
            if (functions.isValueNull(payBrand))
            {
                query2 = query2 + " AND ct.cardtypeid = '" + payBrand + "'";
            }
            if (functions.isValueNull(payMode))
            {
                query2 = query2 + " AND pt.paymodeid = '" + payMode + "'";
            }

            if ((functions.isValueNull(dashboard_value)) || (functions.isValueNull(dateVO.getDateLabel()) && ("today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel())))  && (functions.isValueNull(status)  || !functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }
            else if ( (functions.isValueNull(dateVO.getDateLabel()) && ("Last six months".equalsIgnoreCase(dateVO.getDateLabel()))) && (functions.isValueNull(status) || !functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,MONTH (FROM_UNIXTIME(dtstamp))";
            }
            else if ((functions.isValueNull(status)))
            {
                query2 = query2 + " GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }
            else
            {
                query2= query2 +" GROUP BY currency,DATE(from_unixtime(dtstamp))";
            }

            logger.error("Query for getMonthlyTransactionDetailsForMerchantNew+++++ "+query2);
            pstmt = connection.prepareStatement(query2);

            pstmt.setString(1, merchantId);
            Date date5 = new Date();
            logger.error("getMonthlyTransactionDetailsForMerchantNew query starts######## " + date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getMonthlyTransactionDetailsForMerchantNew query ends###### " + new Date().getTime());
            logger.error("getMonthlyTransactionDetailsForMerchantNew query diff###### " + (new Date().getTime() - date5.getTime()));
            logger.error("getMonthlyTransactionDetailsForMerchantNew query ###### " + pstmt);

            while (rs.next())
            {
                Float amount = 0.0f;
                Float Captureamount = 0.0f;
                Float TotalAmount = 0.0f;
                Float Payoutamount = 0.0f;
                if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                {
                    Payoutamount = rs.getFloat("payoutAmount");
                    obj.put(rs.getString("lDate") + "_" + rs.getString("currencyName"), Float.toString(Payoutamount));
                }
                else
                {
                    amount = rs.getFloat("Amount");
                    Captureamount = rs.getFloat("Captureamount");
                    TotalAmount = (amount + Captureamount);
                    obj.put(rs.getString("lDate") + "_" + rs.getString("currencyName"), Float.toString(TotalAmount));
                }
            }
            logger.error("Json Data for Bar---"+obj);

            JSONObject jsonObject = new JSONObject(obj.toString());
            Iterator<String> keys = jsonObject.sortedKeys();
            while(keys.hasNext())
            {
                String key = keys.next();
                if(key.contains("_"))
                {
                    String sArray[]=key.split("_");
                    label = sArray[0];
                    lCurrency = sArray[1];
                }

                Month month= Month.of(Integer.parseInt(label.split("-")[1])) ;
                String monthname= month.toString().substring(0,3);
                String xlabels= monthname+"-"+label.split("-")[0];

                if ((functions.isValueNull(dashboard_value)) || "today".equalsIgnoreCase(dateVO.getDateLabel()) || "Last seven days".equalsIgnoreCase(dateVO.getDateLabel()) || "Current month".equalsIgnoreCase(dateVO.getDateLabel()) || "Last month".equalsIgnoreCase(dateVO.getDateLabel()) && (functions.isValueNull(status) || !functions.isValueNull(status)))
                {
                    finalValue = "'" + lCurrency + "'" + ":'" + jsonObject.getString(label + "_" + lCurrency) + "'";
                    jsonBuffer.append("{x:'" + label + "'," + finalValue + "},");
                }
                else if ("Last six months".equalsIgnoreCase(dateVO.getDateLabel()) && (functions.isValueNull(status) || !functions.isValueNull(status)) )
                {
                    finalValue = "'" + lCurrency + "'" + ":'" + jsonObject.getString(label + "_" + lCurrency) + "'";
                    jsonBuffer.append("{x:'" + xlabels + "'," + finalValue + "},");
                }
                else if (functions.isValueNull(status))
                {
                    finalValue = "'" + lCurrency + "'" + ":'" + jsonObject.getString(label + "_" + lCurrency) + "'";
                    jsonBuffer.append("{x:'" + label + "'," + finalValue + "},");
                }
            }

            if (jsonBuffer.length()==0)
            {
                finalValue = "'" + lCurrency + "'" + ":'" + " " + "'";
                jsonBuffer.append("{x:'" + " " + "'," + finalValue + "},");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getMonthlyTransactionDetailsForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return jsonBuffer;
    }

    public List<String> getValidCurrencyListForMerchant(String merchantId)
    {
        logger.error("Entering getValidCurrencyListForMerchant method...");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        List<String> currencyList = new ArrayList();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT DISTINCT (UPPER(currency)) AS currencyName FROM transaction_common WHERE STATUS IN ('markedforreversal' ,'chargeback','capturesuccess' ,'settled','reversed','authsuccessful') AND toid =? GROUP BY currency";
            pstmt = connection.prepareStatement(query);
            logger.debug("QUERY for getValidCurrencyListForMerchant " +pstmt.toString());
            pstmt.setString(1, merchantId);
            Date date5= new Date();
            logger.error("getValidCurrencyListForMerchant query starts####### "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getValidCurrencyListForMerchant query ends ####### " + new Date().getTime());
            logger.error("getValidCurrencyListForMerchant query diff ####### " + (new Date().getTime() - date5.getTime()));
            logger.error("getValidCurrencyListForMerchant query  ####### "+pstmt.toString());
            while (rs.next())
            {
                currencyList.add(rs.getString("currencyName"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getValidCurrencyListForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return currencyList;
    }
    public HashMap<String, String> getValidPayBrandListForMerchant(String merchantId)
    {
        logger.error("getValidPayBrandListForMerchant method......");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        HashMap<String, String> hashMapPayBrandDetails = new HashMap<>();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT DISTINCT (ct.cardtypeid) AS cardTypeId ,ct.cardType AS cardType FROM card_type AS ct, transaction_common AS tc WHERE tc.cardtypeid=ct.cardtypeid AND toid=? ORDER BY cardType ASC";
            //String query1 = query + (" ORDER BY  cardType ASC  ");
            pstmt = connection.prepareStatement(query);
            logger.debug("QUERY for getValidPayBrandListForMerchant " +pstmt.toString());
            pstmt.setString(1, merchantId);
            Date date5= new Date();
            logger.error("getValidPayBrandListForMerchant query starts####### "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getValidPayBrandListForMerchant query ends####### " + new Date().getTime());
            logger.error("getValidPayBrandListForMerchant query diff####### " + (new Date().getTime() - date5.getTime()));
            logger.error("getValidPayBrandListForMerchant query ####### "+pstmt.toString());
            while (rs.next())
            {
                hashMapPayBrandDetails.put(rs.getString("cardTypeId"), rs.getString("cardType"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getValidPayBrandListForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapPayBrandDetails;
    }
    public HashMap<String, String> getValidPayModeListForMerchant(String merchantId)
    {
        logger.error("Entering getValidPayModeListForMerchant method...");
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        HashMap<String, String> hashMapPayModeDetails = new HashMap<>();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT DISTINCT(pt.paymodeid) AS payModeId, pt.paymentType AS paymentType FROM payment_type AS pt, transaction_common AS tc WHERE tc.paymodeid=pt.paymodeid AND toid=? ORDER BY paymentType ASC ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, merchantId);
            logger.debug("QUERY for getValidPayModeListForMerchant " +pstmt.toString());
            Date date5= new Date();
            logger.error("getValidPayModeListForMerchant query starts######## "+date5.getTime());
            rs = pstmt.executeQuery();
            logger.error("getValidPayModeListForMerchant query ends######## " + new Date().getTime());
            logger.error("getValidPayModeListForMerchant query diff######## " + (new Date().getTime() - date5.getTime()));
            logger.error("getValidPayModeListForMerchant query ######## "+pstmt.toString());
            while (rs.next())
            {
                hashMapPayModeDetails.put(rs.getString("payModeId"), rs.getString("paymentType"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getValidPayModeListForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapPayModeDetails;
    }
    public HashMap<String, String> getCardPresentPayModeListForMerchant(String merchantId)
    {
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        HashMap<String, String> hashMapPayModeDetails = new HashMap<>();
        try
        {
            connection = Database.getRDBConnection();
            String query = "SELECT DISTINCT(pt.paymodeid) AS payModeId, pt.paymentType AS paymentType FROM payment_type AS pt, transaction_common AS tc WHERE tc.paymodeid=pt.paymodeid AND toid=? ORDER BY paymentType ASC ";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, merchantId);
            logger.debug("QUERY for getValidPayModeListForMerchant " +pstmt.toString());
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                hashMapPayModeDetails.put(rs.getString("payModeId"), rs.getString("paymentType"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while getting getValidPayModeListForMerchant data ", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        return hashMapPayModeDetails;
    }


    public static TreeMap<String, String> getAccountsListOnMemberid( String memberid)
    {
        Connection con = null;
        PreparedStatement psmt = null;
        Functions functions = new Functions();
        TreeMap<String, String> map = new TreeMap();
        int counter = 1;
        StringBuilder query = new StringBuilder("SELECT ga.accountid,ga.merchantid,gt.currency FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt WHERE mam.accountid = ga.accountid AND ga.pgtypeid = gt.pgtypeid");
        try
        {
            con = Database.getConnection();
            if (functions.isValueNull(memberid))
            {
                query.append(" and mam.memberid = ?");
            }

            psmt = con.prepareStatement(query.toString());

            if (functions.isValueNull(memberid))
            {
                psmt.setString(counter, memberid);
                counter++;
            }
            ResultSet rs = psmt.executeQuery();
            logger.debug("Query psmt:::::::::::::::"+psmt.toString());
            while (rs.next())
            {
                map.put(rs.getString("accountid"),rs.getString("accountid")+"-"+rs.getString("merchantid")+"-"+rs.getString("currency"));
            }

        }
        catch (SystemError s)
        {
            logger.error("System error while performing select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(con);
        }
        return map;
    }

    public String getValidValue(String Value)
    {
        if (Value == null)
            return "0.00";
        else
        {
            return Value;
        }
    }

    public MerchantFraudAccountVO getMerchantPartnerFraudServiceConfigurationDetails (String partnerid)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        MerchantFraudAccountVO merchantFraudAccountVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT id, partnerid, fsaccountid, isActive, FROM_UNIXTIME (dtstamp) FROM `partner_fsaccounts_mapping` WHERE partnerid=?");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,partnerid);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                merchantFraudAccountVO=new MerchantFraudAccountVO();
                merchantFraudAccountVO.setPartnerid(rs.getString("partnerid"));
                merchantFraudAccountVO.setFsaccountid(rs.getString("fsaccountid"));
                merchantFraudAccountVO.setPisActive(rs.getString("isActive"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting partner merchant fraud account details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantPartnerFraudServiceConfigurationDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting partner merchant fraud account details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantPartnerFraudServiceConfigurationDetails()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantFraudAccountVO;
    }

    public MerchantFraudAccountVO getMerchantFraudServiceConfigurationDetails1 (String memberId)throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        MerchantFraudAccountVO merchantFraudAccountVO=null;
        try
        {
            con= Database.getRDBConnection();
            StringBuilder query =new StringBuilder("SELECT merchantfraudserviceid,memberid,m.fssubaccountid,m.isactive,m.isVisible,FROM_UNIXTIME(m.dtstamp) AS creationon,m.TIMESTAMP AS lastupdated\n" +
                    " FROM merchant_fssubaccount_mappping AS m, `fsaccount_subaccount_mapping` AS f, `fraudsystem_account_mapping` AS fa, `fraudsystem_master` AS fm\n" +
                    " WHERE memberid=? AND m.fssubaccountid=f.`fssubaccountid` AND f.`fsaccountid`=fa.`fsaccountid` AND fa.`fsid`=fm.`fsid` AND fm.`fsname`!='pz'");
            pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,memberId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                merchantFraudAccountVO=new MerchantFraudAccountVO();
                merchantFraudAccountVO.setMerchantFraudAccountId(rs.getString("merchantfraudserviceid"));
                merchantFraudAccountVO.setMemberId(rs.getString("memberid"));
                merchantFraudAccountVO.setFsSubAccountId(rs.getString("fssubaccountid"));
                merchantFraudAccountVO.setIsActive(rs.getString("isactive"));
                merchantFraudAccountVO.setCreationOn(rs.getString("creationon"));
                merchantFraudAccountVO.setLastUpdated(rs.getString("lastupdated"));
                merchantFraudAccountVO.setIsVisible(rs.getString("isvisible"));
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting merchant fraud account details",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantFraudServiceConfigurationDetails1()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting merchant fraud account details::",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMerchantFraudServiceConfigurationDetails1()", null, "Common", "Sql exception while connecting to Charge's table", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantFraudAccountVO;
    }

    public boolean isMemberExist(String memberid) throws SystemError
    {
        Connection conn         = null;
        PreparedStatement pstmt = null;
        ResultSet rs            = null;
        String mappedPartnerId  = null;
        boolean result          = false;
        try
        {
            conn = Database.getRDBConnection();
            logger.debug("check isMember method");
            String selquery = "SELECT * FROM members WHERE memberid = ? ";

            pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                result=true;
            }

        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMemberUser method: ", se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Exception in isMemberUser method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }
    public Hashtable getMemberTemplateDetails(String memberId)
    {
        Hashtable memberTemplateDetail=new Hashtable();
        Connection conn=null;
        ResultSet resultSet = null;
        try
        {
            conn=Database.getConnection();
            String qry="select memberid,value from template_preferences where memberid=? AND name=?";
            PreparedStatement preparedStatement= conn.prepareStatement(qry);
            preparedStatement.setString(1,memberId);
            preparedStatement.setString(2,"MERCHANTLOGONAME");
            logger.error("getMemberTemplateDetails---" + preparedStatement);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                memberTemplateDetail.put(resultSet.getString("memberid"), resultSet.getString("value"));
            }
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError",systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION",e);
        }
        finally
        {
            //Database.closeConnection(connection);
            Database.closeConnection(conn);
        }
        return memberTemplateDetail;
    }
    //****getPartnerTemplate preferences****//
    public Map<String, Object>  getMemberPartnerTemplateDetails(String partnerid) throws PZDBViolationException
    {
        Connection con              = null;
        ResultSet resultSet         = null;
        PreparedStatement pstmt     = null;
        Map<String, Object> merchantDetailsVOMap = new HashMap<String, Object>();
        logger.debug("Inside saved Template Details:::");
        try
        {
            con             = Database.getRDBConnection();
            String query    = "select name,value from partner_template_preference where partnerid= ? ";
            pstmt           = con.prepareStatement(query);
            pstmt.setString(1, partnerid);
            resultSet       = pstmt.executeQuery();
            while (resultSet.next())
            {
                if (TemplatePreference.getEnum(resultSet.getString("name")) != null && !TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside First Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getString("value"));
                }
                else if (TemplatePreference.getEnum(resultSet.getString("name")) != null && TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name().equals(TemplatePreference.getEnum(resultSet.getString("name")).name()))
                {
                    logger.debug("inside 2nd Condition:::" + resultSet.getString("name") + " enum" + TemplatePreference.getEnum(resultSet.getString("name")).toString());
                    merchantDetailsVOMap.put(TemplatePreference.getEnum(resultSet.getString("name")).toString(), resultSet.getBytes("value"));
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", e);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error : ", systemError);
            PZExceptionHandler.raiseDBViolationException(PartnerDAO.class.getName(), "getMemberTemplateDetails()", null, "Common", "Exception while getting template information", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return merchantDetailsVOMap;
    }
    //****Merchant Dashboard Methods****//
    public String getMerchantCompanyName(String memberId)
    {
        Connection conn = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        String merchant_organization = "";

        try
        {
            // conn = Database.getConnection();
            conn=Database.getRDBConnection();
            String query = "select company_name from members where memberid = "+memberId;
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            if (rs.next())
            {
                merchant_organization = rs.getString("company_name");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----",e);
        }
        catch (SystemError e)
        {
            logger.error("SystemError----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
        return merchant_organization;
    }

    public Hashtable getMembersFlag(String memberid)
    {
        Hashtable hash = null;

        StringBuffer query  = new StringBuffer("SELECT company_name,contact_emails,m.memberid,invoicetemplate,isPoweredBy,template,activation,icici,aptprompt,reserves,chargeper,fixamount,reserve_reason,accountid,reversalcharge,withdrawalcharge,chargebackcharge,taxper,isPharma,isValidateEmail,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,activation,icici,haspaid,isservice,hralertproof,autoredirect,vbv,hrparameterised,masterCardSupported,check_limit,partnerid,iswhitelisted,card_transaction_limit,card_check_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,isrefund,refunddailylimit,agentId,isIpWhitelisted,autoSelectTerminal,isPODRequired,maxScoreAllowed,maxScoreAutoReversal,weekly_amount_limit,isappmanageractivate,iscardregistrationallowed,is_recurring,isRestrictedTicket,isTokenizationAllowed,tokenvaliddays,isAddrDetailsRequired,blacklistTransaction,flightMode,onlineFraudCheck,isSplitPayment,splitPaymentType,isExcessCaptureAllowed,ispcilogo,refundallowed_days,chargebackallowed_days,isCardEncryptionEnable,is_rest_whitelisted,smsactivation,customersmsactivation,emailLimitEnabled,ip_whitelist_invoice,binService,expDateOffset,supportSection,card_whitelist_level,multiCurrencySupport,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access,settings_merchant_config_access,settings_fraudrule_config_access,merchantmgt_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access,accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access, settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access,transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,transmgt_payout_transactions,invoice_generate_access,invoice_history_access,tokenmgt_registration_history_access,tokenmgt_register_card_access,merchantmgt_user_management_access,notificationUrl,termsUrl,ip_validation_required,ispartnerlogo,ismerchantlogo,privacyPolicyUrl,binRouting,vbvLogo,masterSecureLogo,personal_info_display,personal_info_validation,hosted_payment_page,consent,isSecurityLogo,isMultipleRefund,settings_whitelist_details,settings_blacklist_details,emi_configuration,emiSupport,isPartialRefund,internalFraudCheck,card_velocity_check,merchant_order_details,limitRouting,checkoutTimer,checkoutTimerTime,marketplace,rejected_transaction,virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification,ispurchase_inquiry_blacklist,ispurchase_inquiry_refund,isfraud_determined_refund,isfraud_determined_blacklist,isdispute_initiated_refund,isdispute_initiated_blacklist,isstop_payment_blacklist,isexception_file_listing_blacklist,merchantRegistrationMail,merchantChangePassword,merchantChangeProfile,transactionSuccessfulMail,transactionFailMail,transactionCapture,transactionPayoutSuccess,transactionPayoutFail,refundMail,chargebackMail,transactionInvoice,cardRegistration,payoutReport,monitoringAlertMail,monitoringSuspensionMail,highRiskRefunds,fraudFailedTxn,dailyFraudReport,customerTransactionSuccessfulMail,customerTransactionFailMail,customerTransactionPayoutSuccess,customerTransactionPayoutFail,customerRefundMail,customerTokenizationMail,isUniqueOrderIdRequired,daily_payout_amount_limit,weekly_payout_amount_limit,monthly_payout_amount_limit,payout_amount_limit_check,emailTemplateLang,successReconMail,refundReconMail,chargebackReconMail,payoutReconMail,isMerchantLogoBO,supportNoNeeded,mc.cardExpiryDateCheck,mc.payoutRouting,payoutNotification,inquiryNotification,mc.vpaAddressLimitCheck,mc.vpaAddressDailyCount,mc.vpaAddressAmountLimitCheck,mc.vpaAddressDailyAmountLimit,mc.payoutBankAccountNoLimitCheck,mc.bankAccountNoDailyCount,mc.payoutBankAccountNoAmountLimitCheck,mc.bankAccountNoDailyAmountLimit,isShareAllowed,isSignatureAllowed,isSaveReceiptAllowed,defaultLanguage,isDomainWhitelisted,mc.customerIpLimitCheck,mc.customerIpDailyCount,mc.customerIpAmountLimitCheck,mc.customerIpDailyAmountLimit,mc.customerNameLimitCheck,mc.customerNameDailyCount,mc.customerNameAmountLimitCheck,mc.customerNameDailyAmountLimit,mc.customerEmailLimitCheck,mc.customerEmailDailyCount,mc.customerEmailAmountLimitCheck,mc.customerEmailDailyAmountLimit,mc.customerPhoneLimitCheck,mc.customerPhoneDailyCount,mc.customerPhoneAmountLimitCheck,mc.customerPhoneDailyAmountLimit,paybylink,mc.isOTPRequired,mc.isCardStorageRequired,mc.vpaAddressMonthlyCount,mc.vpaAddressMonthlyAmountLimit,mc.customerEmailMonthlyCount,mc.customerEmailMonthlyAmountLimit,mc.customerPhoneMonthlyCount,mc.customerPhoneMonthlyAmountLimit,mc.bankAccountNoMonthlyCount,mc.bankAccountNoMonthlyAmountLimit FROM members AS m JOIN merchant_configuration AS mc ON m.memberid=mc.memberid WHERE m.memberid=?");

        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        try
        {
            Functions f = new Functions();
            con         = Database.getRDBConnection();
            if (f.isValueNull(memberid))
            {
                p       = con.prepareStatement(query.toString());
                p.setString(1,memberid);
                Date d2 = new Date();
                hash    = Database.getHashFromResultSet(p.executeQuery());
                logger.debug("query::::"+p);

            }


        }
        catch (SystemError se)
        {
            logger.error("SystemError::::", se);
        }
        catch (Exception e)
        {
            logger.error("Exception::::",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                logger.error("SQL Exception::::",e);
            }
        }

        return (Hashtable)hash.get("1");
    }

    public MerchantDetailsVO getPartnerAndMerchantDetails(String memberid ,String pid)
    {
        MerchantDetailsVO merchantDetailsVO = null;
        ResultSet res = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        logger.error("inside getPartnerAndMerchantDetails ");
        logger.error("memberid :::"+memberid + " pid :::" +pid);

        try{

            con=Database.getRDBConnection();
            System.out.println("inside getPartnerAndMerchantDetails ");
            String query = "SELECT m.*,p.partnerId,p.partnerName,p.logoName,p.template AS partnertemplate,mc.checkoutTimer,mc.checkoutTimerTime FROM members AS m, partners AS p,merchant_configuration AS mc WHERE m.memberid = ? AND m.memberid = mc.memberid AND m.partnerId = ? ";

            pstmt=con.prepareStatement(query);
            pstmt.setString(1,memberid);
            pstmt.setString(2,pid);
            res=pstmt.executeQuery();
            if(res.next())
            {
                merchantDetailsVO = new MerchantDetailsVO();
                merchantDetailsVO.setPharma(res.getString("isPharma"));
                merchantDetailsVO.setTemplate(res.getString("template"));
                merchantDetailsVO.setPoweredBy(res.getString("isPoweredBy"));
                merchantDetailsVO.setMerchantLogo(res.getString("ismerchantlogo"));
                merchantDetailsVO.setConsentFlag(res.getString("consent"));
                merchantDetailsVO.setCheckoutTimerFlag(res.getString("checkoutTimer"));
                merchantDetailsVO.setCheckoutTimerTime(res.getString("checkoutTimerTime"));
                merchantDetailsVO.setSupportSection(res.getString("supportSection"));
                merchantDetailsVO.setSupportNoNeeded(res.getString("supportNoNeeded"));
                merchantDetailsVO.setContact_emails(res.getString("contact_emails"));
                merchantDetailsVO.setPartnerLogoFlag(res.getString("ispartnerlogo"));
                merchantDetailsVO.setPersonalInfoDisplay(res.getString("personal_info_display"));
                merchantDetailsVO.setMerchantOrderDetailsDisplay(res.getString("merchant_order_details"));

            }
            logger.debug("query for getPartnerAndMerchantDetails ----"+pstmt);
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ", systemError);

        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        logger.debug("Leaving getMemberDetails");

        return merchantDetailsVO;

    }

    public String getTotalPayoutAmount(String memeberId){
        String totalPayoutAmount     = "0.00";
        PreparedStatement psmt   = null;
        ResultSet resultSet      = null;
        Connection conn          = null;
        try{
            conn                = Database.getRDBConnection();
            StringBuffer query  = new StringBuffer("SELECT totalPayoutAmount FROM merchant_configuration WHERE memberid= ?");
            psmt                =  conn.prepareStatement(query.toString());
            psmt.setString(1, memeberId);
            resultSet = psmt.executeQuery();
            logger.error("getTotalPayoutAmount ===== " + psmt);

            while (resultSet.next())
            {
                totalPayoutAmount = String.valueOf(resultSet.getDouble("totalPayoutAmount"));
            }

        }catch (Exception e){
            logger.error("Exception::::: 3456", e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting PayoutBalance");
        }finally
        {
            Database.closeResultSet(resultSet);
            Database.closeConnection(conn);
        }
        return  totalPayoutAmount;
    }


    public String getMemberDetailsForOtpVAlidationActivityTracker(String userid) throws PZDBViolationException
    {
        logger.error("Entering getMemberDetailsForOtpVAlidationActivityTracker Details============"+userid);

        Connection          conn                = null;
        PreparedStatement   pstmt               = null;
        ResultSet           res                 = null;
        String loginName="";
        try
        {
            conn = Database.getRDBConnection();
            if (isLogEnabled)
            {
                logger.debug("Entering getMember Details");
            }

           String query="SELECT login FROM member_users WHERE userid=? ";


            pstmt       = conn.prepareStatement(query);
            pstmt.setString(1,userid);
            if (isLogEnabled)
            {
                logger.debug("merchant Details loaded" + pstmt);
            }
            res = pstmt.executeQuery();
            if (res.next())
            {
                loginName=res.getString("login");

            }
        }
        catch(SystemError systemError)
        {
            logger.error("Leaving Merchants throwing System Exception as System Error :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(), "getMemberDetailsForOtpVAlidationActivityTracker()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("Leaving Merchants throwing SQL Exception as System Error :::: ",e);
            PZExceptionHandler.raiseDBViolationException(MerchantDAO.class.getName(),"getMemberDetailsForOtpVAlidationActivityTracker()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeResultSet(res);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return loginName;
    }


}
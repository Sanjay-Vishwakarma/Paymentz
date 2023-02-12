package com.merchant.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.merchant.vo.requestVOs.*;
import com.merchant.vo.responseVOs.*;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.Hashtable;

/**
 * Created by Sneha on 9/1/2016.
 */
public class ReadMerchantServiceRequest
{
    private Functions functions = new Functions();
    private static Logger log = new Logger(ReadMerchantServiceRequest.class.getName());

    public CommonValidatorVO readRequestForMerchantLogin(MerchantServiceRequestVO request)
{
    CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
    MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
    merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
    if (functions.isValueNull(request.getMerchant().getNewPassword()))
        merchantDetailsVO.setPassword(request.getMerchant().getNewPassword());
    commonValidatorVO.setParetnerId(request.getAuthentication().getPartnerId());
    if (functions.isValueNull(request.getAuthentication().getsKey()))
        merchantDetailsVO.setKey(request.getAuthentication().getsKey());
    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
    return commonValidatorVO;
}
    public CommonValidatorVO readRequestForverifymail(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }
    public CommonValidatorVO readRequestForSendReceiptEmail(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        commonValidatorVO=populateAuthenticationDetails(commonValidatorVO,request);
        if(request.getCustomer()!=null)
        {
            genericAddressDetailsVO.setEmail(request.getCustomer().getEmail());
        }

        commonValidatorVO.setTrackingid(request.getPaymentId());
        log.error("PaymentId-------------->"+request.getPaymentId());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

        return commonValidatorVO;

    }

    public MerchantServiceRequestVO readRequestForSendReceiptEmail(MerchantServiceRequest request)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        merchantServiceRequestVO.setCustomer(getCustomerVO(request.getCustomer()));
        merchantServiceRequestVO.setPaymentId(request.getPaymentId());

        return merchantServiceRequestVO;

    }

    public CommonValidatorVO readRequestForSendReceiptSms(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        commonValidatorVO=populateAuthenticationDetails(commonValidatorVO,request);
        if(request.getCustomer()!=null)
        {
            genericAddressDetailsVO.setPhone(request.getCustomer().getPhone());
            genericAddressDetailsVO.setTelnocc(request.getCustomer().getTelnocc());
        }
        commonValidatorVO.setTrackingid(request.getPaymentId());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

        log.error("Telnocc-------------->" + request.getCustomer().getTelnocc());
        log.error("Phone-------------->" + request.getCustomer().getPhone());
        log.error("PaymentId-------------->"+request.getPaymentId());

        return commonValidatorVO;

    }

    public MerchantServiceRequestVO readRequestForSendReceiptSms(MerchantServiceRequest request)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(request.getAuthentication()));
        merchantServiceRequestVO.setCustomer(getCustomerVO(request.getCustomer()));
        merchantServiceRequestVO.setPaymentId(request.getPaymentId());

        return merchantServiceRequestVO;

    }

    public MerchantServiceRequestVO getLoginFormURLRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));

        return merchantServiceRequestVO;
    }

    public CommonValidatorVO readRequestForMerchantSignUp(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, request);
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        //Authentication Details
        merchantDetailsVO.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        merchantDetailsVO.setNewPassword(request.getMerchant().getNewPassword());
        merchantDetailsVO.setConPassword(request.getMerchant().getConPassword());
        merchantDetailsVO.setCompany_name(request.getMerchant().getCompanyName());
        merchantDetailsVO.setWebsite(request.getMerchant().getWebsite());
        merchantDetailsVO.setOtp(request.getMerchant().getOtp());
        merchantDetailsVO.setEtoken(request.getMerchant().getEtoken());
        genericAddressDetailsVO.setFirstname(request.getMerchant().getGivenName());
        genericAddressDetailsVO.setLastname(request.getMerchant().getSurname());
        genericAddressDetailsVO.setSex(request.getMerchant().getSex());

        genericAddressDetailsVO.setEmail(request.getMerchant().getEmail());
        genericAddressDetailsVO.setPhone(request.getMerchant().getPhone());
        genericAddressDetailsVO.setTelnocc(request.getMerchant().getTelcc());
        genericAddressDetailsVO.setCountry(request.getMerchant().getCountry());
        genericAddressDetailsVO.setZipCode(request.getMerchant().getPostcode());
        genericAddressDetailsVO.setBirthdate(request.getMerchant().getBirthDate());
       // genericAddressDetailsVO.setTelnocc(request.getMerchant().getMobilecountrycode());
        //genericAddressDetailsVO.setPhone(request.getMerchant().getMobilenumber());
        merchantDetailsVO.setContact_persons(request.getMerchant().getContactName());
        if (functions.isValueNull(request.getMerchant().getIsMobileVerified()))
            merchantDetailsVO.setIsMobileVerified(request.getMerchant().getIsMobileVerified());

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
       // System.out.println("telcc---"+commonValidatorVO.getAddressDetailsVO().getTelnocc());

        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestGenerateAppOTP(MerchantServiceRequestVO request)
    {

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, request);

        genericAddressDetailsVO.setCountry(request.getMerchant().getCountry());
      //  genericAddressDetailsVO.setTelnocc(request.getMerchant().getMobilecountrycode());
        genericAddressDetailsVO.setPhone(request.getMerchant().getPhone());
        genericAddressDetailsVO.setEmail(request.getMerchant().getEmail());
        log.debug("request-----"+request.getMerchant().getPhone());
        log.debug("checksum-----"+request.getAuthentication().getChecksum());

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

        return commonValidatorVO;
    }

    public MerchantServiceRequestVO getGenerateOTPRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));

        return merchantServiceRequestVO;
    }

    public CommonValidatorVO readVerifyGenerateAppOTP(MerchantServiceRequestVO request)
    {

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, request);

        genericAddressDetailsVO.setCountry(request.getMerchant().getCountry());
      //  genericAddressDetailsVO.setTelnocc(request.getMerchant().getMobilecountrycode());
        genericAddressDetailsVO.setPhone(request.getMerchant().getPhone());
        merchantDetailsVO.setOtp(request.getMerchant().getOtp());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }

    public MerchantServiceRequestVO getMerchantVerifyOTPRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));

        return merchantServiceRequestVO;
    }

    public CommonValidatorVO readRequestForCustomerSignUp(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, request);
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        //Authentication Details
        merchantDetailsVO.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());

        genericAddressDetailsVO.setFirstname(request.getCustomer().getGivenName());
        genericAddressDetailsVO.setLastname(request.getCustomer().getSurname());
        genericAddressDetailsVO.setSex(request.getCustomer().getSex());
        genericAddressDetailsVO.setEmail(request.getCustomer().getEmail());
        genericAddressDetailsVO.setPhone(request.getCustomer().getPhone());
        genericAddressDetailsVO.setCountry(request.getCustomer().getCountry());
        genericAddressDetailsVO.setZipCode(request.getCustomer().getPostcode());
        genericAddressDetailsVO.setBirthdate(request.getCustomer().getBirthDate());


        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }

    public MerchantServiceRequestVO getNewAuthTokenRequest(MerchantServiceRequest request)
    {
        MerchantServiceRequestVO requestVO = new MerchantServiceRequestVO();
        AuthenticationVO authenticationVO = new AuthenticationVO();
        requestVO.setAuthToken(request.getAuthToken());
        authenticationVO.setPartnerId(request.getAuthentication().getPartnerId());
        requestVO.setAuthentication(authenticationVO);

        return requestVO;
    }

    public CommonValidatorVO readRequestForgetNewAuthToken(MerchantServiceRequestVO requestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(requestVO.getAuthToken());
        commonValidatorVO.setParetnerId(requestVO.getAuthentication().getPartnerId());

        return commonValidatorVO;
    }

    public MerchantServiceRequestVO readCustomerSignupRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));
        merchantServiceRequestVO.setCustomer(getCustomerVO(merchantServiceRequest.getCustomer()));

        return merchantServiceRequestVO;
    }

    private CustomerVO getCustomerVO(Customer customer)
    {
        CustomerVO customerVO = new CustomerVO();
        customerVO.setGivenName(customer.getGivenName());
        customerVO.setSurname(customer.getSurname());
        customerVO.setSex(customer.getSex());
        customerVO.setEmail(customer.getEmail());
        customerVO.setPhone(customer.getPhone());
        customerVO.setCountry(customer.getCountry());
        customerVO.setPostcode(customer.getPostcode());
        customerVO.setBirthDate(customer.getBirthDate());
        customerVO.setTelnocc(customer.getTelnocc());
        return customerVO;
    }

    public CommonValidatorVO readRequestToGetMerchantCurrencies(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        if(request.getAuthentication() != null)
        {
            merchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
            transDetailsVO.setChecksum(request.getAuthentication().getChecksum());
        }
        if(request.getMerchant()!=null)
        {
            merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestUpdateMerchantAddress(MerchantServiceRequestVO request)
    {

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        if(request.getAuthentication() != null)
        {
            merchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
            transDetailsVO.setChecksum(request.getAuthentication().getChecksum());
        }
        if(request.getBillingAddressVO() != null)
        {
            addressDetailsVO.setStreet(request.getBillingAddressVO().getStreet());
            addressDetailsVO.setCity(request.getBillingAddressVO().getCity());
            addressDetailsVO.setState(request.getBillingAddressVO().getState());
            addressDetailsVO.setZipCode(request.getBillingAddressVO().getPostcode());
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public MerchantServiceRequestVO getParamsForMerchantCurrencies(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));

        return merchantServiceRequestVO;
    }

    public CommonValidatorVO readRequestForChangePassword(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        merchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        transDetailsVO.setChecksum(request.getAuthentication().getChecksum());
        merchantDetailsVO.setPassword(request.getMerchant().getNewPassword());
        merchantDetailsVO.setNewPassword(request.getMerchant().getConPassword());
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public MerchantServiceRequestVO readMerchantChangePasswordRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));
        return merchantServiceRequestVO;
    }

    public CommonValidatorVO readRequestForForgetPassword(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }

    public MerchantServiceRequestVO getForgotPasswordParamsAPI(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));

        return merchantServiceRequestVO;
    }


    private CommonValidatorVO populateAuthenticationDetails(CommonValidatorVO commonValidatorVO,MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getMemberId()))
            genericMerchantDetailsVO.setMemberId(merchantServiceRequestVO.getAuthentication().getMemberId());
        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getPartnerId()))
            partnerDetailsVO.setPartnerId(merchantServiceRequestVO.getAuthentication().getPartnerId());
        commonValidatorVO.setParetnerId(merchantServiceRequestVO.getAuthentication().getPartnerId());
        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getChecksum()))
            transDetailsVO.setChecksum(merchantServiceRequestVO.getAuthentication().getChecksum());
        if (functions.isValueNull(merchantServiceRequestVO.getAuthentication().getTerminalId()))
            commonValidatorVO.setTerminalId(merchantServiceRequestVO.getAuthentication().getTerminalId());

        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }


    public MerchantServiceRequestVO getMerchantServiceRequestVO(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();

        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setMerchant(getMerchantVO(merchantServiceRequest.getMerchant()));



        return merchantServiceRequestVO;
    }

    private AuthenticationVO getAuthenticationVO(Authentication authentication)
    {
        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setPartnerId(authentication.getPartnerId());
        authenticationVO.setChecksum(authentication.getChecksum());
        authenticationVO.setTerminalId(authentication.getTerminalId());
        authenticationVO.setMemberId(authentication.getMemberId());
        authenticationVO.setPassword(authentication.getPassword());
        authenticationVO.setsKey(authentication.getsKey());

        return authenticationVO;
    }

    private MerchantVO getMerchantVO(Merchant merchant)
    {

        MerchantVO merchantVO = new MerchantVO();

        merchantVO.setLoginName(merchant.getLoginName());
        merchantVO.setNewPassword(merchant.getNewPassword());
        merchantVO.setConPassword(merchant.getConPassword());
        merchantVO.setBirthDate(merchant.getBirthDate());
        merchantVO.setEmail(merchant.getEmail());
        merchantVO.setCompanyName(merchant.getCompanyName());
        merchantVO.setContactName(merchant.getContactName());
        merchantVO.setGivenName(merchant.getGivenName());
        merchantVO.setCountry(merchant.getCountry());
        merchantVO.setMobilecountrycode(merchant.getMobilecountrycode());
        merchantVO.setOtp(merchant.getOtp());
        merchantVO.setTelcc(merchant.getTelcc());
        merchantVO.setWebsite(merchant.getWebsite());
        merchantVO.setSurname(merchant.getSurname());
        merchantVO.setPostcode(merchant.getPostcode());
        merchantVO.setSex(merchant.getSex());
        merchantVO.setPhone(merchant.getPhone());
        merchantVO.setEtoken(merchant.getEtoken());
        merchantVO.setEmailotp(merchant.getEmailotp());
        merchantVO.setSmsotp(merchant.getSmsotp());
        merchantVO.setMerchanttransactionid(merchant.getMerchanttransactionid());
        merchantVO.setMobilenumber(merchant.getMobilenumber());
        if (functions.isValueNull(merchant.getIsMobileVerified()))
            merchantVO.setIsMobileVerified(merchant.getIsMobileVerified());


        return merchantVO;
    }
    public MerchantServiceRequestVO readMerchantLogoutRequest(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        merchantServiceRequestVO.setAuthentication(getAuthenticationVO(merchantServiceRequest.getAuthentication()));
        merchantServiceRequestVO.setAuthToken(merchantServiceRequest.getAuthToken());
        return merchantServiceRequestVO;
    }
    public CommonValidatorVO readMerchantLogoutRequestJSON(MerchantServiceRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(request.getAuthToken());
        if(request.getAuthentication() != null)
        commonValidatorVO.setParetnerId(request.getAuthentication().getPartnerId());
        return commonValidatorVO;
    }


    public MerchantRequestVO getMerchantRequestVO(MerchantRequest merchantRequest)
    {
        MerchantRequestVO merchantRequestVO = new MerchantRequestVO();

        merchantRequestVO.setAuthentication(getAuthenticationVO(merchantRequest.getAuthentication()));
        merchantRequestVO.setMerchant(getMerchantVO(merchantRequest.getMerchant()));

        return merchantRequestVO;
    }

    public CommonValidatorVO readRequestGenerateAppOTP(MerchantRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

        genericAddressDetailsVO.setPhone(request.getMerchant().getMobilenumber());
        genericAddressDetailsVO.setEmail(request.getMerchant().getEmail());
        genericMerchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        genericMerchantDetailsVO.setTransactionID(request.getMerchant().getMerchanttransactionid());

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO readVerifyOTP(MerchantRequestVO request)
    {

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        merchantDetailsVO.setSmsOtp(request.getMerchant().getSmsotp());
        merchantDetailsVO.setEmailOtp(request.getMerchant().getEmailotp());
        genericAddressDetailsVO.setPhone(request.getMerchant().getMobilenumber());
        genericAddressDetailsVO.setEmail(request.getMerchant().getEmail());
        merchantDetailsVO.setTransactionID(request.getMerchant().getMerchanttransactionid());
        merchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }

    public MerchantResponseFlagsVO getMerchantConfigFlags(String memberId)
    {

        MerchantDAO merchantDAO                         = new MerchantDAO();
        MerchantResponseFlagsVO merchantResponseFlagsVO = new MerchantResponseFlagsVO();
        MemberLimits memberLimits                       = new MemberLimits();
        TransactionLimits transactionLimits             = new TransactionLimits();
        PayoutLimits payoutLimits                       = new PayoutLimits();
        GeneralConfiguration generalConfiguration           = new GeneralConfiguration();
        TransactionConfiguration transactionConfiguration   = new TransactionConfiguration();

        BackOfficeAccessManagement backOfficeAccessManagement = new BackOfficeAccessManagement();
        AccountDetails accountDetails   = new AccountDetails();
        Settings settings               = new Settings();
        TransactionManagement transactionManagement = new TransactionManagement();
        Invoicing invoicing                     = new Invoicing();
        TokenManagement tokenManagement         = new TokenManagement();
        MerchantManagement merchantManagement   = new MerchantManagement();

        TemplateConfiguration templateConfiguration = new TemplateConfiguration();
        TokenConfiguration tokenConfiguration       = new TokenConfiguration();

        FraudDefenderConfiguration fraudDefenderConfiguration = new FraudDefenderConfiguration();
        PurchaseInquiry purchaseInquiry             = new PurchaseInquiry();
        FraudDetermined fraudDetermined             = new FraudDetermined();
        DisputeInitiated disputeInitiated           = new DisputeInitiated();
        ExceptionFileListing exceptionFileListing   = new ExceptionFileListing();
        StopPayment stopPayment                     = new StopPayment();

        WhitelistingConfiguration whitelistingConfiguration     = new WhitelistingConfiguration();
        HRTransactionConfiguration hrTransactionConfiguration   = new HRTransactionConfiguration();
        RefundConfiguration refundConfiguration                 = new RefundConfiguration();
        EmailConfiguration emailConfiguration                   = new EmailConfiguration();
        SMSConfiguration smsConfiguration           = new SMSConfiguration();
        InvoiceConfiguration invoiceConfigurations  = new InvoiceConfiguration();
        FraudConfiguration fraudConfiguration       = new FraudConfiguration();
        SplitTransactionConfiguration splitTransactionConfiguration = new SplitTransactionConfiguration();
        InvoizerConfiguration invoizerConfiguration                 = new InvoizerConfiguration();
        MerchantNotificationCallback merchantNotificationCallback   = new MerchantNotificationCallback();

        Hashtable<String, String> merchantFlags = merchantDAO.getMembersFlag(memberId);

        // Member Limits flags
        String cardLimitCheck           = getMerchantFlagsStringValue(merchantFlags.get("card_check_limit"));
        String cardAmountLimitCheck     = getMerchantFlagsStringValue(merchantFlags.get("card_transaction_limit"));
        String amountLimitCheck         = getMerchantFlagsStringValue(merchantFlags.get("check_limit"));
        String cardVelocityCheck        = merchantFlags.get("card_velocity_check");
        String limitRouting             = merchantFlags.get("limitRouting");
        String payoutAmountLimitCheck   = merchantFlags.get("payout_amount_limit_check");
        String payoutRouting            = merchantFlags.get("payoutRouting");

        // Transaction Limits flags
        String vpaAddressLimitCheck             = merchantFlags.get("vpaAddressLimitCheck");
        String vpaAddressAmountLimitCheck       = merchantFlags.get("vpaAddressAmountLimitCheck");
        String customerIpAmountLimitCheck       = merchantFlags.get("customerIpAmountLimitCheck");
        String customerIpCountLimitCheck        = merchantFlags.get("customerIpLimitCheck");
        String customerNameCountLimitCheck      = merchantFlags.get("customerNameLimitCheck");
        String customerNameAmountLimitCheck     = merchantFlags.get("customerNameAmountLimitCheck");
        String customerEmailCountLimitCheck     = merchantFlags.get("customerEmailLimitCheck");
        String customerEmailAmountLimitCheck    = merchantFlags.get("customerEmailAmountLimitCheck");
        String customerPhoneCountLimitCheck     = merchantFlags.get("customerPhoneLimitCheck");
        String customerPhoneAmountLimitCheck    = merchantFlags.get("customerPhoneAmountLimitCheck");

        // Payout Limits
        String payoutBankAccountNoLimitCheck        = merchantFlags.get("payoutBankAccountNoLimitCheck");
        String payoutBankAccountNoAmountLimitCheck  = merchantFlags.get("payoutBankAccountNoAmountLimitCheck");


        // General Configuration
        String isActivation     = merchantFlags.get("activation");
        String hasPaid          = merchantFlags.get("haspaid");
        String partnerId        = merchantFlags.get("partnerid");
        String agentId          = merchantFlags.get("agentId");
        String isMerchantInterfaceAccess    = merchantFlags.get("icici");
        String blacklistTransactions        = merchantFlags.get("blacklistTransaction");
        String flightMode               = merchantFlags.get("flightMode");
        String isExcessCaptureAllowed   = merchantFlags.get("isExcessCaptureAllowed");


        // Transaction Configuration
        String isService = merchantFlags.get("isservice");
        String autoRedirect = merchantFlags.get("autoredirect");
        String vbv = merchantFlags.get("vbv");
        String masterCardSupported = merchantFlags.get("masterCardSupported");
        String autoSelectTerminal = merchantFlags.get("autoSelectTerminal");
        String isPODRequired = merchantFlags.get("isPODRequired");
        String isRestrictedTicket = merchantFlags.get("isRestrictedTicket");
        String emiSupport = merchantFlags.get("emiSupport");
        String isEmailLimitEnabled = merchantFlags.get("emailLimitEnabled");
        String binService = merchantFlags.get("binService");
        String supportSection = merchantFlags.get("supportSection");
        String supportNumberNeeded = merchantFlags.get("supportNoNeeded");
        String cardWhitelistLevel = merchantFlags.get("card_whitelist_level");
        String multiCurrencySupport = merchantFlags.get("multiCurrencySupport");
        String ipValidationRequired = merchantFlags.get("ip_validation_required");
        String binRouting = merchantFlags.get("binRouting");
        String personalInfoValidation = merchantFlags.get("personal_info_validation");
        String personalInfoDisplay = merchantFlags.get("personal_info_display");
        String restCheckoutPage = merchantFlags.get("hosted_payment_page");
        String merchantOrderDetails = merchantFlags.get("merchant_order_details");
        String marketPlace = merchantFlags.get("marketplace");
        String isUniqueOrderIdRequired = merchantFlags.get("isUniqueOrderIdRequired");
        String cardExpiryDateCheck = merchantFlags.get("cardExpiryDateCheck");
        String isOtpRequired = merchantFlags.get("isOTPRequired");
        String isCardStorageRequired = merchantFlags.get("isCardStorageRequired");


        // BackOffice Access Management
        String dashBoard = merchantFlags.get("dashboard_access");
        String accountDetail = merchantFlags.get("accounting_access");
        String setting = merchantFlags.get("setting_access");
        String transactionMgmt = merchantFlags.get("transactions_access");
        String invoice = merchantFlags.get("invoicing_access");
        String virtualTerminal = merchantFlags.get("virtualterminal_access");
        String merchantMgmt = merchantFlags.get("merchantmgt_access");
        String applicationManager = merchantFlags.get("isappmanageractivate");
        String recurring = merchantFlags.get("is_recurring");
        String tokenMgmt = merchantFlags.get("iscardregistrationallowed");
        String rejectedTransaction = merchantFlags.get("rejected_transaction");
        String virtualCheckout = merchantFlags.get("virtual_checkout");
        String payByLink = merchantFlags.get("paybylink");


        String accountSummary = merchantFlags.get("accounts_account_summary_access");
        String chargesSummary = merchantFlags.get("accounts_charges_summary_access");
        String transactionSummary = merchantFlags.get("accounts_transaction_summary_access");
        String reports = merchantFlags.get("accounts_reports_summary_access");


        String merchantProfile = merchantFlags.get("settings_merchant_profile_access");
        String organisationProfile = merchantFlags.get("settings_organisation_profile_access");
        String generateKey = merchantFlags.get("settings_generate_key_access");
        String merchantConfiguration = merchantFlags.get("settings_merchant_config_access");
        String fraudRuleConfiguration = merchantFlags.get("settings_fraudrule_config_access");
        String whitelistDetails = merchantFlags.get("settings_whitelist_details");
        String blockDetails = merchantFlags.get("settings_blacklist_details");


        String transactions = merchantFlags.get("transmgt_transaction_access");
        String capture = merchantFlags.get("transmgt_capture_access");
        String reversal = merchantFlags.get("transmgt_reversal_access");
        String payout = merchantFlags.get("transmgt_payout_access");
        String payoutTransaction = merchantFlags.get("transmgt_payout_transactions");


        String generateInvoice      = merchantFlags.get("invoice_generate_access");
        String invoiceHistory       = merchantFlags.get("invoice_history_access");
        String invoiceConfiguration = merchantFlags.get("settings_invoice_config_access");


        String registrationHistory  = merchantFlags.get("tokenmgt_registration_history_access");
        String registerCard         = merchantFlags.get("tokenmgt_register_card_access");


        String userManagement = merchantFlags.get("merchantmgt_user_management_access");


        // Template Configuration
        String isPharma         = merchantFlags.get("isPharma");
        String isPoweredByLogo  = merchantFlags.get("isPoweredBy");
        String template             = merchantFlags.get("template");
        String pciLogo              = merchantFlags.get("ispcilogo");
        String partnerLogo          = merchantFlags.get("ispartnerlogo");
        String securityLogo         = merchantFlags.get("isSecurityLogo");
        String isMerchantLogo       = merchantFlags.get("ismerchantlogo");
        String visaSecureLogo       = merchantFlags.get("vbvLogo");
        String isMerchantLogoBO     = merchantFlags.get("isMerchantLogoBO");
        String mcSecureLogo         = merchantFlags.get("masterSecureLogo");
        String consent              = merchantFlags.get("consent");
        String checkoutTimer        = merchantFlags.get("checkoutTimer");


        // Token Configuration
        String isTokenizationAllowed    = merchantFlags.get("isTokenizationAllowed");
        String isAddressDetailsRequired = merchantFlags.get("isAddrDetailsRequired");
        String isCardEncryptionEnable   = merchantFlags.get("isCardEncryptionEnable");


        // Fraud Defender Configuration
        String purchaseRefund       = merchantFlags.get("ispurchase_inquiry_refund");
        String purchaseBlacklist    = merchantFlags.get("ispurchase_inquiry_blacklist");
        String fraudRefund          = merchantFlags.get("isfraud_determined_refund");
        String fraudBlacklist       = merchantFlags.get("isfraud_determined_blacklist");
        String disputeRefund        = merchantFlags.get("isdispute_initiated_refund");
        String disputeBlacklist     = merchantFlags.get("isdispute_initiated_blacklist");
        String exceptionBlacklist   = merchantFlags.get("isexception_file_listing_blacklist");
        String stopBlacklist        = merchantFlags.get("isstop_payment_blacklist");


        // Whitelisting Configuration
        String isCardWhitelisted        = merchantFlags.get("iswhitelisted");
        String isIpWhitelisted          = merchantFlags.get("isIpWhitelisted");
        String isIPWhitelistedForAPIs   = merchantFlags.get("is_rest_whitelisted");
        String isDomainWhitelisted      = merchantFlags.get("isDomainWhitelisted");


        // HR Transaction Configuration
        String hrAlertProof     = merchantFlags.get("hralertproof");
        String hrParameterized  = merchantFlags.get("hrparameterised");


        // Refund Configuration
        String isRefund         = merchantFlags.get("isrefund");
        String isMultipleRefund = merchantFlags.get("isMultipleRefund");
        String isPartialRefund  = merchantFlags.get("isPartialRefund");


        // Email Configuration
        String isValidateEmail          = merchantFlags.get("isValidateEmail");
        String emailTemplateLanguage    = merchantFlags.get("emailTemplateLang");

        // SMS Configuration
        String merchantSMSActivation = merchantFlags.get("smsactivation");
        String customerSMSActivation = merchantFlags.get("customersmsactivation");


        // Invoice Configuration
        String invoiceMerchantDetails   = merchantFlags.get("invoicetemplate");
        String isIPwhitelistForInvoice  = merchantFlags.get("ip_whitelist_invoice");


        // Fraud Configuration
        String onlineFraudCheck     = merchantFlags.get("onlineFraudCheck");
        String internalFraudCheck   = merchantFlags.get("internalFraudCheck");


        // Split transaction configuration
        String splitPaymentAllowed  = merchantFlags.get("isSplitPayment");
        String splitPaymentType     = merchantFlags.get("splitPaymentType");


        // Invoizer Configuration
        String isVirtualCheckoutAllowed = merchantFlags.get("isVirtualCheckoutAllowed");
        String isPhoneRequired          = merchantFlags.get("isMobileAllowedForVC");
        String isEmailRequired          = merchantFlags.get("isEmailAllowedForVC");
        String isShareAllowed           = merchantFlags.get("isShareAllowed");
        String isSignatureAllowed       = merchantFlags.get("isSignatureAllowed");
        String isSaveReceiptAllowed     = merchantFlags.get("isSaveReceiptAllowed");
        String defaultLanguage          = merchantFlags.get("defaultLanguage");


        // Merchant Notification Callback
        String reconciliation           = merchantFlags.get("reconciliationNotification");
        String transaction              = merchantFlags.get("transactionNotification");
        String refundNotification       = merchantFlags.get("refundNotification");
        String chargebackNotification   = merchantFlags.get("chargebackNotification");
        String payoutNotification       = merchantFlags.get("payoutNotification");
        String inquiryNotification      = merchantFlags.get("inquiryNotification");


        memberLimits.setCardLimitCheck(cardLimitCheck);
        memberLimits.setCardAmountLimitCheck(cardAmountLimitCheck);
        memberLimits.setAmountLimitCheck(amountLimitCheck);
        memberLimits.setCardVelocityCheck(cardVelocityCheck);
        memberLimits.setLimitRouting(limitRouting);
        memberLimits.setPayoutAmountLimitCheck(payoutAmountLimitCheck);
        memberLimits.setPayoutRouting(payoutRouting);

        transactionLimits.setVpaAddressAmountLimitCheck(vpaAddressAmountLimitCheck);
        transactionLimits.setVpaAddressLimitCheck(vpaAddressLimitCheck);
        transactionLimits.setCustomerIpAmountLimitCheck(customerIpAmountLimitCheck);
        transactionLimits.setCustomerIpCountLimitCheck(customerIpCountLimitCheck);
        transactionLimits.setCustomerNameAmountLimitCheck(customerNameAmountLimitCheck);
        transactionLimits.setCustomerNameCountLimitCheck(customerNameCountLimitCheck);
        transactionLimits.setCustomerEmailAmountLimitCheck(customerEmailAmountLimitCheck);
        transactionLimits.setCustomerEmailCountLimitCheck(customerEmailCountLimitCheck);
        transactionLimits.setCustomerPhoneAmountLimitCheck(customerPhoneAmountLimitCheck);
        transactionLimits.setCustomerPhoneCountLimitCheck(customerPhoneCountLimitCheck);

        payoutLimits.setPayoutBankAccountNoAmountLimitCheck(payoutBankAccountNoAmountLimitCheck);
        payoutLimits.setPayoutBankAccountNoLimitCheck(payoutBankAccountNoLimitCheck);

        generalConfiguration.setAgentId(agentId);
        generalConfiguration.setIsActivation(isActivation);
        generalConfiguration.setHasPaid(hasPaid);
        generalConfiguration.setPartnerId(partnerId);
        generalConfiguration.setIsMerchantInterfaceAccess(isMerchantInterfaceAccess);
        generalConfiguration.setBlacklistTransactions(blacklistTransactions);
        generalConfiguration.setFlightMode(flightMode);
        generalConfiguration.setIsExcessCaptureAllowed(isExcessCaptureAllowed);

        transactionConfiguration.setIsService(isService);
        transactionConfiguration.setAutoRedirect(autoRedirect);
        transactionConfiguration.setVbv(vbv);
        transactionConfiguration.setMasterCardSupported(masterCardSupported);
        transactionConfiguration.setAutoSelectTerminal(autoSelectTerminal);
        transactionConfiguration.setIsPODRequired(isPODRequired);
        transactionConfiguration.setIsRestrictedTicket(isRestrictedTicket);
        transactionConfiguration.setEmiSupport(emiSupport);
        transactionConfiguration.setIsEmailLimitEnabled(isEmailLimitEnabled);
        transactionConfiguration.setBinService(binService);
        transactionConfiguration.setSupportNumberNeeded(supportNumberNeeded);
        transactionConfiguration.setSupportSection(supportSection);
        transactionConfiguration.setCardWhitelistLevel(cardWhitelistLevel);
        transactionConfiguration.setMultiCurrencySupport(multiCurrencySupport);
        transactionConfiguration.setIpValidationRequired(ipValidationRequired);
        transactionConfiguration.setBinRouting(binRouting);
        transactionConfiguration.setPersonalInfoDisplay(personalInfoDisplay);
        transactionConfiguration.setPersonalInfoValidation(personalInfoValidation);
        transactionConfiguration.setRestCheckoutPage(restCheckoutPage);
        transactionConfiguration.setMerchantOrderDetails(merchantOrderDetails);
        transactionConfiguration.setMarketPlace(marketPlace);
        transactionConfiguration.setIsUniqueOrderIdRequired(isUniqueOrderIdRequired);
        transactionConfiguration.setCardExpiryDateCheck(cardExpiryDateCheck);
        transactionConfiguration.setIsOtpRequired(isOtpRequired);
        transactionConfiguration.setIsCardStorageRequired(isCardStorageRequired);

        backOfficeAccessManagement.setDashBoard(dashBoard);
        backOfficeAccessManagement.setAccountDetail(accountDetail);
        backOfficeAccessManagement.setSetting(setting);
        backOfficeAccessManagement.setTransactionMgmt(transactionMgmt);
        backOfficeAccessManagement.setInvoice(invoice);
        backOfficeAccessManagement.setVirtualTerminal(virtualTerminal);
        backOfficeAccessManagement.setMerchantMgmt(merchantMgmt);
        backOfficeAccessManagement.setApplicationManager(applicationManager);
        backOfficeAccessManagement.setRecurring(recurring);
        backOfficeAccessManagement.setTokenMgmt(tokenMgmt);
        backOfficeAccessManagement.setRejectedTransaction(rejectedTransaction);
        backOfficeAccessManagement.setVirtualCheckout(virtualCheckout);
        backOfficeAccessManagement.setPayByLink(payByLink);

        accountDetails.setAccountSummary(accountSummary);
        accountDetails.setChargesSummary(chargesSummary);
        accountDetails.setTransactionSummary(transactionSummary);
        accountDetails.setReports(reports);

        settings.setMerchantProfile(merchantProfile);
        settings.setOrganisationProfile(organisationProfile);
        settings.setGenerateKey(generateKey);
        settings.setMerchantConfiguration(merchantConfiguration);
        settings.setFraudRuleConfiguration(fraudRuleConfiguration);
        settings.setWhitelistDetails(whitelistDetails);
        settings.setBlockDetails(blockDetails);

        transactionManagement.setTransactions(transactions);
        transactionManagement.setCapture(capture);
        transactionManagement.setReversal(reversal);
        transactionManagement.setPayout(payout);
        transactionManagement.setPayoutTransaction(payoutTransaction);

        invoicing.setGenerateInvoice(generateInvoice);
        invoicing.setInvoiceHistory(invoiceHistory);
        invoicing.setInvoiceConfiguration(invoiceConfiguration);

        tokenManagement.setRegistrationHistory(registrationHistory);
        tokenManagement.setRegisterCard(registerCard);

        merchantManagement.setUserManagement(userManagement);

        backOfficeAccessManagement.setAccountDetails(accountDetails);
        backOfficeAccessManagement.setSettings(settings);
        backOfficeAccessManagement.setTransactionManagement(transactionManagement);
        backOfficeAccessManagement.setInvoicing(invoicing);
        backOfficeAccessManagement.setTokenManagement(tokenManagement);
        backOfficeAccessManagement.setMerchantManagement(merchantManagement);


        templateConfiguration.setIsPharma(isPharma);
        templateConfiguration.setIsPoweredByLogo(isPoweredByLogo);
        templateConfiguration.setTemplate(template);
        templateConfiguration.setPciLogo(pciLogo);
        templateConfiguration.setPartnerLogo(partnerLogo);
        templateConfiguration.setSecurityLogo(securityLogo);
        templateConfiguration.setIsMerchantLogo(isMerchantLogo);
        templateConfiguration.setVisaSecureLogo(visaSecureLogo);
        templateConfiguration.setIsMerchantLogoBO(isMerchantLogoBO);
        templateConfiguration.setMcSecureLogo(mcSecureLogo);
        templateConfiguration.setConsent(consent);
        templateConfiguration.setCheckoutTimer(checkoutTimer);


        tokenConfiguration.setIsTokenizationAllowed(isTokenizationAllowed);
        tokenConfiguration.setIsAddressDetailsRequired(isAddressDetailsRequired);
        tokenConfiguration.setIsCardEncryptionEnable(isCardEncryptionEnable);


        purchaseInquiry.setRefund(purchaseRefund);
        purchaseInquiry.setBlacklist(purchaseBlacklist);

        fraudDetermined.setBlacklist(fraudBlacklist);
        fraudDetermined.setRefund(fraudRefund);

        disputeInitiated.setBlacklist(disputeBlacklist);
        disputeInitiated.setRefund(disputeRefund);

        exceptionFileListing.setBlacklist(exceptionBlacklist);

        stopPayment.setBlacklist(stopBlacklist);

        fraudDefenderConfiguration.setPurchaseInquiry(purchaseInquiry);
        fraudDefenderConfiguration.setFraudDetermined(fraudDetermined);
        fraudDefenderConfiguration.setDisputeInitiated(disputeInitiated);
        fraudDefenderConfiguration.setStopPayment(stopPayment);
        fraudDefenderConfiguration.setExceptionFileListing(exceptionFileListing);

        whitelistingConfiguration.setIsCardWhitelisted(isCardWhitelisted);
        whitelistingConfiguration.setIsIpWhitelisted(isIpWhitelisted);
        whitelistingConfiguration.setIsIPWhitelistedForAPIs(isIPWhitelistedForAPIs);
        whitelistingConfiguration.setIsDomainWhitelisted(isDomainWhitelisted);

        hrTransactionConfiguration.setHrAlertProof(hrAlertProof);
        hrTransactionConfiguration.setHrParameterized(hrParameterized);


        refundConfiguration.setIsMultipleRefund(isMultipleRefund);
        refundConfiguration.setIsPartialRefund(isPartialRefund);
        refundConfiguration.setIsRefund(isRefund);

        emailConfiguration.setEmailTemplateLanguage(emailTemplateLanguage);
        emailConfiguration.setIsValidateEmail(isValidateEmail);

        smsConfiguration.setCustomerSMSActivation(customerSMSActivation);
        smsConfiguration.setMerchantSMSActivation(merchantSMSActivation);

        invoiceConfigurations.setInvoiceMerchantDetails(invoiceMerchantDetails);
        invoiceConfigurations.setIsIPwhitelistForInvoice(isIPwhitelistForInvoice);

        fraudConfiguration.setOnlineFraudCheck(onlineFraudCheck);
        fraudConfiguration.setInternalFraudCheck(internalFraudCheck);

        splitTransactionConfiguration.setSplitPaymentAllowed(splitPaymentAllowed);
        splitTransactionConfiguration.setSplitPaymentType(splitPaymentType);

        invoizerConfiguration.setIsVirtualCheckoutAllowed(isVirtualCheckoutAllowed);
        invoizerConfiguration.setIsPhoneRequired(isPhoneRequired);
        invoizerConfiguration.setIsEmailRequired(isEmailRequired);
        invoizerConfiguration.setIsShareAllowed(isShareAllowed);
        invoizerConfiguration.setIsSignatureAllowed(isSignatureAllowed);
        invoizerConfiguration.setIsSaveReceiptAllowed(isSaveReceiptAllowed);
        invoizerConfiguration.setDefaultLanguage(defaultLanguage);

        merchantNotificationCallback.setReconciliation(reconciliation);
        merchantNotificationCallback.setTransactions(transaction);
        merchantNotificationCallback.setRefundNotification(refundNotification);
        merchantNotificationCallback.setChargebackNotification(chargebackNotification);
        merchantNotificationCallback.setPayoutNotification(payoutNotification);
        merchantNotificationCallback.setInquiryNotification(inquiryNotification);


        merchantResponseFlagsVO.setMemberLimits(memberLimits);
        merchantResponseFlagsVO.setTransactionLimits(transactionLimits);
        merchantResponseFlagsVO.setPayoutLimits(payoutLimits);
        merchantResponseFlagsVO.setGeneralConfiguration(generalConfiguration);
        merchantResponseFlagsVO.setTransactionConfiguration(transactionConfiguration);
        merchantResponseFlagsVO.setBackOfficeAccessManagement(backOfficeAccessManagement);
        merchantResponseFlagsVO.setTemplateConfiguration(templateConfiguration);
        merchantResponseFlagsVO.setTokenConfiguration(tokenConfiguration);
        merchantResponseFlagsVO.setFraudDefenderConfiguration(fraudDefenderConfiguration);
        merchantResponseFlagsVO.setWhitelistingConfiguration(whitelistingConfiguration);
        merchantResponseFlagsVO.setHrTransactionConfiguration(hrTransactionConfiguration);
        merchantResponseFlagsVO.setRefundConfiguration(refundConfiguration);
        merchantResponseFlagsVO.setEmailConfiguration(emailConfiguration);
        merchantResponseFlagsVO.setSmsConfiguration(smsConfiguration);
        merchantResponseFlagsVO.setInvoiceConfiguration(invoiceConfigurations);
        merchantResponseFlagsVO.setFraudConfiguration(fraudConfiguration);
        merchantResponseFlagsVO.setSplitTransactionConfiguration(splitTransactionConfiguration);
        merchantResponseFlagsVO.setInvoizerConfiguration(invoizerConfiguration);
        merchantResponseFlagsVO.setMerchantNotificationCallback(merchantNotificationCallback);


        return merchantResponseFlagsVO;
    }

    private String getMerchantFlagsStringValue(String columnName)
    {
        String flag = "";

        if(functions.isValueNull(columnName) && columnName.equals("1"))
            flag = "Y";

        else if(functions.isValueNull(columnName) && columnName.equals("0"))
            flag = "N";

        return flag;
    }

    public CommonValidatorVO readRequestForMerchantTheme(MerchantRequestVO request)
    {
        CommonValidatorVO commonValidatorVO                 = new CommonValidatorVO();
        MerchantDetailsVO genericMerchantDetailsVO          = new MerchantDetailsVO();

        genericMerchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestTerminaldetails(MerchantRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

        genericMerchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        transDetailsVO.setCardType(request.getPaymentBrand());
        transDetailsVO.setPaymentid(request.getPaymentMode());
        transDetailsVO.setCurrency(request.getCurrency());
        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }
}
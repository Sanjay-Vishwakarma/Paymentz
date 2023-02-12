package com.merchant.manager;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.manager.*;
import com.manager.dao.FraudTransactionDAO;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TransactionDAO;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.payment.Mail.*;
import com.payment.MultipleMemberUtill;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.sms.SMSService;
import com.payment.sms.TransactionSMSEventUtil;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Sneha on 9/1/2016.
 */
public class RegistrationManager
{
    Functions functions = new Functions();
    private Logger logger = new Logger(RegistrationManager.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(RegistrationManager.class.getName());
    private TransactionUtil transactionUtil = new TransactionUtil();

    public DirectKitResponseVO processCCustomerRegistration(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        logger.debug("Inside processCCustomerRegistration in RegistrationManager :::");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        CardholderResponseVO cardholderResponseVO = null;
        TokenManager tokenManager = new TokenManager();
        String cardholderId = "";

        CardHolderRequestVO cardHolderRequestVO=new CardHolderRequestVO();
        cardHolderRequestVO.setToId(merchantDetailsVO.getMemberId());
        cardHolderRequestVO.setFirstName(addressDetailsVO.getFirstname());
        cardHolderRequestVO.setLastName(addressDetailsVO.getLastname());
        cardHolderRequestVO.setEmail(addressDetailsVO.getEmail());
        cardHolderRequestVO.setTelNo(addressDetailsVO.getPhone());
        cardHolderRequestVO.setBirthDate(addressDetailsVO.getBirthdate());
        cardHolderRequestVO.setGender(addressDetailsVO.getSex());
        cardHolderRequestVO.setZip(addressDetailsVO.getZipCode());
        cardHolderRequestVO.setPartnerId(partnerDetailsVO.getPartnerId());
        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
        {
            cardholderResponseVO = tokenManager.registerCardholder(cardHolderRequestVO);
            cardholderResponseVO.setMemberId(cardHolderRequestVO.getToId());
        }
        else
        {
            cardholderResponseVO= tokenManager.registerCardholderByPartner(cardHolderRequestVO);
            cardholderResponseVO.setPartnerId(cardHolderRequestVO.getPartnerId());
        }
        if ("Y".equals(cardholderResponseVO.getStatus()))
        {
            cardholderId = cardholderResponseVO.getCardholderId();
        }
        directKitResponseVO.setStatus(cardholderResponseVO.getStatus());
        directKitResponseVO.setStatusMsg(cardholderResponseVO.getStatusDescription());
        directKitResponseVO.setHolder(cardholderId);
        if(functions.isValueNull(cardholderResponseVO.getMemberId()))
            directKitResponseVO.setMemberId(cardholderResponseVO.getMemberId());
        if(functions.isValueNull(cardholderResponseVO.getPartnerId()))
            directKitResponseVO.setPartnerId(cardholderResponseVO.getPartnerId());
        return directKitResponseVO;
    }
    public DirectKitResponseVO processOtpGeneration(CommonValidatorVO commonValidatorVO)
    {
        OTPValidationManager otpValidationManager = new OTPValidationManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String otpStatus = otpValidationManager.insertOtp(commonValidatorVO);
        directKitResponseVO.setStatus(otpStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
        return directKitResponseVO;
    }
    public DirectKitResponseVO processOtpVerification(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        OTPValidationManager otpValidationManager = new OTPValidationManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String verifyStatus = otpValidationManager.isVerifyMerchantOtp(commonValidatorVO);
        directKitResponseVO.setStatus(verifyStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_VERIFICATION + verifyStatus);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processMerchantSignUp(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request) throws PZDBViolationException, PZConstraintViolationException
    {
        logger.debug("Inside processMerchantSignUp in RegistrationManager :::");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        OTPValidationManager otpValidationManager = new OTPValidationManager();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        Merchants merchants = new Merchants();
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        Functions functions = new Functions();
        Hashtable details = new Hashtable();

        String remoteAddr = Functions.getIpAddress(request);
        String httpProtocol = request.getScheme();
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String userAgent = request.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String url=request.getHeader("referer");
        if(!functions.isValueNull(url))
            url=httpProtocol+"://"+request.getRemoteHost()+servletPath;


        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
        String newPassword = commonValidatorVO.getMerchantDetailsVO().getNewPassword();
        String companyName = "";
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            companyName = commonValidatorVO.getMerchantDetailsVO().getCompany_name();
        String website = "";
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getWebsite()))
            website = commonValidatorVO.getMerchantDetailsVO().getWebsite();
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        String contactName = commonValidatorVO.getMerchantDetailsVO().getContact_persons();
        String contactEmail = commonValidatorVO.getAddressDetailsVO().getEmail();
        String contactPhone = commonValidatorVO.getAddressDetailsVO().getTelnocc() + "-" + commonValidatorVO.getAddressDetailsVO().getPhone();
        String fromtype=commonValidatorVO.getPartnerDetailsVO().getCompanyName();
        String etoken= ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
        String isMobileVerified = commonValidatorVO.getMerchantDetailsVO().getIsMobileVerified();
        String country = "";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCountry()))
            country = commonValidatorVO.getAddressDetailsVO().getCountry();
        String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
        String clKey = partnerManager.getselfPartnerDetails(partnerId).getClkey();
        Member member = null;
        try
        {
            if (merchants.isMember(loginName) || multipleMemberUtill.isUniqueChildMember(loginName))
            {
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MEMBER_EXIST, ErrorMessages.SYS_MEMBER_ALREADY_EXIST));
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processDeleteRegisteredToken()", null, "Common", ErrorMessages.SYS_MEMBER_ALREADY_EXIST, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            else
            {
                details.put("login", loginName);
                details.put("passwd", newPassword);
                details.put("company_name", companyName);
                details.put("sitename", website);
                details.put("contact_persons", contactName);
                details.put("telno", contactPhone);
                details.put("contact_emails", contactEmail);
                details.put("country", country);
                details.put("partnerid", partnerId);
                details.put("clkey", clKey);
                details.put("emailtoken", etoken);
                if (functions.isValueNull(isMobileVerified))
                    details.put("isMobileVerified", isMobileVerified);

                details.put("url", url);
                details.put("ipAddress", remoteAddr);
                details.put("httpheader", header);
                String toId = "";
                member = addMerchant(details);
                if (member != null && functions.isValueNull(String.valueOf(member.memberid)))
                {
                    toId = String.valueOf(member.memberid);
                    directKitResponseVO.setSecureKey(member.secureKey);
                    directKitResponseVO.setStatus("success");
                    directKitResponseVO.setMemberId(toId);
                    // updateFlag(member.memberid);
                    MailService mailService = new MailService();
                    HashMap merchantSignupMail = new HashMap();
                    merchantSignupMail.put(MailPlaceHolder.USERNAME, details.get("login"));
                    merchantSignupMail.put(MailPlaceHolder.NAME, details.get("contact_persons"));
                    merchantSignupMail.put(MailPlaceHolder.TOID, String.valueOf(member.memberid));
                    merchantSignupMail.put(MailPlaceHolder.CTOKEN, etoken);
                    merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveUrl);
                    merchantSignupMail.put(MailPlaceHolder.PARTNERID, partnerId);
                    merchantSignupMail.put(MailPlaceHolder.FROM_TYPE, fromtype);
                    mailService.sendMail(MailEventEnum.PARTNERS_MERCHANT_REGISTRATION, merchantSignupMail);
                    invoiceEntry.insertInvoiceConfigDetails(String.valueOf(member.memberid));
                }
                else
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_MEMBER_EXIST, ErrorMessages.SYS_MEMBER_ALREADY_EXIST));
                    directKitResponseVO.setStatus("fail");
                    directKitResponseVO.setStatusMsg("Merchant Signup Failed ");
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            logger.error("ArrayIndexOutOfBoundsException----",e);
        }
        return directKitResponseVO;
    }
    public DirectKitResponseVO processEmailVerify(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();

        String partnerid = commonValidatorVO.getMerchantDetailsVO().getPartnerId();
        String fromtype = commonValidatorVO.getMerchantDetailsVO().getPartnerName();

        String etoken =  isMember(loginName);
        String memberid =  Memberid(loginName);
        String isemailverify =emailverified(loginName);
        String liveurl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");

        if (isemailverify.equals("N"))
        {
            MailService mailService = new MailService();
            HashMap merchantSignupMail = new HashMap();
            merchantSignupMail.put(MailPlaceHolder.USERNAME, loginName);
            merchantSignupMail.put(MailPlaceHolder.TOID, memberid);
            merchantSignupMail.put(MailPlaceHolder.PARTNERID, partnerid);
            merchantSignupMail.put(MailPlaceHolder.FROM_TYPE, fromtype);
            merchantSignupMail.put(MailPlaceHolder.CTOKEN, etoken);
            merchantSignupMail.put(MailPlaceHolder.PARTNER_URL, liveurl);
            mailService.sendMail(MailEventEnum.RESEND_VERIFY_EMAIL, merchantSignupMail);
            directKitResponseVO.setStatus("success");
        }
        else
        {
            directKitResponseVO.setStatus("Email already verified");
        }
        return directKitResponseVO;
    }

    public DirectKitResponseVO processSendReceiptEmail(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZDBViolationException, PZConstraintViolationException
    {

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        SendTransactionEventMailUtil sendTransactionEventMailUtil = new SendTransactionEventMailUtil();
        TransactionDAO transactionDAO= new TransactionDAO();
        String mailtransactionStatus = "";
        String trackingId=commonValidatorVO.getTrackingid();
        logger.error("PaymentId --- >"+trackingId);
        String memberid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        mailtransactionStatus=transactionDAO.getStatusFromCommon(trackingId,memberid);
        logger.error("trackingId --- >"+trackingId);
        logger.error("memberid --- >"+memberid);
        logger.error("mailtransactionStatus --- >"+mailtransactionStatus);

        if(functions.isValueNull(mailtransactionStatus))
        {
            directKitResponseVO.setStatus("success");
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            HashMap mailData= sendTransactionEventMailUtil.setSaleTransactionData(trackingId,mailtransactionStatus);
            mailData.put(MailPlaceHolder.CustomerEmail,commonValidatorVO.getAddressDetailsVO().getEmail());
            asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, mailData);
        }
        else
        {
            directKitResponseVO.setStatus("failed");
        }
        return directKitResponseVO;
    }

    public DirectKitResponseVO processSendReceiptSms(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZDBViolationException, PZConstraintViolationException
    {

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
         TransactionDAO transactionDAO= new TransactionDAO();
        TransactionSMSEventUtil transactionSMSEventUtil = new TransactionSMSEventUtil();
        String smstransactionStatus = "";
        String trackingId=commonValidatorVO.getTrackingid();
        logger.error("PaymentId --- >"+trackingId);
        String memberid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        smstransactionStatus=transactionDAO.getStatusFromCommon(trackingId,memberid);
        logger.error("trackingId --- >"+trackingId);
        logger.error("memberid --- >"+memberid);
        logger.error("smstransactionStatus --- >"+smstransactionStatus);

        if(functions.isValueNull(smstransactionStatus))
        {
            directKitResponseVO.setStatus("success");

            com.payment.sms.SMSService smsService = new SMSService();
            HashMap smsdata=transactionSMSEventUtil.setSaleTransactionData(trackingId,smstransactionStatus);
            smsdata.put(MailPlaceHolder.SMS_TO_TELNO,commonValidatorVO.getAddressDetailsVO().getPhone());
            smsdata.put(MailPlaceHolder.SMS_TO_TELNOCC,commonValidatorVO.getAddressDetailsVO().getTelnocc());
            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, smsdata);


        }
        else
        {
            directKitResponseVO.setStatus("failed");
        }
        return directKitResponseVO;
    }

     public DirectKitResponseVO processMerchantLogin(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
        String password = commonValidatorVO.getMerchantDetailsVO().getPassword();
        String etoken = commonValidatorVO.getMerchantDetailsVO().getEtoken();
        String emailtoken="";
        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();
        String userId = "";
        String status = "";
        User user =  (User)session.getAttribute("Anonymous");
        session.setAttribute("Anonymous", new DefaultUser(User.ANONYMOUS.getName()));
        try
        {
            session.invalidate();
            member = merchants.authenticate(loginName, partnerid,request);
            if (member.etoken==null || member.etoken.equals(""))
            {

                merchants.insertetoken (member.memberid, emailtoken);
            }
            if(member.getMemberUser()!=null)
            {
                request.setAttribute("role", "submerchant");
                userId = String.valueOf(member.getMemberUser().getUserId());
            }
            if (etoken!=null)
                if (etoken.equals(member.etoken))
                {
                    updateisemail(loginName);
                }
            user = ESAPI.authenticator().login(request, response);

        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            String mes = "";
            if (e.getMessage().toLowerCase().contains("disabled"))
            {
                mes = "D";
                logger.debug("disabled----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_LOGIN_FAILED, ErrorMessages.SYS_LOGIN_FAILED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_LOGIN_FAILED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else if (e.getMessage().toLowerCase().contains("locked"))
            {
                logger.debug("locked----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_ACCOUNT_LOCKED, ErrorMessages.SYS_ACCOUNT_LOCKED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_ACCOUNT_LOCKED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else if(e.getMessage().toLowerCase().contains("denied"))
            {
                logger.debug("denied----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_LOGIN_FAILED, ErrorMessages.SYS_LOGIN_FAILED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_LOGIN_FAILED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else if(e.getMessage().toLowerCase().contains("whitelisted"))
            {
                logger.debug("whitelisted----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_LOGIN_FAILED, ErrorMessages.SYS_LOGIN_FAILED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_LOGIN_FAILED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else if(e.getMessage().toLowerCase().contains("unauthorized"))
            {
                logger.debug("unauthorized----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_LOGIN_FAILED, ErrorMessages.SYS_LOGIN_FAILED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_LOGIN_FAILED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else
            {
                logger.debug("else----"+e.getMessage());
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_LOGIN_FAILED, ErrorMessages.SYS_LOGIN_FAILED));
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("RestDirectTransactionManager.class", "processMerchantLogin()", null, "Common", ErrorMessages.SYS_LOGIN_FAILED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }

        int merchantid = member.memberid;
        String merchantId = String.valueOf(merchantid);
        if(!request.getAttribute("role").equals("submerchant"))
        {
            userId = merchantId;
        }
        session = request.getSession();
        logger.debug("successfull login----");
        status = "success";
        userId = merchantId;
        commonValidatorVO.getMerchantDetailsVO().setRole((String) request.getAttribute("role"));
        directKitResponseVO = getAuthToken(commonValidatorVO, request, response);
        merchants.updateAuthtoken(loginName, (String) request.getAttribute("role"), directKitResponseVO.getAuthToken());
        directKitResponseVO.setStatus(status);
        directKitResponseVO.setMemberId(userId);
        directKitResponseVO.setSecureKey(member.secureKey);
        directKitResponseVO.setEmail(member.contactemails);
        directKitResponseVO.setTelno(member.telno);
        directKitResponseVO.setLogin(member.login);
        directKitResponseVO.setContactPerson(member.contactpersons);
        directKitResponseVO.setCountry(member.country);
        directKitResponseVO.setIsmobileverified(member.isMobileVerified);
        directKitResponseVO.setUpiSupportInvoice(member.upiSupportInvoice);
        directKitResponseVO.setUpiQRSupportIinvoice(member.upiQRSupportIinvoice);
        directKitResponseVO.setPaybylinkSpportInvoice(member.paybylinkSpportInvoice);
        directKitResponseVO.setAEPSSupportInvoice(member.AEPSSupportInvoice);

        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
        return directKitResponseVO;
    }

    public DirectKitResponseVO getAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();

        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuthFunctions authFunctions = new AuthFunctions();

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();

        String status = "";
        String key = "";
        String authToken = "";
        String role = (String)request.getAttribute("role");
        key = commonValidatorVO.getMerchantDetailsVO().getKey();

        try
        {
            if (functions.isValueNull(key))
            {
                authToken = authFunctions.getAuthToken(loginName,role,commonValidatorVO.getMerchantDetailsVO().getMemberId(),partnerid);
            }
            else
            {
                member = merchants.authenticate(loginName, partnerid,request);
                User user = ESAPI.authenticator().login(request, response);
                authToken = authFunctions.getAuthToken(loginName,role,commonValidatorVO.getMerchantDetailsVO().getMemberId(),partnerid);
            }
            merchants.updateAuthtoken(loginName,role,authToken);

        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            String error = "Password must not be last 5 passwords";
            directKitResponseVO.setStatusMsg("change password failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }
        logger.debug("successfull login----");
        status = "success";
        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);
        directKitResponseVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
        return directKitResponseVO;
    }

    public DirectKitResponseVO getAllMerchantCurrencies(CommonValidatorVO commonValidatorVO)
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String role = commonValidatorVO.getMerchantDetailsVO().getRole();
        TransactionManager transactionManager = new TransactionManager();
        List<String> currencyList=null;
        if("submerchant".equalsIgnoreCase(role))
            currencyList=transactionManager.getListOfCurrenciesForSubMerchant(toid,commonValidatorVO.getMerchantDetailsVO().getLogin());
        else
            currencyList = transactionManager.getListOfCurrencies(toid);

        directKitResponseVO.setCurrencyList(currencyList);
        return directKitResponseVO;
    }
    public DirectKitResponseVO Getaddressdetails(CommonValidatorVO commonValidatorVO)
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        Merchants merchants = new Merchants();
        commonValidatorVO =merchants.Getaddressdetails(commonValidatorVO);
        if (commonValidatorVO!=null)
        {
            directKitResponseVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
        }
        return directKitResponseVO;
    }
    public String updateaddressDetails(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        Merchants merchants = new Merchants();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        boolean status = merchants.updateAddressDetails(commonValidatorVO);
        String successStatus = "";
        if (status)
        {
            successStatus = "success";
        }
        return successStatus;
    }
    public DirectKitResponseVO processMerchantChangePassword(CommonValidatorVO commonValidatorVO, @Context HttpServletRequest request, @Context HttpServletResponse response) throws SystemError
    {
        HttpSession session                         = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();
        Merchants merchants                         = new Merchants();
        ErrorCodeListVO errorCodeListVO             = new ErrorCodeListVO();
        String toid                                 = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String login                                = commonValidatorVO.getMerchantDetailsVO().getLogin();
        String password                             = commonValidatorVO.getMerchantDetailsVO().getPassword();
        String changePassword                       = commonValidatorVO.getMerchantDetailsVO().getNewPassword();
        try
        {
            User user           =   new com.directi.pg.DefaultUser(User.ANONYMOUS.getName());
            session.setAttribute("Anonymous", new DefaultUser(User.ANONYMOUS.getName()));
            ESAPI.httpUtilities().setCurrentHTTP(request, response);
            user                = ESAPI.authenticator().getUser(login);
            HashMap passMap     = new HashMap();
            passMap.put("merchantid",toid);
            passMap.put("userid","");
            if (merchants.changePassword(password, changePassword, passMap,user))
            {
                logger.debug("Password changed successfully");
                directKitResponseVO.setStatus("success");
                directKitResponseVO.setStatusMsg("Password changed successfully.");
            }
            else
            {
                logger.debug("Change password is not successful ");
                directKitResponseVO.setStatus("failed");
                String error = "Password must not be last 5 passwords";
                directKitResponseVO.setStatusMsg("change password failed");
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_DUPLICATE_PASSWORD, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processMerchantChangePassword()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception while forwarding to change password page ", e);
            if(e.getMessage().toLowerCase().contains("mismatch"))
            {
                String error = "Invalide Password";
                if (commonValidatorVO.getErrorCodeListVO() != null){
                    commonValidatorVO.getErrorCodeListVO().addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.REJECTED_MERCHANT_CHANGE_PASSWORD, error));
                }
                logger.error("System error",e);

                throw new SystemError(e.getMessage());
            }
        }
        return directKitResponseVO;
    }
    public DirectKitResponseVO processMerchantForgetPassword(CommonValidatorVO commonValidatorVO, @Context HttpServletRequest request, @Context HttpServletResponse response) throws SystemError, PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        String login = commonValidatorVO.getMerchantDetailsVO().getLogin();
        boolean flag = false;
        Merchants merchants = new Merchants();
        User user =   null;
        String error="";
        String role="merchant";
        String loginUser= merchants.getMemberLoginfromUser(login);
        if(functions.isValueNull(loginUser))
            role="submerchant";
        request.setAttribute("role",role);
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        user = ESAPI.authenticator().getUser(login);
        if(user==null)
        {
            error = "Invalid username";
            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFUL_MERCHANT_FORGET_PASSWORD));
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "processMerchantForgetPassword()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        String fdtstamp=merchants.getFdtstamp(login,role);
        if(functions.isValueNull(fdtstamp) && functions.isPasswordForgotWithInHour(fdtstamp))
        {
            error = "Try to forget password again after 24 hours.";
            errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MEMBER_FORGOT_LIMIT));
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "processMerchantForgetPassword()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        logger.debug("user====" + user.getAccountName());
        flag = merchants.forgotPassword(login, user);
        if (flag == true)
        {
            directKitResponseVO.setStatus("success");
            directKitResponseVO.setStatusMsg("New Password generated successfully.");
        }
        else
        {
            directKitResponseVO.setStatus("fail");
            directKitResponseVO.setStatusMsg("New Password generation failed");
        }
        return directKitResponseVO;
    }
    public Member addMerchant(Hashtable details) throws PZConstraintViolationException
    {
        Merchants merchants=new Merchants();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Member mem = null;
        try
        {
            PartnerManager partnerManager = new PartnerManager();
            PartnerDefaultConfigVO partnerDefaultConfigVO=partnerManager.getPartnerDefaultConfig((String)details.get("partnerid"));
            User user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "merchant");
            mem = addMerchant(user.getAccountId(), details, partnerDefaultConfigVO);
        }
        catch (Exception e)
        {
            transactionLogger.error("Add user throwing Authentication Exception ", e);
            logger.error("Add user throwing Authentication Exception ", e);

            if(e instanceof AuthenticationAccountsException)
            {
                String message=((AuthenticationAccountsException)e).getLogMessage();
                if(message.contains("Duplicate"))
                {
                    String error = "You cannot register token";
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_ALLOWED, error));
                    PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processTokenGeneration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
                }
            }
            try
            {
                merchants.DeleteBoth((String) details.get("login"));
            }
            catch(Exception e1)
            {
                transactionLogger.error("Exception while deletion of Details::",e1);
                logger.error("Exception while deletion of Details::",e1);
            }
        }
        return mem;
    }
    public  Member addMerchant(long accid,Hashtable details, PartnerDefaultConfigVO partnerDefaultConfigVO) throws SystemError
    {
        Merchants merchants=new Merchants();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("insert into members " +
                "(accid,login,clkey,company_name," +
                "contact_persons,contact_emails,country," +
                "telno,dtstamp,sitename,partnerId,emailtoken,isMobileVerified,icici,isappmanageractivate,is_recurring,iscardregistrationallowed,daily_amount_limit,monthly_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,card_check_limit,card_transaction_limit,check_limit,weekly_amount_limit,activation,haspaid,blacklistTransaction,flightMode,isExcessCaptureAllowed,isservice,autoredirect,vbv,masterCardSupported,autoSelectTerminal,isPODRequired,isRestrictedTicket,chargebackallowed_days,emailLimitEnabled,binService,expDateOffset,supportSection,supportNoNeeded,card_whitelist_level,multiCurrencySupport,isPharma,isPoweredBy,template,ispcilogo,isTokenizationAllowed,isAddrDetailsRequired,tokenvaliddays,isCardEncryptionEnable,iswhitelisted,isIpWhitelisted,is_rest_whitelisted,hralertproof,hrparameterised,isrefund,refunddailylimit,refundallowed_days,isValidateEmail,smsactivation,customersmsactivation,invoicetemplate,ip_whitelist_invoice,maxScoreAllowed,maxScoreAutoReversal,onlineFraudCheck, internalFraudCheck,isSplitPayment,splitPaymentType,ispartnerlogo,binRouting,personal_info_display,personal_info_validation,hosted_payment_page,vbvLogo,masterSecureLogo,emiSupport,isShareAllowed,isSignatureAllowed,isSaveReceiptAllowed,defaultLanguage,url,ipAddress,httpheader) values (");

        sb.append("" +ESAPI.encoder().encodeForSQL(me,String.valueOf(accid))+ "");
        sb.append(",'" +details.get("login") + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("company_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("contact_persons")) + "'");
        sb.append(",'" + details.get("contact_emails") + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("telno")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        if(details.get("partnerid")!=null)
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("partnerid")) + "'");
        else
            sb.append(",1");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("emailtoken")) + "'");
        if ((String) details.get("isMobileVerified") != null)
            sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("isMobileVerified")) + "'");
        else
            sb.append(",'" + "Y" + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantInterfaceAccess()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAppManagerActivate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRecurring()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardRegistrationAllowed()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getDailyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getMonthlyCardAmountLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCardTransactionLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getCheckLimit() + "'");
        sb.append(",'" + partnerDefaultConfigVO.getWeeklyAmountLimit() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getHasPaid())+ "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBlacklistTransaction()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getFlightMode()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsExcessCaptureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsService()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoRedirect()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbv()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMastercardSupported()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getAutoSelectTerminal()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPodRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestrictedTicket()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getChargebackAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmailLimitEnabled()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinService()) + "'");
        sb.append(",'" + partnerDefaultConfigVO.getExpDateOffset() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getSupportSection()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getSupportNoNeeded()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, partnerDefaultConfigVO.getCardWhiteListLevel()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMultiCurrencySupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPharma()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPoweredBy()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPciLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsTokenizationAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsAddressDetailsRequired()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getTokenValidDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsCardEncryptionEnable()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsIpWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRestWhiteListed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrAlertProof()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHrparameterised()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsRefund()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundDailyLimit()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getRefundAllowedDays()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsValidateEmail()) + "'");
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustReminderMail()) + "'");*/
        /*sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantEmailSent()) + "'");*/
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMerchantSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getCustomerSmsActivation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInvoiceTemplate()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIpWhiteListInvoice()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMaxScoreAutoReversal()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getOnlineFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getInternalFraudCheck()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSplitPayment()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getSplitPaymentType()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsPartnerLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getBinRouting()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoDisplay()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getPersonalInfoValidation()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getHostedPaymentPage()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getVbvLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getMasterSecureLogo()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getEmiSupport()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsShareAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSignatureAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getIsSaveReceiptAllowed()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,partnerDefaultConfigVO.getDefaultLanguage()) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String)details.get("url")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String)details.get("ipAddress")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String)details.get("httpheader")) + "')");

        logger.error("Add newmerchant----" + sb);
        Member mem = new Member();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            Database.executeUpdate(sb.toString(), conn);
            String selquery = "select clkey,memberid from members where login=? and accid=?";
            PreparedStatement pstmt= conn.prepareStatement(selquery);
            pstmt.setString(1,(String) details.get("login"));
            pstmt.setLong(2,accid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                mem.secureKey= rs.getString("clkey");
                mem.memberid = rs.getInt("memberid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isservice = false;
            }
            String query="insert into merchant_configuration(id,memberid,maincontact_phone,dashboard_access,accounting_access,setting_access,transactions_access,invoicing_access,virtualterminal_access," +
                    "merchantmgt_access,settings_fraudrule_config_access,settings_merchant_config_access,accounts_account_summary_access,accounts_charges_summary_access,accounts_transaction_summary_access," +
                    "accounts_reports_summary_access,settings_merchant_profile_access,settings_organisation_profile_access,settings_checkout_page_access,settings_generate_key_access,settings_invoice_config_access," +
                    "transmgt_transaction_access,transmgt_capture_access,transmgt_reversal_access,transmgt_payout_access,invoice_generate_access,invoice_history_access,tokenmgt_register_card_access,tokenmgt_registration_history_access," +
                    "merchantmgt_user_management_access,ip_validation_required,settings_whitelist_details,settings_blacklist_details,emi_configuration,limitRouting,checkoutTimer,checkoutTimerTime,marketplace,rejected_transaction," +
                    "virtual_checkout,isVirtualCheckoutAllowed,isMobileAllowedForVC,isEmailAllowedForVC,isCvvStore,reconciliationNotification,transactionNotification,refundNotification,chargebackNotification," +
                    "merchantRegistrationMail,merchantChangePassword,merchantChangeProfile,transactionSuccessfulMail,transactionFailMail,transactionCapture,transactionPayoutSuccess,transactionPayoutFail," +
                    "refundMail,chargebackMail,transactionInvoice,cardRegistration,payoutReport,monitoringAlertMail,monitoringSuspensionMail,highRiskRefunds,fraudFailedTxn,dailyFraudReport," +
                    "customerTransactionSuccessfulMail,customerTransactionFailMail,customerTransactionPayoutSuccess,customerTransactionPayoutFail,customerRefundMail,customerTokenizationMail,isUniqueOrderIdRequired," +
                    "emailTemplateLang,successReconMail,refundReconMail,chargebackReconMail,payoutReconMail,isMerchantLogoBO,cardExpiryDateCheck," +
                    "payoutNotification,vpaAddressLimitCheck,vpaAddressDailyCount,vpaAddressAmountLimitCheck,vpaAddressDailyAmountLimit,payoutBankAccountNoLimitCheck,bankAccountNoDailyCount,payoutBankAccountNoAmountLimitCheck,bankAccountNoDailyAmountLimit," +
                    "isDomainWhitelisted,customerIpLimitCheck,customerIpDailyCount,customerIpAmountLimitCheck,customerIpDailyAmountLimit,customerNameLimitCheck,customerNameDailyCount,customerNameAmountLimitCheck,customerNameDailyAmountLimit,customerEmailLimitCheck," +
                    "customerEmailDailyCount,customerEmailAmountLimitCheck,customerEmailDailyAmountLimit,customerPhoneLimitCheck,customerPhoneDailyCount,customerPhoneAmountLimitCheck,customerPhoneDailyAmountLimit,inquiryNotification,paybylink,isOTPRequired,isCardStorageRequired) " +
                    "values (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement=conn.prepareStatement(query);
            preparedStatement.setString(1,String.valueOf(mem.memberid));
            preparedStatement.setString(2, String.valueOf(details.get("maincontact_phone")));
            preparedStatement.setString(3,partnerDefaultConfigVO.getDashboardAccess());
            preparedStatement.setString(4,partnerDefaultConfigVO.getAccountingAccess());
            preparedStatement.setString(5,partnerDefaultConfigVO.getSettingAccess());
            preparedStatement.setString(6,partnerDefaultConfigVO.getTransactionsAccess());
            preparedStatement.setString(7,partnerDefaultConfigVO.getInvoicingAccess());
            preparedStatement.setString(8,partnerDefaultConfigVO.getVirtualTerminalAccess());
            preparedStatement.setString(9,partnerDefaultConfigVO.getMerchantMgtAccess());
            preparedStatement.setString(10,partnerDefaultConfigVO.getSettingsFraudRuleConfigAccess());
            preparedStatement.setString(11,partnerDefaultConfigVO.getSettingsMerchantConfigAccess());
            preparedStatement.setString(12,partnerDefaultConfigVO.getAccountsAccountSummaryAccess());
            preparedStatement.setString(13,partnerDefaultConfigVO.getAccountsChargesSummaryAccess());
            preparedStatement.setString(14,partnerDefaultConfigVO.getAccountsTransactionSummaryAccess());
            preparedStatement.setString(15,partnerDefaultConfigVO.getAccountsReportsSummaryAccess());
            preparedStatement.setString(16,partnerDefaultConfigVO.getSettingsMerchantProfileAccess());
            preparedStatement.setString(17,partnerDefaultConfigVO.getSettingsOrganisationProfileAccess());
            preparedStatement.setString(18, partnerDefaultConfigVO.getSettingsCheckoutPageAccess());
            preparedStatement.setString(19,partnerDefaultConfigVO.getSettingsGenerateKeyAccess());
            preparedStatement.setString(20, partnerDefaultConfigVO.getSettingsInvoiceConfigAccess());
            preparedStatement.setString(21,partnerDefaultConfigVO.getTransMgtTransactionAccess());
            preparedStatement.setString(22,partnerDefaultConfigVO.getTransMgtCaptureAccess());
            preparedStatement.setString(23,partnerDefaultConfigVO.getTransMgtReversalAccess());
            preparedStatement.setString(24,partnerDefaultConfigVO.getTransMgtPayoutAccess());
            preparedStatement.setString(25,partnerDefaultConfigVO.getInvoiceGenerateAccess());
            preparedStatement.setString(26,partnerDefaultConfigVO.getInvoiceHistoryAccess());
            preparedStatement.setString(27, partnerDefaultConfigVO.getTokenMgtRegisterCardAccess());
            preparedStatement.setString(28,partnerDefaultConfigVO.getTokenMgtRegistrationHistoryAccess());
            preparedStatement.setString(29,partnerDefaultConfigVO.getMerchantMgtUserManagementAccess());
            preparedStatement.setString(30,partnerDefaultConfigVO.getIpValidationRequired());
            preparedStatement.setString(31,partnerDefaultConfigVO.getSettingWhiteListDetails());
            preparedStatement.setString(32,partnerDefaultConfigVO.getSettingBlacklistDetails());
            preparedStatement.setString(33,partnerDefaultConfigVO.getEmiConfiguration());
            preparedStatement.setString(34, partnerDefaultConfigVO.getLimitRouting());
            preparedStatement.setString(35, partnerDefaultConfigVO.getCheckoutTimer());
            preparedStatement.setString(36, partnerDefaultConfigVO.getCheckoutTimerTime());
            preparedStatement.setString(37, partnerDefaultConfigVO.getMarketplace());
            preparedStatement.setString(38, partnerDefaultConfigVO.getRejectedTransaction());
            preparedStatement.setString(39, partnerDefaultConfigVO.getVirtualCheckOut());
            preparedStatement.setString(40, partnerDefaultConfigVO.getIsVirtualCheckoutAllowed());
            preparedStatement.setString(41, partnerDefaultConfigVO.getIsMobileAllowedForVC());
            preparedStatement.setString(42, partnerDefaultConfigVO.getIsEmailAllowedForVC());
            preparedStatement.setString(43, partnerDefaultConfigVO.getIsCvvStore());
            preparedStatement.setString(44,partnerDefaultConfigVO.getReconciliationNotification());
            preparedStatement.setString(45,partnerDefaultConfigVO.getTransactionNotification());
            preparedStatement.setString(46,partnerDefaultConfigVO.getReconciliationNotification());
            preparedStatement.setString(47,partnerDefaultConfigVO.getChargebackNotification());
            preparedStatement.setString(48,partnerDefaultConfigVO.getMerchantRegistrationMail());
            preparedStatement.setString(49,partnerDefaultConfigVO.getMerchantChangePassword());
            preparedStatement.setString(50,partnerDefaultConfigVO.getMerchantChangeProfile());
            preparedStatement.setString(51,partnerDefaultConfigVO.getTransactionSuccessfulMail());
            preparedStatement.setString(52,partnerDefaultConfigVO.getTransactionFailMail());
            preparedStatement.setString(53,partnerDefaultConfigVO.getTransactionCapture());
            preparedStatement.setString(54,partnerDefaultConfigVO.getTransactionPayoutSuccess());
            preparedStatement.setString(55,partnerDefaultConfigVO.getTransactionPayoutFail());
            preparedStatement.setString(56,partnerDefaultConfigVO.getRefundMail());
            preparedStatement.setString(57,partnerDefaultConfigVO.getChargebackMail());
            preparedStatement.setString(58,partnerDefaultConfigVO.getTransactionInvoice());
            preparedStatement.setString(59,partnerDefaultConfigVO.getCardRegistration());
            preparedStatement.setString(60,partnerDefaultConfigVO.getPayoutReport());
            preparedStatement.setString(61,partnerDefaultConfigVO.getMonitoringAlertMail());
            preparedStatement.setString(62,partnerDefaultConfigVO.getMonitoringSuspensionMail());
            preparedStatement.setString(63,partnerDefaultConfigVO.getHighRiskRefunds());
            preparedStatement.setString(64,partnerDefaultConfigVO.getFraudFailedTxn());
            preparedStatement.setString(65,partnerDefaultConfigVO.getDailyFraudReport());
            preparedStatement.setString(66,partnerDefaultConfigVO.getCustomerTransactionSuccessfulMail());
            preparedStatement.setString(67,partnerDefaultConfigVO.getCustomerTransactionFailMail());
            preparedStatement.setString(68,partnerDefaultConfigVO.getCustomerTransactionPayoutSuccess());
            preparedStatement.setString(69,partnerDefaultConfigVO.getCustomerTransactionPayoutFail());
            preparedStatement.setString(70,partnerDefaultConfigVO.getCustomerRefundMail());
            preparedStatement.setString(71,partnerDefaultConfigVO.getCustomerTokenizationMail());
            preparedStatement.setString(72,partnerDefaultConfigVO.getIsUniqueOrderIdRequired());
            preparedStatement.setString(73,partnerDefaultConfigVO.getEmailTemplateLang());
            preparedStatement.setString(74,partnerDefaultConfigVO.getSuccessReconMail());
            preparedStatement.setString(75,partnerDefaultConfigVO.getRefundReconMail());
            preparedStatement.setString(76,partnerDefaultConfigVO.getChargebackReconMail());
            preparedStatement.setString(77,partnerDefaultConfigVO.getPayoutReconMail());
            preparedStatement.setString(78,partnerDefaultConfigVO.getIsMerchantLogoBO());
            preparedStatement.setString(79,partnerDefaultConfigVO.getCardExpiryDateCheck());
            preparedStatement.setString(80,partnerDefaultConfigVO.getPayoutNotification());
            preparedStatement.setString(81,partnerDefaultConfigVO.getVpaAddressLimitCheck());
            preparedStatement.setString(82,partnerDefaultConfigVO.getVpaAddressDailyCount());
            preparedStatement.setString(83,partnerDefaultConfigVO.getVpaAddressAmountLimitCheck());
            preparedStatement.setString(84,partnerDefaultConfigVO.getVpaAddressDailyAmountLimit());
            preparedStatement.setString(85,partnerDefaultConfigVO.getPayoutBankAccountNoLimitCheck());
            preparedStatement.setString(86,partnerDefaultConfigVO.getBankAccountNoDailyCount());
            preparedStatement.setString(87,partnerDefaultConfigVO.getPayoutBankAccountNoAmountLimitCheck());
            preparedStatement.setString(88,partnerDefaultConfigVO.getBankAccountNoDailyAmountLimit());
            preparedStatement.setString(89,partnerDefaultConfigVO.getIsDomainWhitelisted());
            preparedStatement.setString(90,partnerDefaultConfigVO.getCustomerIpLimitCheck());
            preparedStatement.setString(91,partnerDefaultConfigVO.getCustomerIpDailyCount());
            preparedStatement.setString(92,partnerDefaultConfigVO.getCustomerIpAmountLimitCheck());
            preparedStatement.setString(93,partnerDefaultConfigVO.getCustomerIpDailyAmountLimit());
            preparedStatement.setString(94,partnerDefaultConfigVO.getCustomerNameLimitCheck());
            preparedStatement.setString(95,partnerDefaultConfigVO.getCustomerNameDailyCount());
            preparedStatement.setString(96,partnerDefaultConfigVO.getCustomerNameAmountLimitCheck());
            preparedStatement.setString(97,partnerDefaultConfigVO.getCustomerNameDailyAmountLimit());
            preparedStatement.setString(98,partnerDefaultConfigVO.getCustomerEmailLimitCheck());
            preparedStatement.setString(99,partnerDefaultConfigVO.getCustomerEmailDailyCount());
            preparedStatement.setString(100,partnerDefaultConfigVO.getCustomerEmailAmountLimitCheck());
            preparedStatement.setString(101,partnerDefaultConfigVO.getCustomerEmailDailyAmountLimit());
            preparedStatement.setString(102,partnerDefaultConfigVO.getCustomerPhoneLimitCheck());
            preparedStatement.setString(103,partnerDefaultConfigVO.getCustomerPhoneDailyCount());
            preparedStatement.setString(104,partnerDefaultConfigVO.getCustomerPhoneAmountLimitCheck());
            preparedStatement.setString(105,partnerDefaultConfigVO.getCustomerPhoneDailyAmountLimit());
            preparedStatement.setString(106,partnerDefaultConfigVO.getInquiryNotification());
            preparedStatement.setString(107,partnerDefaultConfigVO.getPaybylink());
            preparedStatement.setString(108,partnerDefaultConfigVO.getIsOTPRequired());
            preparedStatement.setString(109,partnerDefaultConfigVO.getIsCardStorageRequired());
            int k1=preparedStatement.executeUpdate();
            if (k1>0)
            {
                merchants.refresh();
                FraudTransactionDAO.loadFraudTransactionThresholdForMerchant();
            }
        }
        catch (SystemError se)
        {
            logger.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return mem;
    }
    public DirectKitResponseVO regenerateAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session                     = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName                        = commonValidatorVO.getMerchantDetailsVO().getLogin();
        String partnerid                        = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        AuthFunctions authFunctions             = new AuthFunctions();
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member           = null;
        Merchants merchants     = new Merchants();
        String status       = "";
        String authToken    = "";
        String error        = "";
        String role         = (String)request.getAttribute("role");
        try
        {

            boolean isTokenExpired = authFunctions.verifyExpiry(commonValidatorVO.getAuthToken(),loginName,role);
            if (isTokenExpired)
            {
                member      = merchants.authenticate(loginName, partnerid, request);
                authToken   = authFunctions.getAuthToken(loginName, role);
            }
            else
            {
                error = "Invalid Token";
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
                PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            error       = "Authentication failed";
            directKitResponseVO.setStatusMsg("change password failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }
        logger.debug("successfull login----");
        status      = "success";
        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);
        directKitResponseVO.setMemberId(String.valueOf(member.memberid));
        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
        return directKitResponseVO;
    }
    /*public void updateFlag (int memberid)
    {

        String query = "UPDATE members SET isappmanageractivate='Y',icici='Y' WHERE memberid=?";
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,memberid);
            ps.executeUpdate();

        }
        catch (Exception e)
        {
            logger.error("Error occured while updating flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }*/
    public void updateisemail (String login)
    {
        boolean flag    = false;
        String query    = "UPDATE members SET isemailverified='Y' WHERE login=?";
        Connection conn = null;
        try
        {
            conn                    = Database.getConnection();
            PreparedStatement ps    = conn.prepareStatement(query);
            ps.setString(1,login);
            ps.executeUpdate();
            //System.out.println("query of email flag----"+query);
        }
        catch (Exception e)
        {
            logger.error("Error occured while updating ismail flag", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public String isMember(String login)
    {
        Connection conn = null;
        String etoken   = "";
        try
        {
            conn            = Database.getConnection();
            logger.debug("check isMember method");
            String selquery         = "SELECT memberid,emailtoken,isemailverified FROM members WHERE login =? ";
            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                etoken  = rs.getString("emailtoken");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return etoken;
    }
    public String Memberid(String login)
    {
        Connection conn = null;
        String memberid = "";
        try
        {
            conn    = Database.getConnection();
            logger.debug("check isMember method");
            String selquery         = "SELECT memberid,emailtoken,isemailverified FROM members WHERE login =? ";
            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            ResultSet rs    = pstmt.executeQuery();
            if (rs.next())
            {
                memberid    = rs.getString("memberid");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return memberid;
    }
    public String emailverified(String login)
    {
        Connection conn     = null;
        String emailverify  = "";
        try
        {
            conn    = Database.getConnection();
            logger.debug("check isMember method");
            String selquery = "SELECT isemailverified FROM members WHERE login =? ";

            PreparedStatement pstmt = conn.prepareStatement(selquery);
            pstmt.setString(1, login);
            ResultSet rs    = pstmt.executeQuery();
            if (rs.next())
            {
                emailverify = rs.getString("isemailverified");
            }
        }
        catch (SystemError se)
        {
            logger.error(" SystemError in isMember method: ",se);
        }
        catch (SQLException e)
        {
            logger.error("Exception in isMember method: ",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return emailverify;
    }
    public DirectKitResponseVO removeAuthToken(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();
        Merchants merchants = new Merchants();
        boolean status = merchants.updateAuthtoken(commonValidatorVO.getMerchantDetailsVO().getLogin(), commonValidatorVO.getMerchantDetailsVO().getRole(), "");
        if (status)
            directKitResponseVO.setStatus("success");
        else
            directKitResponseVO.setStatus("failed");

        return directKitResponseVO;
    }

    public DirectKitResponseVO processGenerateOTP(CommonValidatorVO commonValidatorVO)
    {
        OTPManager otpManager = new OTPManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String otpStatus = otpManager.insertOtp(commonValidatorVO);
        directKitResponseVO.setStatus(otpStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
        return directKitResponseVO;
    }

    public DirectKitResponseVO otpVerification(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        OTPManager otpManager = new OTPManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String verifyStatus = otpManager.isVerifyMerchantOtp(commonValidatorVO);
        directKitResponseVO.setStatus(verifyStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_VERIFICATION + verifyStatus);
        return directKitResponseVO;
    }
    public DirectKitResponseVO processCreateLoginMerchantOTP(CommonValidatorVO commonValidatorVO)
    {
        OTPValidationManager otpManager = new OTPValidationManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String otpStatus = otpManager.insertOtpForLoginMerchant(commonValidatorVO);
        directKitResponseVO.setStatus(otpStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
        return directKitResponseVO;
    }

    public DirectKitResponseVO loginMerchantOTPVerification(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        OTPValidationManager otpManager = new OTPValidationManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String verifyStatus = otpManager.isVerifyMerchantLoginOtp(commonValidatorVO);
        directKitResponseVO.setStatus(verifyStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_VERIFICATION + verifyStatus);
        return directKitResponseVO;
    }
    public DirectKitResponseVO getMerchantThemeByMerchantId(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {

        transactionLogger.error("Inside getMerchantThemeByMerchantId ---> "+commonValidatorVO.getMerchantDetailsVO().getMemberId());
        Merchants merchants                             = new Merchants();
        DirectKitResponseVO directKitResponseVO         = new DirectKitResponseVO();
        HashMap<String,String> merchantTemplateHM       = null;
        String merchant_uploaded_path                   = null;
        List<AsyncParameterVO> listOfAsyncParameterVo   = new ArrayList<AsyncParameterVO>();
        try
        {
            try
            {
                merchant_uploaded_path  = ApplicationProperties.getProperty("MERCHANT_LOGO_PATH");
            }
            catch(Exception e)
            {
                transactionLogger.error("Exception while upload logo---",e);
            }
            merchantTemplateHM = merchants.geMemberTemplateDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            directKitResponseVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

            if (merchantTemplateHM == null || merchantTemplateHM.isEmpty())
            {
                merchantTemplateHM      = merchants.getMemberPartnerTemplateDetails(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                merchant_uploaded_path  = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
            }

            if (merchantTemplateHM.containsKey("MERCHANTLOGONAME") && !merchantTemplateHM.isEmpty() )
            {
                String imageName        = merchantTemplateHM.getOrDefault("MERCHANTLOGONAME", "");
                imageName               = merchant_uploaded_path + imageName ;
                String base64String     = functions.encodeFileToBase64Binary(imageName);
                merchantTemplateHM.put("MERCHANTIMAGE",base64String);
            }

            if(merchantTemplateHM != null && !merchantTemplateHM.isEmpty()){
                for(String key :merchantTemplateHM.keySet() ){
                    AsyncParameterVO asyncParameterVO =  new AsyncParameterVO();
                    asyncParameterVO.setName(key);
                    asyncParameterVO.setValue(merchantTemplateHM.getOrDefault(key, ""));

                    listOfAsyncParameterVo.add(asyncParameterVO);
                }
                if(listOfAsyncParameterVo != null  && listOfAsyncParameterVo.size() > 0){
                    directKitResponseVO.getListOfAsyncParameterVo().addAll(listOfAsyncParameterVo);
                    directKitResponseVO.setStatusMsg(ErrorMessages.MERCHANT_THEME_FOUND);
                }else{
                    directKitResponseVO.setStatusMsg(ErrorMessages.MERCHANT_THEME_NOT_FOUND);
                }
            }else{
                directKitResponseVO.setStatusMsg(ErrorMessages.MERCHANT_THEME_NOT_FOUND);
            }

        }catch (Exception e){
            logger.error("Exception getMerchantThemeByMerchantId ---> ",e);
        }
        return directKitResponseVO;
    }

    public DirectKitResponseVO processMemberAllTerminalFlags(CommonValidatorVO commonValidatorVO)
    {
        OTPManager otpManager = new OTPManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String otpStatus = otpManager.insertOtp(commonValidatorVO);
        directKitResponseVO.setStatus(otpStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
        return directKitResponseVO;
    }
}
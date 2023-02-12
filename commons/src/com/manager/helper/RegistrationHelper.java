package com.manager.helper;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PartnerManager;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TokenDetailsVO;
import com.manager.vo.TokenRequestVO;
import com.payment.checkers.PaymentChecker;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/*
*
 * Created by Sneha on 8/22/2016.

*/

public class RegistrationHelper
{
    private static Logger log = new Logger(TransactionHelper.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionHelper.class.getName());
    private static Functions functions = new Functions();

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    //useful for standalone token generation
    public CommonValidatorVO performCustomerRegistrationChecksForStandAloneToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = commonValidatorVO.getErrorCodeListVO();
        String error = null;

        partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getMerchantDetailsVO().getPartnerId()); //fetch the partner details with partnerId
        if (partnerDetailsVO == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_PARTNER);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
        {

        }

        return commonValidatorVO;
    }


    public CommonValidatorVO performCustomerRegistrationChecksForToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        PaymentChecker paymentChecker = new PaymentChecker();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();
        String error = null;
        String key = null;
        String userId = null;
        String checksumAlgo = null;

        //Fetching List of cards and accounts by partnerId
        if (functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
        {
            log.debug("Inside partnerId validation::");
            String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
            //PartnerId Validation
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false) || !functions.isNumericVal(partnerId))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getPartnerDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_PARTNER);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            if ("Y".equals(partnerDetailsVO.getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("ip address--------" + partnerId + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForPartner(partnerId, commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_PARTNER_IPWHITELIST_CHECK);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    commonValidatorVO.setErrorMsg(error);
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return commonValidatorVO;
                }
            }

            if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MERCHANT_REQUIRED_TO_PROCEED_REQUEST);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            userId = partnerDetailsVO.getPartnerId();
            key = partnerDetailsVO.getPartnerKey();
        }
        //Fetching List of cards and accounts by memberId
        else if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            log.debug("Inside memberId validation--");
            String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
            //Toid Validation
            if (!ESAPI.validator().isValidInput("toid", toid, "Numbers", 10, false) || !functions.isNumericVal(toid))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getMerchantDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_PARTNER);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            //Merchant Activation and white-listed IP checking
            commonValidatorVO = merchantActivationChecks(commonValidatorVO);

            if ("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MERCHANT_CAN_NOT_PROCEED_REQUEST);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            userId = merchantDetailsVO.getMemberId();
            key = merchantDetailsVO.getKey();
            checksumAlgo = merchantDetailsVO.getChecksumAlgo();
        }
        else //if memberId and PartnerId both are not provided
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        //Checksum verification
        if (!Checksum.verifyChecksumV4(userId, key, commonValidatorVO.getTransDetailsVO().getChecksum(), checksumAlgo))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        //isCardHolderRegistered checking
        if(functions.isValueNull(commonValidatorVO.getCustomerId()))
        {
            if (!isCustomerRegisteredWithToken(commonValidatorVO))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_CUSTOMER);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }
        return commonValidatorVO;
    }

    private boolean isCustomerRegisteredWithToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        boolean isCardholderAvailable = false;
        TokenManager tokenManager = new TokenManager();

        if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
        {
            if (!tokenManager.isCustomerRegisteredWithMerchant(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCustomerId()))
                isCardholderAvailable = false;
            else
                isCardholderAvailable = true;
        }
        else
        {
            if (!tokenManager.isCustomerRegisteredWithPartner(commonValidatorVO.getPartnerDetailsVO().getPartnerId(), commonValidatorVO.getCustomerId()))
                isCardholderAvailable = false;
            else
                isCardholderAvailable = true;
        }
        //System.out.println("isCardholderAvailable--->"+isCardholderAvailable);
        return isCardholderAvailable;
    }

    public CommonValidatorVO performVerificationWithStandAloneToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();
        PaymentChecker paymentChecker = new PaymentChecker();

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        PartnerManager partnerManager = new PartnerManager();

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;

        //variables for checksum calculations
        String checksumAlgo = null;
        String cardDetails = "";

        //cardDetails setting for checksum
        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            cardDetails = commonValidatorVO.getCardDetailsVO().getCardNum();
        else if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
            cardDetails = commonValidatorVO.getCardDetailsVO().getBIC();
        else if (functions.isValueNull(commonValidatorVO.getReserveField2VO().getAccountNumber()))
            cardDetails = commonValidatorVO.getReserveField2VO().getAccountNumber();

        if (functions.isValueNull(partnerDetailsVO.getPartnerId()))
        {
            log.debug("Inside Partner registration---");
            String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
            //PartnerId Validation
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getPartnerDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            if ("Y".equals(partnerDetailsVO.getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("ip address--------" + commonValidatorVO.getPartnerDetailsVO().getPartnerId() + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransactionByPartner(commonValidatorVO.getPartnerDetailsVO().getPartnerId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Partner's IP is not white listed with us. Kindly Contact the pz Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_PARTNER_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }

            //Checksum calculation
            if (!Checksum.verifyInvalidateTokenChecksum(partnerDetailsVO.getPartnerId(), partnerDetailsVO.getPartnerKey(), cardDetails, genericTransDetailsVO.getChecksum(), merchantDetailsVO.getChecksumAlgo()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            //Partner Interface, Merchant Required For CardRegistration should be 'N' for partner level card registration
            if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MERCHANT_REQUIRED_FOR_REGISTRATION);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            //Partner Interface, Tokenization Allowed:'Y'
            if ("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_ALLOWED);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            {
                TokenManager tokenManager = new TokenManager();
                if (!tokenManager.isCardholderRegisteredWithPartner(partnerDetailsVO.getPartnerId(), commonValidatorVO.getCustomerId()))
                {
                    error = "Customer not registered with us.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_UNAUTHORIZE_CUSTOMER);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            genericTransDetailsVO.setTotype(partnerDetailsVO.getPartnerName());
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            //tokenRequestVO.setGeneratedBy(partnerDetailsVO.getCompanyName());
            tokenRequestVO.setRegistrationGeneratedBy(partnerDetailsVO.getCompanyName());
            addressDetailsVO.setEmail(partnerDetailsVO.getContact_emails());
            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
            {
                /*int month= Integer.parseInt(commonValidatorVO.getCardDetailsVO().getExpMonth());
                int year= Integer.parseInt(commonValidatorVO.getCardDetailsVO().getExpYear());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH,1);
                cal.get(Calendar.MONTH);
                cal.get(Calendar.YEAR);

                if(month <= cal.get(Calendar.MONTH))
                {
                    error = "Month is less then or equal to current month.";
                    errorCodeListVO = getErrorVO(ErrorName.VALIDATION_EXP_MONTH);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                else if(year < cal.get(Calendar.YEAR))
                {
                    error = "Year is less then the current year.";
                    errorCodeListVO = getErrorVO(ErrorName.VALIDATION_EXP_YEAR);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
                if (!functions.isFutureExpMonthYear(commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear()))
                {
                    error = "Kindly Pass Valid Card Expiry Month & Year.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_EXP_MONTH_YEAR);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", error, "Common", "Invalid Credit card Exp Month & Year.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        else if (functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            //toId validation
            String toid = merchantDetailsVO.getMemberId();
            if (!ESAPI.validator().isValidInput("toid", toid, "OnlyNumber", 10, false))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_PARTNER);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            //Merchant activation checks
            commonValidatorVO = merchantActivationChecks(commonValidatorVO);

            if ("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_PARTNER_REQUIRED_FOR_REGISTRATION);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            checksumAlgo = merchantDetailsVO.getChecksumAlgo();

            //Checksum calculation
            if (!Checksum.verifyInvalidateTokenChecksum(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), cardDetails, genericTransDetailsVO.getChecksum(), checksumAlgo))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            {
                TokenManager tokenManager = new TokenManager();
                if (!tokenManager.isCardholderRegistered(merchantDetailsVO.getMemberId(), commonValidatorVO.getCustomerId()))
                {
                    error = "Customer not registered with us.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_UNAUTHORIZE_CUSTOMER);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            //tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
            tokenRequestVO.setRegistrationGeneratedBy(merchantDetailsVO.getLogin());
            addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

            if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpMonth()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getExpYear()))
            {
                /*int month= Integer.parseInt(commonValidatorVO.getCardDetailsVO().getExpMonth());
                int year= Integer.parseInt(commonValidatorVO.getCardDetailsVO().getExpYear());
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH,1);
                cal.get(Calendar.MONTH);
                cal.get(Calendar.YEAR);

                if(month <= cal.get(Calendar.MONTH))
                {
                    error = "Month is less then or equal to current month.";
                    errorCodeListVO = getErrorVO(ErrorName.VALIDATION_EXP_MONTH);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                else if(year < cal.get(Calendar.YEAR))
                {
                    error = "Year is less then the current year.";
                    errorCodeListVO = getErrorVO(ErrorName.VALIDATION_EXP_YEAR);
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
                if (!functions.isFutureExpMonthYear(commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear()))
                {
                    error = "Kindly Pass Valid Card Expiry Month & Year.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_EXP_MONTH_YEAR);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", error, "Common", "Invalid Credit card Exp Month & Year.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        else
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        /*commonValidatorVO.getTransDetailsVO().setOrderDesc("Test");//Hardcode for p4 gateway specific validation
        commonValidatorVO.getTransDetailsVO().setOrderId((int) (Math.random() * 100000) + "JHG54");//Hardcode for p4 gateway specific validation
        commonValidatorVO.getTransDetailsVO().setAmount("0.00");//Hardcode for p4 gateway specific validation*/

        if (functions.isEmptyOrNull(error))
            commonValidatorVO.setErrorMsg(error);

        return commonValidatorVO;
    }

    public CommonValidatorVO performVerificationWithStandAloneTokenByMerchant(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        //Merchant activation checks
        if (!"Y".equals(commonValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK));
        }

        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
        {
            TokenManager tokenManager = new TokenManager();
            if (!tokenManager.isCardholderRegistered(merchantDetailsVO.getMemberId(), commonValidatorVO.getCustomerId()))
            {
                error = "Customer not registered with us.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_CUSTOMER));
            }
        }
        genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
        addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

        commonValidatorVO.getTransDetailsVO().setOrderDesc("Test");//Hardcode for p4 gateway specific validation
        commonValidatorVO.getTransDetailsVO().setOrderId((int) (Math.random() * 100000) + "JHG54");//Hardcode for p4 gateway specific validation
        commonValidatorVO.getTransDetailsVO().setAmount("0.00");//Hardcode for p4 gateway specific validation

        if (functions.isEmptyOrNull(error))
            commonValidatorVO.setErrorMsg(error);

        return commonValidatorVO;
    }

    private CommonValidatorVO merchantActivationChecks(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        PaymentChecker paymentChecker = new PaymentChecker();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        //IP Whitelist check
        if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equals((commonValidatorVO.getMerchantDetailsVO().getIsIpWhiteListedCheckForAPIs())))
        {
            transactionLogger.debug("ip address--------" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the pz Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!"Y".equals(commonValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if (functions.isEmptyOrNull(error))
            commonValidatorVO.setErrorMsg(error);

        return commonValidatorVO;
    }

    public CommonValidatorVO performRESTAPISystemCheckForCardholderRegistration(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        if (functions.isValueNull(partnerDetailsVO.getPartnerId()))
        {
            String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_PARTNERID));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getPartnerDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_MERCHANT_REQUIRED_FOR_REGISTRATION));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            //Checksum calculation with partner details
            if (!Checksum.verifyMD5ChecksumForCardHolderRegistration(partnerId, partnerDetailsVO.getPartnerKey(), addressDetailsVO.getFirstname(), addressDetailsVO.getLastname(), addressDetailsVO.getEmail(), commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            merchantDetailsVO.setPartnerId(partnerDetailsVO.getPartnerId());
        }

        else if (functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            String toid = merchantDetailsVO.getMemberId();
            if (!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 10, false))
            {
                error = "Invalid member ID, member ID should not be empty and should be numeric with max length 10";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                error = "Invalid member ID OR member ID is misconfigured";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Unauthorized partner";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            if ("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                error = "Merchant can not proceed the registration";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_MERCHANT_CAN_NOT_PROCEED_REGISTRATION));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            if (!Checksum.verifyMD5ChecksumForCardHolderRegistration(toid, merchantDetailsVO.getKey(), addressDetailsVO.getFirstname(), addressDetailsVO.getLastname(), addressDetailsVO.getEmail(), commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                commonValidatorVO.setErrorCodeListVO((getErrorVO(ErrorName.SYS_INVALID_CHECKSUM)));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
            {
                //Moved Blacklist checks from system check step1 to system check step2
                if (!isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_BLOCKEDEMAIL));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
                if (!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc()))
                {
                    error = "Your Country is Blocked:::Please contact support for further assistance";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }

                //Added new system check for cardholder name in blacklist entry
                if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
                {
                    error = "Customer Name Has Blocked:::Please contact support for further assistance";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_BLOCKEDNAME));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
            }
        }
        else
        {
            error = "Customer Name Has Blocked:::Please contact support for further assistance";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_REQUEST_NULL));
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        if ("N".equalsIgnoreCase(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            error = "You cannot register token";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_TOKEN_ALLOWED));
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        return commonValidatorVO;
    }

    public boolean isEmailAddressBlocked(String emailAddress) throws PZDBViolationException
    {
        boolean isEmailBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_email WHERE emailAddress=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, emailAddress);

            rs = p.executeQuery();
            if (rs.next())
            {
                isEmailBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isEmailBlocked;
    }

    public boolean isCountryBlocked(String countryCode, String telnocc) throws PZDBViolationException
    {
        boolean isCountryBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_country WHERE country_code=? OR telnocc=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, countryCode);
            p.setString(2, telnocc);

            rs = p.executeQuery();
            if (rs.next())
            {
                isCountryBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isCountryBlocked;
    }

    public boolean isNameBlocked(String name) throws PZDBViolationException
    {
        boolean isNameBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_name WHERE name=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, name);
            rs = p.executeQuery();
            if (rs.next())
            {
                isNameBlocked = false;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isNameBlocked;
    }

    public CommonValidatorVO performRESTAPISystemCheckForMerchantSignup(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        String partnerId = commonValidatorVO.getMerchantDetailsVO().getPartnerId();
        partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);

        log.debug("Requested Checksum---"+commonValidatorVO.getTransDetailsVO().getChecksum());
        if (!Checksum.verifyMD5ChecksumForMerchantSignup(commonValidatorVO.getMerchantDetailsVO().getLogin(), partnerDetailsVO.getPartnerKey(), commonValidatorVO.getMerchantDetailsVO().getNewPassword(), partnerId, commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if (!commonValidatorVO.getMerchantDetailsVO().getNewPassword().equals(commonValidatorVO.getMerchantDetailsVO().getConPassword()))
        {
            error = "Password and confirm password are not matching.";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_PASSWORD_MATCH));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO performChecksumVerification(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        RestCommonInputValidator restCommonInputValidator = new RestCommonInputValidator();
        Merchants merchants=new Merchants();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String error = "";


        String toid = merchantDetailsVO.getMemberId();
        if (!ESAPI.validator().isValidInput("toid", toid, "OnlyNumber", 10, false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        merchantDetailsVO = restCommonInputValidator.getMerchantConfigDetailsByLogin(toid);
        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        merchantDetailsVO.setRole("merchant");
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getLogin()) && !merchantDetailsVO.getLogin().equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLogin()))
        {
            String userid=merchants.isMemberUser(commonValidatorVO.getMerchantDetailsVO().getLogin(),merchantDetailsVO.getMemberId());
            if(!functions.isValueNull(userid)){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setLogin(commonValidatorVO.getMerchantDetailsVO().getLogin());
            merchantDetailsVO.setRole("submerchant");
            merchantDetailsVO.setUserid(userid);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        if (!Checksum.verifyChecksumWithV4(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getInvoiceAction(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {

            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRESTAPIDeleteRegChecks(CommonValidatorVO commonValidatorVO) throws Exception
    {
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        TokenManager tokenManager = new TokenManager();
        TerminalManager terminalManager = new TerminalManager();
        String error = null;

        if (functions.isValueNull(partnerDetailsVO.getPartnerId()))
        {
            String partnerId = partnerDetailsVO.getPartnerId();
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_PARTNERID));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

/*
            if(functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                if(!terminalManager.isPartnersMerchantMappedWithTerminal(partnerDetailsVO.getPartnerId(),commonValidatorVO.getTerminalId()))
                {
                    error = "Terminal ID provided by you is not valid for your merchants. Please check your technical specification.";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_TERMINAL_BY_PARTNER));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
            }
*/

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_MERCHANT_REQUIRED_FOR_DELETE_TOKEN));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(partnerDetailsVO.getPartnerId(), commonValidatorVO.getToken(), commonValidatorVO); //get Partner level registration details

            if ("N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
            {
                error = "Registration id already deleted.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_TOKEN_DELETED));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            String toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
            if (!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 10, false))
            {
                error = "Invalid member ID, member ID should not be empty and should be numeric with max length 10";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            if(functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                if(!terminalManager.isMemberMappedWithTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                {
                    error = "Terminal ID provided by you is not valid for registration";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_TOKEN_TERMINAL));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getMerchantDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Unauthorized partner";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            if("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_MERCHANT_CANNOT_DELETE_TOKEN));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            else
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getToken(), commonValidatorVO, tokenDetailsVO); //get Merchant level registration details

            if ("N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
            {
                error = "Registration id already deleted.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_TOKEN_DELETED));
                PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

        }

        else
        {
            error = "Invalid request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_REQUEST_NULL));
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        if(!functions.isValueNull(tokenDetailsVO.getToken()))
        {
            error = "Invalid RegistrationId or RegistrationId not found";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_REFERENCE_TOKEN));
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        else if("N".equals(tokenDetailsVO.getIsActive()))
        {
            error = "RegistrationId already Deleted";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN));
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPIDeleteRegChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    public MerchantDetailsVO getMerchantConfigDetailsByLogin(String memberId) throws PZDBViolationException, PZConstraintViolationException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO=new MerchantDAO();

        //merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        return merchantDetailsVO;
    }
}
package com.merchant.helper;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PartnerManager;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.merchant.dao.RegistrationDAO;
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

/*
*
 * Created by Sneha on 8/22/2016.

*/

public class RegistrationHelper
{
    private static Logger log = new Logger(RegistrationHelper.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RegistrationHelper.class.getName());
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

    public CommonValidatorVO performRESTAPISystemCheckForGenerateAppOTP(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
        partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);

        log.debug("Requested Checksum---"+commonValidatorVO.getTransDetailsVO().getChecksum());
        //System.out.println("mobilecc--" + commonValidatorVO.getAddressDetailsVO().getTelnocc());
        if (!Checksum.verifyChecksumV3(partnerId, partnerDetailsVO.getPartnerKey(), commonValidatorVO.getAddressDetailsVO().getPhone(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO performChecksumVerificationForMerchantCurrencies(CommonValidatorVO commonValidatorVO) throws NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";
        if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);

        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performChecksumVerificationForMerchantChangePassword(CommonValidatorVO commonValidatorVO) throws NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";
        if(!Checksum.verifyMD5ChecksumForMerchantChangePassword(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getMerchantDetailsVO().getLogin(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);

        }

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
        RegistrationDAO registrationDAO = new RegistrationDAO();

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
                if (!registrationDAO.isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_BLOCKEDEMAIL));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }
                if (!registrationDAO.isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc()))
                {
                    error = "Your Country is Blocked:::Please contact support for further assistance";
                    commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY));
                    PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performRESTAPISystemCheckForCardholderRegistration()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
                }

                //Added new system check for cardholder name in blacklist entry
                if (!registrationDAO.isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
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


}
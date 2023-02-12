package com.payment.validators;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fraud.vo.Result;
import com.google.gson.Gson;
import com.manager.*;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.dao.PaymentDAO;
import com.manager.dao.RecurringDAO;
import com.manager.enums.TransReqRejectCheck;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.payment.Enum.CallTypeEnum;
import com.payment.checkers.PaymentChecker;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sneha on 3/23/2016.
 */
public class RestCommonInputValidator
{
    private static Logger log                           = new Logger(RestCommonInputValidator.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(RestCommonInputValidator.class.getName());
    private Functions functions                         = new Functions();
    private PaymentChecker paymentChecker               = new PaymentChecker();

    private CommonValidatorVO performChecksumVerification(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        transactionLogger.debug("inside performChecksumVerification");

        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        String toid = merchantDetailsVO.getMemberId();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        TransactionManager transactionManager=new TransactionManager();
        MarketPlaceVO marketPlaceVO=null;

        //Toid Validation
        if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }
        transactionLogger.debug("amount-----"+genericTransDetailsVO.getAmount());
        if(!functions.isValueNull(genericTransDetailsVO.getAmount()) || !ESAPI.validator().isValidInput("amount",genericTransDetailsVO.getAmount(),"tenDigitAmount",12,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_AMOUNT.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(), ErrorType.VALIDATION.toString());
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            if(functions.isValueNull( commonValidatorVO.getTrackingid()))
            {
                //for capture and refund transaction
                if (!Checksum.verifyMD5ChecksumRest(toid, merchantDetailsVO.getKey(), commonValidatorVO.getTrackingid(), genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getChecksum()))
                {                    log.error("inside if trackingid not null  verifyMD5ChecksumRest orderid-->"+genericTransDetailsVO.getOrderId()+" amount-->"+genericTransDetailsVO.getChecksumAmount());

                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_CHECKSUM.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                //Activation check
                if (!merchantDetailsVO.getActivation().equals("Y"))
                {
                    error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_ACTIVATION_CHECK.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }

                //getting partner details
                PartnerManager partnerManager = new PartnerManager();
                PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

                //IP Whitelist check
                if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListedCheckForAPIs()))
                {
                    transactionLogger.debug("ip address--------" + merchantDetailsVO.getMemberId() + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
                    if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                    {
                        error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_IPWHITELIST_CHECK.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
                if (commonValidatorVO.getMarketPlaceVOList() == null)//Get Parent Details
                {
                    marketPlaceVO = transactionManager.getParentDetailsByChildTrackingid(commonValidatorVO.getTrackingid());
                    if (marketPlaceVO != null)
                    {
                        commonValidatorVO.setMarketPlaceVO(marketPlaceVO);
                    }
                    List<MarketPlaceVO> list=transactionManager.getChildDetailsByParentTrackingid(commonValidatorVO.getTrackingid());
                    if (list != null && list.size()>0)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARAMETERS_MP);
                        error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                        commonValidatorVO.setErrorMsg(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PARAMETERS_MP.toString(), ErrorType.VALIDATION.toString());
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return commonValidatorVO;
                    }
                }
                else
                {
                    if ("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_REFUND_ALLOWED);
                        error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                        commonValidatorVO.setErrorMsg(error);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_REFUND_ALLOWED_MP.toString(), ErrorType.VALIDATION.toString());
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return commonValidatorVO;
                    }
                    else
                    {
                        List<MarketPlaceVO> marketPlaceVOList = new ArrayList<>();
                        MerchantDetailsVO merchantDetailsVO1 = new MerchantDetailsVO();
                        float totalamount = 0;
                        for (int i = 0; i < commonValidatorVO.getMarketPlaceVOList().size(); i++)
                        {
                            marketPlaceVO = commonValidatorVO.getMarketPlaceVOList().get(i);
                            totalamount += Float.parseFloat(marketPlaceVO.getRefundAmount());
                            MarketPlaceVO marketPlaceVO1 = transactionManager.getChildDetailsByChildTrackingid(marketPlaceVO.getTrackingid());
                            if (!commonValidatorVO.getTrackingid().equalsIgnoreCase(marketPlaceVO1.getParentTrackingid()))
                            {
                                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRACKINGID);
                                error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                                commonValidatorVO.setErrorMsg(error);
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TRACKINGID_MP.toString(), ErrorType.VALIDATION.toString());
                                if (commonValidatorVO.getErrorCodeListVO() != null)
                                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                                return commonValidatorVO;
                            }
                            merchantDetailsVO1 = getMerchantConfigDetailsByLogin(marketPlaceVO1.getMemberid());
                            marketPlaceVO.setMemberid(marketPlaceVO1.getMemberid());
                            marketPlaceVO1.setMerchantDetailsVO(merchantDetailsVO1);
                            marketPlaceVO.setCapturedAmount(marketPlaceVO1.getCapturedAmount());
                            marketPlaceVO.setReversedAmount(marketPlaceVO1.getReversedAmount());
                            marketPlaceVO.setStatus(marketPlaceVO1.getStatus());
                            marketPlaceVOList.add(marketPlaceVO);
                        }
                        if (!String.format("%.2f", totalamount).equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getAmount()))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTAL_AMOUNT_MP);
                            error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                            commonValidatorVO.setErrorMsg(error);
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOTAL_AMOUNT_MP.toString(), ErrorType.VALIDATION.toString());
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                            return commonValidatorVO;
                        }
                        commonValidatorVO.setMarketPlaceVOList(marketPlaceVOList);
                    }
                }
            }
            else
            {
                //for Sync and Async transaction
                if (!ESAPI.validator().isValidInput("orderId", genericTransDetailsVO.getOrderId(), "Description", 180, false))
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DESCRIPTION);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_DESCRIPTION);
                    error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
                    commonValidatorVO.setErrorMsg(error);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_DESCRIPTION.toString(), ErrorType.VALIDATION.toString());
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    return commonValidatorVO;
                }
                if (!Checksum.verifyMD5ChecksumRest(toid, merchantDetailsVO.getKey(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getChecksum()))
                {
                    log.error("inside if  verifyMD5ChecksumRest orderid-->"+genericTransDetailsVO.getOrderId()+" amount-->"+genericTransDetailsVO.getChecksumAmount());
                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_CHECKSUM.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                //Activation check
                if (!merchantDetailsVO.getActivation().equals("Y"))
                {
                    error = "Error- The Merchant Account is not set to LIVE mode. This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MEMBER_ACTIVATION_CHECK.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }

                //getting partner details
                PartnerManager partnerManager = new PartnerManager();
                PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                if(commonValidatorVO.getTransDetailsVO() != null && partnerDetailsVO != null)
                {
                    commonValidatorVO.getTransDetailsVO().setTotype(partnerDetailsVO.getCompanyName());
                }
                //IP Whitelist check
                if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListedCheckForAPIs()))
                {
                    transactionLogger.error("commonValidatorVO.getAddressDetailsVO().getIp()-------------------->"+commonValidatorVO.getAddressDetailsVO().getIp());
                    if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                    {
                        error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_IPWHITELIST_CHECK.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }

                //Partner and Merchant level Address validation checks
                //CommonInputValidator validator = new CommonInputValidator();
                //error = validator.validatePartnerMerchantFlagBasedAddressField(commonValidatorVO, "REST");
            }

            genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
            addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());
        }

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    private CommonValidatorVO performVerificationForCancelInquiryAndDeleteToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO           = commonValidatorVO.getPartnerDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = commonValidatorVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = commonValidatorVO.getAddressDetailsVO();
        TokenRequestVO tokenRequestVO       = new TokenRequestVO();
        PartnerManager partnerManager       = new PartnerManager();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();

        if(functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            transactionLogger.debug("inside performVerificationForCancelInquiryAndDeleteToken::");
            String toid = merchantDetailsVO.getMemberId();
            //Toid Validation
            if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            transactionLogger.debug("PartnerId-->"+merchantDetailsVO.getPartnerId());
            //partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            //commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            transactionLogger.debug("Merchant Activation---->"+merchantDetailsVO.getActivation());
            //Activation check
            if (!merchantDetailsVO.getActivation().equals("Y"))
            {
                error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
            }
            //IP Whitelist check
            transactionLogger.debug("commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()--->"+commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs());
            if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("MerchantId--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
        {
            String partnerId = partnerDetailsVO.getPartnerId();
            //PartnerId Validation
            if(!functions.isValueNull(partnerId) || !ESAPI.validator().isValidInput("partnerid",partnerId,"Numbers",10,false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_PARTNERID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(partnerId); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            //IP Whitelist check
            if("Y".equalsIgnoreCase(partnerDetailsVO.getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("partnerId--------"+partnerId+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransactionByPartner(partnerId, commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        if (functions.isValueNull(commonValidatorVO.getTrackingid()) || functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
        {
            //Inquiry and Cancel checksum verification

            String checksumId=commonValidatorVO.getTrackingid();
            if(functions.isValueNull(commonValidatorVO.getIdType())){
                if(commonValidatorVO.getIdType().equalsIgnoreCase("MID"))
                {
                    checksumId = commonValidatorVO.getTransDetailsVO().getOrderId();
                }
            }
            log.debug("Checksum with trackingId------>"+commonValidatorVO.getTransDetailsVO().getChecksum());
            if (!Checksum.verifyChecksumV3(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(),checksumId,commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getToken()))
        {
            //Delete Token Checksum verification
            log.debug("Checksum with token------>"+commonValidatorVO.getTransDetailsVO().getChecksum());
            if(functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                if (!Checksum.verifyChecksumV3(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getToken(), commonValidatorVO.getTransDetailsVO().getChecksum()))
                {
                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            else
            {
                if (!Checksum.verifyChecksumV3(partnerDetailsVO.getPartnerId(), partnerDetailsVO.getPartnerKey(), commonValidatorVO.getToken(), commonValidatorVO.getTransDetailsVO().getChecksum()))
                {
                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
        tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
        addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    private CommonValidatorVO performVerificationWithTrackingidAndToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        String toid = merchantDetailsVO.getMemberId();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;

        //Toid Validation
        if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }
        merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
        if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        if(functions.isValueNull(commonValidatorVO.getTrackingid()) && functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getRecurringType()))
        {
            if (!Checksum.verifyMD5ChecksumRest(toid, merchantDetailsVO.getKey(), commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getToken()))
        {
            if("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_ALLOWED);
                error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            if (!Checksum.verifyMD5ChecksumRest(toid, merchantDetailsVO.getKey(), commonValidatorVO.getToken(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
        addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

        //getting partner details
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        //IP Whitelist check
        if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListedCheckForAPIs()))
        {
            transactionLogger.debug("ip address--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    //Validations for Rest
    public CommonValidatorVO performRestTransactionValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error                    = "";
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        //Checking login details of merchant
        commonValidatorVO = performChecksumVerification(commonValidatorVO);
        //Transasction type check
        if (("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsService()) && "DB".equalsIgnoreCase(commonValidatorVO.getTransactionType())))//|| ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsService()) && "PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
        {
            error = "Error- The functionality is not allowed for account. Please contact support for help.";
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED_TRANSACTION_TYPE);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.TRANSACTION_REJECTED_TRANSACTION_TYPE.toString(), ErrorType.REJECTED_TRANSACTION.toString());
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        //Validation RecurringType
/*
        if((functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getRecurringType())) && ("REPEATED".equalsIgnoreCase(commonValidatorVO.getRecurringBillingVO().getRecurringType())))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW);
            error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
*/
        error = validateRestKitParameters(commonValidatorVO, "REST");
        if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            error = errorCodeUtils.getSystemErrorCodeVO(commonValidatorVO.getErrorCodeListVO());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }

        error = validateRestKitParametersStep2(commonValidatorVO, "REST");
        if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            error = errorCodeUtils.getSystemErrorCodeVO(commonValidatorVO.getErrorCodeListVO());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }


        /*if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            System.out.println("inside error-----");
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
            return commonValidatorVO;
        }*/
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()) && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage()))
        {
            error=validateMarketPlaceStandardProcess(commonValidatorVO,"REST");
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                String errorName = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                error = errorCodeUtils.getSystemErrorCodeVO(commonValidatorVO.getErrorCodeListVO());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestCaptureValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        //Checking login details of merchant
        commonValidatorVO = performChecksumVerification(commonValidatorVO);

        error =  validateRestKitCaptureParameters(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO performRestCancelValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        //Checking login details of merchant
        commonValidatorVO = performVerificationForCancelInquiryAndDeleteToken(commonValidatorVO);

        error =  validateRestKitCancelParameters(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO performRestInquiryValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        //Checking login details of merchant
        String error = "";
        error = validateRestKitInquiryParameters(directKitValidatorVO, "REST");

        if (functions.isValueNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }
        directKitValidatorVO = performVerificationForCancelInquiryAndDeleteToken(directKitValidatorVO);


        return directKitValidatorVO;
    }
    public CommonValidatorVO performRestRefundValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        //Checking login details of merchant
        commonValidatorVO = performChecksumVerification(commonValidatorVO);
        if(commonValidatorVO.getErrorCodeListVO().getListOfError()!=null && commonValidatorVO.getErrorCodeListVO().getListOfError().size()>0)
        {
            return commonValidatorVO;
        }

        if("N".equals(commonValidatorVO.getMerchantDetailsVO().getIsrefund()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_REFUND_ALLOWED);
            error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        error =  validateRestKitRefundParameters(commonValidatorVO, "REST");
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
        {
            if(commonValidatorVO.getMarketPlaceVOList() != null)
            {
                error = validateMarketPlaceRefundParameters(commonValidatorVO, "REST");
                if (functions.isValueNull(error))
                {
                    commonValidatorVO.setErrorMsg(error);
                    return commonValidatorVO;
                }
            }
            else
            {
                FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARAMETERS_MP);
                error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TRACKINGID_MP.toString(), ErrorType.VALIDATION.toString());
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestRecurringTokenValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        GenericTransDetailsVO genericTransDetailsVO =directKitValidatorVO.getTransDetailsVO();
        TransactionManager transactionManager = new TransactionManager();
        RecurringBillingVO recurringBillingVO = directKitValidatorVO.getRecurringBillingVO();

        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        PartnerDetailsVO partnerDetailsVO = directKitValidatorVO.getPartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        Transaction transaction = new Transaction();
        RecurringDAO recurringDAO = new RecurringDAO();
        String recurringType = recurringBillingVO.getRecurringType();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        ManualRecurringManager manualRecurringManager = new ManualRecurringManager();

        //Checking login details of merchant
        directKitValidatorVO = performVerificationWithTrackingidAndToken(directKitValidatorVO);
        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            return directKitValidatorVO;
        }
        error =  validateRestKitWithTokenParameters(directKitValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }

        MerchantDetailsVO merchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();
        String toid = merchantDetailsVO.getMemberId();

        String orderDes = "";
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyMMddHHmmss");
        String currentSystemDate = dateFormater.format(new Date());

        if(directKitValidatorVO.getToken() != null)
        {
            log.debug("Inside if-------"+directKitValidatorVO.getToken());

            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId
            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(directKitValidatorVO.getMerchantDetailsVO().getMemberId(), directKitValidatorVO.getToken(), directKitValidatorVO, tokenDetailsVO); //Tokn Details By Merchant
            else
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(directKitValidatorVO.getMerchantDetailsVO().getPartnerId(), directKitValidatorVO.getToken(), directKitValidatorVO); //token details by Partner


            if (!functions.isValueNull(tokenDetailsVO.getToken()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MERCHANT_REQUIRED_FOR_REGISTRATION);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }
            if (functions.isValueNull(directKitValidatorVO.getTerminalId()))
            {
                if (!tokenDetailsVO.getTerminalId().equals(directKitValidatorVO.getTerminalId()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }
            }
            else
                directKitValidatorVO.setTerminalId(tokenDetailsVO.getTerminalId());

            if(!functions.isValueNull(directKitValidatorVO.getPaymentType()))
                directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
            if(!functions.isValueNull(directKitValidatorVO.getCardType()))
                directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
            if(!functions.isValueNull(tokenDetailsVO.getCurrency()))
                tokenDetailsVO.setCurrency(directKitValidatorVO.getTransDetailsVO().getCurrency());

            if ("N".equalsIgnoreCase(tokenDetailsVO.getIsActive()) && "N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }

            //getting the initial trackingId of transaction
            tokenDetailsVO = tokenManager.getRegistrationTrackingId(directKitValidatorVO.getMerchantDetailsVO().getMemberId(), tokenDetailsVO);

            if(functions.isValueNull(tokenDetailsVO.getTrackingId()))
            {
                log.debug("Inside trackingid------" + tokenDetailsVO.getTrackingId());
                if(!functions.isValueNull(tokenDetailsVO.getTokenAccountId()) && functions.isValueNull(tokenDetailsVO.getCommCardDetailsVO().getCardNum()))
                {
                    log.debug("trackingid---"+tokenDetailsVO.getTrackingId());
                    //REPEATED transaction with card
                    ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        return directKitValidatorVO;
                    error = error + commonInputValidator.validateRestFlagBasedAddressField(directKitValidatorVO, "REST");
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }

                    String accountid = directKitValidatorVO.getTerminalVO().getAccountId();
                    directKitValidatorVO.getTransDetailsVO().setOrderDesc("Test");////Hardcode for p4
                    directKitValidatorVO.getTransDetailsVO().setOrderId((int)(Math.random()*100000)+"JHG54");////Hardcode for p4
                    String accountAddressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
                    error = error + paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "REST", accountAddressValidation);

                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }

                    /*CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                    if(!ESAPI.validator().isValidInput("cvv", directKitValidatorVO.getCardDetailsVO().getcVV(), "OnlyNumber", 3, false))
                    {
                        error = error + "Invalid CVV, Accept only numeric[0-9] with Max Length 4";
                        errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CVV);
                        PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    commCardDetailsVO.setcVV(directKitValidatorVO.getCardDetailsVO().getcVV());*/
                    directKitValidatorVO.setCardDetailsVO(tokenDetailsVO.getCommCardDetailsVO());
                    merchantDetailsVO.setAccountId(transaction.getAccountID(tokenDetailsVO.getTrackingId()));
                    directKitValidatorVO = transactionManager.getDetailFromTransCommon(directKitValidatorVO, tokenDetailsVO);
                    directKitValidatorVO.setTrackingid(tokenDetailsVO.getTrackingId());

                    manualRecurringManager.checkAmountlimitForRebill(directKitValidatorVO.getTransDetailsVO().getAmount(), toid, directKitValidatorVO.getTerminalVO().getAccountId());
                    recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(tokenDetailsVO.getTrackingId());
                    if (!"INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL);
                        error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                        directKitValidatorVO.setErrorMsg(error);
                        if (directKitValidatorVO.getErrorCodeListVO() != null)
                            directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return directKitValidatorVO;
                    }
                    recurringBillingVO.setRecurringType(recurringType);
                    orderDes = tokenDetailsVO.getTrackingId() + "_" + currentSystemDate;
                }
                else if(functions.isValueNull(tokenDetailsVO.getTokenAccountId()))
                {
                    //REPEATED transaction with bank account
                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);

                    tokenDetailsVO = tokenManager.getAccountDetails(tokenDetailsVO,toid,directKitValidatorVO.getToken());
                    directKitValidatorVO.setCardDetailsVO(tokenDetailsVO.getCommCardDetailsVO());
                    merchantDetailsVO.setAccountId(transaction.getAccountID(tokenDetailsVO.getTrackingId()));
                    directKitValidatorVO = transactionManager.getDetailFromTransCommon(directKitValidatorVO, tokenDetailsVO);

                    recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(tokenDetailsVO.getTrackingId());
                    if (!"INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                    {
                        log.debug("Inside if with initial-----");
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL);
                        error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                        directKitValidatorVO.setErrorMsg(error);
                        if (directKitValidatorVO.getErrorCodeListVO() != null)
                            directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return directKitValidatorVO;
                    }

                    manualRecurringManager.checkAmountlimitForRebill(directKitValidatorVO.getTransDetailsVO().getAmount(), toid, directKitValidatorVO.getTerminalVO().getAccountId());
                    recurringBillingVO.setRecurringType(recurringType);
                    orderDes = tokenDetailsVO.getTrackingId() + "_" + currentSystemDate;
                    directKitValidatorVO.setTrackingid(tokenDetailsVO.getTrackingId()); //setting initial trackingId in commonValidatoVo
                }
            }
            //REPEATED recurring with trackingId
            else if (functions.isValueNull(directKitValidatorVO.getTrackingid()))
            {
                recurringBillingVO = recurringDAO.getRecurringSubscriptionDetailsForRepeatedRecurring(directKitValidatorVO.getTrackingid());
                if (!"INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }

                directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                if ("N".equalsIgnoreCase(directKitValidatorVO.getTerminalVO().getIsManualRecurring()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }
                manualRecurringManager.checkAmountlimitForRebill(directKitValidatorVO.getTransDetailsVO().getAmount(), toid, directKitValidatorVO.getTerminalVO().getAccountId());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL);
                error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                directKitValidatorVO.setErrorMsg(error);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            genericTransDetailsVO.setOrderId(orderDes);
            genericTransDetailsVO.setOrderDesc(orderDes);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            error =  validateRestKitRecurringTokenParameters(directKitValidatorVO, "REST");
            if (functions.isEmptyOrNull(error))
            {
                directKitValidatorVO.setErrorMsg(error);
                return directKitValidatorVO;
            }
        }
        else if (functions.isValueNull(directKitValidatorVO.getTrackingid()))
        {
            {
                if (("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("PA".equalsIgnoreCase(directKitValidatorVO.getTransactionType())) || ("N".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("DB".equalsIgnoreCase(directKitValidatorVO.getTransactionType())))
                {
                    error = "Error- The functionality is not allowed for account. Please contact support for help.";
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED_TRANSACTION_TYPE);
                    if(directKitValidatorVO.getErrorCodeListVO()!=null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }

                recurringBillingVO = recurringDAO.getRecurringSubscriptionDetailsForRepeatedRecurring(directKitValidatorVO.getTrackingid());
                TerminalManager terminalManager = new TerminalManager();
                ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
                if(!functions.isValueNull(recurringBillingVO.getTerminalid()))
                {
                    error = "No Record found.";
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_NO_RECORD_FOUND);
                    if(directKitValidatorVO.getErrorCodeListVO()!=null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }

                if (functions.isValueNull(directKitValidatorVO.getTerminalId()) && !recurringBillingVO.getTerminalid().equals(directKitValidatorVO.getTerminalId()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                    {
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    return directKitValidatorVO;
                }
                if (!"INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                    {
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    return directKitValidatorVO;
                }

                TerminalVO terminalVO = null;
                //TerminalVO terminalVO = terminalManager.getTerminalFromPaymodeCardtypeMemberidCurrency(toid, recurringBillingVO.getPaymentType(), recurringBillingVO.getCardType(), genericTransDetailsVO.getCurrency());
                if (directKitValidatorVO.getTerminalId() != null)
                {
                    terminalVO = terminalManager.getTerminalByTerminalId(directKitValidatorVO.getTerminalId());
                }
                else if (recurringBillingVO.getTerminalid() != null)
                {
                    terminalVO = terminalManager.getTerminalByTerminalId(recurringBillingVO.getTerminalid());
                }

                if(terminalVO == null)
                {
                    error = error + "Account ID-Payment Mode, Card Type or Currency requested by you is not valid for your account. Please check your Technical specification.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CURRENCY_BRAND_MODE);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                if ("N".equalsIgnoreCase(terminalVO.getIsManualRecurring()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }

                directKitValidatorVO.setTerminalVO(terminalVO);
                manualRecurringManager.checkAmountlimitForRebill(directKitValidatorVO.getTransDetailsVO().getAmount(), toid, terminalVO.getAccountId());
            }
            orderDes = directKitValidatorVO.getTrackingid() + "_" + currentSystemDate;
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            genericTransDetailsVO.setOrderId(orderDes);
            genericTransDetailsVO.setOrderDesc(orderDes);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        error =  validateRestKitRecurringTokenParameters(directKitValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }
        return directKitValidatorVO;
    }

    public CommonValidatorVO performRestDeleteTokenValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = null;

        //Checking login details of merchant
        directKitValidatorVO = performVerificationForCancelInquiryAndDeleteToken(directKitValidatorVO);
        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            return directKitValidatorVO;

        error =  validateDeleteTokenParams(directKitValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }

        return directKitValidatorVO;
    }

    public CommonValidatorVO performRestTokenRegistrationValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        String error = "";

        error =  validateRestKitStandAloneTokenParameters(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO performRestTransactionDetail(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        error =  performGettransactionDetailsValidation(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestMerchantSignUpValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        error =  validateRestKitMerchantSignUpParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        partnerDetailsVO=partnerManager.getPartnerDetails(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
        if (!Checksum.verifyMD5ChecksumForMerchantSignup(commonValidatorVO.getMerchantDetailsVO().getLogin(), partnerDetailsVO.getPartnerKey(), commonValidatorVO.getMerchantDetailsVO().getNewPassword(), commonValidatorVO.getMerchantDetailsVO().getPartnerId(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
        }
        if (functions.isValueNull(error))
        {
            errorCodeListVO.addListOfError(errorCodeVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            return commonValidatorVO;
        }
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;
    }
    public CommonValidatorVO performGenerateAppOTP(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        error =  validateGenerateAppOTP(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performVerifyAppOTP(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        error =  validateVerifyAppOTP(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestMerchantLoginValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        error = validateRestKitMerchantLoginParameters(commonValidatorVO, "REST");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        Merchants merchants=new Merchants();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        String password = merchantDetailsVO.getPassword();
        String etoken = merchantDetailsVO.getEtoken();
        String login=merchantDetailsVO.getLogin();
        String role="merchant";

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(merchantDetailsVO.getLogin()))
        {
            String loginUser= merchants.getMemberLoginfromUser(merchantDetailsVO.getLogin());
            if(!functions.isValueNull(loginUser))
                loginUser=merchantDetailsVO.getLogin();
            else
                role="submerchant";
            merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(loginUser);
            if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LOGIN);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setLogin(login);
            merchantDetailsVO.setRole(role);
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getParetnerId());

        merchantDetailsVO.setPassword(password);
        merchantDetailsVO.setEtoken(etoken);

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO performRestMerchantAuthTokenValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        error = validateRestKitMerchantAuthTokenParameters(commonValidatorVO, "REST");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = null;
        String password = merchantDetailsVO.getPassword();
        String sKey = merchantDetailsVO.getKey();
        String loginName = merchantDetailsVO.getLogin();

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(merchantDetailsVO.getLogin()))
        {
            merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(merchantDetailsVO.getLogin());
            if (!loginName.equalsIgnoreCase(merchantDetailsVO.getLogin()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LOGIN);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }
        if (functions.isValueNull(sKey))
        {
            boolean isKey = merchantDAO.authenticateMemberViaKey(loginName, sKey,commonValidatorVO.getParetnerId());
            if (!isKey)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getParetnerId());
        if (partnerDetailsVO == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        merchantDetailsVO.setPassword(password);

        merchantDetailsVO.setKey(sKey);
        merchantDetailsVO.setLogin(loginName);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;

    }


    public CommonValidatorVO performverifymail(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        error = validateRestKitVerifyMailParameters(commonValidatorVO, "REST");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        String password = merchantDetailsVO.getPassword();
        String etoken = merchantDetailsVO.getEtoken();
        String partnerid = merchantDetailsVO.getPartnerId();
        String fromtype = merchantDetailsVO.getPartnerName();

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(merchantDetailsVO.getLogin()))
        {
            merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(merchantDetailsVO.getLogin());
        }

        //  partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getParetnerId());

        merchantDetailsVO.setPassword(password);
        merchantDetailsVO.setEtoken(etoken);


        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);


        //   commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;
    }


    public CommonValidatorVO performGetNewAuthTokenValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        String userName = "";
        String userName1 = "";
        String role = "";
        ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
        String token                    = commonValidatorVO.getAuthToken();
        String partnerId                = commonValidatorVO.getParetnerId();
        AuthFunctions authFunctions         = new AuthFunctions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO             = new MerchantDAO();
        Merchants merchants                 = new Merchants();
        error                               = validateGetNewAuthTokenParameters(commonValidatorVO, "REST");

        if (functions.isValueNull(error))
            commonValidatorVO.setErrorMsg(error);
        else
        {
            userName    = authFunctions.getUserName(token);
            userName1   = userName;
            if (!functions.isValueNull(userName))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;

            }

            role = authFunctions.getUserRole(token);
            if (!functions.isValueNull(role))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            if("submerchant".equalsIgnoreCase(role))
            {
                userName= merchants.getMemberLoginfromUser(userName);
            }
            merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(userName);
            if (!partnerId.equals(merchantDetailsVO.getPartnerId()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_MEMBERID_PARTNERID);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setLogin(userName1);
            merchantDetailsVO.setRole(role);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO performMerchantCurrenciesValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        error = validateRestKitMerchantCurrencyParameters(commonValidatorVO, "REST");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        Merchants merchants=new Merchants();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            merchantDetailsVO = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());
        }

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        merchantDetailsVO.setRole("merchant");
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getLogin()) && !merchantDetailsVO.getLogin().equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLogin()))
        {
            String userId=merchants.isMemberUser(commonValidatorVO.getMerchantDetailsVO().getLogin(),merchantDetailsVO.getMemberId());
            if(!functions.isValueNull(userId))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            merchantDetailsVO.setLogin(commonValidatorVO.getMerchantDetailsVO().getLogin());
            merchantDetailsVO.setRole("submerchant");
        }
        if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO performMerchantAddressValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {

        String error = "";
        MerchantDetailsVO merchantDetailsVO =commonValidatorVO.getMerchantDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        error = validateUpdateAddressParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
        {
            merchantDetailsVO = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());
        }
        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);

        }

        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;
    }



    public CommonValidatorVO performMerchantChangePasswordValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        MerchantDAO merchantDAO = new MerchantDAO();
        Merchants merchants=new Merchants();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        String error = "";
        String role = "merchant";
        error = validateRestKitMerchantChangePasswordParameters(commonValidatorVO,"REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        String loginUser= merchants.getMemberLoginfromUser(merchantDetailsVO.getLogin());
        if(!functions.isValueNull(loginUser))
            loginUser=merchantDetailsVO.getLogin();
        else
            role="submerchant";
        merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(loginUser);
        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LOGIN);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        //merchantDetailsVO = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());

        merchantDetailsVO.setLogin(commonValidatorVO.getMerchantDetailsVO().getLogin());
        merchantDetailsVO.setRole(role);
        merchantDetailsVO.setPassword(commonValidatorVO.getMerchantDetailsVO().getPassword());
        merchantDetailsVO.setNewPassword(commonValidatorVO.getMerchantDetailsVO().getNewPassword());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;

    }

    public CommonValidatorVO performMerchantForgetPasswordValidation(CommonValidatorVO commonValidatorVO)
    {
        String error = "";
        error = validateRestKitMerchantForgetPasswordParameters(commonValidatorVO,"REST");
        if (functions.isValueNull(error))
            commonValidatorVO.setErrorMsg(error);

        return commonValidatorVO;
    }

    public CommonValidatorVO performRestCustomerRegistrationValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        error =  validateRestKitCustomerRegistrationParameters(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performInitialRecurringTokenValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        GenericTransDetailsVO genericTransDetailsVO =directKitValidatorVO.getTransDetailsVO();
        RecurringBillingVO recurringBillingVO = directKitValidatorVO.getRecurringBillingVO();

        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        RecurringDAO recurringDAO = new RecurringDAO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        String recurringType = recurringBillingVO.getRecurringType();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        //checking login details
        directKitValidatorVO = performVerificationWithTrackingidAndToken(directKitValidatorVO);
        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            return directKitValidatorVO;
        }
        if (("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("PA".equalsIgnoreCase(directKitValidatorVO.getTransactionType())) || ("N".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("DB".equalsIgnoreCase(directKitValidatorVO.getTransactionType())))
        {
            error = "Error- The functionality is not allowed for account. Please contact support for help.";
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED_TRANSACTION_TYPE);
            if(directKitValidatorVO.getErrorCodeListVO()!=null)
                directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return directKitValidatorVO;
        }

        error =  validateRestKitWithTokenParameters(directKitValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }
        MerchantDetailsVO merchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();
        String toid = merchantDetailsVO.getMemberId();

        if(directKitValidatorVO.getToken() != null)
        {
            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(directKitValidatorVO.getMerchantDetailsVO().getMemberId(), directKitValidatorVO.getToken(), directKitValidatorVO, tokenDetailsVO);
            else
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(merchantDetailsVO.getPartnerId(), directKitValidatorVO.getToken(), directKitValidatorVO); //token generated by PartnerId

            if (!functions.isValueNull(tokenDetailsVO.getToken()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }

            if (functions.isValueNull(directKitValidatorVO.getTerminalId()) && functions.isValueNull(tokenDetailsVO.getTerminalId()) && !tokenDetailsVO.getTerminalId().equals(directKitValidatorVO.getTerminalId()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }
            if(!functions.isValueNull(directKitValidatorVO.getPaymentType()))
                directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
            if(!functions.isValueNull(directKitValidatorVO.getCardType()))
                directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
            if(!functions.isValueNull(tokenDetailsVO.getCurrency()))
                tokenDetailsVO.setCurrency(directKitValidatorVO.getTransDetailsVO().getCurrency());

            if ("N".equalsIgnoreCase(tokenDetailsVO.getIsActive()) && "N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                {
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }
                return directKitValidatorVO;
            }

            String orderDes = "";
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());
            CommonInputValidator commonInputValidator = new CommonInputValidator();

            //Transaction with Card
            if(!functions.isValueNull(tokenDetailsVO.getTokenAccountId()))
            {
                /*if(!ESAPI.validator().isValidInput("cvv", directKitValidatorVO.getCardDetailsVO().getcVV(), "OnlyNumber", 3, false))
                {
                    error = error + "Invalid CVV, Accept only numeric[0-9] with Max Length 4";
                    errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CVV);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/

                directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    return directKitValidatorVO;

                if ("N".equalsIgnoreCase(directKitValidatorVO.getTerminalVO().getIsManualRecurring()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }
                error = error + commonInputValidator.validateRestFlagBasedAddressField(directKitValidatorVO, "REST");
                if(!functions.isEmptyOrNull(error))
                {
                    directKitValidatorVO.setErrorMsg(error);
                    return directKitValidatorVO;
                }

                String accountid = directKitValidatorVO.getTerminalVO().getAccountId();
                directKitValidatorVO.getTransDetailsVO().setOrderDesc("Test");////Hardcode for p4
                directKitValidatorVO.getTransDetailsVO().setOrderId((int)(Math.random()*100000)+"JHG54");////Hardcode for p4

                String accountAddressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
                error = error + paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "REST", accountAddressValidation);

                if(!functions.isEmptyOrNull(error))
                {
                    directKitValidatorVO.setErrorMsg(error);
                    return directKitValidatorVO;
                }

                //transaction details setting
                merchantDetailsVO.setAccountId(directKitValidatorVO.getTerminalVO().getAccountId());
                genericTransDetailsVO.setFromid(directKitValidatorVO.getTerminalVO().getMemberId());
                genericTransDetailsVO.setFromtype(directKitValidatorVO.getTerminalVO().getGateway());
                recurringBillingVO.setTerminalid(directKitValidatorVO.getTerminalVO().getTerminalId());

                //getting trackingId if exist
                tokenDetailsVO = tokenManager.getRegistrationTrackingId(merchantDetailsVO.getMemberId(), tokenDetailsVO);

                recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(tokenDetailsVO.getTrackingId());
                if (functions.isValueNull(recurringBillingVO.getRecurringType()) && "INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL_INVALID);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }
                else
                {
                    recurringBillingVO.setRecurringType(recurringType);
                }

                //card details are setting
                CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                //commCardDetailsVO.setcVV(directKitValidatorVO.getCardDetailsVO().getcVV());
                directKitValidatorVO.setCardDetailsVO(commCardDetailsVO);
                directKitValidatorVO.setTerminalVO(directKitValidatorVO.getTerminalVO());
                directKitValidatorVO.setTerminalId(directKitValidatorVO.getTerminalId());
                directKitValidatorVO.setPaymentType(directKitValidatorVO.getTerminalVO().getPaymodeId());
                directKitValidatorVO.setCardType(directKitValidatorVO.getTerminalVO().getCardTypeId());
                directKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
                orderDes = currentSystemDate; //unique orderId

            }
            //Transaction with accountId
            else
            {
                directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    return directKitValidatorVO;

                //transaction details setting
                merchantDetailsVO.setAccountId(directKitValidatorVO.getTerminalVO().getAccountId());
                genericTransDetailsVO.setFromid(directKitValidatorVO.getTerminalVO().getMemberId());
                genericTransDetailsVO.setFromtype(directKitValidatorVO.getTerminalVO().getGateway());
                recurringBillingVO.setTerminalid(directKitValidatorVO.getTerminalVO().getTerminalId());

                //getting trackingId if exist
                tokenDetailsVO = tokenManager.getRegistrationTrackingId(merchantDetailsVO.getMemberId(), tokenDetailsVO);

                recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(tokenDetailsVO.getTrackingId());
                if (functions.isValueNull(recurringBillingVO.getRecurringType()) && "INITIAL".equalsIgnoreCase(recurringBillingVO.getRecurringType()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRING_INITIAL_INVALID);
                    error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
                    directKitValidatorVO.setErrorMsg(error);
                    if (directKitValidatorVO.getErrorCodeListVO() != null)
                        directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return directKitValidatorVO;
                }
                else
                {
                    recurringBillingVO.setRecurringType(recurringType);
                }

                tokenDetailsVO = tokenManager.getAccountDetails(tokenDetailsVO,toid,directKitValidatorVO.getToken());
                directKitValidatorVO.setCardDetailsVO(tokenDetailsVO.getCommCardDetailsVO());

                directKitValidatorVO.setTerminalId(directKitValidatorVO.getTerminalVO().getTerminalId());
                directKitValidatorVO.setTerminalVO(directKitValidatorVO.getTerminalVO());
                directKitValidatorVO.setPaymentType(directKitValidatorVO.getTerminalVO().getPaymodeId());
                directKitValidatorVO.setCardType(directKitValidatorVO.getTerminalVO().getCardTypeId());
                orderDes = currentSystemDate; //unique orderId
            }
            tokenDetailsVO.getAddressDetailsVO().setIp(directKitValidatorVO.getAddressDetailsVO().getIp());
            directKitValidatorVO.setAddressDetailsVO(tokenDetailsVO.getAddressDetailsVO());
            genericTransDetailsVO.setOrderId(orderDes);
            genericTransDetailsVO.setOrderDesc(orderDes);
            directKitValidatorVO.setTerminalId(tokenDetailsVO.getTerminalId());
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            directKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        }
        error =  validateRestKitRecurringTokenParameters(directKitValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }
        return directKitValidatorVO;
    }

    public CommonValidatorVO performRestTokenValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        GenericTransDetailsVO genericTransDetailsVO =directKitValidatorVO.getTransDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        TransactionHelper transactionHelper = new TransactionHelper();

        //checking login details
        directKitValidatorVO = performVerificationWithTrackingidAndToken(directKitValidatorVO);
        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            return directKitValidatorVO;
        }
        error =  validateRestKitWithTokenParameters(directKitValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }

        MerchantDetailsVO merchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();

        if (("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("PA".equalsIgnoreCase(directKitValidatorVO.getTransactionType())) || ("N".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getIsService())) && ("DB".equalsIgnoreCase(directKitValidatorVO.getTransactionType())))
        {
            error = "Error- The functionality is not allowed for account. Please contact support for help.";
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED_TRANSACTION_TYPE);
            if(directKitValidatorVO.getErrorCodeListVO()!=null)
                directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return directKitValidatorVO;
        }

        if(directKitValidatorVO.getToken() != null)
        {
            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                tokenDetailsVO = tokenManager.getInitialTokenDetailsWithMemebrId(directKitValidatorVO.getToken(), merchantDetailsVO.getMemberId()); //token generated by merchant
            else
                tokenDetailsVO = tokenManager.getInitialTokenDetailsWithPartnerId(directKitValidatorVO.getToken(), merchantDetailsVO.getPartnerId()); //token generated by PartnerId

            if(tokenDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }

            //getting trackingId if exist
            tokenDetailsVO = tokenManager.getRegistrationTrackingId(merchantDetailsVO.getMemberId(), tokenDetailsVO);

            /*if(functions.isValueNull(directKitValidatorVO.getTerminalId()) && !directKitValidatorVO.getTerminalId().equals(tokenDetailsVO.getTerminalId()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                {
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }
                return directKitValidatorVO;
            }*/
            if ("N".equalsIgnoreCase(tokenDetailsVO.getIsActive()) && "N".equals(tokenDetailsVO.getIsActiveReg()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_DELETE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                {
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }
                return directKitValidatorVO;
            }

            String orderDes = "";
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyMMddHHmmss");
            String currentSystemDate = dateFormater.format(new Date());
            String currency = directKitValidatorVO.getTransDetailsVO().getCurrency();

            //For BankAccount Transaction(Token)
            if(functions.isValueNull(tokenDetailsVO.getTokenAccountId()))
            {
                log.debug("Bank Transaction::");
                if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                {
                    tokenDetailsVO = tokenManager.getBankAccountTokenDetailsWithMemberId(tokenDetailsVO.getTokenAccountId(), directKitValidatorVO.getToken(), directKitValidatorVO.getMerchantDetailsVO().getMemberId(), directKitValidatorVO, tokenDetailsVO);
                }
                else
                {
                    tokenDetailsVO = tokenManager.getBankAccountTokenDetailsWithPartnerId(tokenDetailsVO.getTokenAccountId(), directKitValidatorVO.getToken(), directKitValidatorVO.getMerchantDetailsVO().getPartnerId(), directKitValidatorVO, tokenDetailsVO);
                }

                CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                directKitValidatorVO.setCardDetailsVO(commCardDetailsVO);
                //Token transaction generated with payment
                if (functions.isValueNull(tokenDetailsVO.getTrackingId()))
                {
                    log.debug("TrackingId is not null::");
                    //fetching all details on basis of initial trackingId
                    TransactionManager transactionManager = new TransactionManager();
                    CommonInputValidator commonInputValidator = new CommonInputValidator();
                    directKitValidatorVO.setReserveField2VO(tokenDetailsVO.getReserveField2VO());

                    directKitValidatorVO = transactionManager.getDetailFromTransCommon(directKitValidatorVO, tokenDetailsVO); // getting all the transaction details from transaction_common

                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO); // validation with terminalVo details
                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        return directKitValidatorVO;

                    error = error + commonInputValidator.validateRestFlagBasedAddressField(directKitValidatorVO, "REST");
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }
                    String accountid = directKitValidatorVO.getTerminalVO().getAccountId();
                    log.debug("accountid in stand alone token---"+accountid);
                    directKitValidatorVO.getTransDetailsVO().setOrderDesc("Test");////Hardcode for p4
                    directKitValidatorVO.getTransDetailsVO().setOrderId((int)(Math.random()*100000)+"JHG54");////Hardcode for p4

                    String accountAddressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
                    error = error + paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "REST", accountAddressValidation);
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }

                    merchantDetailsVO.setAccountId(accountid);
                    orderDes = tokenDetailsVO.getTrackingId() + "_" + currentSystemDate;
                }
                else
                {
                    //Token transaction generated with StandAlone - Bank Account
                    log.debug("TrackingId is null::::");
                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        return directKitValidatorVO;

                    //transaction details setting
                    merchantDetailsVO.setAccountId(directKitValidatorVO.getTerminalVO().getAccountId());
                    genericTransDetailsVO.setFromid(directKitValidatorVO.getTerminalVO().getMemberId());
                    genericTransDetailsVO.setFromtype(directKitValidatorVO.getTerminalVO().getGateway());

                    directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
                    directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
                    directKitValidatorVO.setTerminalId(directKitValidatorVO.getTerminalVO().getTerminalId());
                    directKitValidatorVO.setTerminalVO(directKitValidatorVO.getTerminalVO());
                    orderDes = currentSystemDate; //unique orderId
                }
            }
            else
            {
                //For Card transaction(Token+Cvv)
                log.debug("Card Transaction::");
                ErrorCodeListVO errorCodeListVO = null;
                if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration())&& "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(directKitValidatorVO.getMerchantDetailsVO().getMemberId(), directKitValidatorVO.getToken(), directKitValidatorVO, tokenDetailsVO);
                else
                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(directKitValidatorVO.getMerchantDetailsVO().getPartnerId(), directKitValidatorVO.getToken(), directKitValidatorVO);

                //tokenDetailsVO.setPaymentType(directKitValidatorVO.getPaymentType());
                //tokenDetailsVO.setCardType(directKitValidatorVO.getCardType());
                tokenDetailsVO.setTerminalId(directKitValidatorVO.getTerminalId());
                tokenDetailsVO.setCurrency(directKitValidatorVO.getTransDetailsVO().getCurrency());
                /*if(functions.isValueNull(tokenDetailsVO.getNotificationUrl()))
                    directKitValidatorVO.getTransDetailsVO().setNotificationUrl(tokenDetailsVO.getNotificationUrl());*/
                if(functions.isValueNull(tokenDetailsVO.getCustomerId()))
                    directKitValidatorVO.setCustomerId(tokenDetailsVO.getCustomerId());
                if(functions.isValueNull(tokenDetailsVO.getPaymentType()))
                    directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
                if(functions.isValueNull(tokenDetailsVO.getCardType()))
                    directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
                if("N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
                {
                    error = error + "Inactive Token.";
                    errorCodeListVO = getErrorVO(ErrorName.INACTIVE_TOKEN);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (functions.isValueNull(tokenDetailsVO.getTrackingId()))
                {
                    log.debug("TrackingId is not null::");
                    //fetching all details on basis of initial trackingId
                    Transaction transaction = new Transaction();
                    TransactionManager transactionManager = new TransactionManager();
                    CommonInputValidator commonInputValidator = new CommonInputValidator();

                    //getting all transaction details from transaction_common
                    directKitValidatorVO = transactionManager.getDetailFromTransCommon(directKitValidatorVO, tokenDetailsVO); // getting details of transaction based on first transaction with token
                    /*if(!currency.equals(directKitValidatorVO.getTransDetailsVO().getCurrency()))//&& !"ALL".equalsIgnoreCase(directKitValidatorVO.getTransDetailsVO().getCurrency())
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                        if (directKitValidatorVO.getErrorCodeListVO() != null)
                            directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        return directKitValidatorVO;
                    }*/
                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO); //validation
                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        return directKitValidatorVO;

                    error = error + commonInputValidator.validateRestFlagBasedAddressField(directKitValidatorVO, "REST"); //address validation
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }
                    String accountid = directKitValidatorVO.getTerminalVO().getAccountId();
                    String accountAddressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();

                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
                    error = error + paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "REST", accountAddressValidation);

                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }
                    CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                    if (!ESAPI.validator().isValidInput("cvv", commCardDetailsVO.getcVV(), "OnlyNumber", 3, false))
                    {
                        error = error + "Invalid CVV, Accept only numeric[0-9] with Max Length 4";
                        errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CVV);
                        PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    //commCardDetailsVO.setcVV(directKitValidatorVO.getCardDetailsVO().getcVV());
                    directKitValidatorVO.setCardDetailsVO(commCardDetailsVO);
                    merchantDetailsVO.setAccountId(transaction.getAccountID(tokenDetailsVO.getTrackingId()));
                    merchantDetailsVO.setAccountId(accountid);
                    orderDes = tokenDetailsVO.getTrackingId() + "_" + currentSystemDate;
                }
                else
                {
                    log.debug("TrackingID is null");
                    //fetching data from paymodeid and cardtypeid
                    CommonInputValidator commonInputValidator = new CommonInputValidator();

                    directKitValidatorVO = performRestTokenChecks(directKitValidatorVO, tokenDetailsVO);
                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        return directKitValidatorVO;

                    error = error + commonInputValidator.validateRestFlagBasedAddressField(directKitValidatorVO, "REST");
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }

                    String accountid = directKitValidatorVO.getTerminalVO().getAccountId();
                    String accountAddressValidation = GatewayAccountService.getGatewayAccount(accountid).getAddressValidation();
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountid));
                    error = error + paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "REST", accountAddressValidation);
                    if(!functions.isEmptyOrNull(error))
                    {
                        directKitValidatorVO.setErrorMsg(error);
                        return directKitValidatorVO;
                    }
                    CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                    if (!functions.isValueNull(commCardDetailsVO.getcVV()) || !functions.isNumericVal(commCardDetailsVO.getcVV()))
                    {
                        error = error + "Invalid CVV, Accept only numeric[0-9] with Max Length 4";
                        errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CVV);
                        PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    //transaction details setting
                    merchantDetailsVO.setAccountId(accountid);
                    genericTransDetailsVO.setFromid(directKitValidatorVO.getTerminalVO().getMemberId());

                    //card details are setting
                    //CommCardDetailsVO commCardDetailsVO = tokenDetailsVO.getCommCardDetailsVO();
                    //commCardDetailsVO.setcVV(directKitValidatorVO.getCardDetailsVO().getcVV());
                    directKitValidatorVO.setCardDetailsVO(commCardDetailsVO);

                    directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
                    directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
                    directKitValidatorVO.setTerminalId(directKitValidatorVO.getTerminalVO().getTerminalId());
                    directKitValidatorVO.setTerminalVO(directKitValidatorVO.getTerminalVO());
                    orderDes = currentSystemDate; //unique orderId
                }
            }
            genericTransDetailsVO.setOrderId(orderDes);
            genericTransDetailsVO.setOrderDesc(orderDes);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            directKitValidatorVO.setAddressDetailsVO(tokenDetailsVO.getAddressDetailsVO());

            directKitValidatorVO.setCardType(directKitValidatorVO.getTerminalVO().getCardTypeId());
            directKitValidatorVO.setPaymentType(directKitValidatorVO.getTerminalVO().getPaymodeId());

            directKitValidatorVO = transactionHelper.performCommonSystemChecksStep2(directKitValidatorVO);
        }
        error =  validateRestKitRecurringTokenParameters(directKitValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            return directKitValidatorVO;
        }
        return directKitValidatorVO;
    }

    public CommonValidatorVO performGetCardsAndAccountValidator(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";
        error = validateCardsAndAccountsParams(commonValidatorVO, "REST");
        if(functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        return commonValidatorVO;
    }
    public  CommonValidatorVO performGetTransactionValidation (CommonValidatorVO commonValidatorVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String memberId =commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoicelistMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);
        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInvoicelistOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }


        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;

    }


    public  String performGettransactionDetailsValidation (CommonValidatorVO commonValidatorVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException

    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();


        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoicelistMandatoryParams());

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);

        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInvoicelistOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);


        return error;

    }

    public CommonValidatorVO performPayoutTransactionValidation(CommonValidatorVO commonValidatorVO, String actionName) throws PZDBViolationException
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils     = new InputValiDatorUtils();
        InputValidator inputValidator               = new InputValidator();
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        GenericTransDetailsVO transDetailsVO        = commonValidatorVO.getTransDetailsVO();
        String expDateOffset                        = merchantDetailsVO.getExpDateOffset();
        ErrorCodeUtils errorCodeUtils               = new ErrorCodeUtils();
        String trackingId= "";
        String personalInfoValidation                = "N";

        merchantDetailsVO           = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());

        if(functions.isValueNull(merchantDetailsVO.getPayoutPersonalInfoValidation())){
            personalInfoValidation      = merchantDetailsVO.getPayoutPersonalInfoValidation();
        }
        List<InputFields> inputFields           = new ArrayList<InputFields>();

        if(functions.isValueNull(commonValidatorVO.getTrackingid())){
            trackingId = commonValidatorVO.getTrackingid();
        }
        inputFields.addAll(inputValiDatorUtils.getPayoutTransactionMandatoryParams(commonValidatorVO.getReserveField2VO().getTransferType(),trackingId,personalInfoValidation));
        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFields, validationErrorList, false);
        error = error + inputValiDatorUtils.getError(validationErrorList, inputFields, actionName);

        //Validate invoice optional parameters
        List<InputFields> inputPayoutOptionalList   = new ArrayList<InputFields>();
        inputPayoutOptionalList.addAll(inputValiDatorUtils.getPayoutOptionalParams());
        ValidationErrorList optionalErrorList       = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputPayoutOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputPayoutOptionalList,actionName);

        if (!error.isEmpty())
        {
            commonValidatorVO.setErrorMsg(error);
            FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
            String errorName    = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            error               = errorCodeUtils.getSystemErrorCodeVO(commonValidatorVO.getErrorCodeListVO());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.VALIDATION.toString());
            return  commonValidatorVO;
        }

       // merchantDetailsVO = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());

        if (functions.isValueNull(expDateOffset))
            merchantDetailsVO.setExpDateOffset(expDateOffset);

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO performExchangerValidation(CommonValidatorVO commonValidatorVO, String actionName) throws PZDBViolationException
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        GenericTransDetailsVO transDetailsVO = commonValidatorVO.getTransDetailsVO();
        PartnerManager partnerManager = new PartnerManager();

        List<InputFields> inputFields = new ArrayList<InputFields>();

        inputFields.add(InputFields.CUSTOMER_ID);
        inputFields.add(InputFields.AMOUNT);
        inputFields.add(InputFields.CURRENCY);
        inputFields.add(InputFields.PAYMENTMODE);
        inputFields.add(InputFields.PAYMENTBRAND);
        inputFields.add(InputFields.TOID);
        inputFields.add(InputFields.CHECKSUM);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFields, validationErrorList, false);
        error = error + inputValiDatorUtils.getError(validationErrorList, inputFields, actionName);

        if (!error.isEmpty())
        {
            commonValidatorVO.setErrorMsg(error);
            return  commonValidatorVO;
        }

        merchantDetailsVO = merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());
        commonValidatorVO = partnerManager.getPartnerDetailFromMemberId(merchantDetailsVO.getMemberId(),commonValidatorVO);

        commonValidatorVO.getAddressDetailsVO().setEmail(merchantDetailsVO.getContact_emails());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }




    private String validateCardsAndAccountsParams(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = null;
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validate optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getCardAndAccountOptionalParams());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList, true);
        error = error + inputValiDatorUtils.getError(errorList,inputOptionalFieldsList,actionName);

        //Validate optional parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getCardAndAccountMandatoryParams());
        ValidationErrorList errorMandatoryList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorMandatoryList, false);
        error = error + inputValiDatorUtils.getError(errorMandatoryList,inputMandatoryFieldsList,actionName);

        return error;
    }

    private String validateDeleteTokenParams(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestAPIOptionalParams());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList, true);
        error = error + inputValiDatorUtils.getError(errorList,inputOptionalFieldsList,actionName);

        return error;
    }

    private String validateRestKitStandAloneTokenParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterListForRest(commonValidatorVO.getTerminalId()));


        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getRestTokenOptionalParameters());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, true);
        error = error + inputValiDatorUtils.getError(errorList2, inputOptionalFieldsList1, actionName);

        validateMandatoryDetailsBasedOnPaymentMode(commonValidatorVO, actionName);

        return error;
    }

    public String validateMandatoryDetailsBasedOnPaymentMode(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        transactionLogger.debug("Payment Mode---"+commonValidatorVO.getPaymentMode());
        transactionLogger.debug("Hosted PAge---"+commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage());
        transactionLogger.debug("Payment Brand---"+commonValidatorVO.getPaymentBrand());

        if("CC".equals(commonValidatorVO.getPaymentMode()) && (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage()) && commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage().equalsIgnoreCase("N")) && (!("UNICREDIT".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()) || "CLEARSETTLE".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()) || "AVISA".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()) || "AMC".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()))))
        {
            List<InputFields> cardMandatoryInputFields = new ArrayList<InputFields>();
            cardMandatoryInputFields.addAll(inputValiDatorUtils.getRestTokenWithCardMandatoryParameters());
            ValidationErrorList cardMandatoryInputList = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, cardMandatoryInputFields, cardMandatoryInputList, false);
            error = error + inputValiDatorUtils.getError(cardMandatoryInputList, cardMandatoryInputFields, actionName);
        }
        else if ("SEPA".equals(commonValidatorVO.getPaymentMode()))
        {
            //Validate Optional parameters for Account
            List<InputFields> inputMandatoryFieldsListForAccount = new ArrayList<InputFields>();
            inputMandatoryFieldsListForAccount.addAll(inputValiDatorUtils.getRestAccountDetailsValidation());
            ValidationErrorList errorListForAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForAccount, errorListForAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsListForAccount, actionName);
        }else if ("BC".equals(commonValidatorVO.getPaymentMode()))
        {
            List<InputFields> inputMandatoryFieldsListForAccount = new ArrayList<InputFields>();
            inputMandatoryFieldsListForAccount.addAll(inputValiDatorUtils.getRestBitcoinValidation());
            ValidationErrorList errorListForAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForAccount, errorListForAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsListForAccount, actionName);
        }
        else if ("ACH".equals(commonValidatorVO.getPaymentMode()) || "CHK".equals(commonValidatorVO.getPaymentMode()))
        {
            //Validate Optional parameters for Account
            List<InputFields> inputMandatoryFieldsListForPaymitcoAccount = new ArrayList<InputFields>();
            inputMandatoryFieldsListForPaymitcoAccount.addAll(inputValiDatorUtils.getRestPaymitcoDetailsValidation());
            ValidationErrorList errorListForPaymitcoAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForPaymitcoAccount, errorListForPaymitcoAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForPaymitcoAccount, inputMandatoryFieldsListForPaymitcoAccount, actionName);

            List<InputFields> inputOptionalFieldsList=new ArrayList<>();
            inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestOptionalPaymitcoAccountDetailsValidation());
            ValidationErrorList errorList=new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO,inputOptionalFieldsList,errorList,true);
            error=error+inputValiDatorUtils.getError(errorList,inputOptionalFieldsList,actionName);
        }else if ("BT".equals(commonValidatorVO.getPaymentMode()) && ("ECOCASH".equalsIgnoreCase(commonValidatorVO.getCardType()) || "TELECASH".equalsIgnoreCase(commonValidatorVO.getCardType())))
        {
            //Validate Optional parameters for Account
            List<InputFields> inputMandatoryFieldsListForCellulantAccount = new ArrayList<InputFields>();
            inputMandatoryFieldsListForCellulantAccount.addAll(inputValiDatorUtils.getRestCellulantDetailsValidation());
            ValidationErrorList errorListForCellulantAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForCellulantAccount, errorListForCellulantAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForCellulantAccount, inputMandatoryFieldsListForCellulantAccount, actionName);
        }
        else if (("NBI".equals(commonValidatorVO.getPaymentMode()) && ("SEPBANKS".equalsIgnoreCase(commonValidatorVO.getCardType())|| "LZPBANKS".equalsIgnoreCase(commonValidatorVO.getCardType()))) || (("SEPWALLETS".equalsIgnoreCase(commonValidatorVO.getCardType()) || "LZPWALLETS".equalsIgnoreCase(commonValidatorVO.getCardType())) && "EWI".equals(commonValidatorVO.getPaymentMode()))|| ("NBI".equals(commonValidatorVO.getPaymentMode()) && "BDBANKS".equalsIgnoreCase(commonValidatorVO.getCardType())) || ("EWI".equals(commonValidatorVO.getPaymentMode()) && "BDWALLETS".equalsIgnoreCase(commonValidatorVO.getCardType())))
        {
            //For Safexpay and Billdesk Integration
            //Validate Optional parameters for Account
            List<InputFields> inputMandatoryFieldsListForOtherBanks = new ArrayList<InputFields>();
            inputMandatoryFieldsListForOtherBanks.addAll(inputValiDatorUtils.getRestOthersBankMandatoryParametersValidation());
            ValidationErrorList errorListForPaymitcoAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForOtherBanks, errorListForPaymitcoAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForPaymitcoAccount, inputMandatoryFieldsListForOtherBanks, actionName);
        }
       /* else if("UPI".equalsIgnoreCase(commonValidatorVO.getPaymentMode()))
        {
            List<InputFields> inputMandatoryFieldsListForOtherBanks = new ArrayList<InputFields>();
            inputMandatoryFieldsListForOtherBanks.addAll(inputValiDatorUtils.getRestUPIMandatoryParametersValidation());
            ValidationErrorList errorListForPaymitcoAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForOtherBanks, errorListForPaymitcoAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForPaymitcoAccount, inputMandatoryFieldsListForOtherBanks, actionName);
        } */
        return error;
    }
    private String validateRestKitMerchantSignUpParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getRestMerchantSignupParameters());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        //validate address field validation
        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getOptionalValidationForMerchantSignup());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        return error;
    }
    private String validateGenerateAppOTP(CommonValidatorVO commonValidatorVO,String actionName ) throws PZConstraintViolationException
    {

        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();


        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getGenerateAppOTP());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        return error;

    }

    private String validateVerifyAppOTP(CommonValidatorVO commonValidatorVO,String actionName ) throws PZConstraintViolationException
    {

        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getVerifyAppOTP());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        return error;

    }

    private String validateRestKitMerchantLoginParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestMerchantLoginParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;

    }

    private String validateRestKitMerchantAuthTokenParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestMerchantAuthTokenParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestMerchantAuthTokenOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);
        return error;

    }

    private String validateRestKitPartnerAuthTokenParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestPartnerAuthTokenParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;

    }


    private String validateRestKitVerifyMailParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestVerifyMailParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;

    }

    private String validateGetNewAuthTokenParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils             = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator    = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList          = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getNewAuthTokenParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;
    }

    private String validateRestKitMerchantCurrencyParameters(CommonValidatorVO commonValidatorVO , String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputFieldsMandatoryList = new ArrayList<InputFields>();
        inputFieldsMandatoryList.addAll(inputValiDatorUtils.getRestMerchantCurrencyParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputFieldsMandatoryList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputFieldsMandatoryList,actionName);

        List<InputFields> inputFieldsOptionalList = new ArrayList<InputFields>();
        inputFieldsOptionalList.addAll(inputValiDatorUtils.getRestMerchantCurrencyParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputFieldsOptionalList,errorList1,true);
        error = error + inputValiDatorUtils.getError(errorList1,inputFieldsOptionalList,actionName);
        return error;
    }

    private String validateRestKitMerchantChangePasswordParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputFieldsMandatoryList = new ArrayList<InputFields>();
        inputFieldsMandatoryList.addAll(inputValiDatorUtils.getRestMerchantChangePasswordParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputFieldsMandatoryList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputFieldsMandatoryList,actionName);
        return error;
    }

    private String validateRestKitMerchantForgetPasswordParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.LOGIN);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;
    }

    private String validateRestKitCustomerRegistrationParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getRestCustomerRegistrationMandatoryParameters());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        //validate address field validation
        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidationForCardholderRegistration());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        return error;
    }

    private String validateUpdateAddressParameters(CommonValidatorVO commonValidatorVO,String actionName) throws PZConstraintViolationException
    {

        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getUpdateAddressMandatoryParameters());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);
        return error;
    }


    public String validateRestKitRecurringTokenParameters(CommonValidatorVO directKitValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestRecurringAndTokenMandatoryParameters());

        //Validation for Rest Conditional parameters
        List<InputFields> inputFieldsListConditional = new ArrayList<InputFields>();
        inputFieldsListConditional.addAll(inputValiDatorUtils.getRestTokenConditionalParametersForBrands());
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(directKitValidatorVO, inputFieldsListConditional, errorList2, true);
        error = error+ inputValiDatorUtils.getError(errorList2,inputFieldsListConditional,actionName);

        if ("".equalsIgnoreCase(directKitValidatorVO.getRecurringBillingVO().getRecurringType()) && (functions.isValueNull(directKitValidatorVO.getCardDetailsVO().getcVV())))
        {
            //Validation for Rest Conditional parameters
            List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
            inputFieldsListOptional.addAll(inputValiDatorUtils.getRestTokenOptionalParametersForBrands());
            ValidationErrorList errorList3 = new ValidationErrorList();
            inputValidator.RestInputValidations(directKitValidatorVO, inputFieldsListOptional, errorList3, true);
            error = error + inputValiDatorUtils.getError(errorList3, inputFieldsListOptional, actionName);
        }

        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(directKitValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        //validate mandatory parameters while transaction with bank account details
        if(directKitValidatorVO.getBankAccountVO() != null)
        {
            List<InputFields> inputMandatoryFieldsListForAccount = new ArrayList<InputFields>();
            inputMandatoryFieldsListForAccount.addAll(inputValiDatorUtils.getRestAccountDetailsValidation());
            ValidationErrorList errorListForAccount = new ValidationErrorList();
            inputValidator.RestInputValidations(directKitValidatorVO, inputMandatoryFieldsListForAccount, errorListForAccount, false);
            error = error + inputValiDatorUtils.getError(errorListForAccount,inputMandatoryFieldsListForAccount,actionName);
        }
        return error;
    }
    private String validateRestKitRefundParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestRefundCaptureMandatoryParameters());

        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;
    }
    private String validateRestKitCancelParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestCancelMandatoryParameters());
        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;
    }

    private String validateRestKitInquiryParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestInquiryMandatoryParameters(commonValidatorVO));
        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;
    }

    private String validateRestKitCaptureParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestRefundCaptureMandatoryParameters());

        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;
    }

    private String validateRestKitParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        transactionLogger.debug("-----inside validateRestKitParameters-----");
        transactionLogger.debug("-----inside validateRestKitParameters-----"+commonValidatorVO.getMerchantDetailsVO());
        transactionLogger.debug("-----inside validateRestKitParameters-----"+commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage());

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();

        //Validation for Rest Mandatory parameters

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        //inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestTransactionMandatoryParameters());
        // Payment type and card type is already getting validated in getConditionalParameterListForRest(); hence getRestTransactionMandatoryParameters() commented
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getConditionalParameterListForRest(merchantDetailsVO.getAutoSelectTerminal(), merchantDetailsVO.getIpWhiteListed(), commonValidatorVO.getTerminalId()));
        if (commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage()!=null)
        {
            if(commonValidatorVO.getMerchantDetailsVO().getHostedPaymentPage().equals("N")){
                ValidationErrorList errorList = new ValidationErrorList();
                inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
            }
            //validate all parameters
        }
        else {
            //validate all parameters
            ValidationErrorList errorList = new ValidationErrorList();
            inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
        }
        //validate address field validation
        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        //Validation for Rest Optional parameters
        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestTransactionOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

        return error;
    }

    private String validateRestKitParametersStep2(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        error = error+validateMandatoryDetailsBasedOnPaymentMode(commonValidatorVO, actionName);
        transactionLogger.debug("error4-----"+error);

        if (commonValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport().equals("Y"))
        {
            List<InputFields> inputMultiCurrencyFieldList = new ArrayList<InputFields>();
            inputMultiCurrencyFieldList.addAll(inputValiDatorUtils.getDKConditionalParam());
            ValidationErrorList errorList3 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, inputMultiCurrencyFieldList, errorList3, false);
            error = error + inputValiDatorUtils.getError(errorList3,inputMultiCurrencyFieldList,actionName);
        }

        transactionLogger.debug("error5-----"+error);
        return error;
    }

    private String validateRestKitWithTokenParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        log.debug("Mandatory parameters validation::");
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestRecurringAndTokenMandatoryParameters());
        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        //validate address field validation
        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFlagBasedAddressValidation, addressValidationError, true);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        //Validation for Rest Mandatory parameters
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.addAll(inputValiDatorUtils.getRestTransactionRecurringOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFieldsListOptional, errorList1, true);
        error = error+ inputValiDatorUtils.getError(errorList1,inputFieldsListOptional,actionName);
        return error;
    }

    public MerchantDetailsVO getMerchantConfigDetailsByLogin(String memberId) throws PZDBViolationException, PZConstraintViolationException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO=new MerchantDAO();

        //merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        return merchantDetailsVO;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    public CommonValidatorVO performRestTokenChecks(CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Inside performRestTokenChecksNew::");
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TerminalVO terminalVO = null;
        LimitChecker limitChecker = new LimitChecker();
        PaymentChecker paymentChecker = new PaymentChecker();
        TransactionHelper transactionHelper = new TransactionHelper();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions = new Functions();
        TerminalManager terminalManager = new TerminalManager();
        TokenManager tokenManager=new TokenManager();
        Transaction transaction = new Transaction();
        String error = "";
        if(functions.isValueNull(tokenDetailsVO.getTerminalId()))
        {
            terminalVO = terminalManager.getMemberTerminalDetails(tokenDetailsVO.getTerminalId(), commonValidatorVO.getMerchantDetailsVO().getMemberId());
            if(terminalVO!=null)
            {
                commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
                commonValidatorVO.setCardType(terminalVO.getCardTypeId());
            }
        }
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
        {
            String firstSix = tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(0, 6);
            String cNum = tokenDetailsVO.getCommCardDetailsVO().getCardNum();
            if("Card".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        //Giving first priority to specific currency based terminal
                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), cNum);
                        //If specific currency based terminal not found
                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL", cNum);
                            if (terminalVO != null)
                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                        }
                        //TODO set if terminal vo null
                        //terminal picked up with non whitelisted bin based on specific currency
                        if (terminalVO == null)
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                            if (terminalVO != null)
                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                        }
                    }
                    else
                    {
                        log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), cNum);
                        if (terminalVO == null)
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                    }
                }
                else
                {
                    terminalVO = terminalManager.getAccountIdTerminalVOforBinRoutingByCardNumber(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()), cNum);
                    if (terminalVO == null)
                        terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()));
                }
            }
            else
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        //Giving first priority to specific currency based terminal
                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), firstSix);

                        //If specific currency based terminal not found
                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL", firstSix);
                            if (terminalVO != null)
                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                        }
                        //TODO set if terminal vo null
                        //terminal picked up with non whitelisted bin based on specific currency
                        if (terminalVO == null)
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                            if (terminalVO != null)
                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                        }
                    }
                    else
                    {
                        log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), firstSix);
                        if (terminalVO == null)
                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                    }
                }
                else
                {
                    terminalVO = terminalManager.getAccountIdTerminalVOforBinRouting(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()), firstSix);
                    if (terminalVO == null)
                        terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()));
                }
            }
        }
        else if(!functions.isValueNull(tokenDetailsVO.getTerminalId()) && "Y".equals(merchantDetailsVO.getAutoSelectTerminal()))
        {
            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), tokenDetailsVO.getPaymentType(), tokenDetailsVO.getCardType(), tokenDetailsVO.getCurrency());
        }

        if(terminalVO ==  null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        if("N".equalsIgnoreCase(terminalVO.getIsActive()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        commonValidatorVO.setTerminalVO(terminalVO);
        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
        genericTransDetailsVO.setCurrency(terminalVO.getCurrency());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        /*if("N".equals(terminalVO.getIsTokenizationActive()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_ALLOWED);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }*/

        //Transaction limit checking
        limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

        if(genericTransDetailsVO.getCurrency().equals("JPY") && !paymentChecker.isAmountValidForJPY(genericTransDetailsVO.getCurrency(),genericTransDetailsVO.getAmount()))
        {
            error = error + "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
            errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
        }
        //Unique order chacking
        String uniqueorder = null;
        String fromtype = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId()).getGateway();
        uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), fromtype, genericTransDetailsVO.getOrderId());
        if (!uniqueorder.equals(""))
        {
            error = error + (commonValidatorVO.getTransDetailsVO().getOrderId()+"-Duplicate Order Id " + uniqueorder);
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            PZExceptionHandler.raiseConstraintViolationException("RestTransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
        if(merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            if(!transactionHelper.isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = error + "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                PZExceptionHandler.raiseConstraintViolationException("RestTransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if(!transactionHelper.isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getIp(),commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            {
                error = error + "Your IpAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                PZExceptionHandler.raiseConstraintViolationException("RestTransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(!transactionHelper.isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error = error + "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(!transactionHelper.isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(),merchantDetailsVO.getMemberId(),terminalVO.getAccountId()))
            {
                error = error + "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(!transactionHelper.isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                error = error + "Your Name is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        //For Installment
        transactionLogger.error("EMI COUNT---->"+commonValidatorVO.getTransDetailsVO().getEmiCount());
        transactionLogger.error("terminalVO.getIsEmi_support()---->"+terminalVO.getIsEmi_support());
        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getEmiCount()))
        {
            if("N".equalsIgnoreCase(terminalVO.getIsEmi_support()) || "N".equalsIgnoreCase(merchantDetailsVO.getEmiSupport()))
            {
                error = error + "Installment is not Supported. Kindly Contact the admin Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_EMI_SUPPORT);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            EmiVO emiVO=tokenManager.getEmiCountWithTerminalId(commonValidatorVO);
            if(emiVO!=null)
            {
                String emiPeriod=emiVO.getEmiPeriod();
                String emiCount=commonValidatorVO.getTransDetailsVO().getEmiCount();
                boolean isAvailable=true;
                boolean checkInstallment=false;
                if(emiPeriod.contains(","))
                {
                    String[] emi=emiPeriod.split(",");
                    for (int i=0;i<emi.length;i++)
                    {
                        if (emiCount.equalsIgnoreCase(emi[i]))
                        {
                            isAvailable=false;
                            break;
                        }
                    }
                }
                else {
                    if (emiCount.equalsIgnoreCase(emiPeriod))
                    {
                        isAvailable=false;
                    }
                }
                try
                {
                    checkInstallment=Functions.checkInstallment(emiVO.getStartDate(),emiVO.getEndDate());
                }
                catch (ParseException e)
                {
                    log.error("ParseException---->",e);
                }
                if(isAvailable || checkInstallment)
                {
                    error = error + "Installment is not Supported. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_EMI_NOT_FOUND);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            else {
                error = error + "Installment is not Supported. Kindly Contact the admin Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_EMI_NOT_FOUND);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        return commonValidatorVO;
    }
    private String merchantActivationChecks(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        Functions functions = new Functions();
        PaymentChecker paymentChecker = new PaymentChecker();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        //IP Whitelist check
        if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getAddressvalidation()))
        {
            if ("Y".equals((commonValidatorVO.getMerchantDetailsVO().getIsIpWhiteListedCheckForAPIs())))
            {
                transactionLogger.debug("ip address--------" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
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

        return error;
    }

    public CommonValidatorVO performRestPartnerLoginValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getKey())){

            if (!ESAPI.validator().isValidInput("Key", commonValidatorVO.getMerchantDetailsVO().getKey(), "alphanum", 100, false))
            {
                error = error + "-1" + "_" + "invalid secret key, secret key should not be empty";
            }
        }
        else {
            error = validateRestKitMerchantLoginParameters(commonValidatorVO, "REST");
        }

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        String password = merchantDetailsVO.getPassword();
        String key = merchantDetailsVO.getKey();
        String etoken = merchantDetailsVO.getEtoken();

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getParetnerId());

        merchantDetailsVO.setPassword(password);
        merchantDetailsVO.setEtoken(etoken);
        merchantDetailsVO.setKey(key);

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO performFraudGetNewAuthTokenValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        String userName = "";
        String role = "";
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String token = commonValidatorVO.getAuthToken();
        String partnerId = commonValidatorVO.getParetnerId();
        AuthFunctions authFunctions = new AuthFunctions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        error = validateGetNewAuthTokenParameters(commonValidatorVO, "REST");

        if (functions.isValueNull(error))
            commonValidatorVO.setErrorMsg(error);
        else
        {
            userName = authFunctions.getUserName(token);
            if (!functions.isValueNull(userName))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;

            }

            role = authFunctions.getUserRole(token);
            if (!functions.isValueNull(role))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }

            //  merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(userName);
           /* if (!partnerId.equals(merchantDetailsVO.getPartnerId()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_MEMBERID_PARTNERID);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }*/
            merchantDetailsVO.setLogin(userName);
            merchantDetailsVO.setRole(role);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO performRestPartnerAuthTokenValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        error = validateRestKitPartnerAuthTokenParameters(commonValidatorVO, "REST");
        //MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO             = new MerchantDAO();
        PartnerDAO partnerDAO               = new PartnerDAO();
        ErrorCodeVO errorCodeVO             = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        PartnerManager partnerManager       = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO   = commonValidatorVO.getPartnerDetailsVO();
        //String password = merchantDetailsVO.getPassword();
        String sKey                         = partnerDetailsVO.getPartnerKey(); //Partner Secret Key
        String loginName                    = commonValidatorVO.getPartnerName(); //Partner Login

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(loginName))
        {
            partnerDetailsVO = partnerDAO.getPartnerDetailsByLogin(loginName);
            if (!loginName.equalsIgnoreCase(partnerDetailsVO.getPartnerName()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LOGIN);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }
        if (functions.isValueNull(sKey))
        {
            boolean isKey = partnerDAO.authenticatePartnerViaKey(loginName, sKey,commonValidatorVO.getParetnerId());
            if (!isKey)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;

    }


    public CommonValidatorVO performWalletDetailsValidation(CommonValidatorVO commonValidatorVO, String actionName) throws PZDBViolationException
    {
        transactionLogger.error(" IN performWalletDetailsValidation -----------");

        String error = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        String trackingId = commonValidatorVO.getTrackingid();


        transactionLogger.error("amount-----" + amount);
        transactionLogger.error("currency-----" + currency);
        transactionLogger.error("trackingId-----" + trackingId);

        if(!functions.isValueNull(amount) || !ESAPI.validator().isValidInput("amount",amount,"tenDigitAmount",12,false))
        {
            transactionLogger.error("in if null amount-----");
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_AMOUNT.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }

        if(!functions.isValueNull(currency) || !ESAPI.validator().isValidInput("currency",currency,"SafeString",3,false))
        {
            transactionLogger.error("in if null currency-----");
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_CURRENCY.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }

        if(!functions.isValueNull(trackingId) || !ESAPI.validator().isValidInput("trackingId",trackingId,"OnlyNumber",7,false))
        {
            transactionLogger.error("in if null trackingId-----");
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TRACKINGID);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TRACKINGID.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }


        return commonValidatorVO;
    }

    private String validateMarketPlaceRefundParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<MarketPlaceVO> marketPlaceVOList=commonValidatorVO.getMarketPlaceVOList();
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestRefundCaptureMandatoryParameters());

        //validate all parameters
        ValidationErrorList errorList = new ValidationErrorList();
        if(marketPlaceVOList != null)
        {
            for(int i=0;i<marketPlaceVOList.size();i++)
            {
                marketPlaceVO = marketPlaceVOList.get(i);
                inputValidator.RestInputValidations(commonValidatorVO, marketPlaceVO, inputMandatoryFieldsList, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
                if(functions.isValueNull(error))
                {
                    return error;
                }
            }
        }

        return error;
    }
    private String validateMarketPlaceStandardProcess(CommonValidatorVO commonValidatorVO,String actionName) throws PZDBViolationException, PZConstraintViolationException
    {
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();

        String error="";
        float totalamount=0;

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry=new FailedTransactionLogEntry();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;

        List<MarketPlaceVO> marketPlaceVOList=commonValidatorVO.getMarketPlaceVOList();
        if(marketPlaceVOList == null || marketPlaceVOList.size()==0)
        {
            error = "Market Place Parameters are Incomplete";
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PARAMETERS_MP);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_PARAMETERS_MP);
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
        }
        else
        {
            for (int i = 0; i < marketPlaceVOList.size(); i++)
            {
                marketPlaceVO = marketPlaceVOList.get(i);
                //prepare list for general Mandatory parameters
                List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
                inputMandatoryFieldsList.addAll(inputValiDatorUtils.getMarketPlaceMandatoryParamsRest());
                inputValidator.InputValidations(commonValidatorVO,marketPlaceVO, inputMandatoryFieldsList, errorList, false);
                error = error+ inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

                //prepare list for general Optional parameters
                List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
                inputOptionalFieldsList.addAll(inputValiDatorUtils.getMarketPlaceOptionalParamsRest());
                ValidationErrorList errorList1 = new ValidationErrorList();
                inputValidator.InputValidations(commonValidatorVO,marketPlaceVO, inputOptionalFieldsList, errorList1, true);
                error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);

                if(functions.isValueNull(error))
                {
                    return error;
                }
                merchantDetailsVO=merchantDAO.getMemberAndPartnerDetails(marketPlaceVO.getMemberid(), commonValidatorVO.getMerchantDetailsVO().getPartnerName());
                if(merchantDetailsVO == null)
                {
                    error = "Invalid MemberId OR MemberId is Misconfigured.";
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID_INVALID);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID_MP);
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    return error;
                }
                if (!merchantDetailsVO.getActivation().equals("Y"))
                {
                    error = "The "+marketPlaceVO.getMemberid()+" Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any incomplete formality from the Merchant Side. Please contact support so that they can activate your account.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_MEMBER_ACTIVATION_CHECK_MP.toString(),ErrorType.VALIDATION.toString());
                    //failedTransactionLogEntry.suspendedMerchantTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getMerchantDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.MERCHANT_ACCOUNT_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("CommonInputValidator.class", "performStandardProcessStep1Validations()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }
                totalamount=totalamount+Float.parseFloat(marketPlaceVO.getAmount());
            }
            if(!String.format("%.2f",totalamount).equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getAmount()))
            {
                error = "Amount Mismatch.";
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOTAL_AMOUNT_MP);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTAL_AMOUNT_MP);
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }
        }
        return error;
    }


    //Validations for QR Verify
    public CommonValidatorVO performQRTransactionValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        log.error("-- Mandatory parameters validation QR Verify --");
        String error = "";
        String actionName ="REST";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        if ("PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
        {
            inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestDynamicQRParameters());
        }
        else if("DB".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
        {
            inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestStaticQRParameters());
        }


        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        //Validation for other QR Mandatory parameters
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.addAll(inputValiDatorUtils.getRestMandatoryQRParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFieldsListOptional, errorList1, false);
        error = error+ inputValiDatorUtils.getError(errorList1,inputFieldsListOptional,actionName);

        if(functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        return commonValidatorVO;

    }

    // QR Checkout Validation
    public CommonValidatorVO performQRCheckoutValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        log.error("--- Mandatory parameters validation QR Checkout ---");
        String error = "";
        String actionName ="REST";

        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();

        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        //Validation for other QR Mandatory parameters
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.addAll(inputValiDatorUtils.getRestMandatoryQRCheckoutParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputFieldsListOptional, errorList1, false);
        error = error+ inputValiDatorUtils.getError(errorList1,inputFieldsListOptional,actionName);

        if(functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        return commonValidatorVO;
    }

    // QR Inquiry Status
    public CommonValidatorVO performQRInquiryValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        //Checking login details of merchant
        String error = "";

        directKitValidatorVO = performVerificationForQRInquiryStatus(directKitValidatorVO);

        error = validateRestKitInquiryParameters(directKitValidatorVO, "REST");

        if (functions.isValueNull(error))
            directKitValidatorVO.setErrorMsg(error);

        return directKitValidatorVO;
    }

    private CommonValidatorVO performVerificationForQRInquiryStatus(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commonValidatorVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        PartnerManager partnerManager = new PartnerManager();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        if(functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            transactionLogger.debug("inside performVerificationForCancelInquiryAndDeleteToken::");
            String toid = merchantDetailsVO.getMemberId();
            //Toid Validation
            if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            transactionLogger.debug("PartnerId-->"+merchantDetailsVO.getPartnerId());
            //partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            //commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

            transactionLogger.debug("Merchant Activation---->"+merchantDetailsVO.getActivation());
            //Activation check
            if (!merchantDetailsVO.getActivation().equals("Y"))
            {
                error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
            }
            //IP Whitelist check
            transactionLogger.debug("commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()--->"+commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs());
            if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("MerchantId--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
        {
            String partnerId = partnerDetailsVO.getPartnerId();
            //PartnerId Validation
            if(!functions.isValueNull(partnerId) || !ESAPI.validator().isValidInput("partnerid",partnerId,"Numbers",10,false))
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_PARTNERID));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(partnerId); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Invalid request";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
            //IP Whitelist check
            if("Y".equalsIgnoreCase(partnerDetailsVO.getIsIpWhiteListedCheckForAPIs()))
            {
                transactionLogger.debug("partnerId--------"+partnerId+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
                if (!paymentChecker.isIpWhitelistedForTransactionByPartner(partnerId, commonValidatorVO.getAddressDetailsVO().getIp()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        /*if (functions.isValueNull(commonValidatorVO.getTrackingid()) || functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
        {
            //Inquiry and Cancel checksum verification

            String checksumId=commonValidatorVO.getTrackingid();
            if(functions.isValueNull(commonValidatorVO.getIdType())){
                if(commonValidatorVO.getIdType().equalsIgnoreCase("MID"))
                {
                    checksumId = commonValidatorVO.getTransDetailsVO().getOrderId();
                }
            }
            log.debug("Checksum with trackingId------>"+commonValidatorVO.getTransDetailsVO().getChecksum());
            if (!Checksum.verifyChecksumV3(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(),checksumId,commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        else if(functions.isValueNull(commonValidatorVO.getToken()))
        {
            //Delete Token Checksum verification
            log.debug("Checksum with token------>"+commonValidatorVO.getTransDetailsVO().getChecksum());
            if(functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                if (!Checksum.verifyChecksumV3(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getToken(), commonValidatorVO.getTransDetailsVO().getChecksum()))
                {
                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            else
            {
                if (!Checksum.verifyChecksumV3(partnerDetailsVO.getPartnerId(), partnerDetailsVO.getPartnerKey(), commonValidatorVO.getToken(), commonValidatorVO.getTransDetailsVO().getChecksum()))
                {
                    error = "Checksum- Illegal Access. CheckSum mismatch";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
*/
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
        tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
        addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO performRestInstallmentCountValidation(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        GenericAddressDetailsVO addressDetailsVO =directKitValidatorVO.getAddressDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        TokenManager tokenManager = new TokenManager();
        TokenDetailsVO tokenDetailsVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();


        directKitValidatorVO = performInstallmentVerificationWithToken(directKitValidatorVO);
        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            return directKitValidatorVO;
        }
        MerchantDetailsVO merchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();
        ErrorCodeListVO errorCodeListVO = null;
        if(directKitValidatorVO.getToken() != null)
        {
            tokenDetailsVO=new TokenDetailsVO();
            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration())&& "Y".equals(merchantDetailsVO.getIsTokenizationAllowed()))
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(directKitValidatorVO.getMerchantDetailsVO().getMemberId(), directKitValidatorVO.getToken(), directKitValidatorVO, tokenDetailsVO);
            else
                tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(directKitValidatorVO.getMerchantDetailsVO().getPartnerId(), directKitValidatorVO.getToken(), directKitValidatorVO);
            if (tokenDetailsVO == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REFERENCE_TOKEN);
                if (directKitValidatorVO.getErrorCodeListVO() != null)
                    directKitValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return directKitValidatorVO;
            }
            //tokenDetailsVO.setPaymentType(directKitValidatorVO.getPaymentType());
            //tokenDetailsVO.setCardType(directKitValidatorVO.getCardType());
            tokenDetailsVO.setTerminalId(directKitValidatorVO.getTerminalId());
            tokenDetailsVO.setCurrency(directKitValidatorVO.getTransDetailsVO().getCurrency());
            if(functions.isValueNull(tokenDetailsVO.getNotificationUrl()))
                directKitValidatorVO.getTransDetailsVO().setNotificationUrl(tokenDetailsVO.getNotificationUrl());
            if(functions.isValueNull(tokenDetailsVO.getCustomerId()))
                directKitValidatorVO.setCustomerId(tokenDetailsVO.getCustomerId());
            if(functions.isValueNull(tokenDetailsVO.getPaymentType()))
                directKitValidatorVO.setPaymentType(tokenDetailsVO.getPaymentType());
            if(functions.isValueNull(tokenDetailsVO.getCardType()))
                directKitValidatorVO.setCardType(tokenDetailsVO.getCardType());
            //addressDetailsVO=tokenDetailsVO.getAddressDetailsVO();
            //transactionLogger.error("addressDetailsVO-------------->"+addressDetailsVO);
            //directKitValidatorVO.setAddressDetailsVO(addressDetailsVO);
            directKitValidatorVO.setCardDetailsVO(tokenDetailsVO.getCommCardDetailsVO());
            if("N".equalsIgnoreCase(tokenDetailsVO.getIsActiveReg()))
            {
                error = error + "Inactive Token.";
                errorCodeListVO = getErrorVO(ErrorName.INACTIVE_TOKEN);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            directKitValidatorVO=performRestInstallmentChecks(directKitValidatorVO,tokenDetailsVO);


        }
        return directKitValidatorVO;
    }
    public CommonValidatorVO performRestInstallmentChecks(CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Inside performRestTokenChecksNew::");
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TerminalVO terminalVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Functions functions = new Functions();
        TerminalManager terminalManager = new TerminalManager();
        Transaction transaction = new Transaction();
        if(functions.isValueNull(tokenDetailsVO.getTerminalId()))
        {
            terminalVO = terminalManager.getMemberTerminalDetails(tokenDetailsVO.getTerminalId(), commonValidatorVO.getMerchantDetailsVO().getMemberId());
            commonValidatorVO.setPaymentType(terminalVO.getPaymodeId());
            commonValidatorVO.setCardType(terminalVO.getCardTypeId());
        }
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
        {
            String firstSix = tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(0, 6);
            String cNum = tokenDetailsVO.getCommCardDetailsVO().getCardNum();
            if("Card".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
            {
                terminalVO = terminalManager.getAccountIdTerminalVOforBinRoutingByCardNumber(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()), cNum);
                if (terminalVO == null)
                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()));
            }
            else
            {
                terminalVO = terminalManager.getAccountIdTerminalVOforBinRouting(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()), firstSix);
                if (terminalVO == null)
                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), Integer.parseInt(tokenDetailsVO.getPaymentType()), Integer.parseInt(tokenDetailsVO.getCardType()));
            }
        }
        else if(!functions.isValueNull(tokenDetailsVO.getTerminalId()) && "Y".equals(merchantDetailsVO.getAutoSelectTerminal()))
        {
            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), tokenDetailsVO.getPaymentType(), tokenDetailsVO.getCardType(), tokenDetailsVO.getCurrency());
        }

        if(terminalVO ==  null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_TERMINAL);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        if("N".equalsIgnoreCase(terminalVO.getIsActive()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        if("N".equalsIgnoreCase(terminalVO.getIsEmi_support()) || "N".equalsIgnoreCase(merchantDetailsVO.getEmiSupport()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_EMI_SUPPORT);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        commonValidatorVO.setTerminalVO(terminalVO);
        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
        genericTransDetailsVO.setCurrency(terminalVO.getCurrency());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        return commonValidatorVO;
    }
    private CommonValidatorVO performInstallmentVerificationWithToken(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        String toid = merchantDetailsVO.getMemberId();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;

        //Toid Validation
        if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

            return commonValidatorVO;
        }
        merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
        if(merchantDetailsVO==null || merchantDetailsVO.getMemberId()==null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
            error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        if(functions.isValueNull(commonValidatorVO.getToken()))
        {
            if("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_ALLOWED);
                error = errorCodeVO.getErrorCode()+" " +errorCodeVO.getErrorDescription();
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            if (!Checksum.verifyMD5ChecksumForMerchantChangePassword(toid, merchantDetailsVO.getKey(), commonValidatorVO.getToken(), genericTransDetailsVO.getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
        addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());

        //getting partner details
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        //IP Whitelist check
        if("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsIpWhiteListedCheckForAPIs()) && "Y".equalsIgnoreCase(merchantDetailsVO.getIsIpWhiteListedCheckForAPIs()))
        {
            transactionLogger.debug("ip address--------"+merchantDetailsVO.getMemberId()+"---"+commonValidatorVO.getAddressDetailsVO().getIp());
            if (!paymentChecker.isIpWhitelistedForTransaction(merchantDetailsVO.getMemberId(), commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the admin Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        //Activation check
        if (!merchantDetailsVO.getActivation().equals("Y"))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestInitiateAuthenticationValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        transactionLogger.debug("inside performRestInitiateAuthenticationValidation-----");

        String error = "";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TokenRequestVO tokenRequestVO = new TokenRequestVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        String toid = merchantDetailsVO.getMemberId();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();


        //Toid Validation
        if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getApiCode()+" "+errorCodeVO.getApiDescription();
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        else
        {
            transactionLogger.error("Inside else loop-------"+toid);

            merchantDetailsVO = getMerchantConfigDetailsByLogin(toid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

            Gson gson=new Gson();
            String merchantDetails=gson.toJson(commonValidatorVO.getMerchantDetailsVO());

            transactionLogger.error("MerchantDetailsVO-----"+merchantDetails);

            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getApiCode() + " " + errorCodeVO.getApiDescription();
                commonValidatorVO.setErrorMsg(error);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TOID_INVALID.toString(), ErrorType.VALIDATION.toString());
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
            if (functions.isValueNull(commonValidatorVO.getTrackingid()))
            {
                //Activation check
                if (!merchantDetailsVO.getActivation().equals("Y"))
                {
                    error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                //getting partner details
                PartnerManager partnerManager = new PartnerManager();
                PartnerDetailsVO partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
                commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
            }

            genericTransDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            tokenRequestVO.setGeneratedBy(merchantDetailsVO.getLogin());
            addressDetailsVO.setEmail(merchantDetailsVO.getContact_emails());
        }

        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }
    public CommonValidatorVO performSendSmsCodeValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        transactionLogger.debug("Inside performSendSmsCodeValidation ------");
        String error="";
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        error=validateSmsCodeRequestParameters(commonValidatorVO,"REST");
        if(!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=null;
        transactionLogger.debug("getting details on the basis of tracking id in transaction detail vo");
        transactionDetailsVO=transactionManager.getTransDetailFromCommon(commonValidatorVO.getTrackingid());
        transactionLogger.debug("status from transactionDetailsVO ----------"+transactionDetailsVO.getStatus());
        PaymentDAO paymentDAO=new PaymentDAO();
        /*String cvvEncrypted=paymentDAO.getCvv(commonValidatorVO);
        String cvvDecrypted=PzEncryptor.decryptCVV(cvvEncrypted);*/
        if(!functions.isValueNull(transactionDetailsVO.getTrackingid()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRANSACTION_NOT_FOUND));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        if (functions.isValueNull(transactionDetailsVO.getCcnum()))
            cardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));


        String expiry="";
        if (functions.isValueNull(transactionDetailsVO.getExpdate()))
        {
            expiry=PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

            cardDetailsVO.setExpMonth(expiry.split("\\/")[0]);
            cardDetailsVO.setExpYear(expiry.split("\\/")[1]);
        }
        if(!functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CURRENCY));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(!functions.isValueNull(transactionDetailsVO.getCcnum()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_INVALID_CARD));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(!functions.isValueNull(cardDetailsVO.getExpMonth()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_EXP_MONTH));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(!functions.isValueNull(cardDetailsVO.getExpYear()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_EXP_YEAR));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        /*if(!functions.isValueNull(cvvDecrypted))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CVV));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }*/
        if(!functions.isValueNull(transactionDetailsVO.getTelno()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TELNO));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        commonValidatorVO.setTransactionDetailsVO(transactionDetailsVO);
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        if (functions.isValueNull(transactionDetailsVO.getToid()))
        {
            try
            {
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(transactionDetailsVO.getToid());
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.debug("PZDBViolationException ---"+e);
            }
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }

        //cardDetailsVO.setcVV(cvvDecrypted);
        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
        if (merchantDetailsVO.getIsService().equalsIgnoreCase("N"))
            commonValidatorVO.setTransactionType("PA");
        else
            commonValidatorVO.setTransactionType("DB");

        return commonValidatorVO;
    }
    public CommonValidatorVO getTransactionDetails(TransactionDetailsVO transactionDetailsVO,CommonValidatorVO commonValidatorVO)
    {
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO=commonValidatorVO.getCardDetailsVO();
        // transDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        // transDetailsVO.setAmount(transactionDetailsVO.getAmount());

        addressDetailsVO.setTmpl_amount(transactionDetailsVO.getTemplateamount());
        addressDetailsVO.setTmpl_currency(transactionDetailsVO.getTemplatecurrency());
        PaymentDAO paymentDAO=new PaymentDAO();

        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        commonValidatorVO.getTrackingid();

        return commonValidatorVO;
    }
    private String validateSmsCodeRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        log.debug("Mandatory parameters validation::");
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestSmsCodeMandatoryParameters());
        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        return error;
    }
    public CommonValidatorVO performRestGetPaymentAndCardTypeValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        log.debug("Inside performRestTokenChecksNew::");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        String error="";
        error=validatePaymentAndCardTypeRequestParameters(commonValidatorVO,"REST");
        if(functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        merchantDetailsVO = getMerchantConfigDetailsByLogin(merchantDetailsVO.getMemberId());
        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            error = "Invalid request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }
    private String validatePaymentAndCardTypeRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        log.debug("Mandatory parameters validation::");
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestPaymentAndCardTypeMandatoryParameters());
        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        return error;
    }
    public CommonValidatorVO performRestSaveTransactionReceiptValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=null;
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        String error="";
        error=validateSaveTransactionReceiptRequestParameters(commonValidatorVO, "REST");
        if(functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(!functions.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionReceipt()))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRANSACTION_RECEIPT_IMG));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        String detailId=transactionManager.getTransactionDetailIdFromTrackingId(commonValidatorVO.getTrackingid());
        if(!functions.isValueNull(detailId))
        {
            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRANSACTION_NOT_FOUND));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performSendSmsCodeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.getTransDetailsVO().setDetailId(detailId);

        return commonValidatorVO;
    }
    private String validateSaveTransactionReceiptRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        log.debug("Mandatory parameters validation::");
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestSaveTransactionReceiptMandatoryParameters());
        ValidationErrorList errorListForAccount = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorListForAccount, false);
        error = error + inputValiDatorUtils.getError(errorListForAccount, inputMandatoryFieldsList, actionName);

        return error;
    }
    public CommonValidatorVO performRestGetTransactionListValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        String error    = "";
        error           = validateGetTransactionListRequestParameters(commonValidatorVO, "REST");
        if(functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        merchantDetailsVO = getMerchantConfigDetailsByLogin(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            error = "Invalid request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);

        }
        if(functions.isValueNull(commonValidatorVO.getPaginationVO().getStartdate()) && functions.isValueNull(commonValidatorVO.getPaginationVO().getEnddate()))
        {
            boolean isValidDate = functions.isFutureDateComparisonWithFromAndToDate(commonValidatorVO.getPaginationVO().getStartdate(), commonValidatorVO.getPaginationVO().getEnddate(), "dd/MM/yyyy");
            if (isValidDate)
            {
                //throw error
                error = "From date should be greater then To date.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TODATE_GREATER_FROMDATE));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }
        if (!functions.isEmptyOrNull(commonValidatorVO.getTimeZone())){
            System.out.println("TimeZone::->"+commonValidatorVO.getTimeZone());
            boolean isValidTimeZone = functions.isValidTimeZone(commonValidatorVO.getTimeZone());
            System.out.println("isvalid::->"+isValidTimeZone);
            if (!isValidTimeZone){
                error = "Please provide valid TimeZone.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TIMEZONE));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }

        return commonValidatorVO;
    }
    private String validateGetTransactionListRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {

        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();


        log.debug("Mandatory parameters validation::");
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestGetTransactionListMandatoryParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);


        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestGetTransactionListOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1, inputOptionalFieldsList, actionName);
        return error;

    }
    public CommonValidatorVO performMerchantLogoutValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        String error        = "";
        String userName     = "";
        String userName1    = "";
        String role         = "";
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        String token                            = commonValidatorVO.getAuthToken();
        String partnerId                        = commonValidatorVO.getParetnerId();
        AuthFunctions authFunctions             = new AuthFunctions();
        MerchantDetailsVO merchantDetailsVO     = new MerchantDetailsVO();
        MerchantDAO merchantDAO                 = new MerchantDAO();
        Merchants merchants                     = new Merchants();

        error = validateRestMerchantLogoutParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        userName    = authFunctions.getUserName(token);
        userName1   = userName;
        if (!functions.isValueNull(userName))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;

        }

        role = authFunctions.getUserRole(token);
        if (!functions.isValueNull(role))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_GENERATION_FAILED);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        if("submerchant".equalsIgnoreCase(role))
        {
            userName= merchants.getMemberLoginfromUser(userName);
        }
        merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(userName);
        if (!partnerId.equals(merchantDetailsVO.getPartnerId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_MEMBERID_PARTNERID);
            if(commonValidatorVO.getErrorCodeListVO()!=null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        merchantDetailsVO.setLogin(userName1);
        merchantDetailsVO.setRole(role);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }
    private String validateRestMerchantLogoutParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestMerchantLogoutParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;

    }

    public CommonValidatorVO performRestGetQueryFraudefenderValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        String error="";
        error=validateGetQueryFraudefenderRequestParameters(commonValidatorVO, "REST");
        if(functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(!CallTypeEnum.Purchase_Inquiry.getValue().equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getCall_type()) && !CallTypeEnum.Fraud_Determined.getValue().equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getCall_type()) && !CallTypeEnum.Dispute_Initiated.getValue().equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getCall_type()) && !CallTypeEnum.Exception_file_listing.getValue().equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getCall_type()) && !CallTypeEnum.Stop_payment.getValue().equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getCall_type()))
        {
            error="Invalid Call type.";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_CALL_TYPE));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }

        return commonValidatorVO;

    }
    private String validateGetQueryFraudefenderRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {

        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestGetQueryFraudefenderMandatoryParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList, actionName);

       List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestGetQueryFraudefenderOptionalParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList, true);
        error = error + inputValiDatorUtils.getError(errorList, inputOptionalFieldsList, actionName);
        return error;

    }
    public CommonValidatorVO performRestCardWhitelistValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        System.out.println("inside validator::::");
        MerchantDetailsVO merchantDetailsVO = null;
        String error="";

        log.error("MemberId::::" + commonValidatorVO.getMerchantDetailsVO().getMemberId());
        log.error("AccountId::::" + commonValidatorVO.getMerchantDetailsVO().getAccountId());
        log.error("CustonerId::::" + commonValidatorVO.getAddressDetailsVO().getCustomerid());
        log.error("cardmap::::" + commonValidatorVO.getCardVOHashMap());
        commonValidatorVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commonValidatorVO.setCustomerId(commonValidatorVO.getAddressDetailsVO().getCustomerid());
        error = validateGetCardWhitelistRequestParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        merchantDetailsVO = getMerchantConfigDetailsByLogin(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            error = "Invalid request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        if (commonValidatorVO.getCardVOHashMap() == null)
        {
            error = "Invalid request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_CARD_ENCRYPTION));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        /*if (commonValidatorVO.getCardVOHashMap() != null)
        {
            Iterator<Map.Entry<String, CardVO>> map = commonValidatorVO.getCardVOHashMap().entrySet().iterator();
            while (map.hasNext()) {
                CardVO card= (CardVO) commonValidatorVO.getCardVOHashMap().get(commonValidatorVO.getAddressDetailsVO().getCustomerid());
            }
        }*/

        return commonValidatorVO;
    }

    private String validateGetCardWhitelistRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        log.debug("Mandatory parameters validation::"+commonValidatorVO.getCustomerId());
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestGetCardWhitelistMandatoryParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
        return error;
    }

    public CommonValidatorVO performRestGetRefundFraudefenderValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        String error="";
        error=validateGetRefundFraudefenderRequestParameters(commonValidatorVO, "REST");
        //transactionLogger.error("exception ------------------------------>"+error);
        if(functions.isValueNull(error))
        {
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performRestGetPaymentAndCardTypeValidation()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }


        return commonValidatorVO;

    }

    private String validateGetRefundFraudefenderRequestParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {

        String error ="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestGetRefundFraudefenderMandatoryParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1, inputMandatoryFieldsList, actionName);
        return error;

    }

    public CommonValidatorVO performRestMerchantSendReceiptEmailValidation(CommonValidatorVO commonValidatorVO)throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        MerchantDetailsVO merchantDetailsVO=null;
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        error =  validateRestKitMerchantSendReceiptEmailParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        merchantDetailsVO=merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        if (!Checksum.verifyMD5ChecksumForMerchantSendReceiptEmail(commonValidatorVO.getMerchantDetailsVO().getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
        }
        if (functions.isValueNull(error))
        {
            errorCodeListVO.addListOfError(errorCodeVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            return commonValidatorVO;
        }
        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }

    private String validateRestKitMerchantSendReceiptEmailParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList1 = new ArrayList<InputFields>();
        inputMandatoryFieldsList1.addAll(inputValiDatorUtils.getRestMerchantSendReceiptEmailParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1,inputMandatoryFieldsList1,actionName);

        return error;
    }

    private String validateRestKitMerchantSendReceiptSmsParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZConstraintViolationException
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        //Validation for Rest Mandatory parameters
        List<InputFields> inputMandatoryFieldsList1 = new ArrayList<InputFields>();
        inputMandatoryFieldsList1.addAll(inputValiDatorUtils.getRestMerchantSendReceiptSmsParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsList1, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1,inputMandatoryFieldsList1,actionName);

        return error;
    }

    public CommonValidatorVO performRestMerchantSendReceiptSmsValidation(CommonValidatorVO commonValidatorVO)throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        MerchantDetailsVO merchantDetailsVO=null;
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        error =  validateRestKitMerchantSendReceiptSmsParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        merchantDetailsVO=merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        if (!Checksum.verifyMD5ChecksumForMerchantSendReceiptSms(commonValidatorVO.getMerchantDetailsVO().getMemberId(), merchantDetailsVO.getKey(), commonValidatorVO.getAddressDetailsVO().getPhone(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
        }
        if (functions.isValueNull(error))
        {
            errorCodeListVO.addListOfError(errorCodeVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            return commonValidatorVO;
        }
        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }
    public CommonValidatorVO performRestGetDailySalesValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        MerchantDetailsVO merchantDetailsVO = null;
        String error="";
        merchantDetailsVO = getMerchantConfigDetailsByLogin(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
        {
            error = "Invalid member Id request";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TOID_INVALID));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if (functions.isEmptyOrNull(commonValidatorVO.getPaginationVO().getStartdate())){
            error = "Please provide Fromdate.";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_FROM_DATE));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(functions.isEmptyOrNull(commonValidatorVO.getPaginationVO().getEnddate())){
            error = "Please provide ToDate.";
            commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TO_DATE));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
        }
        if(functions.isValueNull(commonValidatorVO.getPaginationVO().getStartdate()) && functions.isValueNull(commonValidatorVO.getPaginationVO().getEnddate()))
        {
            boolean isValidDate = functions.isFutureDateComparisonWithFromAndToDate(commonValidatorVO.getPaginationVO().getStartdate(), commonValidatorVO.getPaginationVO().getEnddate(), "dd/MM/yyyy");
            if (isValidDate)
            {
                error = "From date should be greater then To date.";
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TODATE_GREATER_FROMDATE));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }
        if (!functions.isEmptyOrNull(commonValidatorVO.getTimeZone())){
            boolean isValidTimeZone = functions.isValidTimeZone(commonValidatorVO.getTimeZone());
            if (!isValidTimeZone){
                error = "Please provide valid TimeZone.";
                commonValidatorVO.setStatus(error);
                commonValidatorVO.setErrorCodeListVO(getErrorVO(ErrorName.VALIDATION_TIMEZONE));
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performVerificationForCancelInquiryAndDeleteToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, commonValidatorVO.getErrorCodeListVO(), null, null);
            }
        }
        return commonValidatorVO;
    }

    private String validateGenerateOTP(CommonValidatorVO commonValidatorVO,String actionName ) throws PZConstraintViolationException
    {

        String error                            = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();

        //Validation for Rest Optional parameters (card)
        List<InputFields> inputOptionalFieldsList1 = new ArrayList<InputFields>();
        inputOptionalFieldsList1.addAll(inputValiDatorUtils.getGenerateOTP(commonValidatorVO));
        ValidationErrorList errorList2              = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        return error;

    }

    public CommonValidatorVO performGenerateOTP(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        String error = "";

        error =  validateGenerateOTP(commonValidatorVO, "REST");
        if (functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException, NoSuchAlgorithmException
    {
        transactionLogger.error("---- Inside performValidation ----");
        String error                    = "";
        Merchants merchants             = new Merchants();
        ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
        error                           =  validateParameter(commonValidatorVO, "REST");

        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }else {

            MerchantDetailsVO merchantDetailsVO =  merchants.getMemberDetilas(commonValidatorVO.getMerchantDetailsVO().getMemberId());

            if(merchantDetailsVO != null){
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }else{
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_INVALID);
                errorCodeVO.setErrorReason(errorCodeVO.getErrorDescription());
                commonValidatorVO.setErrorMsg(errorCodeVO.getErrorDescription());
                if(commonValidatorVO.getErrorCodeListVO() != null){
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }
            }
        }
        return commonValidatorVO;
    }

    private String validateParameter(CommonValidatorVO commonValidatorVO,String actionName ) throws PZConstraintViolationException
    {

        String error                                = "";
        InputValiDatorUtils inputValiDatorUtils     = new InputValiDatorUtils();
        InputValidator inputValidator               = new InputValidator();
        List<InputFields> inputOptionalFieldsList1  = new ArrayList<InputFields>();

        inputOptionalFieldsList1.addAll(inputValiDatorUtils.validateParameter(commonValidatorVO));
        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.RestInputValidations(commonValidatorVO, inputOptionalFieldsList1, errorList2, false);

        error = error + inputValiDatorUtils.getError(errorList2,inputOptionalFieldsList1,actionName);

        return error;

    }

    public String performUpiTxnDeatilsValidation(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        String error                                = "";
        InputValiDatorUtils inputValiDatorUtils     = new InputValiDatorUtils();
        InputValidator inputValidator               = new InputValidator();
        List<InputFields> inputParametersList       = new ArrayList<InputFields>();
        ValidationErrorList errorList               = new ValidationErrorList();

        inputParametersList.add(InputFields.MEMBERID);
        inputParametersList.add(InputFields.PAYMENTID);
        inputParametersList.add(InputFields.AMOUNT);
        inputParametersList.add(InputFields.STATUS);
        inputParametersList.add(InputFields.VPA_ADDRESS);
        inputParametersList.add(InputFields.TRANSACTION_DATE);

        error += inputValidator.validateUpiTxnDetails(commonValidatorVO, inputParametersList, errorList, false, error);
//        error = error + inputValiDatorUtils.getError(errorList,inputParametersList,"rest");

        log.error("here for performUpiTxnDeatilsValidation :-----> "+ error);

        return error;
    }


}
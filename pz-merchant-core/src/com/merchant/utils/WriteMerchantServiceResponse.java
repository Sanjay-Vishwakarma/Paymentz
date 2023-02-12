package com.merchant.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TerminalDAO;
import com.manager.vo.DirectKitResponseVO;
import com.merchant.vo.responseVOs.MerchantResponseFlagsVO;
import com.merchant.vo.responseVOs.MerchantResponseVO;
import com.merchant.vo.responseVOs.MerchantServiceResponseVO;
import com.merchant.vo.responseVOs.Result;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 4/27/2017.
 */
public class WriteMerchantServiceResponse
{
    private Logger logger = new Logger(WriteMerchantServiceResponse.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(WriteMerchantServiceResponse.class.getName());
    private ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

    //Start Rest Error Response for Transaction
    public void setLoginResponseForError(MerchantServiceResponseVO response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if(response != null)
        {
            if(commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if(errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for(ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if(errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessMerchantLoginResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_LOGIN);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.SYS_LOGIN_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            response.setRole(commonValidatorVO.getMerchantDetailsVO().getRole());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setSecureKey(directKitResponseVO.getSecureKey());
            response.setEmail(directKitResponseVO.getEmail());
            response.setTelno(directKitResponseVO.getTelno());
            response.setContactPerson(directKitResponseVO.getContactPerson());
            response.setCountry(directKitResponseVO.getCountry());
            response.setAuthToken(directKitResponseVO.getAuthToken());
            response.setIsemailverified(commonValidatorVO.getMerchantDetailsVO().getEmailverified());
            response.setIsMobileVerified(directKitResponseVO.getIsmobileverified());
            response.setIsActive(commonValidatorVO.getMerchantDetailsVO().getActivation());
            response.setVirtualCheckout(commonValidatorVO.getMerchantDetailsVO().getVirtualCheckout());
            response.setIsMobileRequiredForVC(commonValidatorVO.getMerchantDetailsVO().getIsMobileRequiredForVC());
            response.setIsEmailRequiredForVC(commonValidatorVO.getMerchantDetailsVO().getIsEmailRequiredForVC());
            response.setIsPartialRefund(commonValidatorVO.getMerchantDetailsVO().getPartialRefund());
            response.setIsMultipleRefund(commonValidatorVO.getMerchantDetailsVO().getMultipleRefund());
            response.setIsRefund(commonValidatorVO.getMerchantDetailsVO().getIsrefund());
            response.setIsShareAllowed(commonValidatorVO.getMerchantDetailsVO().getIsShareAllowed());
            response.setIsSignatureAllowed(commonValidatorVO.getMerchantDetailsVO().getIsSignatureAllowed());
            response.setIsSaveReceiptAllowed(commonValidatorVO.getMerchantDetailsVO().getIsSaveReceiptAllowed());
            response.setDefaultLanguage(commonValidatorVO.getMerchantDetailsVO().getDefaultLanguage());
            response.setUpiSupportInvoice(directKitResponseVO.getUpiSupportInvoice());
            response.setUpiQRSupportIinvoice(directKitResponseVO.getUpiQRSupportIinvoice());
            response.setPaybylinkSpportInvoice(directKitResponseVO.getPaybylinkSpportInvoice());
            response.setAEPSSupportInvoice(directKitResponseVO.getAEPSSupportInvoice());
        }
    }

    public void setSuccessVerifyMailResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatus());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_VERIFY_MAIL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.REJECTED_VERIFY_MAIL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }


            response.setResult(result);
            response.setIsemailverified(commonValidatorVO.getMerchantDetailsVO().getEmailverified());
        }
    }

    public void setSuccessSendReceiptEmailResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result=new Result();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result.setDescription(directKitResponseVO.getStatus());

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                logger.error("inside setSuccessSendReceiptEmailResponse success condn ");
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_SEND_EMAIL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                logger.error("inside setSuccessSendReceiptEmailResponse failed condn ");
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.SYS_NO_RECORD_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setResult(result);
           }
    }

    public void setSuccessSendReceiptSmsResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result=new Result();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result.setDescription(directKitResponseVO.getStatus());

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                logger.error("inside setSuccessSendReceiptSmsResponse success condn ");
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_SEND_SMS);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                logger.error("inside setSuccessSendReceiptSmsResponse failed condn ");
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.SYS_NO_RECORD_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setResult(result);
        }
    }

    public void setFailAuthTokenResponse(MerchantServiceResponseVO response)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.SYS_AUTHTOKEN_FAILED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessAuthTokenResponse1(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_AUTH_TOKEN_GENERATED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setAuthToken(directKitResponseVO.getAuthToken());
        }
    }
    public void setSuccessAuthTokenResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_AUTH_TOKEN_REGENERATED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setAuthToken(directKitResponseVO.getAuthToken());
        }
    }

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        if(errorCodeVO!=null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public void setSignUpResponseForError(MerchantServiceResponseVO response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if(response != null)
        {
            if(commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if(errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for(ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if(errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessRestCardholderRegistrationResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Functions functions = new Functions();
        if(response != null)
        {
            result = new Result();
            response.setCustomerId(directKitResponseVO.getHolder());
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("Y"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MEMBER_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.MEMBER_SIGNUP_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in responseVO
            if(functions.isValueNull(directKitResponseVO.getPartnerId()))
                response.setPartnerId(directKitResponseVO.getPartnerId());
            if(functions.isValueNull(directKitResponseVO.getMemberId()))
                response.setMemberId(directKitResponseVO.getMemberId());

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessMerchantSignupResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {

                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_MERCHANT_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setSecureKey(directKitResponseVO.getSecureKey());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }


    public void setSuccessMerchantChangePasswordResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {

                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_CHANGE_PASSWORD);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_MERCHANT_CHANGE_PASSWORD);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            //response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessMerchantForgetPasswordResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {

                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_FORGET_PASSWORD);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_MERCHANT_FORGET_PASSWORD);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            //response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            //response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }


    public void setSuccessOTPGenerateResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }
    //new
    public void setSuccessOTPGeneratedResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                // result.setMobilecc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
                response.setMobilenumber(commonValidatorVO.getAddressDetailsVO().getPhone());
            }
            else if(directKitResponseVO.getStatus().equalsIgnoreCase("limitexceed")){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTPG_LIMIT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }



            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    //new

    public void setSuccessOTVerificationResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                //System.out.println("API code--"+errorCodeVO.getApiCode());
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_VERIFICATION);
                //System.out.println("Api code---->"+errorCodeVO.getApiCode() );
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                result.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                result.setMobilecc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
                result.setMobilenumber(commonValidatorVO.getAddressDetailsVO().getPhone());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.OTP_VERIFICATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }



            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessgetaddressResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {

            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getMerchantDetailsVO() != null)
            {

                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_GET_ADDRESS);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                result.setAddress(directKitResponseVO.getMerchantDetailsVO().getAddress());
                result.setCity(directKitResponseVO.getMerchantDetailsVO().getCity());
                result.setState(directKitResponseVO.getMerchantDetailsVO().getState());
                result.setCountry(directKitResponseVO.getMerchantDetailsVO().getCountry());
                result.setZip(directKitResponseVO.getMerchantDetailsVO().getZip());


            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_GET_ADDRESS);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessInvoiceConfigDetailsResponse(MerchantServiceResponseVO response,DirectKitResponseVO directKitResponseVO,CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (commonValidatorVO != null)
        {
            result = new Result();
            if(commonValidatorVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_INVOICE_DETAILS_SET);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_INVOICE_DETAILS_SET);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setResult(result);
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        }
    }
    public void setSuccessUpdateAddressDetailsResponse(MerchantServiceResponseVO response,DirectKitResponseVO directKitResponseVO,CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (commonValidatorVO != null)
        {
            result = new Result();
            if(commonValidatorVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_UPDATE_ADDRESS);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_ADDRESS_DETAILS_SET);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setResult(result);
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        }
    }


    public void setMerchantCurrencyResponse(MerchantServiceResponseVO signupResponse,DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = new Result();
        List currencyList = directKitResponseVO.getCurrencyList();
        result.setCurrencyList(currencyList);
        signupResponse.setResult(result);
    }
    public void setSuccessMerchantLogoutResponse(MerchantServiceResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();

            if("success".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_LOGOUT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.REJECTED_MERCHANT_LOGOUT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }


            response.setResult(result);
        }
    }

    public void setSignUpResponseForError(MerchantResponseVO response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if(response != null)
        {

            if(errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for(ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if(errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessOTPGeneratedResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result               = null;
        ErrorCodeVO errorCodeVO     = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                response.setMobilenumber(commonValidatorVO.getAddressDetailsVO().getPhone());
            }
            else if(directKitResponseVO.getStatus().equalsIgnoreCase("limitexceed")){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTPG_LIMIT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessOTVerificationResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                //System.out.println("API code--"+errorCodeVO.getApiCode());
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_VERIFICATION);
                //System.out.println("Api code---->"+errorCodeVO.getApiCode() );
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                result.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                result.setMobilecc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
                result.setMobilenumber(commonValidatorVO.getAddressDetailsVO().getPhone());
                result.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.OTP_VERIFICATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }



            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }



    public void setFailedOTPGeneratedResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Functions functions =new Functions();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("failed"))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHTOKEN);
              //  errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHTOKEN);
                logger.debug("Inside if Write error errorCodeUtils.getErrorCode VALIDATION_AUTHTOKEN::"+errorCodeVO.getApiCode());
              //  logger.debug("Inside if Write error errorCodeUtils.getErrorCode VALIDATION_AUTHTOKEN::"+errorCodeVO.getApiCode());
             //   logger.debug("Inside if Write error errorCodeUtils.getErrorCode");

                if (functions.isValueNull(errorCodeVO.getApiCode()))
                result.setResultCode(errorCodeVO.getApiCode());
                if (functions.isValueNull(errorCodeVO.getApiDescription()))
                    result.setDescription(errorCodeVO.getApiDescription());

            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessMerchantThemeResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result               = null;
        ErrorCodeVO errorCodeVO     = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            /*if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                response.setMobilenumber(commonValidatorVO.getAddressDetailsVO().getPhone());
            }
            else if(directKitResponseVO.getStatus().equalsIgnoreCase("limitexceed")){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTPG_LIMIT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }*/

            if(directKitResponseVO.getListOfAsyncParameterVo() != null && directKitResponseVO.getListOfAsyncParameterVo().size() > 0){
                result.setMerchantThemeList(directKitResponseVO.getListOfAsyncParameterVo());
            }


            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setMerchantThemeResponseForError(MerchantResponseVO response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if(response != null)
        {

            if(errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for(ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if(errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setFailedResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Functions functions =new Functions();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("failed"))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHTOKEN);
                logger.debug("Inside if Write error errorCodeUtils.getErrorCode VALIDATION_AUTHTOKEN::"+errorCodeVO.getApiCode());

                if (functions.isValueNull(errorCodeVO.getApiCode()))
                    result.setResultCode(errorCodeVO.getApiCode());
                if (functions.isValueNull(errorCodeVO.getApiDescription()))
                    result.setDescription(errorCodeVO.getApiDescription());

            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setFailedMerchantFlagResponse(MerchantResponseFlagsVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result           = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Functions functions     = new Functions();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date               = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("failed"))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHTOKEN);

                if (functions.isValueNull(errorCodeVO.getApiCode()))
                    result.setResultCode(errorCodeVO.getApiCode());
                if (functions.isValueNull(errorCodeVO.getApiDescription()))
                    result.setDescription(errorCodeVO.getApiDescription());

            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessMemberAllTerminalFlagsResponse(MerchantResponseVO response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        TerminalDAO terminalDAO=new TerminalDAO();
        Functions functions=new Functions();
        String memberid="";
        String paymodeid="";
        String cardtypeid="";
        String currency="";
         if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            memberid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
         if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentid()))
            paymodeid=commonValidatorVO.getTransDetailsVO().getPaymentid();
         if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCardType()))
            cardtypeid=commonValidatorVO.getTransDetailsVO().getCardType();
         if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            currency=commonValidatorVO.getTransDetailsVO().getCurrency();
        logger.error("setSuccessMemberAllTerminalFlagsResponse::"+memberid);

        List terminallist= null;
        result = new Result();
        result.setDescription(directKitResponseVO.getStatusMsg());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try
        {
            if(functions.isValueNull(memberid)&&
            (functions.isValueNull(paymodeid)||functions.isValueNull(cardtypeid)||functions.isValueNull(currency)))
            {
             terminallist = terminalDAO.getTerminalsFlagsByMerchantId(memberid,paymodeid,cardtypeid,currency);

                if(terminallist != null&&terminallist.size()>0)
                {
                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                    result.setMemberTerminalList(terminallist);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_NO_RECORD_FOUND);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
             }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_NO_RECORD_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
        catch (PZDBViolationException e)
        {
            e.printStackTrace();
        }
    }
}
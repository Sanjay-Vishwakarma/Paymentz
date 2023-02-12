package com.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;
import com.vo.responseVOs.ApplicationManagerResponse;
import com.vo.responseVOs.Result;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 10/23/2017.
 */
public class WriteMerchantServiceResponse
{
    private Logger logger = new Logger(WriteMerchantServiceResponse.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(WriteMerchantServiceResponse.class.getName());
    private ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        if(errorCodeVO!=null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public void setLoginResponseForError(ApplicationManagerResponse response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
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

    public void setSuccessMerchantLoginResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_LOGIN_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            commonValidatorVO.getMerchantDetailsVO().setLogin(response.getLogin());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setSecureKey(directKitResponseVO.getSecureKey());
            response.setEmail(directKitResponseVO.getEmail());
            response.setTelno(directKitResponseVO.getTelno());
            response.setContactPerson(directKitResponseVO.getContactPerson());
            response.setCountry(directKitResponseVO.getCountry());
            response.setAuthToken(directKitResponseVO.getAuthToken());
           // response.getApplicationManagerVO().setApplicationId(commonValidatorVO.getMerchantDetailsVO().);
        }
    }

    public void setFailAuthTokenResponse(ApplicationManagerResponse response)
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

    public void setSuccessAuthTokenResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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



    public void setSignUpResponseForError(ApplicationManagerResponse response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
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

    public void setSuccessRestCardholderRegistrationResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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

    public void setSuccessMerchantSignupResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            commonValidatorVO.getMerchantDetailsVO().setLogin(response.getLoginName());
            response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }


    public void setSuccessMerchantChangePasswordResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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

    public void setSuccessMerchantForgetPasswordResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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


    public void setSuccessOTPGenerateResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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
    public void setSuccessOTPGeneratedResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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

    public void setSuccessOTVerificationResponse(ApplicationManagerResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
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

    public void setMerchantCurrencyResponse(ApplicationManagerResponse signupResponse,DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = new Result();
        List currencyList = directKitResponseVO.getCurrencyList();
        result.setCurrencyList(currencyList);
        signupResponse.setResult(result);
    }
}

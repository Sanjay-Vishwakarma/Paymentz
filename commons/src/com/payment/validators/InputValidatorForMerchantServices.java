package com.payment.validators;

import com.directi.pg.Functions;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.List;

/**
 * Created by Admin on 4/27/2017.
 */
public class InputValidatorForMerchantServices
{

    public void restInputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        String resField2 = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        Functions functions=new Functions();
        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getLogin(), "username", 255, isOptional)|| functions.hasHTMLTags(commonValidatorVO.getMerchantDetailsVO().getLogin()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LOGIN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LOGIN, ErrorMessages.INVALID_LOGIN + ":::" + commonValidatorVO.getMerchantDetailsVO().getLogin(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PARTNER_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPartnerName(), "Login", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LOGIN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LOGIN, ErrorMessages.INVALID_LOGIN + ":::" + commonValidatorVO.getPartnerName(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getPassword(), "Password", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PASSWORD, ErrorMessages.INVALID_PASSWORD + ":::" + commonValidatorVO.getMerchantDetailsVO().getPassword(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getParetnerId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PARTNERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PARTNERID, ErrorMessages.INVALID_PARTNERID + ":::" + commonValidatorVO.getParetnerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + commonValidatorVO.getMerchantDetailsVO().getMemberId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getNewPassword(), "Password", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PASSWORD, ErrorMessages.INVALID_PASSWORD+ ":::" + commonValidatorVO.getMerchantDetailsVO().getNewPassword(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case AUTHTOKEN:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAuthToken(), "SafeString", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHTOKEN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AUTHTOKEN, ErrorMessages.INVALID_AUTHTOKEN+ ":::" + commonValidatorVO.getAuthToken(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

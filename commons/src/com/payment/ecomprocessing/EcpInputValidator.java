package com.payment.ecomprocessing;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 28-Jan-20.
 */
public class EcpInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger = new TransactionLogger(EcpInputValidator.class.getName());

    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action, String addressValidation)
    {
        transactionLogger.error(":::::Inside EcpInputValidator :::::");
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        String error = "";

        List<InputFields> parameter = new ArrayList<InputFields>();
        parameter.add(InputFields.CITY);
        parameter.add(InputFields.ZIP);
        parameter.add(InputFields.COUNTRY);
        parameter.add(InputFields.STATE);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.STREET);
        parameter.add(InputFields.TELCC);
        parameter.add(InputFields.TELNO);

        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, parameter, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, parameter, action);
            inputValidations(commonValidatorVO, parameter, errorList1, false);
            String EOL = "<BR>";

            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        else
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, parameter, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, parameter, action);
            inputValidations(commonValidatorVO, parameter, errorList1, true);
            String EOL = "<BR>";

            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        return error;
    }
    private void inputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        for (InputFields input : inputFields)
        {
            switch (input)
            {
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "SafeString", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 9, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "SafeString", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTelnocc(), "Phone", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNOCC, ErrorMessages.INVALID_TELNOCC + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

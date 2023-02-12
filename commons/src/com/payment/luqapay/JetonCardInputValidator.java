package com.payment.luqapay;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2/21/2020.
 */
public class JetonCardInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger = new TransactionLogger(JetonCardInputValidator.class.getName());

    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action, String addressValidation)
    {
        transactionLogger.error(":::::Inside JetonCardInputValidator :::::");
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        String error = "";

        List<InputFields> parameter = new ArrayList<InputFields>();
        parameter.add(InputFields.CITY);
        parameter.add(InputFields.ZIP);
        parameter.add(InputFields.COUNTRY);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.STREET);
        //parameter.add(InputFields.BIRTHDATE);
        parameter.add(InputFields.EMAILADDR);

        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            inputValiDatorUtils.getError(errorList, parameter, action);
            inputValidations(commonValidatorVO, parameter, errorList, false);
            String EOL = "<BR>";

            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        else
        {
            ValidationErrorList errorList = new ValidationErrorList();
            inputValiDatorUtils.getError(errorList, parameter, action);
            inputValidations(commonValidatorVO, parameter, errorList, true);
            String EOL = "<BR>";

            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
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
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMAIL, ErrorMessages.INVALID_EMAIL + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getBirthdate(), "Numbers", 8, isOptional) /*|| !Functions.isValidDate(genericAddressDetailsVO.getBirthdate())*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BIRTHDATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BIRTHDATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_BIRTHDATE, ErrorMessages.INVALID_BIRTHDATE + ":::" + genericAddressDetailsVO.getBirthdate(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

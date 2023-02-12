package com.payment.globalgate;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.CurrencyCodeISO4217;
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
 * Created by Admin on 8/17/2021.
 */
public class GlobalGateInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(GlobalGateInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(GlobalGateInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        log.debug("inside validateIntegrationSpecificParameters for globalgate");
        /*String error = "";

        if ("39".equals(commonValidatorVO.getCardType()))
        {
            InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
            InputValidator inputValidator = new InputValidator();
            List<InputFields> param = new ArrayList<InputFields>();

            param.add(InputFields.FIRSTNAME);
            param.add(InputFields.LASTNAME);
            //param.add(InputFields.BIRTHDATE);
            //param.add(InputFields.COUNTRYCODE);

            if ("Y".equalsIgnoreCase(addressValidation))
            {
                ValidationErrorList errorList = new ValidationErrorList();
                inputValidator.InputValidations(commonValidatorVO, param, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, param, actionName);
            }
            return error;
        }
        else
        {
            InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
            InputValidator inputValidator = new InputValidator();
            List<InputFields> param = new ArrayList<InputFields>();

            param.add(InputFields.EMAILADDR);
            param.add(InputFields.TELNO);
            param.add(InputFields.TELCC);
            param.add(InputFields.COUNTRYCODE);
            param.add(InputFields.CITY);
            param.add(InputFields.STATE);
            param.add(InputFields.ZIP);
            param.add(InputFields.STREET);

            if ("Y".equalsIgnoreCase(addressValidation))
            {
                ValidationErrorList errorList = new ValidationErrorList();
                inputValidator.InputValidations(commonValidatorVO, param, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, param, actionName);
            }
            return error;
        }*/


        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
        String error                            = "";
        List<InputFields> parameter             = new ArrayList<>();

        List<InputFields> mandatoryParameter    = new ArrayList<>();

        mandatoryParameter.add(InputFields.CURRENCY);

        parameter.add(InputFields.COUNTRYCODE);
        parameter.add(InputFields.STATE);
        parameter.add(InputFields.STREET);
        parameter.add(InputFields.TELNO);
        parameter.add(InputFields.EMAILADDR);
        parameter.add(InputFields.CITY);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.TELCC);


        if ("Y".equals(addressValidation))
        {
            ValidationErrorList errorList   = new ValidationErrorList();
            InputValidationsforGlobalGate(commonValidatorVO, parameter, errorList, false);
            error                           = error + inputValiDatorUtils.getError(errorList, parameter, actionName);
        }
        else
        {
            ValidationErrorList errorList   = new ValidationErrorList();
            InputValidationsforGlobalGate(commonValidatorVO, parameter, errorList, true);
            error                           = error + inputValiDatorUtils.getError(errorList, parameter, actionName);
        }
        ValidationErrorList errorList1  = new ValidationErrorList();
        transactionLogger.error("Validation for Currency======>");

        InputValidationsforGlobalGate(commonValidatorVO, mandatoryParameter, errorList1, false, currency);
        error                           = error + inputValiDatorUtils.getError(errorList1, mandatoryParameter, actionName);
        transactionLogger.error("mandatory parameter error ==== " + error);
        return error;

    }

    private void InputValidationsforGlobalGate(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        transactionLogger.error(":::::Inside GlobalGateInputValidator:::::");

        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        Functions functions = new Functions();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();

        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getPhone(), "Phone", 24, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCountry(),"StrictString",2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");
                        errorCodeVO.setApiDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");

                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));

                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCity(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "SafeString", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getCurrency(), "SafeString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "AccentUsername", 50, isOptional))
                    {

                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "AccentUsername", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getTelnocc(), "Phone", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_TELNOCC, ErrorMessages.INVALID_TELNOCC + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }


    private void InputValidationsforGlobalGate(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional,String currency)
    {
        transactionLogger.error(":::::Inside GlobalGateInputValidator:::::");
        transactionLogger.error(":::::Inside GlobalGateInputValidator currency validator :::::");

        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();

        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), currency, "SafeString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;

            }
        }
    }
}


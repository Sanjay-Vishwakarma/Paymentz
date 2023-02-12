package com.payment.doku;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
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
 * Created by Admin on 6/23/2021.
 */
public class DokuPaymentInputValidator  extends CommonInputValidator
{
    private static Logger log                               = new Logger(DokuPaymentInputValidator.class.getName());
    private static TransactionLogger transactionLogger      = new TransactionLogger(DokuPaymentInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        transactionLogger.error(":::Inside DokuPaymentInputValidator:::");
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        String error                            = "";
        List<InputFields> parameter             = new ArrayList<>();
        List<InputFields> mandatoryParameter    = new ArrayList<>();

        mandatoryParameter.add(InputFields.COUNTRYCODE);

        parameter.add(InputFields.STATE);
        parameter.add(InputFields.STREET);
        parameter.add(InputFields.TELNO);


        if ("Y".equals(addressValidation))
        {
            ValidationErrorList errorList   = new ValidationErrorList();
            InputValidationsforDoku(commonValidatorVO, parameter, errorList, false);
            error                           = error + inputValiDatorUtils.getError(errorList, parameter, actionName);
        }
        else
        {
            ValidationErrorList errorList   = new ValidationErrorList();
            InputValidationsforDoku(commonValidatorVO, parameter, errorList, true);
            error                           = error + inputValiDatorUtils.getError(errorList, parameter, actionName);
        }

        ValidationErrorList errorList1  = new ValidationErrorList();
        InputValidationsforDoku(commonValidatorVO, mandatoryParameter, errorList1, false);
        error                           = error + inputValiDatorUtils.getError(errorList1, mandatoryParameter, actionName);

        return error;

    }

    private void InputValidationsforDoku(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        transactionLogger.error(":::::Inside DokuPaymentInputValidator:::::");

        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();

        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
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
                case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);

                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));

                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getPhone(), "Phone", 24, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));

                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;

                case STATE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "SafeString", 41, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);

                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);

                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));

                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;
            }
        }
    }
}
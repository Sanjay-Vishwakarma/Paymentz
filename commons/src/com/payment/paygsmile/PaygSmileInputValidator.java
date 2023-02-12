package com.payment.paygsmile;

import com.directi.pg.Functions;
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
 * Created by Admin on 2022-01-24.
 */
public class PaygSmileInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(PaygSmileInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        transactionLogger.error("*** --- Inside PayGSmileIputValidator --- ***");
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> mandatoryParams       = new ArrayList<InputFields>();


        mandatoryParams.add(InputFields.FIRSTNAME);
        mandatoryParams.add(InputFields.LASTNAME);


        if("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidations(commonValidatorVO, mandatoryParams, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);
        }
        else
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidations(commonValidatorVO, mandatoryParams, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);
        }
        transactionLogger.error("error  size------------>");

        return error;
    }

    public void InputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();


        Functions functions = new Functions();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getFirstname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getLastname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }
}

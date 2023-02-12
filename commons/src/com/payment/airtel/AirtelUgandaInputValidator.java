package com.payment.airtel;

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
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 3/23/2021.
 */
public class AirtelUgandaInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(AirtelUgandaInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        transactionLogger.error("inside AirtelUgandaInputValidator -----> ");
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        List<InputFields> mandatoryParameter    = new ArrayList<>();

        mandatoryParameter.add(InputFields.TELNO);
        mandatoryParameter.add(InputFields.EMAILADDR);


        ValidationErrorList errorList  = new ValidationErrorList();
        InputValidation(commonValidatorVO, mandatoryParameter, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, mandatoryParameter, actionName);

        return error;
    }

    private void InputValidation(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        transactionLogger.error(":::::Inside AirtelUgandaInputValidator:::::");

        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case TELNO:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getPhone(), "OnlyNumber", 9, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorDescription("Invalid telephone number, accepts only [0-9] with max length 9");
                        errorCodeVO.setApiDescription("Invalid telephone number, accepts only [0-9] with max length 9");
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}
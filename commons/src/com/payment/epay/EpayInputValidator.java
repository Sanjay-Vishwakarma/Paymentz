package com.payment.epay;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
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
 * Created by Admin on 11/3/17.
 */
public class EpayInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger = new TransactionLogger(EpayInputValidator.class.getName());
    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action)
    {
        transactionLogger.error(":::::Inside Epay Input Validator :::::");
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        String error = "";

        List<InputFields> parameter = new ArrayList<InputFields>();
        parameter.add(InputFields.CUSTOMER_BANK_ID);
        parameter.add(InputFields.EMAILADDR);
        ValidationErrorList errorList = new ValidationErrorList();
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, parameter, errorList, false);
        //error = error + inputValiDatorUtils.getError(errorList, parameter, action);
        InputValidationsforEpay(commonValidatorVO, parameter, errorList1, false);
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
        return error;
    }
    private void InputValidationsforEpay(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();


        for (InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case CUSTOMER_BANK_ID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(),commonValidatorVO.getCustomerBankId(), "alphanum", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_BANK_ID);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" +commonValidatorVO.getCustomerBankId(), errorCodeVO));
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

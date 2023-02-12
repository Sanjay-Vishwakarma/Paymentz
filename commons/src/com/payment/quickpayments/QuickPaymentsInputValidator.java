package com.payment.quickpayments;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
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
 * Created by Admin on 5/17/2021.
 */
public class QuickPaymentsInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger= new TransactionLogger(QuickPaymentsInputValidator.class.getName());
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname,String addressValidation)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        List<InputFields> parameter = new ArrayList();
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidationsforQuickPayments(commonValidatorVO, parameter, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, parameter, actionname);

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
            InputValidationsforQuickPayments(commonValidatorVO, parameter, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, parameter, actionname);

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
    private void InputValidationsforQuickPayments(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
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
            }
        }
    }
}

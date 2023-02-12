package com.payment.iMerchantPay;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/1/18.
 */
public class iMerchantInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger= new TransactionLogger(iMerchantInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action, String addressValidation)
    {
        transactionLogger.error(":::::Inside iMerchantInputValidator :::::");
        String error = "";

        List<InputFields> parameter = new ArrayList<InputFields>();
        parameter.add(InputFields.IPADDR);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        //ValidationErrorList errorList = new ValidationErrorList();
        ValidationErrorList errorList1 = new ValidationErrorList();
        //inputValidator.InputValidations(commonValidatorVO, parameter, errorList, false);
        //error = error + inputValiDatorUtils.getError(errorList, parameter, action);
        InputValidationsforIMerchant(commonValidatorVO, parameter, errorList1, false);

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
    private void InputValidationsforIMerchant(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        for (InputFields inputFields1 : inputFields)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (inputFields1)
            {
                case IPADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getIp(), "IPAddress", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_IPADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_IPADDRESS);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getIp(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 32, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_FIRST_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 32, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_LAST_NAME);
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

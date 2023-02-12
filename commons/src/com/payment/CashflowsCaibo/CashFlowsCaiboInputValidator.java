package com.payment.CashflowsCaibo;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
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
public class CashFlowsCaiboInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(CashFlowsCaiboInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        transactionLogger.error("inside CashFlowsCaiboInputValidator ---> ");
        String error="";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        List<InputFields> addressParameter      = new ArrayList();
        List<InputFields> mandatoryParameter = new ArrayList();

        mandatoryParameter.add(InputFields.COUNTRYCODE);
        mandatoryParameter.add(InputFields.CURRENCY);
        ValidationErrorList errorList2 = new ValidationErrorList();
        InputValidations(commonValidatorVO, mandatoryParameter, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2, mandatoryParameter, actionName);
        transactionLogger.error("mandatory parameter error ==== " + error);

        addressParameter.add(InputFields.COUNTRYCODE);
        addressParameter.add(InputFields.CURRENCY);
        transactionLogger.error("addressValidation ==== " + addressValidation);

        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            InputValidations(commonValidatorVO, addressParameter, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, addressParameter, actionName);
            transactionLogger.error("error in if block ---> " +error);
        }
        else
        {
            ValidationErrorList errorList = new ValidationErrorList();
            InputValidations(commonValidatorVO, addressParameter, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, addressParameter, actionName);
            transactionLogger.error("error in else block ---> " + error);
        }

        return error;
    }

    private void InputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        transactionLogger.error("getCuntry : "+ genericAddressDetailsVO.getCountry() + "\n getCurrency() : "+genericTransDetailsVO.getCurrency());
        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput("country", genericAddressDetailsVO.getCountry(), "StrictString", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");
                        errorCodeVO.setApiDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        transactionLogger.error(genericAddressDetailsVO.getCountry()+"_Countrycode error---> "+errorCodeVO);
                    }
                    else{
                        transactionLogger.error("countrycode matches with validator.StrictString property :"+ genericAddressDetailsVO.getCountry());
                    }
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getCurrency(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorDescription("Invalid currency code, accepts only [a-z][A-Z] with max length 3");
                        errorCodeVO.setApiDescription("Invalid currency code, accepts only [a-z][A-Z] with max length 3");
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        transactionLogger.error(genericTransDetailsVO.getCurrency()+"_CURRENCYcode error---> "+errorCodeVO);
                    }else{
                        transactionLogger.error("currency matches with validator.StrictString property :"+ genericTransDetailsVO.getCurrency());
                    }
                    break;
            }
        }
    }
}

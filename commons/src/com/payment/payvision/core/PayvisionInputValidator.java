package com.payment.payvision.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.utils.CommonFunctionUtil;
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
 * Created by admin on 5/21/2016.
 */
public class PayvisionInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(PayvisionInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayvisionInputValidator.class.getName());

    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();

        param.add(InputFields.COUNTRYCODE);
        ValidationErrorList errorList = new ValidationErrorList();

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            InputValidationsForPayvision(commonValidatorVO, param, errorList, false);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        return error;

    }

    public void InputValidationsForPayvision(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)
        {
            switch (input)
            {
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorDescription("Invalid Country code, Accept only [0-9][a-z][A-Z] with Max Length 2");
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    else if(!commonFunctionUtil.is2LetterCountryCodeValid(genericAddressDetailsVO.getCountry()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorDescription("Invalid Country code, Accept only [0-9][a-z][A-Z] with Max Length 2");
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode() +"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
            }
        }
    }
}
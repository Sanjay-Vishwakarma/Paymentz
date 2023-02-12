package com.payment.sbm.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
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
 * Created by Nikita on 2/7/2017.
 */
public class SBMInputValidator extends CommonInputValidator
{
    private static Logger logger = new Logger(SBMInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SBMInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        param.add(InputFields.CVV);
        param.add(InputFields.AMOUNT);
        param.add(InputFields.CURRENCY);
        param.add(InputFields.REDIRECT_URL);
        param.add(InputFields.TERMINALID);
        param.add(InputFields.DESCRIPTION);
        param.add(InputFields.LANGUAGE);

        ValidationErrorList errorList = new ValidationErrorList();

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidationForSBM(commonValidatorVO, param, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList,param,actionName);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }

        return error;
    }

    public void inputValidationForSBM(CommonValidatorVO commonValidatorVO,List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        for (InputFields input : inputList)
        {
            switch (input)
            {
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getcVV(), "OnlyNumber", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV_WIRECARD);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericCardDetailsVO.getcVV(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAmount(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

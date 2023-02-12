package com.payment.uPayGate;

import com.directi.pg.Logger;
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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/26/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class UPayGateInputValidator extends CommonInputValidator
{
    String error = "";
    private static Logger log = new Logger(UPayGateInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(UPayGateInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        param.add(InputFields.CARDHOLDERIP);
        ValidationErrorList errorList = new ValidationErrorList();

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidation(commonValidatorVO, param, errorList, false);
            //error = error + inputValiDatorUtils.getError(errorList,param,actionName);

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

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void inputValidation(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)
        {
            switch (input)
            {
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMER_IP, ErrorMessages.INVALID_CUSTOMER_IP + ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid State","Invalid State :::"+genericAddressDetailsVO.getState()));
                    }
                    break;
            }
        }
    }
}


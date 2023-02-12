package com.payment.omnipay;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2022-01-24.
 */
public class OmnipayInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(OmnipayInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> mandatoryParams       = new ArrayList<InputFields>();
        List<InputFields> optionalParams       = new ArrayList<InputFields>();


        mandatoryParams.add(InputFields.FIRSTNAME);
        mandatoryParams.add(InputFields.LASTNAME);
        mandatoryParams.add(InputFields.CARDHOLDER);
        mandatoryParams.add(InputFields.EMAILADDR);
        mandatoryParams.add(InputFields.CARDHOLDERIP);

        optionalParams.add(InputFields.ADDRESS);
        optionalParams.add(InputFields.CITY);
        optionalParams.add(InputFields.STREET);
        optionalParams.add(InputFields.COUNTRYCODE);
        optionalParams.add(InputFields.TELNO);
        optionalParams.add(InputFields.ZIP);

        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, (List)mandatoryParams, errorList1, false);
        error += inputValiDatorUtils.getError(errorList1, (List)mandatoryParams, actionName);

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, (List)optionalParams, errorList1, false);
            error += inputValiDatorUtils.getError(errorList1, (List)optionalParams, actionName);
        }
        else
        {
            errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, (List)optionalParams, errorList1, true);
            error += inputValiDatorUtils.getError(errorList1, (List)optionalParams, actionName);
        }


        OmnipayInputValidator.transactionLogger.error("error  size------------>" + commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        OmnipayInputValidator.transactionLogger.error("error  OmnipayInputValidator------------>" + error + " " + addressValidation);
        return error;
    }
}
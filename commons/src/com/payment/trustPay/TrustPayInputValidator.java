package com.payment.trustPay;

import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 11/4/2016.
 */
public class TrustPayInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName, String addressValidation)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();

        List<InputFields> transactionMandatoryFields = new ArrayList<InputFields>();
        transactionMandatoryFields.add(InputFields.AMOUNT);
        transactionMandatoryFields.add(InputFields.CURRENCY);
        transactionMandatoryFields.add(InputFields.CARDTYPE);
        
        ValidationErrorList errorList = new ValidationErrorList();
        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidator.InputValidations(commonValidatorVO, transactionMandatoryFields, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, transactionMandatoryFields, actionName);
        }
        else
        {
            inputValidator.InputValidations(commonValidatorVO, transactionMandatoryFields, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, transactionMandatoryFields, actionName);
        }

        return error;
    }
}

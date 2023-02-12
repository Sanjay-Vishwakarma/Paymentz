package com.payment.inpay;

import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/10/14
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class InPayInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName, String addressValidation)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();

        inputMandatoryFieldsList.add(InputFields.EMAILADDR);
        inputMandatoryFieldsList.add(InputFields.STREET);
        inputMandatoryFieldsList.add(InputFields.CITY);
        inputMandatoryFieldsList.add(InputFields.STATE);
        inputMandatoryFieldsList.add(InputFields.ZIP);
        inputMandatoryFieldsList.add(InputFields.TELNO);


        List<InputFields> mandatoryParameter = new ArrayList<InputFields>();
        mandatoryParameter.add(InputFields.AMOUNT);
        mandatoryParameter.add(InputFields.DESCRIPTION);
        mandatoryParameter.add(InputFields.FIRSTNAME);
        mandatoryParameter.add(InputFields.LASTNAME);
        ValidationErrorList validationErrorList2 = new ValidationErrorList();

        inputValidator.InputValidations(commonValidatorVO, mandatoryParameter, validationErrorList2, false);
        error = error + inputValiDatorUtils.getError(validationErrorList2, mandatoryParameter, actionName);

        ValidationErrorList errorList = new ValidationErrorList();
        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
        }
        else
        {
            inputValidator.InputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, inputMandatoryFieldsList, actionName);
        }
        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }



}

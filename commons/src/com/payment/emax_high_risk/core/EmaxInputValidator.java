package com.payment.emax_high_risk.core;

import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 5/18/2015.
 */
public class EmaxInputValidator extends CommonInputValidator
{
    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.CARDHOLDERIP);
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

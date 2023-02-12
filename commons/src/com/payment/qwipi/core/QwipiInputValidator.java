package com.payment.qwipi.core;

import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/26/14
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiInputValidator extends CommonInputValidator
{


    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.BIRTHDATE);
        inputMandatoryFieldsList.add(InputFields.LANGUAGE);
        ValidationErrorList errorList = new ValidationErrorList();
        if("Y".equalsIgnoreCase(addressValidation))
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

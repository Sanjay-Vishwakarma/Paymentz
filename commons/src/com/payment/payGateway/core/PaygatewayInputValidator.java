package com.payment.payGateway.core;

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
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaygatewayInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        InputValidator inputValidator = new InputValidator();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.BIRTHDATE);
        inputMandatoryFieldsList.add(InputFields.ORDERDESCRIPTION);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

}

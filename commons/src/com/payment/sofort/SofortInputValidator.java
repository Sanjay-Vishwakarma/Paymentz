package com.payment.sofort;

import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 21/1/15
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SofortInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String action, String addressValidation)
    {
        String error="";
        InputValidator inputValidator = new InputValidator();
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
        Hashtable<InputFields, String> param=new Hashtable<InputFields, String>();

        param.put(InputFields.EMAILADDR,genericAddressDetailsVO.getEmail());
        param.put(InputFields.PHONENO,genericAddressDetailsVO.getPhone());
        //param.put(InputFields.TELCC,genericAddressDetailsVO.getTelnocc());

        ValidationErrorList errorList = new ValidationErrorList();
        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidator.InputValidations(param, errorList, false);
        }
        else
        {
            inputValidator.InputValidations(param, errorList, true);
        }
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param.keySet())
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        error = error + errorList.getError(inputFields.toString()).getMessage() + "<BR>";
                    }
                }
            }


        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }


}

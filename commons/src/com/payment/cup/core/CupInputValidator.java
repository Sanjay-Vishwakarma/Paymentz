package com.payment.cup.core;

import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/27/14
 * Time: 1:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class CupInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO)
    {
        String error="";
        InputValidator inputValidator = new InputValidator();
        Hashtable<InputFields, String> param=new Hashtable<InputFields, String>();

        param.put(InputFields.AMOUNT,commonValidatorVO.getTransDetailsVO().getAmount());
        param.put(InputFields.ORDERID,commonValidatorVO.getTransDetailsVO().getOrderId());

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(param,errorList,false);
        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :param.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    error = error + errorList.getError(inputFields.toString()).getMessage()+"<BR>";
                }
            }
        }

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

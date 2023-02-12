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
 * Date: 24/2/15
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdealInputValidator  extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO)
    {
        String error="";
        InputValidator inputValidator = new InputValidator();
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
        Hashtable<InputFields, String> param=new Hashtable<InputFields, String>();

        param.put(InputFields.IDEAL_BANK_CODE,commonValidatorVO.getSenderBankCode());

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

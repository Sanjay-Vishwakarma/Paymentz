package com.payment.paySafeCard;

import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/19/15
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaySafeCardInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO)
    {
        String error="";
        InputValidator inputValidator = new InputValidator();
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        Hashtable<InputFields, String> param=new Hashtable<InputFields, String>();

        param.put(InputFields.AMOUNT,genericTransDetailsVO.getAmount());
        param.put(InputFields.ORDERID,genericTransDetailsVO.getOrderId());
        param.put(InputFields.ORDERDESC,genericTransDetailsVO.getOrderDesc());
        param.put(InputFields.EMAIL,genericAddressDetailsVO.getEmail());
        param.put(InputFields.FIRSTNAME,genericAddressDetailsVO.getFirstname());
        param.put(InputFields.LASTNAME,genericAddressDetailsVO.getLastname());
        param.put(InputFields.BIRTHDATE,genericAddressDetailsVO.getBirthdate());

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

        return error;
    }
}

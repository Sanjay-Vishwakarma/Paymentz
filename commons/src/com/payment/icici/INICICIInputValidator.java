package com.payment.icici;

import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 9/7/15.
 */
public class INICICIInputValidator extends CommonInputValidator
{
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        String error="";
        if (commonValidatorVO.getAddressDetailsVO().getCountry().length()!=3)
        {
            error="Invalid Country code[Should be 3 Character]";
        }
        return  error;
    }
}

package com.payment.visaNet;

import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;

import javax.smartcardio.Card;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/6/15
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisaNetInputValidator extends CommonInputValidator
{
    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        String error="";
        if("Y".equalsIgnoreCase(addressValidation))
        {
            if (!ESAPI.validator().isValidInput("country", commonValidatorVO.getAddressDetailsVO().getCountry(), "CountryCode", 2, false))
            {
                error = "Invalid Country code[This must not be special character in]";
            }
            if (!ESAPI.validator().isValidInput("state", commonValidatorVO.getAddressDetailsVO().getState(), "CountryCode", 2, false))
            {
                error = "Invalid State[Parameter must be completed with 2 letters/must not be special character]";
            }
            if (!ESAPI.validator().isValidInput("city", commonValidatorVO.getAddressDetailsVO().getCity(), "Description", 100, false))
            {
                error = "Invalid City[Parameter must not be special character]";
            }
            if (!ESAPI.validator().isValidInput("street", commonValidatorVO.getAddressDetailsVO().getStreet(), "alphanum", 100, false))
            {
                error = "Invalid Street[Parameter must not be special character]";
            }
        }
            return error;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public String validateCVVForTokenTransaction(CommonValidatorVO commonValidatorVO)
    {
        String error = "";
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        if ("---".equals(genericCardDetailsVO.getcVV()))
        {
        }
        else
        {
            if (!ESAPI.validator().isValidInput(genericCardDetailsVO.getcVV(), genericCardDetailsVO.getcVV(), "CVV", 4, false))
            {
                error = "Invalid CVV,Accepts only numeric[0-9] with max length 4 Or Accepts string [---] with max length 3";
            }
        }
        return error;
    }
}

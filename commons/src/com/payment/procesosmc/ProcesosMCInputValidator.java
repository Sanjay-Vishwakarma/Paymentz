package com.payment.procesosmc;

import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/2/15
 * Time: 04:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCInputValidator extends CommonInputValidator
{
    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName)
    {
        return "";  //To change body of implemented methods use File | Settings | File Templates.
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
                error = "Accept only numeric[0-9] with Max Length 4 Or [---] string for token transaction";
            }
        }
        return error;
    }
}

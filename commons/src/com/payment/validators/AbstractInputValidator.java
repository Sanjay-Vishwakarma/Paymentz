package com.payment.validators;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 7/25/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractInputValidator
{
    public abstract String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String action) throws PZDBViolationException;

    public abstract String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String action,String addressValidation);

    public abstract String validateCVVForTokenTransaction(CommonValidatorVO commonValidatorVO);
}

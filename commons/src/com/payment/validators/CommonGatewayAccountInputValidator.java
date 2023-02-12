package com.payment.validators;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by supriya on 5/11/16.
 */
public class CommonGatewayAccountInputValidator extends AbstractGatewayAccountInputValidator
{
    public String gatewaySpecificAccountValidation(HttpServletRequest request)
    {
        return null;
    }
}

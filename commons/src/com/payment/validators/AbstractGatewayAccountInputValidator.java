package com.payment.validators;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 7/25/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractGatewayAccountInputValidator
{
    public abstract String gatewaySpecificAccountValidation(HttpServletRequest request);
}

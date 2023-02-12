package com.payment.validators.vo;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardKitValidatorVO extends CommonValidatorVO
{
    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    private String errorMsg;
}

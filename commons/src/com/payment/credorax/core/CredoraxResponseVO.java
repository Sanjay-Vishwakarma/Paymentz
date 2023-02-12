package com.payment.credorax.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 5/13/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class CredoraxResponseVO extends CommResponseVO
{

    private String responseAuthCode;

    public String getResponseAuthCode()
    {
        return responseAuthCode;
    }

    public void setResponseAuthCode(String responseAuthCode)
    {
        this.responseAuthCode = responseAuthCode;
    }

}

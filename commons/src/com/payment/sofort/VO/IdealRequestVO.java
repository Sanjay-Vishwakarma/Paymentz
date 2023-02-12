package com.payment.sofort.VO;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 22/2/15
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class IdealRequestVO extends CommRequestVO
{
    private String senderBankCode;

    public String getSenderBankCode()
    {
        return senderBankCode;
    }

    public void setSenderBankCode(String senderBankCode)
    {
        this.senderBankCode = senderBankCode;
    }
}

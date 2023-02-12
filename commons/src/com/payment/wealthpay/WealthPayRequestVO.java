package com.payment.wealthpay;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Admin on 6/28/2022.
 */
public class WealthPayRequestVO extends CommRequestVO
{

    private String branchName;
    private String bankCode;

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }
}

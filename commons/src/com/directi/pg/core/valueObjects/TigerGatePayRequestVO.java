package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 9/20/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class TigerGatePayRequestVO extends CommRequestVO
{
    private  String bank_Name;
    private String branch_Name;
    private String branch_Code;
    private String bank_Code;
    private String account_Type;


    public String getBank_Name()
    {
        return bank_Name;
    }

    public void setBank_Name(String bank_Name)
    {
        this.bank_Name = bank_Name;
    }

    public String getBranch_Name()
    {
        return branch_Name;
    }

    public void setBranch_Name(String branch_Name)
    {
        this.branch_Name = branch_Name;
    }

    public String getBranch_Code()
    {
        return branch_Code;
    }

    public void setBranch_Code(String branch_Code)
    {
        this.branch_Code = branch_Code;
    }

    public String getBank_Code()
    {
        return bank_Code;
    }

    public void setBank_Code(String bank_Code)
    {
        this.bank_Code = bank_Code;
    }

    public String getAccount_Type()
    {
        return account_Type;
    }

    public void setAccount_Type(String account_Type)
    {
        this.account_Type = account_Type;
    }
}

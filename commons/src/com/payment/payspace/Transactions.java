package com.payment.payspace;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Admin on 8/3/17.
 */
public class Transactions extends CommResponseVO
{
    private String date;
    private String type;
    private String status;
    private String amount;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String getAmount()
    {
        return amount;
    }

    @Override
    public void setAmount(String amount)
    {
        this.amount = amount;
    }
}

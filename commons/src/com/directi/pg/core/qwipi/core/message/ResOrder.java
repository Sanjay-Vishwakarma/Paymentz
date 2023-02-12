package com.directi.pg.core.qwipi.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/8/12
 * Time: 2:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResOrder
{
    private String id;
    private String status;
    private String billNo;
    private String amount;
    private String date;
    private String currency;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }
}

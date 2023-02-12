package com.directi.pg.core.qwipi.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/8/12
 * Time: 2:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Refund
{
    private String code;
    private String text;
    private String amount;
    private String date;
    private String remark;
    private String message;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

package com.payment.nestpay;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Admin on 7/9/18.
 */
public class NestPayRequestVO extends CommRequestVO
{

    private String eci;
    private String xid;
    private String cavv;
    private String number;
    private String transType="";

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }

    public String getCavv()
    {
        return cavv;
    }

    public void setCavv(String cavv)
    {
        this.cavv = cavv;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getTransType()
    {
        return transType;
    }

    public void setTransType(String transType)
    {
        this.transType = transType;
    }
}

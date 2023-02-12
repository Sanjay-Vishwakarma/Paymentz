package com.payment.request;

/**
 * Created by 123 on 7/9/2016.
 */
public class PZTC40Record
{
    private String orderno;
    private String cardid;
    private String trxno;
    private String rrn;

    public String getOrderno()
    {
        return orderno;
    }

    public void setOrderno(String orderno)
    {
        this.orderno = orderno;
    }

    public String getCardid()
    {
        return cardid;
    }

    public void setCardid(String cardid)
    {
        this.cardid = cardid;
    }

    public String getTrxno()
    {
        return trxno;
    }

    public void setTrxno(String trxno)
    {
        this.trxno = trxno;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }
}

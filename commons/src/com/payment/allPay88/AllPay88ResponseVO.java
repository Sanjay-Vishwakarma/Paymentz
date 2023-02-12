package com.payment.allPay88;


import com.payment.common.core.CommResponseVO;


/**
 * Created by admin on 12-12-2018.
 */
public class AllPay88ResponseVO extends CommResponseVO
{
    private String bankflag;
    private String cardnumber;
    private String cardname;
    private String location;

    public String getBankflag()
    {
        return bankflag;
    }

    public void setBankflag(String bankflag)
    {
        this.bankflag = bankflag;
    }

    public String getCardnumber()
    {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber)
    {
        this.cardnumber = cardnumber;
    }

    public String getCardname()
    {
        return cardname;
    }

    public void setCardname(String cardname)
    {
        this.cardname = cardname;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}

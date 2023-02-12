package com.payment.websecpay.core;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 8/8/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class WSecResponseVO extends CommResponseVO
{

    private String ipCountry;

    private String cardCountry;


    public String getIpCountry()
    {
        return ipCountry;
    }

    public void setIpCountry(String ipCountry)
    {
        this.ipCountry = ipCountry;
    }

    public String getCardCountry()
    {
        return cardCountry;
    }

    public void setCardCountry(String cardCountry)
    {
        this.cardCountry = cardCountry;
    }
}

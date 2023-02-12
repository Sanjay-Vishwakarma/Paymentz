package com.manager.vo.merchantmonitoring;

/**
 * Created by supriya on 3/11/2016.
 */
public class CardTypeAmountVO
{
    double MCAmount;
    double VISAAmount;
    double AMEXAmount;
    double DINRESAmount;

    public double getDINRESAmount()
    {
        return DINRESAmount;
    }

    public void setDINRESAmount(double DINRESAmount)
    {
        this.DINRESAmount = DINRESAmount;
    }

    public double getAMEXAmount()
    {
        return AMEXAmount;
    }

    public void setAMEXAmount(double AMEXAmount)
    {
        this.AMEXAmount = AMEXAmount;
    }

    public double getMCAmount()
    {
        return MCAmount;
    }

    public void setMCAmount(double MCAmount)
    {
        this.MCAmount = MCAmount;
    }

    public double getVISAAmount()
    {
        return VISAAmount;
    }

    public void setVISAAmount(double VISAAmount)
    {
        this.VISAAmount = VISAAmount;
    }
}

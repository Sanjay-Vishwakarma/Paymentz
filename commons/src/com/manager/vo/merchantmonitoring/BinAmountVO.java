package com.manager.vo.merchantmonitoring;

/**
 * Created by admin on 4/19/2016.
 */
public class BinAmountVO
{
    String binString;
    String lastFourString;
    double binAmount ;
    int count;

    public String getBinString()
    {
        return binString;
    }
    public void setBinString(String binString)
    {
        this.binString = binString;
    }
    public double getBinAmount()
    {
        return binAmount;
    }
    public void setBinAmount(double binAmount)
    {
        this.binAmount = binAmount;
    }
    public int getCount()
    {
        return count;
    }
    public void setCount(int count)
    {
        this.count = count;
    }

    public String getLastFourString()
    {
        return lastFourString;
    }

    public void setLastFourString(String lastFourString)
    {
        this.lastFourString = lastFourString;
    }
}

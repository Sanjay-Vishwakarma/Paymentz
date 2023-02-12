package com.manager.vo;

/**
 * Created by Trupti on 3/9/2018.
 */
public class CardDetailsVO
{
    String firstsix;
    String lastfour;
    String isTemp;

    public String getLastfour()
    {
        return lastfour;
    }

    public void setLastfour(String lastfour)
    {
        this.lastfour = lastfour;
    }

    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getIsTemp()
    {
        return isTemp;
    }

    public void setIsTemp(String isTemp)
    {
        this.isTemp = isTemp;
    }
}

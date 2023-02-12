package com.payment.endeavourmpi;

/**
 * Created by SurajT on 11/13/2017.
 */
public class ParesDecodeRequestVO
{
    public String massageID;
    public String pares;
    public String trackid;
    public String cRes;
    public String mid;
    public boolean isTestRequest;

    public String getMassageID()
    {
        return massageID;
    }

    public void setMassageID(String massageID)
    {
        this.massageID = massageID;
    }

    public String getPares()
    {
        return pares;
    }

    public void setPares(String pares)
    {
        this.pares = pares;
    }

    public String getTrackid()
    {
        return trackid;
    }

    public void setTrackid(String trackid)
    {
        this.trackid = trackid;
    }

    public boolean isTestRequest()
    {
        return isTestRequest;
    }

    public void setTestRequest(boolean isTestRequest)
    {
        this.isTestRequest = isTestRequest;
    }

    public String getcRes()
    {
        return cRes;
    }

    public void setcRes(String cRes)
    {
        this.cRes = cRes;
    }

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }
}

package com.directi.pg.core.valueObjects;

import com.directi.pg.core.valueObjects.GenericResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 19/2/13
 * Time: 1:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoResponseVO extends GenericResponseVO
{
    String error;
    String trackingid;
    String wctxnid;
    String status;
    String redirecturl;
    String transactionDate;
    String sourceID;
    String destID;
    String responseRemark;

    public String getResponseRemark()
    {
        return responseRemark;
    }

    public void setResponseRemark(String responseRemark)
    {
        this.responseRemark = responseRemark;
    }



    //getter setter


    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getSourceID()
    {
        return sourceID;
    }

    public void setSourceID(String sourceID)
    {
        this.sourceID = sourceID;
    }

    public String getDestID()
    {
        return destID;
    }

    public void setDestID(String destID)
    {
        this.destID = destID;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getWctxnid()
    {
        return wctxnid;
    }

    public void setWctxnid(String wctxnid)
    {
        this.wctxnid = wctxnid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRedirecturl()
    {
        return redirecturl;
    }

    public void setRedirecturl(String redirecturl)
    {
        this.redirecturl = redirecturl;
    }

    @Override
    public String toString()
    {
        return "MyMonederoResponseVO{" +
                "error='" + error + '\'' +
                ", trackingid='" + trackingid + '\'' +
                ", wctxnid='" + wctxnid + '\'' +
                ", status='" + status + '\'' +
                ", redirecturl='" + redirecturl + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", sourceID='" + sourceID + '\'' +
                ", destID='" + destID + '\'' +
                '}';
    }
}

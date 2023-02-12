package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 21, 2012
 * Time: 9:27:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarVBVDetailsVO  extends GenericVO
{
    private boolean vbvTransaction;
    private String vbvTransECI;
    private String vbvCHReturnCode;
    private String vbvPAReturnCode;
    private String vbvTransTime;
    private String vbvTransAuth;
    private String vbvCAVVAlgo;
    private String vbvXID;
    private String vbvMerchantID;
    private String vbvAcquirerBin;
    private String vbvTransStatus;


    public boolean isVbvTransaction()
    {
        return vbvTransaction;
    }

    public void setVbvTransaction(boolean vbvTransaction)
    {
        this.vbvTransaction = vbvTransaction;
    }

    public String getVbvTransECI()
    {
        return vbvTransECI;
    }

    public void setVbvTransECI(String vbvTransECI)
    {
        this.vbvTransECI = vbvTransECI;
    }

    public String getVbvCHReturnCode()
    {
        return vbvCHReturnCode;
    }

    public void setVbvCHReturnCode(String vbvCHReturnCode)
    {
        this.vbvCHReturnCode = vbvCHReturnCode;
    }

    public String getVbvPAReturnCode()
    {
        return vbvPAReturnCode;
    }

    public void setVbvPAReturnCode(String vbvPAReturnCode)
    {
        this.vbvPAReturnCode = vbvPAReturnCode;
    }

    public String getVbvTransTime()
    {
        return vbvTransTime;
    }

    public void setVbvTransTime(String vbvTransTime)
    {
        this.vbvTransTime = vbvTransTime;
    }

    public String getVbvTransAuth()
    {
        return vbvTransAuth;
    }

    public void setVbvTransAuth(String vbvTransAuth)
    {
        this.vbvTransAuth = vbvTransAuth;
    }

    public String getVbvCAVVAlgo()
    {
        return vbvCAVVAlgo;
    }

    public void setVbvCAVVAlgo(String vbvCAVVAlgo)
    {
        this.vbvCAVVAlgo = vbvCAVVAlgo;
    }

    public String getVbvXID()
    {
        return vbvXID;
    }

    public void setVbvXID(String vbvXID)
    {
        this.vbvXID = vbvXID;
    }

    public String getVbvMerchantID()
    {
        return vbvMerchantID;
    }

    public void setVbvMerchantID(String vbvMerchantID)
    {
        this.vbvMerchantID = vbvMerchantID;
    }

    public String getVbvAcquirerBin()
    {
        return vbvAcquirerBin;
    }

    public void setVbvAcquirerBin(String vbvAcquirerBin)
    {
        this.vbvAcquirerBin = vbvAcquirerBin;
    }

    public String getVbvTransStatus()
    {
        return vbvTransStatus;
    }

    public void setVbvTransStatus(String vbvTransStatus)
    {
        this.vbvTransStatus = vbvTransStatus;
    }

}

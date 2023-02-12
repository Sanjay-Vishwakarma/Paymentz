package com.payment.ems.core;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created by Admin on 1/26/2018.
 */
public class EMSResponseVO extends Comm3DResponseVO
{
    private String avsResponse;
    private String brand;
    private String serviceProvider;
    private String paymentType;
    private String pApproveCode;
    private String pCCVResponse;
    private String pRefNumber;
    private String pRespCode;
    private String tDate;
    private String tDateFormat;
    private String tTime;
    private String successfully;
    //private String pRespCode;


    public String getAvsResponse()
    {
        return avsResponse;
    }

    public void setAvsResponse(String avsResponse)
    {
        this.avsResponse = avsResponse;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getServiceProvider()
    {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider)
    {
        this.serviceProvider = serviceProvider;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getpApproveCode()
    {
        return pApproveCode;
    }

    public void setpApproveCode(String pApproveCode)
    {
        this.pApproveCode = pApproveCode;
    }

    public String getpCCVResponse()
    {
        return pCCVResponse;
    }

    public void setpCCVResponse(String pCCVResponse)
    {
        this.pCCVResponse = pCCVResponse;
    }

    public String getpRefNumber()
    {
        return pRefNumber;
    }

    public void setpRefNumber(String pRefNumber)
    {
        this.pRefNumber = pRefNumber;
    }

    public String getpRespCode()
    {
        return pRespCode;
    }

    public void setpRespCode(String pRespCode)
    {
        this.pRespCode = pRespCode;
    }

    public String gettDate()
    {
        return tDate;
    }

    public void settDate(String tDate)
    {
        this.tDate = tDate;
    }

    public String gettDateFormat()
    {
        return tDateFormat;
    }

    public void settDateFormat(String tDateFormat)
    {
        this.tDateFormat = tDateFormat;
    }

    public String gettTime()
    {
        return tTime;
    }

    public void settTime(String tTime)
    {
        this.tTime = tTime;
    }

    public String getSuccessfully()
    {
        return successfully;
    }

    public void setSuccessfully(String successfully)
    {
        this.successfully = successfully;
    }
}

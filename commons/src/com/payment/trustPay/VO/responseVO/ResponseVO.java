package com.payment.trustPay.VO.responseVO;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Sneha on 11/7/2016.
 */
public class ResponseVO extends CommResponseVO
{
    String id;
    String paymentType;
    String paymentBrand;
    String amount;
    Result result;
    ResultDetails resultDetails;
    String buildNumber;
    String timestamp;
    String ndc;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public ResultDetails getResultDetails()
    {
        return resultDetails;
    }

    public void setResultDetails(ResultDetails resultDetails)
    {
        this.resultDetails = resultDetails;
    }

    public String getBuildNumber()
    {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber)
    {
        this.buildNumber = buildNumber;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getNdc()
    {
        return ndc;
    }

    public void setNdc(String ndc)
    {
        this.ndc = ndc;
    }
}

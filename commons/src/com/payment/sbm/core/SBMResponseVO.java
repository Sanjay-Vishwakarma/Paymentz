package com.payment.sbm.core;

import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/12/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class SBMResponseVO extends Comm3DResponseVO
{
    String isCvvValid;
    String eci;
    String depositAmount;
    String referenceNumber;
    String paymentAuthCode;
    String processingAuthCode;
    String acsURL;
    String acsRequest;
    String postDate;

    //mutator


    public String getCvvValid()
    {
        return isCvvValid;
    }

    public void setCvvValid(String cvvValid)
    {
        isCvvValid = cvvValid;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getDepositAmount()
    {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount)
    {
        this.depositAmount = depositAmount;
    }

    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    public String getPaymentAuthCode()
    {
        return paymentAuthCode;
    }

    public void setPaymentAuthCode(String paymentAuthCode)
    {
        this.paymentAuthCode = paymentAuthCode;
    }

    public String getProcessingAuthCode()
    {
        return processingAuthCode;
    }

    public void setProcessingAuthCode(String processingAuthCode)
    {
        this.processingAuthCode = processingAuthCode;
    }

    public String getAcsURL()
    {
        return acsURL;
    }

    public void setAcsURL(String acsURL)
    {
        this.acsURL = acsURL;
    }

    public String getAcsRequest()
    {
        return acsRequest;
    }

    public void setAcsRequest(String acsRequest)
    {
        this.acsRequest = acsRequest;
    }

    public String getPostDate()
    {
        return postDate;
    }

    public void setPostDate(String postDate)
    {
        this.postDate = postDate;
    }
}

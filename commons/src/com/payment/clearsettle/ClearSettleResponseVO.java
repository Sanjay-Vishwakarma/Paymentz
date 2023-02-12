package com.payment.clearsettle;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Sneha on 1/16/2017.
 */
public class ClearSettleResponseVO extends CommResponseVO
{
    private String referenceNo;
    private String is3d;
    private String only3d;
    private String date;
    private String code;
    private String message;
    private String operation;
    private String type;
    private String status;
    private String isLive;
    private String descriptor;
    private String customerIp;
    private String customerUserAgent;
    private String returnUrl;
    private String form3d;
    private String purchaseUrl;



    public String getReferenceNo()
    {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo)
    {
        this.referenceNo = referenceNo;
    }

    public String getIs3d()
    {
        return is3d;
    }

    public void setIs3d(String is3d)
    {
        this.is3d = is3d;
    }

    public String getOnly3d()
    {
        return only3d;
    }

    public void setOnly3d(String only3d)
    {
        this.only3d = only3d;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIsLive()
    {
        return isLive;
    }

    public void setIsLive(String isLive)
    {
        this.isLive = isLive;
    }

    @Override
    public String getDescriptor()
    {
        return descriptor;
    }

    @Override
    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getCustomerIp()
    {
        return customerIp;
    }

    public void setCustomerIp(String customerIp)
    {
        this.customerIp = customerIp;
    }

    public String getCustomerUserAgent()
    {
        return customerUserAgent;
    }

    public void setCustomerUserAgent(String customerUserAgent)
    {
        this.customerUserAgent = customerUserAgent;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl)
    {
        this.returnUrl = returnUrl;
    }

    public String getForm3d()
    {
        return form3d;
    }

    public void setForm3d(String form3d)
    {
        this.form3d = form3d;
    }

    public String getPurchaseUrl()
    {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl)
    {
        this.purchaseUrl = purchaseUrl;
    }
}

package com.manager.vo;

/**
 * Created by Jinesh on 1/18/2016.
 */
public class ManualRebillResponseVO
{
    String trackingId;
    String description;
    String amount;
    String status;
    String errorMessage;
    String key;
    String checksumAlgo;
    String billingDescriptor;
    String checksum;

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getChecksumAlgo()
    {
        return checksumAlgo;
    }

    public void setChecksumAlgo(String checksumAlgo)
    {
        this.checksumAlgo = checksumAlgo;
    }

    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }

    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }
}

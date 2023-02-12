package com.vo.applicationManagerVOs;

/**
 * Created by Sneha on 6/7/2017.
 */
public class BankTypeVO
{
    String bankId;
    int count;
    String bankName;
    String fileName;
    String partnerId;
    String partnerName;
    boolean mappedForApplication;
    boolean defaultApplication;

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public boolean isMappedForApplication()
    {
        return mappedForApplication;
    }

    public void setMappedForApplication(boolean mappedForApplication)
    {
        this.mappedForApplication = mappedForApplication;
    }

    public boolean isDefaultApplication()
    {
        return defaultApplication;
    }

    public void setDefaultApplication(boolean defaultApplication)
    {
        this.defaultApplication = defaultApplication;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getBankId()
    {
        return bankId;
    }

    public void setBankId(String bankId)
    {
        this.bankId = bankId;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}

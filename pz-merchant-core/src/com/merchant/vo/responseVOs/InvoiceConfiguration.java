package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="invoiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceConfiguration
{
    @XmlElement(name="invoiceMerchantDetails")
    private String invoiceMerchantDetails;

    @XmlElement(name="isIPwhitelistForInvoice")
    private String isIPwhitelistForInvoice;

    public String getInvoiceMerchantDetails()
    {
        return invoiceMerchantDetails;
    }

    public void setInvoiceMerchantDetails(String invoiceMerchantDetails)
    {
        this.invoiceMerchantDetails = invoiceMerchantDetails;
    }

    public String getIsIPwhitelistForInvoice()
    {
        return isIPwhitelistForInvoice;
    }

    public void setIsIPwhitelistForInvoice(String isIPwhitelistForInvoice)
    {
        this.isIPwhitelistForInvoice = isIPwhitelistForInvoice;
    }

    @Override
    public String toString()
    {
        return "InvoiceConfiguration{" +
                "invoiceMerchantDetails='" + invoiceMerchantDetails + '\'' +
                ", isIPwhitelistForInvoice='" + isIPwhitelistForInvoice + '\'' +
                '}';
    }
}

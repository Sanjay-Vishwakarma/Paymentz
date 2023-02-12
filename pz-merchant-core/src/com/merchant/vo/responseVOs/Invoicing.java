package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="invoicing")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoicing
{
    @XmlElement(name="generateInvoice")
    private String generateInvoice;

    @XmlElement(name="invoiceHistory")
    private String invoiceHistory;

    @XmlElement(name="invoiceConfiguration")
    private String invoiceConfiguration;

    public String getGenerateInvoice()
    {
        return generateInvoice;
    }

    public void setGenerateInvoice(String generateInvoice)
    {
        this.generateInvoice = generateInvoice;
    }

    public String getInvoiceHistory()
    {
        return invoiceHistory;
    }

    public void setInvoiceHistory(String invoiceHistory)
    {
        this.invoiceHistory = invoiceHistory;
    }

    public String getInvoiceConfiguration()
    {
        return invoiceConfiguration;
    }

    public void setInvoiceConfiguration(String invoiceConfiguration)
    {
        this.invoiceConfiguration = invoiceConfiguration;
    }

    @Override
    public String toString()
    {
        return "Invoicing{" +
                "generateInvoice='" + generateInvoice + '\'' +
                ", invoiceHistory='" + invoiceHistory + '\'' +
                ", invoiceConfiguration='" + invoiceConfiguration + '\'' +
                '}';
    }
}

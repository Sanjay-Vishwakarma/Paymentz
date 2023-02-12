package com.invoice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 6/22/2017.
 */

@XmlRootElement(name="customerDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDetailList
{

    @XmlElement(name = "customerName")
    String customerName;

    @XmlElement(name="customerEmail")
    String customerEmail;

    @XmlElement(name="customerPhoneCC")
    String customerPhoneCC;

    @XmlElement(name="customerPhone")
    String customerPhone;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="transactionUrl")
    String transactionUrl;

    @XmlElement(name="QR_Code")
    String QR_Code;;

    @XmlElement(name="systemInvoiceId")
    String invoicenumber;

    @XmlElement(name="merchantInvoiceId")
    String merchantInvoiceId;

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail)
    {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneCC()
    {
        return customerPhoneCC;
    }

    public void setCustomerPhoneCC(String customerPhoneCC)
    {
        this.customerPhoneCC = customerPhoneCC;
    }

    public String getCustomerPhone()
    {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone)
    {
        this.customerPhone = customerPhone;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getTransactionUrl()
    {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl)
    {
        this.transactionUrl = transactionUrl;
    }

    public String getInvoicenumber()
    {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber)
    {
        this.invoicenumber = invoicenumber;
    }

    public String getMerchantInvoiceId()
    {
        return merchantInvoiceId;
    }

    public void setMerchantInvoiceId(String merchantInvoiceId)
    {
        this.merchantInvoiceId = merchantInvoiceId;
    }

    public String getQR_Code()
    {
        return QR_Code;
    }

    public void setQR_Code(String QR_Code)
    {
        this.QR_Code = QR_Code;
    }
}

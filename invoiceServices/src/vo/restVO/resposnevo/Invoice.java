package vo.restVO.resposnevo;

import com.invoice.vo.CustomerDetailList;
import com.invoice.vo.ProductList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Sneha on 2/6/2017.
 */
@XmlRootElement(name="invoiceDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice
{
    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="refundAmount")
    String refundAmount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name = "expirationPeriod")
    String expirationPeriod;

    @XmlElement(name = "createdDate")
    String date;

    @XmlElement(name = "createdTime")
    String time;

    @XmlElement(name = "merchantInvoiceId")
    String merchantInvoiceId;

    @XmlElement(name = "customerEmail")
    String emailaddr;

    @XmlElement(name = "customerName")
    String customerName;

    @XmlElement(name="merchantOrderDescription")
    String merchantOrderDescription;

    @XmlElement(name="merchantOrderId")
    String merchantOrderId;

    @XmlElement(name="systemInvoiceId")
    String invoiceId;

    @XmlElement(name="transactionUrl")
    String transactionUrl;

    @XmlElement(name="invoiceStatus")
    String invoiceStatus;

    @XmlElement(name="transactionStatus")
    String transactionStatus;

    @XmlElement(name="paymentId")
    String paymentId;

    @XmlElement(name="transactionDetails")
    Transaction transaction;

    @XmlElement(name="customerDetails")
    CustomerDetails customerDetails;

    @XmlElement(name="invoiceDetails")
    InvoiceDetails invoiceDetails;

    @XmlElement(name="raiseby")
    String raiseby;

    @XmlElement(name="orderId")
    String orderId;

    @XmlElement(name="orderDescription")
    String orderDescription;

    @XmlElement(name="trackingid")
    String trackingid;

    @XmlElement(name="dtstamp")
    String dtstamp;

    @XmlElement(name="cancelreason")
    String cancelreason;

    @XmlElement(name="gst")
    String gst;
    @XmlElement(name="intial")
    String intial;

    //new

    @XmlElement(name="issms")
    String issms;

    @XmlElement(name="isemail")
    String isemail;

    @XmlElement(name="isapp")
    String isapp;

    @XmlElement(name="paymentterms")
    String paymentterms;

    @XmlElement(name="duedate")
    String duedate;

    @XmlElement(name="latefee")
    String latefee;

    @XmlElement(name="isduedate")
    String isduedate;

    @XmlElement(name="islatefee")
    String islatefee;

    @XmlElement(name="redirectUrl")
    String redirectUrl;

    @XmlElement(name="unit")
    String unit;

    @XmlElement(name="smsactivation")
    String smsactivation;

    @XmlElement(name="productlist")
    List<ProductList> productlist;

    @XmlElement(name="customerDetailList")
    List<CustomerDetailList> customerDetailList;

    @XmlElement(name="QR_Code")
    String QR_Code;

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getRaiseby()
    {
        return raiseby;
    }

    public void setRaiseby(String raiseby)
    {
        this.raiseby = raiseby;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;

    }

    public InvoiceDetails getInvoiceDetails()
    {
        return invoiceDetails;
    }

    public void setInvoiceDetails(InvoiceDetails invoiceDetails)
    {
        this.invoiceDetails = invoiceDetails;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getInvoiceStatus()
    {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus)
    {
        this.invoiceStatus = invoiceStatus;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionUrl()
    {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl)
    {
        this.transactionUrl = transactionUrl;
    }

    public String getExpirationPeriod()
    {
        return expirationPeriod;
    }

    public void setExpirationPeriod(String expirationPeriod)
    {
        this.expirationPeriod = expirationPeriod;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getMerchantInvoiceId()
    {
        return merchantInvoiceId;
    }

    public void setMerchantInvoiceId(String merchantTransactionId)
    {
        this.merchantInvoiceId = merchantTransactionId;
    }

    public String getEmailaddr()
    {
        return emailaddr;
    }

    public void setEmailaddr(String emailaddr)
    {
        this.emailaddr = emailaddr;
    }

    public String getMerchantOrderDescription()
    {
        return merchantOrderDescription;
    }

    public void setMerchantOrderDescription(String merchantOrderDescription)
    {
        this.merchantOrderDescription = merchantOrderDescription;
    }

    public String getMerchantOrderId()
    {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId)
    {
        this.merchantOrderId = merchantOrderId;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }


    public String getCustomerName()
    {
        return customerName;
    }

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCancelreason()
    {
        return cancelreason;
    }

    public void setCancelreason(String cancelreason)
    {
        this.cancelreason = cancelreason;
    }

    public CustomerDetails getCustomerDetails()
    {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetails customerDetails)
    {
        this.customerDetails = customerDetails;
    }

    public String getGst()
    {
        return gst;
    }

    public void setGst(String gst)
    {
        this.gst = gst;
    }

    public String getIntial()
    {
        return intial;
    }

    public void setIntial(String intial)
    {
        this.intial = intial;
    }


    public String getIssms()
    {
        return issms;
    }

    public void setIssms(String issms)
    {
        this.issms = issms;
    }

    public String getIsemail()
    {
        return isemail;
    }

    public void setIsemail(String isemail)
    {
        this.isemail = isemail;
    }

    public String getIsapp()
    {
        return isapp;
    }

    public void setIsapp(String isapp)
    {
        this.isapp = isapp;
    }

    public String getPaymentterms()
    {
        return paymentterms;
    }

    public void setPaymentterms(String paymentterms)
    {
        this.paymentterms = paymentterms;
    }

    public String getDuedate()
    {
        return duedate;
    }

    public void setDuedate(String duedate)
    {
        this.duedate = duedate;
    }

    public String getLatefee()
    {
        return latefee;
    }

    public void setLatefee(String latefee)
    {
        this.latefee = latefee;
    }

    public String getIsduedate()
    {
        return isduedate;
    }

    public void setIsduedate(String isduedate)
    {
        this.isduedate = isduedate;
    }

    public String getIslatefee()
    {
        return islatefee;
    }

    public void setIslatefee(String islatefee)
    {
        this.islatefee = islatefee;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getSmsactivation()
    {
        return smsactivation;
    }

    public void setSmsactivation(String smsactivation)
    {
        this.smsactivation = smsactivation;
    }

    public List<ProductList> getProductlist()
    {
        return productlist;
    }

    public void setProductlist(List<ProductList> productlist)
    {
        this.productlist = productlist;
    }

    public List<CustomerDetailList> getCustomerDetailList()
    {
        return customerDetailList;
    }

    public void setCustomerDetailList(List<CustomerDetailList> customerDetailList)
    {
        this.customerDetailList = customerDetailList;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
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

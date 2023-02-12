package vo.restVO.resposnevo;

import com.invoice.vo.DefaultProductList;
import com.invoice.vo.UnitList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response
{
    @XmlElement(name="paymentId")
    String paymentId;

    @XmlElement(name="status")
    String status;

    @XmlElement(name="registrationId")
    String registrationId;

    @XmlElement(name="partnerId")
    String partnerId;

    @XmlElement(name="memberId")
    String memberId;

    @XmlElement(name="holderId")
    String holderId;

    @XmlElement(name="paymentType")
    String paymentType;

    @XmlElement(name="paymentBrand")
    String paymentBrand;

    @XmlElement(name="paymentMode")
    String paymentMode;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement (name="timestamp")
    String timestamp;

    @XmlElement(name = "token")
    String token;

    @XmlElement (name="statusInvoiceResult")
    StatusInvoiceResult statusInvoiceResult;

    @XmlElement(name="invoiceDetails")
    Invoice invoice;

    @XmlElement(name = "merchantTransactionId")
    String merchantTransactionId;

    @XmlElement(name = "customerId")
    String customerId;

    @XmlElement(name = "customerEmail")
    String emailaddr;

    @XmlElement(name = "date")
    String date;

    @XmlElement(name = "time")
    String time;

    @XmlElement(name = "invoiceExpirationPeriod")
    String invoiceExpirationPeriod;

    @XmlElement (name = "invoiceList")
    List<Invoice> invoiceList;

    @XmlElement (name = "defaultProductList")
    List<DefaultProductList> defaultProductList;

    @XmlElement (name = "defaultUnitList")
    List<UnitList> defaultUnitList;

    @XmlElement(name = "isSplitInvoice")
    String isSplitInvoice;

    public StatusInvoiceResult getStatusInvoiceResult()
    {
        return statusInvoiceResult;
    }

    public void setStatusInvoiceResult(StatusInvoiceResult statusInvoiceResult)
    {
        this.statusInvoiceResult = statusInvoiceResult;
    }

    public Invoice getInvoice()
    {
        return invoice;
    }

    public void setInvoice(Invoice invoice)
    {
        this.invoice = invoice;
    }

    public String getInvoiceExpirationPeriod()
    {
        return invoiceExpirationPeriod;
    }

    public void setInvoiceExpirationPeriod(String invoiceExpirationPeriod)
    {
        this.invoiceExpirationPeriod = invoiceExpirationPeriod;
    }

    public String getEmailaddr()
    {
        return emailaddr;
    }

    public void setEmailaddr(String emailaddr)
    {
        this.emailaddr = emailaddr;
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

    @XmlElement(name = "invoiceId")
    String invoiceId;

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getHolderId()
    {
        return holderId;
    }

    public void setHolderId(String holderId)
    {
        this.holderId = holderId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }


    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
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

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getRegistrationId()
    {
        return registrationId;
    }

    public List<Invoice> getInvoiceList()
    {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList)
    {
        this.invoiceList = invoiceList;
    }

    public void setRegistrationId(String registrationId)
    {
        this.registrationId = registrationId;
    }



    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public List<DefaultProductList> getDefaultProductList()
    {
        return defaultProductList;
    }

    public void setDefaultProductList(List<DefaultProductList> defaultProductList)
    {
        this.defaultProductList = defaultProductList;
    }

    public List<UnitList> getDefaultUnitList()
    {
        return defaultUnitList;
    }

    public void setDefaultUnitList(List<UnitList> defaultUnitList)
    {
        this.defaultUnitList = defaultUnitList;
    }

    public String getIsSplitInvoice()
    {
        return isSplitInvoice;
    }

    public void setIsSplitInvoice(String isSplitInvoice)
    {
        this.isSplitInvoice = isSplitInvoice;
    }
}
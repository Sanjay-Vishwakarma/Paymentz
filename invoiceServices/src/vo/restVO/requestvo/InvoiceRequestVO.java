package vo.restVO.requestvo;

import com.invoice.vo.*;
import com.merchant.vo.requestVOs.AuthenticationVO;
import com.merchant.vo.requestVOs.BillingAddressVO;
import com.merchant.vo.requestVOs.CustomerVO;
import com.merchant.vo.requestVOs.MerchantVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by admin on 10-07-2017.
 */
@XmlRootElement(name="product")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceRequestVO
{


    @XmlElement(name="id")
    String id;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="currency")
    String currency;


    @XmlElement(name="actionType")
    String actionType;


    @XmlElement(name="paymentBrand")
    String paymentBrand;


    @XmlElement(name="paymentMode")
    String paymentMode;


    @XmlElement(name="paymentType")
    String paymentType;


    @XmlElement(name="merchantOrderDescription")
    String merchantOrderDescription;


    @XmlElement(name="merchantTransactionId")
    String merchantTransactionId;


    @XmlElement(name="merchantInvoiceId")
    String merchantInvoiceId;

    @XmlElement(name="merchantOrderId")
    String merchantOrderId;


    @XmlElement(name="invoiceId")
    String invoiceId;


    @XmlElement(name="invoiceExpirationPeriod")
    String invoiceExpirationPeriod;


    @XmlElement(name="createRegistration")
    String createRegistration;


    @XmlElement(name="recurringType")
    String recurringType;


    @XmlElement(name="merchantRedirectUrl")
    String merchantRedirectUrl;


    @XmlElement(name="notificationUrl")
    String notificationUrl;


    @XmlElement(name="responseType")
    String responseType;


    @XmlElement(name="cancelReason")
    String cancelReason;


    @XmlElement(name="customParameters")
    String[] customParameters;


    @XmlElement(name="product")
    List<ProductList> productList;


    @XmlElement(name="invoiceAction")
    String[] invoiceAction;


    @XmlElement(name="authentication")
    AuthenticationVO authentication;


    @XmlElement(name="customer")
    CustomerVO customer;

    @XmlElement(name = "merchant")
    MerchantVO merchant;

    @XmlElement(name="billingAddress")
    BillingAddressVO billingAddress;


    @XmlElement(name="pagination")
    Pagination pagination;

    @XmlElement(name="initial")
    String initial;


    @XmlElement(name="gst")
    String gst;

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

    @XmlElement(name="unit")
    String unit;

    @XmlElement(name="quantityTotal")
    String quantityTotal;

    @XmlElement(name="defaultUnit")
    List<UnitList> unitList;

    @XmlElement(name="defaultProductList")
    List<DefaultProductList> defaultProductList;

    @XmlElement(name="customerDetail")
    List<CustomerDetailList> customerDetailList;

    public String getId()
    {
        return id;
    }

    @XmlElement(name="isSplitInvoice")
    String isSplitInvoice;

    public void setId(String id)
    {
        this.id = id;
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

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getMerchantOrderDescription()
    {
        return merchantOrderDescription;
    }

    public void setMerchantOrderDescription(String merchantOrderDescription)
    {
        this.merchantOrderDescription = merchantOrderDescription;
    }

    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getMerchantInvoiceId()
    {
        return merchantInvoiceId;
    }

    public void setMerchantInvoiceId(String merchantInvoiceId)
    {
        this.merchantInvoiceId = merchantInvoiceId;
    }

    public String getMerchantOrderId()
    {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId)
    {
        this.merchantOrderId = merchantOrderId;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceExpirationPeriod()
    {
        return invoiceExpirationPeriod;
    }

    public void setInvoiceExpirationPeriod(String invoiceExpirationPeriod)
    {
        this.invoiceExpirationPeriod = invoiceExpirationPeriod;
    }

    public String getCreateRegistration()
    {
        return createRegistration;
    }

    public void setCreateRegistration(String createRegistration)
    {
        this.createRegistration = createRegistration;
    }

    public String getRecurringType()
    {
        return recurringType;
    }

    public void setRecurringType(String recurringType)
    {
        this.recurringType = recurringType;
    }

    public String getMerchantRedirectUrl()
    {
        return merchantRedirectUrl;
    }

    public void setMerchantRedirectUrl(String merchantRedirectUrl)
    {
        this.merchantRedirectUrl = merchantRedirectUrl;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getResponseType()
    {
        return responseType;
    }

    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public String[] getCustomParameters()
    {
        return customParameters;
    }

    public void setCustomParameters(String[] customParameters)
    {
        this.customParameters = customParameters;
    }

    public List<ProductList> getItemList()
    {
        return productList;
    }

    public void setItemList(List<ProductList> itemList)
    {
        this.productList = itemList;
    }

    public String[] getInvoiceAction()
    {
        return invoiceAction;
    }

    public void setInvoiceAction(String[] invoiceAction)
    {
        this.invoiceAction = invoiceAction;
    }

    public AuthenticationVO getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(AuthenticationVO authentication)
    {
        this.authentication = authentication;
    }

    public CustomerVO getCustomer()
    {
        return customer;
    }

    public void setCustomer(CustomerVO customer)
    {
        this.customer = customer;
    }

    public MerchantVO getMerchant()
    {
        return merchant;
    }

    public void setMerchant(MerchantVO merchant)
    {
        this.merchant = merchant;
    }

    public Pagination getPagination()
    {
        return pagination;
    }

    public void setPagination(Pagination pagination)
    {
        this.pagination = pagination;
    }

    public String getInitial()
    {
        return initial;
    }

    public void setInitial(String initial)
    {
        this.initial = initial;
    }

    public String getGst()
    {
        return gst;
    }

    public void setGst(String gst)
    {
        this.gst = gst;
    }


    public BillingAddressVO getBillingAddress()
    {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddressVO billingAddress)
    {
        this.billingAddress = billingAddress;
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

    public List<UnitList> getUnitList()
    {
        return unitList;
    }

    public void setUnitList(List<UnitList> unitList)
    {
        this.unitList = unitList;
    }

    public List<DefaultProductList> getDefaultProductList()
    {
        return defaultProductList;
    }

    public void setDefaultProductList(List<DefaultProductList> defaultProductList)
    {
        this.defaultProductList = defaultProductList;
    }

    public List<CustomerDetailList> getCustomerDetailList()
    {
        return customerDetailList;
    }

    public void setCustomerDetailList(List<CustomerDetailList> customerDetailList)
    {
        this.customerDetailList = customerDetailList;
    }

    public String getIsSplitInvoice()
    {
        return isSplitInvoice;
    }

    public void setIsSplitInvoice(String isSplitInvoice)
    {
        this.isSplitInvoice = isSplitInvoice;
    }

    public String getQuantityTotal()
    {
        return quantityTotal;
    }

    public void setQuantityTotal(String quantityTotal)
    {
        this.quantityTotal = quantityTotal;
    }
}

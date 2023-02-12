package vo.restVO.requestvo;

import com.invoice.vo.*;
import com.merchant.vo.requestVOs.Authentication;
import com.merchant.vo.requestVOs.Customer;
import com.merchant.vo.requestVOs.Merchant;
import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.FormParam;
import java.util.List;

/**
 * Created by Sneha on 6/20/2016.
 */

public class InvoiceRequest
{
    @FormParam("id")
    String id;

    @FormParam("amount")
    String amount;

    @FormParam("currency")
    String currency;

    @FormParam("actionType")
    String actionType;

    @FormParam("paymentBrand")
    String paymentBrand;

    @FormParam("paymentMode")
    String paymentMode;

    @FormParam("paymentType")
    String paymentType;

    @FormParam("merchantOrderDescription")
    String merchantOrderDescription;

    @FormParam("merchantTransactionId")
    String merchantTransactionId;

    @FormParam("merchantInvoiceId")
    String merchantInvoiceId;

    @FormParam("merchantOrderId")
    String merchantOrderId;

    @FormParam("invoiceId")
    String invoiceId;

    @FormParam("invoiceExpirationPeriod")
    String invoiceExpirationPeriod;

    @FormParam("createRegistration")
    String createRegistration;

    @FormParam("recurringType")
    String recurringType;

    @FormParam("merchantRedirectUrl")
    String merchantRedirectUrl;

    @FormParam("notificationUrl")
    String notificationUrl;

    @FormParam("responseType")
    String responseType;

    @FormParam("cancelReason")
    String cancelReason;

    @FormParam("customParameters")
    String[] customParameters;

    @InjectParam("productList")
    List<ProductList> productList;

    @FormParam("invoiceAction")
    String[] invoiceAction;

    @InjectParam("authentication")
    Authentication authentication;

    @InjectParam ("customer")
    Customer customer;

    @InjectParam("merchant")
    Merchant merchant;

    @InjectParam ("billingAddress")
    BillingAddress billingAddress;

    @InjectParam ("pagination")
    Pagination pagination;

    @FormParam ("initial")
    String initial;



    @FormParam ("gst")
    String gst;

    @FormParam("issms")
    String issms;

    @FormParam("isemail")
    String isemail;

    @FormParam("isapp")
    String isapp;

    @FormParam("paymentterms")
    String paymentterms;

    @FormParam("duedate")
    String duedate;

    @FormParam("latefee")
    String latefee;


    @FormParam("isduedate")
    String isduedate;

    @FormParam("islatefee")
    String islatefee;

    @FormParam("unit")
    String unit;

    @FormParam("quantityTotal")
    String quantityTotal;

    @InjectParam("defaultUnitList")
    List<UnitList> unitList;

    @InjectParam("defaultProductList")
    List<DefaultProductList> defaultProductList;

    @InjectParam("customerDetailList")
    List<CustomerDetailList> customerDetailList;

    @FormParam("isSplitInvoice")
    String isSplitInvoice;

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

    public String getResponseType()
    {
        return responseType;
    }

    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String[] getCustomParameters()
    {
        return customParameters;
    }

    public void setCustomParameters(String[] customParameters)
    {
        this.customParameters = customParameters;
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

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public BillingAddress getBillingAddress() {return billingAddress;}

    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress = billingAddress;
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

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public Pagination getPagination()
    {
        return pagination;
    }

    public void setPagination(Pagination pagination)
    {
        this.pagination = pagination;
    }

    public String[] getInvoiceAction()
    {
        return invoiceAction;
    }

    public void setInvoiceAction(String[] invoiceAction)
    {
        this.invoiceAction = invoiceAction;
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

    public List<ProductList> getItemList()
    {
        return productList;
    }

    public void setItemList(List<ProductList> itemList)
    {
        this.productList = itemList;
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

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getIslatefee()
    {
        return islatefee;
    }

    public void setIslatefee(String islatefee)
    {
        this.islatefee = islatefee;
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

    public Merchant
    getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
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
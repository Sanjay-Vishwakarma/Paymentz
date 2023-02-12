package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/20/2020.
 */

@XmlRootElement(name="fraudDefender")
@XmlAccessorType(XmlAccessType.FIELD)
public class FraudDefender
{


    @XmlElement(name="chargebackInfo")
    ChargebackInfo chargebackInfo;

    @XmlElement(name="merchantInfo")
    MerchantInfo merchantInfo;

    @XmlElement(name="productInfo")
    ProductInfo productInfo;

    @XmlElement(name="purchaseInfo")
    PurchaseInfo purchaseInfo;

    @XmlElement(name="billingInfo")
    BillingInfo billingInfo;


    @XmlElement(name="bin_number")
    String bin_number;

    @XmlElement(name="currency_code")
    String currency_code;

    @XmlElement(name="currency_iso_number")
    String currency_iso_number;

    @XmlElement(name="customerInfo")
    QueryCustomer queryCustomer;

    @XmlElement(name="cvv")
    String cvv;

    @XmlElement(name="description")
    String description;

    @XmlElement(name="last_four")
    String last_four;

    @XmlElement(name="message")
    String message;

    @XmlElement(name="order_number")
    String order_number;

    @XmlElement(name="recurring")
    String recurring;


    @XmlElement(name="shippingInfo")
    ShippingInfo shippingInfo;

    @XmlElement(name="refundInfo")
    RefundInfo refundInfo;

    @XmlElement(name="result")
    Result result;



    public ShippingInfo getShippingInfo()
    {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo)
    {
        this.shippingInfo = shippingInfo;
    }

    @XmlElement(name="transaction")
    Transaction transaction;


    public QueryCustomer getQueryCustomer()
    {
        return queryCustomer;
    }

    public void setQueryCustomer(QueryCustomer queryCustomer)
    {
        this.queryCustomer = queryCustomer;
    }





    public ChargebackInfo getChargebackInfo()
    {
        return chargebackInfo;
    }

    public void setChargebackInfo(ChargebackInfo chargebackInfo)
    {
        this.chargebackInfo = chargebackInfo;
    }

    public MerchantInfo getMerchantInfo()
    {
        return merchantInfo;
    }

    public void setMerchantInfo(MerchantInfo merchantInfo)
    {
        this.merchantInfo = merchantInfo;
    }

    public ProductInfo getProductInfo()
    {
        return productInfo;
    }

    public void setProductInfo(ProductInfo productInfo)
    {
        this.productInfo = productInfo;
    }

    public PurchaseInfo getPurchaseInfo()
    {
        return purchaseInfo;
    }

    public void setPurchaseInfo(PurchaseInfo purchaseInfo)
    {
        this.purchaseInfo = purchaseInfo;
    }

    public BillingInfo getBillingInfo()
    {
        return billingInfo;
    }

    public void setBillingInfo(BillingInfo billingInfo)
    {
        this.billingInfo = billingInfo;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }

    public void setTransaction(Transaction transaction)
    {
        this.transaction = transaction;
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public RefundInfo getRefundInfo()
    {
        return refundInfo;
    }

    public void setRefundInfo(RefundInfo refundInfo)
    {
        this.refundInfo = refundInfo;
    }

    public String getBin_number()
    {
        return bin_number;
    }

    public void setBin_number(String bin_number)
    {
        this.bin_number = bin_number;
    }

    public String getCurrency_code()
    {
        return currency_code;
    }

    public void setCurrency_code(String currency_code)
    {
        this.currency_code = currency_code;
    }

    public String getCurrency_iso_number()
    {
        return currency_iso_number;
    }

    public void setCurrency_iso_number(String currency_iso_number)
    {
        this.currency_iso_number = currency_iso_number;
    }

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLast_four()
    {
        return last_four;
    }

    public void setLast_four(String last_four)
    {
        this.last_four = last_four;
    }

    public String getOrder_number()
    {
        return order_number;
    }

    public void setOrder_number(String order_number)
    {
        this.order_number = order_number;
    }

    public String getRecurring()
    {
        return recurring;
    }

    public void setRecurring(String recurring)
    {
        this.recurring = recurring;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

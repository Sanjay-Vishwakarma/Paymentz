package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Pranav on 2/6/2017.
 */
@XmlRootElement(name="transactionDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction
{
    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement(name="systemPaymentId")
    String systemPaymentId;

    @XmlElement(name="merchantTransactionId")
    String merchantTransactionId;

    @XmlElement(name="transactionStatus")
    String transactionStatus;

    @XmlElement(name="count")
    String count;

    @XmlElement(name="captureamount")
    String captureamount;

    @XmlElement(name="refundamount")
    String refundamount;

    @XmlElement(name="chargebackamount")
    String chargebackamount;

    @XmlElement(name="payoutamount")
    String payoutamount;

    @XmlElement(name="date")
    String date;

    @XmlElement(name="transactionDate")
    String transactionDate;

    @XmlElement(name="remark")
    String remark;

    @XmlElement(name="customer")
    Customer customer;

    @XmlElement(name="card")
    Card card;

    @XmlElement(name="chargebackInfo")
    ChargebackInfo chargebackInfo;

    @XmlElement(name="merchantInfo")
    MerchantInfo merchantInfo;

    @XmlElement(name="productInfo")
    ProductInfo productInfo;

    @XmlElement(name="PurchaseInfo purchaseInfo")
    PurchaseInfo purchaseInfo;

    @XmlElement(name="billingInfo")
    BillingInfo billingInfo;

    @XmlElement(name="transactionReceiptImg")
    String transactionReceiptImg;

    @XmlElement(name="bankReferenceId")
    String bankReferenceId;

    @XmlElement(name="terminalid")
    String terminalid;

    @XmlElement(name="paymentBrand")
    String paymentBrand ;

    @XmlElement(name="paymentMode")
    String paymentMode;

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

    public String getSystemPaymentId()
    {
        return systemPaymentId;
    }

    public void setSystemPaymentId(String systemPaymentId)
    {
        this.systemPaymentId = systemPaymentId;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getCount()
    {
        return count;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public String getCaptureamount()
    {
        return captureamount;
    }

    public void setCaptureamount(String captureamount)
    {
        this.captureamount = captureamount;
    }

    public String getRefundamount()
    {
        return refundamount;
    }

    public void setRefundamount(String refundamount)
    {
        this.refundamount = refundamount;
    }

    public String getChargebackamount()
    {
        return chargebackamount;
    }

    public void setChargebackamount(String chargebackamount)
    {
        this.chargebackamount = chargebackamount;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public Card getCard()
    {
        return card;
    }

    public void setCard(Card card)
    {
        this.card = card;
    }

    public String getPayoutamount()
    {
        return payoutamount;
    }

    public void setPayoutamount(String payoutamount)
    {
        this.payoutamount = payoutamount;
    }

    public String getTransactionReceiptImg() {return transactionReceiptImg;}

    public void setTransactionReceiptImg(String transactionReceiptImg) {this.transactionReceiptImg = transactionReceiptImg;}

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

    public BillingInfo getBillingInfo()
    {
        return billingInfo;
    }

    public PurchaseInfo getPurchaseInfo()
    {
        return purchaseInfo;
    }

    public void setPurchaseInfo(PurchaseInfo purchaseInfo)
    {
        this.purchaseInfo = purchaseInfo;
    }

    public void setBillingInfo(BillingInfo billingInfo)
    {
        this.billingInfo = billingInfo;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getBankReferenceId()
    {
        return bankReferenceId;
    }

    public void setBankReferenceId(String bankReferenceId)
    {
        this.bankReferenceId = bankReferenceId;
    }

    public String getTerminalid()
    {
        return terminalid;
    }

    public void setTerminalid(String terminalid)
    {
        this.terminalid = terminalid;
    }


    @Override
    public String toString()
    {
        return "Transaction{" +
                "amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", systemPaymentId='" + systemPaymentId + '\'' +
                ", merchantTransactionId='" + merchantTransactionId + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", count='" + count + '\'' +
                ", captureamount='" + captureamount + '\'' +
                ", refundamount='" + refundamount + '\'' +
                ", chargebackamount='" + chargebackamount + '\'' +
                ", payoutamount='" + payoutamount + '\'' +
                ", date='" + date + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", remark='" + remark + '\'' +
                ", customer=" + customer +
                ", card=" + card +
                ", chargebackInfo=" + chargebackInfo +
                ", merchantInfo=" + merchantInfo +
                ", productInfo=" + productInfo +
                ", purchaseInfo=" + purchaseInfo +
                ", billingInfo=" + billingInfo +
                ", transactionReceiptImg='" + transactionReceiptImg + '\'' +
                ", bankReferenceId='" + bankReferenceId + '\'' +
                ", terminalid='" + terminalid + '\'' +
                '}';
    }
}
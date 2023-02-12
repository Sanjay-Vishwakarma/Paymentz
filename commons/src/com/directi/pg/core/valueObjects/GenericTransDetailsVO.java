package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 9:29:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenericTransDetailsVO  extends GenericVO
{
    String amount;
    String refundAmount;
    String payoutAmount;
    String currency;
    String orderId;
    String orderDesc;
    String responseOrderNumber;
    String paymentType;
    String cardType;
    String responseHashInfo;
    String toId;
    String header;
    String totype;
    String redirectUrl;
    String checksum;
    String fromid;
    String fromtype;
    String billingDiscriptor;
    String notificationUrl;
    String transactionType;
    String walletId;
    String walletAmount;
    String walletCurrency;
    String redirectMethod;
    String paymentid;
    String purchase_identifier;
    String transactionDate;
    String personalAccountNumber;
    String refundCurrency;
    String clientTransactionId;
    String merchant_id;
    String authorization_code;
    String rrn;
    String arn;
    String stan;
    String call_type;
    String cvv;
    String checksumAmount;
    private String emiCount;
    private String eci;
    private String xid;
    private String verificationId;
    private String transactionReceipt;
    private String detailId;
    private String paymentProvider;

    private String transactionmode;

      public GenericTransDetailsVO()
    {

    }

    public GenericTransDetailsVO(String amount, String currency, String orderId, String orderDesc, String paymentType)
    {
        this.amount = amount;
        this.currency = currency;
        this.orderId = orderId;
        this.orderDesc = orderDesc;
        this.paymentType = paymentType;
    }

    public String getCvv()
    {
        return cvv;
    }

    public void setCvv(String cvv)
    {
        this.cvv = cvv;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getResponseHashInfo()
    {
        return responseHashInfo;
    }

    public void setResponseHashInfo(String responseHashInfo)
    {
        this.responseHashInfo = responseHashInfo;
    }

    public String getResponseOrderNumber()
    {
        return responseOrderNumber;
    }

    public void setResponseOrderNumber(String responseOrderNumber)
    {
        this.responseOrderNumber = responseOrderNumber;
    }

    public String getBillingDiscriptor()
    {
        return billingDiscriptor;
    }

    public void setBillingDiscriptor(String billingDiscriptor)
    {
        this.billingDiscriptor = billingDiscriptor;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getTotype()
    {
        return totype;
    }

    public void setTotype(String totype)
    {
        this.totype = totype;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getFromid()
    {
        return fromid;
    }

    public void setFromid(String fromid)
    {
        this.fromid = fromid;
    }

    public String getFromtype()
    {
        return fromtype;
    }

    public void setFromtype(String fromtype)
    {
        this.fromtype = fromtype;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }


    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderDesc()
    {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc)
    {
        this.orderDesc = orderDesc;
    }
    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getToId()
    {
        return toId;
    }

    public void setToId(String toId)
    {
        this.toId = toId;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }

    public String getVerificationId()
    {
        return verificationId;
    }

    public void setVerificationId(String verificationId)
    {
        this.verificationId = verificationId;
    }

    public String getEmiCount()
    {
        return emiCount;
    }

    public void setEmiCount(String emiCount)
    {
        this.emiCount = emiCount;
    }

    public String getWalletId()
    {
        return walletId;
    }

    public void setWalletId(String walletId)
    {
        this.walletId = walletId;
    }

    public String getWalletAmount()
    {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount)
    {
        this.walletAmount = walletAmount;
    }

    public String getWalletCurrency()
    {
        return walletCurrency;
    }

    public void setWalletCurrency(String walletCurrency)
    {
        this.walletCurrency = walletCurrency;
    }

    public String getRedirectMethod() {return redirectMethod;}

    public void setRedirectMethod(String redirectMethod) {this.redirectMethod = redirectMethod;}

    public String getTransactionReceipt()
    {
        return transactionReceipt;
    }

    public void setTransactionReceipt(String transactionReceipt)
    {
        this.transactionReceipt = transactionReceipt;
    }

    public String getDetailId()
    {
        return detailId;
    }

    public void setDetailId(String detailId)
    {
        this.detailId = detailId;
    }

    public String getPaymentProvider()
    {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider)
    {
        this.paymentProvider = paymentProvider;
    }

    public String getPaymentid()
    {
        return paymentid;
    }

    public void setPaymentid(String paymentid)
    {
        this.paymentid = paymentid;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getPersonalAccountNumber()
    {
        return personalAccountNumber;
    }

    public void setPersonalAccountNumber(String personalAccountNumber)
    {
        this.personalAccountNumber = personalAccountNumber;
    }

    public String getClientTransactionId()
    {
        return clientTransactionId;
    }

    public void setClientTransactionId(String clientTransactionId)
    {
        this.clientTransactionId = clientTransactionId;
    }

    public String getRefundCurrency()
    {
        return refundCurrency;
    }

    public void setRefundCurrency(String refundCurrency)
    {
        this.refundCurrency = refundCurrency;
    }

    public String getPurchase_identifier()
    {
        return purchase_identifier;
    }

    public void setPurchase_identifier(String purchase_identifier)
    {
        this.purchase_identifier = purchase_identifier;
    }

    public String getMerchant_id()
    {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id)
    {
        this.merchant_id = merchant_id;
    }

    public String getAuthorization_code()
    {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code)
    {
        this.authorization_code = authorization_code;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getStan()
    {
        return stan;
    }

    public void setStan(String stan)
    {
        this.stan = stan;
    }

    public String getCall_type()
    {
        return call_type;
    }

    public void setCall_type(String call_type)
    {
        this.call_type = call_type;
    }

    public String getTransactionmode()
    {
        return transactionmode;
    }

    public void setTransactionmode(String transactionmode)
    {
        this.transactionmode = transactionmode;
    }

    public String getChecksumAmount()
    {
        return checksumAmount;
    }

    public void setChecksumAmount(String checksumAmount)
    {
        this.checksumAmount = checksumAmount;
    }
}
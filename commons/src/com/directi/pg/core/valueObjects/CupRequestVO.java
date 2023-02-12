package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 8:57:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class CupRequestVO extends GenericRequestVO
{
    private String transactionId;
    private String detailsId;
    private String merchantId;
    private String merchantName;
    private String mcc;
    private String amount;
    private String currencyCode;
    private String ip;
    private String transType;
    private String orderTime;
    private String testAccount;


    public String getTransType()

    {
        return transType;
    }

    public String getTestAccount()
    {
        return testAccount;
    }

    public void setTestAccount(String testAccount)
    {
        this.testAccount = testAccount;
    }

    public String getOrderTime()
    {
        return orderTime;
    }

    public void setOrderTime(String orderTime)
    {
        this.orderTime = orderTime;
    }

    public void setTransType(String transType)
    {
        this.transType = transType;
    }

    public String getTransactionId()
    {
        return transactionId;

    }

    public String getDetailsId()
    {
        return detailsId;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public String getMerchantName()
    {
        return merchantName;
    }

    public String getMcc()
    {
        return mcc;
    }

    public String getAmount()
    {
        return amount;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public String getIp()
    {
        return ip;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public void setDetailsId(String detailsId)
    {
        this.detailsId = detailsId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    public void setMerchantName(String merchantName)
    {
        this.merchantName = merchantName;
    }

    public void setMcc(String mcc)
    {
        this.mcc = mcc;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }


}

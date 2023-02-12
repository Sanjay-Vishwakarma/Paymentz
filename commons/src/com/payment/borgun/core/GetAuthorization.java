package com.payment.borgun.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 9/14/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("getAuthorization")
public class GetAuthorization
{
    @XStreamAlias("Version")
    String version;

    @XStreamAlias("Currency")
    String Currency;

    @XStreamAlias("Processor")
    int processor;

    @XStreamAlias("MerchantID")
    String merchantID;

    @XStreamAlias("TerminalID")
    int terminalID;

    @XStreamAlias("TransType")
    int transType;

    @XStreamAlias("TrAmount")
    String trAmount;

    @XStreamAlias("TrCurrency")
    int trCurrency;

    @XStreamAlias("DateAndTime")
    String dateAndTime;

    @XStreamAlias("Batch")
    String batch;

    @XStreamAlias("Transaction")
    String transaction;

    @XStreamAlias("RRN")
    String rrn;

    @XStreamAlias("AuthCode")
    String authCode;

    @XStreamAlias("PAN")
    String pan;

    @XStreamAlias("ExpDate")
    String expDate;

    @XStreamAlias("CVC2")
    String cvc2;

    @XStreamAlias("MerchantName")
    String merchantName;

    @XStreamAlias("MerchantHome")
    String merchantHome;

    @XStreamAlias("MerchantCity")
    String merchantCity;

    @XStreamAlias("MerchantZipCode")
    String merchantZipCode;

    @XStreamAlias("ReturnRecurrentTicket")
    String ReturnRecurrentTicket;

    @XStreamAlias("RecurrentTicket")
    String RecurrentTicket;

    @XStreamAlias("MerchantCountry")
    String merchantCountry;

    @XStreamAlias("Ecommerce")
    String ecommerce;

    @XStreamAlias("EcommercePhone")
    String ecommercePhone;

    public String getCurrency()
    {
        return Currency;
    }

    public void setCurrency(String currency)
    {
        Currency = currency;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public int getProcessor()
    {
        return processor;
    }

    public void setProcessor(int processor)
    {
        this.processor = processor;
    }

    public String getMerchantID()
    {
        return merchantID;
    }

    public void setMerchantID(String merchantID)
    {
        this.merchantID = merchantID;
    }

    public int getTerminalID()
    {
        return terminalID;
    }

    public void setTerminalID(int terminalID)
    {
        this.terminalID = terminalID;
    }

    public int getTransType()
    {
        return transType;
    }

    public void setTransType(int transType)
    {
        this.transType = transType;
    }

    public String getTrAmount()
    {
        return trAmount;
    }

    public void setTrAmount(String trAmount)
    {
        this.trAmount = trAmount;
    }

    public int getTrCurrency()
    {
        return trCurrency;
    }

    public void setTrCurrency(int trCurrency)
    {
        this.trCurrency = trCurrency;
    }

    public String getDateAndTime()
    {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime)
    {
        this.dateAndTime = dateAndTime;
    }

    public String getBatch()
    {
        return batch;
    }

    public void setBatch(String batch)
    {
        this.batch = batch;
    }

    public String getTransaction()
    {
        return transaction;
    }

    public void setTransaction(String transaction)
    {
        this.transaction = transaction;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getPan()
    {
        return pan;
    }

    public void setPan(String pan)
    {
        this.pan = pan;
    }

    public String getExpDate()
    {
        return expDate;
    }

    public void setExpDate(String expDate)
    {
        this.expDate = expDate;
    }

    public String getCvc2()
    {
        return cvc2;
    }

    public void setCvc2(String cvc2)
    {
        this.cvc2 = cvc2;
    }

    public String getMerchantName()
    {
        return merchantName;
    }

    public void setMerchantName(String merchantName)
    {
        this.merchantName = merchantName;
    }

    public String getMerchantHome()
    {
        return merchantHome;
    }

    public void setMerchantHome(String merchantHome)
    {
        this.merchantHome = merchantHome;
    }

    public String getMerchantCity()
    {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity)
    {
        this.merchantCity = merchantCity;
    }

    public String getMerchantZipCode()
    {
        return merchantZipCode;
    }

    public void setMerchantZipCode(String merchantZipCode)
    {
        this.merchantZipCode = merchantZipCode;
    }

    public String getMerchantCountry()
    {
        return merchantCountry;
    }

    public void setMerchantCountry(String merchantCountry)
    {
        this.merchantCountry = merchantCountry;
    }

    public String getReturnRecurrentTicket()
    {
        return ReturnRecurrentTicket;
    }

    public void setReturnRecurrentTicket(String returnRecurrentTicket)
    {
        ReturnRecurrentTicket = returnRecurrentTicket;
    }

    public String getRecurrentTicket()
    {
        return RecurrentTicket;
    }

    public void setRecurrentTicket(String recurrentTicket)
    {
        RecurrentTicket = recurrentTicket;
    }

    public String getEcommerce()
    {
        return ecommerce;
    }

    public void setEcommerce(String ecommerce)
    {
        this.ecommerce = ecommerce;
    }

    public String getEcommercePhone()
    {
        return ecommercePhone;
    }

    public void setEcommercePhone(String ecommercePhone)
    {
        this.ecommercePhone = ecommercePhone;
    }
}

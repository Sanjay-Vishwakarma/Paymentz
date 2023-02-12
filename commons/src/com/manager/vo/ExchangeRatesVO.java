package com.manager.vo;

/**
 * Created by Jitendra on 27/02/2018.
 */
public class ExchangeRatesVO
{
    int id;
    String fromCurrency;
    String toCurrency;
    Double exchangeValue;
    String creationTime;
    String timestamp;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFromCurrency()
    {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency)
    {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency()
    {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency)
    {
        this.toCurrency = toCurrency;
    }

    public Double getExchangeValue()
    {
        return exchangeValue;
    }

    public void setExchangeValue(Double exchangeValue)
    {
        this.exchangeValue = exchangeValue;
    }

    public String getCreationTime()
    {
        return creationTime;
    }
    public void setCreationTime(String creationTime)
    {
        this.creationTime = creationTime;
    }
    public String getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
}

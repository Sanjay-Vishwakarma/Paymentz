package com.directi.pg;

/**
 * Created by Supriya on 7/11/2016.
 */
public class FailedTransactionLogVO
{
    public int toId;
    public String toType;
    public String fromId;
    public String fromType;
    public String description;
    public String orderDescription;
    public int terminalId;
    public int paymodeId;
    public int cardTypeId;
    public int accountId;
    public String firstName;
    public String lastName;
    public String email;
    public String cardNumber;
    public String expiryDate;
    public double amount;
    public double captureAmount;
    public double refundAmount;
    public double chargebackAmount;
    public String country;
    public String city;
    public String state;
    public String street;
    public String zip;
    public String telno;
    public String firstSix;
    public String lastFour;
    public String remark;
    public String httpHeader;
    public String requestedIP;
    public String requestedHost;


    public int getToId()
    {
        return toId;
    }

    public void setToId(int toId)
    {
        this.toId = toId;
    }

    public String getToType()
    {
        return toType;
    }

    public void setToType(String toType)
    {
        this.toType = toType;
    }

    public String getFromId()
    {
        return fromId;
    }

    public void setFromId(String fromId)
    {
        this.fromId = fromId;
    }

    public String getFromType()
    {
        return fromType;
    }

    public void setFromType(String fromType)
    {
        this.fromType = fromType;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public int getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(int terminalId)
    {
        this.terminalId = terminalId;
    }

    public int getPaymodeId()
    {
        return paymodeId;
    }

    public void setPaymodeId(int paymodeId)
    {
        this.paymodeId = paymodeId;
    }

    public int getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(int cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public double getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(double captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public double getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public double getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(double chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getFirstSix()
    {
        return firstSix;
    }

    public void setFirstSix(String firstSix)
    {
        this.firstSix = firstSix;
    }

    public String getLastFour()
    {
        return lastFour;
    }

    public void setLastFour(String lastFour)
    {
        this.lastFour = lastFour;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getHttpHeader()
    {
        return httpHeader;
    }

    public void setHttpHeader(String httpHeader)
    {
        this.httpHeader = httpHeader;
    }

    public String getRequestedIP()
    {
        return requestedIP;
    }

    public void setRequestedIP(String requestedIP)
    {
        this.requestedIP = requestedIP;
    }

    public String getRequestedHost()
    {
        return requestedHost;
    }

    public void setRequestedHost(String requestedHost)
    {
        this.requestedHost = requestedHost;
    }
}

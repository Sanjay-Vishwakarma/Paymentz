package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created by admin on 5/13/2016.
 */
@XmlRootElement(name="Authorize")
@XmlAccessorType(XmlAccessType.FIELD)
public class Authorize
{
    @XmlElement(name = "memberId",required = true)
    private long memberId;

    @XmlElement(name = "memberGuid",required = true)
    private String memberGuid;

    @XmlElement(name = "countryId",required = true)
    private int countryId;

    @XmlElement(name = "amount",required = true)
    private String amount;

    @XmlElement(name = "currencyId",required = true)
    private int currencyId;

    @XmlElement(name = "trackingMemberCode",required = true)
    private String trackingMemberCode;

    @XmlElement(name = "cardNumber",required = true)
    private String cardNumber;

    @XmlElement(name = "cardholder",required = true)
    private String cardholder;

    @XmlElement(name = "cardExpiryMonth",required = true)
    private int cardExpiryMonth;

    @XmlElement(name = "cardExpiryYear",required = true)
    private int cardExpiryYear;

    @XmlElement(name = "cardCvv",required = true)
    private String cardCvv;

    @XmlElement(name = "merchantAccountType",required = true)
    private int merchantAccountType;

    @XmlElement(name = "dbaName",required = true)
    private String dbaName;

    @XmlElement(name = "dbaCity",required = true)
    private String dbaCity;

    @XmlElement(name = "avsAddress",required = true)
    private String avsAddress;

    @XmlElement(name= "avsZip",required=true)
    private String avsZip;

    @XmlElement(name = "additionalInfo",required = true)
    private AdditionalInfo additionalInfo;

    @XmlElement(name = "authenticationIndicator",required = true)
    private String authenticationIndicator;

    public String getAuthenticationIndicator()
    {
        return authenticationIndicator;
    }

    public void setAuthenticationIndicator(String authenticationIndicator)
    {
        this.authenticationIndicator = authenticationIndicator;
    }

    public long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(long memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberGuid()
    {
        return memberGuid;
    }

    public void setMemberGuid(String memberGuid)
    {
        this.memberGuid = memberGuid;
    }

    public int getCountryId()
    {
        return countryId;
    }

    public void setCountryId(int countryId)
    {
        this.countryId = countryId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public int getCurrencyId()
    {
        return currencyId;
    }

    public void setCurrencyId(int currencyId)
    {
        this.currencyId = currencyId;
    }

    public String getTrackingMemberCode()
    {
        return trackingMemberCode;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public void setTrackingMemberCode(String trackingMemberCode)
    {
        this.trackingMemberCode = trackingMemberCode;
    }

    public String getCardholder()
    {
        return cardholder;
    }

    public void setCardholder(String cardholder)
    {
        this.cardholder = cardholder;
    }

    public int getCardExpiryMonth()
    {
        return cardExpiryMonth;
    }

    public void setCardExpiryMonth(int cardExpiryMonth)
    {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public int getCardExpiryYear()
    {
        return cardExpiryYear;
    }

    public void setCardExpiryYear(int cardExpiryYear)
    {
        this.cardExpiryYear = cardExpiryYear;
    }

    public String getCardCvv()
    {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv)
    {
        this.cardCvv = cardCvv;
    }

    public int getMerchantAccountType()
    {
        return merchantAccountType;
    }

    public void setMerchantAccountType(int merchantAccountType)
    {
        this.merchantAccountType = merchantAccountType;
    }

    public String getDbaName()
    {
        return dbaName;
    }

    public void setDbaName(String dbaName)
    {
        this.dbaName = dbaName;
    }

    public String getDbaCity()
    {
        return dbaCity;
    }

    public void setDbaCity(String dbaCity)
    {
        this.dbaCity = dbaCity;
    }

    public String getAvsAddress()
    {
        return avsAddress;
    }

    public void setAvsAddress(String avsAddress)
    {
        this.avsAddress = avsAddress;
    }

    public String getAvsZip()
    {
        return avsZip;
    }

    public void setAvsZip(String avsZip)
    {
        this.avsZip = avsZip;
    }

    public AdditionalInfo getAdditionalInfo()
    {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }
}

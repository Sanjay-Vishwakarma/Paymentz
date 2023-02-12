package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("CREDIT_CARD_DATA")
public class CREDIT_CARD_DATA
{
    @XStreamAlias("CreditCardNumber")
    String creditCardNumber;

    @XStreamAlias("CardHolderName")
    String cardHolderName;

    @XStreamAlias("ExpirationYear")
    String expireYear;

    @XStreamAlias("ExpirationMonth")
    String expireMonth;

    @XStreamAlias("CVC2")
    String cvc2;


    public String getCardHolderName()
    {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName)
    {
        this.cardHolderName = cardHolderName;
    }

    public String getCvc2()
    {
        return cvc2;
    }

    public void setCvc2(String cvc2)
    {
        this.cvc2 = cvc2;
    }

    public String getCreditCardNumber()
    {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        this.creditCardNumber = creditCardNumber;
    }


    public String getExpireMonth()
    {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth)
    {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear()
    {
        return expireYear;
    }

    public void setExpireYear(String expireYear)
    {
        this.expireYear = expireYear;
    }

}

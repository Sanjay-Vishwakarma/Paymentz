package com.payment.beekash.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 2/20/2019.
 */
@XmlRootElement(name="card")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardVO
{
    @XmlElement(name ="id")
    String id;

    @XmlElement(name="key")
    String key;

    @XmlElement(name = "card_number")
    String card_number;

    @XmlElement(name = "name_on_card")
    String name_on_card;

    @XmlElement(name = "cvc_code")
    String cvc_code;

    @XmlElement(name = "expiry_month")
    String expiry_month;

    @XmlElement(name = "expiry_year")
    String expiry_year;

    @XmlElement(name="firstsix")
    String firstsix;

    @XmlElement(name="lastfour")
    String lastfour;

    public String getCard_number()
    {
        return card_number;
    }

    public void setCard_number(String card_number)
    {
        this.card_number = card_number;
    }

    public String getName_on_card()
    {
        return name_on_card;
    }

    public void setName_on_card(String name_on_card)
    {
        this.name_on_card = name_on_card;
    }

    public String getCvc_code()
    {
        return cvc_code;
    }

    public void setCvc_code(String cvc_code)
    {
        this.cvc_code = cvc_code;
    }

    public String getExpiry_month() {return expiry_month;}
    public void setExpiry_month(String expiry_month) {this.expiry_month = expiry_month;}

    public String getExpiry_year() {return expiry_year;}
    public void setExpiry_year(String expiry_year) {this.expiry_year = expiry_year;}

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}

    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getLastfour()
    {
        return lastfour;
    }

    public void setLastfour(String lastfour)
    {
        this.lastfour = lastfour;
    }
}

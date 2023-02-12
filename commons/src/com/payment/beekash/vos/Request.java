package com.payment.beekash.vos;

import com.manager.vo.CardDetailsVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URLEncoder;

/**
 * Created by admin on 12/28/2015.
 */
@XmlRootElement(name="Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Request
{
    @XmlElement(name = "publish_key")
    String publish_key;

    @XmlElement(name = "api_id")
    String api_id;

    @XmlElement(name = "api_password")
    String api_password;

    @XmlElement(name = "url")
    String url;

    @XmlElement(name = "card")
    CardVO card;

    @XmlElement(name = "customer")
    CustomerVO customer;

    @XmlElement(name="shipping")
    ShippingVO shipping;

    @XmlElement(name = "card_holder_ip")
    String card_holder_ip;

    @XmlElement(name = "currency")
    String currency;

    @XmlElement(name = "amount")
    String amount;

    @XmlElement(name = "language")
    String language;

    @XmlElement(name = "ordernumber")
    String ordernumber;

    @XmlElement(name = "birthdate")
    String birthdate;

    @XmlElement(name = "products")
    String products;

    @XmlElement(name = "remarks")
    String remarks;

    @XmlElement(name = "charge_type")
    String charge_type;

    @XmlElement(name = "capture")
    boolean capture;

    @XmlElement(name = "ip_address")
    String ip_address;

    @XmlElement(name= "return_url")
    String return_url;

    public String getReturn_url() {return return_url;}
    public void setReturn_url(String return_url) {this.return_url = return_url;}

    public String getApi_id()
    {
        return api_id;
    }
    public void setApi_id(String api_id)
    {
        this.api_id = api_id;
    }

    public String getApi_password()
    {
        return api_password;
    }
    public void setApi_password(String api_password)
    {
        this.api_password = api_password;
    }

    public String getUrl()
    {
        return url;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getCard_holder_ip()
    {
        return card_holder_ip;
    }
    public void setCard_holder_ip(String card_holder_ip)
    {
        this.card_holder_ip = card_holder_ip;
    }

    public String getCurrency()
    {
        return currency;
    }
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getAmount()
    {
        return amount;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getLanguage()
    {
        return language;
    }
    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getOrdernumber()
    {
        return ordernumber;
    }
    public void setOrdernumber(String ordernumber)
    {
        this.ordernumber = ordernumber;
    }

    public String getBirthdate()
    {
        return birthdate;
    }
    public void setBirthdate(String birthdate)
    {
        this.birthdate = birthdate;
    }

    public ShippingVO getShipping()
    {
        return shipping;
    }
    public void setShipping(ShippingVO shipping)
    {
        this.shipping = shipping;
    }

    public String getProducts()
    {
        return products;
    }
    public void setProducts(String products)
    {
        this.products = products;
    }

    public String getRemarks()
    {
        return remarks;
    }
    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getCharge_type() {return charge_type;}
    public void setCharge_type(String charge_type) {this.charge_type = charge_type;}

    public boolean isCapture()
    {
        return capture;
    }
    public void setCapture(boolean capture)
    {
        this.capture = capture;
    }

    public String getPublish_key() {return publish_key;}
    public void setPublish_key(String publish_key) {this.publish_key = publish_key;}

    public CardVO getCard() {return card;}
    public void setCard(CardVO card) {this.card = card;}

    public CustomerVO getCustomer() {return customer;}
    public void setCustomer(CustomerVO customer) {this.customer = customer;}

    public String getIp_address() {return ip_address;}
    public void setIp_address(String ip_address) {this.ip_address = ip_address;}
}

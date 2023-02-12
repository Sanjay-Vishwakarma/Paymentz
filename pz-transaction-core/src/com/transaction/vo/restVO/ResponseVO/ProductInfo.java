package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/18/2020.
 */


@XmlRootElement(name = "productinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInfo
{

    @XmlElement(name="product_contact_phone")
    String product_contact_phone;

    @XmlElement(name="product_cost")
    String product_cost;

    @XmlElement(name="product_name")
    String product_name;

    @XmlElement(name="product_quantity")
    String product_quantity;

    @XmlElement(name="product_website")
    String product_website;

    public String getProduct_contact_phone()
    {
        return product_contact_phone;
    }

    public void setProduct_contact_phone(String product_contact_phone)
    {
        this.product_contact_phone = product_contact_phone;
    }

    public String getProduct_cost()
    {
        return product_cost;
    }

    public void setProduct_cost(String product_cost)
    {
        this.product_cost = product_cost;
    }

    public String getProduct_name()
    {
        return product_name;
    }

    public void setProduct_name(String product_name)
    {
        this.product_name = product_name;
    }

    public String getProduct_quantity()
    {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity)
    {
        this.product_quantity = product_quantity;
    }

    public String getProduct_website()
    {
        return product_website;
    }

    public void setProduct_website(String product_website)
    {
        this.product_website = product_website;
    }
}

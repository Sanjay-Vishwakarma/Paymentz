package com.invoice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 24-08-2017.
 */
@XmlRootElement(name="defaultProductList")
@XmlAccessorType(XmlAccessType.FIELD)
public class DefaultProductList
{
        @XmlElement(name = "productDescription")
        String productDescription;

        @XmlElement(name="productAmount")
        String productAmount;

        @XmlElement(name="unit")
        String unit;

        @XmlElement(name="tax")
        String tax;

        @XmlElement(name="quantity")
        String quantity;

        @XmlElement(name="productTotal")
        String productTotal;

    public String getTax()
    {
        return tax;
    }

    public void setTax(String tax)
    {
        this.tax = tax;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getProductAmount()
    {
        return productAmount;
    }

    public void setProductAmount(String productAmount)
    {
        this.productAmount = productAmount;
    }

    public String getProductDescription()
    {
        return productDescription;
    }

    public void setProductDescription(String productDescription)
    {
        this.productDescription = productDescription;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public void setQuantity(String quantity)
    {
        this.quantity = quantity;
    }

    public String getProductTotal()
    {
        return productTotal;
    }

    public void setProductTotal(String productTotal)
    {
        this.productTotal = productTotal;
    }
}

package com.invoice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 6/22/2017.
 */

@XmlRootElement(name="product")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductList
{

    @XmlElement(name = "productDescription")
    String productDescription;

    @XmlElement(name="productAmount")
    String productAmount;

    @XmlElement(name="quantity")
    String quantity;

    @XmlElement(name="productTotal")
    String productTotal;

    @XmlElement(name="productUnit")
    String productUnit;

    @XmlElement(name="tax")
    String tax;

    public String getProductDescription()
    {
        return productDescription;
    }

    public void setProductDescription(String productDescription)
    {
        this.productDescription = productDescription;
    }

    public String getProductAmount()
    {
        return productAmount;
    }

    public void setProductAmount(String productAmount)
    {
        this.productAmount = productAmount;
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

    public String getProductUnit()
    {
        return productUnit;
    }

    public void setProductUnit(String productUnit)
    {
        this.productUnit = productUnit;
    }

    public String getTax()
    {
        return tax;
    }

    public void setTax(String tax)
    {
        this.tax = tax;
    }
}

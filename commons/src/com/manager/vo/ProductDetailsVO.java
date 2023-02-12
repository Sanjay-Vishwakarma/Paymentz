package com.manager.vo;

/**
 * Created by Admin on 7/17/2022.
 */
public class ProductDetailsVO
{
    public  String SKU;
    public  String productName;
    public  String productUnit;
    public  String productAmount;
    public  String shippingAmount;

    public String getSKU()
    {
        return SKU;
    }

    public void setSKU(String SKU)
    {
        this.SKU = SKU;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductUnit()
    {
        return productUnit;
    }

    public void setProductUnit(String productUnit)
    {
        this.productUnit = productUnit;
    }

    public String getProductAmount()
    {
        return productAmount;
    }

    public void setProductAmount(String productAmount)
    {
        this.productAmount = productAmount;
    }

    public String getShippingAmount()
    {
        return shippingAmount;
    }

    public void setShippingAmount(String shippingAmount)
    {
        this.shippingAmount = shippingAmount;
    }
}

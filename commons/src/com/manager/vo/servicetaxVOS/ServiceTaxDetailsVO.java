package com.manager.vo.servicetaxVOS;


import com.manager.vo.payoutVOs.ChargeDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 4/18/15
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTaxDetailsVO extends ChargeDetailsVO
{
    double serviceTaxAmount;
    double serviceTaxRate;

    public double getServiceTaxAmount()
    {
        return serviceTaxAmount;
    }
    public void setServiceTaxAmount(double serviceTaxAmount)
    {
        this.serviceTaxAmount = serviceTaxAmount;
    }
    public double getServiceTaxRate()
    {
        return serviceTaxRate;
    }
    public void setServiceTaxRate(double serviceTaxRate)
    {
        this.serviceTaxRate = serviceTaxRate;
    }
}

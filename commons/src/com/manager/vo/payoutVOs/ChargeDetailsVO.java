package com.manager.vo.payoutVOs;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/2/14
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargeDetailsVO
{

    String chargeName;
    String chargeValue;
    long count;
    double amount;
    double total;
    String valueType;
    String chargeid;
    String negativebalance;




    public String getNegativebalance()
    {
        return negativebalance;
    }

    public void setNegativebalance(String negativebalance)
    {
        this.negativebalance = negativebalance;
    }

    public String getChargeid()
    {
        return chargeid;
    }

    public void setChargeid(String chargeid)
    {
        this.chargeid = chargeid;
    }

    public String getChargeName()
    {
        return chargeName;
    }

    public void setChargeName(String chargeName)
    {
        this.chargeName = chargeName;
    }

    public String getChargeValue()
    {
        return chargeValue;
    }

    public void setChargeValue(String chargeValue)
    {
        this.chargeValue = chargeValue;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public double getTotal()
    {
        return total;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }


}

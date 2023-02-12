package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/1/14
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireAmountVO
{
    long wireCount;

    double paidAmount;
    double unpaidAmount;         //sum of wires whose status is unpaid
    double unpaidBalanceAmount;          // Sum of unpaidamount column
    double wireBalanceAmount;

    public long getWireCount()
    {
        return wireCount;
    }

    public void setWireCount(long wireCount)
    {
        this.wireCount = wireCount;
    }

    public double getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public double getUnpaidAmount()
    {
        return unpaidAmount;
    }

    public void setUnpaidAmount(double unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
    }

    public double getUnpaidBalanceAmount()
    {
        return unpaidBalanceAmount;
    }

    public void setUnpaidBalanceAmount(double unpaidBalanceAmount)
    {
        this.unpaidBalanceAmount = unpaidBalanceAmount;
    }

    public double getWireBalanceAmount()
    {
        return wireBalanceAmount;
    }

    public void setWireBalanceAmount(double wireBalanceAmount)
    {
        this.wireBalanceAmount = wireBalanceAmount;
    }
}

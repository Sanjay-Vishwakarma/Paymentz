package com.manager.vo.payoutVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/11/14
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetupChargeVO extends ChargeDetailsVO
{
    String lastChargeDate;

    public String getLastChargeDate()
    {
        return lastChargeDate;
    }
    public void setLastChargeDate(String lastChargeDate)
    {
        this.lastChargeDate = lastChargeDate;
    }


}

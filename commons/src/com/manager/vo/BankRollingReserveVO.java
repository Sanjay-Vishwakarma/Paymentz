package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/4/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankRollingReserveVO
{
    String bankRollingReserveId;
    String accountId;
    String rollingReserveDateUpTo;
    //temporary has to be removed
    String rollingRelease_time;
    String bankRollingReserveDateTime;
    String merchantId;
    public String getBankRollingReserveId()
    {
        return bankRollingReserveId;
    }

    public void setBankRollingReserveId(String bankRollingReserveId)
    {
        this.bankRollingReserveId = bankRollingReserveId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getRollingReserveDateUpTo()
    {
        return rollingReserveDateUpTo;
    }

    public void setRollingReserveDateUpTo(String rollingReserveDateUpTo)
    {
        this.rollingReserveDateUpTo = rollingReserveDateUpTo;
    }

    //temporary hs to be removed

    public String getRollingRelease_time()
    {
        return rollingRelease_time;
    }

    public void setRollingRelease_time(String rollingRelease_time)
    {
        this.rollingRelease_time = rollingRelease_time;
    }

    public String getBankRollingReserveDateTime()
    {
        return bankRollingReserveDateTime;
    }

    public void setBankRollingReserveDateTime(String bankRollingReserveDateTime)
    {
        this.bankRollingReserveDateTime = bankRollingReserveDateTime;
    }
    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }
}

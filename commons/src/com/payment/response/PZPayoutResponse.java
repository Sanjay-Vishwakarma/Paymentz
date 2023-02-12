package com.payment.response;

/**
 * Created by admin on 7/21/2017.
 */
public class PZPayoutResponse extends PZResponse
{
    private String payoutAmount;
    private String voucherNumber;
    private String voucherCurrency;
    private String tmpl_amount;
    private String tmpl_currency;

    //For VM
    private String merchantUsersCommission;//for payout commission
    private String commissionCurrency;//for payout customer currency
    private String commissionToPay;//for deposit customer commission sent in response

    public String getVoucherNumber()
    {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber)
    {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherCurrency()
    {
        return voucherCurrency;
    }

    public void setVoucherCurrency(String voucherCurrency)
    {
        this.voucherCurrency = voucherCurrency;
    }

    public String getTmpl_amount()
    {
        return tmpl_amount;
    }

    public void setTmpl_amount(String tmpl_amount)
    {
        this.tmpl_amount = tmpl_amount;
    }

    public String getTmpl_currency()
    {
        return tmpl_currency;
    }

    public void setTmpl_currency(String tmpl_currency)
    {
        this.tmpl_currency = tmpl_currency;
    }

    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public String getMerchantUsersCommission()
    {
        return merchantUsersCommission;
    }

    public void setMerchantUsersCommission(String merchantUsersCommission)
    {
        this.merchantUsersCommission = merchantUsersCommission;
    }

    public String getCommissionCurrency()
    {
        return commissionCurrency;
    }

    public void setCommissionCurrency(String commissionCurrency)
    {
        this.commissionCurrency = commissionCurrency;
    }

    public String getCommissionToPay()
    {
        return commissionToPay;
    }

    public void setCommissionToPay(String commissionToPay)
    {
        this.commissionToPay = commissionToPay;
    }
}

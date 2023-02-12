package com.payment.bitcoinpayget;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Admin on 28-Aug-19.
 */
public class BitcoinPaygateResponseVO extends CommResponseVO
{

    String amount_btc;
    String currency_btc;
    String exchange_rate;
    String paid;
    String paid_btc;
    String payment_id;
    String remaining_to_pay;
    String remaining_to_pay_btc;
    String resp_status;
    String pz_transaction_status;
    String fiat_amount;
    String payoutid;


    public String getAmount_btc()
    {
        return amount_btc;
    }

    public void setAmount_btc(String amount_btc)
    {
        this.amount_btc = amount_btc;
    }

    public String getCurrency_btc()
    {
        return currency_btc;
    }

    public void setCurrency_btc(String currency_btc)
    {
        this.currency_btc = currency_btc;
    }

    public String getExchange_rate()
    {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate)
    {
        this.exchange_rate = exchange_rate;
    }

    public String getPaid()
    {
        return paid;
    }

    public void setPaid(String paid)
    {
        this.paid = paid;
    }

    public String getPaid_btc()
    {
        return paid_btc;
    }

    public void setPaid_btc(String paid_btc)
    {
        this.paid_btc = paid_btc;
    }

    public String getPayment_id()
    {
        return payment_id;
    }

    public void setPayment_id(String payment_id)
    {
        this.payment_id = payment_id;
    }

    public String getRemaining_to_pay()
    {
        return remaining_to_pay;
    }

    public void setRemaining_to_pay(String remaining_to_pay)
    {
        this.remaining_to_pay = remaining_to_pay;
    }

    public String getRemaining_to_pay_btc()
    {
        return remaining_to_pay_btc;
    }

    public void setRemaining_to_pay_btc(String remaining_to_pay_btc)
    {
        this.remaining_to_pay_btc = remaining_to_pay_btc;
    }

    public String getResp_status()
    {
        return resp_status;
    }

    public void setResp_status(String resp_status)
    {
        this.resp_status = resp_status;
    }

    public String getPz_transaction_status()
    {
        return pz_transaction_status;
    }

    public void setPz_transaction_status(String pz_transaction_status)
    {
        this.pz_transaction_status = pz_transaction_status;
    }

    public String getFiat_amount()
    {
        return fiat_amount;
    }

    public void setFiat_amount(String fiat_amount)
    {
        this.fiat_amount = fiat_amount;
    }

    public String getPayoutid()
    {
        return payoutid;
    }

    public void setPayoutid(String payoutid)
    {
        this.payoutid = payoutid;
    }
}

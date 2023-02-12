package com.payment.europay.core;

import com.payment.payon.core.message.Request;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/6/13
 * Time: 4:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundRequest extends Request
{
    public Integer getAmount()
    {
        return amount;
    }

    public void setAmount(Integer amount)
    {
        this.amount = amount;
    }

    public String getTxId()
    {
        return txId;
    }

    public void setTxId(String txId)
    {
        this.txId = txId;
    }

    Integer amount;
    String txId;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    String url;
}

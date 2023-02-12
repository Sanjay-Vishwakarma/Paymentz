package com.payment.payspace;

import com.directi.pg.core.valueObjects.GenericResponseVO;

import java.util.List;
/**
 * Created by Uday on 7/14/17.
 */
public class PaySpaceResponseVO extends GenericResponseVO
{
    private String action;
    private String result;
    private String status;
    private String trans_id;
    private String order_id;
    private String descriptor;
    private String trans_date;
    private String recurring_token;
    private String redirect_url;
    private String redirect_method;
    private String decline_reason;
    private String name;
    private String mail;
    private String ip;
    private String amount;
    private String currency;
    private String card;
    private List<Transactions> transactions;
    private RedirectParams redirect_params;


    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTrans_id()
    {
        return trans_id;
    }

    public void setTrans_id(String trans_id)
    {
        this.trans_id = trans_id;
    }

    public String getOrder_id()
    {
        return order_id;
    }

    public void setOrder_id(String order_id)
    {
        this.order_id = order_id;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getTrans_date()
    {
        return trans_date;
    }

    public void setTrans_date(String trans_date)
    {
        this.trans_date = trans_date;
    }

    public String getRecurring_token()
    {
        return recurring_token;
    }

    public void setRecurring_token(String recurring_token)
    {
        this.recurring_token = recurring_token;
    }

    public String getRedirect_url()
    {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url)
    {
        this.redirect_url = redirect_url;
    }

    public String getRedirect_method()
    {
        return redirect_method;
    }

    public void setRedirect_method(String redirect_method)
    {
        this.redirect_method = redirect_method;
    }

    public String getDecline_reason()
    {
        return decline_reason;
    }

    public void setDecline_reason(String decline_reason)
    {
        this.decline_reason = decline_reason;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCard()
    {
        return card;
    }

    public void setCard(String card)
    {
        this.card = card;
    }

    public List<Transactions> getTransactions() {return transactions;}

    public void setTransactions(List<Transactions> transactions) {this.transactions = transactions;}

    public RedirectParams getRedirect_params()
    {
        return redirect_params;
    }
    public void setRedirect_params(RedirectParams redirect_params)
    {
        this.redirect_params = redirect_params;
    }
}

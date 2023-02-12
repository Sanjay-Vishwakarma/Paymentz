package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/30/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZReconcilationResponce extends PZResponse
{
    public String getRestransactionId()
    {
        return restransactionId;
    }

    public void setRestransactionId(String restransactionId)
    {
        this.restransactionId = restransactionId;
    }

    public String getResupdatedStatus()
    {
        return resupdatedStatus;
    }

    public void setResupdatedStatus(String resupdatedStatus)
    {
        this.resupdatedStatus = resupdatedStatus;
    }

    public String getRestoid()
    {
        return restoid;
    }

    public void setRestoid(String restoid)
    {
        this.restoid = restoid;
    }

    public String getResfromID()
    {
        return resfromID;
    }

    public void setResfromID(String resfromID)
    {
        this.resfromID = resfromID;
    }

    public String getResaccountid()
    {
        return resaccountid;
    }

    public void setResaccountid(String resaccountid)
    {
        this.resaccountid = resaccountid;
    }


    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getOrderDesc()
    {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc)
    {
        this.orderDesc = orderDesc;
    }

    String restransactionId;
    String resupdatedStatus;
    String restoid;
    String resfromID;
    String resaccountid;
    String amount;
    String orderDesc;



}
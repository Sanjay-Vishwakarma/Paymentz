package com.directi.pg.core.qwipi.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/8/12
 * Time: 2:34 AM
 * To change this template use File | Settings | File Templates.
 */

public class Response
{

    private String result;
    private String code;
    private String text;
    private ResOrder order;
    private Refund refund;
    private Settle settle;

    private String operation;
    private String resultCode;
    private String paymentOrderNo;
    private ResOrder billNo;
    private Refund refundAmount;

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getPaymentOrderNo()
    {
        return paymentOrderNo;
    }

    public void setPaymentOrderNo(String paymentOrderNo)
    {
        this.paymentOrderNo = paymentOrderNo;
    }

    public ResOrder getBillNo()
    {
        return billNo;
    }

    public void setBillNo(ResOrder billNo)
    {
        this.billNo = billNo;
    }

    public Refund getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(Refund refundAmount)
    {
        this.refundAmount = refundAmount;
    }




    public Chargeback getChargeback()
    {
        return chargeback;
    }

    public void setChargeback(Chargeback chargeback)
    {
        this.chargeback = chargeback;
    }

    private Chargeback chargeback;


    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public ResOrder getOrder()
    {
        return order;
    }

    public void setOrder(ResOrder order)
    {
        this.order = order;
    }

    public Refund getRefund()
    {
        return refund;
    }

    public void setRefund(Refund refund)
    {
        this.refund = refund;
    }

    public Settle getSettle()
    {
        return settle;
    }

    public void setSettle(Settle settle)
    {
        this.settle = settle;
    }
}

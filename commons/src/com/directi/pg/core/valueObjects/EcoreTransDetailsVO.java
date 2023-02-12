package com.directi.pg.core.valueObjects;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 11/14/12
 * Time: 9:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreTransDetailsVO extends GenericTransDetailsVO
{


    public String getPaymentOrderNo()
    {
        return paymentOrderNo;
    }

    public void setPaymentOrderNo(String paymentOrderNo)
    {
        this.paymentOrderNo = paymentOrderNo;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundamount)
    {
        this.refundAmount = refundamount;
    }

    public String getReturnURL()
    {
        return returnURL;
    }

    public void setReturnURL(String returnurl)
    {
        this.returnURL = returnurl;
    }

    public String getMerNo()
    {
        return merNo;
    }

    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }


    private String paymentOrderNo;
    private String billNo;
    private String refundAmount;
    private String returnURL;
    private String merNo;
    private String language;

    private String operation;

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    private String mid;


    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }


}

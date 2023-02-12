package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 12, 2012
 * Time: 12:19:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarResponseVO   extends CommResponseVO
{


    private String secondResponseCode;
    private String primaryResponseCode;
    private String bankOrderId;
    private String holder;
    private String successCode;
    //private String orderRef;       //Tracking Id
    private String payRef;
    //private String amount;
    private String currencyCode;
    private String authId;
    //private String txTime;

    private String errMsg;
    //private String orderStatus;
    private String resultCode;
    private String mpsMode;
    private String html;
    private String gatewayStatus;
    private String ref;

    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }

    public String getGatewayStatus()
    {
        return gatewayStatus;
    }

    public void setGatewayStatus(String gatewayStatus)
    {
        this.gatewayStatus = gatewayStatus;
    }

    public String getHtml()
       {
           return html;
       }

       public void setHtml(String html)
       {
           this.html = html;
       }
    

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }



    public String getMpsMode()
    {
        return mpsMode;
    }

    public void setMpsMode(String mpsMode)
    {
        this.mpsMode = mpsMode;
    }


    public PayDollarResponseVO()
    {
    }

    public String getSecondResponseCode()
    {
        return secondResponseCode;
    }

    public void setSecondResponseCode(String secondResponseCode)
    {
        this.secondResponseCode = secondResponseCode;
    }

    public String getPrimaryResponseCode()
    {
        return primaryResponseCode;
    }

    public void setPrimaryResponseCode(String primaryResponseCode)
    {
        this.primaryResponseCode = primaryResponseCode;
    }

    public String getBankOrderId()
    {
        return bankOrderId;
    }

    public void setBankOrderId(String bankOrderId)
    {
        this.bankOrderId = bankOrderId;
    }

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getSuccessCode()
    {
        return successCode;
    }

    public void setSuccessCode(String successCode)
    {
        this.successCode = successCode;
    }


    public String getPayRef()
    {
        return payRef;
    }

    public void setPayRef(String payRef)
    {
        this.payRef = payRef;
    }


    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getAuthId()
    {
        return authId;
    }

    public void setAuthId(String authId)
    {
        this.authId = authId;
    }

   
    public String getErrMsg()
    {
        return errMsg;
    }

    public void setErrMsg(String errMsg)
    {
        this.errMsg = errMsg;
    }


}

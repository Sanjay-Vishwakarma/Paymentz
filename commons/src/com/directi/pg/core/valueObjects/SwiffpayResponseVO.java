package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 6/19/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwiffpayResponseVO  extends CommResponseVO
{
    String AuthorizationCode;
    String ReferenceNumber;
    String BatchNumber;
    String AVSResultCode;
    String AuthNetSpecific;
    String CVC2ResultCode;
    String PartialAuth;
    String orderid;
    String historyid;

    public String getResponseRemark()
    {
        return responseRemark;
    }

    public void setResponseRemark(String responseRemark)
    {
        this.responseRemark = responseRemark;
    }

    String responseRemark;
    public String getOrderid()
    {
        return orderid;
    }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    public String getHistoryid()
    {
        return historyid;
    }

    public void setHistoryid(String historyid)
    {
        this.historyid = historyid;
    }



    public String getAuthorizationCode()
    {
        return AuthorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        AuthorizationCode = authorizationCode;
    }

    public String getReferenceNumber()
    {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String referenceNumber)
    {
        ReferenceNumber = referenceNumber;
    }

    public String getBatchNumber()
    {
        return BatchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        BatchNumber = batchNumber;
    }

    public String getAVSResultCode()
    {
        return AVSResultCode;
    }

    public void setAVSResultCode(String AVSResultCode)
    {
        this.AVSResultCode = AVSResultCode;
    }

    public String getAuthNetSpecific()
    {
        return AuthNetSpecific;
    }

    public void setAuthNetSpecific(String authNetSpecific)
    {
        AuthNetSpecific = authNetSpecific;
    }

    public String getCVC2ResultCode()
    {
        return CVC2ResultCode;
    }

    public void setCVC2ResultCode(String CVC2ResultCode)
    {
        this.CVC2ResultCode = CVC2ResultCode;
    }

    public String getPartialAuth()
    {
        return PartialAuth;
    }

    public void setPartialAuth(String partialAuth)
    {
        PartialAuth = partialAuth;
    }


}

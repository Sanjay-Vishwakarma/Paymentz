package com.payment.cup.core.request;

import com.payment.request.PZInquiryRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Jimmy
 * Date: Apr 20, 2013
 * Time: 6:00:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class CUPInquiryRequest extends PZInquiryRequest
{
    String operationStatus;

    public String getOperationStatus()
    {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus)
    {
        this.operationStatus = operationStatus;
    }


}

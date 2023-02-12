package com.payment.payvision.core;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/13/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVisionResponseVO extends Comm3DResponseVO
{

    private String transactionGuid;
    private String trackingMemberCode;
    private int enrollmentId;


    public String getTransactionGuid()
    {
        return transactionGuid;
    }

    public void setTransactionGuid(String transactionGuid)
    {
        this.transactionGuid = transactionGuid;
    }

    public String getTrackingMemberCode()
    {
        return trackingMemberCode;
    }

    public void setTrackingMemberCode(String trackingMemberCode)
    {
        this.trackingMemberCode = trackingMemberCode;
    }

    public int getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId)
    {
        this.enrollmentId = enrollmentId;
    }
}

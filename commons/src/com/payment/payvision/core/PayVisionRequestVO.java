package com.payment.payvision.core;

import com.payment.common.core.CommRequestVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/13/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVisionRequestVO extends CommRequestVO
{

    private String transactionGuid ;
    private String trackingMemberCode;
    private String cardId;
    private String cardGuid;
    private String PaRes;
    private int enrollmentId;
    private String enrollmentTrackingMemberCode;


    public String getCardId()
    {
        return cardId;
    }

    public void setCardId(String cardId)
    {
        this.cardId = cardId;
    }

    public String getCardGuid()
    {
        return cardGuid;
    }

    public void setCardGuid(String cardGuid)
    {
        this.cardGuid = cardGuid;
    }

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

    public String getPaRes()
    {
        return PaRes;
    }

    public void setPaRes(String paRes)
    {
        PaRes = paRes;
    }

    public int getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId)
    {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentTrackingMemberCode()
    {
        return enrollmentTrackingMemberCode;
    }

    public void setEnrollmentTrackingMemberCode(String enrollmentTrackingMemberCode)
    {
        this.enrollmentTrackingMemberCode = enrollmentTrackingMemberCode;
    }

}

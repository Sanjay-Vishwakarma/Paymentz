package com.payment.Enum;

/**
 * Created by Vivek on 5/29/2020.
 */
public enum CallTypeEnum
{
    Purchase_Inquiry("Purchase Inquiry"),
    Fraud_Determined("Fraud Determined"),
    Dispute_Initiated("Dispute Initiated"),
    Exception_file_listing("Exception file listing"),
    Stop_payment("Stop payment");
    private String callType;

    CallTypeEnum(String callType)
    {
        this.callType = callType;
    }

    public String getValue() {
        return callType;
    }
}

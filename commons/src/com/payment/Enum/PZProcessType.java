package com.payment.Enum;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 10/4/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PZProcessType
{
    SALE("Sale"),
    REFUND("Refund"),
    INQUIRY("Inquiry"),
    CANCEL("Cancel"),
    AUTHORIZATION("Authorization"),
    AUTH("Auth"),
    CAPTURE("Capture"),
    PAYOUT("Payout"),
    THREE_D_SALE("3D_Sale"),
    THREE_D_AUTH("3D_Auth"),
    REBILLING("Rebilling");

    private String status;

    PZProcessType(String status)
    {
       this.status=status;
    }


    @Override
    public String toString()
    {
        return status;
    }
}

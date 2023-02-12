package com.payment.response;

/**
 * Created by Admin on 5/9/15.
 */
public enum PZShortResponseStatus
{
    SUCCESS("Y"),
    authsuccessful("Y"),
    capturesuccess("Y"),
    setteled("Y"),
    settled("Y"),
    markedforreversal("Y"),
    reversed("Y"),
    chargeback("Y"),
    payoutsuccessful("Y"),
    success("Y"),

    PARTIAL_SUCCESS("P"),
    begun("P"),
    authstarted("P"),
    authstarted_3D("P"),
    authcancelled("P"),
    capturestarted("P"),
    payoutstarted("P"),

    cancelled("C"),

    FAILED("N"),
    authfailed("N"),
    capturefailed("N"),
    payoutfailed("N"),
    failed("N"),

    ERROR("E"),
    error("E"),
    ;

    private String status;

    PZShortResponseStatus(String status)
    {
        this.status = status;
    }


    @Override
    public String toString()
    {
        return status;
    }
}

package com.payment.response;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public enum PZResponseStatus
{
    SUCCESS,
    FAILED,
    PENDING,
    ACCEPTED,          //Added for Sofort refund
    ERROR,
    REVERSED,
    SETTLED,
    CHARGEBACK,
    CANCELLED,
    PARTIALREFUND,
    PAYOUTSUCCESSFUL,      //added for international integrations
    PAYOUTFAILED

;

}

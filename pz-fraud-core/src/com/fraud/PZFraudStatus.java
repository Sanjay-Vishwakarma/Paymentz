package com.fraud;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 10/7/14
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PZFraudStatus
{
    PROCESS_SUCCESSFULLY("Process Successfully"),
    PROCESS_FAILED("Process Failed"),
    PENDING("Pending");

    private String status;

    PZFraudStatus(String status)
    {
        this.status = status;
    }
    @Override
    public String toString()
    {
        return status;
    }
}



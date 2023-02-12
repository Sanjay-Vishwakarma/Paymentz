package com.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/4/15
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ApplicationStatus
{
    SAVED("SAVED"),
    SUBMIT("SUBMIT"),
    STEP1_SUBMIT("STEP1_SUBMIT"),
    STEP1_SAVED("STEP1_SAVED"),
    MODIFIED("MODIFIED"),
    VERIFIED("VERIFIED");

    private String applicationstatus;

    ApplicationStatus(String ApplicationStatus)
    {
        this.applicationstatus=ApplicationStatus;
    }

    public String toString()
    {
        return applicationstatus;
    }
}

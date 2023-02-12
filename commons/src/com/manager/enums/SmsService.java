package com.manager.enums;

/**
 * Created by Kalyani on 03/02/2022.
 */
public enum SmsService
{
    N("N"),
    SMSGLOBAL("smsglobal"),
    FAST2SMS("fast2sms");


    private String smsService;
    SmsService(String smsService)
    {
        this.smsService = smsService;
    }
    @Override
    public String toString()
    {
        return smsService;
    }
}

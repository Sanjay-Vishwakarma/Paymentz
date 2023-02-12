package com.payment.borgun.core;

import com.directi.pg.Functions;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 6/20/14
 * Time: 1:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class BorgunErrorCodeClass
{
    private static HashMap<String, String> errorStringFromCode = new HashMap<String, String>();
    private static Functions functions=new Functions();
    static
    {
        errorStringFromCode.put("000","Success");
        errorStringFromCode.put("100","Do not honor (Not accepted)");
        errorStringFromCode.put("101","Expired card");
        errorStringFromCode.put("102","Suspected card forgery (fraud)");
        errorStringFromCode.put("103","Merchant call acquirer");
        errorStringFromCode.put("104","Restricted card");
        errorStringFromCode.put("106","Allowed PIN retries exceeded");
        errorStringFromCode.put("107","Merchant call issuer");
        errorStringFromCode.put("109","Merchant not identified");
        errorStringFromCode.put("110","Invalid amount");
        errorStringFromCode.put("111","Invalid card number");
        errorStringFromCode.put("112","PIN required");
        errorStringFromCode.put("116","Not sufficient funds");
        errorStringFromCode.put("117","Invalid PIN");
        errorStringFromCode.put("118","Unknown card");
        errorStringFromCode.put("119","Transaction not allowed to cardholder");
        errorStringFromCode.put("120","Transaction not allowed to terminal");
        errorStringFromCode.put("121","Exceeds limits to withdrawal");
        errorStringFromCode.put("125","Card not valid");
        errorStringFromCode.put("126","False PIN block");
        errorStringFromCode.put("129","Suspected fraud");
        errorStringFromCode.put("130","Invalid Track2");
        errorStringFromCode.put("131","Invalid Expiration Date");
        errorStringFromCode.put("161","DCC transaction not allowed to cardholder");
        errorStringFromCode.put("162","DCC cardholder currency not supported");
        errorStringFromCode.put("163","DCC exceeds time limit for withdrawal");
        errorStringFromCode.put("164","DCC transaction not allowed to terminal");
        errorStringFromCode.put("165","DCC not allowed to merchant");
        errorStringFromCode.put("166","DCC unknown error");
        errorStringFromCode.put("200","No not honor");
        errorStringFromCode.put("201","Card not valid");
        errorStringFromCode.put("202","Suspected card forgery (fraud)");
        errorStringFromCode.put("203","Merchant contact acquirer");
        errorStringFromCode.put("204","Limited card");
        errorStringFromCode.put("205","Merchant contact police");
        errorStringFromCode.put("206","Allowed PIN-retries exceeded");
        errorStringFromCode.put("207","Special occasion");
        errorStringFromCode.put("208","Lost card");
        errorStringFromCode.put("209","Stolen card");
        errorStringFromCode.put("210","Suspected fraud");
        errorStringFromCode.put("902","False transaction");
        errorStringFromCode.put("904","Form error");
        errorStringFromCode.put("907","Issuer not responding");
        errorStringFromCode.put("908","Message can not be routed");
        errorStringFromCode.put("909","System error");
        errorStringFromCode.put("910","Issuer did not respond");
        errorStringFromCode.put("911","Issuer timed out");
        errorStringFromCode.put("912","Issuer not reachable");
        errorStringFromCode.put("913","Double transaction transmission");
        errorStringFromCode.put("914","Original transaction can not be traced");
        errorStringFromCode.put("916","Merchant not found");
        errorStringFromCode.put("923","Request in progress");
        errorStringFromCode.put("950","No financial record found for detail data");
        errorStringFromCode.put("951","Batch already closed");
        errorStringFromCode.put("952","Invalid batch number");
        errorStringFromCode.put("953","Host timeout");
        errorStringFromCode.put("954","Batch not closed");
        errorStringFromCode.put("955","Merchant not active");
        errorStringFromCode.put("956","Transaction number not unique");

    }
    public static String getErrorStringFromCode(String errorCode)
    {
        String errorDescription=errorStringFromCode.get(errorCode.toUpperCase());
        if(functions.isValueNull(errorDescription)){return errorDescription;}
        else{return "Transaction declined";}
    }
}

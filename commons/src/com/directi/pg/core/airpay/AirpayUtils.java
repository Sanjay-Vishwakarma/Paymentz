package com.directi.pg.core.airpay;

/**
 * Created by Admin on 10/17/2021.
 */
public class AirpayUtils
{



    public String getPaymentType(String paymentMode)
    {
        String payMode = "";

        if("NBI".equalsIgnoreCase(paymentMode)||"NetBankingIndia".equalsIgnoreCase(paymentMode))
            payMode = "nb";

        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "upi";

        return payMode;
    }

}

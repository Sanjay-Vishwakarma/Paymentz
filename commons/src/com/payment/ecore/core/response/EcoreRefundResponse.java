package com.payment.ecore.core.response;


import com.payment.response.PZRefundResponse;


/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreRefundResponse extends PZRefundResponse
{
     String captureAmount;

    public String getCardHolderName()
    {
        return cardHolderName;
    }

    String cardHolderName;

    public void setCardHolderName(String cardHolderName)
    {
        this.cardHolderName = cardHolderName;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getCaptureAmount()
    {

        return captureAmount;
    }
}

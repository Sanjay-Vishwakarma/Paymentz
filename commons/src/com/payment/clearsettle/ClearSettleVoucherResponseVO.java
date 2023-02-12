package com.payment.clearsettle;

/**
 * Created by Roshan on 9/7/2017.
 */
public class ClearSettleVoucherResponseVO extends ClearSettleResponseVO
{
  String amount;
  String currency;
  String paymentMethod;
  String transactionId;
  String purchaseUrl;

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPurchaseUrl()
    {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl)
    {
        this.purchaseUrl = purchaseUrl;
    }
}

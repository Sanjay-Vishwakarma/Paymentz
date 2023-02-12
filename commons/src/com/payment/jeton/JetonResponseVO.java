package com.payment.jeton;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Uday on 8/11/17.
 */
public class JetonResponseVO extends CommResponseVO
{
    private String token;
    private String redirectUrl;
    private String paymentId;
    private String statusCode;
    private String orderId;
    private String checkout;

    public String getToken() {return token;}

    public void setToken(String token) {this.token = token;}

    public String getRedirectUrl() {return redirectUrl;}

    public void setRedirectUrl(String redirectUrl) {this.redirectUrl = redirectUrl;}

    public String getPaymentId() {return paymentId;}

    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

    public String getStatusCode() {return statusCode;}

    public void setStatusCode(String statusCode) {this.statusCode = statusCode;}

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getCheckout()
    {
        return checkout;
    }

    public void setCheckout(String checkout)
    {
        this.checkout = checkout;
    }
}

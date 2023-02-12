package com.payment.vouchermoney;

import com.payment.common.core.CommResponseVO;

/**
 * Created by my pc on 6/30/2017.
 */
public class VoucherMoneyResponse extends CommResponseVO
{
    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private String paymentFormUrl;
    private String userMessage;
    private String userCode;

    private boolean newVoucherGenerated;
    private String newVoucherAmount;
    private String newVoucherCurrency;
    private String newVoucherSerialNumber;
    private String redirectURL;
    private String errorMessage;
    private String tmpl_amount;
    private String tmpl_currency;

    private String merchantUsersCommission;//for payout commission
    private String commissionCurrency;//for payout customer currency
    private String commissionToPay;//for deposit customer commission sent in response
    private String commissionPaidCurrency;//for deposit customer commission sent in response

    public String getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(String responseCode)
    {
        this.responseCode = responseCode;
    }

    public String getResponseStatus()
    {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus)
    {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage()
    {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage)
    {
        this.responseMessage = responseMessage;
    }

    public String getPaymentFormUrl()
    {
        return paymentFormUrl;
    }

    public void setPaymentFormUrl(String paymentFormUrl)
    {
        this.paymentFormUrl = paymentFormUrl;
    }

    public String getUserMessage()
    {
        return userMessage;
    }

    public void setUserMessage(String userMessage)
    {
        this.userMessage = userMessage;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public boolean isNewVoucherGenerated()
    {
        return newVoucherGenerated;
    }

    public void setNewVoucherGenerated(boolean newVoucherGenerated)
    {
        this.newVoucherGenerated = newVoucherGenerated;
    }

    public String getNewVoucherAmount()
    {
        return newVoucherAmount;
    }

    public void setNewVoucherAmount(String newVoucherAmount)
    {
        this.newVoucherAmount = newVoucherAmount;
    }

    public String getNewVoucherCurrency(){return newVoucherCurrency;}

    public void setNewVoucherCurrency(String newVoucherCurrency){this.newVoucherCurrency = newVoucherCurrency;}

    public String getNewVoucherSerialNumber(){return newVoucherSerialNumber;}

    public void setNewVoucherSerialNumber(String newVoucherSerialNumber) {this.newVoucherSerialNumber = newVoucherSerialNumber;}

    public String getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getTmpl_amount()
    {
        return tmpl_amount;
    }

    public void setTmpl_amount(String tmpl_amount)
    {
        this.tmpl_amount = tmpl_amount;
    }

    public String getTmpl_currency()
    {
        return tmpl_currency;
    }

    public void setTmpl_currency(String tmpl_currency)
    {
        this.tmpl_currency = tmpl_currency;
    }

    public String getMerchantUsersCommission()
    {
        return merchantUsersCommission;
    }

    public void setMerchantUsersCommission(String merchantUsersCommission)
    {
        this.merchantUsersCommission = merchantUsersCommission;
    }

    public String getCommissionCurrency()
    {
        return commissionCurrency;
    }

    public void setCommissionCurrency(String commissionCurrency)
    {
        this.commissionCurrency = commissionCurrency;
    }

    public String getCommissionToPay()
    {
        return commissionToPay;
    }

    public void setCommissionToPay(String commissionToPay)
    {
        this.commissionToPay = commissionToPay;
    }

    public String getCommissionPaidCurrency()
    {
        return commissionPaidCurrency;
    }

    public void setCommissionPaidCurrency(String commissionPaidCurrency)
    {
        this.commissionPaidCurrency = commissionPaidCurrency;
    }
}

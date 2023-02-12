package com.manager.vo;

/**
 * Created by Admin on 5/18/2020.
 */
public class PartnerEmailNotificationVO
{
    String salesBillingDescriptor;
    String merchantSalesTransaction;
    String salesAdminFailedTransaction;
    String salesPartnerCardRegistration;
    String salesMerchantCardRegistration;
    String salesPayoutTransaction;
    String fraudFailedTransaction;
    String chargebackTransaction;
    String refundTransaction;

    public String getSalesBillingDescriptor()
    {
        return salesBillingDescriptor;
    }

    public void setSalesBillingDescriptor(String salesBillingDescriptor)
    {
        this.salesBillingDescriptor = salesBillingDescriptor;
    }

    public String getMerchantSalesTransaction()
    {
        return merchantSalesTransaction;
    }

    public void setMerchantSalesTransaction(String merchantSalesTransaction)
    {
        this.merchantSalesTransaction = merchantSalesTransaction;
    }

    public String getSalesAdminFailedTransaction()
    {
        return salesAdminFailedTransaction;
    }

    public void setSalesAdminFailedTransaction(String salesAdminFailedTransaction)
    {
        this.salesAdminFailedTransaction = salesAdminFailedTransaction;
    }

    public String getSalesPartnerCardRegistration()
    {
        return salesPartnerCardRegistration;
    }

    public void setSalesPartnerCardRegistration(String salesPartnerCardRegistration)
    {
        this.salesPartnerCardRegistration = salesPartnerCardRegistration;
    }

    public String getSalesMerchantCardRegistration()
    {
        return salesMerchantCardRegistration;
    }

    public void setSalesMerchantCardRegistration(String salesMerchantCardRegistration)
    {
        this.salesMerchantCardRegistration = salesMerchantCardRegistration;
    }

    public String getSalesPayoutTransaction()
    {
        return salesPayoutTransaction;
    }

    public void setSalesPayoutTransaction(String salesPayoutTransaction)
    {
        this.salesPayoutTransaction = salesPayoutTransaction;
    }

    public String getFraudFailedTransaction()
    {
        return fraudFailedTransaction;
    }

    public void setFraudFailedTransaction(String fraudFailedTransaction)
    {
        this.fraudFailedTransaction = fraudFailedTransaction;
    }

    public String getChargebackTransaction()
    {
        return chargebackTransaction;
    }

    public void setChargebackTransaction(String chargebackTransaction)
    {
        this.chargebackTransaction = chargebackTransaction;
    }

    public String getRefundTransaction()
    {
        return refundTransaction;
    }

    public void setRefundTransaction(String refundTransaction)
    {
        this.refundTransaction = refundTransaction;
    }
}

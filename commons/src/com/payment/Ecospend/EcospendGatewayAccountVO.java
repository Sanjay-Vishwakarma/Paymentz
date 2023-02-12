package com.payment.Ecospend;

/**
 * Created by Admin on 06-Oct-21.
 */
public class EcospendGatewayAccountVO
{
    String accountId;
    String Reference;
    String creditor_account_type;
    String creditor_account_identification;
    String creditor_account_owner_name;
    String creditor_account_currency;
    String creditor_account_bic;
    String scheduled_for;
    String psu_id;
    String payment_rails;
    String send_email_notification;
    String send_sms_notification;

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getReference()
    {
        return Reference;
    }

    public void setReference(String reference)
    {
        Reference = reference;
    }

    public String getCreditor_account_type()
    {
        return creditor_account_type;
    }

    public void setCreditor_account_type(String creditor_account_type)
    {
        this.creditor_account_type = creditor_account_type;
    }

    public String getCreditor_account_identification()
    {
        return creditor_account_identification;
    }

    public void setCreditor_account_identification(String creditor_account_identification)
    {
        this.creditor_account_identification = creditor_account_identification;
    }

    public String getCreditor_account_owner_name()
    {
        return creditor_account_owner_name;
    }

    public void setCreditor_account_owner_name(String creditor_account_owner_name)
    {
        this.creditor_account_owner_name = creditor_account_owner_name;
    }

    public String getCreditor_account_currency()
    {
        return creditor_account_currency;
    }

    public void setCreditor_account_currency(String creditor_account_currency)
    {
        this.creditor_account_currency = creditor_account_currency;
    }

    public String getCreditor_account_bic()
    {
        return creditor_account_bic;
    }

    public void setCreditor_account_bic(String creditor_account_bic)
    {
        this.creditor_account_bic = creditor_account_bic;
    }

    public String getScheduled_for()
    {
        return scheduled_for;
    }

    public void setScheduled_for(String scheduled_for)
    {
        this.scheduled_for = scheduled_for;
    }

    public String getPsu_id()
    {
        return psu_id;
    }

    public void setPsu_id(String psu_id)
    {
        this.psu_id = psu_id;
    }

    public String getPayment_rails() { return payment_rails;}

    public void setPayment_rails(String payment_rails)
    {
        this.payment_rails = payment_rails;
    }

    public String getSend_email_notification() { return send_email_notification; }

    public void setSend_email_notification(String send_email_notification) { this.send_email_notification = send_email_notification; }

    public String getSend_sms_notification() { return send_sms_notification; }

    public void setSend_sms_notification(String send_sms_notification) { this.send_sms_notification = send_sms_notification; }
}

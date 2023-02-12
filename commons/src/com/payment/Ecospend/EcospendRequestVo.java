package com.payment.Ecospend;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Admin on 9/9/2021.
 */
public class EcospendRequestVo extends CommRequestVO
{
    String accessTken;
    String bankid;
    String reference;
    String creditortype;
    String creditorID;
    String creditorCurrency;
    String creditorName;
    String CreditorBic;
    String debtortype;
    String debtorID;
    String debtorCurrency;
    String debtorName;
    String debtorBic;
    String psuid;
    String paymentrails;
    String debtoraccount;
    String scheduledFor;
    String scheduledForDate;
    String paymentMethod;
    String send_email_notification;
    String send_sms_notification;
    String first_payment_date;
    String number_of_payments;
    String Period;
    String firstPaymentAmount="";
    String lastPaymentAmount="";
    String email="";
    String phonecc="";
    String hostUrl="";
    String isRefundAllow;

    public String getIsRefundAllow()
    {
        return isRefundAllow;
    }

    public void setIsRefundAllow(String isRefundAllow)
    {
        this.isRefundAllow = isRefundAllow;
    }

    public String getDebtoraccount()
    {
        return debtoraccount;
    }

    public void setDebtoraccount(String debtoraccount)
    {
        this.debtoraccount = debtoraccount;
    }

    public String getCreditortype()
    {
        return creditortype;
    }

    public void setCreditortype(String creditortype)
    {
        this.creditortype = creditortype;
    }

    public String getBankid()
    {
        return bankid;
    }

    public void setBankid(String bankid)
    {
        this.bankid = bankid;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getCreditorID()
    {
        return creditorID;
    }

    public void setCreditorID(String creditorID)
    {
        this.creditorID = creditorID;
    }

    public String getCreditorCurrency()
    {
        return creditorCurrency;
    }

    public void setCreditorCurrency(String creditorCurrency)
    {
        this.creditorCurrency = creditorCurrency;
    }

    public String getCreditorName()
    {
        return creditorName;
    }

    public void setCreditorName(String creditorName)
    {
        this.creditorName = creditorName;
    }

    public String getCreditorBic()
    {
        return CreditorBic;
    }

    public void setCreditorBic(String creditorBic)
    {
        CreditorBic = creditorBic;
    }

    public String getDebtortype()
    {
        return debtortype;
    }

    public void setDebtortype(String debtortype)
    {
        this.debtortype = debtortype;
    }

    public String getDebtorID()
    {
        return debtorID;
    }

    public void setDebtorID(String debtorID)
    {
        this.debtorID = debtorID;
    }

    public String getDebtorCurrency()
    {
        return debtorCurrency;
    }

    public void setDebtorCurrency(String debtorCurrency)
    {
        this.debtorCurrency = debtorCurrency;
    }

    public String getDebtorName()
    {
        return debtorName;
    }

    public void setDebtorName(String debtorName)
    {
        this.debtorName = debtorName;
    }

    public String getDebtorBic()
    {
        return debtorBic;
    }

    public void setDebtorBic(String debtorBic)
    {
        this.debtorBic = debtorBic;
    }

    public String getPsuid()
    {
        return psuid;
    }

    public void setPsuid(String psuid)
    {
        this.psuid = psuid;
    }

    public String getPaymentrails()
    {
        return paymentrails;
    }

    public void setPaymentrails(String paymentrails)
    {
        this.paymentrails = paymentrails;
    }

    public String getAccessTken()
    {
        return accessTken;
    }

    public void setAccessTken(String accessTken)
    {
        this.accessTken = accessTken;
    }

    public String getScheduledFor() { return scheduledFor; }

    public void setScheduledFor(String scheduledFor) { this.scheduledFor = scheduledFor; }

    public String getScheduledForDate() { return scheduledForDate; }

    public void setScheduledForDate(String scheduledForDate) { this.scheduledForDate = scheduledForDate; }

    public String getPaymentMethod() { return paymentMethod; }

    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod;}

    public String getSend_email_notification() { return send_email_notification; }

    public void setSend_email_notification(String send_email_notification) { this.send_email_notification = send_email_notification; }

    public String getSend_sms_notification() { return send_sms_notification; }

    public void setSend_sms_notification(String send_sms_notification) { this.send_sms_notification = send_sms_notification; }

    public String getFirst_payment_date() { return first_payment_date; }

    public void setFirst_payment_date(String first_payment_date) { this.first_payment_date = first_payment_date;}

    public String getNumber_of_payments() { return number_of_payments;}

    public void setNumber_of_payments(String number_of_payments) { this.number_of_payments = number_of_payments;}

    public String getPeriod() { return Period; }

    public void setPeriod(String period) { Period = period;}

    public String getFirstPaymentAmount() { return firstPaymentAmount;}

    public void setFirstPaymentAmount(String firstPaymentAmount) { this.firstPaymentAmount = firstPaymentAmount;}

    public String getLastPaymentAmount() { return lastPaymentAmount;}

    public void setLastPaymentAmount(String lastPaymentAmount) { this.lastPaymentAmount = lastPaymentAmount;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPhonecc() {return phonecc;}

    public void setPhonecc(String phonecc) {this.phonecc = phonecc;}

    public String getHostUrl()
    {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl)
    {
        this.hostUrl = hostUrl;
    }
}

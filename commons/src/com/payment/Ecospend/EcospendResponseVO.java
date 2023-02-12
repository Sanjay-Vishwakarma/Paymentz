package com.payment.Ecospend;

import com.payment.common.core.Comm3DResponseVO;

import java.util.TreeMap;

/**
 * Created by Admin on 9/4/2021.
 */
public class EcospendResponseVO extends Comm3DResponseVO
{
    TreeMap<String,String> bankid;
    String accessTken;
    String id;
    String bank_id;
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
    String getrefundinfo;
    String for_payout;
    String scheduled_for;
    String description;
    String first_payment_date;
    String standing_order_type;
    String number_of_payments;
    String period;

    public String getBank_id()
    {
        return bank_id;
    }

    public void setBank_id(String bank_id)
    {
        this.bank_id = bank_id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getReference()
    {
        return reference;
    }

    public void setReference(String reference)
    {
        this.reference = reference;
    }

    public String getCreditortype()
    {
        return creditortype;
    }

    public void setCreditortype(String creditortype)
    {
        this.creditortype = creditortype;
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

    public String getDebtoraccount()
    {
        return debtoraccount;
    }

    public void setDebtoraccount(String debtoraccount)
    {
        this.debtoraccount = debtoraccount;
    }

    public String getGetrefundinfo()
    {
        return getrefundinfo;
    }

    public void setGetrefundinfo(String getrefundinfo)
    {
        this.getrefundinfo = getrefundinfo;
    }

    public String getFor_payout()
    {
        return for_payout;
    }

    public void setFor_payout(String for_payout)
    {
        this.for_payout = for_payout;
    }

    public String getScheduled_for()
    {
        return scheduled_for;
    }

    public void setScheduled_for(String scheduled_for)
    {
        this.scheduled_for = scheduled_for;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    public TreeMap<String, String> getBankid()
    {
        return bankid;
    }

    public void setBankid(TreeMap<String, String> bankid)
    {
        this.bankid = bankid;
    }

    public String getAccessTken()
    {
        return accessTken;
    }

    public void setAccessTken(String accessTken)
    {
        this.accessTken = accessTken;
    }

    public String getFirst_payment_date() { return first_payment_date;}

    public void setFirst_payment_date(String first_payment_date) { this.first_payment_date = first_payment_date; }

    public String getStanding_order_type() { return standing_order_type;}

    public void setStanding_order_type(String standing_order_type) { this.standing_order_type = standing_order_type;}

    public String getNumber_of_payments() { return number_of_payments;}

    public void setNumber_of_payments(String number_of_payments) { this.number_of_payments = number_of_payments;}

    public String getPeriod() { return period;}

    public void setPeriod(String period) { this.period = period;}
}

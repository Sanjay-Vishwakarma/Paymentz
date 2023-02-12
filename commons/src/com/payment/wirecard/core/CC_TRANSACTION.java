package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("CC_TRANSACTION")
public class CC_TRANSACTION
{
    @XStreamAlias("ReferenceTransactionID")
    String referenceTransactionID;

    @XStreamAlias("mode")
    @XStreamAsAttribute
    String Mode;

    @XStreamAlias("TransactionID")
    String transactionID;

    @XStreamAlias("SalesDate")
    String salesDate;

    @XStreamAlias("Amount")
    String amount;

    @XStreamAlias("Currency")
    String currency;

    @XStreamAlias("CountryCode")
    String countryCode;

    @XStreamAlias("GuWID")
    String guWID;

    @XStreamAlias("Usage")
    String usage;

    @XStreamAlias("CORPTRUSTCENTER_DATA")
    CORPTRUSTCENTER_DATA corptrustcenter_data;

    @XStreamAlias("RECURRING_TRANSACTION")
    RECURRING_TRANSACTION recurring_transaction;

    @XStreamAlias("CREDIT_CARD_DATA")
    CREDIT_CARD_DATA credit_card_data;

    @XStreamAlias("CONTACT_DATA")
    CONTACT_DATA contact_data;


    @XStreamAlias("PROCESSING_STATUS")
    PROCESSING_STATUS processing_status;

    @XStreamAlias("AuthorizationCode")
    String AuthorizationCode;

    public String getReferenceTransactionID()
    {
        return referenceTransactionID;
    }

    public void setReferenceTransactionID(String referenceTransactionID)
    {
        this.referenceTransactionID = referenceTransactionID;
    }

    public String getAuthorizationCode()
    {
        return AuthorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        AuthorizationCode = authorizationCode;
    }


    public CORPTRUSTCENTER_DATA getCorptrustcenter_data()
    {
        return corptrustcenter_data;
    }

    public void setCorptrustcenter_data(CORPTRUSTCENTER_DATA corptrustcenter_data)
    {
        this.corptrustcenter_data = corptrustcenter_data;
    }

    public String getMode()
    {
        return Mode;
    }

    public void setMode(String mode)
    {
        Mode = mode;
    }

    public String getTransactionID()
    {
        return transactionID;
    }

    public void setTransactionID(String transactionID)
    {
        this.transactionID = transactionID;
    }

    public String getSalesDate()
    {
        return salesDate;
    }

    public void setSalesDate(String salesDate)
    {
        this.salesDate = salesDate;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getUsage()
    {
        return usage;
    }

    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    public RECURRING_TRANSACTION getRecurring_transaction()
    {
        return recurring_transaction;
    }

    public void setRecurring_transaction(RECURRING_TRANSACTION recurring_transaction)
    {
        this.recurring_transaction = recurring_transaction;
    }

    public CREDIT_CARD_DATA getCredit_card_data()
    {
        return credit_card_data;
    }

    public void setCredit_card_data(CREDIT_CARD_DATA credit_card_data)
    {
        this.credit_card_data = credit_card_data;
    }

    public CONTACT_DATA getContact_data()
    {
        return contact_data;
    }

    public void setContact_data(CONTACT_DATA contact_data)
    {
        this.contact_data = contact_data;
    }

    public String getGuWID()
    {
        return guWID;
    }

    public void setGuWID(String guWID)
    {
        this.guWID = guWID;
    }

    public PROCESSING_STATUS getProcessing_status()
    {
        return processing_status;
    }

    public void setProcessing_status(PROCESSING_STATUS processing_status)
    {
        this.processing_status = processing_status;
    }
}

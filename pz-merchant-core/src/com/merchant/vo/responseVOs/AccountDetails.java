package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="accountDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountDetails
{
    @XmlElement(name="accountSummary")
    private String accountSummary;

    @XmlElement(name="chargesSummary")
    private String chargesSummary;

    @XmlElement(name="transactionSummary")
    private String transactionSummary;

    @XmlElement(name="reports")
    private String reports;

    public String getAccountSummary()
    {
        return accountSummary;
    }

    public void setAccountSummary(String accountSummary)
    {
        this.accountSummary = accountSummary;
    }

    public String getChargesSummary()
    {
        return chargesSummary;
    }

    public void setChargesSummary(String chargesSummary)
    {
        this.chargesSummary = chargesSummary;
    }

    public String getTransactionSummary()
    {
        return transactionSummary;
    }

    public void setTransactionSummary(String transactionSummary)
    {
        this.transactionSummary = transactionSummary;
    }

    public String getReports()
    {
        return reports;
    }

    public void setReports(String reports)
    {
        this.reports = reports;
    }

    @Override
    public String toString()
    {
        return "AccountDetails{" +
                "accountSummary='" + accountSummary + '\'' +
                ", chargesSummary='" + chargesSummary + '\'' +
                ", transactionSummary='" + transactionSummary + '\'' +
                ", reports='" + reports + '\'' +
                '}';
    }
}

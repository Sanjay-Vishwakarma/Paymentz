package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Nikita on 3/5/2016.
 */
@XmlRootElement(name="bankAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankAccountVO
{
    @XmlElement(name="bic")
    String bic;

    @XmlElement(name="iban")
    String iban;

    @XmlElement(name="country")
    String country;

    @XmlElement(name="bankName")
    String bankName;

    @XmlElement(name="accountNumber")
    String accountNumber;

    @XmlElement(name="bankCode")
    String bankCode;

    @XmlElement(name="transactionDueDate")
    String transactionDueDate;

    @XmlElement(name="mandate")
    String mandate;

    @XmlElement(name="accountType")
    String accountType;

    @XmlElement(name="routingNumber")
    String routingNumber;

    @XmlElement(name="mandateId")
    String mandateId;

    @XmlElement(name="checkNumber")
    String checkNumber;

    @XmlElement(name="bankAccountName")
    String bankAccountName;

    @XmlElement(name="transferType")
    String transferType;

    @XmlElement(name="bankIfsc")
    String bankIfsc;

    @XmlElement(name="bankAccountNumber")
    String bankAccountNumber;

    @XmlElement(name="branchName")
    String branchName;

    @XmlElement(name="branchCode")
    String branchCode;

    public String getBranchName()
    {
        return branchName;
    }

    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    public String getBranchCode()
    {
        return branchCode;
    }

    public void setBranchCode(String branchCode)
    {
        this.branchCode = branchCode;
    }

    public String getBic()
    {
        return bic;
    }

    public void setBic(String bic)
    {
        this.bic = bic;
    }

    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public String getTransactionDueDate()
    {
        return transactionDueDate;
    }

    public void setTransactionDueDate(String transactionDueDate)
    {
        this.transactionDueDate = transactionDueDate;
    }

    public String getMandate()
    {
        return mandate;
    }

    public void setMandate(String mandate)
    {
        this.mandate = mandate;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getRoutingNumber()
    {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber)
    {
        this.routingNumber = routingNumber;
    }

    public String getMandateId()
    {
        return mandateId;
    }

    public void setMandateId(String mandateId)
    {
        this.mandateId = mandateId;
    }

    public String getCheckNumber()
    {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber)
    {
        this.checkNumber = checkNumber;
    }

    public String getBankAccountName()
    {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName)
    {
        this.bankAccountName = bankAccountName;
    }

    public String getTransferType()
    {
        return transferType;
    }

    public void setTransferType(String transferType)
    {
        this.transferType = transferType;
    }

    public String getBankIfsc()
    {
        return bankIfsc;
    }

    public void setBankIfsc(String bankIfsc)
    {
        this.bankIfsc = bankIfsc;
    }

    public String getBankAccountNumber()
    {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber)
    {
        this.bankAccountNumber = bankAccountNumber;
    }
}

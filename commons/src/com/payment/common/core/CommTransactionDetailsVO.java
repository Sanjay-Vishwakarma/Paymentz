package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericTransDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/4/13
 * Time: 1:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommTransactionDetailsVO extends GenericTransDetailsVO
{
    private String detailId;
    private String previousTransactionId;
    private String prevTransactionStatus;
    private String responsetime;
    private String customerId;
    private String customerBankId;
    private String templateAmount;
    private String templateCurrency;
    private String terminalId;
    private String previousTransactionAmount;
    private String paymentId;
    private String merchantOrderId;
    private String payoutType;
    private String version;
    private String reversedAmount;
    private String remainingAmount;

    String customerBankCode;
    String customerBankAccountNumber;
    String customerBankAccountName;

    String bankTransferType;
    String bankAccountNo;
    String bankIfsc;
    String cres;
    String vpaAddress;

    String sessionId;
    String sessionCode;
    private String bankName;
    private String branchName;
    private String branchCode;
    private String bankCode;
    private String accountType;

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

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getReversedAmount()
    {
        return reversedAmount;
    }

    public void setReversedAmount(String reversedAmount)
    {
        this.reversedAmount = reversedAmount;
    }

    public String getPreviousTransactionAmount()
    {
        return previousTransactionAmount;
    }

    public void setPreviousTransactionAmount(String previousTransactionAmount)
    {
        this.previousTransactionAmount = previousTransactionAmount;
    }

    public String getDetailId()
    {
        return detailId;
    }

    public void setDetailId(String detailId)
    {
        this.detailId = detailId;
    }

    public String getPreviousTransactionId()
    {
        return previousTransactionId;
    }

    public void setPreviousTransactionId(String previousTransactionId)
    {
        this.previousTransactionId = previousTransactionId;
    }

    public String getPrevTransactionStatus()
    {
        return prevTransactionStatus;
    }

    public void setPrevTransactionStatus(String prevTransactionStatus)
    {
        this.prevTransactionStatus = prevTransactionStatus;
    }

    public String getResponsetime()
    {
        return responsetime;
    }

    public void setResponsetime(String responsetime)
    {
        this.responsetime = responsetime;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerBankId()
    {
        return customerBankId;
    }

    public void setCustomerBankId(String customerBankId)
    {
        this.customerBankId = customerBankId;
    }

    public String getTemplateAmount()
    {
        return templateAmount;
    }

    public void setTemplateAmount(String templateAmount)
    {
        this.templateAmount = templateAmount;
    }

    public String getTemplateCurrency()
    {
        return templateCurrency;
    }

    public void setTemplateCurrency(String templateCurrency)
    {
        this.templateCurrency = templateCurrency;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getMerchantOrderId()
    {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId)
    {
        this.merchantOrderId = merchantOrderId;
    }

    public String getPayoutType(){
        return payoutType;
    }

    public void setPayoutType(String payoutType){
        this.payoutType=payoutType;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCustomerBankCode()
    {
        return customerBankCode;
    }

    public void setCustomerBankCode(String customerBankCode)
    {
        this.customerBankCode = customerBankCode;
    }

    public String getCustomerBankAccountNumber()
    {
        return customerBankAccountNumber;
    }

    public void setCustomerBankAccountNumber(String customerBankAccountNumber)
    {
        this.customerBankAccountNumber = customerBankAccountNumber;
    }

    public String getCustomerBankAccountName()
    {
        return customerBankAccountName;
    }

    public void setCustomerBankAccountName(String customerBankAccountName)
    {
        this.customerBankAccountName = customerBankAccountName;
    }

    public String getRemainingAmount()
    {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount)
    {
        this.remainingAmount = remainingAmount;
    }

    public String getBankTransferType()
    {
        return bankTransferType;
    }

    public void setBankTransferType(String bankTransferType)
    {
        this.bankTransferType = bankTransferType;
    }

    public String getBankAccountNo()
    {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo)
    {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankIfsc()
    {
        return bankIfsc;
    }

    public void setBankIfsc(String bankIfsc)
    {
        this.bankIfsc = bankIfsc;
    }

    public String getCres()
    {
        return cres;
    }

    public void setCres(String cres)
    {
        this.cres = cres;
    }

    public String getVpaAddress()
    {
        return vpaAddress;
    }

    public void setVpaAddress(String vpaAddress)
    {
        this.vpaAddress = vpaAddress;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getSessionCode()
    {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode)
    {
        this.sessionCode = sessionCode;
    }
}
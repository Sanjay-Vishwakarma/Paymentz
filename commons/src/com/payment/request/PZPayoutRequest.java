package com.payment.request;

/**
 * Created by admin on 7/21/2017.
 */
public class PZPayoutRequest extends PZRequest
{
    String payoutAmount;
    String payoutCurrency;
    String orderId;
    String orderDescription;
    String terminalId;
    String header;
    String customerEmail;
    String customerId;
    String customerBankId;
    String customerAccount;
    String payoutReason;
    String expDateOffset;
    String tmpl_currency;
    String tmpl_amount;
    String payoutType;
    String notificationUrl;
    String walletId;
    String walletAmount;
    String walletCurrency;
    String customerBitcoinAddress;
    String status;

    String customerBankCode;
    String customerBankAccountNumber;
    String customerBankAccountName;
    String bankTransferType;
    String bankAccountNo;
    String bankIfsc;
    String bankName;
    String branchName;
    String branchCode;
    String bankCode;
    String accountType;
    String phone;
    String phoneCountryCode;
    String customerCountry;
    String id;
    String fileName;

    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

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

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
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

    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public String getPayoutCurrency()
    {
        return payoutCurrency;
    }

    public void setPayoutCurrency(String payoutCurrency)
    {
        this.payoutCurrency = payoutCurrency;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail)
    {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAccount()
    {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount)
    {
        this.customerAccount = customerAccount;
    }

    public String getPayoutReason() {return payoutReason;}

    public void setPayoutReason(String payoutReason) {this.payoutReason = payoutReason;}

    public String getExpDateOffset()
    {
        return expDateOffset;
    }

    public void setExpDateOffset(String expDateOffset)
    {
        this.expDateOffset = expDateOffset;
    }

    public String getTmpl_currency()
    {
        return tmpl_currency;
    }

    public void setTmpl_currency(String tmpl_currency)
    {
        this.tmpl_currency = tmpl_currency;
    }

    public String getTmpl_amount()
    {
        return tmpl_amount;
    }

    public void setTmpl_amount(String tmpl_amount)
    {
        this.tmpl_amount = tmpl_amount;
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

    public String getPayoutType()
    {
        return payoutType;
    }

    public void setPayoutType(String payoutType)
    {
        this.payoutType = payoutType;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getWalletId()
    {
        return walletId;
    }

    public void setWalletId(String walletId)
    {
        this.walletId = walletId;
    }

    public String getWalletAmount()
    {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount)
    {
        this.walletAmount = walletAmount;
    }

    public String getWalletCurrency()
    {
        return walletCurrency;
    }

    public void setWalletCurrency(String walletCurrency)
    {
        this.walletCurrency = walletCurrency;
    }

    public String getCustomerBitcoinAddress()
    {
        return customerBitcoinAddress;
    }

    public void setCustomerBitcoinAddress(String customerBitcoinAddress)
    {
        this.customerBitcoinAddress = customerBitcoinAddress;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhoneCountryCode()
    {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode)
    {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getCustomerCountry()
    {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry)
    {
        this.customerCountry = customerCountry;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
}
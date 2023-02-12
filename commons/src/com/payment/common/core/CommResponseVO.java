package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/4/13
 * Time: 1:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommResponseVO extends GenericResponseVO
{
    private String merchantId;
    private String merchantOrderId;
    private String transactionId;
    private String transactionType;
    private String transactionStatus;
    private String status;           //Status to be set by gateway as Success or Fail
    private String errorCode;
    private String descriptor;
    private String description;
    private String responseTime;
    private String amount;
    private String ipaddress;
    private String currency;
    private String bankTransactionDate;
    private String authCode;
    private String responseHashInfo;
    private String remark;
    private String redirectUrl;
    private String errorName;
    private String eci;
    private String arn;
    private String tmpl_Amount;
    private String tmpl_Currency;
    private String walletId;
    private String walletAmount;
    private String walletCurrecny;
    private String bankCode;
    private String bankDescription;
    private String rrn;
    private String fromAccountId;
    private String fromMid;
    private String terminalId;
    private String fullname;
    private String bankaccount;
    private String ifsc;
    private String spkRefNo;
    private String bankRefNo;
    private String threeDVersion;
    private String paymentURL;

    public String getPaymentURL()
    {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL)
    {
        this.paymentURL = paymentURL;
    }

    public String getResponseHashInfo()
    {
        return responseHashInfo;
    }

    public void setResponseHashInfo(String responseHashInfo)
    {
        this.responseHashInfo = responseHashInfo;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }


    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMerchantOrderId()
    {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId)
    {
        this.merchantOrderId = merchantOrderId;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getResponseTime()
    {
        return responseTime;
    }

    public void setResponseTime(String responseTime)
    {
        this.responseTime = responseTime;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getBankTransactionDate()
    {
        return bankTransactionDate;
    }

    public void setBankTransactionDate(String bankTransactionDate)
    {
        this.bankTransactionDate = bankTransactionDate;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getRedirectUrl() {return redirectUrl;}

    public void setRedirectUrl(String redirectUrl) {this.redirectUrl = redirectUrl;}

    public String getErrorName()
    {
        return errorName;
    }

    public void setErrorName(String errorName)
    {
        this.errorName = errorName;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getTmpl_Amount()
    {
        return tmpl_Amount;
    }

    public void setTmpl_Amount(String tmpl_Amount)
    {
        this.tmpl_Amount = tmpl_Amount;
    }

    public String getTmpl_Currency()
    {
        return tmpl_Currency;
    }

    public void setTmpl_Currency(String tmpl_Currency)
    {
        this.tmpl_Currency = tmpl_Currency;
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

    public String getWalletCurrecny()
    {
        return walletCurrecny;
    }

    public void setWalletCurrecny(String walletCurrecny)
    {
        this.walletCurrecny = walletCurrecny;
    }

    public String getBankCode() {return bankCode;}

    public void setBankCode(String bankCode) {this.bankCode = bankCode;}

    public String getBankDescription() {return bankDescription;}

    public void setBankDescription(String bankDescription) {this.bankDescription = bankDescription;}

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getFromAccountId()
    {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId)
    {
        this.fromAccountId = fromAccountId;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getFromMid()
    {
        return fromMid;
    }

    public void setFromMid(String fromMid)
    {
        this.fromMid = fromMid;
    }

    public String getBankaccount()
    {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount)
    {
        this.bankaccount = bankaccount;
    }

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getIfsc()
    {
        return ifsc;
    }

    public void setIfsc(String ifsc)
    {
        this.ifsc = ifsc;
    }

    public String getSpkRefNo()
    {
        return spkRefNo;
    }

    public void setSpkRefNo(String spkRefNo)
    {
        this.spkRefNo = spkRefNo;
    }

    public String getBankRefNo()
    {
        return bankRefNo;
    }

    public void setBankRefNo(String bankRefNo)
    {
        this.bankRefNo = bankRefNo;
    }

    public String getThreeDVersion()
    {
        return threeDVersion;
    }

    public void setThreeDVersion(String threeDVersion)
    {
        this.threeDVersion = threeDVersion;
    }
}

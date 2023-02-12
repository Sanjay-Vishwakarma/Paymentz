package com.manager.vo;

import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 6/1/15
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectKitResponseVO
{
    String trackingId;
    String description;
    String amount;
    String refundAmount;
    String captureAmount;
    String status;
    String statusMsg;
    String billingDescriptor;
    String token;
    String generatedCheckSum;
    String flightMode;
    String splitTransactionId;
    String holder;
    String expMonth;
    String expYear;
    int tokenValidDays;
    String bankRedirectionUrl;
    String name;
    List<AsyncParameterVO> listOfAsyncParameterVo = new ArrayList<AsyncParameterVO>();
    String md;
    String paReq;
    String remark;
    String paymentBrand;
    String partnerId;
    String memberId;
    String invoiceId;
    String action;
    String secureKey;
    String email;
    String telno;
    String login;
    String contactPerson;
    List currencyList = new ArrayList();
    String country;
    String authToken;
    String voucherNumber;
    String tmpl_currency;
    String tmpl_amount;
    String custAccount;
    String custId;
    String custBankId;
    String isemailverified;
    String ismobileverified;
    String errorName;
    String payoutAmount;
    String walletAddress;
    MerchantDetailsVO merchantDetailsVO;
    String startDate;
    String endDate;
    String emiPeriod;
    String upiSupportInvoice;
    String upiQRSupportIinvoice;
    String paybylinkSpportInvoice;
    String AEPSSupportInvoice;

    String method;
    String target;
    String terminalId;
    String bankReferenceId;

    private boolean isFraud;

    //For VM
    String merchantUsersCommission;//for payout commission
    String commissionCurrency;//for payout customer currency
    String commissionToPay;//for deposit customer commission sent in response

    String initToken;
    String referenceId;
    Map<String,Map<String,Set<String>>> paymentCardTypeMap;
    Map<String,Map<String,Map<String,Object>>> dailySalesReport;

    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getPaReq()
    {
        return paReq;
    }

    public void setPaReq(String paReq)
    {
        this.paReq = paReq;
    }

    public String getMd()
    {
        return md;
    }

    public void setMd(String md)
    {
        this.md = md;
    }

    public void addListOfAsyncParameters(AsyncParameterVO asyncParameterVO)
    {
        this.listOfAsyncParameterVo.add(asyncParameterVO);
    }

    public List<AsyncParameterVO> getListOfAsyncParameterVo()
    {
        return listOfAsyncParameterVo;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    String value;


    public int getTokenValidDays()
    {
        return tokenValidDays;
    }

    public void setTokenValidDays(int validDays)
    {
        this.tokenValidDays = validDays;
    }

    public String getHolder()
    {
        return holder;
    }

    public void setHolder(String holder)
    {
        this.holder = holder;
    }

    public String getExpMonth()
    {
        return expMonth;
    }

    public void setExpMonth(String expMonth)
    {
        this.expMonth = expMonth;
    }

    public String getExpYear()
    {
        return expYear;
    }

    public void setExpYear(String expYear)
    {
        this.expYear = expYear;
    }

    ErrorCodeListVO errorCodeListVO;

    String cardnum;

    public String getCardnum()
    {
        return cardnum;
    }

    public void setCardnum(String cardnum)
    {
        this.cardnum = cardnum;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusMsg()
    {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg)
    {
        this.statusMsg = statusMsg;
    }

    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }

    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getGeneratedCheckSum()
    {
        return generatedCheckSum;
    }

    public void setGeneratedCheckSum(String generatedCheckSum)
    {
        this.generatedCheckSum = generatedCheckSum;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
    }

    public String getSplitTransactionId()
    {
        return splitTransactionId;
    }

    public void setSplitTransactionId(String splitTransactionId)
    {
        this.splitTransactionId = splitTransactionId;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }


    public String getBankRedirectionUrl()
    {
        return bankRedirectionUrl;
    }

    public void setBankRedirectionUrl(String bankRedirectionUrl)
    {
        this.bankRedirectionUrl = bankRedirectionUrl;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getSecureKey()
    {
        return secureKey;
    }

    public void setSecureKey(String secureKey)
    {
        this.secureKey = secureKey;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public List getCurrencyList()
    {
        return currencyList;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public void setCurrencyList(List currencyList)
    {
        this.currencyList = currencyList;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getVoucherNumber()
    {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber)
    {
        this.voucherNumber = voucherNumber;
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

    public String getCustAccount()
    {
        return custAccount;
    }

    public void setCustAccount(String custAccount)
    {
        this.custAccount = custAccount;
    }

    public String getCustId()
    {
        return custId;
    }

    public void setCustId(String custId)
    {
        this.custId = custId;
    }

    public String getCustBankId()
    {
        return custBankId;
    }

    public void setCustBankId(String custBankId)
    {
        this.custBankId = custBankId;
    }

    public String getIsemailverified()
    {
        return isemailverified;
    }

    public void setIsemailverified(String isemailverified)
    {
        this.isemailverified = isemailverified;
    }

    public String getIsmobileverified()
    {
        return ismobileverified;
    }

    public void setIsmobileverified(String ismobileverified)
    {
        this.ismobileverified = ismobileverified;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public String getErrorName()
    {
        return errorName;
    }

    public void setErrorName(String errorName)
    {
        this.errorName = errorName;
    }

    public String getMerchantUsersCommission()
    {
        return merchantUsersCommission;
    }

    public void setMerchantUsersCommission(String merchantUsersCommission)
    {
        this.merchantUsersCommission = merchantUsersCommission;
    }

    public String getCommissionCurrency()
    {
        return commissionCurrency;
    }

    public void setCommissionCurrency(String commissionCurrency)
    {
        this.commissionCurrency = commissionCurrency;
    }

    public String getCommissionToPay()
    {
        return commissionToPay;
    }

    public void setCommissionToPay(String commissionToPay)
    {
        this.commissionToPay = commissionToPay;
    }

    public boolean isFraud()
    {
        return isFraud;
    }

    public String getWalletAddress()
    {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress)
    {
        this.walletAddress = walletAddress;
    }

    public void setFraud(boolean fraud)
    {
        isFraud = fraud;
    }

    public String getStartDate() {return startDate;}

    public void setStartDate(String startDate) {this.startDate = startDate;}

    public String getEndDate() {return endDate;}

    public void setEndDate(String endDate) {this.endDate = endDate;}

    public String getEmiPeriod() {return emiPeriod;}

    public void setEmiPeriod(String emiPeriod) {this.emiPeriod = emiPeriod;}

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getInitToken()
    {
        return initToken;
    }

    public void setInitToken(String initToken)
    {
        this.initToken = initToken;
    }

    public String getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }

    public Map<String, Map<String, Set<String>>> getPaymentCardTypeMap()
    {
        return paymentCardTypeMap;
    }

    public void setPaymentCardTypeMap(Map<String, Map<String, Set<String>>> paymentCardTypeMap)
    {
        this.paymentCardTypeMap = paymentCardTypeMap;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getBankReferenceId()
    {
        return bankReferenceId;
    }

    public void setBankReferenceId(String bankReferenceId)
    {
        this.bankReferenceId = bankReferenceId;
    }
    public Map<String, Map<String, Map<String, Object>>> getDailySalesReport()
    {
        return dailySalesReport;
    }

    public void setDailySalesReport(Map<String, Map<String, Map<String, Object>>> dailySalesReport)
    {
        this.dailySalesReport = dailySalesReport;
    }

    public String getUpiSupportInvoice()
    {
        return upiSupportInvoice;
    }

    public void setUpiSupportInvoice(String upiSupportInvoice)
    {
        this.upiSupportInvoice = upiSupportInvoice;
    }

    public String getUpiQRSupportIinvoice()
    {
        return upiQRSupportIinvoice;
    }

    public void setUpiQRSupportIinvoice(String upiQRSupportIinvoice)
    {
        this.upiQRSupportIinvoice = upiQRSupportIinvoice;
    }

    public String getPaybylinkSpportInvoice()
    {
        return paybylinkSpportInvoice;
    }

    public void setPaybylinkSpportInvoice(String paybylinkSpportInvoice)
    {
        this.paybylinkSpportInvoice = paybylinkSpportInvoice;
    }

    public String getAEPSSupportInvoice()
    {
        return AEPSSupportInvoice;
    }

    public void setAEPSSupportInvoice(String AEPSSupportInvoice)
    {
        this.AEPSSupportInvoice = AEPSSupportInvoice;
    }
}
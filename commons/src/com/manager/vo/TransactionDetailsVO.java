package com.manager.vo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/4/14
 * Time: 7:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDetailsVO
{
    public String accountId;
    public String amount;
    public String captureAmount;
    public String currency;
    public String emailaddr;
    public String firstName;
    public String lastName;
    public String toid;
    public String totype;
    public String fromid;
    public String fromtype;
    public String description;
    public String redirectURL;
    public String paymentId;
    public String trackingid;
    public String orderDescription;
    public String status;
    public String paymodeId;
    public String cardTypeId;
    public String ipAddress;
    public String name;
    public String ccnum;
    public String expdate;
    public String cardtype;
    public String country;
    public String state;
    public String city;
    public String zip;
    public String street;
    public String telcc;
    public String telno;
    public String httpHeader;
    public String birthDate;
    public String language;
    public String transactionTime;
    public String firstSix;
    public String lastFour;
    public String templateamount;
    public String templatecurrency;
    public String notificationUrl;
    public String customerId;
    public String eci;
    public String terminalId;
    public String version;
    public String commissionToPay;
    public String commCurrency;
    public String billingDesc;
    public String secretKey;
    public String arn;
    public String refundAmount;
    public String chargebackAmount;
    public String emiCount;
    public String payoutamount;
    public String walletAmount;
    public String walletCurrency;
    public String remark;
    public String companyName;
    public String chargeBackInfo;
    public String issuingBank;
    public String dtstamp;
    public String action;
    public String actionExecutorId;
    public String actionExecutorName;
    public String reponseDescriptor;
    public String responseCode;
    public String responseHashInfo;
    public String count;
    public String errorCode;
    public String errorDescription;
    public String bankCode;
    public String bankDescription;
    public String refund_exists;
    public String transactionType;
    public String transactionNotification;
    public String transactionMode;
    public String authorization_code;
    public String customerIp;
    public String bankReferenceId;
    public String podBatch;
    private String redirectMethod;
    public String merchantNotificationUrl;
    private MerchantDetailsVO merchantDetailsVO;
    public String orderId;


    public String getMerchantNotificationUrl()
    {
        return merchantNotificationUrl;
    }

    public void setMerchantNotificationUrl(String merchantNotificationUrl)
    {
        this.merchantNotificationUrl = merchantNotificationUrl;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getRedirectMethod()
    {
        return redirectMethod;
    }

    public void setRedirectMethod(String redirectMethod)
    {
        this.redirectMethod = redirectMethod;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public List<MarketPlaceVO> marketPlaceVOList;

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
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

    public String getEmailaddr()
    {
        return emailaddr;
    }

    public void setEmailaddr(String emailaddr)
    {
        this.emailaddr = emailaddr;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getToid()
    {
        return toid;
    }

    public void setToid(String toid)
    {
        this.toid = toid;
    }

    public String getTotype()
    {
        return totype;
    }

    public void setTotype(String totype)
    {
        this.totype = totype;
    }

    public String getFromid()
    {
        return fromid;
    }

    public void setFromid(String fromid)
    {
        this.fromid = fromid;
    }

    public String getFromtype()
    {
        return fromtype;
    }

    public void setFromtype(String fromtype)
    {
        this.fromtype = fromtype;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPaymodeId()
    {
        return paymodeId;
    }

    public void setPaymodeId(String paymodeId)
    {
        this.paymodeId = paymodeId;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCcnum()
    {
        return ccnum;
    }

    public void setCcnum(String ccnum)
    {
        this.ccnum = ccnum;
    }

    public String getExpdate()
    {
        return expdate;
    }

    public void setExpdate(String expdate)
    {
        this.expdate = expdate;
    }

    public String getCardtype()
    {
        return cardtype;
    }

    public void setCardtype(String cardtype)
    {
        this.cardtype = cardtype;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getTelcc()
    {
        return telcc;
    }

    public void setTelcc(String telcc)
    {
        this.telcc = telcc;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getHttpHeader()
    {
        return httpHeader;
    }

    public void setHttpHeader(String httpHeader)
    {
        this.httpHeader = httpHeader;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getTransactionTime()
    {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime)
    {
        this.transactionTime = transactionTime;
    }

    public String getFirstSix()
    {
        return firstSix;
    }

    public void setFirstSix(String firstSix)
    {
        this.firstSix = firstSix;
    }

    public String getLastFour()
    {
        return lastFour;
    }

    public void setLastFour(String lastFour)
    {
        this.lastFour = lastFour;
    }

    public String getTemplateamount()
    {
        return templateamount;
    }

    public void setTemplateamount(String templateamount)
    {
        this.templateamount = templateamount;
    }

    public String getTemplatecurrency()
    {
        return templatecurrency;
    }

    public void setTemplatecurrency(String templatecurrency)
    {
        this.templatecurrency = templatecurrency;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCommissionToPay()
    {
        return commissionToPay;
    }

    public void setCommissionToPay(String commissionToPay)
    {
        this.commissionToPay = commissionToPay;
    }

    public String getCommCurrency()
    {
        return commCurrency;
    }

    public void setCommCurrency(String commCurrency)
    {
        this.commCurrency = commCurrency;
    }

    public String getBillingDesc()
    {
        return billingDesc;
    }

    public void setBillingDesc(String billingDesc)
    {
        this.billingDesc = billingDesc;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getEmiCount()
    {
        return emiCount;
    }

    public void setEmiCount(String emiCount)
    {
        this.emiCount = emiCount;
    }

    public String getPayoutamount()
    {
        return payoutamount;
    }

    public void setPayoutamount(String payoutamount)
    {
        this.payoutamount = payoutamount;
    }

    public String getWalletAmount() { return walletAmount; }

    public void setWalletAmount(String walletAmount) { this.walletAmount = walletAmount; }

    public String getWalletCurrency() { return walletCurrency; }

    public void setWalletCurrency(String walletCurrency) { this.walletCurrency = walletCurrency; }

    public String getRemark() {return remark;}

    public void setRemark(String remark) {this.remark = remark;}

    public String getCompanyName() {return companyName;}

    public void setCompanyName(String companyName) {this.companyName = companyName;}

    public String getChargeBackInfo() {return chargeBackInfo;}

    public void setChargeBackInfo(String chargeBackInfo) {this.chargeBackInfo = chargeBackInfo;}

    public String getIssuingBank() {return issuingBank;}

    public void setIssuingBank(String issuingBank) {this.issuingBank = issuingBank;}

    public String getDtstamp() {return dtstamp;}

    public void setDtstamp(String dtstamp) {this.dtstamp = dtstamp;}

    public String getAction() {return action;}

    public void setAction(String action) {this.action = action;}

    public String getActionExecutorId() {return actionExecutorId;}

    public void setActionExecutorId(String actionExecutorId) {this.actionExecutorId = actionExecutorId;}

    public String getActionExecutorName() {return actionExecutorName;}

    public void setActionExecutorName(String actionExecutorName) {this.actionExecutorName = actionExecutorName;}

    public String getReponseDescriptor() {return reponseDescriptor;}

    public void setReponseDescriptor(String reponseDescriptor) {this.reponseDescriptor = reponseDescriptor;}

    public String getResponseCode() {return responseCode;}

    public void setResponseCode(String responseCode) {this.responseCode = responseCode;}

    public String getResponseHashInfo() {return responseHashInfo;}

    public void setResponseHashInfo(String responseHashInfo) {this.responseHashInfo = responseHashInfo;}

    public String getCount() {return count;}

    public void setCount(String count) {this.count = count;}

    public String getErrorCode() {return errorCode;}

    public void setErrorCode(String errorCode) {this.errorCode = errorCode;}

    public String getErrorDescription() {return errorDescription;}

    public void setErrorDescription(String errorDescription) {this.errorDescription = errorDescription;}

    public String getBankCode()
    {
        return bankCode;
    }

    public void setBankCode(String bankCode)
    {
        this.bankCode = bankCode;
    }

    public String getBankDescription()
    {
        return bankDescription;
    }

    public void setBankDescription(String bankDescription)
    {
        this.bankDescription = bankDescription;
    }

    public String getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(String chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getRefund_exists()
    {
        return refund_exists;
    }

    public void setRefund_exists(String refund_exists)
    {
        this.refund_exists = refund_exists;
    }

    public List<MarketPlaceVO> getMarketPlaceVOList()
    {
        return marketPlaceVOList;
    }

    public void setMarketPlaceVOList(List<MarketPlaceVO> marketPlaceVOList)
    {
        this.marketPlaceVOList = marketPlaceVOList;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getTransactionNotification()
    {
        return transactionNotification;
    }

    public void setTransactionNotification(String transactionNotification)
    {
        this.transactionNotification = transactionNotification;
    }

    public String getTransactionMode()
    {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode)
    {
        this.transactionMode = transactionMode;
    }

    public String getAuthorization_code()
    {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code)
    {
        this.authorization_code = authorization_code;
    }

    public String getCustomerIp()
    {
        return customerIp;
    }

    public void setCustomerIp(String customerIp)
    {
        this.customerIp = customerIp;
    }

    public String getBankReferenceId()
    {
        return bankReferenceId;
    }

    public void setBankReferenceId(String bankReferenceId)
    {
        this.bankReferenceId = bankReferenceId;
    }

    public String getPodBatch()
    {
        return podBatch;
    }

    public void setPodBatch(String podBatch)
    {
        this.podBatch = podBatch;
    }
}
package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/4/14
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionVO
{
    String city;
    String state;
    String street;
    String zip;
    String telnocc;
    String telno;
    String trackingId;
    String transactionDate;
    String status;
    String custFirstName;
    String custLastName;
    String amount;
    String currency;
    String orderId;
    String orderDesc;
    String reverseAmount;
    //for transaction_common/qwipi/ecore
    String toid;
    String timestamp;
    String dtStamp;
    String fromid;
    String qwipiPaymentOrderNumber;
    String ecorePaymentOrderNumber;
    String accountId;
    String paymentId;
    String cardTypeId;
    String paymodeid;
    String remark;
    String reason;
    String refundAmount;
    String capAmount;
    String chargebackAmount;
    double captureAmount;
    long count;
    String updateStatusTo;
    String startDate;
    String endDate;
    String memberId;
    String partnerId;
    String gatewayName;
    int refundCount;
    //transaction_common_details table fields
    String detailId;
    String responseTransactionId;
    String isRetrivalRequest;
    String pgtypeid;
    String totalTransCount;
    String TransactionStatus;
    String toType;
    String terminalId;
    String cardNumber;
    String fraudRequest;
    String emailAddr;
    String ipAddress;
    String firstSix;
    String lastFour;
    String country;
    String name;
    String firstName;
    String lastName;
    String arn;
    String payoutAmount;
    String transactionReceiptImg;
    String authstartedCount;
    String authstartedAmount;
    String payoutCount;
    String begunCount;
    String begunAmount;
    String failedCount;
    String failedAmount;
    String markforreversalCount;
    String markforreversalAmount;
    String fullname;
    String bankaccount;
    String IFSCCode;
    String notificationUrl;
    String bankReferenceId;
    String customerId;
    String paymentBrand ;
    String paymentMode;

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getTelnocc()
    {
        return telnocc;
    }

    public void setTelnocc(String telnocc)
    {
        this.telnocc = telnocc;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
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

    String fromType;
    String counts;
    String isRollingReserve;
    String type;
    String code;
    String fraudreason;
    String walletAmount;
    String walletCurrency;
    String settlmentCurrency;
    String date;

    String captureCount;
    String chargebackCount;
    String declineCount;
    String declineAmount;
    String refundcount;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getFraudAmount()
    {
        return fraudAmount;
    }

    public void setFraudAmount(String fraudAmount)
    {
        this.fraudAmount = fraudAmount;
    }

    String fraudAmount;


    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getFraudreason()
    {
        return fraudreason;
    }

    public void setFraudreason(String fraudreason)
    {
        this.fraudreason = fraudreason;
    }


    private PaginationVO paginationVO;

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public String getPaymodeid()
    {
        return paymodeid;
    }

    public void setPaymodeid(String paymodeid)
    {
        this.paymodeid = paymodeid;
    }

    public String getToType()
    {
        return toType;
    }

    public void setToType(String toType)
    {
        this.toType = toType;
    }

    public String getTotalTransCount()
    {
        return totalTransCount;
    }

    public void setTotalTransCount(String totalTransCount)
    {
        this.totalTransCount = totalTransCount;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getGatewayName()
    {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName)
    {
        this.gatewayName = gatewayName;
    }

    public int getRefundCount()
    {
        return refundCount;
    }

    public void setRefundCount(int refundCount)
    {
        this.refundCount = refundCount;
    }

    public String getIsRetrivalRequest()
    {
        return isRetrivalRequest;
    }

    public void setIsRetrivalRequest(String isRetrivalRequest)
    {
        this.isRetrivalRequest = isRetrivalRequest;
    }

    public String getReverseAmount()
    {
        return reverseAmount;
    }

    public void setReverseAmount(String reverseAmount)
    {
        this.reverseAmount = reverseAmount;
    }

    public String getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(String chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCustFirstName()
    {
        return custFirstName;
    }

    public void setCustFirstName(String custFirstName)
    {
        this.custFirstName = custFirstName;
    }

    public String getCustLastName()
    {
        return custLastName;
    }

    public void setCustLastName(String custLastName)
    {
        this.custLastName = custLastName;
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

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderDesc()
    {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc)
    {
        this.orderDesc = orderDesc;
    }

    public long getCount()
    {
        return count;
    }

    public void setCount(long count)
    {
        this.count = count;
    }

    public double getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(double captureAmount)
    {
        this.captureAmount = captureAmount;
    }
    //update status to mutator
    public String getUpdateStatusTo()
    {
        return updateStatusTo;
    }

    public void setUpdateStatusTo(String updateStatusTo)
    {
        this.updateStatusTo = updateStatusTo;
    }

    //transaction_common_details fields
    public String getDetailId()
    {
        return detailId;
    }

    public void setDetailId(String detailId)
    {
        this.detailId = detailId;
    }

    public String getResponseTransactionId()
    {
        return responseTransactionId;
    }

    public void setResponseTransactionId(String responseTransactionId)
    {
        this.responseTransactionId = responseTransactionId;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getCapAmount()
    {
        return capAmount;
    }

    public void setCapAmount(String capAmount)
    {
        this.capAmount = capAmount;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getToid()
    {
        return toid;
    }

    public void setToid(String toid)
    {
        this.toid = toid;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getFromid()
    {
        return fromid;
    }

    public void setFromid(String fromid)
    {
        this.fromid = fromid;
    }

    public String getQwipiPaymentOrderNumber()
    {
        return qwipiPaymentOrderNumber;
    }

    public void setQwipiPaymentOrderNumber(String qwipiPaymentOrderNumber)
    {
        this.qwipiPaymentOrderNumber = qwipiPaymentOrderNumber;
    }
    public String getEcorePaymentOrderNumber()
    {
        return ecorePaymentOrderNumber;
    }

    public void setEcorePaymentOrderNumber(String ecorePaymentOrderNumber)
    {
        this.ecorePaymentOrderNumber = ecorePaymentOrderNumber;
    }

    public String getDtStamp()
    {
        return dtStamp;
    }

    public void setDtStamp(String dtStamp)
    {
        this.dtStamp = dtStamp;
    }

    public String getRetrivalRequest()
    {
        return isRetrivalRequest;
    }

    public void setRetrivalRequest(String retrivalRequest)
    {
        isRetrivalRequest = retrivalRequest;
    }

    public String getPgtypeid()
    {
        return pgtypeid;
    }

    public void setPgtypeid(String pgtypeid)
    {
        this.pgtypeid = pgtypeid;
    }

    public String getTransactionStatus()
    {
        return TransactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        TransactionStatus = transactionStatus;
    }

    public String getCounts()
    {
        return counts;
    }

    public void setCounts(String counts)
    {
        this.counts = counts;
    }

    public PaginationVO getPaginationVO()
    {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO)
    {
        this.paginationVO = paginationVO;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getFraudRequest()
    {
        return fraudRequest;
    }
    public void setFraudRequest(String fraudRequest)
    {
        this.fraudRequest = fraudRequest;
    }

    public String getEmailAddr()
    {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr)
    {
        this.emailAddr = emailAddr;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
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

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getFromType()
    {
        return fromType;
    }

    public void setFromType(String fromType)
    {
        this.fromType = fromType;
    }

    public String getIsRollingReserve()
    {
        return isRollingReserve;
    }

    public void setIsRollingReserve(String isRollingReserve)
    {
        this.isRollingReserve = isRollingReserve;
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

    public String getSettlmentCurrency()
    {
        return settlmentCurrency;
    }

    public void setSettlmentCurrency(String settlmentCurrency)
    {
        this.settlmentCurrency = settlmentCurrency;
    }

    public String getCaptureCount()
    {
        return captureCount;
    }

    public void setCaptureCount(String captureCount)
    {
        this.captureCount = captureCount;
    }

    public String getChargebackCount()
    {
        return chargebackCount;
    }

    public void setChargebackCount(String chargebackCount)
    {
        this.chargebackCount = chargebackCount;
    }

    public String getDeclineCount()
    {
        return declineCount;
    }

    public void setDeclineCount(String declineCount)
    {
        this.declineCount = declineCount;
    }

    public String getDeclineAmount()
    {
        return declineAmount;
    }

    public void setDeclineAmount(String declineAmount)
    {
        this.declineAmount = declineAmount;
    }

    public String getRefundcount()
    {
        return refundcount;
    }

    public void setRefundcount(String refundcount)
    {
        this.refundcount = refundcount;
    }

    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }

    public String getTransactionReceiptImg() {return transactionReceiptImg;}

    public void setTransactionReceiptImg(String transactionReceiptImg) {this.transactionReceiptImg = transactionReceiptImg;}

    public String getAuthstartedCount()
    {
        return authstartedCount;
    }

    public void setAuthstartedCount(String authstartedCount)
    {
        this.authstartedCount = authstartedCount;
    }

    public String getAuthstartedAmount()
    {
        return authstartedAmount;
    }

    public void setAuthstartedAmount(String authstartedAmount)
    {
        this.authstartedAmount = authstartedAmount;
    }

    public String getPayoutCount()
    {
        return payoutCount;
    }

    public void setPayoutCount(String payoutCount)
    {
        this.payoutCount = payoutCount;
    }

    public String getBegunCount()
    {
        return begunCount;
    }

    public void setBegunCount(String begunCount)
    {
        this.begunCount = begunCount;
    }

    public String getBegunAmount()
    {
        return begunAmount;
    }

    public void setBegunAmount(String begunAmount)
    {
        this.begunAmount = begunAmount;
    }

    public String getFailedCount()
    {
        return failedCount;
    }

    public void setFailedCount(String failedCount)
    {
        this.failedCount = failedCount;
    }

    public String getFailedAmount()
    {
        return failedAmount;
    }

    public void setFailedAmount(String failedAmount)
    {
        this.failedAmount = failedAmount;
    }

    public String getMarkforreversalCount()
    {
        return markforreversalCount;
    }

    public void setMarkforreversalCount(String markforreversalCount)
    {
        this.markforreversalCount = markforreversalCount;
    }

    public String getMarkforreversalAmount()
    {
        return markforreversalAmount;
    }

    public void setMarkforreversalAmount(String markforreversalAmount)
    {
        this.markforreversalAmount = markforreversalAmount;
    }

    public String getIFSCCode()
    {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode)
    {
        this.IFSCCode = IFSCCode;
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

    public String getBankReferenceId()
    {
        return bankReferenceId;
    }

    public void setBankReferenceId(String bankReferenceId)
    {
        this.bankReferenceId = bankReferenceId;
    }
}
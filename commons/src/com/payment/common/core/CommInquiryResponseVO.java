package com.payment.common.core;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/20/15
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommInquiryResponseVO
{
    String orderId;
    String trackingId;
    String status;
    String statusMessage;
    String authAmount;
    String captureAmount;
    String checksum;
    String toid;

    String authCode;
    String resultCode;
    String resultDescription;
    String cardSource;
    String cardIssuerName;
    String eci;
    String eciDescription;
    String cvvResult;
    String bankTransID;
    String cardCountryCode;
    String validationMsg;
    String bankTransDate;

    String numberOfFees;
    String firstFeeDate;
    String feeCurrency;
    String feeAmount;
    String refundAmount;

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage)
    {
        this.statusMessage = statusMessage;
    }

    public String getAuthAmount()
    {
        return authAmount;
    }

    public void setAuthAmount(String authAmount)
    {
        this.authAmount = authAmount;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getResultDescription()
    {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription)
    {
        this.resultDescription = resultDescription;
    }

    public String getCardSource()
    {
        return cardSource;
    }

    public void setCardSource(String cardSource)
    {
        this.cardSource = cardSource;
    }

    public String getCardIssuerName()
    {
        return cardIssuerName;
    }

    public void setCardIssuerName(String cardIssuerName)
    {
        this.cardIssuerName = cardIssuerName;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getEciDescription()
    {
        return eciDescription;
    }

    public void setEciDescription(String eciDescription)
    {
        this.eciDescription = eciDescription;
    }

    public String getCvvResult()
    {
        return cvvResult;
    }

    public void setCvvResult(String cvvResult)
    {
        this.cvvResult = cvvResult;
    }

    public String getBankTransID()
    {
        return bankTransID;
    }

    public void setBankTransID(String bankTransID)
    {
        this.bankTransID = bankTransID;
    }

    public String getValidationMsg()
    {
        return validationMsg;
    }

    public void setValidationMsg(String validationMsg)
    {
        this.validationMsg = validationMsg;
    }

    public String getBankTransDate()
    {
        return bankTransDate;
    }

    public void setBankTransDate(String bankTransDate)
    {
        this.bankTransDate = bankTransDate;
    }

    public String getNumberOfFees()
    {
        return numberOfFees;
    }

    public void setNumberOfFees(String numberOfFees)
    {
        this.numberOfFees = numberOfFees;
    }

    public String getFirstFeeDate()
    {
        return firstFeeDate;
    }

    public void setFirstFeeDate(String firstFeeDate)
    {
        this.firstFeeDate = firstFeeDate;
    }

    public String getFeeCurrency()
    {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency)
    {
        this.feeCurrency = feeCurrency;
    }

    public String getFeeAmount()
    {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount)
    {
        this.feeAmount = feeAmount;
    }

    public String getCardCountryCode()
    {
        return cardCountryCode;
    }

    public void setCardCountryCode(String cardCountryCode)
    {
        this.cardCountryCode = cardCountryCode;
    }

    public String getToid()
    {
        return toid;
    }

    public void setToid(String toid)
    {
        this.toid = toid;
    }
}

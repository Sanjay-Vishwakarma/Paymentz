package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 10/27/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class VTResponseVO
{

    String orderId;
    String status;
    String statusDescription;
    String trackingId;
    String resAmount;
    String checkSum;
    String billingDescriptor;
    String isSuccessful;
    String currency;
    String htmlFormValue;
    //ProcessMC extra params
    private String authCode;
    private String resultCode;
    private String resultDescription;
    private String validationDescription;
    private String bankTransDate;
    private String TxAcqId;
    private String CardCountryCode;
    private String cardSource;
    private String cardIssuerName;
    private String eci;
    private String eciDescription;
    private String cvvResult;
    private String bankTransId;
    //fraud parameters
    private String fraudScore;
    private String rulesTriggered;
    private String errorName;

    public String getHtmlFormValue()
    {
        return htmlFormValue;
    }

    public void setHtmlFormValue(String htmlFormValue)
    {
        this.htmlFormValue = htmlFormValue;
    }

    public String getOrderId()
    {
        return orderId;
    }
    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getStatusDescription()
    {
        return statusDescription;
    }
    public void setStatusDescription(String statusDescription)
    {
        this.statusDescription = statusDescription;
    }
    public String getTrackingId()
    {
        return trackingId;
    }
    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }
    public String getResAmount()
    {
        return resAmount;
    }
    public void setResAmount(String resAmount)
    {
        this.resAmount = resAmount;
    }
    public String getCheckSum()
    {
        return checkSum;
    }
    public void setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
    }
    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }
    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }

    public String getIsSuccessful()
    {
        return isSuccessful;
    }

    public void setIsSuccessful(String isSuccessful)
    {
        this.isSuccessful = isSuccessful;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
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

    public String getValidationDescription()
    {
        return validationDescription;
    }

    public void setValidationDescription(String validationDescription)
    {
        this.validationDescription = validationDescription;
    }

    public String getBankTransDate()
    {
        return bankTransDate;
    }

    public void setBankTransDate(String bankTransDate)
    {
        this.bankTransDate = bankTransDate;
    }

    public String getTxAcqId()
    {
        return TxAcqId;
    }

    public void setTxAcqId(String txAcqId)
    {
        TxAcqId = txAcqId;
    }

    public String getCardCountryCode()
    {
        return CardCountryCode;
    }

    public void setCardCountryCode(String cardCountryCode)
    {
        CardCountryCode = cardCountryCode;
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

    public String getBankTransId()
    {
        return bankTransId;
    }

    public void setBankTransId(String bankTransId)
    {
        this.bankTransId = bankTransId;
    }

    public String getFraudScore()
    {
        return fraudScore;
    }

    public void setFraudScore(String fraudScore)
    {
        this.fraudScore = fraudScore;
    }

    public String getRulesTriggered()
    {
        return rulesTriggered;
    }

    public void setRulesTriggered(String rulesTriggered)
    {
        this.rulesTriggered = rulesTriggered;
    }

    public String getErrorName()
    {
        return errorName;
    }

    public void setErrorName(String errorName)
    {
        this.errorName = errorName;
    }
}

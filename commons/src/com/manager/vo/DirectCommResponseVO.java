package com.manager.vo;

import com.payment.common.core.CommResponseVO;

/**
 * Created by admin on 10/13/2015.
 */
public class DirectCommResponseVO extends CommResponseVO
{
    private String commStatus;
    private String commStatusMessage;
    private String token;
    private String commErrorCode;
    private String trackingid;

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

    public String getCommStatus()
    {
        return commStatus;
    }

    public void setCommStatus(String commStatus)
    {
        this.commStatus = commStatus;
    }

    public String getCommStatusMessage()
    {
        return commStatusMessage;
    }

    public void setCommStatusMessage(String commStatusMessage)
    {
        this.commStatusMessage = commStatusMessage;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getCommErrorCode()
    {
        return commErrorCode;
    }

    public void setCommErrorCode(String commErrorCode)
    {
        this.commErrorCode = commErrorCode;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
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
}



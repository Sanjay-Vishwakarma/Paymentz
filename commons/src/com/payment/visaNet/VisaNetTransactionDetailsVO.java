package com.payment.visaNet;

import com.manager.vo.TransactionDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/18/15
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisaNetTransactionDetailsVO extends TransactionDetailsVO
{
    String authCode;
    String resultCode;
    String resultDescription;
    String cardSource;
    String cardIssuerName;
    String ECI;
    String ECIDescription;
    String cvvResult;
    String txAcqId;
    String bankTransDate;
    String validationDescription;

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

    public String getECI()
    {
        return ECI;
    }

    public void setECI(String ECI)
    {
        this.ECI = ECI;
    }

    public String getECIDescription()
    {
        return ECIDescription;
    }

    public void setECIDescription(String ECIDescription)
    {
        this.ECIDescription = ECIDescription;
    }

    public String getCvvResult()
    {
        return cvvResult;
    }

    public void setCvvResult(String cvvResult)
    {
        this.cvvResult = cvvResult;
    }

    public String getTxAcqId()
    {
        return txAcqId;
    }

    public void setTxAcqId(String txAcqId)
    {
        this.txAcqId = txAcqId;
    }

    public String getBankTransDate()
    {
        return bankTransDate;
    }

    public void setBankTransDate(String bankTransDate)
    {
        this.bankTransDate = bankTransDate;
    }

    public String getValidationDescription()
    {
        return validationDescription;
    }

    public void setValidationDescription(String validationDescription)
    {
        this.validationDescription = validationDescription;
    }
}

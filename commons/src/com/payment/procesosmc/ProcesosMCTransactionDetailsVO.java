package com.payment.procesosmc;

import com.manager.vo.TransactionDetailsVO;

/**
 * Created by 123 on 6/20/2015.
 */
public class ProcesosMCTransactionDetailsVO extends TransactionDetailsVO
{
    String authCode;
    String referencePMP;
    String numberOFFees;
    String firstFeeDate;
    String feeCurrency;
    String feeAmount;
    String resultCode;
    String resultDescription;
    String txAcqId;
    String cardCountryCode;
    String bankTransDate;
    String bankTransTime;

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getReferencePMP()
    {
        return referencePMP;
    }

    public void setReferencePMP(String referencePMP)
    {
        this.referencePMP = referencePMP;
    }

    public String getNumberOFFees()
    {
        return numberOFFees;
    }

    public void setNumberOFFees(String numberOFFees)
    {
        this.numberOFFees = numberOFFees;
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

    public String getBankTransTime()
    {
        return bankTransTime;
    }

    public void setBankTransTime(String bankTransTime)
    {
        this.bankTransTime = bankTransTime;
    }

    public String getCardCountryCode()
    {
        return cardCountryCode;
    }

    public void setCardCountryCode(String cardCountryCode)
    {
        this.cardCountryCode = cardCountryCode;
    }
}

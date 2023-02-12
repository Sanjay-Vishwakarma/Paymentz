package com.payment.procesosmc;

import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 5/19/15
 * Time: 05:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCResponseVO extends CommResponseVO
{
    String authCode;
    String refNumber;
    String numberOfFees;
    String firstFeeDate;
    String feeCurrency;
    double feeAmount;
    String resultCode;
    String resultDescription;
    String txAcqId;
    String cardCountryCode;
    String bankTransDate;
    String bankTransTime;
    String lote;

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getRefNumber()
    {
        return refNumber;
    }

    public void setRefNumber(String refNumber)
    {
        this.refNumber = refNumber;
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

    public double getFeeAmount()
    {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount)
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

    public String getLote()
    {
        return lote;
    }

    public void setLote(String lote)
    {
        this.lote = lote;
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

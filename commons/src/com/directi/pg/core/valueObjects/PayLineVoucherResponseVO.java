package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 5, 2012
 * Time: 10:14:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineVoucherResponseVO   extends CommResponseVO
{

    private String mode;
    private String channel;
    private String shortId;
    private String uniqueId;
    //private String transactionId;
    private String referenceId;
    private String paymentCode;
    private String clearingAmount;
    private String currency;
    private String descriptor;
    private String fxRate;
    private String fxSource;
    private String fxDate;
    private String processingCode;
    private String timeStamp;
    private String result;
    private String status;
    private String statusCode;
    //private String reason;
    //private String reasonCode;
    private String returnMessage;
    private String returnCode;
    private String risk;
    private String riskScore;

    public PayLineVoucherResponseVO(String mode, String channel, String shortId, String uniqueId, String transactionId, String referenceId, String paymentCode, String clearingAmount, String currency, String descriptor, String fxRate, String fxSource, String fxDate, String processingCode, String timeStamp, String result, String status, String statusCode, String reason, String reasonCode, String returnMessage, String returnCode, String risk, String riskScore)
    {
        this.mode = mode;
        this.channel = channel;
        this.shortId = shortId;
        this.uniqueId = uniqueId;
        this.setTransactionId(uniqueId);
        this.setMerchantId(transactionId);
        this.referenceId =referenceId;
        this.paymentCode = paymentCode;
        this.clearingAmount = clearingAmount;
        this.currency = currency;
        this.descriptor = descriptor;
        this.fxRate = fxRate;
        this.fxSource = fxSource;
        this.fxDate = fxDate;
        this.processingCode = processingCode;
        this.timeStamp = timeStamp;
        this.result = result;
        this.status = status;
        this.statusCode = statusCode;
        this.setErrorCode(reasonCode);
        this.setDescription(reason); 
        this.returnMessage = returnMessage;
        this.returnCode = returnCode;
        this.risk = risk;
        this.riskScore = riskScore;
    }

    public PayLineVoucherResponseVO()
    {
    }


    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getShortId()
    {
        return shortId;
    }

    public void setShortId(String shortId)
    {
        this.shortId = shortId;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public String getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }

    public String getPaymentCode()
    {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode)
    {
        this.paymentCode = paymentCode;
    }

    public String getClearingAmount()
    {
        return clearingAmount;
    }

    public void setClearingAmount(String clearingAmount)
    {
        this.clearingAmount = clearingAmount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getProcessingCode()
    {
        return processingCode;
    }

    public void setProcessingCode(String processingCode)
    {
        this.processingCode = processingCode;
    }

    public String getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode(String statusCode)
    {
        this.statusCode = statusCode;
    }



    public String getReturnMessage()
    {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage)
    {
        this.returnMessage = returnMessage;
    }

    public String getReturnCode()
    {
        return returnCode;
    }

    public void setReturnCode(String returnCode)
    {
        this.returnCode = returnCode;
    }



    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getFxRate()
    {
        return fxRate;
    }

    public void setFxRate(String fxRate)
    {
        this.fxRate = fxRate;
    }

    public String getFxSource()
    {
        return fxSource;
    }

    public void setFxSource(String fxSource)
    {
        this.fxSource = fxSource;
    }

    public String getFxDate()
    {
        return fxDate;
    }

    public void setFxDate(String fxDate)
    {
        this.fxDate = fxDate;
    }

    public String getRisk()
    {
        return risk;
    }

    public void setRisk(String risk)
    {
        this.risk = risk;
    }

    public String getRiskScore()
    {
        return riskScore;
    }

    public void setRiskScore(String riskScore)
    {
        this.riskScore = riskScore;
    }

}

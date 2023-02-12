package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 2/8/2016.
 */
@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result
{
    @XmlElement(name="code")
    String code;

    @XmlElement(name="description")
    String description;

    @XmlElement(name="bankCode")
    String bankCode;

    @XmlElement(name="bankDescription")
    String bankDescription;

    @XmlElement(name="status")
    String status;

    @XmlElement(name="transactionStatus")
    String transactionStatus;

    @XmlElement(name="totalBalance")
    String totalBalance;

    @XmlElement(name="currentPayoutBalance")
    String currentPayoutBalance;

    public String getResultCode()
    {
        return code;
    }

    public void setResultCode(String resultCode)
    {
        this.code = resultCode;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

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

    public String getTotalBalance()
    {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance)
    {
        this.totalBalance = totalBalance;
    }

    public String getCurrentPayoutBalance()
    {
        return currentPayoutBalance;
    }

    public void setCurrentPayoutBalance(String currentPayoutBalance)
    {
        this.currentPayoutBalance = currentPayoutBalance;
    }
}

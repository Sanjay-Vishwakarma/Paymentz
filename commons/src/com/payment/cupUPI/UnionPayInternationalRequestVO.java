package com.payment.cupUPI;

import com.payment.common.core.CommRequestVO;

/**
 * Created by Jitendra on 09-Jul-19.
 */
public class UnionPayInternationalRequestVO extends CommRequestVO
{
    String version;
    String encoding;
    String certId;
    String signature;
    String signMethod;
    String txnType;
    String txnSubType;
    String bizType;
    String accessType;
    String channelType;
    String acqInsCode;
    String merId;
    String merCatCode;
    String merName;
    String merAbbr;
    String subMerId;
    String subMerName;
    String subMerAbbr;
    String orderId;
    String txnTime;
    String txnAmt;
    String currencyCode;
    String accNo; // Card Number
    String customerInfo;
    String reqReserved;
    String reserved;
    String encryptCertId;


    String phone;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public String getCertId()
    {
        return certId;
    }

    public void setCertId(String certId)
    {
        this.certId = certId;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getSignMethod()
    {
        return signMethod;
    }

    public void setSignMethod(String signMethod)
    {
        this.signMethod = signMethod;
    }

    public String getTxnType()
    {
        return txnType;
    }

    public void setTxnType(String txnType)
    {
        this.txnType = txnType;
    }

    public String getTxnSubType()
    {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType)
    {
        this.txnSubType = txnSubType;
    }

    public String getBizType()
    {
        return bizType;
    }

    public void setBizType(String bizType)
    {
        this.bizType = bizType;
    }

    public String getAccessType()
    {
        return accessType;
    }

    public void setAccessType(String accessType)
    {
        this.accessType = accessType;
    }

    public String getChannelType()
    {
        return channelType;
    }

    public void setChannelType(String channelType)
    {
        this.channelType = channelType;
    }

    public String getAcqInsCode()
    {
        return acqInsCode;
    }

    public void setAcqInsCode(String acqInsCode)
    {
        this.acqInsCode = acqInsCode;
    }

    public String getMerId()
    {
        return merId;
    }

    public void setMerId(String merId)
    {
        this.merId = merId;
    }

    public String getMerCatCode()
    {
        return merCatCode;
    }

    public void setMerCatCode(String merCatCode)
    {
        this.merCatCode = merCatCode;
    }

    public String getMerName()
    {
        return merName;
    }

    public void setMerName(String merName)
    {
        this.merName = merName;
    }

    public String getMerAbbr()
    {
        return merAbbr;
    }

    public void setMerAbbr(String merAbbr)
    {
        this.merAbbr = merAbbr;
    }

    public String getSubMerId()
    {
        return subMerId;
    }

    public void setSubMerId(String subMerId)
    {
        this.subMerId = subMerId;
    }

    public String getSubMerName()
    {
        return subMerName;
    }

    public void setSubMerName(String subMerName)
    {
        this.subMerName = subMerName;
    }

    public String getSubMerAbbr()
    {
        return subMerAbbr;
    }

    public void setSubMerAbbr(String subMerAbbr)
    {
        this.subMerAbbr = subMerAbbr;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getTxnTime()
    {
        return txnTime;
    }

    public void setTxnTime(String txnTime)
    {
        this.txnTime = txnTime;
    }

    public String getTxnAmt()
    {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt)
    {
        this.txnAmt = txnAmt;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getAccNo()
    {
        return accNo;
    }

    public void setAccNo(String accNo)
    {
        this.accNo = accNo;
    }

    public String getCustomerInfo()
    {
        return customerInfo;
    }

    public void setCustomerInfo(String customerInfo)
    {
        this.customerInfo = customerInfo;
    }

    public String getReqReserved()
    {
        return reqReserved;
    }

    public void setReqReserved(String reqReserved)
    {
        this.reqReserved = reqReserved;
    }

    public String getReserved()
    {
        return reserved;
    }

    public void setReserved(String reserved)
    {
        this.reserved = reserved;
    }

    public String getEncryptCertId()
    {
        return encryptCertId;
    }

    public void setEncryptCertId(String encryptCertId)
    {
        this.encryptCertId = encryptCertId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}

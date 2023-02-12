package com.payment.cupUPI;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Jitendra on 06-Jul-19.
 */
public class UnionPayInternationalResponseVO extends CommResponseVO
{
    String txnType;
    String txnSubType;
    String bizType;
    String accessType;
    String acqInsCode;
    String txnTime;
    String reqReserved;
    String reserved;
    String respCode;
    String respMsg;
    String signPubKeyCert;

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

    public String getAcqInsCode()
    {
        return acqInsCode;
    }

    public void setAcqInsCode(String acqInsCode)
    {
        this.acqInsCode = acqInsCode;
    }

    public String getTxnTime()
    {
        return txnTime;
    }

    public void setTxnTime(String txnTime)
    {
        this.txnTime = txnTime;
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

    public String getRespCode()
    {
        return respCode;
    }

    public void setRespCode(String respCode)
    {
        this.respCode = respCode;
    }

    public String getRespMsg()
    {
        return respMsg;
    }

    public void setRespMsg(String respMsg)
    {
        this.respMsg = respMsg;
    }

    public String getSignPubKeyCert()
    {
        return signPubKeyCert;
    }

    public void setSignPubKeyCert(String signPubKeyCert)
    {
        this.signPubKeyCert = signPubKeyCert;
    }
}

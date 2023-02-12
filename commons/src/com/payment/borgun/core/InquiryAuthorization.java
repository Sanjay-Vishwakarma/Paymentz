package com.payment.borgun.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by Jinesh on 7/22/2016.
 */

@XStreamAlias("TransactionListRequest")
public class InquiryAuthorization
{
    @XStreamAlias("Version")
    String version;

    @XStreamAlias("Processor")
    String processor;

    @XStreamAlias("MerchantId")
    String merchantID;

    @XStreamAlias("TerminalID")
    int terminalID;

    @XStreamAlias("BatchNumber")
    String batchNumber;

    @XStreamAlias("FromDate")
    String fromDate;

    @XStreamAlias("ToDate")
    String toDate;

    @XStreamAlias("RRN")
    String rrn;

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getProcessor()
    {
        return processor;
    }

    public void setProcessor(String processor)
    {
        this.processor = processor;
    }

    public String getMerchantID()
    {
        return merchantID;
    }

    public void setMerchantID(String merchantID)
    {
        this.merchantID = merchantID;
    }

    public int getTerminalID()
    {
        return terminalID;
    }

    public void setTerminalID(int terminalID)
    {
        this.terminalID = terminalID;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(String fromDate)
    {
        this.fromDate = fromDate;
    }

    public String getToDate()
    {
        return toDate;
    }

    public void setToDate(String toDate)
    {
        this.toDate = toDate;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }
}

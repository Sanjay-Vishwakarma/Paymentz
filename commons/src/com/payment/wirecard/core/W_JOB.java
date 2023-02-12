package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("W_JOB")
public class W_JOB
{
    @XStreamAlias("BusinessCaseSignature")
    String businessCaseSignature;

    @XStreamAlias("JobID")
    String jobID;

    @XStreamAlias("FNC_CC_PREAUTHORIZATION")
    FNC_CC_PREAUTHORIZATION fnc_cc_preauthorization;

    @XStreamAlias("FNC_CC_PURCHASE")
    FNC_CC_PURCHASE fnc_cc_purchase;

    @XStreamAlias("FNC_CC_BOOKBACK")
    FNC_CC_BOOKBACK fnc_cc_bookback;

    @XStreamAlias("FNC_CC_REFUND")
    FNC_CC_REFUND fnc_cc_refund;

    @XStreamAlias("FNC_CC_CAPTURE")
    FNC_CC_CAPTURE fnc_cc_capture;

    @XStreamAlias("FNC_CC_REVERSAL")
    FNC_CC_REVERSAL fnc_cc_reversal;

    @XStreamAlias("FNC_CC_QUERY")
    FNC_CC_QUERY fnc_cc_query;

    public ERROR getError()
    {
        return error;
    }

    public void setError(ERROR error)
    {
        this.error = error;
    }

    @XStreamAlias("ERROR")
    ERROR error;

    public FNC_CC_BOOKBACK getFnc_cc_bookback()
    {
        return fnc_cc_bookback;
    }

    public void setFnc_cc_bookback(FNC_CC_BOOKBACK fnc_cc_bookback)
    {
        this.fnc_cc_bookback = fnc_cc_bookback;
    }


    public FNC_CC_REFUND getFnc_cc_refund()
    {
        return fnc_cc_refund;
    }

    public void setFnc_cc_refund(FNC_CC_REFUND fnc_cc_refund)
    {
        this.fnc_cc_refund = fnc_cc_refund;
    }


    public FNC_CC_CAPTURE getFnc_cc_capture()
    {
        return fnc_cc_capture;
    }

    public void setFnc_cc_capture(FNC_CC_CAPTURE fnc_cc_capture)
    {
        this.fnc_cc_capture = fnc_cc_capture;
    }

    public FNC_CC_REVERSAL getFnc_cc_reversal()
    {
        return fnc_cc_reversal;
    }

    public void setFnc_cc_reversal(FNC_CC_REVERSAL fnc_cc_reversal)
    {
        this.fnc_cc_reversal = fnc_cc_reversal;
    }

    public FNC_CC_QUERY getFnc_cc_query()
    {
        return fnc_cc_query;
    }

    public void setFnc_cc_query(FNC_CC_QUERY fnc_cc_query)
    {
        this.fnc_cc_query = fnc_cc_query;
    }

    public void setJobID(String jobID)
    {
        this.jobID = jobID;
    }

    public void setBusinessCaseSignature(String businessCaseSignature)
    {
        this.businessCaseSignature = businessCaseSignature;
    }

    public FNC_CC_PREAUTHORIZATION getFnc_cc_preauthorization()
    {
        return fnc_cc_preauthorization;
    }

    public void setFnc_cc_preauthorization(FNC_CC_PREAUTHORIZATION fnc_cc_preauthorization)
    {
        this.fnc_cc_preauthorization = fnc_cc_preauthorization;
    }

    public String getBusinessCaseSignature()
    {
        return businessCaseSignature;
    }

    public String getJobID()
    {
        return jobID;
    }

    public FNC_CC_PURCHASE getFnc_cc_purchase()
    {
        return fnc_cc_purchase;
    }

    public void setFnc_cc_purchase(FNC_CC_PURCHASE fnc_cc_purchase)
    {
        this.fnc_cc_purchase = fnc_cc_purchase;
    }
}

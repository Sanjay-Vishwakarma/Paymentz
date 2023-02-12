package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="fraudDefenderConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class FraudDefenderConfiguration
{

    @XmlElement(name="purchaseInquiry")
    private PurchaseInquiry purchaseInquiry;

    @XmlElement(name="fraudDetermined")
    private FraudDetermined fraudDetermined;

    @XmlElement(name="disputeInitiated")
    private DisputeInitiated disputeInitiated;

    @XmlElement(name="exceptionFileListing")
    private ExceptionFileListing exceptionFileListing;

    @XmlElement(name="stopPayment")
    private StopPayment stopPayment;

    public PurchaseInquiry getPurchaseInquiry()
    {
        return purchaseInquiry;
    }

    public void setPurchaseInquiry(PurchaseInquiry purchaseInquiry)
    {
        this.purchaseInquiry = purchaseInquiry;
    }

    public FraudDetermined getFraudDetermined()
    {
        return fraudDetermined;
    }

    public void setFraudDetermined(FraudDetermined fraudDetermined)
    {
        this.fraudDetermined = fraudDetermined;
    }

    public DisputeInitiated getDisputeInitiated()
    {
        return disputeInitiated;
    }

    public void setDisputeInitiated(DisputeInitiated disputeInitiated)
    {
        this.disputeInitiated = disputeInitiated;
    }

    public ExceptionFileListing getExceptionFileListing()
    {
        return exceptionFileListing;
    }

    public void setExceptionFileListing(ExceptionFileListing exceptionFileListing)
    {
        this.exceptionFileListing = exceptionFileListing;
    }

    public StopPayment getStopPayment()
    {
        return stopPayment;
    }

    public void setStopPayment(StopPayment stopPayment)
    {
        this.stopPayment = stopPayment;
    }
}

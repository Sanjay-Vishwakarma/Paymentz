package com.payment.payvision.core.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Uday on 1/6/18.
 */

@XmlRootElement(name = "PaymentUsingIntegratedMPI")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentUsingIntegratedMPI
{
    @XmlElement(name = "memberId",required = false)
    private long memberId;

    @XmlElement(name = "memberGuid", required = false)
    private String memberGuid;

    @XmlElement(name = "countryId",required = false)
    private int countryId;

    @XmlElement(name ="trackingMemberCode",required = false)
    private String trackingMemberCode;

    @XmlElement(name = "cardCvv",required = true)
    private String cardCvv;

    @XmlElement(name = "merchantAccountType",required = false)
    private int merchantAccountType;

    @XmlElement(name = "enrollmentId",required =false)
    private long enrollmentId;

    @XmlElement(name = "enrollmentTrackingMemberCode",required = false)
    private String enrollmentTrackingMemberCode;

    @XmlElement(name = "payerAuthenticationResponse",required = true)
    private String payerAuthenticationResponse;

    @XmlElement(name = "dbaName",required = true)
    private String dbaName;

    @XmlElement(name = "dbaCity",required = true)
    private String dbaCity;

    @XmlElement(name = "additionalInfo",required = true)
    private AdditionalInfo additionalInfo;

    public long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(long memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberGuid()
    {
        return memberGuid;
    }

    public void setMemberGuid(String memberGuid)
    {
        this.memberGuid = memberGuid;
    }

    public int getCountryId()
    {
        return countryId;
    }

    public void setCountryId(int countryId)
    {
        this.countryId = countryId;
    }

    public String getTrackingMemberCode()
    {
        return trackingMemberCode;
    }

    public void setTrackingMemberCode(String trackingMemberCode)
    {
        this.trackingMemberCode = trackingMemberCode;
    }

    public String getCardCvv()
    {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv)
    {
        this.cardCvv = cardCvv;
    }

    public int getMerchantAccountType()
    {
        return merchantAccountType;
    }

    public void setMerchantAccountType(int merchantAccountType)
    {
        this.merchantAccountType = merchantAccountType;
    }

    public long getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId(long enrollmentId)
    {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentTrackingMemberCode()
    {
        return enrollmentTrackingMemberCode;
    }

    public void setEnrollmentTrackingMemberCode(String enrollmentTrackingMemberCode)
    {
        this.enrollmentTrackingMemberCode = enrollmentTrackingMemberCode;
    }

    public String getPayerAuthenticationResponse()
    {
        return payerAuthenticationResponse;
    }

    public void setPayerAuthenticationResponse(String payerAuthenticationResponse)
    {
        this.payerAuthenticationResponse = payerAuthenticationResponse;
    }

    public String getDbaName()
    {
        return dbaName;
    }

    public void setDbaName(String dbaName)
    {
        this.dbaName = dbaName;
    }

    public String getDbaCity()
    {
        return dbaCity;
    }

    public void setDbaCity(String dbaCity)
    {
        this.dbaCity = dbaCity;
    }

    public AdditionalInfo getAdditionalInfo()
    {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo)
    {
        this.additionalInfo = additionalInfo;
    }
}

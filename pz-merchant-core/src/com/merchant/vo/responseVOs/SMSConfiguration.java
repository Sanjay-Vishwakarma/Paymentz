package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="smsConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSConfiguration
{
    @XmlElement(name="merchantSMSActivation")
    private String merchantSMSActivation;

    @XmlElement(name="customerSMSActivation")
    private String customerSMSActivation;

    public String getMerchantSMSActivation()
    {
        return merchantSMSActivation;
    }

    public void setMerchantSMSActivation(String merchantSMSActivation)
    {
        this.merchantSMSActivation = merchantSMSActivation;
    }

    public String getCustomerSMSActivation()
    {
        return customerSMSActivation;
    }

    public void setCustomerSMSActivation(String customerSMSActivation)
    {
        this.customerSMSActivation = customerSMSActivation;
    }

    @Override
    public String toString()
    {
        return "SMSConfiguration{" +
                "merchantSMSActivation='" + merchantSMSActivation + '\'' +
                ", customerSMSActivation='" + customerSMSActivation + '\'' +
                '}';
    }
}

package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="fraudConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class FraudConfiguration
{

    @XmlElement(name="onlineFraudCheck")
    private String onlineFraudCheck;

    @XmlElement(name="internalFraudCheck")
    private String internalFraudCheck;

    public String getOnlineFraudCheck()
    {
        return onlineFraudCheck;
    }

    public void setOnlineFraudCheck(String onlineFraudCheck)
    {
        this.onlineFraudCheck = onlineFraudCheck;
    }

    public String getInternalFraudCheck()
    {
        return internalFraudCheck;
    }

    public void setInternalFraudCheck(String internalFraudCheck)
    {
        this.internalFraudCheck = internalFraudCheck;
    }

    @Override
    public String toString()
    {
        return "FraudConfiguration{" +
                "onlineFraudCheck='" + onlineFraudCheck + '\'' +
                ", internalFraudCheck='" + internalFraudCheck + '\'' +
                '}';
    }
}

package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="hrTransactionConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class HRTransactionConfiguration
{
    @XmlElement(name="hrAlertProof")
    private String hrAlertProof;

    @XmlElement(name="hrParameterized")
    private String hrParameterized;

    public String getHrAlertProof()
    {
        return hrAlertProof;
    }

    public void setHrAlertProof(String hrAlertProof)
    {
        this.hrAlertProof = hrAlertProof;
    }

    public String getHrParameterized()
    {
        return hrParameterized;
    }

    public void setHrParameterized(String hrParameterized)
    {
        this.hrParameterized = hrParameterized;
    }

    @Override
    public String toString()
    {
        return "HRTransactionConfiguration{" +
                "hrAlertProof='" + hrAlertProof + '\'' +
                ", hrParameterized='" + hrParameterized + '\'' +
                '}';
    }
}

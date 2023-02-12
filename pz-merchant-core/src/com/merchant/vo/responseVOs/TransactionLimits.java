package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="transactionLimits")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionLimits
{
    @XmlElement(name="vpaAddressLimitCheck")
    private String vpaAddressLimitCheck;

    @XmlElement(name="vpaAddressAmountLimitCheck")
    private String vpaAddressAmountLimitCheck;

    @XmlElement(name="customerIpAmountLimitCheck")
    private String customerIpAmountLimitCheck;

    @XmlElement(name="customerIpCountLimitCheck")
    private String customerIpCountLimitCheck;

    @XmlElement(name="customerNameCountLimitCheck")
    private String customerNameCountLimitCheck;

    @XmlElement(name="customerNameAmountLimitCheck")
    private String customerNameAmountLimitCheck;

    @XmlElement(name="customerEmailCountLimitCheck")
    private String customerEmailCountLimitCheck;

    @XmlElement(name="customerEmailAmountLimitCheck")
    private String customerEmailAmountLimitCheck;

    @XmlElement(name="customerPhoneCountLimitCheck")
    private String customerPhoneCountLimitCheck;

    @XmlElement(name="customerPhoneAmountLimitCheck")
    private String customerPhoneAmountLimitCheck;

    public String getVpaAddressLimitCheck()
    {
        return vpaAddressLimitCheck;
    }

    public void setVpaAddressLimitCheck(String vpaAddressLimitCheck)
    {
        this.vpaAddressLimitCheck = vpaAddressLimitCheck;
    }

    public String getVpaAddressAmountLimitCheck()
    {
        return vpaAddressAmountLimitCheck;
    }

    public void setVpaAddressAmountLimitCheck(String vpaAddressAmountLimitCheck)
    {
        this.vpaAddressAmountLimitCheck = vpaAddressAmountLimitCheck;
    }

    public String getCustomerIpAmountLimitCheck()
    {
        return customerIpAmountLimitCheck;
    }

    public void setCustomerIpAmountLimitCheck(String customerIpAmountLimitCheck)
    {
        this.customerIpAmountLimitCheck = customerIpAmountLimitCheck;
    }

    public String getCustomerIpCountLimitCheck()
    {
        return customerIpCountLimitCheck;
    }

    public void setCustomerIpCountLimitCheck(String customerIpCountLimitCheck)
    {
        this.customerIpCountLimitCheck = customerIpCountLimitCheck;
    }

    public String getCustomerNameCountLimitCheck()
    {
        return customerNameCountLimitCheck;
    }

    public void setCustomerNameCountLimitCheck(String customerNameCountLimitCheck)
    {
        this.customerNameCountLimitCheck = customerNameCountLimitCheck;
    }

    public String getCustomerNameAmountLimitCheck()
    {
        return customerNameAmountLimitCheck;
    }

    public void setCustomerNameAmountLimitCheck(String customerNameAmountLimitCheck)
    {
        this.customerNameAmountLimitCheck = customerNameAmountLimitCheck;
    }

    public String getCustomerEmailCountLimitCheck()
    {
        return customerEmailCountLimitCheck;
    }

    public void setCustomerEmailCountLimitCheck(String customerEmailCountLimitCheck)
    {
        this.customerEmailCountLimitCheck = customerEmailCountLimitCheck;
    }

    public String getCustomerEmailAmountLimitCheck()
    {
        return customerEmailAmountLimitCheck;
    }

    public void setCustomerEmailAmountLimitCheck(String customerEmailAmountLimitCheck)
    {
        this.customerEmailAmountLimitCheck = customerEmailAmountLimitCheck;
    }

    public String getCustomerPhoneCountLimitCheck()
    {
        return customerPhoneCountLimitCheck;
    }

    public void setCustomerPhoneCountLimitCheck(String customerPhoneCountLimitCheck)
    {
        this.customerPhoneCountLimitCheck = customerPhoneCountLimitCheck;
    }

    public String getCustomerPhoneAmountLimitCheck()
    {
        return customerPhoneAmountLimitCheck;
    }

    public void setCustomerPhoneAmountLimitCheck(String customerPhoneAmountLimitCheck)
    {
        this.customerPhoneAmountLimitCheck = customerPhoneAmountLimitCheck;
    }

    @Override
    public String toString()
    {
        return "TransactionLimits{" +
                "vpaAddressLimitCheck='" + vpaAddressLimitCheck + '\'' +
                ", vpaAddressAmountLimitCheck='" + vpaAddressAmountLimitCheck + '\'' +
                ", customerIpAmountLimitCheck='" + customerIpAmountLimitCheck + '\'' +
                ", customerIpCountLimitCheck='" + customerIpCountLimitCheck + '\'' +
                ", customerNameCountLimitCheck='" + customerNameCountLimitCheck + '\'' +
                ", customerNameAmountLimitCheck='" + customerNameAmountLimitCheck + '\'' +
                ", customerEmailCountLimitCheck='" + customerEmailCountLimitCheck + '\'' +
                ", customerEmailAmountLimitCheck='" + customerEmailAmountLimitCheck + '\'' +
                ", customerPhoneCountLimitCheck='" + customerPhoneCountLimitCheck + '\'' +
                ", customerPhoneAmountLimitCheck='" + customerPhoneAmountLimitCheck + '\'' +
                '}';
    }
}

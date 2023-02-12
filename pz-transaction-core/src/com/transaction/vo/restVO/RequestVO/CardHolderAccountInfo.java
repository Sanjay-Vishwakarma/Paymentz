package com.transaction.vo.restVO.RequestVO;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="cardHolderAccountInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardHolderAccountInfo
{
    @FormParam("cardHolderAccountInfo.accActivationDate")
    String accActivationDate;

    @FormParam("cardHolderAccountInfo.accChangeDate")
    String accChangeDate;

    @FormParam("cardHolderAccountInfo.accPwChangeDate")
    String accPwChangeDate;

    @FormParam("cardHolderAccountInfo.addressUseDate")
    String addressUseDate;

    @FormParam("cardHolderAccountInfo.paymentAccActivationDate")
    String paymentAccActivationDate;


    public String getAccActivationDate()
    {
        return accActivationDate;
    }

    public void setAccActivationDate(String accActivationDate)
    {
        this.accActivationDate = accActivationDate;
    }

    public String getAccChangeDate()
    {
        return accChangeDate;
    }

    public void setAccChangeDate(String accChangeDate)
    {
        this.accChangeDate = accChangeDate;
    }

    public String getAccPwChangeDate()
    {
        return accPwChangeDate;
    }

    public void setAccPwChangeDate(String accPwChangeDate)
    {
        this.accPwChangeDate = accPwChangeDate;
    }

    public String getAddressUseDate()
    {
        return addressUseDate;
    }

    public void setAddressUseDate(String addressUseDate)
    {
        this.addressUseDate = addressUseDate;
    }

    public String getPaymentAccActivationDate()
    {
        return paymentAccActivationDate;
    }

    public void setPaymentAccActivationDate(String paymentAccActivationDate)
    {
        this.paymentAccActivationDate = paymentAccActivationDate;
    }
}

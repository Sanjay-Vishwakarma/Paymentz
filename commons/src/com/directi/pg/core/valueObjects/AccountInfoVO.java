package com.directi.pg.core.valueObjects;

/**
 * Created by admin on 08-11-2019.
 */
public class AccountInfoVO  extends GenericVO
{
    String accActivationDate;
    String accChangeDate;
    String accPwChangeDate;
    String addressUseDate;
    String paymentAccActivationDate;


    public String getAccPwChangeDate()
    {
        return accPwChangeDate;
    }

    public void setAccPwChangeDate(String accPwChangeDate)
    {
        this.accPwChangeDate = accPwChangeDate;
    }
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

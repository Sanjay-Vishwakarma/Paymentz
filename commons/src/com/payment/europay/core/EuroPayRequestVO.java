package com.payment.europay.core;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/6/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EuroPayRequestVO extends CommRequestVO
{
    String type;

    public String getUsageL2()
    {
        return usageL2;
    }

    public void setUsageL2(String usageL2)
    {
        this.usageL2 = usageL2;
    }

    public String getUsageL1()
    {
        return usageL1;
    }

    public void setUsageL1(String usageL1)
    {
        this.usageL1 = usageL1;
    }

    String usageL2;
    String usageL1;
    String birthDay;
    String birthMonth;

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    String mode;


    public String getBirthDay()
    {
        return birthDay;
    }

    public void setBirthDay(String birthDay)
    {
        this.birthDay = birthDay;
    }

    public String getBirthMonth()
    {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth)
    {
        this.birthMonth = birthMonth;
    }

    public String getBirthYear()
    {
        return birthYear;
    }

    public void setBirthYear(String birthYear)
    {
        this.birthYear = birthYear;
    }

    public String getSalutation()
    {
        return salutation;
    }

    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    String birthYear;
    String salutation;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }


}

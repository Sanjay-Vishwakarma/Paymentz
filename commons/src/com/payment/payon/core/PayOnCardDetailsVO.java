package com.payment.payon.core;

import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.payment.common.core.CommCardDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/28/12
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnCardDetailsVO extends CommCardDetailsVO
{

    private String startMonth;
    private String startYear;

    public String getStartMonth()
    {
        return startMonth;
    }

    public void setStartMonth(String startMonth)
    {
        this.startMonth = startMonth;
    }

    public String getStartYear()
    {
        return startYear;
    }

    public void setStartYear(String startYear)
    {
        this.startYear = startYear;
    }

}

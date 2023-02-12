package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommTransactionDetailsVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 21, 2012
 * Time: 9:21:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarTransDetailsVO  extends CommTransactionDetailsVO
{
    private String language;
    private String payRef;
    private String enrolled3D;      // Y, N, null

       public String getEnrolled3D()
    {
        return enrolled3D;
    }

    public void setEnrolled3D(String enrolled3D)
    {
        this.enrolled3D = enrolled3D;
    }


    public String getPayRef()
    {
        return payRef;
    }


    public void setPayRef(String payRef)
    {
        this.payRef = payRef;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

   
}

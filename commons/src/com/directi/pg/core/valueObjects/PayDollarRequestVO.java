package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 12, 2012
 * Time: 1:23:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayDollarRequestVO  extends CommRequestVO
{

     private PayDollarVBVDetailsVO  vbvDetails;

     private String ctoken ;

    private String language;
    private String payRef;
    private String enrolled3D;      // Y, N, null

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getPayRef()
    {
        return payRef;
    }

    public void setPayRef(String payRef)
    {
        this.payRef = payRef;
    }

    public String getEnrolled3D()
    {
        return enrolled3D;
    }

    public void setEnrolled3D(String enrolled3D)
    {
        this.enrolled3D = enrolled3D;
    }

    public String getCtoken()
    {
        return ctoken;
    }

    public void setCtoken(String ctoken)
    {
        this.ctoken = ctoken;
    }


    
    public PayDollarVBVDetailsVO getVbvDetails()
    {
        return vbvDetails;
    }

    public void setVbvDetails(PayDollarVBVDetailsVO vbvDetails)
    {
        this.vbvDetails = vbvDetails;
    }
}

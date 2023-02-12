package com.payment.common.core;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 5/13/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComConfirmRequestVO extends GenericRequestVO
{

    private String paRes;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    private String userName;
    private String password;

    public String getPaRes()
    {
        return paRes;
    }

    public void setPaRes(String paRes)
    {
        this.paRes = paRes;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    private String trackingId;
    private String merchantId;


}

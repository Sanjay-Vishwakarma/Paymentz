package com.payment.websecpay.core;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Aug 9, 2013
 * Time: 1:53:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class WSecRequestVO extends CommRequestVO
{
    private String key;

    private String siteUrl;


    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getSiteUrl()
    {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl)
    {
        this.siteUrl = siteUrl;
    }
}

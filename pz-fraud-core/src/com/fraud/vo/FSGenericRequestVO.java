package com.fraud.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/31/14
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSGenericRequestVO
{
    public String merchant_id;
    public String password;
    public String getMerchant_id()
    {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id)
    {
        this.merchant_id = merchant_id;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}

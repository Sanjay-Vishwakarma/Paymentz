package com.payment.elegro;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Admin on 1/11/2019.
 */
public class ElegroResponseVO extends CommResponseVO
{
    private String publickey;

    public String getPublickey()
    {
        return publickey;
    }

    public void setPublickey(String publickey)
    {
        this.publickey = publickey;
    }
}

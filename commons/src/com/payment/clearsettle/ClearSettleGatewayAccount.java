package com.payment.clearsettle;

/**
 * Created by sandip on 4/28/2017.
 */
public class ClearSettleGatewayAccount
{
    int id;
    String is3dSecureAccount;
    String isOnly3DSecureAccount;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getIs3dSecureAccount()
    {
        return is3dSecureAccount;
    }

    public void setIs3dSecureAccount(String is3dSecureAccount)
    {
        this.is3dSecureAccount = is3dSecureAccount;
    }

    public String getIsOnly3DSecureAccount()
    {
        return isOnly3DSecureAccount;
    }

    public void setIsOnly3DSecureAccount(String isOnly3DSecureAccount)
    {
        this.isOnly3DSecureAccount = isOnly3DSecureAccount;
    }
}

package com.directi.pg;

/**
 * Created by admin on 5/18/2016.
 */
public class PartnerUser extends PartnerAuthenticate
{
    public int partnerUserId;

    public int getPartnerUserId()
    {
        return partnerUserId;
    }

    public void setPartnerUserId(int partnerUserId)
    {
        this.partnerUserId = partnerUserId;
    }
}

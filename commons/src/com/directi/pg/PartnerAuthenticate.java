package com.directi.pg;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/16/13
 * Time: 4:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerAuthenticate implements Serializable
{
    public int partnerid = -9999;
    public String activation = null;
    public String authenticate = null;
    public String address = null;
    public String telno = null;
    public String contactemails = null;
    public boolean isservice;
    public String template=null;
    public String partnername=null;
    public String logoname=null;
    public boolean isFlightPartner=false;
    public String hostURL=null;
    public String isRefund=null;
    public String emiConfiguration=null;
    public PartnerUser partnerUser;

    public PartnerUser getPartnerUser()
    {
        return partnerUser;
    }

    public void setPartnerUser(PartnerUser partnerUser)
    {
        this.partnerUser = partnerUser;
    }
}

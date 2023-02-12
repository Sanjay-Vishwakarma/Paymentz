package com.directi.pg;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/21/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class customerSupportExecutive implements Serializable
{
    public int csId = -9999;
    public String csactivation = null;
    public String csauthenticate = null;
    public String csAddress = null;
    public String csContactNumber = null;
    public String csEmail = null;
    private String csaccountId = null;
    public String csLoginDate=null;
    public void setAccountId(String accId)
    {
        csaccountId = accId;
    }

    public String getAccountId()
    {
        return csaccountId;
    }
}

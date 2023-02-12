package com.fraud.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 11/1/14
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudServiceConfigurationVO
{
    String mappingId;
    String memberId;
    String fraudSystemId;
    String isOnlineFraudCheck;
    String isAPIUser;
    String isActive;

    public String getFraudSystemId()
    {
        return fraudSystemId;
    }
    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }
    public String getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(String mappingId)
    {
        this.mappingId = mappingId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getOnlineFraudCheck()
    {
        return isOnlineFraudCheck;
    }

    public void setOnlineFraudCheck(String onlineFraudCheck)
    {
        isOnlineFraudCheck = onlineFraudCheck;
    }

    public String getAPIUser()
    {
        return isAPIUser;
    }

    public void setAPIUser(String APIUser)
    {
        isAPIUser = APIUser;
    }

    public String getActive()
    {
        return isActive;
    }

    public void setActive(String active)
    {
        isActive = active;
    }

}

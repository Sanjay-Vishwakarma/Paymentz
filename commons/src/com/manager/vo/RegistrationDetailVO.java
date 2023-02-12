package com.manager.vo;

/**
 * Created by Sneha on 8/16/2016.
 */
public class RegistrationDetailVO extends TokenRequestVO
{
    TokenDetailsVO tokenDetailsVO;
    public String registration_id;
    public String registration_token;
    public String token;
    public String trackingid;
    public String paymodeid;
    public String cardtypeid;
    public String toid;
    public String isActive;
    public String customerId;
    public String tokencreation_date;
    public String tokeninactive_date;

    public String getCustomerId()
    {
        return customerId;
    }

    public TokenDetailsVO getTokenDetailsVO()
    {
        return tokenDetailsVO;
    }

    public void setTokenDetailsVO(TokenDetailsVO tokenDetailsVO)
    {
        this.tokenDetailsVO = tokenDetailsVO;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getRegistration_id()
    {
        return registration_id;
    }

    public void setRegistration_id(String registration_id)
    {
        this.registration_id = registration_id;
    }

    public String getRegistration_token()
    {
        return registration_token;
    }

    public void setRegistration_token(String registration_token)
    {
        this.registration_token = registration_token;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getPaymodeid()
    {
        return paymodeid;
    }

    public void setPaymodeid(String paymodeid)
    {
        this.paymodeid = paymodeid;
    }

    public String getCardtypeid()
    {
        return cardtypeid;
    }

    public void setCardtypeid(String cardtypeid)
    {
        this.cardtypeid = cardtypeid;
    }

    public String getToid()
    {
        return toid;
    }

    public void setToid(String toid)
    {
        this.toid = toid;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getTokencreation_date()
    {
        return tokencreation_date;
    }

    public void setTokencreation_date(String tokencreation_date)
    {
        this.tokencreation_date = tokencreation_date;
    }

    public String getTokeninactive_date()
    {
        return tokeninactive_date;
    }

    public void setTokeninactive_date(String tokeninactive_date)
    {
        this.tokeninactive_date = tokeninactive_date;
    }
}

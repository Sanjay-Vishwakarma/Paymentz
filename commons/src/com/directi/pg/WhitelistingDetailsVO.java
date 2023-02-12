package com.directi.pg;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/3/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhitelistingDetailsVO
{
    private int accountid;
    private String memberid;
    private String firstsix;
    private String lastfour;
    private String email;
    private String cardNum;
    private String startBin;
    private String endBin;
    private String startCard;
    private String endCard;
    private String ipAddress;
    private String id;
    private String name;
    private String expiryDate;
    private String isTemp;
    private String country;
    private String actionExecutorId;
    private String actionExecutorName;

    public String getWhitelistBinId()
    {
        return id;
    }
    public void setWhitelistBinId(String id)
    {
        this.id=id;
    }
    public int getAccountid()
    {
        return accountid;
    }

    public void setAccountid(int accountid)
    {
        this.accountid = accountid;
    }

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getFirstsix()
    {
        return firstsix;
    }

    public void setFirstsix(String firstsix)
    {
        this.firstsix = firstsix;
    }

    public String getLastfour()
    {
        return lastfour;
    }

    public void setLastfour(String lastfour)
    {
        this.lastfour = lastfour;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCardNum()
    {
        return cardNum;
    }

    public void setCardNum(String cardNum)
    {
        this.cardNum = cardNum;
    }

    public String getStartBin()
    {
        return startBin;
    }

    public void setStartBin(String startBin)
    {
        this.startBin = startBin;
    }

    public String getEndBin()
    {
        return endBin;
    }

    public void setEndBin(String endBin)
    {
        this.endBin = endBin;
    }

    public String getStartCard()
    {
        return startCard;
    }

    public void setStartCard(String startCard)
    {
        this.startCard = startCard;
    }

    public String getEndCard()
    {
        return endCard;
    }

    public void setEndCard(String endCard)
    {
        this.endCard = endCard;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public String getIsTemp() {return isTemp;}

    public void setIsTemp(String isTemp) {this.isTemp = isTemp;}

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }
}

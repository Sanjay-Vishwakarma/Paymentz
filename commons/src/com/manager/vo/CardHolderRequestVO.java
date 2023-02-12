package com.manager.vo;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 12/11/15
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CardHolderRequestVO
{
    String toId;
    String firstName = "";
    String lastName = "";
    String email = "";
    String telNo = "";
    String birthDate = "";
    String gender = "";
    String zip = "";
    String partnerId="";

    public String getToId()
    {
        return toId;
    }

    public void setToId(String toId)
    {
        this.toId = toId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getTelNo()
    {
        return telNo;
    }

    public void setTelNo(String telNo)
    {
        this.telNo = telNo;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
}

package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/11/15
 * Time: 1:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminDetailsVO
{
    //Add properties as per programmer requirement
    String adminId;
    String login;
    String password;
    String contactEmails;
    String signUpTime;
    String accountId;
    String isActive;

    public String getAdminId()
    {
        return adminId;
    }

    public void setAdminId(String adminId)
    {
        this.adminId = adminId;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getContactEmails()
    {
        return contactEmails;
    }

    public void setContactEmails(String contactEmails)
    {
        this.contactEmails = contactEmails;
    }

    public String getSignUpTime()
    {
        return signUpTime;
    }

    public void setSignUpTime(String signUpTime)
    {
        this.signUpTime = signUpTime;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getIsActive()
    {
        return isActive;
    }
    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

}

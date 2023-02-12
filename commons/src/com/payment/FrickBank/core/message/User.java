package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class User
{

    @XStreamAsAttribute
    private String login="";

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    @XStreamAsAttribute
    private String pwd="";
}

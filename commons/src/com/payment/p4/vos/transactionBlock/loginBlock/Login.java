package com.payment.p4.vos.transactionBlock.loginBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 10/2/2015.
 */

@XmlRootElement(name="Login")
@XmlAccessorType(XmlAccessType.FIELD)
public class Login
{

    @XmlElement(name="User")
    String User;
    @XmlElement(name="Password")
    String Password;
    @XmlElement(name="ProjectID")
    String ProjectID;

    public String getUser()
    {
        return User;
    }

    public void setUser(String user)
    {
        User = user;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String password)
    {
        Password = password;
    }

    public String getProjectID()
    {
        return ProjectID;
    }

    public void setProjectID(String projectID)
    {
        ProjectID = projectID;
    }
}

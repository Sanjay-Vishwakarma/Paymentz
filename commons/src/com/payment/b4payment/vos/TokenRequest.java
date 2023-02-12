package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 16/1/2017.
 */
@XmlRootElement(name="TokenRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenRequest
{
    @XmlElement(name="grant_type")
    String grant_type;

    @XmlElement(name="client_id")
    String client_id;

    @XmlElement(name="client_secret")
    String client_secret;

    @XmlElement(name="scope")
    String scope;

    @XmlElement(name="username")
    String username;

    @XmlElement(name="password")
    String password;

    public String getGrant_type()
    {
        return grant_type;
    }

    public void setGrant_type(String grant_type)
    {
        this.grant_type = grant_type;
    }

    public String getClient_id()
    {
        return client_id;
    }

    public void setClient_id(String client_id)
    {
        this.client_id = client_id;
    }

    public String getClient_secret()
    {
        return client_secret;
    }

    public void setClient_secret(String client_secret)
    {
        this.client_secret = client_secret;
    }

    public String getScope()
    {
        return scope;
    }

    public void setScope(String scope)
    {
        this.scope = scope;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}

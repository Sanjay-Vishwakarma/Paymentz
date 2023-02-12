package com.payment.b4payment.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 16/1/2017.
 */
@XmlRootElement(name = "TokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TokenResponse
{
    @XmlElement(name = "access_token")
    String access_token;

    @XmlElement(name = "expires_in")
    String expires_in;

    @XmlElement(name = "token_type")
    String token_type;

    public String getAccess_token()
    {
        return access_token;
    }

    public void setAccess_token(String access_token)
    {
        this.access_token = access_token;
    }

    public String getExpires_in()
    {
        return expires_in;
    }

    public void setExpires_in(String expires_in)
    {
        this.expires_in = expires_in;
    }

    public String getToken_type()
    {
        return token_type;
    }

    public void setToken_type(String token_type)
    {
        this.token_type = token_type;
    }
}

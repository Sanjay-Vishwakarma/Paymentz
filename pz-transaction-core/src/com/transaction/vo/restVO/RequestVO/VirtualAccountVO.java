package com.transaction.vo.restVO.RequestVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="virtualAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualAccountVO
{

    @XmlElement(name="accountId")
    String accountId;

    @XmlElement(name="password")
    String password;

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
}

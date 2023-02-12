package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Contact
{
    @XStreamAlias("Ip")
    private String ip;
    @XStreamAlias("Email")
    private String email;
    @XStreamAlias("Phone")
    private String phone;
    @XStreamAlias("Mobile")
    private String mobile;

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
}

package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="exceptionFileListing")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExceptionFileListing
{

    @XmlElement(name="blacklist")
    private String blacklist;

    public String getBlacklist()
    {
        return blacklist;
    }

    public void setBlacklist(String blacklist)
    {
        this.blacklist = blacklist;
    }

    @Override
    public String toString()
    {
        return "ExceptionFileListing{" +
                "blacklist='" + blacklist + '\'' +
                '}';
    }
}

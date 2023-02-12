package com.invoice.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 31-08-2017.
 */
@XmlRootElement(name="defaultUnit")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitList
{
    @XmlElement(name = "defaultunit")
    String defaultunit;

    public String getDefaultunit()
    {
        return defaultunit;
    }

    public void setDefaultunit(String defaultunit)
    {
        this.defaultunit = defaultunit;
    }
}

package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/22/13
 * Time: 12:06 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Criterion")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class Criterion1
{
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    @XStreamAsAttribute
    private  String name;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    private String  value;
}

package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/17/12
 * Time: 10:40 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
@XStreamAlias("Result")
public class Result
{
    @XStreamAsAttribute
    private String name;
    private String value;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}

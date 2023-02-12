package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/10/12
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class Parameter
{
    private String name;
    @XStreamAsAttribute
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

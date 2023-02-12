package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/21/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Status")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"value"})
public class Status
{
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @XStreamAsAttribute
    private String code="";


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

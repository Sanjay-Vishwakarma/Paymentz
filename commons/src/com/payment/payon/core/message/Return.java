package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamConverter(value=ToAttributedValueConverter.class, strings={"message"})
public class Return
{
    @XStreamAsAttribute
    private String code;

    @XStreamAlias("")
    private String message;


    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

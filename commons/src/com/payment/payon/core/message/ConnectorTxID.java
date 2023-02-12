package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"message"})
public class ConnectorTxID
{
    @XStreamAsAttribute
    private String description;
    private String message;


    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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

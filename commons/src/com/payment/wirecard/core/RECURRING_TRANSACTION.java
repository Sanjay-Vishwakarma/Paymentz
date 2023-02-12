package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("RECURRING_TRANSACTION")
public class RECURRING_TRANSACTION
{
    @XStreamAlias("Type")
    String Type;

    public String getType()
    {
        return Type;
    }

    public void setType(String type)
    {
        Type = type;
    }
}

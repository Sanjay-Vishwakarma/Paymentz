package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("FNC_CC_QUERY")
public class FNC_CC_QUERY
{
    @XStreamAlias("CC_TRANSACTION")
    CC_TRANSACTION cc_transaction;

    @XStreamAlias("FunctionID")
    String functionID;

    @XStreamAlias("type")
    @XStreamAsAttribute
    String type;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public CC_TRANSACTION getCc_transaction()
    {
        return cc_transaction;
    }

    public void setCc_transaction(CC_TRANSACTION cc_transaction)
    {
        this.cc_transaction = cc_transaction;
    }

    public String getFunctionID()
    {
        return functionID;
    }

    public void setFunctionID(String functionID)
    {
        this.functionID = functionID;
    }
}

package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReferencedTransaction
{

    @XStreamAsAttribute
    private String type;
    @XStreamAsAttribute
    private String requestTimestamp;
    @XStreamAlias("Amount")
    private String amount;
    @XStreamAlias("ConnectorTxID1")
    private ConnectorTxID connectorTxID1;
    @XStreamAlias("ConnectorTxID2")
    private ConnectorTxID connectorTxID2;
    @XStreamAlias("ConnectorTxID3")
    private ConnectorTxID connectorTxID3;


    public void setType(String type)
    {
        this.type = type;
    }
    public String getType()
    {
        return type;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }
    public String getAmount()
    {
        return amount;
    }

    public void setRequestTimestamp(String requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }
    public String getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public void setConnectorTxID1(ConnectorTxID connectorTxID1)
    {
        this.connectorTxID1 = connectorTxID1;
    }
    public ConnectorTxID getConnectorTxID1()
    {
        return connectorTxID1;
    }

    public void setConnectorTxID2(ConnectorTxID connectorTxID2)
    {
        this.connectorTxID2 = connectorTxID2;
    }
    public ConnectorTxID getConnectorTxID2()
    {
        return connectorTxID2;
    }

    public ConnectorTxID getConnectorTxID3()
    {
        return connectorTxID3;
    }

    public Authentication getRefAuthentication()
    {
        return refAuthentication;
    }



    public void setRefAuthentication(Authentication refAuthentication)
    {
        this.refAuthentication = refAuthentication;
    }

    private Authentication refAuthentication ;












    public void setConnectorTxID3(ConnectorTxID connectorTxID3)
    {
        this.connectorTxID3 = connectorTxID3;
    }
}

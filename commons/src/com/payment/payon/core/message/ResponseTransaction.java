package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 10:17 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Transaction")
public class ResponseTransaction extends Transaction
{

    @XStreamAlias("Identification")
    private Identification identification;
    @XStreamAlias("ConnectorSent")
    private ConnectorSent connectorSent;
    @XStreamAlias("ConnectorReceived")
    private ConnectorReceived connectorReceived;

    @XStreamAlias("Processing")
    private Processing processing;


    public Identification getIdentification()
    {
        return identification;
    }

    public void setIdentification(Identification identification)
    {
        this.identification = identification;
    }

    public Processing getProcessing()
    {
        return processing;
    }

    public ConnectorReceived getConnectorReceived()
    {
        return connectorReceived;
    }

    public void setProcessing(Processing processing)
    {
        this.processing = processing;
    }

    public void setConnectorReceived(ConnectorReceived connectorReceived)
    {
        this.connectorReceived = connectorReceived;
    }

    public ConnectorSent getConnectorSent()
    {
        return connectorSent;
    }

    public void setConnectorSent(ConnectorSent connectorSent)
    {
        this.connectorSent = connectorSent;
    }
}

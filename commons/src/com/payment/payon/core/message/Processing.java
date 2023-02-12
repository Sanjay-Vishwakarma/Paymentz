package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Processing
{
    @XStreamAsAttribute
    private String requestTimestamp;
    @XStreamAsAttribute
    private String responseTimestamp;
    @XStreamAsAttribute
    private String payPipeProcessingTime;

    @XStreamAsAttribute
    private String connectorTime;

    @XStreamAlias("ConnectorTxID1")
    private ConnectorTxID connectorTxID1;
    @XStreamAlias("ConnectorTxID2")
    private ConnectorTxID connectorTxID2;
    @XStreamAlias("ConnectorTxID3")
    private ConnectorTxID connectorTxID3;

    @XStreamAlias("ConnectorDetails")
    private ConnectorDetails connectorDetails;

    @XStreamAlias("Return")
    private Return returnresp;

    @XStreamAlias("Redirect")
    private Redirect redirectresp;

    public String getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public void setRequestTimestamp(String requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }

    public String getResponseTimestamp()
    {
        return responseTimestamp;
    }

    public void setResponseTimestamp(String responseTimestamp)
    {
        this.responseTimestamp = responseTimestamp;
    }

    public String getPayPipeProcessingTime()
    {
        return payPipeProcessingTime;
    }

    public void setPayPipeProcessingTime(String payPipeProcessingTime)
    {
        this.payPipeProcessingTime = payPipeProcessingTime;
    }

    public String getConnectorTime()
    {
        return connectorTime;
    }

    public void setConnectorTime(String connectorTime)
    {
        this.connectorTime = connectorTime;
    }

    public ConnectorTxID getConnectorTxID1()
    {
        return connectorTxID1;
    }

    public ConnectorTxID getConnectorTxID2()
    {
        return connectorTxID2;
    }

    public ConnectorTxID getConnectorTxID3()
    {
        return connectorTxID3;
    }

    public Return getReturnresp()
    {
        return returnresp;
    }

    public void setConnectorTxID1(ConnectorTxID connectorTxID1)
    {
        this.connectorTxID1 = connectorTxID1;
    }


    public void setConnectorTxID2(ConnectorTxID connectorTxID2)
    {
        this.connectorTxID2 = connectorTxID2;
    }

    public void setConnectorTxID3(ConnectorTxID connectorTxID3)
    {
        this.connectorTxID3 = connectorTxID3;
    }

    public ConnectorDetails getConnectorDetails()
    {
        return connectorDetails;
    }

    public void setConnectorDetails(ConnectorDetails connectorDetails)
    {
        this.connectorDetails = connectorDetails;
    }

    public Redirect getRedirectresp()
    {
        return redirectresp;
    }

    public void setRedirectresp(Redirect redirectresp)
    {
        this.redirectresp = redirectresp;
    }

}

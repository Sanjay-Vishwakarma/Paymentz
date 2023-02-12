package com.payment.trustPay.VO.responseVO;

/**
 * Created by Sneha on 11/7/2016.
 */
public class ResultDetails
{
    String ConnectorTxID1;
    String ConnectorTxID2;
    String ConnectorTxID3;

    public String getConnectorTxID1()
    {
        return ConnectorTxID1;
    }

    public void setConnectorTxID1(String connectorTxID1)
    {
        ConnectorTxID1 = connectorTxID1;
    }

    public String getConnectorTxID2()
    {
        return ConnectorTxID2;
    }

    public void setConnectorTxID2(String connectorTxID2)
    {
        ConnectorTxID2 = connectorTxID2;
    }

    public String getConnectorTxID3()
    {
        return ConnectorTxID3;
    }

    public void setConnectorTxID3(String connectorTxID3)
    {
        ConnectorTxID3 = connectorTxID3;
    }
}

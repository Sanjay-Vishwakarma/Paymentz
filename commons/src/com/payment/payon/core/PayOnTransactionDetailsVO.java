package com.payment.payon.core;

import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/28/12
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnTransactionDetailsVO extends CommTransactionDetailsVO
{

    private String shortId;
    private String UUID;

    public String getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public void setRequestTimestamp(String requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }

    private String requestTimestamp;


    private String referenceTransactionType;
    private String connectorTxID1;
    private String connectorTxID2;
    private String connectorTxID3;
    private String connectorTxID4;


    private String recurrenceMode;

    public String getRecurrenceMode()
    {
        return recurrenceMode;
    }

    public void setRecurrenceMode(String recurrenceMode)
    {
        this.recurrenceMode = recurrenceMode;
    }

    public String getShortId()
    {
        return shortId;
    }

    public void setShortId(String shortId)
    {
        this.shortId = shortId;
    }

    public String getUUID()
    {
        return UUID;
    }

    public void setUUID(String UUID)
    {
        this.UUID = UUID;
    }

    public String getReferenceTransactionType()
    {
        return referenceTransactionType;
    }

    public void setReferenceTransactionType(String referenceTransactionType)
    {
        this.referenceTransactionType = referenceTransactionType;
    }

    public String getConnectorTxID1()
    {
        return connectorTxID1;
    }

    public void setConnectorTxID1(String connectorTxID1)
    {
        this.connectorTxID1 = connectorTxID1;
    }

    public String getConnectorTxID2()
    {
        return connectorTxID2;
    }

    public void setConnectorTxID2(String connectorTxID2)
    {
        this.connectorTxID2 = connectorTxID2;
    }

    public String getConnectorTxID3()
    {
        return connectorTxID3;
    }

    public void setConnectorTxID3(String connectorTxID3)
    {
        this.connectorTxID3 = connectorTxID3;
    }

    public String getConnectorTxID4()
    {
        return connectorTxID4;
    }

    public void setConnectorTxID4(String connectorTxID4)
    {
        this.connectorTxID4 = connectorTxID4;
    }
}

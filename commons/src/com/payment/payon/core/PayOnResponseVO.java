package com.payment.payon.core;

import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/13/12
 * Time: 8:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnResponseVO extends CommResponseVO
{

    private String shortId;
    private String UUID;

    private String requestTimestamp;
    private String responseTimestamp;

    private String payPipeProcessingTime;
    private String connectorTime;

    private String returnCode;
    private String returnMesaage;

    private String connectorTxID1;
    private String connectorTxID2;
    private String connectorTxID3;


    private String connectorCode;
    private String connectorMessage;

    private  String body;
    private String statusDesc;



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

    public String getReturnCode()
    {
        return returnCode;
    }

    public void setReturnCode(String returnCode)
    {
        this.returnCode = returnCode;
    }

    public String getReturnMesaage()
    {
        return returnMesaage;
    }

    public void setReturnMesaage(String returnMesaage)
    {
        this.returnMesaage = returnMesaage;
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

    public String getConnectorCode()
    {
        return connectorCode;
    }

    public void setConnectorCode(String connectorCode)
    {
        this.connectorCode = connectorCode;
    }

    public String getConnectorMessage()
    {
        return connectorMessage;
    }

    public void setConnectorMessage(String connectorMessage)
    {
        this.connectorMessage = connectorMessage;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getStatusDesc()
    {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc)
    {
        this.statusDesc = statusDesc;
    }
}

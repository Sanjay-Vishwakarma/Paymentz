package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 6, 2013
 * Time: 8:46:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UGSPayResponseVO extends CommResponseVO
{
   //AuthorizationResponse, Response,RebillResponse
   String AVSResult;
   String FSResult;
   String FSStatus;
   //S3dcheckResponse,
   String ACSUrl;
   String ACSRequestMessage;

   //OrderStatusResponse
   String result;
   String transactionErrorcode;
   String transactionErrormessage;
   String responseRemark;

    public String getResponseRemark()
    {
        return responseRemark;
    }

    public void setResponseRemark(String responseRemark)
    {
        this.responseRemark = responseRemark;
    }





    public String getAVSResult()
    {
        return AVSResult;
    }

    public void setAVSResult(String AVSResult)
    {
        this.AVSResult = AVSResult;
    }

    public String getFSResult()
    {
        return FSResult;
    }

    public void setFSResult(String FSResult)
    {
        this.FSResult = FSResult;
    }

    public String getFSStatus()
    {
        return FSStatus;
    }

    public void setFSStatus(String FSStatus)
    {
        this.FSStatus = FSStatus;
    }


    public String getACSUrl()
    {
        return ACSUrl;
    }

    public void setACSUrl(String ACSUrl)
    {
        this.ACSUrl = ACSUrl;
    }

    public String getACSRequestMessage()
    {
        return ACSRequestMessage;
    }

    public void setACSRequestMessage(String ACSRequestMessage)
    {
        this.ACSRequestMessage = ACSRequestMessage;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getTransactionErrorcode()
    {
        return transactionErrorcode;
    }

    public void setTransactionErrorcode(String transactionErrorcode)
    {
        this.transactionErrorcode = transactionErrorcode;
    }

    public String getTransactionErrormessage()
    {
        return transactionErrormessage;
    }

    public void setTransactionErrormessage(String transactionErrormessage)
    {
        this.transactionErrormessage = transactionErrormessage;
    }



}

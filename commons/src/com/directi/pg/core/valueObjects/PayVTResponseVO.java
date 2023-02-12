package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 9:35:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVTResponseVO  extends GenericResponseVO
{

       private String result;
       private String transId;
       private String responsecode;
       private String message;

    public PayVTResponseVO(String result, String transId, String responsecode, String message)
    {
        this.result = result;
        this.transId = transId;
        this.responsecode = responsecode;
        this.message = message;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getTransId()
    {
        return transId;
    }

    public void setTransId(String transId)
    {
        this.transId = transId;
    }

    public String getResponsecode()
    {
        return responsecode;
    }

    public void setResponsecode(String responsecode)
    {
        this.responsecode = responsecode;
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

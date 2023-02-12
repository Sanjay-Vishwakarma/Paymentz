package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 5, 2013
 * Time: 8:45:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Generic3DDetailsVO extends GenericVO
{

    private String AVSPolicy;

    private String FSPolicy;

    private String Secure3DAcsMessage;

    private String Secure3DCheckTransactionID;

    public Generic3DDetailsVO()
    {
    }



    public Generic3DDetailsVO(String AVSPolicy, String FSPolicy, String secure3DAcsMessage, String secure3DCheckTransactionID)
    {
        this.AVSPolicy = AVSPolicy;
        this.FSPolicy = FSPolicy;
        Secure3DAcsMessage = secure3DAcsMessage;
        Secure3DCheckTransactionID = secure3DCheckTransactionID;
    }

    public String getAVSPolicy()
    {
        return AVSPolicy;
    }

    public void setAVSPolicy(String AVSPolicy)
    {
        this.AVSPolicy = AVSPolicy;
    }

    public String getFSPolicy()
    {
        return FSPolicy;
    }

    public void setFSPolicy(String FSPolicy)
    {
        this.FSPolicy = FSPolicy;
    }

    public String getSecure3DAcsMessage()
    {
        return Secure3DAcsMessage;
    }

    public void setSecure3DAcsMessage(String secure3DAcsMessage)
    {
        Secure3DAcsMessage = secure3DAcsMessage;
    }

    public String getSecure3DCheckTransactionID()
    {
        return Secure3DCheckTransactionID;
    }

    public void setSecure3DCheckTransactionID(String secure3DCheckTransactionID)
    {
        Secure3DCheckTransactionID = secure3DCheckTransactionID;
    }


}

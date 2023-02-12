package com.payment.p4.vos.transactionBlock.processingBlock;

import com.payment.p4.vos.transactionBlock.processingBlock.historyBlock.History;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.*;

/**
 * Created by admin on 10/2/2015.
 */
@XmlRootElement(name="Processing")
@XmlAccessorType(XmlAccessType.FIELD)
public class Processing
{
    @XmlElement(name = "Timestamp")
    private String Timestamp;

    @XmlPath("Result/@code")
    private String code1;

    @XmlElement(name = "Result")
    private String Result;

    @XmlPath("Reason/@code")
    private String code2;

    @XmlElement(name = "Reason")
    private String Reason;



    @XmlPath("Status/@code")
    private String code3;

    @XmlElement(name = "Status")
    private String Status;

    @XmlPath("State/@modified")
    private String code4;

    @XmlElement(name = "State")
    String State;

    @XmlPath("Return/@code")
    private String code5;

    @XmlElement(name = "Return")
    private String Return;

    @XmlElement(name = "History")
    History History;

    @XmlElement(name = "Modified")
    String Modified;





    public String getTimestamp()
    {
        return Timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        Timestamp = timestamp;
    }

    public String getCode1()
    {
        return code1;
    }

    public void setCode1(String code1)
    {
        this.code1 = code1;
    }

    public String getResult()
    {
        return Result;
    }

    public void setResult(String result)
    {
        Result = result;
    }

    public String getCode2()
    {
        return code2;
    }

    public void setCode2(String code2)
    {
        this.code2 = code2;
    }

    public String getReason()
    {
        return Reason;
    }

    public void setReason(String reason)
    {
        Reason = reason;
    }

    public String getCode3()
    {
        return code3;
    }

    public void setCode3(String code3)
    {
        this.code3 = code3;
    }

    public String getStatus()
    {
        return Status;
    }

    public void setStatus(String status)
    {
        Status = status;
    }

    public History getHistory()
    {
        return History;
    }

    public void setHistory(History history)
    {
        History = history;
    }

    public String getCode4()
    {
        return code4;
    }

    public void setCode4(String code4)
    {
        this.code4 = code4;
    }

    public String getState()
    {
        return State;
    }

    public void setState(String state)
    {
        State = state;
    }

    public String getCode5()
    {
        return code5;
    }

    public void setCode5(String code5)
    {
        this.code5 = code5;
    }

    public String getReturn()
    {
        return Return;
    }

    public void setReturn(String aReturn)
    {
        Return = aReturn;
    }

    public String getModified()
    {
        return Modified;
    }

    public void setModified(String modified)
    {
        Modified = modified;
    }
}

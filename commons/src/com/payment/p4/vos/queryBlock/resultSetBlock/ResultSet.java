package com.payment.p4.vos.queryBlock.resultSetBlock;

import com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock.Result;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.*;

/**
 * Created by Admin on 4/11/2015.
 */
@XmlRootElement(name = "ResultSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultSet
{
    @XmlAttribute(name = "count")
    private String count;

    @XmlElement(name = "Result")
    private Result Result;

    public String getCount()
    {
        return count;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock.Result getResult()
    {
        return Result;
    }

    public void setResult(com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock.Result result)
    {
        Result = result;
    }
}

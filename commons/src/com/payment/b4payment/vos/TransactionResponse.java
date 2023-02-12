package com.payment.b4payment.vos;

import com.payment.common.core.CommResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by admin on 02-01-2017.
 */
@XmlRootElement(name="TransactionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionResponse extends CommResponseVO
{
    @XmlElement(name="result")
    List<Result> Result;

    @XmlElement(name="message")
    String message ;

    public List<Result> getResult()
    {
        return Result;
    }

    public void setResult(List<Result> result)
    {
        Result = result;
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

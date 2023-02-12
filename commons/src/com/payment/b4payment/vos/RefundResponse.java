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
@XmlRootElement(name="RefundResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundResponse extends CommResponseVO
{
    @XmlElement(name = "result")
    List<RefundResult> RefundResult;

    @XmlElement(name = "message")
    String message;

    public List<com.payment.b4payment.vos.RefundResult> getRefundResult()
    {
        return RefundResult;
    }

    public void setRefundResult(List<com.payment.b4payment.vos.RefundResult> refundResult)
    {
        RefundResult = refundResult;
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

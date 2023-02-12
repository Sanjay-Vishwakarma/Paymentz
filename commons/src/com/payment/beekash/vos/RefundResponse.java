package com.payment.beekash.vos;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 12/30/2015.
 */
@XmlRootElement(name = "RefundResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundResponse
{
    @XmlElement(name = "resultcode")
    private String resultcode;
    @XmlElement(name = "billno")
    private String billno;
    @XmlElement(name = "refundamount")
    private String refundamount;
    @XmlElement(name = "paymentorderno")
    private String paymentorderno;
    @XmlElement(name = "remarks")
    private String remarks;

    public String getResultcode()
    {
        return resultcode;
    }

    public void setResultcode(String resultcode)
    {
        this.resultcode = resultcode;
    }

    public String getBillno()
    {
        return billno;
    }

    public void setBillno(String billno)
    {
        this.billno = billno;
    }

    public String getRefundamount()
    {
        return refundamount;
    }

    public void setRefundamount(String refundamount)
    {
        this.refundamount = refundamount;
    }

    public String getPaymentorderno()
    {
        return paymentorderno;
    }

    public void setPaymentorderno(String paymentorderno)
    {
        this.paymentorderno = paymentorderno;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }
}

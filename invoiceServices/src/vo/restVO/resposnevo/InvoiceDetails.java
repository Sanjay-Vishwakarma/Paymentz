package vo.restVO.resposnevo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 30-05-2017.
 */
@XmlRootElement(name="InvoiceDetailsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDetails
{

    @XmlElement(name="transactionstatus")
    String transactionstatus;

    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="count")
    String count;

    @XmlElement(name="captureamount")
    String captureamount;

    @XmlElement(name="refundamount")
    String refundamount;

    @XmlElement(name="chargebackamount")
    String chargebackamount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name="dtstamp")
    String dtstamp;

    @XmlElement(name="date")
    String date;




    public String getTransactionstatus()
    {
        return transactionstatus;
    }

    public void setTransactionstatus(String transactionstatus)
    {
        this.transactionstatus = transactionstatus;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCount()
    {
        return count;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCaptureamount()
    {
        return captureamount;
    }

    public void setCaptureamount(String captureamount)
    {
        this.captureamount = captureamount;
    }

    public String getRefundamount()
    {
        return refundamount;
    }

    public void setRefundamount(String refundamount)
    {
        this.refundamount = refundamount;
    }

    public String getChargebackamount()
    {
        return chargebackamount;
    }

    public void setChargebackamount(String chargebackamount)
    {
        this.chargebackamount = chargebackamount;
    }

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}

package vo.restVO.resposnevo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/6/2017.
 */
@XmlRootElement(name="transactionDetails")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transaction
{
    @XmlElement(name="amount")
    String amount;

    @XmlElement(name="refundAmount")
    String refundAmount;

    @XmlElement(name="currency")
    String currency;

    @XmlElement(name="descriptor")
    String descriptor;

    @XmlElement(name="systemPaymentId")
    String systemPaymentId;

    @XmlElement(name="transactionStatus")
    String transactionStatus;

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }

    public String getSystemPaymentId()
    {
        return systemPaymentId;
    }

    public void setSystemPaymentId(String systemPaymentId)
    {
        this.systemPaymentId = systemPaymentId;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }
}
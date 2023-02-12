package com.payment.beekash.vos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 12/30/2015.
 */
@XmlRootElement(name = "Refund")
@XmlAccessorType(XmlAccessType.FIELD)
public class Refund
{
    @XmlElement(name = "publish_key")
    private String publish_key;

    @XmlElement(name = "bill_no")
    private String bill_no;

    @XmlElement(name = "amount")
    private String amount;

    @XmlElement(name = "refund_amount")
    String refund_amount;

    @XmlElement(name = "payment_order_no")
    private String payment_order_no;

    @XmlElement(name="remarks")
    private String remarks;

    @XmlElement(name = "transaction_id")
     String transaction_id;

    public String getPayment_order_no()
    {
        return payment_order_no;
    }
    public void setPayment_order_no(String payment_order_no)
    {
        this.payment_order_no = payment_order_no;
    }

    public String getBill_no()
    {
        return bill_no;
    }
    public void setBill_no(String bill_no)
    {
        this.bill_no = bill_no;
    }

    public String getAmount() {return amount;}
    public void setAmount(String amount) {this.amount = amount;}

    public String getRefund_amount()
    {
        return refund_amount;
    }
    public void setRefund_amount(String refund_amount) {this.refund_amount = refund_amount;}

    public String getPublish_key() {return publish_key;}
    public void setPublish_key(String publish_key) {this.publish_key = publish_key;}

    public String getRemarks() {return remarks;}
    public void setRemarks(String remarks) {this.remarks = remarks;}

    public String getTransaction_id() {return transaction_id;}
    public void setTransaction_id(String transaction_id) {this.transaction_id = transaction_id;}
}

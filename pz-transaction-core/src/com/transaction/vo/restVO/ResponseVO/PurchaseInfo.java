package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/18/2020.
 */
@XmlRootElement(name = "purchaseinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PurchaseInfo
{
    @XmlElement(name="client_transaction_id")
    String client_transaction_id;

    @XmlElement(name="bank_order_number")
    String bank_order_number;


    public String getClient_transaction_id()
    {
        return client_transaction_id;
    }

    public void setClient_transaction_id(String client_transaction_id)
    {
        this.client_transaction_id = client_transaction_id;
    }

    public String getBank_order_number()
    {
        return bank_order_number;
    }

    public void setBank_order_number(String bank_order_number)
    {
        this.bank_order_number = bank_order_number;
    }


}

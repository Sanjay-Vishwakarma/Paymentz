package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 4/18/2020.
 */
@XmlRootElement(name = "chargebackinfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargebackInfo
{

    @XmlElement(name="cb_amount")
    String cb_amount;
    @XmlElement(name="cb_arn")
    String cb_arn;
    @XmlElement(name="cb_case_number")
    String cb_case_number;
    @XmlElement(name="cb_currency")
    String cb_currency;
    @XmlElement(name="cb_datetime")
    String cb_datetime;
    @XmlElement(name="cb_exists")
    String cb_exists;
    @XmlElement(name="cb_purchase_identifier")
    String cb_purchase_identifier;

    public String getCb_amount()
    {
        return cb_amount;
    }

    public void setCb_amount(String cb_amount)
    {
        this.cb_amount = cb_amount;
    }

    public String getCb_arn()
    {
        return cb_arn;
    }

    public void setCb_arn(String cb_arn)
    {
        this.cb_arn = cb_arn;
    }

    public String getCb_case_number()
    {
        return cb_case_number;
    }

    public void setCb_case_number(String cb_case_number)
    {
        this.cb_case_number = cb_case_number;
    }

    public String getCb_currency()
    {
        return cb_currency;
    }

    public void setCb_currency(String cb_currency)
    {
        this.cb_currency = cb_currency;
    }

    public String getCb_datetime()
    {
        return cb_datetime;
    }

    public void setCb_datetime(String cb_datetime)
    {
        this.cb_datetime = cb_datetime;
    }

    public String getCb_exists()
    {
        return cb_exists;
    }

    public void setCb_exists(String cb_exists)
    {
        this.cb_exists = cb_exists;
    }

    public String getCb_purchase_identifier()
    {
        return cb_purchase_identifier;
    }

    public void setCb_purchase_identifier(String cb_purchase_identifier)
    {
        this.cb_purchase_identifier = cb_purchase_identifier;
    }
}

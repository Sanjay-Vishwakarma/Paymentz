
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Payment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Payment">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="currency" use="required" type="{}Currency" />
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="usageL1" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="usageL2" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Payment")
public class Payment {

    @XmlAttribute(name = "currency", required = true)
    protected Currency currency;
    @XmlAttribute(name = "amount", required = true)
    protected long amount;
    @XmlAttribute(name = "usageL1", required = true)
    protected String usageL1;
    @XmlAttribute(name = "usageL2")
    protected String usageL2;

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link Currency }
     *     
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Currency }
     *     
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(long value) {
        this.amount = value;
    }

    /**
     * Gets the value of the usageL1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsageL1() {
        return usageL1;
    }

    /**
     * Sets the value of the usageL1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsageL1(String value) {
        this.usageL1 = value;
    }

    /**
     * Gets the value of the usageL2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsageL2() {
        return usageL2;
    }

    /**
     * Sets the value of the usageL2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsageL2(String value) {
        this.usageL2 = value;
    }

}

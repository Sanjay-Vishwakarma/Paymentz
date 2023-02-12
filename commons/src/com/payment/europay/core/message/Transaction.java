
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Transaction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Transaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="location" type="{}Location" minOccurs="0"/>
 *         &lt;element name="payment" type="{}Payment"/>
 *         &lt;element name="customer" type="{}Contact" minOccurs="0"/>
 *         &lt;element name="creditCard" type="{}CreditCard" minOccurs="0"/>
 *         &lt;element name="debitCardClassic" type="{}DebitCardClassic" minOccurs="0"/>
 *         &lt;element name="debitCardSepa" type="{}DebitCardSepa" minOccurs="0"/>
 *         &lt;element name="token" type="{}Token" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="mcTxId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mcTxDate" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="method" use="required" type="{}TransactionMethod" />
 *       &lt;attribute name="type" use="required" type="{}TransactionType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transaction", propOrder = {
    "location",
    "payment",
    "customer",
    "creditCard",
    "debitCardClassic",
    "debitCardSepa",
    "token"
})
public class Transaction {

    protected Location location;
    @XmlElement(required = true)
    protected Payment payment;
    protected Contact customer;
    protected CreditCard creditCard;
    protected DebitCardClassic debitCardClassic;
    protected DebitCardSepa debitCardSepa;
    protected Token token;
    @XmlAttribute(name = "mcTxId")
    protected String mcTxId;
    @XmlAttribute(name = "mcTxDate")
    protected String mcTxDate;
    @XmlAttribute(name = "method", required = true)
    protected TransactionMethod method;
    @XmlAttribute(name = "type", required = true)
    protected TransactionType type;

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link Location }
     *     
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link Location }
     *     
     */
    public void setLocation(Location value) {
        this.location = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link Payment }
     *     
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Payment }
     *     
     */
    public void setPayment(Payment value) {
        this.payment = value;
    }

    /**
     * Gets the value of the customer property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setCustomer(Contact value) {
        this.customer = value;
    }

    /**
     * Gets the value of the creditCard property.
     * 
     * @return
     *     possible object is
     *     {@link CreditCard }
     *     
     */
    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * Sets the value of the creditCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditCard }
     *     
     */
    public void setCreditCard(CreditCard value) {
        this.creditCard = value;
    }

    /**
     * Gets the value of the debitCardClassic property.
     * 
     * @return
     *     possible object is
     *     {@link DebitCardClassic }
     *     
     */
    public DebitCardClassic getDebitCardClassic() {
        return debitCardClassic;
    }

    /**
     * Sets the value of the debitCardClassic property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebitCardClassic }
     *     
     */
    public void setDebitCardClassic(DebitCardClassic value) {
        this.debitCardClassic = value;
    }

    /**
     * Gets the value of the debitCardSepa property.
     * 
     * @return
     *     possible object is
     *     {@link DebitCardSepa }
     *     
     */
    public DebitCardSepa getDebitCardSepa() {
        return debitCardSepa;
    }

    /**
     * Sets the value of the debitCardSepa property.
     * 
     * @param value
     *     allowed object is
     *     {@link DebitCardSepa }
     *     
     */
    public void setDebitCardSepa(DebitCardSepa value) {
        this.debitCardSepa = value;
    }

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link Token }
     *     
     */
    public Token getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link Token }
     *     
     */
    public void setToken(Token value) {
        this.token = value;
    }

    /**
     * Gets the value of the mcTxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcTxId() {
        return mcTxId;
    }

    /**
     * Sets the value of the mcTxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcTxId(String value) {
        this.mcTxId = value;
    }

    /**
     * Gets the value of the mcTxDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcTxDate() {
        return mcTxDate;
    }

    /**
     * Sets the value of the mcTxDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcTxDate(String value) {
        this.mcTxDate = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionMethod }
     *     
     */
    public TransactionMethod getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionMethod }
     *     
     */
    public void setMethod(TransactionMethod value) {
        this.method = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionType }
     *     
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionType }
     *     
     */
    public void setType(TransactionType value) {
        this.type = value;
    }

}

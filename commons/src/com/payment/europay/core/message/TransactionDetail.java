
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contact" type="{}Contact"/>
 *         &lt;element name="token" type="{}Token"/>
 *       &lt;/sequence>
 *       &lt;attribute name="txId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mcTxId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mcTxDate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="message" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="creditAmount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="debitAmount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="currency" type="{}Currency" />
 *       &lt;attribute name="reference" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="startDate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="startStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="endDate" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="endStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="deltaMsec" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="method" type="{}TransactionMethod" />
 *       &lt;attribute name="type" type="{}TransactionType" />
 *       &lt;attribute name="merchantName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionDetail", propOrder = {
    "contact",
    "token"
})
public class TransactionDetail {

    @XmlElement(required = true)
    protected Contact contact;
    @XmlElement(required = true)
    protected Token token;
    @XmlAttribute(name = "txId", required = true)
    protected String txId;
    @XmlAttribute(name = "mcTxId")
    protected String mcTxId;
    @XmlAttribute(name = "mcTxDate", required = true)
    protected String mcTxDate;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "message", required = true)
    protected String message;
    @XmlAttribute(name = "creditAmount")
    protected Long creditAmount;
    @XmlAttribute(name = "debitAmount")
    protected Long debitAmount;
    @XmlAttribute(name = "currency")
    protected Currency currency;
    @XmlAttribute(name = "reference")
    protected String reference;
    @XmlAttribute(name = "startDate", required = true)
    protected String startDate;
    @XmlAttribute(name = "startStamp", required = true)
    protected long startStamp;
    @XmlAttribute(name = "endDate", required = true)
    protected String endDate;
    @XmlAttribute(name = "endStamp", required = true)
    protected long endStamp;
    @XmlAttribute(name = "deltaMsec", required = true)
    protected long deltaMsec;
    @XmlAttribute(name = "method")
    protected TransactionMethod method;
    @XmlAttribute(name = "type")
    protected TransactionType type;
    @XmlAttribute(name = "merchantName")
    protected String merchantName;

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link Contact }
     *     
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link Contact }
     *     
     */
    public void setContact(Contact value) {
        this.contact = value;
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
     * Gets the value of the txId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxId() {
        return txId;
    }

    /**
     * Sets the value of the txId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxId(String value) {
        this.txId = value;
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
     * Gets the value of the code property.
     * 
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     */
    public void setCode(int value) {
        this.code = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the creditAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the value of the creditAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCreditAmount(Long value) {
        this.creditAmount = value;
    }

    /**
     * Gets the value of the debitAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the value of the debitAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDebitAmount(Long value) {
        this.debitAmount = value;
    }

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
     * Gets the value of the reference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the value of the reference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the startStamp property.
     * 
     */
    public long getStartStamp() {
        return startStamp;
    }

    /**
     * Sets the value of the startStamp property.
     * 
     */
    public void setStartStamp(long value) {
        this.startStamp = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the endStamp property.
     * 
     */
    public long getEndStamp() {
        return endStamp;
    }

    /**
     * Sets the value of the endStamp property.
     * 
     */
    public void setEndStamp(long value) {
        this.endStamp = value;
    }

    /**
     * Gets the value of the deltaMsec property.
     * 
     */
    public long getDeltaMsec() {
        return deltaMsec;
    }

    /**
     * Sets the value of the deltaMsec property.
     * 
     */
    public void setDeltaMsec(long value) {
        this.deltaMsec = value;
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

    /**
     * Gets the value of the merchantName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Sets the value of the merchantName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantName(String value) {
        this.merchantName = value;
    }

}

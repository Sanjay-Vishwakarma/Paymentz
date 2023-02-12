
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionCompact complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionCompact">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customer" type="{}Contact"/>
 *         &lt;element name="authorization">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="routingKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="serviceKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="serviceIP" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="token" type="{}Token"/>
 *       &lt;/sequence>
 *       &lt;attribute name="txId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mcTxId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="clearingInstitute" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="method" type="{}TransactionMethod" />
 *       &lt;attribute name="type" type="{}TransactionType" />
 *       &lt;attribute name="startStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="endStamp" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="reference" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="message" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="channelName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="debitAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="creditAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="customerShort" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="currency" use="required" type="{}Currency" />
 *       &lt;attribute name="merchantName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionCompact", propOrder = {
    "customer",
    "authorization",
    "token"
})
public class TransactionCompact {

    @XmlElement(required = true)
    protected Contact customer;
    @XmlElement(required = true)
    protected TransactionCompact.Authorization authorization;
    @XmlElement(required = true)
    protected Token token;
    @XmlAttribute(name = "txId", required = true)
    protected String txId;
    @XmlAttribute(name = "mcTxId", required = true)
    protected String mcTxId;
    @XmlAttribute(name = "clearingInstitute")
    protected String clearingInstitute;
    @XmlAttribute(name = "method")
    protected TransactionMethod method;
    @XmlAttribute(name = "type")
    protected TransactionType type;
    @XmlAttribute(name = "startStamp", required = true)
    protected long startStamp;
    @XmlAttribute(name = "endStamp", required = true)
    protected long endStamp;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "reference", required = true)
    protected String reference;
    @XmlAttribute(name = "message", required = true)
    protected String message;
    @XmlAttribute(name = "channelName")
    protected String channelName;
    @XmlAttribute(name = "debitAmount", required = true)
    protected long debitAmount;
    @XmlAttribute(name = "creditAmount", required = true)
    protected long creditAmount;
    @XmlAttribute(name = "customerShort")
    protected String customerShort;
    @XmlAttribute(name = "currency", required = true)
    protected Currency currency;
    @XmlAttribute(name = "merchantName")
    protected String merchantName;

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
     * Gets the value of the authorization property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionCompact.Authorization }
     *     
     */
    public TransactionCompact.Authorization getAuthorization() {
        return authorization;
    }

    /**
     * Sets the value of the authorization property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionCompact.Authorization }
     *     
     */
    public void setAuthorization(TransactionCompact.Authorization value) {
        this.authorization = value;
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
     * Gets the value of the clearingInstitute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClearingInstitute() {
        return clearingInstitute;
    }

    /**
     * Sets the value of the clearingInstitute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClearingInstitute(String value) {
        this.clearingInstitute = value;
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
     * Gets the value of the channelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Sets the value of the channelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannelName(String value) {
        this.channelName = value;
    }

    /**
     * Gets the value of the debitAmount property.
     * 
     */
    public long getDebitAmount() {
        return debitAmount;
    }

    /**
     * Sets the value of the debitAmount property.
     * 
     */
    public void setDebitAmount(long value) {
        this.debitAmount = value;
    }

    /**
     * Gets the value of the creditAmount property.
     * 
     */
    public long getCreditAmount() {
        return creditAmount;
    }

    /**
     * Sets the value of the creditAmount property.
     * 
     */
    public void setCreditAmount(long value) {
        this.creditAmount = value;
    }

    /**
     * Gets the value of the customerShort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerShort() {
        return customerShort;
    }

    /**
     * Sets the value of the customerShort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerShort(String value) {
        this.customerShort = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *       &lt;/sequence>
     *       &lt;attribute name="routingKey" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="serviceKey" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="serviceIP" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Authorization {

        @XmlAttribute(name = "routingKey")
        protected String routingKey;
        @XmlAttribute(name = "serviceKey")
        protected String serviceKey;
        @XmlAttribute(name = "serviceIP")
        protected String serviceIP;

        /**
         * Gets the value of the routingKey property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRoutingKey() {
            return routingKey;
        }

        /**
         * Sets the value of the routingKey property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRoutingKey(String value) {
            this.routingKey = value;
        }

        /**
         * Gets the value of the serviceKey property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceKey() {
            return serviceKey;
        }

        /**
         * Sets the value of the serviceKey property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceKey(String value) {
            this.serviceKey = value;
        }

        /**
         * Gets the value of the serviceIP property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServiceIP() {
            return serviceIP;
        }

        /**
         * Sets the value of the serviceIP property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServiceIP(String value) {
            this.serviceIP = value;
        }

    }

}

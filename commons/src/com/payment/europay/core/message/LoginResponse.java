
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="account" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="login" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="groupId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="merchant_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accountId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="merchantId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="mcTxId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="hash" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="serviceKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="routingKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "account"
})
@XmlRootElement(name = "login_response")
public class LoginResponse {

    protected LoginResponse.Account account;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "version", required = true)
    protected String version;
    @XmlAttribute(name = "accountId")
    protected Integer accountId;
    @XmlAttribute(name = "merchantId")
    protected Integer merchantId;
    @XmlAttribute(name = "mcTxId")
    protected Integer mcTxId;
    @XmlAttribute(name = "hash")
    protected String hash;
    @XmlAttribute(name = "serviceKey")
    protected String serviceKey;
    @XmlAttribute(name = "routingKey")
    protected String routingKey;

    /**
     * Gets the value of the account property.
     * 
     * @return
     *     possible object is
     *     {@link LoginResponse.Account }
     *     
     */
    public LoginResponse.Account getAccount() {
        return account;
    }

    /**
     * Sets the value of the account property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginResponse.Account }
     *     
     */
    public void setAccount(LoginResponse.Account value) {
        this.account = value;
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
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the accountId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountId(Integer value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the merchantId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMerchantId() {
        return merchantId;
    }

    /**
     * Sets the value of the merchantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMerchantId(Integer value) {
        this.merchantId = value;
    }

    /**
     * Gets the value of the mcTxId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMcTxId() {
        return mcTxId;
    }

    /**
     * Sets the value of the mcTxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMcTxId(Integer value) {
        this.mcTxId = value;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHash(String value) {
        this.hash = value;
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
     *       &lt;attribute name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="lastName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="login" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="groupId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="merchant_id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Account {

        @XmlAttribute(name = "firstName")
        protected String firstName;
        @XmlAttribute(name = "lastName")
        protected String lastName;
        @XmlAttribute(name = "login", required = true)
        protected String login;
        @XmlAttribute(name = "groupId", required = true)
        protected int groupId;
        @XmlAttribute(name = "merchant_id", required = true)
        protected int merchantId;

        /**
         * Gets the value of the firstName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         * Sets the value of the firstName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFirstName(String value) {
            this.firstName = value;
        }

        /**
         * Gets the value of the lastName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLastName() {
            return lastName;
        }

        /**
         * Sets the value of the lastName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLastName(String value) {
            this.lastName = value;
        }

        /**
         * Gets the value of the login property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLogin() {
            return login;
        }

        /**
         * Sets the value of the login property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLogin(String value) {
            this.login = value;
        }

        /**
         * Gets the value of the groupId property.
         * 
         */
        public int getGroupId() {
            return groupId;
        }

        /**
         * Sets the value of the groupId property.
         * 
         */
        public void setGroupId(int value) {
            this.groupId = value;
        }

        /**
         * Gets the value of the merchantId property.
         * 
         */
        public int getMerchantId() {
            return merchantId;
        }

        /**
         * Sets the value of the merchantId property.
         * 
         */
        public void setMerchantId(int value) {
            this.merchantId = value;
        }

    }

}

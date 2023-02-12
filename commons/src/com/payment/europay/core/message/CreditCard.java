
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CreditCard complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreditCard">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="brand" type="{}CCBrand" />
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="verification" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expireYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expireMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerFirstName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerLastName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alias" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditCard")
public class CreditCard {

    @XmlAttribute(name = "brand")
    protected CCBrand brand;
    @XmlAttribute(name = "number", required = true)
    protected String number;
    @XmlAttribute(name = "verification", required = true)
    protected String verification;
    @XmlAttribute(name = "expireYear", required = true)
    protected String expireYear;
    @XmlAttribute(name = "expireMonth", required = true)
    protected String expireMonth;
    @XmlAttribute(name = "ownerFirstName", required = true)
    protected String ownerFirstName;
    @XmlAttribute(name = "ownerLastName", required = true)
    protected String ownerLastName;
    @XmlAttribute(name = "alias")
    protected String alias;

    /**
     * Gets the value of the brand property.
     * 
     * @return
     *     possible object is
     *     {@link CCBrand }
     *     
     */
    public CCBrand getBrand() {
        return brand;
    }

    /**
     * Sets the value of the brand property.
     * 
     * @param value
     *     allowed object is
     *     {@link CCBrand }
     *     
     */
    public void setBrand(CCBrand value) {
        this.brand = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the verification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerification() {
        return verification;
    }

    /**
     * Sets the value of the verification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerification(String value) {
        this.verification = value;
    }

    /**
     * Gets the value of the expireYear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpireYear() {
        return expireYear;
    }

    /**
     * Sets the value of the expireYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpireYear(String value) {
        this.expireYear = value;
    }

    /**
     * Gets the value of the expireMonth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpireMonth() {
        return expireMonth;
    }

    /**
     * Sets the value of the expireMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpireMonth(String value) {
        this.expireMonth = value;
    }

    /**
     * Gets the value of the ownerFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    /**
     * Sets the value of the ownerFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerFirstName(String value) {
        this.ownerFirstName = value;
    }

    /**
     * Gets the value of the ownerLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerLastName() {
        return ownerLastName;
    }

    /**
     * Sets the value of the ownerLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerLastName(String value) {
        this.ownerLastName = value;
    }

    /**
     * Gets the value of the alias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the value of the alias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlias(String value) {
        this.alias = value;
    }

}

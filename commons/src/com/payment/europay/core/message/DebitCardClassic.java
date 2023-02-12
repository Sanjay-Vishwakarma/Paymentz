
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DebitCardClassic complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DebitCardClassic">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="brand" type="{}DCBrand" />
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="bankCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="bankName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expireYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expireMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerFirstName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerLastName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DebitCardClassic")
public class DebitCardClassic {

    @XmlAttribute(name = "brand")
    protected DCBrand brand;
    @XmlAttribute(name = "number", required = true)
    protected String number;
    @XmlAttribute(name = "bankCode", required = true)
    protected String bankCode;
    @XmlAttribute(name = "bankName")
    protected String bankName;
    @XmlAttribute(name = "expireYear", required = true)
    protected String expireYear;
    @XmlAttribute(name = "expireMonth", required = true)
    protected String expireMonth;
    @XmlAttribute(name = "ownerFirstName", required = true)
    protected String ownerFirstName;
    @XmlAttribute(name = "ownerLastName", required = true)
    protected String ownerLastName;

    /**
     * Gets the value of the brand property.
     * 
     * @return
     *     possible object is
     *     {@link DCBrand }
     *     
     */
    public DCBrand getBrand() {
        return brand;
    }

    /**
     * Sets the value of the brand property.
     * 
     * @param value
     *     allowed object is
     *     {@link DCBrand }
     *     
     */
    public void setBrand(DCBrand value) {
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
     * Gets the value of the bankCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the value of the bankCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankCode(String value) {
        this.bankCode = value;
    }

    /**
     * Gets the value of the bankName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the value of the bankName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankName(String value) {
        this.bankName = value;
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

}

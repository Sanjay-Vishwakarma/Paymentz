
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Token complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Token">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="details" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="brand" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="issuer" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="expireYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="expireMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="panAlias" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="trail4" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lead6" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="clearingInstitute" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expireYear" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="expireMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="ownerFirstName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ownerLastName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ccBrand" type="{}CCBrand" />
 *       &lt;attribute name="dcBrand" type="{}DCBrand" />
 *       &lt;attribute name="bankCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="swift" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Token", propOrder = {
    "details"
})
public class Token {

    protected Token.Details details;
    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "panAlias")
    protected String panAlias;
    @XmlAttribute(name = "trail4")
    protected String trail4;
    @XmlAttribute(name = "lead6")
    protected String lead6;
    @XmlAttribute(name = "clearingInstitute")
    protected String clearingInstitute;
    @XmlAttribute(name = "expireYear", required = true)
    protected int expireYear;
    @XmlAttribute(name = "expireMonth", required = true)
    protected int expireMonth;
    @XmlAttribute(name = "ownerFirstName", required = true)
    protected String ownerFirstName;
    @XmlAttribute(name = "ownerLastName", required = true)
    protected String ownerLastName;
    @XmlAttribute(name = "ccBrand")
    protected CCBrand ccBrand;
    @XmlAttribute(name = "dcBrand")
    protected DCBrand dcBrand;
    @XmlAttribute(name = "bankCode")
    protected String bankCode;
    @XmlAttribute(name = "swift")
    protected String swift;

    /**
     * Gets the value of the details property.
     * 
     * @return
     *     possible object is
     *     {@link Token.Details }
     *     
     */
    public Token.Details getDetails() {
        return details;
    }

    /**
     * Sets the value of the details property.
     * 
     * @param value
     *     allowed object is
     *     {@link Token.Details }
     *     
     */
    public void setDetails(Token.Details value) {
        this.details = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the panAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPanAlias() {
        return panAlias;
    }

    /**
     * Sets the value of the panAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPanAlias(String value) {
        this.panAlias = value;
    }

    /**
     * Gets the value of the trail4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrail4() {
        return trail4;
    }

    /**
     * Sets the value of the trail4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrail4(String value) {
        this.trail4 = value;
    }

    /**
     * Gets the value of the lead6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLead6() {
        return lead6;
    }

    /**
     * Sets the value of the lead6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLead6(String value) {
        this.lead6 = value;
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
     * Gets the value of the expireYear property.
     * 
     */
    public int getExpireYear() {
        return expireYear;
    }

    /**
     * Sets the value of the expireYear property.
     * 
     */
    public void setExpireYear(int value) {
        this.expireYear = value;
    }

    /**
     * Gets the value of the expireMonth property.
     * 
     */
    public int getExpireMonth() {
        return expireMonth;
    }

    /**
     * Sets the value of the expireMonth property.
     * 
     */
    public void setExpireMonth(int value) {
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
     * Gets the value of the ccBrand property.
     * 
     * @return
     *     possible object is
     *     {@link CCBrand }
     *     
     */
    public CCBrand getCcBrand() {
        return ccBrand;
    }

    /**
     * Sets the value of the ccBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link CCBrand }
     *     
     */
    public void setCcBrand(CCBrand value) {
        this.ccBrand = value;
    }

    /**
     * Gets the value of the dcBrand property.
     * 
     * @return
     *     possible object is
     *     {@link DCBrand }
     *     
     */
    public DCBrand getDcBrand() {
        return dcBrand;
    }

    /**
     * Sets the value of the dcBrand property.
     * 
     * @param value
     *     allowed object is
     *     {@link DCBrand }
     *     
     */
    public void setDcBrand(DCBrand value) {
        this.dcBrand = value;
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
     * Gets the value of the swift property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSwift() {
        return swift;
    }

    /**
     * Sets the value of the swift property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSwift(String value) {
        this.swift = value;
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
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="brand" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="issuer" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="expireYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="expireMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Details {

        @XmlAttribute(name = "id", required = true)
        protected long id;
        @XmlAttribute(name = "brand", required = true)
        protected String brand;
        @XmlAttribute(name = "issuer", required = true)
        protected String issuer;
        @XmlAttribute(name = "expireYear", required = true)
        protected String expireYear;
        @XmlAttribute(name = "expireMonth", required = true)
        protected String expireMonth;

        /**
         * Gets the value of the id property.
         * 
         */
        public long getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         */
        public void setId(long value) {
            this.id = value;
        }

        /**
         * Gets the value of the brand property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBrand() {
            return brand;
        }

        /**
         * Sets the value of the brand property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBrand(String value) {
            this.brand = value;
        }

        /**
         * Gets the value of the issuer property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIssuer() {
            return issuer;
        }

        /**
         * Sets the value of the issuer property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIssuer(String value) {
            this.issuer = value;
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

    }

}

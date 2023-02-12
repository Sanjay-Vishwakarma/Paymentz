
package com.payment.europay.core.message;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="currencyCode" type="{}CurrencyCode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="errorCode" type="{}ErrorCode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dateTypes" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="example" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="specification" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "currencyCode",
    "errorCode",
    "dateTypes"
})
@XmlRootElement(name = "sysinfo_response")
public class SysinfoResponse {

    @XmlElement(nillable = true)
    protected List<CurrencyCode> currencyCode;
    @XmlElement(nillable = true)
    protected List<ErrorCode> errorCode;
    @XmlElement(nillable = true)
    protected List<SysinfoResponse.DateTypes> dateTypes;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Gets the value of the currencyCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the currencyCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCurrencyCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CurrencyCode }
     * 
     * 
     */
    public List<CurrencyCode> getCurrencyCode() {
        if (currencyCode == null) {
            currencyCode = new ArrayList<CurrencyCode>();
        }
        return this.currencyCode;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the errorCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getErrorCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorCode }
     * 
     * 
     */
    public List<ErrorCode> getErrorCode() {
        if (errorCode == null) {
            errorCode = new ArrayList<ErrorCode>();
        }
        return this.errorCode;
    }

    /**
     * Gets the value of the dateTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dateTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDateTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SysinfoResponse.DateTypes }
     * 
     * 
     */
    public List<SysinfoResponse.DateTypes> getDateTypes() {
        if (dateTypes == null) {
            dateTypes = new ArrayList<SysinfoResponse.DateTypes>();
        }
        return this.dateTypes;
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
     *       &lt;attribute name="example" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="specification" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DateTypes {

        @XmlAttribute(name = "example")
        protected String example;
        @XmlAttribute(name = "description")
        protected String description;
        @XmlAttribute(name = "specification")
        protected String specification;

        /**
         * Gets the value of the example property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getExample() {
            return example;
        }

        /**
         * Sets the value of the example property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setExample(String value) {
            this.example = value;
        }

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescription(String value) {
            this.description = value;
        }

        /**
         * Gets the value of the specification property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSpecification() {
            return specification;
        }

        /**
         * Sets the value of the specification property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSpecification(String value) {
            this.specification = value;
        }

    }

}


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
 *         &lt;element name="brandCheck" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="brand" type="{}CCBrand" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="processResults" type="{}TransactionDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="searchResults" type="{}TransactionCompact" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="statistics" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="totalCount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="totalDebitAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="totalCreditAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
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
    "brandCheck",
    "processResults",
    "searchResults",
    "statistics"
})
@XmlRootElement(name = "transactions_response")
public class TransactionsResponse {

    protected TransactionsResponse.BrandCheck brandCheck;
    @XmlElement(nillable = true)
    protected List<TransactionDetail> processResults;
    @XmlElement(nillable = true)
    protected List<TransactionCompact> searchResults;
    protected TransactionsResponse.Statistics statistics;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Gets the value of the brandCheck property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionsResponse.BrandCheck }
     *     
     */
    public TransactionsResponse.BrandCheck getBrandCheck() {
        return brandCheck;
    }

    /**
     * Sets the value of the brandCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionsResponse.BrandCheck }
     *     
     */
    public void setBrandCheck(TransactionsResponse.BrandCheck value) {
        this.brandCheck = value;
    }

    /**
     * Gets the value of the processResults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processResults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionDetail }
     * 
     * 
     */
    public List<TransactionDetail> getProcessResults() {
        if (processResults == null) {
            processResults = new ArrayList<TransactionDetail>();
        }
        return this.processResults;
    }

    /**
     * Gets the value of the searchResults property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchResults property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionCompact }
     * 
     * 
     */
    public List<TransactionCompact> getSearchResults() {
        if (searchResults == null) {
            searchResults = new ArrayList<TransactionCompact>();
        }
        return this.searchResults;
    }

    /**
     * Gets the value of the statistics property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionsResponse.Statistics }
     *     
     */
    public TransactionsResponse.Statistics getStatistics() {
        return statistics;
    }

    /**
     * Sets the value of the statistics property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionsResponse.Statistics }
     *     
     */
    public void setStatistics(TransactionsResponse.Statistics value) {
        this.statistics = value;
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
     *       &lt;attribute name="brand" type="{}CCBrand" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BrandCheck {

        @XmlAttribute(name = "brand")
        protected CCBrand brand;

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
     *       &lt;attribute name="totalCount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="totalDebitAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="totalCreditAmount" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Statistics {

        @XmlAttribute(name = "totalCount", required = true)
        protected long totalCount;
        @XmlAttribute(name = "totalDebitAmount", required = true)
        protected long totalDebitAmount;
        @XmlAttribute(name = "totalCreditAmount", required = true)
        protected long totalCreditAmount;

        /**
         * Gets the value of the totalCount property.
         * 
         */
        public long getTotalCount() {
            return totalCount;
        }

        /**
         * Sets the value of the totalCount property.
         * 
         */
        public void setTotalCount(long value) {
            this.totalCount = value;
        }

        /**
         * Gets the value of the totalDebitAmount property.
         * 
         */
        public long getTotalDebitAmount() {
            return totalDebitAmount;
        }

        /**
         * Sets the value of the totalDebitAmount property.
         * 
         */
        public void setTotalDebitAmount(long value) {
            this.totalDebitAmount = value;
        }

        /**
         * Gets the value of the totalCreditAmount property.
         * 
         */
        public long getTotalCreditAmount() {
            return totalCreditAmount;
        }

        /**
         * Sets the value of the totalCreditAmount property.
         * 
         */
        public void setTotalCreditAmount(long value) {
            this.totalCreditAmount = value;
        }

    }

}

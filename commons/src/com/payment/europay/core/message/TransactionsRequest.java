
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
 *         &lt;element name="transactions" type="{}Transaction" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="transactionSearch" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="routingKeys" type="{}Key" maxOccurs="unbounded"/>
 *                   &lt;element name="timeFrame" type="{}TimeFrame" minOccurs="0"/>
 *                   &lt;element name="filtering" type="{}NamedValue" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="sorting" type="{}NamedValue" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="offset" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="size" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="accountId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="mode" type="{}TransactionMode" />
 *       &lt;attribute name="accountId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transactions",
    "transactionSearch"
})
@XmlRootElement(name = "transactions_request")
public class TransactionsRequest {

    @XmlElement(nillable = true)
    protected List<Transaction> transactions;
    protected TransactionsRequest.TransactionSearch transactionSearch;
    @XmlAttribute(name = "mode")
    protected TransactionMode mode;
    @XmlAttribute(name = "accountId")
    protected Integer accountId;

    /**
     * Gets the value of the transactions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transaction }
     * 
     * 
     */
    public List<Transaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<Transaction>();
        }
        return this.transactions;
    }

    /**
     * Gets the value of the transactionSearch property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionsRequest.TransactionSearch }
     *     
     */
    public TransactionsRequest.TransactionSearch getTransactionSearch() {
        return transactionSearch;
    }

    /**
     * Sets the value of the transactionSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionsRequest.TransactionSearch }
     *     
     */
    public void setTransactionSearch(TransactionsRequest.TransactionSearch value) {
        this.transactionSearch = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionMode }
     *     
     */
    public TransactionMode getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionMode }
     *     
     */
    public void setMode(TransactionMode value) {
        this.mode = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="routingKeys" type="{}Key" maxOccurs="unbounded"/>
     *         &lt;element name="timeFrame" type="{}TimeFrame" minOccurs="0"/>
     *         &lt;element name="filtering" type="{}NamedValue" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="sorting" type="{}NamedValue" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="offset" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="size" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="accountId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "routingKeys",
        "timeFrame",
        "filtering",
        "sorting"
    })
    public static class TransactionSearch {

        @XmlElement(required = true)
        protected List<Key> routingKeys;
        protected TimeFrame timeFrame;
        @XmlElement(nillable = true)
        protected List<NamedValue> filtering;
        @XmlElement(nillable = true)
        protected List<NamedValue> sorting;
        @XmlAttribute(name = "offset", required = true)
        protected int offset;
        @XmlAttribute(name = "size", required = true)
        protected int size;
        @XmlAttribute(name = "accountId")
        protected Integer accountId;

        /**
         * Gets the value of the routingKeys property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the routingKeys property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRoutingKeys().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Key }
         * 
         * 
         */
        public List<Key> getRoutingKeys() {
            if (routingKeys == null) {
                routingKeys = new ArrayList<Key>();
            }
            return this.routingKeys;
        }

        /**
         * Gets the value of the timeFrame property.
         * 
         * @return
         *     possible object is
         *     {@link TimeFrame }
         *     
         */
        public TimeFrame getTimeFrame() {
            return timeFrame;
        }

        /**
         * Sets the value of the timeFrame property.
         * 
         * @param value
         *     allowed object is
         *     {@link TimeFrame }
         *     
         */
        public void setTimeFrame(TimeFrame value) {
            this.timeFrame = value;
        }

        /**
         * Gets the value of the filtering property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the filtering property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFiltering().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NamedValue }
         * 
         * 
         */
        public List<NamedValue> getFiltering() {
            if (filtering == null) {
                filtering = new ArrayList<NamedValue>();
            }
            return this.filtering;
        }

        /**
         * Gets the value of the sorting property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sorting property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSorting().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NamedValue }
         * 
         * 
         */
        public List<NamedValue> getSorting() {
            if (sorting == null) {
                sorting = new ArrayList<NamedValue>();
            }
            return this.sorting;
        }

        /**
         * Gets the value of the offset property.
         * 
         */
        public int getOffset() {
            return offset;
        }

        /**
         * Sets the value of the offset property.
         * 
         */
        public void setOffset(int value) {
            this.offset = value;
        }

        /**
         * Gets the value of the size property.
         * 
         */
        public int getSize() {
            return size;
        }

        /**
         * Sets the value of the size property.
         * 
         */
        public void setSize(int value) {
            this.size = value;
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

    }

}

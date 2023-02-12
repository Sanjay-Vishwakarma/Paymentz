
package com.payment.europay.core.message;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="merchant" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="address" type="{}Address"/>
 *                   &lt;element name="numbers" type="{}Phone" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="legalName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="merchantId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="terminalId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="website" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="levelNodes" type="{}TreeNode" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tree" type="{}TreeNode" minOccurs="0"/>
 *         &lt;element name="serviceKey" type="{}Key" minOccurs="0"/>
 *         &lt;element name="routingKeys" type="{}Key" maxOccurs="unbounded" minOccurs="0"/>
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
    "merchant",
    "levelNodes",
    "tree",
    "serviceKey",
    "routingKeys"
})
@XmlRootElement(name = "tree_response")
public class TreeResponse {

    protected TreeResponse.Merchant merchant;
    @XmlElement(nillable = true)
    protected List<TreeNode> levelNodes;
    protected TreeNode tree;
    protected Key serviceKey;
    @XmlElement(nillable = true)
    protected List<Key> routingKeys;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Gets the value of the merchant property.
     * 
     * @return
     *     possible object is
     *     {@link TreeResponse.Merchant }
     *     
     */
    public TreeResponse.Merchant getMerchant() {
        return merchant;
    }

    /**
     * Sets the value of the merchant property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreeResponse.Merchant }
     *     
     */
    public void setMerchant(TreeResponse.Merchant value) {
        this.merchant = value;
    }

    /**
     * Gets the value of the levelNodes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the levelNodes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLevelNodes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TreeNode }
     * 
     * 
     */
    public List<TreeNode> getLevelNodes() {
        if (levelNodes == null) {
            levelNodes = new ArrayList<TreeNode>();
        }
        return this.levelNodes;
    }

    /**
     * Gets the value of the tree property.
     * 
     * @return
     *     possible object is
     *     {@link TreeNode }
     *     
     */
    public TreeNode getTree() {
        return tree;
    }

    /**
     * Sets the value of the tree property.
     * 
     * @param value
     *     allowed object is
     *     {@link TreeNode }
     *     
     */
    public void setTree(TreeNode value) {
        this.tree = value;
    }

    /**
     * Gets the value of the serviceKey property.
     * 
     * @return
     *     possible object is
     *     {@link Key }
     *     
     */
    public Key getServiceKey() {
        return serviceKey;
    }

    /**
     * Sets the value of the serviceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Key }
     *     
     */
    public void setServiceKey(Key value) {
        this.serviceKey = value;
    }

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
     *         &lt;element name="address" type="{}Address"/>
     *         &lt;element name="numbers" type="{}Phone" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="legalName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="merchantId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="terminalId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="website" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "address",
        "numbers"
    })
    public static class Merchant {

        @XmlElement(required = true)
        protected Address address;
        @XmlElement(nillable = true)
        protected List<Phone> numbers;
        @XmlAttribute(name = "legalName", required = true)
        protected String legalName;
        @XmlAttribute(name = "merchantId", required = true)
        protected int merchantId;
        @XmlAttribute(name = "terminalId")
        protected Integer terminalId;
        @XmlAttribute(name = "website")
        @XmlSchemaType(name = "anyURI")
        protected String website;

        /**
         * Gets the value of the address property.
         * 
         * @return
         *     possible object is
         *     {@link Address }
         *     
         */
        public Address getAddress() {
            return address;
        }

        /**
         * Sets the value of the address property.
         * 
         * @param value
         *     allowed object is
         *     {@link Address }
         *     
         */
        public void setAddress(Address value) {
            this.address = value;
        }

        /**
         * Gets the value of the numbers property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the numbers property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNumbers().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Phone }
         * 
         * 
         */
        public List<Phone> getNumbers() {
            if (numbers == null) {
                numbers = new ArrayList<Phone>();
            }
            return this.numbers;
        }

        /**
         * Gets the value of the legalName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLegalName() {
            return legalName;
        }

        /**
         * Sets the value of the legalName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLegalName(String value) {
            this.legalName = value;
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

        /**
         * Gets the value of the terminalId property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getTerminalId() {
            return terminalId;
        }

        /**
         * Sets the value of the terminalId property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setTerminalId(Integer value) {
            this.terminalId = value;
        }

        /**
         * Gets the value of the website property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWebsite() {
            return website;
        }

        /**
         * Sets the value of the website property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWebsite(String value) {
            this.website = value;
        }

    }

}

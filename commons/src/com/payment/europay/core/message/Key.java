
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Key complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Key">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{}MerchantKeyType" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="keyId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Key")
public class Key {

    @XmlAttribute(name = "type")
    protected MerchantKeyType type;
    @XmlAttribute(name = "value")
    protected String value;
    @XmlAttribute(name = "keyId", required = true)
    protected int keyId;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link MerchantKeyType }
     *     
     */
    public MerchantKeyType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link MerchantKeyType }
     *     
     */
    public void setType(MerchantKeyType value) {
        this.type = value;
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
     * Gets the value of the keyId property.
     * 
     */
    public int getKeyId() {
        return keyId;
    }

    /**
     * Sets the value of the keyId property.
     * 
     */
    public void setKeyId(int value) {
        this.keyId = value;
    }

}

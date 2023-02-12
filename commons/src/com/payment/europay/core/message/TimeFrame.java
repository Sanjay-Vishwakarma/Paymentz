
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeFrame complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeFrame">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="minDateTime" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxDateTime" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="minTimeStamp" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="maxTimeStamp" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeFrame")
public class TimeFrame {

    @XmlAttribute(name = "minDateTime")
    protected String minDateTime;
    @XmlAttribute(name = "maxDateTime")
    protected String maxDateTime;
    @XmlAttribute(name = "minTimeStamp")
    protected Long minTimeStamp;
    @XmlAttribute(name = "maxTimeStamp")
    protected Long maxTimeStamp;

    /**
     * Gets the value of the minDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinDateTime() {
        return minDateTime;
    }

    /**
     * Sets the value of the minDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinDateTime(String value) {
        this.minDateTime = value;
    }

    /**
     * Gets the value of the maxDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxDateTime() {
        return maxDateTime;
    }

    /**
     * Sets the value of the maxDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxDateTime(String value) {
        this.maxDateTime = value;
    }

    /**
     * Gets the value of the minTimeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMinTimeStamp() {
        return minTimeStamp;
    }

    /**
     * Sets the value of the minTimeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMinTimeStamp(Long value) {
        this.minTimeStamp = value;
    }

    /**
     * Gets the value of the maxTimeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxTimeStamp() {
        return maxTimeStamp;
    }

    /**
     * Sets the value of the maxTimeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxTimeStamp(Long value) {
        this.maxTimeStamp = value;
    }

}

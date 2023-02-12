
package com.payment.europay.core.message;

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
 *         &lt;element name="channelUpdate" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="routingData" type="{}OutboundRouting"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="channelId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="accountId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "channelUpdate"
})
@XmlRootElement(name = "keymanagement_request")
public class KeymanagementRequest {

    protected KeymanagementRequest.ChannelUpdate channelUpdate;
    @XmlAttribute(name = "accountId", required = true)
    protected int accountId;

    /**
     * Gets the value of the channelUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link KeymanagementRequest.ChannelUpdate }
     *     
     */
    public KeymanagementRequest.ChannelUpdate getChannelUpdate() {
        return channelUpdate;
    }

    /**
     * Sets the value of the channelUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeymanagementRequest.ChannelUpdate }
     *     
     */
    public void setChannelUpdate(KeymanagementRequest.ChannelUpdate value) {
        this.channelUpdate = value;
    }

    /**
     * Gets the value of the accountId property.
     * 
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     */
    public void setAccountId(int value) {
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
     *         &lt;element name="routingData" type="{}OutboundRouting"/>
     *       &lt;/sequence>
     *       &lt;attribute name="channelId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "routingData"
    })
    public static class ChannelUpdate {

        @XmlElement(required = true)
        protected OutboundRouting routingData;
        @XmlAttribute(name = "channelId", required = true)
        protected int channelId;
        @XmlAttribute(name = "name")
        protected String name;

        /**
         * Gets the value of the routingData property.
         * 
         * @return
         *     possible object is
         *     {@link OutboundRouting }
         *     
         */
        public OutboundRouting getRoutingData() {
            return routingData;
        }

        /**
         * Sets the value of the routingData property.
         * 
         * @param value
         *     allowed object is
         *     {@link OutboundRouting }
         *     
         */
        public void setRoutingData(OutboundRouting value) {
            this.routingData = value;
        }

        /**
         * Gets the value of the channelId property.
         * 
         */
        public int getChannelId() {
            return channelId;
        }

        /**
         * Sets the value of the channelId property.
         * 
         */
        public void setChannelId(int value) {
            this.channelId = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

    }

}

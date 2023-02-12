
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Permission complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Permission">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="resourceId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="groupId" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="granted" use="required" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *       &lt;attribute name="permissionId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Permission")
public class Permission {

    @XmlAttribute(name = "resourceId", required = true)
    protected int resourceId;
    @XmlAttribute(name = "groupId", required = true)
    protected int groupId;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "granted", required = true)
    protected byte granted;
    @XmlAttribute(name = "permissionId")
    protected String permissionId;

    /**
     * Gets the value of the resourceId property.
     * 
     */
    public int getResourceId() {
        return resourceId;
    }

    /**
     * Sets the value of the resourceId property.
     * 
     */
    public void setResourceId(int value) {
        this.resourceId = value;
    }

    /**
     * Gets the value of the groupId property.
     * 
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     */
    public void setGroupId(int value) {
        this.groupId = value;
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

    /**
     * Gets the value of the granted property.
     * 
     */
    public byte getGranted() {
        return granted;
    }

    /**
     * Sets the value of the granted property.
     * 
     */
    public void setGranted(byte value) {
        this.granted = value;
    }

    /**
     * Gets the value of the permissionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPermissionId() {
        return permissionId;
    }

    /**
     * Sets the value of the permissionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPermissionId(String value) {
        this.permissionId = value;
    }

}

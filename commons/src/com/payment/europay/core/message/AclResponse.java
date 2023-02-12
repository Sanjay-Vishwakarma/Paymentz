
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
 *         &lt;element name="partialResult" type="{}PartialResult" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="permissions" type="{}Permission" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="group" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="permissions" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *                 &lt;attribute name="groupId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resource" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                 &lt;/sequence>
 *                 &lt;attribute name="installed" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="resource" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="resourceId" type="{http://www.w3.org/2001/XMLSchema}int" />
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
    "partialResult",
    "permissions",
    "group",
    "resource"
})
@XmlRootElement(name = "acl_response")
public class AclResponse {

    @XmlElement(nillable = true)
    protected List<PartialResult> partialResult;
    @XmlElement(nillable = true)
    protected List<Permission> permissions;
    @XmlElement(nillable = true)
    protected List<AclResponse.Group> group;
    @XmlElement(nillable = true)
    protected List<AclResponse.Resource> resource;
    @XmlAttribute(name = "code", required = true)
    protected int code;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Gets the value of the partialResult property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partialResult property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartialResult().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartialResult }
     * 
     * 
     */
    public List<PartialResult> getPartialResult() {
        if (partialResult == null) {
            partialResult = new ArrayList<PartialResult>();
        }
        return this.partialResult;
    }

    /**
     * Gets the value of the permissions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the permissions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPermissions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Permission }
     * 
     * 
     */
    public List<Permission> getPermissions() {
        if (permissions == null) {
            permissions = new ArrayList<Permission>();
        }
        return this.permissions;
    }

    /**
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AclResponse.Group }
     * 
     * 
     */
    public List<AclResponse.Group> getGroup() {
        if (group == null) {
            group = new ArrayList<AclResponse.Group>();
        }
        return this.group;
    }

    /**
     * Gets the value of the resource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AclResponse.Resource }
     * 
     * 
     */
    public List<AclResponse.Resource> getResource() {
        if (resource == null) {
            resource = new ArrayList<AclResponse.Resource>();
        }
        return this.resource;
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
     *       &lt;attribute name="permissions" type="{http://www.w3.org/2001/XMLSchema}byte" />
     *       &lt;attribute name="groupId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Group {

        @XmlAttribute(name = "permissions")
        protected Byte permissions;
        @XmlAttribute(name = "groupId")
        protected Integer groupId;
        @XmlAttribute(name = "name")
        protected String name;

        /**
         * Gets the value of the permissions property.
         * 
         * @return
         *     possible object is
         *     {@link Byte }
         *     
         */
        public Byte getPermissions() {
            return permissions;
        }

        /**
         * Sets the value of the permissions property.
         * 
         * @param value
         *     allowed object is
         *     {@link Byte }
         *     
         */
        public void setPermissions(Byte value) {
            this.permissions = value;
        }

        /**
         * Gets the value of the groupId property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getGroupId() {
            return groupId;
        }

        /**
         * Sets the value of the groupId property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setGroupId(Integer value) {
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
     *       &lt;attribute name="installed" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="resource" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="resourceId" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Resource {

        @XmlAttribute(name = "installed")
        protected String installed;
        @XmlAttribute(name = "resource")
        protected String resource;
        @XmlAttribute(name = "resourceId")
        protected Integer resourceId;

        /**
         * Gets the value of the installed property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstalled() {
            return installed;
        }

        /**
         * Sets the value of the installed property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstalled(String value) {
            this.installed = value;
        }

        /**
         * Gets the value of the resource property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResource() {
            return resource;
        }

        /**
         * Sets the value of the resource property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResource(String value) {
            this.resource = value;
        }

        /**
         * Gets the value of the resourceId property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getResourceId() {
            return resourceId;
        }

        /**
         * Sets the value of the resourceId property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setResourceId(Integer value) {
            this.resourceId = value;
        }

    }

}

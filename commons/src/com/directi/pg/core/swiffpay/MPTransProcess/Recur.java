/**
 * Recur.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class Recur  implements java.io.Serializable {
    private int create;

    private int billingcycle;

    private int billingmax;

    private int start;

    private float amount;

    public Recur() {
    }

    public Recur(
           int create,
           int billingcycle,
           int billingmax,
           int start,
           float amount) {
           this.create = create;
           this.billingcycle = billingcycle;
           this.billingmax = billingmax;
           this.start = start;
           this.amount = amount;
    }


    /**
     * Gets the create value for this Recur.
     * 
     * @return create
     */
    public int getCreate() {
        return create;
    }


    /**
     * Sets the create value for this Recur.
     * 
     * @param create
     */
    public void setCreate(int create) {
        this.create = create;
    }


    /**
     * Gets the billingcycle value for this Recur.
     * 
     * @return billingcycle
     */
    public int getBillingcycle() {
        return billingcycle;
    }


    /**
     * Sets the billingcycle value for this Recur.
     * 
     * @param billingcycle
     */
    public void setBillingcycle(int billingcycle) {
        this.billingcycle = billingcycle;
    }


    /**
     * Gets the billingmax value for this Recur.
     * 
     * @return billingmax
     */
    public int getBillingmax() {
        return billingmax;
    }


    /**
     * Sets the billingmax value for this Recur.
     * 
     * @param billingmax
     */
    public void setBillingmax(int billingmax) {
        this.billingmax = billingmax;
    }


    /**
     * Gets the start value for this Recur.
     * 
     * @return start
     */
    public int getStart() {
        return start;
    }


    /**
     * Sets the start value for this Recur.
     * 
     * @param start
     */
    public void setStart(int start) {
        this.start = start;
    }


    /**
     * Gets the amount value for this Recur.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this Recur.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Recur)) return false;
        Recur other = (Recur) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.create == other.getCreate() &&
            this.billingcycle == other.getBillingcycle() &&
            this.billingmax == other.getBillingmax() &&
            this.start == other.getStart() &&
            this.amount == other.getAmount();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getCreate();
        _hashCode += getBillingcycle();
        _hashCode += getBillingmax();
        _hashCode += getStart();
        _hashCode += new Float(getAmount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Recur.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "Recur"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("create");
        elemField.setXmlName(new javax.xml.namespace.QName("", "create"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingcycle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billingcycle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingmax");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billingmax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("start");
        elemField.setXmlName(new javax.xml.namespace.QName("", "start"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

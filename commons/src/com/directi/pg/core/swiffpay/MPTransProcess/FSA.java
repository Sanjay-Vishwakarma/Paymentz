/**
 * FSA.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class FSA  implements java.io.Serializable {
    private int healthcareflag;

    private float rxamount;

    public FSA() {
    }

    public FSA(
           int healthcareflag,
           float rxamount) {
           this.healthcareflag = healthcareflag;
           this.rxamount = rxamount;
    }


    /**
     * Gets the healthcareflag value for this FSA.
     * 
     * @return healthcareflag
     */
    public int getHealthcareflag() {
        return healthcareflag;
    }


    /**
     * Sets the healthcareflag value for this FSA.
     * 
     * @param healthcareflag
     */
    public void setHealthcareflag(int healthcareflag) {
        this.healthcareflag = healthcareflag;
    }


    /**
     * Gets the rxamount value for this FSA.
     * 
     * @return rxamount
     */
    public float getRxamount() {
        return rxamount;
    }


    /**
     * Sets the rxamount value for this FSA.
     * 
     * @param rxamount
     */
    public void setRxamount(float rxamount) {
        this.rxamount = rxamount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FSA)) return false;
        FSA other = (FSA) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.healthcareflag == other.getHealthcareflag() &&
            this.rxamount == other.getRxamount();
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
        _hashCode += getHealthcareflag();
        _hashCode += new Float(getRxamount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FSA.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "FSA"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("healthcareflag");
        elemField.setXmlName(new javax.xml.namespace.QName("", "healthcareflag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rxamount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rxamount"));
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

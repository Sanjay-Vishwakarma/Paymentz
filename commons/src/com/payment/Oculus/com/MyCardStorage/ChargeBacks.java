/**
 * ChargeBacks.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class ChargeBacks  implements java.io.Serializable {
    private java.lang.String chargeBack;

    public ChargeBacks() {
    }

    public ChargeBacks(
           java.lang.String chargeBack) {
           this.chargeBack = chargeBack;
    }


    /**
     * Gets the chargeBack value for this ChargeBacks.
     * 
     * @return chargeBack
     */
    public java.lang.String getChargeBack() {
        return chargeBack;
    }


    /**
     * Sets the chargeBack value for this ChargeBacks.
     * 
     * @param chargeBack
     */
    public void setChargeBack(java.lang.String chargeBack) {
        this.chargeBack = chargeBack;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChargeBacks)) return false;
        ChargeBacks other = (ChargeBacks) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chargeBack==null && other.getChargeBack()==null) || 
             (this.chargeBack!=null &&
              this.chargeBack.equals(other.getChargeBack())));
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
        if (getChargeBack() != null) {
            _hashCode += getChargeBack().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChargeBacks.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ChargeBacks"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeBack");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "chargeBack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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

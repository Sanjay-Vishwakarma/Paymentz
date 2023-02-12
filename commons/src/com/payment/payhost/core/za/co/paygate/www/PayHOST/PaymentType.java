/**
 * PaymentType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class PaymentType  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentMethodType method;

    private org.apache.axis.types.Token detail;

    public PaymentType() {
    }

    public PaymentType(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentMethodType method,
           org.apache.axis.types.Token detail) {
           this.method = method;
           this.detail = detail;
    }


    /**
     * Gets the method value for this PaymentType.
     * 
     * @return method
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentMethodType getMethod() {
        return method;
    }


    /**
     * Sets the method value for this PaymentType.
     * 
     * @param method
     */
    public void setMethod(com.payment.payhost.core.za.co.paygate.www.PayHOST.PaymentMethodType method) {
        this.method = method;
    }


    /**
     * Gets the detail value for this PaymentType.
     * 
     * @return detail
     */
    public org.apache.axis.types.Token getDetail() {
        return detail;
    }


    /**
     * Sets the detail value for this PaymentType.
     * 
     * @param detail
     */
    public void setDetail(org.apache.axis.types.Token detail) {
        this.detail = detail;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentType)) return false;
        PaymentType other = (PaymentType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.method==null && other.getMethod()==null) || 
             (this.method!=null &&
              this.method.equals(other.getMethod()))) &&
            ((this.detail==null && other.getDetail()==null) || 
             (this.detail!=null &&
              this.detail.equals(other.getDetail())));
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
        if (getMethod() != null) {
            _hashCode += getMethod().hashCode();
        }
        if (getDetail() != null) {
            _hashCode += getDetail().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PaymentType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("method");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Method"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentMethodType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("detail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Detail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
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

/**
 * ChargeBackRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class ChargeBackRequest  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity;

    private com.payment.Oculus.com.MyCardStorage.ChargeBackData chargeBackData;

    public ChargeBackRequest() {
    }

    public ChargeBackRequest(
            com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity,
            com.payment.Oculus.com.MyCardStorage.ChargeBackData chargeBackData) {
           this.serviceSecurity = serviceSecurity;
           this.chargeBackData = chargeBackData;
    }


    /**
     * Gets the serviceSecurity value for this ChargeBackRequest.
     * 
     * @return serviceSecurity
     */
    public com.payment.Oculus.com.MyCardStorage.ServiceSecurity getServiceSecurity() {
        return serviceSecurity;
    }


    /**
     * Sets the serviceSecurity value for this ChargeBackRequest.
     * 
     * @param serviceSecurity
     */
    public void setServiceSecurity(com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity) {
        this.serviceSecurity = serviceSecurity;
    }


    /**
     * Gets the chargeBackData value for this ChargeBackRequest.
     * 
     * @return chargeBackData
     */
    public com.payment.Oculus.com.MyCardStorage.ChargeBackData getChargeBackData() {
        return chargeBackData;
    }


    /**
     * Sets the chargeBackData value for this ChargeBackRequest.
     * 
     * @param chargeBackData
     */
    public void setChargeBackData(com.payment.Oculus.com.MyCardStorage.ChargeBackData chargeBackData) {
        this.chargeBackData = chargeBackData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChargeBackRequest)) return false;
        ChargeBackRequest other = (ChargeBackRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceSecurity==null && other.getServiceSecurity()==null) || 
             (this.serviceSecurity!=null &&
              this.serviceSecurity.equals(other.getServiceSecurity()))) &&
            ((this.chargeBackData==null && other.getChargeBackData()==null) || 
             (this.chargeBackData!=null &&
              this.chargeBackData.equals(other.getChargeBackData())));
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
        if (getServiceSecurity() != null) {
            _hashCode += getServiceSecurity().hashCode();
        }
        if (getChargeBackData() != null) {
            _hashCode += getChargeBackData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChargeBackRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ChargeBackRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceSecurity");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chargeBackData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ChargeBackData"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ChargeBackData"));
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

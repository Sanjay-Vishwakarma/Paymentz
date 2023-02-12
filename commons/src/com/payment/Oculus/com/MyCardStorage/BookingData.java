/**
 * BookingData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class BookingData  implements java.io.Serializable {
    private int companyID;

    private java.lang.String rawData;

    public BookingData() {
    }

    public BookingData(
           int companyID,
           java.lang.String rawData) {
           this.companyID = companyID;
           this.rawData = rawData;
    }


    /**
     * Gets the companyID value for this BookingData.
     * 
     * @return companyID
     */
    public int getCompanyID() {
        return companyID;
    }


    /**
     * Sets the companyID value for this BookingData.
     * 
     * @param companyID
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }


    /**
     * Gets the rawData value for this BookingData.
     * 
     * @return rawData
     */
    public java.lang.String getRawData() {
        return rawData;
    }


    /**
     * Sets the rawData value for this BookingData.
     * 
     * @param rawData
     */
    public void setRawData(java.lang.String rawData) {
        this.rawData = rawData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BookingData)) return false;
        BookingData other = (BookingData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.companyID == other.getCompanyID() &&
            ((this.rawData==null && other.getRawData()==null) || 
             (this.rawData!=null &&
              this.rawData.equals(other.getRawData())));
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
        _hashCode += getCompanyID();
        if (getRawData() != null) {
            _hashCode += getRawData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BookingData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BookingData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CompanyID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "RawData"));
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

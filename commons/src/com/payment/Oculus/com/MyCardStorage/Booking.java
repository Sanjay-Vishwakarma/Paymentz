/**
 * Booking.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class Booking  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity;

    private com.payment.Oculus.com.MyCardStorage.BookingData bookingRequest;

    public Booking() {
    }

    public Booking(
            com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity,
            com.payment.Oculus.com.MyCardStorage.BookingData bookingRequest) {
           this.serviceSecurity = serviceSecurity;
           this.bookingRequest = bookingRequest;
    }


    /**
     * Gets the serviceSecurity value for this Booking.
     * 
     * @return serviceSecurity
     */
    public com.payment.Oculus.com.MyCardStorage.ServiceSecurity getServiceSecurity() {
        return serviceSecurity;
    }


    /**
     * Sets the serviceSecurity value for this Booking.
     * 
     * @param serviceSecurity
     */
    public void setServiceSecurity(com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity) {
        this.serviceSecurity = serviceSecurity;
    }


    /**
     * Gets the bookingRequest value for this Booking.
     * 
     * @return bookingRequest
     */
    public com.payment.Oculus.com.MyCardStorage.BookingData getBookingRequest() {
        return bookingRequest;
    }


    /**
     * Sets the bookingRequest value for this Booking.
     * 
     * @param bookingRequest
     */
    public void setBookingRequest(com.payment.Oculus.com.MyCardStorage.BookingData bookingRequest) {
        this.bookingRequest = bookingRequest;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Booking)) return false;
        Booking other = (Booking) obj;
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
            ((this.bookingRequest==null && other.getBookingRequest()==null) || 
             (this.bookingRequest!=null &&
              this.bookingRequest.equals(other.getBookingRequest())));
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
        if (getBookingRequest() != null) {
            _hashCode += getBookingRequest().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Booking.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Booking"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceSecurity");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bookingRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BookingRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BookingData"));
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

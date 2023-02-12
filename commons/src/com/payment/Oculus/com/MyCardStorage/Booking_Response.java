/**
 * Booking_Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class Booking_Response  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.BookingData bookingResponse;

    private com.payment.Oculus.com.MyCardStorage.Result result;

    private java.lang.String MCSTransactionID;

    public Booking_Response() {
    }

    public Booking_Response(
            com.payment.Oculus.com.MyCardStorage.BookingData bookingResponse,
            com.payment.Oculus.com.MyCardStorage.Result result,
           java.lang.String MCSTransactionID) {
           this.bookingResponse = bookingResponse;
           this.result = result;
           this.MCSTransactionID = MCSTransactionID;
    }


    /**
     * Gets the bookingResponse value for this Booking_Response.
     * 
     * @return bookingResponse
     */
    public com.payment.Oculus.com.MyCardStorage.BookingData getBookingResponse() {
        return bookingResponse;
    }


    /**
     * Sets the bookingResponse value for this Booking_Response.
     * 
     * @param bookingResponse
     */
    public void setBookingResponse(com.payment.Oculus.com.MyCardStorage.BookingData bookingResponse) {
        this.bookingResponse = bookingResponse;
    }


    /**
     * Gets the result value for this Booking_Response.
     * 
     * @return result
     */
    public com.payment.Oculus.com.MyCardStorage.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this Booking_Response.
     * 
     * @param result
     */
    public void setResult(com.payment.Oculus.com.MyCardStorage.Result result) {
        this.result = result;
    }


    /**
     * Gets the MCSTransactionID value for this Booking_Response.
     * 
     * @return MCSTransactionID
     */
    public java.lang.String getMCSTransactionID() {
        return MCSTransactionID;
    }


    /**
     * Sets the MCSTransactionID value for this Booking_Response.
     * 
     * @param MCSTransactionID
     */
    public void setMCSTransactionID(java.lang.String MCSTransactionID) {
        this.MCSTransactionID = MCSTransactionID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Booking_Response)) return false;
        Booking_Response other = (Booking_Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bookingResponse==null && other.getBookingResponse()==null) || 
             (this.bookingResponse!=null &&
              this.bookingResponse.equals(other.getBookingResponse()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.MCSTransactionID==null && other.getMCSTransactionID()==null) || 
             (this.MCSTransactionID!=null &&
              this.MCSTransactionID.equals(other.getMCSTransactionID())));
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
        if (getBookingResponse() != null) {
            _hashCode += getBookingResponse().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getMCSTransactionID() != null) {
            _hashCode += getMCSTransactionID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Booking_Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Booking_Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bookingResponse");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BookingResponse"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "BookingData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Result"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MCSTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "MCSTransactionID"));
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

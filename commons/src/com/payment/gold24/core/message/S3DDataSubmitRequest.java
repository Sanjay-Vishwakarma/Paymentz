/**
 * S3DDataSubmitRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.gold24.core.message;

public class S3DDataSubmitRequest  implements java.io.Serializable {
    private int merchantid;

    private java.lang.String password;

    private java.lang.String secure3Dacsmessage;

    private java.lang.String secure3Dchecktransactionid;

    public S3DDataSubmitRequest() {
    }

    public S3DDataSubmitRequest(
           int merchantid,
           java.lang.String password,
           java.lang.String secure3Dacsmessage,
           java.lang.String secure3Dchecktransactionid) {
           this.merchantid = merchantid;
           this.password = password;
           this.secure3Dacsmessage = secure3Dacsmessage;
           this.secure3Dchecktransactionid = secure3Dchecktransactionid;
    }


    /**
     * Gets the merchantid value for this S3DDataSubmitRequest.
     * 
     * @return merchantid
     */
    public int getMerchantid() {
        return merchantid;
    }


    /**
     * Sets the merchantid value for this S3DDataSubmitRequest.
     * 
     * @param merchantid
     */
    public void setMerchantid(int merchantid) {
        this.merchantid = merchantid;
    }


    /**
     * Gets the password value for this S3DDataSubmitRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this S3DDataSubmitRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the secure3Dacsmessage value for this S3DDataSubmitRequest.
     * 
     * @return secure3Dacsmessage
     */
    public java.lang.String getSecure3Dacsmessage() {
        return secure3Dacsmessage;
    }


    /**
     * Sets the secure3Dacsmessage value for this S3DDataSubmitRequest.
     * 
     * @param secure3Dacsmessage
     */
    public void setSecure3Dacsmessage(java.lang.String secure3Dacsmessage) {
        this.secure3Dacsmessage = secure3Dacsmessage;
    }


    /**
     * Gets the secure3Dchecktransactionid value for this S3DDataSubmitRequest.
     * 
     * @return secure3Dchecktransactionid
     */
    public java.lang.String getSecure3Dchecktransactionid() {
        return secure3Dchecktransactionid;
    }


    /**
     * Sets the secure3Dchecktransactionid value for this S3DDataSubmitRequest.
     * 
     * @param secure3Dchecktransactionid
     */
    public void setSecure3Dchecktransactionid(java.lang.String secure3Dchecktransactionid) {
        this.secure3Dchecktransactionid = secure3Dchecktransactionid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof S3DDataSubmitRequest)) return false;
        S3DDataSubmitRequest other = (S3DDataSubmitRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.merchantid == other.getMerchantid() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.secure3Dacsmessage==null && other.getSecure3Dacsmessage()==null) || 
             (this.secure3Dacsmessage!=null &&
              this.secure3Dacsmessage.equals(other.getSecure3Dacsmessage()))) &&
            ((this.secure3Dchecktransactionid==null && other.getSecure3Dchecktransactionid()==null) || 
             (this.secure3Dchecktransactionid!=null &&
              this.secure3Dchecktransactionid.equals(other.getSecure3Dchecktransactionid())));
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
        _hashCode += getMerchantid();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getSecure3Dacsmessage() != null) {
            _hashCode += getSecure3Dacsmessage().hashCode();
        }
        if (getSecure3Dchecktransactionid() != null) {
            _hashCode += getSecure3Dchecktransactionid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(S3DDataSubmitRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "S3DDataSubmitRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secure3Dacsmessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "secure3dacsmessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secure3Dchecktransactionid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "secure3dchecktransactionid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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

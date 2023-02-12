/**
 * PrecheckPayoutReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class PrecheckPayoutReturn  implements java.io.Serializable {
    private int resultCode;

    private int errorCode;

    private java.lang.String errorCodeDescription;

    private java.lang.String mid;

    private java.lang.String merchantPayoutTransactionId;

    public PrecheckPayoutReturn() {
    }

    public PrecheckPayoutReturn(
           int resultCode,
           int errorCode,
           java.lang.String errorCodeDescription,
           java.lang.String mid,
           java.lang.String merchantPayoutTransactionId) {
           this.resultCode = resultCode;
           this.errorCode = errorCode;
           this.errorCodeDescription = errorCodeDescription;
           this.mid = mid;
           this.merchantPayoutTransactionId = merchantPayoutTransactionId;
    }


    /**
     * Gets the resultCode value for this PrecheckPayoutReturn.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this PrecheckPayoutReturn.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this PrecheckPayoutReturn.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PrecheckPayoutReturn.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorCodeDescription value for this PrecheckPayoutReturn.
     * 
     * @return errorCodeDescription
     */
    public java.lang.String getErrorCodeDescription() {
        return errorCodeDescription;
    }


    /**
     * Sets the errorCodeDescription value for this PrecheckPayoutReturn.
     * 
     * @param errorCodeDescription
     */
    public void setErrorCodeDescription(java.lang.String errorCodeDescription) {
        this.errorCodeDescription = errorCodeDescription;
    }


    /**
     * Gets the mid value for this PrecheckPayoutReturn.
     * 
     * @return mid
     */
    public java.lang.String getMid() {
        return mid;
    }


    /**
     * Sets the mid value for this PrecheckPayoutReturn.
     * 
     * @param mid
     */
    public void setMid(java.lang.String mid) {
        this.mid = mid;
    }


    /**
     * Gets the merchantPayoutTransactionId value for this PrecheckPayoutReturn.
     * 
     * @return merchantPayoutTransactionId
     */
    public java.lang.String getMerchantPayoutTransactionId() {
        return merchantPayoutTransactionId;
    }


    /**
     * Sets the merchantPayoutTransactionId value for this PrecheckPayoutReturn.
     * 
     * @param merchantPayoutTransactionId
     */
    public void setMerchantPayoutTransactionId(java.lang.String merchantPayoutTransactionId) {
        this.merchantPayoutTransactionId = merchantPayoutTransactionId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrecheckPayoutReturn)) return false;
        PrecheckPayoutReturn other = (PrecheckPayoutReturn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.resultCode == other.getResultCode() &&
            this.errorCode == other.getErrorCode() &&
            ((this.errorCodeDescription==null && other.getErrorCodeDescription()==null) || 
             (this.errorCodeDescription!=null &&
              this.errorCodeDescription.equals(other.getErrorCodeDescription()))) &&
            ((this.mid==null && other.getMid()==null) || 
             (this.mid!=null &&
              this.mid.equals(other.getMid()))) &&
            ((this.merchantPayoutTransactionId==null && other.getMerchantPayoutTransactionId()==null) || 
             (this.merchantPayoutTransactionId!=null &&
              this.merchantPayoutTransactionId.equals(other.getMerchantPayoutTransactionId())));
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
        _hashCode += getResultCode();
        _hashCode += getErrorCode();
        if (getErrorCodeDescription() != null) {
            _hashCode += getErrorCodeDescription().hashCode();
        }
        if (getMid() != null) {
            _hashCode += getMid().hashCode();
        }
        if (getMerchantPayoutTransactionId() != null) {
            _hashCode += getMerchantPayoutTransactionId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrecheckPayoutReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "PrecheckPayoutReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "resultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCodeDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCodeDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantPayoutTransactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "merchantPayoutTransactionId"));
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

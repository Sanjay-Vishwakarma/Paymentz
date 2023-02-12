/**
 * GetPayoutStateReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class GetPayoutStateReturn  implements java.io.Serializable {
    private int resultCode;

    private int errorCode;

    private java.lang.String errorCodeDescription;

    private com.payment.paySafeCard.pscservice.PayoutState[] payoutState;

    public GetPayoutStateReturn() {
    }

    public GetPayoutStateReturn(
           int resultCode,
           int errorCode,
           java.lang.String errorCodeDescription,
           com.payment.paySafeCard.pscservice.PayoutState[] payoutState) {
           this.resultCode = resultCode;
           this.errorCode = errorCode;
           this.errorCodeDescription = errorCodeDescription;
           this.payoutState = payoutState;
    }


    /**
     * Gets the resultCode value for this GetPayoutStateReturn.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this GetPayoutStateReturn.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this GetPayoutStateReturn.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this GetPayoutStateReturn.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the errorCodeDescription value for this GetPayoutStateReturn.
     * 
     * @return errorCodeDescription
     */
    public java.lang.String getErrorCodeDescription() {
        return errorCodeDescription;
    }


    /**
     * Sets the errorCodeDescription value for this GetPayoutStateReturn.
     * 
     * @param errorCodeDescription
     */
    public void setErrorCodeDescription(java.lang.String errorCodeDescription) {
        this.errorCodeDescription = errorCodeDescription;
    }


    /**
     * Gets the payoutState value for this GetPayoutStateReturn.
     * 
     * @return payoutState
     */
    public com.payment.paySafeCard.pscservice.PayoutState[] getPayoutState() {
        return payoutState;
    }


    /**
     * Sets the payoutState value for this GetPayoutStateReturn.
     * 
     * @param payoutState
     */
    public void setPayoutState(com.payment.paySafeCard.pscservice.PayoutState[] payoutState) {
        this.payoutState = payoutState;
    }

    public com.payment.paySafeCard.pscservice.PayoutState getPayoutState(int i) {
        return this.payoutState[i];
    }

    public void setPayoutState(int i, com.payment.paySafeCard.pscservice.PayoutState _value) {
        this.payoutState[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPayoutStateReturn)) return false;
        GetPayoutStateReturn other = (GetPayoutStateReturn) obj;
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
            ((this.payoutState==null && other.getPayoutState()==null) || 
             (this.payoutState!=null &&
              java.util.Arrays.equals(this.payoutState, other.getPayoutState())));
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
        if (getPayoutState() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPayoutState());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPayoutState(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetPayoutStateReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "getPayoutStateReturn"));
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
        elemField.setFieldName("payoutState");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "payoutState"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "payoutState"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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

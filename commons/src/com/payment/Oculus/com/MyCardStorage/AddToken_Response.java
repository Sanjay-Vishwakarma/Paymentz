/**
 * AddToken_Response.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class AddToken_Response  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.TokenData tokenData;

    private com.payment.Oculus.com.MyCardStorage.Result result;

    public AddToken_Response() {
    }

    public AddToken_Response(
            com.payment.Oculus.com.MyCardStorage.TokenData tokenData,
            com.payment.Oculus.com.MyCardStorage.Result result) {
           this.tokenData = tokenData;
           this.result = result;
    }


    /**
     * Gets the tokenData value for this AddToken_Response.
     * 
     * @return tokenData
     */
    public com.payment.Oculus.com.MyCardStorage.TokenData getTokenData() {
        return tokenData;
    }


    /**
     * Sets the tokenData value for this AddToken_Response.
     * 
     * @param tokenData
     */
    public void setTokenData(com.payment.Oculus.com.MyCardStorage.TokenData tokenData) {
        this.tokenData = tokenData;
    }


    /**
     * Gets the result value for this AddToken_Response.
     * 
     * @return result
     */
    public com.payment.Oculus.com.MyCardStorage.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this AddToken_Response.
     * 
     * @param result
     */
    public void setResult(com.payment.Oculus.com.MyCardStorage.Result result) {
        this.result = result;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AddToken_Response)) return false;
        AddToken_Response other = (AddToken_Response) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.tokenData==null && other.getTokenData()==null) || 
             (this.tokenData!=null &&
              this.tokenData.equals(other.getTokenData()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult())));
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
        if (getTokenData() != null) {
            _hashCode += getTokenData().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddToken_Response.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "AddToken_Response"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tokenData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
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

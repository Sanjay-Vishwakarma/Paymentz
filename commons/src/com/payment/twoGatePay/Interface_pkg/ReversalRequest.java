/**
 * ReversalRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

public class ReversalRequest  implements java.io.Serializable {
    private int loginid;

    private java.lang.String password;

    private java.lang.String transactionid;

    private int amount;

    public ReversalRequest() {
    }

    public ReversalRequest(
           int loginid,
           java.lang.String password,
           java.lang.String transactionid,
           int amount) {
           this.loginid = loginid;
           this.password = password;
           this.transactionid = transactionid;
           this.amount = amount;
    }


    /**
     * Gets the loginid value for this ReversalRequest.
     * 
     * @return loginid
     */
    public int getLoginid() {
        return loginid;
    }


    /**
     * Sets the loginid value for this ReversalRequest.
     * 
     * @param loginid
     */
    public void setLoginid(int loginid) {
        this.loginid = loginid;
    }


    /**
     * Gets the password value for this ReversalRequest.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this ReversalRequest.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the transactionid value for this ReversalRequest.
     * 
     * @return transactionid
     */
    public java.lang.String getTransactionid() {
        return transactionid;
    }


    /**
     * Sets the transactionid value for this ReversalRequest.
     * 
     * @param transactionid
     */
    public void setTransactionid(java.lang.String transactionid) {
        this.transactionid = transactionid;
    }


    /**
     * Gets the amount value for this ReversalRequest.
     * 
     * @return amount
     */
    public int getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this ReversalRequest.
     * 
     * @param amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReversalRequest)) return false;
        ReversalRequest other = (ReversalRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.loginid == other.getLoginid() &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.transactionid==null && other.getTransactionid()==null) || 
             (this.transactionid!=null &&
              this.transactionid.equals(other.getTransactionid()))) &&
            this.amount == other.getAmount();
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
        _hashCode += getLoginid();
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getTransactionid() != null) {
            _hashCode += getTransactionid().hashCode();
        }
        _hashCode += getAmount();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReversalRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "ReversalRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loginid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "loginid"));
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
        elemField.setFieldName("transactionid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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

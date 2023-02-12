/**
 * CreditCardTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class CreditCardTransaction  implements java.io.Serializable {
    private com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity;

    private com.payment.Oculus.com.MyCardStorage.TokenData tokenData;

    private com.payment.Oculus.com.MyCardStorage.TransactionData transactionData;

    public CreditCardTransaction() {
    }

    public CreditCardTransaction(
            com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity,
            com.payment.Oculus.com.MyCardStorage.TokenData tokenData,
            com.payment.Oculus.com.MyCardStorage.TransactionData transactionData) {
           this.serviceSecurity = serviceSecurity;
           this.tokenData = tokenData;
           this.transactionData = transactionData;
    }


    /**
     * Gets the serviceSecurity value for this CreditCardTransaction.
     * 
     * @return serviceSecurity
     */
    public com.payment.Oculus.com.MyCardStorage.ServiceSecurity getServiceSecurity() {
        return serviceSecurity;
    }


    /**
     * Sets the serviceSecurity value for this CreditCardTransaction.
     * 
     * @param serviceSecurity
     */
    public void setServiceSecurity(com.payment.Oculus.com.MyCardStorage.ServiceSecurity serviceSecurity) {
        this.serviceSecurity = serviceSecurity;
    }


    /**
     * Gets the tokenData value for this CreditCardTransaction.
     * 
     * @return tokenData
     */
    public com.payment.Oculus.com.MyCardStorage.TokenData getTokenData() {
        return tokenData;
    }


    /**
     * Sets the tokenData value for this CreditCardTransaction.
     * 
     * @param tokenData
     */
    public void setTokenData(com.payment.Oculus.com.MyCardStorage.TokenData tokenData) {
        this.tokenData = tokenData;
    }


    /**
     * Gets the transactionData value for this CreditCardTransaction.
     * 
     * @return transactionData
     */
    public com.payment.Oculus.com.MyCardStorage.TransactionData getTransactionData() {
        return transactionData;
    }


    /**
     * Sets the transactionData value for this CreditCardTransaction.
     * 
     * @param transactionData
     */
    public void setTransactionData(com.payment.Oculus.com.MyCardStorage.TransactionData transactionData) {
        this.transactionData = transactionData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CreditCardTransaction)) return false;
        CreditCardTransaction other = (CreditCardTransaction) obj;
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
            ((this.tokenData==null && other.getTokenData()==null) || 
             (this.tokenData!=null &&
              this.tokenData.equals(other.getTokenData()))) &&
            ((this.transactionData==null && other.getTransactionData()==null) || 
             (this.transactionData!=null &&
              this.transactionData.equals(other.getTransactionData())));
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
        if (getTokenData() != null) {
            _hashCode += getTokenData().hashCode();
        }
        if (getTransactionData() != null) {
            _hashCode += getTransactionData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CreditCardTransaction.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "CreditCardTransaction"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceSecurity");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ServiceSecurity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tokenData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TransactionData"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TransactionData"));
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

/**
 * S3DDataSubmitRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.uPayGate.Interface_pkg;

public class S3DDataSubmitRequest  implements java.io.Serializable {
    private String accountID;

    private String password;

    private String transactionID;

    private String secure3DAcsMessage;

    public S3DDataSubmitRequest() {
    }

    public S3DDataSubmitRequest(
           String accountID,
           String password,
           String transactionID,
           String secure3DAcsMessage) {
           this.accountID = accountID;
           this.password = password;
           this.transactionID = transactionID;
           this.secure3DAcsMessage = secure3DAcsMessage;
    }


    /**
     * Gets the accountID value for this S3DDataSubmitRequest.
     * 
     * @return accountID
     */
    public String getAccountID() {
        return accountID;
    }


    /**
     * Sets the accountID value for this S3DDataSubmitRequest.
     * 
     * @param accountID
     */
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }


    /**
     * Gets the password value for this S3DDataSubmitRequest.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this S3DDataSubmitRequest.
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * Gets the transactionID value for this S3DDataSubmitRequest.
     * 
     * @return transactionID
     */
    public String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this S3DDataSubmitRequest.
     * 
     * @param transactionID
     */
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the secure3DAcsMessage value for this S3DDataSubmitRequest.
     * 
     * @return secure3DAcsMessage
     */
    public String getSecure3DAcsMessage() {
        return secure3DAcsMessage;
    }


    /**
     * Sets the secure3DAcsMessage value for this S3DDataSubmitRequest.
     * 
     * @param secure3DAcsMessage
     */
    public void setSecure3DAcsMessage(String secure3DAcsMessage) {
        this.secure3DAcsMessage = secure3DAcsMessage;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
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
            ((this.accountID==null && other.getAccountID()==null) || 
             (this.accountID!=null &&
              this.accountID.equals(other.getAccountID()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.secure3DAcsMessage==null && other.getSecure3DAcsMessage()==null) || 
             (this.secure3DAcsMessage!=null &&
              this.secure3DAcsMessage.equals(other.getSecure3DAcsMessage())));
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
        if (getAccountID() != null) {
            _hashCode += getAccountID().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getSecure3DAcsMessage() != null) {
            _hashCode += getSecure3DAcsMessage().hashCode();
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
        elemField.setFieldName("accountID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secure3DAcsMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Secure3DAcsMessage"));
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
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

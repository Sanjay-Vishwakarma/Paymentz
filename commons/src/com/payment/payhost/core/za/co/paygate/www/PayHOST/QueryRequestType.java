/**
 * QueryRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class QueryRequestType  implements java.io.Serializable {
    private com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account;

    private java.lang.String transactionId;

    private org.apache.axis.types.Token payRequestId;

    private org.apache.axis.types.Token merchantOrderId;

    private com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryTransactionType transactionType;

    //private com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields;

    public QueryRequestType() {
    }

    public QueryRequestType(
            com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account,
           java.lang.String transactionId,
           org.apache.axis.types.Token payRequestId,
           org.apache.axis.types.Token merchantOrderId,
            com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryTransactionType transactionType)
            //com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields)
            {
           this.account = account;
           this.transactionId = transactionId;
           this.payRequestId = payRequestId;
           this.merchantOrderId = merchantOrderId;
           this.transactionType = transactionType;
           //this.userDefinedFields = userDefinedFields;
    }


    /**
     * Gets the account value for this QueryRequestType.
     * 
     * @return account
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType getAccount() {
        return account;
    }


    /**
     * Sets the account value for this QueryRequestType.
     * 
     * @param account
     */
    public void setAccount(com.payment.payhost.core.za.co.paygate.www.PayHOST.PayGateAccountType account) {
        this.account = account;
    }


    /**
     * Gets the transactionId value for this QueryRequestType.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this QueryRequestType.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the payRequestId value for this QueryRequestType.
     * 
     * @return payRequestId
     */
    public org.apache.axis.types.Token getPayRequestId() {
        return payRequestId;
    }


    /**
     * Sets the payRequestId value for this QueryRequestType.
     * 
     * @param payRequestId
     */
    public void setPayRequestId(org.apache.axis.types.Token payRequestId) {
        this.payRequestId = payRequestId;
    }


    /**
     * Gets the merchantOrderId value for this QueryRequestType.
     * 
     * @return merchantOrderId
     */
    public org.apache.axis.types.Token getMerchantOrderId() {
        return merchantOrderId;
    }


    /**
     * Sets the merchantOrderId value for this QueryRequestType.
     * 
     * @param merchantOrderId
     */
    public void setMerchantOrderId(org.apache.axis.types.Token merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }


    /**
     * Gets the transactionType value for this QueryRequestType.
     * 
     * @return transactionType
     */
    public com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryTransactionType getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this QueryRequestType.
     * 
     * @param transactionType
     */
    public void setTransactionType(com.payment.payhost.core.za.co.paygate.www.PayHOST.QueryTransactionType transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the userDefinedFields value for this QueryRequestType.
     * 
     * @return userDefinedFields
     */
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] getUserDefinedFields() {
        return userDefinedFields;
    }
*/

    /*public void setUserDefinedFields(com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType[] userDefinedFields) {
        this.userDefinedFields = userDefinedFields;
    }
*/
    /*public com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType getUserDefinedFields(int i) {
        return this.userDefinedFields[i];
    }
*/
    /*public void setUserDefinedFields(int i, com.payment.payhost.core.za.co.paygate.www.PayHOST.KeyValueType _value) {
        this.userDefinedFields[i] = _value;
    }
*/
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryRequestType)) return false;
        QueryRequestType other = (QueryRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.account==null && other.getAccount()==null) || 
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            ((this.transactionId==null && other.getTransactionId()==null) || 
             (this.transactionId!=null &&
              this.transactionId.equals(other.getTransactionId()))) &&
            ((this.payRequestId==null && other.getPayRequestId()==null) || 
             (this.payRequestId!=null &&
              this.payRequestId.equals(other.getPayRequestId()))) &&
            ((this.merchantOrderId==null && other.getMerchantOrderId()==null) || 
             (this.merchantOrderId!=null &&
              this.merchantOrderId.equals(other.getMerchantOrderId()))) &&
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType()))) /*&&
            ((this.userDefinedFields==null && other.getUserDefinedFields()==null) || 
             (this.userDefinedFields!=null &&
              java.util.Arrays.equals(this.userDefinedFields, other.getUserDefinedFields())))*/;
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
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getTransactionId() != null) {
            _hashCode += getTransactionId().hashCode();
        }
        if (getPayRequestId() != null) {
            _hashCode += getPayRequestId().hashCode();
        }
        if (getMerchantOrderId() != null) {
            _hashCode += getMerchantOrderId().hashCode();
        }
        if (getTransactionType() != null) {
            _hashCode += getTransactionType().hashCode();
        }
        /*if (getUserDefinedFields() != null)
        {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUserDefinedFields());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUserDefinedFields(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }*/
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(QueryRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayGateAccountType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payRequestId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PayRequestId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantOrderId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "MerchantOrderId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "token"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "QueryTransactionType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userDefinedFields");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "UserDefinedFields"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "KeyValueType"));
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

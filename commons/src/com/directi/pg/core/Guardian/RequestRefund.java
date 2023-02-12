/**
 * RequestRefund.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.Guardian;

public class RequestRefund  implements java.io.Serializable {
    private int merchant_id;

    private String merchant_token;

    private String hashkey;

    private String transaction_id;

    private String comments;

    private float amount;

    public RequestRefund() {
    }

    public RequestRefund(
           int merchant_id,
           String merchant_token,
           String hashkey,
           String transaction_id,
           String comments,
           float amount) {
           this.merchant_id = merchant_id;
           this.merchant_token = merchant_token;
           this.hashkey = hashkey;
           this.transaction_id = transaction_id;
           this.comments = comments;
           this.amount = amount;
    }


    /**
     * Gets the merchant_id value for this RequestRefund.
     * 
     * @return merchant_id
     */
    public int getMerchant_id() {
        return merchant_id;
    }


    /**
     * Sets the merchant_id value for this RequestRefund.
     * 
     * @param merchant_id
     */
    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }


    /**
     * Gets the merchant_token value for this RequestRefund.
     * 
     * @return merchant_token
     */
    public String getMerchant_token() {
        return merchant_token;
    }


    /**
     * Sets the merchant_token value for this RequestRefund.
     * 
     * @param merchant_token
     */
    public void setMerchant_token(String merchant_token) {
        this.merchant_token = merchant_token;
    }


    /**
     * Gets the hashkey value for this RequestRefund.
     * 
     * @return hashkey
     */
    public String getHashkey() {
        return hashkey;
    }


    /**
     * Sets the hashkey value for this RequestRefund.
     * 
     * @param hashkey
     */
    public void setHashkey(String hashkey) {
        this.hashkey = hashkey;
    }


    /**
     * Gets the transaction_id value for this RequestRefund.
     * 
     * @return transaction_id
     */
    public String getTransaction_id() {
        return transaction_id;
    }


    /**
     * Sets the transaction_id value for this RequestRefund.
     * 
     * @param transaction_id
     */
    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }


    /**
     * Gets the comments value for this RequestRefund.
     * 
     * @return comments
     */
    public String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this RequestRefund.
     * 
     * @param comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }


    /**
     * Gets the amount value for this RequestRefund.
     * 
     * @return amount
     */
    public float getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this RequestRefund.
     * 
     * @param amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RequestRefund)) return false;
        RequestRefund other = (RequestRefund) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.merchant_id == other.getMerchant_id() &&
            ((this.merchant_token==null && other.getMerchant_token()==null) || 
             (this.merchant_token!=null &&
              this.merchant_token.equals(other.getMerchant_token()))) &&
            ((this.hashkey==null && other.getHashkey()==null) || 
             (this.hashkey!=null &&
              this.hashkey.equals(other.getHashkey()))) &&
            ((this.transaction_id==null && other.getTransaction_id()==null) || 
             (this.transaction_id!=null &&
              this.transaction_id.equals(other.getTransaction_id()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
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
        _hashCode += getMerchant_id();
        if (getMerchant_token() != null) {
            _hashCode += getMerchant_token().hashCode();
        }
        if (getHashkey() != null) {
            _hashCode += getHashkey().hashCode();
        }
        if (getTransaction_id() != null) {
            _hashCode += getTransaction_id().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        _hashCode += new Float(getAmount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestRefund.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:ccgwwsdl", "RequestRefund"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchant_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant_token");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchant_token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hashkey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hashkey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transaction_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transaction_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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

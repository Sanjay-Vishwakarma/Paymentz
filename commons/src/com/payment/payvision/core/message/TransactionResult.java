/**
 * TransactionResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payvision.core.message;

public class TransactionResult  extends com.payment.payvision.core.message.BaseEntity  implements java.io.Serializable {
    private int result;

    private java.lang.String message;

    private java.lang.String trackingMemberCode;

    private int transactionId;

    private java.lang.String transactionGuid;

    private java.util.Calendar transactionDateTime;

    private com.payment.payvision.core.message.CdcEntry[] cdc;

    private java.lang.Boolean recovered;

    public TransactionResult() {
    }

    public TransactionResult(
           int result,
           java.lang.String message,
           java.lang.String trackingMemberCode,
           int transactionId,
           java.lang.String transactionGuid,
           java.util.Calendar transactionDateTime,
           com.payment.payvision.core.message.CdcEntry[] cdc,
           java.lang.Boolean recovered) {
        this.result = result;
        this.message = message;
        this.trackingMemberCode = trackingMemberCode;
        this.transactionId = transactionId;
        this.transactionGuid = transactionGuid;
        this.transactionDateTime = transactionDateTime;
        this.cdc = cdc;
        this.recovered = recovered;
    }


    /**
     * Gets the result value for this TransactionResult.
     * 
     * @return result
     */
    public int getResult() {
        return result;
    }


    /**
     * Sets the result value for this TransactionResult.
     * 
     * @param result
     */
    public void setResult(int result) {
        this.result = result;
    }


    /**
     * Gets the message value for this TransactionResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this TransactionResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the trackingMemberCode value for this TransactionResult.
     * 
     * @return trackingMemberCode
     */
    public java.lang.String getTrackingMemberCode() {
        return trackingMemberCode;
    }


    /**
     * Sets the trackingMemberCode value for this TransactionResult.
     * 
     * @param trackingMemberCode
     */
    public void setTrackingMemberCode(java.lang.String trackingMemberCode) {
        this.trackingMemberCode = trackingMemberCode;
    }


    /**
     * Gets the transactionId value for this TransactionResult.
     * 
     * @return transactionId
     */
    public int getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this TransactionResult.
     * 
     * @param transactionId
     */
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }


    /**
     * Gets the transactionGuid value for this TransactionResult.
     * 
     * @return transactionGuid
     */
    public java.lang.String getTransactionGuid() {
        return transactionGuid;
    }


    /**
     * Sets the transactionGuid value for this TransactionResult.
     * 
     * @param transactionGuid
     */
    public void setTransactionGuid(java.lang.String transactionGuid) {
        this.transactionGuid = transactionGuid;
    }


    /**
     * Gets the transactionDateTime value for this TransactionResult.
     * 
     * @return transactionDateTime
     */
    public java.util.Calendar getTransactionDateTime() {
        return transactionDateTime;
    }


    /**
     * Sets the transactionDateTime value for this TransactionResult.
     * 
     * @param transactionDateTime
     */
    public void setTransactionDateTime(java.util.Calendar transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }


    /**
     * Gets the cdc value for this TransactionResult.
     * 
     * @return cdc
     */
    public com.payment.payvision.core.message.CdcEntry[] getCdc() {
        return cdc;
    }


    /**
     * Sets the cdc value for this TransactionResult.
     * 
     * @param cdc
     */
    public void setCdc(com.payment.payvision.core.message.CdcEntry[] cdc) {
        this.cdc = cdc;
    }


    /**
     * Gets the recovered value for this TransactionResult.
     * 
     * @return recovered
     */
    public java.lang.Boolean getRecovered() {
        return recovered;
    }


    /**
     * Sets the recovered value for this TransactionResult.
     * 
     * @param recovered
     */
    public void setRecovered(java.lang.Boolean recovered) {
        this.recovered = recovered;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransactionResult)) return false;
        TransactionResult other = (TransactionResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.result == other.getResult() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.trackingMemberCode==null && other.getTrackingMemberCode()==null) || 
             (this.trackingMemberCode!=null &&
              this.trackingMemberCode.equals(other.getTrackingMemberCode()))) &&
            this.transactionId == other.getTransactionId() &&
            ((this.transactionGuid==null && other.getTransactionGuid()==null) || 
             (this.transactionGuid!=null &&
              this.transactionGuid.equals(other.getTransactionGuid()))) &&
            ((this.transactionDateTime==null && other.getTransactionDateTime()==null) || 
             (this.transactionDateTime!=null &&
              this.transactionDateTime.equals(other.getTransactionDateTime()))) &&
            ((this.cdc==null && other.getCdc()==null) || 
             (this.cdc!=null &&
              java.util.Arrays.equals(this.cdc, other.getCdc()))) &&
            ((this.recovered==null && other.getRecovered()==null) || 
             (this.recovered!=null &&
              this.recovered.equals(other.getRecovered())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        _hashCode += getResult();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getTrackingMemberCode() != null) {
            _hashCode += getTrackingMemberCode().hashCode();
        }
        _hashCode += getTransactionId();
        if (getTransactionGuid() != null) {
            _hashCode += getTransactionGuid().hashCode();
        }
        if (getTransactionDateTime() != null) {
            _hashCode += getTransactionDateTime().hashCode();
        }
        if (getCdc() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCdc());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCdc(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRecovered() != null) {
            _hashCode += getRecovered().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://payvision.com/gateway/", "TransactionResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trackingMemberCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "TrackingMemberCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "TransactionId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionGuid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "TransactionGuid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "TransactionDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cdc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "Cdc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://payvision.com/gateway/", "CdcEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "CdcEntry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recovered");
        elemField.setXmlName(new javax.xml.namespace.QName("http://payvision.com/gateway/", "Recovered"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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

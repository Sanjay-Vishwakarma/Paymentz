/**
 * TransactionResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.Oculus.com.MyCardStorage;

public class TransactionResult  implements java.io.Serializable {
    private int MCSTransactionID;

    private java.lang.String processorTransactionID;

    private java.math.BigDecimal amount;

    private java.lang.String ticketNumber;

    private java.lang.String referenceNumber;

    private java.lang.String processorApprovalCode;

    private com.payment.Oculus.com.MyCardStorage.Result result;

    private com.payment.Oculus.com.MyCardStorage.TokenData tokenData;

    public TransactionResult() {
    }

    public TransactionResult(
           int MCSTransactionID,
           java.lang.String processorTransactionID,
           java.math.BigDecimal amount,
           java.lang.String ticketNumber,
           java.lang.String referenceNumber,
           java.lang.String processorApprovalCode,
           com.payment.Oculus.com.MyCardStorage.Result result,
           com.payment.Oculus.com.MyCardStorage.TokenData tokenData) {
           this.MCSTransactionID = MCSTransactionID;
           this.processorTransactionID = processorTransactionID;
           this.amount = amount;
           this.ticketNumber = ticketNumber;
           this.referenceNumber = referenceNumber;
           this.processorApprovalCode = processorApprovalCode;
           this.result = result;
           this.tokenData = tokenData;
    }


    /**
     * Gets the MCSTransactionID value for this TransactionResult.
     * 
     * @return MCSTransactionID
     */
    public int getMCSTransactionID() {
        return MCSTransactionID;
    }


    /**
     * Sets the MCSTransactionID value for this TransactionResult.
     * 
     * @param MCSTransactionID
     */
    public void setMCSTransactionID(int MCSTransactionID) {
        this.MCSTransactionID = MCSTransactionID;
    }


    /**
     * Gets the processorTransactionID value for this TransactionResult.
     * 
     * @return processorTransactionID
     */
    public java.lang.String getProcessorTransactionID() {
        return processorTransactionID;
    }


    /**
     * Sets the processorTransactionID value for this TransactionResult.
     * 
     * @param processorTransactionID
     */
    public void setProcessorTransactionID(java.lang.String processorTransactionID) {
        this.processorTransactionID = processorTransactionID;
    }


    /**
     * Gets the amount value for this TransactionResult.
     * 
     * @return amount
     */
    public java.math.BigDecimal getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this TransactionResult.
     * 
     * @param amount
     */
    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }


    /**
     * Gets the ticketNumber value for this TransactionResult.
     * 
     * @return ticketNumber
     */
    public java.lang.String getTicketNumber() {
        return ticketNumber;
    }


    /**
     * Sets the ticketNumber value for this TransactionResult.
     * 
     * @param ticketNumber
     */
    public void setTicketNumber(java.lang.String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    /**
     * Gets the referenceNumber value for this TransactionResult.
     * 
     * @return referenceNumber
     */
    public java.lang.String getReferenceNumber() {
        return referenceNumber;
    }


    /**
     * Sets the referenceNumber value for this TransactionResult.
     * 
     * @param referenceNumber
     */
    public void setReferenceNumber(java.lang.String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    /**
     * Gets the processorApprovalCode value for this TransactionResult.
     * 
     * @return processorApprovalCode
     */
    public java.lang.String getProcessorApprovalCode() {
        return processorApprovalCode;
    }


    /**
     * Sets the processorApprovalCode value for this TransactionResult.
     * 
     * @param processorApprovalCode
     */
    public void setProcessorApprovalCode(java.lang.String processorApprovalCode) {
        this.processorApprovalCode = processorApprovalCode;
    }


    /**
     * Gets the result value for this TransactionResult.
     * 
     * @return result
     */
    public com.payment.Oculus.com.MyCardStorage.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this TransactionResult.
     * 
     * @param result
     */
    public void setResult(com.payment.Oculus.com.MyCardStorage.Result result) {
        this.result = result;
    }


    /**
     * Gets the tokenData value for this TransactionResult.
     * 
     * @return tokenData
     */
    public com.payment.Oculus.com.MyCardStorage.TokenData getTokenData() {
        return tokenData;
    }


    /**
     * Sets the tokenData value for this TransactionResult.
     * 
     * @param tokenData
     */
    public void setTokenData(com.payment.Oculus.com.MyCardStorage.TokenData tokenData) {
        this.tokenData = tokenData;
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
        _equals = true && 
            this.MCSTransactionID == other.getMCSTransactionID() &&
            ((this.processorTransactionID==null && other.getProcessorTransactionID()==null) || 
             (this.processorTransactionID!=null &&
              this.processorTransactionID.equals(other.getProcessorTransactionID()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.ticketNumber==null && other.getTicketNumber()==null) || 
             (this.ticketNumber!=null &&
              this.ticketNumber.equals(other.getTicketNumber()))) &&
            ((this.referenceNumber==null && other.getReferenceNumber()==null) || 
             (this.referenceNumber!=null &&
              this.referenceNumber.equals(other.getReferenceNumber()))) &&
            ((this.processorApprovalCode==null && other.getProcessorApprovalCode()==null) || 
             (this.processorApprovalCode!=null &&
              this.processorApprovalCode.equals(other.getProcessorApprovalCode()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.tokenData==null && other.getTokenData()==null) || 
             (this.tokenData!=null &&
              this.tokenData.equals(other.getTokenData())));
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
        _hashCode += getMCSTransactionID();
        if (getProcessorTransactionID() != null) {
            _hashCode += getProcessorTransactionID().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getTicketNumber() != null) {
            _hashCode += getTicketNumber().hashCode();
        }
        if (getReferenceNumber() != null) {
            _hashCode += getReferenceNumber().hashCode();
        }
        if (getProcessorApprovalCode() != null) {
            _hashCode += getProcessorApprovalCode().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getTokenData() != null) {
            _hashCode += getTokenData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TransactionResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TransactionResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MCSTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "MCSTransactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processorTransactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ProcessorTransactionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "Amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ticketNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TicketNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ReferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processorApprovalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "ProcessorApprovalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tokenData");
        elemField.setXmlName(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://MyCardStorage.com/", "TokenData"));
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

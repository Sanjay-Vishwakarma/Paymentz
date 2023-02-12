/**
 * GetPaymentStatusResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService;

public class GetPaymentStatusResponse  implements java.io.Serializable {
    private java.lang.String wcTxnId;

    private java.lang.String merchantTnxId;

    private java.lang.String transactionStatus;

    private java.lang.String errorCode;

    private java.util.Date txnDate;

    private java.lang.String sourceAccount;

    private java.lang.String destinationAccount;

    public GetPaymentStatusResponse() {
    }

    public GetPaymentStatusResponse(
           java.lang.String wcTxnId,
           java.lang.String merchantTnxId,
           java.lang.String transactionStatus,
           java.lang.String errorCode,
           java.util.Date txnDate,
           java.lang.String sourceAccount,
           java.lang.String destinationAccount) {
           this.wcTxnId = wcTxnId;
           this.merchantTnxId = merchantTnxId;
           this.transactionStatus = transactionStatus;
           this.errorCode = errorCode;
           this.txnDate = txnDate;
           this.sourceAccount = sourceAccount;
           this.destinationAccount = destinationAccount;
    }


    /**
     * Gets the wcTxnId value for this GetPaymentStatusResponse.
     * 
     * @return wcTxnId
     */
    public java.lang.String getWcTxnId() {
        return wcTxnId;
    }


    /**
     * Sets the wcTxnId value for this GetPaymentStatusResponse.
     * 
     * @param wcTxnId
     */
    public void setWcTxnId(java.lang.String wcTxnId) {
        this.wcTxnId = wcTxnId;
    }


    /**
     * Gets the merchantTnxId value for this GetPaymentStatusResponse.
     * 
     * @return merchantTnxId
     */
    public java.lang.String getMerchantTnxId() {
        return merchantTnxId;
    }


    /**
     * Sets the merchantTnxId value for this GetPaymentStatusResponse.
     * 
     * @param merchantTnxId
     */
    public void setMerchantTnxId(java.lang.String merchantTnxId) {
        this.merchantTnxId = merchantTnxId;
    }


    /**
     * Gets the transactionStatus value for this GetPaymentStatusResponse.
     * 
     * @return transactionStatus
     */
    public java.lang.String getTransactionStatus() {
        return transactionStatus;
    }


    /**
     * Sets the transactionStatus value for this GetPaymentStatusResponse.
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(java.lang.String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }


    /**
     * Gets the errorCode value for this GetPaymentStatusResponse.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this GetPaymentStatusResponse.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the txnDate value for this GetPaymentStatusResponse.
     * 
     * @return txnDate
     */
    public java.util.Date getTxnDate() {
        return txnDate;
    }


    /**
     * Sets the txnDate value for this GetPaymentStatusResponse.
     * 
     * @param txnDate
     */
    public void setTxnDate(java.util.Date txnDate) {
        this.txnDate = txnDate;
    }


    /**
     * Gets the sourceAccount value for this GetPaymentStatusResponse.
     * 
     * @return sourceAccount
     */
    public java.lang.String getSourceAccount() {
        return sourceAccount;
    }


    /**
     * Sets the sourceAccount value for this GetPaymentStatusResponse.
     * 
     * @param sourceAccount
     */
    public void setSourceAccount(java.lang.String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }


    /**
     * Gets the destinationAccount value for this GetPaymentStatusResponse.
     * 
     * @return destinationAccount
     */
    public java.lang.String getDestinationAccount() {
        return destinationAccount;
    }


    /**
     * Sets the destinationAccount value for this GetPaymentStatusResponse.
     * 
     * @param destinationAccount
     */
    public void setDestinationAccount(java.lang.String destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPaymentStatusResponse)) return false;
        GetPaymentStatusResponse other = (GetPaymentStatusResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.wcTxnId==null && other.getWcTxnId()==null) || 
             (this.wcTxnId!=null &&
              this.wcTxnId.equals(other.getWcTxnId()))) &&
            ((this.merchantTnxId==null && other.getMerchantTnxId()==null) || 
             (this.merchantTnxId!=null &&
              this.merchantTnxId.equals(other.getMerchantTnxId()))) &&
            ((this.transactionStatus==null && other.getTransactionStatus()==null) || 
             (this.transactionStatus!=null &&
              this.transactionStatus.equals(other.getTransactionStatus()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.txnDate==null && other.getTxnDate()==null) || 
             (this.txnDate!=null &&
              this.txnDate.equals(other.getTxnDate()))) &&
            ((this.sourceAccount==null && other.getSourceAccount()==null) || 
             (this.sourceAccount!=null &&
              this.sourceAccount.equals(other.getSourceAccount()))) &&
            ((this.destinationAccount==null && other.getDestinationAccount()==null) || 
             (this.destinationAccount!=null &&
              this.destinationAccount.equals(other.getDestinationAccount())));
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
        if (getWcTxnId() != null) {
            _hashCode += getWcTxnId().hashCode();
        }
        if (getMerchantTnxId() != null) {
            _hashCode += getMerchantTnxId().hashCode();
        }
        if (getTransactionStatus() != null) {
            _hashCode += getTransactionStatus().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getTxnDate() != null) {
            _hashCode += getTxnDate().hashCode();
        }
        if (getSourceAccount() != null) {
            _hashCode += getSourceAccount().hashCode();
        }
        if (getDestinationAccount() != null) {
            _hashCode += getDestinationAccount().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetPaymentStatusResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", ">GetPaymentStatusResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wcTxnId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "wcTxnId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantTnxId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "merchantTnxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "txnDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "sourceAccount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "destinationAccount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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

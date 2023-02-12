/**
 * GetPaymentStatusRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService;

public class GetPaymentStatusRequest  implements java.io.Serializable {
    private com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant;

    private java.lang.String merchantTnxId;

    private java.lang.String wcTxnId;

    private java.lang.String processType;

    private java.lang.String transactionAmount;

    private java.lang.String transactionCurrency;

    private java.util.Date localDate;

    public GetPaymentStatusRequest() {
    }

    public GetPaymentStatusRequest(
           com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant,
           java.lang.String merchantTnxId,
           java.lang.String wcTxnId,
           java.lang.String processType,
           java.lang.String transactionAmount,
           java.lang.String transactionCurrency,
           java.util.Date localDate) {
           this.merchant = merchant;
           this.merchantTnxId = merchantTnxId;
           this.wcTxnId = wcTxnId;
           this.processType = processType;
           this.transactionAmount = transactionAmount;
           this.transactionCurrency = transactionCurrency;
           this.localDate = localDate;
    }


    /**
     * Gets the merchant value for this GetPaymentStatusRequest.
     * 
     * @return merchant
     */
    public com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials getMerchant() {
        return merchant;
    }


    /**
     * Sets the merchant value for this GetPaymentStatusRequest.
     * 
     * @param merchant
     */
    public void setMerchant(com.directi.pg.core.mymonedero.message.gi.wavecrest.www.B2BPayService.MerchantCredentials merchant) {
        this.merchant = merchant;
    }


    /**
     * Gets the merchantTnxId value for this GetPaymentStatusRequest.
     * 
     * @return merchantTnxId
     */
    public java.lang.String getMerchantTnxId() {
        return merchantTnxId;
    }


    /**
     * Sets the merchantTnxId value for this GetPaymentStatusRequest.
     * 
     * @param merchantTnxId
     */
    public void setMerchantTnxId(java.lang.String merchantTnxId) {
        this.merchantTnxId = merchantTnxId;
    }


    /**
     * Gets the wcTxnId value for this GetPaymentStatusRequest.
     * 
     * @return wcTxnId
     */
    public java.lang.String getWcTxnId() {
        return wcTxnId;
    }


    /**
     * Sets the wcTxnId value for this GetPaymentStatusRequest.
     * 
     * @param wcTxnId
     */
    public void setWcTxnId(java.lang.String wcTxnId) {
        this.wcTxnId = wcTxnId;
    }


    /**
     * Gets the processType value for this GetPaymentStatusRequest.
     * 
     * @return processType
     */
    public java.lang.String getProcessType() {
        return processType;
    }


    /**
     * Sets the processType value for this GetPaymentStatusRequest.
     * 
     * @param processType
     */
    public void setProcessType(java.lang.String processType) {
        this.processType = processType;
    }


    /**
     * Gets the transactionAmount value for this GetPaymentStatusRequest.
     * 
     * @return transactionAmount
     */
    public java.lang.String getTransactionAmount() {
        return transactionAmount;
    }


    /**
     * Sets the transactionAmount value for this GetPaymentStatusRequest.
     * 
     * @param transactionAmount
     */
    public void setTransactionAmount(java.lang.String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    /**
     * Gets the transactionCurrency value for this GetPaymentStatusRequest.
     * 
     * @return transactionCurrency
     */
    public java.lang.String getTransactionCurrency() {
        return transactionCurrency;
    }


    /**
     * Sets the transactionCurrency value for this GetPaymentStatusRequest.
     * 
     * @param transactionCurrency
     */
    public void setTransactionCurrency(java.lang.String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }


    /**
     * Gets the localDate value for this GetPaymentStatusRequest.
     * 
     * @return localDate
     */
    public java.util.Date getLocalDate() {
        return localDate;
    }


    /**
     * Sets the localDate value for this GetPaymentStatusRequest.
     * 
     * @param localDate
     */
    public void setLocalDate(java.util.Date localDate) {
        this.localDate = localDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPaymentStatusRequest)) return false;
        GetPaymentStatusRequest other = (GetPaymentStatusRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchant==null && other.getMerchant()==null) || 
             (this.merchant!=null &&
              this.merchant.equals(other.getMerchant()))) &&
            ((this.merchantTnxId==null && other.getMerchantTnxId()==null) || 
             (this.merchantTnxId!=null &&
              this.merchantTnxId.equals(other.getMerchantTnxId()))) &&
            ((this.wcTxnId==null && other.getWcTxnId()==null) || 
             (this.wcTxnId!=null &&
              this.wcTxnId.equals(other.getWcTxnId()))) &&
            ((this.processType==null && other.getProcessType()==null) || 
             (this.processType!=null &&
              this.processType.equals(other.getProcessType()))) &&
            ((this.transactionAmount==null && other.getTransactionAmount()==null) || 
             (this.transactionAmount!=null &&
              this.transactionAmount.equals(other.getTransactionAmount()))) &&
            ((this.transactionCurrency==null && other.getTransactionCurrency()==null) || 
             (this.transactionCurrency!=null &&
              this.transactionCurrency.equals(other.getTransactionCurrency()))) &&
            ((this.localDate==null && other.getLocalDate()==null) || 
             (this.localDate!=null &&
              this.localDate.equals(other.getLocalDate())));
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
        if (getMerchant() != null) {
            _hashCode += getMerchant().hashCode();
        }
        if (getMerchantTnxId() != null) {
            _hashCode += getMerchantTnxId().hashCode();
        }
        if (getWcTxnId() != null) {
            _hashCode += getWcTxnId().hashCode();
        }
        if (getProcessType() != null) {
            _hashCode += getProcessType().hashCode();
        }
        if (getTransactionAmount() != null) {
            _hashCode += getTransactionAmount().hashCode();
        }
        if (getTransactionCurrency() != null) {
            _hashCode += getTransactionCurrency().hashCode();
        }
        if (getLocalDate() != null) {
            _hashCode += getLocalDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetPaymentStatusRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", ">GetPaymentStatusRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "merchant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "MerchantCredentials"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantTnxId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "merchantTnxId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wcTxnId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "wcTxnId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("processType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "processType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "transactionCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.wavecrest.gi/B2BPayService", "localDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
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
